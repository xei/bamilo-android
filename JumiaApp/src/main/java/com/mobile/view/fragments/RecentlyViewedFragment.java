package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.RecentlyViewedAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.products.GetRecentlyViewedHelper;
import com.mobile.helpers.products.ValidateProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.database.LastViewedTableHelper;
import com.mobile.newFramework.objects.cart.AddedItemStructure;
import com.mobile.newFramework.objects.product.ValidProductList;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogSimpleListFragment;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.view.R;

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

    private GridView mGridView;

    private View mClickedBuyButton;

    private ArrayList<String> list;

    /**
     * Empty constructor
     */
    public RecentlyViewedFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.RECENTLY_VIEWED,
                R.layout.recentlyviewed,
                R.string.recently_viewed,
                NO_ADJUST_CONTENT);
    }

    /**
     * Get a new instance
     *
     * @return RecentlyViewedFragment
     */
    public static RecentlyViewedFragment getInstance() {
        return new RecentlyViewedFragment();
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
        mGridView = (GridView) view.findViewById(R.id.recentlyviewed_grid);
        // Get clear all button
        mClearAllButton = (TextView) view.findViewById(R.id.recentlyviewed_button_grey);
        mClearAllButton.setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(RECENT_LIST)) {
            list = savedInstanceState.getStringArrayList(RECENT_LIST);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Tracking page
        TrackerDelegator.trackPage(TrackingPage.RECENTLY_VIEWED, getLoadTime(), false);
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
        else if (id == R.id.recentlyviewed_button_grey) onClickClearAll();
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
            showInfoAddToShoppingCartFailed();
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
        // Item data
        ContentValues values = new ContentValues();
        values.put(ShoppingCartAddItemHelper.PRODUCT_TAG, product.getSku());
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, simple.getSku());
        values.put(ShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putInt(ShoppingCartAddItemHelper.PRODUCT_POS_TAG, position);
        bundle.putString(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, product.getSku());
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
            bundle.putString(TrackerDelegator.BRAND_KEY, addableToCart.getBrand());
            bundle.putDouble(TrackerDelegator.RATING_KEY, addableToCart.getAvgRating());
            bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, addableToCart.getMaxSavingPercentage());
            bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.WISHLISTPAGE);
            bundle.putString(TrackerDelegator.CATEGORY_KEY, addableToCart.getCategories());
            TrackerDelegator.trackProductAddedToCart(bundle);
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
                Print.i(TAG, "ON RESPONSE COMPLETE: GET_RECENTLY_VIEWED_LIST");
                list = (ArrayList<String>)baseResponse.getMetadata().getData();
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
                break;
            case VALIDATE_PRODUCTS:
                mProducts = (ValidProductList) baseResponse.getMetadata().getData();
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
        Print.i(TAG, "ON ERROR RESPONSE");
        // Get type
        EventType eventType = baseResponse.getEventType();
        // Validate the current state
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "WARNING: RECEIVED DATA IN BACKGROUND");
            return;
        }
        // Validate common errors
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            hideActivityProgress();
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
                showInfoAddToShoppingCartOOS();
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
        ContentValues values = new ContentValues();
        for (int i = 0; i < list.size(); i++) {
            values.put(String.format(ValidateProductHelper.VALIDATE_PRODUCTS_KEY, i), list.get(i));
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new ValidateProductHelper(), bundle, this);
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
        // Case from buy button
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
    public void onDialogListDismiss() {
        mClickedBuyButton = null;
    }

}
