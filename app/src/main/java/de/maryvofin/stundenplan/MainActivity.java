package de.maryvofin.stundenplan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Spinner;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.maryvofin.stundenplan.database.Database;
import de.maryvofin.stundenplan.database.Profile;

public class MainActivity extends AppCompatActivity implements ProgressDialog.OnDismissListener {

    ExpandableSemesterListAdapter semesterListAdapter;
    PlanPagerAdapter planPagerAdapter;
    ArrayAdapter<Profile> profileAdapter;


    boolean[] semesterListExpanded = null;
    int semesterListFirstVisiblePosition = 0;
    int currentPage = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity a = this;
        setLastUpdateText(false);

        //Start vorbereiten
        Database db = Database.getInstance();
        long updateIntervalMinutes = getSharedPreferences("update",Context.MODE_PRIVATE).getLong("interval", 60);
        long lastUpdate = getSharedPreferences("update",Context.MODE_PRIVATE).getLong("lastupdate", 0);
        boolean allEventsEmpty = db.getAllEvents(this).isEmpty();
        if(allEventsEmpty || lastUpdate+(updateIntervalMinutes*60*1000) < System.currentTimeMillis()) {
            ProgressDialog dialog = (allEventsEmpty) ? ProgressDialog.show(this, "",
                    "Stundenplan wird heruntergeladen...", true): null;
            if (dialog!= null) dialog.setOnDismissListener(this);
            Updater updater = new Updater();
            UpdateSet set = new UpdateSet(this,dialog);
            if(allEventsEmpty) set.setAbortOnError(false);
            updater.execute(set);
            setLastUpdateText(true);
        }

        semesterListAdapter = new ExpandableSemesterListAdapter(this);
        planPagerAdapter = new PlanPagerAdapter(this.getSupportFragmentManager(), this);

        //Todaybutton aktion setzen
        View todayButton = findViewById(R.id.activity_main_button_today);
        todayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager viewPager = (ViewPager)findViewById(R.id.activity_main_pager);
                viewPager.setCurrentItem(500);
            }
        });

        View infoButton = findViewById(R.id.activity_main_button_about);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_info, (ViewGroup) findViewById(R.id.dialog_info_root));
                String version = "";
                try {
                    version = a.getPackageManager().getPackageInfo(a.getPackageName(),0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                ((TextView)dialogView.findViewById(R.id.dialog_info_version_t)).setText(version);
                AlertDialog.Builder db = new AlertDialog.Builder(a);
                db.setView(dialogView);
                db.setTitle(a.getResources().getString(R.string.text_dialog_info_title));
                db.setPositiveButton(a.getResources().getString(R.string.text_dialog_info_button), new
                        DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                db.show();
            }
        });

        View settingsButton = findViewById(R.id.activity_main_button_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(a,SettingsActivity.class);
                a.startActivity(intent);
            }
        });

        profileAdapter = new ArrayAdapter<Profile>(this,android.R.layout.simple_spinner_item,Database.getInstance().getProfiles().getProfiles());

        setDrawerListeners();

    }

    public void setLastUpdateText(boolean updating) {

        TextView textView = (TextView)findViewById(R.id.activity_main_lastupdate_text);
        long lastUpdate = getSharedPreferences("update",Context.MODE_PRIVATE).getLong("lastupdate", 0);

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

    public void setDrawerListeners() {
        DrawerLayout dl = (DrawerLayout)findViewById(R.id.activity_main_drawerLayout);
        dl.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                setAdapters();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    public void setAdapters() {
        ExpandableListView semesterList = (ExpandableListView)findViewById(R.id.activity_main_leftDrawer_semesterList);
        semesterList.setAdapter(semesterListAdapter);
        semesterListAdapter.notifyDataSetChanged();

        ViewPager viewPager = (ViewPager)findViewById(R.id.activity_main_pager);
        viewPager.setAdapter(planPagerAdapter);
        if(viewPager.getCurrentItem() == 0) viewPager.setCurrentItem(500);
        planPagerAdapter.notifyDataSetChanged();

        Spinner profileSpinner = (Spinner)findViewById(R.id.activity_main_leftDrawer_spinner_profile);
        profileSpinner.setAdapter(profileAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setAdapters();

        restoreSemesterListState();
        restoreCurrentPage();

    }

    private void restoreCurrentPage() {
        ViewPager pager = (ViewPager)findViewById(R.id.activity_main_pager);
        pager.setCurrentItem(currentPage);
    }

    @Override
    protected void onPause() {
        super.onPause();

        storeSemesterListState();
        storeCurrentPage();


    }

    private void storeCurrentPage() {
        ViewPager pager = (ViewPager)findViewById(R.id.activity_main_pager);
        currentPage = pager.getCurrentItem();
    }

    void restoreSemesterListState() {
        ExpandableListView v =(ExpandableListView)findViewById(R.id.activity_main_leftDrawer_semesterList);

        if(semesterListExpanded != null) {
            for(int i=0;i<semesterListExpanded.length;i++) {
                if(semesterListExpanded[i])v.expandGroup(i);
            }
            v.setSelection(semesterListFirstVisiblePosition);
        }

    }

    void storeSemesterListState() {
        ExpandableListView v =(ExpandableListView)findViewById(R.id.activity_main_leftDrawer_semesterList);

        semesterListExpanded = new boolean[semesterListAdapter.getGroupCount()];
        for(int i=0;i<semesterListExpanded.length;i++) {
            semesterListExpanded[i] = v.isGroupExpanded(i);
        }
        semesterListFirstVisiblePosition = v.getFirstVisiblePosition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            toggleDrawer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        setAdapters();
    }

    boolean openDrawer() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.activity_main_drawerLayout);
        if(!drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.openDrawer(GravityCompat.START);
            return false;
        }
        return true;
    }

    boolean closeDrawer() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.activity_main_drawerLayout);
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }

    void toggleDrawer() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.activity_main_drawerLayout);
        if(drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                toggleDrawer();
                return true;
            case KeyEvent.KEYCODE_BACK:
                return !closeDrawer();
        }


        return super.onKeyDown(keyCode, event);
    }
}
