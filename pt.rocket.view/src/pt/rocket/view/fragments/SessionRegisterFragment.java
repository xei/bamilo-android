/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import org.holoeverywhere.FontLoader;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.RegisterAccountEvent;
import pt.rocket.framework.event.events.StoreEvent;
import pt.rocket.framework.forms.Form;
import pt.rocket.framework.forms.InputType;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import org.holoeverywhere.widget.TextView;

import com.actionbarsherlock.internal.widget.IcsAdapterView;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SessionRegisterFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create( SessionRegisterFragment.class );

    private static SessionRegisterFragment registerFragment;

    private Bundle savedInstanceState;

    private TextView termsRequiredText;
    
    private boolean termsAreRequired = false;

    private Button registerButton;

    private View loginRedirect;

    private CheckBox checkTerms;

    private TextView registerRequiredText;

    private DynamicForm serverForm;

    private String terms;
    
    // private String username;

    // private String password;
    
    private Form formResponse;

    private LinearLayout container;
    
    private String registerLocation;

    
    /**
     * 
     * @return
     */
    public static SessionRegisterFragment getInstance() {
        if(registerFragment == null)
            registerFragment = new SessionRegisterFragment();
        return registerFragment;
    }
    
    /**
     * 
     */
    public SessionRegisterFragment() {
        super( EnumSet.of(EventType.GET_REGISTRATION_FORM_EVENT, EventType.GET_TERMS_EVENT),
                EnumSet.of(EventType.REGISTER_ACCOUNT_EVENT), EnumSet.noneOf(MyMenuItem.class), 
                NavigationAction.MyAccount, 
                R.string.register_title);
        this.setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        this.savedInstanceState = savedInstanceState;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        View view = inflater.inflate(R.layout.register, container, false);
        return view;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        registerLocation = getString(R.string.mixprop_loginlocation);
        if (formResponse != null)
            loadForm(formResponse);
        else
            triggerContentEvent(EventType.GET_REGISTRATION_FORM_EVENT);
        
        setAppContentLayout();
        getFormComponents();
        setFormComponents();
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
        
        if(container != null) container.removeAllViews();
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if (null != serverForm) {
            
            Iterator<DynamicFormItem> iterator = serverForm.iterator();

            while (iterator.hasNext()) {
                DynamicFormItem item = iterator.next();
                item.saveState(outState);
            }
            if(getView() != null){
                CheckBox check = (CheckBox) getView().findViewById(R.id.checkTerms);
                outState.putBoolean("" + R.id.checkTerms, check.isChecked());
            }
            
            savedInstanceState = outState;
        }
        
    }
    
    
    /**
     * ##### LAYOUT ####
     */
    
    /**
     * Inflate this layout
     */
    public void setAppContentLayout() {
        //triggerContentEvent(EventType.GET_REGISTRATION_FORM_EVENT);
        
        CheckBox check = (CheckBox) getView().findViewById( R.id.checkTerms );
        check.setPadding(check.getPaddingLeft(), check.getPaddingTop(), check.getPaddingRight(), check.getPaddingBottom());
        termsRequiredText = (TextView) getView().findViewById( R.id.termsRequired);
        
        if (!termsAreRequired) {
            EventManager.getSingleton().triggerRequestEvent(new RequestEvent(EventType.GET_TERMS_EVENT));
        } else {
            View termsContainer = getView().findViewById( R.id.termsContainer );
            termsContainer.setVisibility( View.GONE );
        }

    }
    
    
    /**
     * Get Components
     */
    private void getFormComponents() {
        registerButton = (Button) getView().findViewById(R.id.register_button_submit);
        loginRedirect = getView().findViewById(R.id.orLoginContainer);
        checkTerms = (CheckBox) getView().findViewById(R.id.checkTerms);
        registerRequiredText = (TextView) getView().findViewById( R.id.register_required_text );
        
        registerButton.setTextAppearance(getActivity(), R.style.text_normal);
        FontLoader.apply(registerButton, FontLoader.ROBOTO_BOLD);

        OnClickListener click = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.checkTerms) {
                    if (serverForm != null && serverForm.checkRequired() && checkTerms.isChecked()) {
                        termsRequiredText.setVisibility(View.GONE );
                        registerButton.setTextAppearance(getActivity(), R.style.text_bold);
                        FontLoader.apply(registerButton, FontLoader.ROBOTO_BOLD);
                        
                    } else {
                        registerButton.setTextAppearance(getActivity(), R.style.text_normal);
                        FontLoader.apply(registerButton, FontLoader.ROBOTO_REGULAR);
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
     * #### LISTENERS ####
     */
    
    /**
     * Set Submit button Listener
     */
    private void setSubmitButton() {
        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.d( TAG, "registerButton onClick" );
                
                if ( serverForm != null && !serverForm.checkRequired()) {
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
                    ((BaseActivity) getActivity()).hideKeyboard();
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
                    getActivity().onBackPressed();
                    //getActivity().setResult( Activity.RESULT_CANCELED);
                    //getActivity().finish();
                    //getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    Log.d( TAG, "register canceled via login click" );
                }
            }
        });
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

    /**
     * 
     */
    IcsAdapterView.OnItemSelectedListener selected_listener = new IcsAdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
            checkInputFields();
        }

        @Override
        public void onNothingSelected(IcsAdapterView<?> parent) {
        }
    };
    
    
    /**
     * 
     */
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
     * Sets the listener to handle the expand and colapse of the terms and conditions
     */
    private void detailsListener() {
        if(getView()!=null){
            View termsContainer = getView().findViewById(R.id.termsContainerClick);
            if(termsContainer == null)
                return;
            
            termsContainer.setOnClickListener(new OnClickListener() {
                
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    if (id == R.id.termsContainerClick) {
                        Log.d(TAG, "terms click");
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantsIntentExtra.TERMS_CONDITIONS, terms);
                        ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.TERMS, bundle, FragmentController.ADD_TO_BACK_STACK);
                    }
                    
                }
            });
        }

   
    }

    
    
    /**
     * #### CHECKS ####
     */
    
    /**
     * 
     * @return
     */
    private boolean checkTermsIfRequired() {
        return !termsAreRequired || checkTerms.isChecked();
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
                        item.ShowError( getActivity().getResources().getString( R.string.form_passwordsnomatch ) );
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * This method validates if all necessary input fields are filled
     */
    private void checkInputFields() {
        registerButton = (Button) getView().findViewById(R.id.register_button_submit);

        if (serverForm.checkRequired() && checkTermsIfRequired()) {
            // Log.d( TAG, "checkInputFieds: check passed" );
            registerRequiredText.setVisibility( View.GONE );
            registerButton.setTextAppearance(getActivity(), R.style.text_bold);
            FontLoader.apply( registerButton, FontLoader.ROBOTO_BOLD );
        } else {
            // Log.d( TAG, "checkInputFieds: check not passed" );
            registerButton.setTextAppearance(getActivity(), R.style.text_normal);
            FontLoader.apply( registerButton, FontLoader.ROBOTO_REGULAR );
        }
    }
    
    /**
     * #### EVENT SUPPORT ####
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
    
    
    /**
     * 
     * @return
     */
    private Bundle saveFormToBundle( ) {
        Bundle bundle = new Bundle();
        for(DynamicFormItem entry : serverForm) {
            bundle.putString( entry.getKey(), entry.getValue());
        }        
        return bundle;
    }
    
    
    /**
     * #### EVENTS ####
     */

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.getType()) {
        case REGISTER_ACCOUNT_EVENT:
            // Get Register Completed Event
            Customer customer = (Customer) event.result;
            TrackerDelegator.trackSignupSuccessful(getActivity(), customer, registerLocation);
            // Finish this activity
            //Intent resultData = new Intent();
            //resultData.putExtras(saveFormToBundle());
            //getActivity().setResult(Activity.RESULT_OK, resultData);
            requestStore(saveFormToBundle());
            // Finish
            getActivity().onBackPressed();
            Log.d(TAG, "event done - REGISTER_ACCOUNT_EVENT");
            return false;
        case GET_REGISTRATION_FORM_EVENT:
            Form form = (Form) event.result;
            Log.d(TAG, "getRegistrationFormCompleted: form = " + form.toJSON());
            if (null != form) {
                this.formResponse = form;
                loadForm(form);
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
    
    private void requestStore(Bundle bundle) {
        Log.d(TAG, "requestLogin: trigger LogInEvent for store only");
        if (serverForm == null) {
            return;
        }
        ContentValues values = new ContentValues();
        for (DynamicFormItem item : serverForm) {
            String value = bundle.getString(item.getKey());
            values.put(item.getName(), value);
        }
        values.put(CustomerAccountService.INTERNAL_AUTOLOGIN_FLAG, true);
        EventManager.getSingleton().triggerRequestEvent(new StoreEvent(EventType.STORE_LOGIN, values));
    }
    
    /**
     * 
     * @param form
     */
    private void loadForm(Form form) {
    
        serverForm = FormFactory.getSingleton().CreateForm(FormConstants.REGISTRATION_FORM,
                getActivity(), form);
        serverForm.setOnFocusChangeListener(focus_listener);
        serverForm.setOnItemSelectedListener(selected_listener);
        serverForm.setTextWatcher(text_watcher);
        container = (LinearLayout) getView().findViewById(R.id.registerform_container);
        container.removeAllViews();
        container.addView(serverForm.getContainer());
        if (null != this.savedInstanceState && null != serverForm) {
            Iterator<DynamicFormItem> iter = serverForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                item.loadState(savedInstanceState);
            }
            CheckBox check = (CheckBox) getView().findViewById(R.id.checkTerms);
            check.setChecked(this.savedInstanceState.getBoolean("" + R.id.checkTerms));
        }

    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (event.getType() == EventType.REGISTER_ACCOUNT_EVENT) {
            TrackerDelegator.trackSignupFailed();
            if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                List<String> errorMessages = event.errorMessages.get(RestConstants.JSON_ERROR_TAG);
                if (errorMessages != null
                        && errorMessages.contains(Errors.CODE_REGISTER_CUSTOMEREXISTS)) {
                    ((BaseActivity) getActivity()).showContentContainer();
                    dialog = DialogGenericFragment.newInstance(true, true, false,
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
                    dialog.show(getActivity().getSupportFragmentManager(), null);
                    return true;
                }
            }
        }
        return false;
    }
    
    
    /**
     * #### FUNTIONS NOT USED ON REGISTER ACTIVITY ####
     */

    /**
     * 
     */
//    private void saveCredentialsFromForm() {
//        EditText userView = (EditText) serverForm.getItemByKey( "email" ).getEditControl();
//        username = userView.getText().toString();
//        
//        EditText passwordView = (EditText) serverForm.getItemByKey( "password" ).getEditControl();
//        password = passwordView.getText().toString();
//    }
    
    
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

}
