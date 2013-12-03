/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.FacebookLogInEvent;
import pt.rocket.framework.event.events.LogInEvent;
import pt.rocket.framework.forms.Form;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.MainFragmentActivity;
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
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SessionLoginFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(SessionLoginFragment.class);

    private final static String FORM_ITEM_EMAIL = "email";

    private final static String FORM_ITEM_PASSWORD = "password";

    private MainFragmentActivity parentActivity;

    private View signinButton;

    private ViewGroup container;

    private EditText pass_p;

    private EditText user;

    private View forgetPass;

    private View register;

    private Form formResponse = null;

    private boolean wasAutologin = false;

    private boolean autoLogin = true;

    private DynamicForm dynamicForm;

    private Bundle savedInstanceState;

    private static SessionLoginFragment loginFragment = null;

    private FragmentType nextFragmentType;
    
    private UiLifecycleHelper uiHelper;
    
    private String loginOrigin = "";
    
    /**
     * 
     * @return
     */
    public static SessionLoginFragment getInstance(Bundle bundle) {
        //if (loginFragment == null)
            loginFragment = new SessionLoginFragment();
            
            if(bundle != null){
                loginFragment.nextFragmentType  = (FragmentType) bundle.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
                loginFragment.loginOrigin = bundle.getString(ConstantsIntentExtra.LOGIN_ORIGIN);
            }
        return loginFragment;
    }

    /**
     * Empty constructor
     */
    public SessionLoginFragment() {
        super(EnumSet.of(EventType.GET_LOGIN_FORM_EVENT), 
                EnumSet.of(EventType.LOGIN_EVENT, EventType.FACEBOOK_LOGIN_EVENT),
                EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.LoginOut, 
                R.string.login_title);
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
        // Auto login
        wasAutologin = false;   
        // Auto login
        autoLogin = true;
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
        String appId = getActivity().getResources().getString(R.string.app_id);
        uiHelper = new UiLifecycleHelper(getActivity(), callback, appId);
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
        View view = inflater.inflate(R.layout.login, viewGroup, false);
        signinButton = view.findViewById(R.id.middle_login_button_signin);
        forgetPass = view.findViewById(R.id.middle_login_link_fgtpassword);
        register = view.findViewById(R.id.middle_login_link_register);
        container = (ViewGroup) view.findViewById(R.id.form_container);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        authButton.setReadPermissions(Arrays.asList("email"));
        return view;
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
        String appId = getActivity().getResources().getString(R.string.app_id);
        uiHelper.setJumiaAppId(appId);
        uiHelper.onResume();
        Log.i(TAG, "ON RESUME");
        
        // Valdiate form
        if(ServiceManager.SERVICES.get(CustomerAccountService.class).hasCredentials()) {
            Log.d(TAG, "FORM: TRY AUTO LOGIN");
            triggerContentEvent(LogInEvent.TRY_AUTO_LOGIN);
        } else if (formResponse != null) {
            Log.d(TAG, "FORM ISN'T NULL");
            loadForm(formResponse);
        } else {
            Log.d(TAG, "FORM IS NULL");
            // triggerContentEvent(LogInEvent.TRY_AUTO_LOGIN);
            triggerContentEvent(EventType.GET_LOGIN_FORM_EVENT);
        }

        setLoginBottomLayout();

    }

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
        if (null != dynamicForm) {
            Iterator<DynamicFormItem> iterator = dynamicForm.iterator();
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
        if (state.isOpened()) {
            ((BaseActivity) getActivity()).showLoading();
            // make request to the /me API
            Request request = Request.newMeRequest(
                    session,
                    new Request.GraphUserCallback()
                    {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, com.facebook.Response response)
                        {
                            if (user != null)
                            {   
                                requestFacebookLogin(user);
                            }
                        }
                    }
                    );

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

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
        ((BaseActivity) getActivity()).hideKeyboard();
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
        if (container != null)
            container.removeAllViews();

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
//                Log.d(TAG, "CLICKED ON SIGNIN");
                if ( null != dynamicForm ) {
                    if (dynamicForm.validate())
                        requestLogin();
                } else {
                    triggerContentEvent(EventType.GET_LOGIN_FORM_EVENT);                    
                }

            }
            else if (id == R.id.middle_login_link_fgtpassword) {
                ((MainFragmentActivity) getActivity()).onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
            }
            else if (id == R.id.middle_login_link_register) {
                ((MainFragmentActivity) getActivity()).onSwitchFragment(FragmentType.REGISTER, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
                
            }
        }
    };

    /**
     * Request auto login
     */
    private void requestLogin() {
        Log.d(TAG, "requestLogin: triggerEvent LogInEvent");
        //
        ((BaseActivity) getActivity()).hideKeyboard();
        //
        ContentValues values = dynamicForm.save();
        // if ( autologinCheckBox.isChecked()) {
        values.put(CustomerAccountService.INTERNAL_AUTOLOGIN_FLAG, true);
        // }

        triggerContentEvent(new LogInEvent(values));
        wasAutologin = false;
		autoLogin = false;
    }
    
    private void requestFacebookLogin(GraphUser user) {
        Log.d(TAG, "requestLogin: triggerEvent LogInEvent");
        ContentValues values = new ContentValues();
        
        values.put("email", (String) user.getProperty("email"));
        values.put("first_name", user.getFirstName());
        values.put("last_name", user.getLastName());
        values.put("birthday", user.getBirthday());
        Log.i(TAG, "code3 : "+user.getBirthday());
        values.put("gender", (String) user.getProperty("gender"));
        values.put(CustomerAccountService.INTERNAL_AUTOLOGIN_FLAG, true);
        // }

        triggerContentEvent(new FacebookLogInEvent(values));
        wasAutologin = false;
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
        user.setId(21);

        DynamicFormItem passwordItem = dynamicForm.getItemByKey(FORM_ITEM_PASSWORD);
        if (passwordItem == null) {
            return;
        }
        pass_p = (EditText) passwordItem.getEditControl();
        pass_p.setId(22);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.view.fragments.MyFragment#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent
     * )
     */
    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        Log.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if(!isVisible()){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            EventManager.getSingleton().removeResponseListener(this, EnumSet.of(EventType.GET_LOGIN_FORM_EVENT, EventType.LOGIN_EVENT));
            return true;
        }

        switch (event.type) {

        case FACEBOOK_LOGIN_EVENT:
            Log.d(TAG, "facebookloginCompletedEvent :" + event.getSuccess());
            // Get Customer
            ((BaseActivity) getActivity()).hideKeyboard();
            ((BaseActivity) getActivity()).updateSlidingMenuCompletly();
            
            ((BaseActivity) getActivity()).onBackPressed();
            if(nextFragmentType != null && getActivity() != null){
                ((BaseActivity) getActivity()).onSwitchFragment(nextFragmentType, null, true);
            }
            // NullPointerException on orientation change
            if(getActivity() != null)
                TrackerDelegator.trackLoginSuccessful(getActivity(), (Customer) event.result,
                    wasAutologin, loginOrigin, true);
            
            wasAutologin = false;
            return true;
        
        case LOGIN_EVENT:
            Log.d(TAG, "loginCompletedEvent :" + event.getSuccess());
            // Get Customer
            ((BaseActivity) getActivity()).hideKeyboard();
            ((BaseActivity) getActivity()).updateSlidingMenuCompletly();
            // Switch to next fragment
            getActivity().onBackPressed();
            if(nextFragmentType != null && getActivity() != null){
                ((BaseActivity) getActivity()).onSwitchFragment(nextFragmentType, null, true);
            }
            // NullPointerException on orientation change
            if(getActivity() != null)
                TrackerDelegator.trackLoginSuccessful(getActivity(), (Customer) event.result, wasAutologin, loginOrigin, false);
            
            wasAutologin = false;
            
            return true;

        case GET_LOGIN_FORM_EVENT:
            Form form = (Form) event.result;
            if (form == null) {
                dialog = DialogGenericFragment.createServerErrorDialog(getActivity(),
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                ((BaseActivity) getActivity()).showLoading();
                                EventManager.getSingleton().triggerRequestEvent(event.request);
                                dialog.dismiss();
                            }
                        }, false);
                dialog.show(getActivity().getSupportFragmentManager(), null);
                return false;
            }

            Log.d(TAG, "Form Loaded");
            loadForm(form);
            this.formResponse = form;
        }
        return true;
    }

    /**
     * Load the dynamic form
     * 
     * @param form
     */
    private void loadForm(Form form) {
    
        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.LOGIN_FORM,
                getActivity(), form);
        container.removeAllViews();
        container.addView(dynamicForm.getContainer());
        setFormClickDetails();

        // Show save state
        if (null != this.savedInstanceState && null != dynamicForm) {
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                item.loadState(savedInstanceState);
            }
        }
        container.refreshDrawableState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.view.fragments.MyFragment#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        
        Log.d(TAG, "ON ERROR EVENT: " + event.getType().toString() + " " + event.errorCode);
        
        if (event.getType() == EventType.GET_LOGIN_FORM_EVENT) {
            if (event.errorCode == ErrorCode.UNKNOWN_ERROR && null == dynamicForm) {
                restartAllFragments();
                return true;
            }
        } else if (event.getType() == EventType.LOGIN_EVENT) {
            // Validate fragment visibility
            if(!isVisible()){
                Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
                return true;
            }
            
            if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                TrackerDelegator.trackLoginFailed(wasAutologin);
                wasAutologin = false;
                if (autoLogin) {
                    autoLogin = false;
                    if (formResponse == null) {
                        // Sometimes formDataRegistry is null, so init forms
                        triggerContentEvent(EventType.INIT_FORMS);
                        triggerContentEvent(EventType.GET_LOGIN_FORM_EVENT);
                    }
                } 
                else {
                    
                    Log.d(TAG, "SHOW DIALOG");
                    
                    List<String> errorMessages = event.errorMessages.get(RestConstants.JSON_ERROR_TAG);
                    if (errorMessages != null && (errorMessages.contains(Errors.CODE_LOGIN_FAILED) || errorMessages.contains(Errors.CODE_LOGIN_CHECK_PASSWORD))) {
                        
                        if(getActivity() != null)
                            ((BaseActivity) getActivity()).showContentContainer();
                        
                        dialog = DialogGenericFragment.newInstance(true, true, false,
                                getString(R.string.error_login_title),
                                getString(R.string.error_login_check_text),
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

}
