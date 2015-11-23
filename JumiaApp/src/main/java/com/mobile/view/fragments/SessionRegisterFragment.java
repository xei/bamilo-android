package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.helpers.session.GetRegisterFormHelper;
import com.mobile.helpers.session.RegisterHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Class used to manage the register form
 *
 * @author sergiopereira
 */
public class SessionRegisterFragment extends BaseFragment implements IResponseCallback, OnClickListener {

    private static final String TAG = SessionRegisterFragment.class.getSimpleName();

    private Form mForm;

    private Bundle mFormSavedState;

    private DynamicForm mDynamicForm;

    private ViewGroup mFormContainer;

    private boolean isSubscribingNewsletter;

    private String mCustomerEmail;

    private boolean isInCheckoutProcess;

    private FragmentType mParentFragmentType;

    /**
     * Get new instance of register fragment
     */
    public static SessionRegisterFragment getInstance(Bundle bundle) {
        SessionRegisterFragment fragment = new SessionRegisterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty Constructor
     */
    public SessionRegisterFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.session_register_fragment_main,
                R.string.register_title,
                ADJUST_CONTENT);
    }

    /*
     * ###### LIFE CYCLE ######
     */

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Saved form state
        mFormSavedState = savedInstanceState;
        // Get arguments
        Bundle arguments = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            mParentFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE);
            // Get customer email
            mCustomerEmail = arguments.getString(ConstantsIntentExtra.DATA);
            // Get checkout flag
            isInCheckoutProcess = arguments.getBoolean(ConstantsIntentExtra.FLAG_1);
        }
        // Show checkout tab layout
        if (isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT) {
            checkoutStep = ConstantsCheckout.CHECKOUT_ABOUT_YOU;
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON CREATE VIEW");
        // Get info
        String text = String.format(getString(R.string.register_info), getString(R.string.app_name));
        ((TextView) view.findViewById(R.id.register_text_info)).setText(text);
        // Get form container
        mFormContainer = (ViewGroup) view.findViewById(R.id.register_form_container);
        // Get create button
        View mCreateButton = view.findViewById(R.id.register_button_create);
        mCreateButton.setOnClickListener(this);
        // Validate the current state
        onValidateState();
    }

    private void onValidateState() {
        // Case form is empty
        if (mForm == null) {
            triggerRegisterForm();
        }
        // Case load form
        else {
            loadForm(mForm);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        TrackerDelegator.trackPage(TrackingPage.REGISTRATION, getLoadTime(), false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "ON SAVE INSTANCE STATE");
        super.onSaveInstanceState(outState);
        // Case rotation save state
        if (mDynamicForm != null) {
            mDynamicForm.saveFormState(outState);
        }
        // Save data
        outState.putString(ConstantsIntentExtra.DATA, mCustomerEmail);
        // Save checkout flag
        outState.putBoolean(ConstantsIntentExtra.FLAG_1, isInCheckoutProcess);
        outState.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, mParentFragmentType);
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        // Case goes to back stack save the state
        if(mDynamicForm != null) {
            Bundle bundle = new Bundle();
            mDynamicForm.saveFormState(bundle);
            mFormSavedState = bundle;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
        mForm = null;
        mDynamicForm = null;
    }

    /**
     *
     */
    private void loadForm(Form form) {
        // Create form view
        mDynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.REGISTRATION_FORM, getActivity(), form);
        // Set request callback
        mDynamicForm.setRequestCallBack(this); // Form FormInputType.relatedNumber
        // Set click listener
        mDynamicForm.setOnClickListener(this); // From FormInputType.checkBoxLink
        // Load saved state
        mDynamicForm.loadSaveFormState(mFormSavedState);
        // Set initial value
        if (TextUtils.isNotEmpty(mCustomerEmail)) {
            mDynamicForm.setInitialValue(FormInputType.email, mCustomerEmail);
        }
        // Add form view
        mFormContainer.addView(mDynamicForm.getContainer());
        // Show
        showFragmentContentContainer();
    }

    /**
     * Request a register
     */
    void requestRegister() {
        // Get values
        ContentValues values = mDynamicForm.save();
        // Get value from newsletter
        isSubscribingNewsletter = false;
        if(values.containsKey(RestConstants.JSON_NEWSLETTER_CATEGORIES_SUBSCRIBED_TAG)) {
            isSubscribingNewsletter = values.getAsBoolean(RestConstants.JSON_NEWSLETTER_CATEGORIES_SUBSCRIBED_TAG);
        }
        // Register user
        triggerRegister(values);
    }

    /*
     * ######## LISTENERS ########
     */

    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case create button
        if (id == R.id.register_button_create) {
            onClickCreate();
        }
        // Case terms and conditions from form
        else if (id == R.id.textview_terms) {
            onClickTermsAndConditions();
        }
        // Case default
        else {
            super.onClick(view);
        }
    }

    private void onClickTermsAndConditions() {
        Bundle bundle = new Bundle();
        bundle.putString(RestConstants.JSON_KEY_TAG, GetStaticPageHelper.TERMS_PAGE);
        bundle.putString(RestConstants.TITLE, getString(R.string.terms_and_conditions));
        getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }


    private void onClickCreate() {
        // Hide keyboard
        getBaseActivity().hideKeyboard();
        // Case valid
        if (mDynamicForm.checkPasswords() && mDynamicForm.validate()) {
            requestRegister();
        }
        // Case invalid
        else {
            // Tracking
            TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onValidateState();
    }

    /*
     * ############# TRIGGERS #############
     */

    private void triggerRegister(ContentValues values) {
        triggerContentEvent(new RegisterHelper(), RegisterHelper.createBundle(values), this);
    }

    private void triggerRegisterForm() {
        triggerContentEvent(new GetRegisterFormHelper(), null, this);
    }

    /*
     * ############# CALLBACKS #############
     */

    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate event
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "ON SUCCESS: " + eventType);
        switch (eventType) {
            case REGISTER_ACCOUNT_EVENT:
                // Get Register Completed Event
                //Customer customer = (Customer) baseResponse.getMetadata().getData();
                // Tracking
                if(isSubscribingNewsletter) TrackerDelegator.trackNewsletterGTM("", GTMValues.REGISTER);
                TrackerDelegator.trackSignupSuccessful(GTMValues.REGISTER);

                // Finish
                getActivity().onBackPressed();

                // Notify user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                break;
            case GET_REGISTRATION_FORM_EVENT:
                mForm = (Form) baseResponse.getMetadata().getData();
                loadForm(mForm);
                break;
            default:
                break;
        }
    }


    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "ON ERROR: " + eventType);
        switch (eventType) {
            case GET_REGISTRATION_FORM_EVENT:
                showUnexpectedErrorWarning();
                break;
            case REGISTER_ACCOUNT_EVENT:
                // Tracking
                TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
                // Validate errors
                int code = baseResponse.getError().getCode();
                Map<String, List<String>> messages = baseResponse.getErrorMessages();
                validateErrorMessage(code, messages);
                break;
            default:
                break;
        }
    }

    private void validateErrorMessage(int errorCode, Map<String, List<String>> errorMessages) {
        // Validate error
        if (errorCode == ErrorCode.REQUEST_ERROR) {
            List<String> validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
            // Validate error message
            int message =  R.string.incomplete_alert;
            if (validateMessages != null && validateMessages.contains(Errors.CODE_REGISTER_CUSTOMEREXISTS)) {
                message = R.string.error_register_alreadyexists;
            }
            // Show container
            showFragmentContentContainer();
            // Show dialog
            dialog = DialogGenericFragment.newInstance(true, false,
                    getString(R.string.error_register_title),
                    getString(message),
                    getString(R.string.ok_label),
                    "",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                            }
                        }
                    });
            dialog.show(getActivity().getSupportFragmentManager(), null);
        } else {
            showUnexpectedErrorWarning();
        }
    }

}