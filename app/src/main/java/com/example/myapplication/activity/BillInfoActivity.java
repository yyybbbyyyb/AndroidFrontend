package com.example.myapplication.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MyBillAdapter;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class BillInfoActivity extends AppCompatActivity {

    private List<MyBillData> myBillDataList;
    private int inOut = 0; // 1: 收入，2: 支出
    private int ledger_id = 0;
    private String date_before = "";
    private String date_after = "";
    private int detail_type = -1;
    private int order = 0; // 0: 时间降序，1: 时间升序， 2: 金额降序，3: 金额升序
    private MyBillAdapter billInfoAdapter;


    ImageView returnButton, moreButton;
    TextView title;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_info);

        Intent intent = getIntent();
        inOut = intent.getIntExtra("inOut", 0);
        ledger_id = intent.getIntExtra("ledger_id", 0);
        date_before = intent.getStringExtra("date_before");
        date_after = intent.getStringExtra("date_after");
        detail_type = intent.getIntExtra("detail_type", -1);

        initViews();

        getBills();

        returnButton.setOnClickListener(v -> finish());

        title.setText(inOut == 1 ? "收入" : "支出");

        moreButton.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View sheetView = getLayoutInflater().inflate(R.layout.dialog_order, null);

            bottomSheetDialog.setContentView(sheetView);

            TextView date_up = sheetView.findViewById(R.id.btn_date_up);
            TextView date_down = sheetView.findViewById(R.id.btn_date_down);
            TextView amount_up = sheetView.findViewById(R.id.btn_amount_up);
            TextView amount_down = sheetView.findViewById(R.id.btn_amount_down);

            switch (order) {
                case 0:
                    date_down.setTextColor(getResources().getColor(R.color.md_theme_inversePrimary));
                    break;
                case 1:
                    date_up.setTextColor(getResources().getColor(R.color.md_theme_inversePrimary));
                    break;
                case 2:
                    amount_down.setTextColor(getResources().getColor(R.color.md_theme_inversePrimary));
                    break;
                case 3:
                    amount_up.setTextColor(getResources().getColor(R.color.md_theme_inversePrimary));
                    break;
            }

            date_up.setOnClickListener(v1 -> {
                order = 1;
                getBills();
                bottomSheetDialog.dismiss();
            });

            date_down.setOnClickListener(v1 -> {
                order = 0;
                getBills();
                bottomSheetDialog.dismiss();
            });

            amount_up.setOnClickListener(v1 -> {
                order = 3;
                getBills();
                bottomSheetDialog.dismiss();
            });

            amount_down.setOnClickListener(v1 -> {
                order = 2;
                getBills();
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();

            TextView cancel = sheetView.findViewById(R.id.btn_cancel);
            cancel.setOnClickListener(v1 -> bottomSheetDialog.dismiss());

        });


    }

    private void initViews() {
        title = findViewById(R.id.tv_title);
        returnButton = findViewById(R.id.btn_back);
        moreButton = findViewById(R.id.btn_more);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); // 设置布局管理器
    }

    private void getBills() {
        // 获取账单数据
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Map<String, String> map = new HashMap<>();
        map.put("ledger_id", String.valueOf(ledger_id));
        map.put("inOutType", String.valueOf(inOut));
        map.put("date_before", date_before);
        map.put("date_after", date_after);
        if (detail_type != -1) {
            map.put("detail_type", String.valueOf(detail_type));
        }
        map.put("ordering", getOrderString());

        Call<ApiModels.ApiResponse<List<MyBillData>>> call = apiService.getBills(map);

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<List<MyBillData>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<MyBillData>>> call, retrofit2.Response<ApiModels.ApiResponse<List<MyBillData>>> response) {
                if (response.isSuccessful()) {
                    myBillDataList = response.body().getData();

                    // 检查返回的数据
                    Log.d("BillInfoActivity", "Data: " + myBillDataList.toString());

                    if (billInfoAdapter == null) {
                        // 如果适配器为空，初始化并设置给RecyclerView
                        billInfoAdapter = new MyBillAdapter(myBillDataList, true);
                        billInfoAdapter.setOnItemClickListener(new MyBillAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(MyBillData billData) {
                                // 跳转到修改账单界面
                                Intent intent = new Intent(BillInfoActivity.this, EditBillActivity.class);
                                intent.putExtra("billId", billData.getId());
                                startActivity(intent);
                            }
                        });
                        recyclerView.setAdapter(billInfoAdapter);
                    } else {
                        // 适配器已存在，更新数据并通知刷新
                        billInfoAdapter.updateData(myBillDataList);
                        Log.d("BillInfoActivity", "Adapter: " + billInfoAdapter.toString());
                        billInfoAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(BillInfoActivity.this, "获取账单数据失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Throwable t) {
                Toast.makeText(BillInfoActivity.this, "获取账单数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getOrderString() {
        if (order == 0) {
            return "-date";
        } else if (order == 1) {
            return "date";
        } else if (order == 2) {
            return "-amount";
        } else if (order == 3) {
            return "amount";
        } else {
            return "wrong";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBills();
    }
}
