package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MyBillData;

import java.util.List;

public class MyBillAdapter extends RecyclerView.Adapter<MyBillAdapter.InnerViewHolder> {

    private List<MyBillData> innerDataList;

    public MyBillAdapter(List<MyBillData> innerDataList) {
        this.innerDataList = innerDataList;
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
        holder.tvNote.setText(billData.getNote());
        holder.tvAmount.setText(String.valueOf(billData.getAmount()));

        String logo_name = "ledger_" + billData.getLogo();

        // 动态获取图片资源ID并绑定到ImageView
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                logo_name, "drawable", holder.itemView.getContext().getPackageName());

        // 设置图片
        holder.imgLogo.setImageResource(resourceId);
    }

    @Override
    public int getItemCount() {
        return innerDataList.size();
    }

    public static class InnerViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvNote, tvAmount;
        ImageView imgLogo;

        public InnerViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tv_type);
            tvNote = itemView.findViewById(R.id.tv_note);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            imgLogo = itemView.findViewById(R.id.img_logo);
        }
    }


}
