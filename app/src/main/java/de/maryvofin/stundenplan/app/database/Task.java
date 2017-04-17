package de.maryvofin.stundenplan.app.database;


import com.orm.SugarRecord;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class Task extends SugarRecord implements Comparable<Task>, Serializable{

    public int entryReference;
    public long deadline = System.currentTimeMillis();
    public String text = "";
    public String description = "";
    public long estimatedDuration =durations[0];
    public boolean completed = false;

    final static long minute = 60000;
    final static long hour = 60*minute;
    final static long day = 24 * hour;
    final static long week = 7 * day;
    final static long month = 30*day;
    public final static long durations[] = {5*minute,10*minute, 15*minute, 30*minute,hour,2*hour,5*hour,10*hour,day,2*day,3*day,5*day,week,2*week,month,2*month,3*month};




    public static List<Task> findUncompletedTasks() {
        return Select.from(Task.class).where(Condition.prop("completed").eq("0")).list();
    }

    public static List<Task> findCompletedTasks() {
        return Select.from(Task.class).where(Condition.prop("completed").eq("1")).list();
    }

    public static List<Task> findByEntryReference(int value) {
        return Select.from(Task.class).where(Condition.prop("entryReference").eq(value)).list();
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
