package ml.szymonwozniak.akzutils.model;

import java.sql.Time;
import java.util.HashMap;

public class TimeAndPlace {
    enum DayOfWeek{
        Poniedziałek, Wtorek, Środa, Czwartek, Piątek, Sobota, Niedziela
    }
    enum TypeOfWeek {
        TP, TN
    }

    public static final HashMap<String, DayOfWeek> dayOfWeekshortNames;
    static {
        dayOfWeekshortNames = new HashMap<>();
        dayOfWeekshortNames.put("(pn)", DayOfWeek.Poniedziałek);
        dayOfWeekshortNames.put("(wt)", DayOfWeek.Wtorek);
        dayOfWeekshortNames.put("(sr)", DayOfWeek.Środa);
        dayOfWeekshortNames.put("(cz)", DayOfWeek.Czwartek);
        dayOfWeekshortNames.put("(pt)", DayOfWeek.Piątek);
        dayOfWeekshortNames.put("(so)", DayOfWeek.Sobota);
        dayOfWeekshortNames.put("(nie)", DayOfWeek.Niedziela);
    }

    private DayOfWeekAndTime dayOfWeekAndTime;
    private String place;

    public TimeAndPlace(){

    }

    public TimeAndPlace(String shortDayName, Time start, Time end, String place){
        this.dayOfWeekAndTime = new DayOfWeekAndTime(dayOfWeekshortNames.get(shortDayName), start, end);
        this.place = place;
    }

    public TimeAndPlace(String shortDayName, String typeOfWeek, Time start, Time end, String place){
        this.dayOfWeekAndTime = new DayOfWeekAndTime(dayOfWeekshortNames.get(shortDayName), typeOfWeek, start, end);
        this.place = place;
    }

    public TimeAndPlace (String fullDayName, TypeOfWeek typeOfWeek, Time start, Time end, String building, String room){
        this.dayOfWeekAndTime = new DayOfWeekAndTime(DayOfWeek.valueOf(fullDayName), typeOfWeek, start, end);
        this.place = room + " (" + building + ")";
    }

    public DayOfWeekAndTime getDayOfWeekAndTime() {
        return dayOfWeekAndTime;
    }

    public String getPlace() {
        return place;
    }

    public boolean clashes(TimeAndPlace otherTimeAndPlace){
        return this.dayOfWeekAndTime.clashes(otherTimeAndPlace.dayOfWeekAndTime);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(dayOfWeekAndTime != null) sb.append(dayOfWeekAndTime);
        if(place != null) sb.append(place);
        return sb.toString().trim();
    }

    public class DayOfWeekAndTime {
        private DayOfWeek dayOfWeek;
        private TypeOfWeek typeOfWeek;
        private Time start;
        private Time end;

        DayOfWeekAndTime(DayOfWeek dayOfWeek, Time start, Time end){
            this.dayOfWeek = dayOfWeek;
            typeOfWeek = null;
            this.start = start;
            this.end = end;
        }

        DayOfWeekAndTime(DayOfWeek dayOfWeek, String typeOfWeek, Time start, Time end){
            this.dayOfWeek = dayOfWeek;
            this.typeOfWeek = TypeOfWeek.valueOf(typeOfWeek.replaceAll("\\(|\\)", ""));
            this.start = start;
            this.end = end;
        }

        DayOfWeekAndTime(DayOfWeek dayOfWeek, TypeOfWeek typeOfWeek, Time start, Time end){
            this.dayOfWeek = dayOfWeek;
            this.typeOfWeek = typeOfWeek;
            this.start = start;
            this.end = end;
        }

        public DayOfWeek getDayOfWeek() {
            return dayOfWeek;
        }

        public Time getStart() {
            return start;
        }

        public Time getEnd() {
            return end;
        }

        public boolean clashes(DayOfWeekAndTime otherDayOfWeekAndTime){
            //checking days
            if(this.dayOfWeek == otherDayOfWeekAndTime.dayOfWeek){
                if((this.start.compareTo(otherDayOfWeekAndTime.start) >= 0
                        && this.start.compareTo(otherDayOfWeekAndTime.end) <= 0)||
                        (this.end.compareTo(otherDayOfWeekAndTime.start) >= 0
                                && this.end.compareTo(otherDayOfWeekAndTime.end) <= 0)){
                    return true;
                }
            }
            return false;
        }

        @Override
        public String toString() {
            return this.dayOfWeek + " " + (this.typeOfWeek != null ? typeOfWeek + " " : "") + this.start + " " + this.end;
        }
    }

}
