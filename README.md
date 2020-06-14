# AlarmClock
 AlarmClock with features
 - Alarm to wakeup 
 - Scheduling Task
 - Recurring Task
 - Sync and Async execution of Task
 
 [![GitHub license](https://img.shields.io/github/license/Priytam/clock)](https://github.com/Priytam/alarm/blob/master/LICENSE)
 [![Build Status](https://travis-ci.org/Priytam/alarm.svg?branch=master)](https://travis-ci.org/Priytam/alarm)
 [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.priytam/alarm/badge.svg)](https://search.maven.org/search?q=g:com.github.priytam%20AND%20a:alarm)
 
 [![HitCount](http://hits.dwyl.io/Priytam/alarm.svg?style=svg)](http://hits.dwyl.io/Priytam/alarm)
 [![Open Source Love svg2](https://badges.frapsoft.com/os/v2/open-source.svg?v=103)](https://github.com/ellerbrock/open-source-badges/)
 [![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://GitHub.com/Naereen/StrapDown.js/graphs/commit-activity)


# Basic Example and Usage
  1. [AlarmClock](#alarmclock)
  1. [Scheduling](#scheduling)
  1. [Recurring](#recurring)
  1. [Task](#task)

  
Maven dependency  
```xml
<dependency>
  <groupId>com.github.priytam</groupId>
  <artifactId>clock</artifactId>
  <version>1.0.0</version>
</dependency>
```

Gradle dependency  
```groovy
compile 'com.github.priytam:clock:1.0.0'
```

## AlarmClock

```java
public class AlarmClockExample {
    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws InterruptedException {
        // Get and instance of clock
        AlarmClock clock = AlarmClock.getInstance("TestClock");

        //register to print 'ring ring' after 2 sec
        clock.register(2000, id -> System.out.println("ring ring"));

        //register to print 'ring ring1' after 3 sec
        clock.register(3000, id -> System.out.println("ring ring1"));

        //start this
        clock.start();

        //register to print 'ring ring2' after 2 sec
        clock.register(2000, id -> System.out.println("ring ring2"));

        //register to print 'ring ring3' after 2 sec
        clock.register(1000, id -> System.out.println("ring ring3"));

        //show all alarms set
        System.out.println(clock.getTaskList().showEntries());

        Thread.sleep( 6000);
        clock.shutdown();
    }
}

```
**Output**
```text
object = com.clock.AlarmClockHeartbeatTask@17550481, time remaining = 98
object = com.clock.example.AlarmClockExample$$Lambda$5/321142942@2c6a3f77, time remaining = 999
object = com.clock.example.AlarmClockExample$$Lambda$1/1032616650@2ed94a8b, time remaining = 1993
object = com.clock.example.AlarmClockExample$$Lambda$4/1935637221@180bc464, time remaining = 1999
object = com.clock.example.AlarmClockExample$$Lambda$2/649734728@5f2050f6, time remaining = 2995

ring ring3
ring ring
ring ring2
ring ring1

```
Useful Methods
- start/shutdown: start and stop clock
- register (long, registrants): register an task
- registerWeaK (long, registrants): register a weak task meaning in high load GC may clean this registry
- setNumberOfThreads (int): clock task(registrant) execution pool size, default 2
- setMaxPendingThreads (int): max pending task (registrant), default 4000
- setThrowOnLatencyViolations (boolean): will throw error if set true and there is latency in execution or else execute, default true
- setStatisticsLogger (IAlarmClockStatisticsLogger): to record stats
- getTaskList(): All Task entries registered

**[Back to top](#basic-example-and-usage)**

## Scheduling

```java
public class ScheduleExample {
    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }

    public static void main(String[] args) {
        //create a schedule
        Schedule helloWorldSchedule = new Schedule("HelloWorldSchedule");

        //register a task to print 'Hello world'
        helloWorldSchedule.register(() -> System.out.println("Hello world"));

        //will print 'Hello world' every day 5:10
        helloWorldSchedule.addEntry(new DailyScheduleEntry(5, 10));

        //will print 'Hello world' every hour 1' o clock , 2' o clock, 3' clock
        helloWorldSchedule.addEntry(new HourlyScheduleEntry(0, 0));

        //will print 'Hello world' every monday at 5:10
        helloWorldSchedule.addEntry(new WeeklyScheduleEntry(WeeklyScheduleEntry.MONDAY, 5, 10));

        //print schedule Status
        System.out.println(helloWorldSchedule.getStatus());

        //register another task to print hello Priytam and will run with all entries added above
        helloWorldSchedule.register(() -> System.out.println("Hello Priytam"));
        
        //shutdown
        helloWorldSchedule.shutDown();
    }
}
```

**output**
```text
	                HourlyEntry (0m:0s) 
				Next time will be at: Sun Jun 14 02:00:00 IST 2020
			DailyEntry (5h:10m:0s) 
				Next time will be at: Sun Jun 14 05:10:00 IST 2020
			WeeklyEntry (2d:5h:10m:0s) 
				Next time will be at: Mon Jun 15 05:10:00 IST 2020
```
Useful methods
- Schedule(String clockName, int poolSize) : with custom pool size, default size 10
- start/shutdown : start and stop schedule
- register (Runnable) : schedule a task for execution
- unregister (Runnable) : remove task from schedule
- addEntry (IScheduleEntry) : add an entry [HourlyEntry, DailyEntry, WeeklyEntry] to schedule for all registered tasks
- removeEntry (IScheduleEntry): remove an entry [HourlyEntry, DailyEntry, WeeklyEntry] from schedule for all registered tasks
- getScheduleEntries : List<IScheduleEntry> all entries in schedule
- getRegistrants : List<Runnable> all tasks registered in schedule
- getStatus(): String status of scheduled tasks
- unregisterAll() : unregister all task
- getNextExecutionTime() : next execution time
- getScheduleString() : schedule short info

**[Back to top](#basic-example-and-usage)**

## Recurring
```java
public class RecurringTaskExample {

    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }

    public static void main(String[] args) throws InterruptedException {
        recurringSyncTaskExecution();//default
        recurringAsyncTaskExecution();
    }

    private static void recurringSyncTaskExecution() throws InterruptedException {
        // Create a recurring task with interval 1000 ms by 
        // implementing 'performTask' and 'onTaskFailure'
        RecurringTask recurringTask = new RecurringTask(1000) {
            @Override
            public boolean performTask(Object context) {
                System.out.println("Performing task");;
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {
                System.out.println("Performing task failed");;
            }
        };

        //start recurring task
        recurringTask.start();
        Thread.sleep(4000);

        //stop  task
        recurringTask.stop();
    }
    
    private static void recurringAsyncTaskExecution() throws InterruptedException {
        // Create a recurring task with interval 1000 ms by 
        // implementing 'performTask' and 'onTaskFailure'
        RecurringTask recurringTask = new RecurringTask(1000) {
            @Override
            public boolean performTask(Object context) {
                System.out.println("Performing task");;
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {
                System.out.println("Performing task failed");;
            }
        };
        // set async task execution
        recurringTask.setTaskExecution(new AsyncTaskExecution(4));

        //start recurring task
        recurringTask.start();
        Thread.sleep(4000);
        //stop  task
        recurringTask.stop();
    }
}
```

```text
Performing task
Performing task
Performing task
Performing task
4005 [main] INFO com.clock.AlarmClock  - shutdown()
4008 [main] INFO com.clock.AlarmClock  - start() - starting clock Alarmclock
Performing task
Performing task
Performing task
Performing task
8011 [main] INFO com.clock.AlarmClock  - shutdown()
```
Useful methods
- RecurringTask(long wakeupIntervalMilli, boolean registerBeforeExecution, boolean runImmediately) :
    - (wakeupIntervalMilli) interval of recurrence 
    - (registerBeforeExecution) if true next occurrence will be registered before  executing current 
    - (runImmediately) if true will run first execution immediately 
- start/stop: to start and stop recurring task
- forceRestart: do not wait for running task
- setTaskExecution(TaskExecution): default is sync task execution [SyncTaskExecution, AsyncTaskExecution]
- setContext(Object): same object will be passed in perform task function


**Note:** if performTask() returns false then onTaskFailure() will be called

**[Back to top](#basic-example-and-usage)**

## Task

```java
public class TaskExample {
    static {
        LogManager.getRootLogger().setLevel(Level.INFO);
        BasicConfigurator.configure();
    }
    public static void main(String[] args) {
        // Create a task to execute after 1000 ms by 
        // implementing 'performTask' and 'onTaskFailure'
        new Task(1000) {
            @Override
            public boolean performTask(Object context) {
                System.out.println("Perform task");
                return true;
            }

            @Override
            public void onTaskFailure(Object context) {
                System.out.println("failed execution");
            }
        }.start();
    }
}

```
Useful methods
- start/stop: to start and stop recurring task
- setTaskExecution(TaskExecution): default is sync task execution [SyncTaskExecution, AsyncTaskExecution]
- forceRestart: do not wait for running task
- setContext(Object): same object will be passed in perform task function

**Note:** if performTask() returns false then onTaskFailure() will be called

**[Back to top](#basic-example-and-usage)**
