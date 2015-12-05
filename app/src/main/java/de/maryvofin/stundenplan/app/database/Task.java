package de.maryvofin.stundenplan.app.database;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import info.quantumflux.model.QuantumFluxRecord;
import info.quantumflux.model.query.Select;

public class Task extends QuantumFluxRecord<Task> implements Comparable<Task>{

    public int entryReference;
    public long deadline;
    public String text;
    public String description;
    public long estimatedDuration;
    public boolean completed = false;

    public static List<Task> findUncompletedTasks() {
        return Select.from(Task.class).whereEquals("completed",false).queryAsList();
    }

    public static List<Task> findCompletedTasks() {
        return Select.from(Task.class).whereEquals("completed",true).queryAsList();
    }

    public static List<Task> findByEntryReference(int value) {
        return Select.from(Task.class).whereEquals("entryReference",value).queryAsList();
    }

    public static List<Task> findCriticalTasks() {
        List<Task> list = findUncompletedTasks();
        Iterator<Task> i = list.iterator();
        while(i.hasNext()) {
            Task t = i.next();
            if(!t.isCritical()) i.remove();
        }

        return list;
    }

    public boolean isCritical() {
        return Task.isCritical(this);
    }

    public static boolean isCritical(Task t) {
        if(!t.completed && t.deadline < System.currentTimeMillis()+t.estimatedDuration) return true;
        return false;
    }

    public static List<Task> filterWithPlanEntry(List<Task> list, PlanEntry entry) {
        List<Task> newList = new LinkedList<>();

        for(Task t:list) {
            if(t.entryReference == entry.hashCode()) newList.add(t);
        }

        return newList;
    }

    @Override
    public int compareTo(Task another) {
        return (int)((deadline-estimatedDuration) - (another.deadline-another.estimatedDuration));
    }
}
