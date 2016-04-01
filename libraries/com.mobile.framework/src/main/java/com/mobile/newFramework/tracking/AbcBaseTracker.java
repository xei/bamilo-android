package com.mobile.newFramework.tracking;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Class used to set the base for all trackers.
 *
 * @author spereira
 */
public abstract class AbcBaseTracker {

    public String getId() {
        return "n.a.";
    }

    public String getId(@NonNull Context context) {
        return "n.a.";
    }

    public void debugMode(@NonNull Context context, boolean enable) {
        // ...
    }

}
