package com.example.myapplication.model;

public class MyBudgetData {
    private int id;
    private int ledger;
    private double amount;
    private int month;
    private int year;
    private MyBillData.Category category;
    private double total_expense;

    public MyBudgetData(int id, int ledger, double amount, int month, int year, MyBillData.Category category) {
        this.id = id;
        this.ledger = ledger;
        this.amount = amount;
        this.month = month;
        this.year = year;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public int getLedger() {
        return ledger;
    }

    public double getAmount() {
        return amount;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public MyBillData.Category getCategory() {
        return category;
    }

    public double getTotalExpense() {
        return total_expense;
    }

    public void setTotalExpense(double total_expense) {
        this.total_expense = total_expense;
    }

}
