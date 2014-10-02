/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.FormConstants;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.session.GetForgotPasswordFormHelper;
import pt.rocket.helpers.session.GetForgotPasswordHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SessionForgotPasswordFragment extends BaseFragment implements OnClickListener{

    private static final String TAG = LogTagHelper.create(SessionForgotPasswordFragment.class);

    private static SessionForgotPasswordFragment forgotPasswordFragment;

    protected DynamicForm dynamicForm;

    private LinearLayout container;

    private Form formResponse;

    private Bundle savedInstanceState;

    /**
     * 
     * @return
     */
    public static SessionForgotPasswordFragment getInstance() {
        if (forgotPasswordFragment == null) {
            forgotPasswordFragment = new SessionForgotPasswordFragment();
        }
        return forgotPasswordFragment;
    }

    /**
     * Empty constructor
     */
    public SessionForgotPasswordFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.layout.forgotpassword,
                R.string.forgotpass_header,
                KeyboardState.ADJUST_CONTENT);
        // R.string.forgotpass_header
        this.setRetainInstance(true);
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
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        dynamicForm = null;
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
    // * android.view.ViewGroup, android.os.Bundle)
    // */
    // @Override
    // public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
    // savedInstanceState) {
    // super.onCreateView(inflater, container, savedInstanceState);
    // Log.i(TAG, "ON CREATE VIEW");
    // View view = inflater.inflate(R.layout.forgotpassword, container, false);
    // return view;
    // }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        //Validate is service is available
        if(JumiaApplication.mIsBound){
            
            if (formResponse != null) {
                displayForm(formResponse);
            }
            else{
                triggerForgotForm();
            }
            
        } else {
            showFragmentRetry(this);
        }

            


        setAppContentLayout();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != dynamicForm) {
            Iterator<DynamicFormItem> iterator = dynamicForm.iterator();
            while (iterator.hasNext()) {
                DynamicFormItem item = iterator.next();
                item.saveState(outState);
            }
            savedInstanceState = outState;
        }
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
        if (container != null) {
            try {
                container.removeAllViews();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
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
     * Inflates the required layout for this activity into the main activity template
     */
    private void setAppContentLayout() {
        Button buttons = (Button) getView().findViewById(R.id.submit_button);
        buttons.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (id == R.id.submit_button) {
                    if (dynamicForm.validate()) {
                        requestPassword();
                    }
                }
            }
        });
    }

    /**
     * 
     */
    private void requestPassword() {
        ContentValues values = dynamicForm.save();
        triggerForgot(values);
    }

    /**
     * 
     */
    private void displayForm(Form form) {
        Log.d(TAG, "DISPLAY FORM");
        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.FORGET_PASSWORD_FORM, getActivity(), form);
        DynamicFormItem item = dynamicForm.getItemByKey("email");
        if (item == null)
            return;
        if (item.getEditControl() != null) {
            ((EditText) item.getEditControl()).setHint(getString(R.string.forgotten_password_examplemail));
        }
        if (getView() == null) {
            Log.e(TAG, "NO VIEW - SWITCHING TO HOME");
            restartAllFragments();
            // getActivity().finish();
            return;
        }
        container = (LinearLayout) getView().findViewById(R.id.form_container);
        container.addView(dynamicForm.getContainer());

        // Show save state
        if (null != this.savedInstanceState && null != dynamicForm) {
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while (iter.hasNext()) {
                DynamicFormItem dItem = iter.next();
                dItem.loadState(savedInstanceState);
            }
        }
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        Log.d(TAG, "ON SUCCESS EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        showFragmentContentContainer();
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        switch (eventType) {
        case INIT_FORMS:
        case GET_FORGET_PASSWORD_FORM_EVENT:
            Log.d(TAG, "FORGET_PASSWORD_FORM");
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            if (null != form) {
                this.formResponse = form;
                displayForm(form);
            }
            break;
        case FORGET_PASSWORD_EVENT:
            Log.i(TAG, "FORGET_PASSWORD_EVENT successful");
            dialog = DialogGenericFragment.newInstance(
                    true, true, false, 
                    getString(R.string.forgotten_password_resulttitle),
                    getString(R.string.forgotten_password_successtext), 
                    getString(R.string.ok_label), 
                    "",
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            if (id == R.id.button1) {
                                dialog.dismiss();
                            }
                        }
                    });
            dialog.show(getActivity().getSupportFragmentManager(), null);
            break;
        default:
            break;
        }
        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        Log.d(TAG, "ON ERROR EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }

        if (getBaseActivity().handleErrorEvent(bundle)) {
            return true;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        // ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);

        if (eventType == EventType.FORGET_PASSWORD_EVENT) {
            Log.d(TAG, "FORGET_PASSWORD_EVENT");

            HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            List<String> errorMessages = null;
            if (errors != null) {
                errorMessages = (List<String>) errors.get(RestConstants.JSON_VALIDATE_TAG);
            }
            if (errors != null && errorMessages != null && errorMessages.size() > 0) {
                showFragmentContentContainer();
                dialog = DialogGenericFragment.newInstance(true, true, false,
                        getString(R.string.error_forgotpassword_title),
                        errorMessages.get(0),
                        getString(R.string.ok_label),
                        "",
                        new OnClickListener() {
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
        return false;
    }

    /**
     * TRIGGERS
     * 
     * @author sergiopereira
     */
    private void triggerForgotForm() {
        triggerContentEvent(new GetForgotPasswordFormHelper(), null, mCallBack);
    }

    private void triggerForgot(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetForgotPasswordHelper.CONTENT_VALUES, values);
        triggerContentEvent(new GetForgotPasswordHelper(), bundle, mCallBack);
        getBaseActivity().hideKeyboard();
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
    
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.fragment_root_retry_button) {
            Bundle bundle = new Bundle();
            bundle.putString(ConstantsIntentExtra.LOGIN_ORIGIN, getString(R.string.mixprop_loginlocationmyaccount));
            getBaseActivity().onSwitchFragment(FragmentType.FORGOT_PASSWORD, bundle, FragmentController.ADD_TO_BACK_STACK);

        }
    }
}