package com.example.deepanshutyagi.smartsms.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.deepanshutyagi.smartsms.R;
import com.example.deepanshutyagi.smartsms.models.SmsModel;

import java.util.ArrayList;

/**
 * Created by deepanshu on 12/3/18.
 */

public class TransactionalItemAdapter extends RecyclerView.Adapter<TransactionalItemAdapter.transactionalItemViewHolder> {
    public ArrayList<SmsModel> chatListArrayList2;
    public Context context;

    public static class transactionalItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, income, expense;
        public transactionalItemViewHolder(View itemView) {
            super(itemView);
            income = (TextView) itemView.findViewById(R.id.transactional_header_item_income);
            expense = (TextView) itemView.findViewById(R.id.transactional_header_item_expense);
            name = (TextView) itemView.findViewById(R.id.transactional_header_item_name);
        }
    }

    public TransactionalItemAdapter(ArrayList<SmsModel> arrayList, Context context) {
        this.chatListArrayList2 = arrayList;
        this.context = context;
        Log.i("list---------", arrayList.size()+"");
    }

    @Override
    public transactionalItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactional_header_item, parent, false);
        return new transactionalItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final transactionalItemViewHolder holder, final int position) {
        SmsModel chatModel = chatListArrayList2.get(position);

        holder.income.setText(chatModel.getIncomeExpenseData());
        holder.expense.setText(chatModel.getIncomeExpenseData());
        holder.name.setText(chatModel.getAddress());


    }

    @Override
    public int getItemCount() {
        return chatListArrayList2.size();
    }


}
