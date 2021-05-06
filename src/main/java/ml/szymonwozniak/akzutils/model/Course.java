package ml.szymonwozniak.akzutils.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collection;

public class Course {
    public enum CourseType {
        WSZYSTKIE, WF, JEZYK, INNE
    }

    public enum Stationary{
        WSZYSTKIE, STACJONARNE, NIESTACJONARNE
    }

    public enum Grade {
        WSZYSTKIE, I, II
    }

    private SimpleStringProperty courseCode;
    private SimpleStringProperty groupCode;
	private SimpleStringProperty name;
	private SimpleListProperty<TimeAndPlace> timesAndPlaces;
	private SimpleStringProperty lecturers;
    private SimpleIntegerProperty freePlaces;
    private SimpleIntegerProperty ZZU;
    private SimpleStringProperty stationary;
    private SimpleStringProperty grade;
	private SimpleStringProperty comments;
	private CourseType courseType;


    public Course(String courseCode, String groupCode, String name, ArrayList<TimeAndPlace> timesAndPlaces, String lecturers, int freePlaces, int ZZU, String stationary, String grade, String comments) {
        this.courseCode = new SimpleStringProperty(courseCode);
        this.groupCode = new SimpleStringProperty(groupCode);
        this.name = new SimpleStringProperty(name);
        this.timesAndPlaces = new SimpleListProperty<>(FXCollections.observableArrayList(timesAndPlaces));
        this.lecturers = new SimpleStringProperty(lecturers);
        this.freePlaces = new SimpleIntegerProperty(freePlaces);
        this.ZZU = new SimpleIntegerProperty(ZZU);
        this.stationary = new SimpleStringProperty(stationary);
        this.grade = new SimpleStringProperty(grade);
        this.comments = new SimpleStringProperty(comments);
        if(courseCode.startsWith("WF")) courseType = CourseType.WF;
        else if (courseCode.startsWith("JZL")) courseType = CourseType.JEZYK;
        else courseType = CourseType.INNE;
    }

    public static Course createCourse(String courseCode, String groupCode, String name, ArrayList<TimeAndPlace> timesAndPlaces,
                                      String lecturers, int freePlaces, int ZZU, String stationary, String grade, String comments){
        return new Course(courseCode, groupCode, name, timesAndPlaces, lecturers, freePlaces, ZZU, stationary, grade, comments);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(courseCode.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(groupCode.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(name.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(timesAndPlaces.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(lecturers.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(freePlaces.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(ZZU.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(stationary.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(grade.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(comments.getValue());
        stringBuilder.append(" ");
        return stringBuilder.toString();
    }

    public String toSearch(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(courseCode.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(groupCode.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(name.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(timesAndPlaces.getValue());
        stringBuilder.append(" ");
        stringBuilder.append(lecturers.getValue());
        return stringBuilder.toString();
    }

    public boolean containsAny(Collection<String> queries){
        for (String query : queries) {
            if(contains(query)) return true;
        }
        return false;
    }


    public boolean containsAll(Collection<String> queries){
        for (String query : queries) {
            if(!contains(query)) return false;
        }
        return true;
    }

    public boolean contains (String query){
        return toSearch().toLowerCase().contains(query);
    }

    public boolean clashes(Course otherCourse){
        for (TimeAndPlace timeAndPlace : timesAndPlaces) {
            if(timeAndPlace == null || timeAndPlace.getDayOfWeekAndTime() == null) return false;
            for (TimeAndPlace otherTimeAndPlace : otherCourse.timesAndPlaces) {
                if(otherTimeAndPlace == null || otherTimeAndPlace.getDayOfWeekAndTime() == null) return false;
                else if(timeAndPlace.clashes(otherTimeAndPlace)) return true;
            }
        }
        return false;
    }

    public boolean fitsTimetable(Timetable timetable){
        for (Course course : timetable.listOfCourses) {
            if(clashes(course)) return false;
        }
        return true;
    }

    public String getCourseCode() {
        return courseCode.get();
    }

    public SimpleStringProperty courseCodeProperty() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode.set(courseCode);
    }

    public String getGroupCode() {
        return groupCode.get();
    }

    public SimpleStringProperty groupCodeProperty() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode.set(groupCode);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public ObservableList<TimeAndPlace> getTimesAndPlaces() {
        return timesAndPlaces.get();
    }

    public SimpleListProperty<TimeAndPlace> timesAndPlacesProperty() {
        return timesAndPlaces;
    }

    public void setTimesAndPlaces(ObservableList<TimeAndPlace> timesAndPlaces) {
        this.timesAndPlaces.set(timesAndPlaces);
    }

    public String getLecturers() {
        return lecturers.get();
    }

    public SimpleStringProperty lecturersProperty() {
        return lecturers;
    }

    public void setLecturers(String lecturers) {
        this.lecturers.set(lecturers);
    }

    public int getFreePlaces() {
        return freePlaces.get();
    }

    public SimpleIntegerProperty freePlacesProperty() {
        return freePlaces;
    }

    public void setFreePlaces(int freePlaces) {
        this.freePlaces.set(freePlaces);
    }

    public int getZZU() {
        return ZZU.get();
    }

    public SimpleIntegerProperty ZZUProperty() {
        return ZZU;
    }

    public void setZZU(int ZZU) {
        this.ZZU.set(ZZU);
    }

    public String getStationary() {
        return stationary.get();
    }

    public String getStationaryString(){
        return stationary.getValue();
    }

    public SimpleStringProperty stationaryProperty() {
        return stationary;
    }

    public void setStationary(String stationary) {
        this.stationary.set(stationary);
    }

    public String getGrade() {
        return grade.get();
    }

    public SimpleStringProperty gradeProperty() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade.set(grade);
    }

    public String getComments() {
        return comments.get();
    }

    public SimpleStringProperty commentsProperty() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments.set(comments);
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public String getTitle(){
        return "Kurs: " + getCourseCode() + " " + getGroupCode();
    }

    public String toPrompt(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Terminy: ");
        stringBuilder.append(getTimesAndPlaces().toString());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Prowadzący: ");
        stringBuilder.append(getLecturers());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Wolne miejsca: ");
        stringBuilder.append(getFreePlaces());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("ZZU: ");
        stringBuilder.append(getZZU());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Stacjonarne: ");
        stringBuilder.append(getStationary());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Stopień: ");
        stringBuilder.append(getGrade());
        stringBuilder.append(System.lineSeparator());
        stringBuilder.append("Uwagi: ");
        stringBuilder.append(getComments());
        stringBuilder.append(System.lineSeparator());
        return stringBuilder.toString();
    }
}
