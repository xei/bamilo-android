/**
 * 
 */
package pt.rocket.view.fragments;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.EditText;
import pt.rocket.components.customfontviews.TextView;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.tracking.AdjustTracker;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.cart.GetShoppingCartChangeItemQuantityHelper;
import pt.rocket.helpers.cart.GetShoppingCartItemsHelper;
import pt.rocket.helpers.cart.GetShoppingCartRemoveItemHelper;
import pt.rocket.helpers.checkout.GetNativeCheckoutAvailableHelper;
import pt.rocket.helpers.voucher.RemoveVoucherHelper;
import pt.rocket.helpers.voucher.SetVoucherHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ShoppingCartFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = LogTagHelper.create(ShoppingCartFragment.class);

    private final static String ID_CHANGE_QUANTITY = "id_change_quantity";

    private final static int FAVOURITE_DELAY = 2000;

    private Handler triggerHander;

    private static ShoppingCartFragment shoppingCartFragment;

    private long mBeginRequestMillis;

    private List<ShoppingCartItem> items;

    private ArrayList<CartItemValues> itemsValues;

    private BigDecimal unreduced_cart_price;

    /**
     * Boolean to the define the activities type: false - ShoppingBasket | true . Checkout
     */
    public boolean isShoppingBasket = true;

    /**
     * lView Basket grid view
     */
    private LinearLayout lView;

    /**
     * Button container so we can control positioning in the screen
     */

    private Button checkoutButton;

    private Button mCallToOrderButton;

    /**
     * dialogList for DialogList
     */
    private DialogListFragment dialogList;

    // Voucher
    private Button couponButton;

    private TextView voucherError;

    EditText voucherCode;

    private String mVoucher = null;

    // private boolean noPaymentNeeded = false;

    private boolean removeVoucher = false;

    private String itemRemoved_sku;

    private String itemRemoved_price;

    private String mPhone2Call = "";

    private boolean isCallInProgress = false;

    private boolean isRemovingAllItems = false;
    
    private double itemRemoved_price_tracking = 0d;
    
    private long itemRemoved_quantity;
    
    private double itemRemoved_rating;
    
    private String itemRemoved_cart_value;
    
    private static String cartValue = "";

    public static class CartItemValues {
        // public Boolean is_in_wishlist;
        public Boolean is_checked;
        public String product_name;
        public String price;
        public String price_disc;
        public Integer product_id;
        public long quantity;
        public String image;
        public String simple_product_sku;
        public String product_sku;
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
     * @return
     */
    public static ShoppingCartFragment getInstance() {
        if (shoppingCartFragment == null) {
            shoppingCartFragment = new ShoppingCartFragment();
        }
        return shoppingCartFragment;
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
        // R.string.shoppingcart_title
        this.setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
        setAppContentLayout();

        // EventManager.getSingleton().triggerRequestEvent(new RequestEvent(
        // EventType.GET_MIN_ORDER_AMOUNT));

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        //Validate is service is available
        if (JumiaApplication.mIsBound) {
            Log.i(TAG, "ON RESUME");
            mBeginRequestMillis = System.currentTimeMillis();
            triggerGetShoppingCart();
            setListeners();
            TrackerDelegator.trackPage(TrackingPage.CART, getLoadTime(), false);
        } else {
            showFragmentRetry(this);
        }
    }

    /**
     * Trigger to get cart items validating FavouritesFragment state is completed
     * 
     * @author sergiopereira
     */
    private void triggerGetShoppingCart() {
        // Check if FavouritesFragment is complete
        if (!FavouritesFragment.isOnAddingAllItemsToCart) {
            // Get items
            triggerContentEvent(new GetShoppingCartItemsHelper(), null, responseCallback);
        } else {
            // Show loading when 
            showFragmentLoading();
            // Singleton for handler
            if (triggerHander == null) triggerHander = new Handler();
            // Remove peding posts
            else triggerHander.removeCallbacks(triggerRunnable);
            // Remove trigger
            triggerHander.postDelayed(triggerRunnable, FAVOURITE_DELAY);
        }
    }

    /**
     * Runnable used to get the cart items after FavouritesFragment is complete action
     * 
     * @author sergiopereira
     */
    Runnable triggerRunnable = new Runnable() {
        @Override
        public void run() {
            triggerGetShoppingCart();
        }
    };

    private void triggerRemoveItem(ShoppingCartItem item) {
        ContentValues values = new ContentValues();
        values.put("sku", item.getConfigSimpleSKU());
        itemRemoved_sku = item.getConfigSimpleSKU();
        itemRemoved_price = item.getSpecialPriceVal().toString();
        itemRemoved_price_tracking = item.getPriceForTracking();
        itemRemoved_quantity = item.getQuantity();
        itemRemoved_rating = -1d;
        
        if(TextUtils.isEmpty(cartValue)){
            TextView totalValue = (TextView) getView().findViewById(R.id.total_value);
            itemRemoved_cart_value = totalValue.toString();
        } else itemRemoved_cart_value = cartValue;
        
        if (itemRemoved_price == null) {
            itemRemoved_price = item.getPriceVal().toString();
        }
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartRemoveItemHelper.ITEM, values);
        // only show loading when removing individual items
        if (isRemovingAllItems) {
            bundle.putBoolean(GetShoppingCartRemoveItemHelper.UPDATE_CART, false);
            triggerContentEventWithNoLoading(new GetShoppingCartRemoveItemHelper(), bundle, null);
        } else {
            triggerContentEventProgress(new GetShoppingCartRemoveItemHelper(), bundle, responseCallback);
        }
    }

    private void triggerIsNativeCheckoutAvailable() {
        triggerContentEventWithNoLoading(new GetNativeCheckoutAvailableHelper(), null, responseCallback);
    }

    private void triggerSubmitVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetVoucherHelper.VOUCHER_PARAM, values);
        triggerContentEventProgress(new SetVoucherHelper(), bundle, responseCallback);
    }

    private void triggerRemoveVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RemoveVoucherHelper.VOUCHER_PARAM, values);
        triggerContentEventProgress(new RemoveVoucherHelper(), bundle, responseCallback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
        releaseVars();
        System.gc();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        releaseVars();
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");
    }

    private void releaseVars() {
        shoppingCartFragment = null;

        itemsValues = null;

        lView = null;

        checkoutButton = null;

        dialogList = null;
    }

    /**
     * Set the ShoppingCart layout using inflate
     */
    public void setAppContentLayout() {
        if (getView() == null) {
            return;
        }
        checkoutButton = (Button) getView().findViewById(R.id.checkout_button);
        mCallToOrderButton = (Button) getView().findViewById(R.id.checkout_call_to_order);
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
                        messageDialog = DialogGenericFragment.newInstance(false, true, false, title, message, buttonText, null, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                messageDialog.dismiss();
                            }
                        });
                        messageDialog.show(getActivity().getSupportFragmentManager(), null);
                    }
                    break;
                }
                return false;
            }
        });

        // Show Call To Order if available on the device
        PackageManager pm = getActivity().getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            setCallPhone();
            mCallToOrderButton.setVisibility(View.VISIBLE);
            mCallToOrderButton.setSelected(true);
            mCallToOrderButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String user_id = "";
                    if (JumiaApplication.CUSTOMER != null && JumiaApplication.CUSTOMER.getIdAsString() != null) {
                        user_id = JumiaApplication.CUSTOMER.getIdAsString();
                    }
                    TrackerDelegator.trackCall(getActivity().getApplicationContext(), user_id, JumiaApplication.SHOP_NAME);
                    makeCall();
                }
            });
        } else {
            mCallToOrderButton.setVisibility(View.GONE);
            // Change buttonsContainer height from two vertical buttons to height to accomodate a single button
            LinearLayout buttonsContainer = (LinearLayout) getView().findViewById(R.id.shopping_basket_buttons_container);
            buttonsContainer.getLayoutParams().height = getResources().getDimensionPixelSize(R.dimen.button_container_height);
        }
    }

    private void makeCall() {
        // Displays the phone number but the user must press the Call button to begin the phone call
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mPhone2Call));
        if (intent.resolveActivity(getBaseActivity().getPackageManager()) != null) {
            startActivity(intent);
            isCallInProgress = true;
        }
    }

    private void setCallPhone() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, "");
        if ("".equalsIgnoreCase(mPhone2Call)) {
            mPhone2Call = getString(R.string.call_to_order_number);
        }
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        
        Bundle params;
        
        // Update cart info
        getBaseActivity().handleSuccessEvent(bundle);

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        Log.d(TAG, "onSuccessEvent: eventType = " + eventType);
        switch (eventType) {
        case ADD_VOUCHER:
            couponButton.setText(getString(R.string.voucher_remove));
            voucherError.setVisibility(View.GONE);
            // voucherDivider.setBackgroundColor(R.color.grey_dividerlight);
            hideActivityProgress();
            // noPaymentNeeded = false;
            removeVoucher = true;
            triggerGetShoppingCart();
            return true;
        case REMOVE_VOUCHER:
            // noPaymentNeeded = false;
            couponButton.setText(getString(R.string.voucher_use));
            voucherError.setVisibility(View.GONE);
            // voucherDivider.setBackgroundColor(R.color.grey_dividerlight);
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
                showFragmentContentContainer();
                displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
                hideActivityProgress();
            }
            return true;
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
            hideActivityProgress();
            showFragmentContentContainer();
            params = new Bundle();
            params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);

            TrackerDelegator.trackLoadTiming(params);
            displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
            return true;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            ShoppingCart shoppingCart = (ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            showFragmentContentContainer();
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
        default:
            showFragmentContentContainer();
            params = new Bundle();
            params.putInt(TrackerDelegator.LOCATION_KEY, R.string.gshoppingcart);
            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);

            TrackerDelegator.trackLoadTiming(params);
            displayShoppingCart((ShoppingCart) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY));
        }
        return true;
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
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = DialogGenericFragment.newInstance(true, true, false,
                getString(R.string.shoppingcart_dialog_title),
                getString(R.string.shoppingcart_remove_products),
                getString(R.string.yes_label),
                getString(R.string.no_label),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            
                            isRemovingAllItems = true;

                            List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(shoppingCart.getCartItems().values());

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
                        } else if (id == R.id.button2) {
                            displayShoppingCart(shoppingCart);
                        }

                        dialog.dismiss();
                    }
                });
        dialog.show(getActivity().getSupportFragmentManager(), null);
    }

    protected boolean onErrorEvent(Bundle bundle) {
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
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
            hideActivityProgress();
            break;
        case CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT:
            hideActivityProgress();
            break;
        default:
            break;
        }
        if (getBaseActivity().handleErrorEvent(bundle)) {
            return true;
        }

        mBeginRequestMillis = System.currentTimeMillis();
        return true;
    }

    private void displayShoppingCart(ShoppingCart cart) {
        Log.d(TAG, "displayShoppingCart");
        TextView priceTotal = (TextView) getView().findViewById(R.id.price_total);
        TextView articlesCount = (TextView) getView().findViewById(R.id.articles_count);
        TextView extraCostsValue = (TextView) getView().findViewById(R.id.extra_costs_value);
        View extraCostsMain = getView().findViewById(R.id.extra_costs_container);
        TextView voucherValue = (TextView) getView().findViewById(R.id.text_voucher);
        View voucherContainer = getView().findViewById(R.id.voucher_info_container);
        // Get and set the cart value
        setTotal(cart);
        
        //GTM TRACKER
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

        items = new ArrayList<ShoppingCartItem>(cart.getCartItems().values());
        // Fix NAFAMZ-7848
        // Only convert value if it is a number
        if (CurrencyFormatter.isNumber(cart.getCartCleanValue())) {
            priceTotal.setText(CurrencyFormatter.formatCurrency(cart.getCartCleanValue()));
        } else {
            priceTotal.setText(cart.getCartCleanValue());
        }

        if (!cart.isSumCosts()) {
            extraCostsMain.setVisibility(View.VISIBLE);
            extraCostsValue.setText(CurrencyFormatter.formatCurrency(new BigDecimal(cart.getExtraCosts()).toString()));
        } else {
            extraCostsMain.setVisibility(View.GONE);
        }

        String articleString = getResources().getQuantityString(R.plurals.shoppingcart_text_article, cart.getCartCount());
        articlesCount.setText(cart.getCartCount() + " " + articleString);
        if (items.size() == 0) {
            showNoItems();
        } else {
            lView = (LinearLayout) getView().findViewById(R.id.shoppingcart_list);
            lView.removeAllViewsInLayout();
            itemsValues = new ArrayList<CartItemValues>();
            // Fix NAFAMZ-7848
            unreduced_cart_price = new BigDecimal(0);
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
                unreduced_cart_price = unreduced_cart_price.add(new BigDecimal(item.getPriceVal() * item.getQuantity()));
                Log.e(TAG, "unreduced_cart_price= " + unreduced_cart_price);
            }

            TextView priceUnreduced = (TextView) getView().findViewById(R.id.price_unreduced);
            if (cartHasReducedItem && unreduced_cart_price.intValue() > 0) {
                priceUnreduced.setText(CurrencyFormatter.formatCurrency(unreduced_cart_price.toString()));
                priceUnreduced.setPaintFlags(priceUnreduced.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                priceUnreduced.setVisibility(View.VISIBLE);
            } else {
                priceUnreduced.setVisibility(View.INVISIBLE);
            }
            String vat = cart.getVatValue();
            if (vat != null && !vat.equalsIgnoreCase("null") && !vat.equalsIgnoreCase("")) {
                TextView vatValue = (TextView) getView().findViewById(R.id.vat_value);
                View vatMain = getView().findViewById(R.id.vat_container);
                // Fix NAFAMZ-7848
                // Only convert value if it is a number
                if (CurrencyFormatter.isNumber(vat)) {
                    vatValue.setText(CurrencyFormatter.formatCurrency(vat));
                } else {
                    vatValue.setText(vat);
                }
                vatMain.setVisibility(View.VISIBLE);
            }

            HashMap<String, String> priceRules = cart.getPriceRules();
            if (priceRules != null && priceRules.size() > 0) {
                LinearLayout priceRulesContainer = (LinearLayout) getView().findViewById(R.id.price_rules_container);
                priceRulesContainer.removeAllViews();
                priceRulesContainer.setVisibility(View.VISIBLE);
                LayoutInflater mLayoutInflater = LayoutInflater.from(getBaseActivity());
                Set<String> priceRulesKeys = priceRules.keySet();
                for (String key : priceRulesKeys) {
                    View priceRuleElement = mLayoutInflater.inflate(R.layout.price_rules_element, priceRulesContainer, false);
                    ((TextView) priceRuleElement.findViewById(R.id.price_rules_label)).setText(key);
                    ((TextView) priceRuleElement.findViewById(R.id.price_rules_value)).setText("-" + CurrencyFormatter.formatCurrency(priceRules.get(key)));
                    priceRulesContainer.addView(priceRuleElement);
                }
            }

            hideNoItems();
            TrackerDelegator.trackPage(TrackingPage.FILLED_CART, getLoadTime(),false);

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

    public View getView(final int position, ViewGroup parent, LayoutInflater mInflater, CartItemValues item) {

        View view = mInflater.inflate(R.layout.shopping_basket_product_element_container, parent, false);

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

        RocketImageLoader.instance.loadImage(imageUrl, prodItem.productView, prodItem.pBar, R.drawable.no_image_small);

        if (!prodItem.itemValues.price.equals(prodItem.itemValues.price_disc)) {
            prodItem.priceDisc.setText(prodItem.itemValues.price_disc);
            prodItem.priceDisc.setVisibility(View.VISIBLE);

            prodItem.priceView.setText(prodItem.itemValues.price);
            prodItem.priceView.setVisibility(View.VISIBLE);
            prodItem.priceView.setPaintFlags(prodItem.priceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.priceView.setTextColor(getResources().getColor(R.color.grey_middlelight));

            prodItem.discountPercentage.setText("-" + prodItem.itemValues.discount_value.intValue() + "%");
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

        prodItem.deleteBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                prodItem.itemValues.is_checked = true;
                deleteSelectedElements();
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
                    goToProducDetails((String) v.getTag(R.id.target_url));
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
        showFragmentEmpty(R.string.order_no_items, R.drawable.img_emptycart, R.string.continue_shopping, new OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        });

        TrackerDelegator.trackPage(TrackingPage.EMPTY_CART, getLoadTime(), false);
    }

    /**
     * Function to redirect to the selected product details.
     * 
     * @param position
     */
    private void goToProducDetails(String productUrl) {
        // Log.d(TAG, "CART COMPLETE PRODUCT URL: " + items.get(position).getProductUrl());
        if (TextUtils.isEmpty(productUrl)) return;
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, productUrl);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcart_prefix);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    private void goToWebCheckout() {
        Log.d(TAG, "GOTO WEB CHECKOUT");
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.CHECKOUT_BASKET);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
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
    public void deleteSelectedElements() {
        for (int position = items.size() - 1; position >= 0; position--) {
            if (itemsValues.get(position).is_checked) {
                itemsValues.remove(position);
                mBeginRequestMillis = System.currentTimeMillis();
                triggerRemoveItem(items.get(position));
                items.remove(position);
            }
        }

        if (items.size() == 0) {
            showNoItems();
        } else {
            hideNoItems();
        }
    }

    public void hideNoItems() {
        showFragmentContentContainer();
    }

    public void changeQuantityOfItem(final int position) {
        ArrayList<String> quantities = new ArrayList<String>();

        long stock = items.get(position).getStock();
        int maxQuantity = items.get(position).getMaxQuantity();

        long actualMaxQuantity = stock < maxQuantity ? stock : maxQuantity;

        for (int i = 0; i <= actualMaxQuantity; i++) {
            quantities.add(String.valueOf(i));
        }

        long crrQuantity = items.get(position).getQuantity();

        OnDialogListListener listener = new OnDialogListListener() {
            @Override
            public void onDialogListItemSelect(String id, int quantity, String value) {
                changeQuantityOfItem(position, quantity);
            }
        };

        dialogList = DialogListFragment.newInstance(this, listener, ID_CHANGE_QUANTITY, getString(R.string.shoppingcart_choose_quantity), quantities, (int) crrQuantity);
        dialogList.show(getActivity().getSupportFragmentManager(), null);
    }

    public void changeQuantityOfItem(int position, int quantity) {
        trackAddToCartGTM(items.get(position), quantity);
        items.get(position).setQuantity(quantity);
        mBeginRequestMillis = System.currentTimeMillis();
        changeItemQuantityInShoppingCart(items);
    }

    private void trackAddToCartGTM(ShoppingCartItem item, int quantity){
        try {
            double prods = item.getQuantity();
            Bundle params = new Bundle();
            
            
            params.putString(TrackerDelegator.SKU_KEY, item.getConfigSimpleSKU());
            
            params.putLong(TrackerDelegator.START_TIME_KEY, mBeginRequestMillis);
            params.putDouble(TrackerDelegator.PRICE_KEY, item.getPriceForTracking());
            params.putLong(TrackerDelegator.QUANTITY_KEY, 1);
            params.putDouble(TrackerDelegator.RATING_KEY,-1d);
            params.putString(TrackerDelegator.NAME_KEY,item.getName());

            params.putString(TrackerDelegator.CARTVALUE_KEY, itemRemoved_cart_value);
            
            if(quantity > prods){
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
            values.put("qty_" + item.getConfigSimpleSKU(), String.valueOf(item.getQuantity()));
        }
        bundle.putParcelable(GetShoppingCartChangeItemQuantityHelper.CART_ITEMS, values);
        triggerContentEventProgress(new GetShoppingCartChangeItemQuantityHelper(), bundle, responseCallback);
    }

    IResponseCallback responseCallback = new IResponseCallback() {
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);

        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    private void prepareCouponView() {
        voucherCode = (EditText) getView().findViewById(R.id.voucher_name);
        if (!TextUtils.isEmpty(mVoucher)) {
            voucherCode.setText(mVoucher);
        }

        // voucherDivider = getView().findViewById(R.id.voucher_divider);
        voucherError = (TextView) getView().findViewById(R.id.voucher_error_message);
        couponButton = (Button) getView().findViewById(R.id.voucher_btn);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.fragment_root_retry_button){
          Bundle bundle = new Bundle();
          if(null != JumiaApplication.CUSTOMER){
              bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
              getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
              
          } else {
              getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
//              restartAllFragments();
          }
        }
    }
}