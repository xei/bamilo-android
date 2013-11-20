///**
// * 
// */
//package pt.rocket.view;
//
//import java.util.Collection;
//import java.util.EnumSet;
//import java.util.Iterator;
//
//import pt.rocket.controllers.ActivitiesWorkFlow;
//import pt.rocket.framework.event.EventType;
//import pt.rocket.framework.event.RequestEvent;
//import pt.rocket.framework.event.ResponseResultEvent;
//import pt.rocket.framework.objects.CategoryTeaserGroup;
//import pt.rocket.framework.objects.ITargeting.TargetType;
//import pt.rocket.framework.objects.ImageTeaserGroup;
//import pt.rocket.framework.objects.ProductTeaserGroup;
//import pt.rocket.framework.objects.TeaserSpecification;
//import pt.rocket.framework.utils.AnalyticsGoogle;
//import pt.rocket.utils.CheckVersion;
//import pt.rocket.utils.HockeyStartup;
//import pt.rocket.utils.MyMenuItem;
//import pt.rocket.utils.NavigationAction;
//import pt.rocket.utils.OnActivityFragmentInteraction;
//import pt.rocket.view.fragments.CategoryTeaserFragment;
//import pt.rocket.view.fragments.FragmentType;
//import pt.rocket.view.fragments.MainOneSlideFragment;
//import pt.rocket.view.fragments.ProducTeaserListFragment;
//import pt.rocket.view.fragments.StaticBannerFragment;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.Toast;
//import de.akquinet.android.androlog.Log;
//
///**
// * @author nutzer2
// * @modified manuelsilva
// */
//public class TeaserFragmentActivity extends BaseActivity {
//    private final static String TAG = TeaserFragmentActivity.class.getSimpleName();
//
//    private LinearLayout container;
//    private LayoutInflater mInflater;
//    private Fragment fragmentMainOneSlide;
//    private Fragment fragmentStaticBanner;
//    private Fragment fragmentCategoryTeaser;
//    private Fragment fragmentProductListTeaser;
//    private Fragment fragmentBrandsListTeaser;
//    private OnActivityFragmentInteraction mCallback;
//
//    private OnClickListener teaserClickListener = new OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            String targetUrl = (String) v.getTag(R.id.target_url);
//            TargetType targetType = (TargetType) v.getTag(R.id.target_type);
//            String targetTitle = (String) v.getTag(R.id.target_title);
//            if (targetType != null) {
//                Log.d(TAG, "targetType = " + targetType.name() + " targetUrl = " + targetUrl);
//                switch (targetType) {
//                case CATEGORY:
//                    ActivitiesWorkFlow
//                            .categoriesActivityNew(TeaserFragmentActivity.this, targetUrl);
//                    break;
//                case PRODUCT_LIST:
//                    if (targetUrl != null) {
//                        ActivitiesWorkFlow.productsActivity(TeaserFragmentActivity.this,
//                                targetUrl, targetTitle, null, R.string.gteaser_prefix,
//                                AnalyticsGoogle.prepareNavigationPath(targetUrl));
//                    }
//                    break;
//                case PRODUCT:
//                    if (targetUrl != null) {
//                        ActivitiesWorkFlow.productsDetailsActivity(
//                                TeaserFragmentActivity.this, targetUrl,
//                                R.string.gteaserprod_prefix, "");
//                    }
//                    break;
//                default:
//                    Toast.makeText(TeaserFragmentActivity.this,
//                            "The target for this teaser is not defined!",
//                            Toast.LENGTH_LONG).show();
//                    break;
//                }
//            }
//        }
//    };
//
//    /**
//	 */
//    public TeaserFragmentActivity() {
//        super(R.layout.search,
//                NavigationAction.Home,
//                EnumSet.noneOf(MyMenuItem.class),
//                EnumSet.of(EventType.GET_TEASERS_EVENT),
//                EnumSet.noneOf(EventType.class),
//                0, R.layout.teasers_fragments);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mInflater = (LayoutInflater) this
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        container = (LinearLayout) findViewById(R.id.teasers_container);
//
//        changeSearchBarBehavior();
//        triggerContentEvent(new RequestEvent(EventType.GET_TEASERS_EVENT));
//        HockeyStartup.register(this);
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (CheckVersion.needsToShowDialog()) {
//            CheckVersion.showDialog(this);
//        }
//
//        AnalyticsGoogle.get().trackPage(R.string.ghomepage);
//    }
//
//    private void changeSearchBarBehavior() {
//        final EditText autoComplete = (EditText) findViewById(R.id.search_component);
//        autoComplete.setEnabled(false);
//        autoComplete.setFocusable(false);
//        autoComplete.setFocusableInTouchMode(false);
//        findViewById(R.id.search_overlay).setOnClickListener(
//                new OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        ActivitiesWorkFlow.searchActivity(TeaserFragmentActivity.this);
//                    }
//                });
//    }
//
//    @Override
//    public void onBackPressed() {
//        finish();
//    }
//
//    private void processResult(Collection<? extends TeaserSpecification<?>> result) {
//
//        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
//            TeaserSpecification<?> teaserSpecification = (TeaserSpecification<?>) iterator.next();
//            switch (teaserSpecification.getType()) {
//            case MAIN_ONE_SLIDE:
//                if (fragmentMainOneSlide == null) {
//                    fragmentMainOneSlide = MainOneSlideFragment.getInstance();
//                    // This makes sure that the container activity has implemented
//                    // the callback interface. If not, it throws an exception
//                    try {
//                        mCallback = (OnActivityFragmentInteraction) fragmentMainOneSlide;
//                    } catch (ClassCastException e) {
//                        throw new ClassCastException(fragmentMainOneSlide.toString()
//                                + " must implement OnActivityFragmentInteraction");
//                    }
//
//                    mCallback.sendListener(0, teaserClickListener);
//                    mCallback.sendValuesToFragment(0, ((ImageTeaserGroup) teaserSpecification)
//                            .getTeasers());
//                }
//                View view = mInflater.inflate(R.layout.main_one_fragment_frame, null, false);
//                container.addView(view);
//                fragmentManagerTransition(R.id.main_one_frame, fragmentMainOneSlide, false, false);
//                break;
//            case STATIC_BANNER:
//                if (fragmentStaticBanner == null) {
//                    fragmentStaticBanner = StaticBannerFragment.getInstance();
//
//                    // This makes sure that the container activity has implemented
//                    // the callback interface. If not, it throws an exception
//                    try {
//                        mCallback = (OnActivityFragmentInteraction) fragmentStaticBanner;
//                    } catch (ClassCastException e) {
//                        throw new ClassCastException(fragmentStaticBanner.toString()
//                                + " must implement OnActivityFragmentInteraction");
//                    }
//
//                    mCallback.sendListener(0, teaserClickListener);
//                    mCallback.sendValuesToFragment(0, ((ImageTeaserGroup) teaserSpecification)
//                            .getTeasers());
//
//                }
//                View viewTeaser = mInflater.inflate(R.layout.static_teaser_frame, null, false);
//                container.addView(viewTeaser);
//                fragmentManagerTransition(R.id.static_teaser_frame, fragmentStaticBanner, false,
//                        false);
//                break;
//            case CATEGORIES:
//                if (fragmentCategoryTeaser == null) {
//                    fragmentCategoryTeaser = CategoryTeaserFragment.getInstance();
//
//                    // This makes sure that the container activity has implemented
//                    // the callback interface. If not, it throws an exception
//                    try {
//                        mCallback = (OnActivityFragmentInteraction) fragmentCategoryTeaser;
//                    } catch (ClassCastException e) {
//                        throw new ClassCastException(fragmentCategoryTeaser.toString()
//                                + " must implement OnActivityFragmentInteraction");
//                    }
//
//                    mCallback.sendListener(0, teaserClickListener);
//                    mCallback.sendValuesToFragment(0, (CategoryTeaserGroup) teaserSpecification);
//
//                }
//                View viewGeneric = mInflater.inflate(R.layout.generic_frame, null, false);
//                container.addView(viewGeneric);
//                fragmentManagerTransition(R.id.content_frame, fragmentCategoryTeaser, false, false);
//                break;
//            case PRODUCT_LIST:
//                if (fragmentProductListTeaser == null) {
//                    fragmentProductListTeaser = ProducTeaserListFragment.getInstance();
//
//                    // This makes sure that the container activity has implemented
//                    // the callback interface. If not, it throws an exception
//                    try {
//                        mCallback = (OnActivityFragmentInteraction) fragmentProductListTeaser;
//                    } catch (ClassCastException e) {
//                        throw new ClassCastException(fragmentProductListTeaser.toString()
//                                + " must implement OnActivityFragmentInteraction");
//                    }
//
//                    mCallback.sendListener(0, teaserClickListener);
//                    mCallback.sendValuesToFragment(0, (ProductTeaserGroup) teaserSpecification);
//                }
//                View viewProductList = mInflater.inflate(R.layout.product_list_frame, null, false);
//                container.addView(viewProductList);
//                fragmentManagerTransition(R.id.products_list_frame, fragmentProductListTeaser,
//                        false, false);
//                break;
//            case BRANDS_LIST:
//                if (fragmentBrandsListTeaser == null) {
//                    fragmentBrandsListTeaser = ProducTeaserListFragment.getInstance();
//
//                    // This makes sure that the container activity has implemented
//                    // the callback interface. If not, it throws an exception
//                    try {
//                        mCallback = (OnActivityFragmentInteraction) fragmentBrandsListTeaser;
//                    } catch (ClassCastException e) {
//                        throw new ClassCastException(fragmentBrandsListTeaser.toString()
//                                + " must implement OnActivityFragmentInteraction");
//                    }
//
//                    mCallback.sendListener(0, teaserClickListener);
//                    mCallback.sendValuesToFragment(0, (ProductTeaserGroup) teaserSpecification);
//                }
//                View viewBrandList = mInflater.inflate(R.layout.brands_list_frame, null, false);
//                container.addView(viewBrandList);
//                fragmentManagerTransition(R.id.brands_list_frame, fragmentBrandsListTeaser, false,
//                        false);
//                break;
//            }
//        }
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
//     */
//    @Override
//    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
//        Log.d(TAG, "Got teasers event: " + event);
//        // Get Teasers
//        processResult((Collection<? extends TeaserSpecification<?>>) event.result);
//        return true;
//    }
//
//    @Override
//    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
//        // TODO Auto-generated method stub
//
//    }
//
//}
