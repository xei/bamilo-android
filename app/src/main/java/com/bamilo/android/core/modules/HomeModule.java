package com.bamilo.android.core.modules;

import com.bamilo.android.core.interaction.HomeInteractor;
import com.bamilo.android.core.interaction.HomeInteractorImpl;
import com.bamilo.android.core.presentation.HomePresenter;
import com.bamilo.android.core.presentation.HomePresenterImpl;
import com.bamilo.android.core.view.HomeView;

import dagger.Module;
import dagger.Provides;

/**
 * Created on 12/20/2017.
 */

@Module
public class HomeModule {
    private HomeView homeView;

    public HomeModule(HomeView homeView) {
        this.homeView = homeView;
    }

    @Provides
    public HomeView provideHomeView() {
        return homeView;
    }

    @Provides
    public HomePresenter provideHomePresenter(HomePresenterImpl homePresenter) {
        homePresenter.setView(homeView);
        return homePresenter;
    }

    @Provides
    public HomeInteractor provideHomeInteractor(HomeInteractorImpl homeInteractor) {
        return homeInteractor;
    }
}
