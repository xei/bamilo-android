package com.mobile.di.components;

import com.bamilo.apicore.di.modules.CatalogModule;
import com.mobile.view.fragments.CatalogFragment;

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
