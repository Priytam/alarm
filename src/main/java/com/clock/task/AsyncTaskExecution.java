package com.clock.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncTaskExecution extends TaskExecution {
    private static final String THREAD_POOL_NAME = "ASYNC_TASK_EXECUTION_THREAD_POOL";
    private static final int THREAD_POOL_SIZE = 10;
    private ExecutorService threadPool;

    public AsyncTaskExecution(int poolSize) {
        threadPool = Executors.newFixedThreadPool(poolSize, r -> new Thread(r, THREAD_POOL_NAME));
    }

    public AsyncTaskExecution() {
        this(THREAD_POOL_SIZE);
    }

    @Override
    protected boolean doExecute(final Task task, final Object context) {
        Runnable runnable = () -> task.performTask(context);
        threadPool.submit(runnable);
        return true;
    }

}
