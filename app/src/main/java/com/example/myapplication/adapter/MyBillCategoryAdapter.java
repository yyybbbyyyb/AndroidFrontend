package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MyBillCategoryData;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.List;

public class MyBillCategoryAdapter extends RecyclerView.Adapter<MyBillCategoryAdapter.CateViewHolder> {

    private List<MyBillCategoryData> dataList;
    private OnItemClickListener onItemClickListener;

    public MyBillCategoryAdapter(List<MyBillCategoryData> dataList) {
        this.dataList = dataList;
    }

    public interface OnItemClickListener {
        void onItemClick(MyBillCategoryData billCategoryData);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    public MyBillCategoryData getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public CateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_category, parent, false);
        return new CateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CateViewHolder holder, int position) {
        MyBillCategoryData billCategoryData = dataList.get(position);

        holder.tvCategory.setText(billCategoryData.getCategory().getType());

        // 动态获取图片资源ID并绑定到ImageView
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                billCategoryData.getCategory().getImage(), "drawable", holder.itemView.getContext().getPackageName());

        holder.ivCategory.setImageResource(resourceId);

        if (billCategoryData.getCategory().getInOutType() == 1) {
            holder.tvTotalMoney.setText("+" + billCategoryData.getTotalMoney());
            holder.tvTotalMoney.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.money_green));
        } else {
            holder.tvTotalMoney.setText("-" + billCategoryData.getTotalMoney());
            holder.tvTotalMoney.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.money_red));
        }

        holder.tvPercent.setText(String.valueOf(billCategoryData.getPercent()) + "%");

        holder.progressIndicator.setProgress((int) billCategoryData.getPercent());
        holder.progressIndicator.setIndicatorColor(billCategoryData.getCategory().getColor());

        if (position == dataList.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(billCategoryData);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class CateViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCategory;
        private TextView tvCategory;
        private TextView tvTotalMoney;
        private TextView tvPercent;
        private LinearProgressIndicator progressIndicator;
        private View divider;

        public CateViewHolder(View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.img_logo);
            tvCategory = itemView.findViewById(R.id.tv_type);
            tvTotalMoney = itemView.findViewById(R.id.tv_amount);
            tvPercent = itemView.findViewById(R.id.tv_rate);
            progressIndicator = itemView.findViewById(R.id.progress);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}
