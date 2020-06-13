package com.clock;

import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;

public class TaskList {
    transient private static Logger log = Logger.getLogger(TaskList.class);
    private TreeSet<AlarmClockRequest> treeTaskList;
    private long serialTaskNumber;
    private long currentTasks;
    private TreeMap<Long, AlarmClockRequest> map = null;

    private long lastTimeRegressionViolationRequest = -1;

    public TaskList() {
        RequestComparator comparator = new RequestComparator();
        treeTaskList = new TreeSet<>(comparator);
        map = new TreeMap<>();
        serialTaskNumber = 0;
    }

    public long addTask(AlarmClockRequest request) {
        synchronized (this) {
            try {
                serialTaskNumber++;
                request.setRequestID(serialTaskNumber);
                treeTaskList.add(request);
                map.put(serialTaskNumber, request);
                currentTasks++;
                return serialTaskNumber;
            } catch (Throwable e) {
                if (request != null) {
                    log.warn("addTask() - failed to add task in AlarmClock for request:" + request.getRequestID(), e);
                } else {
                    log.warn("addTask() - failed to add task in AlarmClock for request is null", e);
                }
                return -1;
            } finally {
                this.notifyAll();
            }
        }
    }

    synchronized public boolean cleanUp() {
        log.debug("TaskList::cleanUp() - recieved cleanUp() with " + currentTasks + " in the list");
        map.clear();
        treeTaskList.clear();
        currentTasks = 0;
        serialTaskNumber = 0;
        return true;
    }

    public long getCurrentTasksNumber() {
        return currentTasks;
    }

    public synchronized AlarmClockRequest getFirstTask() {
        while (true) {
            if (treeTaskList.isEmpty()) {
                try {
                    log.debug("getFirstEntry()-  get into wait - no request ");
                    wait();
                    log.debug("getFirstEntry()-  out from wait() ");
                } catch (InterruptedException ex) {
                    log.debug("[catch] wait: InterruptedException");
                    return null;
                }
            }
            AlarmClockRequest result = null;
            try {
                result = treeTaskList.first();
            } catch (NoSuchElementException ne) {
                //no element;
                return null;
            }
            long timeToSleep = result.getTimeMarginFromCurrentTime();
            if (log.isDebugEnabled()) {
                log.debug("AlarmClock going to sleep - " + timeToSleep);
            }
            if (timeToSleep < 10 || isTimeRegressionViolation(result.getRequestID())) {
                removeTask(result.getRequestID());
                return result;
            } else {
                try {
                    wait(timeToSleep);
                } catch (InterruptedException e) {
                    log.debug("getFirstTask() - InterruptedException");
                    log.debug(treeTaskList);
                    return null;
                }
            }
        }
    }

    synchronized public boolean isEmpty() {
        return treeTaskList.isEmpty();
    }

    synchronized public boolean removeTask(long requestNumber) {
        AlarmClockRequest AlarmRequest;
        try {
            AlarmRequest = map.remove(requestNumber);

            if (AlarmRequest == null) {
                return true;
            }
            if (treeTaskList.remove(AlarmRequest)) {
                currentTasks--;
            }
            return true;
        } catch (ClassCastException cce) {
            log.error("removeTask() - got exception:", cce);
            return false;
        } finally {
            this.notifyAll();
        }
    }

    public synchronized String showEntries() {
        StringBuilder out = new StringBuilder();
        Iterator<AlarmClockRequest> i = treeTaskList.iterator();
        AlarmClockRequest req = null;
        while (i.hasNext()) {
            req = i.next();
            out.append("object = ").append(req.getAlarmClockRegistration()).append(", time remaining = ").append(req.getTimeMarginFromCurrentTime()).append("\n");
        }
        return out.toString();
    }

    @Override
    public String toString() {
        return "TaskList:" + treeTaskList;
    }

    public void setLastTimeRegressionViolationRequest(long requestNumber) {
        log.warn("setLastTimeRegressionViolationRequest() - set time violation until request " + requestNumber);
        lastTimeRegressionViolationRequest = requestNumber;
    }

    private boolean isTimeRegressionViolation(long requestID) {
        boolean result = requestID < lastTimeRegressionViolationRequest;
        if (result) {
            log.warn("isTimeRegressionViolation() - got time violation for request " + requestID);
        }
        return result;
    }
}
