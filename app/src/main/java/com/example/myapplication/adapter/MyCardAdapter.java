package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.activity.EditBillActivity;
import com.example.myapplication.adapter.MyBillAdapter;
import com.example.myapplication.fragment.DetailFragment;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.model.MyCardData;
import com.example.myapplication.utils.DialogUtils;

import java.util.List;

public class MyCardAdapter extends RecyclerView.Adapter<MyCardAdapter.OuterViewHolder> {

    private List<MyCardData> outerDataList; // 外层数据，包含日期和账单列表
    private DetailFragment fragment;

    public MyCardAdapter(List<MyCardData> outerDataList, DetailFragment fragment) {
        this.outerDataList = outerDataList;
        this.fragment = fragment;  // 通过构造函数传入 DetailFragment 实例
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
        MyBillAdapter innerAdapter = new MyBillAdapter(cardData.getBillList(), false);
        holder.innerRecyclerView.setAdapter(innerAdapter);
        holder.innerRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        // 设置内层RecyclerView的点击事件
        innerAdapter.setOnItemClickListener(new MyBillAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MyBillData billData) {
                // 跳转到修改账单界面
                Intent intent = new Intent(holder.itemView.getContext(), EditBillActivity.class);
                intent.putExtra("billId", billData.getId());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        // 左滑删除功能
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                MyBillData billData = innerAdapter.getItem(viewHolder.getAdapterPosition());  // 获取对应的账单数据
                int billId = billData.getId();  // 获取账单的id

                // 显示删除确认对话框，并传递id
                DialogUtils.showDeleteConfirmDialog((Activity) holder.itemView.getContext(), billId, () -> {
                    // 删除成功后刷新UI
                    innerAdapter.removeItem(viewHolder.getAdapterPosition());
                    fragment.refresh();  // 调用 DetailFragment 的 refresh 方法
                }, () -> {
                    // 用户取消删除时，恢复被滑动的条目
                    innerAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                });
            }
        };

        // 将 ItemTouchHelper 附加到内层 RecyclerView
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(holder.innerRecyclerView);
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
