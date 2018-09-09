package com.mobile.app;

import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;

import java.util.Set;

/**
 * Fake debug activity for release
 * @author sergiopereira
 */
public abstract class DebugActivity extends BaseActivity {

    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     */
    public DebugActivity(@NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, int titleResId) {
        super(action, enabledMenuItems, titleResId);
    }

}
