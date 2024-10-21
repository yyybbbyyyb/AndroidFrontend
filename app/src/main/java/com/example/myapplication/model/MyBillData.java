package com.example.myapplication.model;

public class MyBillData {
    private String type;   // 账单的类型（如：餐饮、交通）
    private String note;   // 备注
    private double amount; // 金额
    private String logo;   // Logo

    // 构造函数
    public MyBillData(String type, String note, double amount, String logo) {
        this.type = type;
        this.note = note;
        this.amount = amount;
        this.logo = logo;
    }

    // Getter 和 Setter 方法
    public String getType() {
        return type;
    }

    public String getNote() {
        return note;
    }


    public double getAmount() {
        return amount;
    }

    public String getLogo() {
        return logo;
    }
}