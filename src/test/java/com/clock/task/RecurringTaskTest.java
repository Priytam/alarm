package com.clock.task;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: Priytam Jee Pandey
 * Date: 13/06/20
 * Time: 7:36 pm
 * email: priytam.pandey@cleartrip.com
 */
public class RecurringTaskTest extends AbstractTestCase  {
    @Test
    public void shouldRecur() throws InterruptedException {
        CounterTaskResult taskResult = new CounterTaskResult();
        new RecurringTask(1000) {
            @Override
            public boolean performTask(Object context) {
                taskResult.increment();
                return true;
            }
            @Override
            public void onTaskFailure(Object context) {

            }
        }.start();
        Thread.sleep(3000);
        Assert.assertTrue(taskResult.count > 1);
    }

    @Test
    public void shouldPerformOnFail() throws InterruptedException {
        CounterTaskResult taskResult = new CounterTaskResult();
        new RecurringTask(1000) {
            @Override
            public boolean performTask(Object context) {
                return false;
            }
            @Override
            public void onTaskFailure(Object context) {
                taskResult.increment();
            }
        }.start();
        Thread.sleep(3000);
        Assert.assertTrue(taskResult.count > 1);
    }

    @Test
    public void shouldStop() throws InterruptedException {
        CounterTaskResult taskResult = new CounterTaskResult();
        RecurringTask recurringTask = new RecurringTask(1000) {
            @Override
            public boolean performTask(Object context) {
                return false;
            }

            @Override
            public void onTaskFailure(Object context) {
                taskResult.increment();
            }

        };
        recurringTask.start();
        Thread.sleep(2000);
        int count = taskResult.count;
        recurringTask.stop();
        Thread.sleep(2000);
        Assert.assertEquals(count, taskResult.count);
    }
}