package de.maryvofin.stundenplan.app;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import de.maryvofin.stundenplan.app.database.PlanEntry;

/**
 * Created by mark on 13.10.2015.
 */
public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        setTitle(getResources().getString(R.string.text_share_activity_title));

        ViewPager viewPager = (ViewPager)findViewById(R.id.activity_share_viewpager);
        FragmentManager fm = getSupportFragmentManager();
        if(fm == null) throw new NullPointerException();
        ShareFragmentPagerAdapter dfpa = new ShareFragmentPagerAdapter(fm,this);
        viewPager.setAdapter(dfpa);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.activity_share_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

}
