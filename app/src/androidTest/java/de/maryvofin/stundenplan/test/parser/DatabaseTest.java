package de.maryvofin.stundenplan.test.parser;

import android.provider.ContactsContract;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import de.maryvofin.stundenplan.database.Database;
import de.maryvofin.stundenplan.database.PlanEntry;
import de.maryvofin.stundenplan.database.Semester;
import de.maryvofin.stundenplan.parser.Parser;

/**
 * Created by mark on 30.09.2015.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    @Test
    public void update() {
        Database db = Database.getInstance();
        List<Semester> list = new LinkedList<>();
        db.update(InstrumentationRegistry.getContext(),list);
    }

    @Test
    public void testGetTimeEvents() {
        Parser p = new Parser(InstrumentationRegistry.getContext());
        p.parse();
        Database db = Database.getInstance();
        List<PlanEntry> entries = db.getTimeEvents(InstrumentationRegistry.getContext(),System.currentTimeMillis());

        Assert.assertFalse(entries.isEmpty());
    }

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

}
