package com.clock;

import java.util.Calendar;
import java.util.Date;

public class AlarmClockRequest {
    private int requestType;
    public final int ABSOLUTE = 1;
    public final int RELATIVE = 2;
    private IAlarmClockRegistration alarmAlarmClockRegistration = null;
    private Calendar calWakeUpDueTime;
    private long timeUntilWakeUp;
    private final Date dateWakeUpDueTime;
    private long requestID = 0;

    protected AlarmClockRequest(long timeUntilWakeUp, IAlarmClockRegistration alarmClockRegistration) {
        requestType = RELATIVE;
        alarmAlarmClockRegistration = alarmClockRegistration;
        this.timeUntilWakeUp = timeUntilWakeUp;
        dateWakeUpDueTime = new Date();
    }

    protected AlarmClockRequest(Calendar wakeUpDueTime, IAlarmClockRegistration alarmClockRegistration) {
        requestType = ABSOLUTE;
        alarmAlarmClockRegistration = alarmClockRegistration;
        calWakeUpDueTime = wakeUpDueTime;
        dateWakeUpDueTime = wakeUpDueTime.getTime();
    }

    protected void completeTimeEntries() {
        if (requestType == ABSOLUTE) {
            timeUntilWakeUp = dateWakeUpDueTime.getTime() - System.currentTimeMillis();
        } else {
            dateWakeUpDueTime.setTime(System.currentTimeMillis() + timeUntilWakeUp);
        }
    }

    public Calendar getAbsoluteTime() {
        if (null == calWakeUpDueTime) {
            calWakeUpDueTime = Calendar.getInstance();
            calWakeUpDueTime.setTime(dateWakeUpDueTime);
        }
        return calWakeUpDueTime;
    }

    public IAlarmClockRegistration getAlarmClockRegistration() {
        return alarmAlarmClockRegistration;
    }

    public Date getDateTypeAbsoluteTime() {
        return dateWakeUpDueTime;
    }

    public long getRelativeTime() {
        return timeUntilWakeUp;
    }

    public long getRequestID() {
        return requestID;
    }

    public int getRequestType() {
        return requestType;
    }

    public long getTimeMarginFromCurrentTime() {
        return getDateTypeAbsoluteTime().getTime() - System.currentTimeMillis();
    }

    public void setAlarmClockRegistration(IAlarmClockRegistration alarmClockRegistration) {
        alarmAlarmClockRegistration = alarmClockRegistration;
    }

    public void setRequestID(long number) {
        requestID = number;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    @Override
    public String toString() {
        return "AlarmClockRequest{" +
                "requestType=" + requestType +
                ", ABSOLUTE=" + ABSOLUTE +
                ", RELATIVE=" + RELATIVE +
                ", alarmAlarmClockRegistration=" + alarmAlarmClockRegistration +
                ", calWakeUpDueTime=" + calWakeUpDueTime +
                ", timeUntilWakeUp=" + timeUntilWakeUp +
                ", dateWakeUpDueTime=" + dateWakeUpDueTime +
                ", requestID=" + requestID +
                '}';
    }
}
