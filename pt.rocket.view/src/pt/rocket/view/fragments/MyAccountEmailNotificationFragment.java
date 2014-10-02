/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import org.holoeverywhere.widget.CheckBox;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormField;
import pt.rocket.forms.NewsletterOption;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.account.GetNewslettersFormHelper;
import pt.rocket.helpers.account.SubscribeNewslettersHelper;
import pt.rocket.interfaces.IResponseCallback;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show the newsletter form 
 * @author sergiopereira
 */
public class MyAccountEmailNotificationFragment extends BaseFragment implements OnClickListener, IResponseCallback, OnCheckedChangeListener {

    private static final String TAG = LogTagHelper.create(MyAccountEmailNotificationFragment.class);
    
    private final static int UNSUBSCRIBE_VALUE = -1;
    
    private static MyAccountEmailNotificationFragment sEmailNotificationFragment;

    private Form mNewslettersForm;

    private LinearLayout mNewsletterList;

    private ArrayList<NewsletterOption> mNewsletterOptions;

    private LayoutInflater mInflater;
    
    private ArrayList<NewsletterOption> mNewsletterOptionsSaved;

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
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.layout.my_account_email_notification_fragment,
                R.string.myaccount_email_notifications,
                KeyboardState.NO_ADJUST_CONTENT);
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
        // Get inflater
        mInflater = LayoutInflater.from(getBaseActivity());
        // Validate the saved state
        if(savedInstanceState != null && savedInstanceState.containsKey(TAG)){
            Log.i(TAG, "ON GET SAVED STATE");
            mNewsletterOptionsSaved = savedInstanceState.getParcelableArrayList(TAG);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get list view
        mNewsletterList = (LinearLayout) view.findViewById(R.id.myaccount_newsletter_list);
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
        // Tracking page
        TrackerDelegator.trackPage(TrackingPage.NEWSLETTER_SUBS);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE: NEWSLETTER FORM");
        outState.putParcelableArrayList(TAG, mNewsletterOptions);
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
        try {
            FormField formField = mNewslettersForm.fields.get(0);
            if(mNewsletterOptionsSaved != null)
                mNewsletterOptions = mNewsletterOptionsSaved;
            else
                mNewsletterOptions = formField.newsletterOptions;
            generateNewsletterOptions(mNewsletterOptions, mNewsletterList);
            // Show form
            showFragmentContentContainer();
        } catch (IndexOutOfBoundsException e) {
            Log.w(TAG, "IBE ON SHOW NEWSLETTER FORM", e);
            goBackWarningUser();
        } catch (NullPointerException e) {
            Log.w(TAG, "NPE ON SHOW NEWSLETTER FORM", e);
            goBackWarningUser();
        }
    }

    /**
     * Gemerate the newsletter option and add it to container
     * @param layoutInflater
     * @author msilva
     * @param newsletterOptions 
     * @param newsletterList 
     */
    private void generateNewsletterOptions(ArrayList<NewsletterOption> newsletterOptions, LinearLayout newsletterList){
        for (int i = 0; i < newsletterOptions.size(); i++) {
            View view = mInflater.inflate(R.layout.simple_email_notification_option, newsletterList, false);
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.myaccount_newsletter_checkbox);
            checkBox.setTag("" + i);
            checkBox.setText(newsletterOptions.get(i).label);
            checkBox.setChecked(newsletterOptions.get(i).isSubscrided);
            checkBox.setOnCheckedChangeListener(this);
            newsletterList.addView(view);
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
        if(id == R.id.myaccount_newsletter_save){
            onClickSaveButton();
        }
        // Next button
        else if(id == R.id.myaccount_newsletter_cancel){
            onClickCancelButton();
        }
        
        else if(id == R.id.fragment_root_retry_button){
            Bundle bundle = new Bundle();
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.EMAIL_NOTIFICATION);
            bundle.putString(ConstantsIntentExtra.LOGIN_ORIGIN, getString(R.string.mixprop_loginlocationmyaccount));
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);

          }
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on the save button
     * @author sergiopereira
     */
    private void onClickSaveButton() {
        Log.i(TAG, "ON CLICK: SAVE");
        try {
            // Validate the current newsletter form
            ContentValues values = new ContentValues();
            boolean isSubscribed = false;
            for (NewsletterOption option : mNewsletterOptions) {
                if(option.isSubscrided) { values.put(option.name, option.value); isSubscribed = true; }
                else values.put(option.name, UNSUBSCRIBE_VALUE);
            }
            // Trigger
            Log.d(TAG, "VALUES: " + values.toString());
            triggerSubscribeNewsletters(values);
            // Tracking subscritption
            TrackerDelegator.trackNewsletterSubscription(isSubscribed);
        } catch (NullPointerException e) {
            Log.w(TAG, "NPE ON SUBSCRIBE NEWSLETTERS", e);
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
     * @author sergiopereira
     */
    private void triggerSubscribeNewsletters(ContentValues values) {
        Log.i(TAG, "TRIGGER: SUBSCRIBE");
        Bundle bundle = new Bundle();
        bundle.putParcelable(SubscribeNewslettersHelper.FORM_CONTENT_VALUES, values);
        triggerContentEvent(new SubscribeNewslettersHelper(), bundle, (IResponseCallback) this);
    }
    
    /**
     * Trigger to get the newsletters form
     * @author sergiopereira
     */
    private void triggerGetNewslettersForm(){
        Log.i(TAG, "TRIGGER: GET NEWSLETTER FORM");
        if(null != JumiaApplication.CUSTOMER){
            showFragmentLoading();
            triggerContentEvent(new GetNewslettersFormHelper(), null, (IResponseCallback) this);    
        } else {
            showFragmentRetry(this);
        }
        
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
            // Clean options
            mNewsletterOptions = null;
            // Show the form
            showNewslettersForm();
            break;
        case SUBSCRIBE_NEWSLETTERS_EVENT:
            Log.d(TAG, "RECEIVED SUBSCRIBE_NEWSLETTERS_EVENT");
            // Show toast
            Toast.makeText(getBaseActivity(), getString(R.string.newsletter_saved_message), Toast.LENGTH_LONG).show();
            // Goto back
            getBaseActivity().onBackPressed();
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
        
        showFragmentContentContainer();
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case GET_NEWSLETTERS_FORM_EVENT:
            Log.d(TAG, "RECEIVED GET_NEWSLETTERS_FORM_EVENT");
            goBackWarningUser();
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
     * Go to back and warning user through toast
     * @author sergiopereira
     */
    private void goBackWarningUser(){
        getBaseActivity().onBackPressed();
        Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
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

    /*
     * (non-Javadoc)
     * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = Integer.parseInt((String)buttonView.getTag());
        mNewsletterOptions.get(position).isSubscrided = isChecked;
    }
    


//    /**
//     * ########### ADAPTER ###########  
//     */
//    private class NewsletterAdapter extends ArrayAdapter<NewsletterOption> implements OnCheckedChangeListener {
//
//        /**
//         * 
//         * @author sergiopereira
//         * @param context
//         * @param options
//         */
//        public NewsletterAdapter(Context context, ArrayList<NewsletterOption> options ) {
//            super(context, R.layout.simple_email_notification_option, options);
//        }
//
//        /*
//         * (non-Javadoc)
//         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
//         */
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = convertView;
//
//            if(view == null){
//                view = LayoutInflater.from(getContext()).inflate(R.layout.simple_email_notification_option, parent, false);
//            } 
//            
//            CheckBox check = (CheckBox) view.findViewById(R.id.myaccount_newsletter_checkbox);
//            check.setText(getItem(position).toString());
//            Log.i(TAG, "code1news :  "+getItem(position)+" : "+getItem(position).isSubscrided);
//            
//            check.setChecked(getItem(position).isSubscrided);            
//            check.setTag("" + position);
//            check.setOnCheckedChangeListener(this);
//            return view;
//        }
//        
//        /*
//         * (non-Javadoc)
//         * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
//         */
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            int position = Integer.parseInt((String)buttonView.getTag());
//            getItem(position).isSubscrided = isChecked;
//        }
//    }
    
    /**
     * ########### DIALOGS ###########  
     */    
    
}
