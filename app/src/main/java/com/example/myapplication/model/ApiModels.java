package com.example.myapplication.model;

public class ApiModels {
    // 登录请求模型
    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        // getters and setters
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

}
