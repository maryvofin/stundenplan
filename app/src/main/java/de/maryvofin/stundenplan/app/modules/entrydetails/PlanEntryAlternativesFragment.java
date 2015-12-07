package de.maryvofin.stundenplan.app.modules.entrydetails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.PlanEntry;

public class PlanEntryAlternativesFragment extends Fragment {



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_alternatives,container, false);
        //ListView listView = (ListView)view.findViewById(R.id.fragment_entry_alternatives_listview);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler);

        PlanEntry entry = (PlanEntry)getArguments().getSerializable("entry");
        List<PlanEntry> entries = new LinkedList<>();

        //Ausschlafen hinzufügen
        if((entry.getStartHour()*60+entry.getStartMinute()) <= 12*60) entries.add(Database.getInstance().generateSleepOffEntry());


        //Liste der Alternativen erstellen
        Calendar calendar = Calendar.getInstance();
        for(int dayOffset=0;dayOffset<7;dayOffset++) {
            calendar.add(Calendar.DAY_OF_YEAR,1);
            List<PlanEntry> dayEntries = Database.getInstance().getTimeEvents(getContext(), calendar.getTimeInMillis()); //Alle Einträge des Tages
            List<PlanEntry> myEntries = Database.getInstance().getProfiles().getCurrentProfile().filter(dayEntries); //Alle Einträge in meinem Plan

            //Alle Veranstaltungen mit anderem Namen entfernen
            Database.getInstance().removeWrongModules(dayEntries, entry);

            //Entfernen der eigenen Veranstaltungen aus der Liste
            Database.getInstance().removeEntriesFromList(dayEntries, myEntries);

            //Auf Überschneidungen prüfen
            Database.getInstance().removeOverlappings(dayEntries,myEntries);

            //Auf echte alternativen beschränken (Gruppe, Vorlesung)
            Database.getInstance().removeWrongAlternatives(dayEntries,entry);

            //Sortieren der Liste
            Database.getInstance().sortEntryListByTime(dayEntries);

            //Hinzufügen eines Tageselements
            if(!dayEntries.isEmpty()) {
                entries.add(Database.getInstance().generateDayHeaderEntry(calendar));
            }

            //Hinzufügen der Einträge
            for(PlanEntry e: dayEntries) entries.add(e);


        }



        /*PlanEntryAlternativeListAdapter planEntryAlternativeListAdapter = new PlanEntryAlternativeListAdapter(getActivity(),entries);
        listView.setAdapter(planEntryAlternativeListAdapter);*/
        PlanEntryAlternativesAdapter planEntryAlternativesAdapter = new PlanEntryAlternativesAdapter(getActivity(),recyclerView,entries);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setAdapter(planEntryAlternativesAdapter);


        return view;
    }
}
