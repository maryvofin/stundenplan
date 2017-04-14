package de.maryvofin.stundenplan.app.modules.plan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ClickablePagerTabStrip;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.Calendar;

import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.utils.FABAnimator;
import de.maryvofin.stundenplan.app.utils.ViewPager;

public class MainFragment extends Fragment implements android.support.v4.view.ViewPager.OnPageChangeListener {

    PlanPagerAdapter planPagerAdapter;
    View view;

    public MainFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main,container,false);

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        PlanFragmentAdapter.fab = fab;
        PlanFragmentAdapter.fabAnimator = new FABAnimator(fab);
        PlanFragmentAdapter.fab.setImageDrawable(
                new IconicsDrawable(
                        this.getContext())
                        .icon(GoogleMaterial.Icon.gmd_undo)
                        .color(Color.WHITE)
                        .sizeDp(40));


        planPagerAdapter = new PlanPagerAdapter(this.getChildFragmentManager(), this.getContext());
        setAdapter();

        ClickablePagerTabStrip pagerTabStrip = (ClickablePagerTabStrip)view.findViewById(R.id.tabstrip);
        pagerTabStrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDayDialog();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
                pager.setCurrentItem(500);
            }
        });

        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.addOnPageChangeListener(this);


        return view;
    }

    ViewPager getViewPager() {
        return (ViewPager)view.findViewById(R.id.pager);
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapter();
    }

    public void setAdapter() {
        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(planPagerAdapter);
        pager.setOffscreenPageLimit(5);
        restoreCurrentPage();
    }

    public void storeCurrentPage() {
        try {
            ViewPager pager = (ViewPager) view.findViewById(R.id.pager);
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("plan", Context.MODE_PRIVATE).edit();
            editor.putInt("currentPage", pager.getCurrentItem());
            editor.apply();
        }
        catch(NullPointerException e) {
            //Passiert ständig im Emulator
        }
    }

    public void restoreCurrentPage() {
        ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
        try {
            pager.setCurrentItem(getStoredPage());
        }
        catch(NullPointerException e) {
            //Ewiges Leid mit dem Emulator
        }
    }

    public int getStoredPage() {
        return getActivity().getSharedPreferences("plan", Context.MODE_PRIVATE).getInt("currentPage", 500);
    }


    public void update() {
        storeCurrentPage();
        setAdapter();
    }

    public void showSelectDayDialog() {
        final ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
        final Calendar cCal = Calendar.getInstance();
        cCal.add(Calendar.DAY_OF_YEAR,viewPager.getCurrentItem()-500);

        SelectDayDialogFragment ds = new SelectDayDialogFragment();
        Bundle b = new Bundle();
        b.putInt(SelectDayDialogFragment.BUNDLEKEY_DAY, cCal.get(Calendar.DAY_OF_MONTH));
        b.putInt(SelectDayDialogFragment.BUNDLEKEY_MONTH, cCal.get(Calendar.MONTH));
        b.putInt(SelectDayDialogFragment.BUNDLEKEY_YEAR, cCal.get(Calendar.YEAR));
        ds.setArguments(b);
        ds.setTargetFragment(this, SelectDayDialogFragment.ACTIVITY_RESULT_KEY);

        ds.show(getActivity().getSupportFragmentManager(),"datepicker");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SelectDayDialogFragment.ACTIVITY_RESULT_KEY && resultCode == SelectDayDialogFragment.ACTIVITY_RESULT_KEY) {
            long time = data.getLongExtra(SelectDayDialogFragment.INTENTKEY_TIME, 0);

            final ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
            final Calendar cCal = Calendar.getInstance();
            cCal.add(Calendar.DAY_OF_YEAR,viewPager.getCurrentItem()-500);

            long timeDelta = time - cCal.getTimeInMillis();
            int dayDelta = (int)(timeDelta / (60000*60*24));
            viewPager.setCurrentItem(viewPager.getCurrentItem()+dayDelta);
        }

    }

    public boolean backPressed() {
        ViewPager pager = getViewPager();

        int currentItem = pager.getCurrentItem();

        if(currentItem != 500) {
            if (currentItem > 500) pager.setCurrentItem(currentItem - 1);
            if (currentItem < 500) pager.setCurrentItem(currentItem + 1);
        }
        else {
            getActivity().finish();
        }

        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        storeCurrentPage();
        if(position == 500) {
            PlanFragmentAdapter.fabAnimator.hide();
        }
        else {
            PlanFragmentAdapter.fabAnimator.grow();
            PlanFragmentAdapter.fabAnimator.show();
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
