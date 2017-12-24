package com.mobile.di.components;

import com.bamilo.apicore.di.modules.ApiModule;
import com.bamilo.apicore.di.modules.HomeModule;
import com.mobile.di.modules.SchedulerModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created on 12/20/2017.
 */
@Singleton
@Component(modules = {
        ApiModule.class,
        SchedulerModule.class
})
public interface MainComponent {
    HomeComponent plus(HomeModule homeModule);
}
