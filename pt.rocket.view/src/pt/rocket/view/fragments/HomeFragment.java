/**
 * 
 */
package pt.rocket.view.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.LastViewedAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.TeasersFactory;
import pt.rocket.forms.FieldValidation;
import pt.rocket.forms.HomeNewslettersSignupForm;
import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.framework.objects.Homepage;
import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.objects.LastViewed;
import pt.rocket.framework.objects.Promotion;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.MixpanelTracker;
import pt.rocket.helpers.GetCallToOrderHelper;
import pt.rocket.helpers.GetPromotionsHelper;
import pt.rocket.helpers.GetTeasersHelper;
import pt.rocket.helpers.account.GetHomeNewslettersSignupFormHelper;
import pt.rocket.helpers.account.HomeNewslettersSignupHelper;
import pt.rocket.helpers.session.GetLoginHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.JumiaViewPager;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.ScrollViewWithHorizontal;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogPromotionFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.ChangeCountryFragmentActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author manuelsilva
 */
public class HomeFragment extends BaseFragment {
    private final static String TAG = HomeFragment.class.getSimpleName();

    private JumiaViewPager mPager;
    private PagerTabStrip pagerTabStrip;

    private final int TAB_PREV_ID = 0;
    private final int TAB_CURR_ID = 1;
    private final int TAB_NEXT_ID = 2;

    private final int TAB_INDICATOR_HEIGHT = 0;
    private final int TAB_UNDERLINE_HEIGHT = 1;
    private final int TAB_STRIP_COLOR = android.R.color.transparent;
    public static String KEY_CALL_TO_ORDER = "call_to_order";
    private HomeCollectionPagerAdapter mPagerAdapter;
    private static ArrayList<String> pagesTitles;
    public static ArrayList<Collection<? extends TeaserSpecification<?>>> requestResponse;

    // private int defaultPosition=Math.abs(requestResponse.size() / 2);

    private int currentPosition = -1;
    public static int initialPosition = 3;

    private static HomeFragment mHomeFragment;

    public static ArrayList<LastViewed> lastViewed = null;

    private static DialogFragment mDialogNewsletter;

    private static HomeNewslettersSignupForm mHomeNewsletterSignupForm;

    private final static int DELAY_FOR_NEWSLETTER_RETRY = 1000; // 1 seconds

    private static String lastEmail;

    /**
     * Get instance
     * 
     * @return
     */
    public static HomeFragment newInstance() {

        mHomeFragment = new HomeFragment();
        return mHomeFragment;

    }

