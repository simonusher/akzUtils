package ml.szymonwozniak.akzutils.model;

import java.sql.Time;
import java.util.Date;

public class MyTime extends Time{
    public MyTime(int hour, int minute, int second) {
        super(hour, minute, second);
    }

    @Override
    public String toString () {
        int hour = super.getHours();
        int minute = super.getMinutes();
        int second = super.getSeconds();
        String hourString;
        String minuteString;
        String secondString;

        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = Integer.toString(hour);
        }
        if (minute < 10) {
            minuteString = "0" + minute;
        } else {
            minuteString = Integer.toString(minute);
        }
        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = Integer.toString(second);
        }
        return (hourString + ":" + minuteString);
    }

    @Override
    public int compareTo(Date anotherDate) {
        return super.compareTo(anotherDate);
    }
}
