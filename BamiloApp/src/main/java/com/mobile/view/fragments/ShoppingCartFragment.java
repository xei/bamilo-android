package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.EventConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.EventFactory;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.cart.ShoppingCartAddMultipleItemsHelper;
import com.mobile.helpers.cart.ShoppingCartChangeItemQuantityHelper;
import com.mobile.helpers.cart.ShoppingCartRemoveItemHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.tracking.AdjustTracker;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.DarwinRegex;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.CurrencyFormatter;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.cart.UICartUtils;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.utils.product.UIProductUtils;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 *
 */
public class ShoppingCartFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = ShoppingCartFragment.class.getSimpleName();

    private static final String cartValue = "";
    private long mBeginRequestMillis;
    // DROID-10
    private long mGABeginRequestMillis;
    private PurchaseCartItem mQuantityChangedItem;
    private List<PurchaseCartItem> items;
    private ViewGroup mCartItemsContainer;
    private View mTotalContainer;
    private View mCheckoutButton;
    private View mCallToOrderButton;
    private View mFreeShippingView;
    private DialogListFragment dialogList;
    private TextView mCouponButton;
    private EditText mVoucherView;
    private String mVoucherCode;
    private String mItemRemovedSku;
    private String mPhone2Call = "";
    private double mItemRemovedPriceTracking = 0d;
    private long mItemRemovedQuantity;
    private double mItemRemovedRating;
    private String mItemRemovedCartValue;
    private String mItemsToCartDeepLink;
    private int selectedPosition;
    private int crrQuantity;

    /**
     * Empty constructor
     */
    public ShoppingCartFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.BASKET,
                R.layout.shopping_basket,
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mItemsToCartDeepLink = arguments.getString(ConstantsIntentExtra.DATA);
            arguments.remove(ConstantsIntentExtra.DATA);
        }
        // Get saved state
        if(savedInstanceState != null) {
            mVoucherCode = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
        }

        selectedPosition = 0;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get and set views
        setAppContentLayout(view);
        // Set listeners
        setListeners();
    }

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");

    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        mBeginRequestMillis = System.currentTimeMillis();
        mGABeginRequestMillis = System.currentTimeMillis();
        // Case deep link
        if (!TextUtils.isEmpty(mItemsToCartDeepLink)) addItemsToCart(mItemsToCartDeepLink);
        // Case normal
        else triggerGetShoppingCart();
        // Track page
        TrackerDelegator.trackPage(TrackingPage.CART, getLoadTime(), false);
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        if(mVoucherView != null) {
            mVoucherCode = mVoucherView.getText().toString();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE");
        // Save the voucher code
        if(mVoucherView != null) {
            mVoucherCode = mVoucherView.getText().toString();
            outState.putString(ConstantsIntentExtra.ARG_1, mVoucherCode);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
        releaseVars();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
        releaseVars();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    /*
     * ####### LAYOUT #######
     */

    /**
     * Set the ShoppingCart layout using inflate
     */
    public void setAppContentLayout(View view) {
        mCheckoutButton = view.findViewById(R.id.checkout_button);
        mCallToOrderButton = view.findViewById(R.id.checkout_call_to_order);
        mTotalContainer = view.findViewById(R.id.total_container);
        // Set nested scroll and voucher view
        mVoucherView = (EditText) view.findViewById(R.id.voucher_name);
        NestedScrollView mNestedScroll = (NestedScrollView) view.findViewById(R.id.shoppingcart_nested_scroll);
        UIUtils.scrollToViewByClick(mNestedScroll, mVoucherView);
        // Set voucher button
        mCouponButton = (TextView) view.findViewById(R.id.voucher_btn);
        mCouponButton.setOnClickListener(this);
        // Get free shipping
        mFreeShippingView = view.findViewById(R.id.cart_total_text_shipping);
    }

    /**
     * Show the use voucher layout
     */
    private void showUseVoucher() {
        Print.d(TAG, "SHOWING USE VOUCHER");
        mVoucherView.setText(TextUtils.isNotEmpty(mVoucherCode) ? mVoucherCode : "");
        mVoucherView.setFocusable(true);
        mVoucherView.setFocusableInTouchMode(true);
        mCouponButton.setText(getString(R.string.use_label));

    }

    /**
     * Show the remove voucher layout
     */
    private void showRemoveVoucher() {
        Print.d(TAG, "SHOWING REMOVE VOUCHER");
        mVoucherView.setText(mVoucherCode);
        mVoucherView.setFocusable(false);
        mVoucherView.setFocusableInTouchMode(false);
        mCouponButton.setText(getString(R.string.remove_label));
    }

    /**
     * Set the total value
     */
    private void setTotal(@NonNull PurchaseEntity cart) {
        Print.d(TAG, "SET THE TOTAL VALUE");
        // Get views
        TextView totalValue = (TextView) mTotalContainer.findViewById(R.id.total_value);
        // Set views
        totalValue.setText(CurrencyFormatter.formatCurrency(cart.getTotal()));
        mTotalContainer.setVisibility(View.VISIBLE);
        // Set free shipping
        mFreeShippingView.setVisibility(cart.hasFreeShipping() ? View.VISIBLE : View.GONE);
    }

    /**
     * showNoItems update the layout when basket has no items
     */
    public void showNoItems() {
        showErrorFragment(ErrorLayoutFactory.CART_EMPTY_LAYOUT, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        });
        getBaseActivity().hideKeyboard();
        TrackerDelegator.trackPage(TrackingPage.EMPTY_CART, getLoadTime(), false);
    }

    /**
     *
     */
    public void setListeners() {
        // Set checkout listeners
        mCheckoutButton.setOnClickListener(this);
        // Get phone number from country configs
        mPhone2Call = CountryPersistentConfigs.getCountryPhoneNumber(getBaseActivity());
        // Show Call To Order if available on the device
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) && !TextUtils.isEmpty(mPhone2Call)) {
            mCallToOrderButton.setVisibility(View.VISIBLE);
            mCallToOrderButton.setSelected(true);
            mCallToOrderButton.setOnClickListener(this);
        } else {
            mCallToOrderButton.setVisibility(View.GONE);
        }
    }

    /*
     * ####### LISTENER #######
     */

    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case call to order
        if(id == R.id.checkout_call_to_order) {
            onClick2Call();
        }
        // Case voucher
        else if(id == R.id.voucher_btn) {
            onClickVoucherButton();
        }
        // Case next button
        else if(id == R.id.checkout_button) {
            onClickCheckoutButton();
        }
        // Case super
        else super.onClick(view);
    }

    /**
     * Process the click on checkout button.
     */
    private void onClickCheckoutButton() {
        // Case has voucher to submit
        if (!TextUtils.isEmpty(mVoucherView.getText()) && !TextUtils.equals(mCouponButton.getText(), getString(R.string.remove_label))) {
            onClickVoucherButton();
        }
        // Case checkout
        else if (items != null && items.size() > 0) {
            TrackerDelegator.trackCheckout(items);
            Bundle bundle = new Bundle();
            bundle.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, true);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case invalid cart
        else {
            String title = getString(R.string.basket_label);
            String message = getString(R.string.shoppingcart_alert_message_no_items);
            String buttonText = getString(R.string.ok_label);
            DialogGenericFragment.createInfoDialog(title, message, buttonText).show(getActivity().getSupportFragmentManager(), null);
        }
    }

    /**
     * Process the click on voucher button
     */
    private void onClickVoucherButton() {
        mVoucherCode = mVoucherView.getText().toString();
        getBaseActivity().hideKeyboard();
        if (!TextUtils.isEmpty(mVoucherCode)) {
            if (TextUtils.equals(getString(R.string.use_label), mCouponButton.getText().toString())) {
                triggerSubmitVoucher(mVoucherCode);
            } else {
                triggerRemoveVoucher();
            }
        } else {
            showWarningErrorMessage(getString(R.string.voucher_error_message));
        }
    }

    /**
     * Process the click on call to order
     */
    private void onClick2Call() {
        // Displays the phone number but the user must press the Call button to begin the phone call
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhone2Call));
        if (intent.resolveActivity(getBaseActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
        // Tracking
        TrackerDelegator.trackCall(getBaseActivity());
    }

    /*
     * ####### TRIGGERS #######
     */

    /**
     * Trigger to get cart items validating FavouritesFragment state is completed
     */
    private void triggerGetShoppingCart() {
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
    }

    /**
     * Trigger to remove item from cart
     */
    private void triggerRemoveItem(PurchaseCartItem item) {
        mItemRemovedSku = item.getConfigSimpleSKU();
        mItemRemovedPriceTracking = item.getPriceForTracking();
        mItemRemovedQuantity = item.getQuantity();
        mItemRemovedRating = -1d;
        if (TextUtils.isEmpty(cartValue)) {
            TextView totalValue = (TextView) mTotalContainer.findViewById(R.id.total_value);
            mItemRemovedCartValue = totalValue.toString();
        } else {
            mItemRemovedCartValue = cartValue;
        }
        triggerContentEventProgress(new ShoppingCartRemoveItemHelper(), ShoppingCartRemoveItemHelper.createBundle(item.getConfigSimpleSKU()), this);
    }

    /**
     * Trigger to remove the submit a voucher value.
     */
    private void triggerSubmitVoucher(String code) {
        triggerContentEventProgress(new AddVoucherHelper(), AddVoucherHelper.createBundle(code), this);
    }

    /**
     * Trigger to remove the submitted voucher.
     */
    private void triggerRemoveVoucher() {
        triggerContentEventProgress(new RemoveVoucherHelper(), null, this);
    }

    /**
     * Trigger to add all items to cart (Deep link).
     */
    private void triggerAddAllItems(ArrayList<String> values) {
        triggerContentEventProgress(new ShoppingCartAddMultipleItemsHelper(), ShoppingCartAddMultipleItemsHelper.createBundle(values), this);
    }

    /**
     * Trigger used to change quantity
     */
    public void triggerChangeItemQuantityInShoppingCart(int position, int quantity) {
        PurchaseCartItem item = items.get(position);
        TrackerDelegator.trackAddToCartGTM(item, quantity, mItemRemovedCartValue);
        TrackerManager.postEvent(getBaseActivity(), EventConstants.AddToCart, EventFactory.addToCart(item.getSku(), (long) BamiloApplication.INSTANCE.getCart().getTotal(), true));
        item.setQuantity(quantity);
        mBeginRequestMillis = System.currentTimeMillis();
        mGABeginRequestMillis = System.currentTimeMillis();
        mQuantityChangedItem = item;
        //
        triggerContentEventProgress(new ShoppingCartChangeItemQuantityHelper(), ShoppingCartChangeItemQuantityHelper.createBundle(item.getConfigSimpleSKU(), quantity), this);
    }

    /**
     *
     */
    private void releaseVars() {
        mCartItemsContainer = null;
        mCheckoutButton = null;
        dialogList = null;
    }


    /**
     * Get items from string
     *
     * @author sergiopereira
     */
    private void addItemsToCart(String items) {
        String[] itemsToCart = items.split(DarwinRegex.SKU_DELIMITER);
        Print.i(TAG, "RECEIVED : " + items + " " + itemsToCart.length);
        // Create arguments to add all items to cart
        ArrayList<String> productBySku = new ArrayList<>();
        Collections.addAll(productBySku, itemsToCart);
        // Case valid deep link
        if (!productBySku.isEmpty()) {
            triggerAddAllItems(productBySku);
        }
        // Case invalid deep link
        else {
            triggerGetShoppingCart();
        }
    }

    /*
     * ####### RESPONSES #######
     */

    /**
     *
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        Bundle params;

        // Update cart info
        super.handleSuccessEvent(baseResponse);

        EventType eventType = baseResponse.getEventType();

        Print.d(TAG, "onSuccessEvent: eventType = " + eventType);
        switch (eventType) {
            case ADD_VOUCHER:
                PurchaseEntity addVoucherPurchaseEntity = (PurchaseEntity) baseResponse.getContentData();
                hideActivityProgress();
                displayShoppingCart(addVoucherPurchaseEntity);
                break;
            case REMOVE_VOUCHER:
                PurchaseEntity removeVoucherPurchaseEntity = (PurchaseEntity) baseResponse.getContentData();
                hideActivityProgress();
                mVoucherCode = null;
                displayShoppingCart(removeVoucherPurchaseEntity);
                break;
            case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
                //Print.i(TAG, "code1removing and tracking" + itemRemoved_price);
                params = new Bundle();
                params.putString(TrackerDelegator.SKU_KEY, mItemRemovedSku);
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                params.putDouble(TrackerDelegator.PRICE_KEY, mItemRemovedPriceTracking);
                params.putLong(TrackerDelegator.QUANTITY_KEY, mItemRemovedQuantity);
                params.putDouble(TrackerDelegator.RATING_KEY, mItemRemovedRating);
                params.putString(TrackerDelegator.CARTVALUE_KEY, mItemRemovedCartValue);
                TrackerDelegator.trackProductRemoveFromCart(params);
                // DROID-10 TrackerDelegator.trackLoadTiming(params);
                TrackerDelegator.trackScreenLoadTiming(R.string.gaRemoveItemFromShoppingCart, mGABeginRequestMillis, mItemRemovedSku);
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
                hideActivityProgress();
                break;
            case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
                hideActivityProgress();
                params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                // DROID-10 TrackerDelegator.trackLoadTiming(params);
                TrackerDelegator.trackScreenLoadTiming(R.string.gaChangeItemQuantityInShoppingCart, mGABeginRequestMillis, mQuantityChangedItem.getSku());
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
                break;
            case GET_SHOPPING_CART_ITEMS_EVENT:
                hideActivityProgress();
                PurchaseEntity purchaseEntity = (PurchaseEntity) baseResponse.getContentData();
                params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                //DROID-10 TrackerDelegator.trackLoadTiming(params);
                TrackerDelegator.trackScreenLoadTiming(R.string.gaShoppingCart, mGABeginRequestMillis, TextUtils.joinCartItemSKUes(purchaseEntity));
                params.clear();
                params.putParcelable(AdjustTracker.CART, purchaseEntity);
                TrackerDelegator.trackPage(TrackingPage.CART_LOADED, getLoadTime(), false);
                TrackerDelegator.trackPageForAdjust(TrackingPage.CART_LOADED, params);
                displayShoppingCart(purchaseEntity);
                break;
            case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
                onAddItemsToShoppingCartRequestSuccess(baseResponse);
                break;
            default:
                params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                //DROID-10 TrackerDelegator.trackLoadTiming(params);
                PurchaseEntity defPurchaseEntity = (PurchaseEntity) baseResponse.getMetadata().getData();
                TrackerDelegator.trackScreenLoadTiming(R.string.gaShoppingCart, mGABeginRequestMillis, TextUtils.joinCartItemSKUes(defPurchaseEntity));
                displayShoppingCart(defPurchaseEntity);
                break;
        }
    }


    /**
     *
     */
    private void onAddItemsToShoppingCartRequestSuccess(BaseResponse baseResponse){
        hideActivityProgress();
        ShoppingCartAddMultipleItemsHelper.AddMultipleStruct addMultipleStruct = (ShoppingCartAddMultipleItemsHelper.AddMultipleStruct) baseResponse.getContentData();

        if (addMultipleStruct.getErrorMessages() != null) {
            ArrayList<String> notAdded = addMultipleStruct.getErrorMessages();
            if (!notAdded.isEmpty()) {
                getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.some_products_not_added));
            }
        }

        getBaseActivity().updateCartInfo();
        if (BamiloApplication.INSTANCE.getCart() != null) {
            displayShoppingCart(BamiloApplication.INSTANCE.getCart());
        }
    }


    /**
     *
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        hideActivityProgress();
        EventType eventType = baseResponse.getEventType();

        // Validate generic errors
        if (super.handleErrorEvent(baseResponse)) {
            if(eventType == EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT ){
                items.get(selectedPosition).setQuantity(crrQuantity); //restarts the previous position for load selected quantity before the error
            }
            return;
        }

        switch (eventType) {
            case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
                showNoItems();
                break;
            case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
                if (items.size() == 0) {
                    showNoItems();
                } else {
                    hideNoItems();
                }
                break;
            default:
                break;
        }
        mBeginRequestMillis = System.currentTimeMillis();
        mGABeginRequestMillis = System.currentTimeMillis();
    }


    /**
     * Display shopping cart info
     */
    private void displayShoppingCart(PurchaseEntity cart) {
        Print.d(TAG, "displayShoppingCart");
        // Case invalid cart
        if (cart == null || CollectionUtils.isEmpty(cart.getCartItems())) {
            showNoItems();
            return;
        }



        // Case invalid view
        if (getView() == null) {
            showErrorFragment(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, this);
            return;
        }
        // Case valid state
        items = cart.getCartItems();
        // Get views
        TextView subTotal = (TextView) getView().findViewById(R.id.price_total);
        TextView subTotalUnreduced = (TextView) getView().findViewById(R.id.price_unreduced);
        TextView articlesCount = (TextView) getView().findViewById(R.id.articles_count);
        TextView extraCostsValue = (TextView) getView().findViewById(R.id.extra_costs_value);
        TextView vatIncludedLabel = (TextView) getView().findViewById(R.id.vat_included_label);
        TextView vatValue = (TextView) getView().findViewById(R.id.vat_value);
        View extraCostsMain = getView().findViewById(R.id.extra_costs_container);
        View shippingContainer = getView().findViewById(R.id.shipping_container);
        TextView shippingValue = (TextView) getView().findViewById(R.id.shipping_value);
        TextView voucherValue = (TextView) getView().findViewById(R.id.text_voucher);
        final View voucherContainer = getView().findViewById(R.id.voucher_info_container);
        // Get and set the cart value
        setTotal(cart);
        // Set voucher
        if (cart.hasCouponDiscount() && cart.getCouponDiscount() >= 0) {
            // Set voucher value
            String discount = String.format(getString(R.string.placeholder_discount), CurrencyFormatter.formatCurrency(cart.getCouponDiscount()));
            voucherValue.setText(discount);
            voucherContainer.setVisibility(View.VISIBLE);
            // Set voucher code
            mVoucherCode = cart.getCouponCode();
            showRemoveVoucher();
        } else {
            // Set voucher
            voucherContainer.setVisibility(View.GONE);
            showUseVoucher();
        }
        // Set VAT
        UICartUtils.showVatInfo(cart, vatIncludedLabel, vatValue);
        // Set shipping free possible
        UICartUtils.setShippingRule(cart, shippingContainer, shippingValue, extraCostsMain, extraCostsValue);
        // Set number of items
        articlesCount.setText(getResources().getQuantityString(R.plurals.numberOfItems, cart.getCartCount(), cart.getCartCount()));
        // Add all items
        mCartItemsContainer = (ViewGroup) getView().findViewById(R.id.shoppingcart_list);
        mCartItemsContainer.removeAllViewsInLayout();
        for (int i = 0; i < items.size(); i++) {
            PurchaseCartItem item = items.get(i);
            mCartItemsContainer.addView(createCartItemView(i, mCartItemsContainer, LayoutInflater.from(getBaseActivity()), item));
        }
        // Set sub total and sub total unreduced
        UICartUtils.setSubTotal(cart, subTotal, subTotalUnreduced);
        // Cart price rules
        LinearLayout priceRulesContainer = (LinearLayout) getView().findViewById(R.id.price_rules_container);
        CheckoutStepManager.showPriceRules(getActivity(), priceRulesContainer, cart.getPriceRules());
        // Tracking
        TrackerDelegator.trackViewCart(cart.getCartCount(), cart.getPriceForTracking());
        TrackerDelegator.trackPage(TrackingPage.FILLED_CART, getLoadTime(), false);
        // Show content
        showFragmentContentContainer();
    }


    /**
     * Fill view item with PurchaseCartItem data
     */
    public View createCartItemView(final int position, ViewGroup parent, LayoutInflater mInflater, final PurchaseCartItem item) {
        View view = mInflater.inflate(R.layout.shopping_cart_product_container, parent, false);
        Log.d( TAG, "getView: productName = " + item.getName());
        // Get item
        ImageView productView = (ImageView) view.findViewById(R.id.image_view);
        View pBar = view.findViewById(R.id.image_loading_progress);
        TextView itemName = (TextView) view.findViewById(R.id.cart_item_text_name);
        TextView priceView = (TextView) view.findViewById(R.id.cart_item_text_price);
        TextView quantityBtn = (TextView) view.findViewById(R.id.cart_item_button_quantity);
        ImageView shopFirstImage = (ImageView) view.findViewById(R.id.cart_item_image_shop_first);
        TextView deleteBtn = (TextView) view.findViewById(R.id.cart_item_button_delete);
        TextView variationName = (TextView) view.findViewById(R.id.cart_item_text_variation);
        TextView variationValue = (TextView) view.findViewById(R.id.cart_item_text_variation_value);
        // Set item
        itemName.setText(item.getName());
        itemName.setSelected(true);
        String imageUrl = item.getImageUrl();
        // Variation
        UICartUtils.setVariation(item, variationName, variationValue);
        // Hide shop view image if is_shop is false
        UIProductUtils.setShopFirst(item, shopFirstImage);
        // Show shop first overlay message
        UIProductUtils.showShopFirstOverlayMessage(this, item, shopFirstImage);
        // Image
        //RocketImageLoader.instance.loadImage(imageUrl, productView, pBar, R.drawable.no_image_small);
        ImageManager.getInstance().loadImage(imageUrl, productView, pBar, R.drawable.no_image_large);
        // Price
        UIProductUtils.setPriceRules(item, priceView);
        // Delete
        deleteBtn.setTag(R.id.position, position);
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedElements(view);
            }
        });
        // Quantity
        quantityBtn.setText(String.valueOf(item.getQuantity()));
        if(item.getMaxQuantity() > 1) {
            quantityBtn.setEnabled(true);
            quantityBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showQuantityDialog(position);
                }
            });
        } else {
            quantityBtn.setEnabled(false);
            if (DeviceInfoHelper.isPosJellyBean()) {
                quantityBtn.setBackground(null);
            } else {
                //noinspection deprecation
                quantityBtn.setBackgroundDrawable(null);
            }
        }
        // Save the position to process the click on item
        view.setTag(R.id.target_sku, item.getSku());
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    goToProductDetails((String) v.getTag(R.id.target_sku));
                } catch (NullPointerException e) {
                    Print.w(TAG, "WARNING: NPE ON GET CLICKED TAG");
                }
            }
        });

        return view;
    }

    /**
     * Function to redirect to the selected product details.
     */
    private void goToProductDetails(String sku) {
        if (!TextUtils.isEmpty(sku)) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcart_prefix);
            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * This method manages the deletion of selected elements
     */
    public void deleteSelectedElements(View view) {
        // Get position
        int position = (int) view.getTag(R.id.position);
        // Validate position
        if (position < items.size()) {
            mBeginRequestMillis = System.currentTimeMillis();
            mGABeginRequestMillis = System.currentTimeMillis();
            triggerRemoveItem(items.get(position));
        }
    }

    public void hideNoItems() {
        showFragmentContentContainer();
    }

    public void showQuantityDialog(final int position) {
        ArrayList<String> quantities = new ArrayList<>();
        selectedPosition = position;
        for (int i = 1; i <= items.get(position).getMaxQuantity(); i++) {
            quantities.add(String.valueOf(i));
        }
        crrQuantity = items.get(position).getQuantity();
        OnDialogListListener listener = new OnDialogListListener() {
            @Override
            public void onDialogListItemSelect(int quantity, String value) {
                if(quantity != crrQuantity -1){
                    triggerChangeItemQuantityInShoppingCart(position, quantity+1);
                }
                if(dialogList != null) {
                    dialogList.dismissAllowingStateLoss();
                    dialogList = null;
                }
            }
            @Override
            public void onDismiss() {
            }
        };
        dialogList = DialogListFragment.newInstance(this, listener, getString(R.string.choose_quantity), quantities, crrQuantity - 1);
        dialogList.show(getActivity().getSupportFragmentManager(), null);
    }

}
