///**
// *
// */
//package com.mobile.view.fragments;
//
//import android.app.Activity;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//
//import com.mobile.app.JumiaApplication;
//import com.mobile.components.customfontviews.CheckBox;
//import com.mobile.components.customfontviews.EditText;
//import com.mobile.components.customfontviews.FacebookTextView;
//import com.mobile.constants.ConstantsCheckout;
//import com.mobile.constants.FormConstants;
//import com.mobile.controllers.fragments.FragmentController;
//import com.mobile.controllers.fragments.FragmentType;
//import com.mobile.factories.FormFactory;
//import com.mobile.helpers.NextStepStruct;
//import com.mobile.helpers.account.GetCustomerHelper;
//import com.mobile.helpers.cart.GetShoppingCartItemsHelper;
//import com.mobile.helpers.configs.GetInitFormHelper;
//import com.mobile.helpers.session.GetFacebookLoginHelper;
//import com.mobile.helpers.session.GetLoginFormHelper;
//import com.mobile.helpers.session.GetLoginHelper;
//import com.mobile.helpers.session.GetSignUpFormHelper;
//import com.mobile.helpers.session.SetSignUpHelper;
//import com.mobile.interfaces.IResponseCallback;
//import com.mobile.newFramework.ErrorCode;
//import com.mobile.newFramework.forms.Form;
//import com.mobile.newFramework.forms.FormInputType;
//import com.mobile.newFramework.objects.cart.PurchaseEntity;
//import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
//import com.mobile.newFramework.objects.customer.Customer;
//import com.mobile.newFramework.pojo.BaseResponse;
//import com.mobile.newFramework.pojo.RestConstants;
//import com.mobile.newFramework.tracking.TrackingEvent;
//import com.mobile.newFramework.tracking.gtm.GTMValues;
//import com.mobile.newFramework.utils.Constants;
//import com.mobile.newFramework.utils.CustomerUtils;
//import com.mobile.newFramework.utils.EventType;
//import com.mobile.newFramework.utils.output.Print;
//import com.mobile.pojo.DynamicForm;
//import com.mobile.pojo.DynamicFormItem;
//import com.mobile.preferences.CustomerPreferences;
//import com.mobile.utils.MyMenuItem;
//import com.mobile.utils.NavigationAction;
//import com.mobile.utils.Toast;
//import com.mobile.utils.TrackerDelegator;
//import com.mobile.utils.dialogfragments.DialogGenericFragment;
//import com.mobile.utils.social.FacebookHelper;
//import com.mobile.utils.ui.ErrorLayoutFactory;
//import com.mobile.view.R;
//
//import java.util.EnumSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
////import com.mobile.utils.social.FacebookHelper;
//
///**
// * Class used to perform the login or sign up
// *
// * @author sergiopereira
// */
//public class CheckoutAboutYouFragment extends BaseExternalLoginFragment implements IResponseCallback {
//
//    private static final String TAG = CheckoutAboutYouFragment.class.getSimpleName();
//
//    private Form formResponse = null;
//
//    private DynamicForm loginForm;
//
//    private Bundle savedInstanceState;
//
//    private View loginMainContainer;
//
//    private ViewGroup loginFormContainer;
//
//    private CheckBox rememberEmailCheck;
//
//    private View signupMainContainer;
//
//    private boolean onAutoLogin = false;
//
//    private ViewGroup signupFormContainer;
//
//    private DynamicForm signupForm;
//
//    private Form signupFormResponse;
//
//    private View loginToogle;
//
//    private View signupToogle;
//
//    private FragmentType mNextFragment;
//
//    private boolean cameFromSignUp = false;
//
//    private int retryForms = 0;
//
//    /**
//     * Get the instance of CheckoutAboutYouFragment
//     *
//     * @return {@link BaseFragment}
//     */
//    public static CheckoutAboutYouFragment getInstance() {
//        return new CheckoutAboutYouFragment();
//    }
//
//    /**
//     * Empty constructor
//     */
//    public CheckoutAboutYouFragment() {
//        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
//                NavigationAction.Checkout,
//                R.layout.checkout_about_you_main,
//                R.string.checkout_label,
//                KeyboardState.ADJUST_CONTENT,
//                ConstantsCheckout.CHECKOUT_ABOUT_YOU);
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
//     */
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Print.i(TAG, "ON ATTACH");
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Print.i(TAG, "ON CREATE");
//        // Retain the fragment
//        setRetainInstance(true);
//        TrackerDelegator.trackCheckoutStep(TrackingEvent.CHECKOUT_STEP_ABOUT_YOU);
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
//     */
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Print.i(TAG, "ON VIEW CREATED");
//        // Login toggle
//        loginToogle = view.findViewById(R.id.checkout_login_toogle);
//        loginToogle.setOnClickListener(this);
//        loginToogle.setSelected(true);
//        // Login main
//        loginMainContainer = view.findViewById(R.id.checkout_login_form_main);
//        // Login form
//        loginFormContainer = (ViewGroup) view.findViewById(R.id.checkout_login_form_container);
//        // Remember login checkbox
//        rememberEmailCheck = (CheckBox) view.findViewById(R.id.login_remember_user_email);
//        // Login button
//        view.findViewById(R.id.checkout_login_form_button_enter).setOnClickListener(this);
//        // Forget button
//        view.findViewById(R.id.checkout_login_form_button_password).setOnClickListener(this);
//        // Sign toggle
//        signupToogle = view.findViewById(R.id.checkout_signup_toogle);
//        signupToogle.setOnClickListener(this);
//        // Sign main
//        signupMainContainer = view.findViewById(R.id.checkout_signup_form_main);
//        // Sign form
//        signupFormContainer = (ViewGroup) view.findViewById(R.id.checkout_signup_form_container);
//        // Sign button
//        view.findViewById(R.id.checkout_signup_form_button_enter).setOnClickListener(this);
//        // FACEBOOK
//        FacebookTextView mLoginFacebookButton = (FacebookTextView) view.findViewById(R.id.checkout_login_form_button_facebook);
//        FacebookTextView mSignUpFacebookButton = (FacebookTextView) view.findViewById(R.id.checkout_signup_form_button_facebook);
//        View mFacebookLoginDivider = view.findViewById(R.id.checkout_login_form_divider_facebook);
//        View mFacebookSignUpDivider = view.findViewById(R.id.checkout_signup_form_divider_facebook);
//        // Set Facebook
//        FacebookHelper.showOrHideFacebookButton(this, mLoginFacebookButton, mFacebookLoginDivider, mSignUpFacebookButton, mFacebookSignUpDivider);
//        // Callback registration
//        mSignUpFacebookButton.registerCallback(mFacebookCallbackManager, this);
//        mLoginFacebookButton.registerCallback(mFacebookCallbackManager, this);
//    }
//
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#onStart()
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//        Print.i(TAG, "ON START");
//        // Validate current state
//        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
//            Print.d(TAG, "TRIGGER: AUTO LOGIN");
//            triggerAutoLogin();
//        } else if (JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0) {
//            Print.d(TAG, "TRIGGER: INIT FORM");
//            triggerInitForm();
//        } else {
//            boolean temp1 = (formResponse != null) ? loadForm(formResponse) : triggerLoginForm();
//            boolean temp2 = (signupFormResponse != null) ? loadSignUpForm(signupFormResponse) : triggerSignupForm();
//            Print.i(TAG, "VALIDATE: LOGIN/SIGNUP FORM: " + temp1 + " " + temp2);
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onResume()
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        retryForms = 0;
//        Print.i(TAG, "ON RESUME");
//        // validate if there was an error related to facebook
//        validateFacebookNetworkError();
//
//        /**
//         * Force input form align to left.
//         * The restore is performed on the step BaseFragment.onPause().
//         */
//        forceInputAlignToLeft();
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onPause()
//     */
//    @Override
//    public void onPause() {
//        super.onPause();
//        Print.i(TAG, "ON PAUSE");
//        getBaseActivity().hideKeyboard();
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onStop()
//     */
//    @Override
//    public void onStop() {
//        super.onStop();
//        Print.i(TAG, "ON STOP");
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
//     */
//    @Override
//    public void onDestroyView() {
//        Print.i(TAG, "ON DESTROY VIEW");
//        super.onDestroyView();
//        //if (loginFormContainer != null) loginFormContainer.removeAllViews();
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
//     */
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Print.i(TAG, "ON DESTROY");
//        formResponse = null;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
//     */
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
//     */
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        if (null != loginForm) {
//            for (DynamicFormItem item : loginForm) {
//                item.saveState(outState);
//            }
//            savedInstanceState = outState;
//        }
//        super.onSaveInstanceState(outState);
//    }
//
//    /**
//     * ############# CLICK LISTENER #############
//     */
//    /*
//     * (non-Javadoc)
//     * @see android.view.View.OnClickListener#onClick(android.view.View)
//     */
//    @Override
//    public void onClick(View view) {
//        super.onClick(view);
//        // Get view id
//        int id = view.getId();
//        // Login toggle
//        if (id == R.id.checkout_login_toogle) {
//            onClickLoginToogle();
//        }
//        // Login button
//        else if (id == R.id.checkout_login_form_button_enter) {
//            onClickLoginButton();
//        }
//        // Forgot Password
//        else if (id == R.id.checkout_login_form_button_password) {
//            onClickForgotPassword();
//        }
//        // Sign toggle
//        else if (id == R.id.checkout_signup_toogle) {
//            onClickSignupToogle();
//        }
//        // Sign button
//        else if (id == R.id.checkout_signup_form_button_enter) {
//            onClickSignupButton();
//        }
//        // Case FB buttons
//        else if (id == R.id.checkout_login_form_button_facebook || id == R.id.checkout_signup_form_button_facebook) {
//            showFragmentLoading();
//        }
//        // Unknown view
//        else {
//            Print.i(TAG, "ON CLICK: UNKNOWN VIEW");
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
//     */
//    @Override
//    protected void onClickRetryButton(View view) {
//        super.onClickRetryButton(view);
//        triggerAutoLogin();
//    }
//
//    /**
//     * Process the click on the login toogle
//     *
//     */
//    private void onClickLoginToogle() {
//        Print.i(TAG, "ON CLICK: LOGIN TOOGLE");
//        // Validate view
//        if (loginMainContainer != null && signupMainContainer != null) {
//            // Validate visibility
//            if (loginMainContainer.getVisibility() == View.VISIBLE) {
//                loginToogle.setSelected(false);
//                loginMainContainer.setVisibility(View.GONE);
//                signupToogle.setSelected(true);
//                signupMainContainer.setVisibility(View.VISIBLE);
//            } else {
//                loginToogle.setSelected(true);
//                loginMainContainer.setVisibility(View.VISIBLE);
//                signupToogle.setSelected(false);
//                signupMainContainer.setVisibility(View.GONE);
//            }
//        }
//    }
//
//    /**
//     * Process the click on the sign up toogle
//     *
//     */
//    private void onClickSignupToogle() {
//        Print.i(TAG, "ON CLICK: SIGNUP TOOGLE");
//        // Validate view
//        if (signupMainContainer != null && loginMainContainer != null) {
//            // Validate visibility
//            if (signupMainContainer.getVisibility() == View.VISIBLE) {
//                signupToogle.setSelected(false);
//                signupMainContainer.setVisibility(View.GONE);
//                loginToogle.setSelected(true);
//                loginMainContainer.setVisibility(View.VISIBLE);
//            } else {
//                signupToogle.setSelected(true);
//                signupMainContainer.setVisibility(View.VISIBLE);
//                loginToogle.setSelected(false);
//                loginMainContainer.setVisibility(View.GONE);
//            }
//        }
//    }
//
//    /**
//     * Process the click on login button
//     */
//    private void onClickLoginButton() {
//        Print.i(TAG, "ON CLICK: LOGIN");
//        try {
//            // Validate form
//            if (loginForm.validate()) {
//                requestLogin();
//            }
//            // Tracking login failed
//            else {
//                TrackerDelegator.trackLoginFailed(TrackerDelegator.ISNT_AUTO_LOGIN, GTMValues.CHECKOUT, GTMValues.EMAILAUTH);
//            }
//        } catch (NullPointerException e) {
//            Print.w(TAG, "LOGIN FORM IS NULL", e);
//            triggerLoginForm();
//        }
//    }
//
//    /**
//     * Process the click on sign up button
//     */
//    private void onClickSignupButton() {
//        Print.i(TAG, "ON CLICK: SIGNUP");
//        try {
//            if (signupForm.validate()) {
//                requestSignUp();
//            }
//        } catch (NullPointerException e) {
//            Print.w(TAG, "SIGNUP FORM IS NULL", e);
//            triggerSignupForm();
//        }
//    }
//
//    /**
//     * Process the click on forgot password
//     */
//    private void onClickForgotPassword() {
//        Print.i(TAG, "ON CLICK: FORGOT PASS");
//        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//    }
//
//    /**
//     * ########## SET FORMS ##########
//     */
//
//    /**
//     * Load the dynamic form
//     *
//     * @param form The login form
//     */
//    private boolean loadForm(Form form) {
//        Print.i(TAG, "LOAD FORM");
//        loginForm = FormFactory.getSingleton().CreateForm(FormConstants.LOGIN_FORM, getBaseActivity(), form);
//        loginFormContainer.removeAllViews();
//        loginFormContainer.addView(loginForm.getContainer());
//
//        boolean fillEmail = false;
//        String rememberedEmail = CustomerPreferences.getRememberedEmail(getBaseActivity());
//        if (!TextUtils.isEmpty(rememberedEmail)) {
//            fillEmail = true;
//        }
//
//        // Show save state
//        if (null != this.savedInstanceState && null != loginForm) {
//            Iterator<DynamicFormItem> iter = loginForm.getIterator();
//            while (iter.hasNext()) {
//                DynamicFormItem item = iter.next();
//                item.loadState(savedInstanceState);
//
//                if (fillEmail && FormInputType.email.equals(item.getType())) {
//                    ((EditText) item.getEditControl()).setText(rememberedEmail);
//                }
//            }
//        } else if (fillEmail) {
//            Iterator<DynamicFormItem> iter = loginForm.getIterator();
//            while (iter.hasNext()) {
//                DynamicFormItem item = iter.next();
//
//                if (FormInputType.email.equals(item.getType())) {
//                    ((EditText) item.getEditControl()).setText(rememberedEmail);
//                }
//            }
//        }
//        loginFormContainer.refreshDrawableState();
//        // Show container
//        showFragmentContentContainer();
//        Print.i(TAG, "code1 loading form completed : " + loginForm.getControlsCount());
//
//
//        return true;
//    }
//
//    /**
//     * Load the dynamic sign up form
//     *
//     * @param form The sign up form
//     */
//    private boolean loadSignUpForm(Form form) {
//        Print.i(TAG, "LOAD SIGN UP FORM");
//        signupForm = FormFactory.getSingleton().CreateForm(FormConstants.SIGNUP_FORM, getBaseActivity(), form);
//        signupFormContainer.removeAllViews();
//        signupFormContainer.addView(signupForm.getContainer());
//        signupFormContainer.refreshDrawableState();
//        // Show order summary only with cart info
//        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_ABOUT_YOU, JumiaApplication.INSTANCE.getCart());
//        // Show container
//        showFragmentContentContainer();
//        return true;
//    }
//
//
//    /**
//     * ############# REQUESTS #############
//     */
//
//    /**
//     * Method used to trigger the login
//     */
//    private void requestLogin() {
//        Print.i(TAG, "TRIGGER: LOGIN EVENT");
//        getBaseActivity().hideKeyboard();
//        ContentValues values = loginForm.save();
//        values.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
//        triggerLogin(values, true);
//    }
//
//    /**
//     * Method used to trigger the sign up
//     */
//    private void requestSignUp() {
//        Print.i(TAG, "TRIGGER: SIGN UP EVENT");
//        getBaseActivity().hideKeyboard();
//        ContentValues values = signupForm.save();
//        triggerSignup(values, true);
//    }
//
//    /**
//     * ########### TRIGGERS ###########
//     */
//
//    /**
//     * Trigger used to perform an auto login
//     */
//    private void triggerAutoLogin() {
//        Print.i(TAG, "TRIGGER: AUTO LOGIN");
//        onAutoLogin = true;
//
//        ContentValues values = JumiaApplication.INSTANCE.getCustomerUtils().getCredentials();
//
//        // Validate used has facebook credentials
//        try {
//            // Facebook flag
//            if (values.getAsBoolean(CustomerUtils.INTERNAL_FACEBOOK_FLAG)) {
//                Print.i(TAG, "USER HAS FACEBOOK CREDENTIALS");
//                showFragmentLoading();
//                triggerFacebookLogin(values, onAutoLogin);
//                return;
//            }
//        } catch (NullPointerException e) {
//            Print.i(TAG, "USER HASN'T FACEBOOK CREDENTIALS");
//        }
//
//        // Sign up flag
//        try {
//            if (values.getAsBoolean(CustomerUtils.INTERNAL_SIGN_UP_FLAG)) {
//                Print.i(TAG, "USER HAS SIGN UP CREDENTIALS");
//                showFragmentLoading();
//                triggerSignup(values, onAutoLogin);
//                return;
//            }
//        } catch (NullPointerException e) {
//            Print.i(TAG, "USER HASN'T SIGN UP CREDENTIALS");
//        }
//
//        // Try login with saved credentials
//        triggerLogin(values, onAutoLogin);
//
//    }
//
//    /**
//     * Trigger used to login an user
//     */
//    private void triggerLogin(ContentValues values, boolean saveCredentials) {
//        Print.i(TAG, "TRIGGER: LOGIN");
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
//        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, saveCredentials);
//        triggerContentEvent(new GetLoginHelper(), bundle, this);
//    }
//
//    /**
//     * Trigger used to sign up an user
//     */
//    private void triggerSignup(ContentValues values, boolean saveCredentials) {
//        Print.i(TAG, "TRIGGER: SIGN UP " + values.toString());
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
//        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, saveCredentials);
//        triggerContentEvent(new SetSignUpHelper(), bundle, this);
//    }
//
//    /**
//     * Trigger used to perform a login/signup from Facebook
//     */
//    @Override
//    public void triggerFacebookLogin(ContentValues values, boolean saveCredentials) {
//        Print.i(TAG, "TRIGGER: FACEBOOK LOGIN");
//        triggerContentEventNoLoading(new GetFacebookLoginHelper(), GetFacebookLoginHelper.createBundle(values, saveCredentials), this);
//    }
//
//    /**
//     * Trigger used to get the login form
//     *
//     * @return true
//     */
//    private boolean triggerLoginForm() {
//        Print.i(TAG, "TRIGGER: LOGIN FORM");
//        onAutoLogin = false;
//        triggerContentEvent(new GetLoginFormHelper(), null, this);
//        return true;
//    }
//
//    /**
//     * Trigger used to get the signup form
//     *
//     * @return true
//     */
//    private boolean triggerSignupForm() {
//        Print.i(TAG, "TRIGGER: SIGNUP FORM");
//        onAutoLogin = false;
//        triggerContentEvent(new GetSignUpFormHelper(), null, this);
//        return true;
//    }
//
//    /**
//     * Trigger used to get the customer
//     */
//    private void triggerGetCustomer() {
//        triggerContentEventNoLoading(new GetCustomerHelper(), null, this);
//    }
//
//    /**
//     * Trigger used to get the initialize forms
//     */
//    private void triggerInitForm() {
//        Print.i(TAG, "TRIGGER: INIT FORMS");
//        Bundle bundle = new Bundle();
//        triggerContentEvent(new GetInitFormHelper(), bundle, this);
//    }
//
//    /**
//     * Trigger used to force the cart update if user not in auto login
//     */
//    private void triggerGetShoppingCart() {
//        Print.i(TAG, "TRIGGER: GET CART AFTER LOGGED IN");
//        showFragmentLoading();
//        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
//    }
//
//    /*
//     * ########## NEXT STEP VALIDATION ##########
//     */
//
//    /**
//     * Method used to switch the checkout step
//     *
//     * @author sergiopereira
//     */
//    private void gotoNextStep() {
//        // Get next step
//        if (mNextFragment == null || mNextFragment == FragmentType.UNKNOWN) {
//            Print.w(TAG, "NEXT STEP IS NULL");
//            super.showFragmentErrorRetry();
//        } else {
//            Print.i(TAG, "GOTO NEXT STEP: " + mNextFragment.toString());
//            // Clean stack for new native checkout on the back stack (auto login)
//            getBaseActivity().removeAllNativeCheckoutFromBackStack();
//            getBaseActivity().onSwitchFragment(mNextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//        }
//    }
//
//    /**
//     * Before going to next step after Sign Up we need to get the customer information.
//     */
//    private void goToNextStepAfterSignUp() {
//        Print.d(TAG, "RECEIVED SET_SIGNUP_EVENT");
//        JumiaApplication.INSTANCE.setLoggedIn(true);
//        // Set guest user
//        if (JumiaApplication.CUSTOMER != null) {
//            JumiaApplication.CUSTOMER.setGuest(true);
//        }
//        // Track signup
//        trackCheckoutStarted(JumiaApplication.CUSTOMER.getIdAsString());
//        // Next step
//        gotoNextStep();
//    }
//
//    /*
//     * ########## RESPONSE ##########
//     */
//
//    /**
//     * Filter the success response bundle
//     */
//    protected boolean onSuccessEvent(BaseResponse baseResponse) {
//        Print.d(TAG, "ON SUCCESS EVENT");
//
//        if (isOnStoppingProcess) {
//            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
//            return true;
//        }
//
//        EventType eventType = baseResponse.getEventType();
//        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
//
//        switch (eventType) {
//            case INIT_FORMS:
//                triggerLoginForm();
//                triggerSignupForm();
//                break;
//            case SET_SIGNUP_EVENT:
//                cameFromSignUp = true;
//                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getMetadata().getData();
//                mNextFragment = nextStepStruct.getFragmentType();
//                Customer tempCustomer = ((CheckoutStepLogin)nextStepStruct.getCheckoutStepObject()).getCustomer();
//
//                if (null != tempCustomer) {
//                    TrackerDelegator.storeFirstCustomer(tempCustomer);
//                    TrackerDelegator.trackSignupSuccessful(GTMValues.CHECKOUT);
//                }
//                triggerGetCustomer();
//                break;
//            case FACEBOOK_LOGIN_EVENT:
//                // Set logged in
//                JumiaApplication.INSTANCE.setLoggedIn(true);
//                nextStepStruct = (NextStepStruct) baseResponse.getMetadata().getData();
//                // Get customer
//                Customer customerFb = ((CheckoutStepLogin)nextStepStruct.getCheckoutStepObject()).getCustomer();
//                JumiaApplication.CUSTOMER = customerFb;
//                // Get next step
//                mNextFragment = nextStepStruct.getFragmentType();
//                // Tracking login via facebook
//                trackLoginSuccess(customerFb, true);
//                trackCheckoutStarted(customerFb.getIdAsString());
//                // Force update the cart and after goto next step
//                if (!onAutoLogin) {
//                    triggerGetShoppingCart();
//                } else {
//                    gotoNextStep();
//                }
//                break;
//            case LOGIN_EVENT:
//                // Set logged in
//                JumiaApplication.INSTANCE.setLoggedIn(true);
//                nextStepStruct = (NextStepStruct) baseResponse.getMetadata().getData();
//                // Get customer
//                Customer customer = ((CheckoutStepLogin)nextStepStruct.getCheckoutStepObject()).getCustomer();
//                // Get next step
//                mNextFragment = nextStepStruct.getFragmentType();
//                // Persist user email or empty that value after successful login
//                CustomerPreferences.setRememberedEmail(getBaseActivity(), rememberEmailCheck.isChecked() ? customer.getEmail() : null);
//                // Tracking login
//                trackLoginSuccess(customer, false);
//                trackCheckoutStarted(customer.getIdAsString());
//                // Force update the cart and after goto next step
//                if (!onAutoLogin) {
//                    triggerGetShoppingCart();
//                } else {
//                    gotoNextStep();
//                }
//                break;
//            case GET_SHOPPING_CART_ITEMS_EVENT:
//                Print.d(TAG, "RECEIVED GET_SHOPPING_CART_ITEMS_EVENT");
//                // Cart updated goto next step
//                gotoNextStep();
//                break;
//            case GET_SIGNUP_FORM_EVENT:
//                // Save and load form
//                Form signupForm = (Form)baseResponse.getMetadata().getData();
//                loadSignUpForm(signupForm);
//                this.signupFormResponse = signupForm;
//                break;
//            case GET_LOGIN_FORM_EVENT:
//                Form form = (Form)baseResponse.getMetadata().getData();
//                // Validate form
//                if (form == null) {
//                    showLoginFormErrorDialog();
//                } else {
//                    // Save and load form
//                    loadForm(form);
//                    this.formResponse = form;
//                    // Facebook logout
//                    FacebookHelper.facebookLogout();
//                }
//                break;
//            case GET_CUSTOMER:
//                if (cameFromSignUp) {
//                    cameFromSignUp = false;
//                    goToNextStepAfterSignUp();
//                }
//                break;
//            default:
//                break;
//        }
//        return true;
//    }
//
//    /**
//     * Filter the error response bundle
//     */
//    protected boolean onErrorEvent(BaseResponse baseResponse) {
//
//        if (isOnStoppingProcess) {
//            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
//            return true;
//        }
//
//        // Generic error
//        if (super.handleErrorEvent(baseResponse)) {
//            Print.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
//            return true;
//        }
//
//        EventType eventType = baseResponse.getEventType();
//        ErrorCode errorCode = baseResponse.getError().getErrorCode();
//        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
//
//        switch (eventType) {
//            case GET_LOGIN_FORM_EVENT:
//                Print.w(TAG, "ON ERRER RECEIVED: GET_LOGIN_FORM_EVENT");
//                if (errorCode == ErrorCode.UNKNOWN_ERROR && null == loginForm) {
//                    restartAllFragments();
//                    return true;
//                }
//                break;
//            case FACEBOOK_LOGIN_EVENT:
//                // Clear credentials case auto login failed
//                clearCredentials();
//                // Facebook logout
//                FacebookHelper.facebookLogout();
//                // Track
//                TrackerDelegator.trackLoginFailed(onAutoLogin, GTMValues.CHECKOUT, GTMValues.FACEBOOK);
//                // Show alert
//                Map<String, List<String>> fErrors = baseResponse.getErrorMessages();
//                showErrorDialog(fErrors, R.string.error_login_title);
//                // Show container
//                showFragmentContentContainer();
//                break;
//            case LOGIN_EVENT:
//                // Clear credentials case auto login failed
//                clearCredentials();
//                TrackerDelegator.trackLoginFailed(onAutoLogin, GTMValues.CHECKOUT, GTMValues.EMAILAUTH);
//                if (errorCode == ErrorCode.REQUEST_ERROR) {
//
//                    if (onAutoLogin) {
//                        // Sometimes formDataRegistry is null, so init forms
//                        if (formResponse == null) {
//                            triggerInitForm();
//                        }
//                    } else {
//                        // Show error
//                        @SuppressWarnings("unchecked")
//                        Map<String, List<String>> errors = baseResponse.getErrorMessages();
//                        showErrorDialog(errors, R.string.error_login_title);
//                        showFragmentContentContainer();
//                    }
//                } else {
//                    showErrorFragment(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, this);
//                }
//                break;
//            case SET_SIGNUP_EVENT:
//                Print.w(TAG, "ON ERRER RECEIVED: SET_SIGNUP_EVENT");
//                TrackerDelegator.trackSignupFailed(GTMValues.CHECKOUT);
//                if (errorCode == ErrorCode.REQUEST_ERROR) {
//                    @SuppressWarnings("unchecked")
//                    Map<String, List<String>> errors = baseResponse.getErrorMessages();
//                    // Show dialog or toast
//                    if (!showErrorDialog(errors, R.string.error_signup_title)) {
//                        Toast.makeText(getBaseActivity(), R.string.internet_no_connection_details_label, Toast.LENGTH_SHORT).show();
//                    }
//                    showFragmentContentContainer();
//                } else {
//                    showErrorFragment(ErrorLayoutFactory.UNEXPECTED_ERROR_LAYOUT, this);
//                }
//                break;
//            case GET_SIGNUP_FORM_EVENT:
//                Print.w(TAG, "ON ERRER RECEIVED: GET_SIGNUP_FORM_EVENT");
//                if (retryForms < 3) {
//                    triggerInitForm();
//                    retryForms++;
//                }
//                break;
//            case GET_SHOPPING_CART_ITEMS_EVENT:
//                Print.w(TAG, "ON ERRER RECEIVED: GET_SHOPPING_CART_ITEMS_EVENT");
//                // Ignore the cart event
//                gotoNextStep();
//                break;
//            default:
//                Print.w(TAG, "WARNING: UNEXPECTED ERROR EVENT: " + eventType.toString() + " " + errorCode);
//                break;
//        }
//        return true;
//    }
//
//    /*
//     * ########### RESPONSE LISTENER ###########
//     */
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
//     */
//    @Override
//    public void onRequestError(BaseResponse baseResponse) {
//        onErrorEvent(baseResponse);
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
//     */
//    @Override
//    public void onRequestComplete(BaseResponse baseResponse) {
//        onSuccessEvent(baseResponse);
//    }
//
//    /*
//     * ########### TRACKING ###########
//     */
//
//    /**
//     * Method used to track the login success
//     */
//    private void trackLoginSuccess(Customer customer, boolean isFacebookLogin) {
//        Bundle params = new Bundle();
//        params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
//        params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, onAutoLogin);
//        params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, isFacebookLogin);
//        params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.CHECKOUT);
//        TrackerDelegator.trackLoginSuccessful(params);
//    }
//
//    /**
//     * Tracking the Checkout started
//     */
//    private void trackCheckoutStarted(String customerId) {
//        try {
//            PurchaseEntity cart = JumiaApplication.INSTANCE.getCart();
//            TrackerDelegator.trackCheckoutStart(TrackingEvent.CHECKOUT_STEP_ABOUT_YOU, customerId, cart.getCartCount(), cart.getPriceForTracking(), cart.getAttributeSetIdList());
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /*
//     * ########### DIALOGS ###########
//     */
//
//    /**
//     * Dialog used to show an error
//     */
//    private boolean showErrorDialog(Map<String, List<String>> errors, int titleId) {
//        Print.d(TAG, "SHOW ERROR DIALOG");
//        List<String> errorMessages = null;
//        if (errors != null) {
//            errorMessages = errors.get(RestConstants.JSON_VALIDATE_TAG);
//        }
//        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
//            showFragmentContentContainer();
//            dialog = DialogGenericFragment.newInstance(true, false,
//                    getString(titleId),
//                    errorMessages.get(0),
//                    getString(R.string.ok_label),
//                    "",
//                    new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            int id = v.getId();
//                            if (id == R.id.button1) {
//                                dismissDialogFragment();
//                            }
//                        }
//                    });
//            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    /**
//     * Notification used to show an error
//     */
//    private void showLoginFormErrorDialog() {
//        dialog = DialogGenericFragment.createServerErrorDialog(getBaseActivity(),
//                new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        showFragmentLoading();
//                        triggerLoginForm();
//                        dismissDialogFragment();
//                    }
//                }, false);
//        dialog.show(getBaseActivity().getSupportFragmentManager(), null);
//    }
//
//
//}
