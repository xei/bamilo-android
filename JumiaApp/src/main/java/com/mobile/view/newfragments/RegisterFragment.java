package com.mobile.view.newfragments;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.LogOut;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.session.RegisterHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.emarsys.EmarsysTracker;
import com.mobile.utils.pushwoosh.PushWooshTracker;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.Map;

public class RegisterFragment extends NewBaseFragment implements IResponseCallback
{

    private EditText mNationalIdView;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailRView;
    private EditText mPasswordRView;
    private EditText mPhoneView;

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;

    private boolean isInCheckoutProcess;
    private TextView national_id_error_message,first_name_error_message,last_name_error_message,email_error_message,password_error_message,phone_error_message;

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
         if (id == R.id.register_button_create)
        {
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
        phone_error_message = (TextView) view.findViewById(R.id.phone_error_message);
        national_id_error_message = (TextView) view.findViewById(R.id.national_id_error_message);
        first_name_error_message = (TextView) view.findViewById(R.id.first_name_error_message);
        last_name_error_message = (TextView) view.findViewById(R.id.last_name_error_message);
        email_error_message = (TextView) view.findViewById(R.id.email_error_message);
        password_error_message = (TextView) view.findViewById(R.id.password_error_message);
        phone_error_message = (TextView) view.findViewById(R.id.phone_error_message);
        view.findViewById(R.id.register_button_create).setOnClickListener(this);
        mEmailRView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        if (savedInstanceState != null)
        {
            mNationalIdView.setText(savedInstanceState.getString("mNationalIdView"));
            mFirstNameView.setText(savedInstanceState.getString("mFirstNameView"));
            mLastNameView.setText(savedInstanceState.getString("mLastNameView"));
            mEmailRView.setText(savedInstanceState.getString("mEmailRView"));
            mPasswordRView.setText(savedInstanceState.getString("mPasswordRView"));
            mPhoneView.setText(savedInstanceState.getString("mPhoneView"));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null)
        {
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
                switch (key.toString())
                {
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

    private boolean checkValidation()
    {
        Context context = getBaseActivity();

        boolean result = validateStringToPattern(context, R.string.national_id, mNationalIdView, mNationalIdView.getText().toString(), true, 10, 10, R.string.normal_string_regex, "", R.id.national_id_error_message);
        result = validateStringToPattern(context, R.string.first_name, mFirstNameView, mFirstNameView.getText().toString(), true, 2, 50, R.string.normal_string_regex, "", R.id.first_name_error_message) && result;
        result = validateStringToPattern(context, R.string.last_name, mLastNameView, mLastNameView.getText().toString(), true, 2, 50, R.string.normal_string_regex, "", R.id.last_name_error_message) && result;
        result = validateStringToPattern(context, R.string.email, mEmailRView, mEmailRView.getText().toString(), true, 0, 40, R.string.email_regex, getResources().getString(R.string.error_invalid_email), R.id.email_error_message) && result;
        result = validateStringToPattern(context, R.string.new_password, mPasswordRView, mPasswordRView.getText().toString(), true, 6, 50, R.string.normal_string_regex, "", R.id.password_error_message) && result;
        result = validateStringToPattern(context, R.string.phone, mPhoneView, mPhoneView.getText().toString(), true, 0, 40, R.string.normal_string_regex, "", R.id.phone_error_message) && result;
        return result;
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

        // Register user
        triggerRegister("form_submit::customer/create/", values);
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
                PushWooshTracker.signUp(getBaseActivity(),"email",true, JumiaApplication.CUSTOMER.getEmail());
                EmarsysTracker.signUp(getBaseActivity(),"email",true, JumiaApplication.CUSTOMER.getEmail());
                // Notify user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getString(R.string.succes_login));
                // Finish
                getActivity().onBackPressed();
                // Set facebook login
                CustomerUtils.setChangePasswordVisibility(getBaseActivity(), false);
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

               PushWooshTracker.signUp(getBaseActivity(),"email",false, JumiaApplication.CUSTOMER.getEmail());
               EmarsysTracker.signUp(getBaseActivity(),"email",false, JumiaApplication.CUSTOMER.getEmail());
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
