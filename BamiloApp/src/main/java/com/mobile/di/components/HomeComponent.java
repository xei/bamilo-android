package com.mobile.di.components;

import com.bamilo.apicore.di.modules.HomeModule;
import com.mobile.view.fragments.HomeFragment;

import dagger.Subcomponent;

/**
 * Created on 12/20/2017.
 */
@Subcomponent(modules = {
        HomeModule.class
})
public interface HomeComponent {
    void inject(HomeFragment homeFragment);
}
