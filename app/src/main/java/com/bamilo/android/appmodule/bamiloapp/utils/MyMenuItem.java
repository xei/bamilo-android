package com.bamilo.android.appmodule.bamiloapp.utils;

import com.bamilo.android.R;

/**
 * <p>
 * This enum is used to represent menu items to be displayed in activities
 * </p>
 * <p>
 * Its uses the RocketCategoriesGenerator
 * </p>
 * <p>
 * <p>
 * Copyright (C) 2013 Smart Mobile Factory GmbH - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * </p>
 *
 * @author Ralph Holland-Moritz
 * @version 1.00
 * @project WhiteLabelRocket
 * @date 3/22/2013
 * @description This Class shows the home page teasers.
 */
public enum MyMenuItem {

    /* SHARE(R.id.menu_share), */
    BASKET(R.id.menu_basket),
    BASKET_INDICATOR(R.id.menu_basket_indicator),
    SEARCH_VIEW(R.id.menu_search),
    MY_PROFILE(R.id.menu_myprofile),
    HIDE_AB(-1),
    UP_BUTTON_BACK(-1),
    CLOSE_BUTTON(-1);

    public final int resId;

    MyMenuItem(int resId) {
        this.resId = resId;
    }

}
