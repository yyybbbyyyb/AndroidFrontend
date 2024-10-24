package com.example.myapplication.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {


    public static void hideKeyboard(View view, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  // 隐藏键盘
        }
    }


    public static String processAvatarUrl(String avatarUrl) {
        // 替换 localhost 为 10.0.2.2
        return avatarUrl.replace("localhost", "10.0.2.2");
    }

    public static boolean isEmailValid(String email) {
        // 邮箱正则表达式
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(emailPattern);

        // 匹配输入的邮箱字符串
        Matcher matcher = pattern.matcher(email);

        // 返回是否匹配
        return matcher.matches();
    }


}
