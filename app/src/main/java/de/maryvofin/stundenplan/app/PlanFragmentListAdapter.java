package de.maryvofin.stundenplan.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Task;

public class PlanFragmentListAdapter extends ArrayAdapter<PlanEntry> {

    List<PlanEntry> entries;
    Activity activity;
    int futurepast;

    enum ListElementType {PAUSE,EVENT}

    public PlanFragmentListAdapter(Activity context, List<PlanEntry> entries, int futurepast) {
        super(context, R.layout.view_planentry,entries);
        this.entries = entries;
        this.activity = context;
        this.futurepast = futurepast;
    }

    String twoLetterTime(int time) {
        return (time<10) ? "0"+time:""+time;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final PlanEntry entry = entries.get(position);
        View view = convertView;

        if(!entry.getEventType().equals("#pause#")) {
            if(view == null || view.getTag() != ListElementType.EVENT)
            {
                view = inflater.inflate(R.layout.view_planentry, parent, false);

            }
            view.setTag(ListElementType.EVENT);

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



            Calendar cal = Calendar.getInstance();
            int currTimeCode = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
            int entryStartCode = entry.getStartHour()*60+entry.getStartMinute();
            int entryEndCode = entry.getEndHour()*60+entry.getEndMinute();

            //Bestimmen ob vergangenheit
            if(futurepast < 500 || ( futurepast == 500 && currTimeCode > entryEndCode  )) {
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.bgcolor_entry_past));
            }

            //bestimmen ob überschneidung
            int positionCount = entries.size();
            if(position > 0) {
                PlanEntry lastEntry = entries.get(position-1);
                int lastEntryEndCode = lastEntry.getEndHour()*60+lastEntry.getEndMinute();
                if(entryStartCode < lastEntryEndCode) labelView.setTextColor(ContextCompat.getColor(activity,R.color.textcolor_entry_intersects));


            }
            if(position < positionCount-1) {
                PlanEntry nextEntry = entries.get(position+1);
                int nextEntryStartCode = nextEntry.getStartHour()*60+nextEntry.getStartMinute();
                if(entryEndCode > nextEntryStartCode) labelView.setTextColor(ContextCompat.getColor(activity, R.color.textcolor_entry_intersects));
            }

            //Bestimmen ob aktuell
            if(futurepast == 500 && currTimeCode >= entryStartCode && currTimeCode <= entryEndCode) {
                labelView.setTextColor(ContextCompat.getColor(activity,R.color.primary));
            }
            else {
                labelView.setTextColor(timeView.getTextColors().getDefaultColor());
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generateDetailsDialog(entry);
                }
            });

            //Nutzen des CommentCount als TaskCount
            int taskCount = Task.filterWithPlanEntry(Task.findUncompletedTasks(),entry).size();
            if(taskCount != 0) {
                commentView.setText(""+taskCount);
            }
            else {
                commentView.setText("");
            }

        }
        else {
            if(view == null || view.getTag() != ListElementType.PAUSE)
            view = inflater.inflate(R.layout.view_planentry_pause,parent,false);
            view.setTag(ListElementType.PAUSE);

            int pauseInMinutes = (entry.getEndHour()*60+entry.getEndMinute()) - (entry.getStartHour()*60+entry.getStartMinute());

            int hours = pauseInMinutes/60;
            int minutes = pauseInMinutes - (hours*60);

            String text = getContext().getResources().getString(R.string.text_pause)+": ";
            if(hours > 0) {
                text += hours+" " +((hours == 1) ? getContext().getResources().getString(R.string.text_hour) : getContext().getResources().getString(R.string.text_hour_pl)) + " ";
            }
            if(minutes > 0) {
                text += minutes+" " +((minutes == 1) ? getContext().getResources().getString(R.string.text_minute) : getContext().getResources().getString(R.string.text_minute_pl));
            }

            TextView pauseTextView = (TextView)view.findViewById(R.id.view_planentry_pause_text);
            pauseTextView.setText(text);

        }

        return view;
    }

    void generateDetailsDialog(PlanEntry entry) {
        Intent intent = new Intent(activity, DetailsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("entry",entry);
        intent.putExtras(args);
        activity.startActivity(intent);
    }
}
