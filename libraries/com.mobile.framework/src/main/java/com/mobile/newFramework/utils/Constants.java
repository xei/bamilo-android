package com.mobile.newFramework.utils;

import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;

/**
 * Class that contains all static constants utilized in the framework
 *
 * @author josedourado
 */
public class Constants {

    /**
     * Bundle Info Constants - keys used in identifying the bundle parameters used to pass information around
     */
    public static final String BUNDLE_END_POINT_KEY = RestConstants.END_POINT;
    public static final String BUNDLE_DATA_KEY = RestConstants.DATA;
    public static final String BUNDLE_ARRAY_KEY = "array"
    public static final String BUNDLE_PATH_KEY = RestConstants.PATH;
    public static final String BUNDLE_PRIORITY_KEY = RestConstants.PRIORITY;
    public static final String BUNDLE_EVENT_TASK = RestConstants.TASK;

    /**
     * Handler messages
     */

    public static final int SUCCESS = IntConstants.SUCCESS;

    /**
     * ######## Device Info ########
     */
    public static final String INFO_PRE_INSTALL = RestConstants.PRE_INSTALL;
    public static final String INFO_BRAND = RestConstants.BRAND;
    public static final String INFO_SIM_OPERATOR = RestConstants.SIM_OPERATOR;
    public static final String INFO_BUNDLE_VERSION = RestConstants.BUNDLE_VERSION;

    /**
     * Shared Preferences
     */

    public static final String SHARED_PREFERENCES = "whitelabel_prefs";
}
