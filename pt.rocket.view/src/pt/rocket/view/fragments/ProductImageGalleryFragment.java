/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.GalleryPagerAdapter;
import pt.rocket.controllers.NormalizingViewPagerWrapper;
import pt.rocket.controllers.ProductImagesAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.utils.JumiaViewPagerWithZoom;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnFragmentActivityInteraction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
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
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class ProductImageGalleryFragment extends BaseFragment implements OnItemClickListener,
        OnPageChangeListener {

    private static final String TAG = LogTagHelper.create(ProductImageGalleryFragment.class);

    private static ProductImageGalleryFragment productImageGalleryFragment;

    private ProductImagesAdapter mImageListAdapter;

    private NormalizingViewPagerWrapper mPagerWrapper;

    private JumiaViewPagerWithZoom mViewPager;

    private GalleryPagerAdapter galleryAdapter;

    private RelativeLayout mProductImageLoading;

    private CompleteProduct mCompleteProduct;
    
    private String mCompleteProductUrl;
    
    private View mainView;

    private boolean showHorizontalListView = true;

    private boolean isZoomAvailable = false;

    private int mVariationsListPosition;
    
    private HorizontalListView mImagesList;
    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductImageGalleryFragment getInstance() {
        productImageGalleryFragment = new ProductImageGalleryFragment();
        return productImageGalleryFragment;
    }

    
    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductImageGalleryFragment getInstance(Bundle bundle) {
        // Save
        if(productImageGalleryFragment == null) {
            productImageGalleryFragment = new ProductImageGalleryFragment();
        }
        // Save
        productImageGalleryFragment.mCompleteProductUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        productImageGalleryFragment.mVariationsListPosition = bundle.getInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, 0);
        productImageGalleryFragment.isZoomAvailable = bundle.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
        return productImageGalleryFragment;
    }
    
    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public ProductImageGalleryFragment() {
        super(EnumSet.of(EventType.GET_PRODUCT_EVENT), 
                EnumSet.noneOf(EventType.class),
                EnumSet.of(MyMenuItem.SHARE), 
                NavigationAction.Products, 0);
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
        // Retain this fragment across configuration changes.
        setRetainInstance(true);
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();

        
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

    private void createViewPager() {
        ArrayList<String> imagesList = mCompleteProduct.getImageList();
        galleryAdapter = new GalleryPagerAdapter(getActivity(), imagesList, isZoomAvailable);
        
        if (mViewPager == null) {
            mViewPager = (JumiaViewPagerWithZoom) mainView.findViewById(R.id.viewpager);
        }
        mViewPager.setPageMargin((int) getActivity().getResources().getDimension(
                R.dimen.margin_large));
        mViewPager.setAdapter(galleryAdapter);
        mPagerWrapper = new NormalizingViewPagerWrapper(getActivity(), mViewPager, galleryAdapter,
                this);

        if (!showHorizontalListView) {
            final GestureDetector tapGestureDetector = new GestureDetector(getActivity(),
                    new TapGestureListener());
            mViewPager.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    tapGestureDetector.onTouchEvent(event);
                    return false;
                }
            });
        }

    }

    private void updateAdapter() {
        mImageListAdapter.replaceAll(mCompleteProduct.getImageList());
        createViewPager();
        hideImageLoading();
    }

    private void showImageLoading() {
        mProductImageLoading.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
    }

    private void hideImageLoading() {
        mProductImageLoading.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
    }

    private void updateImage(int index) {
        mPagerWrapper.setCurrentItem(index, true);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        mPagerWrapper.setCurrentItem(position, true);

    }

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if(!isZoomAvailable){
                
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
                bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, mPagerWrapper.getCurrentItem());
                bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, true);
                bundle.putBoolean(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, false);
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_GALLERY, bundle, FragmentController.ADD_TO_BACK_STACK);
            } else {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

            return showHorizontalListView;
        }
    }

    @Override
    public boolean allowBackPressed() {
        Log.d(TAG, "ALLOW BACK PRESSED");
        if (mCompleteProduct != null) {
            
            int listPosition;
            if (mImagesList != null)
                listPosition = mImagesList.getPosition();
            else
                listPosition = 0;
            
            ProductDetailsFragment.updateVariantionListPosition(mCompleteProduct.getUrl(), listPosition);
        }
        
        return super.allowBackPressed();
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
//        mImagesList.setSelectedItem(position, HorizontalListView.MOVE_TO_SCROLLED);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        Log.d(TAG, "ON SUCCESS EVENT:" + event.getType().toString());
        
        // Validate if fragment is on the screen
        if(!isVisible()){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        mCompleteProduct = (CompleteProduct) event.result;
//        displayGallery(mCompleteProduct);
        if (mCompleteProduct == null) {
            getActivity().finish();
            return true;
        }
        Log.i(TAG, "ON RESUME");
        mProductImageLoading = (RelativeLayout) mainView.findViewById(R.id.loading_gallery);
        mImagesList = (HorizontalListView) mainView.findViewById(R.id.images_list);
        mImageListAdapter = new ProductImagesAdapter(getActivity(), mCompleteProduct.getImageList());
        mImagesList.setAdapter(mImageListAdapter);
        mImagesList.setOnItemClickListener(this);
        if (!showHorizontalListView) {
            mImagesList.setVisibility(View.GONE);
        }

        mViewPager = (JumiaViewPagerWithZoom) mainView.findViewById(R.id.viewpager);
        createViewPager();
        updateImage(0);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        if (!event.errorCode.isNetworkError()) {
            Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
            getActivity().onBackPressed();
            return true;
        }
        return super.onErrorEvent(event);
    }


    @Override
    public void notifyFragment(Bundle bundle) {
        Log.i(TAG, "code1 notifyFragment Gallery");
        
    }
}
