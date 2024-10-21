package com.example.myapplication.model;

public class ApiModels {

    // 通用API响应模型
    public class ApiResponse<T> {
        private String status;
        private String message;
        private T data;  // 如果是成功响应，包含数据
        private Object errors;  // 如果是失败响应，包含错误信息

        // Getter方法
        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }

        public Object getErrors() {
            return errors;
        }
    }

    // 登录请求模型
    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // 登录响应模型
    public static class LoginResponse {
        private String refresh;
        private String access;

        // getters and setters
        public String getRefresh() {
            return refresh;
        }

        public String getAccess() {
            return access;
        }
    }

    public static class UserInfoResponse {
        private String username;
        private String avatar;
        private String email;
        private String phone;
        private String gender;
        private String used_days;

        // getters and setters
        public String getUsername() {
            return username;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getGender() {
            return gender;
        }

        public String getUsedDays() {
            return used_days;
        }
    }

    public static class MonthlyReportResponse {
        private String income;
        private String expense;

        // getters and setters
        public String getIncome() {
            return income;
        }

        public String getExpense() {
            return expense;
        }
    }

}
