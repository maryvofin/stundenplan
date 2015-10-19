package de.maryvofin.stundenplan.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ClickablePagerTabStrip;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Spinner;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.Profile;

public class MainActivity extends FragmentActivity implements ProgressDialog.OnDismissListener {

    ExpandableSemesterListAdapter semesterListAdapter;
    PlanPagerAdapter planPagerAdapter;
    ArrayAdapter<Profile> profileAdapter;


    boolean[] semesterListExpanded = null;
    int semesterListFirstVisiblePosition = 0;
    int currentPage = 500;
    int selectedProfileItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Activity a = this;
        setLastUpdateText(false);

        //Database.getInstance().load(this);

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

        final View infoButton = findViewById(R.id.activity_main_button_about);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_info, (ViewGroup) findViewById(R.id.dialog_info_root));
                String version = "";
                try {
                    version = a.getPackageManager().getPackageInfo(a.getPackageName(), 0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                ((TextView) dialogView.findViewById(R.id.dialog_info_version_t)).setText(version);
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

        final View shareButton = findViewById(R.id.activity_main_button_share);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShareActivity();
            }
        });

        final Spinner profileSpinner = (Spinner)findViewById(R.id.activity_main_leftDrawer_spinner_profile);
        final View deleteProfileButton = findViewById(R.id.activity_main_leftDrawer_button_profile_delete);


        profileSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Database.getInstance().getProfiles().setCurrentProfile(Database.getInstance().getProfiles().getProfiles().get(position));

                deleteProfileButton.setEnabled(Database.getInstance().getProfiles().getProfiles().size() > 1);

                //System.out.println("??");
                if(selectedProfileItem != position) {
                    selectedProfileItem = position;
                    Database.getInstance().updateProfiles(a);
                    setAdapters();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                profileSpinner.setSelection(0);
            }
        });

        deleteProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(a)
                        .setTitle(Database.getInstance().getProfiles().getCurrentProfile().getName())
                        .setMessage(R.string.text_profile_delete_question)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Database.getInstance().getProfiles().getProfiles().remove(Database.getInstance().getProfiles().getCurrentProfile());

                                profileSpinner.setSelection(0);
                                //Database.getInstance().getProfiles().setCurrentProfile(Database.getInstance().getProfiles().getProfiles().get(0));
                            }
                        })
                        .setNegativeButton(android.R.string.no,null).show();
            }
        });

        View renameProfileButton = findViewById(R.id.activity_main_leftDrawer_button_profile_rename);
        renameProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(a);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(Database.getInstance().getProfiles().getCurrentProfile().getName());

                new AlertDialog.Builder(a)
                        .setTitle(a.getResources().getString(R.string.text_profile_rename))
                        .setView(input)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Database.getInstance().getProfiles().getCurrentProfile().setName(input.getText().toString());
                                Database.getInstance().updateProfiles(a);
                                setAdapters();
                            }
                        })
                        .setNeutralButton(android.R.string.cancel,null).show();
            }
        });

        View newProfileButton = findViewById(R.id.activity_main_leftDrawer_button_profile_new);
        newProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(a);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                //input.setText(Database.getInstance().getProfiles().getCurrentProfile().getName());

                new AlertDialog.Builder(a)
                        .setTitle(a.getResources().getString(R.string.text_profile_rename))
                        .setView(input)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Profile profile = new Profile();
                                profile.setName(input.getText().toString());
                                Database.getInstance().getProfiles().getProfiles().add(profile);
                                Database.getInstance().getProfiles().setCurrentProfile(profile);
                                Database.getInstance().updateProfiles(a);
                                setAdapters();
                            }
                        })
                        .setNeutralButton(android.R.string.cancel,null).show();
            }
        });

        profileAdapter = new ArrayAdapter<Profile>(this,android.R.layout.simple_spinner_item,Database.getInstance().getProfiles().getProfiles());

        ClickablePagerTabStrip pagerTabStrip = (ClickablePagerTabStrip)findViewById(R.id.activity_main_pager_tabstrip);
        pagerTabStrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDayDialog();
            }
        });

        prepareUpdateIntervalSpinner();

        setDrawerListeners();

    }

    public void openShareActivity() {
        Intent intent = new Intent(this, ShareActivity.class);
        /*Bundle args = new Bundle();
        args.putSerializable("entry",entry);
        intent.putExtras(args);*/
        this.startActivity(intent);
    }

    public void prepareUpdateIntervalSpinner() {
        Spinner updateIntervalSpinner = (Spinner)findViewById(R.id.activity_settings_updateinterval_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.text_settings_updateinterval_choices, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateIntervalSpinner.setAdapter(adapter);
        long updateIntervalMinutes = getSharedPreferences("update",Context.MODE_PRIVATE).getLong("interval", 60);
        if(updateIntervalMinutes == 60) {
            updateIntervalSpinner.setSelection(0);
        }
        else {
            updateIntervalSpinner.setSelection(1);
        }

        updateIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor editor = getSharedPreferences("update", Context.MODE_PRIVATE).edit();
                editor.putLong("interval",(position == 0)? 60 : 60*24);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showSelectDayDialog() {
        final ViewPager viewPager = (ViewPager)findViewById(R.id.activity_main_pager);
        final Activity a = this;

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
        ds.show(getFragmentManager(),"datepicker");
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
                storeCurrentPage();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                setAdapters();
                restoreCurrentPage();
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
        for(int i=0;i<profileAdapter.getCount();i++) {
            if(profileAdapter.getItem(i) == Database.getInstance().getProfiles().getCurrentProfile()) profileSpinner.setSelection(i);
        }

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
