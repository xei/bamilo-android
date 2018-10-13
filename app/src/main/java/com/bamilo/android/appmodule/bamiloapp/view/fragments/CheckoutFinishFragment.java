package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ClearShoppingCartHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.GetStepFinishHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.SetStepFinishHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.voucher.RemoveVoucherHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.framework.service.forms.PaymentMethodForm;
import com.bamilo.android.framework.service.objects.addresses.Address;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.checkout.CheckoutFinish;
import com.bamilo.android.framework.service.objects.product.RichRelevance;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.cart.UICartUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogGenericFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.product.UIProductUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.R;

import java.util.EnumSet;

/**
 * Class used to shoe the order
 *
 * @author sergiopereira
 */
public class CheckoutFinishFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = CheckoutFinishFragment.class.getSimpleName();

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

    private CheckoutFinish mCheckoutFinish;

    //DROID-10
    private long mGABeginRequestMillis;

    /**
     * Empty constructor
     */
    public CheckoutFinishFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.checkout_my_order_main,
                R.string.checkout_label,
                NO_ADJUST_CONTENT,
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
        mGABeginRequestMillis = System.currentTimeMillis();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get arguments
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if (arguments != null) {
            mOrderFinish = arguments.getParcelable(ConstantsIntentExtra.ORDER_FINISH);
            mCheckoutFinish = arguments.getParcelable(ConstantsIntentExtra.DATA);
        }

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CHECKOUT_FINISH.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get product items
        mProductsContainer = view.findViewById(R.id.checkout_my_order_products_list);
        // Get sub total
        mProductsNum = view.findViewById(R.id.articles_count);
        mSubTotal = view.findViewById(R.id.price_total);
        mExtraCosts = view.findViewById(R.id.extra_costs_value);
        mExtraCostsContainer = view.findViewById(R.id.extra_costs_container);
        mShipFeeView = view.findViewById(R.id.shipping_container);
        mShipFeeValue = view.findViewById(R.id.shipping_value);
        mVoucherView = view.findViewById(R.id.voucher_info_container);
        mVoucherValue = view.findViewById(R.id.text_voucher);
        mTotalValue = view.findViewById(R.id.total_value);
        // Get shipping address
        mEditShippingAddress = view.findViewById(R.id.checkout_my_order_shipping_address_btn_edit);
        mEditShippingAddress.setOnClickListener(this);
        mShippingAddressContainer = view.findViewById(R.id.checkout_my_order_shipping_address_list);
        // Get billing address
        mEditBillingAddress = view.findViewById(R.id.checkout_my_order_billing_address_btn_edit);
        mEditBillingAddress.setOnClickListener(this);
        mBillingAddressContainer = view.findViewById(R.id.checkout_my_order_billing_address_list);
        mBillingAddressIsSame = view.findViewById(R.id.checkout_my_order_billing_address_is_same);
        // Get shipping method
        mEditShippingMethod = view.findViewById(R.id.checkout_my_order_shipping_method_btn_edit);
        mEditShippingMethod.setOnClickListener(this);
        mShippingMethodName = view.findViewById(R.id.checkout_my_order_shipping_method_name);
        // Get payment options
        mEditPaymentMethod = view.findViewById(R.id.checkout_my_order_payment_options_btn_edit);
        mEditPaymentMethod.setOnClickListener(this);
        mPaymentName = view.findViewById(R.id.checkout_my_order_payment_name);
        mCoupon = view.findViewById(R.id.checkout_my_order_payment_coupon);
        // Get the next step button
        view.findViewById(R.id.checkout_my_order_button_enter).setOnClickListener(this);
        // Price rules
        mPriceRulesContainer = view.findViewById(R.id.price_rules_container);
        // Hide or show edit buttons
        controlEditButtonsVisibility();
        // Validate order
        if (mOrderFinish != null) {
            showMyOrder();
        } else {
            triggerGetMultiStepFinish();
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ConstantsIntentExtra.ORDER_FINISH, mOrderFinish);
        outState.putParcelable(ConstantsIntentExtra.DATA, mCheckoutFinish);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
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
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#allowBackPressed()
     */
    @Override
    public boolean allowBackPressed() {
        if (mCheckoutFinish == null) {
            return false;
        } else {
            dialog = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.confirm_order_loosing_order_title),
                    getString(R.string.confirm_order_loosing_order) + " \n" + mCheckoutFinish.getOrderNumber(),
                    getString(R.string.ok_label),
                    getString(R.string.cancel_label),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                                BamiloApplication.INSTANCE.setCart(null);
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
        // Show products
        for (PurchaseCartItem item : mOrderFinish.getCartItems()) {
            View prodInflateView = LayoutInflater.from(getBaseActivity()).inflate(R.layout.checkout_my_order_product_item, mProductsContainer, false);
            // Image
            ImageView imageView = prodInflateView.findViewById(R.id.image_view);
            //RocketImageLoader.instance.loadImage(item.getImageUrl(), imageView, null, R.drawable.no_image_small);
            ImageManager.getInstance().loadImage(item.getImageUrl(), imageView, null, R.drawable.no_image_large, false);
            //shop first image
            ImageView shopFirstImageView = prodInflateView.findViewById(R.id.shop_first_item);
            UIProductUtils.setShopFirst(item, shopFirstImageView);
            UIProductUtils.showShopFirstOverlayMessage(this,item,shopFirstImageView);
            // Brand
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_brand)).setText(item.getBrandName());
            // Name
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_name)).setText(item.getName());
            // Quantity
            ((TextView) prodInflateView.findViewById(R.id.my_order_item_quantity)).setText(getString(R.string.qty_placeholder, String.valueOf(item.getQuantity())));
            // Price
            UIProductUtils.setPriceRules(item,
                    prodInflateView.findViewById(R.id.my_order_item_price));
            // Variation
            String variation = item.getVariationValue();
            if (variation != null && variation.length() > 0 &&
                    !variation.equalsIgnoreCase(",") &&
                    !variation.equalsIgnoreCase("...") &&
                    !variation.equalsIgnoreCase(".") &&
                    !variation.equalsIgnoreCase("false")) {
                ((TextView) prodInflateView.findViewById(R.id.my_order_item_variation)).setText(variation);
                prodInflateView.findViewById(R.id.my_order_item_variation).setVisibility(View.VISIBLE);
            }
            UIProductUtils.setShopFirst(item, prodInflateView.findViewById(R.id.shop_first_item));
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
        UICartUtils.setShippingRule(mOrderFinish, mShipFeeView, mShipFeeValue, mExtraCostsContainer, mExtraCosts);
        // Voucher
        if (mOrderFinish.hasCouponDiscount()) {
            mVoucherValue.setText(getString(R.string.placeholder_discount, CurrencyFormatter.formatCurrency(mOrderFinish.getCouponDiscount())));
            mVoucherView.setVisibility(View.VISIBLE);
        } else {
            mVoucherView.setVisibility(View.GONE);
        }
        // Total
        mTotalValue.setText(CurrencyFormatter.formatCurrency(mOrderFinish.getTotal()));
        // Show price rules
        CheckoutStepManager.showPriceRules(getBaseActivity(), mPriceRulesContainer, mOrderFinish.getPriceRules());
        //show vat if configuration is enabled
        TextView vatIncludedLabel = getView().findViewById(R.id.vat_included_label);
        TextView vatValue = getView().findViewById(R.id.vat_value);
        UICartUtils.showVatInfo(mOrderFinish, vatIncludedLabel, vatValue);
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
        String name = getString(R.string.first_space_second_placeholder, address.getFirstName(), address.getLastName());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_name)).setText(name);
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_street)).setText(address.getAddress());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_region)).setText(address.getCity());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_postcode)).setText(address.getPostcode());
        ((TextView) shippingAddressView.findViewById(R.id.checkout_address_item_phone)).setText(address.getPhone());
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
        if (null != BamiloApplication.CUSTOMER) {
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
        // this validation is trigger when the user back presses from an external payment
        if (mCheckoutFinish != null) {
            switchToSubmittedPayment();
        } else {
            triggerCheckoutFinish();
        }
    }

    /**
     * Process the click on the edit address button
     */
    private void onClickEditAddressesButton() {
        if (!getBaseActivity().popBackStackUntilTag(FragmentType.CHECKOUT_MY_ADDRESSES.toString())) {
            FragmentController.getInstance().popLastEntry(FragmentType.CHECKOUT_FINISH.toString());
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on the edit shipping method button
     */
    private void onClickEditShippingMethodButton() {
        if (!getBaseActivity().popBackStackUntilTag(FragmentType.CHECKOUT_SHIPPING.toString())) {
            FragmentController.getInstance().popLastEntry(FragmentType.CHECKOUT_FINISH.toString());
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_SHIPPING, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the click on the edit payment method button
     *
     * @author sergiopereira
     */
    private void onClickEditPaymentOptionsButton() {
        if (!getBaseActivity().popBackStackUntilTag(FragmentType.CHECKOUT_PAYMENT.toString())) {
            FragmentController.getInstance().popLastEntry(FragmentType.CHECKOUT_FINISH.toString());
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_PAYMENT, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * ############# TRIGGERS #############
     */

    /**
     * Trigger ti finish the checkout process
     */
    private void triggerCheckoutFinish() {
        triggerContentEvent(new SetStepFinishHelper(), SetStepFinishHelper.createBundle(getUserAgentAsExtraData()), this);
    }

    /**
     * Trigger to get order to finish the checkout process
     */
    private void triggerGetMultiStepFinish() {
        triggerContentEvent(new GetStepFinishHelper(), null, this);
    }

    /**
     * Creates a custom user agent just in case the http user agent be empty.
     */
    private String getUserAgentAsExtraData() {
        return getResources().getBoolean(R.bool.isTablet) ? "tablet" : "mobile";
    }

    /**
     * Trigger to clear cart after checkout finish.
     */
    private void triggerClearCart() {
        triggerContentEventNoLoading(new ClearShoppingCartHelper(), null, this);
        triggerContentEventNoLoading(new RemoveVoucherHelper(), null, this);
    }

    /**
     * ############# RESPONSES #############
     */

    /**
     * Process the success event
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate the event
        switch (eventType) {
            case GET_MULTI_STEP_FINISH:
                mOrderFinish = (PurchaseEntity) baseResponse.getContentData();
                if(mOrderFinish == null) {
                    showFragmentErrorRetry();
                } else {
                    showMyOrder();

                    // Track screen timing
                    BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.CHECKOUT_FINISH.getName()), getString(R.string.gaScreen),
                            "" ,
                            getLoadTime());
                    TrackerManager.trackScreenTiming(getContext(), screenModel);
                }
                break;
            case SET_MULTI_STEP_FINISH:
                mCheckoutFinish = (CheckoutFinish) baseResponse.getContentData();

                // Next step
                switchToSubmittedPayment();
                // Update cart info
                getBaseActivity().updateCartInfo();
                break;
            default:
                break;
        }
    }

    /**
     * Method used to validate the submitted payment.
     */
    private void switchToSubmittedPayment() {
        PaymentMethodForm mPaymentSubmitted = mCheckoutFinish.getPaymentMethodForm();
        // Case external payment
        if (mPaymentSubmitted.isExternalPayment()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantsIntentExtra.DATA, mCheckoutFinish.getPaymentMethodForm());
            if(mCheckoutFinish.getRichRelevance() != null) {
                bundle.putParcelable(RestConstants.RECOMMENDED_PRODUCTS, mCheckoutFinish.getRichRelevance());
            }
            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_EXTERNAL_PAYMENT, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case other
        else {
            Bundle bundle = new Bundle();
            bundle.putString(RestConstants.ORDER_NUMBER, mCheckoutFinish.getOrderNumber());
            bundle.putString(RestConstants.TRANSACTION_SHIPPING, String.valueOf(mOrderFinish.getShippingValue()));
            bundle.putString(RestConstants.TRANSACTION_TAX, String.valueOf(mOrderFinish.getVatValue()));
            bundle.putString(RestConstants.PAYMENT_METHOD, mOrderFinish.getPaymentMethod());
            bundle.putDouble(RestConstants.ORDER_GRAND_TOTAL, mOrderFinish.getPriceForTracking());

            if(mCheckoutFinish.getRichRelevance() == null && CollectionUtils.isNotEmpty(mCheckoutFinish.getRelatedProducts())) {
                final RichRelevance richRelevance = new RichRelevance();
                richRelevance.setRichRelevanceProducts(mCheckoutFinish.getRelatedProducts());
                mCheckoutFinish.setRichRelevance(richRelevance);
            }

            if(mCheckoutFinish.getRichRelevance() != null){
                bundle.putParcelable(RestConstants.RECOMMENDED_PRODUCTS, mCheckoutFinish.getRichRelevance());
            }

            getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_THANKS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Process the error event
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event
        switch (eventType) {
            case SET_MULTI_STEP_FINISH:
                showFragmentContentContainer();
                break;
            default:
                break;
        }
    }

    /**
     * Method that controls the visibility of the Edit buttons for the case where the user goes to external payment
     * and presses back to my order
     */
    private void controlEditButtonsVisibility(){
        int value = mCheckoutFinish != null ? View.GONE : View.VISIBLE;
        UIUtils.showOrHideViews(value, mEditShippingAddress, mEditBillingAddress, mEditShippingMethod, mEditPaymentMethod);
    }

}
