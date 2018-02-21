package com.bamilo.apicore.di.modules;

import com.bamilo.apicore.interaction.HomeInteractor;
import com.bamilo.apicore.interaction.HomeInteractorImpl;
import com.bamilo.apicore.presentation.HomePresenter;
import com.bamilo.apicore.presentation.HomePresenterImpl;
import com.bamilo.apicore.view.HomeView;

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
