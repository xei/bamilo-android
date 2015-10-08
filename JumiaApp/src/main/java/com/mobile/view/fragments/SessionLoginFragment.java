/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.FacebookTextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.configs.GetInitFormHelper;
import com.mobile.helpers.session.GetFacebookLoginHelper;
import com.mobile.helpers.session.GetLoginFormHelper;
import com.mobile.helpers.session.GetLoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.forms.InputType;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.utils.ui.ToastFactory;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SessionLoginFragment extends BaseExternalLoginFragment  {

    private static final String TAG = SessionLoginFragment.class.getSimpleName();

    private final static String FORM_ITEM_EMAIL = "email";

    private final static String FORM_ITEM_PASSWORD = "password";

    protected CheckBox rememberEmailCheck;

    private View signInButton;

    private ViewGroup container;

    private EditText pass_p;

    private EditText user;

    private View forgetPass;

    private View register;

    private Form formResponse = null;

    protected boolean wasAutoLogin = false;

    private DynamicForm dynamicForm;

    private Bundle savedInstanceState;

    protected FragmentType nextFragmentType;

    protected boolean cameFromRegister = false;


    /**
     * 
     * @return
     */
    public static SessionLoginFragment getInstance(Bundle bundle) {
        SessionLoginFragment fragment = new SessionLoginFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public SessionLoginFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LoginOut,
                R.layout.login,
                IntConstants.ACTION_BAR_NO_TITLE,
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
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            // Initialize SessionLoginFragment with no title
            super.titleResId = IntConstants.ACTION_BAR_NO_TITLE;
            // Force load form if comes from deep link
            nextFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
            String path = arguments.getString(ConstantsIntentExtra.DEEP_LINK_TAG);
            if (!TextUtils.isEmpty(path) && path.equals(DeepLinkManager.TAG)) formResponse = null;
        } else {
            // Initialize SessionLoginFragment with proper title
            super.titleResId = R.string.login_label;
        }
        setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        rememberEmailCheck = (CheckBox) view.findViewById(R.id.login_remember_user_email);
        signInButton = view.findViewById(R.id.middle_login_button_signin);
        forgetPass = view.findViewById(R.id.middle_login_link_fgtpassword);
        register = view.findViewById(R.id.middle_login_link_register);
        container = (ViewGroup) view.findViewById(R.id.form_container);
        // Get and set FB button
        FacebookTextView mFacebookButton = (FacebookTextView) view.findViewById(R.id.login_facebook_button);
        FacebookHelper.showOrHideFacebookButton(this, mFacebookButton);
        // Callback registration
        mFacebookButton.registerCallback(callbackManager, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // validate if there was an error related to facebook
        validateFacebookNetworkError();
        TrackerDelegator.trackPage(TrackingPage.LOGIN_SIGNUP, getLoadTime(), false);

        /**
         * Force input form align to left Restore is performed on BaseFragment.onPause()
         */
        forceInputAlignToLeft();

        // Validate form
        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
            Print.d(TAG, "FORM: TRY AUTO LOGIN");
            triggerAutoLogin();
        } else if (formResponse != null) {
            Print.d(TAG, "FORM ISN'T NULL");
            if (facebookLoginClicked) {
                facebookLoginClicked = false;
            } else {
                loadForm(formResponse);
            }
            cameFromRegister = false;
        } else {
            Print.d(TAG, "FORM IS NULL");
            // Clean the Facebook Session
            FacebookHelper.facebookLogout();

            HashMap<String, FormData> formDataRegistry = JumiaApplication.INSTANCE.getFormDataRegistry();
            if (formDataRegistry == null || formDataRegistry.size() == 0) {
                triggerInitForm();
            } else {
                triggerLoginForm();
            }
            cameFromRegister = false;
        }
        
        setLoginBottomLayout();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Print.i(TAG, "code1facebook onActivityResult");
        Log.e("onActivityResult", "requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);
//        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                for (DynamicFormItem item : dynamicForm) {
                    item.saveState(outState);
                }
                savedInstanceState = outState;
            }
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
        Print.i(TAG, "ON PAUSE");
        getBaseActivity().hideKeyboard();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
        if (container != null) {
            try {
                container.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
        formResponse = null;
    }
    
    /*
     * ################ FACEBOOK ################ 
     */




    /**
     * Set listeners
     */
    private void setLoginBottomLayout() {
        signInButton.setOnClickListener(this);
        forgetPass.setOnClickListener(this);
        register.setOnClickListener(this);
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
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            user.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        }
        user.setId(21);

        DynamicFormItem passwordItem = dynamicForm.getItemByKey(FORM_ITEM_PASSWORD);
        if (passwordItem == null) {
            return;
        }
        pass_p = (EditText) passwordItem.getEditControl();
        pass_p.setId(22);
        //#RTL
        if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            pass_p.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        }
    }
    
    /*
     * ################ RESPONSE ################ 
     */
    
    /**
     * CALLBACK
     * 
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {
        @Override
        public void onRequestError(BaseResponse baseResponse) {
            onErrorEvent(baseResponse);
        }

        @Override
        public void onRequestComplete(BaseResponse baseResponse) {
            onSuccessEvent(baseResponse);
        }
    };

    /**
     * 
     * @param baseResponse
     * @return
     */
    protected boolean onSuccessEvent(BaseResponse baseResponse) {
        Print.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        BaseActivity baseActivity = getBaseActivity();
        if (baseActivity != null) {
            super.handleSuccessEvent(baseResponse);
        } else {
            return true;
        }

        EventType eventType = baseResponse.getEventType();

        switch (eventType) {
        case INIT_FORMS:
            triggerLoginForm();
            return true;
        case FACEBOOK_LOGIN_EVENT:
            JumiaApplication.INSTANCE.setLoggedIn(true);
            Print.d(TAG, "facebookloginCompletedEvent : success");
            // Get Customer
            baseActivity.hideKeyboard();
            // NullPointerException on orientation change
            Customer customer =((CheckoutStepLogin)((NextStepStruct)baseResponse.getMetadata().getData()).getCheckoutStepObject()).getCustomer();
            JumiaApplication.CUSTOMER = customer;

            Bundle params = new Bundle();
            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
            params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, wasAutoLogin);
            params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, true);
            params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.LOGIN);

            // Validate the next step
            if (nextFragmentType != null) {
                FragmentController.getInstance().popLastEntry(FragmentType.LOGIN.toString());
                Bundle args = new Bundle();
                args.putBoolean(TrackerDelegator.LOGIN_KEY, true);
                getBaseActivity().onSwitchFragment(nextFragmentType, args, FragmentController.ADD_TO_BACK_STACK);
            } else {
                getBaseActivity().onBackPressed();
            }

            TrackerDelegator.trackLoginSuccessful(params);
            // Notify user
            ToastFactory.SUCCESS_LOGIN.show(baseActivity);
            return true;
        case LOGIN_EVENT:
            onLoginSuccessEvent(baseResponse);
            return true;
        case GET_LOGIN_FORM_EVENT:
            Form form = (Form) baseResponse.getMetadata().getData();
            if (form == null) {
                dialog = DialogGenericFragment.createServerErrorDialog(baseActivity,
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showFragmentLoading();
                                triggerLoginForm();
                                dismissDialogFragment();
                            }
                        }, false);
                dialog.show(baseActivity.getSupportFragmentManager(), null);
                return false;
            }

            Print.d(TAG, "Form Loaded");
            loadForm(form);
            formResponse = form;
        default:
            break;
        }
        return true;
    }

    protected void onLoginSuccessEvent(BaseResponse baseResponse){
        Print.d(TAG, "ON SUCCESS EVENT: LOGIN_EVENT");

        BaseActivity baseActivity = getBaseActivity();
        JumiaApplication.INSTANCE.setLoggedIn(true);
        // Get Customer
        baseActivity.hideKeyboard();
        // NullPointerException on orientation change
        if (!cameFromRegister) {
            Customer customer = ((CheckoutStepLogin)((NextStepStruct)baseResponse.getMetadata().getData()).getCheckoutStepObject()).getCustomer();
            JumiaApplication.CUSTOMER = customer;

            Bundle params = new Bundle();
            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
            params.putBoolean(TrackerDelegator.AUTOLOGIN_KEY, wasAutoLogin);
            params.putBoolean(TrackerDelegator.FACEBOOKLOGIN_KEY, false);
            params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.LOGIN);
            TrackerDelegator.trackLoginSuccessful(params);

            // Persist user email or empty that value after successfully login
            CustomerPreferences.setRememberedEmail(baseActivity, rememberEmailCheck.isChecked() ? customer.getEmail() : null);
        }

        cameFromRegister = false;
        // Validate the next step
        if (nextFragmentType != null) {
            Print.d(TAG, "NEXT STEP: " + nextFragmentType.toString());
            FragmentController.getInstance().popLastEntry(FragmentType.LOGIN.toString());
            Bundle oldArgs = getArguments();
            Bundle args;
            if(oldArgs != null){
                args = oldArgs;
            } else {
                args = new Bundle();
            }
            args.putBoolean(TrackerDelegator.LOGIN_KEY, true);
            baseActivity.onSwitchFragment(nextFragmentType, args, FragmentController.ADD_TO_BACK_STACK);
        } else {
            Print.d(TAG, "NEXT STEP: BACK");
            baseActivity.onBackPressed();
        }
    }

    /**
     * Load the dynamic form
     * 
     * @param form
     */
    private void loadForm(Form form) {
        // Set title when Login form is displayed to allow access to other fragments
        if (nextFragmentType != null) {
            //#specific_shop
            if(getResources().getBoolean(R.bool.is_daraz_specific) ||
                    getResources().getBoolean(R.bool.is_shop_specific) ||
                    ShopSelector.isRtlShop() ){
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
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            container.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
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

                if (fillEmail && FormInputType.email.equals(item.getType())) {
                    ((EditText) item.getEditControl()).setText(rememberedEmail);
                }
            }
        } else if (fillEmail) {
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();

                if (FormInputType.email.equals(item.getType())) {
                    ((EditText) item.getEditControl()).setText(rememberedEmail);
                }
            }
        }
        container.refreshDrawableState();
        showFragmentContentContainer();
    }

    protected boolean onErrorEvent(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        if (super.handleErrorEvent(baseResponse)) {
            return true;
        }

        EventType eventType = baseResponse.getEventType();
        ErrorCode errorCode = baseResponse.getError().getErrorCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);

        if (eventType == EventType.GET_LOGIN_FORM_EVENT) {
            if (errorCode == ErrorCode.UNKNOWN_ERROR && null == dynamicForm) {
                restartAllFragments();
                return true;
            }
        } else if (eventType == EventType.LOGIN_EVENT) {
            clearCredentials();

            TrackerDelegator.trackLoginFailed(wasAutoLogin, GTMValues.LOGIN, GTMValues.EMAILAUTH);

            if (errorCode == ErrorCode.REQUEST_ERROR) {

                if (wasAutoLogin) {
                    if (formResponse == null) {
                        // Sometimes formDataRegistry is null, so init forms
                        triggerInitForm();
                        triggerLoginForm();
                    } else {
                        LogOut.performLogOut(new WeakReference<Activity>(getBaseActivity()));
                    }
                } else {
                    Print.d(TAG, "SHOW DIALOG");
                    Map<String, List<String>> errors = baseResponse.getErrorMessages();
                    List<String> errorMessages = null;
                    if (errors != null) {
                        errorMessages = errors.get(RestConstants.JSON_VALIDATE_TAG);
                    }
                    if (errors != null && errorMessages != null && errorMessages.size() > 0) {
                        showFragmentContentContainer();
                        dialog = DialogGenericFragment.newInstance(true, false,
                                getString(R.string.error_login_title),
                                errorMessages.get(0),
                                getString(R.string.ok_label), "", new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int id = v.getId();
                                        if (id == R.id.button1) {
                                            dismissDialogFragment();
                                        }
                                    }
                                });
                        dialog.show(getBaseActivity().getSupportFragmentManager(), null);
                    }
                }
                return true;
            }
        } else if(eventType == EventType.FACEBOOK_LOGIN_EVENT){
            Print.i(TAG, "SHOW ERROR MESSAGE FOR FACEBOOK_LOGIN_EVENT");
            // Clear credentials case auto login failed
            clearCredentials();
            // Facebook logout
            FacebookHelper.facebookLogout();
            // Show alert
            
            TrackerDelegator.trackLoginFailed(wasAutoLogin, GTMValues.LOGIN, GTMValues.FACEBOOK);
            
            Map<String, List<String>> errors = baseResponse.getErrorMessages();
            List<String> errorMessages = null;
            if (errors != null) {
                Print.i(TAG, "ERRORS: " + errors.toString());
                errorMessages = errors.get(RestConstants.JSON_ERROR_TAG);
            }
            
            if (errors != null && errorMessages != null && errorMessages.size() > 0) {
                showFragmentContentContainer();
                // Dismiss the old fragment
                dismissDialogFragment();
                // Show new
                dialog = DialogGenericFragment.newInstance(true, false,
                        getString(R.string.error_login_title),
                        errorMessages.get(0),
                        getString(R.string.ok_label),
                        "",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int id = v.getId();
                                if (id == R.id.button1) {
                                    dismissDialogFragment();
                                }
                            }
                        });
                dialog.show(getBaseActivity().getSupportFragmentManager(), null);
            }
        }
        return false;
    }

    
    /*
     * ################ TRIGGERS ################
     */
    /**
     * Request auto login
     */
    private void requestLogin() {
        Print.d(TAG, "requestLogin: triggerEvent LogInEvent");
        getBaseActivity().hideKeyboard();
        ContentValues values = dynamicForm.save();
        values.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        triggerLogin(values, true);
    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     */
    private void triggerAutoLogin() {
        wasAutoLogin = true;
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, wasAutoLogin);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        triggerContentEvent(new GetLoginHelper(), bundle, mCallBack);
    }

    private void triggerLogin(ContentValues values, boolean saveCredentials) {
        wasAutoLogin = false;
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, saveCredentials);
        triggerContentEvent(new GetLoginHelper(), bundle, mCallBack);
    }

    /**
     * Trigger used to perform a login/signup from Facebook
     *
     * @param values
     * @param saveCredentials
     */
    @Override
    public void triggerFacebookLogin(ContentValues values, boolean saveCredentials) {
        wasAutoLogin = false;
        triggerContentEventNoLoading(new GetFacebookLoginHelper(), GetFacebookLoginHelper.createBundle(values,saveCredentials), mCallBack);
    }

    private void triggerLoginForm() {
        wasAutoLogin = false;
        triggerContentEvent(new GetLoginFormHelper(), null, mCallBack);
    }

    private void triggerInitForm() {
        wasAutoLogin = false;
        triggerContentEvent(new GetInitFormHelper(), null, mCallBack);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        // Case FB button 
        if(id == R.id.login_facebook_button) {
            facebookLoginClicked = true;
            showFragmentLoading();
        }
        // Case sign in button
        else if (id == R.id.middle_login_button_signin) {
            // Log.d(TAG, "CLICKED ON SIGNIN");
            if (null != dynamicForm) {
                if (dynamicForm.validate()) requestLogin();
                // Tracking login failed
                else TrackerDelegator.trackLoginFailed(TrackerDelegator.ISNT_AUTO_LOGIN, GTMValues.LOGIN, GTMValues.EMAILAUTH);
            } else triggerLoginForm();
        }
        // Case forgot password
        else if (id == R.id.middle_login_link_fgtpassword) {
            getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case register
        else if (id == R.id.middle_login_link_register) {
            cameFromRegister = true;
            getBaseActivity().onSwitchFragment(FragmentType.REGISTER, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

}
