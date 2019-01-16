package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.LogOut;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.EmailCheckHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.LoginHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.utils.CheckoutStepManager;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepLogin;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.objects.customer.CustomerEmailCheck;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.crashlytics.android.Crashlytics;

import java.util.EnumSet;

public class LoginFragment extends NewBaseFragment implements IResponseCallback {
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextInputLayout tilEmail, tilPassword;
    private String mCustomerEmail;
    private String mCustomerPassword;
    private long mGABeginRequestMillis;
    private static final String TAG = LoginFragment.class.getSimpleName();

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;


/*    public LoginFragment()
    {

    }*/

    public LoginFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.fragment_login_user,
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TrackerDelegator.trackPage(TrackingPage.USER_LOGIN, getLoadTime(), false);

        // Get arguments
        Bundle arguments = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            // Force load form if comes from deep link
            mParentFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE);
            mNextStepFromParent = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
            isInCheckoutProcess = arguments.getBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API);
        }
        // Show checkout tab layout
        if (isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT) {
            checkoutStep = ConstantsCheckout.CHECKOUT_ABOUT_YOU;
        }

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.USER_LOGIN.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);


    }


/*    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_session_login_main_fragment, container, false);
        return view;
    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(view);

        mEmailView = (EditText) view.findViewById(R.id.login_email);
        mPasswordView = (EditText) view.findViewById(R.id.login_password);
        mEmailView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        // Get messageItem message
        tilEmail = (TextInputLayout) view.findViewById(R.id.tilEmail);
        tilPassword = (TextInputLayout) view.findViewById(R.id.tilPassword);

        // Get continue button
        view.findViewById(R.id.login_button_continue).setOnClickListener(this);
        view.findViewById(R.id.login_email_button_password).setOnClickListener(this);

        CheckBox cbShowHiderPassword = (CheckBox) view.findViewById(R.id.cbShowHidePassword);
        cbShowHiderPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    mPasswordView.setTransformationMethod(new SingleLineTransformationMethod());
                } else {
                    mPasswordView.setTransformationMethod(new PasswordTransformationMethod());
                }
                // TODO: 8/28/18 farshid
//                HoloFontLoader.applyDefaultFont(mPasswordView);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("mEmailView", mEmailView.getText().toString());
        outState.putString("mPasswordView", mPasswordView.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mEmailView.setText(savedInstanceState.getString("mEmailView"));
            mPasswordView.setText(savedInstanceState.getString("mPasswordView"));
        }
    }

    protected void onClickCheckEmail() {
        mGABeginRequestMillis = System.currentTimeMillis();

        // Get email
        mCustomerEmail = mEmailView.getText().toString();
        mCustomerPassword = mPasswordView.getText().toString();
        tilEmail.setError(null);
        tilPassword.setError(null);
        // Trigger to check email
        if (TextUtils.isNotEmpty(mCustomerEmail) && TextUtils.isNotEmpty(mCustomerPassword) && Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
            triggerEmailCheck(mCustomerEmail);
            tilEmail.setError(null);
            tilPassword.setError(null);
        /*    mEmailView.setError("");
            mPasswordView.setError("");*/

            //mErrorMessage.setVisibility(View.GONE);
        } else {

            if (!TextUtils.isNotEmpty(mCustomerEmail)) {
                tilEmail.setError(getString(R.string.error_ismandatory));
                // TODO: 8/28/18 farshid
//                HoloFontLoader.applyDefaultFont(tilEmail);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
                //mEmailView.setError(getString(R.string.error_invalid_email));
                tilEmail.setError(getString(R.string.error_invalid_email));
                // TODO: 8/28/18 farshid
//                HoloFontLoader.applyDefaultFont(tilEmail);
            }
            if (!TextUtils.isNotEmpty(mCustomerPassword)) {
                // mPasswordView.setError(getString(R.string.error_ismandatory));
                tilPassword.setError(getString(R.string.error_ismandatory));
                // TODO: 8/28/18 farshid
//                HoloFontLoader.applyDefaultFont(tilPassword);
            }

            //mErrorMessage.setText(getString(R.string.error_invalid_email));
            //mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void triggerEmailCheck(String email) {
        triggerContentEventProgress(new EmailCheckHelper(), EmailCheckHelper.createBundle(email), this);
    }

    @Override
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
            case EMAIL_CHECK:
                //DROID-10
