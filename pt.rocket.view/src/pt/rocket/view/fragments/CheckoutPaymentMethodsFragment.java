/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import org.holoeverywhere.widget.Toast;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.checkout.GetPaymentMethodsHelper;
import pt.rocket.helpers.checkout.SetPaymentMethodHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.akquinet.android.androlog.Log;

/**
 * FIXME: Waiting for: 
 * > NAFAMZ-5435:  MOBILE API - Add parameter in the Shipping/Payment Methods to get form with success  
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutPaymentMethodsFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutPaymentMethodsFragment.class);
    
    private static CheckoutPaymentMethodsFragment paymentMethodsFragment;
    
    /**
     * 
     * @return
     */
    public static CheckoutPaymentMethodsFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
        paymentMethodsFragment = new CheckoutPaymentMethodsFragment();
        return paymentMethodsFragment;
    }

    private ViewGroup paymentMethodsContainer;

    private ViewGroup cartContainer;

    private Form formResponse;

    private DynamicForm formGenerator;

    /**
     * Empty constructor
     */
    public CheckoutPaymentMethodsFragment() {
        super(EnumSet.of(EventType.GET_PAYMENT_METHODS_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH), 
                NavigationAction.Checkout, 
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
        setRetainInstance(true);
    }
    

    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.checkout_payment_methods, viewGroup, false);
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
        cartContainer = (ViewGroup) view.findViewById(R.id.checkout_payment_cart_container);
        // Buttons
        view.findViewById(R.id.checkout_payment_button_enter).setOnClickListener(this);
        
        // Get and show addresses
        triggerGetPaymentMethods();
                
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
        formResponse = form;
        
        formGenerator = FormFactory.getSingleton().CreateForm(FormConstants.PAYMENT_DETAILS_FORM, getActivity(), form);
        paymentMethodsContainer.removeAllViews();
        paymentMethodsContainer.addView(formGenerator.getContainer());        
        paymentMethodsContainer.refreshDrawableState();
        getBaseActivity().showContentContainer(false);
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
        } // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    private void onClickSubmitPaymentButton() {
        Log.i(TAG, "ON CLICK: Submit Payment Method");
        if(formGenerator != null){
            if(formGenerator.validate()){
                ContentValues values = formGenerator.save();
                JumiaApplication.INSTANCE.setPaymentMethod(values);
                Log.i(TAG, "code1payment : "+values.toString());
                triggerSubmitPaymentMethod(values);
            } else {
                Toast.makeText(getActivity(), "Please fill all the data",Toast.LENGTH_SHORT).show();
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
            OrderSummary orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_PAYMENT, orderSummary);
            // Form
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            loadForm(form);
            break;
        case SET_PAYMENT_METHOD_EVENT:
            Log.d(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
            // Get order summary
            OrderSummary orderFinish = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            Log.d(TAG, "ORDER SUMMARY: " + orderFinish.toString());
            // Get next step
            FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            nextFragment = (nextFragment != FragmentType.UNKNOWN) ? nextFragment : FragmentType.MY_ORDER;
            // Switch to FINISH
            bundle.clear();
            bundle.putParcelable(ConstantsIntentExtra.ORDER_FINISH, orderFinish);
            getBaseActivity().onSwitchFragment(nextFragment, bundle, FragmentController.ADD_TO_BACK_STACK);
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
        if (getBaseActivity() != null && getBaseActivity().handleErrorEvent(bundle)) {
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
            Log.d(TAG, "RECEIVED SET_PAYMENT_METHOD_EVENT");
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
