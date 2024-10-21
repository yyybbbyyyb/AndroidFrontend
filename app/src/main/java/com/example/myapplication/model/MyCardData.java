package com.example.myapplication.model;

import java.util.List;

public class MyCardData {
    private String date;    // 日期
    private int income;     // 收入
    private int expense;    // 支出
    private List<MyBillData> billList; // 账单列表

    public MyCardData(String date, int income, int expense, List<MyBillData> billList) {
        this.date = date;
        this.income = income;
        this.expense = expense;
        this.billList = billList;
    }

    public String getDate() {
        return date;
    }

    public int getIncome() {
        return income;
    }

    public int getExpense() {
        return expense;
    }

    public List<MyBillData> getBillList() {
        return billList;
    }

}