package de.maryvofin.stundenplan.app;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.interfaces.OnCheckedChangeListener;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.IProfileAdapter;
import de.maryvofin.stundenplan.app.database.Profile;
import de.maryvofin.stundenplan.app.database.Task;
import de.maryvofin.stundenplan.app.modules.plan.MainFragment;
import de.maryvofin.stundenplan.app.modules.planconfig.SemesterSelectionFragment;
import de.maryvofin.stundenplan.app.modules.tasks.TasksFragment;

public class MainActivity extends AppCompatActivity implements ProgressDialog.OnDismissListener{

    public static final int IDENTIFIER_ADD_PROFILE = 1;
    public static final int IDENTIFIER_RENAME_PROFILE = 2;
    public static final int IDENTIFIER_DELETE_PROFILE = 3;
    public static final int IDENTIFIER_PLAN = 4;
    public static final int IDENTIFIER_SELECTION = 5;
    public static final int IDENTIFIER_TASKS = 6;
    public static final int IDENTIFIER_UPDATE_STAND = 7;

    Drawer drawer;
    AccountHeader accountHeader;
    SemesterSelectionFragment semesterSelectionFragment = null;
    MainFragment mainFragment = null;
    Fragment currentFragment = null;

    PrimaryDrawerItem tasksItem;
    SecondaryDrawerItem updateItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database.getInstance().load(this);

        createDrawer(savedInstanceState);

        prepareStart();

