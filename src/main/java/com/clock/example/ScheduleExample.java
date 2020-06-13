package com.clock.example;

import com.clock.scheduling.Schedule;
import com.clock.scheduling.entries.DailyScheduleEntry;
import com.clock.scheduling.entries.HourlyScheduleEntry;
import com.clock.scheduling.entries.WeeklyScheduleEntry;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * User: Priytam Jee Pandey
 * Date: 14/06/20
 * Time: 12:53 am
 * email: priytam.pandey@cleartrip.com
 */
public class ScheduleExample {
    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }

    public static void main(String[] args) {
        //create a schedule
        Schedule helloWorldSchedule = new Schedule("HelloWorldSchedule");

        //register a task to print 'Hello world'
        helloWorldSchedule.register(() -> System.out.println("Hello world"));

        //will print 'Hello world' every day 5:10
        helloWorldSchedule.addEntry(new DailyScheduleEntry(5, 10));

        //will print 'Hello world' every hour 1' o clock , 2' o clock, 3' clock
        helloWorldSchedule.addEntry(new HourlyScheduleEntry(0, 0));

        //will print 'Hello world' every monday at 5:10
        helloWorldSchedule.addEntry(new WeeklyScheduleEntry(WeeklyScheduleEntry.MONDAY, 5, 10));
        System.out.println(helloWorldSchedule.getStatus());

        //register another task to print hello Priytam and will run with all entries added above
        helloWorldSchedule.register(() -> System.out.println("Hello Priytam"));

        //shutdown
        helloWorldSchedule.shutDown();
    }
}
