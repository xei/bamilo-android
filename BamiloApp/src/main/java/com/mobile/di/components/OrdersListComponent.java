package com.mobile.di.components;

import com.bamilo.apicore.di.modules.OrdersListModule;
import com.mobile.view.fragments.order.MyOrdersFragment;

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
