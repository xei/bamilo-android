package com.bamilo.android.core.modules;

import com.bamilo.android.core.interaction.ProfileInteractor;
import com.bamilo.android.core.interaction.ProfileInteractorImpl;
import com.bamilo.android.core.presentation.ProfilePresenter;
import com.bamilo.android.core.presentation.ProfilePresenterImpl;
import com.bamilo.android.core.view.ProfileView;

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
