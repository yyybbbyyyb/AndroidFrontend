package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.MyBillData;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<MyBillData.Category> categoryList;

    public CategoryAdapter(List<MyBillData.Category> categoryList) {
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        MyBillData.Category category = categoryList.get(position);
        int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                category.getImage(), "drawable", holder.itemView.getContext().getPackageName());

        holder.icon.setImageResource(resourceId);

        holder.name.setText(category.getType());

        holder.icon.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("detail_type", category.getDetail_type());
            intent.putExtra("inOutType", category.getInOutType());
            ((Activity) v.getContext()).setResult(Activity.RESULT_OK, intent);
            ((Activity) v.getContext()).finish();
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.icon_label);
        }
    }
}
