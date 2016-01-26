package de.maryvofin.stundenplan.test.parser;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Semester;
import de.maryvofin.stundenplan.app.parser.Parser;

/**
 * Created by mark on 30.09.2015.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    List<PlanEntry> entries;


    public PlanEntry generatePlanEntry(int weekday, String eventType, String lecturer, String room, String eventGroup, String eventName, String semester, String timeSpan, int startHour, int startMinute, int endHour, int endMinute) {
        PlanEntry entry = new PlanEntry();
        entry.setEventType(eventType);
        entry.setWeekDay(weekday);
        entry.setLecturer(lecturer);
        entry.setRoom(room);
        entry.setStartHour(startHour);
        entry.setStartMinute(startMinute);
        entry.setEndHour(endHour);
        entry.setEndMinute(endMinute);
        entry.setEventGroup(eventGroup);
        entry.setEventName(eventName);
        entry.setSemester(semester);
        entry.setTimeSpan(timeSpan);
        return entry;
    }

    @Before
    public void setUp() {
        entries = new LinkedList<PlanEntry>();

        entries.add(generatePlanEntry(0, "V", "Peter", "Besenkammer", null, "Foo verstehen", "STFU1", "28.09.2015-18.01.2016 (KW 40-3)", 9, 0, 10, 30));
        entries.add(generatePlanEntry(2, "V", "Nathan", "Besenkammer", null, "IF-Schleifen", "STFU1", "28.09.2015-18.01.2016 (KW 40-3)", 9, 0, 10, 30));
        entries.add(generatePlanEntry(1, "Ü", "Nathan", "Besenkammer", "a", "Foo verstehen", "STFU1", "28.09.2015-18.01.2016 (KW 40-3)", 11, 0, 12, 30));
        entries.add(generatePlanEntry(5, "Ü", "Cylar", "Abstellraum", "b", "Foo verstehen", "STFU1", "28.09.2015-18.01.2016 (KW 40-3)", 15, 0, 17, 30));
        entries.add(generatePlanEntry(3, "P", "Cylar", "Abstellraum", "a", "IF-Schleifen", "STFU2", "28.09.2015-18.01.2016 (KW 40-3)", 10, 0, 13, 30));
        entries.add(generatePlanEntry(6, "P", "Cylar", "Abstellraum", "b", "IF-Schleifen", "STFU2", "28.09.2015-18.01.2016 (KW 40-3)", 10, 0, 13, 30));
        entries.add(generatePlanEntry(2, "Pj", "Peter", "Besenkammer", null, "Kekse Bröseln", "STFU2", "28.09.2015-18.01.2016 (KW 40-3)", 15, 0, 17, 30));
        entries.add(generatePlanEntry(2, "Ü", "Peter", "Besenkammer", null, "Vorlesungen vermeiden", "STFU2", "28.09.2015-18.01.2016 (KW 40-3)", 15, 0, 17, 30));
        entries.add(generatePlanEntry(1, "V", "Peter", "Besenkammer", null, "Vorlesungen vermeiden", "STFU2", "28.09.2015-18.01.2016 (KW 40-3)", 12, 15, 15, 30));
        entries.add(generatePlanEntry(6, "V", "Peter", "Besenkammer", null, "Vorlesungen vermeiden", "STFU2", "28.09.2015-18.01.2016 (KW 40-3)", 16, 0, 18, 30));

    }


    @Test
    public void update() {
        Database db = Database.getInstance();
        List<Semester> list = new LinkedList<>();
        db.update(InstrumentationRegistry.getContext(), list);
    }

    /*@Test
    public void testGetTimeEvents() {
        Parser p = new Parser(InstrumentationRegistry.getContext());
        p.parse();
        Database db = Database.getInstance();
        List<PlanEntry> entries = db.getTimeEvents(InstrumentationRegistry.getContext(), System.currentTimeMillis());

        Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            Assert.assertFalse(entries.isEmpty());
        }


    }*/

    @Test
    public void testGenerateWeekmap() {
        String kw = " KW 40-3";
        boolean[] map = Database.getInstance().generateWeekmap(kw);
        Assert.assertTrue(map[0]);

        kw = "KW 41";
        Assert.assertTrue(Database.getInstance().generateWeekmap(kw)[40]);
    }

    @Test
    public void testCheckTimespan() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,2015);
        c.set(Calendar.MONTH,9);
        c.set(Calendar.DAY_OF_MONTH,9);
        Assert.assertTrue(Database.getInstance().checkTimespan("08.10.2015-10.10.2015", c));
        Assert.assertTrue(Database.getInstance().checkTimespan("09.10.2015-09.10.2015", c));
        Assert.assertTrue(Database.getInstance().checkTimespan("09.10.2015-09.10.2015 (KW 41)", c));
        Assert.assertFalse(Database.getInstance().checkTimespan("23.10.2015-23.10.2015 (KW 43)", c));
    }

    @Test
    public void testRemoveEntriesFromList() {
        PlanEntry entry1 = entries.get(0);
        PlanEntry entry2 = entries.get(2);

        List<PlanEntry> removeList = new LinkedList<>();
        removeList.add(entry1);
        removeList.add(entry2);

        int sizeBefore = entries.size();
        Database.getInstance().removeEntriesFromList(entries, removeList);
        Assert.assertEquals("Entfernen aus Liste", sizeBefore - 2, entries.size());
        Assert.assertFalse("entry1 sollte nicht mehr in der Liste sein", entries.contains(entry1));
        Assert.assertFalse("entry2 sollte nicht mehr in der Liste sein",entries.contains(entry2));

    }

    @Test
    public void testRemoveWrongModules() {
        PlanEntry reference = entries.get(0);
        Database.getInstance().removeWrongModules(entries, reference);

        for(PlanEntry entry: entries) {
            Assert.assertEquals("Es sollten nur noch Einträge mit den gleichen EventNamen sein",reference.getEventName(),entry.getEventName());
        }

    }

    @Test
    public void testRemoveOverlappings() {
        List<PlanEntry> myEntries = new LinkedList<>();
        myEntries.add(entries.get(0));
        myEntries.add(entries.get(2));

        Database.getInstance().removeOverlappings(entries, myEntries);

        Assert.assertEquals(4, entries.size());

    }

    @Test
    public void testIsAlternative() {
        PlanEntry e1 = entries.get(0);
        PlanEntry e2 = entries.get(2);
        PlanEntry e3 = entries.get(3);

        Assert.assertFalse("Sollte keine Alternative sein",Database.getInstance().isAlternative(e1,e2));
        Assert.assertTrue("Sollte eine Alternative sein",Database.getInstance().isAlternative(e2,e3));

    }

    @Test
    public void testRemoveWrongAlternatives1() {
        PlanEntry reference = entries.get(0);

        Database.getInstance().removeWrongAlternatives(entries, reference);

        //if(entries.size() >0)Assert.assertEquals(null,entries.get(0).getEventName()+" - "+entries.get(0).getEventType()+" - "+entries.get(0).getEventGroup());
        Assert.assertEquals("Es sollte keine Alternative vorhanden sein", 0, entries.size());


    }

    @Test
    public void testRemoveWrongAlternatives2() {
        PlanEntry reference = entries.get(3);

        Database.getInstance().removeWrongAlternatives(entries, reference);


        Assert.assertEquals("Es sollte genau eine Alternative gefunden werden", 1, entries.size());


    }

    @Test
    public void testRemoveWrongAlternatives3() {
        PlanEntry reference = entries.get(7);

        Database.getInstance().removeWrongAlternatives(entries,reference);


            Assert.assertEquals("Es sollte keine Alternative vorhanden sein", 0, entries.size());


    }



}
