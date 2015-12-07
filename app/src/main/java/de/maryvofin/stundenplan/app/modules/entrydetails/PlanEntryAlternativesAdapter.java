package de.maryvofin.stundenplan.app.modules.entrydetails;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.modules.plan.PlanFragmentAdapter;

public class PlanEntryAlternativesAdapter extends PlanFragmentAdapter {

    public static final int DAY_VIEW = 2;

    public PlanEntryAlternativesAdapter(Activity activity, RecyclerView recyclerView, List<PlanEntry> entries
    ) {
        super(activity, recyclerView, entries, 500);
        setHighlightIntersects(false);
        setHighlightPast(false);
    }

    public class DayViewHolder extends PlanFragmentAdapter.PauseViewHolder {

        public DayViewHolder(View v) {
            super(v);
        }

        @Override
        public void fill(int position) {
            super.fill(position);
            pauseTextView.setText(entries.get(position).getEventName());

        }
    }

    public class EntryViewHolder extends PlanFragmentAdapter.EntryViewHolder {

        public EntryViewHolder(View v) {
            super(v);
        }

        @Override
        public void fill(int position) {
            super.fill(position);
            commentView.setText(entries.get(position).getSemester());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(entries.get(position).getEventType().equals("#dayentry#")) return DAY_VIEW;
        return super.getItemViewType(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case DAY_VIEW:
                return new DayViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_planentry_pause,parent,false));
            case ENTRY_VIEW:
                return new EntryViewHolder( LayoutInflater.from(parent.getContext()).inflate(R.layout.view_planentry,parent,false));
        }
        return super.onCreateViewHolder(parent, viewType);
    }
}
