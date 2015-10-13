package de.maryvofin.stundenplan.app;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;

import de.maryvofin.stundenplan.app.database.PlanEntry;

/**
 * Created by mark on 07.10.2015.
 */
public class PlanEntryDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry_details,container, false);

        PlanEntry entry = (PlanEntry)getArguments().getSerializable("entry");

        TextView vSemester = (TextView) view.findViewById(R.id.fragment_entry_details_semester_t);
        TextView vModule = (TextView) view.findViewById(R.id.fragment_entry_details_module_t);
        TextView vEventType = (TextView) view.findViewById(R.id.fragment_entry_details_eventtype_t);
        TextView vGroup = (TextView) view.findViewById(R.id.fragment_entry_details_group_t);
        TextView vTimespan = (TextView) view.findViewById(R.id.fragment_entry_details_timespan_t);
        TextView vWeekday = (TextView) view.findViewById(R.id.fragment_entry_details_weekday_t);
        TextView vTime = (TextView) view.findViewById(R.id.fragment_entry_details_time_t);
        TextView vLecturer = (TextView) view.findViewById(R.id.fragment_entry_details_lecturer_t);
        TextView vRoom = (TextView) view.findViewById(R.id.fragment_entry_details_room_t);

        vSemester.setText(entry.getSemester());
        vModule.setText(entry.getEventName());
        vEventType.setText(transformEventType(entry.getEventType()));
        vGroup.setText((entry.getEventGroup() != null)?entry.getEventGroup():"-");
        vTimespan.setText(entry.getTimeSpan());
        vLecturer.setText(entry.getLecturer());
        vRoom.setText(entry.getRoom());

        String startHour = (entry.getStartHour()<10) ? "0"+entry.getStartHour() : ""+entry.getStartHour();
        String endHour = (entry.getEndHour()<10) ? "0"+entry.getEndHour() : ""+entry.getEndHour();
        String startMinute = (entry.getStartMinute()<10) ? "0"+entry.getStartMinute() : ""+entry.getStartMinute();
        String endMinute = (entry.getEndMinute()<10) ? "0"+entry.getEndMinute() : ""+entry.getEndMinute();
        vTime.setText(startHour+":"+startMinute+" - "+endHour+":"+endMinute);

        vWeekday.setText(DateFormatSymbols.getInstance().getWeekdays()[entry.getWeekDay()+2%7]);


        return view;
    }

    String transformEventType(String eventType) {
        if(eventType.equals("V")) return getActivity().getResources().getString(R.string.text_vorlesung);
        if(eventType.equals("Ü")) return getActivity().getResources().getString(R.string.text_uebung);
        if(eventType.equals("P")) return getActivity().getResources().getString(R.string.text_praktikum);
        if(eventType.equals("S")) return getActivity().getResources().getString(R.string.text_seminar);
        if(eventType.equals("Pj")) return getActivity().getResources().getString(R.string.text_projekt);
        return eventType;
    }

}
