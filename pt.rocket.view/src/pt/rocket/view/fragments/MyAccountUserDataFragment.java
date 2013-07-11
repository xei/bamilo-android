/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.MyAccountAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.ChangePasswordEvent;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.OnFragmentActivityInteraction;
import pt.rocket.view.MyAccountUserDataActivityFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class MyAccountUserDataFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(MyAccountUserDataFragment.class);

    private View mainView;
    
    private final static int PASSWORD_MINLENGTH = 6;
    private TextView firstNameText;
    private TextView lastNameText;
    private TextView emailText;
    
    private TextView newPasswordText;
    private TextView newPassword2Text;
    private TextView passwordErrorHint;
    
    private OnFragmentActivityInteraction mCallbackMyAccountUserDataFragment;
    
    private static MyAccountUserDataFragment myAccountFragment;
    
    
    /**
     * Get instance
     * 
     * @return
     */
    public static MyAccountUserDataFragment getInstance() {
        if (myAccountFragment == null)
            myAccountFragment = new MyAccountUserDataFragment();
        return myAccountFragment;
    }

    /**
     * Empty constructor
     */
    public MyAccountUserDataFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
    }
    
    @Override
    public void sendValuesToFragment(int identifier, Object values) {
        if(identifier == MyAccountUserDataActivityFragment.SEND_ERROR) {
            displayErrorHint((String) values);
        } else {
            Customer customer = (Customer) values;
            lastNameText.setText(customer.getLastName());
            firstNameText.setText(customer.getFirstName());
            emailText.setText(customer.getEmail());
        }
    }
    
    private void startFragmentCallbacks() {
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackMyAccountUserDataFragment = (OnFragmentActivityInteraction) getActivity();

        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnActivityFragmentInteraction");
        }

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
        startFragmentCallbacks();
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
        return mainView;
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

        firstNameText = (TextView) mainView.findViewById(R.id.clientFirstName);
        lastNameText = (TextView) mainView.findViewById(R.id.clientLastName);
        lastNameText.setVisibility(View.GONE);
        emailText = (TextView) mainView.findViewById(R.id.clientEmail);
        
        newPasswordText = (TextView) mainView.findViewById(R.id.typeNewPassword);
        newPassword2Text = (TextView) mainView.findViewById(R.id.retypeNewPassword);
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
        triggerContentEvent(new ChangePasswordEvent(values));
        displayErrorHint(null);
        
        mCallbackMyAccountUserDataFragment.sendValuesToActivity(0, values);
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

    
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();

        if (id == R.id.button_cancel) {
            getActivity().finish();
        } else if (id == R.id.button_save) {
            changePassword();
        }

    }
}
