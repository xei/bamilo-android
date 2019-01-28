package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.factories.FormFactory;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.GetRegisterFormHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.RegisterHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.forms.FormInputType;
import com.bamilo.android.framework.service.objects.configs.AuthInfo;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.LoginHeaderComponent;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;

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
        mGABeginRequestMillis = System.currentTimeMillis();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // Validate the current state
        onValidateState();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_FAILED,
                    Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                    MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
//            TrackerManager.trackEvent(getContext(), EventConstants.Signup, authEventModel);
            String userId = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString() : "UNKNOWN";
            String emailAddress = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : "UNKNOWN";
            String phoneNumber = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getPhoneNumber() : "UNKNOWN";
            EventTracker.INSTANCE.signUp(
                    userId,
                    emailAddress,
                    phoneNumber,
                    TrackingEvents.SignUpMethod.REGISTER_WITH_EMAIL
            );
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
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case REGISTER_ACCOUNT_EVENT:
                // Notify user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                // Finish
                getActivity().onBackPressed();

                // Tracking
                long customerId = SimpleEventModel.NO_VALUE;
                String customerEmail = "";
                if (BamiloApplication.CUSTOMER != null) {
                    customerId = BamiloApplication.CUSTOMER.getId();
                    customerEmail = BamiloApplication.CUSTOMER.getEmail();
                }
                MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_SUCCESS,
                        Constants.LOGIN_METHOD_EMAIL, customerId,
                        MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, customerEmail != null ? EmailHelper.getHost(customerEmail) : "",
                                true));

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
            return;
        }
        // Super
        if (super.handleErrorEvent(baseResponse)) return;
        // Validate
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case GET_REGISTRATION_FORM_EVENT:
                showUnexpectedErrorWarning();
                break;
            case REGISTER_ACCOUNT_EVENT:
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