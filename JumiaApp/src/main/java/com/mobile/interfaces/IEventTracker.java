package com.mobile.interfaces;

import com.mobile.view.BaseActivity;

import java.util.HashMap;

/**
 * Created by Narbeh M. on 4/26/17.
 */

public interface IEventTracker {
    void postEvent(BaseActivity activity, String event, HashMap<String, Object> attributes);
}
