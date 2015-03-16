/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Customer;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.account.GetChangePasswordHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class MyAccountUserDataFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(MyAccountUserDataFragment.class);

    private final static int PASSWORD_MINLENGTH = 6;

    private View mainView;

    private TextView firstNameText;

    private TextView lastNameText;

    private TextView emailText;

    private EditText newPasswordText;

    private EditText newPassword2Text;

    private TextView passwordErrorHint;

    /**
     * Get instance
     * 
     * @return
     */
    public static MyAccountUserDataFragment getInstance() {
        return new MyAccountUserDataFragment();
    }

    /**
     * Empty constructor
     */
    public MyAccountUserDataFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET,
                MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.layout.my_account_user_data_fragment,
                R.string.myaccount_userdata,
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
        Log.i(TAG, "ON ATTACH");
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
        // Retain this fragment across configuration changes.
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
        Log.i(TAG, "ON VIEW CREATED");

        mainView = view;

        if (null != JumiaApplication.CUSTOMER) {
            setAppContentLayout();
            init();
        } else {
            showFragmentErrorRetry();
        }

    }
    
    private void init() {

        // triggerCustomer();

        if (null != lastNameText) {
            lastNameText.setText(JumiaApplication.CUSTOMER.getLastName());
            firstNameText.setText(JumiaApplication.CUSTOMER.getFirstName());
            emailText.setText(JumiaApplication.CUSTOMER.getEmail());
            showFragmentContentContainer();
        } else {
            restartAllFragments();
        }

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
        Log.i(TAG, "ON RESUME");
        
        if (null != JumiaApplication.CUSTOMER) {
            showFragmentContentContainer();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE");
    }

    /**
     * Inflates this activity layout into the main template layout
     */
    public void setAppContentLayout() {
        Button saveButton = (Button) mainView.findViewById(R.id.button_save);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) mainView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(this);

        firstNameText = (TextView) mainView.findViewById(R.id.clientFirstName);
        lastNameText = (TextView) mainView.findViewById(R.id.clientLastName);
        lastNameText.setVisibility(View.GONE);
        emailText = (TextView) mainView.findViewById(R.id.clientEmail);

        newPasswordText = (EditText) mainView.findViewById(R.id.typeNewPassword);
        newPassword2Text = (EditText) mainView.findViewById(R.id.retypeNewPassword);
        passwordErrorHint = (TextView) mainView.findViewById(R.id.passwordErrorHint);
        passwordErrorHint.setVisibility(View.GONE);
    }

    /**
     * This method changes the user's password.
     */
    public void changePassword() {

        String newPassword = newPasswordText.getText().toString();
        if (newPassword.length() < PASSWORD_MINLENGTH) {
            displayErrorHint(getString(R.string.password_new_mincharacters));
            return;
        }

        String newPassword2 = newPassword2Text.getText().toString();
        if (!newPassword.equals(newPassword2)) {
            displayErrorHint(getString(R.string.form_passwordsnomatch));
            return;
        }

        /**
         * FIXME: CREATE A TICKET TO FIX THIS METHOD
         * 
         * @author sergiopereira
         */
        ContentValues values = new ContentValues();
        values.put("Alice_Module_Customer_Model_PasswordForm[password]",
                newPassword);
        values.put("Alice_Module_Customer_Model_PasswordForm[password2]",
                newPassword2);
        values.put("Alice_Module_Customer_Model_PasswordForm[email]", emailText.getText()
                .toString());

        triggerChangePass(values);

        displayErrorHint(null);

    }

    private void displayErrorHint(String hint) {
        if (hint != null) {
            passwordErrorHint.setText(hint);
            passwordErrorHint.setVisibility(View.VISIBLE);
        } else {
            passwordErrorHint.setText("");
            passwordErrorHint.setVisibility(View.GONE);
        }
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");
        if (!isVisible()) {
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

        switch (eventType) {
        case CHANGE_PASSWORD_EVENT:
            Log.d(TAG, "changePasswordEvent: Password changed with success");
            if (null != getActivity()) {
                Toast.makeText(getActivity(), getString(R.string.password_changed),
                        Toast.LENGTH_SHORT).show();
            }
            finish();

            return true;
        case GET_CUSTOMER:
            Customer customer = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.CUSTOMER = customer;
            Log.d(TAG, "CUSTOMER: " + customer.getLastName() + " " + customer.getFirstName() + " "
                    + customer.getEmail());
            if (null != lastNameText) {
                lastNameText.setText(customer.getLastName());
                firstNameText.setText(customer.getFirstName());
                emailText.setText(customer.getEmail());
                showFragmentContentContainer();
            } else {
                restartAllFragments();
                // finish();
                return true;
            }
            return true;
        default:
            return false;
        }
    }

    protected boolean onErrorEvent(Bundle bundle) {
        Log.i(TAG, "ON ERROR EVENT");
        if (!isVisible()) {
            return true;
        }

        if (super.handleErrorEvent(bundle)) {
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        switch (eventType) {
        case CHANGE_PASSWORD_EVENT:
            Log.d(TAG, "changePasswordEvent: Password changed was not successful");
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                if (errorMessages == null) {
                    return false;
                }
                showFragmentContentContainer();

                List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                if (validateMessages == null || validateMessages.isEmpty()) {
                    validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
                }

                String errorMessage = null;
                if (validateMessages.size() == 0) {
                    return false;
                }

                errorMessage = validateMessages.get(0);
                displayErrorHint(errorMessage);
                showFragmentContentContainer();
                return true;

            }
            return false;
        default:
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        hideKeyboard();
        // Cancel button
        if (id == R.id.button_cancel) finish();
        // Save button
        else if (id == R.id.button_save) changePassword();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickErrorButton(android.view.View)
     */
    @Override
    protected void onClickErrorButton(View view) {
        super.onClickErrorButton(view);
        onClickRetryButton();
    }
    
    /**
     * Process the click on retry button.
     * 
     * @author paulo
     */
    private void onClickRetryButton() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_USER_DATA);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * 
     */
    private void finish() {
        getActivity().onBackPressed();
    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     */
    /*-private void triggerCustomer(){
    	Log.i(TAG,"TRIGGER GET CUSTOMER");
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetCustomerHelper(), bundle, mCallBack);
    }*/

    private void triggerChangePass(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, values);
        triggerContentEvent(new GetChangePasswordHelper(), bundle, mCallBack);
    }

    /**
     * CALLBACK
     * 
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {

        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };
}
