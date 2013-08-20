/**
 * 
 */
package pt.rocket.view;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.BrandsTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.Homepage;
import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.objects.ImageTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.MixpanelTracker;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.JumiaViewPager;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.view.fragments.BrandsTeaserListFragment;
import pt.rocket.view.fragments.CategoryTeaserFragment;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.MainOneSlideFragment;
import pt.rocket.view.fragments.ProducTeaserListFragment;
import pt.rocket.view.fragments.StaticBannerFragment;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author manuelsilva
 * 
 */
public class HomeFragmentActivity extends BaseActivity {
    private final static String TAG = HomeFragmentActivity.class.getSimpleName();

    private LayoutInflater mInflater;
    private JumiaViewPager mPager;
    private PagerTabStrip pagerTabStrip;

    private final int TAB_PREV_ID = 0;
    private final int TAB_CURR_ID = 1;
    private final int TAB_NEXT_ID = 2;

    private final int TAB_INDICATOR_HEIGHT = 0;
    private final int TAB_UNDERLINE_HEIGHT = 1;
    private final int TAB_STRIP_COLOR = android.R.color.transparent;
    private final int TAB_COLOR_TEXT_UNSELECTED = R.color.strip_title;
    private final int TAB_COLOR_BACKGROUND = R.color.strip_title_background;

    private final float TAB_SHADOW_COMPLETE = 0.4f;

    // Gradient
    // private final int TAB_COLOR_INDICATOR_UP = R.color.pager_title_up;
    // private final int TAB_COLOR_INDICATOR_DOWN = R.color.pager_title_down;

    private final String FONT_TEXT_SELECTED = "fonts/Roboto-Bold.ttf";

    private HomeCollectionPagerAdapter mPagerAdapter;
    private static ArrayList<String> pagesTitles;
    public static ArrayList<Collection<? extends TeaserSpecification<?>>> requestResponse;

    private OnActivityFragmentInteraction mCallback;

    Activity activity;

    private boolean isFirstBoot = true;

    private int defaultPosition;

    /**
	 */
    public HomeFragmentActivity() {
        super(R.layout.search,
                NavigationAction.Home,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_TEASERS_EVENT, EventType.GET_CALL_TO_ORDER_PHONE),
                EnumSet.noneOf(EventType.class),
                0, R.layout.teasers_fragments_viewpager);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        mInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        changeSearchBarBehavior();

        if (requestResponse == null) {
            setProcessShow(false);
            triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
            triggerContentEvent(new RequestEvent(EventType.GET_CALL_TO_ORDER_PHONE));
        } else {
            restoreLayout();
        }

