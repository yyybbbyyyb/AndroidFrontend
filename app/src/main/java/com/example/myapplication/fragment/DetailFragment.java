package com.example.myapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.MyCardAdapter;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.model.MyCardData;
import com.example.myapplication.model.MyLedgerData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    private MyLedgerData current_ledger;
    private Calendar currentDate;

    private RecyclerView outerRecyclerView;
    private MyCardAdapter cardAdapter;
    private List<MyCardData> cardDataList;

    CircleImageView ledgerIcon;
    TextView ledgerName;
    ImageView dropdownIcon;

    ImageView leftMonth;
    TextView monthText;
    ImageView rightMonth;

    TextView totalIncome;
    TextView totalExpense;
    TextView balance;
    CardView calendar_btn;

    CardView budget_btn;
    LinearProgressIndicator budgetProgress;
    TextView progress_percentage;
    TextView budget_status;

    public DetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        initViews(view);
        initDate();
        initDateBtn();

        setLedger(() -> {
            // Ledger设置完成后，再调用setMonthlyTable
            setMonthlyTable();
        });

        dealCardRecyclerView(view);


        return view;
    }

    private void initViews(View view) {
        ledgerIcon = view.findViewById(R.id.ledger_icon);
        ledgerName = view.findViewById(R.id.ledger_name);
        dropdownIcon = view.findViewById(R.id.dropdown_icon);

        leftMonth = view.findViewById(R.id.calendar_icon_left);
        monthText = view.findViewById(R.id.current_month);
        rightMonth = view.findViewById(R.id.calendar_icon_right);

        totalIncome = view.findViewById(R.id.income_amount);
        totalExpense = view.findViewById(R.id.expense_amount);
        balance = view.findViewById(R.id.balance_amount);
        calendar_btn = view.findViewById(R.id.calendar_button);

        budget_btn = view.findViewById(R.id.budget_card);
        budgetProgress = view.findViewById(R.id.budget_progress);
        progress_percentage = view.findViewById(R.id.progress_percentage);
        budget_status = view.findViewById(R.id.budget_status);
    }

    @FunctionalInterface
    interface LedgerCallback {
        void onLedgerSet();
    }

    private void setLedger(LedgerCallback callback) {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        Call<ApiModels.ApiResponse<List<MyLedgerData>>> call = apiService.getLedgers();

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<List<MyLedgerData>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<MyLedgerData>>> call, Response<ApiModels.ApiResponse<List<MyLedgerData>>> response) {
                if (response.isSuccessful()) {
                    List<MyLedgerData> ledgerList = response.body().getData();
                    for (MyLedgerData ledger : ledgerList) {
                        if (ledger.isDefault()) {
                            Log.d("ledger", ledger.getName());
                            current_ledger = ledger;
                            ledgerName.setText(ledger.getName());
                            String logo_name = "ledger_" + ledger.getImage();
                            int resourceId = getContext().getResources().getIdentifier(
                                    logo_name, "drawable", getContext().getPackageName());
                            ledgerIcon.setImageResource(resourceId);

                            callback.onLedgerSet();
                            break;
                        }

                    }
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

    private void initDate() {
        currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH) + 1;
        monthText.setText(year + "-" + month);
    }

    private void initDateBtn() {
        leftMonth.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, -1);
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH) + 1;
            monthText.setText(year + "-" + String.format("%02d", month));

            rightMonth.setColorFilter(getResources().getColor(R.color.black));
        });

        rightMonth.setOnClickListener(v -> {
            if (currentDate.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                    currentDate.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                return;
            }

            currentDate.add(Calendar.MONTH, 1);
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH) + 1;
            monthText.setText(year + "-" + String.format("%02d", month));

            if (currentDate.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR) &&
                    currentDate.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) {
                rightMonth.setColorFilter(getResources().getColor(R.color.light_gray));
            }
        });

        rightMonth.setColorFilter(getResources().getColor(R.color.light_gray));

    }

    private void setMonthlyTable() {
        Log.d("ledger_id", current_ledger.getId());
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        Map<String, String> map = Map.of("year", String.valueOf(currentDate.get(Calendar.YEAR)),
                "month", String.valueOf(currentDate.get(Calendar.MONTH) + 1),
                    "ledger_id", current_ledger.getId());

        Call<ApiModels.ApiResponse<ApiModels.MonthlyReportResponse>> call = apiService.getMonthlyReport(map);

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<ApiModels.MonthlyReportResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.MonthlyReportResponse>> call, Response<ApiModels.ApiResponse<ApiModels.MonthlyReportResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.MonthlyReportResponse data = response.body().getData();
                    totalIncome.setText(String.valueOf(data.getIncome()));
                    totalExpense.setText(String.valueOf(data.getExpense()));
                    double calc_balance = Double.parseDouble(totalIncome.getText().toString()) - Double.parseDouble(totalExpense.getText().toString());
                    calc_balance = Math.round(calc_balance * 100) / 100.0;
                    balance.setText(String.valueOf(calc_balance));
                } else {
                    Toast.makeText(getContext(), "获取月度报表失败" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.MonthlyReportResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "获取月度报表失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void dealCardRecyclerView(View view) {
        outerRecyclerView = view.findViewById(R.id.outer_recycler_view);
        outerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化数据
        initData();
        //这里可能要用分页的方式加载数据

        // 设置适配器
        cardAdapter = new MyCardAdapter(cardDataList);
        outerRecyclerView.setAdapter(cardAdapter);
    }


    private void initData() {
        cardDataList = new ArrayList<>();

        // 创建一些示例数据
        List<MyBillData> billList1 = new ArrayList<>();
        billList1.add(new MyBillData("餐饮", "午餐", 50.0, "1"));
        billList1.add(new MyBillData("交通", "出租车", 20.0, "2"));

        List<MyBillData> billList2 = new ArrayList<>();
        billList2.add(new MyBillData("购物", "衣服", 200.0, "3"));
        billList2.add(new MyBillData("娱乐", "电影", 100.0, "4"));
        billList2.add(new MyBillData("餐饮", "晚餐", 100.0, "5"));
        billList2.add(new MyBillData("交通", "地铁", 10.0, "6"));


        // 添加不同日期的数据
        cardDataList.add(new MyCardData("2024-10-01", 1000, 500, billList1));
        cardDataList.add(new MyCardData("2024-10-02", 2000, 1200, billList2));
    }
}
