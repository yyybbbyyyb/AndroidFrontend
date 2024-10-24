package com.example.myapplication.fragment;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.CategoryPickerActivity;
import com.example.myapplication.activity.CreateEditLedgerActivity;
import com.example.myapplication.activity.LoginActivity;
import com.example.myapplication.adapter.MyLedgerAdapter;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.model.MyLedgerData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;


public class AccountFragment extends Fragment {

    private static final int REQUEST_CODE_PICK_CATEGORY = 1;

    private MyBillData.Category category;
    private Calendar selectedDate = Calendar.getInstance();
    private List<MyLedgerData> current_ledgerList;
    private MyLedgerData current_ledger;

    ImageView typeIcon;
    LinearLayout typePicker;
    ImageView datePicker, ledgerPicker;
    TextView typeText, dateText, ledgerText;
    TextView billTypeText;
    EditText amountText, descriptionText;
    Button saveButton;

    public AccountFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        initViews(view);
        initLedger();
        setupListeners();


        return view;
    }

    private void initViews(View view) {
        typeIcon = view.findViewById(R.id.type_icon);
        typePicker = view.findViewById(R.id.type_picker);
        datePicker = view.findViewById(R.id.date_picker1);
        ledgerPicker = view.findViewById(R.id.ledger_picker);
        typeText = view.findViewById(R.id.type_text);
        dateText = view.findViewById(R.id.date);
        ledgerText = view.findViewById(R.id.ledger);
        billTypeText = view.findViewById(R.id.bill_type);
        amountText = view.findViewById(R.id.amount_input);
        descriptionText = view.findViewById(R.id.remark_input);
        saveButton = view.findViewById(R.id.btn_submit);
    }

    private void setupListeners() {
        // 日期选择
        datePicker.setOnClickListener(v -> showDatePicker());

        // 类型选择
        typePicker.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CategoryPickerActivity.class);
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
        datePicker.show(getParentFragmentManager(), datePicker.toString());

        // 设置选中日期后的回调
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // 选中日期后的操作，将时间戳转换为可读日期
            selectedDate.setTimeInMillis(selection);

            // 更新显示的日期（格式为 yyyy-MM-dd）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
            dateText.setText(sdf.format(selectedDate.getTime()));
        });
    }

    private void dealLedger() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_ledger, null);

        bottomSheetDialog.setContentView(sheetView);
        // 更新账本数量
        TextView ledgerTitle = sheetView.findViewById(R.id.tv_ledger_title);
        ledgerTitle.setText("我的账本(" + current_ledgerList.size() + ")");

        // 设置 RecyclerView
        RecyclerView recyclerView = sheetView.findViewById(R.id.ledger_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        MyLedgerAdapter adapter = new MyLedgerAdapter(current_ledgerList);
        recyclerView.setAdapter(adapter);

        // 隐藏编辑按钮
        adapter.setOnEditClickListener(position -> {}, false);

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

    private void initLedger() {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
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
                    Toast.makeText(getContext(), "获取账本信息失败" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<MyLedgerData>>> call, Throwable t) {
                Toast.makeText(getContext(), "获取账本信息失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveBill() {
        Utils.hideKeyboard(getView(), getActivity());

        // 获取输入数据
        String amount = amountText.getText().toString();
        String remark = descriptionText.getText().toString();
        String date = dateText.getText().toString();
        String ledger = ledgerText.getText().toString();

        // 校验输入是否为空
        if (amount.isEmpty() || date.isEmpty() || ledger.isEmpty() || category == null) {
            Toast.makeText(getActivity(), "请填写所有字段", Toast.LENGTH_SHORT).show();
            return;
        }

        // 构建请求数据
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        ApiModels.BillCreateRequest request = new ApiModels.BillCreateRequest(
                 Integer.valueOf(current_ledger.getId()) , amount, remark, date, category.getInOutType() + "", category.getDetail_type() + "");

        Call<ApiModels.ApiResponse<Objects>> call = apiService.createBill(request);

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<Objects>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "账单已保存", Toast.LENGTH_SHORT).show();

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new DetailFragment())  // fragment_container 是 Activity 中 Fragment 的容器
                            .addToBackStack(null)
                            .commit();

                    if (getActivity() != null) {
                        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
                        bottomNavigationView.setSelectedItemId(R.id.navigation_detail);  // 更新为 DetailFragment 对应的菜单项
                    }
                } else {
                    Toast.makeText(getActivity(), "保存账单失败" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                Toast.makeText(getActivity(), "保存账单失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                int resourceId = getContext().getResources().getIdentifier("bill_" + inOutType + "_" + detailType, "drawable", getContext().getPackageName());
                typeIcon.setImageResource(resourceId);
                typeText.setText(MyBillData.getTypeName(inOutType, detailType));
                billTypeText.setText(inOutType == 1 ? "收入" : "支出");
                billTypeText.setTextColor(inOutType == 1 ? getResources().getColor(R.color.money_green) : getResources().getColor(R.color.money_red));
                category = MyBillData.getCategoryByType(inOutType, detailType);
            } else {
                Toast.makeText(getActivity(), "选择类型失败", Toast.LENGTH_LONG).show();
            }
        }
    }

}
