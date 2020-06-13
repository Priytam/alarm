package com.clock.scheduling.entries;

import com.clock.scheduling.IScheduleEntry;

import java.util.Calendar;

public class DailyScheduleEntry extends AbstractScheduleEntry {

    public DailyScheduleEntry(int hour, int minute) {
        this(hour, minute, 0);
    }

    public DailyScheduleEntry(int hour, int minute, int second) {
        super(Calendar.DATE, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        representation = "DailyEntry (" + hour + "h:" + minute + "m:" + second + "s)";
    }

    @Override
    public String toString() {
        return representation;
    }

    @Override
    public IScheduleEntry clone() {
        return new DailyScheduleEntry(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
}
