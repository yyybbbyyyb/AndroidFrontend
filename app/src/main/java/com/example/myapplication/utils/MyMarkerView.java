package com.example.myapplication.utils;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.example.myapplication.R;

public class MyMarkerView extends MarkerView {

    private final TextView tvContent;

    public MyMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // 初始化自定义的MarkerView布局中的TextView
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        BarEntry barEntry = (BarEntry) e;
        int day = (int) barEntry.getX();
        float amount = barEntry.getY();

        // 设置MarkerView显示的内容
        tvContent.setText("日期: " + day + "\n金额: " + amount);

        super.refreshContent(e, highlight);
    }


}
