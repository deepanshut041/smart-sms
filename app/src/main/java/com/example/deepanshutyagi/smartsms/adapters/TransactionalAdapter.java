package com.example.deepanshutyagi.smartsms.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.deepanshutyagi.smartsms.R;
import com.example.deepanshutyagi.smartsms.models.SmsModel;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by deepanshu on 12/3/18.
 */

public class TransactionalAdapter extends RecyclerView.Adapter<TransactionalAdapter.transactionalListViewHolder> implements View.OnCreateContextMenuListener {

    public ArrayList<SmsModel> transactionalArrayList;
    public Context context;


    //private int mPosition;

    public static class transactionalListViewHolder extends RecyclerView.ViewHolder {
        TextView address, data, purpose;
        DiscreteScrollView discreteScrollView;
        public transactionalListViewHolder(View itemView) {
            super(itemView);
            address = (TextView) itemView.findViewById(R.id.transactional_header_header);
            discreteScrollView = (DiscreteScrollView ) itemView.findViewById(R.id.transactional_header_discretescrollview);
            purpose = (TextView) itemView.findViewById(R.id.transactional_header_purpose);
            data = (TextView) itemView.findViewById(R.id.transactional_header_expense_data);

        }

    }

    public TransactionalAdapter(ArrayList<SmsModel> arrayList, Context context) {
        this.transactionalArrayList = arrayList;
        this.context = context;
    }

