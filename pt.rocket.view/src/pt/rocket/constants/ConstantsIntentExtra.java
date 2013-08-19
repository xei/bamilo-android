package pt.rocket.constants;



/**
 * The purpose of this class is to provide a central point to identify the intent extras used within the activities
 * This constants are user mainly in the ActivitiesWorkflow activity <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author sergiopereira
 *
 * @version 1.00
 * 
 */
public class ConstantsIntentExtra {
	//
	private final static String mPackage = "pt.rocket.view." ;
	
	//
	public static final String CATEGORY_INDEX = mPackage + "CategoryIndex";
	
    
    // ########### Search ########### 
    //    
    public static final String SEARCH_QUERY			= mPackage + "SearchStr";
	

    // ########### Products ########### 
    //    
    public static final String CONTENT_URL			= mPackage + "ContentUrl";	
    
    public static final String PRODUCT_URL         = mPackage + "ProductUrl";  
    
    // ########### Checkout ########### 
    //
	public static final String CONTENT_TITLE = mPackage + "ContentTitle";
	
	// ########### REVIEW ##############
	public static final String REVIEW_TITLE             = mPackage + "ReviewTitle";
	public static final String REVIEW_COMMENT           = mPackage + "ReviewComment";
	public static final String REVIEW_RATING            = mPackage + "ReviewRating";
	public static final String REVIEW_NAME              = mPackage + "ReviewName";
	public static final String REVIEW_DATE              = mPackage + "ReviewDate";
	
	
    public static final String NAVIGATION_PATH             = mPackage + "NavigationPath";
    public static final String NAVIGATION_SOURCE           = mPackage + "NavigationPrefix";
    
    // ########### Google Analytics: Campaign ##############
    public static final String UTM_STRING         = "UTM";

	
}
