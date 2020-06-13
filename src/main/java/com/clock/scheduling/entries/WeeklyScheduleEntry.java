/**
 *
 */
package com.clock.scheduling.entries;

import com.clock.scheduling.IScheduleEntry;
import org.apache.log4j.Logger;

import java.util.Calendar;

public class WeeklyScheduleEntry extends AbstractScheduleEntry {
    public static final String SUNDAY = "Sun";
    public static final String MONDAY = "Mon";
    public static final String TUESDAY = "Tue";
    public static final String WEDNESDAY = "Wed";
    public static final String THURSDAY = "Thu";
    public static final String FRIDAY = "Fri";
    public static final String SATURDAY = "Sat";

    transient private static Logger log = Logger.getLogger(WeeklyScheduleEntry.class);

    public WeeklyScheduleEntry(String dayOfWeek, int hour, int minute) {
        this(parseDayOfWeek(dayOfWeek), hour, minute, 0);
    }

    public WeeklyScheduleEntry(String dayOfWeek, int hour, int minute, int second) {
        this(parseDayOfWeek(dayOfWeek), hour, minute, second);
    }

    private WeeklyScheduleEntry(int dayOfWeek, int hour, int minute, int second) {
        super(Calendar.DATE, 7);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        representation = "WeeklyEntry (" + dayOfWeek + "d:" + hour + "h:" + minute + "m:" + second + "s)";
    }

    /**
     * Parses the string representation of the week day and transforms it into one acceptable by Calendar
     *
     * @param dayOfWeek String representation of the week day
     * @return Calendar's value representing same day of week
     */
    private static int parseDayOfWeek(String dayOfWeek) {
        if (null == dayOfWeek) {
            log.error("parseDayOfWeek() called with null parameter");
            return Calendar.SUNDAY;
        }

        if (dayOfWeek.equalsIgnoreCase(SUNDAY))
            return Calendar.SUNDAY;
        if (dayOfWeek.equalsIgnoreCase(MONDAY))
            return Calendar.MONDAY;
        if (dayOfWeek.equalsIgnoreCase(TUESDAY))
            return Calendar.TUESDAY;
        if (dayOfWeek.equalsIgnoreCase(WEDNESDAY))
            return Calendar.WEDNESDAY;
        if (dayOfWeek.equalsIgnoreCase(THURSDAY))
            return Calendar.THURSDAY;
        if (dayOfWeek.equalsIgnoreCase(FRIDAY))
            return Calendar.FRIDAY;
        if (dayOfWeek.equalsIgnoreCase(SATURDAY))
            return Calendar.SATURDAY;

        log.warn("Couldn't parse dayOfWeek=" + dayOfWeek + "parameter");

        return Calendar.SUNDAY;
    }

    @Override
    public String toString() {
        return representation;
    }

    @Override
    public IScheduleEntry clone() {
        return new WeeklyScheduleEntry(calendar.get(Calendar.DAY_OF_WEEK), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
}
