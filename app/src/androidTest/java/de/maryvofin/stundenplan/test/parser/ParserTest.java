package de.maryvofin.stundenplan.test.parser;


import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.sql.SQLException;

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
    public void testReceiveStartpage() throws IOException {
        Assert.assertTrue(parser.getDoc() == null);
        parser.receiveStartpage();
        Assert.assertTrue(parser.getDoc() != null);
    }

    @Test
    public void testGenerateSemesterList() throws IOException, SQLException {
        Assert.assertTrue(parser.getSemesters().isEmpty());
        parser.generateSemesterList();
        Assert.assertTrue(parser.getSemesters().size() > 0);
        Assert.assertTrue(parser.getSemesters().get(0).getUrl().length() > 10);
    }

    @Test
    public void testGenerateSemesterData() throws IOException, SQLException {
        parser.generateSemesterList();
        int ecount = 0;
        int mcount = 0;
        for(Semester s: parser.getSemesters()) {
            parser.generateSemesterData(s);
            ecount += s.getEntries().size();
            mcount += s.getModules().size();
        }
        Assert.assertTrue("Keine Module gefunden",mcount > 0);
        Assert.assertTrue("Keine Veranstaltungen gefunden",ecount > 0);
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
