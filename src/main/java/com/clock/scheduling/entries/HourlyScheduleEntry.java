package com.clock.scheduling.entries;

import com.clock.scheduling.IScheduleEntry;

import java.util.Calendar;

public class HourlyScheduleEntry extends AbstractScheduleEntry {
    public HourlyScheduleEntry(int minute) {
        this(minute, 0);
    }

    public HourlyScheduleEntry(int minute, int second) {
        super(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        representation = "HourlyEntry (" + minute + "m:" + second + "s)";
    }

    @Override
    public String toString() {
        return representation;
    }

    @Override
    public IScheduleEntry clone() {
        return new HourlyScheduleEntry(calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }
}
