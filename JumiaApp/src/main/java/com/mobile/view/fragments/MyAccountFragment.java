/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.AppSharingSettingsAdapter;
import com.mobile.controllers.ChooseLanguageController;
import com.mobile.controllers.CountrySettingsAdapter;
import com.mobile.controllers.MyAccountAdapter;
import com.mobile.controllers.MyAccountMoreInfoAdapter;
import com.mobile.controllers.MyAccountSettingsAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.configs.GetFaqTermsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.TargetHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class MyAccountFragment extends BaseFragment implements MyAccountAdapter.OnItemClickListener, IResponseCallback {

    private static final String TAG = MyAccountFragment.class.getSimpleName();

    private static final String TARGETS_TAG = MobileAbout.class.getSimpleName();

    public final static int POSITION_USER_DATA = 0;

    public final static int POSITION_MY_ADDRESSES = 1;

    public final static int POSITION_EMAIL = 2;
    
    public final static int POSITION_SHARE_APP = 0;

    public final static int POSITION_COUNTRY = 0;

    public final static int POSITION_LANGUAGE = 1;
    
    private ViewGroup optionsList;
    
    private ViewGroup appSharingList;

    private ViewGroup chooseLanguageList;

    private ViewGroup moreInfoContainer;

    private MyAccountPushPreferences mPreferencesFragment;

    private ArrayList<TargetHelper> targets;

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
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
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
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");

        if(savedInstanceState != null){
            targets = savedInstanceState.getParcelableArrayList(TARGETS_TAG);
        } else {
            targets = CountryPersistentConfigs.getMoreInfo(this.getContext());
        }
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");

        showMyAccount(view);
        showPreferences();
        showAppSharing(view);
        showChooseLanguage(view);

        moreInfoContainer = (ViewGroup)view.findViewById(R.id.more_info_container);
        if(targets != null){
            showMoreInfo();
        } else {
            triggerFaqAndTerms();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE");
        outState.putParcelableArrayList(TARGETS_TAG, targets);
    }

    /*
         * (non-Javadoc)
         *
         * @see com.mobile.view.fragments.MyFragment#onPause()
         */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
        // Remove PreferencesFragment
        FragmentController.removeChildFragmentById(this, mPreferencesFragment.getId());
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "ON DESTROY");
        super.onDestroy();
    }

    private void triggerFaqAndTerms() {
        triggerContentEvent(new GetFaqTermsHelper(), null, this);
    }

    /**
     * Shows my account options
     */
    private void showMyAccount(View v) {
        // Get User Account Option
        String[] myAccountOptions = getResources().getStringArray(R.array.myaccount_array);
        // Get ListView
        optionsList = (ViewGroup) v.findViewById(R.id.middle_myaccount_list);
        // Create new Adapter
        MyAccountSettingsAdapter myAccountSettingsAdapter = new MyAccountSettingsAdapter(getActivity(), myAccountOptions);

        new MyAccountAdapter(optionsList, myAccountSettingsAdapter, this).buildLayout();
        
    }

    /**
     * Shows user preferences
     */
    private void showPreferences() {
        mPreferencesFragment = new MyAccountPushPreferences();
        FragmentController.addChildFragment(this, R.id.account_preferences_frame, mPreferencesFragment);
    }
    
    /**
     * Shows app sharing options
     */
    private void showAppSharing(View view) {
        appSharingList = (ViewGroup)view.findViewById(R.id.middle_app_sharing_list);
        AppSharingSettingsAdapter appSharingSettingsAdapter = new AppSharingSettingsAdapter(getActivity(), getResources().getStringArray(R.array.app_sharing_array));

        new MyAccountAdapter(appSharingList, appSharingSettingsAdapter, this).buildLayout();
    }

    private void showChooseLanguage(View view) {
        chooseLanguageList = (ViewGroup)view.findViewById(R.id.language_list);
        CountrySettingsAdapter.CountryLanguageInformation countryInformation = CountryPersistentConfigs.getCountryInformation(getActivity());
        chooseLanguageList.setTag(R.string.choose_language, countryInformation);
        CountrySettingsAdapter countrySettingsAdapter = new CountrySettingsAdapter(getActivity(), countryInformation);

        new MyAccountAdapter(chooseLanguageList, countrySettingsAdapter, this).buildLayout();
    }

    private void showMoreInfo() {
        MyAccountMoreInfoAdapter moreInfoAdapter = new MyAccountMoreInfoAdapter(targets, getActivity());
        new MyAccountAdapter(moreInfoContainer, moreInfoAdapter, this).buildLayout();

    }

    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Buy button
        if(id == R.id.more_info_container){
            processOnClickMoreInfo();
        } else{
            super.onClick(view);
        }
    }

    private void handleOnChooseLanguageItemClick(ViewGroup parent, int position) {
        if(position == POSITION_COUNTRY){
            getBaseActivity().onSwitchFragment(FragmentType.CHOOSE_COUNTRY, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        } else if(position == POSITION_LANGUAGE){
            CountrySettingsAdapter.CountryLanguageInformation countryInformation = (CountrySettingsAdapter.CountryLanguageInformation) parent.getTag(R.string.choose_language);
            ChooseLanguageController.chooseLanguageDialog(this, countryInformation.languages, new Runnable() {
                @Override
                public void run() {
                    getBaseActivity().restartAppFlow();
                }
            });
        }
    }

    /**
     *  Handles the item click of childs of options list.
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
        getBaseActivity().onSwitchFragment(FragmentType.MY_ADDRESSES_LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     *  Handles the item click of childs of app sharing list.
     */
    private void handleOnAppSharingListItemClick(int position) {
        switch (position) {
        case POSITION_SHARE_APP:
            String text;
            String preText = getString(R.string.install_jumia_android, getString(R.string.app_name_placeholder));
            if(ShopSelector.isRtl()){
                text = getString(R.string.share_app_link) + " " + preText;
            } else {
                text = preText + " " + getString(R.string.share_app_link);
            }
            ActivitiesWorkFlow.startActivitySendString(getBaseActivity(), getString(R.string.share_the_app), text) ;
            break;
        default:
            break;
        }
        AnalyticsGoogle.get().trackShareApp(TrackingEvent.SHARE_APP, (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getId() + "" : "");
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

    private void processOnClickMoreInfo() {
        getBaseActivity().onSwitchFragment(FragmentType.MY_ACCOUNT_MORE_INFO, null, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    public void onItemClick(ViewGroup parent, View view, int position) {
        // Validate item
        if(parent == this.optionsList){
            handleOnOptionsListItemClick(position);
        } else if(parent == this.appSharingList){
            handleOnAppSharingListItemClick(position);
        } else if(parent == this.chooseLanguageList){
            handleOnChooseLanguageItemClick(parent, position);
        } else if(parent == this.moreInfoContainer){
            handleOnMoreInfoItemClick(position);
        }
    }

    private void handleOnMoreInfoItemClick(int position) {

    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "ON SUCCESS EVENT");

        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return ;
        }

        switch (eventType) {
            case GET_FAQ_TERMS:
                targets = (MobileAbout) baseResponse.getMetadata().getData();
                showMoreInfo();
                break;
        }
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "ON ERROR EVENT");
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return ;
        }

        if (super.handleErrorEvent(baseResponse)) {
            return ;
        }

//        ErrorCode errorCode = baseResponse.getError().getErrorCode();

        switch (eventType) {
            case GET_FAQ_TERMS:
                showMoreInfo();
                break;
        }
    }
}
