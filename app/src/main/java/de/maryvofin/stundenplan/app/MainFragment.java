package de.maryvofin.stundenplan.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
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
        setLastUpdateText(false);
    }

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
}
