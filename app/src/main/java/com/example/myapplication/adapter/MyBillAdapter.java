package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MyBillData;

import java.util.List;

public class MyBillAdapter extends RecyclerView.Adapter<MyBillAdapter.InnerViewHolder> {

    private List<MyBillData> innerDataList;
    private OnItemClickListener onItemClickListener;
    private boolean needDate = false;

    public MyBillAdapter(List<MyBillData> innerDataList, boolean needDate) {
        this.innerDataList = innerDataList;
        this.needDate = needDate;
    }

    public void updateData(List<MyBillData> newData) {
        this.innerDataList = newData;
    }


    // 用于设置点击事件的接口
    public interface OnItemClickListener {
        void onItemClick(MyBillData billData);  // 点击事件
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    // 获取某个位置的数据项
    public MyBillData getItem(int position) {
        return innerDataList.get(position);
    }

    // 删除指定位置的项目
    public void removeItem(int position) {
        innerDataList.remove(position);
        notifyItemRemoved(position);  // 通知 RecyclerView 数据已移除
    }

    @Override
    public InnerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill, parent, false);
        return new InnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InnerViewHolder holder, int position) {
        MyBillData billData = innerDataList.get(position);

        // 绑定内层数据
        holder.tvType.setText(billData.getType());
        holder.tvNote.setText(billData.getRemark());

        if (billData.getCategory().getInOutType() == 1) {
            holder.tvAmount.setText("+" + String.valueOf(billData.getAmountShort()));
        } else {
            holder.tvAmount.setText("-" + String.valueOf(billData.getAmountShort()));
        }

        if (needDate) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.tvDate.setText(billData.getDate());
        }

        // 动态获取图片资源ID并绑定到ImageView
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                billData.getImage(), "drawable", holder.itemView.getContext().getPackageName());

        // 设置图片
        holder.imgLogo.setImageResource(resourceId);

        if (billData.getCategory().getInOutType() == 1) {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.money_green));
        } else {
            holder.tvAmount.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.money_red));
        }

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(billData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return innerDataList.size();
    }

    public static class InnerViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvNote, tvAmount, tvDate;
        ImageView imgLogo;

        public InnerViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_type);
            tvNote = itemView.findViewById(R.id.tv_note);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            imgLogo = itemView.findViewById(R.id.img_logo);
            tvDate = itemView.findViewById(R.id.date);
        }
    }


}