    /**
	 */
    public HomeFragment() {
        super(EnumSet.of(EventType.GET_API_INFO, 
                EventType.GET_TEASERS_EVENT,
                EventType.GET_CALL_TO_ORDER_PHONE, 
                EventType.GET_PROMOTIONS),
                EnumSet.noneOf(EventType.class), 
                EnumSet.of(MyMenuItem.SEARCH_BAR),
                NavigationAction.Home, 0, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
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
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setRetainInstance(true);
        
        HockeyStartup.register(getActivity());
        if(JumiaApplication.mIsBound){
            onCreateExecution();    
        } else {
            JumiaApplication.INSTANCE.setResendHander(serviceConnectedHandler);
        }
        Log.i(TAG, "onCreate");
    }

    Handler serviceConnectedHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
           onCreateExecution();
           onResumeExecution();
        }; 
     };
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.teasers_fragments_viewpager, null, false);

        Log.i(TAG, "onCreateView");
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        
        if(JumiaApplication.mIsBound){
            onResumeExecution();    
        } else {
            JumiaApplication.INSTANCE.setResendHander(serviceConnectedHandler);
        }
        
    }
    
    private void onCreateExecution(){

        if(JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials() && !JumiaApplication.INSTANCE.isLoggedIn()){
            triggerAutoLogin();
        }
    }
    
    private void onResumeExecution(){
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(getActivity());
        }

        SharedPreferences sP = getActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sP.getBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true)) {
            
            /**
             * TRIGGERS
             * @author sergiopereira
             */
            triggerPromotions();
            
        }

        if (requestResponse == null) {
            ((BaseActivity) getActivity()).setProcessShow(false);

            /**
             * TRIGGERS
             * @author sergiopereira
             */
            triggerTeasers();
            triggerCallToOrder();
            triggerHomeNewsletterSignupForm();
            
        } else {
            restoreLayout();
        }

        AnalyticsGoogle.get().trackPage(R.string.ghomepage);
       
        if(LastViewedTableHelper.getLastViewedEntriesCount() > 0){
            lastViewed = LastViewedTableHelper.getLastViewedList();
        } else {
            lastViewed = null;
        }
    }
    
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        mPagerAdapter = null;
        mPager = null;
        pagerTabStrip = null;
    
        super.onPause();

    }
    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        requestResponse = null;
        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        MixpanelTracker.flush();
        mPagerAdapter = null;
        mPager = null;
        pagerTabStrip = null;
    
        // clean lastEmail
        lastEmail = null;

        super.onDestroy();
        System.gc();
    }

    private void setLayout(int currentPositionPager) {
        Log.i(TAG, "setLayout");
        if (mPager == null) {
            if(getView() == null){
                return;
            }
            Log.i(TAG, "setLayout -> mPager NULL");
            mPager = (JumiaViewPager) getView().findViewById(R.id.home_viewpager);
            mPager.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int arg0) {
                    currentPosition = arg0;
                   
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                    int pageCount = pagesTitles.size();
                   

                    if (arg0 == JumiaViewPager.SCROLL_STATE_SETTLING) {
                        if (mPager != null)
                            mPager.setPagingEnabled(false);
                    }

                    if (arg0 == JumiaViewPager.SCROLL_STATE_IDLE) {
                        new ChangePageTask().execute(arg0);
                    }

                }
            });
        } 

        // if(mPager.getAdapter() == null){
        pagerTabStrip = (PagerTabStrip) getView().findViewById(R.id.home_titles);
        if (mPager.getAdapter() == null)
            mPager.setAdapter(mPagerAdapter);
        mPager.setSaveEnabled(false);
        mPager.setCurrentItem(currentPositionPager);
        configureLayout();
        // }
        ((BaseActivity) getActivity()).setProcessShow(true);
        ((BaseActivity) getActivity()).showContentContainer();
        
    }

    private class ChangePageTask extends AsyncTask<Integer, String, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {

            if (null != mPager ) {
                mPager.setPagingEnabled(true);
                mPager.toggleJumiaScroller(true);
              
                
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        int pageCount = pagesTitles.size();
                        if (null != mPager ) {
                        // change event of first(last) fragment to jump for original fragment
                        if (mPager.getCurrentItem() == 0) {
                            mPager.toggleJumiaScroller(false);                    
                            mPager.setCurrentItem(pageCount - 2);

                            // change event of last(first) fragment to jump for original fragment
                        } else if (mPager.getCurrentItem() == pageCount - 1) {
                            mPager.toggleJumiaScroller(false);
                            mPager.setCurrentItem(1);

                        }
                        }
                    }
                });                
            }
                
            return true;

        }

        /**
         * The system calls this to perform work in the UI thread and delivers the result from
         * doInBackground()
         */
        protected void onPostExecute(Boolean result) {            
        }

    }    
    
    private void restoreLayout() {
        Log.i(TAG, "restoreLayout");
        if (requestResponse != null) {
            Log.i(TAG, "restoreLayout -> NOT NULL");
            if (currentPosition == -1) {
                currentPosition = Math.abs((requestResponse.size() + 2) / 2);

                initialPosition = currentPosition;
            }

            // if (mPagerAdapter == null) {
            mPagerAdapter = new HomeCollectionPagerAdapter(getChildFragmentManager());
            // }
            setLayout(currentPosition);
        } else {
            Log.i(TAG, "restoreLayout -> NULL");
            ((BaseActivity) getActivity()).setProcessShow(false);
            
            /**
             * TRIGGERS
             * @author sergiopereira
             */
            triggerTeasers();
            //triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
        }
    }

    /**
     * Set some layout parameters that aren't possible by xml
     * 
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void setLayoutSpec() throws NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        Log.i(TAG, "setLayoutSpec");
        if(pagerTabStrip == null){
            return;
        }
        
        // Get text
        final android.widget.TextView currTextView = (android.widget.TextView) pagerTabStrip
                .getChildAt(TAB_CURR_ID);
        final android.widget.TextView nextTextView = (android.widget.TextView) pagerTabStrip
                .getChildAt(TAB_NEXT_ID);
        final android.widget.TextView prevTextView = (android.widget.TextView) pagerTabStrip
                .getChildAt(TAB_PREV_ID);

        // Set Color
        currTextView.setPadding(0, 0, 0, 1);

        // Calculate the measures
        final float density = getActivity().getResources().getDisplayMetrics().density;
        int mIndicatorHeight = (int) (TAB_INDICATOR_HEIGHT * density + 0.5f);
        int mFullUnderlineHeight = (int) (TAB_UNDERLINE_HEIGHT * density + 0.5f);

        // Set the indicator height
        Field field;
        field = pagerTabStrip.getClass().getDeclaredField("mIndicatorHeight");
        field.setAccessible(true);
        field.set(pagerTabStrip, mIndicatorHeight);
        // Set the underline height
        field = pagerTabStrip.getClass().getDeclaredField("mFullUnderlineHeight");
        field.setAccessible(true);
        field.set(pagerTabStrip, mFullUnderlineHeight);
        // Set the color of indicator
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, 0, 0, mIndicatorHeight, getResources().getColor(
                TAB_STRIP_COLOR), getResources().getColor(
                TAB_STRIP_COLOR), Shader.TileMode.CLAMP));
        field = pagerTabStrip.getClass().getDeclaredField("mTabPaint");
        field.setAccessible(true);
        field.set(pagerTabStrip, paint);
    }

    private void setLayoutFallback() {
        getView().findViewById(R.id.home_view_pager_content).setVisibility(View.GONE);
        getView().findViewById(R.id.home_fallback_content).setVisibility(View.VISIBLE);
        ImageView mapBg = (ImageView) getView().findViewById(R.id.home_fallback_country_map);
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int position = sharedPrefs.getInt(ChangeCountryFragmentActivity.KEY_COUNTRY, 0);

        mapBg.setImageDrawable(getActivity().getResources().obtainTypedArray(R.array.country_fallback_map).getDrawable(position));

        String country = getActivity().getResources().obtainTypedArray(R.array.country_names)
                .getString(position);
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
        ((BaseActivity) getActivity()).setProcessShow(true);
        ((BaseActivity) getActivity()).showContentContainer();
    }

    private void proccessResult(Collection<Parcelable> result) {
        Log.i(TAG,"ON proccessResult");
        requestResponse = new ArrayList<Collection<? extends TeaserSpecification<?>>>();
        pagesTitles = new ArrayList<String>();

        if (currentPosition == -1) {
            currentPosition = Math.abs((result.size() + 2) / 2);
            initialPosition = currentPosition;
        }

        int count = 0;
        Homepage firstHomePage = (Homepage) result.toArray()[0];
        Homepage lastHomePage = (Homepage) result.toArray()[result.size() - 1];

        pagesTitles.add(lastHomePage.getHomepageTitle());
        requestResponse.add(lastHomePage.getTeaserSpecification());
        for (Parcelable homepage : result) {

            pagesTitles.add(((Homepage) homepage).getHomepageTitle());
            requestResponse.add(((Homepage) homepage).getTeaserSpecification());

            count++;
        }
        pagesTitles.add(firstHomePage.getHomepageTitle());
        requestResponse.add(firstHomePage.getTeaserSpecification());

        if (requestResponse != null) {
            // if (mPagerAdapter == null) {
            mPagerAdapter = new HomeCollectionPagerAdapter(getChildFragmentManager());
            // }
            setLayout(currentPosition);
        } else {
            ((BaseActivity) getActivity()).setProcessShow(false);
            triggerTeasers();
        }
        configureLayout();
    }
    private void configureLayout(){
      Log.i(TAG,"configureLayout");
        try {
            setLayoutSpec();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        ((BaseActivity) getActivity()).showContentContainer();
    }

    /**
     * This method will invalidate the HomeNewsletterSignupForm handler avoiding an infinite loop-
     */
    private static void invalidateHomeNewsletterSignupForm() {
        Log.i(TAG, "invalidateHomeNewsletterSignupForm");

        mHomeNewsletterSignupForm = new HomeNewslettersSignupForm();
        mHomeNewsletterSignupForm.isValid = false;
    }

    protected boolean onSuccessEvent(Bundle bundle) {
        Log.i(TAG,"ON onSuccessEvent");
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        if(getBaseActivity() == null){
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_TEASERS_EVENT:
            Collection<Parcelable> collection = (Collection<Parcelable>) bundle.getParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY);
            if(collection != null) {
                proccessResult(collection);
                configureLayout();
            } else
                setLayoutFallback();
            break;
        case GET_CALL_TO_ORDER_PHONE:
            SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
                    ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(KEY_CALL_TO_ORDER,
                    (String) bundle.getString(Constants.BUNDLE_RESPONSE_KEY));
            editor.commit();

            break;
        case GET_PROMOTIONS:
            if (((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getIsStillValid()) {
                DialogPromotionFragment.newInstance((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).show(
                        getChildFragmentManager(), null);
            } else {
                Log.i(TAG, "promotion expired!" + ((Promotion) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getEndDate());
            }

            break;
        case GET_HOME_NEWSLETTERS_SIGNUP_FORM_EVENT:
            mHomeNewsletterSignupForm = (HomeNewslettersSignupForm) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);

            // if response doesn't bring the info, invalidate newsletter form
            if (mHomeNewsletterSignupForm == null) {
                invalidateHomeNewsletterSignupForm();
            }

            break;
        }

        return false;
    }

    public void onErrorEvent(Bundle bundle) {
        if (!isVisible()) {
            return;
        }

        if (getBaseActivity() != null && getBaseActivity().handleErrorEvent(bundle)) {
            invalidateHomeNewsletterSignupForm();

            return;
        }

        if (getBaseActivity() == null) {
            return;
        }

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        switch (eventType) {
        case GET_TEASERS_EVENT:
            setLayoutFallback();
            break;
        case GET_PROMOTIONS:
            break;
        case GET_HOME_NEWSLETTERS_SIGNUP_FORM_EVENT:
            invalidateHomeNewsletterSignupForm();

            break;
        }
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public static class HomeCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public HomeCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = new HomeObjectFragment();
            Bundle args = new Bundle();
            args.putInt(HomeObjectFragment.ARG_OBJECT, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            if (pagesTitles != null) {
                return pagesTitles.size();
            }

            return 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagesTitles.get(position).toUpperCase();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // Since we only use more or less 7 Elements, keep them in memory will consume less.

            // super.destroyItem(container, position, object);

        }

    }

    public void triggerContentEventFromHomeObjectFragment() {
        
        /**
         * TRIGGERS
         * @author sergiopereira
         */
        triggerTeasers();
        
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class HomeObjectFragment extends Fragment {
       
        private LayoutInflater mInflater;

        private int position;

        private ScrollViewWithHorizontal mScrollViewWithHorizontal;
        
        private RelativeLayout mPopArrows;
        
        public HomeObjectFragment() {
        }

        private OnClickListener teaserClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String targetUrl = (String) v.getTag(R.id.target_url);
                TargetType targetType = (TargetType) v.getTag(R.id.target_type);
                String targetTitle = (String) v.getTag(R.id.target_title);
                if (targetType != null) {
                    Bundle bundle = new Bundle();
                    Log.d(TAG, "targetType = " + targetType.name() + " targetUrl = " + targetUrl);
                    switch (targetType) {
                    case CATEGORY:

                        bundle.putString(ConstantsIntentExtra.CATEGORY_URL, targetUrl);
                        bundle.putSerializable(ConstantsIntentExtra.CATEGORY_LEVEL,
                                FragmentType.CATEGORIES_LEVEL_1);
                        ((BaseActivity) getActivity()).onSwitchFragment(
                                FragmentType.CATEGORIES_LEVEL_1, bundle,
                                FragmentController.ADD_TO_BACK_STACK);

                        break;
                    case PRODUCT_LIST:
                        if (targetUrl != null) {
                            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
                            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
                            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, null);
                            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE,
                                    R.string.gteaser_prefix);
                            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, targetUrl);
                            ((BaseActivity) getActivity()).onSwitchFragment(
                                    FragmentType.PRODUCT_LIST, bundle, true);
                        }
                        break;
                    case PRODUCT:
                        if (targetUrl != null) {
                            JumiaApplication.INSTANCE.showRelatedItemsGlobal = false;
                            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
                            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
                            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                            ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
                        }
                        break;
                    case BRAND:
                        if (targetUrl != null) {
                            bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
                            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetUrl);
                            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, targetUrl);
                            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
                            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                            ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_LIST, bundle, FragmentController.ADD_TO_BACK_STACK);                            
                        }
                        break;
                    case CAMPAIGN:
                        String targetPosition = "0";
                        if(v.getTag(R.id.position) != null){
                            targetPosition = v.getTag(R.id.position).toString();
                        }
                        Log.i(TAG, "code1campaign position : "+targetPosition);
                        if (targetUrl != null && targetPosition != null && JumiaApplication.hasSavedTeaserCampaigns()) {
                            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
                            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetTitle);
                            // Selected campaign position
                            Log.d(TAG, "ON CLICK CAMPAIGN: " + targetTitle + " " + targetUrl + " " + targetPosition);
                            bundle.putParcelableArrayList(CampaignsFragment.CAMPAIGNS_TAG, JumiaApplication.getSavedTeaserCampaigns());
                            bundle.putInt(CampaignsFragment.CAMPAIGN_POSITION_TAG, Integer.valueOf(targetPosition));
                            ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.CAMPAIGNS, bundle, FragmentController.ADD_TO_BACK_STACK);
                        }
                        break;
                    default:
                        Toast.makeText(getActivity(),
                                "The target for this teaser is not defined!",
                                Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
        };

        public static final String ARG_OBJECT = "object";
        private SharedPreferences sharedPrefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(
                    R.layout.fragment_collection_object, container, false);
            mScrollViewWithHorizontal = (ScrollViewWithHorizontal) rootView.findViewById(R.id.view_pager_element_scrollview);
            
            mInflater = inflater;

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();

            Bundle args = getArguments();
            LinearLayout view = (LinearLayout) getView()
                    .findViewById(R.id.view_pager_element_frame);
            sharedPrefs = getActivity().getSharedPreferences(
                    ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);

            if (args.getInt(ARG_OBJECT) == HomeFragment.initialPosition) {
                showTips();
            }

            if (requestResponse != null) {
                processTeasersResult(requestResponse.get(args.getInt(ARG_OBJECT)), view);
            }
        }

        @Override
        public void onLowMemory() {
            Log.i(HomeObjectFragment.class.getName(), "onLowMemory");
            super.onLowMemory();
        }

        @Override
        public void onPause() {
            Log.i(HomeObjectFragment.class.getName(), "onPause");
            super.onPause();
        }

        @Override
        public void onDestroy() {
            Log.i(HomeObjectFragment.class.getName(), "onDestroy");
            super.onDestroy();
        }

        /**
         * Show tips if is the first time the user uses the app.
         */
        private void showTips() {

            if (sharedPrefs.getBoolean(ConstantsSharedPrefs.KEY_SHOW_TIPS, true)) {
                RelativeLayout homeTip = (RelativeLayout) getView()
                        .findViewById(R.id.home_tip);
                homeTip.setVisibility(View.VISIBLE);
                homeTip.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        v.setVisibility(View.GONE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putBoolean(ConstantsSharedPrefs.KEY_SHOW_TIPS,
                                false);
                        editor.commit();
                        return false;
                    }
                });
            }
        }

        private void processTeasersResult(Collection<? extends TeaserSpecification<?>> result,
                LinearLayout mainView) {
            TeasersFactory mTeasersFactory = new TeasersFactory();
            try {
                mainView.removeAllViews();
            } catch (IllegalArgumentException e) {
               e.printStackTrace();
            }
            
            for (Iterator iterator = result.iterator(); iterator.hasNext();) {
                TeaserSpecification<?> teaserSpecification = (TeaserSpecification<?>) iterator.next();
                if(mainView != null && mTeasersFactory != null && teaserSpecification != null){
                    View mView = mTeasersFactory.getSpecificTeaser(getActivity(), mainView, teaserSpecification, mInflater, teaserClickListener);
                            if(mView != null){
                                mainView.addView(mView);            
                            }
                }
            }

            if (mainView != null) {
                mainView.addView(generateNewsletterSubscribe(mainView));

                if (lastViewed != null && lastViewed.size() > 0) {
                    mainView.addView(generateLastViewedLayout(mainView));
                }
            }
        }

        /**
         * Handler used to managed newsletter subscription form visibility depending on response
         */
        Handler handlerProccessNewsletterSubscribe = new Handler() {
            public void handleMessage(android.os.Message msg) {
                View newsletterView = (View) msg.obj;
                // check if mHomeNewsletterSignupForm is already set with response
                if (HomeFragment.mHomeNewsletterSignupForm == null) {
                    Log.i(TAG, "handlerProccessNewsletterSubscribe -  mHomeNewsletterSignupForm not initialized");
                    
                    handlerProccessNewsletterSubscribe.sendMessageDelayed(msg, DELAY_FOR_NEWSLETTER_RETRY);
                } else if (HomeFragment.mHomeNewsletterSignupForm.isValid) {
                    Log.i(TAG, "handlerProccessNewsletterSubscribe -  mHomeNewsletterSignupForm correctly initialized");

                    showNewsletterSubscribe(newsletterView);
                } else {
                    // mHomeNewsletterSignupForm is not valid
                    Log.i(TAG, "handlerProccessNewsletterSubscribe -  mHomeNewsletterSignupForm is not valid");

                    newsletterView.setVisibility(View.GONE);
                }
            };
        };

        /**
         * Generate View for newsletter subscription form
         * 
         * @param parent
         * @return
         */
        private View generateNewsletterSubscribe(ViewGroup parent) {
            View newsletterView = mInflater.inflate(R.layout.teaser_newsletter, parent, false);

            // hide newsletter subscription form and show loading
            newsletterView.findViewById(R.id.newsletter_form_container).setVisibility(View.GONE);
            newsletterView.findViewById(R.id.loading_newsletter).setVisibility(View.VISIBLE);

            // check if mHomeNewsletterSignupForm is already set with response
            if (HomeFragment.mHomeNewsletterSignupForm == null) {
                Log.i(TAG, "newsLetterSubscribe is null");

                // pass View newsletterView to handler
                Message msg = new Message();
                msg.obj = newsletterView;

                handlerProccessNewsletterSubscribe.sendMessageDelayed(msg, DELAY_FOR_NEWSLETTER_RETRY);
            } else if (HomeFragment.mHomeNewsletterSignupForm.isValid) {
                Log.i(TAG, "newsLetterSubscribe visible");

                showNewsletterSubscribe(newsletterView);
            } else {
                Log.i(TAG, "newsLetterSubscribe invalid");
                // is not valid
                newsletterView.setVisibility(View.GONE);
            }

            return newsletterView;
        }

        /**
         * Set newsletter subscription form visible
         * 
         * @param newsletterView
         */
        private void showNewsletterSubscribe(View newsletterView) {
            FieldValidation emailValidation = HomeFragment.mHomeNewsletterSignupForm.emailValidation;
            LinkedHashMap<String, String> categories = HomeFragment.mHomeNewsletterSignupForm.categories;

            // show newsletter form again and hide loading
            newsletterView.findViewById(R.id.newsletter_form_container).setVisibility(View.VISIBLE);
            newsletterView.findViewById(R.id.loading_newsletter).setVisibility(View.GONE);

            final EditText newsletterEmail = (EditText) newsletterView.findViewById(R.id.newsletter_subscription_value);

            // save email after EditText lose focus, to be reused after rotations or coming back to Home
            newsletterEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String email = newsletterEmail.getText().toString();
                        if (!email.isEmpty()) {
                            lastEmail = email;
                        } else {
                            lastEmail = null;
                        }
                    }
                }
            });

            // get last email saved before rotation
            String customerEmail = lastEmail;
            if (customerEmail == null) {
                // fill with customer's email address if he is logged in
                if (JumiaApplication.INSTANCE.CUSTOMER != null) {
                    customerEmail = JumiaApplication.INSTANCE.CUSTOMER.getEmail();
                }
            }
            // show customerEmail on EditText
            if (!TextUtils.isEmpty(customerEmail)) {
                newsletterEmail.setText(customerEmail);
            }

            // add maxLength validation
            if (emailValidation.max > 0) {
                InputFilter[] inputFilters = new InputFilter[] { new InputFilter.LengthFilter(emailValidation.max) };
                newsletterEmail.setFilters(inputFilters);
            }

            class NewsletterGenderClickListener implements OnClickListener {
                String emailRegex = "[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}";

                public NewsletterGenderClickListener(String emailRegex) {
                    this.emailRegex = emailRegex;
                }

                @Override
                public void onClick(View v) {
                    String email = newsletterEmail.getText().toString();
                    if (!email.isEmpty() && email.matches(emailRegex)) {
                        // show progress until process response
                        ((BaseActivity) getActivity()).showProgress();

                        // category to use on request
                        int category = 0;
                        Object tagObject = v.getTag();
                        if (tagObject != null && tagObject instanceof String) {
                            try {
                                category = Integer.valueOf(tagObject.toString());
                            } catch (NumberFormatException e) {
                                // TODO default first category
                                category = 6;
                            }
                        }
                        triggerSubscribeNewsletter(email, category);
                    } else {
                        String message = getString(R.string.newsletter_invalid_message);
                        String ok = getString(R.string.ok_label);
                        mDialogNewsletter = DialogGenericFragment.newInstance(null, true, false, null, message, ok, null, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialogNewsletter.dismiss();
                            }
                        });
                        mDialogNewsletter.show(getFragmentManager(), null);
                    }
                }
            }

            Button firstBtn = (Button) newsletterView.findViewById(R.id.newsletter_first_btn);
            Button secondBtn = (Button) newsletterView.findViewById(R.id.newsletter_second_btn);

            // get regex from FieldValidation from response
            NewsletterGenderClickListener newsletterGenderClickListener = new NewsletterGenderClickListener(emailValidation.regex);

            // set buttons' labels
            Iterator categoriesIterator = categories.entrySet().iterator();
            Entry<String, String> firstItem = (Entry<String, String>) categoriesIterator.next();
            if (firstItem != null) {
                // set tag
                String firstTag = firstItem.getKey();
                firstBtn.setTag(firstTag);

                // set label
                String firstLabel = firstItem.getValue();
                firstBtn.setText(firstLabel);
                firstBtn.setContentDescription(firstLabel);
                firstBtn.setOnClickListener(newsletterGenderClickListener);

                Entry<String, String> secondItem = (Entry<String, String>) categoriesIterator.next();
                if (secondItem != null) {
                    // set tag
                    String secondTag = secondItem.getKey();
                    secondBtn.setTag(secondTag);

                    // set label
                    String secondLabel = secondItem.getValue();
                    secondBtn.setText(secondLabel);
                    secondBtn.setContentDescription(secondLabel);
                    secondBtn.setOnClickListener(newsletterGenderClickListener);
                }
            }
        }

        private View generateLastViewedLayout(ViewGroup parent){
            View lastViewedView = mInflater.inflate(R.layout.teaser_last_viewed, parent, false);
            mPopArrows = (RelativeLayout) lastViewedView.findViewById(R.id.pop_arrows_container);
            if(lastViewed.size() >3){
                mScrollViewWithHorizontal.sendListenerAndView(receiveIsVisible, lastViewedView);    
            }
            
            ViewPager mViewPager = (ViewPager) lastViewedView.findViewById(R.id.last_viewed_viewpager);
            int partialSize = 3;
            if(((BaseActivity) getActivity()).isTabletInLandscape(getActivity())){
                partialSize = 5;
            }
            LastViewedAdapter mLastViewedAdapter = new LastViewedAdapter(getActivity(), lastViewed, mInflater, partialSize);
            mViewPager.setAdapter(mLastViewedAdapter);
            return lastViewedView;
        }
        
        private Handler receiveIsVisible = new Handler(){
            
            public void handleMessage(android.os.Message msg) {
                mPopArrows.setVisibility(View.VISIBLE);
                hideArrows.sendEmptyMessageDelayed(0, 1000);
            }
        }; 
        
        private Handler hideArrows = new Handler(){
            
            public void handleMessage(android.os.Message msg) {
                mPopArrows.setVisibility(View.GONE);
            }
        };

        /**
         * HOME TRIGGERS
         * 
         * @author Andre Lopes
         */

        /**
         * request to subscribe user to newsletter chosen
         */
        public void triggerSubscribeNewsletter(String email, int category) {
            Bundle bundle = new Bundle();

            ContentValues contentValues = new ContentValues();
            contentValues.put(mHomeNewsletterSignupForm.emailField, email);
            contentValues.put(mHomeNewsletterSignupForm.categoryField, category);

            // set dynamic url to be used on HomeNewslettersSignupHelper request
            bundle.putString(HomeNewslettersSignupHelper.NEWSLETTER_SIGNUP_URL, mHomeNewsletterSignupForm.action);
            bundle.putParcelable(HomeNewslettersSignupHelper.FORM_CONTENT_VALUES, contentValues);
            JumiaApplication.INSTANCE.sendRequest(new HomeNewslettersSignupHelper(), bundle, homeResponseCallback);
        }

        private void onHomeErrorEvent(Bundle bundle) {
            Log.i(TAG, "ON onHomeErrorEvent");

            // Validate fragment visibility
            if (!isVisible()) {
                Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
                return;
            }

            BaseActivity baseActivity = (BaseActivity) getActivity();
            if (baseActivity != null && baseActivity.handleErrorEvent(bundle)) {
                invalidateHomeNewsletterSignupForm();

                return;
            }

            if (baseActivity == null) {
                invalidateHomeNewsletterSignupForm();

                return;
            }

            baseActivity.dismissProgress();

            EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);

            switch (eventType) {
            case HOME_NEWSLETTERS_SIGNUP_FORM_EVENT:
                Log.d(TAG, "RECEIVED HOME_NEWSLETTERS_SIGNUP_FORM_EVENT");

                mDialogNewsletter = DialogGenericFragment.newInstance(null, true, false, null,
                        getErrorMessage(bundle),
                        getString(R.string.ok_label), null, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialogNewsletter.dismiss();
                            }
                        });
                mDialogNewsletter.show(getFragmentManager(), null);
                break;
            }
        }

        private boolean onHomeSuccessEvent(Bundle bundle) {
            Log.i(TAG, "ON onHomeSuccessEvent");

            // Validate fragment visibility
            if (!isVisible()) {
                Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
                return false;
            }

            BaseActivity baseActivity = (BaseActivity) getActivity();
            baseActivity.dismissProgress();

            EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
            switch (eventType) {
            case HOME_NEWSLETTERS_SIGNUP_FORM_EVENT:
                mDialogNewsletter = DialogGenericFragment.newInstance(null, true, false, null,
                        getSuccessMessage(bundle, eventType),
                        getString(R.string.ok_label), null, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mDialogNewsletter.dismiss();
                            }
                        });
                mDialogNewsletter.show(getFragmentManager(), null);

                break;
            }

            return false;
        }

        /**
         * Get error message from responseerrormessage on bundle
         * 
         * @param bundle
         * 
         * @return the error, or <code>null</code> if no error on <code>responseerrormessage</code>
         */
        String getErrorMessage(Bundle bundle) {
            String errorMessage = null;

            Object errorObject = bundle.get(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
            if (errorObject != null && errorObject instanceof HashMap) {
                Object errorMessagesObject = ((HashMap) errorObject).get(Constants.BUNDLE_ERROR_KEY);
                if (errorMessagesObject != null && errorMessagesObject instanceof List) {
                    List errorMessages = (List) errorMessagesObject;
                    if (errorMessages != null && errorMessages.size() > 0) {
                        // get first error message
                        Object errorMessageObject = errorMessages.get(0);
                        if (errorMessageObject != null && errorMessageObject instanceof String) {
                            errorMessage = (String) errorMessageObject;
                        }
                    }
                }
            }

            if (TextUtils.isEmpty(errorMessage)) {
                // default error message
                errorMessage = getString(R.string.server_error_title);
            }

            return errorMessage;
        }

        /**
         * Get success message from resources depending on eventType
         * 
         * @param bundle
         * 
         * @return the respective message or "ok"
         */
        String getSuccessMessage(Bundle bundle, EventType eventType) {
            String successMessage = null;

            // successMessage = bundle.getString(Constants.BUNDLE_RESPONSE_SUCCESS_MESSAGE_KEY);

            switch (eventType) {
            case HOME_NEWSLETTERS_SIGNUP_FORM_EVENT:
                // newsletter success message
                successMessage = getString(R.string.newsletter_success_message);

                break;
            default:
                // default "success" message
                successMessage = getString(R.string.ok_label);
            }

            return successMessage;
        }

        /**
         * HOME CALLBACK
         */
        IResponseCallback homeResponseCallback = new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
                onHomeErrorEvent(bundle);
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                onHomeSuccessEvent(bundle);
            }
        };
    }

    /**
     * TRIGGERS
     * @author sergiopereira
     */
    private void triggerPromotions(){
        Bundle bundle = new Bundle();
        triggerContentEventWithNoLoading(new GetPromotionsHelper(), bundle, responseCallback);
    }
    
    private void triggerTeasers(){
        triggerContentEvent(new GetTeasersHelper(), null, responseCallback);
    }
    
    private void triggerCallToOrder(){
        Bundle bundle = new Bundle();
        triggerContentEventWithNoLoading(new GetCallToOrderHelper(), bundle, responseCallback);
    }

    private void triggerHomeNewsletterSignupForm() {
        triggerContentEventWithNoLoading(new GetHomeNewslettersSignupFormHelper(), null, responseCallback);
    }

    /**
     * TRIGGERS
     * @author Manuel Silva
     */
    private void triggerAutoLogin(){
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetLoginHelper.LOGIN_CONTENT_VALUES, JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
        triggerContentEventWithNoLoading(new GetLoginHelper(), bundle, responseCallback);
    }
    
    /**
     * CALLBACK
     * @author sergiopereira
     */
    IResponseCallback responseCallback = new IResponseCallback() {
        
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }
        
        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };
}
