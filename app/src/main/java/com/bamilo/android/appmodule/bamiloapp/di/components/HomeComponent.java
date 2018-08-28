package com.bamilo.android.appmodule.bamiloapp.di.components;

import com.bamilo.android.core.modules.HomeModule;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.HomeFragment;

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
