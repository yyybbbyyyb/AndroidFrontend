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

    public static class RequestCategory {
        private String inOutType;
        private String detail_type;

        public RequestCategory(String inOutType, String detail_type) {
            this.inOutType = inOutType;
            this.detail_type = detail_type;
        }

        // Getters and Setters
        public String getInOutType() {
            return inOutType;
        }

        public void setInOutType(String inOutType) {
            this.inOutType = inOutType;
        }

        public String getDetailType() {
            return detail_type;
        }

        public void setDetailType(String detail_type) {
            this.detail_type = detail_type;
        }
    }

    public static class BillCreateRequest {
        private int ledger;
        private RequestCategory category;
        private String amount;
        private String remark;
        private String date;

        public BillCreateRequest(int ledger, String amount, String remark, String date, String inOutType, String detailType) {
            this.ledger = ledger;
            this.category = new RequestCategory(inOutType, detailType);
            this.amount = amount;
            this.remark = remark;
            this.date = date;
        }

    }

    public static class BudgetCreateRequest {
        private int ledger;
        private String amount;
        private String month;
        private String year;
        private RequestCategory category;

        public BudgetCreateRequest(int ledger, String amount, String month, String year, String inOutType, String detailType) {
            this.ledger = ledger;
            this.amount = amount;
            this.month = month;
            this.year = year;
            this.category = new RequestCategory(inOutType, detailType);
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
        private String bill_count;

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

        public String getBillCount() {
            return bill_count;
        }
    }

    public static class ReportResponse {
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

    public static class TotalBudgetResponse {
        private String total_budget;
        // getters and setters
        public Double getTotalBudget() {
            return Double.parseDouble(total_budget);
        }
    }

    public static class TotalExpenseByCategoryResponse {
        private String total_expense;

        public Double getTotalExpense() {
            return Double.parseDouble(total_expense);
        }
    }

    public static class DailyReportResponse {
        private int day;
        private Double income;
        private Double expense;

        public int getDay() {
            return day;
        }

        public Double getIncome() {
            return Double.parseDouble(String.format("%.2f", income));
        }

        public Double getExpense() {
            return Double.parseDouble(String.format("%.2f", expense));
        }
    }

}
