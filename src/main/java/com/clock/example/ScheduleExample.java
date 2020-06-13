package com.clock.example;

import com.clock.scheduling.Schedule;
import com.clock.scheduling.entries.DailyScheduleEntry;
import com.clock.scheduling.entries.HourlyScheduleEntry;
import com.clock.scheduling.entries.WeeklyScheduleEntry;

/**
 * User: Priytam Jee Pandey
 * Date: 14/06/20
 * Time: 12:53 am
 * email: priytam.pandey@cleartrip.com
 */
public class ScheduleExample {
    public static void main(String[] args) {
        Schedule testSchedule = new Schedule("testSchedule");
        testSchedule.register(() -> System.out.println("Hello world"));

        //will print hello world every day 5:10
        testSchedule.addEntry(new DailyScheduleEntry(5, 10));

        //will print hello world every hour 1' o clock , 2' o clock, 3' clock
        testSchedule.addEntry(new HourlyScheduleEntry(0, 0));

        //will print hello world every monday at 5:10
        testSchedule.addEntry(new WeeklyScheduleEntry(WeeklyScheduleEntry.MONDAY, 5, 10));
        System.out.println(testSchedule.getStatus());
    }
}
