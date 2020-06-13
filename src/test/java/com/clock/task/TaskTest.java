package com.clock.task;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: Priytam Jee Pandey
 * Date: 13/06/20
 * Time: 7:36 pm
 * email: priytam.pandey@cleartrip.com
 */
public class TaskTest extends TestCase {

    class TaskResult {
        boolean taskPerformed = false;

        public boolean isTaskPerformed() {
            return taskPerformed;
        }

        public void setTaskPerformed(boolean taskPerformed) {
            this.taskPerformed = taskPerformed;
        }
    }

    @Test
    public void shouldExecuteTask() {
        TaskResult taskResult = new TaskResult();
        Task task = new Task() {
            @Override
            public boolean performTask(Object context) {
                taskResult.setTaskPerformed(true);
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {

            }
        };

        task.start();

        Assert.assertTrue(taskResult.isTaskPerformed());
    }
}