        showMainFragment();

    }

    void createDrawer(Bundle savedInstanceState) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.color.colorPrimary)
                .withSelectionSecondLineShown(false)
                .withCompactStyle(false)
                .withProfiles(IProfileAdapter.generateProfileList())
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        switch ((int)profile.getIdentifier()) {
                            case IDENTIFIER_ADD_PROFILE:
                                showNewProfileDialog();
                                break;
                            case IDENTIFIER_RENAME_PROFILE:
                                Profile p = ((IProfileAdapter) accountHeader.getActiveProfile()).getProfile();
                                showRenameProfileDialog(p);
                                break;
                            case IDENTIFIER_DELETE_PROFILE:
                                showDeleteProfileDialog();
                                break;
                            default:
                                if (!current) changeCurrentProfile(profile);
                                break;
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        final SecondaryDrawerItem sisItem = new SecondaryDrawerItem().withName(R.string.text_link_sis).withIcon(GoogleMaterial.Icon.gmd_link).withSelectable(false);
        final SecondaryDrawerItem leaItem = new SecondaryDrawerItem().withName(R.string.text_link_lea).withIcon(GoogleMaterial.Icon.gmd_link).withSelectable(false);
        final SecondaryDrawerItem evaItem = new SecondaryDrawerItem().withName(R.string.text_link_eva).withIcon(GoogleMaterial.Icon.gmd_link).withSelectable(false);
        final SecondaryDrawerItem webmailItem = new SecondaryDrawerItem().withName(R.string.text_link_webmail).withIcon(GoogleMaterial.Icon.gmd_link).withSelectable(false);

        final PrimaryDrawerItem planItem = new PrimaryDrawerItem().withName(R.string.text_plan).withIcon(GoogleMaterial.Icon.gmd_event).withIdentifier(IDENTIFIER_PLAN);
        final PrimaryDrawerItem eventSelectionItem = new PrimaryDrawerItem().withName(R.string.text_eventselection).withIcon(GoogleMaterial.Icon.gmd_list).withIdentifier(IDENTIFIER_SELECTION);
        tasksItem = new PrimaryDrawerItem().withName(R.string.text_tasks).withIcon(GoogleMaterial.Icon.gmd_assignment).withIdentifier(IDENTIFIER_TASKS).withBadge("0");
        updateItem = new SecondaryDrawerItem().withIcon(GoogleMaterial.Icon.gmd_refresh).withName(R.string.text_update).withIdentifier(IDENTIFIER_UPDATE_STAND).withBadge(R.string.text_updating).withSelectable(false);

        final SecondaryDrawerItem infoItem = new SecondaryDrawerItem().withName(R.string.text_info).withIcon(GoogleMaterial.Icon.gmd_info).withSelectable(false);

        final SecondarySwitchDrawerItem refreshIntervalSwitch = new SecondarySwitchDrawerItem().withName(R.string.text_update_hourly).withIcon(GoogleMaterial.Icon.gmd_settings).withChecked(false).withOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(IDrawerItem drawerItem, CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("update", Context.MODE_PRIVATE).edit();
                editor.putLong("interval", (isChecked) ? 60 : 60 * 24);
                editor.apply();
            }
        });
        long updateIntervalMinutes = getSharedPreferences("update", Context.MODE_PRIVATE).getLong("interval", 60);
        if(updateIntervalMinutes == 60) refreshIntervalSwitch.withChecked(true);

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(accountHeader)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .addDrawerItems(
                        planItem
                        ,eventSelectionItem
                        ,tasksItem
                        ,new DividerDrawerItem()
                        ,refreshIntervalSwitch
                        ,updateItem
                        ,new SectionDrawerItem().withName(R.string.text_links)
                        ,sisItem
                        ,leaItem
                        ,evaItem
                        ,webmailItem
                        ,new DividerDrawerItem()
                        ,infoItem

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem == sisItem) openSIS();
                        if (drawerItem == evaItem) openEVA();
                        if (drawerItem == leaItem) openLEA();
                        if (drawerItem == webmailItem) openWebmail();
                        if (drawerItem == eventSelectionItem) showEventSelectionFragment();
                        if (drawerItem == planItem) showMainFragment();
                        if (drawerItem == infoItem) showInfoDialog();
                        if (drawerItem == tasksItem) showTasksFragment();
                        if (drawerItem == updateItem) updateDatabase(false);
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        update();
    }

    private void showTasksFragment() {
        showFragment(new TasksFragment());
    }

    void showMainFragment() {
        mainFragment = new MainFragment();
        showFragment(mainFragment);
    }

    private void showEventSelectionFragment() {
        semesterSelectionFragment = new SemesterSelectionFragment();
        showFragment(semesterSelectionFragment);
    }

    void showFragment(Fragment f) {
        if (currentFragment == f) return;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
        currentFragment = f;
    }

    void changeCurrentProfile(IProfile profile) {
        changeCurrentProfile(((IProfileAdapter) profile).getProfile());
    }

    void changeCurrentProfile(Profile profile) {
        Database.getInstance().getProfiles().setCurrentProfile(profile);
        Database.getInstance().updateProfiles(this);
        update();
        if(mainFragment != null && mainFragment == currentFragment) mainFragment.update();
        if(semesterSelectionFragment != null && semesterSelectionFragment == currentFragment) showEventSelectionFragment();
    }

    void update() {
        accountHeader.setProfiles(IProfileAdapter.generateProfileList());

        accountHeader.setActiveProfile(Database.getInstance().getProfiles().getCurrentProfile().getUuid().hashCode());

        accountHeader.addProfiles(new ProfileSettingDrawerItem()
                        .withName(this.getResources().getString(R.string.text_profile_new))
                        .withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text))
                        .withIdentifier(IDENTIFIER_ADD_PROFILE),
                new ProfileSettingDrawerItem()
                        .withName(this.getResources().getString(R.string.text_profile_rename))
                        .withIcon(GoogleMaterial.Icon.gmd_settings)
                        .withIdentifier(IDENTIFIER_RENAME_PROFILE)

        );

        if(Database.getInstance().getProfiles().getProfiles().size() > 1) {
            accountHeader.addProfiles(new ProfileSettingDrawerItem()
                    .withName(this.getResources().getString(R.string.text_profile_delete))
                    .withIcon(GoogleMaterial.Icon.gmd_delete)
                    .withIdentifier(IDENTIFIER_DELETE_PROFILE));
        }


    }

    public void updateTaskBadge() {
        drawer.updateBadge(tasksItem.getIdentifier(), new StringHolder("" + Task.findUncompletedTasks().size()));
    }

    void showMin1LetterDialog(Profile profile) {
        new AlertDialog.Builder(this)
                .setTitle(Database.getInstance().getProfiles().getCurrentProfile().getName())
                .setMessage(R.string.text_profile_delete_question)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
               .show();

        if(profile == null) {
            showNewProfileDialog();
        }
        else {
            showRenameProfileDialog(profile);
        }
    }

    void showNewProfileDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        final Activity a = this;

        new AlertDialog.Builder(this)
                .setTitle(a.getResources().getString(R.string.text_profile_new))
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        if (text.equals("")) {
                            //showMin1LetterDialog(null);
                            return;
                        }
                        Profile profile = new Profile();
                        profile.setName(text);
                        Database.getInstance().getProfiles().getProfiles().add(profile);
                        Database.getInstance().getProfiles().setCurrentProfile(profile);
                        Database.getInstance().updateProfiles(a);
                        update();
                    }
                })
                .setNeutralButton(android.R.string.cancel, null).show();
    }

    void showRenameProfileDialog(final Profile profile) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(profile.getName());
        input.setSelection(input.getText().length());
        final Activity a = this;

        new AlertDialog.Builder(this)
                .setTitle(a.getResources().getString(R.string.text_profile_rename))
                .setView(input)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        if(text.equals("")) {
                            //showMin1LetterDialog(profile);
                            return;
                        }
                        profile.setName(text);
                        Database.getInstance().updateProfiles(a);
                        update();
                    }
                })
                .setNeutralButton(android.R.string.cancel, null).show();
    }

    void showDeleteProfileDialog() {
        final Activity a = this;
        new AlertDialog.Builder(a)
                .setTitle(Database.getInstance().getProfiles().getCurrentProfile().getName())
                .setMessage(R.string.text_profile_delete_question)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Database.getInstance().getProfiles().getProfiles().remove(Database.getInstance().getProfiles().getCurrentProfile());
                        Database.getInstance().getProfiles().setCurrentProfile(Database.getInstance().getProfiles().getProfiles().get(0));
                        Database.getInstance().updateProfiles(a);
                        update();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    void openSIS() {
        openURL("http://sis.h-brs.de");
    }

    void openEVA() {
        openURL("http://eva.inf.h-brs.de");
    }

    void openLEA() {
        openURL("http://lea.hochschule-bonn-rhein-sieg.de");
    }

    void openWebmail() {
        openURL("https://www4.inf.fh-bonn-rhein-sieg.de/horde");
    }

    void openURL(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        browserIntent.setData(Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTaskBadge();
        if(semesterSelectionFragment != null) semesterSelectionFragment.restoreSemesterListState();
        if(mainFragment != null) mainFragment.restoreCurrentPage();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(semesterSelectionFragment != null) semesterSelectionFragment.storeSemesterListState();
        if(mainFragment != null) {
            try {
                mainFragment.storeCurrentPage();
            }
            catch(NullPointerException e) {
                //nichts tun. kann schonmal passieren
            }
        }
    }

    void prepareStart() {
        //Start vorbereiten
        updateDatabase(true);
    }

    public void updateDatabase(boolean timed) {
        Database db = Database.getInstance();
        long updateIntervalMinutes = getSharedPreferences("update", Context.MODE_PRIVATE).getLong("interval", 60);
        long lastUpdate = getSharedPreferences("update",Context.MODE_PRIVATE).getLong("lastupdate", 0);
        boolean allEventsEmpty = db.getAllEvents(this).isEmpty();
        if(allEventsEmpty || lastUpdate+(updateIntervalMinutes*60*1000) < System.currentTimeMillis() || !timed) {
            ProgressDialog dialog = (allEventsEmpty) ? ProgressDialog.show(this, "",
                    "Stundenplan wird heruntergeladen...", true): null;
            if (dialog!= null) dialog.setOnDismissListener(this);
            Updater updater = new Updater();
            UpdateSet set = new UpdateSet(this,dialog);
            if(allEventsEmpty) set.setAbortOnError(false);
            updater.execute(set);
            setLastUpdateText(true);
        }
        else {
            setLastUpdateText(false);
        }
    }

    public void setLastUpdateText(boolean updating) {
        try {
            long lastUpdate = getSharedPreferences("update", Context.MODE_PRIVATE).getLong("lastupdate", 0);
            String timeSpanText = DateUtils.getRelativeTimeSpanString(lastUpdate,System.currentTimeMillis(),DateUtils.MINUTE_IN_MILLIS).toString();
            //updateItem.withName(getResources().getString(R.string.text_update_stand) + ": "+timeSpanText);
            if(updating) {
                drawer.updateBadge(IDENTIFIER_UPDATE_STAND,new StringHolder(R.string.text_updating));
            }
            else {
                drawer.updateBadge(IDENTIFIER_UPDATE_STAND,new StringHolder(timeSpanText));
            }
        }
        catch(Exception e) {
            //Mal wieder doof
        }
    }

    void showInfoDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_info, (ViewGroup) findViewById(R.id.dialog_info_root));
        String version = "";
        try {
            version = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ((TextView) dialogView.findViewById(R.id.dialog_info_version_t)).setText(version);
        AlertDialog.Builder db = new AlertDialog.Builder(this);
        db.setView(dialogView);
        db.setTitle(this.getResources().getString(R.string.text_dialog_info_title));
        db.setPositiveButton(this.getResources().getString(R.string.text_dialog_info_button), new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        db.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if(drawer.isDrawerOpen()) {
                    drawer.closeDrawer();
                    return true;
                }

                if(currentFragment == semesterSelectionFragment) {
                    drawer.setSelection(IDENTIFIER_PLAN);
                    return true;
                }
                else if(currentFragment == mainFragment){
                    if(mainFragment != null) mainFragment.backPressed();
                }
                return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        update();
    }
}
