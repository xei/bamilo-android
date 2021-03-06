package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.factories.FormFactory;
import com.bamilo.android.framework.components.customfontviews.EditText;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.GetStepPaymentHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.checkout.SetStepPaymentHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.voucher.AddVoucherHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.voucher.RemoveVoucherHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.forms.FormInputType;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.checkout.MultiStepPayment;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.R;

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

    private String mVoucherCode;

    private boolean removeVoucher = false;

    private Bundle mSavedState;
    
    private String paymentName = "";

    private View mCheckoutTotalBar;

    private View mCheckoutButtonNext;

    private ScrollView mScrollView;

    private View mVoucherContainer;

    private boolean isShowingNoPaymentNecessary;

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

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Validate the saved values
        if(savedInstanceState != null){
            mSavedState = savedInstanceState;
            mVoucherCode = savedInstanceState.getString(ConstantsIntentExtra.ARG_1);
        }
//        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_PAYMENT);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get containers
        mPaymentContainer = view.findViewById(R.id.checkout_payment_methods_container);
        mScrollView = view.findViewById(R.id.payment_scroll);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
//        TrackerDelegator.trackPage(TrackingPage.PAYMENT_SCREEN, getLoadTime(), true);
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
        // Save voucher
        if(mVoucherView != null) {
            outState.putString(ConstantsIntentExtra.ARG_1, mVoucherView.getText().toString());
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
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
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
            if (!TextUtils.isEmpty(mVoucherView.getText()) && !TextUtils.equals(couponButton.getText(), getString(R.string.remove_label))) {
                validateCoupon();
            } else {
                onClickSubmitPaymentButton();
            }
            getBaseActivity().hideKeyboard();
        }
        // Case Unknown
        else {
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
        if (null != BamiloApplication.CUSTOMER) {
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
        mDynamicForm = FormFactory.create(FormConstants.PAYMENT_DETAILS_FORM, getActivity(), form);
        mPaymentContainer.removeAllViews();
        mPaymentContainer.addView(mDynamicForm.getContainer());
        mDynamicForm.loadSaveFormState(mSavedState);
        mPaymentContainer.refreshDrawableState();
        prepareCouponView();
        validatePaymentIsAvailableOrIsNecessary();
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
    @SuppressWarnings("ConstantConditions")
    private void validatePaymentIsAvailableOrIsNecessary() {
        try {
            // Get input type to validate the form
            FormInputType type = (FormInputType) getView().findViewById(R.id.text_information).getTag();
            // Case no payment necessary
            if(type == FormInputType.infoMessage) {
                isShowingNoPaymentNecessary = true;
            }
            // Case no payment available
            else if(type == FormInputType.errorMessage) {
                mCheckoutButtonNext.setVisibility(View.GONE);
            }
        } catch (NullPointerException e) {
            //...
        }
    }

    private void prepareCouponView() {
        mVoucherView = mVoucherContainer.findViewById(R.id.voucher_name);
        if (!TextUtils.isEmpty(mVoucherCode)) {
            mVoucherView.setText(mVoucherCode);
        }
        UIUtils.scrollToViewByClick(mScrollView, mVoucherView);
        couponButton = mVoucherContainer.findViewById(R.id.voucher_btn);
        if (removeVoucher) {
            couponButton.setText(getString(R.string.remove_label));
        }
        couponButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateCoupon();
            }
        });
    }

    private void validateCoupon() {
        mVoucherCode = mVoucherView.getText().toString();
        getBaseActivity().hideKeyboard();
        if (!TextUtils.isEmpty(mVoucherCode)) {
            if (TextUtils.equals(getString(R.string.use_label), couponButton.getText())) {
                triggerSubmitVoucher(mVoucherCode);
            } else {
                triggerRemoveVoucher();
            }
        } else {
            showWarningErrorMessage(getString(R.string.voucher_error_message));
        }
    }
    
    private void onClickSubmitPaymentButton() {
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
                mVoucherCode = orderSummary.getCouponCode();
                if (!TextUtils.isEmpty(mVoucherCode)) {
                    removeVoucher = true;
                    prepareCouponView();
                    mVoucherView.setText(mVoucherCode);
                    mVoucherView.setFocusable(false);
                } else {
                    mVoucherCode = null;
                }
            } else {
                mVoucherView.setText(TextUtils.isNotEmpty(mVoucherCode) ? mVoucherCode : "" );
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
            return;
        }
        
        EventType eventType = baseResponse.getEventType();

        switch (eventType) {
        case GET_MULTI_STEP_PAYMENT:
            // Get order summary
            MultiStepPayment responseData = (MultiStepPayment) baseResponse.getContentData();
            // Form
            loadForm(responseData.getForm());
            // Set the checkout total bar
            setOrderInfo(responseData.getOrderSummary());
            break;
        case SET_MULTI_STEP_PAYMENT:
            // Get next step
            NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
            FragmentType nextFragment = nextStepStruct.getFragmentType();
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.CHECKOUT_FINISH;
            // Tracking
            String userId = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString() : "";
            String email = BamiloApplication.INSTANCE.getCustomerUtils().getEmail();
//            TrackerDelegator.trackPaymentMethod(userId, email, paymentName);
            // Switch to FINISH
            getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            break;
        case REMOVE_VOUCHER:
            mVoucherCode = null;
        case ADD_VOUCHER:
            couponButton.setText(getString(eventType == EventType.ADD_VOUCHER ? R.string.remove_label : R.string.use_label));
            removeVoucher = eventType == EventType.ADD_VOUCHER;
            hideActivityProgress();
            setOrderInfo((PurchaseEntity) baseResponse.getContentData());
            // Case voucher removed and is showing no payment necessary
            if(isShowingNoPaymentNecessary) {
                isShowingNoPaymentNecessary = false;
                triggerGetPaymentMethods();
            }
            break;
        default:
            break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
    	// Generic error
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Get event type and error
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        // Validate event type
        switch (eventType) {
        case GET_MULTI_STEP_PAYMENT:
            break;
        case SET_MULTI_STEP_PAYMENT:
//            TrackerDelegator.trackFailedPayment(paymentName, BamiloApplication.INSTANCE.getCart());
            showWarningErrorMessage(baseResponse.getValidateMessage());
            showFragmentContentContainer();
            break;
        case ADD_VOUCHER:
        case REMOVE_VOUCHER:
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
    
    private void triggerSubmitVoucher(String code) {
        triggerContentEventProgress(new AddVoucherHelper(), AddVoucherHelper.createBundle(code), this);
    }
    
    private void triggerRemoveVoucher() {
        triggerContentEventProgress(new RemoveVoucherHelper(), null, this);
    }
    
    private void triggerGetPaymentMethods(){
        triggerContentEvent(new GetStepPaymentHelper(), null, this);
    }

}
