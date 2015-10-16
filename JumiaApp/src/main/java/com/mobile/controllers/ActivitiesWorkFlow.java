package com.mobile.controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.OverLoadErrorActivity;
import com.mobile.view.R;
import com.mobile.view.SplashScreenActivity;

/**
 * This Class is responsible by all the application workflow. Contains all
 * static methods used to start all the activities in the application.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Sergio Pereira
 * 
 * @version 1.01
 * 
 *          2012/06/19
 * 
 */
public class ActivitiesWorkFlow {
	protected final static String TAG = ActivitiesWorkFlow.class.getSimpleName();
    // private static DialogFragment dialog;
	

	
	public static void splashActivityNewTask(Activity activity ) {
	    Print.i(TAG, "START ACTIVITY: splashActivity");
	    Intent intent = new Intent(activity.getApplicationContext(), SplashScreenActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        addStandardTransition(activity);
	}
	
	/**
	 * Used to share.
	 */
	public static void startActivitySendString(Activity activity, String chooserText, String extraText){
	    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, extraText);
        activity.startActivity(Intent.createChooser(sharingIntent, chooserText));
	}

    public static void startActivityWebLink(@NonNull Activity activity, @NonNull String link){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
        activity.startActivity(browserIntent);
    }

    public static void startActivityWebLink(@NonNull Activity activity, @StringRes int link){
        startActivityWebLink(activity, activity.getString(link));
    }

