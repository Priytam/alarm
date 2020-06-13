package com.clock.scheduling;

import com.clock.scheduling.entries.DailyScheduleEntry;
import com.clock.scheduling.entries.HourlyScheduleEntry;
import com.clock.task.AbstractTestCase;
import org.junit.Test;

/**
 * User: Priytam Jee Pandey
 * Date: 14/06/20
 * Time: 12:25 am
 * email: priytam.pandey@cleartrip.com
 */
public class ScheduleTest extends AbstractTestCase {

    @Test
    public void shouldSchedule() {
        Schedule testSchedule = new Schedule("testSchedule");
        testSchedule.register(() -> System.out.println("I am printer"));
        testSchedule.addEntry(new DailyScheduleEntry(5, 10));
        testSchedule.addEntry(new HourlyScheduleEntry(0, 0));

        System.out.println(testSchedule.getStatus());
    }

}