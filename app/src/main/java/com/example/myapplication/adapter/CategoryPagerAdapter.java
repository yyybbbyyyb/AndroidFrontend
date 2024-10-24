package com.example.myapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.fragment.ExpenseCategoryFragment;
import com.example.myapplication.fragment.IncomeCategoryFragment;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    public CategoryPagerAdapter(FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ExpenseCategoryFragment(); // 支出
        } else {
            return new IncomeCategoryFragment();  // 收入
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 支出和收入两个页面
    }
}
