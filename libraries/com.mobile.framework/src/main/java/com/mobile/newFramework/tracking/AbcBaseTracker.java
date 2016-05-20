package com.mobile.newFramework.tracking;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobile.newFramework.pojo.IntConstants;

/**
 * Class used to set the base for all trackers.
 *
 * @author spereira
 */
public abstract class AbcBaseTracker {

    public static final String NOT_AVAILABLE = "n.a.";
    protected final String SEPARATOR_CARET = "^";
    protected final String SEPARATOR_HYPHEN = "-";
    protected final int CONFIG_SKU_POS = IntConstants.DEFAULT_POSITION;

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
