package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.text.TextUtils;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import android.widget.TextView;
import com.bamilo.android.framework.components.recycler.DividerItemDecoration;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.RecentlyViewedAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartAddItemHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.products.GetRecentlyViewedHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.products.ValidateProductHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.database.LastViewedTableHelper;
import com.bamilo.android.framework.service.objects.campaign.CampaignItem;
import com.bamilo.android.framework.service.objects.cart.AddedItemStructure;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.product.ValidProductList;
import com.bamilo.android.framework.service.objects.product.pojo.ProductMultiple;
import com.bamilo.android.framework.service.objects.product.pojo.ProductSimple;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.tracking.gtm.GTMValues;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.HeaderFooterGridView;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogSimpleListFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show all last viewed items from database
 *
 * @author Andre Lopes
 */
public class RecentlyViewedFragment extends BaseFragment implements IResponseCallback, DialogSimpleListFragment.OnDialogListListener{

    protected final static String TAG = RecentlyViewedFragment.class.getSimpleName();

    protected final static String RECENT_LIST = "recentlyViewedList";

    private TextView mClearAllButton;

    private RecentlyViewedAdapter mAdapter;

    private ArrayList<ProductMultiple> mProducts;

    private HeaderFooterGridView mGridView;

    private View mClickedBuyButton;

    private ArrayList<String> list;
    private boolean pageTracked = false;
    private MainEventModel addToCartEventModel;

