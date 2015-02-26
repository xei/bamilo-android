/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.res.Resources;
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
    
    private static MyAccountFragment myAccountFragment;
    
    private ListView optionsList;
    
    private ListView appSharingList;
    
    /**
     * Get instance
     * 
     * @return
     */
    public static MyAccountFragment getInstance() {
        if (myAccountFragment == null)
            myAccountFragment = new MyAccountFragment();
        return myAccountFragment;
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
        // R.string.account_name
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
        
        // FIXME : The next lines try fix this bug 
        // java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
        View view = getView();
        if (view != null) {
            unbindDrawables(view);
        }
        
        System.gc();
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
        optionsList.setOnItemClickListener((OnItemClickListener) this);
        
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
            handleOnOptionsListItemClick(parent, view, position, id);
        } else if(parent == this.appSharingList){
            handleOnAppSharingListItemClick(parent, view, position, id);
        }
    }
    
    /**
     *  Handles the item click of childs of options list.
     *  
     *  @param parent
     *  @param view
     *  @param position
     *  @param id
     */
    private void handleOnOptionsListItemClick(AdapterView<?> parent, View view, int position, long id) {
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
     *  @param parent
     *  @param view
     *  @param position
     *  @param id
     */
    private void handleOnAppSharingListItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
        case POSITION_SHARE_APP:
            Resources resources = getResources();
            
            String text = "";
            if(resources.getBoolean(R.bool.is_bamilo_specific)){
                text = resources.getString(R.string.share_app_link) + " " + resources.getString(R.string.install_jumia_android);
            } else {
                text = resources.getString(R.string.install_jumia_android)+ " " + resources.getString(R.string.share_app_link);
            }
            
            ActivitiesWorkFlow.startActivitySendString(getBaseActivity(), resources.getString(R.string.share_the_app), text) ;
            break;
        default:
            break;
        }
      
        AnalyticsGoogle.get().trackShareApp(TrackingEvent.SHARE_APP, 
                (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getId()+"":"");
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
