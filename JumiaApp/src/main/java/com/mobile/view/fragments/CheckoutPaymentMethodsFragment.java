package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.checkout.GetStepPaymentHelper;
import com.mobile.helpers.checkout.SetStepPaymentHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.MultiStepPayment;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutPaymentMethodsFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = CheckoutPaymentMethodsFragment.class.getSimpleName();

    private EditText mVoucherView;

    private ViewGroup mPaymentContainer;

    private DynamicForm mDynamicForm;

    private TextView couponButton;

    private String mVoucher = null;

    private boolean removeVoucher = false;

    private Bundle mSavedState;
    
    private String paymentName = "";

    private View mCheckoutTotalBar;

    private View mCheckoutButtonNext;

    private ScrollView mScrollView;

    private View mVoucherContainer;

    /**
     * Empty constructor
     */
    public CheckoutPaymentMethodsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.CHECKOUT,
                R.layout.checkout_payment_main,
                R.string.checkout_label,
                ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_PAYMENT);
    }

    /**
     * Get new instance of CheckoutPaymentMethodsFragment.
     * @return CheckoutPaymentMethodsFragment
     */
    public static CheckoutPaymentMethodsFragment getInstance() {
        return new CheckoutPaymentMethodsFragment();
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
        // Validate the saved values 
        if(savedInstanceState != null){
            mSavedState = savedInstanceState;
        }
        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_PAYMENT);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get containers
        mPaymentContainer = (ViewGroup) view.findViewById(R.id.checkout_payment_methods_container);
        mScrollView = (ScrollView) view.findViewById(R.id.payment_scroll);
        // Buttons
        view.findViewById(R.id.checkout_button_enter).setOnClickListener(this);
        // Voucher
        mVoucherContainer = view.findViewById(R.id.voucher_container);
        // Checkout total view
        mCheckoutTotalBar = view.findViewById(R.id.checkout_total_bar);
        mCheckoutButtonNext = view.findViewById(R.id.checkout_button_enter);
        // Get and show addresses
        triggerGetPaymentMethods();
    }

    
    /*
     * (non-Javadoc)
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
        TrackerDelegator.trackPage(TrackingPage.PAYMENT_SCREEN, getLoadTime(), true);
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
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the form state
        if(mDynamicForm != null) {
            mDynamicForm.saveFormState(outState);
        }
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
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /**
     * ############# CLICK LISTENER #############
     */

    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Submit
        if (id == R.id.checkout_button_enter) {
            if (!TextUtils.isEmpty(mVoucherView.getText()) && !couponButton.getText().toString().equalsIgnoreCase(getString(R.string.voucher_remove))) {
                validateCoupon();
            } else {
                onClickSubmitPaymentButton();
            }
            getBaseActivity().hideKeyboard();
        }
        // Case Unknown
        else {
            Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    // Process the click on retry button.
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        if (null != JumiaApplication.CUSTOMER) {
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }

    /**
     * Load the dynamic form
     */
    private void loadForm(Form form) {
        Print.i(TAG, "LOAD FORM");
        mDynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.PAYMENT_DETAILS_FORM, getActivity(), form);
        mPaymentContainer.removeAllViews();
        mPaymentContainer.addView(mDynamicForm.getContainer());
        mDynamicForm.loadSaveFormState(mSavedState);
        mPaymentContainer.refreshDrawableState();
        prepareCouponView();
        validatePaymentIsAvailable();
        showFragmentContentContainer();
    }

    /**
     * Set the all views associated to order.
     */
    private void setOrderInfo(PurchaseEntity purchaseEntity) {
        // Set the checkout total bar
        CheckoutStepManager.setTotalBar(mCheckoutTotalBar, purchaseEntity);
        // Update voucher
        updateVoucher(purchaseEntity);
        // Show checkout summary
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_PAYMENT, purchaseEntity);
    }

    /*
     * Disable the next button case No payment options available
     */
    private void validatePaymentIsAvailable() {
        try {
            // Case No payment options available hide button
            //noinspection ConstantConditions
            mCheckoutButtonNext.setVisibility(getView().findViewById(R.id.text_information) == null ? View.VISIBLE : View.GONE);
        } catch (NullPointerException e) {
            //...
        }
    }

    private void prepareCouponView() {
        mVoucherView = (EditText) mVoucherContainer.findViewById(R.id.voucher_name);
        if (!TextUtils.isEmpty(mVoucher)) {
            mVoucherView.setText(mVoucher);
        }
        UIUtils.scrollToViewByClick(mScrollView, mVoucherView);
        couponButton = (TextView) mVoucherContainer.findViewById(R.id.voucher_btn);
        if (removeVoucher) {
            couponButton.setText(getString(R.string.voucher_remove));
        }
        couponButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCoupon();
            }
        });
    }

    private void validateCoupon() {
        mVoucher = mVoucherView.getText().toString();
        getBaseActivity().hideKeyboard();
        if (!TextUtils.isEmpty(mVoucher)) {
            if (getString(R.string.voucher_use).equalsIgnoreCase(couponButton.getText().toString())) {
                ContentValues mContentValues = new ContentValues();
                mContentValues.put(AddVoucherHelper.VOUCHER_PARAM, mVoucher);
                triggerSubmitVoucher(mContentValues);
            } else {
                triggerRemoveVoucher();
            }
        } else {
            showWarningErrorMessage(getString(R.string.voucher_error_message));
        }
    }
    
    private void onClickSubmitPaymentButton() {
        Print.i(TAG, "ON CLICK: Submit Payment Method");
        if(mDynamicForm != null){
            if(mDynamicForm.validate()){
                ContentValues values = mDynamicForm.save();
                paymentName = values.getAsString(RestConstants.NAME);
                triggerSubmitPaymentMethod(mDynamicForm.getForm().getAction(), values);
            } else {
                showWarningErrorMessage(getString(R.string.please_fill_all_data));
            }
        }
    }

    /**
     * Fill Coupon field if orderSummary has discountCouponCode
     */
    private void updateVoucher(PurchaseEntity orderSummary) {
        if (orderSummary != null) {
            if (orderSummary.hasCouponDiscount()) {
                mVoucher = orderSummary.getCouponCode();
                if (!TextUtils.isEmpty(mVoucher)) {
                    removeVoucher = true;
                    prepareCouponView();
                    mVoucherView.setText(mVoucher);
                    mVoucherView.setFocusable(false);
                } else {
                    mVoucher = null;
                }
            } else {
                mVoucherView.setText("");
                mVoucherView.setFocusable(true);
                mVoucherView.setFocusableInTouchMode(true);
            }
        }
    }

    /**
     * ############# RESPONSE #############
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_MULTI_STEP_PAYMENT:
            Print.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            // Get order summary
            MultiStepPayment responseData = (MultiStepPayment) baseResponse.getContentData();
            // Form
            loadForm(responseData.getForm());
            // Set the checkout total bar
            setOrderInfo(responseData.getOrderSummary());
            break;
        case SET_MULTI_STEP_PAYMENT:
            Print.d(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            // Get next step
            NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
            FragmentType nextFragment = nextStepStruct.getFragmentType();
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.CHECKOUT_FINISH;
            // Tracking
            String userId = JumiaApplication.CUSTOMER != null ? JumiaApplication.CUSTOMER.getIdAsString() : "";
            String email = JumiaApplication.INSTANCE.getCustomerUtils().getEmail();
            TrackerDelegator.trackPaymentMethod(userId, email, paymentName);
            // Switch to FINISH
            getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            break;
        case ADD_VOUCHER:
        case REMOVE_VOUCHER:
            couponButton.setText(getString(eventType == EventType.ADD_VOUCHER ? R.string.voucher_remove : R.string.voucher_use));
            removeVoucher = eventType == EventType.ADD_VOUCHER;
            hideActivityProgress();
            setOrderInfo((PurchaseEntity) baseResponse.getContentData());
            break;
        default:
            break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
    	// Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return;
        }
        // Get event type and error
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        // Validate event type
        switch (eventType) {
        case GET_MULTI_STEP_PAYMENT:
            Print.i(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            break;
        case SET_MULTI_STEP_PAYMENT:
            Print.i(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            TrackerDelegator.trackFailedPayment(paymentName, JumiaApplication.INSTANCE.getCart());
            showWarningErrorMessage(baseResponse.getValidateMessage());
            showFragmentContentContainer();
            break;
        case ADD_VOUCHER:
        case REMOVE_VOUCHER:
            mVoucherView.setText("");
            hideActivityProgress();
            break;
        default:
            break;
        }
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerSubmitPaymentMethod(String endpoint, ContentValues values) {
        triggerContentEvent(new SetStepPaymentHelper(), SetStepPaymentHelper.createBundle(endpoint, values), this);
    }
    
    private void triggerSubmitVoucher(ContentValues values) {
        triggerContentEventProgress(new AddVoucherHelper(), AddVoucherHelper.createBundle(values), this);
    }
    
    private void triggerRemoveVoucher() {
        triggerContentEventProgress(new RemoveVoucherHelper(), null, this);
    }
    
    private void triggerGetPaymentMethods(){
        triggerContentEvent(new GetStepPaymentHelper(), null, this);
    }

}
