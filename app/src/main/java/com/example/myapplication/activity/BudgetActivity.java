package com.example.myapplication.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MyBudgetAdapter;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.model.MyBudgetData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.utils.DialogUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CATEGORY = 1;
    MyBillData.Category category;
    Button btn_cancel, btn_ok;
    LinearLayout ll_category;
    TextView tv_category, tv_amount;
    ImageView iv_icon;

    private int ledger_id;
    private Calendar current_date = Calendar.getInstance();
    private List<MyBudgetData> myBudgetDataList;
    private double total_expense;

    private int pendingCalls = 0;

    TextView tv_date, tv_budget, tv_info;
    ImageView btn_add, btn_back;
    RecyclerView recyclerView;

    public BudgetActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        Intent intent = getIntent();
        ledger_id = intent.getIntExtra("ledger_id", 0);
        total_expense = intent.getDoubleExtra("total_expense", 0);

        Log.d("total_expense", String.valueOf(total_expense));

        initViews();
        getBudgets();
    }

    private void initViews() {
        tv_date = findViewById(R.id.tv_date);
        tv_budget = findViewById(R.id.tv_budget);
        tv_info = findViewById(R.id.tv_info);
        btn_add = findViewById(R.id.btn_add);
        btn_back = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.recycler_view);

        tv_date.setText(current_date.get(Calendar.YEAR) + "年 " + (current_date.get(Calendar.MONTH) + 1) + "月");
        btn_back.setOnClickListener(v -> finish());
        btn_add.setOnClickListener(v -> {
            showAddBudgetDialog();
        });
    }

    private void getBudgets() {
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Map<String, String> map = new HashMap<>();
        map.put("ledger", String.valueOf(ledger_id));
        map.put("year", String.valueOf(current_date.get(Calendar.YEAR)));
        map.put("month", String.valueOf(current_date.get(Calendar.MONTH) + 1));

        Call<ApiModels.ApiResponse<List<MyBudgetData>>> call = apiService.getBudgets(map);

        call.enqueue(new Callback<ApiModels.ApiResponse<List<MyBudgetData>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<MyBudgetData>>> call, Response<ApiModels.ApiResponse<List<MyBudgetData>>> response) {
                if (response.isSuccessful()) {
                    myBudgetDataList = response.body().getData();
                    if (myBudgetDataList.size() == 0) {
                        tv_budget.setText("暂无预算");
                        tv_info.setText("已用¥" + total_expense);
                    } else {
                        int total_budget = 0;
                        for (MyBudgetData myBudgetData : myBudgetDataList) {
                            total_budget += myBudgetData.getAmount();
                        }
                        tv_budget.setText("¥" + total_budget);
                        tv_info.setText("已用¥" + total_expense + "｜" + "剩余可用¥" +
                                String.format("%.2f", total_budget - total_expense));
                    }

                    getBudgetExpenses();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<MyBudgetData>>> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private void getBudgetExpenses() {
        pendingCalls = myBudgetDataList.size();

        if (pendingCalls == 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.setAdapter(new MyBudgetAdapter(myBudgetDataList));
            return;
        }

        for (MyBudgetData myBudgetData : myBudgetDataList) {
            ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
            Map<String, String> map = new HashMap<>();
            map.put("ledger_id", String.valueOf(ledger_id));
            map.put("year", String.valueOf(current_date.get(Calendar.YEAR)));
            map.put("month", String.valueOf(current_date.get(Calendar.MONTH) + 1));
            map.put("inOutType", String.valueOf(myBudgetData.getCategory().getInOutType()));
            map.put("detail_type", String.valueOf(myBudgetData.getCategory().getDetail_type()));

            Call<ApiModels.ApiResponse<ApiModels.TotalExpenseByCategoryResponse>> call = apiService.getTotalExpenseByCategory(map);

            call.enqueue(new Callback<ApiModels.ApiResponse<ApiModels.TotalExpenseByCategoryResponse>>() {
                @Override
                public void onResponse(Call<ApiModels.ApiResponse<ApiModels.TotalExpenseByCategoryResponse>> call, Response<ApiModels.ApiResponse<ApiModels.TotalExpenseByCategoryResponse>> response) {
                    if (response.isSuccessful()) {
                        double total_expense = response.body().getData().getTotalExpense();
                        myBudgetData.setTotalExpense(total_expense);
                    }

                    pendingCalls--;
                    if (pendingCalls == 0) {
                        setRecyclerView();
                    }
                }

                @Override
                public void onFailure(Call<ApiModels.ApiResponse<ApiModels.TotalExpenseByCategoryResponse>> call, Throwable t) {
                    t.printStackTrace();
                    pendingCalls--;
                    if (pendingCalls == 0) {
                        setRecyclerView();
                    }
                }
            });

        }
    }

    private void setRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        MyBudgetAdapter myBudgetAdapter = new MyBudgetAdapter(myBudgetDataList);
        recyclerView.setAdapter(myBudgetAdapter);
        myBudgetAdapter.setOnItemClickListener(new MyBudgetAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyBudgetData myBudgetData) {
                AlertDialog dialog = initDialog();
                ll_category.setClickable(false);
                int resourceId = getResources().getIdentifier(
                        "bill_" + myBudgetData.getCategory().getInOutType() + "_" + myBudgetData.getCategory().getDetail_type(),
                        "drawable", getPackageName());
                iv_icon.setImageResource(resourceId);
                tv_category.setText(MyBillData.getTypeName(myBudgetData.getCategory().getInOutType(), myBudgetData.getCategory().getDetail_type()));

                tv_amount.setText(String.valueOf(myBudgetData.getAmount()));

                // 取消按钮
                btn_cancel.setOnClickListener(v -> dialog.dismiss());

                // 确定按钮
                btn_ok.setOnClickListener(v -> {
                    if (tv_amount.getText().toString().equals("")) {
                        Toast.makeText(BudgetActivity.this, "请输入金额", Toast.LENGTH_LONG).show();
                        return;
                    }

                    ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
                    ApiModels.BudgetCreateRequest budgetCreateRequest = new ApiModels.BudgetCreateRequest(
                            ledger_id,
                            tv_amount.getText().toString(),
                            String.valueOf(current_date.get(Calendar.MONTH) + 1),
                            String.valueOf(current_date.get(Calendar.YEAR)),
                            String.valueOf(myBudgetData.getCategory().getInOutType()),
                            String.valueOf(myBudgetData.getCategory().getDetail_type())
                    );

                    Call<ApiModels.ApiResponse<Objects>> call = apiService.updateBudget(String.valueOf(myBudgetData.getId()), budgetCreateRequest);
                    call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
                        @Override
                        public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(BudgetActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                                getBudgets();
                            } else {
                                Toast.makeText(BudgetActivity.this, "添加失败", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                            t.printStackTrace();
                            Toast.makeText(BudgetActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                        }
                    });

                    dialog.dismiss();
                });
            }
        });

        // 左滑删除功能
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                MyBudgetData budgetData = myBudgetAdapter.getItem(viewHolder.getAdapterPosition());
                int budgetId = budgetData.getId();
                int position = viewHolder.getAdapterPosition();

                // 显示删除确认对话框，并传递id
                DialogUtils.showDeleteConfirmDialog(BudgetActivity.this, budgetId, 1,  () -> {
                    // 删除成功后刷新UI
                    myBudgetAdapter.removeItem(position);
                    getBudgets();
                }, () -> {
                    // 用户取消删除时，恢复被滑动的条目
                    myBudgetAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                });
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private AlertDialog initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BudgetActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_budget, null);
        builder.setView(dialogView);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        // 调整对话框的宽度
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.8),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        btn_cancel = dialogView.findViewById(R.id.btn_not);
        btn_ok = dialogView.findViewById(R.id.btn_ok);
        ll_category = dialogView.findViewById(R.id.type_picker);
        tv_category = dialogView.findViewById(R.id.type_text);
        tv_amount = dialogView.findViewById(R.id.et_amount_input);
        iv_icon = dialogView.findViewById(R.id.type_icon);

        return dialog;
    }

    private void showAddBudgetDialog() {
        AlertDialog dialog = initDialog();

        // 类型选择
        ll_category.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryPickerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PICK_CATEGORY);
        });

        // 取消按钮
        btn_cancel.setOnClickListener(v -> dialog.dismiss());

        // 确定按钮
        btn_ok.setOnClickListener(v -> {
            if (category == null) {
                Toast.makeText(this, "请选择类型", Toast.LENGTH_LONG).show();
                return;
            }

            if (category.getInOutType() == 1) {
                Toast.makeText(this, "不能为收入类型添加预算", Toast.LENGTH_LONG).show();
                return;
            }

            if (tv_amount.getText().toString().equals("")) {
                Toast.makeText(this, "请输入金额", Toast.LENGTH_LONG).show();
                return;
            }

            int amount = Integer.parseInt(tv_amount.getText().toString());
            if (amount <= 0) {
                Toast.makeText(this, "金额必须大于0", Toast.LENGTH_LONG).show();
                return;
            }

            ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
            ApiModels.BudgetCreateRequest budgetCreateRequest = new ApiModels.BudgetCreateRequest(
                    ledger_id,
                    tv_amount.getText().toString(),
                    String.valueOf(current_date.get(Calendar.MONTH) + 1),
                    String.valueOf(current_date.get(Calendar.YEAR)),
                    String.valueOf(category.getInOutType()),
                    String.valueOf(category.getDetail_type())
            );

            Call<ApiModels.ApiResponse<Objects>> call = apiService.createBudget(budgetCreateRequest);
            call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
                @Override
                public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(BudgetActivity.this, "添加成功", Toast.LENGTH_LONG).show();
                        getBudgets();
                    } else {
                        Toast.makeText(BudgetActivity.this, "添加失败，不可重复添加", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(BudgetActivity.this, "网络错误", Toast.LENGTH_LONG).show();
                }
            });

            dialog.dismiss();
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CATEGORY && resultCode == RESULT_OK && data != null) {
            int detailType = data.getIntExtra("detail_type", -1);
            int inOutType = data.getIntExtra("inOutType", -1);

            if (detailType != -1 && inOutType != -1) {
                // 根据返回的数据更新类型图标和文本
                int resourceId = getResources().getIdentifier(
                        "bill_" + inOutType + "_" + detailType,
                        "drawable", getPackageName());
                iv_icon.setImageResource(resourceId);
                tv_category.setText(MyBillData.getTypeName(inOutType, detailType));
                category = MyBillData.getCategoryByType(inOutType, detailType);
            } else {
                Toast.makeText(this, "选择类型失败", Toast.LENGTH_LONG).show();
            }
        }
    }

}
