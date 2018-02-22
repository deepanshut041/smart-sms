package com.example.deepanshutyagi.smartsms;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.deepanshutyagi.smartsms.adapters.ChatAddressAdapter;
import com.example.deepanshutyagi.smartsms.models.SmsModel;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    private String Address;
    private ArrayList<SmsModel> chatModelList;
    private RecyclerView recyclerView;
    private ChatAddressAdapter addressAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle bundle = this.getIntent().getExtras();
        Address = bundle.getString("address");
        chatModelList = new ArrayList<>();
        refreshSmsChat();

        recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        addressAdapter = new ChatAddressAdapter(chatModelList, this);
        recyclerView.setAdapter(addressAdapter);
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
            smsModel.setAddress(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("address")));
            smsModel.setBody(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("body")));
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
        } while (smsInboxCursor.moveToNext());
    }
}
