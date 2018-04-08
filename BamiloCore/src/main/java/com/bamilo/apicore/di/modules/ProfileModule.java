package com.bamilo.apicore.di.modules;

import com.bamilo.apicore.interaction.ProfileInteractor;
import com.bamilo.apicore.interaction.ProfileInteractorImpl;
import com.bamilo.apicore.presentation.ProfilePresenter;
import com.bamilo.apicore.presentation.ProfilePresenterImpl;
import com.bamilo.apicore.view.ProfileView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mohsen on 2/27/18.
 */
@Module
public class ProfileModule {
    private ProfileView view;

    public ProfileModule(ProfileView view) {
        this.view = view;
    }

    @Provides
    public ProfileView provideProfileView() {
        return view;
    }

    @Provides
    public ProfilePresenter provideProfilePresenter(ProfilePresenterImpl profilePresenter) {
        profilePresenter.setView(view);
        return profilePresenter;
    }

    @Provides
    public ProfileInteractor provideProfileInteractor(ProfileInteractorImpl profileInteractor) {
        return profileInteractor;
    }
}
