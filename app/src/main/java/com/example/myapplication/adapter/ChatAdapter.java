package com.example.myapplication.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatMessage;
import com.example.myapplication.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;

    public ChatAdapter(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) { // 用户消息
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
        } else { // AI消息
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_message, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        holder.textViewMessage.setText(chatMessage.getMessage());

        if (chatMessage.getColor() == 1) {
            holder.textViewMessage.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.money_red));
        } else if (chatMessage.getColor() == 2) {
            holder.textViewMessage.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.money_green));
        }

        if (chatMessage.isUser()) {
            // 用户消息，使用 Picasso 从 URL 加载头像
            Picasso.get()
                    .load(Utils.processAvatarUrl(chatMessage.getAvatarUrl()))
                    .into(holder.imageViewAvatar, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d("Picasso", "Image loaded successfully.");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("Picasso", "Error loading image", e);
                        }
                    });
        } else {
            // 动态获取图片资源ID并绑定到ImageView
            int resourceId = holder.itemView.getContext().getResources().getIdentifier(
                    chatMessage.getAiImage(), "drawable", holder.itemView.getContext().getPackageName());

            // 设置图片
            holder.imageViewAvatar.setImageResource(resourceId);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return chatMessages.get(position).isUser() ? 1 : 0;
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage;
        ImageView imageViewAvatar;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
        }
    }
}
