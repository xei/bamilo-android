/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.constants.FormConstants;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.factories.FormFactory;
import com.mobile.helpers.configs.GetStaticPageHelper;
import com.mobile.helpers.session.GetRegisterFormHelper;
import com.mobile.helpers.session.RegisterHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.ErrorCode;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.forms.NewsletterOption;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.Errors;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.pojo.DynamicForm;
import com.mobile.pojo.DynamicFormItem;
import com.mobile.preferences.CustomerPreferences;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author sergiopereira
 * 
 */
public class SessionRegisterFragment extends BaseFragment {

    private static final String TAG = SessionRegisterFragment.class.getSimpleName();

    private Button registerButton;

    private CheckBox rememberEmailCheck;

    private TextView loginText;

    private CheckBox checkTerms;

    private TextView linkText;

    private TextView mandatory;

    private static DynamicForm serverForm;

    private DynamicFormItem termsLink;

    private DynamicFormItem newsletterSubscribe;

    private LinearLayout container;

    /**
     * 
     * @return
     */
    public static SessionRegisterFragment getInstance(Bundle bundle) {
        SessionRegisterFragment fragment = new SessionRegisterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty Constructor
     */
    public SessionRegisterFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.LoginOut,
                R.layout.register,
                R.string.register_title,
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
     * @see com.mobile.view.fragments.MyFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments();
        if (arguments != null) {
            // Force load form if comes from deep link
            String path = arguments.getString(ConstantsIntentExtra.DEEP_LINK_TAG);
            if (path != null && path.equals(DeepLinkManager.TAG)) {
                JumiaApplication.INSTANCE.registerForm = null;
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        TrackerDelegator.trackPage(TrackingPage.REGISTRATION, getLoadTime(), false);
        // Used for UG
        forceInputAlignToLeft();
        if (JumiaApplication.INSTANCE.registerForm != null) {
            Print.d(TAG, " ON RESUME -> load From");
            loadForm(JumiaApplication.INSTANCE.registerForm);
            JumiaApplication.INSTANCE.registerSavedInstanceState = null;
        } else {
            triggerRegisterForm();
        }

        getFormComponents();
        setFormComponents();
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

        if (container != null) {
            try {
                container.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.d(TAG, "  -----> ON SAVE INSTANCE STATE !!!!!!!!!");
        if (null != serverForm) {

            for (DynamicFormItem item : serverForm) {
                item.saveState(outState);
            }

            // TODO use an alternative to persist filled fields on rotation
            JumiaApplication.INSTANCE.registerSavedInstanceState = outState;
        }
        super.onSaveInstanceState(outState);
    }

    public void saveFormState() {
        if (null != serverForm) {
            Print.d(TAG, "  -----> SAVE FORM STATE <--------- ");

            if (JumiaApplication.INSTANCE.registerSavedInstanceState == null) {
                JumiaApplication.INSTANCE.registerSavedInstanceState = new Bundle();
            }

            for (DynamicFormItem item : serverForm) {
                item.saveState(JumiaApplication.INSTANCE.registerSavedInstanceState);
            }
        }
    }

    /**
     * ##### LAYOUT ####
     */

    /**
     * Get Components
     */
    private void getFormComponents() {
        registerButton = (Button) getView().findViewById(R.id.register_button_submit);
        rememberEmailCheck = (CheckBox) getView().findViewById( R.id.login_remember_user_email);
        loginText = (TextView) getView().findViewById(R.id.loginText);
        // checkTerms = (CheckBox) getView().findViewById(R.id.checkTerms);

        registerButton.setTextAppearance(getActivity(), R.style.text_normal_programatically);
        HoloFontLoader.apply(registerButton, HoloFontLoader.ROBOTO_REGULAR);

        // checkTerms.setOnClickListener(click);
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
                Print.d(TAG, "registerButton onClick");
                if (checkPasswords() && serverForm.validate() && checkTermsIfRequired()) {
                    getBaseActivity().hideKeyboard();
                    requestRegister();
                } else if (!checkTermsIfRequired()) {
                    mandatory.setVisibility(View.VISIBLE);
                    getBaseActivity().hideKeyboard();
                    // Tracking signup failed
                    TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
                } else {
                    getBaseActivity().hideKeyboard();
                    // Tracking signup failed
                    TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
                }
            }
        });
    }

    /**
     * Sets the listener to the login button
     */
    private void setLoginListener() {
        loginText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                Print.d(TAG, "register canceled via login click");
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
            // saveFormState();
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
     * #### CHECKS ####
     */

    /**
     * 
     * @return
     */
    private boolean checkTermsIfRequired() {
        if (termsLink != null && checkTerms != null) {
            return !termsLink.getMandatory() || checkTerms.isChecked();
        }

        return true;
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
            if (item.getType() == FormInputType.password) {
                if (old.equals("")) {
                    old = item.getValue();
                } else {
                    result &= old.equals(item.getValue());
                    if (!result) {
                        item.ShowError(getString(R.string.form_passwordsnomatch));
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
            Print.w(TAG, "CHECK INPUT FIELDS VIEW IS NULL!");
            return;
        }
        registerButton = (Button) getView().findViewById(R.id.register_button_submit);

        if (serverForm.checkRequired() && checkTermsIfRequired()) {
            // Log.d( TAG, "checkInputFieds: check passed" );
            registerButton.setTextAppearance(getActivity(), R.style.text_bold_programatically);
            HoloFontLoader.apply(registerButton, HoloFontLoader.ROBOTO_BOLD);
        } else {
            // Log.d( TAG, "checkInputFieds: check not passed" );
            registerButton.setTextAppearance(getActivity(), R.style.text_normal_programatically);
            HoloFontLoader.apply(registerButton, HoloFontLoader.ROBOTO_REGULAR);
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
        String newsletterType = getSubscribeType();
        if (newsletterType != null && values.containsKey(newsletterSubscribe.getName())) {
            values.remove(newsletterSubscribe.getName());
            values.put(newsletterSubscribe.getName(), newsletterType);
        }
        triggerRegister(values);
    }

    /**
     * #### EVENTS ####
     */

    protected boolean onSuccessEvent(Bundle bundle) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        if (getBaseActivity() != null) {
            super.handleSuccessEvent(bundle);
        } else {
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case REGISTER_ACCOUNT_EVENT:
            
            try {
                if(((CheckBox) newsletterSubscribe.getEditControl()).isChecked()) TrackerDelegator.trackNewsletterGTM("", GTMValues.REGISTER);
            } catch (NullPointerException | ClassCastException e) {
                e.printStackTrace();
            }

            showFragmentContentContainer();
            // Get Register Completed Event
            Customer customer = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.CUSTOMER = customer;
            Bundle params = new Bundle();
            params.putParcelable(TrackerDelegator.CUSTOMER_KEY, customer);
            params.putString(TrackerDelegator.LOCATION_KEY, GTMValues.REGISTER);
            TrackerDelegator.trackSignupSuccessful(params);

            JumiaApplication.INSTANCE.registerForm = null;
            JumiaApplication.INSTANCE.registerSavedInstanceState = null;

            // Persist user email or empty that value after successfully login
            CustomerPreferences.setRememberedEmail(getBaseActivity(), rememberEmailCheck.isChecked() ? customer.getEmail() : null);

            // Finish
            getActivity().onBackPressed();
            Print.d(TAG, "event done - REGISTER_ACCOUNT_EVENT");
            return false;
        case GET_REGISTRATION_FORM_EVENT:
            showFragmentContentContainer();
            Form form = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            Print.d(TAG, "getRegistrationFormCompleted: form = " + (form == null ? "null" : form.toJSON()));
            if (null != form) {
                JumiaApplication.INSTANCE.registerForm = form;
                loadForm(form);
            }
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * 
     * @param form
     */
    private void loadForm(Form form) {
        serverForm = FormFactory.getSingleton().CreateForm(FormConstants.REGISTRATION_FORM, getActivity(), form);
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
        setTermsListener();
        // TODO use an alternative to persist filled fields on rotation
        if (null != JumiaApplication.INSTANCE.registerSavedInstanceState && null != serverForm) {
            Iterator<DynamicFormItem> iter = serverForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem item = iter.next();
                item.loadState(JumiaApplication.INSTANCE.registerSavedInstanceState);
            }
        }

        showFragmentContentContainer();
    }

    private String getSubscribeType() {
        String result = null;
        newsletterSubscribe = serverForm.getItemByKey(RestConstants.JSON_NEWSLETTER_CATEGORIES_SUBSCRIBED_TAG);
        if (newsletterSubscribe == null) {
            return result;
        }

        ArrayList<NewsletterOption> newsletterOptions = serverForm.getForm().getFieldKeyMap().get(RestConstants.JSON_NEWSLETTER_CATEGORIES_SUBSCRIBED_TAG).getNewsletterOptions();

        if (newsletterSubscribe.getEditControl() != null && ((CheckBox) newsletterSubscribe.getEditControl()).isChecked()) {
            DynamicFormItem genderForm = serverForm.getItemByKey(RestConstants.JSON_GENDER_TAG);
            if (newsletterOptions != null) {
                for (NewsletterOption newsletterOption : newsletterOptions) {
                    if (newsletterOption.label.toLowerCase().contains(genderForm.getValue().toLowerCase())) {
                        result = newsletterOption.value;
                        return result;
                    }
                }
            }
        }
        return result;
    }

    private void setTermsListener() {
        termsLink = serverForm.getItemByKey(RestConstants.JSON_TERMS_TAG);
        if (termsLink == null) {
            return;
        }

        mandatory = (TextView) termsLink.getMandatoryControl();

        linkText = (TextView) termsLink.getEditControl().findViewWithTag(
                RestConstants.JSON_TERMS_TAG);
        linkText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFormState();
                Bundle bundle = new Bundle();
                bundle.putString(RestConstants.JSON_KEY_TAG, GetStaticPageHelper.TERMS_PAGE);
                bundle.putString(RestConstants.JSON_TITLE_TAG, getString(R.string.terms_and_conditions));
                getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);

            }
        });

        checkTerms = (CheckBox) termsLink.getEditControl().findViewWithTag("checkbox");
        checkTerms.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(termsLink.hasRules()) {
                    if (((CheckBox) v).isChecked()) {
                        mandatory.setVisibility(View.GONE);
                    } else {
                        mandatory.setVisibility(View.VISIBLE);
                    }
                }

                if (serverForm != null && serverForm.checkRequired()) {
                    registerButton.setTextAppearance(getActivity(), R.style.text_bold_programatically);
                    HoloFontLoader.apply(registerButton, HoloFontLoader.ROBOTO_BOLD);
                } else {
                    registerButton.setTextAppearance(getActivity(), R.style.text_normal_programatically);
                    HoloFontLoader.apply(registerButton, HoloFontLoader.ROBOTO_REGULAR);
                }
            }
        });

    }

    protected boolean onErrorEvent(Bundle bundle) {
        Print.d(TAG, "ON ERROR EVENT");
        
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        if (super.handleErrorEvent(bundle)) {
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (eventType == EventType.REGISTER_ACCOUNT_EVENT) {
            TrackerDelegator.trackSignupFailed(GTMValues.REGISTER);
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                // Log.i(TAG, "code1exists : errorMessages : "+errorMessages);
                List<String> validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
                // Log.i(TAG, "code1exists : validateMessages : "+validateMessages);
                if (validateMessages != null && validateMessages.contains(Errors.CODE_REGISTER_CUSTOMEREXISTS)) {
                    showFragmentContentContainer();
                    dialog = DialogGenericFragment.newInstance(true, false,
                            getString(R.string.error_register_title),
                            getString(R.string.error_register_alreadyexists),
                            getString(R.string.ok_label), 
                            "", 
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dismissDialogFragment();
                                    }
                                }
                            });
                    dialog.show(getActivity().getSupportFragmentManager(), null);
                    return true;
                } else {
                    showFragmentContentContainer();
                    dialog = DialogGenericFragment.newInstance(true, false,
                            getString(R.string.error_register_title),
                            getString(R.string.incomplete_alert),
                            getString(R.string.ok_label), 
                            "", 
                            new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dismissDialogFragment();
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

//    /**
//     * #### FUNCTIONS NOT USED ON REGISTER ACTIVITY ####
//     */
//
//    /**
//     * Measures a text against a text textview size to determine if the text will fit
//     *
//     * @param v
//     *            The textview to measure against
//     * @param text
//     *            The text to measure
//     * @param width
//     *            the max width it can have
//     * @return True, if the textsize is bigger than the width; False, if the textsize is smaller
//     *         than the width
//     */
//    public boolean isToBig(TextView v, String text, int width) {
//        return (v.getPaint().measureText(text) > width);
//    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     * @param values
     */
    private void triggerRegister(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEvent(new RegisterHelper(), bundle, mCallBack);
    }

    private void triggerRegisterForm() {
        triggerContentEvent(new GetRegisterFormHelper(), null, mCallBack);
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

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClickRetryButton(android.view.View)
     */
    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        onResume();
    }

}