package de.maryvofin.stundenplan;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import de.maryvofin.stundenplan.database.Database;
import de.maryvofin.stundenplan.database.PlanEntry;

/**
 * Created by mark on 02.10.2015.
 */
public class GroupSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupselection);

        Bundle b = getIntent().getExtras();
        String entryName = b.getString("entry");
        String semester = b.getString("semester");

        setTitle(entryName);

        List<PlanEntry> correspondingEntries = Database.getInstance().getEntriesFromName(this, entryName,semester);

        //Hin und her um doppelte heraus zu filtern
        HashMap<Integer,PlanEntry> filteredEntries = new HashMap<>();
        for(PlanEntry e: correspondingEntries) filteredEntries.put(e.hashCode(),e);
        correspondingEntries.clear();
        for(PlanEntry e: filteredEntries.values()) correspondingEntries.add(e);

        //sortieren
        Collections.sort(correspondingEntries, new Comparator<PlanEntry>() {
            @Override
            public int compare(PlanEntry lhs, PlanEntry rhs) {
                int compared = lhs.getEventType().compareTo(rhs.getEventType());
                if(compared == 0) {
                    if(lhs.getEventGroup() != null && rhs.getEventGroup() != null) compared = lhs.getEventGroup().compareTo(rhs.getEventGroup());
                }
                return compared;
            }
        });


        GroupSelectionAdapter adapter = new GroupSelectionAdapter(this,correspondingEntries);
        ListView list = (ListView)findViewById(R.id.activity_groupselection_list);
        list.setAdapter(adapter);


    }

    @Override
    protected void onStop() {
        Database.getInstance().updateProfiles(this);
        super.onStop();
    }
}
