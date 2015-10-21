package de.maryvofin.stundenplan.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.PlanEntry;

public class PlanFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan,container,false);

        int planPage = getArguments().getInt("pos");
        int pageOffset = 500 - planPage;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.add(Calendar.DAY_OF_YEAR, -pageOffset);

        List<PlanEntry> events = Database.getInstance().getTimeEvents(getActivity(), calendar.getTimeInMillis());

        events = Database.getInstance().getProfiles().getCurrentProfile().filter(events);
        Collections.sort(events, new Comparator<PlanEntry>() {
            @Override
            public int compare(PlanEntry lhs, PlanEntry rhs) {
                return (lhs.getStartHour()*60+lhs.getStartMinute()) - (rhs.getStartHour()*60+rhs.getStartMinute());
            }
        });

        //Pausen einfügen
        List<PlanEntry> eventsWithPauses = new LinkedList<>();
        PlanEntry lastEntry = null;
        for(PlanEntry entry: events) {
            if(lastEntry != null) {
                int difference = (entry.getStartHour()*60+entry.getStartMinute()) - (lastEntry.getEndHour()*60+lastEntry.getEndMinute());
                if(difference > 0) {
                    PlanEntry pauseEntry = new PlanEntry();
                    pauseEntry.setStartHour(lastEntry.getEndHour());
                    pauseEntry.setStartMinute(lastEntry.getEndMinute());
                    pauseEntry.setEndHour(entry.getStartHour());
                    pauseEntry.setEndMinute(entry.getStartMinute());
                    pauseEntry.setEventType("#pause#");
                    eventsWithPauses.add(pauseEntry);
                }
            }
            eventsWithPauses.add(entry);
            lastEntry = entry;
        }




        ListView listView = (ListView)view.findViewById(R.id.fragment_plan_list);

        if(events.size() != 0) {
            PlanFragmentListAdapter planFragmentListAdapter = new PlanFragmentListAdapter(getActivity(),eventsWithPauses,planPage);
            listView.setAdapter(planFragmentListAdapter);
        }
        else {
            listView.setVisibility(View.GONE);
            View noEventsView = view.findViewById(R.id.fragment_plan_noevents);
            noEventsView.setVisibility(View.VISIBLE);
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}
