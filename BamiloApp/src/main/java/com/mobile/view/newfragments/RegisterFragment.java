package com.mobile.view.newfragments;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.mobile.app.BamiloApplication;
import com.mobile.components.absspinner.PromptSpinnerAdapter;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.EventConstants;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.EventFactory;
import com.mobile.helpers.EmailHelper;
import com.mobile.helpers.session.RegisterHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.tracking.gtm.GTMValues;
import com.mobile.service.utils.ApiConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.CustomerUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;
import com.mobile.view.fragments.ProductDetailsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;

public class RegisterFragment extends NewBaseFragment implements IResponseCallback {

    private EditText mNationalIdView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailRView;
    private EditText mPasswordRView;
    private EditText mPhoneView;
    private Spinner mGenderSpinner;

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;
    private TextView national_id_error_message, first_name_error_message, last_name_error_message,
            email_error_message, password_error_message, phone_error_message, gender_error_message;
    private String userGender;

    public RegisterFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LOGIN_OUT,
                R.layout.new_session_register_main_fragment,
                IntConstants.ACTION_BAR_NO_TITLE,
                ADJUST_CONTENT);

    }


   /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_session_register_main_fragment, container, false);
        return view;
    }*/

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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("mNationalIdView", mNationalIdView.getText().toString());
        outState.putString("mFirstNameView", mFirstNameView.getText().toString());
        outState.putString("mLastNameView", mLastNameView.getText().toString());
        outState.putString("mEmailRView", mEmailRView.getText().toString());
        outState.putString("mPasswordRView", mPasswordRView.getText().toString());
        outState.putString("mPhoneView", mPhoneView.getText().toString());

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        // Case forgot password
        if (id == R.id.register_button_create) {
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
        mNationalIdView = (EditText) view.findViewById(R.id.national_id);
        mFirstNameView = (EditText) view.findViewById(R.id.first_name);
        mLastNameView = (EditText) view.findViewById(R.id.last_name);
        mEmailRView = (EditText) view.findViewById(R.id.email);
        mPasswordRView = (EditText) view.findViewById(R.id.password);
        mPhoneView = (EditText) view.findViewById(R.id.phone);
        mGenderSpinner = (Spinner) view.findViewById(R.id.gender);
        gender_error_message = (TextView) view.findViewById(R.id.gender_error_message);
        fillGenderDropDown();
        phone_error_message = (TextView) view.findViewById(R.id.phone_error_message);
        national_id_error_message = (TextView) view.findViewById(R.id.national_id_error_message);
        first_name_error_message = (TextView) view.findViewById(R.id.first_name_error_message);
        last_name_error_message = (TextView) view.findViewById(R.id.last_name_error_message);
        email_error_message = (TextView) view.findViewById(R.id.email_error_message);
        password_error_message = (TextView) view.findViewById(R.id.password_error_message);
        view.findViewById(R.id.register_button_create).setOnClickListener(this);
        mEmailRView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        if (savedInstanceState != null) {
            mNationalIdView.setText(savedInstanceState.getString("mNationalIdView"));
            mFirstNameView.setText(savedInstanceState.getString("mFirstNameView"));
            mLastNameView.setText(savedInstanceState.getString("mLastNameView"));
            mEmailRView.setText(savedInstanceState.getString("mEmailRView"));
            mPasswordRView.setText(savedInstanceState.getString("mPasswordRView"));
            mPhoneView.setText(savedInstanceState.getString("mPhoneView"));
        }
    }

    private void fillGenderDropDown() {
        ArrayList<String> gender = new ArrayList<>();
        gender.add(getString(R.string.gender_male));
        gender.add(getString(R.string.gender_female));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseActivity(), R.layout.form_spinner_item, gender);
        adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
        PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, getBaseActivity());
        promptAdapter.setPrompt(getString(R.string.gender));
        mGenderSpinner.setAdapter(promptAdapter);
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    userGender = "male";
                } else if (position == 2) {
                    userGender = "female";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mNationalIdView.setText(savedInstanceState.getString("mNationalIdView"));
            mFirstNameView.setText(savedInstanceState.getString("mFirstNameView"));
            mLastNameView.setText(savedInstanceState.getString("mLastNameView"));
            mEmailRView.setText(savedInstanceState.getString("mEmailRView"));
            mPasswordRView.setText(savedInstanceState.getString("mPasswordRView"));
            mPhoneView.setText(savedInstanceState.getString("mPhoneView"));
        }
    }

    private void showValidateMessages(BaseResponse baseResponse) {
        Map map = baseResponse.getValidateMessages();
        national_id_error_message.setVisibility(View.GONE);
        first_name_error_message.setVisibility(View.GONE);
        last_name_error_message.setVisibility(View.GONE);
        email_error_message.setVisibility(View.GONE);
        password_error_message.setVisibility(View.GONE);
        phone_error_message.setVisibility(View.GONE);

        if (CollectionUtils.isNotEmpty(map)) {
            for (Object key : map.keySet()) {
                switch (key.toString()) {
                    case "national_id":
                        national_id_error_message.setVisibility(View.VISIBLE);
                        national_id_error_message.setText(map.get(key).toString());
                        break;
                    case "first_name":
                        first_name_error_message.setVisibility(View.VISIBLE);
                        first_name_error_message.setText(map.get(key).toString());
                        break;
                    case "last_name":
                        last_name_error_message.setVisibility(View.VISIBLE);
                        last_name_error_message.setText(map.get(key).toString());
                        break;
                    case "email":
                        email_error_message.setVisibility(View.VISIBLE);
                        email_error_message.setText(map.get(key).toString());
                        break;
                    case "password":
                        password_error_message.setVisibility(View.VISIBLE);
                        password_error_message.setText(map.get(key).toString());
                        break;
                    case "phone":
                        phone_error_message.setVisibility(View.VISIBLE);
                        phone_error_message.setText(map.get(key).toString());
                        break;
                }
            }
        }
    }

    private boolean checkValidation() {
        Context context = getBaseActivity();

        boolean result = validateStringToPattern(context, R.string.national_id, mNationalIdView, mNationalIdView.getText().toString(), true, 10, 10, R.string.normal_string_regex, "", R.id.national_id_error_message);
        result = validateStringToPattern(context, R.string.first_name, mFirstNameView, mFirstNameView.getText().toString(), true, 2, 50, R.string.normal_string_regex, "", R.id.first_name_error_message) && result;
        result = validateStringToPattern(context, R.string.last_name, mLastNameView, mLastNameView.getText().toString(), true, 2, 50, R.string.normal_string_regex, "", R.id.last_name_error_message) && result;
        result = validateStringToPattern(context, R.string.email, mEmailRView, mEmailRView.getText().toString(), true, 0, 40, R.string.email_regex, getResources().getString(R.string.error_invalid_email), R.id.email_error_message) && result;
        result = validateStringToPattern(context, R.string.new_password, mPasswordRView, mPasswordRView.getText().toString(), true, 6, 50, R.string.normal_string_regex, "", R.id.password_error_message) && result;
        result = validateStringToPattern(context, R.string.phone, mPhoneView, mPhoneView.getText().toString(), true, 0, 40, R.string.normal_string_regex, "", R.id.phone_error_message) && result;
        result &= validateUserGender();
        return result;
    }

    private boolean validateUserGender() {
        if (userGender == null) {
            gender_error_message.setVisibility(View.VISIBLE);
            gender_error_message.setText(R.string.error_isrequired);
            return false;
        }
        gender_error_message.setVisibility(View.GONE);
        return true;
    }


    void requestRegister() {
        // Get values
        ContentValues values = new ContentValues();
        values.put("customer[national_id]", mNationalIdView.getText().toString());
        values.put("customer[first_name]", mFirstNameView.getText().toString());
        values.put("customer[last_name]", mLastNameView.getText().toString());
        values.put("customer[email]", mEmailRView.getText().toString());
        values.put("customer[password]", mPasswordRView.getText().toString());
        values.put("customer[phone]", mPhoneView.getText().toString());
        values.put("customer[phone_prefix]", "100");
        values.put("customer[gender]", userGender);

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
            TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
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
                // Tracking
                TrackerDelegator.trackSignupSuccessful(GTMValues.REGISTER);
                TrackerManager.postEvent(getBaseActivity(), EventConstants.SignUp, EventFactory.signup("email", EmailHelper.getHost(BamiloApplication.CUSTOMER.getEmail()), true));
                // Notify user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                // Finish
                getActivity().onBackPressed();
                // Set facebook login
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
                getBaseActivity().setupDrawerNavigation();

                break;
            default:
                hideActivityProgress();
                break;
        }
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
                TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
                TrackerManager.postEvent(getBaseActivity(), EventConstants.SignUp, EventFactory.signup("email", EventConstants.UNKNOWN_EVENT_VALUE, false));

                // Validate and show errors
                showFragmentContentContainer();
                // Show validate messages
                showValidateMessages(baseResponse);
                break;

            default:
                break;
        }
    }

}
