package ml.szymonwozniak.akzutils.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    static String DEFAULT_ADDRESS = "http://akz.pwr.wroc.pl/katalog_zap.html";

    public enum QueryType {
        Tag, Class, Attribute
    }

    static final HashMap <String, Boolean> logic;
    static {
        logic = new HashMap<>();
        logic.put("TAK", true);
        logic.put("NIE", false);
    }

    String address;
    Document doc;
    Elements elements;

    private Parser(){
        this(DEFAULT_ADDRESS);
    }

    private Parser (String address){
        this.address = address;
    }

    public static Parser getInstance(){
        return getInstance(DEFAULT_ADDRESS);
    }

    public static Parser getInstance(String address){
        return initializeInstance(address);
    }

    public static Parser getInstance(String address, QueryType queryType, String query){
        return initializeInstance(address, queryType, query);
    }

    public static Parser localInstance(String filename, QueryType queryType, String query){
        Parser p = new Parser();
        File input = new File(filename);
        try {
            p.doc = Jsoup.parse(input, "UTF-8");
            p.elements = parse(p.doc, queryType, query);
        }catch (IOException e){
            System.out.println("An error occured while initializing Parser instance.");//TODO: REPLACE WITH A PROPER ERROR MESSAGE
        }
        return p;
    }

    public static Parser localInstance(File file, QueryType queryType, String query){
        Parser p = new Parser();
        try {
            p.doc = Jsoup.parse(file, "UTF-8");
            p.elements = parse(p.doc, queryType, query);
        }catch (IOException e){
            System.out.println("An error occured while initializing Parser instance.");//TODO: REPLACE WITH A PROPER ERROR MESSAGE
        }
        return p;
    }

    private static Parser initializeInstance(String address, QueryType queryType, String query){
        Parser p = address == null ? new Parser() : new Parser(address);
        try {
            p.doc = connectAndFetch(address);
            System.out.println("Downloaded data");
            p.elements = parse(p.doc, queryType, query);
        }catch (IOException e){
            System.out.println("An error occured while initializing Parser instance.");//TODO: REPLACE WITH A PROPER ERROR MESSAGE
        }
        return p;
    }

    private static Parser initializeInstance(String address){
        Parser p = address == null ? new Parser() : new Parser(address);
        try {
            p.doc = connectAndFetch(address);
            p.elements = parse(p.doc, QueryType.Class, "gradeX");
        }catch (IOException e){
            System.out.println("An error occured while initializing Parser instance.");//TODO: REPLACE WITH A PROPER ERROR MESSAGE
        }
        return p;
    }

    private static Document connectAndFetch(String address) throws IOException{
        return Jsoup.connect(address).get();
    }

    public static Elements parse(Document doc, QueryType queryType, String query){
        if(doc == null) return null;

        Elements parsedElements = new Elements();
        switch (queryType){
            case Tag:
                parsedElements = doc.getElementsByTag(query);
                break;
            case Class:
                parsedElements =  doc.getElementsByClass(query);
                break;
            case Attribute:
                parsedElements = doc.getElementsByAttribute(query);
                break;
        }
        return parsedElements;
    }

    public static ArrayList<Course> parseAllCourses(Elements rows){
        ArrayList<Course> courses = new ArrayList<>();
        for (Element row : rows) {
            courses.add(parseCourseInfo(getColumnsFromRow(row.getAllElements().get(0))));
        }
        return courses;
    }

    private static Elements getColumnsFromRow(Element row){
        return row.select("td");
    }

    private static Course parseCourseInfo(Elements columns){
        return new Course(columns.get(0).text().trim(), columns.get(1).text().trim(), columns.get(2).text().trim(),
                parseDateInfo(columns.get(3).text()), columns.get(4).text().trim(), Integer.parseInt(columns.get(5).text()),
                Integer.parseInt(columns.get(6).text()), columns.get(7).text().trim(), columns.get(8).text().trim(),
                columns.get(9).text().trim());
    }

    public static ArrayList<TimeAndPlace> parseDateInfo(String rawData){
        ArrayList<TimeAndPlace> result = new ArrayList<>();
        List<String> parenthesesMatches = new ArrayList<>();
        List<String> hourMatches = new ArrayList<>();
        List<String> placeMatches = new ArrayList<>();

        Matcher parenthesesMatcher = Pattern.compile("\\(.+?\\)").matcher(rawData);
        Matcher hourMatcher = Pattern.compile("\\d\\d:\\d\\d").matcher(rawData);

        while (parenthesesMatcher.find()) {
            parenthesesMatches.add(parenthesesMatcher.group());
        }

        while (hourMatcher.find()) {
            hourMatches.add(hourMatcher.group());
        }

        for (String alreadyMatched : parenthesesMatches) {
            rawData = rawData.replaceAll(alreadyMatched, "");
        }
        for (String alreadyMatched : hourMatches) {
            rawData = rawData.replaceAll(alreadyMatched, "");
        }

        Matcher placeMatcher = Pattern.compile("-?\\w+-?\\w*").matcher(rawData);

        while(placeMatcher.find()){
            placeMatches.add(placeMatcher.group());
        }

        if(parenthesesMatches.size() != 0){
            String place = "";
            String shortDayName;
            String typeOfWeek = null;
            MyTime startTime;
            MyTime endTime;

            for (int i = 0; i < parenthesesMatches.size() / 2; i++) {
                shortDayName = parenthesesMatches.get(2 * i);

                if(shortDayName.length() > 4){
                    String splitIfEvenOrOdd[];
                    splitIfEvenOrOdd = shortDayName.split(" ");
                    shortDayName = splitIfEvenOrOdd[0] + ")";
                    typeOfWeek = "(" + splitIfEvenOrOdd[1];
                }
                String start[] = hourMatches.get(2 * i).split(":");
                if(start[0].charAt(0) == '0') start[0] = start[0].substring(1);
                if(start[1].charAt(0) == '0') start[1] = start[1].substring(1);
                startTime = new MyTime(Integer.parseInt(start[0]), Integer.parseInt(start[1]), 0);

                String end[] = hourMatches.get(2 * i + 1).split(":");
                if(end[0].charAt(0) == '0') end[0] = end[0].substring(1);
                if(end[1].charAt(0) == '0') end[1] = end[1].substring(1);
                endTime = new MyTime(Integer.parseInt(end[0]), Integer.parseInt(end[1]), 0);

                if(placeMatches.size() != 0){
                    place =  placeMatches.get(i) + " " +  parenthesesMatches.get(2 * i + 1);
                }
                if(typeOfWeek == null) result.add(new TimeAndPlace(shortDayName, startTime, endTime, place));
                else result.add(new TimeAndPlace(shortDayName, typeOfWeek, startTime, endTime, place));
            }
        } else {
            result.add(new TimeAndPlace());
        }
        return result;
    }

    public static ArrayList<Course> parseJSOS(Elements rows){
        ArrayList<Course> courses = new ArrayList<>();
        for (Element row : rows) {
            Course course = parseJsosCourseInfo(getColumnsFromRow(row.getAllElements().get(0)));
            if(course != null){
                courses.add(course);
            }
        }
        return courses;
    }

    private static Course parseJsosCourseInfo(Elements columns){

        for (Element column : columns) {
            column.html(column.html().replaceAll("<sup>", ":").replaceAll("</sup>", "").replaceAll("<br>", ":::"));
        }
        //courseCode and name
        String[] firstColumnSplit = columns.get(0).text().split(":::");
        String courseCode = firstColumnSplit[0].trim();
        String name = firstColumnSplit[1].trim();

        //lecturers
        String lecturers = columns.get(1).text().trim();

        //groupCode
        String groupCode = columns.get(2).text().trim();

        //date, time and place
        String[] fourthColumnSplit = columns.get(3).text().split(", ");
        if(fourthColumnSplit[0].equals("Bez terminu")){
            return null;
        }

        //day
        //TODO: ADD SUPPORT FOR MULTIPLE DAY COURSES
        String day = fourthColumnSplit[0].trim();


        //hours
        List<String> hourMatches = new ArrayList<>();
        Matcher hourMatcher = Pattern.compile("\\d+").matcher(fourthColumnSplit[1]);
        while (hourMatcher.find()){
            hourMatches.add(hourMatcher.group());
        }
        assert hourMatches.size() % 4 == 0;

        TimeAndPlace.TypeOfWeek typeOfWeek = null;
        if(fourthColumnSplit[1].contains("TP")) typeOfWeek = TimeAndPlace.TypeOfWeek.TP;
        else if(fourthColumnSplit[1].contains("TN")) typeOfWeek = TimeAndPlace.TypeOfWeek.TN;
        ArrayList<TimeAndPlace> timesAndPlaces = new ArrayList<>();

        String building = fourthColumnSplit[2].trim();
        String roomNumber;
        Matcher roomNumberMatcher = Pattern.compile("(?:s\\. )\\w+").matcher(fourthColumnSplit[3]);
        roomNumberMatcher.find();
        roomNumber = roomNumberMatcher.group();

        timesAndPlaces.add(new TimeAndPlace(day, typeOfWeek,
                new MyTime(Integer.parseInt(hourMatches.get(0)), Integer.parseInt(hourMatches.get(1)), 0),
                new MyTime(Integer.parseInt(hourMatches.get(2)), Integer.parseInt(hourMatches.get(3)), 0),
                building, roomNumber));

        int zzu = Integer.parseInt(columns.get(4).text().trim());

        return Course.createCourse(courseCode, groupCode, name, timesAndPlaces, lecturers, -1, zzu,
                null, null, null);
    }

    public Elements getElements() {
        return elements;
    }
}
