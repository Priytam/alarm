package com.clock.task;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

/**
 * User: Priytam Jee Pandey
 * Date: 14/06/20
 * Time: 12:11 am
 * email: priytam.pandey@cleartrip.com
 */
public class AbstractTestCase {
    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }

    protected static class CounterTaskResult {
        int count = 0;

        public void increment() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    protected static class FlagTaskResult {
        boolean taskPerformed = false;

        public boolean isTaskPerformed() {
            return taskPerformed;
        }

        public void setTaskPerformed(boolean taskPerformed) {
            this.taskPerformed = taskPerformed;
        }
    }
}
