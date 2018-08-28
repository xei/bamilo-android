package com.bamilo.android.appmodule.bamiloapp.di.components;

import com.bamilo.android.core.modules.CatalogModule;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.CatalogFragment;

import dagger.Subcomponent;

/**
 * Created on 12/25/2017.
 */

@Subcomponent(modules = {
        CatalogModule.class
})
public interface CatalogComponent {
    void inject(CatalogFragment catalogFragment);
}
