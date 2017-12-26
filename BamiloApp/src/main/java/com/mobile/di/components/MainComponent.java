package com.mobile.di.components;

import com.bamilo.apicore.di.modules.CatalogModule;
import com.bamilo.apicore.di.modules.RetrofitModule;
import com.bamilo.apicore.di.modules.HomeModule;
import com.mobile.di.modules.AndroidModule;
import com.mobile.di.modules.ApiModule;
import com.mobile.di.modules.SchedulerModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created on 12/20/2017.
 */
@Singleton
@Component(modules = {
        AndroidModule.class,
        ApiModule.class,
        RetrofitModule.class,
        SchedulerModule.class
})
public interface MainComponent {
    HomeComponent plus(HomeModule homeModule);

    CatalogComponent plus(CatalogModule catalogModule);
}
