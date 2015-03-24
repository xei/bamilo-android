/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.objects.ShoppingCart;
import com.mobile.framework.objects.ShoppingCartItem;
import com.mobile.framework.tracking.AdjustTracker;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.tracking.gtm.GTMValues;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.DarwinRegex;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.cart.GetShoppingCartAddMultipleItemsHelper;
import com.mobile.helpers.cart.GetShoppingCartChangeItemQuantityHelper;
import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
import com.mobile.helpers.cart.GetShoppingCartRemoveItemHelper;
import com.mobile.helpers.checkout.GetNativeCheckoutAvailableHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.helpers.voucher.SetVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.preferences.CountryConfigs;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.dialogfragments.DialogListFragment;
import com.mobile.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ShoppingCartUtils;
import com.mobile.view.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ShoppingCartFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = LogTagHelper.create(ShoppingCartFragment.class);

    private final static String ID_CHANGE_QUANTITY = "id_change_quantity";

    private long mBeginRequestMillis;

    private List<ShoppingCartItem> items;

    private ArrayList<CartItemValues> itemsValues;

    private LinearLayout lView;

    private Button checkoutButton;

    private Button mCallToOrderButton;

    private DialogListFragment dialogList;

    private Button couponButton;

    private TextView voucherError;

    private EditText voucherCode;

    private String mVoucher = null;

    private boolean removeVoucher = false;

    private String itemRemoved_sku;

    private String itemRemoved_price;

    private String mPhone2Call = "";

    private boolean isCallInProgress = false;

    private boolean isRemovingAllItems = false; // Flag used to remove all items after call to order

    private double itemRemoved_price_tracking = 0d;

    private long itemRemoved_quantity;

    private double itemRemoved_rating;

    private String itemRemoved_cart_value;

    private static String cartValue = "";

    private String mItemsToCartDeepLink;

    public static class CartItemValues {
        public Boolean is_checked;
        public String product_name;
        public String price;
        public String price_disc;
        public Integer product_id;
        public long quantity;
        public String image;
        public Double discount_value;
        public long stock;
        public Integer min_delivery_time;
        public Integer max_delivery_time;
        public Map<String, String> simpleData;
        public String variation;
        public String productUrl;
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

    /**
     * Empty constructor
     */
    public ShoppingCartFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.Basket,
                R.layout.shopping_basket,
                R.string.cart_label,
                KeyboardState.ADJUST_CONTENT);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            mItemsToCartDeepLink = arguments.getString(ConstantsIntentExtra.CONTENT_URL);
            arguments.remove(ConstantsIntentExtra.CONTENT_URL);
        }
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
        Log.i(TAG, "ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        // Validate is service is available
        if (JumiaApplication.mIsBound) {
            mBeginRequestMillis = System.currentTimeMillis();
            // Case deep link
            if (!TextUtils.isEmpty(mItemsToCartDeepLink)) addItemsToCart(mItemsToCartDeepLink);
            // Case normal
            else triggerGetShoppingCart();
            // Track page
            TrackerDelegator.trackPage(TrackingPage.CART, getLoadTime(), false);
        } else {
            showFragmentErrorRetry();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
        releaseVars();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
        releaseVars();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }

    /**
     *
     */
    private void releaseVars() {
        itemsValues = null;
        lView = null;
        checkoutButton = null;
        dialogList = null;
    }

    /**
     * Trigger to get cart items validating FavouritesFragment state is completed
     * 
     * @author sergiopereira
     */
    private void triggerGetShoppingCart() {
        // Get items
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
    }

    /**
     * Get items from string
     * 
     * @author sergiopereira
     */
    private void addItemsToCart(String items) {
        String[] itemsToCart = items.split(DarwinRegex.SKU_DELIMITER);
        Log.i(TAG, "RECEIVED : " + items + " " + itemsToCart.length);
        // Create arguments to add all items to cart
        HashMap<String, String> productBySku = new HashMap<>();
        for (String sku : itemsToCart) {
            productBySku.put(sku, sku.split("-")[0]);
        }
        // Case valid deep link
        if (productBySku.size() != 0) {
            triggerAddAllItems(productBySku);
        }
        // Case invalid deep link
        else {
            triggerGetShoppingCart();
        }
    }

    /**
     *
     * @param item
     */
    private void triggerRemoveItem(ShoppingCartItem item) {

        ContentValues values = new ContentValues();
        values.put("sku", item.getConfigSimpleSKU());
        itemRemoved_sku = item.getConfigSimpleSKU();
        itemRemoved_price = item.getSpecialPriceVal().toString();
        itemRemoved_price_tracking = item.getPriceForTracking();
        itemRemoved_quantity = item.getQuantity();
        itemRemoved_rating = -1d;

        if (TextUtils.isEmpty(cartValue)) {
            TextView totalValue = (TextView) getView().findViewById(R.id.total_value);
            itemRemoved_cart_value = totalValue.toString();
        } else
            itemRemoved_cart_value = cartValue;

        if (itemRemoved_price == null) {
            itemRemoved_price = item.getPriceVal().toString();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartRemoveItemHelper.ITEM, values);
        // only show loading when removing individual items
        if (isRemovingAllItems) {
            bundle.putBoolean(GetShoppingCartRemoveItemHelper.UPDATE_CART, false);
            triggerContentEventNoLoading(new GetShoppingCartRemoveItemHelper(), bundle, null);
        } else {
            triggerContentEventProgress(new GetShoppingCartRemoveItemHelper(), bundle, this);
        }
    }

    /**
     * Trigger to get if is native checkout is available.
     */
    private void triggerIsNativeCheckoutAvailable() {
        triggerContentEventNoLoading(new GetNativeCheckoutAvailableHelper(), null, this);
    }

    /**
     * Trigger to remove the submit a voucher value.
     * @param values
     */
    private void triggerSubmitVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetVoucherHelper.VOUCHER_PARAM, values);
        triggerContentEventProgress(new SetVoucherHelper(), bundle, this);
    }

    /**
     * Trigger to remove the submitted voucher.
     * @param values - TODO
     */
    private void triggerRemoveVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RemoveVoucherHelper.VOUCHER_PARAM, values);
        triggerContentEventProgress(new RemoveVoucherHelper(), bundle, this);
    }

    /**
     * Trigger to add all items to cart (Deep link).
     * @param values - TODO
     */
    private void triggerAddAllItems(HashMap<String, String> values) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(GetShoppingCartAddMultipleItemsHelper.ADD_ITEMS, values);
        triggerContentEventProgress(new GetShoppingCartAddMultipleItemsHelper(), bundle, this);
    }

    /**
     * Set the ShoppingCart layout using inflate
     */
    public void setAppContentLayout(View view) {
        checkoutButton = (Button) view.findViewById(R.id.checkout_button);
        mCallToOrderButton = (Button) view.findViewById(R.id.checkout_call_to_order);
        voucherCode = (EditText) view.findViewById(R.id.voucher_name);
        voucherError = (TextView) view.findViewById(R.id.voucher_error_message);
        couponButton = (Button) view.findViewById(R.id.voucher_btn);
        prepareCouponView();
    }

    public void setListeners() {
        // checkoutButton.setOnClickListener(checkoutClickListener);
        checkoutButton.setOnTouchListener(new OnTouchListener() {
            private DialogGenericFragment messageDialog;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (items != null && items.size() > 0) {
                        checkMinOrderAmount();
                    } else {
                        // TODO Validate if it is necessary
                        String title = getString(R.string.shoppingcart_alert_header);
                        String message = getString(R.string.shoppingcart_alert_message_no_items);
                        String buttonText = getString(R.string.ok_label);
                        messageDialog = DialogGenericFragment.newInstance(true, false,
                                title, message, buttonText, null, new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        messageDialog.dismissAllowingStateLoss();
                                    }
                                });
                        messageDialog.show(getActivity().getSupportFragmentManager(), null);
                    }
                    break;
                }
                return false;
            }
        });

        // Get phone number from country configs
        mPhone2Call = CountryConfigs.getCountryPhoneNumber(getBaseActivity());
        // Show Call To Order if available on the device
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) && !TextUtils.isEmpty(mPhone2Call)) {
            mCallToOrderButton.setVisibility(View.VISIBLE);
            mCallToOrderButton.setSelected(true);
            mCallToOrderButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TrackerDelegator.trackCall(getBaseActivity());
                    makeCall();
                }
            });
        } else {
            mCallToOrderButton.setVisibility(View.GONE);
        }
    }

    /**
     *
     */
    private void makeCall() {
        // Displays the phone number but the user must press the Call button to begin the phone call
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhone2Call));
        if (intent.resolveActivity(getBaseActivity().getPackageManager()) != null) {
            startActivity(intent);
            isCallInProgress = true;
        }
    }

    /**
     *
     * @param bundle
     * @return
     */
    protected boolean onSuccessEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        Bundle params;

        // Update cart info
        super.handleSuccessEvent(bundle);

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

        Log.d(TAG, "onSuccessEvent: eventType = " + eventType);
        switch (eventType) {
        case ADD_VOUCHER:
            couponButton.setText(getString(R.string.voucher_remove));
            voucherError.setVisibility(View.GONE);
            hideActivityProgress();
            removeVoucher = true;
            triggerGetShoppingCart();
            return true;
        case REMOVE_VOUCHER:
            couponButton.setText(getString(R.string.voucher_use));
            voucherError.setVisibility(View.GONE);
            hideActivityProgress();
            triggerGetShoppingCart();
            removeVoucher = false;
            return true;
        case NATIVE_CHECKOUT_AVAILABLE:
            boolean isAvailable = bundle.getBoolean(Constants.BUNDLE_RESPONSE_KEY);
            if (isAvailable) {
                Log.d(TAG, "ON SUCCESS EVENT: NATIVE_CHECKOUT_AVAILABLE");
                Bundle mBundle = new Bundle();
                getBaseActivity().onSwitchFragment(FragmentType.ABOUT_YOU, mBundle, FragmentController.ADD_TO_BACK_STACK);
            } else {
                Log.d(TAG, "ON SUCCESS EVENT: NOT NATIVE_CHECKOUT_AVAILABLE");
                goToWebCheckout();
            }
            return true;
        case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
            Log.i(TAG, "code1removing and tracking" + itemRemoved_price);
            params = new Bundle();
            params.putString(TrackerDelegator.SKU_KEY, itemRemoved_sku);
            params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
            params.putDouble(TrackerDelegator.PRICE_KEY, itemRemoved_price_tracking);
            params.putLong(TrackerDelegator.QUANTITY_KEY, itemRemoved_quantity);
            params.putDouble(TrackerDelegator.RATING_KEY, itemRemoved_rating);
            params.putString(TrackerDelegator.CARTVALUE_KEY, itemRemoved_cart_value);
            TrackerDelegator.trackProductRemoveFromCart(params);
            TrackerDelegator.trackLoadTiming(params);
            if (!isRemovingAllItems) {
                displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
                hideActivityProgress();
            }
            return true;
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
            hideActivityProgress();
            //showFragmentContentContainer();
            params = new Bundle();
            params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
            TrackerDelegator.trackLoadTiming(params);
            displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
            return true;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            ShoppingCart shoppingCart = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            //showFragmentContentContainer();
            params = new Bundle();
            params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);

            TrackerDelegator.trackLoadTiming(params);

            params = new Bundle();
            params.putString(AdjustTracker.COUNTRY_ISO, JumiaApplication.SHOP_ID);
            params.putBoolean(AdjustTracker.DEVICE, getResources().getBoolean(R.bool.isTablet));
            if (JumiaApplication.CUSTOMER != null) {
                params.putParcelable(AdjustTracker.CUSTOMER, JumiaApplication.CUSTOMER);
            }
            params.putParcelable(AdjustTracker.CART, shoppingCart);

            TrackerDelegator.trackPage(TrackingPage.CART_LOADED, getLoadTime(), false);
            TrackerDelegator.trackPageForAdjust(TrackingPage.CART_LOADED, params);

            // verify if "Call to Order" was used
            if (isCallInProgress) {
                isCallInProgress = false;
                askToRemoveProductsAfterOrder(shoppingCart);
            } else {
                displayShoppingCart(shoppingCart);
            }

            return true;
        case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
            onAddItemsToShoppingCartRequestSuccess(bundle);
            break;
        default:
            //showFragmentContentContainer();
            params = new Bundle();
            params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
            TrackerDelegator.trackLoadTiming(params);
            displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
        }
        return true;
    }

    /**
     *
     */
    private void onAddItemsToShoppingCartRequestError(){
        hideActivityProgress();
        if(JumiaApplication.INSTANCE.getCart() != null)
            displayShoppingCart(JumiaApplication.INSTANCE.getCart());
        Toast.makeText(getBaseActivity(), getString(R.string.some_products_not_added), Toast.LENGTH_LONG).show();
        
    }

    /**
     *
     * @param bundle
     */
    private void onAddItemsToShoppingCartRequestSuccess(Bundle bundle){
        hideActivityProgress();
        if (bundle.containsKey(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY)) {
            ArrayList<String> notAdded = bundle.getStringArrayList(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            
            if (notAdded != null && !notAdded.isEmpty()) {
                Toast.makeText(getBaseActivity(), R.string.some_products_not_added, Toast.LENGTH_SHORT).show();
            }
        }

        getBaseActivity().updateCartInfo();
        if(JumiaApplication.INSTANCE.getCart() != null)
            displayShoppingCart(JumiaApplication.INSTANCE.getCart());
    }
    
    /**
     * Present a dialog to remove all items from cart <br>
     * (Expectly used after user clicks "Call to Order")
     * 
     * @param shoppingCart
     * @author André Lopes
     */
    private void askToRemoveProductsAfterOrder(final ShoppingCart shoppingCart) {
        // Dismiss any existing dialogs
        dismissDialogFragment();
        
        dialog = DialogGenericFragment.newInstance(true, false,
                getString(R.string.shoppingcart_dialog_title),
                getString(R.string.shoppingcart_remove_products),
                getString(R.string.yes_label),
                getString(R.string.no_label),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        // Case remove
                        if (id == R.id.button1) {
                            isRemovingAllItems = true;
                            List<ShoppingCartItem> items = new ArrayList<>(shoppingCart.getCartItems().values());
                            for (ShoppingCartItem item : items) {
                                mBeginRequestMillis = System.currentTimeMillis();
                                triggerRemoveItem(item);
                            }
                            showNoItems();
                            // Update global cart with an empty Cart
                            ShoppingCart cart = new ShoppingCart();
                            JumiaApplication.INSTANCE.setCart(cart);
                            // Update cart
                            getBaseActivity().updateCartInfo();
                        }
                        // Case continue
                        else if (id == R.id.button2) {
                            displayShoppingCart(shoppingCart);
                        }
                        dismissDialogFragment();
                    }
                });
        dialog.show(getActivity().getSupportFragmentManager(), null);
    }

    /**
     *
     * @param bundle
     * @return
     */
    protected boolean onErrorEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        hideActivityProgress();

        // Validate generic errors
        if (super.handleErrorEvent(bundle)) {
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case NATIVE_CHECKOUT_AVAILABLE:
            Log.d(TAG, "ON ERROR EVENT: NATIVE_CHECKOUT_AVAILABLE");
            goToWebCheckout();
            break;
        case ADD_VOUCHER:
        case REMOVE_VOUCHER:
            voucherCode.setText("");
            voucherError.setVisibility(View.VISIBLE);
            // voucherDivider.setBackgroundColor(R.color.red_middle);
            // hideActivityProgress();
            break;
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
            break;
        case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
            onAddItemsToShoppingCartRequestError();
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
        return true;
    }

    /*

     */
    private void displayShoppingCart(ShoppingCart cart) {
        Log.d(TAG, "displayShoppingCart");
        if(cart == null){
            showNoItems();
            return;
        }

        items = new ArrayList<>(cart.getCartItems().values());
        if (items.size() == 0) {
            showNoItems();
        } else {
            showFragmentContentContainer();
            TextView priceTotal = (TextView) getView().findViewById(R.id.price_total);
            TextView articlesCount = (TextView) getView().findViewById(R.id.articles_count);
            TextView extraCostsValue = (TextView) getView().findViewById(R.id.extra_costs_value);
            View extraCostsMain = getView().findViewById(R.id.extra_costs_container);
            View shippingContainer = getView().findViewById(R.id.shipping_container);
            TextView shippingValue = (TextView)getView().findViewById(R.id.shipping_value);
            TextView voucherValue = (TextView) getView().findViewById(R.id.text_voucher);
            View voucherContainer = getView().findViewById(R.id.voucher_info_container);
            // Get and set the cart value
            setTotal(cart);

            // GTM TRACKER
            TrackerDelegator.trackViewCart(cart.getCartCount(), cart.getPriceForTracking());

            // Set voucher
            String couponDiscount = cart.getCouponDiscount();
            String couponCope = cart.getCouponCode();
            if (!TextUtils.isEmpty(couponDiscount)) {
                double couponDiscountValue = Double.parseDouble(couponDiscount);
                if (couponDiscountValue > 0) {
                    // Fix NAFAMZ-7848
                    voucherValue.setText("- " + CurrencyFormatter.formatCurrency(new BigDecimal(couponDiscountValue).toString()));
                    voucherContainer.setVisibility(View.VISIBLE);

                    if (!TextUtils.isEmpty(couponCope)) {
                        // Change Coupon
                        changeVoucher(couponCope);
                    }
                } else {
                    voucherContainer.setVisibility(View.GONE);
                    // Clean Voucher
                    removeVoucher();
                }
            } else {
                voucherContainer.setVisibility(View.GONE);
                // Clean Voucher
                removeVoucher();
            }

            // Only convert value if it is a number
            if (CurrencyFormatter.isNumber(cart.getSubTotal())) {
                priceTotal.setText(CurrencyFormatter.formatCurrency(cart.getSubTotal()));
            } else {
                priceTotal.setText(cart.getSubTotal());
            }

            ShoppingCartUtils.setShippingRule(cart, shippingContainer, shippingValue, extraCostsMain, extraCostsValue);

            articlesCount.setText(getResources().getQuantityString(R.plurals.numberOfArticles, cart.getCartCount(), cart.getCartCount()));

            lView = (LinearLayout) getView().findViewById(R.id.shoppingcart_list);
            lView.removeAllViewsInLayout();
            itemsValues = new ArrayList<>();
            // Fix NAFAMZ-7848
            BigDecimal unreduced_cart_price = new BigDecimal(0);
            // reduced_cart_price = 0;
            boolean cartHasReducedItem = false;
            for (int i = 0; i < items.size(); i++) {
                ShoppingCartItem item = items.get(i);
                CartItemValues values = new CartItemValues();
                // values.is_in_wishlist = false;
                values.is_checked = false;
                values.product_name = item.getName();
                values.price = item.getPrice();
                values.product_id = 0;
                values.quantity = item.getQuantity();
                values.image = item.getImageUrl();
                values.price_disc = item.getSpecialPrice();
                values.discount_value = (double) Math.round(item.getSavingPercentage());
                values.stock = item.getStock();
                values.min_delivery_time = 0;
                values.max_delivery_time = 99;
                values.simpleData = item.getSimpleData();
                values.variation = item.getVariation();
                values.productUrl = item.getProductUrl();

                Log.d(TAG, "HAS VARIATION: " + values.variation + " " + item.getVariation());

                itemsValues.add(values);
                lView.addView(getView(i, lView, LayoutInflater.from(getBaseActivity()), values));
                if (!item.getPrice().equals(item.getSpecialPrice())) {
                    cartHasReducedItem = true;
                }

                // Fix NAFAMZ-7848
                unreduced_cart_price = unreduced_cart_price.add(new BigDecimal(item.getPriceVal()
                        * item.getQuantity()));
                Log.e(TAG, "unreduced_cart_price= " + unreduced_cart_price);
            }

            TextView priceUnreduced = (TextView) getView().findViewById(R.id.price_unreduced);
            if (cartHasReducedItem && unreduced_cart_price.intValue() > 0) {
                priceUnreduced.setText(CurrencyFormatter.formatCurrency(unreduced_cart_price
                        .toString()));
                priceUnreduced.setPaintFlags(priceUnreduced.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                priceUnreduced.setVisibility(View.VISIBLE);
            } else {
                priceUnreduced.setVisibility(View.INVISIBLE);
            }

            HashMap<String, String> priceRules = cart.getPriceRules();
            if (priceRules != null && priceRules.size() > 0) {
                LinearLayout priceRulesContainer = (LinearLayout) getView().findViewById(
                        R.id.price_rules_container);
                priceRulesContainer.removeAllViews();
                priceRulesContainer.setVisibility(View.VISIBLE);
                LayoutInflater mLayoutInflater = LayoutInflater.from(getBaseActivity());
                Set<String> priceRulesKeys = priceRules.keySet();
                for (String key : priceRulesKeys) {
                    View priceRuleElement = mLayoutInflater.inflate(R.layout.price_rules_element,
                            priceRulesContainer, false);
                    ((TextView) priceRuleElement.findViewById(R.id.price_rules_label)).setText(key);
                    ((TextView) priceRuleElement.findViewById(R.id.price_rules_value)).setText("-"
                            + CurrencyFormatter.formatCurrency(priceRules.get(key)));
                    priceRulesContainer.addView(priceRuleElement);
                }
            }

            //hideNoItems();
            TrackerDelegator.trackPage(TrackingPage.FILLED_CART, getLoadTime(), false);

        }
    }

    /**
     * Replace voucher and update Coupon field
     * 
     * @param voucher
     */
    private void changeVoucher(String voucher) {
        Log.d(TAG, "changeVoucher to " + voucher);
        mVoucher = voucher;
        removeVoucher = true;
        prepareCouponView();
    }

    /**
     * Clean Voucher field
     */
    private void removeVoucher() {
        Log.d(TAG, "removeVoucher");
        mVoucher = null;
        removeVoucher = false;
        // Clean Voucher field
        voucherCode.setText("");
        prepareCouponView();
    }

    /**
     * Set the total value
     * 
     * @param cart
     * @author sergiopereira
     */
    private void setTotal(ShoppingCart cart) {
        Log.d(TAG, "SET THE TOTAL VALUE");
        // Get views
        TextView totalValue = (TextView) getView().findViewById(R.id.total_value);
        View totalMain = getView().findViewById(R.id.total_container);
        // Set value
        cartValue = cart.getCartValue();
        if (!TextUtils.isEmpty(cartValue) && !cartValue.equals("null")) {
            totalValue.setText(CurrencyFormatter.formatCurrency(cartValue));
            totalMain.setVisibility(View.VISIBLE);
        } else {
            Log.w(TAG, "CART VALUES IS EMPTY");
        }
    }

    /**
     * A representation of each item on the list
     */
    private static class Item {

        public TextView itemName;
        public TextView priceView;
        public Button quantityBtn;
        public ImageView productView;
        public View pBar;
        public TextView discountPercentage;
        public TextView priceDisc;
        public TextView variancesContainer;
        public Button deleteBtn;
        public CartItemValues itemValues;

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
            discountPercentage = null;
            priceDisc = null;
            variancesContainer = null;
            deleteBtn = null;

            super.finalize();
        }
    }

    public View getView(final int position, ViewGroup parent, LayoutInflater mInflater,
            CartItemValues item) {

        View view = mInflater.inflate(R.layout.shopping_basket_product_element_container, parent,
                false);

        final Item prodItem = new Item();
        prodItem.itemValues = item;
        // Log.d( TAG, "getView: productName = " + itemValues.product_name);

        prodItem.itemName = (TextView) view.findViewById(R.id.item_name);
        prodItem.priceView = (TextView) view.findViewById(R.id.item_regprice);
        prodItem.quantityBtn = (Button) view.findViewById(R.id.changequantity_button);

        prodItem.productView = (ImageView) view.findViewById(R.id.image_view);

        prodItem.pBar = view.findViewById(R.id.image_loading_progress);
        prodItem.discountPercentage = (TextView) view.findViewById(R.id.item_percentage);
        prodItem.priceDisc = (TextView) view.findViewById(R.id.item_discount);
        prodItem.variancesContainer = (TextView) view.findViewById(R.id.variances_container);
        prodItem.deleteBtn = (Button) view.findViewById(R.id.button_delete);
        view.setTag(prodItem);

        prodItem.itemName.setText(prodItem.itemValues.product_name);
        prodItem.itemName.setSelected(true);

        String imageUrl = prodItem.itemValues.image;

        RocketImageLoader.instance.loadImage(imageUrl, prodItem.productView, prodItem.pBar,
                R.drawable.no_image_small);

        if (!prodItem.itemValues.price.equals(prodItem.itemValues.price_disc)) {
            prodItem.priceDisc.setText(prodItem.itemValues.price_disc);
            prodItem.priceDisc.setVisibility(View.VISIBLE);

            prodItem.priceView.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(View.VISIBLE);
            prodItem.priceView.setPaintFlags(prodItem.priceView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.priceView.setTextColor(getResources().getColor(R.color.grey_middlelight));

            prodItem.discountPercentage.setText("-" + prodItem.itemValues.discount_value.intValue()
                    + "%");
            prodItem.discountPercentage.setVisibility(View.VISIBLE);
        } else {
            prodItem.priceDisc.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(View.INVISIBLE);
            prodItem.discountPercentage.setVisibility(View.GONE);
        }
        prodItem.variancesContainer.setVisibility(View.GONE);
        if (prodItem.itemValues.variation != null) {

            // Map<String, String> simpleData = prodItem.itemValues.simpleData;

            String variation = prodItem.itemValues.variation;
            if (variation != null && variation.length() > 0
                    && !variation.equals("1")
                    && !variation.equals(",")
                    && !variation.equals("...")
                    && !variation.equals(".")) {
                prodItem.variancesContainer.setVisibility(View.VISIBLE);
                prodItem.variancesContainer.setText(variation);
            }
        }
        prodItem.deleteBtn.setTag(R.id.position, position);
        prodItem.deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedElements(view);
            }
        });

        prodItem.quantityBtn.setText("  " + String.valueOf(prodItem.itemValues.quantity) + "  ");
        prodItem.quantityBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prodItem.itemValues.is_checked = true;
                changeQuantityOfItem(position);
            }
        });

        // Save the position to process the click on item
        view.setTag(R.id.target_url, item.productUrl);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    goToProductDetails((String) v.getTag(R.id.target_url));
                } catch (NullPointerException e) {
                    Log.w(TAG, "WARNING: NPE ON GET CLICKED TAG");
                }
            }
        });

        return view;
    }

    /**
     * showNoItems update the layout when basket has no items
     */
    public void showNoItems() {
        showFragmentEmpty(R.string.order_no_items, R.drawable.img_emptycart,
                R.string.continue_shopping, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getBaseActivity().onSwitchFragment(FragmentType.HOME,
                                FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                    }
                });
        getBaseActivity().hideKeyboard();
        TrackerDelegator.trackPage(TrackingPage.EMPTY_CART, getLoadTime(), false);
    }

    /**
     * Function to redirect to the selected product details.
     * 
     * @param productUrl
     */
    private void goToProductDetails(String productUrl) {
        // Log.d(TAG, "CART COMPLETE PRODUCT URL: " + items.get(position).getProductUrl());
        if (TextUtils.isEmpty(productUrl))
            return;
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, productUrl);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcart_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    private void goToWebCheckout() {
        Log.d(TAG, "GOTO WEB CHECKOUT");
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE,
                FragmentType.CHECKOUT_BASKET);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    private void checkMinOrderAmount() {
        TrackerDelegator.trackCheckout(items);

        String restbase = getResources().getString(R.string.global_server_api_version);
        if (restbase != null) {
            if (restbase.contains("mobapi/v1")) {
                triggerIsNativeCheckoutAvailable();
            } else {
                goToWebCheckout();
            }
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

    public void changeQuantityOfItem(final int position) {
        ArrayList<String> quantities = new ArrayList<>();
        long stock = items.get(position).getStock();
        int maxQuantity = items.get(position).getMaxQuantity();
        long actualMaxQuantity = stock < maxQuantity ? stock : maxQuantity;
        for (int i = 0; i <= actualMaxQuantity; i++) {
            quantities.add(String.valueOf(i));
        }
        long crrQuantity = items.get(position).getQuantity();
        OnDialogListListener listener = new OnDialogListListener() {
            @Override
            public void onDialogListItemSelect(int quantity, String value) {
                changeQuantityOfItem(position, quantity);
                if(dialogList != null) {
                    dialogList.dismissAllowingStateLoss();
                }
            }
        };

        dialogList = DialogListFragment.newInstance(this, listener, ID_CHANGE_QUANTITY,
                getString(R.string.shoppingcart_choose_quantity), quantities, (int) crrQuantity);
        dialogList.show(getActivity().getSupportFragmentManager(), null);
    }

    public void changeQuantityOfItem(int position, int quantity) {
        trackAddToCartGTM(items.get(position), quantity);
        items.get(position).setQuantity(quantity);
        mBeginRequestMillis = System.currentTimeMillis();
        changeItemQuantityInShoppingCart(items);
    }

    private void trackAddToCartGTM(ShoppingCartItem item, int quantity) {
        try {
            double prods = item.getQuantity();
            Bundle params = new Bundle();

            params.putString(TrackerDelegator.SKU_KEY, item.getConfigSimpleSKU());

            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
            params.putDouble(TrackerDelegator.PRICE_KEY, item.getPriceForTracking());
            params.putLong(TrackerDelegator.QUANTITY_KEY, 1);
            params.putDouble(TrackerDelegator.RATING_KEY, -1d);
            params.putString(TrackerDelegator.NAME_KEY, item.getName());

            params.putString(TrackerDelegator.CARTVALUE_KEY, itemRemoved_cart_value);

            if (quantity > prods) {
                prods = quantity - prods;
                params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.SHOPPINGCART);
                for (int i = 0; i < prods; i++) {
                    TrackerDelegator.trackProductAddedToCart(params);
                }
            } else {
                prods = prods - quantity;
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                for (int i = 0; i < prods; i++) {
                    TrackerDelegator.trackProductRemoveFromCart(params);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void changeItemQuantityInShoppingCart(List<ShoppingCartItem> items) {
        Bundle bundle = new Bundle();
        ContentValues values = new ContentValues();
        for (ShoppingCartItem item : items) {
            values.put(GetShoppingCartChangeItemQuantityHelper.ITEM_QTY + item.getConfigSimpleSKU(), String.valueOf(item.getQuantity()));
        }
        bundle.putParcelable(GetShoppingCartChangeItemQuantityHelper.CART_ITEMS, values);
        triggerContentEventProgress(new GetShoppingCartChangeItemQuantityHelper(), bundle, this);
    }


    private void prepareCouponView() {
        if (!TextUtils.isEmpty(mVoucher)) {
            voucherCode.setText(mVoucher);
            voucherCode.setFocusable(false);
        } else {
            voucherCode.setFocusable(true);
            voucherCode.setFocusableInTouchMode(true);
        }

        if (removeVoucher) {
            couponButton.setText(getString(R.string.voucher_remove));
        }
        couponButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mVoucher = voucherCode.getText().toString();
                getBaseActivity().hideKeyboard();
                if (!TextUtils.isEmpty(mVoucher)) {
                    ContentValues mContentValues = new ContentValues();
                    mContentValues.put(SetVoucherHelper.VOUCHER_PARAM, mVoucher);
                    Log.i(TAG, "code1coupon : " + mVoucher);
                    if (getString(R.string.voucher_use).equalsIgnoreCase(couponButton.getText().toString())) {
                        triggerSubmitVoucher(mContentValues);
                    } else {
                        triggerRemoveVoucher(mContentValues);
                    }
                } else {
                    Toast.makeText(getBaseActivity(), getString(R.string.voucher_error_message), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickErrorButton(android.view.View)
     */
    @Override
    protected void onClickErrorButton(View view) {
        super.onClickErrorButton(view);
        onResume();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onRetryRequest(com.mobile.framework.utils.EventType)
     */
    @Override
    protected void onRetryRequest(EventType eventType) {
        onResume();
    }

    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }

    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }
    
}
