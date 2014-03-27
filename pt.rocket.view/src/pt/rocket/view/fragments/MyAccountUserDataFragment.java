/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetChangePasswordHelper;
import pt.rocket.helpers.GetCustomerHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
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
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class MyAccountUserDataFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(MyAccountUserDataFragment.class);

    private View mainView;
    
    private final static int PASSWORD_MINLENGTH = 6;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText emailText;
    
    private EditText newPasswordText;
    private EditText newPassword2Text;
    private TextView passwordErrorHint;
    
//    private OnFragmentActivityInteraction mCallbackMyAccountUserDataFragment;
    
    private static MyAccountUserDataFragment myAccountFragment;
    
    
    /**
     * Get instance
     * 
     * @return
     */
    public static MyAccountUserDataFragment getInstance() {
        // if (myAccountFragment == null)
        myAccountFragment = new MyAccountUserDataFragment();
        return myAccountFragment;
    }

    /**
     * Empty constructor
     */
    public MyAccountUserDataFragment() {
        super(EnumSet.of(EventType.GET_CUSTOMER), 
                EnumSet.of(EventType.CHANGE_PASSWORD_EVENT), 
                EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.MyAccount, 
                R.string.personal_data_title);
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
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
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
        
        mainView = inflater.inflate(R.layout.my_account_user_data_fragment, container, false);
        setAppContentLayout();
        init();
        return mainView;
    }

    private void init() {
        
        /**
         * TRIGGERS
         * @author sergiopereira
         */
        triggerCustomer();
        //triggerContentEvent(EventType.GET_CUSTOMER);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY");
    }
    
    /**
     * Inflates this activity layout into the main template layout
     */
    public void setAppContentLayout() {
        Button saveButton = (Button) mainView.findViewById(R.id.button_save);
        saveButton.setOnClickListener(this);
        Button cancelButton = (Button) mainView.findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(this);

        firstNameText = (EditText) mainView.findViewById(R.id.clientFirstName);
        lastNameText = (EditText) mainView.findViewById(R.id.clientLastName);
        lastNameText.setVisibility(View.GONE);
        emailText = (EditText) mainView.findViewById(R.id.clientEmail);
        
        newPasswordText = (EditText) mainView.findViewById(R.id.typeNewPassword);
        newPassword2Text = (EditText) mainView.findViewById(R.id.retypeNewPassword);
        passwordErrorHint = (TextView) mainView.findViewById(R.id.passwordErrorHint);
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
        
        /**
         * TRIGGERS
         * @author sergiopereira
         */
        triggerChangePass(values);
        //triggerContentEvent(new ChangePasswordEvent(values));
        
        displayErrorHint(null);
        
//        mCallbackMyAccountUserDataFragment.sendValuesToActivity(0, values);
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

    protected boolean onSuccessEvent(Bundle bundle) {
        if(!isVisible()){
            return true;
        }
        Log.i(TAG, "ON SUCCESS EVENT");
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        
        switch (eventType) {
        case CHANGE_PASSWORD_EVENT:
            Log.d(TAG, "changePasswordEvent: Password changed with success");
            if (null != getActivity() ) {
                Toast.makeText(getActivity(), getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
            }
            finish();
            
            return true;
        case GET_CUSTOMER:
            Customer customer = (Customer) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            JumiaApplication.INSTANCE.CUSTOMER = customer;
            Log.d(TAG, "CUSTOMER: " + customer.getLastName() + " " + customer.getFirstName() + " " + customer.getEmail());
            if ( null != lastNameText ) {
                lastNameText.setText(customer.getLastName());
                firstNameText.setText(customer.getFirstName());
                emailText.setText(customer.getEmail());
                getBaseActivity().showContentContainer();
            } else {
                restartAllFragments();
                finish();
            }
            return true;
        default:
            return false;
        }
    }

    protected boolean onErrorEvent(Bundle bundle) {
        Log.i(TAG, "ON ERROR EVENT");
        if(!isVisible()){
            return true;
        }
        
        if(getBaseActivity().handleErrorEvent(bundle)){
            return true;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        
        switch (eventType) {
        case CHANGE_PASSWORD_EVENT:
            Log.d(TAG, "changePasswordEvent: Password changed was not successful");
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                if (errorMessages == null) {
                    return false;
                }
                ((BaseActivity) getActivity()).showContentContainer();

                List<String> validateMessages = errorMessages.get(RestConstants.JSON_VALIDATE_TAG);
                if (validateMessages == null || validateMessages.isEmpty()) {
                    validateMessages = errorMessages.get(RestConstants.JSON_ERROR_TAG);
                }

                String errorMessage = null;
                if (validateMessages.size() == 0) {
                    return false;
                }

                errorMessage = validateMessages.get(0);
                displayErrorHint(errorMessage);
                ((BaseActivity) getActivity()).showContentContainer();
                return true;

            }
            return false;
        default:
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        hideKeyboard();
        if (id == R.id.button_cancel) {
            finish();
        } else if (id == R.id.button_save) {
            changePassword();
        }
    }
    
    private void finish(){
        getActivity().onBackPressed();
    }
    
    
    /**
     * TRIGGERS
     * @author sergiopereira
     */
    private void triggerCustomer(){
        Bundle bundle = new Bundle();
        triggerContentEvent(new GetCustomerHelper(), bundle, mCallBack);
    }
    
    private void triggerChangePass(ContentValues values) {
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
            onErrorEvent(bundle);
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };
}
