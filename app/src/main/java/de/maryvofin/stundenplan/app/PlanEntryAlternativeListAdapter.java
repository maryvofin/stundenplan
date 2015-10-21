package de.maryvofin.stundenplan.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.maryvofin.stundenplan.app.database.PlanEntry;

/**
 * Created by mark on 07.10.2015.
 */
public class PlanEntryAlternativeListAdapter extends ArrayAdapter<PlanEntry> {

    private final List<PlanEntry> entries;
    private final Activity activity;

    public PlanEntryAlternativeListAdapter(Activity context, List<PlanEntry> entries) {
        super(context, R.layout.view_planentry, entries);
        this.entries = entries;
        this.activity = context;
    }

    String twoLetterTime(int time) {
        return (time<10) ? "0"+time:""+time;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final PlanEntry entry = entries.get(position);
        View view = (convertView == null) ? inflater.inflate(R.layout.view_planentry, parent, false) : convertView;

        if(entry.getEventType().equals("#dayentry#")) {
            view =  inflater.inflate(R.layout.view_planentry_pause, parent, false);
            TextView text = (TextView)view.findViewById(R.id.view_planentry_pause_text);
            text.setText(entry.getEventName());
        }
        else {

            TextView typeView = (TextView) view.findViewById(R.id.view_planentry_text_type);
            TextView commentView = (TextView) view.findViewById(R.id.view_planentry_text_commentcount);
            TextView groupView = (TextView) view.findViewById(R.id.view_planentry_text_group);
            TextView labelView = (TextView) view.findViewById(R.id.view_planentry_text_label);
            TextView lecturerView = (TextView) view.findViewById(R.id.view_planentry_text_lecturer);
            TextView roomView = (TextView) view.findViewById(R.id.view_planentry_text_room);
            TextView timeView = (TextView) view.findViewById(R.id.view_planentry_text_time);

            String startHour = twoLetterTime(entry.getStartHour());
            String startMinute = twoLetterTime(entry.getStartMinute());
            String endHour = twoLetterTime(entry.getEndHour());
            String endMinute = twoLetterTime(entry.getEndMinute());

            typeView.setText("(" + entry.getEventType() + ")");
            if (entry.getEventGroup() != null) {
                groupView.setText(entry.getEventGroup());
            } else {
                groupView.setText("-");
            }
            labelView.setText(entry.getEventName());
            lecturerView.setText(entry.getLecturer());
            roomView.setText(entry.getRoom());
            timeView.setText(startHour + ":" + startMinute + " - " + endHour + ":" + endMinute);

            commentView.setText(entry.getSemester());

        }


        return view;
    }
}
