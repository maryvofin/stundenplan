package de.maryvofin.stundenplan.test.parser;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;

import de.maryvofin.stundenplan.app.modules.plan.MainFragment;

@RunWith(AndroidJUnit4.class)
public class PlanModuleTest {

    private int invokeCalculatePage(long time) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method pageTestMethod = MainFragment.class.getDeclaredMethod("calculatePage", long.class);
        pageTestMethod.setAccessible(true);
        return (int)pageTestMethod.invoke(null,time);
    }

    private long invokeCalculateTimeFromPage(int page) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method pageTestMethod = MainFragment.class.getDeclaredMethod("calculateTimeFromPage", int.class);

        pageTestMethod.setAccessible(true);
        return (long)pageTestMethod.invoke(null,page);
    }

    private static void setCalendarToDayStart(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    @Test
    public void calculateTimeFromPageTest_today() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Calendar c = Calendar.getInstance();
        setCalendarToDayStart(c);
        Assert.assertEquals(c.getTimeInMillis(), invokeCalculateTimeFromPage(500));
    }

    @Test
    public void calculateTimeFromPageTest_tomorrow() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Calendar c = Calendar.getInstance();
        setCalendarToDayStart(c);
        c.add(Calendar.DAY_OF_YEAR, 1);
        Assert.assertEquals(c.getTimeInMillis(), invokeCalculateTimeFromPage(501));
    }

    @Test
    public void calculateTimeFromPageTest_yesterday() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Calendar c = Calendar.getInstance();
        setCalendarToDayStart(c);
        c.add(Calendar.DAY_OF_YEAR, -1);
        Assert.assertEquals(c.getTimeInMillis(), invokeCalculateTimeFromPage(499));
    }

    @Test
    public void calculatePageTest_today() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Calendar c = Calendar.getInstance();
        setCalendarToDayStart(c);
        Assert.assertEquals(500,invokeCalculatePage(c.getTimeInMillis()));
    }

    @Test
    public void calculatePageTest_tomorrow() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Calendar c = Calendar.getInstance();
        setCalendarToDayStart(c);
        c.add(Calendar.DAY_OF_YEAR, 1);
        Assert.assertEquals(501,invokeCalculatePage(c.getTimeInMillis()));
    }

    @Test
    public void calculatePageTest_yesterday() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Calendar c = Calendar.getInstance();
        setCalendarToDayStart(c);
        c.add(Calendar.DAY_OF_YEAR, -1);
        Assert.assertEquals(499,invokeCalculatePage(c.getTimeInMillis()));
    }


}
