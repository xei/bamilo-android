package com.bamilo.android.core.scheduler;

import rx.Scheduler;

/**
 * Created on 12/20/2017.
 */

public interface SchedulerProvider {
    Scheduler getNetworkThreadScheduler();
    Scheduler getMainThreadScheduler();
}
