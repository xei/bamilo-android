package com.mobile.view.newfragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.mobile.app.BamiloApplication;
import com.mobile.classes.models.BaseScreenModel;
import com.mobile.classes.models.EmarsysEventModel;
import com.mobile.classes.models.SimpleEventModel;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.constants.tracking.CategoryConstants;
import com.mobile.constants.tracking.EventActionKeys;
import com.mobile.constants.tracking.EventConstants;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.EmailHelper;
import com.mobile.helpers.session.RegisterHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.tracking.TrackingPage;
import com.mobile.service.utils.ApiConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.CustomerUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterFragment extends NewBaseFragment implements IResponseCallback {

    private static final String CUSTOMER_REGISTRATION_STEP_1_VALIDATED = "CUSTOMER_REGISTRATION_STEP_1_VALIDATED";
    private TextInputLayout tilEmail, tilPhoneNumber, tilNationalId, tilPassword;
    private EditText etEmail, etPhoneNumber, etNationalId, etPassword;

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;
    private boolean phoneVerificationOnGoing;

    public RegisterFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.fragment_register_user,
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);

    }

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

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(getString(TrackingPage.USER_SIGNUP.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (phoneVerificationOnGoing) {
            SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
            boolean isPhoneVerified = prefs.getBoolean(ConstantsSharedPrefs.KEY_SIGNUP_PHONE_VERIFIED, false);
            if (isPhoneVerified) {
                onClickCreate();
                getBaseActivity().getExtraTabLayout().setVisibility(View.GONE);
                return;
            }
        }
        showFragmentContentContainer();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        // Case forgot password
        if (id == R.id.btnRegister) {
            onClickCreate();
        }
        // Case super
        else {
            super.onClick(view);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showGhostFragmentContentContainer();
        HoloFontLoader.applyDefaultFont(view);

        // Text Input Layouts
        tilEmail = (TextInputLayout) view.findViewById(R.id.tilEmail);
        tilPhoneNumber = (TextInputLayout) view.findViewById(R.id.tilPhoneNumber);
        tilNationalId = (TextInputLayout) view.findViewById(R.id.tilNationalId);
        tilPassword = (TextInputLayout) view.findViewById(R.id.tilPassword);

        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        etNationalId = (EditText) view.findViewById(R.id.etNationalId);
        etPassword = (EditText) view.findViewById(R.id.etPassword);

        Button btnRegister = (Button) view.findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);

        CheckBox cbShowHiderPassword = (CheckBox) view.findViewById(R.id.cbShowHidePassword);
        cbShowHiderPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    etPassword.setTransformationMethod(new SingleLineTransformationMethod());
                } else {
                    etPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
                HoloFontLoader.applyDefaultFont(etPassword);
            }
        });
    }

    private void showValidateMessages(BaseResponse baseResponse) {
        Map map = baseResponse.getValidateMessages();
        tilEmail.setError(null);
        tilPhoneNumber.setError(null);
        tilNationalId.setError(null);
        tilPassword.setError(null);

        if (CollectionUtils.isNotEmpty(map)) {
            for (Object key : map.keySet()) {
                switch (key.toString()) {
                    case "national_id":
                        tilNationalId.setError(map.get(key).toString());
                        HoloFontLoader.applyDefaultFont(tilNationalId);
                        break;
                    case "email":
                        tilEmail.setError(map.get(key).toString());
                        HoloFontLoader.applyDefaultFont(tilEmail);
                        break;
                    case "password":
                        tilPassword.setError(map.get(key).toString());
                        HoloFontLoader.applyDefaultFont(tilPassword);
                        break;
                    case "phone":
                        tilPhoneNumber.setError(map.get(key).toString());
                        HoloFontLoader.applyDefaultFont(tilPhoneNumber);
                        break;
                }
            }
        }
    }

    private boolean checkValidation() {
        Context context = getBaseActivity();

        boolean result = validateField(context, getString(R.string.national_id), tilNationalId, etNationalId.getText().toString(), true, 10, 10, getString(R.string.normal_string_regex), "");
        result = validateField(context, getString(R.string.email_address), tilEmail, etEmail.getText().toString(), true, 0, 0, getString(R.string.email_regex), getResources().getString(R.string.error_invalid_email)) && result;
        result = validateField(context, getString(R.string.password), tilPassword, etPassword.getText().toString(), true, 6, 0, null, "") && result;
        result = validateField(context, getString(R.string.mobile_number), tilPhoneNumber, etPhoneNumber.getText().toString(), true, 0, 0, getString(R.string.cellphone_regex), "") && result;
        return result;
    }

    private boolean validateField(Context context, String label, TextInputLayout til, String text, boolean isRequired, int min, int max, String regex, String errorMessage) {
        boolean result = true;
        til.setError(null);
        // Case empty
        if (isRequired && android.text.TextUtils.isEmpty(text)) {
            errorMessage = context.getString(R.string.error_isrequired);
            til.setError(errorMessage);
            HoloFontLoader.applyDefaultFont(til);
            result = false;
        }
        // Case too short
        else if (min > 0 && text.length() < min) {
            til.setError(label + " " + String.format(context.getResources().getString(R.string.form_textminlen), min));
            HoloFontLoader.applyDefaultFont(til);
            result = false;
        }
        // Case too long
        else if (max > 0 && text.length() > max) {
            til.setError(label + " " + String.format(context.getResources().getString(R.string.form_textmaxlen), max));
            HoloFontLoader.applyDefaultFont(til);
            result = false;
        }
        // Case no match regex
        else if (regex != null) {

            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            if (com.mobile.service.utils.TextUtils.isEmpty(errorMessage)) {
                errorMessage = context.getString(R.string.error_invalid_value);
            }

            Matcher matcher = pattern.matcher(text);
            result = matcher.matches();
            if (!result) {
                til.setError(errorMessage);
                HoloFontLoader.applyDefaultFont(til);
            }
        }
        return result;
    }


    void requestRegister() {
        // Get values
        ContentValues values = new ContentValues();
        values.put("customer[national_id]", etNationalId.getText().toString());
        values.put("customer[email]", etEmail.getText().toString());
        values.put("customer[password]", etPassword.getText().toString());
        values.put("customer[phone]", etPhoneNumber.getText().toString());

        // Register user
        triggerRegister(ApiConstants.USER_REGISTRATION_API_PATH, values);
    }

    private void onClickCreate() {
        // Hide keyboard
        getBaseActivity().hideKeyboard();
        // Case valid
        if (checkValidation()) {
            requestRegister();
        }
        // Case invalid
        else {
            // Tracking
            EmarsysEventModel authEventModel = new EmarsysEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_FAILED,
                    Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                    EmarsysEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
            TrackerManager.trackEvent(getContext(), EventConstants.Signup, authEventModel);
        }
    }

    private void triggerRegister(String endpoint, ContentValues values) {
        triggerContentEventProgress(new RegisterHelper(), RegisterHelper.createBundle(endpoint, values), this);
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Print.i(TAG, "ON CLICK RETRY BUTTON");
        onResume();
    }

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

            case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                if (baseResponse.getSuccessMessages() != null &&
                        baseResponse.getSuccessMessages().containsKey(CUSTOMER_REGISTRATION_STEP_1_VALIDATED)) {
                    navigateToVerificationFragment();
                } else {
                    // Tracking
                    long customerId = SimpleEventModel.NO_VALUE;
                    String customerEmail = "";
                    if (BamiloApplication.CUSTOMER != null) {
                        customerId = BamiloApplication.CUSTOMER.getId();
                        customerEmail = BamiloApplication.CUSTOMER.getEmail();
                    }
                    EmarsysEventModel authEventModel = new EmarsysEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_SUCCESS,
                            Constants.LOGIN_METHOD_EMAIL, customerId,
                            EmarsysEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, customerEmail != null ? EmailHelper.getHost(customerEmail) : "",
                                    true));
                    TrackerManager.trackEvent(getContext(), EventConstants.Signup, authEventModel);
