package com.mobile.di.components;

import com.bamilo.apicore.di.modules.ProfileModule;
import com.mobile.view.fragments.EditProfileFragment;

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
