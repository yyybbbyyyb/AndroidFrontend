package com.example.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.activity.AboutUsActivity;
import com.example.myapplication.activity.UserInfoActivity;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.utils.DialogUtils;
import com.example.myapplication.utils.ExcelUtil;
import com.example.myapplication.utils.SnackbarUtils;
import com.squareup.picasso.Picasso;
import com.example.myapplication.utils.Utils;


import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    ImageView avatar;
    TextView username;
    TextView info;

    List<MyBillData> bills;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        avatar = view.findViewById(R.id.avatar);
        username = view.findViewById(R.id.username);
        info = view.findViewById(R.id.use_info);

        getUserInfo();

        View.OnClickListener infoClickListener = v -> {
            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
            startActivity(intent);
        };

        avatar.setOnClickListener(infoClickListener);
        username.setOnClickListener(infoClickListener);

        // 问题反馈点击事件：弹出微信二维码
        LinearLayout feedback = view.findViewById(R.id.feedback);
        feedback.setOnClickListener(v -> {
            DialogUtils.showQRCodeDialog(getActivity());
        });

        // 关于我们点击事件：跳转到关于我们页面
        LinearLayout aboutUs = view.findViewById(R.id.about_us);
        aboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutUsActivity.class);
            startActivity(intent);
        });

        // 给应用评分点击事件：弹出评分对话框
        LinearLayout rateApp = view.findViewById(R.id.rate_app);
        rateApp.setOnClickListener(v -> {
            DialogUtils.showRatingDialog(getActivity());
        });

        // 数据导出点击事件
        LinearLayout exportData = view.findViewById(R.id.export_data);
        exportData.setOnClickListener(v -> {
            exportData();
        });

        // 支出报告点击事件
        LinearLayout report = view.findViewById(R.id.expense_report);
        report.setOnClickListener(v -> {
            ExpenseReportFragment fragment = new ExpenseReportFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();

            fragment.setOnDataLoadedListener(() -> fragment.exportViewToPDF());
        });

        LinearLayout ledger = view.findViewById(R.id.bill_recognition);
        ledger.setOnClickListener(v -> {
            SnackbarUtils.showCustomSnackbar(view,
                    "这学期太忙啦，功能暂未开放",
                    3000,
                    getResources().getColor(R.color.md_theme_tertiaryFixedDim),
                    getResources().getColor(R.color.white),
                    16f,
                    false,
                    false,
                    true);
        });

        LinearLayout budget = view.findViewById(R.id.financial_advice);
        budget.setOnClickListener(v -> {
            SnackbarUtils.showCustomSnackbar(view,
                    "全栈开发太累啦，功能暂未开放",
                    3000,
                    getResources().getColor(R.color.md_theme_inversePrimary),
                    getResources().getColor(R.color.white),
                    16f,
                    false,
                    false,
                    true);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 调用获取用户信息的逻辑
        getUserInfo();
    }

    private void getUserInfo() {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);

        Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call = apiService.getUserInfo();
        call.enqueue(new Callback<ApiModels.ApiResponse<ApiModels.UserInfoResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call, Response<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.UserInfoResponse userInfoResponse = response.body().getData();
                    username.setText(userInfoResponse.getUsername());
                    info.setText("已记账 " + userInfoResponse.getUsedDays() + " 天" + "，共记账 " + userInfoResponse.getBillCount() + " 笔");
                    Picasso.get()
                            .load(Utils.processAvatarUrl(userInfoResponse.getAvatar()))
                            .into(avatar, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("Picasso", "Image loaded successfully.");
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("Picasso", "Error loading image", e);
                                }
                            });

                } else {
                    Toast.makeText(getActivity(), "请求失败: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void exportData() {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        Map<String, String> map = new HashMap<>();
        map.put("ordering", "-date");
        Call<ApiModels.ApiResponse<List<MyBillData>>> call = apiService.getBills(map);
        call.enqueue(new Callback<ApiModels.ApiResponse<List<MyBillData>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Response<ApiModels.ApiResponse<List<MyBillData>>> response) {
                if (response.isSuccessful()) {
                    List<MyBillData> bills = response.body().getData();
                    String[] title = {"账本", "类型", "金额", "备注", "日期"};
                    String fileName = "xiaoYaoExportData" + ".xls";
                    List<ArrayList<String>> recordList = new ArrayList<>();

                    for (MyBillData bill : bills) {
                        ArrayList<String> record = new ArrayList<>();
                        record.add(bill.getLedger_name());
                        record.add(bill.getCategory().getType());
                        record.add(String.valueOf(bill.getAmountShort()));
                        record.add(bill.getRemark());
                        record.add(bill.getDate());
                        recordList.add(record);
                    }

                    ExcelUtil.initExcel(fileName, "账单数据", title);
                    ExcelUtil.writeObjListToExcel(recordList, fileName, getContext());


                } else {
                    Toast.makeText(getActivity(), "请求失败: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Throwable t) {
                Toast.makeText(getActivity(), "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
