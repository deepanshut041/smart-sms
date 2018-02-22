package com.example.deepanshutyagi.smartsms.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deepanshutyagi.smartsms.ChatActivity;
import com.example.deepanshutyagi.smartsms.R;
import com.example.deepanshutyagi.smartsms.models.SmsModel;

import java.util.ArrayList;

/**
 * Created by Deepanshu Tyagi on 2/22/2018.
 */

public class ChatAddressAdapter extends RecyclerView.Adapter<ChatAddressAdapter.chatListViewHolder> {
    public ArrayList<SmsModel> chatListArrayList;
    public Context context;

    public static class chatListViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView body_send, body_receive;
        public chatListViewHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.chat_recyclerView_time);
            body_send = (TextView) itemView.findViewById(R.id.chat_recyclerView_body_send);
            body_receive = (TextView) itemView.findViewById(R.id.chat_recyclerView_body_receive);
        }
    }

    public ChatAddressAdapter(ArrayList<SmsModel> arrayList, Context context) {
        this.chatListArrayList = arrayList;
        this.context = context;
    }

    @Override
    public chatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_recyclerview_item, parent, false);
        return new chatListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final chatListViewHolder holder, final int position) {
        final SmsModel chatModel = chatListArrayList.get(position);
        String value = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").
                format(new java.util.Date(chatModel.getDate()));
        holder.date.setText(value);
        holder.body_send.setText(chatModel.getBody());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatListArrayList.size();
    }
}
