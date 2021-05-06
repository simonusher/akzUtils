package ml.szymonwozniak.akzutils.model;


import java.io.File;
import java.util.List;

public class Timetable {
    List<Course> listOfCourses;

    public Timetable(){
        Parser p = Parser.localInstance("timetable.html", Parser.QueryType.Class, "kliknij");
        listOfCourses = Parser.parseJSOS(p.getElements());
        if(listOfCourses.size() == 0) throw new HtmlParsingException();
    }

    public Timetable(File file){
        Parser p = Parser.localInstance(file, Parser.QueryType.Class, "kliknij");
        listOfCourses = Parser.parseJSOS(p.getElements());
        if(listOfCourses.size() == 0) throw new HtmlParsingException();
    }

    public Timetable(List<Course> listOfCourses){
        this.listOfCourses = listOfCourses;
        if(listOfCourses.size() == 0) throw new HtmlParsingException();
    }
}
