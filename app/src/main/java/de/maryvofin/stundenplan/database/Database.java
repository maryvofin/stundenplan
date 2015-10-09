package de.maryvofin.stundenplan.database;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mark on 30.09.2015.
 */
public class Database {
    public static final String DBFILE = "plan.db";
    public static final String PRFILE = "profiles.db";
    public static final String PATH = "/data/data/de.maryvofin.stundenplan/databases";

    private Pattern timeSpanPattern = Pattern.compile("(\\d\\d?)\\.(\\d\\d?)\\.(\\d\\d\\d\\d?)-(\\d\\d?)\\.(\\d\\d?)\\.(\\d\\d\\d\\d?)(\\s\\((.*)\\))?");
    private Pattern simpleKWPattern = Pattern.compile("^KW\\s?((\\s?,?\\s?\\d\\d?(\\s?-\\s?\\d\\d?\\s?(\\(?\\s?ohne\\s?(\\s?,?\\s?\\d\\d?))*\\)?)?)*)$");
    private Pattern kwSpanPattern = Pattern.compile("(\\d\\d?)-(\\d\\d?)");

    private static Database ourInstance = new Database();

    private List<Semester> semesterList = new ArrayList<>();
    private Profiles profiles = null;

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
    }

    private String getPath(Context context) {
        if(context.getFilesDir() == null) {
            return PATH;
        }
        return context.getFilesDir().getAbsolutePath();
    }

    public synchronized void load(Context context) {

        loadProfiles(context);
        try {
            //FileInputStream fis = new FileInputStream(PATH+DBFILE);
            FileInputStream fis = new FileInputStream(getPath(context)+"/"+DBFILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            semesterList = (List<Semester>)ois.readObject();
        } catch (FileNotFoundException e) {

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void update(Context context, List<Semester> semesters) {
        try {
            semesterList = semesters;
            //FileOutputStream fos = context.openFileOutput(DBFILE, Context.MODE_PRIVATE);
            FileOutputStream fos = new FileOutputStream(getPath(context)+"/"+DBFILE);
            //FileOutputStream fos = new FileOutputStream(PATH+DBFILE);

            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(semesters);
            out.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized List<Semester> getSemesters() {
        return this.semesterList;
    }

    public synchronized List<PlanEntry> getAllEvents(Context context) {
        if(semesterList.isEmpty()) load(context);
        List<PlanEntry> entries = new LinkedList<>();

        for(Semester semester: semesterList) {
            for(PlanEntry entry: semester.getEntries()) {
                entries.add(entry);
                //if(entry.getEventName().contains("Wirtschaft")) System.out.println(entry.getWeekDay()+": "+entry.getEventName()+" - "+entry.getEventType()+" - "+entry.getEventGroup()+" - "+entry.getSemester()+" - "+entry.getTimeSpan()+ " - "+entry.getLecturer());
            }
        }
        return entries;
    }

    public synchronized List<PlanEntry> getEntriesFromName(Context context, String name, String semester) {
        List<PlanEntry> list = new LinkedList<>();
        for(PlanEntry entry: getAllEvents(context)) {
            if(entry.getEventName().equals(name) && entry.getSemester().equals(semester)) {
                list.add(entry);
            }
        }

        return list;
    }

    public synchronized List<PlanEntry> getTimeEvents(Context context, long time) {
        List<PlanEntry> entries = getAllEvents(context);
        List<PlanEntry> filteredEntries = new LinkedList<>();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        for(PlanEntry entry: entries) {
            //if(entry.getEventName().contains("Wirtschaftswi")) System.out.println(entry.getWeekDay()+": "+entry.getEventName()+" - "+entry.getEventType()+" - "+entry.getEventGroup()+" - "+entry.getSemester()+" - "+entry.getTimeSpan()+ " - "+entry.getLecturer());
            if(entry.getWeekDay() == cal.get(Calendar.DAY_OF_WEEK)-2) {
                if(checkTimespan(entry.getTimeSpan(),cal)) {
                    filteredEntries.add(entry);
                    //if(entry.getEventName().contains("Wirtschaftswi"))System.out.println("OK");
                }
                else {
                    //if(entry.getEventName().contains("Wirtschaftswi"))System.out.println("NOK: "+cal.toString());
                }
            }
        }

        return filteredEntries;
    }

    public boolean checkTimespan(String timespan, Calendar cal) {
        Matcher matcher = timeSpanPattern.matcher(timespan);
        if(!matcher.find()) {
            Exception e = new Exception("Unbekanntes Format: "+timespan);
            e.printStackTrace();
        }

        int calCode = cal.get(Calendar.YEAR)*10000+(cal.get(Calendar.MONTH)+1)*100+cal.get(Calendar.DAY_OF_MONTH);

        int startCode = Integer.parseInt(matcher.group(3)+matcher.group(2)+matcher.group(1));
        int endCode = Integer.parseInt(matcher.group(6) + matcher.group(5) + matcher.group(4));
        //System.out.println(startCode+" - "+calCode+" - "+endCode);

        if (startCode > calCode || endCode < calCode) {
            //System.out.println(timespan);
            return false;
        }

        if (matcher.group(8) != null) {
            //System.out.println(matcher.group(8));
            boolean weekmap[] = generateWeekmap(matcher.group(8));

            return weekmap[cal.get(Calendar.WEEK_OF_YEAR)-1];
        }




        return true;
    }

    public boolean[] generateWeekmap(String kwString) {
        kwString = kwString.trim();
        boolean map[] = new boolean[53];
        for(int i=0;i<map.length;i++) map[i] = false;

        Matcher matcher = simpleKWPattern.matcher(kwString);
        if(matcher.find()) {
            String[] parts = matcher.group(1).split(",");
            for(String part: parts) {
                matcher = kwSpanPattern.matcher(part);
                if(matcher.find()) {
                    int w1 = Integer.parseInt(matcher.group(1));
                    int w2 = Integer.parseInt(matcher.group(2));
                    fillSimpleKwMap(w1, w2, map);


                    //Ausnahmen behandeln
                    String[] exceptions = part.replace(matcher.group(0)+"","").replaceAll("\\(|\\)|\\s|ohne","").split(",");
                    for(String exception: exceptions) {
                        if(exception.equals("")) continue;
                        map[Integer.parseInt(exception)-1] = false;
                    }



                }
                else {
                    map[Integer.parseInt(part)-1] = true;
                }
            }

            /*
            int i=0;
            for(boolean b: map) {
                if(i%10==0) System.out.print(" ");
                i++;
                System.out.print(((b)?1:0));
            }
            System.out.println("\t"+kwString);*/
        }
        else { //Immer diese doofen Sonderfälle
            boolean found = false;




            if(!found)System.err.println("Unbekannter KW String: "+kwString);
        }
        return map;
    }

    private void fillSimpleKwMap(int w1, int w2, boolean[] map) {
        for(int i=w1-1;i!=w2;i++) {
            i = i%map.length;
            map[i] = true;
        }
    }

    private void toggleMapList(String sToggle, boolean[] map) {
        String[] sList = sToggle.split(",");
        for(String kw: sList) {
            map[Integer.parseInt(kw)] = true;
        }
    }

    public void loadProfiles(Context context) {
        try {
            //FileInputStream fis = new FileInputStream(PATH+DBFILE);
            FileInputStream fis = new FileInputStream(getPath(context)+"/"+PRFILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            profiles = (Profiles)ois.readObject();
        } catch (FileNotFoundException e) {
            Profile profile = new Profile();
            profile.setName("default");
            profiles = new Profiles(profile);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Profiles getProfiles() {
        return profiles;
    }

    public void updateProfiles(Context context) {
        try {

            //FileOutputStream fos = context.openFileOutput(DBFILE, Context.MODE_PRIVATE);
            FileOutputStream fos = new FileOutputStream(getPath(context)+"/"+PRFILE);
            //FileOutputStream fos = new FileOutputStream(PATH+DBFILE);

            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(profiles);
            out.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
