package com.clock.example;

import com.clock.AlarmClock;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * User: Priytam Jee Pandey
 * Date: 14/06/20
 * Time: 12:27 am
 * email: priytam.pandey@cleartrip.com
 */
public class AlarmClockExample {
    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws InterruptedException {
        // Get and instance of clock
        AlarmClock clock = AlarmClock.getInstance("TestClock");

        //register to print 'ring ring' after 2 sec
        clock.register(2000, id -> System.out.println("ring ring"));

        //register to print 'ring ring1' after 3 sec
        clock.register(3000, id -> System.out.println("ring ring1"));

        //start this
        clock.start();

        //register to print 'ring ring2' after 2 sec
        clock.register(2000, id -> System.out.println("ring ring2"));

        //register to print 'ring ring3' after 2 sec
        clock.register(1000, id -> System.out.println("ring ring3"));

        //show all alarms set
        System.out.println(clock.getTaskList().showEntries());

        Thread.sleep( 6000);
        clock.shutdown();
    }
}
