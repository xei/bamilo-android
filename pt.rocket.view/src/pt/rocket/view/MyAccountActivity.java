package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.app.UrbanAirshipComponent;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.MyAccountAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.CustomerAccountService;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.PreferenceListFragment.OnPreferenceAttachedListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 
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
 * @author paulocarvalho
 * @modified josedourado
 * 
 * @date 30/06/2012
 * 
 * @description
 * 
 */

public class MyAccountActivity extends MyActivity implements OnPreferenceAttachedListener{

    protected final String TAG = LogTagHelper.create(MyAccountActivity.class);
    private final static int POSITION_USER_DATA = 0;
    private final static int POSITION_ADDRESSES = 1;
    private final static int POSITION_PAYMENT_OPTIONS = 2;

    // Context
    private Context context;
    // Home Options
    private String[] myAccountOptions = null;

    boolean isFirstBoot = true;
    Dialog dialogNoActiveSession;
    private DialogGeneric dialog_need_to_login;

    /**
	 * 
	 */
    public MyAccountActivity() {
        super(NavigationAction.MyAccount,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                R.string.account_name, R.layout.myaccount);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Context
        context = getApplicationContext();
    }

    @Override
    public void onResume() {
        super.onResume();

        showMyAccount();
        AnalyticsGoogle.get().trackPage(R.string.gaccount);

    }

    /**
     * Shows my account options
     */
    private void showMyAccount() {
        // Get User Account Option
        myAccountOptions = getResources().getStringArray(R.array.myaccount_array);
        // Get ListView
        ListView optionsList = (ListView) findViewById(R.id.middle_myaccount_list);
        // Create new Adapter
        MyAccountAdapter myAccountAdpater = new MyAccountAdapter(this, myAccountOptions);
        // Set adapter
        optionsList.setAdapter(myAccountAdpater);
        // Set Listener for all items
        optionsList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Validate item
                if (position == POSITION_USER_DATA) {
                    ActivitiesWorkFlow.loginActivity(MyAccountActivity.this, true);
                }

            }
        });
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.PreferenceListFragment.OnPreferenceAttachedListener#onPreferenceAttached(android.preference.PreferenceScreen, int)
     */
    @Override
    public void onPreferenceAttached(PreferenceScreen root, int xmlId) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        JumiaApplication.COMPONENTS.get(UrbanAirshipComponent.class).setUserPushSettings();
    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == maskRequestCodeId(R.id.request_login)
                && resultCode == Activity.RESULT_OK) {
            ActivitiesWorkFlow.myAccountUserDataActivity(this);
        }
    }
}
