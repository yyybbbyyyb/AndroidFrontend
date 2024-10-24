package com.example.myapplication.activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MyLedgerAdapter;
import com.example.myapplication.fragment.DetailFragment;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.model.MyLedgerData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditBillActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PICK_CATEGORY = 1;

    private MyBillData.Category category;
    private Calendar selectedDate = Calendar.getInstance();
    private List<MyLedgerData> current_ledgerList;
    private MyLedgerData current_ledger;


    ImageView typeIcon;
    LinearLayout typePicker;
    ImageView datePicker, ledgerPicker;
    TextView typeText, dateText, ledgerText, createDateText;
    TextView billTypeText;
    EditText amountText, remarkText;
    Button deleteButton;
    ImageView returnButton, saveButton;

    private int billId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bill);

        // 初始化视图
        initViews();

        initLedger();


        // 从 Intent 中获取 billId
        Intent intent = getIntent();
        billId = intent.getIntExtra("billId", -1);

        if (billId != -1) {
            // 获取账单信息并预填充
            fetchBillData();
        }

        setupListeners();

        // 删除按钮点击事件
        deleteButton.setOnClickListener(v -> deleteBill());

        returnButton.setOnClickListener(v -> finish());

    }

    private void initViews() {
        typeIcon = findViewById(R.id.type_icon);
        typePicker = findViewById(R.id.type_picker);
        datePicker = findViewById(R.id.date_picker1);
        ledgerPicker = findViewById(R.id.ledger_picker);
        typeText = findViewById(R.id.type_text);
        dateText = findViewById(R.id.date);
        ledgerText = findViewById(R.id.ledger);
        billTypeText = findViewById(R.id.bill_type);
        amountText = findViewById(R.id.amount_input);
        remarkText = findViewById(R.id.remark_input);
        deleteButton = findViewById(R.id.btn_delete);
        returnButton = findViewById(R.id.btn_back);
        saveButton = findViewById(R.id.btn_ok);
        createDateText = findViewById(R.id.createDate);
    }

    private void fetchBillData() {
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        Call<ApiModels.ApiResponse<MyBillData>> call = apiService.getBill(String.valueOf(billId));
        call.enqueue(new Callback<ApiModels.ApiResponse<MyBillData>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<MyBillData>> call, Response<ApiModels.ApiResponse<MyBillData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 预填充数据
                    populateBillData(response.body().getData());
                } else {
                    Toast.makeText(EditBillActivity.this, "获取账单信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<MyBillData>> call, Throwable t) {
                Toast.makeText(EditBillActivity.this, "获取账单信息失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateBillData(MyBillData billData) {
        // 设置账单金额、备注等信息
        amountText.setText(String.valueOf(billData.getAmountActual()));
        remarkText.setText(billData.getRemark());
        dateText.setText(billData.getDate());
        createDateText.setText(billData.getCreated_time());

        // 根据账单类型设置图标和文字
        int resourceId = getResources().getIdentifier(
                "bill_" + billData.getCategory().getInOutType() + "_" + billData.getCategory().getDetail_type(),
                "drawable", getPackageName());
        typeIcon.setImageResource(resourceId);

        typeText.setText(MyBillData.getTypeName(billData.getCategory().getInOutType(), billData.getCategory().getDetail_type()));
        billTypeText.setText(billData.getCategory().getInOutType() == 1 ? "收入" : "支出");
        billTypeText.setTextColor(billData.getCategory().getInOutType() == 1 ? getResources().getColor(R.color.money_green) : getResources().getColor(R.color.money_red));
        category = billData.getCategory();

        // 获取账本
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Call<ApiModels.ApiResponse<MyLedgerData>> call = apiService.getLedger(String.valueOf(billData.getLedger()));

        call.enqueue(new Callback<ApiModels.ApiResponse<MyLedgerData>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<MyLedgerData>> call, Response<ApiModels.ApiResponse<MyLedgerData>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    current_ledger = response.body().getData();
                    ledgerText.setText(current_ledger.getName());
                } else {
                    Toast.makeText(EditBillActivity.this, "获取账本信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<MyLedgerData>> call, Throwable t) {
                Toast.makeText(EditBillActivity.this, "获取账本信息失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteBill() {
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        Call<ApiModels.ApiResponse<Objects>> call = apiService.deleteBill(String.valueOf(billId));
        call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditBillActivity.this, "账单已删除", Toast.LENGTH_SHORT).show();
                    finish(); // 删除后关闭 Activity
                } else {
                    Toast.makeText(EditBillActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                Toast.makeText(EditBillActivity.this, "删除失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // 日期选择
        datePicker.setOnClickListener(v -> showDatePicker());

        // 类型选择
        typePicker.setOnClickListener(v -> {
            Intent intent = new Intent(this, CategoryPickerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PICK_CATEGORY);
        });

        // 账本选择
        ledgerPicker.setOnClickListener(v -> {
            dealLedger();
        });

        // 保存按钮点击事件
        saveButton.setOnClickListener(v -> saveBill());
    }

    private void showDatePicker() {
        // 创建 MaterialDatePicker 构建器
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();

        // 设置标题和默认选中的日期
        builder.setTitleText("选择日期");

        // 创建日期选择器
        MaterialDatePicker<Long> datePicker = builder.build();

        // 显示选择器
        datePicker.show(getSupportFragmentManager(), "datePicker");

        // 设置选中日期后的回调
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // 选中日期后的操作，将时间戳转换为可读日期
            selectedDate.setTimeInMillis(selection);

            // 更新显示的日期（格式为 yyyy-MM-dd）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            dateText.setText(sdf.format(selectedDate.getTime()));
        });
    }

    private void initLedger() {
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Call<ApiModels.ApiResponse<List<MyLedgerData>>> call = apiService.getLedgers();

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<List<MyLedgerData>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<MyLedgerData>>> call, Response<ApiModels.ApiResponse<List<MyLedgerData>>> response) {
                if (response.isSuccessful()) {
                    List<MyLedgerData> ledgerList = response.body().getData();
                    current_ledgerList = ledgerList;

                    for (MyLedgerData ledger : ledgerList) {
                        if (ledger.isDefault()) {
                            current_ledger = ledger;
                            break;
                        }
                    }

                    ledgerText.setText(current_ledger.getName());
                } else {
                    Toast.makeText(EditBillActivity.this, "获取账本信息失败" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<MyLedgerData>>> call, Throwable t) {
                Toast.makeText(EditBillActivity.this, "获取账本信息失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dealLedger() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_ledger, null);

        bottomSheetDialog.setContentView(sheetView);
        // 更新账本数量
        TextView ledgerTitle = sheetView.findViewById(R.id.tv_ledger_title);
        ledgerTitle.setText("我的账本(" + current_ledgerList.size() + ")");

        // 设置 RecyclerView
        RecyclerView recyclerView = sheetView.findViewById(R.id.ledger_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyLedgerAdapter adapter = new MyLedgerAdapter(current_ledgerList);
        recyclerView.setAdapter(adapter);

        // 隐藏编辑按钮
        adapter.setOnEditClickListener(position -> {
        }, false);

        // 隐藏添加按钮
        sheetView.findViewById(R.id.btn_add).setVisibility(View.GONE);

        // 设置选择按钮点击事件
        adapter.setOnChoiceClickListener(position -> {
            MyLedgerData selectedLedger = current_ledgerList.get(position);
            current_ledger = selectedLedger;
            ledgerText.setText(selectedLedger.getName());
            bottomSheetDialog.dismiss();
        });
        bottomSheetDialog.show();
    }

    private void saveBill() {
        View view = this.getCurrentFocus();  // 获取当前焦点的 View
        if (view != null) {
            Utils.hideKeyboard(view, this);  // 如果当前有焦点的 View，隐藏键盘
        } else {
            // 如果没有焦点的 View，可以直接隐藏键盘
            Utils.hideKeyboard(new View(this), this);  // 创建一个新的 View 来获取 WindowToken
        }

        // 获取输入数据
        String amount = amountText.getText().toString();
        String remark = remarkText.getText().toString();
        String date = dateText.getText().toString();
        String ledger = ledgerText.getText().toString();

        // 校验输入是否为空
        if (amount.isEmpty() || date.isEmpty() || ledger.isEmpty() || category == null) {
            Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构建请求数据
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        ApiModels.BillCreateRequest request = new ApiModels.BillCreateRequest(
                Integer.valueOf(current_ledger.getId()), amount, remark, date, category.getInOutType() + "", category.getDetail_type() + "");

        Call<ApiModels.ApiResponse<Objects>> call = apiService.updateBill(String.valueOf(billId), request);

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<Objects>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditBillActivity.this, "账单已保存", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String error = response.errorBody().string();
                        Gson gson = new Gson();
                        ApiModels.ApiResponse<Objects> apiResponse = gson.fromJson(error, ApiModels.ApiResponse.class);
                        String errors = (String) apiResponse.getErrors();

                        Toast.makeText(EditBillActivity.this, "失败: " + errors, Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        Toast.makeText(EditBillActivity.this, "注册失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                Toast.makeText(EditBillActivity.this, "保存账单失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
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
                typeIcon.setImageResource(resourceId);
                typeText.setText(MyBillData.getTypeName(inOutType, detailType));
                billTypeText.setText(inOutType == 1 ? "收入" : "支出");
                billTypeText.setTextColor(inOutType == 1 ? getResources().getColor(R.color.money_green) : getResources().getColor(R.color.money_red));
                category = MyBillData.getCategoryByType(inOutType, detailType);
            } else {
                Toast.makeText(this, "选择类型失败", Toast.LENGTH_LONG).show();
            }
        }
    }
}
