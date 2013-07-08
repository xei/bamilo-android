/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.Iterator;
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
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import pt.rocket.view.SessionFragmentActivity;
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
public class ForgotPasswordFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ForgotPasswordFragment.class);

    private static ForgotPasswordFragment forgotPasswordFragment;

    protected DynamicForm dynamicForm;
    
    private LinearLayout container;

    private Form formResponse;

    private SessionFragmentActivity parentActivity;

    private Bundle savedInstanceState;

    /**
     * 
     * @return
     */
    public static ForgotPasswordFragment getInstance() {
        if(forgotPasswordFragment == null)
            forgotPasswordFragment = new ForgotPasswordFragment();
        return forgotPasswordFragment;
    }

    /**
     * Empty constructor
     */
    public ForgotPasswordFragment() {
        super(EnumSet.of(EventType.GET_FORGET_PASSWORD_FORM_EVENT), EnumSet.of(EventType.FORGET_PASSWORD_EVENT));
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
        parentActivity = (SessionFragmentActivity) activity;
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
        parentActivity.updateActivityHeader(NavigationAction.MyAccount, R.string.forgotpass_header);
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
            triggerContentEvent(EventType.GET_FORGET_PASSWORD_FORM_EVENT);
        
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
        triggerContentEvent(new ForgetPasswordEvent(values));
    }

    
    /**
     * 
     */
    private void displayForm(Form form) {
        dynamicForm = FormFactory.getSingleton().CreateForm(FormConstants.FORGET_PASSWORD_FORM, getActivity(), form);
        DynamicFormItem item = dynamicForm.getItemByKey("email");
        if (item == null)
            return;

        ((EditText) item.getEditControl()).setHint(getString(R.string.forgotten_password_examplemail));
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
    

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.getType()) {
        case GET_FORGET_PASSWORD_FORM_EVENT:
            Form form = (Form) event.result;
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

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.MyFragment#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (event.getType() == EventType.FORGET_PASSWORD_EVENT) {
            List<String> errorMessages = event.errorMessages.get(Errors.JSON_ERROR_TAG);
            if (errorMessages != null
                    && errorMessages.contains(Errors.CODE_FORGOTPW_NOSUCH_CUSTOMER)) {
                ((BaseActivity) getActivity()).showContentContainer();
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
}
