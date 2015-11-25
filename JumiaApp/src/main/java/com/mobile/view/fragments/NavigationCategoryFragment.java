package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewStub;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.LinearLayout;

import com.mobile.components.AnimatedExpandableListView;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.CategoriesListAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.categories.GetCategoriesHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.database.CategoriesTableHelper;
import com.mobile.newFramework.objects.category.Categories;
import com.mobile.newFramework.objects.category.Category;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.view.MainFragmentActivity;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * Class used to shoe the categories in the navigation container
 *
 * @author sergiopereira
 */
public class NavigationCategoryFragment extends BaseFragment implements IResponseCallback, OnGroupClickListener, OnChildClickListener {

    private static final String TAG = NavigationCategoryFragment.class.getSimpleName();

    private AnimatedExpandableListView mCategoryList;

    private ArrayList<Category> mCategories;


    /**
     * Create a new instance and save the bundle data
     */
    public static NavigationCategoryFragment getInstance(Bundle bundle) {
        NavigationCategoryFragment categoriesFragment = new NavigationCategoryFragment();
        categoriesFragment.setArguments(bundle);
        return categoriesFragment;
    }

    /**
     * Empty constructor as a nested fragment
     */
    public NavigationCategoryFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.navigation_fragment_categories);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get category list view
        mCategoryList = (AnimatedExpandableListView) view.findViewById(R.id.nav_sub_categories_grid);
        // Validation to show content
        if (mCategories != null && mCategories.size() > 0) {
            showCategoryList(mCategories);
        }
        // Case empty
        else if (!TextUtils.isEmpty(ShopSelector.getShopId())) {
            if (getBaseActivity() instanceof MainFragmentActivity && !((MainFragmentActivity) getBaseActivity()).isInMaintenance())
                triggerGetCategories();
        }
        // Case recover from background
        else {
            showRetry();
        }
    }

    /**
     * ######## LAYOUT ########
     */
    /**
     * Show the category list
     */
    private void showCategoryList(ArrayList<Category> categories) {
        // Case branch
        if (categories != null && categories.size() > 0) {
            showRootCategories(categories);
            // Show content
            showFragmentContentContainer();
        }
        // Case error
        else showRetry();

    }

    /**
     * Show the root categories
     */
    private void showRootCategories(final ArrayList<Category> categories) {
        Print.i(TAG, "ON SHOW ROOT CATEGORIES");
        CategoriesListAdapter mCategoryAdapter = new CategoriesListAdapter(getBaseActivity().getApplicationContext(), categories);
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.setOnGroupClickListener(this);
        mCategoryList.setOnChildClickListener(this);
    }

    /**
     * Show only the retry view
     *
     * @author sergiopereira
     */
    private void showRetry() {
        Print.i(TAG, "ON SHOW RETRY");
        showFragmentErrorRetry();
    }

    /**
     * ####### TRIGGERS #######
     */
    /**
     * Trigger to get categories
     */
    private void triggerGetCategories() {
        ContentValues contentValues = new ContentValues();
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, contentValues);
        // Trigger
        triggerContentEvent(new GetCategoriesHelper(), bundle, this);
    }

    /**
     * Show product list
     */
    private void goToCatalog(Category category) {
        // Update counter for tracking
        CategoriesTableHelper.updateCategoryCounter(category.getUrlKey(), category.getName());
        // Close navigation
        getBaseActivity().closeNavigationDrawer();
        // Create bundle for catalog
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, category.getApiUrl());
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, category.getName());
        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, category.getCategoryPath());
        bundle.putString(ConstantsIntentExtra.CATALOG_SOURCE, category.getType());
        // Goto Catalog
        getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * ####### RESPONSE EVENTS #######
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "ON SUCCESS EVENT");
        // Validate fragment state
        if (isOnStoppingProcess) return;
        // Get categories
        mCategories = (Categories) baseResponse.getMetadata().getData();
        if (CollectionUtils.isNotEmpty(mCategories)) {
            // Show categories
            showCategoryList(mCategories);
        } else {
            // Show retry
            showRetry();
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");
        // Validate fragment state
        if (isOnStoppingProcess) return;
        int errorCode = baseResponse.getError().getCode();
        if (errorCode == ErrorCode.TIME_OUT || errorCode == ErrorCode.NO_CONNECTIVITY) {
            showFragmentNoNetworkRetry();
        } else {
            showRetry();
        }
    }


    @Override
    protected void onInflateErrorLayout(ViewStub stub, View inflated) {
        super.onInflateErrorLayout(stub, inflated);
        // Set no network view
        ((TextView) inflated.findViewById(R.id.fragment_root_error_label)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.no_connection_label_small_size));
        ((TextView) inflated.findViewById(R.id.fragment_root_error_details_label)).setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.no_connection_label_details_small_size));
    }

    @Override
    protected void onInflateLoading(View inflated) {
        super.onInflateLoading(inflated);
        View loadingBar = inflated.findViewById(R.id.loading_bar);
        if (loadingBar != null) {
            android.view.ViewGroup.LayoutParams layoutParams = loadingBar.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            loadingBar.setLayoutParams(layoutParams);

        }

    }

    /**
     * ####### LISTENERS #######
     */

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        // Get categories from unexpected error
        triggerGetCategories();
    }

    /**
     * set behavior when clicking on a child
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
        Category category = (Category) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
        goToCatalog(category);
        return true;
    }

    /**
     * Set behavior when clicking on a group
     */
    @Override
    public boolean onGroupClick(ExpandableListView parent, View view, int groupPosition, long id) {
        CategoriesListAdapter adapter = (CategoriesListAdapter) parent.getExpandableListAdapter();
        Category category = (Category) adapter.getGroup(groupPosition);
        Print.i(TAG, "ON GROUP CLICKED:" + category.getName());
        // Case has sub categories
        if (category.hasChildren()) {
            if (parent.isGroupExpanded(groupPosition)) {
                adapter.updateIndicator(view, false);
                ((AnimatedExpandableListView) parent).collapseGroupWithAnimation(groupPosition);
            } else {
                adapter.updateIndicator(view, true);
                ((AnimatedExpandableListView) parent).expandGroupWithAnimation(groupPosition);
            }
        }
        // Case is not a section
        else if (!category.isSection()) {
            Print.i(TAG, "PARENT GO TO CATALOG:" + category.getApiUrl());
            goToCatalog(category);
        }
        return true;
    }

}
