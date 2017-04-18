package de.maryvofin.stundenplan.app.database;


import android.util.Base64;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    @Ignore
    private Long serializeID;


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

    public int getEntryReference() {
        return entryReference;
    }

    public void setEntryReference(int entryReference) {
        this.entryReference = entryReference;
    }

    public long getDeadline() {
        return deadline;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getEstimatedDuration() {
        return estimatedDuration;
    }

    public void setEstimatedDuration(long estimatedDuration) {
        this.estimatedDuration = estimatedDuration;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public Long getSerializeID() {
        return serializeID;
    }

    public void setSerializeID(Long serializeID) {
        this.serializeID = serializeID;
    }

    public String serialize() {
        return serialize(this);
    }

    public static String serialize(Task t) {
        t.setSerializeID(t.getId());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(t);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String serializedTask = new String(Base64.encode(bos.toByteArray(),Base64.DEFAULT));
        return serializedTask;
    }

    public static Task deserialize(String task)  {
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(task.getBytes(), Base64.DEFAULT));
        ObjectInputStream ois = null;
        Task deserializedTask = null;
        try {
            ois = new ObjectInputStream(bis);
            deserializedTask = (Task) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        deserializedTask.setId(deserializedTask.getSerializeID());
        return deserializedTask;
    }
}
