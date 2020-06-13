package com.clock;

import java.lang.ref.WeakReference;

public class WeakAlarmClockRegistration implements IAlarmClockRegistration {

    private final WeakReference<IAlarmClockRegistration> alarmClockRegistration;

    public WeakAlarmClockRegistration(IAlarmClockRegistration alarmClockRegistration) {
        this.alarmClockRegistration = new WeakReference<>(alarmClockRegistration);
    }

    @Override
    public void wakeUp(long id) {
        IAlarmClockRegistration registration = alarmClockRegistration.get();
        if (registration != null) {
            registration.wakeUp(id);
        }
    }

    @Override
    public String toString() {
        return "WeakAlarmClockRegistration{" +
                "alarmClockRegistration=" + alarmClockRegistration +
                '}';
    }
}
