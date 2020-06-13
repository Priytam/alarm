/**
 * Created on Jan 27, 2010
 */
package com.clock;

import com.clock.task.RecurringTask;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

class AlarmClockHeartbeatTask extends RecurringTask {
    private static Logger log = Logger.getLogger(AlarmClockHeartbeatTask.class);
    public static long PERIODICITY = TimeUnit.MINUTES.toMillis(10);
    private final String name;

    AlarmClockHeartbeatTask(String name, AlarmClock alarmClock) {
        super(PERIODICITY);
        this.name = name;
        setAlarmClock(alarmClock);
    }

    @Override
    public void onTaskFailure(Object context) {
    }

    private String getName() {
        return name;
    }

    @Override
    public boolean performTask(Object context) {
        log.info("performTask() - Alarm clock " + getName() + " is alive");
        return true;
    }
}
