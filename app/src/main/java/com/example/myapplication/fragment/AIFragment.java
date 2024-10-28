package com.example.myapplication.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ChatAdapter;
import com.example.myapplication.model.ApiModels;
import com.example.myapplication.model.ChatMessage;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.utils.Utils;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AIFragment extends Fragment {

    private RecyclerView recyclerViewChat;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages = new ArrayList<>();
    private PowerMenu powerMenu;
    private String userAvatar;
    private int ledger_id;
    private int this_type;


    public AIFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai, container, false);

        recyclerViewChat = view.findViewById(R.id.recyclerViewChat);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        getUserInfo();
        setLedger();

        // 设置 RecyclerView
        chatAdapter = new ChatAdapter(chatMessages);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChat.setAdapter(chatAdapter);

        analyzeLedger(() -> {
            initBtn();
        });
        
        return view;
    }

    @FunctionalInterface
    interface AnalyzeLedgerCallback {
        void onAnalyzeLedgerSet();
    }

    private void initBtn() {
        powerMenu = new PowerMenu.Builder(getContext())
                .addItem(new PowerMenuItem("普通模式", false))
                .addItem(new PowerMenuItem("记账模式", false))
                .setAnimation(MenuAnimation.SHOW_UP_CENTER) // 设置动画
                .setMenuRadius(10f) // 设置菜单圆角
                .setMenuShadow(10f) // 设置菜单阴影
                .setTextSize(18) // 设置全局文字大小
                .setTextColor(Color.BLACK) // 设置全局文字颜色
                .setTextGravity(Gravity.CENTER) // 设置文字居中
                .setTextTypeface(Typeface.DEFAULT_BOLD) // 设置文字字体
                .setSelectedTextColor(Color.RED) // 设置选中文字颜色
                .setOnMenuItemClickListener((position, item) -> {
                    if (position == 0) {
                        sendMessage(0); // 普通模式
                    } else if (position == 1) {
                        sendMessage(1); // 记账模式
                    }
                    powerMenu.dismiss();
                })
                .build();

        buttonSend.setOnClickListener(v -> {
            Utils.hideKeyboard(getView(), getActivity());
            powerMenu.showAsAnchorCenter(v);
        });
    }

    private void analyzeLedger(AnalyzeLedgerCallback callback) {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        Map map = new HashMap();
        map.put("ledger_id", ledger_id);
        Call<ApiModels.ApiResponse<ApiModels.WarningResponse>> call = apiService.analyzeLedger(map);
        call.enqueue(new Callback<ApiModels.ApiResponse<ApiModels.WarningResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.WarningResponse>> call, Response<ApiModels.ApiResponse<ApiModels.WarningResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.WarningResponse warningResponse = response.body().getData();
                    if (warningResponse.isWarning()) {
                        chatMessages.add(new ChatMessage(warningResponse.getMessage(), false, "https://example.com/ai_avatar.png", 3, 1));
                    } else {
                        chatMessages.add(new ChatMessage(warningResponse.getMessage(), false, "https://example.com/ai_avatar.png", 8, 2));
                    }
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
                    callback.onAnalyzeLedgerSet();
                } else {
                    Toast.makeText(getActivity(), "请求失败: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.WarningResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getUserInfo() {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);

        Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call = apiService.getUserInfo();
        call.enqueue(new Callback<ApiModels.ApiResponse<ApiModels.UserInfoResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call, Response<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.UserInfoResponse userInfoResponse = response.body().getData();
                    userAvatar = userInfoResponse.getAvatar();
                } else {
                    Toast.makeText(getActivity(), "请求失败: " + response.message(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.UserInfoResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLedger() {
        // 从 SharedPreferences 获取上次选择的账本 ID
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ledger_prefs", Context.MODE_PRIVATE);
        String lastSelectedLedgerId = sharedPreferences.getString("last_selected_ledger_id", null);

        if (lastSelectedLedgerId == null) {
            Toast.makeText(getContext(), "请先选择账本", Toast.LENGTH_SHORT).show();
            return;
        }

        ledger_id = Integer.parseInt(lastSelectedLedgerId);
    }

    private void sendMessage(int type) {
        String message = editTextMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), "请输入消息", Toast.LENGTH_SHORT).show();
            return;
        }


        chatMessages.add(new ChatMessage(message, true, userAvatar, 0, 0));
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
        editTextMessage.setText("");

        // 模拟AI回复
        simulateAIResponse(message, type);
    }

    private void simulateAIResponse(String userMessage, int type) {
        ApiService apiService = ApiClient.getClient(getActivity().getApplicationContext()).create(ApiService.class);
        Map map = new HashMap();
        Call<ApiModels.ApiResponse<ApiModels.ChatResponse>> call;
        if (type == 0) {
            map.put("message", userMessage);
            call = apiService.normalChat(map);
        } else {
            map.put("message", userMessage);
            map.put("ledger_id", ledger_id);
            call = apiService.billChat(map);
        }
        this_type = type;

        call.enqueue(new Callback<ApiModels.ApiResponse<ApiModels.ChatResponse>>() {
            @Override
            public void onResponse(Call<ApiModels.ApiResponse<ApiModels.ChatResponse>> call, Response<ApiModels.ApiResponse<ApiModels.ChatResponse>> response) {
                if (response.isSuccessful()) {
                    ApiModels.ChatResponse chatResponse = response.body().getData();
                    String aiReply = chatResponse.getMessage();
                    Log.d("AI", aiReply);
                    Log.d("AI", String.valueOf(chatResponse.getAiAvatar()));
                    if (this_type == 1) {
                        chatMessages.add(new ChatMessage("账单自动生成成功", false, "https://example.com/ai_avatar.png", 0, 0));
                    }
                    chatMessages.add(new ChatMessage(aiReply, false, "https://example.com/ai_avatar.png", chatResponse.getAiAvatar(), 0));

                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    recyclerViewChat.scrollToPosition(chatMessages.size() - 1);
                } else {
                    Toast.makeText(getActivity(), "请求失败: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiModels.ApiResponse<ApiModels.ChatResponse>> call, Throwable t) {
                Toast.makeText(getActivity(), "请求失败: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
