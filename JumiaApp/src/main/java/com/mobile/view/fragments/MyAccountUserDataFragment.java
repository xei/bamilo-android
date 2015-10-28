package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.account.GetChangePasswordFormHelper;
import com.mobile.helpers.account.GetUserDataFormHelper;
import com.mobile.helpers.account.SetChangePasswordHelper;
import com.mobile.helpers.account.SetUserDataHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

/**
 * Class that shows the personal information of the user, ant let's him change it
 *
 * @author Paulo Carvalho
 */
public class MyAccountUserDataFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = MyAccountUserDataFragment.class.getSimpleName();

    private LinearLayout mUserDataFormContainer;

    private LinearLayout mChangePasswordFormContainer;

    private DynamicForm mUserDataForm;

    private DynamicForm mChangePasswordForm;

    private TextView mSaveUserDataButton;

    private TextView mSavePasswordButton;

    private Bundle mFormSavedState;
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
        // Saved form state
        mFormSavedState = savedInstanceState;
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

    /**
     * Inflates this activity layout into the main template layout
     */
    public void setAppContentLayout(View mainView) {

        mUserDataFormContainer = (LinearLayout) mainView.findViewById(R.id.user_data_container);
        mChangePasswordFormContainer =(LinearLayout) mainView.findViewById(R.id.change_password_layout);

        mSaveUserDataButton = (TextView) mainView.findViewById(R.id.change_password_save_button);
        mSaveUserDataButton.setOnClickListener(this);
        mSavePasswordButton = (TextView) mainView.findViewById(R.id.user_data_save_button);
        mSavePasswordButton.setOnClickListener(this);
    }

    /**
     * call methods to fill layout with forms
     */
    private void init() {
        triggerGetChangePasswordForm();
        triggerGetuserDataForm();
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
        // Case goes to back stack save the state
        Bundle bundle = new Bundle();
        if(mUserDataForm != null) {
            mUserDataForm.saveFormState(bundle);
        }
        if(mChangePasswordForm != null) {
            mChangePasswordForm.saveFormState(bundle);
        }
        mFormSavedState = bundle;
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
        if (mUserDataForm != null) {
            mUserDataForm.saveFormState(outState);
        }
        if (mChangePasswordForm != null) {
            mChangePasswordForm.saveFormState(outState);
        }
    }


    /**
     * Method responsible for showing warning bar with error validation message
     * @param hint
     * @param isUserDataError
     */
    private void displayErrorHint(String hint, boolean isUserDataError) {
        getBaseActivity().warningFactory.showWarning(isUserDataError ? WarningFactory.USER_DATA_VALIDATION : WarningFactory.CHANGE_PASSWORD_VALIDATION, hint);
    }

    /**
     * function used to fill the layout section with the user data form
     * @param userForm
     */
    private void fillUserDataForm(Form userForm){
        mUserDataForm = FormFactory.getSingleton().CreateForm(FormConstants.USER_DATA_FORM,getBaseActivity(),userForm);
        // Load saved state
        mUserDataForm.loadSaveFormState(mFormSavedState);
        mUserDataFormContainer.addView(mUserDataForm.getContainer());
    }

    /**
     * function used to fill the layout section with the change password form
     * @param passwordForm
     */
    protected void fillChangePasswordForm(Form passwordForm) {
        mChangePasswordForm = FormFactory.getSingleton().CreateForm(FormConstants.CHANGE_PASSWORD_FORM,getBaseActivity(),passwordForm);
        // Load saved state
        mChangePasswordForm.loadSaveFormState(mFormSavedState);
        mChangePasswordFormContainer.addView(mChangePasswordForm.getContainer());
    }
    protected boolean onSuccessEvent(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "ON SUCCESS EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return false;
        }

        switch (eventType) {
            case GET_CHANGE_PASSWORD_FORM_EVENT:
                Form passwordForm = (Form)baseResponse.getMetadata().getData();
                fillChangePasswordForm(passwordForm);
                Print.i(TAG, "GET CHANGE PASSWORD FORM");
                return true;
            case EDIT_USER_DATA_FORM_EVENT:
                Form userForm = (Form)baseResponse.getMetadata().getData();
                fillUserDataForm(userForm);
                showFragmentContentContainer();
                Print.i(TAG, "GET USER DATA FORM");
                return true;
            case CHANGE_PASSWORD_EVENT:
                Print.d(TAG, "changePasswordEvent: Password changed with success");
                if (null != getActivity()) {
                    getBaseActivity().warningFactory.showWarning(WarningFactory.CHANGE_PASSWORD_SUCCESS);
                    showFragmentContentContainer();
                }
                return true;
            case EDIT_USER_DATA_EVENT:
                Print.d(TAG, "editUserEvent: user data edit with success fsdfsdffd ");
                if (null != getActivity()) {
                    getBaseActivity().warningFactory.showWarning(WarningFactory.USER_DATA_SUCCESS);
                    showFragmentContentContainer();
                }
                return true;
            default:
                return false;
        }
    }

    protected boolean onErrorEvent(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return false;
        }

        if (super.handleErrorEvent(baseResponse)) {
            return true;
        }

        ErrorCode errorCode = baseResponse.getError().getErrorCode();

        switch (eventType) {
            case GET_CHANGE_PASSWORD_FORM_EVENT:
                onErrorFormEvent();
                return true;
            case EDIT_USER_DATA_FORM_EVENT:
                onErrorFormEvent();
                return true;
            case CHANGE_PASSWORD_EVENT:
                Print.d(TAG, "changePasswordEvent: Password changed was not successful");
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    Map<String, List<String>> errorMessages = baseResponse.getErrorMessages();
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
                    displayErrorHint(errorMessage, false);
                    showFragmentContentContainer();
                    return true;

                }
                return false;
            case EDIT_USER_DATA_EVENT:
                Print.d(TAG, "EditUserData: Edit user was not successful");
                if (errorCode == ErrorCode.REQUEST_ERROR) {
                    Map<String, List<String>> errorMessages = baseResponse.getErrorMessages();
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
                    displayErrorHint(errorMessage, true);
                    showFragmentContentContainer();
                    return true;

                }
                return false;
            default:
                return false;
        }
    }

    protected void onErrorFormEvent() {
        showFragmentErrorRetry();
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_USER_DATA);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * TRIGGERS
     **/

    /**
     *  method that changes the user data
     */
    private void triggerChangeUserData() {
        if (mUserDataForm.validate()) {
            ContentValues values = mUserDataForm.save();
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
            triggerContentEvent(new SetUserDataHelper(), bundle, this);
        }
    }

    /**
     * This method changes the user's password.
     */
    public void triggerChangePassword() {
        if (mChangePasswordForm.validate()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(Constants.BUNDLE_DATA_KEY, mChangePasswordForm.save());
            triggerContentEvent(new SetChangePasswordHelper(), bundle, this);
        }
    }

    private void triggerGetChangePasswordForm(){
        triggerContentEvent(new GetChangePasswordFormHelper(), null, this);
    }

    private void triggerGetuserDataForm(){
        triggerContentEvent(new GetUserDataFormHelper(), null, this);
    }
    /**
     * CALLBACK
     */


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        hideKeyboard();
        // Cancel button
        if (id == R.id.user_data_save_button) triggerChangeUserData();
            // Save button
        else if (id == R.id.change_password_save_button) triggerChangePassword();
    }


    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        onSuccessEvent(baseResponse);

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        onErrorEvent(baseResponse);

    }

}
