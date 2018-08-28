package com.bamilo.android.appmodule.bamiloapp.di.modules;

import com.bamilo.android.core.scheduler.SchedulerProvider;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created on 12/23/2017.
 */

@Module
public class SchedulerModule {

    @Provides
    public SchedulerProvider provideScheduler() {
        return new SchedulerProvider() {

            @Override
            public Scheduler getNetworkThreadScheduler() {
                return Schedulers.io();
            }

            @Override
            public Scheduler getMainThreadScheduler() {
                return AndroidSchedulers.mainThread();
            }
        };
    }
}
