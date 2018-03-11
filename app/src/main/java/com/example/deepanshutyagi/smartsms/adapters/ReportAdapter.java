package com.example.deepanshutyagi.smartsms.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.deepanshutyagi.smartsms.R;
import com.example.deepanshutyagi.smartsms.models.SmsModel;

import java.util.ArrayList;

/**
 * Created by Deepanshu Tyagi on 2/22/2018.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.chatListViewHolder> {
    public ArrayList<SmsModel> chatListArrayList2;
    public Context context;

    public static class chatListViewHolder extends RecyclerView.ViewHolder {
        TextView income_expense_data, purpose;
        public chatListViewHolder(View itemView) {
            super(itemView);
            income_expense_data = (TextView) itemView.findViewById(R.id.income_expense_data);
            purpose = (TextView) itemView.findViewById(R.id.purpose);
        }
    }

    public ReportAdapter(ArrayList<SmsModel> arrayList, Context context) {
        this.chatListArrayList2 = arrayList;
        this.context = context;
    }

    @Override
    public chatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_rv_item, parent, false);
        return new chatListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final chatListViewHolder holder, final int position) {
        final SmsModel chatModel = chatListArrayList2.get(position);

        holder.income_expense_data.setText(chatModel.getIncomeExpenseData());
        holder.purpose.setText(chatModel.getPurpose());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatListArrayList2.size();
    }
}
