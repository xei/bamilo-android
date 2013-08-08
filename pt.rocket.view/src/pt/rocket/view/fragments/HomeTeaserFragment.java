/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.BrandsTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.ITargeting.TargetType;
import pt.rocket.framework.objects.ImageTeaserGroup;
import pt.rocket.framework.objects.ProductTeaserGroup;
import pt.rocket.framework.objects.TeaserSpecification;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.utils.OnActivityFragmentInteraction;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author manuelsilva
 */
public class HomeTeaserFragment extends BaseFragment {
    private final static String TAG = HomeTeaserFragment.class.getSimpleName();

    private LayoutInflater mInflater;
    private Fragment fragmentMainOneSlide;
    private Fragment fragmentStaticBanner;
    private Fragment fragmentCategoryTeaser;
    private Fragment fragmentProductListTeaser;
    private Fragment fragmentBrandsListTeaser;
    private OnActivityFragmentInteraction mCallback;
    private LinearLayout mainView;
    Collection<? extends TeaserSpecification<?>> fragmentContent;
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
                        Toast.makeText(getActivity(), "CLICKED ON brand"+targetUrl , Toast.LENGTH_LONG);
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

    /**
     * Get instance
     * 
     * @return
     */
    public static HomeTeaserFragment newInstance() {
        HomeTeaserFragment teasersFragment = new HomeTeaserFragment();
        return teasersFragment;
        
    } 
    
    /**
	 */
    public HomeTeaserFragment() {
        super(EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class));
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values){
        this.fragmentContent = (Collection<TeaserSpecification<?>>) values;
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
        
        mInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       
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
        Log.i(TAG, "ON CREATE VIEW");
        View view;
        view = inflater.inflate(R.layout.teasers_fragments_element, container, false);
        mainView = (LinearLayout) view.findViewById(R.id.teasers_container);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                processResult(fragmentContent);
            }
        }).run();
        
    }
    
    private void processResult(Collection<? extends TeaserSpecification<?>> result) {

        Log.i(TAG, "teaserType processResult "+result.size());
        for (Iterator iterator = result.iterator(); iterator.hasNext();) {
            TeaserSpecification<?> teaserSpecification = (TeaserSpecification<?>) iterator.next();
            Log.i(TAG, "teaserType : "+teaserSpecification.getType());
            switch (teaserSpecification.getType()) {
            case MAIN_ONE_SLIDE:
                if(((ImageTeaserGroup) teaserSpecification).getTeasers().size()>0){
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
                        mCallback.sendValuesToFragment(0, ((ImageTeaserGroup) teaserSpecification)
                                .getTeasers());
                    }
                    View view = mInflater.inflate(R.layout.main_one_fragment_frame, null, false);
                    mainView.addView(view);
                    fragmentManagerTransition(R.id.main_one_frame, fragmentMainOneSlide, false, false);
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
                fragmentManagerTransition(R.id.static_teaser_frame, fragmentStaticBanner, false, false);
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
                    mCallback.sendValuesToFragment(0, (CategoryTeaserGroup) teaserSpecification);

                }
                View viewGeneric = mInflater.inflate(R.layout.generic_frame, null, false);
                mainView.addView(viewGeneric);
                fragmentManagerTransition(R.id.content_frame, fragmentCategoryTeaser, false, false);
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
                View viewProductList = mInflater.inflate(R.layout.product_list_frame, null, false);
                mainView.addView(viewProductList);
                fragmentManagerTransition(R.id.products_list_frame, fragmentProductListTeaser, false, false);
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
                fragmentManagerTransition(R.id.brands_list_frame, fragmentBrandsListTeaser, false, false);
                break;
            }
        }
    }

    
    protected void fragmentManagerTransition(int container, Fragment fragment, Boolean addToBackStack, Boolean animated) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Animations
        if(animated)
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment);
        // BackStack
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        // Commit
        fragmentTransaction.commit();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
       
        return true;
    }

}
