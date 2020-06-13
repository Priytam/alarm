package com.clock.task;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;


public abstract class RecurringTask extends Task {
    transient private static Logger log = Logger.getLogger(RecurringTask.class);
    private boolean registerBeforeExecution = false;
    private boolean isRunning = false;
    private boolean runImmediately = true;
    private boolean executionTimeImpactsInterval = false;

    public RecurringTask(long wakeupIntervalMilli) {
        this(wakeupIntervalMilli, false);
    }

    public RecurringTask(long wakeupIntervalMilli, boolean registerBeforeExecution) {
        this(wakeupIntervalMilli, registerBeforeExecution, true);
        this.registerBeforeExecution = registerBeforeExecution;
    }

    protected void setExecutionTimeImpactsInterval(boolean bExecutionTimeImpactsInterval) {
        Preconditions.checkState(!bExecutionTimeImpactsInterval || !isRegisterBeforeExecution());
        executionTimeImpactsInterval = bExecutionTimeImpactsInterval;
    }

    private boolean isExecutionTimeImpactsInterval() {
        return executionTimeImpactsInterval;
    }

    public RecurringTask(long wakeupIntervalMilli, boolean registerBeforeExecution, boolean runImmediately) {
        super(wakeupIntervalMilli);
        this.registerBeforeExecution = registerBeforeExecution;
        this.runImmediately = runImmediately;
    }

    public RecurringTask(long wakeupIntervalMilli, Object context) {
        super(wakeupIntervalMilli, context);
    }

    @Override
    synchronized public void wakeUp(long id) {
        long startTime = System.currentTimeMillis();
        boolean bNeedToRun = false;
        synchronized (getSyncObject()) {
            if (!isStop()) {
                if (id == getRegistrationID()) {
                    bNeedToRun = true;
                } else {
                    log.warn("ignoring wakeUp: id=" + id + ",getRegistrationID()=" + getRegistrationID());
                }
            }
        }
        if (!bNeedToRun) {
            return;
        }
        if (isRegisterBeforeExecution()) {
            registerForWakeup();
            if (isRunning()) {
                log.warn("wakeUp() - Skipping task invocation because another instance is still running");
                return;
            }
            setRunning(true);
        }

        try {
            super.wakeUp(id);
        } catch (Throwable t) {
            log.warn("wakeUp() - exception occurred", t);
        } finally {
            if (isRegisterBeforeExecution()) {
                setRunning(false);
            } else {
                registerForWakeup(getInterval(System.currentTimeMillis() - startTime));
            }
        }
    }

    private long getInterval(long executionTime) {
        return isExecutionTimeImpactsInterval() ? (getWakeupInterval() - executionTime) : getWakeupInterval(); // if negative - alarm clock will run in 10 ms
    }

    public boolean isRegisterBeforeExecution() {
        return registerBeforeExecution;
    }

    private boolean isRunning() {
        return isRunning;
    }

    private void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void start() {
        synchronized (getSyncObject()) {
            setStop(false);
            registerForWakeup(runImmediately ? 100 : getWakeupInterval());
            startClock();
        }
    }

    public void startAfterWait() {
        super.start();
    }

    public void startAfterWait(long waitMillis) {
        synchronized (getSyncObject()) {
            setStop(false);
            registerForWakeup(waitMillis);
            startClock();
        }
    }

    @Override
    protected final boolean isAllowDuplicateExecution() {
        return isRegisterBeforeExecution();
    }

    public void forceRestart() {
        stop();
        restart();
    }
}
