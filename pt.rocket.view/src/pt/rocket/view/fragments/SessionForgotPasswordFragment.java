/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import pt.rocket.constants.FormConstants;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetChangePasswordHelper;
import pt.rocket.helpers.GetInitFormHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class SessionForgotPasswordFragment extends BaseFragment {

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
        if(forgotPasswordFragment == null)
            forgotPasswordFragment = new SessionForgotPasswordFragment();
        return forgotPasswordFragment;
    }

    /**
     * Empty constructor
     */
    public SessionForgotPasswordFragment() {
        super(EnumSet.of(EventType.GET_FORGET_PASSWORD_FORM_EVENT), EnumSet.of(EventType.FORGET_PASSWORD_EVENT),EnumSet.noneOf(MyMenuItem.class),NavigationAction.MyAccount,R.string.forgotpass_header);
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
        View view = inflater.inflate(R.layout.forgtopassword, container, false);
        return view;
    }

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
        
        if (formResponse != null)
            displayForm(formResponse);
        else
            /**
             * TRIGGERS
             * @author sergiopereira
             */
            triggerForgotForm();
            //triggerContentEvent(EventType.GET_FORGET_PASSWORD_FORM_EVENT);
        
        setAppContentLayout();
    }
    
    /*
     * (non-Javadoc)
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
    
    /*
     * (non-Javadoc)
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
        
        /**
         * TRIGGERS
         * @author sergiopereira
         */
        triggerForgot(values);
        //triggerContentEvent(new ForgetPasswordEvent(values));
    }

    
    /**
     * 
     */
    private void displayForm(Form form) {
        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.FORGET_PASSWORD_FORM, getActivity(), form);
        DynamicFormItem item = dynamicForm.getItemByKey("email");
        if (item == null)
            return;
        if(item.getEditControl()!=null){
            ((EditText) item.getEditControl()).setHint(getString(R.string.forgotten_password_examplemail));
        }
        if(getView() == null){
            getActivity().finish();
            return;
        }
        container = (LinearLayout) getView().findViewById(R.id.form_container);
        container.addView(dynamicForm.getContainer());
        
        // Show save state
        if (null != this.savedInstanceState && null != dynamicForm) {
            Iterator<DynamicFormItem> iter = dynamicForm.getIterator();
            while( iter.hasNext()) {
                DynamicFormItem dItem = iter.next();
                dItem.loadState(savedInstanceState);
            }
        }
    }
    
    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        switch (eventType) {
        case GET_FORGET_PASSWORD_FORM_EVENT:
            Form form = (Form) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            if (null != form) {
                this.formResponse = form;
                displayForm(form);
            }
            break;
        case FORGET_PASSWORD_EVENT:
            Log.i(TAG, "FORGET_PASSWORD_EVENT successful");
            dialog = DialogGenericFragment.newInstance(
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
            dialog.show(getActivity().getSupportFragmentManager(), null);
            break;
        }
        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        if(isVisible()){
            return true;
        }
        
        if(getBaseActivity().handleErrorEvent(bundle)){
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        if (eventType == EventType.FORGET_PASSWORD_EVENT) {
            List<String> errorMessages = (List<String>) bundle
                    .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            if (errorMessages != null
                    && errorMessages.contains(Errors.CODE_FORGOTPW_NOSUCH_CUSTOMER)) {
                ((BaseActivity) getActivity()).showContentContainer(false);
                dialog = DialogGenericFragment.newInstance(true, true, false,
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
                dialog.show(getActivity().getSupportFragmentManager(), null);
                return true;
            }
        }
        return false;  
    }
    
    
    /**
     * TRIGGERS
     * @author sergiopereira
     */
    private void triggerForgotForm(){
        Bundle bundle = new Bundle();
        if(JumiaApplication.INSTANCE.getFormDataRegistry() != null && JumiaApplication.INSTANCE.getFormDataRegistry().size() > 0){
            triggerContentEvent(new GetInitFormHelper(), bundle, mCallBack);    
        } else {
            bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, ErrorCode.UNKNOWN_ERROR);
            mCallBack.onRequestError(bundle);
        }
        
    }
    
    private void triggerForgot(ContentValues values){
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetChangePasswordHelper.CONTENT_VALUES, values);
        triggerContentEvent(new GetChangePasswordHelper(), bundle, mCallBack);
    }
    
    /**
     * CALLBACK
     * @author sergiopereira
     */
    IResponseCallback mCallBack = new IResponseCallback() {
        
        @Override
        public void onRequestError(Bundle bundle) {
            // TODO
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {
            // TODO
        }
    };
    
}
