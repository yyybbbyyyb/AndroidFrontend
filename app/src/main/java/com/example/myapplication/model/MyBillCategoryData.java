package com.example.myapplication.model;

public class MyBillCategoryData {

    private MyBillData.Category category;
    private int totalMoney;
    private float percent;

    public MyBillCategoryData(MyBillData.Category category, int totalMoney, float percent) {
        this.category = category;
        this.totalMoney = totalMoney;
        this.percent = percent;
    }

    public MyBillData.Category getCategory() {
        return category;
    }

    public float getPercent() {
        // 保留两位小数
        return (float) (Math.round(percent * 100) / 100.0);
    }

    public int getTotalMoney() {
        return totalMoney;
    }
}
