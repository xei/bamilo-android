/**
 * 
 */
package pt.rocket.view;

import java.util.EnumSet;
import java.util.List;

import pt.rocket.constants.FormConstants;
import pt.rocket.factories.FormFactory;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.ForgetPasswordEvent;
import pt.rocket.framework.forms.Form;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import de.akquinet.android.androlog.Log;

/**
 * 
 * <p>This class manages the forgotten password feature.</p>
 * <p/>
 * <p>Copyright (C) 2012 Rocket Internet - All Rights Reserved</p>
 * <p/>
 * <p>Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. Written by nunocastro, 15/08/2012.</p>
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author nunocastro
 * 
 * 
 * @date 15/08/2012
 * 
 * @description
 * 
 */

public class ForgotPasswordActivity extends MyActivity {

    private final String TAG = LogTagHelper.create( ForgotPasswordActivity.class );

    private Activity activity;
    public Context context;

    private DynamicForm serverForm;
    private LinearLayout container;

    private Dialog dialog;
    
    public ForgotPasswordActivity() {
        super(NavigationAction.MyAccount, EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_FORGET_PASSWORD_FORM_EVENT),
                EnumSet.of(EventType.FORGET_PASSWORD_EVENT),
                R.string.forgotpass_header, R.layout.forgtopassword);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        context = getApplicationContext();
        serverForm = null;
        setAppContentLayout();
    }
     
    @Override
    protected void onDestroy() {
		super.onDestroy();
		
		unbindDrawables(findViewById(R.id.rocket_app_forgotpass));
		System.gc();
    }

    /**
     * Inflates the required layout for this activity into the main activity template
     */
    private void setAppContentLayout() {
        Button buttons = (Button) findViewById(R.id.submit_button);
        buttons.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
				if (id == R.id.submit_button) {
				    if (serverForm.validate()) {
                    	requestPassword();
                    }
				}

            }
        });
        triggerContentEvent(EventType.GET_FORGET_PASSWORD_FORM_EVENT);
    }
    
    private void requestPassword() {
        ContentValues values = serverForm.save();
        triggerContentEvent(new ForgetPasswordEvent(values));
    }
    
    private void displayForm() {
        DynamicFormItem item = serverForm.getItemByKey("email");
        if ( item == null)
            return;
        
        ((EditText)item.getEditControl()).setHint(getString( R.string.forgotten_password_examplemail));                
        container = (LinearLayout) findViewById(R.id.form_container);
        container.addView(serverForm.getContainer());
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.getType()) {
        case GET_FORGET_PASSWORD_FORM_EVENT:
            Form form = (Form) event.result;
            if (null != form) {
                serverForm = FormFactory.getSingleton().CreateForm(
                        FormConstants.FORGET_PASSWORD_FORM, context, form);
                displayForm();
            }
            break;
        case FORGET_PASSWORD_EVENT:
            Log.i(TAG, "FORGET_PASSWORD_EVENT successful");
            dialog = new DialogGeneric(activity,
                    true, true, false, getString(R.string.forgotten_password_resulttitle),
                    getString(R.string.forgotten_password_successtext), getResources()
                            .getString(R.string.ok_label), "",
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dialog.dismiss();
                            }

                        }

                    });
            dialog.show();
            break;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (event.getType() == EventType.FORGET_PASSWORD_EVENT) {
            List<String> errorMessages = event.errorMessages.get(Errors.JSON_ERROR_TAG);
            if (errorMessages != null
                    && errorMessages.contains(Errors.CODE_FORGOTPW_NOSUCH_CUSTOMER)) {
                showContentContainer();
                dialog = new DialogGeneric(this, true, true, false,
                        getString(R.string.error_forgotpassword_title),
                        getString(R.string.error_forgotpassword_text),
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
                return true;
            }
        }
        return false;
    }

}
