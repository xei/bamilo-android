package pt.rocket.view;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.ChangePasswordEvent;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class shows the options that exist in the MyAccount menu.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential. Written by josedourado, 30/06/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author josedourado
 * 
 * @date 30/06/2012
 * 
 * @description
 * 
 */

public class MyAccountUserData extends BaseActivity implements OnClickListener {

    private final static String TAG = LogTagHelper
            .create(MyAccountUserData.class);
    private final static int PASSWORD_MINLENGTH = 6;
    private TextView firstNameText;
    private TextView lastNameText;
    private TextView emailText;
    private TextView newPasswordText;
    private TextView newPassword2Text;
    private TextView passwordErrorHint;

    /**
	 * 
	 */
    public MyAccountUserData() {
        super(NavigationAction.MyAccount,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_CUSTOMER),
                EnumSet.of(EventType.CHANGE_PASSWORD_EVENT),
                R.string.personal_data_title, R.layout.my_account_user_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppContentLayout();
        init();
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init();
    }
    
    private void init() {
        triggerContentEvent(EventType.GET_CUSTOMER);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        AnalyticsGoogle.get().trackPage(R.string.gcustomeraccount);
    }

    /**
     * Inflates this activity layout into the main template layout
     */
    public void setAppContentLayout() {
        Button saveButton = (Button) findViewById(R.id.button_save);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(this);

        firstNameText = (TextView) findViewById(R.id.clientFirstName);
        lastNameText = (TextView) findViewById(R.id.clientLastName);
        lastNameText.setVisibility(View.GONE);
        emailText = (TextView) findViewById(R.id.clientEmail);
        
        newPasswordText = (TextView) findViewById(R.id.typeNewPassword);
        newPassword2Text = (TextView) findViewById(R.id.retypeNewPassword);
        passwordErrorHint = (TextView) findViewById(R.id.passwordErrorHint);
        passwordErrorHint.setVisibility(View.GONE);
    }

    /**
     * This method changes the user's password.
     */
    public void changePassword() {
        
        String newPassword = newPasswordText.getText().toString();
        if (newPassword.length() < PASSWORD_MINLENGTH) {
            displayErrorHint( getString(R.string.password_new_mincharacters));
            return;
        }

        String newPassword2 = newPassword2Text.getText().toString();
        if (!newPassword.equals(newPassword2)) {
            displayErrorHint( getString( R.string.form_passwordsnomatch));
            return;
        }

        ContentValues values = new ContentValues();
        values.put("Alice_Module_Customer_Model_PasswordForm[password]",
                newPassword);
        values.put("Alice_Module_Customer_Model_PasswordForm[password2]",
                newPassword2);
        values.put("Alice_Module_Customer_Model_PasswordForm[email]", emailText.getText()
                .toString());
        triggerContentEvent(new ChangePasswordEvent(values));
        displayErrorHint(null);
    }
    
    private void displayErrorHint( String hint ) {
        if ( hint != null) {
            passwordErrorHint.setText(hint);
            passwordErrorHint.setVisibility( View.VISIBLE );
        } else {
            passwordErrorHint.setText("");
            passwordErrorHint.setVisibility( View.GONE );
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.button_cancel) {
            finish();
        } else if (id == R.id.button_save) {
            changePassword();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.type) {
        case CHANGE_PASSWORD_EVENT:
            Log.d(TAG, "changePasswordEvent: Password changed with success");
            Toast.makeText(this, getString(R.string.password_changed),
                    Toast.LENGTH_SHORT).show();
            finish();
            return false;
        case GET_CUSTOMER:
            Customer customer = (Customer) event.result;
            lastNameText.setText(customer.getLastName());
            firstNameText.setText(customer.getFirstName());
            emailText.setText(customer.getEmail());
            return true;
        default:
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        switch (event.type) {
        case CHANGE_PASSWORD_EVENT:
            Log.d(TAG,
                    "changePasswordEvent: Password changed was not successful");
            if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                List<String> errorMessages = event.errorMessages.get(RestConstants.JSON_ERROR_TAG);
                if ( errorMessages == null) {
                    return false;
                }
                showContentContainer();
                Map<String, ? extends List<String>> messages = event.errorMessages;
                List<String> validateMessages = messages.get(RestConstants.JSON_VALIDATE_TAG);
                if (validateMessages == null || validateMessages.isEmpty()) {
                    validateMessages = messages.get(RestConstants.JSON_ERROR_TAG);
                }
                
                String errorMessage = null;
                if (validateMessages.size() == 0) {
                    return false;
                }

                errorMessage = validateMessages.get(0);
                displayErrorHint(errorMessage);
                showContentContainer();
                return true;

            }
            return false;
        default:
            return false;
        }
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }
    
}
