package de.maryvofin.stundenplan.app.modules.plan;


import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import android.content.Context;
import android.os.Bundle;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class PlanPagerAdapter extends FragmentStatePagerAdapter {

    Context context;
    FloatingActionButton fab;

    public PlanPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new PlanFragment();
        Bundle args = new Bundle();
        args.putInt("pos", position);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return 1000;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        int pageOffset = 500 - position;
        c.add(Calendar.DAY_OF_YEAR, -pageOffset);
        long planTime = c.getTimeInMillis();
        return String.format("%tA", c) + " " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date(planTime));
    }


}
