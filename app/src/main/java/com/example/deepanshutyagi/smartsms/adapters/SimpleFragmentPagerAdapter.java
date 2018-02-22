package com.example.deepanshutyagi.smartsms.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.deepanshutyagi.smartsms.R;
import com.example.deepanshutyagi.smartsms.fragments.PersonalFragment;
import com.example.deepanshutyagi.smartsms.fragments.TransactionalFragment;

/**
 * Created by Deepanshu Tyagi on 2/22/2018.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    // This variable stores context of current class it is used in
    private Context mContext;

    public SimpleFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }


    // This determines the fragment for each tab
    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new PersonalFragment();
        } else{
            return new TransactionalFragment();
        }
    }

    // This determines the number of tabs
    @Override
    public int getCount() {
        return 2;
    }

    // This determines the title for each tab
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return mContext.getString(R.string.personal);
            case 1:
                return mContext.getString(R.string.transactional);
            default:
                return null;
        }
    }
}