	   /**
     * Start Login Activity validating Customer
     * 
     * @param activity
     */
//    public static void changeCountryActivity(Activity activity) {
//        Log.i(TAG, "START ACTIVITY: changeCountryActivity");
//        Intent intent = new Intent(activity.getApplicationContext(), ChangeCountryFragmentActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        activity.startActivity(intent);
//        addStandardTransition(activity);
//    }
    
    
    /**
     * Start HomePage Activity
     * 
     * @param activity
     */
//    public static void homePageActivity(Activity activity) {
//        // Intent intent = new Intent(activity.getApplicationContext(), TeaserFragmentActivity.class);
//        Intent intent = new Intent(activity.getApplicationContext(), MainFragmentActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        activity.startActivity(intent);
//        addStandardTransition(activity);
//    }
    
    
//    public static void homePageActivity(Activity activity, String productUrl, int navigationSource, String navigationPath) {
//        // Create bundle for fragment
//        Bundle bundle = new Bundle();
//        bundle.putString(ConstantsIntentExtra.CONTENT_URL, productUrl);
//        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
//        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
//        // Create intent with fragment type and bundle
//        Intent intent = new Intent(activity.getApplicationContext(), MainFragmentActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
//        intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.PRODUCT_DETAILS);
//        intent.putExtra(ConstantsIntentExtra.FRAGMENT_BUNDLE, bundle);
//        // Start activity
//        activity.startActivity(intent);
//        //addStandardTransition(activity);
//    }

    
    public static void addStandardTransition(Activity activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Shows server overload page
     */
    public static void showOverLoadErrorActivity(Activity activity){

        Intent intent = new Intent(activity.getApplicationContext(), OverLoadErrorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
	

/**
 * ##################### OLD #####################
 */

	// public static void searchActivity(Activity activity) {
//  // Create Intent
//  // Intent intent = new Intent(activity.getApplicationContext(),SearchActivity.class);
//  Intent intent = new Intent(activity.getApplicationContext(),SearchFragmentActivity.class);
//  intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//  activity.startActivity(intent);
//  addStandardTransition(activity);
//}
	
	
//	/**
//	 * Start Register Activity
//	 * 
//	 * @param activity
//	 * 
//	 */
//    public static void registerActivity(Activity activity) {
//        Intent registerIntent = new Intent(
//                activity.getApplicationContext(), RegisterActivity.class);
//        activity.startActivityForResult(registerIntent, BaseActivity.maskRequestCodeId(R.id.request_register));
//        addStandardTransition(activity);
//    }

//	/**
//	 * Start Login Activity validating Customer
//	 * 
//	 * @param activity
//	 * @param requestResult
//	 */
//    public static void loginActivity(Activity activity, boolean requestResult) {
//        
//        Intent loginIntent = new Intent(activity.getApplicationContext(), SessionFragmentActivity.class);
//        //Intent loginIntent = new Intent(activity.getApplicationContext(), LogInActivity.class);
//        
//        if (requestResult) {
//            activity.startActivityForResult(loginIntent, BaseActivity.maskRequestCodeId(R.id.request_login));
//        } else {
//            activity.startActivity(loginIntent );
//        }
//        addStandardTransition(activity);
//    }
    
//    public static void loginOut(final FragmentActivity activity) {
//        if(ServiceManager.SERVICES.get(CustomerAccountService.class).hasCredentials()) {
//            FragmentManager fm = activity.getSupportFragmentManager();
//            dialog = DialogGenericFragment.newInstance(false, true, false,
//                    activity.getString(R.string.logout_title),
//                    activity.getString(R.string.logout_text_question),
//                    activity.getString(R.string.no_label), activity.getString(R.string.yes_label),
//                    new OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            if (v.getId() == R.id.button2) {
//                                LogOut.performLogOut(new WeakReference<Activity>(activity));
//                            }
//                            dialog.dismiss();
//                        }
//
//                    });
//
//            dialog.show(fm, null);
//        } else {
//            loginActivity(activity, false);
//        }
//    }


//	/**
//	 * Starts the subcategories activity
//	 * 
//	 * @param activity
//	 * @param categoryIndex
//	 *            Indication of what is the parent category index
//	 */
//	public static void categorySubLisActivity(Activity activity,int categoryIndex) {
//		Intent categoriesIntent = new Intent(activity.getApplicationContext(), InnerCategoriesActivity.class);
//		categoriesIntent.putExtra(ConstantsIntentExtra.CATEGORY_INDEX, categoryIndex);
//		categoriesIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		activity.startActivity(categoriesIntent);
//		addStandardTransition(activity);
//	}
//
//	/**
//	 * Start Forgot Password Activity
//	 * 
//	 * @param activity
//	 */
//	public static void forgotPasswordActivity(Activity activity) {
//		Intent loginIntent = new Intent(activity.getApplicationContext(),
//				ForgotPasswordActivity.class);
//		activity.startActivity(loginIntent);
//		addStandardTransition(activity);
//	}
//	
//	public static void descriptionActivity(Activity activity, String productUrl) {
//		Intent descrIntent = new Intent(activity.getApplicationContext(),
//				ProductDetailsDescriptionActivityFragment.class);
//		descrIntent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
//		descrIntent.putExtra( ConstantsIntentExtra.CONTENT_URL, productUrl );
//		activity.startActivity(descrIntent);
//		addStandardTransition(activity);
//	}
//
//	/**
//	 * Start the terms activity
//	 * 
//	 * @param activity
//	 * @param terms
//	 *            The terms text to display to the user
//	 */
//	public static void termsActivity(Activity activity, String terms) {
//		Intent descrIntent = new Intent(activity.getApplicationContext(),
//				TermsActivityFragment.class);
//		descrIntent.putExtra("terms_conditions", terms);
//		activity.startActivity(descrIntent);
//		addStandardTransition(activity);
//	}
//	
//	/**
//	 */
//	public static void categoriesActivityNew(Activity activity, String categoryUrl) {
//	    
//		//Intent intent = new Intent(activity.getApplicationContext(),CategoriesNewActivity.class);
//		Intent intent = new Intent(activity.getApplicationContext(), CategoriesFragmentActivity.class);
//		
//		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		intent.putExtra(ConstantsIntentExtra.CONTENT_URL, categoryUrl);
//		activity.startActivity( intent );
//		addStandardTransition(activity);
//		
//	}
//
//	public static void reviewActivity(Activity activity, String title, String comment, String name, double rating, String date) {
//		//Intent intent = new Intent(activity.getApplicationContext(),ReviewActivity.class);
//		Intent intent = new Intent(activity.getApplicationContext(),ReviewFragmentActivity.class);
//		intent.putExtra( ConstantsIntentExtra.REVIEW_TITLE, title);
//		intent.putExtra( ConstantsIntentExtra.REVIEW_COMMENT, comment);
//		intent.putExtra( ConstantsIntentExtra.REVIEW_NAME, name);
//		intent.putExtra( ConstantsIntentExtra.REVIEW_RATING, rating);
//		intent.putExtra( ConstantsIntentExtra.REVIEW_DATE, date);
//		activity.startActivity(intent);
//		addStandardTransition(activity);
//	}
//	
//	/**
//	 * Starts the products list activity
//	 * 
//	 * @param activity
//	 * @param productsURL
//	 *            The URL where to ger the product list
//	 * @param navigationPath 
//	 */
//	public static void productsActivity(Activity activity, String productsURL, String title, String searchQuery, int navigationSource, String navigationPath ) {
//		Intent intent = new Intent(activity.getApplicationContext(), ProductsActivityFragment.class);
//		//intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		intent.putExtra(ConstantsIntentExtra.CONTENT_URL, productsURL);
//		intent.putExtra(ConstantsIntentExtra.CONTENT_TITLE, title);
//		intent.putExtra(ConstantsIntentExtra.SEARCH_QUERY, searchQuery);
//		intent.putExtra(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
//		intent.putExtra(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
//		activity.startActivity(intent);
//		addStandardTransition(activity);
//	}
//	
//	/**
//	 * Start Sales Products Activity
//	 * 
//	 * @param activity
//	 */
//	public static void SalesActivity(Activity activity) {
//
//		Intent intent = new Intent(activity.getApplicationContext(),
//				ProductsActivityFragment.class);
//		activity.startActivity(intent);
//		addStandardTransition(activity);
//	}
//
//	/**
//	 * Start WishList Activity
//	 * 
//	 * @param activity
//	 */
////	public static void wishListActivity(Activity activity) {
////		Intent intent = new Intent(activity.getApplicationContext(),
////				WishListActivity.class);
////		activity.startActivity(intent);
////		addStandarTransition(activity);
////	}
//
//	/**
//	 * Start ShoppingCart Activity
//	 * 
//	 * @param activity
//	 */
//	public static void shoppingCartActivity(Activity activity) {
//		//Intent intent = new Intent(activity.getApplicationContext(), ShoppingCartActivity.class);
//		Intent intent = new Intent(activity.getApplicationContext(), ShoppingCartFragmentActivity.class);
//		activity.startActivity(intent);
//		addStandardTransition(activity);
//	}
//
//	/**
//	 * Starts the product details activity
//	 * 
//	 * @param activity
//	 * @param productUrl
//	 *            The URL of the product to display
//	 */
//	
//	public static void productsDetailsActivity(Activity activity, String productUrl, int navigationSource, String navigationPath) {
//		//Intent intent = new Intent(activity.getApplicationContext(), ProductDetailsActivityFragment.class);
//		
////		Intent intent = new Intent(activity.getApplicationContext(), ProductDetailsFragmentActivity.class);
////		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
////		intent.putExtra(ConstantsIntentExtra.CONTENT_URL, productUrl);
////		intent.putExtra(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
////		intent.putExtra(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
//		
//		Bundle bundle = new Bundle();
//		bundle.putString(ConstantsIntentExtra.CONTENT_URL, productUrl);
//		bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
//		bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
//		
//		
//        Intent intent = new Intent(activity.getApplicationContext(), HomeFragmentActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        intent.putExtra(ConstantsIntentExtra.FRAGMENT_TYPE, FragmentType.PRODUCT_DETAILS);
//        intent.putExtra(ConstantsIntentExtra.FRAGMENT_BUNDLE, bundle);
////        intent.putExtra(ConstantsIntentExtra.CONTENT_URL, productUrl);
////        intent.putExtra(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
////        intent.putExtra(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
//
//        activity.startActivity(intent);
//        addStandardTransition(activity);
//	}
//
//	/**
//	 * Start the product gallery activity
//	 * 
//	 * @param activity
//	 */
//	public static void productsGalleryActivity(Activity activity, String productUrl, int listPosition) {
//		Intent intent = new Intent(activity.getApplicationContext(),
//				ProductsGalleryActivityFragment.class);
//		intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
//		intent.putExtra(ConstantsIntentExtra.CONTENT_URL, productUrl);
//		intent.putExtra(ProductsGalleryActivityFragment.EXTRA_CURRENT_LISTPOSITION, listPosition);
//		activity.startActivityForResult(intent, ProductsGalleryActivityFragment.REQUEST_CODE_GALLERY);
//		addStandardTransition(activity);
//	}



//	/**
//	 * Start MyAccount Activity
//	 * 
//	 * @param activity
//	 */
//	public static void myAccountActivity(Activity activity) {
//	    Intent myAccountIntent = new Intent(
//					activity.getApplicationContext(), MyAccountActivityFragment.class);
//		myAccountIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//		activity.startActivity(myAccountIntent);
//		addStandardTransition(activity);
//	}
//
//	/**
//	 * Start MyAccountUserData Activity
//	 * 
//	 * @param activity
//	 */
//    public static void myAccountUserDataActivity(Activity activity) {
//        //Intent intent = new Intent(activity.getApplicationContext(), MyAccountUserDataActivityFragment.class);
//        Intent intent = new Intent(activity.getApplicationContext(), MyAccountUserDataFragmentActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        activity.startActivity(intent);
//        addStandardTransition(activity);
//    }
//
//	/**
//	 * Start Popularity Activity
//	 * 
//	 * @param activity
//	 */
//	public static void popularityActivity(Activity activity) {
//
//	    Intent popularityIntent = new Intent(activity.getApplicationContext(),PopularityFragmentActivity.class);
//		//Intent popularityIntent = new Intent(activity.getApplicationContext(),PopularityActivity.class);
//		activity.startActivity(popularityIntent);
//		addStandardTransition(activity);
//	}
//
//	/**
//	 * Start WriteReview Activity
//	 * 
//	 * @param activity
//	 */
//	public static void writeReviewActivity(Activity activity) {
//
//		// Intent popularityIntent = new Intent(activity.getApplicationContext(),WriteReviewActivity.class);
//		Intent popularityIntent = new Intent(activity.getApplicationContext(),WriteReviewFragmentActivity.class);
//		activity.startActivity(popularityIntent);
//		addStandardTransition(activity);
//	}
//
//	/**
//	 * Start Checkout Activity
//	 * 
//	 * @param activity
//	 */
//	public static void checkoutActivity(Activity activity, int... params) {
//		Intent intent = null;
//		switch (params[0]) {
//		case ConstantsCheckout.CHECKOUT_BASKET: // checkout step 1 (shopping
//												// basket)
//			// intent = new Intent(activity.getApplicationContext(),
//			// CheckoutStep1Activity.class);
//			// intent.putExtra(ConstantsIntentExtra.CHECKOUT_LOCKED, false);
//			// intent.putExtra(ConstantsIntentExtra.CHECKOUT_SKIPPED, false);
//			// intent.putExtra(ConstantsIntentExtra.CHECKOUT_SPINNER,
//			// params[1]);
//			// intent.putExtra(ConstantsIntentExtra.CHECKOUT_ADDRESS, -1);
//			intent = new Intent(activity.getApplicationContext(),
//					CheckoutWebActivityFragment.class);
//			break;
//		case ConstantsCheckout.CHECKOUT_THANKS: // checkout step 5 ( thank you )
//			// intent = new Intent(activity.getApplicationContext(), CheckoutStep5Activity.class);
//			intent = new Intent(activity.getApplicationContext(), CheckoutStep5FragmentActivity.class);
//			break;
//		}
//		activity.startActivity(intent);
//		addStandardTransition(activity);
//	}
	
