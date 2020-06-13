package com.clock.task;

public class SyncTaskExecution extends TaskExecution {
    @Override
    protected final boolean doExecute(Task task, Object context) {
        return task.performTask(context);
    }
}
