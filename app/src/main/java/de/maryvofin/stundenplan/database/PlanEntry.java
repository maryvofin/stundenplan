package de.maryvofin.stundenplan.database;

import java.io.Serializable;

/**
 * Created by mark on 29.09.2015.
 */
public class PlanEntry implements Serializable, Comparable<PlanEntry>{

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private int weekDay;
    private String room = "";
    private String eventName = "";
    private String eventType = "";
    private String eventGroup = null;
    private String timeSpan = "";
    private String lecturer = "";
    private String semester = "";

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventGroup(String eventGroup) {
        this.eventGroup = eventGroup;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventGroup() {
        return eventGroup;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public String getRoom() {
        return room;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PlanEntry)) return false;
        PlanEntry other = (PlanEntry)o;
        if (!this.getEventType().equals(other.getEventType())) return false;
        if (!this.getEventName().equals(other.getEventName())) return false;
        if (this.getEventGroup() != null && other.getEventGroup() != null) if (!this.getEventGroup().equals(other.getEventGroup())) return false;
        if (!this.getLecturer().equals(other.getLecturer())) return false;
        if (!this.getTimeSpan().equals(other.getTimeSpan())) return false;

        return true;
    }

    @Override
    public int compareTo(PlanEntry another) {
        if (this.getStartHour() < another.getStartHour()) return -1;
        if (this.getStartHour() > another.getStartHour()) return 1;
        if (this.getStartMinute() < another.getStartMinute()) return -1;
        if (this.getStartMinute() > another.getStartMinute()) return 1;
        return 0;
    }

    @Override
    public int hashCode() {
        return (eventGroup != null) ? eventName.hashCode() + eventType.hashCode() +eventGroup.hashCode() +semester.hashCode()+lecturer.hashCode():  eventName.hashCode() + eventType.hashCode()+semester.hashCode()+lecturer.hashCode();
    }

}
