package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.ActivitiesWorkFlow;
import com.bamilo.android.appmodule.bamiloapp.controllers.AdapterBuilder;
import com.bamilo.android.appmodule.bamiloapp.controllers.CountrySettingsAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.MyAccountMoreInfoAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.MyAccountNotificationsAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.MyAccountSettingsAdapter;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.configs.GetFaqTermsHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.preferences.CountryPersistentConfigs;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.framework.components.customfontviews.CheckBox;
import com.bamilo.android.framework.service.database.SectionsTablesHelper;
import com.bamilo.android.framework.service.objects.catalog.ITargeting;
import com.bamilo.android.framework.service.objects.statics.MobileAbout;
import com.bamilo.android.framework.service.objects.statics.TargetHelper;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.shop.ShopSelector;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author sergiopereira
 */
public class MyAccountFragment extends BaseFragment implements AdapterBuilder.OnItemClickListener,
        IResponseCallback {

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
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET,
                MyMenuItem.MY_PROFILE),
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
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        TrackerDelegator.trackPage(TrackingPage.USER_PROFILE, getLoadTime(), false);

        if (savedInstanceState != null) {
            targets = savedInstanceState.getParcelableArrayList(TARGETS_TAG);
        } else {
            setTargets(CountryPersistentConfigs.getMoreInfo(this.getContext()));
        }

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(
                getString(TrackingPage.USER_PROFILE.getName()), getString(R.string.gaScreen),
                "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showMyAccount(view);
        // TODO: 8/28/2017 Notification and newsletter settings removed for release 2.5.0 because API doesn't support these settings
        // showPreferences(view);
        showAppSharing(view);
        //showChooseLanguage(view);

        moreInfoContainer = view.findViewById(R.id.more_info_container);
        if (targets != null) {
            //showMoreInfo();
        } else {
            triggerFaqAndTerms();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(TARGETS_TAG, targets);
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
        optionsList = v.findViewById(R.id.middle_myaccount_list);
        // Create new Adapter
        MyAccountSettingsAdapter myAccountSettingsAdapter = new MyAccountSettingsAdapter(
                getActivity(), myAccountOptions);

        new AdapterBuilder(optionsList, myAccountSettingsAdapter, this).buildLayout();
    }

    /**
     * Shows user preferences
     */
    private void showPreferences(View view) {
        notificationList = view.findViewById(R.id.notification_list);
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
        appSocialList = view.findViewById(R.id.middle_app_sharing_list);
        MyAccountSettingsAdapter appSharingSettingsAdapter = new MyAccountSettingsAdapter(
                getActivity(), getResources().getStringArray(R.array.app_sharing_array));

        new AdapterBuilder(appSocialList, appSharingSettingsAdapter, this).buildLayout();
    }

    private void showChooseLanguage(View view) {
        chooseLanguageList = view.findViewById(R.id.language_list);
        CountrySettingsAdapter.CountryLanguageInformation countryInformation = CountryPersistentConfigs
                .getCountryInformation(getActivity());
        chooseLanguageList.setTag(R.string.shop_settings, countryInformation);
        CountrySettingsAdapter countrySettingsAdapter = new CountrySettingsAdapter(getActivity(),
                countryInformation);

        new AdapterBuilder(chooseLanguageList, countrySettingsAdapter, this).buildLayout();
    }

    private void showMoreInfo() {
        MyAccountMoreInfoAdapter moreInfoAdapter = new MyAccountMoreInfoAdapter(targets,
                getActivity());
        new AdapterBuilder(moreInfoContainer, moreInfoAdapter, this).buildLayout();
    }

    private void handleOnChooseLanguageItemClick(ViewGroup parent, int position) {
        // Case country
        if (!ShopSelector.isSingleShopCountry() && position == POSITION_COUNTRY) {
            getBaseActivity()
                    .onSwitchFragment(FragmentType.CHOOSE_COUNTRY, FragmentController.NO_BUNDLE,
                            FragmentController.ADD_TO_BACK_STACK);
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
    private void clearCountryConfigs() {
        SectionsTablesHelper.deleteConfigurations();
    }


    /**
     * Handles the item click of childs of options list.
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
        getBaseActivity()
                .onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Handles the item click of childs of app sharing list.
     */
    private void handleOnAppSocialListItemClick(int position) {
        /*Share item was removed in order to meet Cafebazaar policies*/
        if (position == 0) {
            goToMarketPage();
        }
        /*switch (position) {
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
        }*/
    }

    /**
     * Handles the item click of childs of app sharing list.
     */
    private void handleOnNotificationListItemClick(ViewGroup parent, int position) {
        switch (position) {
            case NOTIFICATION_STATUS:
                CheckBox m = parent
                        .findViewWithTag(MyAccountNotificationsAdapter.NOTIFICATION_CHECKBOX_TAG);
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
     *
     * @author sergiopereira
     */
    private void processOnClickUserData() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE, FragmentType.MY_USER_DATA);
        getBaseActivity()
                .onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Process the click on the email notification
     *
     * @author sergiopereira
     */
    private void processOnClickEmailNotification() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(ConstantsIntentExtra.NEXT_FRAGMENT_TYPE,
                FragmentType.EMAIL_NOTIFICATION);
        getBaseActivity()
                .onSwitchFragment(FragmentType.LOGIN, bundle, FragmentController.ADD_TO_BACK_STACK);
    }

    @Override
    public void onItemClick(ViewGroup parent, View view, int position) {
        // Validate item
        if (parent == this.optionsList) {
            handleOnOptionsListItemClick(position);
        } else if (parent == this.notificationList) {
            handleOnNotificationListItemClick(parent, position);
        } else if (parent == this.appSocialList) {
            handleOnAppSocialListItemClick(position);
        } else if (parent == this.chooseLanguageList) {
            handleOnChooseLanguageItemClick(parent, position);
        } else if (parent == this.moreInfoContainer) {
            handleOnMoreInfoItemClick(position);
        }
    }

    private void handleOnMoreInfoItemClick(int position) {
        if (position == MyAccountMoreInfoAdapter.APP_VERSION_POSITION) {
            goToMarketPage();
        } else {
            TargetHelper targetHelper = targets.get(position - 1);
            if (targetHelper.getTargetType() == ITargeting.TargetType.SHOP) {
                onClickStaticPageButton(targetHelper.getTargetValue(),
                        targetHelper.getTargetTitle());
            }
        }
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
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
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess || eventType == null) {
            return;
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
        getBaseActivity().onSwitchFragment(FragmentType.STATIC_PAGE, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    private void setTargets(@Nullable List<TargetHelper> targetHelpers) {
        if (CollectionUtils.isNotEmpty(targetHelpers)) {
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
