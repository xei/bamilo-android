/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import pt.rocket.constants.FormConstants;
import pt.rocket.factories.FormFactory;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.LogInEvent;
import pt.rocket.framework.forms.Form;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import pt.rocket.view.SessionFragmentActivity;
import android.app.Activity;
import android.content.ContentValues;
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
public class LoginFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(LoginFragment.class);

    private final static String FORM_ITEM_EMAIL = "email";

    private final static String FORM_ITEM_PASSWORD = "password";

    private SessionFragmentActivity parentActivity;

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

    private static LoginFragment loginFragment = null;
    
    private String loginOrigin = "";

    /**
     * 
     * @return
     */
    public static LoginFragment getInstance(String origin) {
        //if (loginFragment == null)
            loginFragment = new LoginFragment(origin);
        return loginFragment;
    }

    /**
     * constructor
     */
    public LoginFragment(String origin) {
        super(EnumSet.of(EventType.GET_LOGIN_FORM_EVENT), EnumSet.of(EventType.LOGIN_EVENT));
        loginOrigin = origin;
        Log.d(TAG, "CONSTRUCTOR");
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
        parentActivity = (SessionFragmentActivity) activity;
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
        wasAutologin = true;       
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
        parentActivity.updateActivityHeader(NavigationAction.LoginOut, R.string.login_title);
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

        if (formResponse != null) {
            Log.d(TAG, "FORM ISN'T NULL");
            loadForm(formResponse);
        } else {
            Log.d(TAG, "FORM IS NULL");
            triggerContentEvent(LogInEvent.TRY_AUTO_LOGIN);
        }
        
        setLoginBottomLayout();
    }

    /*
     * (non-Javadoc)
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
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        formResponse = null;
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
                Log.d(TAG, "CLICKED ON SIGNIN");
                if (dynamicForm.validate())
                    requestLogin();
            }
            else if (id == R.id.middle_login_link_fgtpassword) {
                parentActivity.onSwitchFragment(FragmentType.FORGOT_PASSWORD, true);
            }
            else if (id == R.id.middle_login_link_register) {
                parentActivity.onSwitchFragment(FragmentType.REGISTER, true);
            }
        }
    };

    /**
     * Request auto login
     */
    private void requestLogin() {
        Log.d(TAG, "requestLogin: triggerEvent LogInEvent");
        ContentValues values = dynamicForm.save();
        // if ( autologinCheckBox.isChecked()) {
        values.put(CustomerAccountService.INTERNAL_AUTOLOGIN_FLAG, true);
        // }
        triggerContentEvent(new LogInEvent(values));
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

        switch (event.type) {

        case LOGIN_EVENT:
            Log.d(TAG, "loginCompletedEvent :" + event.getSuccess());
            // Get Customer
            ((SessionFragmentActivity) getActivity()).hideKeyboard();
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
            getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            TrackerDelegator.trackLoginSuccessful(getActivity(), (Customer) event.result, wasAutologin, loginOrigin);
            wasAutologin = false;
            return false;

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
        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.LOGIN_FORM, getActivity(), form);
        container.addView(dynamicForm.getContainer());
        setFormClickDetails();
        
        // Show save state
        if (null != this.savedInstanceState && null != dynamicForm) {
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while( iter.hasNext()) {
                DynamicFormItem item = iter.next();
                item.loadState(savedInstanceState);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.view.fragments.MyFragment#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (event.getType() == EventType.LOGIN_EVENT) {
            if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                TrackerDelegator.trackLoginFailed(wasAutologin);
                wasAutologin = false;
                if (autoLogin) {
                    autoLogin = false;
                    triggerContentEvent(EventType.GET_LOGIN_FORM_EVENT);
                } else {
                    List<String> errorMessages = event.errorMessages.get(Errors.JSON_ERROR_TAG);
                    if (errorMessages != null && (errorMessages.contains(Errors.CODE_LOGIN_FAILED)
                            || errorMessages.contains(Errors.CODE_LOGIN_CHECK_PASSWORD))) {
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
