package com.example.myapplication.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.BillInfoActivity;
import com.example.myapplication.adapter.MyBillCategoryAdapter;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillCategoryData;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.model.MyLedgerData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.utils.MyMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class ChartFragment extends Fragment {

    final int AUTO_DISMISS_DELAY = 2000; // 设置自动消失时间，单位毫秒

    private Calendar currentDate;
    private int ledger_id;
    private int inOut = 1; // 1: 收入，2: 支出
    private List<MyBillData> myBillDataList = new ArrayList<>();

    ImageView monthLeft, monthRight;
    TextView monthText;

    TextView inOutText;
    ImageView changeInOut;

    LinearLayout ll_income, ll_expense;
    TextView tv_income, tv_expense, tv_balance, tv_daily_expense;

    BarChart barChart;
    PieChart pieChart;
    RecyclerView recyclerView;

    public ChartFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        initViews(view);
        initDate();
        initDateBtn();
        setInOut();
        setInOutBtn();

        setLedger(() -> {
            setDataCard();
            getBillData(() -> {
                setupBarChart();
                setupPieChart();
                setLL();
            });

        });


        return view;
    }

    private void initViews(View view) {
        // 初始化视图
        monthLeft = view.findViewById(R.id.calendar_icon_left);
        monthRight = view.findViewById(R.id.calendar_icon_right);
        monthText = view.findViewById(R.id.current_month);
        inOutText = view.findViewById(R.id.inOut);
        changeInOut = view.findViewById(R.id.change_circle);
        ll_income = view.findViewById(R.id.ll_income);
        ll_expense = view.findViewById(R.id.ll_expense);
        tv_income = view.findViewById(R.id.tv_income);
        tv_expense = view.findViewById(R.id.tv_expense);
        tv_balance = view.findViewById(R.id.tv_balance);
        tv_daily_expense = view.findViewById(R.id.tv_daily_expense);
        barChart = view.findViewById(R.id.bar_chart);
        pieChart = view.findViewById(R.id.pie_chart);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // 设置布局管理器
    }

    private void initDate() {
        currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH) + 1;
        monthText.setText(year + "-" + month);
    }

    private void initDateBtn() {
        monthLeft.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, -1);
            int year = currentDate.get(Calendar.YEAR);
            int month = currentDate.get(Calendar.MONTH) + 1;
            monthText.setText(year + "-" + String.format("%02d", month));

            monthRight.setColorFilter(getResources().getColor(R.color.black));

            refreshData();
        });

        monthRight.setOnClickListener(v -> {
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
                monthRight.setColorFilter(getResources().getColor(R.color.light_gray));
            }

            refreshData();
        });

        monthRight.setColorFilter(getResources().getColor(R.color.light_gray));

    }

    private void setInOut() {
        if (inOut == 2) {
            inOutText.setText("支出");
            inOutText.setTextColor(getResources().getColor(R.color.light_red));
        } else {
            inOutText.setText("收入");
            inOutText.setTextColor(getResources().getColor(R.color.light_green));
        }
    }

    private void setInOutBtn() {
        changeInOut.setOnClickListener(v -> {
            if (inOut == 1) {
                inOut = 2;
            } else {
                inOut = 1;
            }
            setInOut();
            refreshData();
        });
    }

    @FunctionalInterface
    interface LedgerCallback {
        void onLedgerSet();
    }

    @FunctionalInterface
    interface BillCallback {
        void onBillSet();
    }

    private void setLedger(LedgerCallback callback) {
        // 从 SharedPreferences 获取上次选择的账本 ID
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE);
        String lastSelectedLedgerId = sharedPreferences.getString("last_selected_ledger_id", null);

        if (lastSelectedLedgerId == null) {
            Toast.makeText(getContext(), "请先选择账本", Toast.LENGTH_SHORT).show();
        }

        ledger_id = Integer.parseInt(lastSelectedLedgerId);

        callback.onLedgerSet();
    }

    private void setDataCard() {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        Map<String, String> map = Map.of("year", String.valueOf(currentDate.get(Calendar.YEAR)),
                "month", String.valueOf(currentDate.get(Calendar.MONTH) + 1),
                "ledger_id", ledger_id + "");
        Call<ApiModels.ApiResponse<ApiModels.ReportResponse>> call = apiService.getMonthlyReport(map);
        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<ApiModels.ReportResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.ReportResponse>> call, Response<ApiModels.ApiResponse<ApiModels.ReportResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.ReportResponse data = response.body().getData();
                    tv_income.setText(data.getIncome());
                    tv_expense.setText(data.getExpense());
                    double calc_balance = Double.parseDouble(tv_income.getText().toString()) - Double.parseDouble(tv_expense.getText().toString());
                    Log.d("calc_balance", String.valueOf(calc_balance));


                    calc_balance = Math.round(calc_balance * 100) / 100.0;
                    tv_balance.setText(String.valueOf(calc_balance));
                    int daysInMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                    double calc_daily_expense = calc_balance / daysInMonth;
                    calc_daily_expense = Math.round(calc_daily_expense * 100) / 100.0;
                    tv_daily_expense.setText(String.valueOf(calc_daily_expense));
                } else {
                    try {
                        String error = response.errorBody().string();
                        Toast.makeText(getContext(), "获取月度报表失败" + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.ReportResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "获取月度报表失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBillData(BillCallback callback) {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);

        // 获取currentDate当前月份的第一天和最后一天
        Calendar calendar = (Calendar) currentDate.clone();  // 克隆 currentDate，避免修改原对象
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String date_after = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        String date_before = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        // 构建查询参数
        Map<String, String> map = new HashMap<>();
        map.put("ledger_id", ledger_id + "");
        map.put("date_after", date_after);
        map.put("date_before", date_before);
        map.put("ordering", "date");
        map.put("inOutType", inOut + "");

        Call<ApiModels.ApiResponse<List<MyBillData>>> call = apiService.getBills(map);
        call.enqueue(new retrofit2.Callback<ApiModels.ApiResponse<List<MyBillData>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Response<ApiModels.ApiResponse<List<MyBillData>>> response) {
                if (response.isSuccessful()) {
                    myBillDataList = response.body().getData();
                    callback.onBillSet();
                } else {
                    try {
                        String error = response.errorBody().string();
                        Toast.makeText(getContext(), "获取账单数据失败" + error, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Throwable t) {
                Toast.makeText(getContext(), "获取账单数据失败" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupBarChart() {
        Map<String, List<MyBillData>> groupedBills = new HashMap<>();
        Map<String, Double> dailyMoney = new HashMap<>();

        // 遍历账单列表，按日期分组
        for (MyBillData bill : myBillDataList) {
            String billDate = bill.getDate();
            if (!groupedBills.containsKey(billDate)) {
                groupedBills.put(billDate, new ArrayList<>());
                dailyMoney.put(billDate, 0.0);
            }
            groupedBills.get(billDate).add(bill);
            dailyMoney.put(billDate, dailyMoney.get(billDate) + bill.getAmountActual());
        }

        int daysInMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);

        List<BarEntry> entries = new ArrayList<>();
        List<BarEntry> actualData = new ArrayList<>();
        for (int day = 1; day <= daysInMonth; day++) {
            String date = String.format("%d-%02d-%02d", currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH) + 1, day);
            if (!groupedBills.containsKey(date)) {
                entries.add(new BarEntry(day, 0)); // 默认为灰色
            } else {
                actualData.add(new BarEntry(day, dailyMoney.get(date).floatValue()));
            }
        }

        // 设置灰色填充的条目
        BarDataSet backgroundDataSet = new BarDataSet(entries, "背景");
        backgroundDataSet.setColor(getResources().getColor(R.color.light_gray)); // 设置灰色
        backgroundDataSet.setDrawValues(false); // 隐藏顶部的数值

        // 设置实际数据的条目
        BarDataSet actualDataSet = new BarDataSet(actualData, "每日开销");
        actualDataSet.setColor(getResources().getColor(R.color.md_theme_primaryFixedDim)); // 设置主题颜色
        actualDataSet.setDrawValues(false); // 隐藏顶部数值

        // 合并数据集
        BarData barData = new BarData(backgroundDataSet, actualDataSet);
        barData.setBarWidth(0.6f);

        barChart.setData(barData);

        // 隐藏左右Y轴
        barChart.getAxisLeft().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);

        // 隐藏网格线、图例和描述
        barChart.getXAxis().setDrawGridLines(false);
        barChart.getDescription().setEnabled(false); // 再次禁用描述
        barChart.getLegend().setEnabled(false); // 再次禁用图例
        barChart.invalidate(); // 刷新图表

        // 使用自定义 MarkerView
        MyMarkerView markerView = new MyMarkerView(getContext(), R.layout.layout_marker_view);
        markerView.setChartView(barChart); // 设置MarkerView的Chart
        barChart.setMarker(markerView); // 将自定义MarkerView设置到BarChart
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                // 启动自动消失任务
                barChart.postDelayed(() -> barChart.highlightValue(null), AUTO_DISMISS_DELAY);
            }

            @Override
            public void onNothingSelected() {
                // 当没有选中的条目时，MarkerView自动消失
                markerView.setVisibility(View.GONE);
            }
        });


        // 禁用拖动手势，避免与ScrollView滑动冲突
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(false); // 禁用拖动
        barChart.setScaleEnabled(false); // 禁用缩放
        barChart.setPinchZoom(false); // 禁用捏合缩放

    }

    private void setupPieChart() {
        Map<MyBillData.Category, Float> categoryData = new HashMap<>();
        List<Integer> pieColors = new ArrayList<>();

        for (MyBillData bill : myBillDataList) {
            MyBillData.Category category = bill.getCategory();
            float amount = (float) bill.getAmountActual();
            if (!categoryData.containsKey(category)) {
                categoryData.put(category, amount);
            } else {
                categoryData.put(category, categoryData.get(category) + amount);
            }
        }

        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<MyBillData.Category, Float> entry : categoryData.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
            pieColors.add(entry.getKey().getColor());
        }

        // 设置数据集
        PieDataSet dataSet = new PieDataSet(pieEntries, "支出分类详情");
        dataSet.setColors(pieColors); // 设置颜色
        dataSet.setSliceSpace(3f); // 每个扇形之间的间隔
        dataSet.setSelectionShift(0f); // 选中时扇形的偏移量
        dataSet.setValueLinePart1Length(0.6f); // 第一段连线的长度
        dataSet.setValueLinePart2Length(0.3f); // 第二段连线的长度
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // 把值和标签放到扇形外面
        dataSet.setValueLineColor(getResources().getColor(R.color.gray)); // 连线颜色

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueTextSize(14f);
        data.setValueTextColor(getResources().getColor(R.color.gray)); // 设置值的颜色

        // 使用PercentFormatter来显示百分比
        data.setValueFormatter(new PercentFormatter());

        // 设置PieChart
        pieChart.setData(data);
        pieChart.setUsePercentValues(true); // 显示百分比


        pieChart.setDrawHoleEnabled(true); // 设置中间有洞
        pieChart.setHoleRadius(70f); // 设置洞的大小，增大洞的大小使得有颜色的部分变细
        pieChart.setHoleColor(Color.WHITE); // 设置洞的颜色


        pieChart.setCenterTextSize(20f); // 设置中心文本大小
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD); // 设置中心文本加粗

        // 中间文本设置
        if (calculateTotal() == 0) {
            pieChart.setCenterText("暂无数据"); // 设置中心文本
            pieChart.setCenterTextColor(getResources().getColor(R.color.gray)); // 设置中心文本颜色
        } else {
            if (inOut == 1) {
                pieChart.setCenterText(calculateTotal() + ""); // 设置中心文本
                pieChart.setCenterTextColor(getResources().getColor(R.color.money_green)); // 设置中心文本颜色
            } else {
                pieChart.setCenterText("-" + calculateTotal() + ""); // 设置中心文本
                pieChart.setCenterTextColor(getResources().getColor(R.color.money_red)); // 设置中心文本颜色
            }
        }

        pieChart.getLegend().setEnabled(false); // 禁用图例
        pieChart.getDescription().setEnabled(false); // 禁用描述

        pieChart.setTouchEnabled(true); // 启用触摸事件
        pieChart.setDragDecelerationFrictionCoef(0.95f); // 设置旋转时的摩擦系数
        pieChart.setRotationEnabled(true); // 启用旋转
        pieChart.setHighlightPerTapEnabled(true); // 启用点击高亮

        pieChart.setExtraOffsets(8, 8, 8, 8); // 设置上下左右的偏移量（单位为像素）

        pieChart.invalidate(); // 刷新图表


        List<MyBillCategoryData> categoryDataList = new ArrayList<>();

        for (Map.Entry<MyBillData.Category, Float> entry : categoryData.entrySet()) {
            MyBillData.Category category = entry.getKey();
            float amount = entry.getValue();
            float percent = (amount / calculateTotal()) * 100; // 计算百分比
            categoryDataList.add(new MyBillCategoryData(category, (int) amount, percent));
        }

        MyBillCategoryAdapter adapter = new MyBillCategoryAdapter(categoryDataList);
        recyclerView.setAdapter(adapter); // 设置适配器

        adapter.setOnItemClickListener(new MyBillCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyBillCategoryData billCategoryData) {
                Intent intent = new Intent(getActivity(), BillInfoActivity.class);
                intent.putExtra("inOut", inOut);
                intent.putExtra("ledger_id", ledger_id);
                intent.putExtra("date_before", currentDate.get(Calendar.YEAR) + "-" + (currentDate.get(Calendar.MONTH) + 1) + "-" + currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
                intent.putExtra("date_after", currentDate.get(Calendar.YEAR) + "-" + (currentDate.get(Calendar.MONTH) + 1) + "-01");
                intent.putExtra("detail_type", billCategoryData.getCategory().getDetail_type());
                startActivity(intent);
            }
        });


    }

    private float calculateTotal() {
        float total = 0;
        for (MyBillData bill : myBillDataList) {
            total += bill.getAmountActual();
        }
        return total;
    }

    private void refreshData() {
        setDataCard();
        getBillData(() -> {
            setupBarChart();
            setupPieChart();
        });
    }

    private void setLL() {
        ll_income.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BillInfoActivity.class);
            intent.putExtra("inOut", 1);
            intent.putExtra("ledger_id", ledger_id);
            intent.putExtra("date_before", currentDate.get(Calendar.YEAR) + "-" + (currentDate.get(Calendar.MONTH) + 1) + "-" + currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            intent.putExtra("date_after", currentDate.get(Calendar.YEAR) + "-" + (currentDate.get(Calendar.MONTH) + 1) + "-01");
            startActivity(intent);
        });

        ll_expense.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BillInfoActivity.class);
            intent.putExtra("inOut", 2);
            intent.putExtra("ledger_id", ledger_id);
            intent.putExtra("date_before", currentDate.get(Calendar.YEAR) + "-" + (currentDate.get(Calendar.MONTH) + 1) + "-" + currentDate.getActualMaximum(Calendar.DAY_OF_MONTH));
            intent.putExtra("date_after", currentDate.get(Calendar.YEAR) + "-" + (currentDate.get(Calendar.MONTH) + 1) + "-01");
            startActivity(intent);
        });
    }
}
