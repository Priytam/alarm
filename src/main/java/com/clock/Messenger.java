package com.clock;

import com.clock.stopwatch.IStopWatch;
import com.clock.stopwatch.StopWatch;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

class Messenger implements Runnable {
    transient private static Logger log = Logger.getLogger(Messenger.class);
    private AlarmClockRequest request;
    public static long MAX_RUN_TIME_ALLOWED = TimeUnit.SECONDS.toMillis(3);
    public static long MAX_LATE_TIME_ALLOWED = TimeUnit.MINUTES.toMillis(2);
    public static boolean REPORT_STATISTICS = true;
    private static boolean CHECK_LATENCY = true;
    private IAlarmClockStatisticsLogger statLogger;
    private final String clockName;
    private final boolean alertOnLatencyViolations;

    public Messenger(AlarmClockRequest request, IAlarmClockStatisticsLogger statLogger, String clockName, boolean alertOnLatencyViolations) {
        super();
        this.statLogger = statLogger;
        this.clockName = clockName;
        this.alertOnLatencyViolations = alertOnLatencyViolations;
        setRequest(request);
    }

    private IAlarmClockRegistration getTarget() {
        return request.getAlarmClockRegistration();
    }

    @Override
    public void run() {
        if (log.isDebugEnabled()) {
            log.debug("will run target " + getTarget());
        }
        if (CHECK_LATENCY && alertOnLatencyViolations) {
            long timeTillRun = -request.getTimeMarginFromCurrentTime();
            if ((timeTillRun > MAX_LATE_TIME_ALLOWED)) {
                throw new Error("AlarmClock task is late: AlarmClock = " + clockName + ", Task =  " + request.getAlarmClockRegistration() + ", Late = " + timeTillRun + " ms (MAX_LATE_TIME_ALLOWED = " + MAX_LATE_TIME_ALLOWED + " ms)");
            }
        }
        IStopWatch stopWatch = StopWatch.createAndStart();
        try {
            getTarget().wakeUp(getRequestID());
            stopWatch.pause();
            log.debug("elapsedMillis " + stopWatch.elapsedMillis());
            if (alertOnLatencyViolations && stopWatch.elapsedMillis() > MAX_RUN_TIME_ALLOWED)
                log.warn("AlarmClock task " + request.getAlarmClockRegistration() + " took " + stopWatch.elapsedMillis() + " ms, Max allowed time is " + MAX_RUN_TIME_ALLOWED + " ms");

            if (REPORT_STATISTICS && null != statLogger) {
                String name = request.getAlarmClockRegistration().toString();
                int index = name.indexOf("@");
                if (index != -1) {
                    name = name.substring(0, index);
                }

                statLogger.recordTask(clockName + " : " + name, stopWatch.elapsedMillis());
            }
        } finally {
            stopWatch.stop();
        }
    }

    private void setRequest(AlarmClockRequest request) {
        this.request = request;
    }

    public long getRequestID() {
        return request.getRequestID();
    }
}
