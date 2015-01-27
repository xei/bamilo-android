/**
 * 
 */
package pt.rocket.view.fragments;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.CheckBox;
import pt.rocket.components.customfontviews.EditText;
import pt.rocket.components.customfontviews.FacebookTextView;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.LogOut;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormData;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.configs.GetInitFormHelper;
import pt.rocket.helpers.session.GetFacebookLoginHelper;
import pt.rocket.helpers.session.GetLoginFormHelper;
import pt.rocket.helpers.session.GetLoginHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.preferences.CustomerPreferences;
import pt.rocket.utils.InputType;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.deeplink.DeepLinkManager;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.social.FacebookHelper;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.LayoutDirection;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.facebook.FacebookAuthorizationException;
import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SessionLoginFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = LogTagHelper.create(SessionLoginFragment.class);

    private final static String FORM_ITEM_EMAIL = "email";

    private final static String FORM_ITEM_PASSWORD = "password";

    private CheckBox rememberEmailCheck;

    private View signinButton;

    private ViewGroup container;

    private EditText pass_p;

    private EditText user;

    private View forgetPass;

    private View register;

    private Form formResponse = null;

    private boolean wasAutologin = false;

    private DynamicForm dynamicForm;

    private Bundle savedInstanceState;

    private static SessionLoginFragment sLoginFragment = null;

    private FragmentType nextFragmentType;

    private UiLifecycleHelper uiHelper;

    private boolean cameFromRegister = false;
    
    private FacebookTextView mFacebookButton;
    
    /**
     * 
     * @return
     */
    public static SessionLoginFragment getInstance(Bundle bundle) {
        if (bundle != null) {
            // Initialize SessionLoginFragment with no title
            sLoginFragment = new SessionLoginFragment(0);
            sLoginFragment.nextFragmentType = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
            // Force load form if comes from deep link
            String path = bundle.getString(ConstantsIntentExtra.DEEP_LINK_TAG);
            if (path != null && path.equals(DeepLinkManager.TAG))
                sLoginFragment.formResponse = null;
        } else {
            // Initialize SessionLoginFragment with proper title
            sLoginFragment = new SessionLoginFragment(R.string.login_label);
        }
        return sLoginFragment;
    }

    /**
     * Empty constructor
     */
    public SessionLoginFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LoginOut,
                R.layout.login,
                0,
                KeyboardState.ADJUST_CONTENT);
    }

    public SessionLoginFragment(int titleResId) {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LoginOut,
                R.layout.login,
                titleResId,
                KeyboardState.ADJUST_CONTENT);
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
        // String appId = getBaseActivity().getResources().getString(R.string.facebook_app_id);
        uiHelper = new UiLifecycleHelper(getBaseActivity(), callback/*-, appId*/);
        uiHelper.onCreate(savedInstanceState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        rememberEmailCheck = (CheckBox) view.findViewById(R.id.login_remember_user_email);
        signinButton = view.findViewById(R.id.middle_login_button_signin);
        forgetPass = view.findViewById(R.id.middle_login_link_fgtpassword);
        register = view.findViewById(R.id.middle_login_link_register);
        container = (ViewGroup) view.findViewById(R.id.form_container);
        // Get Facebook
        mFacebookButton = (FacebookTextView) view.findViewById(R.id.authButton);
        // Set Facebook
        FacebookHelper.showOrHideFacebookButton(this, mFacebookButton);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
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
        TrackerDelegator.trackPage(TrackingPage.LOGIN_SIGNUP, getLoadTime(), false);

        /*-String appId = getBaseActivity().getResources().getString(R.string.facebook_app_id);
        uiHelper.setJumiaAppId(appId);*/
        uiHelper.onResume();

        /**
         * Force input form align to left Restore is performed on BaseFragment.onPause()
         */
        forceInputAlignToLeft();

        //Validate is service is available
        if(JumiaApplication.mIsBound){
          
            // Valdiate form
            if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
                Log.d(TAG, "FORM: TRY AUTO LOGIN");

                triggerAutoLogin();
            } else if (formResponse != null) {
                Log.d(TAG, "FORM ISN'T NULL");
                loadForm(formResponse);
                cameFromRegister = false;
            } else {
                Log.d(TAG, "FORM IS NULL");

                // Clean the Facebook Session
                FacebookHelper.cleanFacebookSession();

                HashMap<String, FormData> formDataRegistry = JumiaApplication.INSTANCE.getFormDataRegistry();
                if (formDataRegistry == null || formDataRegistry.size() == 0) {
                    triggerInitForm();
                } else {
                    triggerLoginForm();
                }
                cameFromRegister = false;
            }
            
        } else {
            showFragmentRetry(this);
        }
        
        setLoginBottomLayout();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "code1facebook onActivityResult");
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
        if (!JumiaApplication.INSTANCE.isLoggedIn()) {
            if (null != dynamicForm) {
                Iterator<DynamicFormItem> iterator = dynamicForm.iterator();
                while (iterator.hasNext()) {
                    DynamicFormItem item = iterator.next();
                    item.saveState(outState);
                }
                savedInstanceState = outState;
            }
            // super.onSaveInstanceState(outState);
            uiHelper.onSaveInstanceState(outState);
        }
    }

    /**
     * Facebook Methods
     */

    /**
     * Verify facebook session state
     * 
     * @param session
     * @param state
     * @param exception
     */
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Exception handling for no network error
        if(exception instanceof FacebookAuthorizationException){
            createNoNetworkDialog();
            return;
        }
        
        if (state.isOpened()) {
            showFragmentLoading();
            // make request to the /me API
            Request request = Request.newMeRequest(
                    session,
                    new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, com.facebook.Response response) {
                            if (user != null) {
                                requestFacebookLogin(user);
                            }
                        }
                    });
            Request.executeBatchAsync(request);
        } else if (state.isClosed()) {
            Log.w(TAG, "USER Logged out!");
        }
    }

    /**
     * Listener for Session state changes
     */
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    /**
     * No network dialog for facebook exception handling
     */
    private void createNoNetworkDialog(){
        dialog = DialogGenericFragment.createNoNetworkDialog(getActivity(), 
                new OnClickListener() { 
                    @Override
                    public void onClick(View v) {                      
                        mFacebookButton.performClick();
                        if(dialog != null) dialog.dismiss();
                        
                    }
                }, new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        if(dialog != null) dialog.dismiss();
                        
                    }
                }, false);
         try {
             dialog.show(getActivity().getSupportFragmentManager(), null);
         } catch (Exception e) {
             
         }
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
        if (container != null) {
            try {
                container.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        uiHelper.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        formResponse = null;
        onCommonClickListener = null;
        uiHelper.onDestroy();
    }

    /**
     * Set listeners
     */
    private void setLoginBottomLayout() {
        signinButton.setOnClickListener(onCommonClickListener);
        forgetPass.setOnClickListener(onCommonClickListener);
        register.setOnClickListener(onCommonClickListener);
    }

    /**
     * Common Listener
     */
    OnClickListener onCommonClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.middle_login_button_signin) {
                // Log.d(TAG, "CLICKED ON SIGNIN");
                if (null != dynamicForm) {
                    if (dynamicForm.validate()) {
                        requestLogin();
                    }
                    // Tracking login failed
                    else TrackerDelegator.trackLoginFailed(TrackerDelegator.ISNT_AUTO_LOGIN, GTMValues.LOGIN, GTMValues.EMAILAUTH);
                } else {
                    triggerLoginForm();
                }

            }
            else if (id == R.id.middle_login_link_fgtpassword) {
                getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD,
                        FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
            else if (id == R.id.middle_login_link_register) {
                cameFromRegister = true;
                getBaseActivity().onSwitchFragment(FragmentType.REGISTER,
                        FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
        }
    };

    /**
     * Request auto login
     */
    private void requestLogin() {
        Log.d(TAG, "requestLogin: triggerEvent LogInEvent");
        getBaseActivity().hideKeyboard();
        ContentValues values = dynamicForm.save();
        values.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        triggerLogin(values, true);
    }

    private void requestFacebookLogin(GraphUser user) {
        Log.d(TAG, "requestLogin: triggerEvent LogInEvent");
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
     * This method defines the click behavior of the edit texts from the dynamic form, allowing the
     * to login only when the form is completely filled.
     */
    public void setFormClickDetails() {
        DynamicFormItem emailItem = dynamicForm.getItemByKey(FORM_ITEM_EMAIL);
        if (emailItem == null) {
            return;
        }
        user = (EditText) emailItem.getEditControl();
        //#RTL
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            user.setLayoutDirection(LayoutDirection.LOCALE);
        }
        user.setId(21);

        DynamicFormItem passwordItem = dynamicForm.getItemByKey(FORM_ITEM_PASSWORD);
        if (passwordItem == null) {
            return;
        }
        pass_p = (EditText) passwordItem.getEditControl();
        pass_p.setId(22);
        //#RTL
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            pass_p.setLayoutDirection(LayoutDirection.LOCALE);
        }
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            super.handleSuccessEvent(bundle);
        } else {
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

        switch (eventType) {
        case INIT_FORMS:
            triggerLoginForm();
            return true;
        case FACEBOOK_LOGIN_EVENT:
            JumiaApplication.INSTANCE.setLoggedIn(true);
            Log.d(TAG, "facebookloginCompletedEvent : success");
            // Get Customer
            baseActivity.hideKeyboard();
            baseActivity.updateSlidingMenuCompletly();

            // NullPointerException on orientation change
            if (baseActivity != null) {
                Customer customer = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                JumiaApplication.CUSTOMER = customer;

                Bundle params = new Bundle();
                params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
                params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, wasAutologin);
                params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, true);
                params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.LOGIN);
                
                // Validate the next step
                if (nextFragmentType != null) {
                    FragmentController.getInstance().popLastEntry(FragmentType.LOGIN.toString());
                    Bundle agrs = new Bundle();
                    agrs.putBoolean(TrackerDelegator.LOGIN_KEY, true);
                    getBaseActivity().onSwitchFragment(nextFragmentType, agrs, FragmentController.ADD_TO_BACK_STACK);
                } else {
                    getBaseActivity().onBackPressed();
                }

                TrackerDelegator.trackLoginSuccessful(params);
            }

          
            return true;

        case LOGIN_EVENT:
            Log.d(TAG, "ON SUCCESS EVENT: LOGIN_EVENT");
            JumiaApplication.INSTANCE.setLoggedIn(true);
            // Get Customer
            baseActivity.hideKeyboard();
            baseActivity.updateSlidingMenuCompletly();

            // NullPointerException on orientation change
            if (baseActivity != null && !cameFromRegister) {
                Customer customer = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                JumiaApplication.CUSTOMER = customer;

                Bundle params = new Bundle();
                params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
                params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, wasAutologin);
                params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, false);
                params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.LOGIN);
                TrackerDelegator.trackLoginSuccessful(params);

                // Persist user email or empty that value after successfull login
                CustomerPreferences.setRememberedEmail(baseActivity,
                        rememberEmailCheck.isChecked() ? customer.getEmail() : null);
            }

            cameFromRegister = false;
            // Validate the next step
            if (nextFragmentType != null && baseActivity != null) {
                Log.d(TAG, "NEXT STEP: " + nextFragmentType.toString());
                FragmentController.getInstance().popLastEntry(FragmentType.LOGIN.toString());
                Bundle agrs = new Bundle();
                agrs.putBoolean(TrackerDelegator.LOGIN_KEY, true);
                baseActivity.onSwitchFragment(nextFragmentType, agrs,
                        FragmentController.ADD_TO_BACK_STACK);
            } else {
                Log.d(TAG, "NEXT STEP: BACK");
                baseActivity.onBackPressed();
            }

            return true;
        case GET_LOGIN_FORM_EVENT:
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            if (form == null) {
                dialog = DialogGenericFragment.createServerErrorDialog(baseActivity,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showFragmentLoading();
                                triggerLoginForm();
                                dialog.dismiss();
                            }
                        }, false);
                dialog.show(baseActivity.getSupportFragmentManager(), null);
                return false;
            }

            Log.d(TAG, "Form Loaded");
            loadForm(form);
            formResponse = form;
        default:
            break;
        }
        return true;
    }

    /**
     * Load the dynamic form
     * 
     * @param form
     */
    private void loadForm(Form form) {
        // Set title when Login form is displayed to allow access to other fragments
        if (null != sLoginFragment && sLoginFragment.nextFragmentType != null) {
            
            //#specific_shop
            if(getResources().getBoolean(R.bool.is_daraz_specific) ||
                    getResources().getBoolean(R.bool.is_shop_specific) ||
                    getResources().getBoolean(R.bool.is_bamilo_specific) ){
                getBaseActivity().hideActionBarTitle();
                getBaseActivity().setTitle(R.string.login_label);
            } else {
                getBaseActivity().setActionBarTitle(R.string.login_label);
            }
        }

        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.LOGIN_FORM,
                getBaseActivity(), form);
        try {
            container.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        
        //#RTL
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            container.setLayoutDirection(LayoutDirection.LOCALE);
        }
        
        container.addView(dynamicForm.getContainer());
        setFormClickDetails();

        boolean fillEmail = false;
        String rememberedEmail = CustomerPreferences.getRememberedEmail(getBaseActivity());
        if (!TextUtils.isEmpty(rememberedEmail)) {
            fillEmail = true;
        }

        // Show save state
        if (null != this.savedInstanceState && null != dynamicForm) {
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                item.loadState(savedInstanceState);

                if (fillEmail && InputType.email.equals(item.getType())) {
                    ((EditText) item.getEditControl()).setText(rememberedEmail);
                }
            }
        } else if (fillEmail) {
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();

                if (InputType.email.equals(item.getType())) {
                    ((EditText) item.getEditControl()).setText(rememberedEmail);
                }
            }
        }
        container.refreshDrawableState();
        showFragmentContentContainer();
    }

    protected boolean onErrorEvent(Bundle bundle) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        if (super.handleErrorEvent(bundle)) {
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        if (eventType == EventType.GET_LOGIN_FORM_EVENT) {
            if (errorCode == ErrorCode.UNKNOWN_ERROR && null == dynamicForm) {
                restartAllFragments();
                return true;
            }
        } else if (eventType == EventType.LOGIN_EVENT) {
            clearCredentials();

            TrackerDelegator.trackLoginFailed(wasAutologin, GTMValues.LOGIN, GTMValues.EMAILAUTH);

            if (errorCode == ErrorCode.REQUEST_ERROR) {

                if (wasAutologin) {
                    if (formResponse == null) {
                        // Sometimes formDataRegistry is null, so init forms
                        triggerInitForm();
                        triggerLoginForm();
                    } else {
                        LogOut.performLogOut(new WeakReference<Activity>(getBaseActivity()));
                    }
                } else {
                    Log.d(TAG, "SHOW DIALOG");
                    HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle
                            .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                    List<String> errorMessages = null;
                    if (errors != null) {
                        errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);
                    }
                    if (errors != null && errorMessages != null && errorMessages.size() > 0) {
                        showFragmentContentContainer();
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
                    }
                }
                return true;
            }
        } else if(eventType == EventType.FACEBOOK_LOGIN_EVENT){
            Log.i(TAG, "SHOW ERROR MESSAGE FOR FACEBOOK_LOGIN_EVENT");
            // Clear credentials case auto login failed
            clearCredentials();
            // Clean the Facebook Session
            FacebookHelper.cleanFacebookSession();
            // Show alert
            
            TrackerDelegator.trackLoginFailed(wasAutologin, GTMValues.LOGIN, GTMValues.FACEBOOK);
            
            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            List<String> errorMessages = null;
            if (errors != null) {
                Log.i(TAG, "ERRROS: " + errors.toString());    
                errorMessages = (List<String>) errors.get(RestConstants.JSON_ERROR_TAG);
            }
            
            if (errors != null && errorMessages != null && errorMessages.size() > 0) {
                showFragmentContentContainer();
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
            }
        }
        return false;
    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     */
    private void triggerAutoLogin() {
        wasAutologin = true;
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, wasAutologin);
        triggerContentEvent(new GetLoginHelper(), bundle, mCallBack);
    }

    private void triggerLogin(ContentValues values, boolean saveCredentials) {
        wasAutologin = false;
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEvent(new GetLoginHelper(), bundle, mCallBack);
    }

    private void triggerFacebookLogin(ContentValues values, boolean saveCredentials) {
        wasAutologin = false;
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, saveCredentials);
        triggerContentEventWithNoLoading(new GetFacebookLoginHelper(), bundle, mCallBack);
    }

    private void triggerLoginForm() {
        wasAutologin = false;
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetLoginFormHelper(), bundle, mCallBack);
    }

    private void triggerInitForm() {
        wasAutologin = false;
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetInitFormHelper(), bundle, mCallBack);
    }

    /**
     * CALLBACK
     * 
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fragment_root_retry_button) {
            Bundle bundle = new Bundle();
            getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);

        }
    }
}
