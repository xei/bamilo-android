package com.bamilo.android.core.presentation;

import com.bamilo.android.core.view.HomeView;

/**
 * Created on 12/20/2017.
 */

public interface HomePresenter extends BasePresenter<HomeView> {
    void loadHome(boolean isConnected);
}
