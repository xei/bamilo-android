package com.bamilo.android.appmodule.bamiloapp.view.newfragments;

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

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.EmailHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.RegisterHelper;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.modernbamilo.customview.BamiloActionButton;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsCheckout;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsSharedPrefs;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.CategoryConstants;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.appmodule.modernbamilo.tracking.TrackingEvents;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.ApiConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;

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
            boolean isPhoneVerified = prefs.getBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, false);
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
        // TODO: 8/28/18 farshid
//        HoloFontLoader.applyDefaultFont(view);

        // Text Input Layouts
        tilEmail = (TextInputLayout) view.findViewById(R.id.tilEmail);
        tilPhoneNumber = (TextInputLayout) view.findViewById(R.id.tilPhoneNumber);
        tilNationalId = (TextInputLayout) view.findViewById(R.id.tilNationalId);
        tilPassword = (TextInputLayout) view.findViewById(R.id.tilPassword);

        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPhoneNumber = (EditText) view.findViewById(R.id.etPhoneNumber);
        etNationalId = (EditText) view.findViewById(R.id.etNationalId);
        etPassword = (EditText) view.findViewById(R.id.etPassword);

        BamiloActionButton btnRegister = (BamiloActionButton) view.findViewById(R.id.btnRegister);
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
                // TODO: 8/28/18 farshid
//                HoloFontLoader.applyDefaultFont(etPassword);
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
                        // TODO: 8/28/18 farshid
//                        HoloFontLoader.applyDefaultFont(tilNationalId);
                        break;
                    case "email":
                        tilEmail.setError(map.get(key).toString());
                        // TODO: 8/28/18 farshid
//                        HoloFontLoader.applyDefaultFont(tilEmail);
                        break;
                    case "password":
                        tilPassword.setError(map.get(key).toString());
                        // TODO: 8/28/18 farshid
//                        HoloFontLoader.applyDefaultFont(tilPassword);
                        break;
                    case "phone":
                        tilPhoneNumber.setError(map.get(key).toString());
                        // TODO: 8/28/18 farshid
//                        HoloFontLoader.applyDefaultFont(tilPhoneNumber);
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
            // TODO: 8/28/18 farshid
//            HoloFontLoader.applyDefaultFont(til);
            result = false;
        }
        // Case too short
        else if (min > 0 && text.length() < min) {
            til.setError(label + " " + String.format(context.getResources().getString(R.string.form_textminlen), min));
            // TODO: 8/28/18 farshid
//            HoloFontLoader.applyDefaultFont(til);
            result = false;
        }
        // Case too long
        else if (max > 0 && text.length() > max) {
            til.setError(label + " " + String.format(context.getResources().getString(R.string.form_textmaxlen), max));
            // TODO: 8/28/18 farshid
//            HoloFontLoader.applyDefaultFont(til);
            result = false;
        }
        // Case no match regex
        else if (regex != null) {

            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            if (TextUtils.isEmpty(errorMessage)) {
                errorMessage = context.getString(R.string.error_invalid_value);
            }

            Matcher matcher = pattern.matcher(text);
            result = matcher.matches();
            if (!result) {
                til.setError(errorMessage);
                // TODO: 8/28/18 farshid
//                HoloFontLoader.applyDefaultFont(til);
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
            MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_FAILED,
                    Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                    MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
//            TrackerManager.trackEvent(getContext(), EventConstants.Signup, authEventModel);
        }
    }

    private void triggerRegister(String endpoint, ContentValues values) {
        triggerContentEventProgress(new RegisterHelper(), RegisterHelper.createBundle(endpoint, values), this);
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        // Validate fragment visibility
        if (isOnStoppingProcess || getBaseActivity() == null) {
            return;
        }
        // Call super
        super.handleSuccessEvent(baseResponse);
        // Validate event
        EventType eventType = baseResponse.getEventType();
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
                    MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_SUCCESS,
                            Constants.LOGIN_METHOD_EMAIL, customerId,
                            MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, customerEmail != null ? EmailHelper.getHost(customerEmail) : "",
                                    true));
//                    TrackerManager.trackEvent(getContext(), EventConstants.Signup, authEventModel);
                    EventTracker.INSTANCE.register(
                            String.valueOf(customerId),
                            TrackingEvents.RegistrationType.REGISTER_WITH_EMAIL,
                            true);
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
        editor.putBoolean(ConstantsSharedPrefs.KEY_IS_PHONE_VERIFIED, false);
        editor.apply();
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        hideActivityProgress();
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Validate error o super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate event
        EventType eventType = baseResponse.getEventType();
        switch (eventType) {

            case REGISTER_ACCOUNT_EVENT:
                hideActivityProgress();
                // Tracking
                MainEventModel authEventModel = new MainEventModel(CategoryConstants.ACCOUNT, EventActionKeys.SIGNUP_FAILED,
                        Constants.LOGIN_METHOD_EMAIL, SimpleEventModel.NO_VALUE,
                        MainEventModel.createAuthEventModelAttributes(Constants.LOGIN_METHOD_EMAIL, "", false));
//                TrackerManager.trackEvent(getContext(), EventConstants.Signup, authEventModel);
                EventTracker.INSTANCE.register("UNKNOWN", TrackingEvents.RegistrationType.REGISTER_WITH_EMAIL, false);

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
