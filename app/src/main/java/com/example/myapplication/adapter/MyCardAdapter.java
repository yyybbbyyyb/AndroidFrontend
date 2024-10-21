package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MyCardData;

import java.util.List;

public class MyCardAdapter extends RecyclerView.Adapter<MyCardAdapter.OuterViewHolder> {

    private List<MyCardData> outerDataList; // 外层数据，包含日期和账单列表

    public MyCardAdapter(List<MyCardData> outerDataList) {
        this.outerDataList = outerDataList;
    }

    @Override
    public OuterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new OuterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OuterViewHolder holder, int position) {
        MyCardData cardData = outerDataList.get(position);

        // 绑定外层数据
        holder.tvDate.setText(cardData.getDate());
        holder.tvIncome.setText(String.valueOf(cardData.getIncome()));
        holder.tvExpense.setText(String.valueOf(cardData.getExpense()));

        // 内层RecyclerView的适配器
        MyBillAdapter innerAdapter = new MyBillAdapter(cardData.getBillList());
        holder.innerRecyclerView.setAdapter(innerAdapter);
        holder.innerRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return outerDataList.size();
    }

    public static class OuterViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvIncome, tvExpense;
        RecyclerView innerRecyclerView;

        public OuterViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvIncome = itemView.findViewById(R.id.tv_income_amount);
            tvExpense = itemView.findViewById(R.id.tv_expense_amount);
            innerRecyclerView = itemView.findViewById(R.id.inner_recycler_view);
        }
    }



}
