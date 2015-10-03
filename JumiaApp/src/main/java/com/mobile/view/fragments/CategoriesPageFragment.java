//package com.mobile.view.fragments;
//
//import android.app.Activity;
//import android.content.ContentValues;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ListView;
//
//import com.mobile.components.customfontviews.TextView;
//import com.mobile.constants.ConstantsIntentExtra;
//import com.mobile.controllers.CategoriesAdapter;
//import com.mobile.controllers.SubCategoriesAdapter;
//import com.mobile.controllers.fragments.FragmentController;
//import com.mobile.controllers.fragments.FragmentType;
//import com.mobile.helpers.categories.GetCategoriesPerLevelsHelper;
//import com.mobile.interfaces.IResponseCallback;
//import com.mobile.newFramework.database.CategoriesTableHelper;
//import com.mobile.newFramework.objects.category.Category;
//import com.mobile.newFramework.utils.CollectionUtils;
//import com.mobile.newFramework.utils.Constants;
//import com.mobile.newFramework.utils.output.Print;
//import com.mobile.newFramework.utils.shop.ShopSelector;
//import com.mobile.view.R;
//
//import java.util.ArrayList;
//
///**
// * Class used to show the categories in the main container
// * @author sergiopereira
// */
//public class CategoriesPageFragment extends BaseFragment implements OnItemClickListener, IResponseCallback {
//
//    private static final String TAG = CategoriesPageFragment.class.getSimpleName();
//
//    private static final int NUMBER_OF_HEADERS = 1;
//
//    private static final int HEADER_FOR_ALL_POSITION = 0;
//
//    private static final int NUMBER_OF_HEADERS_LANDSCAPE = 2;
//
//    private static final int HEADER_FOR_ALL_POSITION_LANDSCAPE = 1;
//
//    private static final int HEADER_FOR_BACK_POSITION_LANDSCAPE = 0;
//
//    private static final int NO_SELECTED_LANDSCAPE_CATEGORY = 0;
//
//    private static final int CLICK_FROM_DEFAULT_CONTAINER = 0;
//
//    private static final int CLICK_FROM_LANDSCAPE_CONTAINER = 1;
//
//    private static final Object ROOT_CATEGORIES = null;
//
//    private ListView mCategoryList;
//
//    private LayoutInflater mInflater;
//
//    private String mParentCategoryName;
//
//    private ListView mLandscapeCategoryChildrenList;
//
//    private int mSelectedParentPosition;
//
//    private String mTitleCategory;
//
//    private View mLandscapeLoading;
//
//    private String mCategoryKey;
//
//    private ArrayList<Category> mCategories;
//
//    private Category mCurrentSubCategory;
//
//    /**
//     * Create a new instance and save the bundle data
//     * @param bundle The arguments
//     * @return NavigationCategoryFragment
//     * @author sergiopereira
//     */
//    public static CategoriesPageFragment getInstance(Bundle bundle) {
//        CategoriesPageFragment fragment = new CategoriesPageFragment();
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
//    /**
//     * Empty constructor
//     */
//    public CategoriesPageFragment() {
//        super(IS_NESTED_FRAGMENT, R.layout.categories_page_fragment);
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
//     */
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Print.i(TAG, "ON ATTACH");
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Print.i(TAG, "ON CREATE");
//        // Get data from saved instance or from arguments
//        Bundle bundle = (savedInstanceState != null) ? savedInstanceState : getArguments();
//        // Get data
//        if(bundle != null) {
//            Print.i(TAG, "ON GET DATA FROM BUNDLE");
//            // Save data
//            mCategoryKey = bundle.getString(ConstantsIntentExtra.CATEGORY_ID);
//            mParentCategoryName = bundle.getString(ConstantsIntentExtra.CATEGORY_PARENT_NAME);
//            mSelectedParentPosition = bundle.getInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX);
//            mTitleCategory = bundle.getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
//            Print.i(TAG, "ON LOAD SAVED STATE: " + mParentCategoryName + " " + mSelectedParentPosition);
//        }
//        // Case bundle null
//        else Print.w(TAG, "WARNING: SOMETHING IS WRONG BUNDLE IS NULL ON CREATE");
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
//     */
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Print.i(TAG, "ON VIEW CREATED");
//        // Get inflater
//        mInflater = LayoutInflater.from(getBaseActivity());
//        // Get category list view
//        mCategoryList = (ListView) view.findViewById(R.id.sub_categories_grid);
//        // Get child category list view (landscape)
//        mLandscapeCategoryChildrenList = (ListView) view.findViewById(R.id.sub_categories_grid_right);
//        // Get loading (landscape)
//        mLandscapeLoading = view.findViewById(R.id.loading_bar);
//
//        // Validation to show content
//        // Case cache
//        if (mCategories != null && mCategories.size() > 0) showCategoryList(mCategories);
//        // Case empty
//        else if(!TextUtils.isEmpty(ShopSelector.getShopId())) triggerGetCategories(mCategoryKey);
//        // Case recover from background
//        else { Print.w(TAG, "APPLICATION IS ON BIND PROCESS"); showRetry(); }
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onStart()
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//        Print.i(TAG, "ON START");
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onResume()
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        Print.i(TAG, "ON RESUME");
//        // Set title
//        getBaseActivity().setTitle(TextUtils.isEmpty(mTitleCategory) ? getString(R.string.categories_title) : mTitleCategory);
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
//     */
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        Print.i(TAG, "ON SAVE INSTANCE");
//        outState.putString(ConstantsIntentExtra.CATEGORY_ID, mCategoryKey);
//        outState.putString(ConstantsIntentExtra.CATEGORY_PARENT_NAME, mParentCategoryName);
//        outState.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, mSelectedParentPosition);
//        outState.putString(ConstantsIntentExtra.CATEGORY_TREE_NAME, mTitleCategory);
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.view.fragments.MyFragment#onPause()
//     */
//    @Override
//    public void onPause() {
//        super.onPause();
//        Print.i(TAG, "ON PAUSE");
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.view.fragments.MyFragment#onStop()
//     */
//    @Override
//    public void onStop() {
//        super.onStop();
//        Print.i(TAG, "ON STOP");
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onDestroyView()
//     */
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Print.i(TAG, "ON DESTROY VIEW");
//    }
//
//    /**
//     * ######## LAYOUT ########
//     */
//    /**
//     * Show the category list for current level
//     * @author sergiopereira
//     */
//    private void showCategoryList(ArrayList<Category> categories) {
//        // Case root
//        if(isRootCategory()) showRootCategories(categories);
//        // Case branch
//        else if(categories != null && categories.size() > 0) showSubCategory(categories.get(0));
//        // Case error
//        else showRetry();
//        // Show content
//        showFragmentContentContainer();
//    }
//
//    /**
//     * Show the root categories without headers
//     * @author sergiopereira
//     */
//    private void showRootCategories(ArrayList<Category> categories) {
//        Print.i(TAG, "ON SHOW ROOT CATEGORIES: " + mSelectedParentPosition);
//        // Container
//        CategoriesAdapter mCategoryAdapter = new CategoriesAdapter(getBaseActivity(), categories);
//        mCategoryList.setTag(CLICK_FROM_DEFAULT_CONTAINER);
//        mCategoryList.setAdapter(mCategoryAdapter);
//        mCategoryList.setOnItemClickListener(this);
//        // Validate and fill the landscape container
//        if(isPresentLandscapeContainer()) triggerGetCategoriesLandscape(categories.get(mSelectedParentPosition).getUrlKey());
//    }
//
//    /**
//     * Show the nested categories with respective headers
//     * @author sergiopereira
//     */
//    private void showSubCategory(Category category) {
//        Print.i(TAG, "ON SHOW NESTED CATEGORIES");
//        try {
//            // Get data
//            mCurrentSubCategory = category;
//            ArrayList<Category> child = category.getChildren();
//            //String categoryName = category.getName();
//            // Set adapter, tag and listener
//            SubCategoriesAdapter mSubCategoryAdapter = new SubCategoriesAdapter(getBaseActivity(), child, category);
//            mCategoryList.setTag(CLICK_FROM_DEFAULT_CONTAINER);
//            mCategoryList.setAdapter(mSubCategoryAdapter);
//            mCategoryList.setOnItemClickListener(this);
//            // Validate and fill the landscape container
//            if(isPresentLandscapeContainer()) triggerGetCategoriesLandscape(child.get(mSelectedParentPosition).getUrlKey());
//
//        } catch (NullPointerException e) {
//            Print.w(TAG, "WARNING NPE ON SHOW NESTED CATEGORIES: GOTO ROOT CATEGORIES");
//            //goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
//        } catch (IndexOutOfBoundsException e) {
//            Print.w(TAG, "WARNING IOE ON SHOW NESTED CATEGORIES: GOTO ROOT CATEGORIES");
//            //goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
//        }
//    }
//
//    /**
//     * Create a header using a layout with R.id.text
//     * @param layout The header layout
//     * @param text The header text
//     * @return View
//     * @author sergiopereira
//     */
//    private View createHeader(int layout, String text){
//        View headerForAll = mInflater.inflate(layout, null);
//        ((TextView) headerForAll.findViewById(R.id.text)).setText(text);
//        return headerForAll;
//    }
//
//    /**
//     * Show only the retry view
//     * @author sergiopereira
//     */
//    private void showRetry() {
//        showFragmentErrorRetry();
//    }
//
//    /**
//     * ############# LANDSCAPE LAYOUT #############
//     */
//
//    /**
//     * Method used to validate if the landscape container is present or not
//     * @return true/false
//     * @author sergiopereira
//     */
//    private boolean isPresentLandscapeContainer(){
//        return mLandscapeCategoryChildrenList != null;
//    }
//
//    /**
//     * Method used to fill the landscape container with the sub categories from selected category in the default container.
//     * @param category The category object
//     * @author sergiopereira
//     */
//    private void showChildrenInLandscape(Category category) {
//        // Create and add the header for back
//        if(mLandscapeCategoryChildrenList.getHeaderViewsCount() == 0 && !isRootCategory()) {
//            View headerForBack = createHeader(R.layout.category_inner_top_back, getString(R.string.back_label));
//            mLandscapeCategoryChildrenList.addHeaderView(headerForBack);
//        }
//        // Set adapter, tag and listener
//        SubCategoriesAdapter adapter = new SubCategoriesAdapter(getBaseActivity(), category.getChildren(), category);
//        mLandscapeCategoryChildrenList.setAdapter(adapter);
//        mLandscapeCategoryChildrenList.setTag(CLICK_FROM_LANDSCAPE_CONTAINER);
//        mLandscapeCategoryChildrenList.setOnItemClickListener(this);
//    }
//
//    /**
//     * Show the landscape loading
//     * @author sergiopereira
//     */
//    private void showLandscapeLoading() {
//        // Show inner loading
//        mLandscapeLoading.setVisibility(View.VISIBLE);
//    }
//
//    /**
//     * Hide the landscape loading
//     * @author sergiopereira
//     */
//    private void hideLandscapeLoading() {
//        // Hide inner loading
//        mLandscapeLoading.setVisibility(View.GONE);
//    }
//
//    /**
//     * ####### TRIGGERS #######
//     */
//    /**
//     * Trigger to get categories
//     * @author sergiopereira
//     */
//    private void triggerGetCategories(String categoryKey) {
//        Print.i(TAG, "GET CATEGORY PER LEVEL: " + categoryKey);
//
//        // Get per levels
//        ContentValues contentValues = new ContentValues();
//
//        contentValues.put(GetCategoriesPerLevelsHelper.PAGINATE_KEY, GetCategoriesPerLevelsHelper.PAGINATE_ENABLE);
//        // Get category
//        if(!TextUtils.isEmpty(categoryKey)) contentValues.put(GetCategoriesPerLevelsHelper.CATEGORY_KEY, categoryKey);
//        // Create bundle
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, contentValues);
//        // Trigger
//        triggerContentEvent(new GetCategoriesPerLevelsHelper(), bundle, this);
//    }
//
//    /**
//     * Trigger to get sub categories for landscape container.
//     * @param categoryKey url key
//     * @author sergiopereira
//     */
//    private void triggerGetCategoriesLandscape(String categoryKey) {
//        Print.i(TAG, "GET CATEGORY PER LEVEL: " + categoryKey);
//        // Show inner loading
//        showLandscapeLoading();
//
//        ContentValues contentValues = new ContentValues();
//
//        // Get category per levels
//        contentValues.put(GetCategoriesPerLevelsHelper.PAGINATE_KEY, GetCategoriesPerLevelsHelper.PAGINATE_ENABLE);
//        contentValues.put(GetCategoriesPerLevelsHelper.CATEGORY_KEY, categoryKey);
//
//        // Create bundle
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, contentValues);
//        // Trigger
//        triggerContentEventNoLoading(new GetCategoriesPerLevelsHelper(), bundle, new IResponseCallback() {
//
//            @Override
//            public void onRequestError(Bundle bundle) {
//                // Validate fragment state
//                if (isOnStoppingProcess) return;
//                // Hide loading
//                hideLandscapeLoading();
//            }
//
//            @Override
//            public void onRequestComplete(Bundle bundle) {
//                // Validate fragment state
//                if (isOnStoppingProcess) return;
//                // Get categories
//                ArrayList<Category> categories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
//                // Show categories and hide loading
//                showChildrenInLandscape(CollectionUtils.isNotEmpty(categories) ? categories.get(0) : new Category());
//                hideLandscapeLoading();
//            }
//        });
//    }
//
//    /**
//     * ####### LISTENERS #######
//     */
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
//     */
//    @Override
//    protected void onClickRetryButton(View view) {
//        super.onClickRetryButton(view);
//        Print.d(TAG, "ON CLICK RETRY");
//        triggerGetCategories(mCategoryKey);
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
//     * android.view.View, int, long)
//     */
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Print.d(TAG, "ON ITEM CLICKED: " + position + " TAG:" + parent.getTag());
//        try {
//            // Get tag
//            int from = (Integer) parent.getTag();
//            // Validation
//            switch (from) {
//            case CLICK_FROM_DEFAULT_CONTAINER:
//                Print.i(TAG, "CLICK_FROM_DEFAULT_CONTAINER");
//                // Case root
//                if(isRootCategory()){
//                    onClickRootCategory(parent, position);
//                }
//                // Case branch or leaf
//                else {
//                    onClickNestedCategory(parent, position);
//                }
//                break;
//            case CLICK_FROM_LANDSCAPE_CONTAINER:
//                Print.i(TAG, "CLICK_FROM_LANDSCAPE_CONTAINER");
//                // Case root level from landscape without back button
//                if(isRootCategory()){
//                    onClickSubCategoryWithoutBack(parent, position);
//                }
//                // Case other level from landscape with back button
//                else {
//                    onClickSubCategoryWithBack(parent, position);
//                }
//                break;
//            default:
//                Print.w(TAG, "WARNING: UNKNOWN CLICK");
//                break;
//            }
//        } catch (ClassCastException e) {
//            Print.w(TAG, "WARNING CCE ON CLICK ITEM, THE TAG MUST BE AN INTEGER", e);
//        } catch (NullPointerException e) {
//            Print.w(TAG, "WARNING NPE ON CLICK ITEM, THE TAG IS NULL", e);
//        }
//    }
//
//    /**
//     * Process the click on a root category.
//     * In landscape shows the sub categories in the right container.
//     * @param parent The adapter
//     * @param position The position
//     * @author sergiopereira
//     */
//    private void onClickRootCategory(AdapterView<?> parent, int position) {
//        try {
//            // Validate item
//            mSelectedParentPosition = position;
//            // Get category
//            Category category = (Category) parent.getAdapter().getItem(position);
//            // Show product list
//            if (!category.hasChildren()) gotoCatalog(category);
//            // Show sub level in landscape container
//            else if (isPresentLandscapeContainer()) triggerGetCategoriesLandscape(category.getUrlKey());
//            // Show sub level
//            else gotoNestedCategories(category.getName(), category.getUrlKey(), NO_SELECTED_LANDSCAPE_CATEGORY);
//        } catch (NullPointerException e) {
//            Print.w(TAG, "WARNING NPE ON CLICK ROOT CATEGORY POS: " + position);
//        }
//    }
//
//    /**
//     * Process the click on a nested category.<br>
//     * In landscape shows the sub categories in the right container.
//     * @param parent The adapter
//     * @param position The position
//     * @author sergiopereira
//     */
//    private void onClickNestedCategory(AdapterView<?> parent, int position) {
//        try {
//            // Validate position
//            switch (position) {
//            case HEADER_FOR_ALL_POSITION:
//                // Second header goes to all
//                gotoCatalog(mCurrentSubCategory);
//                break;
//            default:
//                // Get real position
//                mSelectedParentPosition = position - NUMBER_OF_HEADERS;
//                // Validate item goes to product list or a sub level
//                Category selectedCategory = (Category) parent.getAdapter().getItem(position);
//                // Show product list
//                if (!selectedCategory.hasChildren()) gotoCatalog(selectedCategory);
//                // Show sub level in landscape container
//                else if (isPresentLandscapeContainer()) triggerGetCategoriesLandscape(selectedCategory.getUrlKey());
//                // Show sub level
//                else gotoNestedCategories(selectedCategory.getName(), selectedCategory.getUrlKey() , NO_SELECTED_LANDSCAPE_CATEGORY);
//                break;
//            }
//        } catch (NullPointerException e) {
//            Print.w(TAG, "WARNING NPE ON CLICK NESTED CATEGORY POS: " + position, e);
//        } catch (IndexOutOfBoundsException e) {
//            Print.w(TAG, "WARNING IOE ON CLICK NESTED CATEGORY", e);
//        }
//    }
//
//
//    /**
//     * Process the click on a category in landscape container.<br>
//     * The behavior it's like {@link #onClickNestedCategory(AdapterView, int)}.
//     * @param parent The adapter
//     * @param position The position
//     * @author sergiopereira
//     */
//    private void onClickSubCategoryWithoutBack(AdapterView<?> parent, int position) {
//        try {
//            // Validate position
//            switch (position) {
//            case HEADER_FOR_ALL_POSITION:
//                // Second header goes to all
//                gotoCatalog(mCategories.get(mSelectedParentPosition));
//                break;
//            default:
//                // Get real position
//                int pos = position - NUMBER_OF_HEADERS;
//                // Validate item goes to product list or a sub level
//                Category selectedCategory = (Category) parent.getAdapter().getItem(position);
//                // Show product list
//                if (!selectedCategory.hasChildren()) gotoCatalog(selectedCategory);
//                // Show sub level
//                else gotoNestedCategories(selectedCategory.getParent().getName(), selectedCategory.getParent().getUrlKey(), pos);
//                break;
//            }
//        } catch (NullPointerException e) {
//            Print.w(TAG, "WARNING: NPE ON CLICK NESTED CATEGORY IN LANDSCAPE POS: " + position, e);
//        } catch (IndexOutOfBoundsException e) {
//            Print.w(TAG, "WARNING IOE ON CLICK NESTED CATEGORY IN LANDSCAPE", e);
//        }
//    }
//
//    /**
//     * Proccess the click on a category in landscape container.<br>
//     * This layout contains two header, BACK and ALL.
//     * @param parent The adapter
//     * @param position The position
//     * @author sergiopereira
//     */
//    private void onClickSubCategoryWithBack(AdapterView<?> parent, int position) {
//        try {
//            // Validate position
//            switch (position) {
//            case HEADER_FOR_BACK_POSITION_LANDSCAPE:
//                // First header goes to parent
//                goToParentCategoryFromType(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL);
//                break;
//            case HEADER_FOR_ALL_POSITION_LANDSCAPE:
//                // Second header goes to all
//                gotoCatalog(mCurrentSubCategory.getChildren().get(mSelectedParentPosition));
//                break;
//            default:
//                // Get real position
//                int pos = position - NUMBER_OF_HEADERS_LANDSCAPE;
//                // Validate item goes to product list or a sub level
//                Category selectedCategory = (Category) parent.getAdapter().getItem(position);
//                // Show product list
//                if (!selectedCategory.hasChildren()) gotoCatalog(selectedCategory);
//                // Show sub level
//                else gotoNestedCategories(selectedCategory.getParent().getName(), selectedCategory.getParent().getUrlKey(), pos);
//                break;
//            }
//        } catch (NullPointerException e) {
//            Print.w(TAG, "WARNING: NPE ON CLICK NESTED CATEGORY IN LANDSCAPE POS: " + position, e);
//        } catch (IndexOutOfBoundsException e) {
//            Print.w(TAG, "WARNING IOE ON CLICK NESTED CATEGORY IN LANDSCAPE", e);
//        }
//    }
//
//    /**
//     * Show nested categories with title name
//     * @param name The category name
//     * @param key The category id
//     * @param landscapeCategoryPosition The sub category position
//     * @author sergiopereira
//     */
//    private void gotoNestedCategories(String name, String key, int landscapeCategoryPosition){
//        Bundle bundle = new Bundle();
//        bundle.putString(ConstantsIntentExtra.CATEGORY_ID, key);
//        bundle.putString(ConstantsIntentExtra.CATEGORY_PARENT_NAME, name);
//        bundle.putInt(ConstantsIntentExtra.SELECTED_SUB_CATEGORY_INDEX, landscapeCategoryPosition);
//        String treeName = TextUtils.isEmpty(mTitleCategory) ? name : mTitleCategory + "/" + name;
//        bundle.putString(ConstantsIntentExtra.CATEGORY_TREE_NAME,  treeName);
//        // Switch
//        ((CategoriesCollectionFragment) getParentFragment()).onSwitchChildFragment(FragmentType.NAVIGATION_CATEGORIES_SUB_LEVEL, bundle);
//    }
//
//    /**
//     * Show product list
//     * @param category The category object
//     * @author sergiopereira
//     */
//    private void gotoCatalog(Category category) {
//        // Update category counter for tracking
//        CategoriesTableHelper.updateCategoryCounter(category.getType(), category.getName());
//        // Tracking category
//        Bundle bundle = new Bundle();
//        bundle.putString(ConstantsIntentExtra.CONTENT_URL, category.getApiUrl());
//        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, category.getName());
//        bundle.putString(ConstantsIntentExtra.CATEGORY_ID, category.getType());
//        String categoryTree = "";
//        if(!TextUtils.isEmpty(getBaseActivity().getCategoriesTitle())){
//            categoryTree = getBaseActivity().getCategoriesTitle() + "/" + category.getName();
//            categoryTree = categoryTree.replace("/", ",");
//        }
//        bundle.putString(ConstantsIntentExtra.CATEGORY_TREE_NAME,  categoryTree);
//        bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
//        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcategory_prefix);
//        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, category.getCategoryPath());
//        bundle.putBoolean(ConstantsIntentExtra.REMOVE_ENTRIES, false);
//        // Goto Catalog
//        getBaseActivity().onSwitchFragment(FragmentType.CATALOG, bundle, FragmentController.ADD_TO_BACK_STACK);
//    }
//
//    /**
//     * Pop the back stack until the parent from current level type
//     * @param type The fragment type
//     * @author sergiopereira
//     */
//    private void goToParentCategoryFromType(FragmentType type){
//        Print.i(TAG, "GOTO PARENT LEVEL FROM: " + type.toString());
//        switch (type) {
//        case NAVIGATION_CATEGORIES_ROOT_LEVEL:
//            ((CategoriesCollectionFragment) getParentFragment()).goToBackUntil(FragmentType.NAVIGATION_CATEGORIES_ROOT_LEVEL);
//            break;
//        case NAVIGATION_CATEGORIES_SUB_LEVEL:
//            ((CategoriesCollectionFragment) getParentFragment()).goToParentCategory();
//            break;
//        default:
//            Print.w(TAG, "WARNING: ON GOTO PARENT UNKNOWN LEVEL");
//            break;
//        }
//    }
//
//    /**
//     * ####### RESPONSE EVENTS #######
//     */
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
//     */
//    @Override
//    public void onRequestComplete(Bundle bundle) {
//        Print.i(TAG, "ON SUCCESS EVENT");
//        // Validate fragment state
//        if(isOnStoppingProcess) return;
//        // Get categories
//        mCategories = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
//        // Show categories
//        showCategoryList(mCategories);
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
//     */
//    @Override
//    public void onRequestError(Bundle bundle) {
//        Print.i(TAG, "ON ERROR EVENT");
//        // Validate fragment state
//        if(isOnStoppingProcess) return;
//        // Generic errors
//        super.handleErrorEvent(bundle);
//    }
//
//    protected boolean isRootCategory(){
//        return mCategoryKey == ROOT_CATEGORIES;
//    }
//
//}
