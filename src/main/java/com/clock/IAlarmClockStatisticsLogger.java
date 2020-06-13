package com.clock;

public interface IAlarmClockStatisticsLogger {
    void recordTask(String name, long time);
}
