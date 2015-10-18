package de.maryvofin.stundenplan.app;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by mark on 14.10.2015.
 */
public class ShareFragmentPagerAdapter extends FragmentPagerAdapter {

    Activity activity;
    BluetoothService bluetoothService;


    public ShareFragmentPagerAdapter(FragmentManager fm, Activity activity, BluetoothService bluetoothService) {
        super(fm);
        this.activity = activity;
        this.bluetoothService = bluetoothService;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch (position) {
            case 0:

                return new ShareFragmentSending();
            case 1:
                return new ShareFragmentReceiving();


        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return activity.getResources().getStringArray(R.array.text_share_activity_tabslabels)[position];
    }
}
