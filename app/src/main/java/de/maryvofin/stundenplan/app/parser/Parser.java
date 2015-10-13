package de.maryvofin.stundenplan.app.parser;

import android.content.Context;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.maryvofin.stundenplan.app.database.Database;
import de.maryvofin.stundenplan.app.database.PlanEntry;
import de.maryvofin.stundenplan.app.database.Semester;

/**
 * Created by mark on 28.09.2015.
 */
public class Parser {

    private Pattern eventPatternWithGroup = Pattern.compile("^(.*)(Gr(\\.|\\s)([^\\(]*)).*?\\s?\\((.{1,4})\\)");
    private Pattern eventPattern = Pattern.compile("^(.*)\\((.*)\\)");

    private final String homePageUrl = "https://eva2.inf.h-brs.de/stundenplan/";
    private String weeks = "";
    private volatile boolean parsing = false;
    private boolean error = false;
    private Context context;
    private List<Semester> semesters = new LinkedList<>();
    private Document doc;
    private static final String SELECT_SEMESTER = "identifier_semester";
    private static final String SELECT_WEEKS = "input_weeks";
    private Connection con;

    Pattern hourMinutePattern = Pattern.compile("(\\d\\d?):(\\d\\d?)");

    public Parser(Context context) {
        this.context = context;
    }

    public String getWeeks() {
        return weeks;
    }

    public boolean hasError() {
        return error;
    }

    synchronized public void parse()  {

        try {
            con = Jsoup.connect(homePageUrl);
            doc = con.get();
            generateSemesterList();

            //Alle bekannten Semester parsen
            for(Semester semester : semesters) {
                generateSemesterData(semester);
            }

            //Datenbank aktualisieren
            Database.getInstance().update(context, semesters);

            error = false;
        } catch (IOException e) {
            error = true;
        } catch (SQLException e) {
            error = true;
        }

    }

    public boolean isParsing() {
        return parsing;
    }

    public List<Semester> getSemesters() {
        return semesters;
    }

    protected void generateSemesterList() throws SQLException {
        List<Semester> semesterList = new ArrayList<>();

        Element e = doc.getElementById(SELECT_WEEKS);
        weeks = e.getElementsByTag("option").first().attr("value").replaceAll(";", "%3B");


        e = doc.getElementById(SELECT_SEMESTER);
        for (Element option: e.getElementsByTag("option")) {
            String value = option.attr("value").replace("#","%23");
            if(value.equals("")) continue;
            Semester s = new Semester();

            String url = "https://eva2.inf.h-brs.de/stundenplan/anzeigen/?weeks={WEEKS}&days=1-7&mode=table&identifier_semester={SEMESTER}&show_semester=&identifier_dozent=&identifier_raum=&term=a9aff3b9d154e7ed04f62222c86ddb4e";
            url = url.replace("{WEEKS}",weeks).replace("{SEMESTER}",value);
            s.setName(option.html());
            s.setUrl(url);
            semesterList.add(s);
        }
        semesters = semesterList;


    }

    protected void generateSemesterData(Semester semester) throws IOException {
        con = Jsoup.connect(semester.getUrl());
        doc = con.get();



        Element table = doc.getElementsByTag("table").first();
        Elements rows = table.getElementsByTag("tr");
        boolean first = true;
        String weekday = "Mo";
        for(Element row: rows) {
            if(first) {
                first = false;
                continue;
            }
            PlanEntry entry = new PlanEntry();

            try {
                weekday = row.getElementsByClass("liste-wochentag").first().html();
            }
            catch(Exception e) {

            }


            if(weekday.equals("Mo")) entry.setWeekDay(0);
            if(weekday.equals("Di")) entry.setWeekDay(1);
            if(weekday.equals("Mi")) entry.setWeekDay(2);
            if(weekday.equals("Do")) entry.setWeekDay(3);
            if(weekday.equals("Fr")) entry.setWeekDay(4);
            if(weekday.equals("Sa")) entry.setWeekDay(5);
            if(weekday.equals("So")) entry.setWeekDay(6);

            Matcher matcher = hourMinutePattern.matcher(row.getElementsByClass("liste-startzeit").first().html());
            matcher.find();
            entry.setStartHour(Integer.parseInt(matcher.group(1)));
            entry.setStartMinute(Integer.parseInt(matcher.group(2)));

            matcher = hourMinutePattern.matcher(row.getElementsByClass("liste-endzeit").first().html());
            matcher.find();
            entry.setEndHour(Integer.parseInt(matcher.group(1)));
            entry.setEndMinute(Integer.parseInt(matcher.group(2)));

            String eventString = row.getElementsByClass("liste-veranstaltung").first().html();
            matcher = eventPatternWithGroup.matcher(eventString);
            if(matcher.find()) {
                entry.setEventName(matcher.group(1).trim());
                entry.setEventType(matcher.group(5).trim());
                entry.setEventGroup(matcher.group(4).trim());
            }
            else {
                matcher = eventPattern.matcher(eventString);
                matcher.find();
                entry.setEventName(matcher.group(1).trim());
                entry.setEventType(matcher.group(2).trim());
                entry.setEventGroup(null);
            }

            entry.setTimeSpan(row.getElementsByClass("liste-beginn").first().html());
            entry.setLecturer(row.getElementsByClass("liste-wer").first().html());
            entry.setRoom(row.getElementsByClass("liste-raum").first().html());
            entry.setSemester(semester.getName());



            semester.getEntries().add(entry);
        }



    }




}
