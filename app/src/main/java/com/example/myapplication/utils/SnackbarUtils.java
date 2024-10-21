package com.example.myapplication.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

public class SnackbarUtils {

    /**
     * 显示自定义 Snackbar
     *
     * @param view            Snackbar 依附的根视图 (必传)
     * @param message         显示的消息内容 (必传)
     * @param duration        Snackbar 显示时间 (默认值为 3000 毫秒)
     * @param backgroundColor 背景颜色 (默认值为 Color.BLACK)
     * @param textColor       文本颜色 (默认值为 Color.WHITE)
     * @param textSize        文本大小 (默认值为 16f)
     * @param boldText        是否加粗 (默认值为 false)
     * @param isAtTop         是否显示在顶部（默认值为 false, 显示在底部）
     * @param centerAligned   是否启用居中适配 (默认值为 false)
     */
    public static void showCustomSnackbar(View view, String message,
                                          int duration, int backgroundColor,
                                          int textColor, float textSize,
                                          boolean boldText, boolean isAtTop,
                                          boolean centerAligned) {
        // 创建 Snackbar
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(duration);  // 设置自定义时长

        // 获取 Snackbar 的根视图
        View snackbarView = snackbar.getView();

        // 设置背景颜色
        snackbarView.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));  // 设置背景颜色

        // 获取 Snackbar 的 TextView 并设置自定义样式
        TextView snackbarTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarTextView.setTextColor(textColor);  // 设置文字颜色
        snackbarTextView.setTextSize(textSize);    // 设置文字大小
        if (boldText) {
            snackbarTextView.setTypeface(null, Typeface.BOLD);  // 设置加粗
        }

        // 设置 Snackbar 的宽度为内容适配大小，并居中
        if (centerAligned) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.width = FrameLayout.LayoutParams.WRAP_CONTENT;  // 宽度适应文字
            params.gravity = Gravity.CENTER_HORIZONTAL | (isAtTop ? Gravity.TOP : Gravity.BOTTOM);  // 水平居中，并根据需要显示在顶部或底部
            snackbarView.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = (isAtTop ? Gravity.TOP : Gravity.BOTTOM);  // 默认位置，顶部或底部
            snackbarView.setLayoutParams(params);
        }

        // 显示 Snackbar
        snackbar.show();
    }
}
