/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.Iterator;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.Button;
import pt.rocket.components.customfontviews.EditText;
import pt.rocket.components.customfontviews.TextView;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.tracking.TrackingEvent;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.checkout.GetPaymentMethodsHelper;
import pt.rocket.helpers.checkout.SetPaymentMethodHelper;
import pt.rocket.helpers.voucher.RemoveVoucherHelper;
import pt.rocket.helpers.voucher.SetVoucherHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.Toast;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutPaymentMethodsFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutPaymentMethodsFragment.class);

    private static final String SAVED_STATE = "saved_state";
    
    private static CheckoutPaymentMethodsFragment paymentMethodsFragment;

    private ViewGroup paymentMethodsContainer;
    
    private DynamicForm formGenerator;

    //Voucher
    private Button couponButton;
    // private View voucherDivider;
    private TextView voucherError;
    EditText voucherCode;
    private String mVoucher = null;
    private boolean noPaymentNeeded = false;
    
    private boolean removeVoucher = false;
    private OrderSummary orderSummary;

    private ContentValues mSavedState;
    
    private String paymentName = "";
    
    /**
     * 
     * @return
     */
    public static CheckoutPaymentMethodsFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
        paymentMethodsFragment = new CheckoutPaymentMethodsFragment();
        return paymentMethodsFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutPaymentMethodsFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.layout.checkout_payment_main,
                R.string.checkout_label,
                KeyboardState.ADJUST_CONTENT,
                ConstantsCheckout.CHECKOUT_PAYMENT);
        // ConstantsCheckout.CHECKOUT_PAYMENT
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
        // Validate the saved values 
        if(savedInstanceState != null){
            // Get the ship content values
            mSavedState = savedInstanceState.getParcelable(SAVED_STATE);
        }
        Bundle params = new Bundle();        
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putSerializable(TrackerDelegator.GA_STEP_KEY, TrackingEvent.CHECKOUT_STEP_PAYMENT);
        TrackerDelegator.trackCheckoutStep(params);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get containers
        paymentMethodsContainer = (ViewGroup) view.findViewById(R.id.checkout_payment_methods_container);
        // Buttons
        view.findViewById(R.id.checkout_payment_button_enter).setOnClickListener(this);
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            // Get and show addresses
            triggerGetPaymentMethods();
        } else {
            showFragmentRetry(this);
        }
    }

    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
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
        TrackerDelegator.trackPage(TrackingPage.PAYMENT_SCREEN, getLoadTime(), true);
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
            Log.w(TAG, "TRY SAVE FORM BUT IS NULL");
        } 
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
        Log.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    /**
     * Load the dynamic form
     * 
     * @param form
     */
    private void loadForm(Form form) {
        Log.i(TAG, "LOAD FORM");
        formGenerator = FormFactory.getSingleton().CreateForm(FormConstants.PAYMENT_DETAILS_FORM, getActivity(), form);
        paymentMethodsContainer.removeAllViews();
        paymentMethodsContainer.addView(formGenerator.getContainer());
        loadSavedValues(mSavedState, formGenerator.getIterator());
        paymentMethodsContainer.refreshDrawableState();
        prepareCouponView();
        showFragmentContentContainer();
    }
    
    /**
     * Load the saved values and update the form
     * @param savedValues
     * @param iter
     * @author sergiopereira
     */
    private void loadSavedValues(ContentValues savedValues, Iterator<DynamicFormItem> iter){
        // Load save state
        if (savedValues != null){
            while (iter.hasNext()){
                try {
                    ((DynamicFormItem) iter.next()).setSelectedPaymentMethod(JumiaApplication.INSTANCE.lastPaymentSelected);
                    ((DynamicFormItem) iter.next()).loadState(mSavedState);
                } catch (Exception e) {
                    Log.w(TAG, "CAN'T LOAD THE SAVED STATE");
                }
            }
        }
    }
    
    
    private void generateNoPayment(){
        paymentMethodsContainer.removeAllViews();
        LayoutInflater mLayoutInflater = LayoutInflater.from(getBaseActivity());
        View view = mLayoutInflater.inflate(R.layout.no_payment_layout, null);
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

    
    /**
     * ############# CLICK LISTENER #############
     */
    
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Submit
        if(id == R.id.checkout_payment_button_enter){
            onClickSubmitPaymentButton(); 
            getBaseActivity().hideKeyboard();
        }
        // Case retry
        else if(id == R.id.fragment_root_retry_button) onClickRetryButton();
        // Case Unknown
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
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
//            restartAllFragments();
        }
    }
    
    
    private void onClickSubmitPaymentButton() {
        Log.i(TAG, "ON CLICK: Submit Payment Method");
        if(formGenerator != null){
            if(formGenerator.validate()){
                ContentValues values = formGenerator.save();
                // JumiaApplication.INSTANCE.setPaymentMethod(values);
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
     * 
     * @param orderSummary
     */
    private void updateVoucher(OrderSummary orderSummary) {
        if (orderSummary != null) {
            if (orderSummary.hasCouponCode()) {
                mVoucher = orderSummary.getDiscountCouponCode();
                if (!TextUtils.isEmpty(mVoucher)) {
                    removeVoucher = true;
                    prepareCouponView();
                } else {
                    mVoucher = null;
                }
            }
        }
    }

    /**
     * ############# RESPONSE #############
     */
  
    protected boolean onSuccessEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_PAYMENT_METHODS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            // Get order summary
            orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_PAYMENT, orderSummary);
            if(orderSummary != null && orderSummary.getTotal()!= null && Float.parseFloat(orderSummary.getTotal()) == 0){
                noPaymentNeeded = true;
                formGenerator = null;
                generateNoPayment();
            } else {
                // Form
                Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                loadForm(form);                
            }

            updateVoucher(orderSummary);

            break;
        case SET_PAYMENT_METHOD_EVENT:
            Log.d(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            // Get order summary
            OrderSummary orderFinish = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            Log.d(TAG, "ORDER SUMMARY: " + orderFinish.toString());
            // Get next step
            FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.MY_ORDER;
            
            Bundle params = new Bundle();
            params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
            params.putString(TrackerDelegator.PAYMENT_METHOD_KEY, paymentName);    
            TrackerDelegator.trackPaymentMethod(params);
            
            // Switch to FINISH
            bundle.clear();
            bundle.putParcelable(ConstantsIntentExtra.ORDER_FINISH, orderFinish);
            getBaseActivity().onSwitchFragment(nextFragment, bundle, FragmentController.ADD_TO_BACK_STACK);
            

            
            break;
        case ADD_VOUCHER:
            couponButton.setText(getString(R.string.voucher_remove));
            voucherError.setVisibility(View.GONE);
            // voucherDivider.setBackgroundColor(R.color.grey_dividerlight);
            hideActivityProgress();
            noPaymentNeeded = false;
            removeVoucher = true;
            triggerGetPaymentMethods();
            break;
        case REMOVE_VOUCHER:
            noPaymentNeeded = false;
            couponButton.setText(getString(R.string.voucher_use));
            voucherError.setVisibility(View.GONE);
            // voucherDivider.setBackgroundColor(R.color.grey_dividerlight);
            hideActivityProgress();
            triggerGetPaymentMethods();
            removeVoucher = false;
            break;
        default:
            break;
        }
        
        return true;
    }
    

    protected boolean onErrorEvent(Bundle bundle) {
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
    	// Generic error
        if (super.handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case GET_PAYMENT_METHODS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHIPPING_METHODS_EVENT");
            break;
        case SET_PAYMENT_METHOD_EVENT:
            //GTM TRACKING
            ContentValues values = new ContentValues();
            if(formGenerator != null){
                values = formGenerator.save();
                if(values != null && values.containsKey("name") && null != JumiaApplication.INSTANCE.getCart()){
                    String paymentMethod = values.getAsString("name");
                    TrackerDelegator.trackFailedPayment(paymentMethod, JumiaApplication.INSTANCE.getCart().getCartValueEuroConverted());
                }
            }
            

            Log.d(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            break;
        case ADD_VOUCHER:
        case REMOVE_VOUCHER:
            voucherCode.setText("");
            voucherError.setVisibility(View.VISIBLE);
            // voucherDivider.setBackgroundColor(R.color.red_middle);
            hideActivityProgress();
            break;
        default:
            break;
        }
        
        
        return false;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    private void triggerSubmitPaymentMethod(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetPaymentMethodHelper.FORM_CONTENT_VALUES, values);
        triggerContentEvent(new SetPaymentMethodHelper(), bundle, this);
    }
    
    private void triggerSubmitVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetVoucherHelper.VOUCHER_PARAM, values);
        triggerContentEventProgress(new SetVoucherHelper(), bundle, this);
    }
    
    private void triggerRemoveVoucher(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RemoveVoucherHelper.VOUCHER_PARAM, values);
        triggerContentEventProgress(new RemoveVoucherHelper(), bundle, this);
    }
    
    private void triggerGetPaymentMethods(){
        Log.i(TAG, "TRIGGER: GET PAYMENT METHODS");
        triggerContentEvent(new GetPaymentMethodsHelper(), null, this);
    }
    
    /**
     * ########### RESPONSE LISTENER ###########  
     */
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }
        
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }

}
