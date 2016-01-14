package com.mobile.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.widget.NestedScrollView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.cart.ShoppingCartAddMultipleItemsHelper;
import com.mobile.helpers.cart.ShoppingCartChangeItemQuantityHelper;
import com.mobile.helpers.cart.ShoppingCartRemoveItemHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.DarwinRegex;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ErrorLayoutFactory;
import com.mobile.utils.ui.ShoppingCartUtils;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * @author sergiopereira
 *
 */
public class ShoppingCartFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = ShoppingCartFragment.class.getSimpleName();

    private static final String cartValue = "";
    private long mBeginRequestMillis;
    private List<PurchaseCartItem> items;
    private LinearLayout lView;
    private View mTotalContainer;
    private Button mCheckoutButton;
    private Button mCallToOrderButton;
    private DialogListFragment dialogList;
    private TextView mCouponButton;
    private EditText mVoucherView;
    private String mVoucherCode = null;
    private String mItemRemovedSku;
    private String mPhone2Call = "";
    private double mItemRemovedPriceTracking = 0d;
    private long mItemRemovedQuantity;
    private double mItemRemovedRating;
    private String mItemRemovedCartValue;
    private String mItemsToCartDeepLink;
    private int selectedPosition;
    private long crrQuantity;

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

    /**
     * Get instance
     *
     * @return ShoppingCartFragment
     */
    public static ShoppingCartFragment getInstance(Bundle bundle) {
        ShoppingCartFragment fragment = new ShoppingCartFragment();
        fragment.setArguments(bundle);
        return fragment;
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
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE");
        // Save the voucher code
        if(mVoucherView != null) {
            outState.putString(ConstantsIntentExtra.ARG_1, mVoucherView.getText().toString());
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
        mCheckoutButton = (Button) view.findViewById(R.id.checkout_button);
        mCallToOrderButton = (Button) view.findViewById(R.id.checkout_call_to_order);
        mTotalContainer = view.findViewById(R.id.total_container);
        // Set nested scroll and voucher view
        mVoucherView = (EditText) view.findViewById(R.id.voucher_name);
        NestedScrollView mNestedScroll = (NestedScrollView) view.findViewById(R.id.shoppingcart_nested_scroll);
        UIUtils.scrollToViewByClick(mNestedScroll, mVoucherView);
        // Set voucher button
        mCouponButton = (TextView) view.findViewById(R.id.voucher_btn);
        mCouponButton.setOnClickListener(this);
    }

    /**
     * Show the use voucher layout
     */
    private void showUseVoucher() {
        Print.d(TAG, "SHOWING USE VOUCHER");
        mVoucherView.setText(TextUtils.isNotEmpty(mVoucherCode) ? mVoucherCode : "");
        mVoucherView.setFocusable(true);
        mVoucherView.setFocusableInTouchMode(true);
        mCouponButton.setText(getString(R.string.voucher_use));

    }

    /**
     * Show the remove voucher layout
     */
    private void showRemoveVoucher() {
        Print.d(TAG, "SHOWING REMOVE VOUCHER");
        mVoucherView.setText(mVoucherCode);
        mVoucherView.setFocusable(false);
        mVoucherView.setFocusableInTouchMode(false);
        mCouponButton.setText(getString(R.string.voucher_remove));
    }

    /**
     * Set the total value
     */
    private void setTotal(PurchaseEntity cart) {
        Print.d(TAG, "SET THE TOTAL VALUE");
        // Get views
        TextView totalValue = (TextView) mTotalContainer.findViewById(R.id.total_value);
        // Set value
        totalValue.setText(CurrencyFormatter.formatCurrency(cart.getTotal()));
        mTotalContainer.setVisibility(View.VISIBLE);
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
        if (!android.text.TextUtils.isEmpty(mVoucherView.getText()) && !mCouponButton.getText().toString().equalsIgnoreCase(getString(R.string.voucher_remove))) {
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
            String title = getString(R.string.shoppingcart_alert_header);
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
            if (getString(R.string.voucher_use).equalsIgnoreCase(mCouponButton.getText().toString())) {
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
        triggerContentEventProgress(new ShoppingCartRemoveItemHelper(), ShoppingCartRemoveItemHelper.createBundle(item.getConfigSimpleSKU(), true), this);
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
        item.setQuantity(quantity);
        mBeginRequestMillis = System.currentTimeMillis();
        //
        triggerContentEventProgress(new ShoppingCartChangeItemQuantityHelper(), ShoppingCartChangeItemQuantityHelper.createBundle(item.getConfigSimpleSKU(), quantity), this);
    }

    /**
     *
     */
    private void releaseVars() {
        lView = null;
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
                TrackerDelegator.trackLoadTiming(params);
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
                hideActivityProgress();
                break;
            case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
                hideActivityProgress();
                params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                TrackerDelegator.trackLoadTiming(params);
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
                break;
            case GET_SHOPPING_CART_ITEMS_EVENT:
                hideActivityProgress();
                PurchaseEntity purchaseEntity = (PurchaseEntity) baseResponse.getContentData();
                params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                TrackerDelegator.trackLoadTiming(params);
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
                TrackerDelegator.trackLoadTiming(params);
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
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
        if (JumiaApplication.INSTANCE.getCart() != null) {
            displayShoppingCart(JumiaApplication.INSTANCE.getCart());
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
    }

    /*

     */
    private void displayShoppingCart(PurchaseEntity cart) {
        Print.d(TAG, "displayShoppingCart");
        if(cart == null){
            showNoItems();
            return;
        }

        items = cart.getCartItems();
        if (items.size() == 0) {
            showNoItems();
        } else if(getView() == null) {
            showErrorFragment(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, this);
        } else {
            showFragmentContentContainer();
            TextView priceTotal = (TextView) getView().findViewById(R.id.price_total);
            TextView articlesCount = (TextView) getView().findViewById(R.id.articles_count);
            TextView extraCostsValue = (TextView) getView().findViewById(R.id.extra_costs_value);
            TextView vatIncludedLabel = (TextView)getView().findViewById(R.id.vat_included_label);
            TextView vatValue = (TextView) getView().findViewById(R.id.vat_value);
            View extraCostsMain = getView().findViewById(R.id.extra_costs_container);
            View shippingContainer = getView().findViewById(R.id.shipping_container);
            TextView shippingValue = (TextView)getView().findViewById(R.id.shipping_value);
            TextView voucherValue = (TextView) getView().findViewById(R.id.text_voucher);
            final View voucherContainer = getView().findViewById(R.id.voucher_info_container);

            // Get and set the cart value
            setTotal(cart);

            // GTM TRACKER
            TrackerDelegator.trackViewCart(cart.getCartCount(), cart.getPriceForTracking());

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

            // Price
            priceTotal.setText(CurrencyFormatter.formatCurrency(cart.getSubTotal()));

            if(cart.isVatLabelEnable()) {
                vatIncludedLabel.setVisibility(View.VISIBLE);
                vatValue.setVisibility(View.VISIBLE);
                vatValue.setText(CurrencyFormatter.formatCurrency(cart.getVatValue()));
                vatIncludedLabel.setText(cart.getVatLabel());
            } else {
                vatValue.setVisibility(View.GONE);
                vatIncludedLabel.setVisibility(View.GONE);
            }

            ShoppingCartUtils.setShippingRule(cart, shippingContainer, shippingValue, extraCostsMain, extraCostsValue);

            articlesCount.setText(getResources().getQuantityString(R.plurals.numberOfItems, cart.getCartCount(), cart.getCartCount()));

            lView = (LinearLayout) getView().findViewById(R.id.shoppingcart_list);
            lView.removeAllViewsInLayout();
            // Fix NAFAMZ-7848
            BigDecimal unreduced_cart_price = new BigDecimal(0);
            // reduced_cart_price = 0;
            boolean cartHasReducedItem = false;

            // TODO Validate this method
            for (int i = 0; i < items.size(); i++) {
                PurchaseCartItem item = items.get(i);
                CartItemValues values = new CartItemValues();
                values.is_checked = false;
                values.product_name = item.getName();
                values.price = CurrencyFormatter.formatCurrency(item.getPrice());
                values.product_id = 0;
                values.quantity = item.getQuantity();
                values.image = item.getImageUrl();
                values.price_disc = CurrencyFormatter.formatCurrency(item.getSpecialPrice());
                values.discount_value = (double) Math.round(item.getSavingPercentage());
                values.min_delivery_time = 0;
                values.max_delivery_time = 99;
                values.variation = item.getVariation();
                values.productSku = item.getSku();
                values.maxQuantity = item.getMaxQuantity();
                values.shop_first = item.isShopFirst();

                Print.d(TAG, "HAS VARIATION: " + values.variation + " " + item.getVariation());

                lView.addView(getView(i, lView, LayoutInflater.from(getBaseActivity()), values));
                if(!TextUtils.equals(item.getPriceString(), item.getSpecialPriceString())){
                    cartHasReducedItem = true;
                }

                // Fix NAFAMZ-7848
                unreduced_cart_price = unreduced_cart_price.add(new BigDecimal(item.getPrice() * item.getQuantity()));
                Print.e(TAG, "unreduced_cart_price= " + unreduced_cart_price);
            }

            TextView priceUnreduced = (TextView) getView().findViewById(R.id.price_unreduced);
            if (cartHasReducedItem && unreduced_cart_price.intValue() > 0) {
                priceUnreduced.setText(CurrencyFormatter.formatCurrency(unreduced_cart_price.toString()));
                priceUnreduced.setPaintFlags(priceUnreduced.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                priceUnreduced.setVisibility(View.VISIBLE);
            } else {
                priceUnreduced.setVisibility(View.INVISIBLE);
            }

            HashMap<String, String> priceRules = cart.getPriceRules();
            LinearLayout priceRulesContainer = (LinearLayout) getView().findViewById(R.id.price_rules_container);
            CheckoutStepManager.showPriceRules(getActivity(),priceRulesContainer,priceRules);

            //hideNoItems();
            TrackerDelegator.trackPage(TrackingPage.FILLED_CART, getLoadTime(), false);

        }
    }



    public View getView(final int position, ViewGroup parent, LayoutInflater mInflater, CartItemValues item) {

        View view = mInflater.inflate(R.layout.shopping_cart_product_container, parent, false);

        final Item prodItem = new Item();
        prodItem.itemValues = item;
        // Log.d( TAG, "getView: productName = " + itemValues.product_name);

        prodItem.itemName = (TextView) view.findViewById(R.id.item_name);
        prodItem.priceView = (TextView) view.findViewById(R.id.item_regprice);
        prodItem.quantityBtn = (TextView) view.findViewById(R.id.changequantity_button);
        prodItem.productView = (ImageView) view.findViewById(R.id.image_view);
        prodItem.shopFirstImage = (ImageView) view.findViewById(R.id.item_shop_first);

        prodItem.pBar = view.findViewById(R.id.image_loading_progress);
        prodItem.deleteBtn = (TextView) view.findViewById(R.id.button_delete);
        view.setTag(prodItem);

        prodItem.itemName.setText(prodItem.itemValues.product_name);
        prodItem.itemName.setSelected(true);

        String imageUrl = prodItem.itemValues.image;

        // Hide shop view image if is_shop is false
        prodItem.shopFirstImage.setVisibility((!prodItem.itemValues.shop_first || ShopSelector.isRtlShop()) ? View.GONE : View.VISIBLE);

        RocketImageLoader.instance.loadImage(imageUrl, prodItem.productView, prodItem.pBar,
                R.drawable.no_image_small);

        if (!prodItem.itemValues.price.equals(prodItem.itemValues.price_disc)) {
            prodItem.priceView.setText(prodItem.itemValues.price_disc);
            prodItem.priceView.setVisibility(View.VISIBLE);
        } else {
            prodItem.priceView.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(android.view.View.VISIBLE);
        }
        prodItem.deleteBtn.setTag(R.id.position, position);
        prodItem.deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedElements(view);
            }
        });

        prodItem.quantityBtn.setText("  " + String.valueOf(prodItem.itemValues.quantity) + "  ");
        if(prodItem.itemValues.maxQuantity > 1) {
            prodItem.quantityBtn.setEnabled(true);
            prodItem.quantityBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    prodItem.itemValues.is_checked = true;
                    showQuantityDialog(position);
                }
            });
        } else {
            prodItem.quantityBtn.setEnabled(false);
            DeviceInfoHelper.executeCodeBasedOnJellyBeanVersion(new DeviceInfoHelper.IDeviceVersionBasedCode() {
                @Override
                @SuppressLint("NewApi")
                public void highVersionCallback() {
                    prodItem.quantityBtn.setBackground(null);
                }
                @Override
                @SuppressWarnings("deprecation")
                public void lowerVersionCallback() {
                    prodItem.quantityBtn.setBackgroundDrawable(null);
                }
            });

        }

        // Save the position to process the click on item
        view.setTag(R.id.target_sku, item.productSku);
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

        dialogList = DialogListFragment.newInstance(this, listener, RestConstants.ID_CHANGE_QUANTITY,
                getString(R.string.shoppingcart_choose_quantity), quantities, (int) crrQuantity-1);
        dialogList.show(getActivity().getSupportFragmentManager(), null);
    }




    public static class CartItemValues {
        public Boolean is_checked;
        public String product_name;
        public String price;
        public String price_disc;
        public Integer product_id;
        public long quantity;
        public String image;
        public Double discount_value;
        public Integer min_delivery_time;
        public Integer max_delivery_time;
        public String variation;
        public int maxQuantity;
        public String productSku;
        public boolean shop_first;
    }

    /**
     * A representation of each item on the list
     */
    private static class Item {

        public TextView itemName;
        public TextView priceView;
        public TextView quantityBtn;
        public ImageView productView;
        public View pBar;
        public TextView deleteBtn;
        public CartItemValues itemValues;
        public ImageView shopFirstImage;

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#finalize()
         */
        @Override
        protected void finalize() throws Throwable {
            itemValues = null;
            itemName = null;
            priceView = null;
            quantityBtn = null;
            productView = null;
            pBar = null;
            deleteBtn = null;
            shopFirstImage = null;
            super.finalize();
        }
    }

}
