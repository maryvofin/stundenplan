package de.maryvofin.stundenplan.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mark on 20.10.2015.
 */
public class MainFragment extends Fragment {

    PlanPagerAdapter planPagerAdapter;
    View view;
    int currentPage = 500;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main,container,false);

        planPagerAdapter = new PlanPagerAdapter(this.getFragmentManager(), this.getContext());
        setAdapter();

        return view;
    }

    public void setAdapter() {
        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(planPagerAdapter);
        restoreCurrentPage();
    }

    public void storeCurrentPage() {
        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        currentPage = pager.getCurrentItem();
    }

    public void restoreCurrentPage() {
        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.setCurrentItem(currentPage);
    }


    public void update() {
        storeCurrentPage();
        setAdapter();
    }
}
