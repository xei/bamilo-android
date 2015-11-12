package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

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
import com.mobile.helpers.checkout.GetPaymentMethodsHelper;
import com.mobile.helpers.checkout.SetPaymentMethodHelper;
import com.mobile.helpers.voucher.AddVoucherHelper;
import com.mobile.helpers.voucher.RemoveVoucherHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.objects.checkout.CheckoutFormPayment;
import com.mobile.newFramework.objects.checkout.SetPaymentMethod;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.Iterator;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutPaymentMethodsFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = CheckoutPaymentMethodsFragment.class.getSimpleName();

    private static final String SAVED_STATE = "saved_state";

    EditText voucherCode;

    private ViewGroup paymentMethodsContainer;

    private DynamicForm formGenerator;

    private TextView couponButton;

    private TextView voucherError;

    private String mVoucher = null;

    private boolean noPaymentNeeded = false;
    
    private boolean removeVoucher = false;
    
    private PurchaseEntity orderSummary;

    private ContentValues mSavedState;
    
    private String paymentName = "";

    private View mCheckoutTotalBar;

    private View mCheckoutButtonNext;
    
    /**
     * Empty constructor
     */
    public CheckoutPaymentMethodsFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.Checkout,
                R.layout.checkout_payment_main,
                R.string.checkout_label,
                KeyboardState.ADJUST_CONTENT,
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
            // Get the ship content values
            mSavedState = savedInstanceState.getParcelable(SAVED_STATE);
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
        paymentMethodsContainer = (ViewGroup) view.findViewById(R.id.checkout_payment_methods_container);
        // Buttons
        view.findViewById(R.id.checkout_button_enter).setOnClickListener(this);
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
        if(formGenerator != null){
            JumiaApplication.INSTANCE.lastPaymentSelected = formGenerator.getSelectedValueIndex();
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
            if (!TextUtils.isEmpty(voucherCode.getText()) && !couponButton.getText().toString().equalsIgnoreCase(getString(R.string.voucher_remove))) {
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

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current selected item
        try {
            ContentValues values = formGenerator.save();
            if(values.size() > 0)
                outState.putParcelable(SAVED_STATE, values);
        } catch (Exception e) {
            Print.w(TAG, "TRY SAVE FORM BUT IS NULL");
        }
    }

    /**
     * Load the dynamic form
     */
    private void loadForm(Form form) {
        Print.i(TAG, "LOAD FORM");
        formGenerator = FormFactory.getSingleton().CreateForm(FormConstants.PAYMENT_DETAILS_FORM, getActivity(), form);
        paymentMethodsContainer.removeAllViews();
        paymentMethodsContainer.addView(formGenerator.getContainer());
        loadSavedValues(mSavedState, formGenerator.getIterator());
        paymentMethodsContainer.refreshDrawableState();
        prepareCouponView();
        validatePaymentIsAvailable();
        showFragmentContentContainer();
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

    /**
     * Load the saved values and update the form
     */
    private void loadSavedValues(ContentValues savedValues, Iterator<DynamicFormItem> iter){
        // Load save state
        if (savedValues != null){
            while (iter.hasNext()){
                try {
                    iter.next().setSelectedPaymentMethod(JumiaApplication.INSTANCE.lastPaymentSelected);
                    iter.next().loadState(mSavedState);
                } catch (Exception e) {
                    Print.w(TAG, "CAN'T LOAD THE SAVED STATE");
                }
            }
        }
    }

    private void generateNoPayment(){
        paymentMethodsContainer.removeAllViews();
        LayoutInflater mLayoutInflater = LayoutInflater.from(getBaseActivity());
        View view = mLayoutInflater.inflate(R.layout.no_payment_layout, paymentMethodsContainer, false);
        paymentMethodsContainer.addView(view);
        prepareCouponView();
        paymentMethodsContainer.refreshDrawableState();
        showFragmentContentContainer();
    }

    private void prepareCouponView() {
        voucherCode = (EditText) getView().findViewById(R.id.voucher_name);
        if (!TextUtils.isEmpty(mVoucher)) {
            voucherCode.setText(mVoucher);
        }

        // voucherDivider = getView().findViewById(R.id.voucher_divider);
        voucherError = (TextView) getView().findViewById(R.id.voucher_error_message);
        couponButton = (TextView) getView().findViewById(R.id.voucher_btn);
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
        mVoucher = voucherCode.getText().toString();
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
            getBaseActivity().warningFactory.showWarning(com.mobile.utils.ui.WarningFactory.ERROR_VOUCHER, getString(R.string.voucher_error_message));
        }
    }
    
    private void onClickSubmitPaymentButton() {
        Print.i(TAG, "ON CLICK: Submit Payment Method");
        if(formGenerator != null){
            if(formGenerator.validate()){
                ContentValues values = formGenerator.save();
                paymentName = values.getAsString("name");
                triggerSubmitPaymentMethod(values);
            } else {
                Toast.makeText(getActivity(), getString(R.string.please_fill_all_data),Toast.LENGTH_SHORT).show();
            }
        } else if (noPaymentNeeded) {
            // Get next step
            FragmentType nextFragment = FragmentType.MY_ORDER;
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantsIntentExtra.ORDER_FINISH, orderSummary);
            getBaseActivity().onSwitchFragment(nextFragment, bundle, FragmentController.ADD_TO_BACK_STACK);
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
                    voucherCode.setText(mVoucher);
                    voucherCode.setFocusable(false);
                } else {
                    mVoucher = null;
                }
            } else {
                voucherCode.setText("");
                voucherCode.setFocusable(true);
                voucherCode.setFocusableInTouchMode(true);
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
        case GET_PAYMENT_METHODS_EVENT:
            Print.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            // Get order summary
            CheckoutFormPayment responseData = (CheckoutFormPayment) baseResponse.getMetadata().getData();
            orderSummary = responseData.getOrderSummary();
            super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_PAYMENT, orderSummary);
            // Set the checkout total bar
            CheckoutStepManager.setTotalBar(mCheckoutTotalBar, orderSummary);
            // Validate
            // TODO VALIDATE THIS NECESSARY
            if(orderSummary != null && orderSummary.getTotal() == 0){
                noPaymentNeeded = true;
                formGenerator = null;
                generateNoPayment();
            } else {
                // Form
                Form form = responseData.getForm();
                loadForm(form);                
            }
            updateVoucher(orderSummary);
            break;
        case SET_PAYMENT_METHOD_EVENT:
            Print.d(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            // Get next step
            NextStepStruct nextStepStruct = (NextStepStruct)baseResponse.getMetadata().getData();
            FragmentType nextFragment = nextStepStruct.getFragmentType();
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.MY_ORDER;
            // Tracking
            String userId = JumiaApplication.CUSTOMER != null ? JumiaApplication.CUSTOMER.getIdAsString() : "";
            String email = JumiaApplication.INSTANCE.getCustomerUtils().getEmail();
            TrackerDelegator.trackPaymentMethod(userId, email, paymentName);
            // Switch to FINISH
            Bundle bundle = new Bundle();
            bundle.putParcelable(ConstantsIntentExtra.ORDER_FINISH, ((SetPaymentMethod) nextStepStruct.getCheckoutStepObject()).getOrderSummary());
            getBaseActivity().onSwitchFragment(nextFragment, bundle, FragmentController.ADD_TO_BACK_STACK);
            break;
        case ADD_VOUCHER:
            couponButton.setText(getString(R.string.voucher_remove));
            voucherError.setVisibility(View.GONE);
            hideActivityProgress();
            noPaymentNeeded = false;
            removeVoucher = true;
            triggerGetPaymentMethods();
            break;
        case REMOVE_VOUCHER:
            noPaymentNeeded = false;
            couponButton.setText(getString(R.string.voucher_use));
            voucherError.setVisibility(View.GONE);
            hideActivityProgress();
            triggerGetPaymentMethods();
            removeVoucher = false;
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
        ErrorCode errorCode = baseResponse.getError().getErrorCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);
        // Validate event type
        switch (eventType) {
        case GET_PAYMENT_METHODS_EVENT:
            Print.i(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            break;
        case SET_PAYMENT_METHOD_EVENT:
            Print.i(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            //GTM TRACKING
            if(formGenerator != null){
                ContentValues values = formGenerator.save();
                if(values != null && values.containsKey("name") && null != JumiaApplication.INSTANCE.getCart()){
                    String paymentMethod = values.getAsString("name");
                    TrackerDelegator.trackFailedPayment(paymentMethod, JumiaApplication.INSTANCE.getCart().getCartValueEuroConverted());
                }
            }
            showUnexpectedErrorWarning();
            showFragmentContentContainer();
            break;
        case ADD_VOUCHER:
        case REMOVE_VOUCHER:
            voucherCode.setText("");
            voucherError.setVisibility(View.VISIBLE);
            hideActivityProgress();
            break;
        default:
            break;
        }
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerSubmitPaymentMethod(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new SetPaymentMethodHelper(), bundle, this);
    }
    
    private void triggerSubmitVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventProgress(new AddVoucherHelper(), bundle, this);
    }
    
    private void triggerRemoveVoucher() {
        triggerContentEventProgress(new RemoveVoucherHelper(), null, this);
    }
    
    private void triggerGetPaymentMethods(){
        Print.i(TAG, "TRIGGER: GET PAYMENT METHODS");
        triggerContentEvent(new GetPaymentMethodsHelper(), null, this);
    }

}
