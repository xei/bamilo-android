package com.bamilo.android.appmodule.bamiloapp.di.components;

import com.bamilo.android.core.modules.OrdersListModule;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.order.MyOrdersFragment;

import dagger.Subcomponent;

/**
 * Created on 12/30/2017.
 */
@Subcomponent(modules = {
        OrdersListModule.class
})
public interface OrdersListComponent {
    void inject(MyOrdersFragment fragment);
}
