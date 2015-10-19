package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.FacebookTextView;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.session.EmailCheckHelper;
import com.mobile.helpers.session.GetFacebookLoginHelper;
import com.mobile.helpers.session.GetLoginHelper;
import com.mobile.helpers.session.SetSignUpHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerEmailCheck;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.utils.ui.ToastManager;
import com.mobile.view.R;

import java.lang.ref.WeakReference;
import java.util.EnumSet;

/**
 * Class TODO
 * @author sergiopereira
 */
public class SessionLoginMainFragment extends BaseExternalLoginFragment implements IResponseCallback {

    private static final String TAG = SessionLoginMainFragment.class.getSimpleName();

    private EditText mEmailView;

    private String mCustomerEmail;

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;

    /**
     * Get new instance
     */
    public static SessionLoginMainFragment getInstance(Bundle bundle) {
        SessionLoginMainFragment fragment = new SessionLoginMainFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public SessionLoginMainFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LoginOut,
                R.layout.session_login_fragment,
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
        Bundle arguments = savedInstanceState == null ? getArguments() : savedInstanceState;
        if (arguments != null) {
            // Force load form if comes from deep link
            mParentFragmentType = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE);
            mNextStepFromParent = (FragmentType) arguments.getSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE);
            isInCheckoutProcess = arguments.getBoolean(ConstantsIntentExtra.IS_IN_CHECKOUT_PROCESS);
        }
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
        // Get info
        String text = String.format(getString(R.string.login_main_info), getString(R.string.app_name));
        ((TextView) view.findViewById(R.id.login_text_info)).setText(text);
        // Get and set FB button
        FacebookTextView mFacebookButton = (FacebookTextView) view.findViewById(R.id.login_button_facebook);
        FacebookHelper.showOrHideFacebookButton(this, mFacebookButton);
        mFacebookButton.registerCallback(mFacebookCallbackManager, this);
        // Get email
        mEmailView = (EditText) view.findViewById(R.id.login_text_email);
        // Get continue button
        view.findViewById(R.id.login_button_continue).setOnClickListener(this);
        // Get
        view.findViewById(R.id.login_button_guest).setOnClickListener(this);

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
        // Tracking
        TrackerDelegator.trackPage(TrackingPage.LOGIN_SIGNUP, getLoadTime(), false);
        // Validate if there was an error related to facebook
        validateFacebookNetworkError();
        // Case auto login
        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials()) {
            triggerAutoLogin();
        }
        // Case show content
        else {
            Print.i(TAG, "FORM IS NULL");
            // Clean the Facebook Session
            FacebookHelper.facebookLogout();
            // Show content
            showFragmentContentContainer();
        }
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Print.i(TAG, "ON ACTIVITY RESULT: " + "request:" + requestCode + " result:" + resultCode + " intent:" + data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, mParentFragmentType);
        outState.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, mNextStepFromParent);
        outState.putBoolean(ConstantsIntentExtra.IS_IN_CHECKOUT_PROCESS, isInCheckoutProcess);
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
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");

    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

     /*
     * ################ LISTENERS ################
     */

    @Override
    public void onClick(View view) {
        int id = view.getId();
        // Case FB button
        if (id == R.id.login_button_facebook) {
            facebookLoginClicked = true;
            showFragmentLoading();
        }
        // Case forgot password
        else if (id == R.id.login_button_continue) {
            onClickCheckEmail();
        }
        // Case guest login
        else if (id == R.id.login_button_guest) {
            onClickGuestLogin();
        }
        // Case super
        else {
            super.onClick(view);
        }
    }

    private void onClickGuestLogin() {
        Print.i(TAG, "ON CLICK GUEST LOGIN");
        // Get email
        mCustomerEmail = mEmailView.getText().toString();
        // Trigger to check email
        if(TextUtils.isNotEmpty(mCustomerEmail) && Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
            triggerGuestLogin(mCustomerEmail);
        } else {
            ToastManager.show(getBaseActivity().getApplicationContext(), ToastManager.ERROR_INVALID_EMAIL);
        }
    }

    protected void onClickCheckEmail() {
        Print.i(TAG, "ON CLICK CHECK EMAIL");
        // Get email
        mCustomerEmail = mEmailView.getText().toString();
        // Trigger to check email
        if(TextUtils.isNotEmpty(mCustomerEmail) && Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
            triggerEmailCheck(mCustomerEmail);
        } else {
            ToastManager.show(getBaseActivity().getApplicationContext(), ToastManager.ERROR_INVALID_EMAIL);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Print.i(TAG, "ON CLICK RETRY BUTTON");
        onResume();
    }

    /*
     * ################ TRIGGERS ################
     */

    private void triggerGuestLogin(String email) {
        Print.i(TAG, "TRIGGER EMAIL CHECK");
        ContentValues values = new ContentValues();
        values.put("key", email);
        triggerContentEvent(new SetSignUpHelper(), SetSignUpHelper.createBundle(values), this);
    }

    private void triggerEmailCheck(String email) {
        Print.i(TAG, "TRIGGER EMAIL CHECK");
        triggerContentEvent(new EmailCheckHelper(), EmailCheckHelper.createBundle(email), this);
    }

    private void triggerAutoLogin() {
        Print.i(TAG, "TRIGGER AUTO LOGIN");
        triggerContentEvent(new GetLoginHelper(), GetLoginHelper.createAutoLoginBundle(), this);
    }

    @Override
    public void triggerFacebookLogin(ContentValues values, boolean saveCredentials) {
        Print.i(TAG, "TRIGGER FACEBOOK LOGIN");
        triggerContentEventNoLoading(new GetFacebookLoginHelper(), GetFacebookLoginHelper.createBundle(values, saveCredentials), this);
    }

    /*
     * ################ RESPONSE ################
     */

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate event
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType.toString());
        switch (eventType) {
            case EMAIL_CHECK:
                // Get value
                boolean exist = ((CustomerEmailCheck) baseResponse.getMetadata().getData()).exist();
                // Validate next login step
                FragmentType fragmentType = exist ? FragmentType.LOGIN_EMAIL : FragmentType.REGISTER;
                // Go to next login step
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.DATA, mCustomerEmail);
                getBaseActivity().onSwitchFragment(fragmentType, bundle, FragmentController.ADD_TO_BACK_STACK);
                break;
            case FACEBOOK_LOGIN_EVENT:
            case LOGIN_EVENT:
                // Get Customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getMetadata().getData();
                Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                FragmentType nextStepFromApi = nextStepStruct.getFragmentType();
                // Tracking
                TrackerDelegator.trackLoginSuccessful(customer, true, true);
                // Validate the next step
                GetLoginHelper.validateNextStep(getBaseActivity(), isInCheckoutProcess, mParentFragmentType, mNextStepFromParent, nextStepFromApi);
                return;
            default:
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Validate error o super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON ERROR EVENT: " + eventType.toString());
        switch (eventType) {
            case EMAIL_CHECK:
                ToastManager.show(getBaseActivity().getApplicationContext(), ToastManager.ERROR_INVALID_EMAIL);
                showFragmentContentContainer();
                break;
            case FACEBOOK_LOGIN_EVENT:
            case LOGIN_EVENT:
                // Clear credentials case auto login failed
                clearCredentials();
                // Logout
                LogOut.perform(new WeakReference<Activity>(getBaseActivity()));
                // Facebook logout
                FacebookHelper.facebookLogout();
                // Tracking
                String value = eventType == EventType.FACEBOOK_LOGIN_EVENT ? GTMValues.FACEBOOK : GTMValues.EMAILAUTH;
                TrackerDelegator.trackLoginFailed(true, GTMValues.LOGIN, value);
                // Show content
                showFragmentContentContainer();
            default:
                break;
        }
    }

}
