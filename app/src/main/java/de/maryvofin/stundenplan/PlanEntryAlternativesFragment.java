package de.maryvofin.stundenplan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import de.maryvofin.stundenplan.database.Database;
import de.maryvofin.stundenplan.database.PlanEntry;

/**
 * Created by mark on 07.10.2015.
 */
public class PlanEntryAlternativesFragment extends Fragment {

    boolean overlaps(PlanEntry entry, List<PlanEntry> entries) {
        int entryStartCode = entry.getStartHour()*60+entry.getStartMinute();
        int entryEndCode = entry.getEndHour()*60+entry.getEndMinute();

        for(PlanEntry e: entries) {
            int eStartCode = e.getStartHour()*60+e.getStartMinute();
            int eEndCode = e.getEndHour()*60+e.getEndMinute();

            if( (entryStartCode >= eStartCode && entryStartCode <= eEndCode) || (entryEndCode >= eStartCode && entryEndCode <= eEndCode) ) {
                //System.out.println(entry.getEventName()+": überschneidung");
                return true;
            }

        }
        //System.out.println(entry.getEventName()+": geht");

        return false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_alternatives,container, false);

        PlanEntry entry = (PlanEntry)getArguments().getSerializable("entry");


        ListView listView = (ListView)view.findViewById(R.id.fragment_entry_alternatives_listview);

        Calendar calendar = Calendar.getInstance();
        List<PlanEntry> entries = new LinkedList<>();

        if((entry.getStartHour()*60+entry.getStartMinute()) <= 12*60) {

            PlanEntry alternative = new PlanEntry();
            alternative.setEventType("P");
            alternative.setEventName("Ausschlafen");
            alternative.setStartHour(9);
            alternative.setStartMinute(0);
            alternative.setEndMinute(0);
            alternative.setEndHour(12);
            alternative.setRoom("Schlafzimmer");
            alternative.setLecturer("Du");


            entries.add(alternative);
        }

        //Liste der Alternativen erstellen
        /*
        Alternative wenn:
            Gruppe == null -> übereinstimmung in eventtype, eventname, unterschied in raum oder dozent, zeiträume unterschiedlich!
            Gruppe != null -> übereinstimmung in eventtype, eventname, unterschied in gruppe, zeiträume unterschiedlich!
         */

        for(int dayOffset=0;dayOffset<6;dayOffset++) {
            calendar.add(Calendar.DAY_OF_YEAR,dayOffset);
            List<PlanEntry> dayEntries = Database.getInstance().getTimeEvents(getContext(), calendar.getTimeInMillis());
            List<PlanEntry> myEntries = Database.getInstance().getProfiles().getCurrentProfile().filter(dayEntries);
            //Entfernen der eigenen Veranstaltungen aus der Liste
            for(PlanEntry e: myEntries) dayEntries.remove(dayEntries);

            //Auf Überschneidungen oder passendes pattern prüfen
            Iterator<PlanEntry> iterator = dayEntries.iterator();
            while(iterator.hasNext()) {
                PlanEntry checkEntry = iterator.next();

                if(!entry.getEventName().equals(checkEntry.getEventName())) {
                    iterator.remove();
                    continue;
                }

                if(!entry.getEventType().equals(checkEntry.getEventType())) {
                    iterator.remove();
                    continue;
                }

                if(entry.getEventGroup() != null && checkEntry.getEventGroup() != null) {
                      if(entry.getEventGroup().equals(checkEntry.getEventGroup())) {
                          iterator.remove();
                          continue;
                      }
                }

                if(overlaps(checkEntry,myEntries)) iterator.remove();
            }

            //Sortieren der Liste
            Collections.sort(dayEntries, new Comparator<PlanEntry>() {

                @Override
                public int compare(PlanEntry lhs, PlanEntry rhs) {
                    return (lhs.getStartHour()*60+lhs.getStartMinute()) - (rhs.getStartHour()*60+rhs.getStartMinute());
                }
            });


            //Hinzufügen eines Tageselements
            if(!dayEntries.isEmpty()) {
                PlanEntry dayEntry = new PlanEntry();
                dayEntry.setEventType("#dayentry#");
                dayEntry.setWeekDay(calendar.get(Calendar.DAY_OF_WEEK));
                dayEntry.setEventName(DateFormatSymbols.getInstance().getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)]);
                entries.add(dayEntry);
            }

            //Hinzufügen der Einträge
            for(PlanEntry e: dayEntries) entries.add(e);


        }



        PlanEntryAlternativeListAdapter planEntryAlternativeListAdapter = new PlanEntryAlternativeListAdapter(getActivity(),entries);
        listView.setAdapter(planEntryAlternativeListAdapter);



        return view;
    }
}
