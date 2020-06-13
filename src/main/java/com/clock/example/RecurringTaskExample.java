package com.clock.example;

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

    public static void main(String[] args) throws InterruptedException {
        startStop();
        asyncTaskExecution();
    }

    private static void asyncTaskExecution() throws InterruptedException {
        RecurringTask recurringTask = new RecurringTask(2000) {
            @Override
            public boolean performTask(Object context) {
                System.out.println("Performing task");;
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {
                System.out.println("Performing task failed");;
            }
        };
        // set async task execution
        recurringTask.setTaskExecution(new AsyncTaskExecution(4));

        //start recurring task
        recurringTask.start();
        Thread.sleep(4000);
        //stop  task
        recurringTask.stop();
    }

    private static void startStop() throws InterruptedException {
        // Create a recurring task with 'performTask' and 'onTaskFailure'
        RecurringTask recurringTask = new RecurringTask(2000) {
            @Override
            public boolean performTask(Object context) {
                System.out.println("Performing task");;
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {
                System.out.println("Performing task failed");;
            }
        };

        //start recurring task
        recurringTask.start();
        Thread.sleep(4000);

        //stop  task
        recurringTask.stop();
    }
}