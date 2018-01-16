package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.SimpleEventModel;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.constants.tracking.CategoryConstants;
import com.mobile.constants.tracking.EventActionKeys;
import com.mobile.constants.tracking.EventConstants;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.session.GetRegisterFormHelper;
import com.mobile.helpers.session.RegisterHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.pojo.DynamicForm;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.service.forms.Form;
import com.mobile.service.forms.FormInputType;
import com.mobile.service.objects.configs.AuthInfo;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.CustomerUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.TextUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.LoginHeaderComponent;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.deeplink.TargetLink;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;

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
    //DROID-10
    private long mGABeginRequestMillis;
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
        mGABeginRequestMillis = System.currentTimeMillis();
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
        final LoginHeaderComponent loginHeaderComponent = (LoginHeaderComponent) view.findViewById(R.id.login_component);
        loginHeaderComponent.setSubTitle(text);

        AuthInfo authInfo = CountryPersistentConfigs.getAuthInfo(getContext());
        loginHeaderComponent.showAuthInfo(LoginHeaderComponent.CREATE_ACCOUNT, authInfo, null);

        // Get form container
        mFormContainer = (ViewGroup) view.findViewById(R.id.register_form_container);
        // Get create button
        View mCreateButton = view.findViewById(R.id.register_button_create);
        mCreateButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
        // Validate the current state
        onValidateState();
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
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
     * Validate form
     */
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

    /**
     * Create dynamic form
     */
    private void loadForm(Form form) {
        // Create form view
        mDynamicForm = FormFactory.create(FormConstants.REGISTRATION_FORM, getActivity(), form)
                .addOnClickListener(this) // From FormInputType.checkBoxLink
                .addRequestCallBack(this); // Form FormInputType.relatedNumber
        // Load saved state
        mDynamicForm.loadSaveFormState(mFormSavedState);
        // Set initial value
        if (TextUtils.isNotEmpty(mCustomerEmail)) {
            mDynamicForm.setInitialValue(FormInputType.email, mCustomerEmail);
        }
        // Add form view
        mFormContainer.removeAllViews();
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
        if(values.containsKey(RestConstants.REGISTER_NEWSLETTER_CATEGORIES_SUBSCRIBED)) {
            isSubscribingNewsletter = values.getAsBoolean(RestConstants.REGISTER_NEWSLETTER_CATEGORIES_SUBSCRIBED);
        }
        // Register user
        triggerRegister(mDynamicForm.getForm().getAction(), values);
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
            onClickTermsAndConditions(view);
        }
        // Case default
        else {
            super.onClick(view);
        }
    }

    private void onClickTermsAndConditions(View view) {
        @TargetLink.Type String target = (String) view.getTag();
        new TargetLink(getWeakBaseActivity(), target)
                .addTitle(R.string.terms_and_conditions)
                .enableWarningErrorMessage()
                .run();
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
            SimpleEventModel sem = new SimpleEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_FAILED,
                    Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE);
            TrackerManager.trackEvent(getContext(), EventConstants.Signup, sem);
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

    private void triggerRegister(String endpoint, ContentValues values) {
        triggerContentEvent(new RegisterHelper(), RegisterHelper.createBundle(endpoint, values), this);
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
                // Tracking
                SimpleEventModel sem = new SimpleEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_SUCCESS,
                        Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE);
                if (BamiloApplication.CUSTOMER != null) {
                    sem.value = BamiloApplication.CUSTOMER.getId();
                }
                TrackerManager.trackEvent(getContext(), EventConstants.Signup, sem);

                // Notify user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                // Finish
                getActivity().onBackPressed();
                // Set facebook login
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                break;
            case GET_REGISTRATION_FORM_EVENT:
                mForm = (Form) baseResponse.getContentData();
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
        if (super.handleErrorEvent(baseResponse)) return;
        // Validate
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "ON ERROR: " + eventType);
        switch (eventType) {
            case GET_REGISTRATION_FORM_EVENT:
                showUnexpectedErrorWarning();
                break;
            case REGISTER_ACCOUNT_EVENT:
                // Tracking
                SimpleEventModel sem = new SimpleEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_FAILED,
                        Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE);
                TrackerManager.trackEvent(getContext(), EventConstants.Signup, sem);
                // Validate and show errors
                showFragmentContentContainer();
                // Show validate messages
                showFormValidateMessages(mDynamicForm, baseResponse, eventType);
                break;
            default:
                break;
        }
    }

}