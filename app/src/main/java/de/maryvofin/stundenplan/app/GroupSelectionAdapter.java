package de.maryvofin.stundenplan.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.PlanEntry;

/**
 * Created by mark on 02.10.2015.
 */
public class GroupSelectionAdapter extends ArrayAdapter<PlanEntry> {
    List<PlanEntry> entries;
    Activity activity;

    public GroupSelectionAdapter(Activity activity, List<PlanEntry> entries) {
        super(activity, R.layout.view_groupselection_item, entries);
        this.entries = entries;
        this.activity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = (convertView == null) ? inflater.inflate(R.layout.view_groupselection_item,parent,false) : convertView;

        final CheckBox checkBox = (CheckBox)view.findViewById(R.id.view_groupselection_item_checkbox);
        TextView text = (TextView)view.findViewById(R.id.view_groupselection_item_text);
        TextView lecturer = (TextView)view.findViewById(R.id.view_groupselection_item_lecturer);

        final PlanEntry currentEntry = entries.get(position);

        String label = "";
        if(currentEntry.getEventGroup() == null) {
            label = transformEventType(currentEntry.getEventType());
        }
        else {
            label = currentEntry.getEventGroup()+" ("+transformEventType(currentEntry.getEventType())+")";
        }

        text.setText(label);
        text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
        lecturer.setText(currentEntry.getLecturer());
        lecturer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(Database.getInstance().getProfiles().getCurrentProfile().getFilter().containsKey(currentEntry.hashCode()));

        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Database.getInstance().getProfiles().getCurrentProfile().getFilter().put(currentEntry.hashCode(),currentEntry);
                }
                else {
                    Database.getInstance().getProfiles().getCurrentProfile().getFilter().remove(currentEntry.hashCode());
                }
            }
        });


        return view;
    }

    String transformEventType(String eventType) {
        if(eventType.equals("V")) return activity.getResources().getString(R.string.text_vorlesung);
        if(eventType.equals("Ü")) return activity.getResources().getString(R.string.text_uebung);
        if(eventType.equals("P")) return activity.getResources().getString(R.string.text_praktikum);
        if(eventType.equals("S")) return activity.getResources().getString(R.string.text_seminar);
        if(eventType.equals("Pj")) return activity.getResources().getString(R.string.text_projekt);
        return eventType;
    }
}
