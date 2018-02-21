package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;

import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.SubmitFormHelper;
import com.mobile.helpers.account.GetNewslettersFormHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.service.forms.Form;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.UIUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;

/**
 * Class used to show the newsletter form
 * 
 * @author sergiopereira
 */
public class MyAccountNewslettersFragment extends BaseFragment implements IResponseCallback, CompoundButton.OnCheckedChangeListener {

    private ScrollView mNewsletterScroll;
    private Form mNewslettersForm;
    private DynamicForm mDynamicForm;
    private View mButton;
    private Bundle mSavedState;

    /**
     * Empty constructor
     * 
     * @author sergiopereira
     */
    public MyAccountNewslettersFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT_EMAIL_NOTIFICATION,
                R.layout.my_account_email_notification_fragment,
                R.string.newsletter_label,
                NO_ADJUST_CONTENT);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i("ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i("ON CREATE");
        // Validate the saved state
        if (savedInstanceState != null) {
            mSavedState = savedInstanceState;
        }
        hideActivityProgress();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i("ON VIEW CREATED");
        // Get container
        mNewsletterScroll = (ScrollView) view.findViewById(R.id.email_notifications_scroll);
        // Get save button
        mButton = view.findViewById(R.id.email_notifications_save);
        mButton.setOnClickListener(this);
        // Validate the current sate
        onValidateState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i("ON RESUME");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i("ON SAVE INSTANCE STATE: NEWSLETTER FORM");
        if (mDynamicForm != null) {
            mDynamicForm.saveFormState(outState);
        }
    }

    /**
     * Validate the current state
     */
    public void onValidateState() {
        Print.i("ON VALIDATE STATE");
        // Case first time
        if (mNewslettersForm == null) {
            triggerGetNewslettersForm();
        }
        // Case rotate restore the form
        else if(mNewsletterScroll.getChildCount() == 0){
            showDynamicForm(mNewslettersForm);
        }
        // Default, show current form
        else {
            showFragmentContentContainer();
        }
    }

    /**
     * Show dynamic form.
     * TODO: The FormFactory should use the builder pattern to set extras listener when the form item is being created.
     */
    private void showDynamicForm(Form form) {
        // Create form
        mDynamicForm = FormFactory.create(FormConstants.NEWSLETTER_PREFERENCES_FORM, getContext(), form)
                .addRequestCallBack(this)               // Used to intercept generic messages
                .addCheckedChangeListener(this)         // Used to intercept the un subscribe value
                .addParentFragment(this)                // Used to know the current view state
                .addParentActivity(getBaseActivity());  // Used to show inner dialog fragment
        // Set button TODO: Temporary hack until FormFactory with builder pattern
        DynamicFormItem formItem = mDynamicForm.getItemByKey(RestConstants.UNSUBSCRIBE_SECTION);
        UIUtils.setVisibility(mButton, formItem!= null && formItem.getEntry().isChecked());
        // Load saved state
        mDynamicForm.loadSaveFormState(mSavedState);
        // Add form
        mNewsletterScroll.addView(mDynamicForm.getContainer());
        // Show form
        showFragmentContentContainer();
    }


    /*
     * ############# CLICK LISTENER #############
     */

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // Get the check value from "screen_radio"
        UIUtils.setVisibility(mButton, isChecked);
    }

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
        if (id == R.id.email_notifications_save) onClickSaveButton();
        // Unknown view
        else Print.i("ON CLICK: UNKNOWN VIEW");
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onValidateState();
    }

    /**
     * Process the click on the save button
     * 
     * @author sergiopereira
     */
    private void onClickSaveButton() {
        Print.i("ON CLICK: SAVE");
        // Validate and save form
        if (mDynamicForm.validate()) {
            triggerSubmitForm(mDynamicForm.getForm().getAction(), mDynamicForm.save());
        }
    }

    /*
     * ############# REQUESTS #############
     */

    private void triggerGetNewslettersForm() {
        Print.i("TRIGGER: GET NEWSLETTER FORM");
        triggerContentEvent(new GetNewslettersFormHelper(), null, this);
    }

    private void triggerSubmitForm(String action, ContentValues values) {
        Print.i("TRIGGER: GET NEWSLETTER FORM");
        triggerContentEvent(new SubmitFormHelper(), SubmitFormHelper.createBundle(action, values), this);
    }

    /*
     * ############# RESPONSE #############
     */

    /**
     * Filter the success response
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i("ON SUCCESS EVENT");
        // Get type
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w("RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Super
        super.handleSuccessEvent(baseResponse);
        // Validate type
        Print.i("ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
            case GET_NEWSLETTER_PREFERENCES_FORM_EVENT:
                // Save the form
                mNewslettersForm = (Form) baseResponse.getContentData();
                //  Show
                showDynamicForm(mNewslettersForm);
                break;
            case SUBMIT_FORM:
                // Goto back
                getBaseActivity().onBackPressed();
                break;
            default:
                break;
        }
    }

    /**
     * Filter the error response
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i("ON ERROR EVENT");
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w("RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Generic error
        if (super.handleErrorEvent(baseResponse)) {
            Print.d("BASE FRAGMENT HANDLE ERROR EVENT");
            return;
        }
        // Show container
        showFragmentContentContainer();
        // Validate type
        Print.i("ON ERROR EVENT: " + eventType);
        switch (eventType) {
        case GET_NEWSLETTER_PREFERENCES_FORM_EVENT:
            goBackWarningUser();
            break;
        case SUBMIT_FORM:
            getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_please_try_again));
            break;
        default:
            break;
        }
    }

    /**
     * Go to back and warning user through toast
     * 
     * @author sergiopereira
     */
    private void goBackWarningUser() {
        getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.error_please_try_again));
        getBaseActivity().onBackPressed();
    }

}
