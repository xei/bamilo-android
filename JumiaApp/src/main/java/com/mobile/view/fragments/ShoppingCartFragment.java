package com.mobile.view.fragments;

import android.annotation.SuppressLint;
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
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DarwinRegex;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
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
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sergiopereira
 *
 */
public class ShoppingCartFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = ShoppingCartFragment.class.getSimpleName();

    private final static String ID_CHANGE_QUANTITY = "id_change_quantity";
    private static final String cartValue = "";
    private long mBeginRequestMillis;
    private List<PurchaseCartItem> items;
    private LinearLayout lView;
    private Button checkoutButton;
    private Button mCallToOrderButton;
    private DialogListFragment dialogList;
    private TextView couponButton;
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
    private String mItemsToCartDeepLink;
    private NestedScrollView mNestedScroll;

    /**
     * Empty constructor
     */
    public ShoppingCartFragment() {
        super(EnumSet.of( MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
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

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE");
    }

    /**
     *
     */
    private void releaseVars() {
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
        Print.i(TAG, "RECEIVED : " + items + " " + itemsToCart.length);
        // Create arguments to add all items to cart
        HashMap<String, String> productBySku = new HashMap<>();
        for (String simpleSku : itemsToCart) {
            // sku -> simple sku
            productBySku.put(simpleSku.split("-")[0], simpleSku);
        }
        // Case valid deep link
        if (!productBySku.isEmpty()) {
            triggerAddAllItems(productBySku);
        }
        // Case invalid deep link
        else {
            triggerGetShoppingCart();
        }
    }

    /**
     *
     */
    private void triggerRemoveItem(PurchaseCartItem item) {

        ContentValues values = new ContentValues();
        values.put("sku", item.getConfigSimpleSKU());
        itemRemoved_sku = item.getConfigSimpleSKU();
        itemRemoved_price = item.getSpecialPriceVal().toString();
        itemRemoved_price_tracking = item.getPriceForTracking();
        itemRemoved_quantity = item.getQuantity();
        itemRemoved_rating = -1d;

        // TODO Validate this
        if (TextUtils.isEmpty(cartValue)) {
            TextView totalValue = (TextView) getView().findViewById(R.id.total_value);
            itemRemoved_cart_value = totalValue.toString();
        } else
            itemRemoved_cart_value = cartValue;

        if (itemRemoved_price == null) {
            itemRemoved_price = item.getPriceVal().toString();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        // only show loading when removing individual items
        if (isRemovingAllItems) {
            bundle.putBoolean(ShoppingCartRemoveItemHelper.UPDATE_CART, false);
            triggerContentEventNoLoading(new ShoppingCartRemoveItemHelper(), bundle, null);
        } else {
            triggerContentEventProgress(new ShoppingCartRemoveItemHelper(), bundle, this);
        }
    }

    /**
     * Trigger to remove the submit a voucher value.
     */
    private void triggerSubmitVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventProgress(new AddVoucherHelper(), bundle, this);
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
    private void triggerAddAllItems(HashMap<String, String> values) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ShoppingCartAddMultipleItemsHelper.ADD_ITEMS, values);
        triggerContentEventProgress(new ShoppingCartAddMultipleItemsHelper(), bundle, this);
    }

    /**
     * Set the ShoppingCart layout using inflate
     */
    public void setAppContentLayout(View view) {
        checkoutButton = (Button) view.findViewById(R.id.checkout_button);
        mCallToOrderButton = (Button) view.findViewById(R.id.checkout_call_to_order);
        voucherCode = (EditText) view.findViewById(R.id.voucher_name);
        voucherError = (TextView) view.findViewById(R.id.voucher_error_message);
        couponButton = (TextView) view.findViewById(R.id.voucher_btn);
        mNestedScroll = (NestedScrollView) view.findViewById(R.id.shoppingcart_nested_scroll);
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
                            TrackerDelegator.trackCheckout(items);

                            Bundle bundle = new Bundle();
                            bundle.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, true);
                            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);

                        } else {
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
        mPhone2Call = CountryPersistentConfigs.getCountryPhoneNumber(getBaseActivity());
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
     */
    protected boolean onSuccessEvent(BaseResponse baseResponse) {


        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        Bundle params;

        // Update cart info
        super.handleSuccessEvent(baseResponse);

        EventType eventType = baseResponse.getEventType();

        Print.d(TAG, "onSuccessEvent: eventType = " + eventType);
        switch (eventType) {
            case ADD_VOUCHER:
                PurchaseEntity addVoucherPurchaseEntity = (PurchaseEntity) baseResponse.getMetadata().getData();
                couponButton.setText(getString(R.string.voucher_remove));
                voucherError.setVisibility(View.GONE);
                hideActivityProgress();
                removeVoucher = true;
                displayShoppingCart(addVoucherPurchaseEntity);
                return true;
            case REMOVE_VOUCHER:
                PurchaseEntity removeVoucherPurchaseEntity = (PurchaseEntity) baseResponse.getMetadata().getData();
                couponButton.setText(getString(R.string.voucher_use));
                voucherError.setVisibility(View.GONE);
                hideActivityProgress();
                removeVoucher = false;
                displayShoppingCart(removeVoucherPurchaseEntity);
                return true;
            case REMOVE_ITEM_FROM_SHOPPING_CART_EVENT:
                Print.i(TAG, "code1removing and tracking" + itemRemoved_price);
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
                    displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
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
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
                return true;
            case GET_SHOPPING_CART_ITEMS_EVENT:
                //alexandrapires: loading dismiss
                hideActivityProgress();
                PurchaseEntity purchaseEntity = (PurchaseEntity) baseResponse.getMetadata().getData();
                //showFragmentContentContainer();
                params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);

                TrackerDelegator.trackLoadTiming(params);

                params.clear();
                params.putParcelable(AdjustTracker.CART, purchaseEntity);
                TrackerDelegator.trackPage(TrackingPage.CART_LOADED, getLoadTime(), false);
                TrackerDelegator.trackPageForAdjust(TrackingPage.CART_LOADED, params);

                // verify if "Call to Order" was used
//                if (isCallInProgress) {
//                    isCallInProgress = false;
//                    askToRemoveProductsAfterOrder(purchaseEntity);
//                } else {
                    displayShoppingCart(purchaseEntity);
//                }

                return true;
            case ADD_ITEMS_TO_SHOPPING_CART_EVENT:
                onAddItemsToShoppingCartRequestSuccess(baseResponse);
                break;
            default:
                //showFragmentContentContainer();
                params = new Bundle();
                params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
                params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
                TrackerDelegator.trackLoadTiming(params);
                displayShoppingCart((PurchaseEntity) baseResponse.getMetadata().getData());
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
        getBaseActivity().warningFactory.showWarning(WarningFactory.ERROR_MESSAGE, getString(R.string.some_products_not_added));

    }

    /**
     *
     */
    private void onAddItemsToShoppingCartRequestSuccess(BaseResponse baseResponse){
        hideActivityProgress();
        ShoppingCartAddMultipleItemsHelper.AddMultipleStruct addMultipleStruct = (ShoppingCartAddMultipleItemsHelper.AddMultipleStruct) baseResponse.getMetadata().getData();

        if (addMultipleStruct.getErrorMessages() != null) {
            ArrayList<String> notAdded = addMultipleStruct.getErrorMessages();
            if (!notAdded.isEmpty()) {
                getBaseActivity().warningFactory.showWarning(WarningFactory.ERROR_MESSAGE, getString(R.string.some_products_not_added));
            }
        }

        getBaseActivity().updateCartInfo();
        if (JumiaApplication.INSTANCE.getCart() != null) {
            displayShoppingCart(JumiaApplication.INSTANCE.getCart());
        }
    }

    /**
     * Present a dialog to remove all items from cart <br>
     * (Expectly used after user clicks "Call to Order")
     */
    private void askToRemoveProductsAfterOrder(final PurchaseEntity purchaseEntity) {
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
                            List<PurchaseCartItem> items = new ArrayList<>(purchaseEntity.getCartItems());
                            for (PurchaseCartItem item : items) {
                                mBeginRequestMillis = System.currentTimeMillis();
                                triggerRemoveItem(item);
                            }
                            showNoItems();
                            // Update global cart with an empty Cart
                            PurchaseEntity cart = new PurchaseEntity();
                            JumiaApplication.INSTANCE.setCart(cart);
                            // Update cart
                            getBaseActivity().updateCartInfo();
                        }
                        // Case continue
                        else if (id == R.id.button2) {
                            displayShoppingCart(purchaseEntity);
                        }
                        dismissDialogFragment();
                    }
                });
        dialog.show(getActivity().getSupportFragmentManager(), null);
    }

    /**
     *
     */
    protected boolean onErrorEvent(BaseResponse baseResponse) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        hideActivityProgress();

        // Validate generic errors
        if (super.handleErrorEvent(baseResponse)) {
            return true;
        }

        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
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
            View voucherRemove = getView().findViewById(R.id.basket_voucher_remove);

            TextView voucherLabel = (TextView) getView().findViewById(R.id.basket_voucher_label);
            // Get and set the cart value
            setTotal(cart);

            // GTM TRACKER
            TrackerDelegator.trackViewCart(cart.getCartCount(), cart.getPriceForTracking());

            // Set voucher
            if (cart.hasCouponDiscount()) {
                double couponDiscountValue = cart.getCouponDiscount();
                if (couponDiscountValue >= 0) {
                    voucherValue.setText("- " + CurrencyFormatter.formatCurrency(new BigDecimal(couponDiscountValue).toString()));
                    voucherContainer.setVisibility(View.VISIBLE);
                    voucherRemove.setVisibility(View.VISIBLE);
                    voucherRemove.setOnClickListener(new android.view.View.OnClickListener() {
                        @Override
                        public void onClick(android.view.View v) {
                            voucherContainer.setVisibility(View.GONE);
                            triggerRemoveVoucher();
                            // Clean Voucher
                            removeVoucher();
                            couponButton.setText(getString(R.string.voucher_use));
                        }
                    });
                    // Change Coupon
                    changeVoucher(cart.getCouponCode());
                    voucherLabel.setText(getString(R.string.my_order_voucher_label) + " " + voucherCode.getText());
                } else {
                    voucherContainer.setVisibility(View.GONE);
                    couponButton.setText(getString(R.string.voucher_use));
                    // Clean Voucher
                    removeVoucher();
                }
            } else {
                voucherContainer.setVisibility(View.GONE);
                // Clean Voucher
                removeVoucher();
            }

            // Price
            priceTotal.setText(CurrencyFormatter.formatCurrency(cart.getSubTotal()));

            if(cart.isVatLabelEnable()) {
                vatValue.setVisibility(View.VISIBLE);
                vatValue.setText(CurrencyFormatter.formatCurrency(cart.getVatValue()));
                vatIncludedLabel.setText(getString(R.string.vat_string));
            } else {
                vatValue.setVisibility(View.GONE);
                vatIncludedLabel.setText(getString(R.string.string_vat_included));
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
                values.simpleData = item.getSimpleData();
                values.variation = item.getVariation();
                values.productSku = item.getConfigSKU();
                values.productUrl = item.getProductUrl();
                values.maxQuantity = item.getMaxQuantity();

                Print.d(TAG, "HAS VARIATION: " + values.variation + " " + item.getVariation());

                lView.addView(getView(i, lView, LayoutInflater.from(getBaseActivity()), values));
                if (!item.getPrice().equals(item.getSpecialPrice())) {
                    cartHasReducedItem = true;
                }

                // Fix NAFAMZ-7848
                unreduced_cart_price = unreduced_cart_price.add(new BigDecimal(item.getPriceVal() * item.getQuantity()));
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

    /**
     * Replace voucher and update Coupon field
     */
    private void changeVoucher(String voucher) {
        Print.d(TAG, "changeVoucher to " + voucher);
        mVoucher = voucher;
        removeVoucher = true;
        prepareCouponView();
    }

    /**
     * Clean Voucher field
     */
    private void removeVoucher() {
        Print.d(TAG, "removeVoucher");
        mVoucher = null;
        removeVoucher = false;
        // Clean Voucher field
        voucherCode.setText("");
        prepareCouponView();
    }

    /**
     * Set the total value
     * @author sergiopereira
     */
    private void setTotal(PurchaseEntity cart) {
        Print.d(TAG, "SET THE TOTAL VALUE");
        // Get views
        TextView totalValue = (TextView) getView().findViewById(R.id.total_value);
        View totalMain = getView().findViewById(R.id.total_container);
        // Set value
        totalValue.setText(CurrencyFormatter.formatCurrency(cart.getTotal()));
        totalMain.setVisibility(View.VISIBLE);
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

        prodItem.pBar = view.findViewById(R.id.image_loading_progress);
//        prodItem.discountPercentage = (TextView) view.findViewById(R.id.item_percentage);
//        prodItem.priceDisc = (TextView) view.findViewById(R.id.item_discount);
//        prodItem.variancesContainer = (TextView) view.findViewById(R.id.variances_container);
        prodItem.deleteBtn = (TextView) view.findViewById(R.id.button_delete);
        view.setTag(prodItem);

        prodItem.itemName.setText(prodItem.itemValues.product_name);
        prodItem.itemName.setSelected(true);

        String imageUrl = prodItem.itemValues.image;

        RocketImageLoader.instance.loadImage(imageUrl, prodItem.productView, prodItem.pBar,
                R.drawable.no_image_small);

        if (!prodItem.itemValues.price.equals(prodItem.itemValues.price_disc)) {
            prodItem.priceView.setText(prodItem.itemValues.price_disc);
            prodItem.priceView.setVisibility(View.VISIBLE);
//            prodItem.priceView.setPaintFlags(prodItem.priceView.getPaintFlags()
//                    | Paint.STRIKE_THRU_TEXT_FLAG);
//            prodItem.priceView.setTextColor(getResources().getColor(R.color.grey_middlelight));

//            prodItem.discountPercentage.setText("-" + prodItem.itemValues.discount_value.intValue()
//                    + "%");
//            prodItem.discountPercentage.setVisibility(View.VISIBLE);
        } else {
            prodItem.priceView.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(android.view.View.VISIBLE);
//            prodItem.discountPercentage.setVisibility(View.GONE);
        }
//        prodItem.variancesContainer.setVisibility(View.GONE);
//        if (prodItem.itemValues.variation != null) {
//            // Map<String, String> simpleData = prodItem.itemValues.simpleData;
//            String variation = prodItem.itemValues.variation;
//            if (variation.length() > 0
//                    && !variation.equals("1")
//                    && !variation.equals(",")
//                    && !variation.equals("...")
//                    && !variation.equals(".")) {
//                prodItem.variancesContainer.setVisibility(View.VISIBLE);
//                prodItem.variancesContainer.setText(variation);
//            }
//        }
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
                    changeQuantityOfItem(position);
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
     * showNoItems update the layout when basket has no items
     */
    public void showNoItems() {
        showErrorFragment(ErrorLayoutFactory.CART_EMPTY_LAYOUT, new OnClickListener() {
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
     */
    private void goToProductDetails(String sku) {
        if (!TextUtils.isEmpty(sku)) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.PRODUCT_SKU, sku);
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

    public void changeQuantityOfItem(final int position) {
        ArrayList<String> quantities = new ArrayList<>();
        for (int i = 1; i <= items.get(position).getMaxQuantity(); i++) {
            quantities.add(String.valueOf(i));
        }
        final long crrQuantity = items.get(position).getQuantity();
        OnDialogListListener listener = new OnDialogListListener() {
            @Override
            public void onDialogListItemSelect(int quantity, String value) {
                if(quantity != crrQuantity -1){
                    changeQuantityOfItem(position, quantity+1);
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

        dialogList = DialogListFragment.newInstance(this, listener, ID_CHANGE_QUANTITY,
                getString(R.string.shoppingcart_choose_quantity), quantities, (int) crrQuantity-1);
        dialogList.show(getActivity().getSupportFragmentManager(), null);
    }

    public void changeQuantityOfItem(int position, int quantity) {
        trackAddToCartGTM(items.get(position), quantity);
        items.get(position).setQuantity(quantity);
        mBeginRequestMillis = System.currentTimeMillis();
        changeItemQuantityInShoppingCart(items);
    }

    private void trackAddToCartGTM(PurchaseCartItem item, int quantity) {
        try {
            double prods = item.getQuantity();
            Bundle params = new Bundle();

            params.putString(TrackerDelegator.SKU_KEY, item.getConfigSimpleSKU());

            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
            params.putDouble(TrackerDelegator.PRICE_KEY, item.getPriceForTracking());
            params.putLong(TrackerDelegator.QUANTITY_KEY, 1);
            params.putDouble(TrackerDelegator.RATING_KEY, -1d);
            params.putString(TrackerDelegator.NAME_KEY, item.getName());
            params.putString(TrackerDelegator.CATEGORY_KEY, item.getCategoriesIds());
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

    private void changeItemQuantityInShoppingCart(List<PurchaseCartItem> items) {
        Bundle bundle = new Bundle();
        ContentValues values = new ContentValues();
        for (PurchaseCartItem item : items) {
            values.put(ShoppingCartChangeItemQuantityHelper.ITEM_QTY + item.getConfigSimpleSKU(), String.valueOf(item.getQuantity()));
        }
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventProgress(new ShoppingCartChangeItemQuantityHelper(), bundle, this);
    }

    private void prepareCouponView() {
        if (!TextUtils.isEmpty(mVoucher)) {
            voucherCode.setText(mVoucher);
            voucherCode.setFocusable(false);
        } else {
            voucherCode.setFocusable(true);
            voucherCode.setFocusableInTouchMode(true);
        }
        scrollToViewByClick(mNestedScroll, voucherCode);

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
                    mContentValues.put(AddVoucherHelper.VOUCHER_PARAM, mVoucher);
                    Print.i(TAG, "code1coupon : " + mVoucher);
                    if (getString(R.string.voucher_use).equalsIgnoreCase(couponButton.getText().toString())) {
                        triggerSubmitVoucher(mContentValues);
                    } else {
                        triggerRemoveVoucher();
                    }
                } else {
                    getBaseActivity().warningFactory.showWarning(WarningFactory.ERROR_MESSAGE, getString(R.string.voucher_error_message));
                }
            }
        });
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        onSuccessEvent(baseResponse);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        onErrorEvent(baseResponse);
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
        public Map<String, String> simpleData;
        public String variation;
        public String productUrl;
        public int maxQuantity;
        public String productSku;
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
//        public TextView discountPercentage;
//        public TextView priceDisc;
//        public TextView variancesContainer;
        public TextView deleteBtn;
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
//            discountPercentage = null;
//            priceDisc = null;
//            variancesContainer = null;
            deleteBtn = null;

            super.finalize();
        }
    }

}
