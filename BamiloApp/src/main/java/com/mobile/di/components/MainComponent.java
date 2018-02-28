package com.mobile.di.components;

import com.bamilo.apicore.di.modules.CatalogModule;
import com.bamilo.apicore.di.modules.ItemTrackingModule;
import com.bamilo.apicore.di.modules.OrderCancellationModule;
import com.bamilo.apicore.di.modules.OrdersListModule;
import com.bamilo.apicore.di.modules.ProfileModule;
import com.bamilo.apicore.di.modules.RetrofitModule;
import com.bamilo.apicore.di.modules.HomeModule;
import com.mobile.di.modules.AndroidModule;
import com.mobile.di.modules.ApiModule;
import com.mobile.di.modules.SchedulerModule;
import com.mobile.view.fragments.OrderCancellationSuccessFragment;

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

    OrdersListComponent plus(OrdersListModule ordersListModule);

    ItemTrackingComponent plus(ItemTrackingModule itemTrackingModule);

    OrderCancellationComponent plus(OrderCancellationModule orderCancellationModule);

    ProfileComponent plus(ProfileModule profileModule);

    void inject(OrderCancellationSuccessFragment fragment);
}
