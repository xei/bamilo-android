package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.account.GetChangePasswordFormHelper;
import com.mobile.helpers.account.SetChangePasswordHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author sergiopereira
 * 
 */
public class MyAccountUserDataFragment extends BaseFragment {

    private static final String TAG = MyAccountUserDataFragment.class.getSimpleName();

    private TextView firstNameText;

    private TextView lastNameText;

    private TextView emailText;

    private DynamicForm dynamicForm;

    private TextView passwordErrorHint;

    /**
     * Get instance
     */
    public static MyAccountUserDataFragment getInstance() {
        return new MyAccountUserDataFragment();
    }

    /**
     * Empty constructor
     */
    public MyAccountUserDataFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccountUserData,
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
        if (null != JumiaApplication.CUSTOMER) {
            setAppContentLayout(view);
            init();
        } else {
            showFragmentErrorRetry();
        }
    }
    
    private void init() {
        if (null != lastNameText) {
            lastNameText.setText(JumiaApplication.CUSTOMER.getLastName());
            firstNameText.setText(JumiaApplication.CUSTOMER.getFirstName());
            emailText.setText(JumiaApplication.CUSTOMER.getEmail());
            triggerGetChangePasswordForm();
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
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE");
    }

    /**
     * Inflates this activity layout into the main template layout
     */
    public void setAppContentLayout(View mainView) {
        Button saveButton = (Button) mainView.findViewById(R.id.button_save);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) mainView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(this);

        firstNameText = (TextView) mainView.findViewById(R.id.clientFirstName);
        lastNameText = (TextView) mainView.findViewById(R.id.clientLastName);
        lastNameText.setVisibility(View.GONE);
        emailText = (TextView) mainView.findViewById(R.id.clientEmail);

        passwordErrorHint = (TextView) mainView.findViewById(R.id.passwordErrorHint);
        passwordErrorHint.setVisibility(View.GONE);
    }

    /**
     * This method changes the user's password.
     */
    public void changePassword() {
        if(!checkPasswords()){
            displayErrorHint(getString(R.string.form_passwordsnomatch));
        } else if (!dynamicForm.validate()) {
            displayErrorHint(getString(R.string.password_new_mincharacters));
        } else {
            triggerChangePass(dynamicForm.save());
            displayErrorHint(null);
        }
    }

    /**
     * This method checks if both passwords inserted match
     *
     * @return true if yes false if not
     */
    private boolean checkPasswords() {
        boolean result = true;
        Iterator<DynamicFormItem> iterator = dynamicForm.getIterator();
        String old = "";
        while (iterator.hasNext()) {
            DynamicFormItem item = iterator.next();
            if (item.getType() == FormInputType.password) {
                if (TextUtils.isEmpty(old)) {
                    old = item.getValue();
                } else {
                    result &= old.equals(item.getValue());
                }
            }
        }
        return result;
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
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Print.d(TAG, "ON SUCCESS EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return false;
        }

        switch (eventType) {
        case GET_CHANGE_PASSWORD_FORM_EVENT:
            onSuccessGetChangePasswordFormEvent(bundle);
            return true;
        case CHANGE_PASSWORD_EVENT:
            Print.d(TAG, "changePasswordEvent: Password changed with success");
            if (null != getActivity()) {
                Toast.makeText(getActivity(), getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
            }
            gotoBack();
            return true;
        default:
            return false;
        }
    }

    protected boolean onErrorEvent(Bundle bundle) {
        Print.i(TAG, "ON ERROR EVENT");
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return false;
        }

        if (super.handleErrorEvent(bundle)) {
            return true;
        }

        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        switch (eventType) {
            case GET_CHANGE_PASSWORD_FORM_EVENT:
                onErrorGetChangePasswordFormEvent(bundle);
                return true;
        case CHANGE_PASSWORD_EVENT:
            Print.d(TAG, "changePasswordEvent: Password changed was not successful");
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                if (errorMessages == null) {
                    return false;
                }
                showFragmentContentContainer();

                List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                if (validateMessages == null || validateMessages.isEmpty()) {
                    validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
                }

                String errorMessage;
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

    protected void onErrorGetChangePasswordFormEvent(Bundle bundle) {
        showFragmentErrorRetry();
    }

    protected void onSuccessGetChangePasswordFormEvent(Bundle bundle) {
        Form form = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.CHANGE_PASSWORD_FORM,getBaseActivity(),form);
        ((ViewGroup)getView().findViewById(R.id.changePasswordLayout)).addView(dynamicForm.getContainer());
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        hideKeyboard();
        // Cancel button
        if (id == R.id.button_cancel) gotoBack();
        // Save button
        else if (id == R.id.button_save) changePassword();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
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
    private void gotoBack() {
        getActivity().onBackPressed();
    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     */
    private void triggerChangePass(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new SetChangePasswordHelper(), bundle, mCallBack);
    }

    private void triggerGetChangePasswordForm(){
        triggerContentEvent(new GetChangePasswordFormHelper(), null, mCallBack);
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
