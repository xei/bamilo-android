package com.mobile.interfaces.tracking;

import android.content.Context;

import com.mobile.classes.models.BaseScreenModel;

/**
 * Created by narbeh on 12/3/17.
 */

public interface IScreenTracker extends IBaseTracker {
    void trackScreenAndTiming(Context context, BaseScreenModel screenModel);

    void trackScreen(Context context, BaseScreenModel screenModel);

    void trackScreenTiming(Context context, BaseScreenModel screenModel);
}
