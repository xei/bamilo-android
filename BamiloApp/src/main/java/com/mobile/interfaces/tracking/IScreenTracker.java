package com.mobile.interfaces.tracking;

import com.mobile.view.BaseActivity;

import java.util.HashMap;

/**
 * Created by narbeh on 12/3/17.
 */

public interface IScreenTracker extends IBaseTracker {
    void trackScreen(BaseActivity activity, String screen);
}
