package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.myapplication.R;
import com.example.myapplication.activity.CreateEditLedgerActivity;
import com.example.myapplication.adapter.MyCardAdapter;
import com.example.myapplication.adapter.MyLedgerAdapter;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.model.MyCardData;
import com.example.myapplication.model.MyLedgerData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

public class DetailFragment extends Fragment {

    private List<MyLedgerData> current_ledgerList;
    private MyLedgerData current_ledger;
    private Calendar currentDate;
    private List<MyCardData> cardDataList;

    // View
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

    RecyclerView outerRecyclerView;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        initViews(view);
        initDate();
        initDateBtn();

        // Ledger设置完成后，再调用setMonthlyTable
        setLedger(() -> {
            setMonthlyTable();
            initDropdownIcon();
            setCards();
        });


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

        outerRecyclerView = view.findViewById(R.id.outer_recycler_view);
        outerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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

            refresh();
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

            refresh();
        });

        rightMonth.setColorFilter(getResources().getColor(R.color.light_gray));

    }

    private void initDropdownIcon() {
        dropdownIcon.setOnClickListener(v -> {
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

            // 设置编辑按钮点击事件
            adapter.setOnEditClickListener(position -> {
                MyLedgerData selectedLedger = current_ledgerList.get(position);

                // 跳转到编辑账本界面
                Intent intent = new Intent(getContext(), CreateEditLedgerActivity.class);
                intent.putExtra("mode", "edit");
                intent.putExtra("ledger_name", selectedLedger.getName());
                intent.putExtra("ledger_icon", selectedLedger.getIconIndex());
                intent.putExtra("ledger_id", selectedLedger.getId());
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }, true);

            // 跳转到添加账本界面
            sheetView.findViewById(R.id.btn_add).setOnClickListener(v1 -> {
                Intent intent = new Intent(getContext(), CreateEditLedgerActivity.class);
                intent.putExtra("mode", "create");
                startActivity(intent);
                bottomSheetDialog.dismiss();
            });

            // 设置选择按钮点击事件
            adapter.setOnChoiceClickListener(position -> {
                MyLedgerData selectedLedger = current_ledgerList.get(position);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("last_selected_ledger_id", selectedLedger.getId());
                editor.apply();

                current_ledger = selectedLedger;
                ledgerName.setText(selectedLedger.getName());
                int resourceId = getContext().getResources().getIdentifier(
                        selectedLedger.getImage(), "drawable", getContext().getPackageName());
                ledgerIcon.setImageResource(resourceId);
                initDate();
                refresh();
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.show();
        });
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
                    current_ledgerList = ledgerList;

                    // 从 SharedPreferences 获取上次选择的账本 ID
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE);
                    String lastSelectedLedgerId = sharedPreferences.getString("last_selected_ledger_id", null);

                    if (lastSelectedLedgerId != null) {
                        boolean found = false;
                        // 尝试找到之前选择的账本
                        for (MyLedgerData ledger : ledgerList) {
                            if (ledger.getId().equals(lastSelectedLedgerId)) {
                                current_ledger = ledger;
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            current_ledger = null;
                        }
                    }

                    // 如果没有上次选择的账本，使用默认账本
                    if (current_ledger == null) {
                        for (MyLedgerData ledger : ledgerList) {
                            if (ledger.isDefault()) {
                                current_ledger = ledger;
                                break;
                            }
                        }
                    }

                    if (current_ledger != null) {
                        ledgerName.setText(current_ledger.getName());
                        int resourceId = getContext().getResources().getIdentifier(
                                current_ledger.getImage(), "drawable", getContext().getPackageName());
                        ledgerIcon.setImageResource(resourceId);
                    }

                    callback.onLedgerSet();
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

    private void setMonthlyTable() {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        Map<String, String> map = Map.of("year", String.valueOf(currentDate.get(Calendar.YEAR)),
                "month", String.valueOf(currentDate.get(Calendar.MONTH) + 1),
                "ledger_id", current_ledger.getId());

        Call<ApiModels.ApiResponse<ApiModels.ReportResponse>> call = apiService.getMonthlyReport(map);

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<ApiModels.ReportResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.ReportResponse>> call, Response<ApiModels.ApiResponse<ApiModels.ReportResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.ReportResponse data = response.body().getData();
                    totalIncome.setText(data.getIncome());
                    totalExpense.setText(data.getExpense());
                    double calc_balance = Double.parseDouble(totalIncome.getText().toString()) - Double.parseDouble(totalExpense.getText().toString());
                    calc_balance = Math.round(calc_balance * 100) / 100.0;
                    balance.setText(String.valueOf(calc_balance));
                } else {
                    Toast.makeText(getContext(), "获取月度报表失败" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.ReportResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "获取月度报表失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCards() {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);

        // 获取currentDate当前月份的第一天和最后一天
        Calendar calendar = (Calendar) currentDate.clone();  // 克隆 currentDate，避免修改原对象
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String date_after = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String date_before = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        // 构建查询参数
        Map<String, String> map = new HashMap<>();
        map.put("ledger_id", current_ledger.getId());
        map.put("date_after", date_after);
        map.put("date_before", date_before);
        map.put("ordering", "-date");

        Call<ApiModels.ApiResponse<List<MyBillData>>> call = apiService.getBills(map);

        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<List<MyBillData>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Response<ApiModels.ApiResponse<List<MyBillData>>> response) {
                if (response.isSuccessful()) {
                    cardDataList = new ArrayList<>();
                    List<MyBillData> billList = response.body().getData();
                    Map<String, List<MyBillData>> groupedBills = new HashMap<>();

                    // 遍历账单列表，按日期分组
                    for (MyBillData bill : billList) {
                        String billDate = bill.getDate();
                        if (!groupedBills.containsKey(billDate)) {
                            groupedBills.put(billDate, new ArrayList<>());
                        }
                        groupedBills.get(billDate).add(bill);
                    }

                    for (Map.Entry<String, List<MyBillData>> entry : groupedBills.entrySet()) {
                        String date = entry.getKey();
                        List<MyBillData> bills = entry.getValue();

                        int income = 0;
                        int expense = 0;

                        for (MyBillData bill : bills) {
                            if (bill.getCategory().getInOutType() == 1) {
                                income += bill.getAmountShort();
                            } else {
                                expense += bill.getAmountShort();
                            }
                        }

                        cardDataList.add(new MyCardData(date, income, expense, bills));
                    }

                    outerRecyclerView.setAdapter(new MyCardAdapter(cardDataList, DetailFragment.this));
                } else {
                    Toast.makeText(getContext(), "获取账单信息失败" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Throwable t) {
                Toast.makeText(getContext(), "获取账单信息失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void refresh() {
        setMonthlyTable();
        setCards();
    }


    //TODO:后端返回格式

    @Override
    public void onResume() {
        super.onResume();

        initDate();
        initDateBtn();

        // Ledger设置完成后，再调用setMonthlyTable
        setLedger(() -> {
            setMonthlyTable();
            initDropdownIcon();
            setCards();
        });
    }
}
