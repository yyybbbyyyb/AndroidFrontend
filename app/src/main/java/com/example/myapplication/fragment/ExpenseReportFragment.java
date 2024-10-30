package com.example.myapplication.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ExpenseReportAdapter;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.MyBillData;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseReportFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<MyBillData> bills = new ArrayList<>();
    private ExpenseReportAdapter adapter;

    private OnDataLoadedListener onDataLoadedListener;

    public interface OnDataLoadedListener {
        void onDataLoaded();
    }

    public void setOnDataLoadedListener(OnDataLoadedListener listener) {
        this.onDataLoadedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_expense_report, container, false);
        recyclerView = view.findViewById(R.id.inner_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 加载数据
        loadExpenseData();

        // 在界面展示2秒后自动返回
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }, 3000); // 2000 毫秒 = 2 秒

        return view;
    }

    private void loadExpenseData() {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        Map<String, String> map = new HashMap<>();
        map.put("ordering", "-date");
        map.put("inOutType", "2");
        Call<ApiModels.ApiResponse<List<MyBillData>>> call = apiService.getBills(map);
        call.enqueue(new Callback<ApiModels.ApiResponse<List<MyBillData>>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Response<ApiModels.ApiResponse<List<MyBillData>>> response) {
                if (response.isSuccessful()) {
                    bills = response.body().getData();

                    if (bills == null || bills.size() == 0) {
                        Toast.makeText(getActivity(), "没有数据可以导出", Toast.LENGTH_LONG).show();
                        return;
                    }

                    // 按年月分类
                    HashMap<String, List<MyBillData>> dateMap = new HashMap<>();
                    for (MyBillData bill : bills) {
                        String date = bill.getDate().substring(0, 7);
                        String formatData = date.substring(0, 4) + "年" + date.substring(5, 7) + "月";
                        if (dateMap.containsKey(formatData)) {
                            dateMap.get(formatData).add(bill);
                        } else {
                            List<MyBillData> list = new ArrayList<>();
                            list.add(bill);
                            dateMap.put(formatData, list);
                        }
                    }

                    adapter = new ExpenseReportAdapter(dateMap);
                    recyclerView.setAdapter(adapter);

                    // 如果有监听器，数据加载完成后通知
                    if (onDataLoadedListener != null) {
                        onDataLoadedListener.onDataLoaded();
                    }
                } else {
                    Toast.makeText(getActivity(), "请求失败: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<List<MyBillData>>> call, Throwable t) {
                Toast.makeText(getActivity(), "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void exportViewToPDF() {
        recyclerView.post(() -> {
            // 计算RecyclerView的总高度
            int totalHeight = 0;
            for (int i = 0; i < adapter.getItemCount(); i++) {
                // 创建ViewHolder并绑定视图
                ExpenseReportAdapter.ViewHolder holder = (ExpenseReportAdapter.ViewHolder) adapter.onCreateViewHolder(recyclerView, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                View itemView = holder.itemView;

                // 测量itemView的高度
                itemView.measure(View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalHeight += itemView.getMeasuredHeight();
            }

            totalHeight += adapter.getItemCount() * 30; // 加上每个item之间的间距

            totalHeight += 300; // 加上标题栏和底部栏的高度

            // 创建Bitmap来保存整个RecyclerView的内容
            Bitmap bigBitmap = Bitmap.createBitmap(recyclerView.getWidth(), totalHeight, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            recyclerView.draw(bigCanvas);

            // 创建PDF文档
            PdfDocument pdfDocument = new PdfDocument();
            int pageHeight = recyclerView.getHeight(); // 页面高度
            int totalPages = (int) Math.ceil((double) totalHeight / pageHeight); // 总页数

            for (int i = 0; i < totalPages; i++) {
                int startY = i * pageHeight;
                int endY = Math.min(startY + pageHeight, totalHeight);
                Bitmap pageBitmap = Bitmap.createBitmap(bigBitmap, 0, startY, recyclerView.getWidth(), endY - startY);

                // 创建PDF页面
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageBitmap.getWidth(), pageBitmap.getHeight(), i + 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                Canvas pageCanvas = page.getCanvas();
                pageCanvas.drawBitmap(pageBitmap, 0, 0, null);
                pdfDocument.finishPage(page);

                // 释放Bitmap内存
                pageBitmap.recycle();
            }

            // 保存PDF到文件
            File exportDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Export");
            if (!exportDir.exists()) {
                exportDir.mkdirs(); // 如果文件夹不存在，则创建
            }

            File pdfFile = new File(exportDir, "xiaoYaoExpenseReport.pdf");

            try {
                pdfFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(pdfFile);
                pdfDocument.writeTo(fos);
                fos.close();
                Toast.makeText(getContext(), "导出成功，路径为：Download/xiaoYao/\n三秒后返回", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "PDF 导出失败", Toast.LENGTH_SHORT).show();
            }

            // 关闭PDF文档
            pdfDocument.close();
            bigBitmap.recycle(); // 释放内存
        });
    }




}
