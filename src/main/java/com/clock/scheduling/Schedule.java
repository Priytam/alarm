package com.clock.scheduling;

import com.clock.AlarmClock;
import com.clock.IAlarmClockRegistration;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Schedule {
    final transient private static Logger log = Logger.getLogger(Schedule.class);
    private final Object lock = new Object();
    private final Collection<Runnable> registrants = Sets.newHashSet();
    private final Collection<IScheduleEntry> entries = Sets.newHashSet();
    private final Collection<ScheduleRegistration> registrations = Sets.newHashSet();
    private final ExecutorService threadPool;
    private static final int DEFAULT_THREAD_COUNT = 10;
    private static final String SCHEDULE_THREAD_POOL = "SCHEDULE_THREAD_POOL";
    private String executionIntervalExp = null;
    private String alarmClockName = null;
    private long executionInterval = Long.MIN_VALUE;
    private int hash;

    public Schedule(String alarmClockName) {
        this(alarmClockName, DEFAULT_THREAD_COUNT);
    }

    public Schedule(String clockName, int poolSize) {
        setAlarmClockName(clockName);
        log.debug("Starting schedule thread pool");
        threadPool = Executors.newFixedThreadPool(poolSize, r -> new Thread(r, SCHEDULE_THREAD_POOL));
    }

    private class ScheduleRegistration implements IAlarmClockRegistration {
        private IScheduleEntry scheduleEntry;
        private Runnable runnableRegistrant;
        private long clockRegistrationID;
        private boolean stopped = false;

        @Override
        public void wakeUp(long id) {
            try {
                if (getClockRegistrationID() == id) {
                    if (log.isDebugEnabled()) {
                        log.debug("Sending registrant " + getRegistrant() + " to execution");
                    }
                    threadPool.submit(getRegistrant());
                }
            } finally {
                if (!stopped()) {
                    long newClockId = getAlarmClock().register(getEntry().getNextInvocationTime(), this);
                    setClockRegistrationID(newClockId);
                }
            }

        }

        public void stop() {
            stopped = true;
        }

        private boolean stopped() {
            return stopped;
        }

        public IScheduleEntry getEntry() {
            return scheduleEntry;
        }

        public void setEntry(IScheduleEntry entry) {
            scheduleEntry = entry;
        }

        public Runnable getRegistrant() {
            return runnableRegistrant;
        }

        public void setRegistrant(Runnable registrant) {
            runnableRegistrant = registrant;
        }

        public long getClockRegistrationID() {
            return clockRegistrationID;
        }

        public void setClockRegistrationID(long clockRegistrationID) {
            this.clockRegistrationID = clockRegistrationID;
        }

    }

    public void register(Runnable registrant) {
        if (null == registrant) {
            log.error("register() called with a null parameter");
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("register() called with " + registrant);
        }
        synchronized (lock) {
            if (!registrants.contains(registrant)) {
                registrants.add(registrant);
                Iterator<IScheduleEntry> itEntries = entries.iterator();
                while (itEntries.hasNext()) {
                    IScheduleEntry entry = itEntries.next();
                    addRegistration(entry, registrant);
                }
            }
        }
    }

    public void unregister(Runnable registrant) {
        if (null == registrant) {
            log.warn("unregister() called with a null parameter");
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("unregister() called with" + registrant);
        }
        synchronized (lock) {
            if (registrants.contains(registrant)) {
                registrants.remove(registrant);
                Collection<ScheduleRegistration> registrationsToRemove = Lists.newArrayList();
                Iterator<ScheduleRegistration> itRegistrations = registrations.iterator();
                while (itRegistrations.hasNext()) {
                    ScheduleRegistration registration = itRegistrations.next();

                    if (registration.getRegistrant() == registrant) {
                        registration.stop();
                        registrationsToRemove.add(registration);
                    }
                }
                itRegistrations = registrationsToRemove.iterator();
                while (itRegistrations.hasNext()) {
                    ScheduleRegistration registration = itRegistrations.next();

                    log.debug("Canceling registration " + registration.getClockRegistrationID());
                    getAlarmClock().cancelRegistration(registration.getClockRegistrationID());
                    registrations.remove(registration);
                }
            }
        }
    }

    public void addEntry(IScheduleEntry entry) {
        if (null == entry) {
            log.error("addEntry() called with null parameter");
            return;
        }

        synchronized (lock) {
            if (!entries.contains(entry)) {
                entries.add(entry);
                Iterator<Runnable> itRegistrants = registrants.iterator();
                while (itRegistrants.hasNext()) {
                    Runnable registrant = itRegistrants.next();
                    addRegistration(entry, registrant);
                }
            }
        }
    }

    public void removeEntry(IScheduleEntry entry) {
        if (null == entry) {
            log.warn("removeEntry() called with null parameter");
            return;
        }

        synchronized (lock) {
            if (entries.contains(entry)) {
                entries.remove(entry);
                Collection<ScheduleRegistration> registrationsToRemove = Lists.newArrayList();
                Iterator<ScheduleRegistration> itRegistrations = registrations.iterator();
                while (itRegistrations.hasNext()) {
                    ScheduleRegistration registration = itRegistrations.next();

                    if (registration.getEntry() == entry) {
                        registration.stop();
                        registrationsToRemove.add(registration);
                    }
                }
                itRegistrations = registrationsToRemove.iterator();
                while (itRegistrations.hasNext()) {
                    ScheduleRegistration registration = itRegistrations.next();
                    getAlarmClock().cancelRegistration(registration.getClockRegistrationID());
                    registrations.remove(registration);
                }
            }
        }
    }

    public Collection<IScheduleEntry> getScheduleEntries() {
        synchronized (lock) {
            return Lists.newArrayList(entries);
        }
    }

    public Collection<Runnable> getRegistrants() {
        synchronized (lock) {
            return Lists.newArrayList(registrants);
        }
    }

    private void addRegistration(IScheduleEntry entry, Runnable registrant) {
        if (null == entry || null == registrant) {
            log.error("addRegistration() was called with a null parameter");
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("addRegistration() - called with " + entry + ", " + registrant);
        }
        ScheduleRegistration registration = new ScheduleRegistration();
        registration.setEntry(entry);
        registration.setRegistrant(registrant);
        long clockRegistrationID = getAlarmClock().register(entry.getFirstInvocationTime(), registration);
        registration.setClockRegistrationID(clockRegistrationID);
        registrations.add(registration);
    }

    public String getAlarmClockName() {
        return alarmClockName;
    }

    public void setAlarmClockName(String alarmClockName) {
        this.alarmClockName = alarmClockName;
    }

    private AlarmClock getAlarmClock() {
        String alarmClockName = getAlarmClockName();
        if (null == alarmClockName) {
            return AlarmClock.getInstance();
        } else {
            return AlarmClock.getInstance(alarmClockName);
        }
    }

    public void unregisterAll() {
        for (Object r : getRegistrants()) {
            unregister((Runnable) r);
        }
        for (Object e : getScheduleEntries()) {
            removeEntry((IScheduleEntry) e);
        }
    }

    public String getStatus() {
        String $ = "";
        Comparator<ScheduleRegistration> c = Comparator.comparing(o -> o.getEntry().getNextInvocationTime());
        ArrayList<ScheduleRegistration> l = Lists.newArrayList(registrations);
        Collections.sort(l, c);
        for (ScheduleRegistration s : l) {
            $ += "\n\t\t\t" + s.getEntry().getDescription() + " ";
            $ += "\n\t\t\t\tNext time will be at: " + s.getEntry().getNextInvocationTime().getTime();
        }
        return $;
    }

    public long getNextExecutionTime() {
        if (CollectionUtils.isEmpty(registrations)) {
            return 0;
        }
        Comparator<ScheduleRegistration> c = Comparator.comparing(o -> o.getEntry().getNextInvocationTime());
        ArrayList<ScheduleRegistration> l = Lists.newArrayList(registrations);
        l.sort(c);
        return l.get(0).getEntry().getNextInvocationTime().getTimeInMillis();
    }

    public long getExecutionInterval() {
        return executionInterval;
    }

    public void setExecutionInterval(long executionIntervalMillis) {
        executionInterval = executionIntervalMillis;
    }

    public String getExecutionIntervalExpression() {
        return executionIntervalExp;
    }

    public void setExecutionIntervalExpression(String executionIntervalExp) {
        this.executionIntervalExp = executionIntervalExp;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int iHash) {
        hash = iHash;
    }

    public String getScheduleString() {
        List<String> lstSchedules = Lists.newArrayList();
        for (IScheduleEntry iScheduleEntry : getScheduleEntries()) {
            lstSchedules.add(iScheduleEntry.getDescription());
        }
        return Joiner.on(",").join(lstSchedules);
    }
}
