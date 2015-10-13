package de.maryvofin.stundenplan.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.maryvofin.stundenplan.app.database.PlanEntry;

/**
 * Created by mark on 07.10.2015.
 */
public class DetailsFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private Context context;
    private PlanEntry entry;

    public DetailsFragmentPagerAdapter(Context context, FragmentManager fm, PlanEntry entry) {
        super(fm);
        this.context = context;
        this.entry = entry;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putSerializable("entry",entry);
        Fragment f;
        switch (position) {
            case 0:
                f = new PlanEntryDetailsFragment();
                f.setArguments(args);
                return f;
            case 1:
                f = new PlanEntryAlternativesFragment();
                f.setArguments(args);
                return f;
        }

        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getStringArray(R.array.text_details_activity_tabslabels)[position];
    }
}
