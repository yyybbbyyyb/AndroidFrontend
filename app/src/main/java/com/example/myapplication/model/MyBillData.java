package com.example.myapplication.model;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyBillData {
    private int id;
    private int ledger;
    private double amount;
    private String remark;
    private String date;
    private String create_time;
    private Category category;
    private String ledger_name;

    public MyBillData(int id, int ledger, double amount, String remark, String date, String created_time, Category category) {
        this.id = id;
        this.ledger = ledger;
        this.amount = amount;
        this.remark = remark;
        this.date = date;
        this.create_time = created_time;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public int getLedger() {
        return ledger;
    }

    public double getAmountShort() {
        return Math.round(amount * 10) / 10.0;
    }

    public double getAmountActual() {
        return amount;
    }

    public String getRemark() {
        return remark;
    }

    public String getDate() {
        return date;
    }

    public String getCreated_time() {
        // 正则表达式匹配 年-月-日T小时:分钟
        String regex = "(\\d{4}-\\d{2}-\\d{2})T(\\d{2}:\\d{2})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(create_time);

        if (matcher.find()) {
            // 提取匹配的日期和时间
            String date = matcher.group(1);
            String time = matcher.group(2);
            return date + " " + time;  // 格式为 "yyyy-MM-dd HH:mm"
        } else {
            return null;
        }
    }

    public String getLedger_name() {
        return ledger_name;
    }

    public Category getCategory() {
        return category;
    }

    public String getImage() {
        return "bill_" + category.getInOutType() + "_" + category.getDetail_type();
    }

    public String getType() {
        return category.getType();
    }

    public static class Category {
        private int inOutType;      // 1 为收入, 2 为支出
        private int detail_type;
        private String type;

        //改写判断相等的方法
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Category) {
                Category category = (Category) obj;
                return this.inOutType == category.inOutType && this.detail_type == category.detail_type;
            }
            return false;
        }

        //改写hashCode方法
        @Override
        public int hashCode() {
            return inOutType * 100 + detail_type;
        }

        public Category(int inOutType, int detail_type, String type) {
            this.inOutType = inOutType;
            this.detail_type = detail_type;
            this.type = type;
        }

        public int getInOutType() {
            return inOutType;
        }

        public int getColor() {
            if (inOutType == 1) {
                return INCOME_COLORS[detail_type - 1];
            } else {
                return EXPENSE_COLORS[detail_type - 1];
            }
        }

        public int getDetail_type() {
            return detail_type;
        }

        public String getType() {
            if (inOutType == 1) {
                if (detail_type >= 8) {
                    return "其他";
                }
                return INCOME[detail_type - 1];
            } else {
                if (detail_type >= 32) {
                    return "其他";
                }
                return EXPENSE[detail_type - 1];
            }
        }

        public String getImage() {
            return "bill_" + inOutType + "_" + detail_type;
        }


        public static final String[] INCOME = {
                "工资", "生活费", "奖金", "理财",
                "收红包", "外快", "零花钱", "其他"
        };

        // 收入类别颜色数组（蓝色调）
        public static final int[] INCOME_COLORS = {
                Color.parseColor("#E3F2FD"),  // 工资 - 最浅蓝
                Color.parseColor("#BBDEFB"),  // 生活费 - 浅蓝
                Color.parseColor("#90CAF9"),  // 奖金 - 蓝色
                Color.parseColor("#64B5F6"),  // 理财 - 中蓝色
                Color.parseColor("#42A5F5"),  // 收红包 - 深蓝色
                Color.parseColor("#2196F3"),  // 外快 - 标准蓝
                Color.parseColor("#1E88E5"),  // 零花钱 - 更深蓝
                Color.parseColor("#1976D2")   // 其他 - 最深蓝
        };

        // 支出类别颜色数组（粉色到紫色到红色渐变）
        public static final int[] EXPENSE_COLORS = {
                Color.parseColor("#F8BBD0"),  // 餐饮 - 浅粉
                Color.parseColor("#F48FB1"),  // 交通 - 粉色
                Color.parseColor("#F06292"),  // 日用品 - 深粉
                Color.parseColor("#EC407A"),  // 购物 - 中粉色
                Color.parseColor("#E91E63"),  // 零食 - 标准粉
                Color.parseColor("#D81B60"),  // 饮品 - 深粉
                Color.parseColor("#C2185B"),  // 蔬菜 - 更深粉
                Color.parseColor("#AD1457"),  // 水果 - 很深粉
                Color.parseColor("#880E4F"),  // 服饰 - 最深粉
                Color.parseColor("#CE93D8"),  // 娱乐 - 浅紫
                Color.parseColor("#BA68C8"),  // 美容 - 紫色
                Color.parseColor("#AB47BC"),  // 通讯 - 中紫色
                Color.parseColor("#9C27B0"),  // 医疗 - 标准紫
                Color.parseColor("#8E24AA"),  // 学习 - 深紫
                Color.parseColor("#7B1FA2"),  // 游戏 - 更深紫
                Color.parseColor("#6A1B9A"),  // 红包 - 很深紫
                Color.parseColor("#4A148C"),  // 婴用 - 最深紫
                Color.parseColor("#FF80AB"),  // 酒店 - 浅玫红
                Color.parseColor("#FF4081"),  // 住房 - 玫红
                Color.parseColor("#F50057"),  // 转账 - 标准玫红
                Color.parseColor("#C51162"),  // 社交 - 深玫红
                Color.parseColor("#FFCDD2"),  // 礼品 - 浅红
                Color.parseColor("#EF9A9A"),  // 宠物 - 红色
                Color.parseColor("#E57373"),  // 汽车 - 中红色
                Color.parseColor("#EF5350"),  // 数码 - 深红
                Color.parseColor("#F44336"),  // 书籍 - 标准红
                Color.parseColor("#E53935"),  // 追星 - 更深红
                Color.parseColor("#D32F2F"),  // 办公 - 很深红
                Color.parseColor("#C62828"),  // 运动 - 更深红
                Color.parseColor("#B71C1C"),  // 捐赠 - 最深红
                Color.parseColor("#FF1744"),  // 金融 - 红色调高亮
                Color.parseColor("#D50000")   // 其他 - 最深亮红
        };


        public static final String[] EXPENSE = {
                "餐饮", "交通", "日用品", "购物",
                "零食", "饮品", "蔬菜", "水果",
                "服饰", "娱乐", "美容", "通讯",
                "医疗", "学习", "游戏", "红包",
                "婴用", "酒店", "住房", "转账",
                "社交", "礼品", "宠物", "汽车",
                "数码", "书籍", "追星", "办公",
                "运动", "捐赠", "金融", "其他"
        };

    }

    public static Category getCategoryByType(int inOutType, int detail_type) {
        if (inOutType == 1) {
            if (detail_type >= 8) {
                return new Category(inOutType, detail_type, "其他");
            }
            return new Category(inOutType, detail_type, Category.INCOME[detail_type - 1]);
        } else {
            if (detail_type >= 32) {
                return new Category(inOutType, detail_type, "其他");
            }
            return new Category(inOutType, detail_type, Category.EXPENSE[detail_type - 1]);
        }
    }

    public static List<Category> getExpenseCategories() {
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i <= 32; i++) {
            categories.add(new Category(2, i, Category.EXPENSE[i - 1]));
        }
        return categories;
    }

    public static List<Category> getIncomeCategories() {
        List<Category> categories = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            categories.add(new Category(1, i, Category.INCOME[i - 1]));
        }
        return categories;
    }

    public static String getTypeName(int inOutType, int detail_type) {
        if (inOutType == 1) {
            if (detail_type >= 8) {
                return "其他";
            }
            return Category.INCOME[detail_type - 1];
        } else {
            if (detail_type >= 32) {
                return "其他";
            }
            return Category.EXPENSE[detail_type - 1];
        }
    }
}
