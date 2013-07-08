package pt.rocket.view;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.SubCategoriesAdapter;
import pt.rocket.framework.components.ExpandableGridViewComponent;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetCategoriesEvent;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class shows all sub categories of a given activity.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. Written by josedourado, 30/06/2012.
 * </p>
 * 
 */

public class InnerCategoriesActivity extends BaseActivity implements OnItemClickListener {
	private final static String TAG = LogTagHelper.create( InnerCategoriesActivity.class );
	
	private int categoryIndex = 0;
	private List<Category> categories;
	private List<Category> parent;
	private ArrayList<Category> child;
	private Category currentCategory;
	private SubCategoriesAdapter catAdapter;
	private ListView categoriesList;
	private Activity activity;
	private int subLevel = 0;
	private int previousPosition = 0;

	private long beginRequestMillis;
	
	/**
	 * 
	 */
	public InnerCategoriesActivity() {
		super(NavigationAction.Categories,
		        EnumSet.of(MyMenuItem.SEARCH),
		        EnumSet.of(EventType.GET_CATEGORIES_EVENT),
		        EnumSet.noneOf(EventType.class),
		        0, R.layout.categories_inner_container);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
	    setAppContentLayout();
		init( getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
        init( intent );
	}
	
	private void init( Intent intent) {
	    categoryIndex = intent.getIntExtra(ConstantsIntentExtra.CATEGORY_INDEX, -1);
	    categories = null;
	    parent = null;
	    child = null;
	    currentCategory = null;
	    subLevel = 0;
	    previousPosition = 0;
	    setSubCategories();
	}

	/**
	 * Set the ShoppingCart layout using inflate
	 */
	public void setAppContentLayout() {
		categoriesList = (ListView) activity
                .findViewById(R.id.sub_categories_grid);

	}
	
    private void setSubCategories() {
        beginRequestMillis = System.currentTimeMillis();
        triggerContentEvent(GetCategoriesEvent.ALL);
    }

	public void onDestroy() {
		super.onDestroy();
		activity = null;
		unbindDrawables(findViewById(R.id.root_subcategories));
		System.gc();
	}

	public void setSubCategoryList() {
		parent = categories;
		child = categories.get(categoryIndex).getChildren();

		if (subLevel > 0) {
			Log.d(TAG, "setSubCategoryList: entering backwards subLevel "
					+ subLevel + " previousPosition " + previousPosition);
			currentCategory = child.get(previousPosition);
			child = currentCategory.getChildren();
			Log.d( TAG, "setSubCategoryList: currentCategory name = " + currentCategory.getName());			
			String categoryTitle = currentCategory.getName();
			Log.d( TAG, "setSubCategoryList: currentCategory name = " + categoryTitle );
			setTitle(currentCategory.getName());
			catAdapter.setAdapterData(child, categoryTitle);
		} else {
			Log.d(TAG, "setSubCategoryList: entering forward: categoryIndex = "
					+ categoryIndex);
			currentCategory = parent.get(categoryIndex);
			String categoryTitle = currentCategory.getName();
			Log.d( TAG, "setSubCategoryList: currentCategory name = " + categoryTitle );
			setTitle(categoryTitle);
			catAdapter = new SubCategoriesAdapter(activity, child, 
					categoryTitle);
			categoriesList.setAdapter(catAdapter);
			categoriesList.setOnItemClickListener(this);
		}
		if (parent.get(categoryIndex).getHasChildren()) {
			parent = parent.get(categoryIndex).getChildren();
		}		
	}

//	private void requestAllProductsForCategory(String category) {
//		String categoryPath = currentCategory.getCategoryPath();
//		Log.d(TAG,
//				"requestAll: currentCategory name = "
//						+ currentCategory.getName() + " path = " + categoryPath + " urlkey = " + currentCategory.getUrlKey());
//
//		ActivitiesWorkFlow.productsActivity(activity, currentCategory.getId(),
//				currentCategory.getName(), categoryPath, R.string.gcategory);
//
//	}
	
	private void showProducts( Category category ) {
		ActivitiesWorkFlow.productsActivity(activity, category.getApiUrl(), category.getName(), null, R.string.gcategory_prefix, category.getCategoryPath());
	}

	private void requestSubcategory(int pos) {
		// This condition verifies if we are in the root of the
		// category chosen

		if (subLevel < 1 && parent.get(pos).getHasChildren()) {
			// printCategories(parent, "parent");
			child = parent.get(pos).getChildren();
			printCategories(child, "child");
			previousPosition = pos;

			currentCategory = parent.get(pos);
			String categoryTitle = currentCategory.getName();
			catAdapter.setAdapterData(child, categoryTitle);

			setTitle(categoryTitle);
			subLevel++;
			startAnimation();
			Log.v(TAG, "CLIQUE HERE " + subLevel);
			
		}

		else if (child.size() >= 1 && pos < child.size()
				&& !child.get(pos).getHasChildren()) {
			Log.v(TAG, " GO TO SUB LIST " + subLevel);
			showProducts(child.get(pos));
		} else if (subLevel == 0 && !parent.get(pos).getHasChildren()) {
		    showProducts( parent.get(pos));
		}

	}

	/**
	 * This method starts the animation when a sub-level of a sub category is
	 * called
	 */
	public void startAnimation() {
		TranslateAnimation a = new TranslateAnimation(500,
				Animation.RELATIVE_TO_SELF, 0, 0);
		a.setDuration(300);
		a.setFillAfter(true);
		a.setFillEnabled(true);

		Animation fadeIn = new AlphaAnimation(0, 1);
		fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
		fadeIn.setDuration(300);

		AnimationSet animation = new AnimationSet(false); // change to false
		animation.addAnimation(fadeIn);
		animation.addAnimation(a);

		categoriesList.setAnimation(animation);

	}

	/**
	 * This method is called when the back button is clicked to simulate a
	 * reverse of the first animation
	 */
	public void reverseAnimation() {
		final TranslateAnimation a = new TranslateAnimation(-500, Animation.RELATIVE_TO_SELF, 0, 0);
		a.setDuration(300);

		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new DecelerateInterpolator()); // add this
		fadeOut.setDuration(300);

		fadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// noop
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// noop
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				categoriesList.startAnimation(a);
				displayParentCategory();
			}
		});

		// categoriesList.setAnimation(animation);
		categoriesList.startAnimation(fadeOut);

	}

	private void displayParentCategory() {
		currentCategory = categories.get(categoryIndex);
		Log.d(TAG, "displayParentCategory: currentCategory name = "
				+ currentCategory.getName() + " categoryIndex = "
				+ categoryIndex);
		String categoryTitle = currentCategory.getName();
		catAdapter.setAdapterData(parent, categoryTitle);
		setTitle(categoryTitle);
		subLevel--;

		Log.d(TAG, "updateCategories: subLevel " + subLevel);
	}

	@Override
	public void onBackPressed() {

		if (subLevel > 0) {
			reverseAnimation();
			return;
		} else {
			super.onBackPressed();
		}
	}



	/**
	 * This method prints all categories of a given level
	 * 
	 * @param categories
	 *            - arraylist of categories
	 * @param level
	 *            - desired level
	 */
	void printCategories(ArrayList<Category> categories, String level) {
		Log.d(TAG, "categories: level = " + level);
		for (int i = 0; i < categories.size(); i++) {
			Log.i(TAG, "categories: id = " + categories.get(i).getId()
					+ " name = " + categories.get(i).getName());
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
		if (pos == 0) {
			showProducts(currentCategory);
		} else {
			requestSubcategory(pos - 1);
		}		
	}

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        AnalyticsGoogle.get().trackLoadTiming(R.string.gcategories, beginRequestMillis);
        categories = (List<Category>) event.result;
        setSubCategoryList();
        return true;
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

}
