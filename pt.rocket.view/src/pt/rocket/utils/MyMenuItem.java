package pt.rocket.utils;

import pt.rocket.view.R;

/**
 * 
 * <p>
 * This enum is used to represent menu items to be displayed in activities
 * </p>
 * <p>
 * Its uses the RocketCategoriesGenerator
 * </p>
 * 
 * <p>
 * Copyright (C) 2013 Smart Mobile Factory GmbH - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 * </p>
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.00
 * 
 * @author Ralph Holland-Moritz
 * 
 * 
 * @date 3/22/2013
 * 
 * @description This Class shows the home page teasers.
 * 
 */
public enum MyMenuItem {

	/*SHARE(R.id.menu_share), */SEARCH_VIEW(R.id.menu_search), MY_PROFILE(R.id.menu_myprofile);

	public final int resId;

	MyMenuItem(int resId) {
		this.resId = resId;
	}

}
