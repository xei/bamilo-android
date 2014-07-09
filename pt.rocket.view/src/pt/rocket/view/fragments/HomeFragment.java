/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.components.androidslidingtabstrip.SlidingTabLayout;
import pt.rocket.framework.objects.Homepage;
import pt.rocket.framework.objects.Promotion;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.framework.utils.MixpanelTracker;
import pt.rocket.framework.utils.ShopSelector;
import pt.rocket.helpers.GetPromotionsHelper;
import pt.rocket.helpers.GetTeasersHelper;
import pt.rocket.helpers.GetUpdatedTeasersHelper;
import pt.rocket.helpers.session.GetLoginHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogPromotionFragment;
import pt.rocket.utils.dialogfragments.WizardPreferences;
import pt.rocket.utils.dialogfragments.WizardPreferences.WizardType;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show all campaigns
 * @author sergiopereira
 */
public class HomeFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = LogTagHelper.create(HomeFragment.class);
    
    public static final String CAMPAIGNS_TAG = "campaigns";
    
    public static final String CAMPAIGN_POSITION_TAG = "campaign_position";
    
    private static HomeFragment sHomeFragment;

    private ViewPager mHomePager;

    private HomePagerAdapter mHomePagerAdapter;
    
    private SlidingTabLayout mHomePagerTabStrip;
    
    private String mCurrentMd5Collection;
    
    /**
     * Constructor via bundle
     * @return CampaignsFragment
     * @author sergiopereira
     */
    public static HomeFragment newInstance() {
        sHomeFragment = new HomeFragment();
        return sHomeFragment;
    }

    /**
     * Empty constructor
     */
    public HomeFragment() {
        super(EnumSet.of(EventType.GET_TEASERS_EVENT),
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.Home, 
                0, 
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
        //
        HockeyStartup.register(getBaseActivity());
//        //
//        if (JumiaApplication.mIsBound) onCreateExecution();
//        else JumiaApplication.INSTANCE.setResendHander(serviceConnectedHandler);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.home_fragment_main, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        // Get view pager 
        mHomePager = (ViewPager) view.findViewById(R.id.home_pager);
        // Get tab pager
        mHomePagerTabStrip = (SlidingTabLayout) view.findViewById(R.id.home_pager_tab);
        mHomePagerTabStrip.setCustomTabView(R.layout.tab_simple_item, R.id.tab);
        
        // XXX
        if (JumiaApplication.mIsBound && !TextUtils.isEmpty(ShopSelector.getShopId())) onResumeExecution();
        else JumiaApplication.INSTANCE.setResendHander(serviceConnectedHandler);
        
//        // Validate if is bound
//        // Valdiate the shop id in this point
//        if(!TextUtils.isEmpty(ShopSelector.getShopId())) {
//            Log.i(TAG, "WARNING SHOP ID IS NULL");
//            onResumeExecution();
//        } else {
//            Log.w(TAG, "WARNING SHOP ID IS NULL"); 
//            ActivitiesWorkFlow.splashActivityNewTask(getBaseActivity());
//        }
        
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
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
//        // XXX
//        if (JumiaApplication.mIsBound) onResumeExecution();
//        else JumiaApplication.INSTANCE.setResendHander(serviceConnectedHandler);
        
    }
    
    /**
     * XXX
     */
    Handler serviceConnectedHandler = new Handler() {
        public void handleMessage(android.os.Message msg) { 
            mHomePagerAdapter = null;
            //onCreateExecution();
            onResumeExecution();
        };
    };
    
//    /**
//     * XXX
//     */
//    private void onCreateExecution() {
//        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() && !JumiaApplication.INSTANCE.isLoggedIn())
//            triggerAutoLogin();
//    }

    /**
     * XXX
     */
    private void onResumeExecution() {
        
        // TODO : Comment for Samsung store
        if (CheckVersion.needsToShowDialog()) CheckVersion.showDialog(getActivity());
        
        // Validate the user credentials
        if (JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() && !JumiaApplication.INSTANCE.isLoggedIn())
            triggerAutoLogin();

        // Validate promotions
        SharedPreferences sP = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sP.getBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true)) {
            triggerPromotions();
        }

        // Validate current state
        
        //mHomePagerAdapter = (HomePagerAdapter) mHomePager.getAdapter();
        if(mHomePagerAdapter == null) {
            Log.i(TAG, "ADAPTER IS NULL");
            getBaseActivity().setProcessShow(false);
            triggerTeasers();
            
        } else {
            Log.i(TAG, "ADAPTER IS NOT NULL");
            mHomePager.setAdapter(mHomePagerAdapter);
            //mHomePager.setOffscreenPageLimit(1);
            mHomePagerTabStrip.setViewPager(mHomePager);
            getBaseActivity().setProcessShow(true);
            getBaseActivity().showContentContainer();
        }
        
        AnalyticsGoogle.get().trackPage(R.string.ghomepage);
    }
    

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        MixpanelTracker.flush();
        // Destroy adapter
        mHomePagerAdapter = null;
    }
    
    /**
     * ########### LAYOUT ###########  
     */
    
    /**
     * 
     * @param collection
     */
    private void onShow(ArrayList<Homepage> collection) {
        Log.i(TAG, "ON SHOW");
        if(mHomePagerAdapter == null) {
            Log.i(TAG, "ADAPTER IS NULL");
            mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager(), collection);
            mHomePager.setAdapter(mHomePagerAdapter);
            mHomePagerTabStrip.setViewPager(mHomePager);
        } else {
            Log.i(TAG, "UPDATE ADAPTER");
            mHomePagerAdapter.updateCollection(collection);
        }
        // Validate if is to show home wizard
        showHomeWizard();
        // Show container
        getBaseActivity().setProcessShow(true);
        getBaseActivity().showContentContainer();
    }
    
    
    /**
     * Show tips if is the first time the user uses the app.
     * @author sergiopereira
     */
    private void showHomeWizard() {
        Log.i(TAG, "ON SHOW WIZARD");
        if (WizardPreferences.isFirstTime(getActivity(), WizardType.HOME)) {
            RelativeLayout homeTip = (RelativeLayout) getView().findViewById(R.id.home_tip);
            homeTip.setVisibility(View.VISIBLE);
            homeTip.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    WizardPreferences.changeState(getActivity(), WizardType.HOME);
                    v.setVisibility(View.GONE);
                    return false;
                }
            });
        }
    }
    
    /**
     * Show the fall back
     * @author msilva
     */
    private void showLayoutFallback() {
        Log.i(TAG, "ON SHOW FALLBACK");
        getView().findViewById(R.id.home_pager_tab).setVisibility(View.GONE);
        getView().findViewById(R.id.home_pager).setVisibility(View.GONE);
        
        getView().findViewById(R.id.home_fallback_content).setVisibility(View.VISIBLE);
        ImageView mapBg = (ImageView) getView().findViewById(R.id.home_fallback_country_map);
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        
        AQuery aq = new AQuery(getBaseActivity());
        aq.id(mapBg).image(sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_MAP_FLAG, ""));

        String country = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_NAME, "Jumia");
        TextView fallbackBest = (TextView) getView().findViewById(R.id.fallback_best);
        fallbackBest.setText(R.string.fallback_best);
        if (country.split(" ").length == 1) {
            TextView tView = (TextView) getView().findViewById(R.id.fallback_country);
            tView.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.fallback_country_double).setVisibility(View.GONE);
            tView.setText(country.toUpperCase());
        } else {
            TextView tView = (TextView) getView().findViewById(R.id.fallback_country_top);
            tView.setText(country.split(" ")[0].toUpperCase());
            TextView tViewBottom = (TextView) getView().findViewById(R.id.fallback_country_bottom);
            tViewBottom.setText(country.split(" ")[1].toUpperCase());
            fallbackBest.setTextSize(11.88f);
            getView().findViewById(R.id.fallback_country_double).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.fallback_country).setVisibility(View.GONE);
        }

        fallbackBest.setSelected(true);
        getBaseActivity().setProcessShow(true);
        getBaseActivity().showContentContainer();
    }
    
    
    /**
     * ########### TRIGGERS ###########  
     */
    
    /**
     * Trigger to get teasers
     * @author sergiopereira
     */
    private void triggerTeasers() {
        Log.d(TAG, "ON TRIGGER: GET TEASERS");
        // Get teaser collection
        triggerContentEvent(new GetTeasersHelper(), null, (IResponseCallback) this);
        // Validate the current md5 to check updated teaser collection
        Log.d(TAG, "ON TRIGGER: GET UPDATED TEASERS " + mCurrentMd5Collection);
        Bundle bundle = new Bundle();
        bundle.putString(GetUpdatedTeasersHelper.OLD_MD5_KEY, mCurrentMd5Collection);
        triggerContentEvent(new GetUpdatedTeasersHelper(), bundle, (IResponseCallback) this);
    }
    
    /**
     * Trigger to get promotions
     * @author sergiopereira
     */
    private void triggerPromotions() {
        Log.d(TAG, "ON TRIGGER: GET PROMOTIONS");
        Bundle bundle = new Bundle();
        triggerContentEventWithNoLoading(new GetPromotionsHelper(), bundle, (IResponseCallback) this);
    }
    
    /**
     * Trigger to perform auto login in background
     * @author sergiopereira
     */
    private void triggerAutoLogin() {
        Log.d(TAG, "ON TRIGGER: AUTO LOGIN");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        triggerContentEventWithNoLoading(new GetLoginHelper(), bundle, (IResponseCallback) this);
    }
    
    /**
     * ########### RESPONSES ###########  
     */
    
  
    @Override
    public void onRequestComplete(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_UPDATED_TEASERS_EVENT:
            Log.i(TAG, "ON SUCCESS RESPONSE: GET_UPDATED_TEASERS_EVENT");
            // Get current md5 response
            mCurrentMd5Collection = bundle.getString(GetUpdatedTeasersHelper.MD5_KEY);
            // Get updated teaser collection
            ArrayList<Homepage> updatedCollection = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            if (updatedCollection != null) onShow(updatedCollection);
            break;
        case GET_TEASERS_EVENT:
            Log.i(TAG, "ON SUCCESS RESPONSE: GET_TEASERS_EVENT");
            // Get current md5 response
            mCurrentMd5Collection = bundle.getString(GetTeasersHelper.MD5_KEY);
            // Get collection
            ArrayList<Homepage> collection = bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            if (collection != null) onShow(collection);
            else showLayoutFallback();
            break;
        case GET_PROMOTIONS:
            Log.i(TAG, "ON SUCCESS RESPONSE: GET_TEASERS_EVENT");
            onGetPromotions(bundle);
            break;
        }

    }

    
    private void onGetPromotions(Bundle bundle){
        if (((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getIsStillValid()) {
            DialogPromotionFragment.newInstance((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).show(getChildFragmentManager(), null);
        } else {
            Log.i(TAG, "promotion expired!" + ((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getEndDate());
        }
    }
    


    @Override
    public void onRequestError(Bundle bundle) {
    
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_UPDATED_TEASERS_EVENT:
            Log.d(TAG, "ON ERROR RESPONSE: DISCARDED RECEIVED GET_UPDATED_TEASERS_EVENT");
            // Discarded the error response
            getBaseActivity().setProcessShow(true);
            getBaseActivity().showContentContainer();
            break;
        case GET_TEASERS_EVENT:
            Log.i(TAG, "ON ERROR RESPONSE: GET_TEASERS_EVENT");
            showLayoutFallback();
            break;
        case GET_PROMOTIONS:
            break;
        }
    }
    
    
    
    /**
     * ########### DIALOGS ###########  
     */    

    
    /**
     * ########### ADAPTERS ###########  
     */
    
    /**
     * Class used as an simple pager adapter that represents each campaign fragment
     * @author sergiopereira
     */
    private class HomePagerAdapter extends FragmentStatePagerAdapter {
        
        private ArrayList<Homepage> mHomePages;
        
        /**
         * Constructor
         * @param fm
         * @param collection
         * @author sergiopereira
         */
        public HomePagerAdapter(FragmentManager fm, ArrayList<Homepage> collection) {
            super(fm);
            this.mHomePages = collection;
        }

        /**
         * 
         * @param collection
         */
        public void updateCollection(ArrayList<Homepage> collection) {
            this.mHomePages = collection;
            this.notifyDataSetChanged();
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(HomePageFragment.HOME_PAGE_KEY, this.mHomePages.get(position)); 
            return HomePageFragment.getInstance(bundle);
        }

        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return (mHomePages != null) ? mHomePages.size() : 0;
        }
        
        /*
         * (non-Javadoc)
         * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return mHomePages.get(position).getHomepageTitle().toUpperCase();
        }
        
        /*
         * (non-Javadoc)
         * @see android.support.v4.app.FragmentPagerAdapter#destroyItem(android.view.ViewGroup, int, java.lang.Object)
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
        
    }

}
