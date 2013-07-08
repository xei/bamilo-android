package pt.rocket.view;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.ChangePasswordEvent;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.MyAccountUserDataFragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * <p>
 * This class shows the options that exist in the MyAccount menu.
 * </p>
 * <p/>
 * <p>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential. Written by josedourado, 30/06/2012.
 * </p>
 * 
 * 
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.01
 * 
 * @author josedourado
 * @modified Manuel Silva
 * 
 * @date 30/06/2012
 * 
 * @description
 * 
 */

public class MyAccountUserDataActivityFragment extends BaseActivity {

    private final static String TAG = LogTagHelper
            .create(MyAccountUserDataActivityFragment.class);
    
    private OnActivityFragmentInteraction mCallbackMyAccountUserDataActivityFragment;
    private MyAccountUserDataFragment myAccountUserDataFragment;
    
    private final int SEND_CUSTOMER = 0;
    public final static int SEND_ERROR = 1;

    /**
	 * 
	 */
    public MyAccountUserDataActivityFragment() {
        super(NavigationAction.MyAccount,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_CUSTOMER),
                EnumSet.of(EventType.CHANGE_PASSWORD_EVENT),
                R.string.personal_data_title, R.layout.my_account_user_data_frame);
    }

    @Override
    public void sendValuesToActivity(int identifier, Object values){
        Log.i(TAG, "sendValuesToActivity "+values.toString());
        triggerContentEvent(new ChangePasswordEvent((ContentValues) values));
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        triggerContentEvent(EventType.GET_CUSTOMER);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        setFragment();
        AnalyticsGoogle.get().trackPage(R.string.gcustomeraccount);
    }


    private void setFragment(){
        if(myAccountUserDataFragment == null){
            myAccountUserDataFragment = MyAccountUserDataFragment.getInstance();
            startFragmentCallbacks();
            fragmentManagerTransition(R.id.my_account_user_data_container, myAccountUserDataFragment, false, true);
        }
    }
        
    private void startFragmentCallbacks() {
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallbackMyAccountUserDataActivityFragment = (OnActivityFragmentInteraction) myAccountUserDataFragment;

        } catch (ClassCastException e) {
            throw new ClassCastException(myAccountUserDataFragment.toString()
                    + " must implement OnActivityFragmentInteraction");
        }

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.type) {
        case CHANGE_PASSWORD_EVENT:
            Log.d(TAG, "changePasswordEvent: Password changed with success");
            Toast.makeText(this, getString(R.string.password_changed), Toast.LENGTH_SHORT).show();
            finish();
            return false;
        case GET_CUSTOMER:
            Customer customer = (Customer) event.result;
            mCallbackMyAccountUserDataActivityFragment.sendValuesToFragment(SEND_CUSTOMER, customer);
            return true;
        default:
            return false;
        }
    }
   
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        switch (event.type) {
        case CHANGE_PASSWORD_EVENT:
            Log.d(TAG,
                    "changePasswordEvent: Password changed was not successful");
            if (event.errorCode == ErrorCode.REQUEST_ERROR) {
                List<String> errorMessages = event.errorMessages.get(Errors.JSON_ERROR_TAG);
                if ( errorMessages == null) {
                    return false;
                }
                showContentContainer();
                Map<String, ? extends List<String>> messages = event.errorMessages;
                List<String> validateMessages = messages.get(Errors.JSON_VALIDATE_TAG);
                if (validateMessages == null || validateMessages.isEmpty()) {
                    validateMessages = messages.get(Errors.JSON_ERROR_TAG);
                }
                
                String errorMessage = null;
                if (validateMessages.size() == 0) {
                    return false;
                }

                errorMessage = validateMessages.get(0);
                //displayErrorHint(errorMessage);
                mCallbackMyAccountUserDataActivityFragment.sendValuesToFragment(SEND_ERROR, errorMessage);
                showContentContainer();
                return true;

            }
            return false;
        default:
            return false;
        }
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }
    
}
