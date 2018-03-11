package com.example.deepanshutyagi.smartsms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.deepanshutyagi.smartsms.adapters.ChatAddressAdapter;
import com.example.deepanshutyagi.smartsms.adapters.ReportAdapter;

import com.example.deepanshutyagi.smartsms.models.SmsModel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatActivity extends AppCompatActivity {
    private String Address;
    private ArrayList<SmsModel> chatModelList;
    private RecyclerView recyclerView;
    private ChatAddressAdapter addressAdapter;
    private ReportAdapter reportAdapter;

    private TextView income_data_tv, expense_data_tv, bank_name_tv;

    private RelativeLayout chat_rlayout1;

    private float total_exp = 0.00f, total_income = 0.00f;
    private String bank_name="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle bundle = this.getIntent().getExtras();
        Address = bundle.getString("address");
        chatModelList = new ArrayList<>();

        chat_rlayout1 = (RelativeLayout) findViewById(R.id.chat_rlayout1);
        chat_rlayout1.setVisibility(View.GONE);

        income_data_tv = (TextView) findViewById(R.id.income_data_tv);
        expense_data_tv = (TextView) findViewById(R.id.expense_data_tv);
        bank_name_tv = (TextView) findViewById(R.id.bank_name_tv);


        recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(MainActivity.selectedTab == 0){
            //all tab selected
            chat_rlayout1.setVisibility(View.GONE);
            refreshSmsChat();
            addressAdapter = new ChatAddressAdapter(chatModelList, this);
            recyclerView.setAdapter(addressAdapter);
        }
        else if(MainActivity.selectedTab == 1){
            //transactional tab selected
            chat_rlayout1.setVisibility(View.VISIBLE);
            refreshSmsChat();
            bank_name = fetchBankName(Address);
            if(bank_name.equals("")){
                bank_name_tv.setText("NA");
                income_data_tv.setText("Income\n"+"₹"+total_income);
                expense_data_tv.setText("Expense\n"+"₹"+total_exp);
            }
            else {
                bank_name_tv.setText(bank_name);
                income_data_tv.setText("Income\n"+"₹"+total_income);
                expense_data_tv.setText("Expense\n"+"₹"+total_exp);
            }
            reportAdapter = new ReportAdapter(chatModelList, this);
            recyclerView.setAdapter(reportAdapter);
        }
        //addressAdapter = new ChatAddressAdapter(chatModelList, this);
        //recyclerView.setAdapter(addressAdapter);
    }

    public void refreshSmsChat(){
        ContentResolver contentResolver = this.getContentResolver();
        String whereClause = "address = ?";
        String[] whereArgs = new String[]{
          Address
        };
        this.chatModelList.clear();
        String orderBy = "date";
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, whereClause, whereArgs, orderBy);
        if (!smsInboxCursor.moveToFirst()) return;
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

                Float mamt = processAmnt(amnt);
                if(mamt>0){
                    total_exp = total_exp + mamt;
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
                Float mamt = processAmnt(amnt);
                if(mamt>0){
                    total_income = total_income + mamt;
                }
                chatModelList.add(smsModel);
            }
            else{
                // normal sms
                if(MainActivity.selectedTab == 0){
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
            }

        } while (smsInboxCursor.moveToNext());
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

    private float processAmnt(String amnt_str){
        float final_amount = -1;

        String string = amnt_str;
        Pattern pattern = Pattern.compile("([.])");
        Matcher matcher = pattern.matcher(string);
        int count = 0;
        while (matcher.find()) count++;

        if(count == 1){
            //remove only Rs
            if((string.indexOf(".") - string.indexOf("Rs")) <3){
                string = string.replace(".","");
            }
            string = string.replace("Rs","");
            string = string.replace(" ","");
            string = string.replace("INR","");
            string = string.replace("inr","");
            string = string.replace(",","");
            string = string.replace("₹","");
            final_amount = Float.parseFloat(string);
        }
        else if(count == 2){
            //remove Rs.
            string = string.replace("Rs","");
            string = string.replace(" ","");
            string = string.replaceFirst(".","");
            string = string.replace("INR","");
            string = string.replace("inr","");
            string = string.replace(",","");
            string = string.replace("₹","");
            final_amount = Float.parseFloat(string);
        }
        else{
            //strange case
        }

        return final_amount;
    }


    private String fetchBankName(String in){
        //String out="";
        String out = in;

        /*
        if(in.contains("SMS") || in.contains("sms"))
        {
            out = in.replace("SMS","");
            out = out.replace("sms","");
        }
        if(out.contains("-")){
            out = out.substring(out.indexOf("-")+1);
        }
        */
        /*
        if(in.contains("PNB") || in.contains("pnb")){
            out = "PNB";
        }
        else if(in.contains("KOTAK") || in.contains("kotak")){
            out = "KOTAK";
        }
        else if(in.contains("YES") || in.contains("yes")){
            out = "YES BANK";
        }
        else if(in.contains("HDFC") || in.contains("hdfc")){
            out = "HDFC";
        }
        else if(in.contains("SBI") || in.contains("sbi")){
            out = "SBI";
        }
        else if(in.contains("BOI") || in.contains("boi")){
            out = "BOI";
        }
        else if(in.contains("UBI") || in.contains("ubi")){
            out = "UBI";
        }
        else if(in.contains("AXIS") || in.contains("axis")){
            out = "AXIS";
        }
        else if(in.contains("CBI") || in.contains("cbi")){
            out = "CBI";
        }
        */
        return out;
    }

}
