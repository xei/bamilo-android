/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.output.Print;
import com.mobile.framework.tracking.TrackingPage;
import com.mobile.framework.tracking.gtm.GTMValues;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.account.GetNewslettersFormHelper;
import com.mobile.helpers.account.SubscribeNewslettersHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.NewsletterOption;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Class used to show the newsletter form
 * 
 * @author sergiopereira
 */
public class MyAccountEmailNotificationFragment extends BaseFragment implements IResponseCallback, OnCheckedChangeListener {

    private static final String TAG = LogTagHelper.create(MyAccountEmailNotificationFragment.class);

    private final static int UNSUBSCRIBE_VALUE = -1;

    private Form mNewslettersForm;

    private LinearLayout mNewsletterList;

    private ArrayList<NewsletterOption> mNewsletterOptions;

    private LayoutInflater mInflater;

    private ArrayList<NewsletterOption> mNewsletterOptionsSaved;

    /**
     * Create new instance
     * 
     * @return MyAccountEmailNotificationFragment
     * @author sergiopereira
     */
    public static MyAccountEmailNotificationFragment newInstance() {
        return new MyAccountEmailNotificationFragment();
    }

    /**
     * Empty constructor
     * 
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
        // Get inflater
        mInflater = LayoutInflater.from(getBaseActivity());
        // Validate the saved state
        if (savedInstanceState != null && savedInstanceState.containsKey(TAG)) {
            Print.i(TAG, "ON GET SAVED STATE");
            mNewsletterOptionsSaved = savedInstanceState.getParcelableArrayList(TAG);
        }
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
        // Get list view
        mNewsletterList = (LinearLayout) view.findViewById(R.id.myaccount_newsletter_list);
        // Get save button
        view.findViewById(R.id.myaccount_newsletter_save).setOnClickListener(this);
        // Get cancel button
        view.findViewById(R.id.myaccount_newsletter_cancel).setOnClickListener(this);
        // Validate data
        if (mNewslettersForm == null) triggerGetNewslettersForm();
         else showNewslettersForm();
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
        if (mNewslettersForm != null) {
            showFragmentContentContainer();
        }
        // Tracking page
        TrackerDelegator.trackPage(TrackingPage.NEWSLETTER_SUBS, getLoadTime(), false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE: NEWSLETTER FORM");
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
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
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

    /**
     * Show the newsletter form
     * 
     * @author sergiopereira
     */
    private void showNewslettersForm() {
        try {
            FormField formField = mNewslettersForm.fields.get(0);
            if (mNewsletterOptionsSaved != null)
                mNewsletterOptions = mNewsletterOptionsSaved;
            else
                mNewsletterOptions = formField.newsletterOptions;
            generateNewsletterOptions(mNewsletterOptions, mNewsletterList);
            // Show form
            showFragmentContentContainer();
        } catch (IndexOutOfBoundsException e) {
            Print.w(TAG, "IBE ON SHOW NEWSLETTER FORM", e);
            goBackWarningUser();
        } catch (NullPointerException e) {
            Print.w(TAG, "NPE ON SHOW NEWSLETTER FORM", e);
            goBackWarningUser();
        }
    }

    /**
     * Gemerate the newsletter option and add it to container
     *
     * @author msilva
     * @param newsletterOptions
     * @param newsletterList
     */
    private void generateNewsletterOptions(ArrayList<NewsletterOption> newsletterOptions,
            LinearLayout newsletterList) {
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
     * 
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Next button
        if (id == R.id.myaccount_newsletter_save) onClickSaveButton();
        // Cancel button
        else if (id == R.id.myaccount_newsletter_cancel) onClickCancelButton();
        // Unknown view
        else Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onClickRetryButton();
    }
    