    @Override
    public transactionalListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactional_header, parent, false);
        itemView.setOnCreateContextMenuListener(this);
        return new transactionalListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final transactionalListViewHolder holder, final int position) {
        final SmsModel smsModel = transactionalArrayList.get(position);
        ArrayList <SmsModel> chatList = getAllSms(smsModel.getAddress());
        TransactionalItemAdapter transactionalItemAdapter = new TransactionalItemAdapter(chatList, holder.itemView.getContext());
        holder.discreteScrollView.setAdapter(transactionalItemAdapter);
        int current_pos = holder.discreteScrollView.getCurrentItem();
        if(current_pos >= 0 ) {
            SmsModel currentItem = chatList.get(current_pos);
            holder.data.setText(currentItem.getIncomeExpenseData());
            holder.purpose.setText(currentItem.getBody());
        }
        holder.address.setText(smsModel.getAddress());
    }


    @Override
    public int getItemCount() {
        return transactionalArrayList.size();
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, R.id.ctx_mark, 0, "Mark Transactional");//groupId, itemId, order, title
        menu.add(0, R.id.ctx_unmark, 0, "Unmark Transactional");//groupId, itemId, order, title

    }

    public ArrayList<SmsModel> getAllSms(String address){
        ArrayList<SmsModel> chatModelList = new ArrayList<>();
            ContentResolver contentResolver = this.context.getContentResolver();
            String whereClause = "address = ?";
            String[] whereArgs = new String[]{
                    address
            };
            String orderBy = "date";
            Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, whereClause, whereArgs, orderBy);
            if (!smsInboxCursor.moveToFirst()) return null;
            do {
                SmsModel smsModel = new SmsModel();

                String myData = smsInboxCursor.getString(smsInboxCursor.getColumnIndex("body"));
                String amnt = getAmountFrmSMS(myData);
            /*if(myData.contains("Rs")){
                int firstIndex = myData.indexOf(".",myData.indexOf("Rs"));
                int secondIndex = myData.indexOf(".",firstIndex+1);
                amnt = myData.substring(myData.indexOf("Rs"),secondIndex+3);
            }*/

                if((myData.contains("Rs") || myData.contains("INR")) && ((myData.contains("Debited") || myData.contains("debited"))
                        || myData.contains("has been made") || myData.contains("was spent") ||
                        (myData.contains("paid to") && myData.contains("Rs.")))){
                    if(myData.contains("Debitcard") || myData.contains("Debit card") || myData.contains("DEBIT CARD")
                            || myData.contains("DEBITCARD")){
                        smsModel.setPurpose("Debitcard");
                        smsModel.setIncomeExpenseData("Expense "+amnt);
                    }
                    else if(myData.contains("Creditcard") || myData.contains("credit card") || myData.contains("CREDIT Card")){
                        smsModel.setPurpose("Creditcard");
                        smsModel.setIncomeExpenseData("Expense "+amnt);
                    }
                    else if(myData.contains("NetBanking") || myData.contains("Net Banking")){
                        smsModel.setPurpose("NetBanking");
                        smsModel.setIncomeExpenseData("Expense "+amnt);
                    }
                    else if(myData.contains("UPI") || myData.contains("upi")){
                        smsModel.setPurpose("UPI");
                        smsModel.setIncomeExpenseData("Expense "+amnt);
                    }
                    else{
                        smsModel.setPurpose("Other");
                        smsModel.setIncomeExpenseData("Expense "+amnt);
                    }

                    smsModel.setAddress(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("address")));
                    //smsModel.setBody(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("body")));
                    smsModel.setBody(myData);
                    smsModel.setServiceCenter(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("service_center")));

                    smsModel.setDate(Long.parseLong(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date"))));
                    smsModel.setDateSent(Long.parseLong(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date_sent"))));

                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("status")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("status"))));
                    }
                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("protocol")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("protocol"))));
                    }
                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("read")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("read"))));
                    }
                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("seen")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("seen"))));
                    }
                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id"))));
                    }
                    chatModelList.add(smsModel);
                }
                else if((myData.contains("Rs") || myData.contains("INR")) && (myData.contains("Credited") || myData.contains("credited") ||
                        (myData.contains("has added") && (myData.contains("cashback") || myData.contains("Rs."))))){
                    if(myData.contains("NEFT") || myData.contains("neft")){
                        smsModel.setPurpose("NEFT");
                        smsModel.setIncomeExpenseData("Income "+amnt);
                    }
                    else if(myData.contains("IMPS") || myData.contains("imps")){
                        smsModel.setPurpose("IMPS");
                        smsModel.setIncomeExpenseData("Income "+amnt);
                    }
                    else if(myData.contains("NetBanking") || myData.contains("Net Banking")){
                        smsModel.setPurpose("NetBanking");
                        smsModel.setIncomeExpenseData("Income "+amnt);
                    }
                    else if(myData.contains("UPI") || myData.contains("upi")){
                        smsModel.setPurpose("UPI");
                        smsModel.setIncomeExpenseData("Income "+amnt);
                    }
                    else if(myData.contains("Debitcard") || myData.contains("Debit Card") || myData.contains("Debit card")){
                        smsModel.setPurpose("Debitcard");
                        smsModel.setIncomeExpenseData("Income "+amnt);
                    }
                    else if(myData.contains("Creditcard") || myData.contains("credit card") || myData.contains("CREDIT Card")){
                        smsModel.setPurpose("Creditcard");
                        smsModel.setIncomeExpenseData("Income "+amnt);
                    }
                    else{
                        smsModel.setPurpose("Other");
                        smsModel.setIncomeExpenseData("Income "+amnt);
                    }

                    smsModel.setAddress(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("address")));
                    //smsModel.setBody(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("body")));
                    smsModel.setBody(myData);
                    smsModel.setServiceCenter(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("service_center")));

                    smsModel.setDate(Long.parseLong(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date"))));
                    smsModel.setDateSent(Long.parseLong(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date_sent"))));

                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("status")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("status"))));
                    }
                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("protocol")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("protocol"))));
                    }
                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("read")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("read"))));
                    }
                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("seen")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("seen"))));
                    }
                    if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id")) != null){
                        smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id"))));
                    }
                    chatModelList.add(smsModel);
                }
                else{
                    // normal sms
                        smsModel.setPurpose("NA");
                        smsModel.setIncomeExpenseData("NA");

                        smsModel.setAddress(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("address")));
                        //smsModel.setBody(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("body")));
                        smsModel.setBody(myData);
                        smsModel.setServiceCenter(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("service_center")));

                        smsModel.setDate(Long.parseLong(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date"))));
                        smsModel.setDateSent(Long.parseLong(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date_sent"))));

                        if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("status")) != null){
                            smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("status"))));
                        }
                        if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("protocol")) != null){
                            smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("protocol"))));
                        }
                        if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("read")) != null){
                            smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("read"))));
                        }
                        if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("seen")) != null){
                            smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("seen"))));
                        }
                        if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id")) != null){
                            smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id"))));
                        }

                        chatModelList.add(smsModel);

                }

            } while (smsInboxCursor.moveToNext());

            return chatModelList;
        }

    private String getAmountFrmSMS(String mmsg){
        String amnt = "";
        //Pattern regEx
        //       = Pattern.compile("(?:inr|rs)+[\\s]*[0-9+[\\,]*+[0-9]*]+[\\.]*[0-9]+");
        Pattern regEx = Pattern.compile("(?i)(?:(?:RS|INR|MRP|Rs)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)");
        // Find instance of pattern matches
        Matcher m = regEx.matcher(mmsg);
        if (m.find()) {
            try {
                amnt =  m.group(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //no value found
        }
        return amnt;
    }
    }

