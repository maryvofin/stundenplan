package de.maryvofin.stundenplan.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Task;
import de.maryvofin.stundenplan.app.utils.RecyclerScrollListener;

public class TasksFragment extends Fragment {
    View view;
    PlanEntry entry = null;
    int fabMargin;

    TasksAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tasks, container, false);

        fabMargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);

        final FloatingActionButton fab = (FloatingActionButton)view.findViewById(R.id.fab);
        fab.setImageDrawable(
                new IconicsDrawable(
                        this.getContext())
                        .icon(GoogleMaterial.Icon.gmd_add)
                        .color(Color.WHITE)
                        .sizeDp(40));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabClicked();
            }
        });

        Bundle args = getArguments();
        if(args != null) {
            entry = (PlanEntry)args.getSerializable("entry");
        }

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        adapter = new TasksAdapter(entry, recyclerView);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerScrollListener() {
            @Override
            public void show() {
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                fab.animate().translationY(fab.getHeight()+fabMargin).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });

        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.simple_grow);
        fab.startAnimation(animation);

        return view;
    }

    void fabClicked() {
        Intent intent = new Intent(getContext(), AddTaskActivity.class);

        if (entry != null) {
            Task task = new Task();
            task.estimatedDuration = Task.durations[0];
            task.deadline = System.currentTimeMillis();
            task.entryReference = entry.hashCode();
            Bundle extras = new Bundle();
            extras.putSerializable("task",task);
            intent.putExtras(extras);
        }

        getActivity().startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.updateData();
    }
}
