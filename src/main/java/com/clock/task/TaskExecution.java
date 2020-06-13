package com.clock.task;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;

public abstract class TaskExecution {
    private static final Logger log = Logger.getLogger(TaskExecution.class);

    public void execute(Task task) {
        Preconditions.checkNotNull(task);

        Object context = task.getContext();
        boolean success = false;
        try {
            success = doExecute(task, context);
        } catch (Throwable t) {
            log.error("execute() - unhandled Exception when executing task", t);
        }
        if (!success) {
            try {
                task.onTaskFailure(context);
            } catch (Throwable t) {
                log.error("execute() - unhandled Exception when executing onTaskFailure", t);
            }
        }
    }

    protected abstract boolean doExecute(Task task, Object context);
}
