package com.clock.task;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: Priytam Jee Pandey
 * Date: 13/06/20
 * Time: 7:36 pm
 * email: priytam.pandey@cleartrip.com
 */
public class TaskTest extends AbstractTestCase {

    @Test
    public void shouldExecuteTask() throws InterruptedException {
        FlagTaskResult taskResult = new FlagTaskResult();
        new Task() {
            @Override
            public boolean performTask(Object context) {
                taskResult.setTaskPerformed(true);
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {

            }
        }.start();
        Thread.sleep(2000);
        Assert.assertTrue(taskResult.isTaskPerformed());
    }

    @Test
    public void shouldExecuteOnTaskFailure() throws InterruptedException {
        FlagTaskResult taskResult = new FlagTaskResult();
        new Task() {
            @Override
            public boolean performTask(Object context) {
                return false;
            }

            @Override
            public void onTaskFailure(Object context) {
                taskResult.setTaskPerformed(true);
            }
        }.start();
        Thread.sleep(2000);
        Assert.assertTrue(taskResult.isTaskPerformed());
    }
}