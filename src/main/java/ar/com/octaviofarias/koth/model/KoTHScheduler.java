package ar.com.octaviofarias.koth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;

@Data
@AllArgsConstructor
public class KoTHScheduler {
    private DayOfWeek day;
    private int hour, minutes, seconds, duration;

    public String getFormatedHour() {
        return hour < 10 ? "0"+hour : ""+hour;
    }

    public String getFormatedMinutes() {
        return minutes < 10 ? "0"+minutes : ""+minutes;
    }

    public String getFormatedSeconds() {
        return seconds < 10 ? "0"+seconds : ""+seconds;
    }

}
