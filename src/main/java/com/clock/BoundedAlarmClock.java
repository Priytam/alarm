package com.clock;

import org.apache.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class BoundedAlarmClock extends AlarmClock {
    private int capacity = 1000;
    private static Logger log = Logger.getLogger(BoundedAlarmClock.class.getName());
    private int numberOfThreads = NUMBER_OF_THREAD;
    protected BoundedAlarmClock(int capacity) {
        super();
        this.capacity = capacity;
    }

    @Override
    protected ThreadPoolExecutor createThreadPool(int numOfThread) {
        int minSize = getMinimumNumberOfThreads();
        long keepAlive = TimeUnit.MINUTES.toMillis(5);
        return new ThreadPoolExecutor(minSize, numOfThread, keepAlive, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(capacity));
    }

    @Override
    public String toString() {
        return "BoundedAlarmClock Task List:" + getTaskList();
    }

    public static BoundedAlarmClock create(String sName, String sThreadPoolName, int minThreadPoolSize, int maxThreadPoolSize, boolean alertOnLatencyViolations, int capacity) {
        BoundedAlarmClock cClock = new BoundedAlarmClock(capacity);
        cClock.setThreadPoolName(sThreadPoolName);
        cClock.setNumberOfThreads(maxThreadPoolSize);
        cClock.setMinimumNumberOfThreads(minThreadPoolSize);
        cClock.setClockName(sName);
        cClock.setThrowOnLatencyViolations(alertOnLatencyViolations);
        mpClocks.put(sName, cClock);
        return cClock;

    }

    public static BoundedAlarmClock create(String sName, String sThreadPoolName, int minThreadPoolSize, int maxThreadPoolSize, int capacity) {
        return create(sName, sThreadPoolName, minThreadPoolSize, maxThreadPoolSize, true, capacity);
    }

    public int getMinimumNumberOfThreads() {
        return numberOfThreads;
    }

    public void setMinimumNumberOfThreads(int minNumberOfThreads) {
        numberOfThreads = minNumberOfThreads;
    }

}
