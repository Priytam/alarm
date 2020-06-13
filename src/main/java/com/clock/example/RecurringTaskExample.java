package com.clock.example;

import com.clock.AlarmClock;
import com.clock.task.AsyncTaskExecution;
import com.clock.task.RecurringTask;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * User: Priytam Jee Pandey
 * Date: 13/06/20
 * Time: 7:36 pm
 * email: priytam.pandey@cleartrip.com
 */
public class RecurringTaskExample {

    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }

    private static class TaskResult {
        int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        startStop();
        asyncTaskExecution();
    }

    private static void asyncTaskExecution() throws InterruptedException {
        TaskResult taskResult = new TaskResult();
        RecurringTask recurringTask = new RecurringTask(2000) {
            @Override
            public boolean performTask(Object context) {
                taskResult.increment();
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {

            }
        };
        recurringTask.setTaskExecution(new AsyncTaskExecution(4));
        recurringTask.start();

        Thread.sleep(4000);
        recurringTask.stop();
    }

    private static void startStop() throws InterruptedException {
        TaskResult taskResult = new TaskResult();
        RecurringTask recurringTask = new RecurringTask(2000) {
            @Override
            public boolean performTask(Object context) {
                taskResult.increment();
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {

            }
        };
        recurringTask.start();

        Thread.sleep(4000);
        recurringTask.stop();
    }

}