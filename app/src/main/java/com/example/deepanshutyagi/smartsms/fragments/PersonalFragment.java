package com.example.deepanshutyagi.smartsms.fragments;


import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.deepanshutyagi.smartsms.R;
import com.example.deepanshutyagi.smartsms.adapters.SmsAddressAdapter;
import com.example.deepanshutyagi.smartsms.models.SmsModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArrayList<SmsModel> smsModelList;
    private ArrayList<SmsModel> uniqueSmsList;
    private SmsAddressAdapter addressAdapter;
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;

    public PersonalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_personal, container, false);
        Toast.makeText(getContext(), "Personal Fragment", Toast.LENGTH_SHORT).show();
        smsModelList = new ArrayList<>();
        uniqueSmsList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            refreshSmsInbox();
        }

        recyclerView = (RecyclerView) rootView.findViewById(R.id.personal_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        addressAdapter = new SmsAddressAdapter(uniqueSmsList, getContext());
        recyclerView.setAdapter(addressAdapter);
        return rootView;
    }

    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_SMS)) {
                    Toast.makeText(getContext(), "Please allow permission!", Toast.LENGTH_SHORT).show();
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_SMS},
                        READ_SMS_PERMISSIONS_REQUEST);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_SMS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(getContext(), "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);

        uniqueSmsList.clear();
        smsModelList.clear();
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
            if (smsInboxCursor.getString(smsInboxCursor.getColumnIndex("seen")) != null){
                smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id"))));
            }
            smsModelList.add(smsModel);
        } while (smsInboxCursor.moveToNext());
        refreshUniquesSmsInbox();
    }

    public void refreshUniquesSmsInbox(){
        for(SmsModel smsModel:smsModelList){
            int position = 0;
            boolean isPresent = false;
            for(SmsModel smsModel1:uniqueSmsList){
                if((smsModel1.getAddress()).equals(smsModel.getAddress())){
                    if(smsModel1.getId() < smsModel.getId()){
                        uniqueSmsList.add(position, smsModel);
                    }
                    isPresent = true;
                }
                position ++;
            }
            if(isPresent == false){
                uniqueSmsList.add(smsModel);
            }
        }

//        This code is only for debugging purpose
//        int position = 0;
//        for (SmsModel smsModel:uniqueSmsList){
//            Log.i("Unique address", smsModel.getAddress() + "  " + position);
//            Log.i("length", uniqueSmsList.size() + "");
//            position ++;
//        }
    }

}
