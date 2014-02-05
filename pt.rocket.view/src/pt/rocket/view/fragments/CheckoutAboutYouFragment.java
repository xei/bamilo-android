/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
import pt.rocket.helpers.GetFacebookLoginHelper;
import pt.rocket.helpers.GetInitFormHelper;
import pt.rocket.helpers.GetLoginFormHelper;
import pt.rocket.helpers.GetLoginHelper;
import pt.rocket.helpers.address.GetDefaultBillingAddressHelper;
import pt.rocket.helpers.address.GetDefaultShippingAddressHelper;
import pt.rocket.helpers.address.SetBillingAddressHelper;
import pt.rocket.helpers.address.SetShippingAddressHelper;
import pt.rocket.helpers.checkout.GetFormPollHelper;
import pt.rocket.helpers.checkout.SetPollAnswerHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.JumiaApplication;
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
 * FIXME: Waiting for NAFAMZ-5272 MOBILE API - allow GUEST CHECKOUT on the Native Checkout 
 * 
 * 
 * @author sergiopereira
 * 
 */
public class CheckoutAboutYouFragment extends BaseFragment implements OnClickListener, GraphUserCallback, StatusCallback, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CheckoutAboutYouFragment.class);

    private final static String FORM_ITEM_EMAIL = "email";

    private final static String FORM_ITEM_PASSWORD = "password";

    private EditText pass_p;

    private EditText user;

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

    private EditText signupEmail;

    private boolean onAutoLogin = false;
    
    private static final String PERMISSION_EMAIL = "email";
    
    /**
     * 
     * @return
     */
    public static CheckoutAboutYouFragment getInstance(Bundle bundle) {
        // if (loginFragment == null)
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
        super(EnumSet.of(EventType.GET_LOGIN_FORM_EVENT), 
                EnumSet.of(EventType.LOGIN_EVENT, EventType.FACEBOOK_LOGIN_EVENT),
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
        view.findViewById(R.id.checkout_login_toogle).setOnClickListener(this);
        // Login main
        loginMainContainer = view.findViewById(R.id.checkout_login_form_main);
        // Login form
        loginFormContainer = (ViewGroup) view.findViewById(R.id.checkout_login_form_container);
        // Login button
        view.findViewById(R.id.checkout_login_form_button_enter).setOnClickListener(this);
        // Forget button
        view.findViewById(R.id.checkout_login_form_button_password).setOnClickListener(this);
        
        // Sign toggle
        view.findViewById(R.id.checkout_signup_toogle).setOnClickListener(this);
        // Sign main
        signupMainContainer = view.findViewById(R.id.checkout_signup_form_main);
        // Sign form
        signupEmail = (EditText) view.findViewById(R.id.checkout_signup_form_email);
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
        } else if(formResponse != null){
            loadForm(formResponse);
        } else {
            Log.d(TAG, "TRIGGER: LOGIN FORM");
            triggerLoginForm();
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
        String appId = getActivity().getResources().getString(R.string.app_id);
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
    
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Login toggle
        if(id == R.id.checkout_login_toogle) onClickLoginToogle();
        // Login button
        else if(id == R.id.checkout_login_form_button_enter) onClickLoginButton();
        // Forgot Password
        else if(id == R.id.checkout_login_form_button_password) onClickForgotPassword();
        // Sign toggle
        else if(id == R.id.checkout_signup_toogle) onClickSignupToogle();
        // Sign button
        else if(id == R.id.checkout_signup_form_button_enter) onClickSignupButton();
        // Facebook button
        else if(id == R.id.checkout_login_form_button_facebook) onClickFacebookButton();
        // Facebook button
        else if(id == R.id.checkout_signup_form_button_facebook) onClickFacebookButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    private void onClickLoginToogle() {
        Log.i(TAG, "ON CLICK: LOGIN TOOGLE");
        // Validate view
        if(loginMainContainer != null){
            // Validate visibility
            if(loginMainContainer.getVisibility() == View.VISIBLE)
                loginMainContainer.setVisibility(View.GONE);
            else
                loginMainContainer.setVisibility(View.VISIBLE);
        }
    }
    
    private void onClickSignupToogle() {
        Log.i(TAG, "ON CLICK: SIGNUP TOOGLE");
        // Validate view
        if(signupMainContainer != null){
            // Validate visibility
            if(signupMainContainer.getVisibility() == View.VISIBLE)
                signupMainContainer.setVisibility(View.GONE);
            else
                signupMainContainer.setVisibility(View.VISIBLE);
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
        // TODO:
        // Validate form
        // Show create address
        getBaseActivity().onSwitchFragment(FragmentType.CREATE_ADDRESS, null, FragmentController.ADD_TO_BACK_STACK);
        
    }
    
    private void onClickForgotPassword() {
        Log.i(TAG, "ON CLICK: FORGOT PASS");
        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }
    
    private void onClickFacebookButton() {
        Log.i(TAG, "ON CLICK: FACEBOOK");
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
     * This method defines the click behavior of the edit texts from the dynamic form, allowing the
     * to login only when the form is completely filled.
     */
    public void setFormClickDetails() {

        DynamicFormItem emailItem = loginForm.getItemByKey(FORM_ITEM_EMAIL);
        if (emailItem == null) {
            return;
        }
        user = (EditText) emailItem.getEditControl();
        user.setId(21);

        DynamicFormItem passwordItem = loginForm.getItemByKey(FORM_ITEM_PASSWORD);
        if (passwordItem == null) {
            return;
        }
        pass_p = (EditText) passwordItem.getEditControl();
        pass_p.setId(22);
    }


    protected boolean onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");
        
        if(getBaseActivity() != null){
            getBaseActivity().handleSuccessEvent(bundle);
        } else {
            return true;
        }
         
        // Validate fragment visibility
        if(!isVisible()){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case INIT_FORMS:
            triggerLoginForm();
            return true;
            
//        case FACEBOOK_LOGIN_EVENT:
//            Log.d(TAG, "facebookloginCompletedEvent : success");
//            // Get Customer
//            getBaseActivity().hideKeyboard();
//            getBaseActivity().updateSlidingMenuCompletly();
//            getBaseActivity().onBackPressed();
//            if(nextFragmentType != null) 
//                getBaseActivity().onSwitchFragment(nextFragmentType, null, true);
//            TrackerDelegator.trackLoginSuccessful(getBaseActivity(), (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY), onAutoLogin, loginOrigin, true);
//            return true;
            
        case FACEBOOK_LOGIN_EVENT:
        case LOGIN_EVENT:
            // Get Customer
            getBaseActivity().hideKeyboard();
            getBaseActivity().updateSlidingMenuCompletly();
//            getBaseActivity().onBackPressed();
//            if(nextFragmentType != null) 
//                getBaseActivity().onSwitchFragment(nextFragmentType, null, true);
//            TrackerDelegator.trackLoginSuccessful(getBaseActivity(), (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY), onAutoLogin, loginOrigin, false);
            
            
            /**
             * TODO: REMOVE THIS, ONLY FOR TESTS
             */
            //sendRequest(new GetFormPollHelper(), null, this);    // DONE
            //Bundle b = new Bundle();
            //b.putString("Alice_Module_Checkout_Model_PollingForm[id_poll]", "");
            //b.putString("Alice_Module_Checkout_Model_PollingForm[pollRevision]", "");
            //b.putString("Alice_Module_Checkout_Model_PollingForm[pollQuestion]", "Facebook");
            //sendRequest(new SetPollAnswerHelper(), b, this);
            
            //Bundle b1 = new Bundle();
            //b1.putString("billingForm[billingAddressId]", "4040");
            //b1.putString("billingForm[shippingAddressDifferent]", "1");
            //b1.putString("billingForm[shippingAddressId]", "4040");
            //sendRequest(new SetBillingAddressHelper(), b1, this);
            //Bundle b2 = new Bundle();
            //b2.putString("shippingForm[shippingAddressId]", "4040");
            //sendRequest(new SetShippingAddressHelper(), b2, this);
            
            sendRequest(new GetDefaultBillingAddressHelper(), null, this);
            sendRequest(new GetDefaultShippingAddressHelper(), null, this);
            
            return true;
            
            // TODO: Only for tests
           case GET_POLL_FORM_EVENT:
               Log.d(TAG, "RECEIVED GET_POLL_FORM_EVENT");
               return true;
           case SEND_POLL_ANSWER_EVENT:
               Log.d(TAG, "RECEIVED SEND_POLL_ANSWER_EVENT");
               return true;
           case SET_BILLING_ADDRESS_EVENT:
               Log.d(TAG, "RECEIVED SET_BILLING_ADDRESS_EVENT");
               return true;
           case SET_SHIPPING_ADDRESS_EVENT:
               Log.d(TAG, "RECEIVED SET_SHIPPING_ADDRESS_EVENT");               
               return true;
           case GET_DEFAULT_BILLING_ADDRESS_EVENT:
               Log.d(TAG, "RECEIVED GET_DEFAULT_BILLING_ADDRESS_EVENT");
               return true;
           case GET_DEFAULT_SHIPPING_ADDRESS_EVENT:
               Log.d(TAG, "RECEIVED GET_DEFAULT_SHIPPING_ADDRESS_EVENT");
               // Next step
               getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
               return true;

            
        case GET_LOGIN_FORM_EVENT:
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            if (form == null) {
                dialog = DialogGenericFragment.createServerErrorDialog(getActivity(),
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                ((BaseActivity) getActivity()).showLoading(false);
                                triggerLoginForm();
                                dialog.dismiss();
                            }
                        }, false);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                return false;
            }

            // Save and load form
            loadForm(form);
            this.formResponse = form;
            // Clean FACEBOOK session
            cleanFacebookSession();
        }
        return true;
    }

    /**
     * Load the dynamic form
     * 
     * @param form
     */
    private void loadForm(Form form) {
        Log.i(TAG, "LOAD FORM: " + form.name);
        loginForm = FormFactory.getSingleton().CreateForm(FormConstants.LOGIN_FORM, getActivity(), form);
        loginFormContainer.removeAllViews();
        loginFormContainer.addView(loginForm.getContainer());
        setFormClickDetails();

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
    }


    protected boolean onErrorEvent(Bundle bundle) {
    	if(!isVisible()){
    		return true;
    	}
        if(getBaseActivity().handleErrorEvent(bundle)){
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        if (eventType == EventType.GET_LOGIN_FORM_EVENT) {
            if (errorCode == ErrorCode.UNKNOWN_ERROR && null == loginForm) {
                restartAllFragments();
                return true;
            }
        } else if (eventType == EventType.LOGIN_EVENT) {
            // Validate fragment visibility
            if(!isVisible()){
                Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
                return true;
            }
            
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                TrackerDelegator.trackLoginFailed(onAutoLogin);
                if (onAutoLogin) {
                    if (formResponse == null) {
                        // Sometimes formDataRegistry is null, so init forms
                        triggerInitForm();
                    }
                } else {
                    
                    Log.d(TAG, "SHOW DIALOG");
                    HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                    List<String> errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);

                    if (errors != null && errorMessages.size() > 0) {
                        
                        if(getActivity() != null)
                            ((BaseActivity) getActivity()).showContentContainer(false);
                        
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
                        dialog.show(getActivity().getSupportFragmentManager(), null);
                    }
                }
                return true;
            }
        }
        return false;
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
    
    private void triggerFacebookLogin(ContentValues values,  boolean saveCredentials){
        Log.i(TAG, "TRIGGER: FACEBOOK LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEventWithNoLoading(new GetFacebookLoginHelper(), bundle, this);
    }
    
    private void triggerLoginForm(){
        Log.i(TAG, "TRIGGER: LOGIN FORM");
        onAutoLogin = false;
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetLoginFormHelper(), bundle, this);
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
        
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }

}
