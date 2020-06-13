package com.clock;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AlarmClock implements Runnable {
    private static Logger log = Logger.getLogger(AlarmClock.class.getName());
    public static final String MAIN_CLOCK_THREAD_POOL_NAME = "CLOCK_THREAD_POOL";
    private static IAlarmClockStatisticsLogger statLogger = null;
    private static final Object statLoggerLock = new Object();
    public static long ALLOWED_TIME_REGRESSION = TimeUnit.MINUTES.toMillis(25);
    private String threadPoolName = MAIN_CLOCK_THREAD_POOL_NAME;
    private String clockName = "Alarmclock";
    private AlarmClockHeartbeatTask heartbeatTask;
    protected static Map<String, AlarmClock> mpClocks = Collections.synchronizedMap(Maps.<String, AlarmClock>newHashMap());
    private TaskList taskList = null;
    private static AlarmClock instance;// = new AlarmClock();
    private volatile boolean active = false;
    private Thread thread;
    public static int NUMBER_OF_THREAD = 2;
    private int numberOfThreads = NUMBER_OF_THREAD;
    public static int MAX_PENDING_THREADS = 4000;
    private int maxPendingThreads = MAX_PENDING_THREADS;
    private boolean threadsWakeupFinished;
    private boolean mainClock = true;
    private long lastTimeSeen = 0;
    private final Object timeRegressionSycObject = new Object();
    private boolean alertOnLatencyViolations = true;
    private ThreadPoolExecutor sltp;

    protected AlarmClock() {
        setTaskList(new TaskList());
    }

    public long register(long timeUntilWakeUp, IAlarmClockRegistration alarmClockRegistration) {
        return myRegister(timeUntilWakeUp, alarmClockRegistration);
    }

    public long registerWeak(long timeUntilWakeUp, IAlarmClockRegistration alarmClockRegistration) {
        return myRegister(timeUntilWakeUp, new WeakAlarmClockRegistration(alarmClockRegistration));
    }

    private long myRegister(long timeUntilWakeUp, IAlarmClockRegistration alarmClockRegistration) {
        long delay = timeUntilWakeUp;
        if (delay < 10) {
            delay = 10;
        }
        AlarmClockRequest request = new AlarmClockRequest(delay, alarmClockRegistration);
        long requestNumber;
        request.completeTimeEntries();
        requestNumber = getTaskList().addTask(request);
        handleTimeRegression(requestNumber);
        if (log.isDebugEnabled()) {
            log.debug("registering " + alarmClockRegistration + " in " + delay + " requestNumber " + requestNumber);
        }
        return requestNumber;
    }

    private void handleTimeRegression(long requestNumber) {
        synchronized (timeRegressionSycObject) {
            long currentTimeMillis = System.currentTimeMillis();
            if (lastTimeSeen > currentTimeMillis + ALLOWED_TIME_REGRESSION) {
                log.warn("handleTimeRegression() - discovered time violation");
                log.warn("handleTimeRegression() - m_lLastTimeSeen(" + lastTimeSeen + ") > currentTimeMillis(" + currentTimeMillis + ") + allowdDelta(" + ALLOWED_TIME_REGRESSION + ")");
                log.warn("handleTimeRegression() - probably clock was adjusted on machine");
                log.warn("handleTimeRegression() - will trigger all entries in AlarmClock before " + requestNumber);
                getTaskList().setLastTimeRegressionViolationRequest(requestNumber - 1);
            }
            lastTimeSeen = currentTimeMillis;
        }
    }

    public long register(Calendar wakeUpDueTime, IAlarmClockRegistration alarmClockRegistration) {
        return myRegister(wakeUpDueTime, alarmClockRegistration);
    }

    public long registerWeak(Calendar wakeUpDueTime, IAlarmClockRegistration alarmClockRegistration) {
        return myRegister(wakeUpDueTime, new WeakAlarmClockRegistration(alarmClockRegistration));
    }

    private long myRegister(Calendar wakeUpDueTime, IAlarmClockRegistration alarmClockRegistration) {
        AlarmClockRequest request = new AlarmClockRequest(wakeUpDueTime, alarmClockRegistration);
        long requestNumber;
        request.completeTimeEntries();
        requestNumber = getTaskList().addTask(request);
        handleTimeRegression(requestNumber);
        return requestNumber;
    }

    public boolean cancelRegistration(long registrationNumber) {
        getTaskList().removeTask(registrationNumber);
        return true;
    }

    public boolean cleanUp() {
        log.debug("AlarmClock:cleanUp() - preforming clean up");
        return getTaskList().cleanUp();
    }

    static synchronized public AlarmClock getInstance() {
        if (null == instance) {
            instance = new AlarmClock();
            instance.setMainClock(true);
        }
        return instance;
    }

    public long getNumberOfRequestsInList() {
        return getTaskList().getCurrentTasksNumber();
    }

    public static void setStatisticsLogger(IAlarmClockStatisticsLogger statLogger) {
        synchronized (statLoggerLock) {
            AlarmClock.statLogger = statLogger;
        }
    }

    public TaskList getTaskList() {
        return taskList;
    }

    protected Thread getThread() {
        return thread;
    }

    protected void startThreadPool() {
        int numOfThread = getNumberOfThreads();
        String sNumber = System.getProperty("AlarmClock");
        if (sNumber != null) {
            numOfThread = Integer.getInteger(sNumber);
        }
        sltp = createThreadPool(numOfThread);
    }

    protected ThreadPoolExecutor createThreadPool(int numOfThread) {
        return (ThreadPoolExecutor)Executors.newFixedThreadPool(numOfThread, r -> new Thread(r, getThreadPoolName()));
    }

    @Override
    public void run() {
        active = true;
        startThreadPool();
        setThread(Thread.currentThread());
        while (active) {
            try {
                executeNextTask();

            } catch (Throwable t) {
                log.error("run() - got throwable will go to sleep for 60 sec!!!", t);
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException ie) {
                    log.warn("run() failed to sleep " + ie.getMessage());
                }

            }
        }
        threadsWakeupFinished = true;
        long registeredNumber = getNumberOfRequestsInList();
        log.debug("run() - alarm clock sutdown with " + registeredNumber + " registered objects");
        cleanUp();
        log.debug("run() - exit ...");
    }

    protected void executeNextTask() {
        AlarmClockRequest m_reqRequest = getTaskList().getFirstTask();
        if (log.isDebugEnabled()) {
            log.debug("run() - will execute " + m_reqRequest);
        }
        if (null != m_reqRequest) {
            execute(m_reqRequest);
        }
    }

    public void setLog(Logger newLog) {
        log = newLog;
    }

    public void setTaskList(
            TaskList list) {
        taskList = list;
    }

    protected void setThread(Thread thread) {
        this.thread = thread;
    }

    public void shutdown() {
        log.info("shutdown()");
        active = false;
        if (null != getThread()) {
            getThread().interrupt();
        }
    }

    public boolean isStopped() {
        return (threadsWakeupFinished && 0 == getThreadPool().getActiveCount());
    }

    @Override
    public String toString() {
        return "AlarmClock Task List:" + getTaskList();
    }

    private void execute(AlarmClockRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("execute() - waking up the object " + request.getAlarmClockRegistration());
        }
        if (isMainClock() && (getThreadPool().getQueue().size() > getMaxPendingThreads())) {
            log.info("number of pending request in ThreadPool = " + getThreadPool().getQueue().size() + "> MAX_PENDING_THREADS(" + getMaxPendingThreads() + ")");
        }
        Messenger runnable = new Messenger(request, statLogger, getClockName(), alertOnLatencyViolations);
        runTask(runnable);
    }

    protected void runTask(Messenger runnable) {
        getThreadPool().submit(runnable);
    }

    private ThreadPoolExecutor getThreadPool() {
        return sltp;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public synchronized void start() {
        if (!active) {
            log.info("start() - starting clock " + getClockName());
            Thread t = new Thread(this, getClockName());
            t.start();
            active = true;

            String name = getClockName();
            if (null == name) {
                heartbeatTask = new AlarmClockHeartbeatTask("default", this);
            } else {
                heartbeatTask = new AlarmClockHeartbeatTask(name, this);
            }
            heartbeatTask.start();
        }
    }

    protected String getThreadPoolName() {
        return threadPoolName;
    }

    protected void setThreadPoolName(String threadPoolName) {
        this.threadPoolName = threadPoolName;
    }

    public static AlarmClock create(String sName, String sThreadPoolName, int iThreadPoolSize, boolean alertOnLatencyViolations) {
        AlarmClock cClock = new AlarmClock();
        cClock.setThreadPoolName(sThreadPoolName);
        cClock.setNumberOfThreads(iThreadPoolSize);
        cClock.setClockName(sName);
        cClock.setAlertOnLatencyViolations(alertOnLatencyViolations);
        mpClocks.put(sName, cClock);
        return cClock;
    }

    protected void setAlertOnLatencyViolations(boolean alertOnLatencyViolations) {
        this.alertOnLatencyViolations = alertOnLatencyViolations;
    }

    public static AlarmClock create(String sName, String sThreadPoolName, int iThreadPoolSize) {
        return create(sName, sThreadPoolName, iThreadPoolSize, true);
    }

    public synchronized static AlarmClock getInstance(String sName) {
        AlarmClock cClock = mpClocks.get(sName);
        if (null == cClock) {
            cClock = create(sName, sName + "ThreadPool", NUMBER_OF_THREAD);
            mpClocks.put(sName, cClock);
        }
        return cClock;
    }

    private String getClockName() {
        return clockName;
    }

    protected void setClockName(String clockName) {
        this.clockName = clockName;
    }

    public int getMaxPendingThreads() {
        return maxPendingThreads;
    }

    public void setMaxPendingThreads(int maxPendingThreads) {
        this.maxPendingThreads = maxPendingThreads;
    }

    private boolean isMainClock() {
        return mainClock;
    }

    public void setMainClock(boolean mainClock) {
        this.mainClock = mainClock;
    }

}