    /**
     * Empty constructor
     */
    public RecentlyViewedFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.RECENTLY_VIEWED,
                R.layout._def_recently_viewed_fragment,
                R.string.recently_viewed,
                NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.RECENTLY_VIEWED.getName()), getString(R.string.gaScreen),
                "" ,
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get grid view
        mGridView = (HeaderFooterGridView) view.findViewById(R.id.recently_viewed_grid);
        mGridView.setHasFixedSize(true);
        mGridView.setGridLayoutManager(getResources().getInteger(R.integer.favourite_num_columns));
        mGridView.setNestedScrollingEnabled(false);
        mGridView.setItemAnimator(new DefaultItemAnimator());
        mGridView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        mGridView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL_LIST));
        // Get clear all button
        mClearAllButton = (TextView) view.findViewById(R.id.recently_viewed_button);
        mClearAllButton.setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(RECENT_LIST)) {
            list = savedInstanceState.getStringArrayList(RECENT_LIST);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Show Loading View
        showFragmentLoading();
        // Get RecentlyViewed
        Print.i(TAG, "LOAD LAST VIEWED ITEMS");
        new GetRecentlyViewedHelper(this);
    }


    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(RECENT_LIST, list);
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVED INSTANCE");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /**
     * ######### LAYOUTS #########
     */

    /**
     * Show content after get items
     *
     * @author sergiopereira
     */
    protected void showContent() {
        // Validate favourites
        if (mProducts != null && !mProducts.isEmpty()) {
            Print.i(TAG, "ON SHOW CONTENT");
            mAdapter = new RecentlyViewedAdapter(getBaseActivity(), mProducts, this);
            mGridView.setAdapter(mAdapter);
            showFragmentContentContainer();
        } else {
            Print.i(TAG, "ON SHOW IS EMPTY");
            showEmpty();
        }
    }

    /**
     * Show empty content
     *
     * @author Andre Lopes
     */
    protected void showEmpty() {
        getBaseActivity().hideWarningMessage();
        mClearAllButton.setVisibility(View.GONE);
        mClearAllButton.setOnClickListener(null);
        showErrorFragment(ErrorLayoutFactory.NO_RECENTLY_VIEWED_LAYOUT, this);
    }

    /**
     * ######### LISTENERS #########
     */

    /*
     * (non-Javadoc)
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Case item
        if (id == R.id.addabletocart_item_container) onItemClick(view);
        // Case add to cart
        else if (id == R.id.button_shop) onClickAddToCart(view);
        // Case simple
        else if (id == R.id.button_variant) onClickVariation(view);
        // Case size guide
        else if (id == R.id.dialog_list_size_guide_button) onClickSizeGuide(view);
        // Case clear all
        else if (id == R.id.recently_viewed_button) onClickClearAll();
        // Case unknown
        else Print.w(TAG, "WARNING ON CLICK UNKNOWN VIEW");
    }

    /**
     * Process the click on size guide.
     * @author sergiopereira
     */
    protected void onClickSizeGuide(View view){
        try {
            // Get size guide url
            String url = (String) view.getTag();
            // Validate url
            if(!TextUtils.isEmpty(url)) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.SIZE_GUIDE_URL, url);
                getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_SIZE_GUIDE, bundle, FragmentController.ADD_TO_BACK_STACK);
            } else Print.w(TAG, "WARNING: SIZE GUIDE URL IS EMPTY");
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK SIZE GUIDE");
        }
    }

    /**
     * Process the click on variation button
     */
    protected void onClickVariation(View view) {
        try {
            // Hide warning
            getBaseActivity().hideWarningMessage();
            // Show dialog
            int position = Integer.parseInt(view.getTag().toString());
            ProductMultiple addableToCart = mProducts.get(position);
            showVariantsDialog(addableToCart);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON CLICK VARIATION");
        }
    }

    /**
     * Process the click on item
     */
    protected void onItemClick(View view) {
        Print.i(TAG, "ON ITEM CLICK");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            ProductMultiple addableToCart = mProducts.get(position);
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, addableToCart.getSku());
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON ITEM CLICK");
        }
    }

    /**
     * Process the click on clear all button
     *
     * @author sergiopereira
     */
    private void onClickClearAll() {
        Print.i(TAG, "ON CLICK CLEAR ALL ITEMS");
        try {
            // Delete all items in database
            LastViewedTableHelper.deleteAllLastViewed();
            // Clean products
            mProducts.clear();
            // Update adapter
            mAdapter.notifyDataSetChanged();
            // Sow empty layout
            showEmpty();
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON ITEM CLICK");
        }
    }

    /**
     * Update the layout after user action
     *
     * @author sergiopereira
     */
    protected synchronized void updateLayoutAfterAction(int pos) {
        Print.i(TAG, "ON UPDATE LAYOUT AFTER ACTION");
        // Remove item
        try {
            mProducts.remove(pos);
        } catch (IndexOutOfBoundsException e) {
            Print.w(TAG, "IndexOutOfBoundsException avoided when removing position: " + pos);
        }
        // Update adapter
        mAdapter.notifyDataSetChanged();
        // Validate current state
        if (mProducts.isEmpty()) {
            showEmpty();
        }
        // Dismiss progress, used because of onClickClearAll() on RecentlyViewedFragment
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideActivityProgress();
            }
        }, 300);
    }


    /**
     * Process the click on add button
     */
    protected void onClickAddToCart(View view) {
        Print.i(TAG, "ON CLICK ADD ITEM TO CART");
        try {
            int position = Integer.parseInt(view.getTag().toString());
            ProductMultiple product = mProducts.get(position);
            // Validate simple variations
            if(product.hasMultiSimpleVariations() && !product.hasSelectedSimpleVariation()) {
                mClickedBuyButton = view;
                onClickVariation(view);
            } else {
                triggerAddProductToCart(product, position);
            }
        } catch (IndexOutOfBoundsException e) {
            Print.w(TAG, "WARNING: IOB ON ADD ITEM TO CART", e);
            if(mAdapter != null) mAdapter.notifyDataSetChanged();
            showWarningErrorMessage(getString(R.string.error_add_to_shopping_cart));
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON ADD ITEM TO CART", e);
            view.setEnabled(false);
        }
    }

    /**
     * ######### TRIGGERS #########
     */

    /**
     * Trigger to add an item to cart
     */
    protected synchronized void triggerAddProductToCart(ProductMultiple product, int position) {
        Print.i(TAG, "ON TRIGGER ADD TO CART: " + position);
        ProductSimple simple = product.getSelectedSimple();
        if(simple == null) {
            showUnexpectedErrorWarning();
            return;
        }
        // Request data
        Bundle bundle = ShoppingCartAddItemHelper.createBundle(simple.getSku());
        bundle.putInt(ShoppingCartAddItemHelper.PRODUCT_POS_TAG, position);
        bundle.putString(RestConstants.SKU, product.getSku());
        bundle.putBoolean(ShoppingCartAddItemHelper.REMOVE_RECENTLY_VIEWED_TAG, true);
        // Trigger
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), bundle, this);
        // Tracking
        trackAddToCart(simple.getSku(), product);
    }

    /**
     * Track add to cart
     */
    protected void trackAddToCart(String sku, @NonNull ProductMultiple addableToCart) {
        try {
            // Tracking
            Bundle bundle = new Bundle();
            bundle.putString(TrackerDelegator.SKU_KEY, sku);
            bundle.putDouble(TrackerDelegator.PRICE_KEY, addableToCart.getPriceForTracking());
            bundle.putString(TrackerDelegator.NAME_KEY, addableToCart.getName());
            bundle.putString(TrackerDelegator.BRAND_KEY, addableToCart.getBrandName());
            bundle.putDouble(TrackerDelegator.RATING_KEY, addableToCart.getAvgRating());
            bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, addableToCart.getMaxSavingPercentage());
            bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.WISHLISTPAGE);
            bundle.putString(TrackerDelegator.CATEGORY_KEY, addableToCart.getCategories());
            TrackerDelegator.trackProductAddedToCart(bundle);

            // Global Tracker
            addToCartEventModel = new MainEventModel(getString(TrackingPage.RECENTLY_VIEWED.getName()),
                    EventActionKeys.ADD_TO_CART, sku, (long) addableToCart.getPrice(), null);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * ######### RESPONSE #########
     */

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "ON RESPONSE COMPLETE " + getId());
        // Get event type
        EventType eventType = baseResponse.getEventType();
        // Validate the current state
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }
        // Validate event
        super.handleSuccessEvent(baseResponse);
        // Validate the event type
        switch (eventType) {
            case GET_RECENTLY_VIEWED_LIST:
                if (!pageTracked) {
                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.RECENTLY_VIEWED.getName()), getString(R.string.gaScreen),
                            "" ,
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);

                    pageTracked = true;
                }
                Print.i(TAG, "ON RESPONSE COMPLETE: GET_RECENTLY_VIEWED_LIST");
                list = (ArrayList<String>)baseResponse.getContentData();
                if (!CollectionUtils.isEmpty(list)) {
                    triggerValidateRecentlyViewed(list);
                } else {
                    showEmpty();
                }
                break;
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                Print.i(TAG, "ON RESPONSE COMPLETE: ADD_ITEM_TO_SHOPPING_CART_EVENT");
                int position = ((AddedItemStructure) baseResponse.getMetadata().getData()).getCurrentPos();
                updateLayoutAfterAction(position);

                // Tracking add to cart
                if (addToCartEventModel != null) {
                    PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
                    if (cart != null) {
                        addToCartEventModel.customAttributes = MainEventModel
                                .createAddToCartEventModelAttributes(addToCartEventModel.label, (long) cart.getTotal(), true);
                    } else {
                        addToCartEventModel.customAttributes = MainEventModel
                                .createAddToCartEventModelAttributes(addToCartEventModel.label, 0, true);

                    }
                    TrackerManager.trackEvent(getContext(), EventConstants.AddToCart, addToCartEventModel);
                }
                break;
            case VALIDATE_PRODUCTS:
                mProducts = (ValidProductList) baseResponse.getContentData();
                if (!CollectionUtils.isEmpty(mProducts)) {
                    showContent();
                } else {
                    showEmpty();
                }
                break;
            default:
                Print.d(TAG, "ON RESPONSE COMPLETE: UNKNOWN TYPE");
                break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        Print.i(TAG, "ON ERROR RESPONSE");
        // Get type
        EventType eventType = baseResponse.getEventType();
        // Validate the current state
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }
        // Validate common errors
        if (baseResponse.getEventType() == EventType.VALIDATE_PRODUCTS) {
            baseResponse.setEventTask(EventTask.NORMAL_TASK);
        }
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            return;
        }
        // Validate type
        switch (eventType) {
            case GET_RECENTLY_VIEWED_LIST:
                Print.d(TAG, "ON RESPONSE ERROR: GET_RECENTLY_VIEWED_LIST");
                showEmpty();
                break;
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                Print.d(TAG, "ON RESPONSE ERROR: ADD_ITEM_TO_SHOPPING_CART_EVENT");
                hideActivityProgress();
                break;
            case VALIDATE_PRODUCTS:
                Print.d(TAG, "ON RESPONSE ERROR: VALIDATE_PRODUCTS");
                showEmpty();
                break;
            default:
                Print.d(TAG, "ON RESPONSE ERROR: UNKNOWN TYPE");
                break;
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        if (!CollectionUtils.isEmpty(list)) {
            new GetRecentlyViewedHelper(this);
        } else {
            showEmpty();
        }
    }

    /**
     * send a array of products sku's to be validated by the API
     */
    private void triggerValidateRecentlyViewed(ArrayList<String> list) {
        triggerContentEvent(new ValidateProductHelper(), ValidateProductHelper.createBundle(list), this);
    }

    /**
     * ###### DIALOG ######
     */

    protected void showVariantsDialog(ProductMultiple product) {
        try {
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    getString(R.string.product_variance_choose),
                    product,
                    this);
            dialog.show(getFragmentManager(), null);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
        }
    }

    @Override
    public void onDialogListItemSelect(int position) {
        // Update the recently adapter
        mAdapter.notifyDataSetChanged();
        // Case from sendPurchaseRecommend button
        if(mClickedBuyButton != null) {
            onClickAddToCart(mClickedBuyButton);
        }
    }

    @Override
    public void onDialogListClickView(View view) {
        // Process the click in the main method
        onClick(view);
    }

    @Override
    public void onDialogSizeListClickView(int position, CampaignItem item) {

    }

    @Override
    public void onDialogListDismiss() {
        mClickedBuyButton = null;
    }

}
