package de.maryvofin.stundenplan.test.parser;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.maryvofin.stundenplan.app.database.Semester;
import de.maryvofin.stundenplan.app.parser.Parser;

@RunWith(AndroidJUnit4.class)
public class ParserTest extends InstrumentationTestCase {

    Parser parser;

    @Before
    public void setUp() {
        parser = new Parser(InstrumentationRegistry.getContext());
    }

    @Test
    public void testhomePageWeekParsing() {
        parser.parse();
        while(parser.isParsing());
        Assert.assertFalse("Parsed Weeks: "+parser.getWeeks(),parser.getWeeks() == "");
    }


    @Test
    public void parserTest_Complete() {
        parser.parse();
        while(parser.isParsing());

        Assert.assertFalse(parser.getSemesters().isEmpty());
        int count = 0;
        for (Semester s : parser.getSemesters()) {
            count += s.getEntries().size();
        }
        Assert.assertTrue(count > 0);

    }




}
