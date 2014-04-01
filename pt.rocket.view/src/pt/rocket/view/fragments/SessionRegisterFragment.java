/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetTermsConditionsHelper;
import pt.rocket.helpers.session.GetRegisterFormHelper;
import pt.rocket.helpers.session.GetRegisterHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.InputType;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.actionbarsherlock.internal.widget.IcsAdapterView;



/**
 * @author sergiopereira
 * 
 */
public class SessionRegisterFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(SessionRegisterFragment.class);

    private static SessionRegisterFragment registerFragment;

    private TextView termsRequiredText;

    private boolean termsAreRequired = false;

    private Button registerButton;

    private View loginRedirect;

    private CheckBox checkTerms;

    private TextView registerRequiredText;

    private static DynamicForm serverForm;

    private String terms;

    private LinearLayout container;

    private String registerLocation;

    // Reinforce locale to avoid RTL on UG
    private Locale mLocale = null;;

    /**
     * 
     * @return
     */
    public static SessionRegisterFragment getInstance() {
        if (registerFragment == null)
            registerFragment = new SessionRegisterFragment();
        return registerFragment;
    }

    /**
     * 
     */
    public SessionRegisterFragment() {
        super(EnumSet.of(EventType.GET_REGISTRATION_FORM_EVENT, EventType.GET_TERMS_EVENT),
                EnumSet.of(EventType.REGISTER_ACCOUNT_EVENT), 
                EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.MyAccount,
                R.string.register_title);
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
     * @see pt.rocket.view.fragments.MyFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        savedInstanceState = savedInstanceState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
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
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        mLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        registerLocation = getString(R.string.mixprop_loginlocation);
        if (JumiaApplication.INSTANCE.registerForm != null){
            loadForm(JumiaApplication.INSTANCE.registerForm);
        } else {

            /**
             * TRIGGERS
             * 
             * @author sergiopereira
             */
            triggerRegisterForm();
        }
        // triggerContentEvent(EventType.GET_REGISTRATION_FORM_EVENT);

        setAppContentLayout();
        getFormComponents();
        setFormComponents();
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
        
        //restore locale
        if(mLocale != null){
            Locale.setDefault(mLocale);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");

        if (container != null){
            try {
                container.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != serverForm) {

            Iterator<DynamicFormItem> iterator = serverForm.iterator();

            while (iterator.hasNext()) {
                DynamicFormItem item = iterator.next();
                item.saveState(outState);
            }
            if (getView() != null) {
                checkTerms = (CheckBox) getView().findViewById(R.id.checkTerms);
                outState.putBoolean("" + R.id.checkTerms, checkTerms.isChecked());
            }
            JumiaApplication.INSTANCE.registerSavedInstanceState = outState;
        }
        super.onSaveInstanceState(outState);
    }
    
    public void saveFormState(){
        if (null != serverForm) {
            if(JumiaApplication.INSTANCE.registerSavedInstanceState == null){
                JumiaApplication.INSTANCE.registerSavedInstanceState = new Bundle();
            }
            Iterator<DynamicFormItem> iterator = serverForm.iterator();

            while (iterator.hasNext()) {
                DynamicFormItem item = iterator.next();
                item.saveState(JumiaApplication.INSTANCE.registerSavedInstanceState);
            }
            if (getView() != null) {
                checkTerms = (CheckBox) getView().findViewById(R.id.checkTerms);
                JumiaApplication.INSTANCE.registerSavedInstanceState.putBoolean("" + R.id.checkTerms, checkTerms.isChecked());
            }
        }
    }

    /**
     * ##### LAYOUT ####
     */

    /**
     * Inflate this layout
     */
    public void setAppContentLayout() {

        checkTerms = (CheckBox) getView().findViewById(R.id.checkTerms);
        checkTerms.setPadding(checkTerms.getPaddingLeft(), checkTerms.getPaddingTop(), checkTerms.getPaddingRight(),
                checkTerms.getPaddingBottom());
        termsRequiredText = (TextView) getView().findViewById(R.id.termsRequired);

        if (!termsAreRequired) {

            /**
             * TRIGGERS
             * 
             * @author sergiopereira
             */
            triggerTerms();

        } else {
            View termsContainer = getView().findViewById(R.id.termsContainer);
            termsContainer.setVisibility(View.GONE);
        }

    }

    /**
     * Get Components
     */
    private void getFormComponents() {
        registerButton = (Button) getView().findViewById(R.id.register_button_submit);
        loginRedirect = getView().findViewById(R.id.orLoginContainer);
        checkTerms = (CheckBox) getView().findViewById(R.id.checkTerms);
        registerRequiredText = (TextView) getView().findViewById(R.id.register_required_text);

        registerButton.setTextAppearance(getActivity(), R.style.text_normal);
        FontLoader.apply(registerButton, FontLoader.ROBOTO_REGULAR);

        OnClickListener click = new OnClickListener() {

            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.checkTerms) {
                    if (serverForm != null && serverForm.checkRequired() && checkTerms.isChecked()) {
                        termsRequiredText.setVisibility(View.GONE);
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
                Log.d(TAG, "registerButton onClick");

                if (serverForm != null && !serverForm.checkRequired()) {
                    registerRequiredText.setVisibility(View.VISIBLE);
                    return;
                } else {
                    registerRequiredText.setVisibility(View.GONE);
                }

                if (checkPasswords() && serverForm.validate() && checkTermsIfRequired()) {
                    getBaseActivity().hideKeyboard();
                    registerRequiredText.setVisibility(View.GONE);
                    termsRequiredText.setVisibility(View.GONE);
                    requestRegister();
                } else if (!checkTermsIfRequired()) {
                    termsRequiredText.setVisibility(View.VISIBLE);
                    getBaseActivity().hideKeyboard();
                } else {
                    getBaseActivity().hideKeyboard();
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
                    // getActivity().setResult( Activity.RESULT_CANCELED);
                    // getActivity().finish();
                    // getActivity().overridePendingTransition(R.anim.slide_in_left,
                    // R.anim.slide_out_right);
                    Log.d(TAG, "register canceled via login click");
                }
            }
        });
    }

    /**
     * create the listener to monitor the changes to the mandatory fields, to enable the register
     * button when all the mandatory fields are filled
     */
    OnFocusChangeListener focus_listener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            saveFormState();
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
        if (getView() != null) {
            View termsContainer = getView().findViewById(R.id.termsContainerClick);
            if (termsContainer == null)
                return;

            termsContainer.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    if (id == R.id.termsContainerClick) {
                        Log.d(TAG, "terms click");
                        saveFormState();
                        Bundle bundle = new Bundle();
                        bundle.putString(ConstantsIntentExtra.TERMS_CONDITIONS, terms);
                        ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.TERMS, bundle,
                                FragmentController.ADD_TO_BACK_STACK);
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
     * 
     * @return true if yes false if not
     */
    private boolean checkPasswords() {
        boolean result = true;

        Iterator<DynamicFormItem> iter = serverForm.getIterator();
        String old = "";

        while (iter.hasNext()) {
            DynamicFormItem item = iter.next();
            if (item.getType() == InputType.password) {
                if (old.equals("")) {
                    old = (String) item.getValue();
                } else {
                    result &= old.equals(item.getValue());
                    if (!result) {
                        item.ShowError(getActivity().getResources().getString(
                                R.string.form_passwordsnomatch));
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
        if (getView() == null) {
            Log.w(TAG, "CHECK INPUT FIELDS VIEW IS NULL!");
            return;
        }
        registerButton = (Button) getView().findViewById(R.id.register_button_submit);

        if (serverForm.checkRequired() && checkTermsIfRequired()) {
            // Log.d( TAG, "checkInputFieds: check passed" );
            registerRequiredText.setVisibility(View.GONE);
            registerButton.setTextAppearance(getActivity(), R.style.text_bold);
            FontLoader.apply(registerButton, FontLoader.ROBOTO_BOLD);
        } else {
            // Log.d( TAG, "checkInputFieds: check not passed" );
            registerButton.setTextAppearance(getActivity(), R.style.text_normal);
            FontLoader.apply(registerButton, FontLoader.ROBOTO_REGULAR);
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

        /**
         * TRIGGERS
         * 
         * @author sergiopereira
         */
        triggerRegister(values);
        // triggerContentEvent(new RegisterAccountEvent(values));
    }

    /**
     * 
     * @return
     */
    private Bundle saveFormToBundle() {
        Bundle bundle = new Bundle();
        for (DynamicFormItem entry : serverForm) {
            bundle.putString(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    /**
     * #### EVENTS ####
     */


    protected boolean onSuccessEvent(Bundle bundle) {
        if(getBaseActivity() != null){
            getBaseActivity().handleSuccessEvent(bundle);
        } else {
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case REGISTER_ACCOUNT_EVENT:
            getBaseActivity().showContentContainer();
            // Get Register Completed Event
            Customer customer = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.INSTANCE.CUSTOMER = customer;
            TrackerDelegator.trackSignupSuccessful(getActivity(), customer, registerLocation);
            // Finish this activity
            // Intent resultData = new Intent();
            // resultData.putExtras(saveFormToBundle());
            // getActivity().setResult(Activity.RESULT_OK, resultData);
            requestStore(saveFormToBundle());
            // Finish
            getActivity().onBackPressed();
            Log.d(TAG, "event done - REGISTER_ACCOUNT_EVENT");
            return false;
        case GET_REGISTRATION_FORM_EVENT:
            getBaseActivity().showContentContainer();
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            Log.d(TAG, "getRegistrationFormCompleted: form = " + form.toJSON());
            if (null != form) {
                JumiaApplication.INSTANCE.registerForm = form;
                loadForm(form);
            }
            break;
        case GET_TERMS_EVENT:
            terms = (String) bundle.getString(Constants.BUNDLE_RESPONSE_KEY);
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
        values.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);

        /**
         * TRIGGERS
         * 
         * @author sergiopereira
         */
        triggerStoreLogin(values);
     
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
            try {
                container.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            container.addView(serverForm.getContainer());
            if (null != JumiaApplication.INSTANCE.registerSavedInstanceState && null != serverForm) {
                Iterator<DynamicFormItem> iter = serverForm.getIterator();
                while (iter.hasNext()) {
                    DynamicFormItem item = iter.next();
                    item.loadState(JumiaApplication.INSTANCE.registerSavedInstanceState);
                }
                CheckBox check = (CheckBox) getView().findViewById(R.id.checkTerms);
                check.setChecked(JumiaApplication.INSTANCE.registerSavedInstanceState.getBoolean("" + R.id.checkTerms));
            }
    }

    protected boolean onErrorEvent(Bundle bundle) {
        Log.d(TAG, "ON ERROR EVENT");
        
        if(getBaseActivity().handleErrorEvent(bundle)){
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (eventType == EventType.REGISTER_ACCOUNT_EVENT) {
            TrackerDelegator.trackSignupFailed();
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
//                Log.i(TAG, "code1exists : errorMessages : "+errorMessages);
                List<String> validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
//                Log.i(TAG, "code1exists : validateMessages : "+validateMessages);
                if (validateMessages != null
                        && validateMessages.contains(Errors.CODE_REGISTER_CUSTOMEREXISTS)) {
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
                } else {
                    ((BaseActivity) getActivity()).showContentContainer();
                    dialog = DialogGenericFragment.newInstance(true, true, false,
                            getString(R.string.error_register_title),
                            getString(R.string.incomplete_alert),
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
    // private void saveCredentialsFromForm() {
    // EditText userView = (EditText) serverForm.getItemByKey( "email" ).getEditControl();
    // username = userView.getText().toString();
    //
    // EditText passwordView = (EditText) serverForm.getItemByKey( "password" ).getEditControl();
    // password = passwordView.getText().toString();
    // }

    /**
     * Measures a text against a text textview size to determine if the text will fit
     * 
     * @param v
     *            The textview to measure against
     * @param text
     *            The text to measure
     * @param width
     *            the max width it can have
     * @return True, if the textsize is bigger than the width; False, if the textsize is smaller
     *         than the width
     */
    public boolean isToBig(TextView v, String text, int width) {

        return (v.getPaint().measureText(text) > width);
    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     * @param values
     */
    private void triggerRegister(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetRegisterHelper.REGISTER_CONTENT_VALUES, values);
        triggerContentEvent(new GetRegisterHelper(), bundle, mCallBack);
    }

    private void triggerRegisterForm() {
        triggerContentEvent(new GetRegisterFormHelper(), null, mCallBack);
    }

    private void triggerTerms() {
        triggerContentEventWithNoLoading(new GetTermsConditionsHelper(), null, mCallBack);
    }

    private void triggerStoreLogin(ContentValues values) {
        JumiaApplication.INSTANCE.getCustomerUtils().storeLogin(values);
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
