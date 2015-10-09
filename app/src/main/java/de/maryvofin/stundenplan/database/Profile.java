package de.maryvofin.stundenplan.database;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mark on 30.09.2015.
 */
public class Profile implements Serializable {

    private String name;

    private HashMap<Integer,PlanEntry> filter = new HashMap<>();

    public String getName() {
        return name;
    }

    public HashMap<Integer, PlanEntry> getFilter() {
        return filter;
    }

    public List<PlanEntry> filter(List<PlanEntry> unfiltered) {
        LinkedList<PlanEntry> filtered = new LinkedList<>();

        for(PlanEntry entry: unfiltered) {
            if(filter.get(entry.hashCode()) != null) {
                //if(entry.getEventName().contains("Wirtschaftswi")) System.out.println("OK");
                filtered.add(entry);
            }
        }

        return filtered;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilter(HashMap<Integer, PlanEntry> filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return name;
    }
}
