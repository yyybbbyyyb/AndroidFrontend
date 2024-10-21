package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity {

    private ImageView avatarImageView, sexImageView, changeSexIcon,
            changePhoneIcon, changeEmailIcon, returnButton;
    private TextView tvUsername, tvPhone, tvEmail;
    private boolean isMale = true; // 默认性别
    private LinearLayout bnt_logout;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        // 初始化控件
        avatarImageView = findViewById(R.id.img_avatar);
        sexImageView = findViewById(R.id.sex);
        changeSexIcon = findViewById(R.id.change_sex);
        changePhoneIcon = findViewById(R.id.change_phone);
        changeEmailIcon = findViewById(R.id.change_email);
        tvUsername = findViewById(R.id.tv_username);
        tvPhone = findViewById(R.id.phone);
        tvEmail = findViewById(R.id.email);
        bnt_logout = findViewById(R.id.btn_logout);

        getUserInfo();

        returnButton = findViewById(R.id.btn_back);
        returnButton.setOnClickListener(v -> finish());

        avatarImageView.setOnClickListener(v -> showAvatarOptions());

        // 2. 点击右上角的三个点，修改密码
        ImageView btnMore = findViewById(R.id.btn_more);
        btnMore.setOnClickListener(v -> showMoreOptions());

        // 3. 切换性别
        changeSexIcon.setOnClickListener(v -> changeGender());

        // 4. 修改手机号
        changePhoneIcon.setOnClickListener(v -> showEditDialog("修改手机号", "请输入新手机号", tvPhone));

        // 5. 修改邮箱
        changeEmailIcon.setOnClickListener(v -> showEditDialog("修改邮箱", "请输入新邮箱", tvEmail));

        bnt_logout.setOnClickListener(v -> {
            logout();
        });
    }

    private void logout() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("my_shared_pref", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);

        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Map<String, String> map = new HashMap<>();
        map.put("refresh_token", refreshToken);
        Call<ApiModels.ApiResponse<Objects>> call = apiService.logout(map);
        call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserInfoActivity.this, "退出登录成功", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("access_token");
                    editor.remove("refresh_token");
                    editor.apply();

                    Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UserInfoActivity.this, "退出登录失败: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                Toast.makeText(UserInfoActivity.this, "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showAvatarOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_avatar_options, null);
        bottomSheetDialog.setContentView(sheetView);

        // 取消按钮
        sheetView.findViewById(R.id.btn_cancel).setOnClickListener(v -> bottomSheetDialog.dismiss());

        // 选择图片按钮（这里可以调用选择图片逻辑，后续上传图片接口）
        sheetView.findViewById(R.id.btn_select_image).setOnClickListener(v -> {
            requestStoragePermission();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            uploadAvatar(selectedImageUri);
        }
    }

    private void uploadAvatar(Uri imageUri) {
        // 将Uri转换为文件
        File imageFile = new File(getRealPathFromURI(imageUri));

        // 创建RequestBody用于文件上传
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);

        Log.d("uploadAvatar", "uploadAvatar: " + imageFile.getName());

        // 创建MultipartBody.Part，"avatar" 是服务器要求的字段名
        MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", imageFile.getName(), requestFile);

        // 创建API服务
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        // 发起上传请求
        Call<ApiModels.ApiResponse<Objects>> call = apiService.updateUserInfo(avatarPart);
        call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserInfoActivity.this, "头像上传成功", Toast.LENGTH_SHORT).show();
                    // 更新用户信息，重新加载头像
                    getUserInfo();
                } else {
                    Toast.makeText(UserInfoActivity.this, "头像上传失败", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                Toast.makeText(UserInfoActivity.this, "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("uploadAvatar", "onFailure: " + t.getMessage());
            }
        });
    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        return null;
    }

    private void requestStoragePermission() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // 请求存储权限
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            // 权限已经被授予，执行选择图片逻辑
            openImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予
                openImagePicker();
            } else {
                Toast.makeText(this, "存储权限被拒绝", Toast.LENGTH_LONG).show();
            }
        }
    }

    // 调用图片选择逻辑
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // 2. 显示修改密码的弹出框
    private void showMoreOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.dialog_more_options, null);
        bottomSheetDialog.setContentView(sheetView);

        // 取消按钮
        sheetView.findViewById(R.id.btn_cancel).setOnClickListener(v -> bottomSheetDialog.dismiss());

        // 修改密码按钮
        sheetView.findViewById(R.id.btn_change_password).setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            showChangePasswordDialog();
        });

        bottomSheetDialog.show();
    }

    // 2.1 显示修改密码的Dialog
    private void showChangePasswordDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        TextInputEditText etCurrentPassword = dialogView.findViewById(R.id.et_current_password);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.et_new_password);

        builder.setTitle("修改密码");
        builder.setPositiveButton("确认", (dialog, which) -> {
            String currentPassword = etCurrentPassword.getText().toString();
            String newPassword = etNewPassword.getText().toString();

            if (currentPassword.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
            } else if (currentPassword.equals(newPassword)) {
                Toast.makeText(this, "新密码不能与旧密码相同", Toast.LENGTH_LONG).show();
            } else if (newPassword.length() < 6 || newPassword.length() > 20) {
                Toast.makeText(this, "密码长度应在6-20位之间", Toast.LENGTH_LONG).show();
            } else {
                ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
                Map<String, String> map = new HashMap<>();
                map.put("current_password", currentPassword);
                map.put("new_password", newPassword);
                Call<ApiModels.ApiResponse<Objects>> call = apiService.updateUserInfo(map);
                call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
                    @Override
                    public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(UserInfoActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                String error = response.errorBody().string();
                                Gson gson = new Gson();
                                ApiModels.ApiResponse<Objects> apiResponse = gson.fromJson(error, ApiModels.ApiResponse.class);
                                Map<String, List<String>> errors = (Map<String, List<String>>) apiResponse.getErrors();
                                List<String> pwd_errors = errors.get("current_password");
                                Toast.makeText(UserInfoActivity.this, "修改密码失败: " + pwd_errors.get(0), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(UserInfoActivity.this, "修改密码失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                        Toast.makeText(UserInfoActivity.this, "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // 4 & 5. 显示修改手机号和邮箱的Dialog
    private void showEditDialog(String title, String hint, TextView textView) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_text, null);
        builder.setView(dialogView);

        TextInputEditText editText = dialogView.findViewById(R.id.edit_text);
        editText.setHint(hint);

        builder.setTitle(title);
        builder.setPositiveButton("确认", (dialog, which) -> {
            String newText = editText.getText().toString();
            Map<String, String> map = new HashMap<>();

            if (title.equals("修改手机号")) {
                if (newText.length() != 11) {
                    Toast.makeText(this, "手机号格式错误", Toast.LENGTH_LONG).show();
                    return;
                }
                map.put("phone", newText);
            } else {
                if (!Utils.isEmailValid(newText)) {
                    return;
                }
                map.put("email", newText);
            }

            ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
            Call<ApiModels.ApiResponse<Objects>> call = apiService.updateUserInfo(map);
            call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
                @Override
                public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                    if (response.isSuccessful()) {
                        textView.setText(newText);
                        Toast.makeText(UserInfoActivity.this, title + "修改成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserInfoActivity.this, title + "修改失败: " + response.message(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                    Toast.makeText(UserInfoActivity.this, title + "修改失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void changeGender() {
        isMale = !isMale;

        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);
        Map<String, String> map = new HashMap<>();
        map.put("gender", isMale ? "M" : "F");
        Call<ApiModels.ApiResponse<Objects>> call = apiService.updateUserInfo(map);
        call.enqueue(new Callback<ApiModels.ApiResponse<Objects>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<Objects>> call, Response<ApiModels.ApiResponse<Objects>> response) {
                if (response.isSuccessful()) {
                    getUserInfo();
                } else {
                    Toast.makeText(UserInfoActivity.this, "请求失败: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<Objects>> call, Throwable t) {
                Toast.makeText(UserInfoActivity.this, "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserInfo() {
        ApiService apiService = ApiClient.getClient(getApplicationContext()).create(ApiService.class);

        Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call = apiService.getUserInfo();
        call.enqueue(new Callback<ApiModels.ApiResponse<ApiModels.UserInfoResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call, Response<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.UserInfoResponse userInfoResponse = response.body().getData();
                    tvUsername.setText(userInfoResponse.getUsername());

                    Picasso.get().load(Utils.processAvatarUrl(userInfoResponse.getAvatar()))
                            .into(avatarImageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    Log.d("Picasso", "Image loaded successfully.");
                                }

                                @Override
                                public void onError(Exception e) {
                                    Log.e("Picasso", "Error loading image", e);
                                }
                            });
                    isMale = userInfoResponse.getGender().equals("M");
                    setChangeSexIcon();
                    setTvPhone(userInfoResponse.getPhone());
                    setTvEmail(userInfoResponse.getEmail());
                } else {
                    Toast.makeText(UserInfoActivity.this, "请求失败: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call, Throwable t) {
                Toast.makeText(UserInfoActivity.this, "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setChangeSexIcon() {
        if (isMale) {
            sexImageView.setImageResource(R.drawable.male_24px);
        } else {
            sexImageView.setImageResource(R.drawable.female_24px);
        }
    }

    public void setTvEmail(String email) {
        if (email == null || email.isEmpty()) {
            tvEmail.setText(R.string.email_default);
        } else {
            tvEmail.setText(email);
        }
    }

    public void setTvPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            tvPhone.setText(R.string.phone_default);
        } else {
            tvPhone.setText(phone);
        }
    }
}
