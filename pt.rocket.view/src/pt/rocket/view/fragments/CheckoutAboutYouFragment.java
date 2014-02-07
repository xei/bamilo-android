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
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetInitFormHelper;
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
import pt.rocket.view.BaseActivity;
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
import android.widget.Toast;

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
 * 
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutAboutYouFragment extends BaseFragment implements OnClickListener, GraphUserCallback, StatusCallback, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutAboutYouFragment.class);

    private final static String FORM_ITEM_EMAIL = "email";

    private final static String FORM_ITEM_PASSWORD = "password";

    private Form formResponse = null;

    private DynamicForm loginForm;

    private Bundle savedInstanceState;

    private static CheckoutAboutYouFragment loginFragment = null;

    private FragmentType nextFragmentType;
    
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
    
    private static final String PERMISSION_EMAIL = "email";
    
    /**
     * 
     * @return
     */
    public static CheckoutAboutYouFragment getInstance(Bundle bundle) {
        loginFragment = new CheckoutAboutYouFragment();
        if (bundle != null) {
            loginFragment.nextFragmentType = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
            loginFragment.loginOrigin = bundle.getString(ConstantsIntentExtra.LOGIN_ORIGIN);
        }
        return loginFragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutAboutYouFragment() {
        super(EnumSet.of(EventType.GET_LOGIN_FORM_EVENT, EventType.GET_SIGNUP_FORM_EVENT), 
                EnumSet.of(EventType.LOGIN_EVENT, EventType.FACEBOOK_LOGIN_EVENT, EventType.SET_SIGNUP_EVENT),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.LoginOut, 
                BaseActivity.CHECKOUT_STEP_1);
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
        uiHelper = new UiLifecycleHelper(getActivity(), this, appId);
        uiHelper.onCreate(savedInstanceState);
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
            temp = (formResponse != null)       ? loadForm(formResponse)                : triggerLoginForm() ;
            temp = (signupFormResponse != null) ? loadSignupForm(signupFormResponse)    : triggerSignupForm();
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
//        // Facebook button
//        else if(id == R.id.checkout_login_form_button_facebook) onClickFacebookLoginButton();
//        // Facebook button
//        else if(id == R.id.checkout_signup_form_button_facebook) onClickFacebookSignupButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
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
    
    private void onClickLoginButton() {
        Log.i(TAG, "ON CLICK: LOGIN");
        try {
            if (loginForm.validate()) requestLogin();
        } catch (NullPointerException e) {
            Log.w(TAG, "LOGIN FORM IS NULL", e);
            triggerLoginForm();
        }
    }
    
    private void onClickSignupButton() {
        Log.i(TAG, "ON CLICK: SIGNUP");
        try {
            if (signupForm.validate()) requestSignup();
        } catch (NullPointerException e) {
            Log.w(TAG, "SIGNUP FORM IS NULL", e);
            triggerSignupForm();
        }
    }
    
    private void onClickForgotPassword() {
        Log.i(TAG, "ON CLICK: FORGOT PASS");
        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    private void onClickFacebookLoginButton() {
        Log.i(TAG, "ON CLICK: FACEBOOK");
        // TODO
    }
    
    private void onClickFacebookSignupButton() {
        Log.i(TAG, "ON CLICK: FACEBOOK");
     // TODO
    }

    /**
     * ############# FACEBOOK #############
     */
    
    private void setFacebookButton(LoginButton button){
        button.setFragment(this);
        button.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        button.setReadPermissions(Arrays.asList(PERMISSION_EMAIL));
    }
    
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
        loginForm = FormFactory.getSingleton().CreateForm(FormConstants.LOGIN_FORM, getActivity(), form);
        loginFormContainer.removeAllViews();
        loginFormContainer.addView(loginForm.getContainer());
        setFormClickDetails(loginForm);

        // Show save state
        if (null != this.savedInstanceState && null != loginForm) {
            Iterator<DynamicFormItem> iter = loginForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                item.loadState(savedInstanceState);
            }
        }
        loginFormContainer.refreshDrawableState();
        getBaseActivity().showContentContainer(false);
        Log.i(TAG, "code1 loading form completed : "+loginForm.getControlsCount());
        
        return true;
    }
    
    /**
     * Load the dynamic signup form
     * 
     * @param form
     */
    private boolean loadSignupForm(Form form) {
        Log.i(TAG, "LOAD SIGNUP FORM: " + form.name);
        signupForm = FormFactory.getSingleton().CreateForm(FormConstants.SIGNUP_FORM, getActivity(), form);
        signupFormContainer.removeAllViews();
        signupFormContainer.addView(signupForm.getContainer());
        setFormClickDetails(signupForm);
        signupFormContainer.refreshDrawableState();
        getBaseActivity().showContentContainer(false);
        return true;
    }
    
    
    /**
     * ########## RESPONSE ########## 
     */
    
    /**
     * 
     * @param bundle
     * @return
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");
        
        // Validate fragment visibility
        if(!isVisible()){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        if(getBaseActivity() != null){
            Log.d(TAG, "BASE ACTIVITY HANDLE SUCCESS EVENT");
            getBaseActivity().handleSuccessEvent(bundle);
        } else {
            return true;
        }
         
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case INIT_FORMS:
            triggerLoginForm();
            triggerSignupForm();
            return true;            
        case SET_SIGNUP_EVENT:
            Log.d(TAG, "RECEIVED SET_SIGNUP_EVENT");
            getBaseActivity().hideKeyboard();
            getBaseActivity().updateSlidingMenuCompletly();
            getBaseActivity().onBackPressed();
            getBaseActivity().onSwitchFragment(FragmentType.CREATE_ADDRESS, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            // TODO: Add the respective track
            break;
        case FACEBOOK_LOGIN_EVENT:
          // Get Customer
          getBaseActivity().hideKeyboard();
          getBaseActivity().updateSlidingMenuCompletly();
          getBaseActivity().onBackPressed();
          /**
           * TODO:
           * Login with Facebook button ->  Default Shipping Address page
           * Sign Up with Facebook button -> Add New Address
           */
          // if(nextFragmentType != null) getBaseActivity().onSwitchFragment(nextFragmentType, null, true);
          getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
          
          TrackerDelegator.trackLoginSuccessful(getBaseActivity(), (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY), onAutoLogin, loginOrigin, true);
          return true;
        case LOGIN_EVENT:
            // Get Customer
            getBaseActivity().hideKeyboard();
            getBaseActivity().updateSlidingMenuCompletly();
            getBaseActivity().onBackPressed();
            getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            TrackerDelegator.trackLoginSuccessful(getBaseActivity(), (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY), onAutoLogin, loginOrigin, false);
            break;
       case GET_SIGNUP_FORM_EVENT:
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
            
            
//            /**
//             * TODO: REMOVE THIS, ONLY FOR TESTS
//             */
//            sendRequest(new GetFormPollHelper(), null, this);
//            Bundle b = new Bundle();
//            b.putString("Alice_Module_Checkout_Model_PollingForm[pollQuestion]", "Facebook");
//            sendRequest(new SetPollAnswerHelper(), b, this);
//            
//            //Bundle b1 = new Bundle();
//            //b1.putString("billingForm[billingAddressId]", "4040");
//            //b1.putString("billingForm[shippingAddressDifferent]", "1");
//            //b1.putString("billingForm[shippingAddressId]", "4040");
//            //sendRequest(new SetBillingAddressHelper(), b1, this);
//            //Bundle b2 = new Bundle();
//            //b2.putString("shippingForm[shippingAddressId]", "4040");
//            //sendRequest(new SetShippingAddressHelper(), b2, this);
//            
//            sendRequest(new GetDefaultBillingAddressHelper(), null, this);
//            sendRequest(new GetDefaultShippingAddressHelper(), null, this);
//            
//            return true;
//            
//        // TODO: Only for tests
//       case GET_POLL_FORM_EVENT:
//           Log.d(TAG, "RECEIVED GET_POLL_FORM_EVENT");
//           return true;
//       case SET_POLL_ANSWER_EVENT:
//           Log.d(TAG, "RECEIVED SET_POLL_ANSWER_EVENT");
//           return true;
//       case SET_BILLING_ADDRESS_EVENT:
//           Log.d(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT");
//           return true;
//       case SET_SHIPPING_ADDRESS_EVENT:
//           Log.d(TAG, "RECEIVED SET_SHIPPING_ADDRESS_EVENT");               
//           return true;
//       case GET_DEFAULT_BILLING_ADDRESS_EVENT:
//           Log.d(TAG, "RECEIVED GET_DEFAULT_BILLING_ADDRESS_EVENT");
//           return true;
//       case GET_DEFAULT_SHIPPING_ADDRESS_EVENT:
//           Log.d(TAG, "RECEIVED GET_DEFAULT_SHIPPING_ADDRESS_EVENT");
//           // Next step
//           getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
//           return true;
            
        }
        return true;
    }


    /**
     * 
     * @param bundle
     * @return
     */
    protected boolean onErrorEvent(Bundle bundle) {
        
    	if(!isVisible()){
    	    Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
    		return true;
    	}
    	
        if(getBaseActivity().handleErrorEvent(bundle)){
            Log.w(TAG, "BASE ACTIVITY HANDLE ERROR EVENT!");
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
        case LOGIN_EVENT:
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                TrackerDelegator.trackLoginFailed(onAutoLogin);
                if (onAutoLogin) {
                    // Sometimes formDataRegistry is null, so init forms
                    if (formResponse == null) triggerInitForm();
                } else {
                    // Show error
                    HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                    showErrorDialog(errors);
                    getBaseActivity().showContentContainer(false);
                }
            }
            break;
        case SET_SIGNUP_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: SET_SIGNUP_EVENT");
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY); 
                showErrorDialog(errors);
                getBaseActivity().showContentContainer(false);
            }
            break;
        case GET_SIGNUP_FORM_EVENT:
            Log.w(TAG, "ON ERRER RECEIVED: GET_SIGNUP_FORM_EVENT");
            triggerInitForm();
            break;
        default:
            break;
        }
        return true;
    }
    
    /**
     * ############# REQUESTS #############
     */
    
    /**
     * Request auto login
     */
    private void requestLogin() {
        Log.i(TAG, "TRIGGER: LOGIN EVENT");
        getBaseActivity().hideKeyboard();
        ContentValues values = loginForm.save();
        values.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        triggerLogin(values, true);
    }
    
    private void requestSignup() {
        Log.i(TAG, "TRIGGER: SIGNUP EVENT");
        getBaseActivity().hideKeyboard();
        ContentValues values = signupForm.save();
        // Get the flag for this scenario
        /**
         * TODO: Get the guest flag from form
         */
        //FormField scenarioItem = signupFormResponse.theRealFieldMapping.get("scenario");
        // DynamicFormItem scenarioItem = signupForm.getItemByKey("scenario");
        //values.put(scenarioItem.getName(), scenarioItem.getValue());
        values.put("Alice_Module_Customer_Model_RegistrationForm[scenario]", "guest");
        values.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        triggerSignup(values, true);
    }

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
     * TRIGGERS
     * @author sergiopereira
     */
    private void triggerAutoLogin(){
        Log.i(TAG, "TRIGGER: AUTO LOGIN");
        onAutoLogin = true;
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, onAutoLogin);
        triggerContentEvent(new GetLoginHelper(), bundle, this);
    }
    
    private void triggerLogin(ContentValues values, boolean saveCredentials) {
        Log.i(TAG, "TRIGGER: LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEvent(new GetLoginHelper(), bundle, this);
    }
    
    private void triggerSignup(ContentValues values, boolean saveCredentials) {
        Log.i(TAG, "TRIGGER: SIGNUP " + values.toString());
        Bundle bundle = new Bundle();
        bundle.putParcelable(SetSignupHelper.FORM_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEvent(new SetSignupHelper(), bundle, this);
    }
    
    private void triggerFacebookLogin(ContentValues values,  boolean saveCredentials){
        Log.i(TAG, "TRIGGER: FACEBOOK LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEventWithNoLoading(new GetFacebookLoginHelper(), bundle, this);
    }
    
    private boolean triggerLoginForm(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        onAutoLogin = false;
        triggerContentEvent(new GetLoginFormHelper(), null, this);
        return true;
    }
    
    private boolean triggerSignupForm(){
        Log.i(TAG, "TRIGGER: SIGNUP FORM");
        triggerContentEvent(new GetSignupFormHelper(), null, this);
        return true;
    }
    
    private void triggerInitForm(){
        Log.i(TAG, "TRIGGER: INIT FORMS");
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetInitFormHelper(), bundle, this);
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
    
    
    private void showErrorDialog(HashMap<String, List<String>> errors){
        Log.d(TAG, "SHOW LOGIN ERROR DIALOG");
        List<String> errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);

        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            
            if(getBaseActivity() != null) getBaseActivity().showContentContainer(false);
            
            dialog = DialogGenericFragment.newInstance(true, true, false,
                    getString(R.string.error_login_title),
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
        } else {
            
            /**
             * TODO: THE ERROR MUST RETURN THE MESSAGE
             */
            Toast.makeText(getBaseActivity(), "Please try with other email!", Toast.LENGTH_SHORT).show();
        }
    }
    
    
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
