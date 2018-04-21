package com.bamilo.apicore.presentation;

import com.bamilo.apicore.view.HomeView;

/**
 * Created on 12/20/2017.
 */

public interface HomePresenter extends BasePresenter<HomeView> {
    void loadHome(boolean isConnected);
}
