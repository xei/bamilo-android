package com.mobile.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.app.BamiloApplication;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.controllers.AdapterBuilder;
import com.mobile.controllers.CountrySettingsAdapter;
import com.mobile.controllers.MyAccountMoreInfoAdapter;
import com.mobile.controllers.MyAccountNotificationsAdapter;
import com.mobile.controllers.MyAccountSettingsAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.configs.GetFaqTermsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.service.database.SectionsTablesHelper;
import com.mobile.service.objects.catalog.ITargeting;
import com.mobile.service.objects.statics.MobileAbout;
import com.mobile.service.objects.statics.TargetHelper;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.tracking.AnalyticsGoogle;
import com.mobile.service.tracking.TrackingEvent;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class MyAccountFragment extends BaseFragment implements AdapterBuilder.OnItemClickListener, IResponseCallback {

    private static final String TAG = MyAccountFragment.class.getSimpleName();

    private static final String TARGETS_TAG = MobileAbout.class.getSimpleName();

    public final static int POSITION_USER_DATA = 0;

    public final static int POSITION_MY_ADDRESSES = 1;

    public final static int POSITION_EMAIL = 2;
    
    public final static int POSITION_SHARE_APP = 0;

    public final static int POSITION_RATE_APP = 1;

    public final static int POSITION_COUNTRY = 0;

    public final static int NOTIFICATION_STATUS = 0;

    public final static int EMAIL_NOTIFICATION_STATUS = 1;

    private ViewGroup optionsList;
    
    private ViewGroup appSocialList;

    private ViewGroup chooseLanguageList;

    private ViewGroup moreInfoContainer;

    private ViewGroup notificationList;

    private ArrayList<TargetHelper> targets;

    /**
     * Empty constructor
     */
    public MyAccountFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.MY_ACCOUNT,
                R.layout.my_account_fragment,
                R.string.account_name,
                NO_ADJUST_CONTENT);
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
            setTargets(CountryPersistentConfigs.getMoreInfo(this.getContext()));
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
        showPreferences(view);
        showAppSharing(view);
        //showChooseLanguage(view);

        moreInfoContainer = (ViewGroup)view.findViewById(R.id.more_info_container);
        if(targets != null){
            //showMoreInfo();
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

        new AdapterBuilder(optionsList, myAccountSettingsAdapter, this).buildLayout();
        
    }

    /**
     * Shows user preferences
     */
    private void showPreferences(View view) {
        notificationList = (ViewGroup)view.findViewById(R.id.notification_list);
        MyAccountNotificationsAdapter notificationSettingsAdapter = new MyAccountNotificationsAdapter(
                getBaseActivity().getApplicationContext(),
                getResources().getStringArray(R.array.app_notification_array),
                getResources().getIntArray(R.array.app_notification_array_checkboxes));


        new AdapterBuilder(notificationList, notificationSettingsAdapter, this).buildLayout();
    }
    
    /**
     * Shows app sharing options
     */
    private void showAppSharing(View view) {
        appSocialList = (ViewGroup)view.findViewById(R.id.middle_app_sharing_list);
        MyAccountSettingsAdapter appSharingSettingsAdapter = new MyAccountSettingsAdapter(getActivity(), getResources().getStringArray(R.array.app_sharing_array));

        new AdapterBuilder(appSocialList, appSharingSettingsAdapter, this).buildLayout();
    }

    private void showChooseLanguage(View view) {
        chooseLanguageList = (ViewGroup)view.findViewById(R.id.language_list);
        CountrySettingsAdapter.CountryLanguageInformation countryInformation = CountryPersistentConfigs.getCountryInformation(getActivity());
        chooseLanguageList.setTag(R.string.shop_settings, countryInformation);
        CountrySettingsAdapter countrySettingsAdapter = new CountrySettingsAdapter(getActivity(), countryInformation);

        new AdapterBuilder(chooseLanguageList, countrySettingsAdapter, this).buildLayout();
    }

   private void showMoreInfo() {
        MyAccountMoreInfoAdapter moreInfoAdapter = new MyAccountMoreInfoAdapter(targets, getActivity());
        new AdapterBuilder(moreInfoContainer, moreInfoAdapter, this).buildLayout();
    }

    private void handleOnChooseLanguageItemClick(ViewGroup parent, int position) {
        // Case country
        if (!ShopSelector.isSingleShopCountry() && position == POSITION_COUNTRY) {
            getBaseActivity().onSwitchFragment(FragmentType.CHOOSE_COUNTRY, FragmentController.NO_BUNDLE, FragmentController.ADD_TO_BACK_STACK);
        }
        // Case language
        else {
          /*  CountrySettingsAdapter.CountryLanguageInformation countryInformation = (CountrySettingsAdapter.CountryLanguageInformation) parent.getTag(R.string.shop_settings);
            ChooseLanguageController.chooseLanguageDialog(this, countryInformation.languages, new Runnable() {
                @Override
                public void run() {
                    // Clear Country Configs to force update.
                    clearCountryConfigs();
                    getBaseActivity().restartAppFlow();
                }
            });*/
        }
    }


    /**
     * Trigger to get the country configurations
     */
    private void clearCountryConfigs(){
        SectionsTablesHelper.deleteConfigurations();
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
        bundle.putSerializable(ConstantsIntentExtra.PARENT_FRAGMENT_TYPE, FragmentType.MY_ACCOUNT);
        bundle.putBoolean(ConstantsIntentExtra.GET_NEXT_STEP_FROM_MOB_API, true);
        getBaseActivity().onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     *  Handles the item click of childs of app sharing list.
     */
    private void handleOnAppSocialListItemClick(int position) {
        switch (position) {
            case POSITION_SHARE_APP:
                String text;
                String preText = getString(R.string.install_android, getString(R.string.app_name_placeholder));
                if(ShopSelector.isRtl()){
                    text = getString(R.string.share_app_link) + " " + preText;
                } else {
                    text = preText + " " + getString(R.string.share_app_link);
                }
                ActivitiesWorkFlow.startActivitySendString(getBaseActivity(), getString(R.string.share_the_app), text);
                AnalyticsGoogle.get().trackShareApp(TrackingEvent.SHARE_APP, (BamiloApplication.CUSTOMER != null) ? BamiloApplication.CUSTOMER.getId() + "" : "");
            break;
            case POSITION_RATE_APP:
                goToMarketPage();
                break;
        default:
            break;
        }
    }

    /**
     *  Handles the item click of childs of app sharing list.
     */
    private void handleOnNotificationListItemClick(ViewGroup parent, int position) {
        switch (position) {
            case NOTIFICATION_STATUS:
                CheckBox m = (CheckBox) parent.findViewWithTag(MyAccountNotificationsAdapter.NOTIFICATION_CHECKBOX_TAG);
                m.setChecked(!m.isChecked());
                break;
            case EMAIL_NOTIFICATION_STATUS:
                processOnClickEmailNotification();
                break;
            default:
                break;
        }
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

    @Override
    public void onItemClick(ViewGroup parent, View view, int position) {
        // Validate item
        if(parent == this.optionsList){
            handleOnOptionsListItemClick(position);
        } else if(parent == this.notificationList){
            handleOnNotificationListItemClick(parent, position);
        } else if(parent == this.appSocialList){
            handleOnAppSocialListItemClick(position);
        } else if(parent == this.chooseLanguageList){
            handleOnChooseLanguageItemClick(parent, position);
        } else if(parent == this.moreInfoContainer){
            handleOnMoreInfoItemClick(position);
        }
    }

    private void handleOnMoreInfoItemClick(int position) {
        if(position == MyAccountMoreInfoAdapter.APP_VERSION_POSITION){
            goToMarketPage();
        } else {
            TargetHelper targetHelper = targets.get(position - 1);
            if(targetHelper.getTargetType() == ITargeting.TargetType.SHOP) {
                onClickStaticPageButton(targetHelper.getTargetValue(), targetHelper.getTargetTitle());
            }
        }
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Case GET_FAQ_TERMS
        if (eventType == EventType.GET_FAQ_TERMS) {
            setTargets((MobileAbout) baseResponse.getMetadata().getData());
           // showMoreInfo();
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
        // Case GET_FAQ_TERMS
        if (eventType == EventType.GET_FAQ_TERMS) {
            //showMoreInfo();
            showFragmentContentContainer();
        }
    }

    private void onClickStaticPageButton(String key, String label) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, key);
        bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, label);
        getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    private void setTargets(@Nullable List<TargetHelper> targetHelpers){
        if(CollectionUtils.isNotEmpty(targetHelpers)) {
            this.targets = new ArrayList<>();
            for (TargetHelper targetHelper : targetHelpers) {
                if (targetHelper.getTargetType() == ITargeting.TargetType.SHOP) {
                    this.targets.add(targetHelper);
                }
            }
        }
    }

    /**
     * Method that re directs to app market or web page
     */
    private void goToMarketPage() {
        ActivitiesWorkFlow.startMarketActivity(getActivity(), getString(R.string.id_market));
    }
}