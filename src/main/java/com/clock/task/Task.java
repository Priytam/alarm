package com.clock.task;

import com.clock.AlarmClock;
import com.clock.IAlarmClockRegistration;
import org.apache.log4j.Logger;

public abstract class Task implements IAlarmClockRegistration {
    final transient private static Logger log = Logger.getLogger(Task.class);
    private long wakeupInterval;
    private boolean isStop = true;
    private long registrationID = -1;
    private Object context;
    private final Object objSync = new Object();
    private AlarmClock clock = null;
    private TaskExecution taskExecution = new SyncTaskExecution();

    public abstract boolean performTask(Object context);

    public abstract void onTaskFailure(Object context);

    protected Task() {
    }

    public Task(long wakeupIntervalMilli) {
        this(wakeupIntervalMilli, null);
    }

    public Task(long wakeupIntervalMilli, Object context) {
        setWakeupInterval(wakeupIntervalMilli);
        setContext(context);
    }

    public void setTaskExecution(TaskExecution execution) {
        taskExecution = execution;
    }

    protected Object getSyncObject() {
        return objSync;
    }

    public void start() {
        synchronized (getSyncObject()) {
            setStop(false);
            registerForWakeup();
        }
    }

    public void stop() {
        synchronized (getSyncObject()) {
            setStop(true);
            unregister();
        }
    }

    public void restart() {
        if (isStop()) {
            unregister();
            start();
        }
    }

    @Override
    synchronized public void wakeUp(long id) {
        boolean needToRun = false;
        synchronized (getSyncObject()) {
            if ((getRegistrationID() == id) || isAllowDuplicateExecution()) {
                needToRun = true;
            }
        }
        if (!needToRun) {
            log.warn("ignoring wakeUp: id=" + id + ",getRegistrationID()=" + getRegistrationID());
            return;
        }

        taskExecution.execute(this);
    }

    protected boolean isAllowDuplicateExecution() {
        return false;
    }

    protected void registerForWakeup(long time) {
        synchronized (getSyncObject()) {
            long registrationID = getAlarmClock().register(time, this);
            setRegistrationID(registrationID);
        }
    }

    protected void registerForWakeup() {
        long interval = getWakeupInterval();
        registerForWakeup(interval);
    }

    protected void unregister() {
        synchronized (getSyncObject()) {
            getAlarmClock().cancelRegistration(getRegistrationID());
            setRegistrationID(-1);
        }
    }

    public long getWakeupInterval() {
        return wakeupInterval;
    }

    protected void setWakeupInterval(long wakeupInterval) {
        this.wakeupInterval = wakeupInterval;
        if (this.wakeupInterval < 100) {
            this.wakeupInterval = 100;
        }
    }

    protected boolean isStop() {
        return isStop;
    }

    protected void setStop(boolean stop) {
        isStop = stop;
    }

    protected long getRegistrationID() {
        return registrationID;
    }

    private void setRegistrationID(long registrationID) {
        this.registrationID = registrationID;
    }

    public Object getContext() {
        return context;
    }

    public void setContext(Object context) {
        this.context = context;
    }

    private AlarmClock getAlarmClock() {
        if (null != clock) {
            return clock;
        }
        return AlarmClock.getInstance();
    }

    protected void setAlarmClock(AlarmClock cClock) {
        clock = cClock;
    }
}
