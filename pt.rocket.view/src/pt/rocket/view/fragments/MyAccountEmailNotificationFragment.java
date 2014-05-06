/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import org.holoeverywhere.widget.CheckBox;

import pt.rocket.app.JumiaApplication;
import pt.rocket.forms.Form;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.CustomerNewsletterSubscription;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.account.GetNewslettersFormHelper;
import pt.rocket.helpers.account.SubscribeNewslettersHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show the newsletter form 
 * @author sergiopereira
 */
public class MyAccountEmailNotificationFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(MyAccountEmailNotificationFragment.class);
    
    public static final int NEWSLETTER_MALE_ID = 6;
    
    public static final int NEWSLETTER_FEMALE_ID = 5;
    
    public static final int NEWSLETTER_UNKNOWN_ID = 0;
    
    private static MyAccountEmailNotificationFragment sEmailNotificationFragment;

    private Form mNewslettersForm;

    private CheckBox mNewsletterMale;

    private CheckBox mNewsletterFemale;

    /**
     * Create new instance
     * @return MyAccountEmailNotificationFragment
     * @author sergiopereira
     */
    public static MyAccountEmailNotificationFragment newInstance(Bundle bundle) {
        sEmailNotificationFragment = new MyAccountEmailNotificationFragment();
        return sEmailNotificationFragment;
    }

    /**
     * Empty constructor
     * @author sergiopereira
     */
    public MyAccountEmailNotificationFragment() {
        super(EnumSet.noneOf(EventType.class), 
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.MyAccount, 
                R.string.myaccount_email_notifications, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.my_account_email_notification_fragment, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get check box male
        mNewsletterMale = (CheckBox) view.findViewById(R.id.myaccount_newsletter_checkbox_male);
        // Get check box female
        mNewsletterFemale = (CheckBox) view.findViewById(R.id.myaccount_newsletter_checkbox_female);
        // Get save button
        view.findViewById(R.id.myaccount_newsletter_save).setOnClickListener((OnClickListener) this);
        // Get cancel button
        view.findViewById(R.id.myaccount_newsletter_cancel).setOnClickListener((OnClickListener) this);
        // Validate data
        if(mNewslettersForm == null)
            triggerGetNewslettersForm();
        else 
            showNewslettersForm();
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
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
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
     * Show the newsletter form
     * @author sergiopereira
     */
    private void showNewslettersForm() {
        // Load customer state
        loadCustomerNewsletterState();
        // Show form
        getBaseActivity().showContentContainer();
    }
    
    /**
     * Load the customer newsletters subscription
     * @author sergiopereira
     */
    private void loadCustomerNewsletterState(){
        Customer customer = JumiaApplication.CUSTOMER;
        if(customer.hasNewsletterSubscriptions()) {
            for (CustomerNewsletterSubscription subscription : customer.getNewsletterSubscriptions()) {
                mNewsletterMale.setChecked((subscription.getId() == 6) ? true : false);
                mNewsletterFemale.setChecked((subscription.getId() == 5) ? true : false);
            }
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
        // Get view id
        int id = view.getId();
        // Next button
        if(id == R.id.myaccount_newsletter_save) onClickSaveButton();
        // Next button
        else if(id == R.id.myaccount_newsletter_cancel) onClickCancelButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on the save button
     * @author sergiopereira
     */
    private void onClickSaveButton() {
        Log.i(TAG, "ON CLICK: SAVE");
        // Validate the current newsletter form
        if(mNewslettersForm != null && mNewslettersForm.fields != null && mNewslettersForm.fields.size() > 0){
            String fieldName = mNewslettersForm.fields.get(0).getName();
            ContentValues values = new ContentValues();
            
            // FIXME
            fieldName = "NewsletterForm[newsletter_categories_subscribed]";
            
            values.put(fieldName + "[" + NEWSLETTER_MALE_ID + "]", (mNewsletterMale.isChecked()) ? NEWSLETTER_MALE_ID : NEWSLETTER_UNKNOWN_ID );
            values.put(fieldName + "[" + NEWSLETTER_FEMALE_ID + "]", (mNewsletterFemale.isChecked()) ? NEWSLETTER_FEMALE_ID : NEWSLETTER_UNKNOWN_ID);
            Log.d(TAG, "VALUES: " + values.toString());
            // Subscribe or unsubscribe newsletters
            triggerSubscribeNewsletters(values);
        }
    }
    
    /**
     * Process the click on the cancel button
     * @author sergiopereira
     */
    private void onClickCancelButton() {
        Log.i(TAG, "ON CLICK: CANCEL");
        getBaseActivity().onBackPressed();
    }
    
    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to subscribe newsletters
     * @param values
     */
    private void triggerSubscribeNewsletters(ContentValues values) {
        Log.i(TAG, "TRIGGER: POLL SUBSCRIBE");
        Bundle bundle = new Bundle();
        bundle.putParcelable(SubscribeNewslettersHelper.FORM_CONTENT_VALUES, values);
        triggerContentEvent(new SubscribeNewslettersHelper(), bundle, (IResponseCallback) this);
    }
    
    /**
     * Trigger to get the newsletters form
     */
    private void triggerGetNewslettersForm(){
        Log.i(TAG, "TRIGGER: GET NEWSLETTER FORM");
        getBaseActivity().showLoadingInfo();
        triggerContentEvent(new GetNewslettersFormHelper(), null, (IResponseCallback) this);
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
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_NEWSLETTERS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_NEWSLETTERS_FORM_EVENT");
            // Get the form
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            // Save the form
            mNewslettersForm = form;
            // Show the form
            showNewslettersForm();
            break;
        case SUBSCRIBE_NEWSLETTERS_EVENT:
            Log.d(TAG, "RECEIVED SUBSCRIBE_NEWSLETTERS_EVENT");
            // Goto back
            getBaseActivity().onBackPressed();
            // Show toast
            Toast.makeText(getBaseActivity(), getString(R.string.newsletter_saved_message), Toast.LENGTH_LONG).show();
            break;
        default:
            break;
        }
        
        return true;
    }
    
    /**
     * Filter the error response
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
        
        getBaseActivity().showContentContainer();
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case GET_NEWSLETTERS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_NEWSLETTERS_FORM_EVENT");
            getBaseActivity().onBackPressed();
            Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
            break;
        case SUBSCRIBE_NEWSLETTERS_EVENT:
            Log.d(TAG, "RECEIVED SUBSCRIBE_NEWSLETTERS_EVENT");
            Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
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
