package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.forms.PaymentMethodForm;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Address;
import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.tracking.TrackingEvent;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.cart.ClearShoppingCartHelper;
import com.mobile.helpers.checkout.CheckoutFinishHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.objects.cart.ShoppingCartItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ShoppingCartUtils;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import de.akquinet.android.androlog.Log;

/**
 * Class used to shoe the order
 * @author sergiopereira
 */
public class CheckoutMyOrderFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutMyOrderFragment.class);

    private ViewGroup mProductsContainer;

    private TextView mTotalValue;

    private ShoppingCart cart;

    private TextView mShipFeeValue;

    private ViewGroup mShippingAddressContainer;

    private TextView mShippingMethodName;

    private ViewGroup mVoucherView;

    private TextView mVoucherValue;

    private ViewGroup mBillingAddressContainer;

    private View mBillingAddressIsSame;

    private TextView mPaymentName;

    private Address shippingAddress;

    private Address billingAddress;

    private TextView mProductsNum;

    private TextView mSubTotal;
    
    private TextView mExtraCosts;
    
    private RelativeLayout mExtraCostsContainer;
    
    private OrderSummary mOrderFinish;

    private String shipMethod;

    private String payMethod;

    private TextView mCoupon;

    private ViewGroup mShipFeeView;
    
    /**
     * Get CheckoutMyOrderFragment instance
     * @return CheckoutMyOrderFragment
     */
    public static CheckoutMyOrderFragment getInstance(Bundle bundle) {
        CheckoutMyOrderFragment fragment = new CheckoutMyOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutMyOrderFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.layout.checkout_my_order_main,
                R.string.checkout_label,
                KeyboardState.NO_ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_ORDER);
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
        // Get arguments
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if(arguments != null) {
            // Save order
            mOrderFinish = arguments.getParcelable(ConstantsIntentExtra.ORDER_FINISH);
        }
        // Track
        Bundle params = new Bundle();        
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putSerializable(TrackerDelegator.GA_STEP_KEY, TrackingEvent.CHECKOUT_STEP_ORDER);
        TrackerDelegator.trackCheckoutStep(params);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get product items
        mProductsContainer = (ViewGroup) view.findViewById(R.id.checkout_my_order_products_list);
        // Get sub total
        mProductsNum = (TextView) view.findViewById(R.id.articles_count);
        mSubTotal = (TextView) view.findViewById(R.id.price_total);
        mExtraCosts = (TextView) view.findViewById(R.id.extra_costs_value);
        mExtraCostsContainer = (RelativeLayout) view.findViewById(R.id.extra_costs_container);
        mShipFeeView = (ViewGroup) view.findViewById(R.id.shipping_container);
        mShipFeeValue = (TextView) view.findViewById(R.id.shipping_value);
        mVoucherView = (ViewGroup) view.findViewById(R.id.voucher_info_container);
        mVoucherValue = (TextView) view.findViewById(R.id.text_voucher);
        mTotalValue = (TextView) view.findViewById(R.id.total_value);
        // Get shipping address
        view.findViewById(R.id.checkout_my_order_shipping_address_btn_edit).setOnClickListener(this);
        mShippingAddressContainer = (ViewGroup) view.findViewById(R.id.checkout_my_order_shipping_address_list);
        // Get billing address
        view.findViewById(R.id.checkout_my_order_billing_address_btn_edit).setOnClickListener(this);
        mBillingAddressContainer = (ViewGroup) view.findViewById(R.id.checkout_my_order_billing_address_list);
        mBillingAddressIsSame = view.findViewById(R.id.checkout_my_order_billing_address_is_same);
        // Get shipping method
        view.findViewById(R.id.checkout_my_order_shipping_method_btn_edit).setOnClickListener(this);
        mShippingMethodName = (TextView) view.findViewById(R.id.checkout_my_order_shipping_method_name);
        // Get payment options
        view.findViewById(R.id.checkout_my_order_payment_options_btn_edit).setOnClickListener(this);
        mPaymentName = (TextView) view.findViewById(R.id.checkout_my_order_payment_name);
        mCoupon = (TextView) view.findViewById(R.id.checkout_my_order_payment_coupon);
        // Get the next step button
        view.findViewById(R.id.checkout_my_order_button_enter).setOnClickListener(this);
        // Get my Order
        showMyOrder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        TrackerDelegator.trackPage(TrackingPage.ORDER_CONFIRM, getLoadTime(), true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ConstantsIntentExtra.ORDER_FINISH, mOrderFinish);
    }

    /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.Fragment#onPause()
         */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }

    /**
     * Show the order content
     * @author sergiopereira
     */
    private void showMyOrder() {
        // Validate order
        if(mOrderFinish == null) {
            Log.w(TAG, "WARNING: ORDER IS NULL - GOTO WEB CHECKOUT");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "WARNING: ORDER IS NULL - GOTO WEB CHECKOUT");
            return;
        }
        // Get cart
        cart = mOrderFinish.getCart();
        if(cart != null) showProducts();
        // Get shipping address
        shippingAddress = mOrderFinish.getShippingAddress();
        if(shippingAddress != null) showShippingAddress();
        // Get billing address
        billingAddress = mOrderFinish.getBillingAddress();
        if(billingAddress != null) showBillingAddress();
        // Get shipping method
        shipMethod = mOrderFinish.getShippingMethodLabel();
        if(shipMethod != null) showShippingMethod();
        // Get payment options
        payMethod = mOrderFinish.getPaymentMethodLabel();
        if(payMethod != null) showPaymentOptions();
        // Show container
        showFragmentContentContainer();
    }
    
    /**
     * Show products on order
     * @author sergiopereira
     */
    private void showProducts(){
        boolean first = true;
        // Show products
        for (ShoppingCartItem item : cart.getCartItems().values()) {
            View prodInflateView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_my_order_product_item, mProductsContainer, false);
            // Image
            ImageView imageView = (ImageView) prodInflateView.findViewById(R.id.image_view);
            RocketImageLoader.instance.loadImage(item.getImageUrl(), imageView,  null, R.drawable.no_image_small);
            // Name
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_name)).setText(item.getName());
            // Quantity
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_quantity)).setText(getString(R.string.my_order_qty) + ": " + item.getQuantity());
            // Price
            String price = item.getPrice();
            if (!item.getPrice().equals(item.getSpecialPrice())) price = item.getSpecialPrice();  
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_price)).setText(CurrencyFormatter.formatCurrency(price));
            // Variation
            String variation = item.getVariation(); 
            if ( variation != null && variation.length() > 0 && !variation.equalsIgnoreCase(",") && 
                 !variation.equalsIgnoreCase("...") && !variation.equalsIgnoreCase(".") && !variation.equalsIgnoreCase("false")) {
                ((TextView) prodInflateView.findViewById(R.id.my_order_item_variation)).setText(variation);
                prodInflateView.findViewById(R.id.my_order_item_variation).setVisibility(View.VISIBLE);
            } 
            // // Hide first divider
            if (first) {
                first = false;
                prodInflateView.findViewById(R.id.my_order_item_divider).setVisibility(View.GONE);
            }
            
            // Add item view
            mProductsContainer.addView(prodInflateView);
        }
        // Show sub total
        showSubTotal();
    }
    
    
    /**
     * Show the sub total container
     * @author sergiopereira
     */
    private void showSubTotal() {
        int size = cart.getCartCount();
        mProductsNum.setText(getResources().getQuantityString(R.plurals.numberOfItems, size, size));
        // Set cart value
        mSubTotal.setText(CurrencyFormatter.formatCurrency(cart.getSubTotal()));
        // Set costs
        ShoppingCartUtils.setShippingRule(cart, mShipFeeView, mShipFeeValue, mExtraCostsContainer, mExtraCosts);
        // Voucher
        if(mOrderFinish.hasCouponDiscount()) mVoucherValue.setText("- " + CurrencyFormatter.formatCurrency(mOrderFinish.getDiscountCouponValue()));
        else mVoucherView.setVisibility(View.GONE);
        // Total
        mTotalValue.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getTotal()));
        
        if(cart.getPriceRules() != null && cart.getPriceRules().size() > 0){
            Log.i(TAG, "code1rules : pass");
            LinearLayout priceRulesContainer = (LinearLayout) getView().findViewById(R.id.price_rules_container);
            priceRulesContainer.setVisibility(View.VISIBLE);
            LayoutInflater mLayoutInflater = LayoutInflater.from(getBaseActivity());
            Set<String> priceRulesKeys = cart.getPriceRules().keySet();
            for (String key : priceRulesKeys) {
                View priceRuleElement = mLayoutInflater.inflate(R.layout.price_rules_summary_element, priceRulesContainer, false);
                ((TextView) priceRuleElement.findViewById(R.id.price_rules_label)).setText(key);
                ((TextView) priceRuleElement.findViewById(R.id.price_rules_value)).setText("-"+CurrencyFormatter.formatCurrency(cart.getPriceRules().get(key)));
                priceRulesContainer.addView(priceRuleElement);
            }
            
        }
    }
    
    /**
     * Show shipping address
     * @author sergiopereira
     */
    private void showShippingAddress() {
        Log.d(TAG, "SHOW SHIPPING ADDRESS: " + shippingAddress.getAddress());
        View shippingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, mShippingAddressContainer, false);
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_name)).setText(shippingAddress.getFirstName() + " " + shippingAddress.getLastName());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_street)).setText(shippingAddress.getAddress());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_region)).setText(shippingAddress.getCity());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(shippingAddress.getPostcode());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(""+shippingAddress.getPhone());
        shippingAddressView.findViewById(R.id.checkout_address_item_btn_container).setVisibility(View.GONE);
        mShippingAddressContainer.addView(shippingAddressView);
    }
    
    /**
     * Show billing address
     * @author sergiopereira
     */
    private void showBillingAddress() {
        // Validate address
        if(shippingAddress.getId() != billingAddress.getId()){
            // Hide text is the same
            mBillingAddressIsSame.setVisibility(View.GONE);
            // Show address
            View billingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, mBillingAddressContainer, false);
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_name)).setText(billingAddress.getFirstName() + " " + billingAddress.getLastName());
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_street)).setText(billingAddress.getAddress());
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_region)).setText(billingAddress.getCity());
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(billingAddress.getPostcode());
            ((TextView) billingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(""+billingAddress.getPhone());
            billingAddressView.findViewById(R.id.checkout_address_item_btn_container).setVisibility(View.GONE);
            mBillingAddressContainer.addView(billingAddressView);
        } else {
            mBillingAddressContainer.setVisibility(View.GONE);
        }
    }
    
    /**
     * Show shipping method
     * @author sergiopereira
     */
    private void showShippingMethod() {
        mShippingMethodName.setText(shipMethod);
    }
    
    /**
     * Show payment options
     * @author sergiopereira
     */
    private void showPaymentOptions() {
        // Payment name
        mPaymentName.setText(payMethod);
        // Coupon
        if(mOrderFinish.hasCouponCode()){
            mCoupon.setText(getString(R.string.my_order_coupon_label) + "\n" + mOrderFinish.getDiscountCouponCode());
            mCoupon.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ############# CLICK LISTENER #############
     */
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Shipping Address Edit
        if (id == R.id.checkout_my_order_shipping_address_btn_edit) onClickEditAddressesButton();
        // Billing Address Edit
        else if (id == R.id.checkout_my_order_billing_address_btn_edit) onClickEditAddressesButton();
        // Shipping method
        else if (id == R.id.checkout_my_order_shipping_method_btn_edit) onClickEditShippingMethodButton();
        // Payment options
        else if (id == R.id.checkout_my_order_payment_options_btn_edit) onClickEditPaymentOptionsButton();
        // Next step button
        else if (id == R.id.checkout_my_order_button_enter) onClickNextStepButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
   
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onClickRetryButton();
    }
    
    /**
     * Process the click on retry button.
     * @author paulo
     */
    private void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if(null != JumiaApplication.CUSTOMER){
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on next step button
     * @author sergiopereira
     */
    private void onClickNextStepButton() {
        Log.i(TAG, "ON CLICK: NextStep");
        if(JumiaApplication.INSTANCE.getPaymentMethodForm() != null ){
            if(JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_REDIRECT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_RENDER_INTERNAL){
                getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EXTERNAL_PAYMENT, null, FragmentController.ADD_TO_BACK_STACK);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber());
                bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_SHIPPING, String.valueOf(mOrderFinish.getShippingAmount()));
                bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TAX, mOrderFinish.getTaxAmount());
                bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_PAYMENT_METHOD, mOrderFinish.getPaymentMethod());
                bundle.putDouble(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TOTAL, mOrderFinish.getValueForTracking());
                getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK); 
            }
        } else {
            triggerCheckoutFinish();    
        }
    }    
    
    /**
     * Process the click on the edit address button
     * @author sergiopereira
     */
    private void onClickEditAddressesButton() {
        Log.i(TAG, "ON CLICK: EditAddresses");
        if(JumiaApplication.INSTANCE.getPaymentMethodForm() == null){
            if(!getBaseActivity().popBackStackUntilTag(FragmentType.MY_ADDRESSES.toString())) {
                FragmentController.getInstance().popLastEntry(FragmentType.MY_ORDER.toString());
                getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    }

    /**
     *
     * Process the click on the edit shipping method button
     * @author sergiopereira
     */
    private void onClickEditShippingMethodButton() {
        Log.i(TAG, "ON CLICK: EditShippingMethod");
        if(JumiaApplication.INSTANCE.getPaymentMethodForm() == null){
            if(!getBaseActivity().popBackStackUntilTag(FragmentType.SHIPPING_METHODS.toString())) {
                FragmentController.getInstance().popLastEntry(FragmentType.MY_ORDER.toString());
                getBaseActivity().onSwitchFragment(FragmentType.SHIPPING_METHODS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    }
    
    /**
     * Process the click on the edit payment method button
     * @author sergiopereira
     */
    private void onClickEditPaymentOptionsButton() {
        Log.i(TAG, "ON CLICK: EditPaymentOptions");
        if(JumiaApplication.INSTANCE.getPaymentMethodForm() == null){
            if(!getBaseActivity().popBackStackUntilTag(FragmentType.PAYMENT_METHODS.toString())) {
                FragmentController.getInstance().popLastEntry(FragmentType.MY_ORDER.toString());
                getBaseActivity().onSwitchFragment(FragmentType.PAYMENT_METHODS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    /**
     * Trigger ti finish the checkout process
     * @author sergiopereira
     */
    private void triggerCheckoutFinish() {
        Log.i(TAG, "TRIGGER: CHECKOUT FINISH");
        Bundle bundle = new Bundle();
        bundle.putString(CheckoutFinishHelper.USER_AGENT, getUserAgentAsExtraData());
        triggerContentEvent(new CheckoutFinishHelper(), bundle, this);
    }
    
    /**
     * Creates a custom user agent just in case the http user agent be empty.
     * @return Stirng
     * @author sergiopereira
     */
    private String getUserAgentAsExtraData(){
        String device = (getResources().getBoolean(R.bool.isTablet)) ? "tablet" : "mobile";      
        //return "app=android&customer_device=" + device;
        return device;
    }

    /**
     * Trigger to clear cart after checkout finish.
     */
    private void triggerClearCart() {
        Log.i(TAG, "TRIGGER: CHECKOUT FINISH");
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, this);
        triggerContentEventNoLoading(new AddVoucherHelper(), null, this);
    }

    /**
     * ############# RESPONSE #############
     */
    /**
     * Process the success event
     * @param bundle The success response
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS EVENT");
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
            case CHECKOUT_FINISH_EVENT:
                Log.i(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
                if (JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_REDIRECT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_RENDER_INTERNAL) {
                    JumiaApplication.INSTANCE.getPaymentMethodForm().setCameFromWebCheckout(false);
                    getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EXTERNAL_PAYMENT, null, FragmentController.ADD_TO_BACK_STACK);
                } else {
                    JumiaApplication.INSTANCE.getPaymentMethodForm().setCameFromWebCheckout(false);
                    bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR, JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber());
                    bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_SHIPPING, String.valueOf(mOrderFinish.getShippingAmount()));
                    bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_TAX, mOrderFinish.getTaxAmount());
                    bundle.putString(ConstantsCheckout.CHECKOUT_THANKS_PAYMENT_METHOD, mOrderFinish.getPaymentMethod());
                    getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK);
                }
                getBaseActivity().updateCartInfo();
                break;
            default:
                break;
        }
    }

    /**
     * Process the error event
     * @param bundle The error response
     */
    @Override
    public void onRequestError(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
            case CHECKOUT_FINISH_EVENT:
                Log.d(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                    showErrorDialog(errors);
                    showFragmentContentContainer();
                } else {
                    Log.w(TAG, "RECEIVED CHECKOUT_FINISH_EVENT: " + errorCode.name());
                    super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "RECEIVED CHECKOUT_FINISH_EVENT: " + errorCode.name());
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * ########### DIALOGS ###########  
     */    
    /**
     * Dialog used to show an error
     * @param errors
     */
    private void showErrorDialog(HashMap<String, List<String>> errors) {
        Log.d(TAG, "SHOW LOGIN ERROR DIALOG");
        List<String> temp = null;
        if (errors != null) {
            temp = errors.get(RestConstants.JSON_VALIDATE_TAG);
        }
        final List<String> errorMessages = temp;
        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            if (getBaseActivity() != null) {
                showFragmentContentContainer();
            }
            dialog = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.error_login_title),
                    errorMessages.get(0),
                    getString(R.string.ok_label),
                    "",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                                gotoWebCheckout(errorMessages.get(0));
                            }
                        }
                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
        } else {
            Log.w(TAG, "ERROR ON FINISH CHECKOUT");
            gotoWebCheckout("ERROR ON FINISH CHECKOUT");
        }
    }
    
    /**
     * Redirect for web checkout
     * @author sergiopereira
     */
    private void gotoWebCheckout(String error){
        Log.w(TAG, "GO TO WEBCKECOUT");
        super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), error);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        if (JumiaApplication.INSTANCE.getPaymentMethodForm() == null) {
            return false;
        } else {
            dialog = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.confirm_order_loosing_order_title),
                    getString(R.string.confirm_order_loosing_order) + " \n" + JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber(),
                    getString(R.string.ok_label), 
                    getString(R.string.cancel_label),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                                JumiaApplication.INSTANCE.setPaymentMethodForm(null);
                                JumiaApplication.INSTANCE.setCart(null);
                                triggerClearCart();
                                getBaseActivity().updateCartInfo();
                                getBaseActivity().onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                            } else if (id == R.id.button2) {
                                dismissDialogFragment();
                            }
                        }
                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
            return true;
        }
    }

}
