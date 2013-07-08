package pt.rocket.view;

import java.util.EnumSet;

import pt.rocket.app.UrbanAirshipComponent;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.PreferenceListFragment.OnPreferenceAttachedListener;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.MyAccountFragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.util.Log;

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
 * @modified Manuel Silva
 * 
 * @date 30/06/2012
 * 
 * @description
 * 
 */

public class MyAccountActivityFragment extends BaseActivity implements OnPreferenceAttachedListener{

    protected final String TAG = LogTagHelper.create(MyAccountActivityFragment.class);
    
    // Context
    private Context context;

    private MyAccountFragment myAccountFragment;
    
    /**
	 * 
	 */
    public MyAccountActivityFragment() {
        super(NavigationAction.MyAccount,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                R.string.account_name, R.layout.myaccount_frame);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Context
        context = getApplicationContext();
        
        Log.i(TAG, "onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        if(myAccountFragment == null){
            myAccountFragment = MyAccountFragment.getInstance();
            fragmentManagerTransition(R.id.myaccount_frame_container, myAccountFragment, false, true);
        }
        AnalyticsGoogle.get().trackPage(R.string.gaccount);

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

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub
        
    }

}
