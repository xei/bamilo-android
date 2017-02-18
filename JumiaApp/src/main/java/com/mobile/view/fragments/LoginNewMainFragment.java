/*
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.session.EmailCheckHelper;
import com.mobile.helpers.session.LoginAutoHelper;
import com.mobile.helpers.session.LoginFacebookHelper;
import com.mobile.helpers.session.LoginGuestHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;
import com.mobile.view.newfragments.NewBaseFragment;

import java.util.EnumSet;

*/
/**
 * Class used to perform login via Facebook,
 * @author sergiopereira
 *//*

public class LoginNewMainFragment extends NewBaseFragment implements  IResponseCallback, View.OnClickListener {

    private static final String TAG = LoginNewMainFragment.class.getSimpleName();
    TextView fgpass , geust_button , mErrorMessage;
    EditText loginEmail , loginPassword;
    private String mCustomerEmail;

    private FragmentType mParentFragmentType;

    private FragmentType mNextStepFromParent;
    private boolean istrue;
    public LoginNewMainFragment() {
        super();
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.new_login_session_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        fgpass = (TextView) view.findViewById(R.id.fgt_pass);
        mErrorMessage = (TextView) view.findViewById(R.id.login_text_error_message);
        geust_button = (TextView) view.findViewById(R.id.continue_without_log_in);
        loginEmail = (EditText) view.findViewById(R.id.login_email);
        loginPassword = (EditText) view.findViewById(R.id.login_password);
        fgpass.setOnClickListener(this);
        geust_button.setOnClickListener(this);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
             istrue = bundle.getBoolean("istrue");
            if (istrue) {
                geust_button.setVisibility(View.VISIBLE);
            }
            else
            {
                geust_button.setVisibility(View.GONE);
            }
            }
        }
    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
         if (id == R.id.fgt_pass) {
            onClickForgotPassword();
        }
         else if (id == R.id.login_button_continue) {
             onClickCheckEmail();
         }
         // Case guest login
         else if (id == R.id.continue_without_log_in) {
             onClickGuestLogin();
         }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, mParentFragmentType);
        outState.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, mNextStepFromParent);
        outState.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, istrue);
    }

    private void onClickForgotPassword() {
        getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
    }




    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

    }




    private void onClickGuestLogin() {
        Print.i(TAG, "ON CLICK GUEST LOGIN");
        // Get email
        mCustomerEmail = loginEmail.getText().toString();
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
        mCustomerEmail = loginEmail.getText().toString();
        // Trigger to check email
        if(TextUtils.isNotEmpty(mCustomerEmail) && Patterns.EMAIL_ADDRESS.matcher(mCustomerEmail).matches()) {
            triggerEmailCheck(mCustomerEmail);
            mErrorMessage.setVisibility(View.GONE);
        } else {
            mErrorMessage.setText(getString(R.string.error_invalid_email));
            mErrorMessage.setVisibility(View.VISIBLE);
        }
    }
    private void triggerGuestLogin(String email) {
        Print.i(TAG, "TRIGGER GUEST LOGIN");
        triggerContentEvent(new LoginGuestHelper(), LoginGuestHelper.createBundle(email), this);
    }

    private void triggerEmailCheck(String email) {
        Print.i(TAG, "TRIGGER EMAIL CHECK");
        triggerContentEvent(new EmailCheckHelper(), EmailCheckHelper.createBundle(email), this);
    }



}
*/
