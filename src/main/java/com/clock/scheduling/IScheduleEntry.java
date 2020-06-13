/**
 * 
 */
package com.clock.scheduling;

import java.util.Calendar;

/**
 * Represents a single schedule entry. The <code>IScheduleEntry</code>s are used by Schedule class. 
 * Schedule entry is expected to be able at all times to calculate its
 * next invocation time. This allows building schedules with dynamic recurrencies
 *
 * @see Schedule
 * 
 * @author advinsky
 */
public interface IScheduleEntry
{
	Calendar getFirstInvocationTime();
	Calendar getNextInvocationTime();
	String getDescription();
	void setScheduleInterval(long interval);
	IScheduleEntry clone();
}
