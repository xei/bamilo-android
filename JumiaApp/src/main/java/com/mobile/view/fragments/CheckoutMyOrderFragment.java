package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.cart.ClearShoppingCartHelper;
import com.mobile.helpers.checkout.CheckoutFinishHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.PaymentMethodForm;
import com.mobile.newFramework.objects.addresses.Address;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ShoppingCartUtils;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Class used to shoe the order
 *
 * @author sergiopereira
 */
public class CheckoutMyOrderFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = CheckoutMyOrderFragment.class.getSimpleName();

    private ViewGroup mProductsContainer;

    private TextView mTotalValue;

    private TextView mShipFeeValue;

    private ViewGroup mShippingAddressContainer;

    private TextView mShippingMethodName;

    private ViewGroup mVoucherView;

    private TextView mVoucherValue;

    private ViewGroup mBillingAddressContainer;

    private View mBillingAddressIsSame;

    private TextView mPaymentName;

    private TextView mProductsNum;

    private TextView mSubTotal;

    private TextView mExtraCosts;

    private RelativeLayout mExtraCostsContainer;

    private TextView mCoupon;

    private ViewGroup mShipFeeView;

    private ViewGroup mPriceRulesContainer;

    private PurchaseEntity mOrderFinish;

    private ImageView mEditShippingAddress;

    private ImageView mEditBillingAddress;

    private ImageView mEditShippingMethod;

    private ImageView mEditPaymentMethod;

    /**
     * Empty constructor
     */
    public CheckoutMyOrderFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.checkout_my_order_main,
                R.string.checkout_label,
                NO_ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_ORDER);
    }

    /**
     * Get CheckoutMyOrderFragment instance
     *
     * @return CheckoutMyOrderFragment
     */
    public static CheckoutMyOrderFragment getInstance(Bundle bundle) {
        CheckoutMyOrderFragment fragment = new CheckoutMyOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if (arguments != null) {
            mOrderFinish = arguments.getParcelable(ConstantsIntentExtra.ORDER_FINISH);
        }
        // Track
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_ORDER);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
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
        mEditShippingAddress = (ImageView) view.findViewById(R.id.checkout_my_order_shipping_address_btn_edit);
        mEditShippingAddress.setOnClickListener(this);
        mShippingAddressContainer = (ViewGroup) view.findViewById(R.id.checkout_my_order_shipping_address_list);
        // Get billing address
        mEditBillingAddress = (ImageView) view.findViewById(R.id.checkout_my_order_billing_address_btn_edit);
        mEditBillingAddress.setOnClickListener(this);
        mBillingAddressContainer = (ViewGroup) view.findViewById(R.id.checkout_my_order_billing_address_list);
        mBillingAddressIsSame = view.findViewById(R.id.checkout_my_order_billing_address_is_same);
        // Get shipping method
        mEditShippingMethod = (ImageView) view.findViewById(R.id.checkout_my_order_shipping_method_btn_edit);
        mEditShippingMethod.setOnClickListener(this);
        mShippingMethodName = (TextView) view.findViewById(R.id.checkout_my_order_shipping_method_name);
        // Get payment options
        mEditPaymentMethod = (ImageView) view.findViewById(R.id.checkout_my_order_payment_options_btn_edit);
        mEditPaymentMethod.setOnClickListener(this);
        mPaymentName = (TextView) view.findViewById(R.id.checkout_my_order_payment_name);
        mCoupon = (TextView) view.findViewById(R.id.checkout_my_order_payment_coupon);
        // Get the next step button
        view.findViewById(R.id.checkout_my_order_button_enter).setOnClickListener(this);
        // Price rules
        mPriceRulesContainer = (ViewGroup) view.findViewById(R.id.price_rules_container);
        // Hide or show edit buttons
        controlEditButtonsVisibility();
        // Validate order
        if (mOrderFinish != null) {
            showMyOrder();
        } else {
            Print.w(TAG, "WARNING: ORDER IS NULL - SHOWS UNEXPECTED ERROR");
            super.showFragmentErrorRetry();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        TrackerDelegator.trackPage(TrackingPage.ORDER_CONFIRM, getLoadTime(), true);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
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
        Print.i(TAG, "ON DESTROY");
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
        else Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ConstantsIntentExtra.ORDER_FINISH, mOrderFinish);
    }

    /**
     * Show the order content
     *
     * @author sergiopereira
     */
    private void showMyOrder() {
        // Get cart
        showProducts();
        // Get addresses
        showOrderAddresses();
        // Get shipping method
        showShippingMethod();
        // Get payment options
        showPaymentOptions();
        // Show container
        showFragmentContentContainer();
    }

    /**
     * Show products on order
     *
     * @author sergiopereira
     */
    private void showProducts() {
        boolean first = true;
        // Show products
        for (PurchaseCartItem item : mOrderFinish.getCartItems()) {
            View prodInflateView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_my_order_product_item, mProductsContainer, false);
            // Image
            ImageView imageView = (ImageView) prodInflateView.findViewById(R.id.image_view);
            RocketImageLoader.instance.loadImage(item.getImageUrl(), imageView, null, R.drawable.no_image_small);
            // Brand
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_brand)).setText(item.getBrand());
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
            if (variation != null && variation.length() > 0 && !variation.equalsIgnoreCase(",") &&
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
     *
     * @author sergiopereira
     */
    private void showSubTotal() {
        int size = mOrderFinish.getCartCount();
        mProductsNum.setText(getResources().getQuantityString(R.plurals.numberOfItems, size, size));
        // Set cart value
        mSubTotal.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getSubTotal()));
        // Set costs
        ShoppingCartUtils.setShippingRule(mOrderFinish, mShipFeeView, mShipFeeValue, mExtraCostsContainer, mExtraCosts);
        // Voucher
        if (mOrderFinish.hasCouponDiscount()) {
            mVoucherValue.setText("- " + CurrencyFormatter.formatCurrency(mOrderFinish.getCouponDiscount()));
        } else {
            mVoucherView.setVisibility(View.GONE);
        }
        // Total
        mTotalValue.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getTotal()));
        // Show price rules
        CheckoutStepManager.showPriceRules(getBaseActivity(), mPriceRulesContainer, mOrderFinish.getPriceRules());
    }

    /**
     * Show shipping address
     *
     * @author sergiopereira
     */
    private void showOrderAddresses() {
        // Show shipping address
        if (mOrderFinish.hasShippingAddress()) {
            addAddressView(mShippingAddressContainer, mOrderFinish.getShippingAddress());
        }
        // Show same addresses
        if (mOrderFinish.hasSameAddresses()) {
            mBillingAddressContainer.setVisibility(View.GONE);
        }
        // Show billing address
        else if (mOrderFinish.hasBillingAddress()) {
            mBillingAddressIsSame.setVisibility(View.GONE);
            addAddressView(mBillingAddressContainer, mOrderFinish.getBillingAddress());
        }
    }

    /**
     * Add address to view group.
     */
    private void addAddressView(ViewGroup container, Address address) {
        View shippingAddressView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_address_item, container, false);
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_name)).setText(address.getFirstName() + " " + address.getLastName());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_street)).setText(address.getAddress());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_region)).setText(address.getCity());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(address.getPostcode());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(address.getPhone());
        shippingAddressView.findViewById(R.id.checkout_address_item_radio_btn).setVisibility(View.GONE);
        shippingAddressView.findViewById(R.id.checkout_address_item_btn_edit).setVisibility(View.GONE);
        container.addView(shippingAddressView);
    }

    /**
     * Show shipping method
     *
     * @author sergiopereira
     */
    private void showShippingMethod() {
        if (!TextUtils.isEmpty(mOrderFinish.getShippingMethod())) {
            mShippingMethodName.setText(mOrderFinish.getShippingMethod());
        }
    }

    /**
     * Show payment options
     *
     * @author sergiopereira
     */
    private void showPaymentOptions() {
        // Payment name
        if (!TextUtils.isEmpty(mOrderFinish.getPaymentMethod())) {
            mPaymentName.setText(mOrderFinish.getPaymentMethod());
        }
        // Coupon
        if (mOrderFinish.hasCouponDiscount()) {
            mCoupon.setText(getString(R.string.my_order_coupon_label) + "\n" + mOrderFinish.getCouponCode());
            mCoupon.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Process the click on retry button.
     *
     * @author paulo
     */
    private void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if (null != JumiaApplication.CUSTOMER) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on next step button
     *
     * @author sergiopereira
     */
    private void onClickNextStepButton() {
        Print.i(TAG, "ON CLICK: NextStep");
        // this validation is trigger when the user back presses from an external payment
        if (JumiaApplication.INSTANCE.getPaymentMethodForm() != null) {
            if (JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_REDIRECT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_RENDER_INTERNAL) {
                getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EXTERNAL_PAYMENT, null, FragmentController.ADD_TO_BACK_STACK);
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(RestConstants.ORDER_NR, JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber());
                bundle.putString(RestConstants.TRANSACTION_SHIPPING, String.valueOf(mOrderFinish.getShippingValue()));
                bundle.putString(RestConstants.TRANSACTION_TAX, "" + mOrderFinish.getVatValue());
                bundle.putString(RestConstants.PAYMENT_METHOD, mOrderFinish.getPaymentMethod());
                bundle.putDouble(RestConstants.ORDER_GRAND_TOTAL, mOrderFinish.getPriceForTracking());
                getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK);
            }
        } else {
            triggerCheckoutFinish();
        }
    }

    /**
     * Process the click on the edit address button
     *
     * @author sergiopereira
     */
    private void onClickEditAddressesButton() {
        Print.i(TAG, "ON CLICK: EditAddresses");
        if (JumiaApplication.INSTANCE.getPaymentMethodForm() == null) {
            if (!getBaseActivity().popBackStackUntilTag(FragmentType.MY_ADDRESSES.toString())) {
                FragmentController.getInstance().popLastEntry(FragmentType.MY_ORDER.toString());
                getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    }

    /**
     * Process the click on the edit shipping method button
     *
     * @author sergiopereira
     */
    private void onClickEditShippingMethodButton() {
        Print.i(TAG, "ON CLICK: EditShippingMethod");
        if (JumiaApplication.INSTANCE.getPaymentMethodForm() == null) {
            if (!getBaseActivity().popBackStackUntilTag(FragmentType.SHIPPING_METHODS.toString())) {
                FragmentController.getInstance().popLastEntry(FragmentType.MY_ORDER.toString());
                getBaseActivity().onSwitchFragment(FragmentType.SHIPPING_METHODS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    }

    /**
     * ############# REQUESTS #############
     */

    /**
     * Process the click on the edit payment method button
     *
     * @author sergiopereira
     */
    private void onClickEditPaymentOptionsButton() {
        Print.i(TAG, "ON CLICK: EditPaymentOptions");
        if (JumiaApplication.INSTANCE.getPaymentMethodForm() == null) {
            if (!getBaseActivity().popBackStackUntilTag(FragmentType.PAYMENT_METHODS.toString())) {
                FragmentController.getInstance().popLastEntry(FragmentType.MY_ORDER.toString());
                getBaseActivity().onSwitchFragment(FragmentType.PAYMENT_METHODS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    }

    /**
     * Trigger ti finish the checkout process
     *
     * @author sergiopereira
     */
    private void triggerCheckoutFinish() {
        Print.i(TAG, "TRIGGER: CHECKOUT FINISH");
        Bundle bundle = new Bundle();
        bundle.putString(CheckoutFinishHelper.USER_AGENT, getUserAgentAsExtraData());
        triggerContentEvent(new CheckoutFinishHelper(), bundle, this);
    }

    /**
     * Creates a custom user agent just in case the http user agent be empty.
     *
     * @return Stirng
     * @author sergiopereira
     */
    private String getUserAgentAsExtraData() {
        return getResources().getBoolean(R.bool.isTablet) ? "tablet" : "mobile";
    }

    /**
     * ############# RESPONSE #############
     */

    /**
     * Trigger to clear cart after checkout finish.
     */
    private void triggerClearCart() {
        Print.i(TAG, "TRIGGER: CHECKOUT FINISH");
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, this);
        triggerContentEventNoLoading(new RemoveVoucherHelper(), null, this);
    }

    /**
     * Process the success event
     *
     * @param baseResponse The success response
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "ON SUCCESS EVENT");
        EventType eventType = baseResponse.getEventType();

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }


        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
            case CHECKOUT_FINISH_EVENT:
                Print.i(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
                if (JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_SUBMIT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_AUTO_REDIRECT_EXTERNAL || JumiaApplication.INSTANCE.getPaymentMethodForm().getPaymentType() == PaymentMethodForm.METHOD_RENDER_INTERNAL) {
                    JumiaApplication.INSTANCE.getPaymentMethodForm().setCameFromWebCheckout(false);
                    getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EXTERNAL_PAYMENT, null, FragmentController.ADD_TO_BACK_STACK);
                } else {
                    JumiaApplication.INSTANCE.getPaymentMethodForm().setCameFromWebCheckout(false);
                    Bundle bundle = new Bundle();
                    bundle.putString(RestConstants.ORDER_NR, JumiaApplication.INSTANCE.getPaymentMethodForm().getOrderNumber());
                    bundle.putString(RestConstants.TRANSACTION_SHIPPING, String.valueOf(mOrderFinish.getShippingValue()));
                    bundle.putString(RestConstants.TRANSACTION_TAX, "" + mOrderFinish.getVatValue());
                    bundle.putString(RestConstants.PAYMENT_METHOD, mOrderFinish.getPaymentMethod());
                    bundle.putDouble(RestConstants.ORDER_GRAND_TOTAL, mOrderFinish.getPriceForTracking());
                    getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK);
                }
                getBaseActivity().updateCartInfo();
                break;
            default:
                break;
        }
    }

    /**
     * ########### DIALOGS ###########  
     */

    /**
     * Process the error event
     *
     * @param baseResponse The error response
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {

        EventType eventType = baseResponse.getEventType();

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return;
        }


        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);

        switch (eventType) {
            case CHECKOUT_FINISH_EVENT:
                Print.d(TAG, "RECEIVED CHECKOUT_FINISH_EVENT");
                boolean hasErrorMessage = false;
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    hasErrorMessage = showErrorDialog(baseResponse.getErrorMessage());
                }
                if (!hasErrorMessage) {
                    Print.w(TAG, "RECEIVED CHECKOUT_FINISH_EVENT: " + errorCode);
                    super.showUnexpectedErrorWarning();
                }
                showFragmentContentContainer();
                break;
            default:
                break;
        }
    }

    /**
     * Dialog used to show an error
     */
    private boolean showErrorDialog(String message) {
        Print.d(TAG, "SHOW LOGIN ERROR DIALOG");
        if (!TextUtils.isEmpty(message)) {
            dialog = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.error_occured),
                    message,
                    getString(R.string.ok_label),
                    "",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                                showFragmentErrorRetry();
                            }
                        }
                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
            return true;
        } else {
            Print.w(TAG, "ERROR ON FINISH CHECKOUT");
            return false;
        }
    }

    /**
     * Method that controls the visibility of the Edit buttons for the case where the user goes to external payment
     * and presses back to my order
     */
    private void controlEditButtonsVisibility(){
        if(JumiaApplication.INSTANCE.getPaymentMethodForm() != null){
            mEditShippingAddress.setVisibility(View.GONE);
            mEditBillingAddress.setVisibility(View.GONE);
            mEditShippingMethod.setVisibility(View.GONE);
            mEditPaymentMethod.setVisibility(View.GONE);
        } else {
            mEditShippingAddress.setVisibility(View.VISIBLE);
            mEditBillingAddress.setVisibility(View.VISIBLE);
            mEditShippingMethod.setVisibility(View.VISIBLE);
            mEditPaymentMethod.setVisibility(View.VISIBLE);
        }
    }

}
