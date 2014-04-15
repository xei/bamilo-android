/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetCustomerHelper;
import pt.rocket.helpers.GetInitFormHelper;
import pt.rocket.helpers.GetShoppingCartItemsHelper;
import pt.rocket.helpers.session.GetFacebookLoginHelper;
import pt.rocket.helpers.session.GetLoginFormHelper;
import pt.rocket.helpers.session.GetLoginHelper;
import pt.rocket.helpers.session.GetSignupFormHelper;
import pt.rocket.helpers.session.SetSignupHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import de.akquinet.android.androlog.Log;

/** 
 * Class used to perform the login or sign up
 * @author sergiopereira
 */
public class CheckoutAboutYouFragment extends BaseFragment implements OnClickListener, GraphUserCallback, StatusCallback, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutAboutYouFragment.class);

    private static final String FORM_ITEM_EMAIL = "email";

    private static final String FORM_ITEM_PASSWORD = "password";
    
    private static final String FB_PERMISSION_EMAIL = "email";
    
    private static CheckoutAboutYouFragment aboutYouFragment = null;

    private Form formResponse = null;

    private DynamicForm loginForm;

    private Bundle savedInstanceState;

    private UiLifecycleHelper uiHelper;
    
    private String loginOrigin = "";

    private View loginMainContainer;

    private ViewGroup loginFormContainer;

    private View signupMainContainer;

    private boolean onAutoLogin = false;

    private ViewGroup signupFormContainer;

    private DynamicForm signupForm;

    private Form signupFormResponse;

    @SuppressWarnings("unused")
    private boolean temp;

    private View loginToogle;

    private View signupToogle;

    private OrderSummary mOrderSummary;

    private FragmentType mNextFragment;
    
    private boolean cameFromSignUp = false;
    
    /**
     * Get the instance of CheckoutAboutYouFragment
     * @return {@link BaseFragment}
     */
    public static CheckoutAboutYouFragment getInstance(Bundle bundle) {
        aboutYouFragment = new CheckoutAboutYouFragment();
        
        if (bundle != null) aboutYouFragment.loginOrigin = bundle.getString(ConstantsIntentExtra.LOGIN_ORIGIN);
        
        return aboutYouFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutAboutYouFragment() {
        super(EnumSet.of(EventType.GET_LOGIN_FORM_EVENT, 
                EventType.GET_SIGNUP_FORM_EVENT), 
                EnumSet.of(EventType.LOGIN_EVENT, 
                EventType.FACEBOOK_LOGIN_EVENT, 
                EventType.SET_SIGNUP_EVENT),
                EnumSet.noneOf(MyMenuItem.class), 
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
        // Retain the fragment
        setRetainInstance(true);
        // Init the helper
        String appId = getBaseActivity().getResources().getString(R.string.app_id);
        uiHelper = new UiLifecycleHelper(getActivity(), (StatusCallback) this, appId);
        uiHelper.onCreate(savedInstanceState);
        
        TrackerDelegator.trackCheckoutStep(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), R.string.gcheckoutAboutYou, R.string.xcheckoutaboutyou, R.string.mixprop_checkout_about_you);
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
        return inflater.inflate(R.layout.checkout_about_you, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        
        // Login toggle
        loginToogle = view.findViewById(R.id.checkout_login_toogle);
        loginToogle.setOnClickListener(this);
        loginToogle.setSelected(true);
        // Login main
        loginMainContainer = view.findViewById(R.id.checkout_login_form_main);
        // Login form
        loginFormContainer = (ViewGroup) view.findViewById(R.id.checkout_login_form_container);
        // Login button
        view.findViewById(R.id.checkout_login_form_button_enter).setOnClickListener(this);
        // Forget button
        view.findViewById(R.id.checkout_login_form_button_password).setOnClickListener(this);
        
        // Sign toggle
        signupToogle = view.findViewById(R.id.checkout_signup_toogle);
        signupToogle.setOnClickListener(this);
        // Sign main
        signupMainContainer = view.findViewById(R.id.checkout_signup_form_main);
        // Sign form
        signupFormContainer = (ViewGroup) view.findViewById(R.id.checkout_signup_form_container);
        // Sign button
        view.findViewById(R.id.checkout_signup_form_button_enter).setOnClickListener(this);
        
        // FACEBOOK
        LoginButton facebookButton1 = (LoginButton) view.findViewById(R.id.checkout_login_form_button_facebook);
        LoginButton facebookButton2 = (LoginButton) view.findViewById(R.id.checkout_signup_form_button_facebook);
        setFacebookButton(facebookButton1);
        setFacebookButton(facebookButton2);
        
        // Validate current state
        if(JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()){
            Log.d(TAG, "TRIGGER: AUTO LOGIN");
            triggerAutoLogin();
        } else if(JumiaApplication.INSTANCE.getFormDataRegistry() == null || JumiaApplication.INSTANCE.getFormDataRegistry().size() == 0){
            Log.d(TAG, "TRIGGER: INIT FORM");
            triggerInitForm();
        } else {
            Log.d(TAG, "VALIDATE: LOGIN/SIGNUP FORM");
            temp = (formResponse != null)                                   ? loadForm(formResponse)                : triggerLoginForm() ;
            temp = (signupFormResponse != null && mOrderSummary != null)    ? loadSignupForm(signupFormResponse)    : triggerSignupForm();
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
        // Resume helper
        String appId = getBaseActivity().getResources().getString(R.string.app_id);
        uiHelper.setJumiaAppId(appId);
        uiHelper.onResume();
        
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
        getBaseActivity().hideKeyboard();
        uiHelper.onPause();
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
        uiHelper.onStop();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
        //if (loginFormContainer != null) loginFormContainer.removeAllViews();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        formResponse = null;
        uiHelper.onDestroy();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != loginForm) {
            Iterator<DynamicFormItem> iterator = loginForm.iterator();
            while (iterator.hasNext()) {
                DynamicFormItem item = iterator.next();
                item.saveState(outState);
            }
            savedInstanceState = outState;
        }
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
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
        // Login toggle
        if(id == R.id.checkout_login_toogle) onClickLoginToogle(view);
        // Login button
        else if(id == R.id.checkout_login_form_button_enter) onClickLoginButton();
        // Forgot Password
        else if(id == R.id.checkout_login_form_button_password) onClickForgotPassword();
        // Sign toggle
        else if(id == R.id.checkout_signup_toogle) onClickSignupToogle(view);
        // Sign button
        else if(id == R.id.checkout_signup_form_button_enter) onClickSignupButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on the login toogle
     * @param toogle view
     */
    private void onClickLoginToogle(View view) {
        Log.i(TAG, "ON CLICK: LOGIN TOOGLE");
        // Validate view
        if(loginMainContainer != null && signupMainContainer != null){
            // Validate visibility
            if (loginMainContainer.getVisibility() == View.VISIBLE) {
                loginToogle.setSelected(false);
                loginMainContainer.setVisibility(View.GONE);
                signupToogle.setSelected(true);
                signupMainContainer.setVisibility(View.VISIBLE);
            } else {
                loginToogle.setSelected(true);
                loginMainContainer.setVisibility(View.VISIBLE);
                signupToogle.setSelected(false);
                signupMainContainer.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * Process the click on the sign up toogle
     * @param toogle view
     */
    private void onClickSignupToogle(View view) {
        Log.i(TAG, "ON CLICK: SIGNUP TOOGLE");
        // Validate view
        if(signupMainContainer != null && loginMainContainer != null){
            // Validate visibility
            if (signupMainContainer.getVisibility() == View.VISIBLE) {
                signupToogle.setSelected(false);
                signupMainContainer.setVisibility(View.GONE);
                loginToogle.setSelected(true);
                loginMainContainer.setVisibility(View.VISIBLE);
            } else {
                signupToogle.setSelected(true);
                signupMainContainer.setVisibility(View.VISIBLE);
                loginToogle.setSelected(false);
                loginMainContainer.setVisibility(View.GONE);
            }
        }
    }
    
    /**
     * Process the click on login button
     */
    private void onClickLoginButton() {
        Log.i(TAG, "ON CLICK: LOGIN");
        try {
            if (loginForm.validate()) requestLogin();
        } catch (NullPointerException e) {
            Log.w(TAG, "LOGIN FORM IS NULL", e);
            triggerLoginForm();
        }
    }
    
    /**
     * Process the click on sign up button
     */
    private void onClickSignupButton() {
        Log.i(TAG, "ON CLICK: SIGNUP");
        try {
            if (signupForm.validate()) requestSignup();
        } catch (NullPointerException e) {
            Log.w(TAG, "SIGNUP FORM IS NULL", e);
            triggerSignupForm();
        }
    }
    
    /**
     * Process the click on forgot password
     */
    private void onClickForgotPassword() {
        Log.i(TAG, "ON CLICK: FORGOT PASS");
        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * ############# FACEBOOK #############
     */
    
    /**
     * Set the facebook button behavior
     * @param button
     */
    private void setFacebookButton(LoginButton button){
        button.setFragment(this);
        button.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        button.setReadPermissions(Arrays.asList(FB_PERMISSION_EMAIL));
    }
    
    /**
     * Clean the facebook session
     */
    private void cleanFacebookSession(){
        Session s = Session.getActiveSession();
        s.closeAndClearTokenInformation();
    }
    
    /*
     * (non-Javadoc)
     * @see com.facebook.Request.GraphUserCallback#onCompleted(com.facebook.model.GraphUser, com.facebook.Response)
     */
    @Override
    public void onCompleted(GraphUser user, Response response) {
        Log.i(TAG, "ON COMPLETED GRAPH USER");
        if (user != null)  requestFacebookLogin(user);   
    }
    
    /*
     * (non-Javadoc)
     * @see com.facebook.Session.StatusCallback#call(com.facebook.Session, com.facebook.SessionState, java.lang.Exception)
     */
    @Override
    public void call(Session session, SessionState state, Exception exception) {
        // Validate the FACEBOOK session state
        if (state.isOpened()) {
            Log.i(TAG, "SESSION IS OPENED");
            getBaseActivity().showLoading(false);
            Request request = Request.newMeRequest(session, this);
            Request.executeBatchAsync(request);
        } else
            Log.i(TAG, "SESSION IS CLOSED");
    }

    /**
     * ########## SET FORMS ########## 
     */
    
    /**
     * This method defines the click behavior of the edit texts from the dynamic form, allowing the
     * to login only when the form is completely filled.
     */
    public void setFormClickDetails(DynamicForm dynamicForm) {
        // Email
        DynamicFormItem emailItem = dynamicForm.getItemByKey(FORM_ITEM_EMAIL);
        if (emailItem == null) {
            return;
        }
        ((EditText) emailItem.getEditControl()).setId(21);
        // Pass
        DynamicFormItem passwordItem = dynamicForm.getItemByKey(FORM_ITEM_PASSWORD);
        if (passwordItem == null) {
            return;
        }
        ((EditText) emailItem.getEditControl()).setId(22);
    }    
    
    /**
     * Load the dynamic form
     * 
     * @param form
     */
    private boolean loadForm(Form form) {
        Log.i(TAG, "LOAD FORM: " + form.name);
        loginForm = FormFactory.getSingleton().CreateForm(FormConstants.LOGIN_FORM, getBaseActivity(), form);
        loginFormContainer.removeAllViews();
        loginFormContainer.addView(loginForm.getContainer());
        setFormClickDetails(loginForm);
        
//        // Validate the credentials and add the email 
//        if(JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
//            EditText editText = (EditText) loginForm.getItemByKey("email").getEditControl();
//            editText.setText(JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
//        }
                
        // Show save state
        if (null != this.savedInstanceState && null != loginForm) {
            Iterator<DynamicFormItem> iter = loginForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                item.loadState(savedInstanceState);
            }
        }
        loginFormContainer.refreshDrawableState();
        getBaseActivity().showContentContainer();
        Log.i(TAG, "code1 loading form completed : "+loginForm.getControlsCount());
        

        
        return true;
    }
    
    /**
     * Load the dynamic sign up form
     * 
     * @param form
     */
    private boolean loadSignupForm(Form form) {
        Log.i(TAG, "LOAD SIGNUP FORM: " + form.name);
        signupForm = FormFactory.getSingleton().CreateForm(FormConstants.SIGNUP_FORM, getBaseActivity(), form);
        signupFormContainer.removeAllViews();
        signupFormContainer.addView(signupForm.getContainer());
        setFormClickDetails(signupForm);
        signupFormContainer.refreshDrawableState();
        // Show order summary
        super.showOrderSummaryIfPresent(ConstantsCheckout.CHECKOUT_ABOUT_YOU, mOrderSummary);
        // Show container
        getBaseActivity().showContentContainer();
        return true;
    }
    
    
    /**
     * ############# REQUESTS #############
     */
    
    /**
     * Method used to trigger the login
     */
    private void requestLogin() {
        Log.i(TAG, "TRIGGER: LOGIN EVENT");
        getBaseActivity().hideKeyboard();
        ContentValues values = loginForm.save();
        values.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        triggerLogin(values, true);
    }
    
    /**
     * Method used to trigger the sign up
     */
    private void requestSignup() {
        Log.i(TAG, "TRIGGER: SIGNUP EVENT");
        getBaseActivity().hideKeyboard();
        ContentValues values = signupForm.save();
        triggerSignup(values, true);
    }

    /**
     * Method used to trigger the Facebook login
     * @param user
     */
    private void requestFacebookLogin(GraphUser user) {
        Log.d(TAG, "REQUEST FACEBOOK LOGIN");
        ContentValues values = new ContentValues();
        values.put("email", (String) user.getProperty("email"));
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("birthday", user.getBirthday());
        values.put("gender", (String) user.getProperty("gender"));
        values.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        triggerFacebookLogin(values, true);
    }
    
    
    /**
     * ########### TRIGGERS ###########  
     */
    
    /**
     * Trigger used to perform an auto login
     */
    private void triggerAutoLogin(){
        Log.i(TAG, "TRIGGER: AUTO LOGIN");
        onAutoLogin = true;
        
        ContentValues values = JumiaApplication.INSTANCE.getCustomerUtils().getCredentials();
        
        // Validate used has facebook credentials
        try {
            // Facebook flag
            if(values.getAsBoolean(CustomerUtils.INTERNAL_FACEBOOK_FLAG)) {
                Log.i(TAG, "USER HAS FACEBOOK CREDENTIALS");
                getBaseActivity().showLoading(false);
                triggerFacebookLogin(values, onAutoLogin);
                return;
            }
        } catch (NullPointerException e) {
            Log.i(TAG, "USER HASN'T FACEBOOK CREDENTIALS");
        }
        
        // Signup flag
        try {
            if(values.getAsBoolean(CustomerUtils.INTERNAL_SIGNUP_FLAG)){
                Log.i(TAG, "USER HAS SIGNUP CREDENTIALS");
                getBaseActivity().showLoading(false);
                triggerSignup(values, onAutoLogin);
                return;
            }
        } catch (NullPointerException e) {
            Log.i(TAG, "USER HASN'T SIGNUP CREDENTIALS");
        }
        
        // Try login with saved credentials
        triggerLogin(values, onAutoLogin);
    }
    
    /**
     * Trigger used to login an user
     * @param values
     * @param saveCredentials
     */
    private void triggerLogin(ContentValues values, boolean saveCredentials) {
        Log.i(TAG, "TRIGGER: LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEvent(new GetLoginHelper(), bundle, this);
    }
    
    /**
     * Trigger used to sign up an user
     * @param values
     * @param saveCredentials
     */
    private void triggerSignup(ContentValues values, boolean saveCredentials) {
        Log.i(TAG, "TRIGGER: SIGNUP " + values.toString());
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetSignupHelper.FORM_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEvent(new SetSignupHelper(), bundle, this);
    }
    
    /**
     * Trigger used to perform a login/signup from Facebook
     * @param values
     * @param saveCredentials
     */
    private void triggerFacebookLogin(ContentValues values,  boolean saveCredentials){
        Log.i(TAG, "TRIGGER: FACEBOOK LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEventWithNoLoading(new GetFacebookLoginHelper(), bundle, this);
    }
    
    /**
     * Trigger used to get the login form
     * @return true
     */
    private boolean triggerLoginForm(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        onAutoLogin = false;
        triggerContentEvent(new GetLoginFormHelper(), null, this);
        return true;
    }
    
    /**
     * Trigger used to get the signup form
     * @return true
     */
    private boolean triggerSignupForm(){
        Log.i(TAG, "TRIGGER: SIGNUP FORM");
        onAutoLogin = false;
        triggerContentEvent(new GetSignupFormHelper(), null, this);
        return true;
    }
    
    private void triggerGetCustomer(){
        
        triggerContentEventWithNoLoading(new GetCustomerHelper(), null, this);
    }
    
    /**
     * Trigger used to get the initialize forms
     */
    private void triggerInitForm(){
        Log.i(TAG, "TRIGGER: INIT FORMS");
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetInitFormHelper(), bundle, this);
    }
    
    /**
     * Trigger used to force the cart update if user not in auto login 
     */
    private void triggerGetShoppingCart(){
        Log.i(TAG, "TRIGGER: GET CART AFTER LOGGED IN");
        getBaseActivity().showLoading(false);
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
    }
    
    /**
     * ########## NEXT STEP VALIDATION ########## 
     */
    
    /**
     * Method used to switch the checkoput step
     * @author sergiopereira
     */
    private void gotoNextStep(){
        // Get next step
        if(mNextFragment == null || mNextFragment == FragmentType.UNKNOWN) {
            Log.w(TAG, "NEXT STEP IS NULL");
            super.gotoOldCheckoutMethod(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail(), "next step is null");
        } else {
            Log.i(TAG, "GOTO NEXT STEP: " + mNextFragment.toString());
            // Update
            getBaseActivity().hideKeyboard();
            getBaseActivity().updateSlidingMenuCompletly();
            // Clean stack for new native checkout on the back stack (auto ogin)
            super.removeNativeCheckoutFromBackStack();
            // Goto next step
            getBaseActivity().onSwitchFragment(mNextFragment, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }
    
    /**
     * ########## RESPONSE ########## 
     */
    
    
    /**
     * Before going to next step after Sign Up we need to get the customer information.
     */
    private void goToNextStepAfterSignUp(){
        Log.d(TAG, "RECEIVED SET_SIGNUP_EVENT");
        JumiaApplication.INSTANCE.setLoggedIn(true);
        
        if(JumiaApplication.INSTANCE.CUSTOMER != null){
            JumiaApplication.INSTANCE.CUSTOMER.setGuest(true);    
        }
        
        TrackerDelegator.trackSignUp(getBaseActivity(), JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        
        // Next step
        gotoNextStep();            
    }
    
    /**
     * Filter the response bundle
     * @param bundle
     * @return true/false
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");
        
        if(isOnStoppingProcess){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case INIT_FORMS:
            triggerLoginForm();
            triggerSignupForm();
            break;
        case SET_SIGNUP_EVENT:
            cameFromSignUp = true;
            mNextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            triggerGetCustomer();
            break;
        case FACEBOOK_LOGIN_EVENT:
            // Set logged in
            JumiaApplication.INSTANCE.setLoggedIn(true);
            // Get customer
            Customer customerFb = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.INSTANCE.CUSTOMER = customerFb;
            // Get next step
            mNextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);

            // Force update the cart and after goto next step
            if(!onAutoLogin){
                triggerGetShoppingCart();
                // Tracking
                TrackerDelegator.trackLoginSuccessful(getBaseActivity(), customerFb, onAutoLogin, loginOrigin, true);
            } else {
                gotoNextStep();
            }
            break;
        case LOGIN_EVENT:
            // Set logged in
            JumiaApplication.INSTANCE.setLoggedIn(true);
            // Get customer
            Customer customer = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            // Get next step
            mNextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            
            // Force update the cart and after goto next step
            if(!onAutoLogin){
                // Tracking
                TrackerDelegator.trackLoginSuccessful(getBaseActivity(), customer, onAutoLogin, loginOrigin, false);
                triggerGetShoppingCart();
            } else {
                gotoNextStep();
            }
            
          
            
            break;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            Log.d(TAG, "RECEIVED GET_SHOPPING_CART_ITEMS_EVENT");
            // Cart updated goto next step
            gotoNextStep();
            break;
        case GET_SIGNUP_FORM_EVENT:
           // Get order summary
           mOrderSummary = bundle.getParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY);
           // Save and load form
           Form signupForm = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
           loadSignupForm(signupForm);
           this.signupFormResponse = signupForm;
           break;
        case GET_LOGIN_FORM_EVENT:
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            // Validate form
            if (form == null) {
                showLoginFormErrorDialog();
            } else {
                // Save and load form
                loadForm(form);
                this.formResponse = form;
                // Clean FACEBOOK session
                cleanFacebookSession();
            }
            break;
        case GET_CUSTOMER:
            if(cameFromSignUp){
                cameFromSignUp = false;
                goToNextStepAfterSignUp();
            }
            
            break;
        }
        return true;
    }


    /**
     * 
     * @param bundle
     * @return
     */
    protected boolean onErrorEvent(Bundle bundle) {
        
    	if(isOnStoppingProcess){
    	    Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
    		return true;
    	}
    	
    	// Generic error
        if (getBaseActivity() != null && getBaseActivity().handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }
    	
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case GET_LOGIN_FORM_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: GET_LOGIN_FORM_EVENT");
            if (errorCode == ErrorCode.UNKNOWN_ERROR && null == loginForm) {
                restartAllFragments();
            }
            break;
        case FACEBOOK_LOGIN_EVENT:
        case LOGIN_EVENT:
            TrackerDelegator.trackLoginFailed(onAutoLogin);
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                
                if (onAutoLogin) {
                    // Sometimes formDataRegistry is null, so init forms
                    if (formResponse == null) triggerInitForm();
                } else {
                    // Show error
                    @SuppressWarnings("unchecked")
                    HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                    showErrorDialog(errors, R.string.error_login_title);
                    getBaseActivity().showContentContainer();
                }
            }
            break;
        case SET_SIGNUP_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: SET_SIGNUP_EVENT");
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                @SuppressWarnings("unchecked")
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY); 
                showErrorDialog(errors, R.string.error_signup_title);
                getBaseActivity().showContentContainer();
            }
            break;
        case GET_SIGNUP_FORM_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: GET_SIGNUP_FORM_EVENT");
            triggerInitForm();
            break;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: GET_SHOPPING_CART_ITEMS_EVENT");
            // Ignore the cart event
            gotoNextStep();
            break;
        default:
            if(getBaseActivity().handleErrorEvent(bundle)){
                Log.w(TAG, "BASE ACTIVITY HANDLE ERROR EVENT!");
                return true;
            }
            break;
        }
        return true;
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
    
    /**
     * Dialog used to show an error
     * @param errors
     */
    private void showErrorDialog(HashMap<String, List<String>> errors, int titleId){
        Log.d(TAG, "SHOW ERROR DIALOG: " + errors.toString());
        List<String> errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);

        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            
            if(getBaseActivity() != null) getBaseActivity().showContentContainer();
            
            dialog = DialogGenericFragment.newInstance(true, true, false,
                    getString(titleId),
                    errorMessages.get(0),
                    getString(R.string.ok_label), "", new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dialog.dismiss();
                            }

                        }

                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
        }
    }
    
    /**
     * Notification used to show an error
     */
    private void showLoginFormErrorDialog(){
        dialog = DialogGenericFragment.createServerErrorDialog(getBaseActivity(),
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getBaseActivity().showLoading(false);
                        triggerLoginForm();
                        dialog.dismiss();
                    }
                }, false);
        dialog.show(getBaseActivity().getSupportFragmentManager(), null);
    }
    
    
}
