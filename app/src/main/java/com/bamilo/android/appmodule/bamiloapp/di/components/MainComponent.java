package com.bamilo.android.appmodule.bamiloapp.di.components;

import com.bamilo.android.core.modules.CatalogModule;
import com.bamilo.android.core.modules.ItemTrackingModule;
import com.bamilo.android.core.modules.OrderCancellationModule;
import com.bamilo.android.core.modules.OrdersListModule;
import com.bamilo.android.core.modules.ProfileModule;
import com.bamilo.android.core.modules.RetrofitModule;
import com.bamilo.android.core.modules.HomeModule;
import com.bamilo.android.appmodule.bamiloapp.di.modules.AndroidModule;
import com.bamilo.android.appmodule.bamiloapp.di.modules.ApiModule;
import com.bamilo.android.appmodule.bamiloapp.di.modules.SchedulerModule;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.OrderCancellationSuccessFragment;

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
