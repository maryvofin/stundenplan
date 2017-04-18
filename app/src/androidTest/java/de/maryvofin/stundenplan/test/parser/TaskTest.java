package de.maryvofin.stundenplan.test.parser;

import android.util.Base64;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Task;

public class TaskTest {

    PlanEntry e1;
    PlanEntry e2;

    Task t1;
    Task t2;
    Task t3;
    Task t4;

    @Before
    public void setUp() {

        Task.deleteAll(Task.class);

        long twoMinutes = 2000*60;
        long fiveMinutes = 5000*60;

        e1 = new PlanEntry();
        e1.setEventName("Testeintrag 1");

        e2 = new PlanEntry();
        e2.setEventName("Testeintrag 2");

        t1 = new Task();
        t1.estimatedDuration = twoMinutes;
        t1.entryReference = e1.hashCode();
        t1.deadline = System.currentTimeMillis()+fiveMinutes;
        t1.text = "Task 1";

        t2 = new Task();
        t2.estimatedDuration = fiveMinutes;
        t2.entryReference = e1.hashCode();
        t2.deadline = System.currentTimeMillis()+twoMinutes;
        t2.text = "Task 2";

        t3 = new Task();
        t3.estimatedDuration = fiveMinutes;
        t3.entryReference = e2.hashCode();
        t3.deadline = System.currentTimeMillis()-twoMinutes;
        t3.text = "Task 3";

        t4 = new Task();
        t4.estimatedDuration = fiveMinutes;
        t4.entryReference = e2.hashCode();
        t4.deadline = System.currentTimeMillis()-twoMinutes;
        t4.completed = true;
        t4.text = "Task 4";

        t1.save();
        t2.save();
        t3.save();
        t4.save();
    }

    @Test
    public void updateTaskTest() {
        int initialCount = (int) Task.count(Task.class);
        t1.setCompleted(true);
        t1.save();
        int endCount = (int) Task.count(Task.class);
        Assert.assertEquals("Count shouldn't change", initialCount, endCount);

        Assert.assertEquals("Number of Completed Tasks in Database", 2, Task.findCompletedTasks().size());

    }

    @Test
    public void taskSurviveSerializationTest() throws IOException, ClassNotFoundException {

        String serializedTask = t1.serialize();

        Task deserializedTask = Task.deserialize(serializedTask);

        Assert.assertNotNull("t1 should not have id null", t1.getId());
        Assert.assertNotNull("deserialized t1 should not have id null", deserializedTask.getId());
        Assert.assertEquals(t1.getId(), deserializedTask.getId());

    }

    @Test()
    public void testFindTasks() {
        Assert.assertEquals(4,Task.count(Task.class));
    }

    @Test
    public void testFindUncompletedTasks() {
        Assert.assertEquals(3,Task.findUncompletedTasks().size());
    }

    @Test
    public void testFindCompletedTasks() {
        Assert.assertEquals(1,Task.findCompletedTasks().size());
    }

    @Test
    public void testFindCriticalTasks() {
        Assert.assertEquals(2,Task.findCriticalTasks().size());
    }

    @Test
    public void testIsCritical() {
        Assert.assertEquals(false,t1.isCritical());
        Assert.assertEquals(true,t2.isCritical());
        Assert.assertEquals(true,t3.isCritical());
        Assert.assertEquals(false,t4.isCritical());
    }

    @Test
    public void testFilterWithPlanEntry() {
        Assert.assertEquals(2,Task.filterWithPlanEntry(Task.findUncompletedTasks(),e1).size());
    }

}
