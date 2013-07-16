/**
 * 
 */
package pt.rocket.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;

import de.akquinet.android.androlog.Log;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.HomePageViewPagerAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.BrandsTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.Homepage;
import pt.rocket.framework.objects.ImageTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.utils.BaseActivity;
import pt.rocket.utils.CheckVersion;
import pt.rocket.utils.HockeyStartup;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.view.fragments.BaseFragment;
import pt.rocket.view.fragments.BrandsTeaserListFragment;
import pt.rocket.view.fragments.CategoryTeaserFragment;
import pt.rocket.view.fragments.FragmentType;
import pt.rocket.view.fragments.HomeTeaserFragment;
import pt.rocket.view.fragments.MainOneSlideFragment;
import pt.rocket.view.fragments.ProducTeaserListFragment;
import pt.rocket.view.fragments.StaticBannerFragment;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
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
    private ViewPager mPager;
    private DemoCollectionPagerAdapter mPagerAdapter;
    private static ArrayList<String> pagesTitles;
    private static ArrayList<Collection<? extends TeaserSpecification<?>>> requestResponse;

    private OnActivityFragmentInteraction mCallback;

    Activity activity;

    /**
	 */
    public HomeFragmentActivity() {
        super(R.layout.search,
                NavigationAction.Home,
                EnumSet.noneOf(MyMenuItem.class),
                EnumSet.of(EventType.GET_TEASERS_EVENT),
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
        triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
        activity = this;
        mInflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPager = (ViewPager) findViewById(R.id.home_viewpager);
        changeSearchBarBehavior();
        HockeyStartup.register(this);

    }

    private void proccessResult(Collection<? extends Homepage> result) {

        // ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        // ArrayList<String> fragmentsTitles = new ArrayList<String>();
        // for (int i = 0; i < 5; i++) {
        // HomeTeaserFragment fragment1 = HomeTeaserFragment.newInstance();
        // try {
        // mCallback = (OnActivityFragmentInteraction) fragment1;
        // } catch (ClassCastException e) {
        // throw new ClassCastException(fragment1.toString()
        // + " must implement OnActivityFragmentInteraction");
        // }
        //
        // mCallback.sendValuesToFragment(0, result);
        //
        // fragments.add(fragment1);
        // fragmentsTitles.add("Home"+i);
        // }

        // HomeTeaserFragment fragment2 = HomeTeaserFragment.newInstance();
        // HomeTeaserFragment fragment3 = HomeTeaserFragment.newInstance();
        // HomeTeaserFragment fragment4 = HomeTeaserFragment.newInstance();
        //
        //
        // fragments.add(fragment2);
        // fragmentsTitles.add("Fragment2");
        // fragments.add(fragment3);
        // fragmentsTitles.add("Fragment3");
        // fragments.add(fragment4);
        // fragmentsTitles.add("Fragment4");
        requestResponse = new ArrayList<Collection<? extends TeaserSpecification<?>>>();
        pagesTitles = new ArrayList<String>();

        for (Homepage homepage : result) {
            pagesTitles.add(homepage.getHomepageTitle());
            requestResponse.add(homepage.getTeaserSpecification());
            Log.i(TAG, "code1 teaser size is: " + homepage.getTeaserSpecification().size());
        }

        mPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());

        mPager.setAdapter(mPagerAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (CheckVersion.needsToShowDialog()) {
            CheckVersion.showDialog(this);
        }

        AnalyticsGoogle.get().trackPage(R.string.ghomepage);
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
        Log.d(TAG, "Got teasers event: " + event);

        proccessResult((Collection<? extends Homepage>) event.result);
        return false;
    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
    // and NOT a FragmentPagerAdapter.
    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DemoObjectFragment();
            Bundle args = new Bundle();
            // Our object is just an integer :-P
            args.putInt(DemoObjectFragment.ARG_OBJECT, i);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return pagesTitles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagesTitles.get(position);
        }
    }

    // Instances of this class are fragments representing a single
    // object in our collection.
    public static class DemoObjectFragment extends Fragment {

        private Fragment fragmentMainOneSlide;
        private Fragment fragmentStaticBanner;
        private Fragment fragmentCategoryTeaser;
        private Fragment fragmentProductListTeaser;
        private Fragment fragmentBrandsListTeaser;

        private OnActivityFragmentInteraction mCallback;

        private LayoutInflater mInflater;

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
                            Toast.makeText(getActivity(), "CLICKED ON brand" + targetUrl,
                                    Toast.LENGTH_LONG);
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
            // TODO Auto-generated method stub
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

            Bundle args = getArguments();
            LinearLayout view = (LinearLayout) rootView.findViewById(R.id.view_pager_element_frame);
            processResult(requestResponse.get(args.getInt(ARG_OBJECT)), view);
            // ((TextView)
            // rootView.findViewById(R.id.view_pager_element_frame)).setText(pagesTitles.get(0));
            return rootView;
        }

        private void processResult(Collection<? extends TeaserSpecification<?>> result,
                LinearLayout mainView) {

            Log.i(TAG, "teaserType processResult " + result.size());
            for (Iterator iterator = result.iterator(); iterator.hasNext();) {
                TeaserSpecification<?> teaserSpecification = (TeaserSpecification<?>) iterator
                        .next();
                Log.i(TAG, "teaserType : " + teaserSpecification.getType());
                switch (teaserSpecification.getType()) {
                case MAIN_ONE_SLIDE:
                    if (((ImageTeaserGroup) teaserSpecification).getTeasers().size() > 0) {
                        if (fragmentMainOneSlide == null) {
                            fragmentMainOneSlide = MainOneSlideFragment.getInstance();
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
                        }
                        View view = mInflater
                                .inflate(R.layout.main_one_fragment_frame, null, false);
                        mainView.addView(view);
                        fragmentManagerTransition(R.id.main_one_frame, fragmentMainOneSlide, false,
                                false);
                    }
                    break;
                case STATIC_BANNER:
                    if (fragmentStaticBanner == null) {
                        fragmentStaticBanner = StaticBannerFragment.getInstance();

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

                    }
                    View viewTeaser = mInflater.inflate(R.layout.static_teaser_frame, null, false);
                    mainView.addView(viewTeaser);
                    fragmentManagerTransition(R.id.static_teaser_frame, fragmentStaticBanner,
                            false, false);
                    break;
                case CATEGORIES:
                    if (fragmentCategoryTeaser == null) {
                        fragmentCategoryTeaser = CategoryTeaserFragment.getInstance();

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

                    }
                    View viewGeneric = mInflater.inflate(R.layout.generic_frame, null, false);
                    mainView.addView(viewGeneric);
                    fragmentManagerTransition(R.id.content_frame, fragmentCategoryTeaser, false,
                            false);
                    break;
                case PRODUCT_LIST:
                    if (fragmentProductListTeaser == null) {
                        fragmentProductListTeaser = ProducTeaserListFragment.getInstance();

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
                    }
                    View viewProductList = mInflater.inflate(R.layout.product_list_frame, null,
                            false);
                    mainView.addView(viewProductList);
                    fragmentManagerTransition(R.id.products_list_frame, fragmentProductListTeaser,
                            false, false);
                    break;
                case BRANDS_LIST:
                    if (fragmentBrandsListTeaser == null) {
                        fragmentBrandsListTeaser = BrandsTeaserListFragment.getInstance();

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
                    }
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
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
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
