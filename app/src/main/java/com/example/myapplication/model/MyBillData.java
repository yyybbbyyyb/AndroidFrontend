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

        // 收入类别颜色数组 //TODO：这个要设置的好看点
        public static final int[] INCOME_COLORS = {
                Color.parseColor("#FFB74D"),  // 工资 - 橙色
                Color.parseColor("#4FC3F7"),  // 生活费 - 天蓝色
                Color.parseColor("#81C784"),  // 奖金 - 绿色
                Color.parseColor("#BA68C8"),  // 理财 - 紫色
                Color.parseColor("#FF8A65"),  // 收红包 - 浅红色
                Color.parseColor("#FFD54F"),  // 外快 - 黄色
                Color.parseColor("#4DB6AC"),  // 零花钱 - 青色
                Color.parseColor("#DCE775")   // 其他 - 浅绿
        };

        // 支出类别颜色数组
        public static final int[] EXPENSE_COLORS = {
                Color.parseColor("#E57373"),  // 餐饮 - 红色
                Color.parseColor("#64B5F6"),  // 交通 - 蓝色
                Color.parseColor("#81C784"),  // 日用品 - 绿色
                Color.parseColor("#4FC3F7"),  // 购物 - 天蓝色
                Color.parseColor("#FFB74D"),  // 零食 - 橙色
                Color.parseColor("#FF8A65"),  // 饮品 - 浅红色
                Color.parseColor("#BA68C8"),  // 蔬菜 - 紫色
                Color.parseColor("#FFD54F"),  // 水果 - 黄色
                Color.parseColor("#4DB6AC"),  // 服饰 - 青色
                Color.parseColor("#DCE775"),  // 娱乐 - 浅绿
                Color.parseColor("#FFB74D"),  // 美容 - 橙色
                Color.parseColor("#64B5F6"),  // 通讯 - 蓝色
                Color.parseColor("#81C784"),  // 医疗 - 绿色
                Color.parseColor("#4FC3F7"),  // 学习 - 天蓝色
                Color.parseColor("#FF8A65"),  // 游戏 - 浅红色
                Color.parseColor("#BA68C8"),  // 红包 - 紫色
                Color.parseColor("#FFD54F"),  // 婴用 - 黄色
                Color.parseColor("#4DB6AC"),  // 酒店 - 青色
                Color.parseColor("#DCE775"),  // 住房 - 浅绿
                Color.parseColor("#FFB74D"),  // 转账 - 橙色
                Color.parseColor("#64B5F6"),  // 社交 - 蓝色
                Color.parseColor("#81C784"),  // 礼品 - 绿色
                Color.parseColor("#4FC3F7"),  // 宠物 - 天蓝色
                Color.parseColor("#FF8A65"),  // 汽车 - 浅红色
                Color.parseColor("#BA68C8"),  // 数码 - 紫色
                Color.parseColor("#FFD54F"),  // 书籍 - 黄色
                Color.parseColor("#4DB6AC"),  // 追星 - 青色
                Color.parseColor("#DCE775"),  // 办公 - 浅绿
                Color.parseColor("#FFB74D"),  // 运动 - 橙色
                Color.parseColor("#64B5F6"),  // 捐赠 - 蓝色
                Color.parseColor("#81C784"),  // 金融 - 绿色
                Color.parseColor("#FF8A65")   // 其他 - 浅红色
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