//                TrackerManager.trackEvent(getBaseActivity(), EmarsysEventConstants.SignUp, EmarsysEventFactory.signup("email", EmailHelper.getHost(BamiloApplication.CUSTOMER.getEmail()), true));
                    // Notify user
                    getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                    // Finish
                    getActivity().onBackPressed();
                    // Set facebook login
                    CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                    getBaseActivity().setupDrawerNavigation();
                }

                break;
            default:
                hideActivityProgress();
                break;
        }
    }

    private void navigateToVerificationFragment() {
        showGhostFragmentContentContainer();
        showFragmentLoading();
        phoneVerificationOnGoing = true;
        String phoneNumber = etPhoneNumber.getText().toString();
        Bundle args = new Bundle();
        args.putString(ConstantsIntentExtra.PHONE_NUMBER, phoneNumber);
        getBaseActivity().onSwitchFragment(FragmentType.MOBILE_VERIFICATION, args, false);

        SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(ConstantsSharedPrefs.KEY_SIGNUP_PHONE_VERIFIED, false);
        editor.apply();
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
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

            case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                // Tracking
                EmarsysEventModel authEventModel = new EmarsysEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_FAILED,
                        Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                        EmarsysEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
                TrackerManager.trackEvent(getContext(), EventConstants.Signup, authEventModel);

                // Validate and show errors
                showFragmentContentContainer();
                // Show validate messages
                showValidateMessages(baseResponse);
                getBaseActivity().getExtraTabLayout().setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }
    }

}
