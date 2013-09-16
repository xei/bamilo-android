/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.GalleryPagerAdapter;
import pt.rocket.controllers.NormalizingViewPagerWrapper;
import pt.rocket.controllers.ProductImagesAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.utils.JumiaViewPager;
import pt.rocket.utils.OnFragmentActivityInteraction;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class ProductImageGalleryFragment extends BaseFragment implements OnItemClickListener,
        OnPageChangeListener {

    private static final String TAG = LogTagHelper.create(ProductImageGalleryFragment.class);

    private ProductDetailsActivityFragment parentActivity;

    private HorizontalListView mImagesList;

    private ProductImagesAdapter mImageListAdapter;

    private NormalizingViewPagerWrapper mPagerWrapper;

    private ViewPager mViewPager;

    private GalleryPagerAdapter galleryAdapter;

    private RelativeLayout mProductImageLoading;

    private CompleteProduct mCompleteProduct;

    private View mainView;

    private OnFragmentActivityInteraction mCallback;

    private boolean showHorizontalListView = true;

    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductImageGalleryFragment getInstance() {
        ProductImageGalleryFragment productImageShowOffFragment = new ProductImageGalleryFragment();
        return productImageShowOffFragment;
    }

    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public ProductImageGalleryFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
        this.mCompleteProduct = (CompleteProduct) values;
        if (identifier == 1) {
            updateAdapter();
        } else if (identifier == 2) {
            showHorizontalListView = false;
        }
    }

    @Override
    public void sendPositionToFragment(int position) {

        /**
         * if still loading the product info, show image loading.
         */
        if (position < 0) {
            showImageLoading();
        }
    }

    @Override
    public void sendListener(int identifier, OnClickListener onTeaserClickListener) {

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
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentActivityInteraction) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnActivityFragmentInteraction");
        }
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup,
            Bundle savedInstanceState) {
        super.onCreateView(mInflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");

        mainView = mInflater.inflate(R.layout.product_showoff_viewpager_frame, viewGroup, false);
        
        return mainView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
        // FlurryTracker.get().begin();
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
        mProductImageLoading = (RelativeLayout) mainView.findViewById(R.id.loading_gallery);
        mImagesList = (HorizontalListView) mainView.findViewById(R.id.images_list);
        mImageListAdapter = new ProductImagesAdapter(getActivity(), mCompleteProduct.getImageList());
        mImagesList.setAdapter(mImageListAdapter);
        mImagesList.setOnItemClickListener(this);
        if (!showHorizontalListView) {
            mImagesList.setVisibility(View.GONE);
        }
        
        
        mViewPager = (ViewPager) mainView.findViewById(R.id.viewpager);
        createViewPager();
        updateImage(0);
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
        // FlurryTracker.get().end();
    }

    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        return true;
    }

    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        return false;
    }

    private void createViewPager() {
        ArrayList<String> imagesList = mCompleteProduct.getImageList();
        galleryAdapter = new GalleryPagerAdapter(getActivity(), imagesList);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.margin_large));
        mViewPager.setAdapter(galleryAdapter);
        mPagerWrapper = new NormalizingViewPagerWrapper(getActivity(), mViewPager, galleryAdapter,
                this);
        
        final GestureDetector tapGestureDetector = new GestureDetector(getActivity(), new TapGestureListener());
        mViewPager.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
    });

    }

    private void updateAdapter() {
        mImageListAdapter.replaceAll(mCompleteProduct.getImageList());
        createViewPager();
        hideImageLoading();
    }

    private void showImageLoading() {
        mProductImageLoading.setVisibility(View.VISIBLE);
        mImagesList.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
    }

    private void hideImageLoading() {
        mProductImageLoading.setVisibility(View.GONE);
        if(showHorizontalListView){
            mImagesList.setVisibility(View.VISIBLE);
        }
        mViewPager.setVisibility(View.VISIBLE);
    }

    private void updateImage(int index) {
        Log.d(TAG, "updateImage index = " + index);
        mImagesList.setSelectedItem(index, HorizontalListView.MOVE_TO_SCROLLED);
        mPagerWrapper.setCurrentItem(index, true);
        Log.d(TAG,
                "updateImage: index = " + index + " currentItem = " + mViewPager.getCurrentItem());
    }

    OnClickListener clickToGallery = new OnClickListener() {

        @Override
        public void onClick(View v) {
            ActivitiesWorkFlow.productsGalleryActivity(getActivity(), mCompleteProduct.getUrl(),
                    mViewPager.getCurrentItem());

        }
    };

    OnClickListener clickToFinish = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getActivity().finish();

        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        mPagerWrapper.setCurrentItem(position, true);

    }

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            
            if(showHorizontalListView){
                if(getActivity()!=null){
                    getActivity().finish();
                }
            } else {
                ActivitiesWorkFlow.productsGalleryActivity(getActivity(), mCompleteProduct.getUrl(),
                        mPagerWrapper.getCurrentItem());
            }
            
            return showHorizontalListView;
        }
    }

    @Override
    public void onPageScrollStateChanged(int position) {
        // noop
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // noop
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected position = " + position);
        mImagesList.setSelectedItem(position, HorizontalListView.MOVE_TO_SCROLLED);
    }

}
