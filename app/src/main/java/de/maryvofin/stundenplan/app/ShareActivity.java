package de.maryvofin.stundenplan.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by mark on 13.10.2015.
 */
public class ShareActivity extends AppCompatActivity {

    BluetoothService bService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        setTitle(getResources().getString(R.string.text_share_activity_title));

        bService = new BluetoothService(this);
        if(!bService.startBluetoothAction()) this.finish();


        ViewPager viewPager = (ViewPager)findViewById(R.id.activity_share_viewpager);
        FragmentManager fm = getSupportFragmentManager();
        if(fm == null) throw new NullPointerException();
        ShareFragmentPagerAdapter dfpa = new ShareFragmentPagerAdapter(fm,this,bService);
        viewPager.setAdapter(dfpa);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.activity_share_sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bService != null) bService.endBluetoothAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BluetoothService.REQUEST_ENABLE_BT && resultCode == RESULT_CANCELED) finish();
    }
}
