package com.example.myapplication.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MyBillData;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseReportAdapter extends RecyclerView.Adapter<ExpenseReportAdapter.ViewHolder> {

    private HashMap<String, List<MyBillData>> billDataMap;

    public ExpenseReportAdapter(HashMap<String, List<MyBillData>> billDataMap) {
        this.billDataMap = billDataMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = (String) billDataMap.keySet().toArray()[position];
        List<MyBillData> billDataLists = billDataMap.get(title);
        holder.bind(billDataLists, title);
    }

    @Override
    public int getItemCount() {
        return billDataMap.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private PieChart pieChart;
        private TextView tv_title;

        ViewHolder(View itemView) {
            super(itemView);
            pieChart = itemView.findViewById(R.id.pie_chart);
            tv_title = itemView.findViewById(R.id.tv_title);
        }

        void bind(List<MyBillData> billData, String title) {
            // 使用之前提供的setupPieChart方法初始化PieChart
            setupPieChart(pieChart, billData);
            tv_title.setText(title);
        }

        private void setupPieChart(PieChart pieChart, List<MyBillData> billData) {
            Map<MyBillData.Category, Float> categoryData = new HashMap<>();
            List<Integer> pieColors = new ArrayList<>();
            List<LegendEntry> legendEntries = new ArrayList<>();

            for (MyBillData bill : billData) {
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
                pieEntries.add(new PieEntry(entry.getValue(), ""));
                pieColors.add(entry.getKey().getColor());
                // 创建图例项
                LegendEntry legendEntry = new LegendEntry();
                legendEntry.label = entry.getKey().getType(); // 图例的标签
                legendEntry.formColor = entry.getKey().getColor(); // 图例的颜色
                legendEntries.add(legendEntry);
            }

            // 设置数据集
            PieDataSet dataSet = new PieDataSet(pieEntries, "支出分类详情");
            dataSet.setColors(pieColors); // 设置颜色
            dataSet.setSliceSpace(3f); // 每个扇形之间的间隔
            dataSet.setSelectionShift(0f); // 选中时扇形的偏移量
            dataSet.setValueLinePart1Length(0.6f); // 第一段连线的长度
            dataSet.setValueLinePart2Length(0.3f); // 第二段连线的长度
            dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE); // 把值和标签放到扇形外面
            dataSet.setValueLineColor(Color.GRAY); // 连线的颜色

            PieData data = new PieData(dataSet);
            data.setDrawValues(true);
            data.setValueTextSize(14f);
            data.setValueTextColor(Color.GRAY);

            // 使用PercentFormatter来显示百分比
            data.setValueFormatter(new PercentFormatter());

            // 设置PieChart
            pieChart.setData(data);
            pieChart.setUsePercentValues(true); // 显示百分比


            pieChart.setDrawHoleEnabled(true); // 设置中间有洞
            pieChart.setHoleRadius(70f); // 设置洞的大小，增大洞的大小使得有颜色的部分变细
            pieChart.setHoleColor(Color.WHITE); // 设置洞的颜色


            pieChart.setCenterTextSize(26f); // 设置中心文本大小
            pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD); // 设置中心文本加粗

            float total = 0;
            for (MyBillData bill : billData) {
                total += bill.getAmountActual();
            }

            pieChart.setCenterText("-" + total + ""); // 设置中心文本
            pieChart.setCenterTextColor(Color.BLACK); // 设置中心文本颜色


            // 启用图例并设置样式
            pieChart.getLegend().setEnabled(true);
            pieChart.getLegend().setTextColor(Color.BLACK);
            pieChart.getLegend().setTextSize(14f);
            pieChart.getLegend().setFormSize(14f);
            pieChart.getLegend().setFormToTextSpace(6f);
            pieChart.getLegend().setXEntrySpace(10f);
            pieChart.getLegend().setYEntrySpace(5f);
            pieChart.getLegend().setCustom(legendEntries); // 使用自定义的图例项

            pieChart.getDescription().setEnabled(false);

            pieChart.setExtraOffsets(36, 36, 36, 36); // 设置上下左右的偏移量（单位为像素）

            pieChart.invalidate(); // 刷新图表
        }
    }
}
