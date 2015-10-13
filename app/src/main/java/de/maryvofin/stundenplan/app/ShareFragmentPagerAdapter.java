package de.maryvofin.stundenplan.app;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by mark on 14.10.2015.
 */
public class ShareFragmentPagerAdapter extends FragmentPagerAdapter {

    Activity activity;

    public ShareFragmentPagerAdapter(FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }


    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
