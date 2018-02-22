package com.example.deepanshutyagi.smartsms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.deepanshutyagi.smartsms.adapters.SimpleFragmentPagerAdapter;
import com.example.deepanshutyagi.smartsms.models.SmsModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int READ_SMS_PERMISSIONS_REQUEST = 1;
    private ViewPager mViewPager;
    private TabLayout mtabLayout;
    private ArrayList<SmsModel> smsModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the view pager that will allow the user to swipe between fragments
        mViewPager = (ViewPager) findViewById(R.id.main_viewPager);

        // Create an adapter that knows which fragment should be shown on each page
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        mViewPager.setAdapter(adapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabLayout);
        tabLayout.setupWithViewPager(mViewPager);

        smsModelList = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            getPermissionToReadSMS();
        } else {
            refreshSmsInbox();
        }

    }


    public void getPermissionToReadSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_SMS)) {
                    Toast.makeText(this, "Please allow permission!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Read SMS permission granted", Toast.LENGTH_SHORT).show();
                refreshSmsInbox();
            } else {
                Toast.makeText(this, "Read SMS permission denied", Toast.LENGTH_SHORT).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void refreshSmsInbox() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null);
//        int indexBody = smsInboxCursor.getColumnIndex("body");
//        int indexAddress = smsInboxCursor.getColumnIndex("address");
        String names[] = smsInboxCursor.getColumnNames();
        smsModelList.clear();
        if (!smsInboxCursor.moveToFirst()) return;
        do {
            SmsModel smsModel = new SmsModel();
            smsModel.setAddress(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("address")));
            smsModel.setBody(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("body")));
            smsModel.setDate(Long.parseLong(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date"))));
            smsModel.setDateSent(Long.parseLong(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("date_sent"))));
            smsModel.setId(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("_id"))));
            smsModel.setProtocol(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("protocol"))));
            smsModel.setRead(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("read"))));
            smsModel.setSeen(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("seen"))));
            smsModel.setServiceCenter(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("service_center")));
            smsModel.setStatus(Integer.parseInt(smsInboxCursor.getString(smsInboxCursor.getColumnIndex("status"))));
            smsModelList.add(smsModel);
        } while (smsInboxCursor.moveToNext());
    }
}
