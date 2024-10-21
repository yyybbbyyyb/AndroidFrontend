package com.example.myapplication.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.utils.SnackbarUtils;

public class DialogUtils {

    // 展示二维码对话框
    public static void showQRCodeDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_qr_code, null);
        builder.setView(dialogView);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();


        // 点击外部区域关闭对话框
        dialogView.setOnClickListener(v -> dialog.dismiss());
    }

    // 展示评分对话框
    public static void showRatingDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rating, null);
        builder.setView(dialogView);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        // 添加按钮事件逻辑（例如：五星好评和我不喜欢按钮）
        dialogView.findViewById(R.id.btn_rate_now).setOnClickListener(v -> {
            // 处理五星好评事件
            SnackbarUtils.showCustomSnackbar(
                    activity.findViewById(android.R.id.content),
                    "感谢您的五星好评！",
                    3000,
                    Color.WHITE,
                    Color.GREEN,
                    20,
                    true,
                    false,
                    true
            );
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_not_now).setOnClickListener(v -> {
            // 处理我不喜欢事件
            SnackbarUtils.showCustomSnackbar(
                    activity.findViewById(android.R.id.content),
                    "感谢您的反馈，我们会继续努力！",
                    3000,
                    Color.WHITE,
                    Color.RED,
                    20,
                    true,
                    false,
                    true
            );
            dialog.dismiss();
        });
    }
}
