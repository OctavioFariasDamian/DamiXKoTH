package ar.com.octaviofarias.koth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;

/**
 * This class contains the information of a KoTH scheduler
 */
@Data
@AllArgsConstructor
public class KoTHScheduler {
    private DayOfWeek day;
    private int hour, minutes, seconds, duration;
    private boolean infintie;

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
