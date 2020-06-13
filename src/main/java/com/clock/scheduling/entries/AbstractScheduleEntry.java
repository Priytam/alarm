package com.clock.scheduling.entries;

import com.clock.scheduling.IScheduleEntry;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Random;

public abstract class AbstractScheduleEntry implements IScheduleEntry {
	transient private static Logger log = Logger.getLogger(AbstractScheduleEntry.class);
	protected Calendar calendar = Calendar.getInstance();
	private final int field;
	private final int period;
	protected long scheduleInterval = Long.MIN_VALUE;
	protected String representation;

    protected AbstractScheduleEntry(int field, int period) {
        this.field = field;
        this.period = period;
    }

    @Override
    public String getDescription() {
        return representation;
    }

    @Override
    public final Calendar getFirstInvocationTime() {
        Calendar now = Calendar.getInstance();
        while (calendar.getTimeInMillis() <= now.getTimeInMillis()) {
            calendar.add(field, period);
        }
        if (log.isDebugEnabled()) {
            log.debug("First invocation of " + this + " is " + calendar);
        }
        return calendar;
    }

    @Override
    public final Calendar getNextInvocationTime() {
        Calendar now = Calendar.getInstance();

        while (calendar.getTimeInMillis() <= now.getTimeInMillis() + 20) {
            calendar.add(field, period);
        }

        if (log.isDebugEnabled()) {
            log.debug("Next invocation of " + this + " is " + calendar);
        }

        return calendar;
    }

    private void addInterval() {
        if (0 >= scheduleInterval) {
            return;
        }
        Random random = new Random();

        long randomValue = (long) (random.nextDouble() * scheduleInterval);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + randomValue);
    }

    @Override
    public void setScheduleInterval(long interval) {
        scheduleInterval = interval;
        addInterval();
    }

    @Override
    public abstract IScheduleEntry clone();

}
