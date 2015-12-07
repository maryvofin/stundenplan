package de.maryvofin.stundenplan.app.modules.plan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Task;
import de.maryvofin.stundenplan.app.modules.entrydetails.DetailsActivity;
import de.maryvofin.stundenplan.app.modules.tasks.TasksAdapter;
import de.maryvofin.stundenplan.app.utils.FABAnimator;

public class PlanFragmentAdapter extends RecyclerView.Adapter<PlanFragmentAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    public static FloatingActionButton fab;
    public static FABAnimator fabAnimator;
    List<PlanEntry> entries;
    RecyclerView recyclerView;
    int futurepast;
    Activity activity;

    public static final int PAUSE_VIEW = 0;
    public static final int ENTRY_VIEW = 1;

    public PlanFragmentAdapter(Activity activity, RecyclerView recyclerView, List<PlanEntry> entries, int futurepast) {
        this.entries = entries;
        this.futurepast = futurepast;
        this.recyclerView = recyclerView;

        this.activity = activity;
    }

    String twoLetterTime(int time) {
        return (time<10) ? "0"+time:""+time;
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void fill(int position);
    }

    public class EntryViewHolder extends ViewHolder {
        View view;
        TextView typeView;
        TextView commentView;
        TextView groupView;
        TextView labelView;
        TextView lecturerView;
        TextView roomView;
        TextView timeView;
        CardView cardView;

        public EntryViewHolder(View v) {
            super(v);
            this.view = v;
            typeView = (TextView) view.findViewById(R.id.view_planentry_text_type);
            commentView = (TextView) view.findViewById(R.id.view_planentry_text_commentcount);
            groupView = (TextView) view.findViewById(R.id.view_planentry_text_group);
            labelView = (TextView) view.findViewById(R.id.view_planentry_text_label);
            lecturerView = (TextView) view.findViewById(R.id.view_planentry_text_lecturer);
            roomView = (TextView) view.findViewById(R.id.view_planentry_text_room);
            timeView = (TextView) view.findViewById(R.id.view_planentry_text_time);
            cardView = (CardView)view.findViewById(R.id.card_view);
        }

        @Override
        public void fill(int position) {
            PlanEntry entry = entries.get(position);

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
                cardView.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.bgcolor_entry_past));
            }

            //bestimmen ob überschneidung
            boolean intersects = Database.getInstance().overlapCount(entry,entries) > 1;

            //Bestimmen ob aktuell
            boolean current = futurepast == 500 && currTimeCode >= entryStartCode && currTimeCode <= entryEndCode;
            if(current) {
                labelView.setTextColor(ContextCompat.getColor(activity,R.color.primary));
            }
            else if(intersects) {
                labelView.setTextColor(ContextCompat.getColor(activity, R.color.textcolor_entry_intersects));
            }
            else {
                labelView.setTextColor(timeView.getTextColors().getDefaultColor());
            }



            //Nutzen des CommentCount als TaskCount
            int taskCount = Task.filterWithPlanEntry(Task.findUncompletedTasks(), entry).size();
            if(taskCount != 0) {
                commentView.setText(""+taskCount);
            }
            else {
                commentView.setText("");
            }



        }

    }

    public class PauseViewHolder extends ViewHolder {
        View view;

        TextView pauseTextView;

        public PauseViewHolder(View v) {
            super(v);
            this.view = v;
            pauseTextView = (TextView)view.findViewById(R.id.view_planentry_pause_text);
        }

        @Override
        public void fill(int position) {
            PlanEntry entry = entries.get(position);

            int pauseInMinutes = (entry.getEndHour()*60+entry.getEndMinute()) - (entry.getStartHour()*60+entry.getStartMinute());

            int hours = pauseInMinutes/60;
            int minutes = pauseInMinutes - (hours*60);

            String text = view.getContext().getResources().getString(R.string.text_pause)+": ";
            if(hours > 0) {
                text += hours+" " +((hours == 1) ? view.getContext().getResources().getString(R.string.text_hour) : view.getContext().getResources().getString(R.string.text_hour_pl)) + " ";
            }
            if(minutes > 0) {
                text += minutes+" " +((minutes == 1) ? view.getContext().getResources().getString(R.string.text_minute) : view.getContext().getResources().getString(R.string.text_minute_pl));
            }

            pauseTextView.setText(text);
        }
    }

    @Override
    public PlanFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case PAUSE_VIEW:
                return new PauseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_planentry_pause,parent,false));
            case ENTRY_VIEW:
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_planentry,parent,false);
                v.setOnClickListener(this);
                return new EntryViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(PlanFragmentAdapter.ViewHolder holder, int position) {
        holder.fill(position);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(entries.get(position).getEventType().equals("#pause#")) return PAUSE_VIEW;
        return ENTRY_VIEW;
    }

    @Override
    public void onClick(View v) {
        int i = recyclerView.getChildAdapterPosition(v);
        generateDetailsDialog(entries.get(i));
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    void generateDetailsDialog(PlanEntry entry) {
        Intent intent = new Intent(activity, DetailsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("entry",entry);
        intent.putExtras(args);
        activity.startActivity(intent);
    }
}
