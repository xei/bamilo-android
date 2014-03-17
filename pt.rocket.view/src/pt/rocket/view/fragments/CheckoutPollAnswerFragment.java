/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
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
import pt.rocket.helpers.GetInitFormHelper;
import pt.rocket.helpers.checkout.GetPollFormHelper;
import pt.rocket.helpers.checkout.SetPollAnswerHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
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
public class CheckoutPollAnswerFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutPollAnswerFragment.class);
    
    private static CheckoutPollAnswerFragment pollFragment;
    
    private ViewGroup pollFormContainer;

    private DynamicForm pollFormGenerator;

    private Form formResponse;

    private OrderSummary orderSummary;
    
    
    /**
     * 
     * @return
     */
    public static CheckoutPollAnswerFragment getInstance(Bundle bundle) {
        if(pollFragment == null)
            pollFragment = new CheckoutPollAnswerFragment();
        return pollFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutPollAnswerFragment() {
        super(EnumSet.of(EventType.GET_POLL_FORM_EVENT, EventType.SET_POLL_ANSWER_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH), 
                NavigationAction.Checkout, 
                ConstantsCheckout.CHECKOUT_ABOUT_YOU);
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
        return inflater.inflate(R.layout.checkout_poll_question, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
                
        // Create address form
        pollFormContainer = (ViewGroup) view.findViewById(R.id.checkout_poll_form_container);
        // Next button
        view.findViewById(R.id.checkout_poll_button_enter).setOnClickListener((OnClickListener) this);

        // Get and show form
        if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
            triggerInitForm();
        } else if(formResponse != null && orderSummary != null){
            loadPollForm(formResponse);
        } else {
            triggerPollForm();
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
        TrackerDelegator.trackCheckoutStep(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), R.string.gcheckoutPollQuestion, R.string.xcheckoutpollquestion, R.string.mixprop_checkout_poll_question);
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
    
    
    /**
     * Load the dynamic form
     * @param form
     */
    private void loadPollForm(Form form) {
        Log.i(TAG, "LOAD POLL FORM");
        pollFormGenerator = FormFactory.getSingleton().CreateForm(FormConstants.POLL_FORM, getBaseActivity(), form);
        pollFormContainer.removeAllViews();
        pollFormContainer.addView(pollFormGenerator.getContainer());                
        pollFormContainer.refreshDrawableState();
        // Show
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_ABOUT_YOU, orderSummary);
        getBaseActivity().showContentContainer(false);
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
        // Get view id
        int id = view.getId();
        // Next button
        if(id == R.id.checkout_poll_button_enter) onClickPollAnswerButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on the next step button
     */
    private void onClickPollAnswerButton() {
        Log.i(TAG, "ON CLICK: POLL ANSWER");
        if(pollFormGenerator.validate()) {
            Log.i(TAG, "POLL ANSWER");    
            triggerPollAnswer(createContentValues(pollFormGenerator));
        }
    }
    
    /**
     * Method used to create the content values
     * @param dynamicForm
     * @param isDefaultShipping
     * @param isDefaultBilling
     * @return new content values
     */
    private ContentValues createContentValues(DynamicForm dynamicForm){
        // Save content values
        ContentValues mContentValues = dynamicForm.save();
        Log.d(TAG, "CURRENT CONTENT VALUES: " + mContentValues.toString());
        // FIXME:
        mContentValues.put("Alice_Module_Checkout_Model_PollingForm[pollQuestion]", "Facebook");
        Log.d(TAG, "CURRENT CONTENT VALUES: " + mContentValues.toString());
        // return the new content values
        return mContentValues;
    }
    
    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to poll answer
     * @param values
     */
    private void triggerPollAnswer(ContentValues values) {
        Log.i(TAG, "TRIGGER: POLL ANSWER");
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetPollAnswerHelper.FORM_CONTENT_VALUES, values);
        triggerContentEvent(new SetPollAnswerHelper(), bundle, this);
    }
    
    /**
     * Trigger to get the address form
     */
    private void triggerPollForm(){
        Log.i(TAG, "TRIGGER: POLLFORM");
        triggerContentEvent(new GetPollFormHelper(), null, this);
    }
    
    /**
     * Trigger to initialize forms
     */
    private void triggerInitForm(){
        Log.i(TAG, "TRIGGER: INIT FORMS");
        triggerContentEvent(new GetInitFormHelper(), null, this);
    }
    
   
    /**
     * ############# RESPONSE #############
     */
    /**
     * Filter the success response
     * @param bundle
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        Log.i(TAG, "ON SUCCESS EVENT");
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
//        if(getBaseActivity() != null){
//            Log.d(TAG, "BASE ACTIVITY HANDLE SUCCESS EVENT");
//            getBaseActivity().handleSuccessEvent(bundle);
//        } else {
//            return true;
//        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case INIT_FORMS:
            Log.d(TAG, "RECEIVED INIT_FORMS");
            triggerPollForm();
            break;
        case GET_POLL_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_POLL_FORM_EVENT");
            // Get order summary
            orderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
            // Form
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            formResponse = form;
            loadPollForm(form);
            break;
        case SET_POLL_ANSWER_EVENT:
            Log.d(TAG, "RECEIVED SET_POLL_ANSWER_EVENT");
            // Get next step
            FragmentType nextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            // Switch
            getBaseActivity().onSwitchFragment(nextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            break;
        default:
            break;
        }
        
        return true;
    }
    
    /**
     * Filter the error response
     * 
     * TODO: ADD ERROR VALIDATIONS
     * 
     * @param bundle
     * @return boolean
     */
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
        
        getBaseActivity().showContentContainer(false);
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case INIT_FORMS:
            Log.d(TAG, "RECEIVED INIT_FORMS");
            break;
        case GET_POLL_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_POLL_FORM_EVENT");
            break;
        case SET_POLL_ANSWER_EVENT:
            Log.d(TAG, "RECEIVED SET_POLL_ANSWER_EVENT");
            break;
        default:
            break;
        }
        
        return false;
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
       
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }
    
    /**
     * ########### DIALOGS ###########  
     */    

}
