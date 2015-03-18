/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.AppSharingAdapter;
import com.mobile.controllers.MyAccountAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.tracking.AnalyticsGoogle;
import com.mobile.framework.tracking.TrackingEvent;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class MyAccountFragment extends BaseFragment implements OnItemClickListener{

    private static final String TAG = LogTagHelper.create(MyAccountFragment.class);

    public final static int POSITION_USER_DATA = 0;

    public final static int POSITION_MY_ADDRESSES = 1;

    public final static int POSITION_EMAIL = 2;
    
    public final static int POSITION_SHARE_APP = 0;
    
    private ListView optionsList;
    
    private ListView appSharingList;
    
    /**
     * Get instance
     * 
     * @return
     */
    public static MyAccountFragment getInstance() {
        return new MyAccountFragment();
    }

    /**
     * Empty constructor
     */
    public MyAccountFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MyAccount,
                R.layout.my_account_fragment,
                R.string.account_name,
                KeyboardState.NO_ADJUST_CONTENT);
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
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        showMyAccount(view);
        showAppSharing(view);
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
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
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
     * Shows my account options
     */
    private void showMyAccount(View v) {
        // Get User Account Option
        String[] myAccountOptions = getResources().getStringArray(R.array.myaccount_array);
        // Get ListView
        optionsList = (ListView) v.findViewById(R.id.middle_myaccount_list);
        // Create new Adapter
        MyAccountAdapter myAccountAdapter = new MyAccountAdapter(getActivity(), myAccountOptions);
        // Set adapter
        optionsList.setAdapter(myAccountAdapter);
        // Set Listener for all items
        optionsList.setOnItemClickListener(this);
        
    }
    
    /**
     * Shows app sharing options
     */
    private void showAppSharing(View view) {
        appSharingList = (ListView)view.findViewById(R.id.middle_app_sharing_list);
        appSharingList.setAdapter(new AppSharingAdapter(getActivity(), getResources().getStringArray(R.array.app_sharing_array)));
        appSharingList.setOnItemClickListener(this);
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Validate item
        if(parent == this.optionsList){
            handleOnOptionsListItemClick(position);
        } else if(parent == this.appSharingList){
            handleOnAppSharingListItemClick(position);
        }
    }
    
    /**
     *  Handles the item click of childs of options list.
     *
     * @param position
     */
    private void handleOnOptionsListItemClick(int position) {
        switch (position) {
        case POSITION_USER_DATA:
            processOnClickUserData();
            break;
        case POSITION_MY_ADDRESSES:
            processOnClickMyAddresses();
            break;
        case POSITION_EMAIL:
            processOnClickEmailNotification();
            break;
        default:
            break;
        }
        
    }

    private void processOnClickMyAddresses() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_ACCOUNT_MY_ADDRESSES);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     *  Handles the item click of childs of app sharing list.
     *
     * @param position
     */
    private void handleOnAppSharingListItemClick(int position) {
        switch (position) {
        case POSITION_SHARE_APP:
            String text;
            String preText = getString(R.string.install_jumia_android, getString(R.string.app_name_placeholder));
            if(getResources().getBoolean(R.bool.is_bamilo_specific)){
                text = getString(R.string.share_app_link) + " " + preText;
            } else {
                text = preText + " " + getString(R.string.share_app_link);
            }
            ActivitiesWorkFlow.startActivitySendString(getBaseActivity(), getString(R.string.share_the_app), text) ;
            break;
        default:
            break;
        }
        AnalyticsGoogle.get().trackShareApp(TrackingEvent.SHARE_APP, (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getId()+"":"");
    }

    /**
     * Process the click on the user data
     * @author sergiopereira
     */
    private void processOnClickUserData(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_USER_DATA);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Process the click on the email notification
     * @author sergiopereira
     */
    private void processOnClickEmailNotification(){
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.EMAIL_NOTIFICATION);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
}
