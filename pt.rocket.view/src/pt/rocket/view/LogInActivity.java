package pt.rocket.view;

import java.util.EnumSet;
import java.util.List;

import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.factories.FormFactory;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.LogInEvent;
import pt.rocket.framework.event.events.StoreEvent;
import pt.rocket.framework.forms.Form;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.FlurryTracker;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * 
 * <p>
 * This class is responsible for validating the user as a registered user, and allow full access and
 * possibility to buy.
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
 * @author paulocarvalho
 * @modified nunocastro , josedourado
 * 
 * @date 30/06/2012
 * 
 * @description
 * 
 */

public class LogInActivity extends MyActivity {

    protected final String TAG = LogTagHelper.create(LogInActivity.class);

    private final static String FORM_ITEM_EMAIL = "email";
    private final static String FORM_ITEM_PASSWORD = "password";

    private ViewGroup container;
    private DialogGeneric dialog;

    private DynamicForm serverForm;
    private Button signinButton;

    private EditText pass_p;
    private EditText user;
    
    private boolean autoLogin = true;
    private boolean wasAutologin = false;

    /**
	 * 
	 */
    public LogInActivity() {
        super(NavigationAction.LoginOut,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_LOGIN_FORM_EVENT),
                EnumSet.of(EventType.LOGIN_EVENT),
                R.string.login_title, R.layout.login);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signinButton = (Button) findViewById(R.id.middle_login_button_signin);
        wasAutologin = true;
        triggerContentEvent(LogInEvent.TRY_AUTO_LOGIN);
    }

    public void onStart() {
        super.onStart();
        FlurryTracker.get().begin(this);
    }

    public void onStop() {
        super.onStop();
        FlurryTracker.get().end(this);
    }

    public void onResume() {
        super.onResume();
        setLoginFormLayout();
        setLoginBottomLayout();
        AnalyticsGoogle.get().trackPage(R.string.glogin);
    }

    // ################### Layout ###################

    /**
     * Set Login Form Layout.
     */
    private void setLoginFormLayout() {

        signinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (serverForm.validate()) {
                    requestLogin();
                }
            }
        });
    }

    private void requestLogin() {
        Log.d(TAG, "requestLogin: triggerEvent LogInEvent");
        ContentValues values = serverForm.save();
//        if ( autologinCheckBox.isChecked()) {
            values.put(CustomerAccountService.INTERNAL_AUTOLOGIN_FLAG, true);
//        }
        triggerContentEvent(new LogInEvent(values));
        wasAutologin = false;
    }
    
    private void requestStore(Bundle bundle) {
        Log.d( TAG, "requestLogin: trigger LogInEvent for store only" );
        if ( serverForm == null) {
            return;
        }
        
        ContentValues values = new ContentValues();
        for( DynamicFormItem item: serverForm) {
            String value = bundle.getString( item.getKey());
            values.put( item.getName(), value);
        }
        values.put(CustomerAccountService.INTERNAL_AUTOLOGIN_FLAG, true);
       
        EventManager.getSingleton().triggerRequestEvent( new StoreEvent( EventType.STORE_LOGIN, values));        
    }

    /**
     * Set Bottom Layout, links for register and forget password.
     */
    private void setLoginBottomLayout() {
        // Forget TextView
        final TextView forgetPass = (TextView) findViewById(R.id.middle_login_link_fgtpassword);
        // Register TextView
        final TextView register = (TextView) findViewById(R.id.middle_login_link_register);
        // Set ForgetPass link
        forgetPass.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActivitiesWorkFlow.forgotPasswordActivity(LogInActivity.this);
            }
        });

        // Set Register Link
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActivitiesWorkFlow.registerActivity(LogInActivity.this);
            }
        });
    }

    private void loadForm(Form form) {
        serverForm = FormFactory.getSingleton().CreateForm(
                FormConstants.LOGIN_FORM, getApplicationContext(), form);
        container = (ViewGroup) findViewById(R.id.form_container);
        container.addView(serverForm.getContainer());
        setFormClickDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == maskRequestCodeId(R.id.request_register)) {

            if (resultCode == RESULT_OK) {
                requestStore(data.getExtras());
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    /**
     * This method defines the click behavior of the edit texts from the dynamic form, allowing the
     * to login only when the form is completely filled.
     */
    public void setFormClickDetails() {

        DynamicFormItem emailItem = serverForm.getItemByKey(FORM_ITEM_EMAIL);
        if (emailItem == null) {
            return;
        }
        user = (EditText) emailItem.getEditControl();
        user.setId(21);

        DynamicFormItem passwordItem = serverForm.getItemByKey(FORM_ITEM_PASSWORD);
        if (passwordItem == null) {
            return;
        }
        pass_p = (EditText) passwordItem.getEditControl();
        pass_p.setId(22);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        switch (event.type) {
        case LOGIN_EVENT:
            Log.d(TAG, "loginCompletedEvent :" + event.getSuccess());
            // Get Customer
            hideKeyboard();
            setResult(Activity.RESULT_OK);
            finish();
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_out_right);
            TrackerDelegator.trackLoginSuccessful(getApplicationContext(), (Customer) event.result, wasAutologin);
            wasAutologin = false;
            return false;
        case GET_LOGIN_FORM_EVENT:
            Form form = (Form) event.result;
            if (form == null ) {
                dialog = DialogGeneric.createServerErrorDialog(LogInActivity.this, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showLoading();
                        EventManager.getSingleton().triggerRequestEvent(event.request);
                        dialog.dismiss();
                    }
                }, false);
                dialog.show();
                return false;
            }
            
            Log.d(TAG, "Form Loaded");
            loadForm(form);
        }
        return true;        
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (event.getType() == EventType.LOGIN_EVENT) {
            if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                TrackerDelegator.trackLoginFailed(wasAutologin);
                wasAutologin = false;
                if (autoLogin) {
                    autoLogin = false;
                    triggerContentEvent(EventType.GET_LOGIN_FORM_EVENT);
                } else {
                    List<String> errorMessages = event.errorMessages.get(Errors.JSON_ERROR_TAG);
                    if (errorMessages != null && (errorMessages.contains(Errors.CODE_LOGIN_FAILED)
                            || errorMessages.contains(Errors.CODE_LOGIN_CHECK_PASSWORD))) {
                        showContentContainer();
                        dialog = new DialogGeneric(this, true, true, false,
                                getString(R.string.error_login_title),
                                getString(R.string.error_login_check_text),
                                getString(R.string.ok_label), "", new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        int id = v.getId();
                                        if (id == R.id.button1) {
                                            dialog.dismiss();
                                        }

                                    }

                                });
                        dialog.show();
                    }
                }
                return true;
            }
        }
        return false;
    }

}
