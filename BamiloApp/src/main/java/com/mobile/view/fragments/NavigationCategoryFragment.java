package com.mobile.view.fragments;

import android.content.ActivityNotFoundException;
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

import com.mobile.adapters.NewCategoriesListAdapter;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.components.AnimatedExpandableListView;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.CategoriesListAdapter;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.GetExternalLinksHelper;
import com.mobile.helpers.categories.GetCategoriesHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.database.CategoriesTableHelper;
import com.mobile.service.objects.ExternalLinks;
import com.mobile.service.objects.ExternalLinksSection;
import com.mobile.service.objects.category.Categories;
import com.mobile.service.objects.category.Category;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.ShopSelector;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.view.MainFragmentActivity;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListView;
import pl.openrnd.multilevellistview.OnItemClickListener;

/**
 * Class used to shoe the categories in the navigation container
 *
 * @author sergiopereira
 */
public class NavigationCategoryFragment extends BaseFragment implements IResponseCallback, OnGroupClickListener, OnChildClickListener, TargetLink.OnAppendDataListener {

    private static final String TAG = NavigationCategoryFragment.class.getSimpleName();

    private View mPartialErrorView;

    private Categories mCategories;
    private Categories mCategories2;
    private ExternalLinksSection mExternalLinksSection;
    private Category mCategory;
    private MultiLevelListView mCategoryListNew;
    private boolean pageTracked = false;
    private EventType errorType;


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
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.BASKET, MyMenuItem.SEARCH_VIEW),
                NavigationAction.CATEGORIES,
                R.layout.navigation_fragment_categories,
                R.string.categories_fragment_title,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CATEGORY_MENU.getName()), getString(R.string.gaScreen), "", getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get category list view

        mCategoryListNew = (MultiLevelListView) view.findViewById(R.id.nav_sub_categories_grid);
        // AnimatedExpandableListView mCategoryList = (AnimatedExpandableListView) view.findViewById(R.id.nav_sub_categories_grid2);
        // Get Error view to retry categories load
        mPartialErrorView = view.findViewById(R.id.partial_error_button);
        // Validation to show content
        if (mCategories != null && mCategories.size() > 0) {
            showCategoryList(mCategories, mExternalLinksSection);
        } else
            // Case empty
            if (!TextUtils.isEmpty(ShopSelector.getShopId())) {
                if (getBaseActivity() instanceof MainFragmentActivity && !((MainFragmentActivity) getBaseActivity()).isInMaintenance()) {
                    triggerGetExternalLinksSection();
                    triggerGetCategories();
                }

            }
            // Case recover from background
            else {
                showRetry();
            }
        mCategoryListNew.setClipToPadding(false);
        getBaseActivity().enableSearchBar(false, mCategoryListNew);
    }

    /**
     * ######## LAYOUT ########
     */
    /**
     * Show the category list
     */
    /*private void showCategoryList(Categories categories, ExternalLinksSection externalLinksSection) {
        // Case No categories are retrieved
        if (CollectionUtils.isNotEmpty(categories)) {
            hidePartialRetry();
            showRootCategories(categories);

            // Show content
            showFragmentContentContainer();
        }
        // Case error
        else {
            showRetry();
        }
    }*/
    private void showCategoryList(Categories categories, ExternalLinksSection externalLinksSection) {
        // Case No categories are retrieved but we have some External Links Section to show
        if (CollectionUtils.isNotEmpty(categories) && (errorType == EventType.GET_EXTERNAL_LINKS || (externalLinksSection != null && CollectionUtils.isNotEmpty(externalLinksSection.getExternaLinks())))) {
            if (externalLinksSection != null && CollectionUtils.isNotEmpty(externalLinksSection.getExternaLinks())) {
                hidePartialRetry();
                Category externalSection = new Category();
                externalSection.setName(mExternalLinksSection.getLabel());
                externalSection.markAsSection();
                int position = mExternalLinksSection.getPosition();
                if (CollectionUtils.isEmpty(categories.getMainCategoryIndexMapping())) {
                    position = 0;
                } else if (CollectionUtils.isNotEmpty(categories.getMainCategoryIndexMapping()) && categories.getMainCategoryIndexMapping().size() >= mExternalLinksSection.getPosition() && mExternalLinksSection.getPosition() > 0) {
                    position = categories.getMainCategoryIndexMapping().get(mExternalLinksSection.getPosition() - 1);
                } else if (CollectionUtils.isNotEmpty(categories.getMainCategoryIndexMapping()) && mExternalLinksSection.getPosition() > 0) {
                    position = categories.size();
                }

                categories.add(position, externalSection);

                for (ExternalLinks externalLinks : mExternalLinksSection.getExternaLinks()) {
                    position++;
                    Category externalLink = new Category();
                    externalLink.setIsExternalLinkType(true);
                    externalLink.setName(externalLinks.getLabel());
                    externalLink.setTargetLink(externalLinks.getLink());
                    externalLink.setImage(externalLinks.getImage());
                    categories.add(position, externalLink);
                }

            }


            showRootCategories(categories);

            // Show content
            showFragmentContentContainer();
        }
    }

    /**
     * Show the root categories
     */
    /*private void showRootCategories(final ArrayList<Category> categories) {
        Print.i(TAG, "ON SHOW ROOT CATEGORIES "+categories.size());
        CategoriesListAdapter mCategoryAdapter = new CategoriesListAdapter(getBaseActivity().getApplicationContext(), categories);
        mCategoryList.setAdapter(mCategoryAdapter);
        mCategoryList.setOnGroupClickListener(this);
        mCategoryList.setOnChildClickListener(this);
    }*/
    private void showRootCategories(final ArrayList<Category> categories) {
        setCategoryLevel(categories, 0);
        Print.i(TAG, "ON SHOW ROOT CATEGORIES " + categories.size());
        NewCategoriesListAdapter mCategoryAdapter = new NewCategoriesListAdapter(getBaseActivity());
        mCategoryListNew.setAdapter(mCategoryAdapter);
        mCategoryAdapter.setDataItems(categories);
        mCategoryListNew.setOnItemClickListener(mOnItemClickListener);
        //mCategoryListNew.setOnGroupClickListener(this);
        //mCategoryListNew.setOnChildClickListener(this);
    }

    private void setCategoryLevel(ArrayList<Category> categories, int level) {
        for (Category category : categories
                ) {
            category.setLevel(level);
            if (category.hasChildren()) {
                setCategoryLevel(category.getChildren(), level + 1);
            }
        }

    }


    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
            Category category = (Category) item;
            if (category.isExternalLinkType()) {
                // Open External Link
                try {
                    ActivitiesWorkFlow.startExternalWebActivity(getBaseActivity(), category.getTargetLink(), category.getName());
                } catch (ActivityNotFoundException e) {
                    showUnexpectedErrorWarning();
                }

            } else {
                if (!category.isSection()) {
                    goToCatalog(category);
                }
            }


        }

        @Override
        public void onGroupItemClicked(MultiLevelListView parent, View view, Object item, ItemInfo itemInfo) {
        }
    };

    /**
     * Show only the retry view
     * Show the partial error view in case we have External Links Section
     * Retry generic view otherwise
     *
     * @author sergiopereira
     */
    private void showRetry() {
        Print.i(TAG, "ON SHOW RETRY");
        if (CollectionUtils.isEmpty(mCategories)) {
            showFragmentErrorRetry();
        }
    }

    /**
     * Hide Partial Retry View
     */
    private void hidePartialRetry() {
        mPartialErrorView.setVisibility(View.GONE);
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
        triggerContentEventNoLoading(new GetExternalLinksHelper(), null, this);
    }

    private void triggerGetExternalLinksSection() {
        triggerContentEventNoLoading(new GetExternalLinksHelper(), null, this);
    }

    /**
     * Show product list
     */
    private void goToCatalog(Category category) {
        // Update counter for tracking
        CategoriesTableHelper.updateCategoryCounter(category.getUrlKey(), category.getName());
        // Close navigation
        getBaseActivity().closeNavigationDrawer();
        mCategory = category;
        @TargetLink.Type String link = category.getTargetLink();

        // Parse target link
        new TargetLink(getWeakBaseActivity(), link)
                .addTitle(category.getName())
                .addAppendListener(this)
                .enableWarningErrorMessage()
                .retainBackStackEntries()
                .run();
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
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_CATEGORIES_EVENT:
                // Get categories
                mCategories = (Categories) baseResponse.getContentData();


                if (CollectionUtils.isNotEmpty(mCategories)) {
                    // Show categories
                    showCategoryList(mCategories, mExternalLinksSection);


                } else {
                    // Show retry
                    showRetry();
                }

                if (!pageTracked) {
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CATEGORIES.getName()), getString(R.string.gaScreen), "", getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                    pageTracked = true;
                }

                break;
            case GET_EXTERNAL_LINKS:
                mExternalLinksSection = (ExternalLinksSection) baseResponse.getContentData();
                // Show categories
                showCategoryList(mCategories, mExternalLinksSection);
                break;
        }

    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");

        errorType = baseResponse.getEventType();

        // Validate fragment state
        if (isOnStoppingProcess) return;
        switch (errorType) {
            case GET_EXTERNAL_LINKS:
                return;
        }

        // Check base errors
        if (!super.handleErrorEvent(baseResponse)) {
            showFragmentErrorRetry();
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
        if (category.isExternalLinkType()) {
            // Open External Link
            try {
                ActivitiesWorkFlow.startExternalWebActivity(getBaseActivity(), category.getTargetLink(), category.getName());
            } catch (ActivityNotFoundException e) {
                showUnexpectedErrorWarning();
            }

        } else {
            goToCatalog(category);
        }

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
                ((AnimatedExpandableListView) parent).collapseGroupWithAnimation(groupPosition);
            } else {
                ((AnimatedExpandableListView) parent).expandGroupWithAnimation(groupPosition);
            }
        }
        // Case is not a section
        else if (!category.isSection()) {
            Print.i(TAG, "PARENT GO TO CATALOG:" + category.getTargetLink());
            if (category.isExternalLinkType()) {
                // Open External Link
                try {
                    ActivitiesWorkFlow.startExternalWebActivity(getBaseActivity(), category.getTargetLink(), category.getName());
                } catch (ActivityNotFoundException e) {
                    showUnexpectedErrorWarning();
                }
            } else {
                goToCatalog(category);
            }
        }
        return true;
    }

    @Override
    public void onAppendData(FragmentType next, String title, String id, Bundle data) {
        // Create bundle for catalog
        data.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
        data.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
        data.putString(ConstantsIntentExtra.NAVIGATION_PATH, mCategory.getCategoryPath());
        data.putString(RestConstants.MAIN_CATEGORY, mCategory.getMainCategory());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.partial_error_button) {
            // Get categories from unexpected error
            triggerGetCategories();
        } else {
            super.onClick(view);
        }

    }
}
