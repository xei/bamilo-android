/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.CheckBox;
import pt.rocket.components.customfontviews.EditText;
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
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import pt.rocket.framework.tracking.TrackingEvent;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.NetworkConnectivity;
import pt.rocket.helpers.account.GetCustomerHelper;
import pt.rocket.helpers.cart.GetShoppingCartItemsHelper;
import pt.rocket.helpers.configs.GetInitFormHelper;
import pt.rocket.helpers.session.GetFacebookLoginHelper;
import pt.rocket.helpers.session.GetLoginFormHelper;
import pt.rocket.helpers.session.GetLoginHelper;
import pt.rocket.helpers.session.GetSignupFormHelper;
import pt.rocket.helpers.session.SetSignupHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.preferences.CustomerPreferences;
import pt.rocket.utils.InputType;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.social.FacebookHelper;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.facebook.Request.GraphUserCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
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

    private Form formResponse = null;

    private DynamicForm loginForm;

    private Bundle savedInstanceState;

    private UiLifecycleHelper uiHelper;

    private View loginMainContainer;

    private ViewGroup loginFormContainer;
    
    private CheckBox rememberEmailCheck;

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
    
    private int retryForms = 0;

    private LoginButton mLoginFacebookButton;
    
    /**
     * Get the instance of CheckoutAboutYouFragment
     * @return {@link BaseFragment}
     */
    public static CheckoutAboutYouFragment getInstance(Bundle bundle) { 
        return new CheckoutAboutYouFragment();
    }

    /**
     * Empty constructor
     */
    public CheckoutAboutYouFragment() {
        super(EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Checkout,
                R.layout.checkout_about_you_main,
                R.string.checkout_label,
                KeyboardState.ADJUST_CONTENT,
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
        uiHelper = new UiLifecycleHelper(getActivity(), (StatusCallback) this);
        uiHelper.onCreate(savedInstanceState);
        
        Bundle params = new Bundle();        
        params.putString(TrackerDelegator.EMAIL_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getEmail());
        params.putSerializable(TrackerDelegator.GA_STEP_KEY, TrackingEvent.CHECKOUT_STEP_ABOUT_YOU);     
        
        TrackerDelegator.trackCheckoutStep(params);
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
        // Remember login checkbox
        rememberEmailCheck = (CheckBox) view.findViewById(R.id.login_remember_user_email);
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
        mLoginFacebookButton = (LoginButton) view.findViewById(R.id.checkout_login_form_button_facebook);
        LoginButton facebookButton2 = (LoginButton) view.findViewById(R.id.checkout_signup_form_button_facebook);
        View facebookDivider1 = view.findViewById(R.id.checkout_login_form_divider_facebook);
        View facebookDivider2 = view.findViewById(R.id.checkout_signup_form_divider_facebook);
        // Set Facebook
        FacebookHelper.showOrHideFacebookButton(this, mLoginFacebookButton, facebookDivider1, facebookButton2, facebookDivider2);
        
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
        
        retryForms = 0;
        Log.i(TAG, "ON RESUME");
        // Resume helper
        uiHelper.onResume();
        
        /**
         * Force input form align to left.
         * The restore is performed on the step BaseFragment.onPause().
         */
        forceInputAlignToLeft();
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
        //retry button
        else if(id == R.id.fragment_root_retry_button) onClickRetryButton();
        // Case FB buttons
        else if(id == R.id.checkout_login_form_button_facebook || id == R.id.checkout_signup_form_button_facebook) showFragmentLoading();        
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on retry button.
     * @author paulo
     */
    private void onClickRetryButton() {
        Bundle bundle = new Bundle();
        if(null != JumiaApplication.CUSTOMER){
            bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.SHOPPING_CART);
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
            
        } else {
            getBaseActivity().onSwitchFragment(FragmentType.SHOPPING_CART, bundle, FragmentController.ADD_TO_BACK_STACK);
//            restartAllFragments();
        }
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
            // Validate form
            if (loginForm.validate()) requestLogin();
            // Tracking login failed
            else TrackerDelegator.trackLoginFailed(TrackerDelegator.ISNT_AUTO_LOGIN, GTMValues.CHECKOUT, GTMValues.EMAILAUTH);
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
     * ################ FACEBOOK ################ 
     */
    /*
     * (non-Javadoc)
     * @see com.facebook.Session.StatusCallback#call(com.facebook.Session, com.facebook.SessionState, java.lang.Exception)
     */
    @Override
    public void call(Session session, SessionState state, Exception exception) {
        onSessionStateChange(session, state, exception);
    }
    
    /**
     * Validate Facebook session.
     * @param session
     * @param state
     * @param exception
     * @author sergiopereira
     */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        Log.i(TAG, "SESSION: " + session.toString() + "STATE: " + state.toString());
        // Exception handling for no network error
        if(exception != null && !NetworkConnectivity.isConnected(getBaseActivity())) {
            createNoNetworkDialog(mLoginFacebookButton);
            return;
        }
        // Validate state
        if (state.isOpened() && session.isOpened()) {
            // Case user not accept the new request for required permissions
            if(FacebookHelper.userNotAcceptRequiredPermissions(session)) 
                super.onUserNotAcceptRequiredPermissions();
            // Case required permissions are not granted then request again
            else if(FacebookHelper.wereRequiredPermissionsGranted(session))
                super.onMakeNewRequiredPermissionsRequest(this, session, this);
            // Case accept permissions
            else super.onMakeGraphUserRequest(session, this);
        }
        // Other cases
        else if (state.isClosed()) {
            Log.i(TAG, "USER Logged out!");
            showFragmentContentContainer();
        }
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
        
        boolean fillEmail = false;
        String rememberedEmail = CustomerPreferences.getRememberedEmail(getBaseActivity());
        if (!TextUtils.isEmpty(rememberedEmail)) {
            fillEmail = true;
        }
        
        // Show save state
        if (null != this.savedInstanceState && null != loginForm) {
            Iterator<DynamicFormItem> iter = loginForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                item.loadState(savedInstanceState);

                if (fillEmail && InputType.email.equals(item.getType())) {
                    ((EditText) item.getEditControl()).setText(rememberedEmail);
                }
            }
        } else if (fillEmail) {
            Iterator<DynamicFormItem> iter = loginForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();

                if (InputType.email.equals(item.getType())) {
                    ((EditText) item.getEditControl()).setText(rememberedEmail);
                }
            }
        }
        loginFormContainer.refreshDrawableState();
        showFragmentContentContainer();
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
        showFragmentContentContainer();
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
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            ContentValues values = JumiaApplication.INSTANCE.getCustomerUtils().getCredentials();

            // Validate used has facebook credentials
            try {
                // Facebook flag
                if(values.getAsBoolean(CustomerUtils.INTERNAL_FACEBOOK_FLAG)) {
                    Log.i(TAG, "USER HAS FACEBOOK CREDENTIALS");
                    showFragmentLoading();
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
                    showFragmentLoading();
                    triggerSignup(values, onAutoLogin);
                    return;
                }
            } catch (NullPointerException e) {
                Log.i(TAG, "USER HASN'T SIGNUP CREDENTIALS");
            }
            
            // Try login with saved credentials
            triggerLogin(values, onAutoLogin);
        } else {
            showFragmentRetry(this);
        }
     
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

    /**
     * Trigger used to get the customer
     */
    private void triggerGetCustomer(){
        triggerContentEventWithNoLoading(new GetCustomerHelper(), null, this);
    }
    
    /**
     * Trigger used to get the initialize forms
     */
    private void triggerInitForm(){        
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            Log.i(TAG, "TRIGGER: INIT FORMS");
            Bundle bundle = new Bundle();
            triggerContentEvent(new GetInitFormHelper(), bundle, this);
        } else {
            showFragmentRetry(this);
        }     

    }
    
    /**
     * Trigger used to force the cart update if user not in auto login 
     */
    private void triggerGetShoppingCart(){
        Log.i(TAG, "TRIGGER: GET CART AFTER LOGGED IN");
        showFragmentLoading();
        triggerContentEvent(new GetShoppingCartItemsHelper(), null, this);
    }
    
    /*
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
            // Clean stack for new native checkout on the back stack (auto login)
            super.removeNativeCheckoutFromBackStack();
            // Goto next step
            Bundle bundle = new Bundle();
            // Validate if is guest user and sent the flag 
            if(JumiaApplication.CUSTOMER.isGuest()) bundle.putBoolean(ConstantsIntentExtra.IS_SIGNUP, true);
            // Go
            getBaseActivity().onSwitchFragment(mNextFragment, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    }    
    
    /**
     * Before going to next step after Sign Up we need to get the customer information.
     */
    private void goToNextStepAfterSignUp(){
        Log.d(TAG, "RECEIVED SET_SIGNUP_EVENT");
        JumiaApplication.INSTANCE.setLoggedIn(true);
        // Set guest user
        if(JumiaApplication.CUSTOMER != null) JumiaApplication.CUSTOMER.setGuest(true);
        // Track signup
        trackCheckoutStarted(JumiaApplication.CUSTOMER.getIdAsString());
        // Next step
        gotoNextStep();
    }
    
    /*
     * ########## RESPONSE ########## 
     */
    
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
            Customer tempCustomer = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);

            if(null != tempCustomer) {
                TrackerDelegator.storeFirstCustomer(tempCustomer);
                Bundle params = new Bundle();
                params.putParcelable(TrackerDelegator.CUSTOMER_KEY, tempCustomer);
                params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.CHECKOUT);
                TrackerDelegator.trackSignupSuccessful(params);
            }
            triggerGetCustomer();
            break;
        case FACEBOOK_LOGIN_EVENT:
            // Set logged in
            JumiaApplication.INSTANCE.setLoggedIn(true);
            // Get customer
            Customer customerFb = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.CUSTOMER = customerFb;
            // Get next step
            mNextFragment = (FragmentType) bundle.getSerializable(Constants.BUNDLE_NEXT_STEP_KEY);
            // Tracking login via facebook
            trackLoginSuccess(customerFb, true);
            trackCheckoutStarted(customerFb.getIdAsString());
            // Force update the cart and after goto next step
            if(!onAutoLogin){
                triggerGetShoppingCart();
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
            // Persist user email or empty that value after successful login
            CustomerPreferences.setRememberedEmail(getBaseActivity(), rememberEmailCheck.isChecked() ? customer.getEmail() : null);
            // Tracking login
            trackLoginSuccess(customer, false);
            trackCheckoutStarted(customer.getIdAsString());
            // Force update the cart and after goto next step
            if(!onAutoLogin){
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
                FacebookHelper.cleanFacebookSession();
            }
            break;
        case GET_CUSTOMER:
            if(cameFromSignUp){
                cameFromSignUp = false;
                goToNextStepAfterSignUp();
            }
            break;
        default:
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
        if (super.handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE FRAGMENT HANDLE ERROR EVENT");
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
                return true;
            }
            break;
        case FACEBOOK_LOGIN_EVENT:
            // Clear credentials case auto login failed
            clearCredentials();
            // Clean the Facebook Session
            FacebookHelper.cleanFacebookSession();
            // Track
            TrackerDelegator.trackLoginFailed(onAutoLogin, GTMValues.CHECKOUT, GTMValues.FACEBOOK);
            // Show alert
            HashMap<String, List<String>> fErrors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            showErrorDialog(fErrors, R.string.error_login_title);
            // Show container
            showFragmentContentContainer();
            break;
        case LOGIN_EVENT:
            // Clear credentials case auto login failed
            clearCredentials();
            // Validate type
            String type = (eventType == EventType.FACEBOOK_LOGIN_EVENT) ? type = GTMValues.FACEBOOK : GTMValues.EMAILAUTH;
            TrackerDelegator.trackLoginFailed(onAutoLogin, GTMValues.CHECKOUT, type);
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                
                if (onAutoLogin) {
                    // Sometimes formDataRegistry is null, so init forms
                    if (formResponse == null) triggerInitForm();
                } else {
                    // Show error
                    @SuppressWarnings("unchecked")
                    HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                    showErrorDialog(errors, R.string.error_login_title);
                    showFragmentContentContainer();
                }
            }
            break;
        case SET_SIGNUP_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: SET_SIGNUP_EVENT");
            TrackerDelegator.trackSignupFailed(GTMValues.CHECKOUT);
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                @SuppressWarnings("unchecked")
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY); 
                showErrorDialog(errors, R.string.error_signup_title);
                showFragmentContentContainer();
            }
            break; 
        case GET_SIGNUP_FORM_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: GET_SIGNUP_FORM_EVENT");
            if(retryForms <3){
                triggerInitForm();
                retryForms++;
            }
            
            break;
        case GET_SHOPPING_CART_ITEMS_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: GET_SHOPPING_CART_ITEMS_EVENT");
            // Ignore the cart event
            gotoNextStep();
            break;
        default:
            Log.w(TAG, "WARNING: UNEXPECTED ERROR EVENT: " + eventType.toString() + " " + errorCode);
            break;
        }
        return true;
    }
    
    /*
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
     * ########### TRACKING ###########  
     */
    
    /**
     * Method used to track the login success
     * @param customer
     * @param isFacebookLogin
     * @author sergiopereira
     */
    private void trackLoginSuccess(Customer customer, boolean isFacebookLogin) {
        Bundle params = new Bundle();
        params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
        params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, onAutoLogin);
        params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, isFacebookLogin);
        params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.CHECKOUT);
        TrackerDelegator.trackLoginSuccessful(params);
    }

    /**
     * Tracking the Checkout started
     * @param customerId
     * @author sergiopereira
     */
    private void trackCheckoutStarted(String customerId) {
        try {
            ShoppingCart cart = JumiaApplication.INSTANCE.getCart();
            TrackerDelegator.trackCheckoutStart(TrackingEvent.CHECKOUT_STEP_ABOUT_YOU, customerId, cart.getCartCount(), cart.getPriceForTracking());   
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * ########### DIALOGS ###########  
     */    
    
    /**
     * Dialog used to show an error
     * @param errors
     */
    private void showErrorDialog(HashMap<String, List<String>> errors, int titleId) {
        Log.d(TAG, "SHOW ERROR DIALOG");
        List<String> errorMessages = null;
        if (errors != null) {
            errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);
        }
        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            showFragmentContentContainer();
            dialog = DialogGenericFragment.newInstance(true, true, false,
                    getString(titleId),
                    errorMessages.get(0),
                    getString(R.string.ok_label),
                    "",
                    new OnClickListener() {
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
                        showFragmentLoading();
                        triggerLoginForm();
                        dialog.dismiss();
                    }
                }, false);
        dialog.show(getBaseActivity().getSupportFragmentManager(), null);
    }


    
    
}
