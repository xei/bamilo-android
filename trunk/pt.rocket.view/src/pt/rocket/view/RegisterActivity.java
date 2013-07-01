package pt.rocket.view;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.holoeverywhere.FontLoader;

import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.factories.FormFactory;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.RegisterAccountEvent;
import pt.rocket.framework.forms.Form;
import pt.rocket.framework.forms.InputType;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.internal.widget.IcsAdapterView;

import de.akquinet.android.androlog.Log;

/**
 * <p>This class manages user registration.</p>
 * <p/>
 * <p>Copyright (C) 2012 Rocket Internet - All Rights Reserved</p>
 * <p/>
 * <p>Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential. Written by sergiopereira, 19/06/2012.</p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author sergiopereira
 * @modified nunocastro
 * 
 * @date 19/06/2012
 * 
 * @description
 * 
 */

public class RegisterActivity extends MyActivity {

    private final static String TAG = LogTagHelper.create(RegisterActivity.class);

    //
    private Handler handleComponent;
    //
    private Activity activity;

    // Form Components
    private DynamicForm serverForm;

    private Button registerButton;
    private View loginRedirect;
    int mEndHeight = 0;
    boolean mActive;
    private CheckBox checkTerms;
    private Context context;
    private String terms;
    private Bundle savedInstanceState;
    
    private String username;
    private String password;

	private TextView registerRequiredText;

	private TextView termsRequiredText;

	private boolean termsAreRequired = false;
    
    /**
	 * 
	 */
	public RegisterActivity() {
		super(NavigationAction.MyAccount,
		        EnumSet.noneOf(MyMenuItem.class),
		        EnumSet.of(EventType.GET_REGISTRATION_FORM_EVENT,
		                EventType.GET_TERMS_EVENT),
		                EnumSet.of(EventType.REGISTER_ACCOUNT_EVENT),
		                R.string.register_title, R.layout.register);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Save This
        activity = this;
        context = getApplicationContext();

        this.savedInstanceState = savedInstanceState;

        setAppContentLayout();
        getFormComponents();
        setFormComponents();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (null != serverForm) {
            
            Iterator<DynamicFormItem> iterator = serverForm.iterator();
            Entry<String, DynamicFormItem> entry;

            while (iterator.hasNext()) {
                DynamicFormItem item = iterator.next();
                item.saveState(outState);
            }

            CheckBox check = (CheckBox) findViewById(R.id.checkTerms);
            outState.putBoolean("" + R.id.checkTerms, check.isChecked());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FlurryTracker.get().begin(this);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        FlurryTracker.get().end(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        AnalyticsGoogle.get().trackPage( R.string.gregister );
    }

    /*
     * ################### # Activity Layout # ###################
     */

    @Override
    protected void onDestroy() {
		super.onDestroy();

			activity=null;
			unbindDrawables(findViewById(R.id.rocket_app_register));
			System.gc();
			
    }

    /**
     * Get Components
     */
    private void getFormComponents() {
        registerButton = (Button) findViewById(R.id.register_button_submit);
        loginRedirect = findViewById(R.id.orLoginContainer);
        checkTerms = (CheckBox) findViewById(R.id.checkTerms);
        registerRequiredText = (TextView) findViewById( R.id.register_required_text );
        
        registerButton.setTextAppearance(context, R.style.text_normal);
        FontLoader.apply( registerButton );

        OnClickListener click = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
				if (id == R.id.checkTerms) {
					if (serverForm.checkRequired() && checkTerms.isChecked()) {
						termsRequiredText.setVisibility(View.GONE );
						registerButton.setTextAppearance(context, R.style.text_bold);
                        FontLoader.apply( registerButton );
                        
                    } else {
                        registerButton.setTextAppearance(context, R.style.text_normal);
                        FontLoader.apply( registerButton );
                    }
				}
            }
        };

        checkTerms.setOnClickListener(click);
    }

    /**
     * Set Components
     */
    private void setFormComponents() {
        setSubmitButton();
        setLoginListener();
    }

