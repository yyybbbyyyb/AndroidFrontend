package com.example.myapplication.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // 展示删除订单对话框
    public static void showDeleteConfirmDialog(Activity activity, int id, Runnable onDeleteConfirmed, Runnable onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirm_delete, null);
        builder.setView(dialogView);

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        // 调整对话框的宽度
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout((int) (activity.getResources().getDisplayMetrics().widthPixels * 0.7),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // 删除按钮事件
        dialogView.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            // 执行删除操作
            ApiService apiService = ApiClient.getClient(activity.getApplicationContext()).create(ApiService.class);

            Call<ApiModels.ApiResponse<Objects>> call = apiService.deleteBill(String.valueOf(id));
            call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
                @Override
                public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                    if (response.isSuccessful()) {
                        onDeleteConfirmed.run();  // 删除完成后回调刷新UI
                        Toast.makeText(activity, "删除成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                    Toast.makeText(activity, "网络错误", Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });

        // 当对话框关闭时，调用取消回调
        dialog.setOnDismissListener(d -> onCancel.run());
    }


}
