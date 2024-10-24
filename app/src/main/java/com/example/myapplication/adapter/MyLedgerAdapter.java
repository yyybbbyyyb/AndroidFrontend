package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MyCardData;
import com.example.myapplication.model.MyLedgerData;

import java.util.List;

public class MyLedgerAdapter extends RecyclerView.Adapter<MyLedgerAdapter.LedgerViewHolder> {

    private List<MyLedgerData> outerDataList;
    private OnEditClickListener onEditClickListener;
    private OnChoiceClickListener onChoiceClickListener;
    private boolean editVisible = true;

    public MyLedgerAdapter(List<MyLedgerData> outerDataList) {
        this.outerDataList = outerDataList;
    }

    public void setOnEditClickListener(OnEditClickListener listener, boolean isVisible) {
        this.onEditClickListener = listener;
        this.editVisible = isVisible;
    }

    public void setOnChoiceClickListener(OnChoiceClickListener listener) {
        this.onChoiceClickListener = listener;
    }

    @Override
    public MyLedgerAdapter.LedgerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ledger, parent, false);
        return new LedgerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyLedgerAdapter.LedgerViewHolder holder, int position) {
        MyLedgerData ledgerData = outerDataList.get(position);

        holder.tvName.setText(ledgerData.getName());

        // 动态获取图片资源ID并绑定到ImageView
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                ledgerData.getImage(), "drawable", holder.itemView.getContext().getPackageName());

        // 设置图片
        holder.logo.setImageResource(resourceId);

        if (editVisible) {
            holder.editButton.setVisibility(View.VISIBLE);

            // 设置编辑按钮点击事件
            holder.editButton.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.onEditClick(position);
                }
            });
        } else {
            holder.editButton.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onChoiceClickListener != null) {
                onChoiceClickListener.onChoiceClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return outerDataList.size();
    }

    public static class LedgerViewHolder extends RecyclerView.ViewHolder {
        ImageView logo, editButton;
        TextView tvName;


        public LedgerViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.ledger_logo);
            tvName = itemView.findViewById(R.id.tv_name);
            editButton = itemView.findViewById(R.id.btn_edit);
        }
    }

    // 定义接口
    public interface OnEditClickListener {
        void onEditClick(int position);
    }

    public interface OnChoiceClickListener {
        void onChoiceClick(int position);
    }
}
