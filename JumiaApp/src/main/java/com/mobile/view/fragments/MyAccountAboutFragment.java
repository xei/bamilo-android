package com.mobile.view.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobile.app.JumiaApplication;
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
import com.mobile.newFramework.database.SectionsTablesHelper;
import com.mobile.newFramework.objects.catalog.ITargeting;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.TargetHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.TrackingEvent;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import de.akquinet.android.androlog.Log;

/**
 * @author shahrooz
 *
 */
public class MyAccountAboutFragment extends BaseFragment /*implements  IResponseCallback*/ {

    private static final String TAG = MyAccountAboutFragment.class.getSimpleName();

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
    public MyAccountAboutFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK, MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.ABOUT,
                R.layout.my_account_about_fragment,
                R.string.account_about,
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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
     /*   Button call_btn = (Button) getBaseActivity().findViewById(R.id.about_call_btn);
        call_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.onClickCallToOrder(getBaseActivity());
            }
        });
        Button email_btn = (Button) getBaseActivity().findViewById(R.id.about_email_btn);
        email_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.onClickEmailToCS(getBaseActivity());
            }
        });*/
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


    /**
     * Shows my account options
     */













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
                AnalyticsGoogle.get().trackShareApp(TrackingEvent.SHARE_APP, (JumiaApplication.CUSTOMER != null) ? JumiaApplication.CUSTOMER.getId() + "" : "");
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