package com.bamilo.android.appmodule.bamiloapp.di.components;

import com.bamilo.android.core.modules.ProfileModule;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.EditProfileFragment;

import dagger.Subcomponent;

/**
 * Created by mohsen on 2/27/18.
 */
@Subcomponent(modules = {
        ProfileModule.class
})
public interface ProfileComponent {
    void inject(EditProfileFragment fragment);
}
