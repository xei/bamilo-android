/**
 * 
 */
package pt.rocket.view.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.factories.TeasersFactory;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.BrandsTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.Homepage;
import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.objects.ImageTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.Promotion;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.MixpanelTracker;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.JumiaViewPager;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.utils.dialogfragments.DialogPromotionFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.ChangeCountryFragmentActivity;
import pt.rocket.view.R;
import uk.co.senab.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Type.CubemapFace;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
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

    private boolean isFirstBoot = true;

    // private int defaultPosition=Math.abs(requestResponse.size() / 2);

    private int currentPosition = -1;
    public static int initialPosition = 3;

    private static HomeFragment mHomeFragment;

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
        super(EnumSet.of(EventType.GET_API_INFO, EventType.GET_TEASERS_EVENT,
                EventType.GET_CALL_TO_ORDER_PHONE, EventType.GET_PROMOTIONS,
                EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT),
                EnumSet.noneOf(EventType.class), EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Home, 0);
        // this.setRetainInstance(true);
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
        HockeyStartup.register(getActivity());
        Log.i(TAG, "onCreate");
    }

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
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(getActivity());
        }

        SharedPreferences sP = getActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sP.getBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true)) {
            triggerContentEvent(new RequestEvent(EventType.GET_PROMOTIONS));
        }

        if (requestResponse == null) {
            ((BaseActivity) getActivity()).setProcessShow(false);
            triggerContentEvent(new RequestEvent(EventType.GET_NAVIGATION_LIST_COMPONENTS_EVENT));
            triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
            triggerContentEvent(new RequestEvent(EventType.GET_CALL_TO_ORDER_PHONE));
        } else {
            restoreLayout();
        }

        AnalyticsGoogle.get().trackPage(R.string.ghomepage);
       
        
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
        // TODO Auto-generated method stub
        //getActivity().finish();
        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        MixpanelTracker.flush();
        mPagerAdapter = null;
        mPager = null;
        pagerTabStrip = null;
    
        super.onDestroy();
        System.gc();
    }

    private void setLayout(int currentPositionPager) {
        Log.i(TAG, "setLayout");
        if (mPager == null) {
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
                   

                    if (arg0 == mPager.SCROLL_STATE_SETTLING) {
                        if (mPager != null)
                            mPager.setPagingEnabled(false);
                    }

                    if (arg0 == mPager.SCROLL_STATE_IDLE) {
                        new ChangePageTask().execute(arg0);
//                        mPager.setPagingEnabled(true);
//                        mPager.toggleJumiaScroller(true);
//                        Log.i(TAG, "ANDRE - SCROLL_STATE_IDLE POSITION : " + currentPosition);
//                        Log.i(TAG,
//                                "ANDRE - SCROLL_STATE_IDLE mpager item : "
//                                        + mPager.getCurrentItem());
//                        // change event of first(last) fragment to jump for original fragment
//                        if (mPager.getCurrentItem() == 0) {
//                            mPager.toggleJumiaScroller(false);
//
//                            mPager.setCurrentItem(pageCount - 2);
//
//                            // change event of last(first) fragment to jump for original fragment
//                        } else if (mPager.getCurrentItem() == pageCount - 1) {
//                            mPager.toggleJumiaScroller(false);
//
//                            mPager.setCurrentItem(1);
//
//                        }
//
//                        Log.i(TAG, "ANDRE - SCROLL_STATE_IDLE: FINISHED");
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
        try {
            setLayoutSpec();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        // }
        ((BaseActivity) getActivity()).setProcessShow(true);
        ((BaseActivity) getActivity()).showContentContainer();
    }

    private class ChangePageTask extends AsyncTask<Integer, String, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {
//            int arg0 = params[0];
//            
//            if (arg0 == mPager.SCROLL_STATE_IDLE) {
            if (null != mPager ) {
                mPager.setPagingEnabled(true);
                mPager.toggleJumiaScroller(true);
              
                
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        int pageCount = pagesTitles.size();
                        
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
            mPagerAdapter = new HomeCollectionPagerAdapter(getFragmentManager());
            // }
            setLayout(currentPosition);
        } else {
            Log.i(TAG, "restoreLayout -> NULL");
            ((BaseActivity) getActivity()).setProcessShow(false);
            triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
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

        mapBg.setImageDrawable(this.getResources().obtainTypedArray(R.array.country_fallback_map)
                .getDrawable(position));

        String country = this.getResources().obtainTypedArray(R.array.country_names)
                .getString(position);
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
            ((TextView) getView().findViewById(R.id.fallback_best)).setTextSize(11.88f);
            getView().findViewById(R.id.fallback_country_double).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.fallback_country).setVisibility(View.GONE);

        }
        getView().findViewById(R.id.fallback_best).setSelected(true);
        ((BaseActivity) getActivity()).setProcessShow(true);
        ((BaseActivity) getActivity()).showContentContainer();
    }

    private void proccessResult(Collection<? extends Homepage> result) {
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
        for (Homepage homepage : result) {

            pagesTitles.add(homepage.getHomepageTitle());
            requestResponse.add(homepage.getTeaserSpecification());

            count++;
        }
        pagesTitles.add(firstHomePage.getHomepageTitle());
        requestResponse.add(firstHomePage.getTeaserSpecification());

        if (requestResponse != null) {
            // if (mPagerAdapter == null) {
            mPagerAdapter = new HomeCollectionPagerAdapter(getFragmentManager());
            // }
            setLayout(currentPosition);
        } else {
            ((BaseActivity) getActivity()).setProcessShow(false);
            triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
        }
        try {
            setLayoutSpec();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        Log.i(TAG,"ON onSuccessEvent");
        switch (event.getType()) {
        case GET_API_INFO:
            break;
        case GET_TEASERS_EVENT:
            isFirstBoot = false;
            proccessResult((Collection<? extends Homepage>) event.result);
            Log.i(TAG, "code1 checkversion called");
            break;
        case GET_CALL_TO_ORDER_PHONE:
            SharedPreferences sharedPrefs = getActivity().getSharedPreferences(
                    ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(KEY_CALL_TO_ORDER,
                    (String) event.result);
            editor.commit();

            break;
        case GET_PROMOTIONS:
            if (((Promotion) event.result).getIsStillValid()) {
                DialogPromotionFragment.newInstance((Promotion) event.result).show(
                        getChildFragmentManager(), null);
            } else {
                Log.i(TAG, "promotion expired!" + ((Promotion) event.result).getEndDate());
            }

            break;
        }

        return false;
    }

    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        switch (event.getType()) {
        case GET_TEASERS_EVENT:
            setLayoutFallback();
            break;
        case GET_PROMOTIONS:
            break;
        }
        return true;
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
        triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class HomeObjectFragment extends Fragment {
       
        private LayoutInflater mInflater;

        private int position;

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
                            bundle.putString(ConstantsIntentExtra.CONTENT_URL, targetUrl);
                            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE,
                                    R.string.gteaserprod_prefix);
                            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                            ((BaseActivity) getActivity()).onSwitchFragment(
                                    FragmentType.PRODUCT_DETAILS, bundle,
                                    FragmentController.ADD_TO_BACK_STACK);
                        }
                        break;
                    case BRAND:
                        if (targetUrl != null) {
                            bundle.putString(ConstantsIntentExtra.CONTENT_URL, null);
                            bundle.putString(ConstantsIntentExtra.CONTENT_TITLE, targetUrl);
                            bundle.putString(ConstantsIntentExtra.SEARCH_QUERY, targetUrl);
                            bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gsearch);
                            bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                            ((BaseActivity) getActivity()).onSwitchFragment(
                                    FragmentType.PRODUCT_LIST, bundle,
                                    FragmentController.ADD_TO_BACK_STACK);

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
            Log.i(TAG, "onLowMemory");
            super.onLowMemory();
        }

        @Override
        public void onPause() {
            Log.i(TAG, "onLowMemory");
            super.onPause();
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "onDestroy");
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
            mainView.removeAllViews();
            for (Iterator iterator = result.iterator(); iterator.hasNext();) {
                TeaserSpecification<?> teaserSpecification = (TeaserSpecification<?>) iterator
                        .next();

                mainView.addView(mTeasersFactory.getSpecificTeaser(getActivity(), mainView,
                        teaserSpecification, mInflater, teaserClickListener));
            }
        }

    }
}
