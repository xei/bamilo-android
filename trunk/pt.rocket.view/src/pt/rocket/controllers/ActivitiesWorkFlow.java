package pt.rocket.controllers;

import java.lang.ref.WeakReference;

import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.MyActivity;
import pt.rocket.view.CategoriesNewActivity;
import pt.rocket.view.ChangeCountryActivity;
import pt.rocket.view.CheckoutStep5Activity;
import pt.rocket.view.CheckoutWebActivity;
import pt.rocket.view.ForgotPasswordActivity;
import pt.rocket.view.InnerCategoriesActivity;
import pt.rocket.view.LogInActivity;
import pt.rocket.view.MyAccountActivity;
import pt.rocket.view.MyAccountUserData;
import pt.rocket.view.PopularityActivity;
import pt.rocket.view.ProductDetailsActivityNew;
import pt.rocket.view.ProductDetailsDescriptionActivity;
import pt.rocket.view.ProductsActivity;
import pt.rocket.view.ProductsGalleryActivityNew;
import pt.rocket.view.R;
import pt.rocket.view.RegisterActivity;
import pt.rocket.view.ReviewActivity;
import pt.rocket.view.SearchActivity;
import pt.rocket.view.ShoppingCartActivity;
import pt.rocket.view.SplashScreen;
import pt.rocket.view.TeaserActivity;
import pt.rocket.view.TermsActivity;
import pt.rocket.view.WriteReviewActivity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.View.OnClickListener;

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
	private final static String TAG = LogTagHelper.create(ActivitiesWorkFlow.class);
    private static Dialog dialog;
	
	public static void searchActivity(Activity activity) {

		// Create Intent
		Intent intent = new Intent(activity.getApplicationContext(),
				SearchActivity.class);
		intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);		
		activity.startActivity(intent);
		addStandardTransition(activity);
	}
	
	public static void splashActivityNewTask(Activity activity ) {

	    Intent intent = new Intent(activity.getApplicationContext(), SplashScreen.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        TaskStackBuilder.from(activity).addNextIntent(intent).startActivities();
//        activity.startActivity(intent);
        addStandardTransition(activity);
	}

	/**
	 * Start Register Activity
	 * 
	 * @param activity
	 * 
	 */
    public static void registerActivity(Activity activity) {
        Intent registerIntent = new Intent(
                activity.getApplicationContext(), RegisterActivity.class);
        activity.startActivityForResult(registerIntent, MyActivity.maskRequestCodeId(R.id.request_register));
        addStandardTransition(activity);
    }

	/**
	 * Start Login Activity validating Customer
	 * 
	 * @param activity
	 * @param requestResult TODO
	 */
    public static void loginActivity(Activity activity, boolean requestResult) {
        Intent loginIntent = new Intent(activity.getApplicationContext(),
                LogInActivity.class);
        if (requestResult) {
            activity.startActivityForResult(loginIntent, MyActivity.maskRequestCodeId(R.id.request_login));
        } else {
            activity.startActivity(loginIntent );
        }
        addStandardTransition(activity);
    }
    
    public static void loginOut(final Activity activity) {
        if(ServiceManager.SERVICES.get(CustomerAccountService.class).hasCredentials()) {
            dialog = new DialogGeneric(activity, false, true, false,
                    activity.getString(R.string.logout_title),
                    activity.getString(R.string.logout_text_question),
                    activity.getString(R.string.no_label), activity.getString(R.string.yes_label),
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (v.getId() == R.id.button2) {
                                LogOut.performLogOut(new WeakReference<Activity>(
                                        activity));
                            }
                            dialog.dismiss();
                        }

                    });

            dialog.show();
        } else {
            loginActivity(activity, false);
        }
    }
	/**
	 * Start Login Activity validating Customer
	 * 
	 * @param activity
	 */
	public static void changeCountryActivity(Activity activity) {
			Intent intent = new Intent(activity.getApplicationContext(),ChangeCountryActivity.class);
			intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
			activity.startActivity(intent);
			addStandardTransition(activity);
	}

	/**
	 * Starts the subcategories activity
	 * 
	 * @param activity
	 * @param categoryIndex
	 *            Indication of what is the parent category index
	 */
	public static void categorySubLisActivity(Activity activity,
			int categoryIndex) {
		Intent categoriesIntent = new Intent(activity.getApplicationContext(),
				InnerCategoriesActivity.class);
		categoriesIntent.putExtra(ConstantsIntentExtra.CATEGORY_INDEX, categoryIndex);
		categoriesIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity(categoriesIntent);
		addStandardTransition(activity);
	}

	/**
	 * Start Forgot Password Activity
	 * 
	 * @param activity
	 */
	public static void forgotPasswordActivity(Activity activity) {
		Intent loginIntent = new Intent(activity.getApplicationContext(),
				ForgotPasswordActivity.class);
		activity.startActivity(loginIntent);
		addStandardTransition(activity);
	}
	
	public static void descriptionActivity(Activity activity, String productUrl) {
		Intent descrIntent = new Intent(activity.getApplicationContext(),
				ProductDetailsDescriptionActivity.class);
		descrIntent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
		descrIntent.putExtra( ConstantsIntentExtra.CONTENT_URL, productUrl );
		activity.startActivity(descrIntent);
		addStandardTransition(activity);
	}

	/**
	 * Start the terms activity
	 * 
	 * @param activity
	 * @param terms
	 *            The terms text to display to the user
	 */
	public static void termsActivity(Activity activity, String terms) {
		Intent descrIntent = new Intent(activity.getApplicationContext(),
				TermsActivity.class);
		descrIntent.putExtra("terms_conditions", terms);
		activity.startActivity(descrIntent);
		addStandardTransition(activity);
	}
	
	/**
	 */
	public static void categoriesActivityNew(Activity activity, String categoryUrl) {
		Intent intent = new Intent(activity.getApplicationContext(),
				CategoriesNewActivity.class);
		intent.putExtra(ConstantsIntentExtra.CONTENT_URL, categoryUrl);
	    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity( intent );
		addStandardTransition(activity);
		
	}

	public static void reviewActivity(Activity activity, String title,
			String comment, String name, double rating, String date) {
		Intent intent = new Intent(activity.getApplicationContext(),
				ReviewActivity.class);
		intent.putExtra( ConstantsIntentExtra.REVIEW_TITLE, title);
		intent.putExtra( ConstantsIntentExtra.REVIEW_COMMENT, comment);
		intent.putExtra( ConstantsIntentExtra.REVIEW_NAME, name);
		intent.putExtra( ConstantsIntentExtra.REVIEW_RATING, rating);
		intent.putExtra( ConstantsIntentExtra.REVIEW_DATE, date);
		activity.startActivity(intent);
		addStandardTransition(activity);
	}
	
	/**
	 * Starts the products list activity
	 * 
	 * @param activity
	 * @param productsURL
	 *            The URL where to ger the product list
	 * @param navigationPath TODO
	 */
	public static void productsActivity(Activity activity, String productsURL, String title, String searchQuery, int navigationSource, String navigationPath ) {
		Intent intent = new Intent(activity.getApplicationContext(),
				ProductsActivity.class);
		intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(ConstantsIntentExtra.CONTENT_URL, productsURL);
		intent.putExtra(ConstantsIntentExtra.CONTENT_TITLE, title);
		intent.putExtra(ConstantsIntentExtra.SEARCH_QUERY, searchQuery);
		intent.putExtra(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
		intent.putExtra(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
		activity.startActivity(intent);
		addStandardTransition(activity);
	}
	
	/**
	 * Start Sales Products Activity
	 * 
	 * @param activity
	 */
	public static void SalesActivity(Activity activity) {

		Intent intent = new Intent(activity.getApplicationContext(),
				ProductsActivity.class);
		activity.startActivity(intent);
		addStandardTransition(activity);
	}

	/**
	 * Start WishList Activity
	 * 
	 * @param activity
	 */
//	public static void wishListActivity(Activity activity) {
//		Intent intent = new Intent(activity.getApplicationContext(),
//				WishListActivity.class);
//		activity.startActivity(intent);
//		addStandarTransition(activity);
//	}

	/**
	 * Start ShoppingCart Activity
	 * 
	 * @param activity
	 */
	public static void shoppingCartActivity(Activity activity) {
		Intent intent = new Intent(activity.getApplicationContext(),
				ShoppingCartActivity.class);
		activity.startActivity(intent);
		addStandardTransition(activity);
	}

	/**
	 * Starts the product details activity
	 * 
	 * @param activity
	 * @param productUrl
	 *            The URL of the product to display
	 */
	
	public static void productsDetailsActivity(Activity activity,
			String productUrl, int navigationSource, String navigationPath) {
		Intent intent = new Intent(activity.getApplicationContext(),
				ProductDetailsActivityNew.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		intent.putExtra(ConstantsIntentExtra.CONTENT_URL, productUrl);
		intent.putExtra(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
		intent.putExtra(ConstantsIntentExtra.NAVIGATION_PATH, navigationPath);
		activity.startActivity(intent);
		addStandardTransition(activity);
	}

	/**
	 * Start the product gallery activity
	 * 
	 * @param activity
	 */
	public static void productsGalleryActivity(Activity activity, String productUrl, int listPosition) {
		Intent intent = new Intent(activity.getApplicationContext(),
				ProductsGalleryActivityNew.class);
		intent.addFlags( Intent.FLAG_ACTIVITY_REORDER_TO_FRONT );
		intent.putExtra(ConstantsIntentExtra.CONTENT_URL, productUrl);
		intent.putExtra(ProductsGalleryActivityNew.EXTRA_CURRENT_LISTPOSITION, listPosition);
		activity.startActivityForResult(intent, ProductsGalleryActivityNew.REQUEST_CODE_GALLERY);
		addStandardTransition(activity);
	}

	/**
	 * Start HomePage Activity
	 * 
	 * @param activity
	 */
	public static void homePageActivity(Activity activity) {
		Intent intent = new Intent(activity.getApplicationContext(),
				TeaserActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		activity.startActivity(intent);
		addStandardTransition(activity);
	}

	/**
	 * Start MyAccount Activity
	 * 
	 * @param activity
	 */
	public static void myAccountActivity(Activity activity) {
	    Intent myAccountIntent = new Intent(
					activity.getApplicationContext(), MyAccountActivity.class);
		myAccountIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		activity.startActivity(myAccountIntent);
		addStandardTransition(activity);
	}

	/**
	 * Start MyAccountUserData Activity
	 * 
	 * @param activity
	 */
    public static void myAccountUserDataActivity(Activity activity) {
        Intent intent = new Intent(
                activity.getApplicationContext(), MyAccountUserData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        activity.startActivity(intent);
        addStandardTransition(activity);
    }

	/**
	 * Start Popularity Activity
	 * 
	 * @param activity
	 */
	public static void popularityActivity(Activity activity) {

		Intent popularityIntent = new Intent(activity.getApplicationContext(),
				PopularityActivity.class);
		activity.startActivity(popularityIntent);
		addStandardTransition(activity);
	}

	/**
	 * Start WriteReview Activity
	 * 
	 * @param activity
	 */
	public static void writeReviewActivity(Activity activity) {

		Intent popularityIntent = new Intent(activity.getApplicationContext(),
				WriteReviewActivity.class);
		activity.startActivity(popularityIntent);
		addStandardTransition(activity);
	}

	/**
	 * Start Checkout Activity
	 * 
	 * @param activity
	 */
	public static void checkoutActivity(Activity activity, int... params) {
		Intent intent = null;
		switch (params[0]) {
		case ConstantsCheckout.CHECKOUT_BASKET: // checkout step 1 (shopping
												// basket)
			// intent = new Intent(activity.getApplicationContext(),
			// CheckoutStep1Activity.class);
			// intent.putExtra(ConstantsIntentExtra.CHECKOUT_LOCKED, false);
			// intent.putExtra(ConstantsIntentExtra.CHECKOUT_SKIPPED, false);
			// intent.putExtra(ConstantsIntentExtra.CHECKOUT_SPINNER,
			// params[1]);
			// intent.putExtra(ConstantsIntentExtra.CHECKOUT_ADDRESS, -1);
			intent = new Intent(activity.getApplicationContext(),
					CheckoutWebActivity.class);
			break;
		case ConstantsCheckout.CHECKOUT_THANKS: // checkout step 5 ( thank you )
			intent = new Intent(activity.getApplicationContext(),
					CheckoutStep5Activity.class);
			break;
		}
		activity.startActivity(intent);
		addStandardTransition(activity);
	}
	
	public static void addStandardTransition(Activity activity) {
		activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

}
