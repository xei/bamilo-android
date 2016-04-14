package com.mobile.newFramework.tracking;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Class used to set the base for all trackers.
 *
 * @author spereira
 */
public abstract class AbcBaseTracker {

    protected static final String NOT_AVAILABLE = "n.a.";

    public String getId() {
        return NOT_AVAILABLE;
    }

    public String getId(@NonNull Context context) {
        return NOT_AVAILABLE;
    }

    public void debugMode(@NonNull Context context, boolean enable) {
        // ...
    }

}
