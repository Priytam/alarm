package com.clock.example;

import com.clock.AlarmClock;

/**
 * User: Priytam Jee Pandey
 * Date: 14/06/20
 * Time: 12:27 am
 * email: priytam.pandey@cleartrip.com
 */
public class AlarmClockExample {
    public static void main(String[] args) throws InterruptedException {
        AlarmClock test = AlarmClock.getInstance("Test");

        test.register(2000, id -> System.out.println("ring ring"));
        test.register(2000, id -> System.out.println("ring ring1"));

        test.start();

        test.register(2000, id -> System.out.println("ring ring2"));
        test.register(2000, id -> System.out.println("ring ring3"));

        System.out.println(test.getTaskList().showEntries());
        System.out.println(test.getTaskList());

        Thread.sleep( 6000);

        System.out.println(test.getTaskList());
        test.shutdown();
    }
}
