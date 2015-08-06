package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.NewAddableToCartListAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.helpers.products.wishlist.AddToWishListHelper;
import com.mobile.helpers.products.wishlist.GetWishListHelper;
import com.mobile.helpers.products.wishlist.RemoveFromWishListHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.product.NewProductAddableToCart;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Catalog fragment.
 *
 * @author sergiopereira
 */
public class WishListFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = WishListFragment.class.getSimpleName();

    private GridView mListView;

    /**
     * Create and return a new instance.
     */
    public static WishListFragment getInstance() {
        return new WishListFragment();
    }

    /**
     * Empty constructor
     */
    public WishListFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Favorite,
                R.layout._def_wishlist_fragment,
                R.string.favourites,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    /*
     * ############ LIFE CYCLE ############
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Get data from saved instance
        if (savedInstanceState != null) {
            Log.i(TAG, "SAVED STATE: " + savedInstanceState.toString());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // List view
        mListView = (GridView) view.findViewById(R.id.wish_list_grid);

        triggerAddToWishList();
        //triggerGetWishListInitialPage();
        //triggerRemoveFromWishList();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }


    /**
     * Show content after get items
     *
     * @author sergiopereira
     */
    protected void showContent(ArrayList<NewProductAddableToCart> wishList) {
        // Validate favourites
        if (CollectionUtils.isNotEmpty(wishList)) {
            Print.i(TAG, "ON SHOW CONTENT");
            NewAddableToCartListAdapter listAdapter = new NewAddableToCartListAdapter(getBaseActivity(), wishList, this);
            mListView.setAdapter(listAdapter);
            showFragmentContentContainer();
        } else {
            Print.i(TAG, "ON SHOW IS EMPTY");
            showErrorFragment(ErrorLayoutFactory.NO_FAVOURITES_LAYOUT, this);
        }
    }
    

    /*
     * ############## LISTENERS ##############
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        Log.i(TAG, "ON CLICK");
        // Get view id
        int id = view.getId();
        // Case item
        if (id == R.id.addabletocart_item_container) onItemClick(view);
        // Case delete
        else if (id == R.id.button_delete) onClickDeleteItem(view);
        // Case add to cart
        else if (id == R.id.button_shop) onClickAddToCart(view);
        // Case add all
        else if (id == R.id.button_shop_all) onClickAddAllToCart();
        // Case simple
        else if (id == R.id.button_variant) onClickVariation(view);
        // Case size guide
        else if (id == R.id.dialog_list_size_guide_button) onClickSizeGuide(view);
        // Case unknown
        else super.onClick(view);

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
     *
     * @param view
     * @author sergiopereira
     */
    protected void onClickVariation(View view) {
//        try {
//            // Hide warning
//            getBaseActivity().warningFactory.hideWarning();
//            // Show dialog
//            int position = Integer.parseInt(view.getTag().toString());
//            AddableToCart addableToCart = mAddableToCartList.get(position);
//            addableToCart.setFavoriteSelected(position);
//            showVariantsDialog(addableToCart);
//        } catch (NullPointerException e) {
//            Print.w(TAG, "WARNING: NPE ON CLICK VARIATION");
//        }
    }

    /**
     * Process the click on item
     *
     * @param view
     * @author sergiopereira
     */
    protected void onItemClick(View view) {
        Print.i(TAG, "ON ITEM CLICK");
        try {
//            int position = Integer.parseInt(view.getTag().toString());
//            AddableToCart addableToCart = mAddableToCartList.get(position);
//            addableToCart.setFavoriteSelected(position);
//            Bundle bundle = new Bundle();
//            bundle.putString(ConstantsIntentExtra.CONTENT_URL, addableToCart.getUrl());
//            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        } catch (NullPointerException e) {
            Print.w(TAG, "WARNING: NPE ON ITEM CLICK");
        }
    }

    /**
     * Process the click on delete button
     *
     * @param view
     * @author sergiopereira
     */
    protected void onClickDeleteItem(View view) {
        Print.i(TAG, "ON CLICK DELETE ITEM");
        String sku = (String) view.getTag(R.id.target_sku);
        triggerRemoveFromWishList(sku);
        Toast.makeText(getBaseActivity(), getString(R.string.products_removed_favourite), Toast.LENGTH_SHORT).show();
    }

    /**
     * Process the click on add all button
     *
     * @author sergiopereira
     */
    protected void onClickAddAllToCart() {
        Print.i(TAG, "ON CLICK ADD ALL TO CART");
        try {
            // TODO
        } catch (NullPointerException | IllegalStateException e) {
            Print.e(TAG, "WARNING: EXCEPTION ON ADD ALL TO CART", e);
            getBaseActivity().warningFactory.showWarning(WarningFactory.PROBLEM_FETCHING_DATA);
        }
    }

    /**
     * Process the click on add button
     *
     * @param view
     * @author sergiopereira
     */
    protected void onClickAddToCart(View view) {
        Print.i(TAG, "ON CLICK ADD ITEM TO CART");
        String sku = (String) view.getTag(R.id.target_sku);
        String simpleSku = (String) view.getTag(R.id.target_simple_sku);
        triggerAddProductToCart(sku, simpleSku);
    }

    /*
     * ############## TRIGGERS ##############
     */

    private void triggerGetWishListInitialPage() {
        triggerContentEvent(new GetWishListHelper(), null, this);
    }

    private void triggerAddToWishList() {
        ContentValues values = new ContentValues();
        values.put("sku", "IN311ELAEQG7NAFAMZ-449912");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new AddToWishListHelper(), bundle, this);
    }

    private void triggerRemoveFromWishList(String sku) {
        ContentValues values = new ContentValues();
        values.put("sku", sku);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new RemoveFromWishListHelper(), bundle, this);
    }


    /**
     * Trigger to add an item to cart
     * @author sergiopereira
     */
    protected synchronized void triggerAddProductToCart(String sku, String simpleSku) {
        Print.i(TAG, "ON TRIGGER ADD TO CART" + simpleSku);
        // Item data
        ContentValues values = new ContentValues();
        values.put(ShoppingCartAddItemHelper.PRODUCT_TAG, sku);
        values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, simpleSku);
        values.put(ShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
        // Request data
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        // Trigger
        triggerContentEventNoLoading(new ShoppingCartAddItemHelper(), bundle, this);
    }

    /*
     * ############## RESPONSES ##############
     */
    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS");
        // Validate fragment state
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Get the catalog
        ArrayList<NewProductAddableToCart> wishList = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
        // Show content
        showContent(wishList);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        Log.i(TAG, "ON REQUEST ERROR");
        // Validate fragment state
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Case No Network
        if (super.handleErrorEvent(bundle)) {
            Log.i(TAG, "HANDLE BASE FRAGMENT");
        }
        // Case unexpected error
        else {
            showContinueShopping();
        }
    }

}