    /**
     * Inflate this layout
     */
    public void setAppContentLayout() {
        triggerContentEvent(EventType.GET_REGISTRATION_FORM_EVENT);
        
        CheckBox check = (CheckBox) findViewById( R.id.checkTerms );
        check.setPadding(check.getPaddingLeft(), check.getPaddingTop(), check.getPaddingRight(), check.getPaddingBottom());
        termsRequiredText = (TextView) findViewById( R.id.termsRequired);
        
        if (termsAreRequired) {
        	EventManager.getSingleton().triggerRequestEvent(new RequestEvent(EventType.GET_TERMS_EVENT));
        } else {
            View termsContainer = findViewById( R.id.termsContainer );
        	termsContainer.setVisibility( View.GONE );
        }

    }

    /**
     * Set Submit button Listener
     */
    private void setSubmitButton() {
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	Log.d( TAG, "registerButton onClick" );
            	
            	if ( !serverForm.checkRequired()) {
                	registerRequiredText.setVisibility( View.VISIBLE );
                	return;
            	} else {
            		registerRequiredText.setVisibility( View.GONE );
            	}
            		
            	if ( checkPasswords() && serverForm.validate() && checkTermsIfRequired()) {
                	registerRequiredText.setVisibility( View.GONE );
                	termsRequiredText.setVisibility( View.GONE );
                	requestRegister();
            	} else if ( !checkTermsIfRequired()) {
                	termsRequiredText.setVisibility( View.VISIBLE );
                }  else {
                    hideKeyboard();
                }
                    
                
            }
        });
    }

    /**
     * Sets the listener to the login button
     */
    private void setLoginListener() {

        loginRedirect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
				if (id == R.id.orLoginContainer) {
				    setResult( Activity.RESULT_CANCELED);
	                finish();
	                overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_right);
	                Log.d( TAG, "register canceled via login click" );
				}
            }
        });
    }
    
    private boolean checkTermsIfRequired() {
    	return !termsAreRequired || checkTerms.isChecked();
    }

    /**
     * Sets the listener to handle the expand and colapse of the terms and conditions
     */
    private void detailsListener() {
        View termsContainer = findViewById(R.id.termsContainerClick);
        termsContainer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                int id = v.getId();
				if (id == R.id.termsContainerClick) {
					Log.d(TAG, "terms click");
					ActivitiesWorkFlow.termsActivity(activity, terms);
				}
                
            }
        });

   
    }

    /**
     * Measures a text against a text textview size to determine if the text will fit
     * 
     * @param v
     *            The textview to measure against
     * @param text
     *            The text to measure
     * @param width
     *            the max width it can have
     * @return True, if the textsize is bigger than the width; False, if the textsize is smaller than the width
     */
    public boolean isToBig(TextView v, String text, int width) {

        return (v.getPaint().measureText(text) > width);
    }

    /*
     * ################# # Darwin Access # #################
     */
    /**
     * Request a register
     * 
     */
    void requestRegister() {
        // Create Event Manager
        ContentValues values = serverForm.save();
        triggerContentEvent(new RegisterAccountEvent(values));
    }
    
    private void saveCredentialsFromForm() {
    	EditText userView = (EditText) serverForm.getItemByKey( "email" ).getEditControl();
    	username = userView.getText().toString();
    	
    	EditText passwordView = (EditText) serverForm.getItemByKey( "password" ).getEditControl();
    	password = passwordView.getText().toString();
    }
    
    private Bundle saveFormToBundle( ) {
        Bundle bundle = new Bundle();
        for(DynamicFormItem entry : serverForm) {
            bundle.putString( entry.getKey(), entry.getValue());
        }        
        return bundle;
    }

    /**
     * create the listener to monitor the changes to the mandatory fields, to enable the register button when all the mandatory fields are filled
     */
    OnFocusChangeListener focus_listener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                checkInputFields();
            }
        }
    };

    IcsAdapterView.OnItemSelectedListener selected_listener = new IcsAdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
            checkInputFields();
        }

        @Override
        public void onNothingSelected(IcsAdapterView<?> parent) {
        }
    };

    TextWatcher text_watcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            checkInputFields();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

    };

    /**
     * This method validates if all necessary input fields are filled
     */
    private void checkInputFields() {
        registerButton = (Button) findViewById(R.id.register_button_submit);

        if (serverForm.checkRequired() && checkTermsIfRequired()) {
        	// Log.d( TAG, "checkInputFieds: check passed" );
        	registerRequiredText.setVisibility( View.GONE );
            registerButton.setTextAppearance(context, R.style.text_bold);
            FontLoader.apply( registerButton, FontLoader.HoloFont.ROBOTO_BOLD );
        } else {
        	// Log.d( TAG, "checkInputFieds: check not passed" );
            registerButton.setTextAppearance(context, R.style.text_normal);
            FontLoader.apply( registerButton );
        }
        
    }
    
    /**
     * This method checks if both passwords inserted match
     * @return true if yes false if not
     */
    private boolean checkPasswords() {
        boolean result = true;
        
        Iterator<DynamicFormItem> iter = serverForm.getIterator();        
        String old = "";
        
        while ( iter.hasNext() ) {
            DynamicFormItem item = iter.next();            
            if ( item.getType() == InputType.password ) {
                if ( old.equals("") ) {
                    old = (String) item.getValue();
                } else {
                    result &= old.equals(item.getValue());
                    if ( !result ) {
                        item.ShowError( getApplicationContext().getResources().getString( R.string.form_passwordsnomatch ) );
                    }
                }
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.getType()) {
        case REGISTER_ACCOUNT_EVENT:
            // Get Register Completed Event
            Customer customer = (Customer) event.result;
            TrackerDelegator.trackSignupSuccessful(getApplicationContext(), customer);
            // Finish this activity
            Intent resultData = new Intent();
            resultData.putExtras(saveFormToBundle());
            setResult(Activity.RESULT_OK, resultData);
            finish();
            overridePendingTransition(R.anim.slide_in_left,
                    R.anim.slide_out_right);
            Log.d(TAG, "event done - REGISTER_ACCOUNT_EVENT");
            return false;
        case GET_REGISTRATION_FORM_EVENT:
            Form form = (Form) event.result;
            Log.d(TAG, "getRegistrationFormCompleted: form = " + form.toJSON());
            if (null != form) {
                serverForm = FormFactory.getSingleton().CreateForm(FormConstants.REGISTRATION_FORM,
                        activity, form);
                serverForm.setOnFocusChangeListener(focus_listener);
                serverForm.setOnItemSelectedListener(selected_listener);
                serverForm.setTextWatcher(text_watcher);

                LinearLayout container = (LinearLayout) findViewById(R.id.registerform_container);
                container.addView(serverForm.getContainer());

                if (null != this.savedInstanceState && null != serverForm) {
                    Iterator<DynamicFormItem> iter = serverForm.getIterator();
                    while( iter.hasNext()) {
                        DynamicFormItem item = iter.next();
                        item.loadState(savedInstanceState);
                    }

                    CheckBox check = (CheckBox) findViewById(R.id.checkTerms);
                    check.setChecked(this.savedInstanceState.getBoolean("" + R.id.checkTerms));

                }
            }
            break;
        case GET_TERMS_EVENT:
            terms = (String) event.result;
            // Remove the listener
            detailsListener();
            break;
        }
        return true;
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (event.getType() == EventType.REGISTER_ACCOUNT_EVENT) {
            TrackerDelegator.trackSignupFailed();
            if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                List<String> errorMessages = event.errorMessages.get(Errors.JSON_ERROR_TAG);
                if (errorMessages != null
                        && errorMessages.contains(Errors.CODE_REGISTER_CUSTOMEREXISTS)) {
                    showContentContainer();
                    dialog = new DialogGeneric(this, true, true, false,
                            getString(R.string.error_register_title),
                            getString(R.string.error_register_alreadyexists),
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
        }
        return false;
    }

}
