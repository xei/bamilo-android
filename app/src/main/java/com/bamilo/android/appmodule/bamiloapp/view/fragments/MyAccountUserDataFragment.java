package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.factories.FormFactory;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.FormConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.account.GetChangePasswordFormHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.account.GetUserDataFormHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.account.SetChangePasswordHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.account.SetUserDataHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.pojo.DynamicForm;
import com.bamilo.android.framework.service.forms.Form;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.KeyboardUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.WarningFactory;
import com.bamilo.android.R;

import java.util.EnumSet;

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

    private Bundle mFormSavedState;

    private TextView mChangePasswordTitle;

    private TextView mChangePasswordButton;
    /**
     * Empty constructor
     */
    public MyAccountUserDataFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT_USER_DATA,
                R.layout.my_account_user_data_fragment,
                R.string.myaccount_userdata,
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Saved form state
        mFormSavedState = savedInstanceState;
        hideActivityProgress();
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
        if (null != BamiloApplication.CUSTOMER) {
            setAppContentLayout(view);
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
        if (null != BamiloApplication.CUSTOMER) {
            init();
        } else {
            showFragmentErrorRetry();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        // Case goes to back stack save the state
        Bundle bundle = new Bundle();
        if(mUserDataForm != null) {
            mUserDataForm.saveFormState(bundle);
            mFormSavedState = bundle;
        }
        if(mChangePasswordForm != null) {
            mChangePasswordForm.saveFormState(bundle);
            mFormSavedState = bundle;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mUserDataForm != null) {
            // save edited data in user data form
            mUserDataForm.saveFormState(outState);
        }
        if (mChangePasswordForm != null) {
            // save edited data in change password form
            mChangePasswordForm.saveFormState(outState);
        }

        if (mUserDataForm == null && mChangePasswordForm == null) {
            outState = mFormSavedState;
        }
        super.onSaveInstanceState(outState);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * CALLBACK
     */


    @Override
    public void onClick(View view) {
        super.onClick(view);
        int id = view.getId();
        // Hide keyboard
        KeyboardUtils.hide(view);
        // Cancel button
        if (id == R.id.user_data_save_button) {
            triggerChangeUserData();
        }
        // Save button
        else if (id == R.id.change_password_save_button) {
            triggerChangePassword();
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_USER_DATA);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Inflates this activity layout into the main template layout
     */
    public void setAppContentLayout(View mainView) {
        mUserDataFormContainer = (LinearLayout) mainView.findViewById(R.id.user_data_container);
        mChangePasswordFormContainer = (LinearLayout) mainView.findViewById(R.id.change_password_layout);
        mChangePasswordTitle = (TextView) mainView.findViewById(R.id.change_password_title);
        mChangePasswordButton = (TextView) mainView.findViewById(R.id.change_password_save_button);
        mChangePasswordButton.setOnClickListener(this);
        mainView.findViewById(R.id.user_data_save_button).setOnClickListener(this);
    }

    /**
     * call methods to fill layout with forms
     */
    private void init() {
        triggerGetUserDataForm();
        if(!CustomerUtils.isChangePasswordHidden(getBaseActivity())){
            triggerGetChangePasswordForm();
            mChangePasswordFormContainer.setVisibility(View.VISIBLE);
            mChangePasswordTitle.setVisibility(View.VISIBLE);
            mChangePasswordButton.setVisibility(View.VISIBLE);
        } else {
            mChangePasswordFormContainer.setVisibility(View.GONE);
            mChangePasswordTitle.setVisibility(View.GONE);
            mChangePasswordButton.setVisibility(View.GONE);
        }

    }

    /**
     * function used to fill the layout section with the user data form
     */
    private void fillUserDataForm(Form userForm){
        mUserDataFormContainer.removeAllViews();
        mUserDataForm = FormFactory.create(FormConstants.USER_DATA_FORM,getBaseActivity(),userForm);
        // Load saved state
        mUserDataForm.loadSaveFormState(mFormSavedState);
        mUserDataFormContainer.addView(mUserDataForm.getContainer());
    }

    /**
     * TRIGGERS
     **/

    /**
     * function used to fill the layout section with the change password form
     */
    protected void fillChangePasswordForm(Form passwordForm) {
        mChangePasswordForm = FormFactory.create(FormConstants.CHANGE_PASSWORD_FORM,getBaseActivity(),passwordForm);
        // Load saved state
        mChangePasswordForm.loadSaveFormState(mFormSavedState);
        mChangePasswordFormContainer.addView(mChangePasswordForm.getContainer());
    }

    /**
     *  method that changes the user data
     */
    private void triggerChangeUserData() {
        if (mUserDataForm != null && mUserDataForm.validate()) {
            triggerContentEvent(new SetUserDataHelper(), SetUserDataHelper.createBundle(mUserDataForm.getForm().getAction(), mUserDataForm.save()), this);
        }
    }

    /**
     * This method changes the user's password.
     * FIXME: https://rink.hockeyapp.net/manage/apps/33641/app_versions/164/crash_reasons/111735919?type=crashes
     */
    public void triggerChangePassword() {
        if (mChangePasswordForm.validate()) {
            triggerContentEvent(new SetChangePasswordHelper(), SetChangePasswordHelper.createBundle(mChangePasswordForm.getForm().getAction(), mChangePasswordForm.save()), this);
        }
    }

    private void triggerGetChangePasswordForm(){
        triggerContentEvent(new GetChangePasswordFormHelper(), null, this);
    }

    private void triggerGetUserDataForm(){
        triggerContentEvent(new GetUserDataFormHelper(), null, this);
    }



    /**
     * Reset change password form to the initial state (cleaned, eye unchecked and unfocused)
     * */
    private void resetChangePasswordForm(){
        mChangePasswordForm.reset();
        mChangePasswordFormContainer.removeAllViews();
        mChangePasswordFormContainer.addView(mChangePasswordForm.getContainer());
        mChangePasswordFormContainer.requestFocus();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null || getBaseActivity() == null) {
            return;
        }
        // Validate event type
        switch (eventType) {
            case GET_CHANGE_PASSWORD_FORM_EVENT:
                Form passwordForm = (Form)baseResponse.getContentData();
                fillChangePasswordForm(passwordForm);
                break;
            case EDIT_USER_DATA_FORM_EVENT:
                Form userForm = (Form)baseResponse.getContentData();
                fillUserDataForm(userForm);
                showFragmentContentContainer();
                break;
            case CHANGE_PASSWORD_EVENT:
                // Warning user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE, getBaseActivity().getResources().getString(R.string.password_changed));
                // Reset form
                resetChangePasswordForm();
                mUserDataForm.getContainer().requestFocus();
                showFragmentContentContainer();
                break;
            case EDIT_USER_DATA_EVENT:
                // Warning user
                getBaseActivity().showWarningMessage(WarningFactory.SUCCESS_MESSAGE,  getBaseActivity().getResources().getString(R.string.edit_user_success));
                showFragmentContentContainer();
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            return;
        }
        // Call super
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate type
        switch (eventType) {
            case GET_CHANGE_PASSWORD_FORM_EVENT:
            case EDIT_USER_DATA_FORM_EVENT:
                showFragmentErrorRetry();
                break;
            case CHANGE_PASSWORD_EVENT:
                showFragmentContentContainer();
                showFormValidateMessages(mChangePasswordForm, baseResponse, eventType);
                break;
            case EDIT_USER_DATA_EVENT:
                showFragmentContentContainer();
                showFormValidateMessages(mUserDataForm, baseResponse, eventType);
                break;
            default:
                break;
        }

    }

}