//                TrackerDelegator.trackScreenLoadTiming(R.string.gaLogin, mGABeginRequestMillis, mCustomerEmail);
                tilEmail.setError(null);
                // Get value
                boolean exist = ((CustomerEmailCheck) baseResponse.getMetadata().getData()).exist();
                if (exist) {
                    ContentValues values = new ContentValues();
                    values.put("login[email]", mCustomerEmail);
                    values.put("login[password]", mCustomerPassword);
                    triggerContentEventProgress(new LoginHelper(getContext()), LoginHelper.createLoginBundle(values), this);

                } else {
                    hideActivityProgress();

                    getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.email_password_invalid));

                }

                break;
            case AUTO_LOGIN_EVENT:
                // Get Customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                FragmentType nextStepFromApi = nextStepStruct.getFragmentType();

                // Case valid next step
                if (nextStepFromApi != FragmentType.UNKNOWN) {
                    Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                    // Tracking
                    String userId = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString() : null;
                    String emailAddress = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null;
                    String phoneNumber = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getPhoneNumber() : null;
                    EventTracker.INSTANCE.login(userId, emailAddress, phoneNumber, TrackingEvents.LoginMethod.LOGIN_WITH_EMAIL);

                    TrackerDelegator.trackLoginSuccessful(customer, true, false);

                    // Global Tracker
//                    MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_SUCCESS,
//                            Constants.LOGIN_METHOD_EMAIL, customer.getId(),
//                            MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, EmailHelper.getHost(customer.getEmail()),
//                                    true));
//                    TrackerManager.trackEvent(getContext(), EventConstants.Login, authEventModel);
//                    EmarsysTracker.getInstance().trackEventAppLogin(
//                            Integer.parseInt(getContext().getResources().getString(R.string.Emarsys_ContactFieldID)),
//                                    BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null);

                    // Validate the next step
                    CheckoutStepManager.validateLoggedNextStep(getBaseActivity(), isInCheckoutProcess, mParentFragmentType, mNextStepFromParent, nextStepFromApi, getArguments());
                }
                // Case unknown checkout step
                else {
                    hideActivityProgress();
                    // Show layout to call to order
                    showFragmentUnknownCheckoutStepError();
                }
                getBaseActivity().setupDrawerNavigation();

                break;
            case LOGIN_EVENT:
                hideActivityProgress();
                // Get customer
                nextStepStruct = (NextStepStruct) baseResponse.getContentData();
                Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                // Set hide change password
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                // Tracking
                TrackerDelegator.trackLoginSuccessful(customer, false, false);

               /* RecommendManager recommendManager = new RecommendManager();
                recommendManager.setEmail(BamiloApplication.CUSTOMER.getEmail(), ""+BamiloApplication.CUSTOMER.getId());*/

                // Finish
                getActivity().onBackPressed();

                // TODO: 8/28/18 farshid
                // Pushwoosh.getInstance().setUserId(getBaseActivity(), BamiloApplication.CUSTOMER.getEmail() + "");

                Crashlytics.setUserEmail(BamiloApplication.CUSTOMER.getEmail());

                // Global Tracker
                MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.LOGIN_SUCCESS,
                        Constants.LOGIN_METHOD_EMAIL, customer.getId(),
                        MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, EmailHelper.getHost(customer.getEmail()),
                                true));
//                TrackerManager.trackEvent(getBaseActivity(), EventConstants.Login, authEventModel);

//                EmarsysTracker.getInstance().trackEventAppLogin(Integer.parseInt(getContext().getResources().getString(R.string.Emarsys_ContactFieldID)),
//                        BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : null);

                String userId = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getIdAsString() : "UNKNOWN";
                String emailAddress = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getEmail() : "UNKNOWN";
                String phoneNumber = BamiloApplication.CUSTOMER != null ? BamiloApplication.CUSTOMER.getPhoneNumber() : "UNKNOWN";
                EventTracker.INSTANCE.login(userId, emailAddress, phoneNumber, TrackingEvents.LoginMethod.LOGIN_WITH_EMAIL);

                if (isInCheckoutProcess) {
                    getBaseActivity().onSwitchFragment(FragmentType.CHECKOUT_MY_ADDRESSES, null, FragmentController.ADD_TO_BACK_STACK);
                }
                getBaseActivity().setupDrawerNavigation();

                return;
           /* case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                // Tracking
                TrackerDelegator.trackSignupSuccessful(GTMValues.REGISTER);
                // Notify user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                // Finish
                getActivity().onBackPressed();
                // Set facebook login
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                break;*/
            default:
                hideActivityProgress();
                break;
        }

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Validate messageItem o super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {
            case EMAIL_CHECK:
                // Show warning
                tilEmail.setError(getString(R.string.error_invalid_email));
                // TODO: 8/28/18 farshid
//                HoloFontLoader.applyDefaultFont(tilEmail);
                //showWarningErrorMessage(getString(R.string.error_invalid_email));
                // Show content
                showFragmentContentContainer();
                break;
            case AUTO_LOGIN_EVENT:
                // Logout
                LogOut.perform(getWeakBaseActivity(), null);
            /*case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                // Tracking
                TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
                // Validate and show errors
                showFragmentContentContainer();
                // Show validate messages
                showValidateMessages(baseResponse);
                break;*/
            case LOGIN_EVENT:
                hideActivityProgress();
                getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.email_password_invalid));
//                TrackerManager.trackEvent(getBaseActivity(), EmarsysEventConstants.Login, EmarsysEventFactory.login("email", EmarsysEventConstants.UNKNOWN_EVENT_VALUE, false));
                break;
            default:
                break;
        }
    }

    private void onClickForgotPassword() {
        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        // Case forgot password
        if (id == R.id.login_button_continue) {
            onClickCheckEmail();
        } else if (id == R.id.login_email_button_password) {
            onClickForgotPassword();
        }
        /*else if (id == R.id.register_button_create)
        {
            onClickCreate();
        }*/
        // Case super
        else {
            super.onClick(view);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }
}