        HockeyStartup.register(this);
    }

    public void moveToRight() {
    }

    public void moveToLeft() {
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
        // Get text
        final TextView currTextView = (TextView) pagerTabStrip.getChildAt(TAB_CURR_ID);
        final TextView nextTextView = (TextView) pagerTabStrip.getChildAt(TAB_NEXT_ID);
        final TextView prevTextView = (TextView) pagerTabStrip.getChildAt(TAB_PREV_ID);

        // Set Color
        currTextView.setPadding(0, 0, 0, 1);

        // Calculate the measures
        final float density = activity.getResources().getDisplayMetrics().density;
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

    private void setLayout(int defaultPosition) {
        if (mPager == null) {
            mPager = (JumiaViewPager) findViewById(R.id.home_viewpager);
        }

        if (pagerTabStrip == null) {
            pagerTabStrip = (PagerTabStrip) findViewById(R.id.home_titles);
        }
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(defaultPosition);
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
        setProcessShow(true);
        showContentContainer();
    }

    private void restoreLayout() {

        if (requestResponse != null) {
            defaultPosition = Math.abs(requestResponse.size() / 2);

            if (mPagerAdapter == null) {
                mPagerAdapter = new HomeCollectionPagerAdapter(getSupportFragmentManager());
            }

            setLayout(defaultPosition);
        } else {
            setProcessShow(false);
            triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
        }
    }

    private void proccessResult(Collection<? extends Homepage> result) {
        requestResponse = new ArrayList<Collection<? extends TeaserSpecification<?>>>();
        pagesTitles = new ArrayList<String>();
        defaultPosition = Math.abs(result.size() / 2);

        int count = 0;
        for (Homepage homepage : result) {
            pagesTitles.add(homepage.getHomepageTitle());
            requestResponse.add(homepage.getTeaserSpecification());
            // if(homepage.isDefaultHomepage()){
            // defaultPosition = count;
            // }
            count++;
        }

        if (requestResponse != null) {
            if (mPagerAdapter == null) {
                mPagerAdapter = new HomeCollectionPagerAdapter(getSupportFragmentManager());
            }
            setLayout(defaultPosition);
        } else {
            setProcessShow(false);
            triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(this);
        }

        if (requestResponse == null && !isFirstBoot) {
            setProcessShow(false);
            triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
        } else {
            restoreLayout();
        }

        AnalyticsGoogle.get().trackPage(R.string.ghomepage);
    }

    @Override
    protected void onDestroy() {
        MixpanelTracker.flush();
        mPagerAdapter = null;

        super.onDestroy();
    }

    private void changeSearchBarBehavior() {
        final EditText autoComplete = (EditText) findViewById(R.id.search_component);
        autoComplete.setEnabled(false);
        autoComplete.setFocusable(false);
        autoComplete.setFocusableInTouchMode(false);
        findViewById(R.id.search_overlay).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ActivitiesWorkFlow.searchActivity(HomeFragmentActivity.this);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        switch (event.getType()) {
        case GET_TEASERS_EVENT:
            isFirstBoot = false;
            proccessResult((Collection<? extends Homepage>) event.result);
            break;
        case GET_CALL_TO_ORDER_PHONE:
            SharedPreferences sharedPrefs = this.getSharedPreferences(
                    ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(ProductDetailsActivityFragment.KEY_CALL_TO_ORDER,
                    (String) event.result);
            editor.commit();

            break;
        }

        return true;
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public class HomeCollectionPagerAdapter extends FragmentStatePagerAdapter {
        public HomeCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new HomeObjectFragment(position);
        }

        @Override
        public int getCount() {
            return pagesTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagesTitles.get(position).toUpperCase();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            super.destroyItem(container, position, object);
        }
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class HomeObjectFragment extends Fragment {

        private OnActivityFragmentInteraction mCallback;

        private LayoutInflater mInflater;
        
        private int position;

        public HomeObjectFragment(int position) {
            this.position = position;
        }
        
        private OnClickListener teaserClickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                String targetUrl = (String) v.getTag(R.id.target_url);
                TargetType targetType = (TargetType) v.getTag(R.id.target_type);
                String targetTitle = (String) v.getTag(R.id.target_title);
                if (targetType != null) {
                    Log.d(TAG, "targetType = " + targetType.name() + " targetUrl = " + targetUrl);
                    switch (targetType) {
                    case CATEGORY:
                        ActivitiesWorkFlow
                                .categoriesActivityNew(getActivity(), targetUrl);
                        break;
                    case PRODUCT_LIST:
                        if (targetUrl != null) {
                            ActivitiesWorkFlow.productsActivity(getActivity(),
                                    targetUrl, targetTitle, null, R.string.gteaser_prefix,
                                    AnalyticsGoogle.prepareNavigationPath(targetUrl));
                        }
                        break;
                    case PRODUCT:
                        if (targetUrl != null) {
                            ActivitiesWorkFlow.productsDetailsActivity(
                                    getActivity(), targetUrl,
                                    R.string.gteaserprod_prefix, "");
                        }
                        break;
                    case BRAND:
                        if (targetUrl != null) {
                            ActivitiesWorkFlow.productsActivity(getActivity(), null, targetUrl,
                                    targetUrl, R.string.gsearch, "");
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

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mInflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.fragment_collection_object, container, false);
            // ((TextView)
            // rootView.findViewById(R.id.view_pager_element_frame)).setText(pagesTitles.get(0));
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();

            LinearLayout view = (LinearLayout) getView()
                    .findViewById(R.id.view_pager_element_frame);

            if (requestResponse != null) {
                Log.i(TAG, "code1onstart "+position);
                processResult(requestResponse.get(position), view);
            } else {
                ((HomeFragmentActivity) getActivity()).triggerContentEvent(new RequestEvent(
                        EventType.GET_TEASERS_EVENT));
            }
        }

        private void processResult(Collection<? extends TeaserSpecification<?>> result,
                LinearLayout mainView) {

            for (Iterator iterator = result.iterator(); iterator.hasNext();) {
                TeaserSpecification<?> teaserSpecification = (TeaserSpecification<?>) iterator
                        .next();
                switch (teaserSpecification.getType()) {
                case MAIN_ONE_SLIDE:

                    if (((ImageTeaserGroup) teaserSpecification).getTeasers().size() > 0) {

                        Fragment fragmentMainOneSlide = MainOneSlideFragment.getInstance();
                        // This makes sure that the container activity has implemented
                        // the callback interface. If not, it throws an exception
                        try {
                            mCallback = (OnActivityFragmentInteraction) fragmentMainOneSlide;
                        } catch (ClassCastException e) {
                            throw new ClassCastException(fragmentMainOneSlide.toString()
                                    + " must implement OnActivityFragmentInteraction");
                        }

                        mCallback.sendListener(0, teaserClickListener);
                        mCallback.sendValuesToFragment(0,
                                ((ImageTeaserGroup) teaserSpecification)
                                        .getTeasers());
                        View view = mInflater
                                .inflate(R.layout.main_one_fragment_frame, null, false);
                        mainView.addView(view);
                        fragmentManagerTransition(R.id.main_one_frame, fragmentMainOneSlide, false,
                                false);
                    }
                    break;
                case STATIC_BANNER:
                    Log.i(TAG, "code1static STATIC_BANNER");
                    Fragment fragmentStaticBanner = StaticBannerFragment.getInstance();

                    // This makes sure that the container activity has implemented
                    // the callback interface. If not, it throws an exception
                    try {
                        mCallback = (OnActivityFragmentInteraction) fragmentStaticBanner;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(fragmentStaticBanner.toString()
                                + " must implement OnActivityFragmentInteraction");
                    }

                    mCallback.sendListener(0, teaserClickListener);

                    mCallback.sendValuesToFragment(0, ((ImageTeaserGroup) teaserSpecification)
                            .getTeasers());

                    View viewTeaser = mInflater.inflate(R.layout.static_teaser_frame, null, false);
                    mainView.addView(viewTeaser);
                    fragmentManagerTransition(R.id.static_teaser_frame, fragmentStaticBanner,
                            false, false);
                    break;
                case CATEGORIES:

                    Fragment fragmentCategoryTeaser = CategoryTeaserFragment.getInstance();

                    // This makes sure that the container activity has implemented
                    // the callback interface. If not, it throws an exception
                    try {
                        mCallback = (OnActivityFragmentInteraction) fragmentCategoryTeaser;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(fragmentCategoryTeaser.toString()
                                + " must implement OnActivityFragmentInteraction");
                    }

                    mCallback.sendListener(0, teaserClickListener);
                    mCallback
                            .sendValuesToFragment(0, (CategoryTeaserGroup) teaserSpecification);

                    View viewGeneric = mInflater.inflate(R.layout.generic_frame, null, false);
                    mainView.addView(viewGeneric);
                    fragmentManagerTransition(R.id.content_frame, fragmentCategoryTeaser, false,
                            false);
                    break;
                case PRODUCT_LIST:

                    Fragment fragmentProductListTeaser = ProducTeaserListFragment.getInstance();

                    // This makes sure that the container activity has implemented
                    // the callback interface. If not, it throws an exception
                    try {
                        mCallback = (OnActivityFragmentInteraction) fragmentProductListTeaser;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(fragmentProductListTeaser.toString()
                                + " must implement OnActivityFragmentInteraction");
                    }

                    mCallback.sendListener(0, teaserClickListener);
                    mCallback.sendValuesToFragment(0, (ProductTeaserGroup) teaserSpecification);
                    View viewProductList = mInflater.inflate(R.layout.product_list_frame, null,
                            false);
                    mainView.addView(viewProductList);
                    fragmentManagerTransition(R.id.products_list_frame, fragmentProductListTeaser,
                            false, false);
                    break;
                case BRANDS_LIST:

                    Fragment fragmentBrandsListTeaser = BrandsTeaserListFragment.getInstance();

                    // This makes sure that the container activity has implemented
                    // the callback interface. If not, it throws an exception
                    try {
                        mCallback = (OnActivityFragmentInteraction) fragmentBrandsListTeaser;
                    } catch (ClassCastException e) {
                        throw new ClassCastException(fragmentBrandsListTeaser.toString()
                                + " must implement OnActivityFragmentInteraction");
                    }

                    mCallback.sendListener(0, teaserClickListener);
                    mCallback.sendValuesToFragment(0, (BrandsTeaserGroup) teaserSpecification);

                    View viewBrandList = mInflater.inflate(R.layout.brands_list_frame, null, false);
                    mainView.addView(viewBrandList);
                    fragmentManagerTransition(R.id.brands_list_frame, fragmentBrandsListTeaser,
                            false, false);
                    break;
                }
            }
        }

        protected void fragmentManagerTransition(int container, Fragment fragment,
                Boolean addToBackStack, Boolean animated) {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            // Animations
            if (animated)
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,
                        R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
            // Replace
            fragmentTransaction.replace(container, fragment);
            // BackStack
            if (addToBackStack)
                fragmentTransaction.addToBackStack(null);
            // Commit
            fragmentTransaction.commit();
        }

    }
}
