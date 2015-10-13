package de.maryvofin.stundenplan.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Profile;
import de.maryvofin.stundenplan.app.database.Semester;

/**
 * Created by mark on 01.10.2015.
 */
public class ExpandableSemesterListAdapter extends BaseExpandableListAdapter {

    Activity activity;

    public ExpandableSemesterListAdapter(Activity activity) {
        this.activity = activity;

    }

    @Override
    public int getGroupCount() {
        return Database.getInstance().getSemesters().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return Database.getInstance().getSemesters().get(groupPosition).getModules().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        List<Semester> list = Database.getInstance().getSemesters();
        Collections.sort(list);
        return list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<String> list = new LinkedList<>();

        Collection<String> collection = Database.getInstance().getSemesters().get(groupPosition).getModules();
        for(String s: collection) list.add(s);
        Collections.sort(list);

        return list.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = (convertView == null) ? inflater.inflate(R.layout.view_expandable_semester_group,parent,false) : convertView;

        TextView text = (TextView)view.findViewById(R.id.view_expandable_semester_group_text);
        text.setText(((Semester)getGroup(groupPosition)).getName());

        return view;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = (convertView == null) ? inflater.inflate(R.layout.view_expandable_semester_item,parent,false) : convertView;

        final String entry = (String)getChild(groupPosition, childPosition);
        final Semester semester= (Semester)getGroup(groupPosition);

        TextView text = (TextView)view.findViewById(R.id.view_expandable_semester_item_text);
        text.setText(entry);
        text.setOnClickListener(new TextView.OnClickListener() {

            @Override
            public void onClick(View v) {

                List<PlanEntry> correspondingEntries = Database.getInstance().getEntriesFromName(activity, entry,semester.getName() );
                openGroupSelectionActivity(entry,semester.getName());
            }
        });


        CheckBox checkbox = (CheckBox)view.findViewById(R.id.view_expandable_semester_item_checkbox);
        checkbox.setOnCheckedChangeListener(null);
        checkbox.setChecked(false);

        Profile profile = Database.getInstance().getProfiles().getCurrentProfile();
        //durchsuchen des filters nach passenden Einträgen:
        for (PlanEntry pe: profile.getFilter().values()) {
            if(pe.getEventName().equals(entry)) {
                checkbox.setChecked(true);
            }
        }
        checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                List<PlanEntry> correspondingEntries = Database.getInstance().getEntriesFromName(activity, entry,semester.getName());
                //Hin und her um doppelte heraus zu filtern
                HashMap<Integer,PlanEntry> filteredEntries = new HashMap<>();
                for(PlanEntry e: correspondingEntries) filteredEntries.put(e.hashCode(),e);
                correspondingEntries.clear();
                for(PlanEntry e: filteredEntries.values()) correspondingEntries.add(e);

                if (isChecked) {

                    if (correspondingEntries.size() == 1) {
                        Database.getInstance().getProfiles().getCurrentProfile().getFilter().put(correspondingEntries.get(0).hashCode(), correspondingEntries.get(0));
                    } else {
                        openGroupSelectionActivity(entry,semester.getName());


                    }
                } else {
                    for (PlanEntry pe : correspondingEntries) {

                        Database.getInstance().getProfiles().getCurrentProfile().getFilter().remove(pe.hashCode());
                    }
                }
                Database.getInstance().updateProfiles(activity);
            }
        });



        return view;
    }

    void openGroupSelectionActivity(String entry, String semester) {
        Intent intent = new Intent(activity,GroupSelectionActivity.class);
        Bundle b = new Bundle();
        b.putString("entry",entry);
        b.putString("semester",semester);
        intent.putExtras(b);
        activity.startActivity(intent);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}
