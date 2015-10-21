package de.maryvofin.stundenplan.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ClickablePagerTabStrip;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

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

        planPagerAdapter = new PlanPagerAdapter(this.getChildFragmentManager(), this.getContext());
        setAdapter();
        setLastUpdateText(false);

        ClickablePagerTabStrip pagerTabStrip = (ClickablePagerTabStrip)view.findViewById(R.id.tabstrip);
        pagerTabStrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDayDialog();
            }
        });

        FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager pager = (ViewPager)view.findViewById(R.id.pager);
                pager.setCurrentItem(500);
            }
        });


        return view;
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
        setLastUpdateText(false);
    }

    @SuppressLint("SetTextI18n")
    public void setLastUpdateText(boolean updating) {
        TextView textView = (TextView)view.findViewById(R.id.lastupdate_text);
        long lastUpdate = getActivity().getSharedPreferences("update", Context.MODE_PRIVATE).getLong("lastupdate", 0);

        if(updating) {
            textView.setText(getResources().getString(R.string.text_updating));
        }
        else {
            String text = (lastUpdate != 0) ? DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(lastUpdate))
                    + " "+ DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date(lastUpdate))+" "+getResources().getString(R.string.text_clock)
                    : getResources().getString(R.string.text_no_update);
            textView.setText(getResources().getString(R.string.text_last_update)+": "+text);
        }

    }

    public void showSelectDayDialog() {
        final ViewPager viewPager = (ViewPager)view.findViewById(R.id.pager);
        final Activity a = getActivity();

        final Calendar cCal = Calendar.getInstance();
        cCal.add(Calendar.DAY_OF_YEAR,viewPager.getCurrentItem()-500);

        DialogFragment ds = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                return new DatePickerDialog(a, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar nCal = Calendar.getInstance();
                        nCal.set(Calendar.YEAR,year);
                        nCal.set(Calendar.MONTH,monthOfYear);
                        nCal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        long timeDelta = nCal.getTimeInMillis() - cCal.getTimeInMillis();
                        int dayDelta = (int)(timeDelta / (60000*60*24));
                        viewPager.setCurrentItem(viewPager.getCurrentItem()+dayDelta);

                    }
                },cCal.get(Calendar.YEAR),cCal.get(Calendar.MONTH),cCal.get(Calendar.DAY_OF_MONTH));
            }
        };
        ds.show(getActivity().getFragmentManager(),"datepicker");
    }
}
