package de.maryvofin.stundenplan.test.parser;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.maryvofin.stundenplan.database.PlanEntry;

/**
 * Created by mark on 30.09.2015.
 */
@RunWith(AndroidJUnit4.class)
public class PlanEntryTest {

    @Test
    public void testEquals() {
        PlanEntry e1 = new PlanEntry();
        PlanEntry e2 = new PlanEntry();
        e1.setRoom("test");
        e1.setTimeSpan("dgshg");
        e1.setEventGroup("5");
        e1.setEventType("v");
        e1.setLecturer("sf");

        Assert.assertFalse(e1.equals(e2));
        Assert.assertTrue(e1.equals(e1));
        Assert.assertTrue(e2.equals(e2));
    }

}
