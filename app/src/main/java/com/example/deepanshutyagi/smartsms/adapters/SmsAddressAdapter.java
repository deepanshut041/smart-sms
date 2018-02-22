package com.example.deepanshutyagi.smartsms.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deepanshutyagi.smartsms.R;
import com.example.deepanshutyagi.smartsms.models.SmsModel;

import java.util.ArrayList;

/**
 * Created by Deepanshu Tyagi on 2/22/2018.
 */

public class SmsAddressAdapter extends RecyclerView.Adapter<SmsAddressAdapter.smsListViewHolder> {
    public ArrayList<SmsModel> smsListArrayList;
    public Context context;

    public static class smsListViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView address;
        TextView msg;
        public smsListViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.personal_recyclerView_item_avatar);
            address = (TextView) itemView.findViewById(R.id.personal_recyclerView_item_address);
            msg = (TextView) itemView.findViewById(R.id.personal_recyclerView_item_msg);
        }
    }

    public SmsAddressAdapter(ArrayList<SmsModel> arrayList, Context context) {
        this.smsListArrayList = arrayList;
        this.context = context;
    }

    @Override
    public smsListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_recyclerview_item, parent, false);
        return new smsListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final smsListViewHolder holder, final int position) {
        final SmsModel smsModel = smsListArrayList.get(position);
        holder.address.setText(smsModel.getAddress());
        holder.msg.setText((smsModel.getBody()).substring(0, 20) + "...");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type2;
                
            }
        });
    }

    @Override
    public int getItemCount() {
        return smsListArrayList.size();
    }
}
