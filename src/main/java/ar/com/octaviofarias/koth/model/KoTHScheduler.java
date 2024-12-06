package ar.com.octaviofarias.koth.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;

@Data
@AllArgsConstructor
public class KoTHScheduler {
    private DayOfWeek day;
    private int hour, minutes, seconds, duration;
}
