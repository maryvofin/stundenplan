package de.maryvofin.stundenplan.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.maryvofin.stundenplan.app.database.PlanEntry;

public class TasksFragment extends Fragment {
    View view;
    PlanEntry entry = null;

    TasksAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasks, container, false);

        Bundle args = getArguments();
        if(args != null) {
            entry = (PlanEntry)args.getSerializable("entry");
        }

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new TasksAdapter(entry, recyclerView);
        recyclerView.setAdapter(adapter);




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateData();
    }
}
