package de.maryvofin.stundenplan.app.modules.tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.maryvofin.stundenplan.app.MainActivity;
import de.maryvofin.stundenplan.app.R;
import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Task;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    PlanEntry referenceEntry;
    List<Task> taskList;
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
    RecyclerView recyclerView;


    public TasksAdapter(PlanEntry reference, RecyclerView recyclerView) {
        referenceEntry = reference;
        this.recyclerView = recyclerView;
        updateData();

    }

    void updateData() {
        taskList = (referenceEntry != null) ? Task.filterWithPlanEntry(Task.findUncompletedTasks(),referenceEntry): Task.findUncompletedTasks();
        Collections.sort(taskList);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_tasklist_element,parent,false);
        v.setOnClickListener(this);
        v.setOnLongClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.secondaryTextView.setText(holder.view.getContext().getResources().getString(R.string.text_deadline)+": "+dateFormat.format(new Date(task.deadline)));
        String text = (task.text.equals("")) ? task.description : task.description+" - "+task.text;
        holder.primaryTextView.setText(text);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    @Override
    public void onClick(View v) {
        int i = recyclerView.getChildAdapterPosition(v);
        Task task = taskList.get(i);
        Bundle extras = new Bundle();
        extras.putSerializable("task",task);
        Intent intent = new Intent(v.getContext(), AddTaskActivity.class);
        intent.putExtras(extras);
        v.getContext().startActivity(intent);
    }

    @Override
    public boolean onLongClick(final View v) {
        AlertDialog.Builder db = new AlertDialog.Builder(v.getContext());
        db.setMessage(v.getContext().getResources().getString(R.string.text_delete_task_question));
        db.setPositiveButton(v.getContext().getResources().getString(R.string.text_delete), new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        int i = recyclerView.getChildAdapterPosition(v);
                        Task task = taskList.get(i);
                        task.delete();
                        updateData();
                        ((MainActivity) v.getContext()).updateTaskBadge();
                    }
                });
        db.setNegativeButton(v.getContext().getResources().getString(R.string.text_abort), null);
        db.show();



        return true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        TextView primaryTextView;
        TextView secondaryTextView;

        public ViewHolder(View v) {
            super(v);
            this.view = v;
            primaryTextView = (TextView)v.findViewById(R.id.primaryText);
            secondaryTextView = (TextView)v.findViewById(R.id.secondaryText);

        }
    }
}
