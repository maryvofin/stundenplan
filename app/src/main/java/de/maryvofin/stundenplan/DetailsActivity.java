package de.maryvofin.stundenplan;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;

import de.maryvofin.stundenplan.R;
import de.maryvofin.stundenplan.database.PlanEntry;

/**
 * Created by mark on 07.10.2015.
 */
public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle args = getIntent().getExtras();
        PlanEntry entry = (PlanEntry)args.getSerializable("entry");

        setTitle(entry.getEventName());

        ViewPager viewPager = (ViewPager)findViewById(R.id.activity_details_viewpager);
        FragmentManager fm = getSupportFragmentManager();
        if(fm == null) throw new NullPointerException();
        DetailsFragmentPagerAdapter dfpa = new DetailsFragmentPagerAdapter(this,fm, entry);
        viewPager.setAdapter(dfpa);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.activity_details_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

}
