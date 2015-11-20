package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Patterns;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.FacebookTextView;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.session.EmailCheckHelper;
import com.mobile.helpers.session.LoginFacebookHelper;
import com.mobile.helpers.session.LoginGuestHelper;
import com.mobile.helpers.session.LoginHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.objects.customer.CustomerEmailCheck;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.view.R;

import java.lang.ref.WeakReference;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Class used to perform login via Facebook,
 * @author sergiopereira
 */
public class SessionLoginMainFragment extends BaseExternalLoginFragment implements IResponseCallback {

    private static final String TAG = SessionLoginMainFragment.class.getSimpleName();

    private EditText mEmailView;

    private String mCustomerEmail;

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;

    private TextView mErrorMessage;

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
                NavigationAction.LOGIN_OUT,
                R.layout.session_login_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);
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
            isInCheckoutProcess = arguments.getBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API);
        }
        // Show checkout tab layout
        if (isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT) {
            checkoutStep = ConstantsCheckout.CHECKOUT_ABOUT_YOU;
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
        View divider = view.findViewById(R.id.login_divider);
        FacebookHelper.showOrHideFacebookButton(this, mFacebookButton, divider);
        mFacebookButton.registerCallback(mFacebookCallbackManager, this);
        // Get email
        mEmailView = (EditText) view.findViewById(R.id.login_text_email);
        // Get error message
        mErrorMessage = (TextView) view.findViewById(R.id.login_text_error_message);
        // Get continue button
        view.findViewById(R.id.login_button_continue).setOnClickListener(this);
        // Get and set guest button
        setGuestButton(view.findViewById(R.id.login_button_guest), isInCheckoutProcess && mParentFragmentType != FragmentType.MY_ACCOUNT);
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
        // Case resume from facebook dialog
        else if(facebookLoginClicked) {
            facebookLoginClicked = false;
        }
        // Case show content
        else {
            Print.i(TAG, "USER WITHOUT CREDENTIALS");
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
        outState.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, isInCheckoutProcess);
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

    /**
     * Method used to show the guest login only for checkout process.
     */
    private void setGuestButton(@NonNull View view, boolean checkout) {
        if(checkout) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(this);
        }
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
            mErrorMessage.setVisibility(View.GONE);
        } else {
            mErrorMessage.setText(getString(R.string.error_invalid_email));
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    protected void onClickCheckEmail() {
        Print.i(TAG, "ON CLICK CHECK EMAIL");
        // Get email
        mCustomerEmail = mEmailView.getText().toString();
        // Trigger to check email
        if(TextUtils.isNotEmpty(mCustomerEmail) && Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
            triggerEmailCheck(mCustomerEmail);
            mErrorMessage.setVisibility(View.GONE);
        } else {
            mErrorMessage.setText(getString(R.string.error_invalid_email));
            mErrorMessage.setVisibility(View.VISIBLE);
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
        Print.i(TAG, "TRIGGER GUEST LOGIN");
        triggerContentEvent(new LoginGuestHelper(), LoginGuestHelper.createBundle(email), this);
    }

    private void triggerEmailCheck(String email) {
        Print.i(TAG, "TRIGGER EMAIL CHECK");
        triggerContentEvent(new EmailCheckHelper(), EmailCheckHelper.createBundle(email), this);
    }

    private void triggerAutoLogin() {
        Print.i(TAG, "TRIGGER AUTO LOGIN");
        triggerContentEvent(new LoginHelper(), LoginHelper.createAutoLoginBundle(), this);
    }

    @Override
    public void triggerFacebookLogin(ContentValues values, boolean saveCredentials) {
        Print.i(TAG, "TRIGGER FACEBOOK LOGIN");
        triggerContentEventNoLoading(new LoginFacebookHelper(), LoginFacebookHelper.createBundle(values, saveCredentials), this);
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
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);
        switch (eventType) {
            case EMAIL_CHECK:
                // Get value
                boolean exist = ((CustomerEmailCheck) baseResponse.getMetadata().getData()).exist();
                // Validate next login step
                FragmentType fragmentType = exist ? FragmentType.LOGIN_EMAIL : FragmentType.REGISTER;
                // Go to next login step
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.DATA, mCustomerEmail);
                bundle.putBoolean(ConstantsIntentExtra.FLAG_1, isInCheckoutProcess);
                bundle.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, mParentFragmentType);
                getBaseActivity().onSwitchFragment(fragmentType, bundle, FragmentController.ADD_TO_BACK_STACK);
                break;
            case GUEST_LOGIN_EVENT:
            case FACEBOOK_LOGIN_EVENT:
            case LOGIN_EVENT:
                // Get Customer
                NextStepStruct nextStepStruct = (NextStepStruct) baseResponse.getMetadata().getData();
                FragmentType nextStepFromApi = nextStepStruct.getFragmentType();
                // Case valid next step
                if(nextStepFromApi != FragmentType.UNKNOWN) {
                    Customer customer = ((CheckoutStepLogin) nextStepStruct.getCheckoutStepObject()).getCustomer();
                    // Tracking
                    if (eventType == EventType.GUEST_LOGIN_EVENT) {
                        TrackerDelegator.storeFirstCustomer(customer);
                        TrackerDelegator.trackSignupSuccessful(GTMValues.CHECKOUT);
                    } else {
                        TrackerDelegator.trackLoginSuccessful(customer, true, true);
                    }
                    // Validate the next step
                    CheckoutStepManager.validateLoggedNextStep(getBaseActivity(), isInCheckoutProcess, mParentFragmentType, mNextStepFromParent, nextStepFromApi);
                }
                // Case unknown checkout step
                else {
                    // Show layout to call to order
                    showFragmentUnknownCheckoutStepError();
                }
                break;
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
        Print.i(TAG, "ON ERROR EVENT: " + eventType);
        switch (eventType) {
            case EMAIL_CHECK:
                getBaseActivity().warningFactory.showWarning(com.mobile.utils.ui.WarningFactory.ERROR_MESSAGE, getString(R.string.error_invalid_email));
                showFragmentContentContainer();
                break;
            case FACEBOOK_LOGIN_EVENT:
            case LOGIN_EVENT:
                // Logout
                LogOut.perform(new WeakReference<Activity>(getBaseActivity()));
                // Tracking
                TrackerDelegator.trackLoginFailed(true, GTMValues.LOGIN, eventType == EventType.LOGIN_EVENT ? GTMValues.EMAILAUTH : GTMValues.FACEBOOK);
            case GUEST_LOGIN_EVENT:
                // Tracking
                if(eventType == EventType.GUEST_LOGIN_EVENT) TrackerDelegator.trackSignupFailed(GTMValues.CHECKOUT);
                // Show warning
                int errorCode = baseResponse.getError().getCode();
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    @SuppressWarnings("unchecked")
                    Map<String, List<String>> errors = baseResponse.getErrorMessages();
                    // Show dialog or toast
                    if (!showErrorDialog(errors, R.string.error_signup_title)) {
                        getBaseActivity().warningFactory.showWarning(com.mobile.utils.ui.WarningFactory.ERROR_MESSAGE, getString(R.string.no_connect_dialog_content));
                    }
                } else {
                    showUnexpectedErrorWarning();
                }
                // Show content
                showFragmentContentContainer();
                break;
            default:
                break;
        }
    }

        /**
     * Dialog used to show an error
     */
    private boolean showErrorDialog(Map<String, List<String>> errors, int titleId) {
        Print.d(TAG, "SHOW ERROR DIALOG");
        List<String> errorMessages = null;
        if (errors != null) {
            errorMessages = errors.get(RestConstants.JSON_VALIDATE_TAG);
        }
        if (errors != null && errorMessages != null && errorMessages.size() > 0) {
            showFragmentContentContainer();
            dialog = DialogGenericFragment.newInstance(true, false,
                    getString(titleId),
                    errorMessages.get(0),
                    getString(R.string.ok_label),
                    "",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dismissDialogFragment();
                            }
                        }
                    });
            dialog.show(getBaseActivity().getSupportFragmentManager(), null);
            return true;
        } else {
            return false;
        }
    }

}
