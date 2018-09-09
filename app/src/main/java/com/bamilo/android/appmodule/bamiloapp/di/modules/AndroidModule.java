package com.bamilo.android.appmodule.bamiloapp.di.modules;

import android.content.Context;
import android.content.res.Resources;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;

import javax.inject.Singleton;

import dagger.*;

/**
 * Created on 12/24/2017.
 */

@Module
public class AndroidModule {
    private BamiloApplication application;

    public AndroidModule(BamiloApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application.getApplicationContext();
    }

    @Provides
    @Singleton
    Resources provideResources() {
        return application.getResources();
    }
}