    /**
     * Process the click on retry button.
     * 
     * @author paulo
     */
    private void onClickRetryButton() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.EMAIL_NOTIFICATION);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on the save button
     * 
     * @author sergiopereira
     */
    private void onClickSaveButton() {
        Print.i(TAG, "ON CLICK: SAVE");
        try {
            // Validate the current newsletter form
            ContentValues values = new ContentValues();
            boolean isSubscribed = false;
            for (NewsletterOption option : mNewsletterOptions) {
                if (option.isSubscrided) {
                    values.put(option.name, option.value);
                    isSubscribed = true;
                } else
                    values.put(option.name, UNSUBSCRIBE_VALUE);
            }
            // Trigger
            Print.d(TAG, "VALUES: " + values.toString());
            triggerSubscribeNewsletters(values);
            // Tracking subscritption
            TrackerDelegator.trackNewsletterSubscription(isSubscribed, GTMValues.MYACCOUNT);
        } catch (NullPointerException e) {
            Print.w(TAG, "NPE ON SUBSCRIBE NEWSLETTERS", e);
        }
    }

    /**
     * Process the click on the cancel button
     * 
     * @author sergiopereira
     */
    private void onClickCancelButton() {
        Print.i(TAG, "ON CLICK: CANCEL");
        getBaseActivity().onBackPressed();
    }

    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to subscribe newsletters
     * 
     * @param values
     * @author sergiopereira
     */
    private void triggerSubscribeNewsletters(ContentValues values) {
        Print.i(TAG, "TRIGGER: SUBSCRIBE");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new SubscribeNewslettersHelper(), bundle, this);
    }

    /**
     * Trigger to get the newsletters form
     * 
     * @author sergiopereira
     */
    private void triggerGetNewslettersForm() {
        Print.i(TAG, "TRIGGER: GET NEWSLETTER FORM");
        if (null != JumiaApplication.CUSTOMER) {
            showFragmentLoading();
            triggerContentEvent(new GetNewslettersFormHelper(), null, this);
        } else {
            showFragmentErrorRetry();
        }
    }

    /**
     * ############# RESPONSE #############
     */
    /**
     * Filter the success response
     * 
     * @param bundle
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        Print.i(TAG, "ON SUCCESS EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        switch (eventType) {
        case GET_NEWSLETTERS_FORM_EVENT:
            Print.d(TAG, "RECEIVED GET_NEWSLETTERS_FORM_EVENT");
            // Get the form
            // Save the form
            mNewslettersForm = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            // Clean options
            mNewsletterOptions = null;
            // Show the form
            showNewslettersForm();
            break;
        case SUBSCRIBE_NEWSLETTERS_EVENT:
            Print.d(TAG, "RECEIVED SUBSCRIBE_NEWSLETTERS_EVENT");
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
     * 
     * @param bundle
     * @return boolean
     */
    protected boolean onErrorEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        // Generic error
        if (super.handleErrorEvent(bundle)) {
            Print.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
            return true;
        }

        showFragmentContentContainer();

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        switch (eventType) {
        case GET_NEWSLETTERS_FORM_EVENT:
            Print.d(TAG, "RECEIVED GET_NEWSLETTERS_FORM_EVENT");
            goBackWarningUser();
            break;
        case SUBSCRIBE_NEWSLETTERS_EVENT:
            Print.d(TAG, "RECEIVED SUBSCRIBE_NEWSLETTERS_EVENT");
            Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
            break;
        default:
            break;
        }

        return false;
    }

    /**
     * Go to back and warning user through toast
     * 
     * @author sergiopereira
     */
    private void goBackWarningUser() {
        getBaseActivity().onBackPressed();
        Toast.makeText(getBaseActivity(), getString(R.string.error_please_try_again), Toast.LENGTH_LONG).show();
    }

    /*
     * ########### RESPONSE LISTENER ###########
     */
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle )
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged
     * (android.widget.CompoundButton, boolean)
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = Integer.parseInt((String) buttonView.getTag());
        mNewsletterOptions.get(position).isSubscrided = isChecked;
    }

}
