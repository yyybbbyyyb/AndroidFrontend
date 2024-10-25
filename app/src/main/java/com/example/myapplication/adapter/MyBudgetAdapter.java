package com.example.myapplication.adapter;

import android.icu.text.UFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MyBillCategoryData;
import com.example.myapplication.model.MyBudgetData;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class MyBudgetAdapter extends RecyclerView.Adapter<MyBudgetAdapter.BudgetViewHolder> {

    List<MyBudgetData> dataList;
    private OnItemClickListener onItemClickListener;

    public MyBudgetAdapter(List<MyBudgetData> dataList) {
        this.dataList = dataList;
    }

    public interface OnItemClickListener {
        void onItemClick(MyBudgetData budgetData);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    //删除指定位置的项目
    public void removeItem(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);  // 通知 RecyclerView 数据已移除
    }

    public MyBudgetData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public BudgetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BudgetViewHolder holder, int position) {
        MyBudgetData data = dataList.get(position);

        // 动态获取图片资源ID并绑定到ImageView
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                data.getCategory().getImage(), "drawable", holder.itemView.getContext().getPackageName());

        holder.ivBudgetCategory.setImageResource(resourceId);

        holder.tv_typename.setText(data.getCategory().getType());

        holder.tv_info.setText("预算 " + String.format("%.2f", data.getAmount()) + " | " + "支出 " + String.format("%.2f", data.getTotalExpense()));

        if (data.getAmount() == 0) {
            holder.tv_rate.setText("0%");
            holder.tv_simple_info.setText("预算为0");
            holder.progressIndicator.setProgress(0);
        } else {
            int rate = (int) (data.getTotalExpense() / data.getAmount() * 100);
            double rate_double = data.getTotalExpense() / data.getAmount() * 100;
            holder.tv_rate.setText(String.format("%.2f", rate_double) + "%");
            if (rate > 100) {
                holder.tv_simple_info.setText("超支 " + String.format("%.2f", data.getTotalExpense() - data.getAmount()));
                holder.progressIndicator.setProgress(100);
                holder.tv_rate.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.light_red));
                holder.tv_simple_info.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.light_red));
                holder.progressIndicator.setIndicatorColor(holder.itemView.getContext().getResources().getColor(R.color.light_red));
            } else {
                holder.tv_simple_info.setText("剩余 " + String.format("%.2f", data.getAmount() - data.getTotalExpense()));
                holder.progressIndicator.setProgress(rate);
                if (rate > 60) {
                    holder.tv_rate.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.light_orange));
                    holder.tv_simple_info.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.light_orange));
                    holder.progressIndicator.setIndicatorColor(holder.itemView.getContext().getResources().getColor(R.color.light_orange));
                } else {
                    holder.tv_rate.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.light_green));
                    holder.tv_simple_info.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.light_green));
                    holder.progressIndicator.setIndicatorColor(holder.itemView.getContext().getResources().getColor(R.color.light_green));
                }

            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(data);
                }
            }
        });
    }


    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tv_typename, tv_rate, tv_simple_info, tv_info;
        ImageView ivBudgetCategory;
        LinearProgressIndicator progressIndicator;

        public BudgetViewHolder(View itemView) {
            super(itemView);
            tv_info = itemView.findViewById(R.id.tv_info);
            tv_simple_info = itemView.findViewById(R.id.tv_simple_info);
            tv_typename = itemView.findViewById(R.id.tv_typename);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            ivBudgetCategory = itemView.findViewById(R.id.img_logo);
            progressIndicator = itemView.findViewById(R.id.progress);

        }
    }
}
