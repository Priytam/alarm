package com.clock.example;

import com.clock.task.Task;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * User: Priytam Jee Pandey
 * Date: 14/06/20
 * Time: 12:30 am
 * email: priytam.pandey@cleartrip.com
 */
public class TaskExample {
    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }
    public static void main(String[] args) {
        new Task() {
            @Override
            public boolean performTask(Object context) {
                System.out.println("Perform task");
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {
                System.out.println("failed execution");
            }
        }.start();
    }
}
