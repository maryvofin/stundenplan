package de.maryvofin.stundenplan.database;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mark on 28.09.2015.
 */
public class Semester implements Serializable, Comparable<Semester>{

    private String name;
    private String url;
    private List<PlanEntry> entries = new LinkedList();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addEntry(PlanEntry pe) {
        entries.add(pe);
    }

    public List<PlanEntry> getEntries() {
        return entries;
    }

    public Collection<String> getModules() {
        HashMap<String,String> list = new HashMap<>();

        for(PlanEntry entry: entries) {
            list.put(entry.getEventName(),entry.getEventName());
        }

        return list.values();
    }

    @Override
    public int compareTo(Semester another) {
        return this.name.compareTo(another.getName());
    }
}
