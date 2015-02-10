/**
 * 
 */
package com.mobile.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import org.apache.commons.collections4.CollectionUtils;

import com.mobile.app.JumiaApplication;
import com.mobile.components.infiniteviewpager.InfiniteCirclePageIndicator;
import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.GalleryPagerAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.products.GetProductHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.utils.FragmentCommunicatorForProduct;
import com.mobile.utils.JumiaViewPagerWithZoom;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * @modified sergiopereria
 * 
 */
public class ProductImageGalleryFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ProductImageGalleryFragment.class);

    private static ProductImageGalleryFragment sProductImageGalleryFragment;

    private JumiaViewPagerWithZoom mViewPager;

    private GalleryPagerAdapter galleryAdapter;

    private RelativeLayout mProductImageLoading;

    private CompleteProduct mCompleteProduct;

    private View mainView;

    private boolean isZoomAvailable = false;

    private int currentPosition = 0;

    private ArrayList<String> imagesList;

    private String mCompleteProductUrl;

    private boolean errorLoadingImages = false;
    
    /**
     * Constructor using a nested flag
     * 
     * @param bundle
     * @param isNested
     * @return ProductImageGalleryFragment
     * @author sergiopereira
     */
    private static ProductImageGalleryFragment getInstance(Bundle bundle, boolean isNested) {
        // Validate if is nested or not
        sProductImageGalleryFragment = isNested ? new ProductImageGalleryFragment(isNested) : new ProductImageGalleryFragment();
        // Save arguments
        sProductImageGalleryFragment.currentPosition = bundle.getInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, 1);
        // if (sProductImageGalleryFragment.currentPosition <= 0) sProductImageGalleryFragment.currentPosition = 0;
        sProductImageGalleryFragment.isZoomAvailable = bundle.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
        String contentUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        sProductImageGalleryFragment.mCompleteProductUrl = contentUrl != null ? contentUrl : "";
        // Return instance
        return sProductImageGalleryFragment;
    }

    /**
     * Construtor with arguments, called from {@link BaseActivity#onSwitchFragment(FragmentType, Bundle, Boolean)}.
     * @param bundle
     * @return ProductImageGalleryFragment 
     * @author sergiopereira
     */
    public static ProductImageGalleryFragment getInstance(Bundle bundle) {
        return getInstance(bundle, ISNT_NESTED_FRAGMENT);
    }
    
    /**
     * Constructor as nested fragment, called from {@link ProductDetailsFragment#displayProduct()}.
     * @param bundle
     * @return ProductImageGalleryFragment
     * @author sergiopereira
     */
    public static ProductImageGalleryFragment getInstanceAsNested(Bundle bundle) {
        return getInstance(bundle, IS_NESTED_FRAGMENT);
    }
    
    /**
     * Default constructor
     */
    public ProductImageGalleryFragment() {
        super(EnumSet.of(MyMenuItem.HIDE_AB, MyMenuItem.UP_BUTTON_BACK), NavigationAction.Products,R.layout.product_gallery_fragment, 0, KeyboardState.NO_ADJUST_CONTENT);
    }

    /**
     * Constuctor as nested
     * @param 
     */
    public ProductImageGalleryFragment(Boolean isNested) {
        super(IS_NESTED_FRAGMENT, R.layout.product_gallery_fragment);
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
        // Restore isZoomAvailable after rotation
        if (savedInstanceState != null) {
            isZoomAvailable = savedInstanceState.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        mainView = view;
        mProductImageLoading = (RelativeLayout) view.findViewById(R.id.loading_gallery);
        mViewPager = (JumiaViewPagerWithZoom) view.findViewById(R.id.viewpager);
        View closeView = view.findViewById(R.id.gallery_button_close);
        // Set close button
        setCloseButton(closeView);
        
        /* Necessary for adding virtual positions (older implementation)
        // Set indicators
        setIndicators();
        setPageListener();
        */
    }
    
//    @Deprecated
//    private void setPageListener(){
//        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
//
//            @Override
//            public void onPageSelected(int arg0) {
//                currentPosition = arg0;
//            }
//
//            @Override
//            public void onPageScrolled(int arg0, float arg1, int arg2) {
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int arg0) {
//                if (arg0 == ViewPager.SCROLL_STATE_SETTLING) {
//                    if (mViewPager != null)
//                        mViewPager.setPagingEnabled(false);
//                }
//                if (arg0 == ViewPager.SCROLL_STATE_IDLE) {   
//                    changePage();
//                }
//            }
//        });
//    }
//    
//    /** Older implementation of infinite view pager.
//     * 
//     */
//    @Deprecated
//    private void changePage() {
//        try {
//            getBaseActivity().runOnUiThread(new Runnable() {
//                public void run() {
//                    int pageCount = galleryAdapter.getCount();
//                    try {
//                        mViewPager.setPagingEnabled(true);
//                        // mViewPager.toggleJumiaScroller(true);
//
//                        if (currentPosition == 0) {
//                            // mViewPager.toggleJumiaScroller(false);
//                            mViewPager.setCurrentItem(pageCount - 2, false);
////                            view_pager_indicator.onPageSelected(pageCount - 2);
//                            //
//                        } else if (currentPosition == pageCount - 1) 
//                            // mViewPager.toggleJumiaScroller(false);
//                            mViewPager.setCurrentItem(1, false);
////                            view_pager_indicator.onPageSelected(1);
////                        } else 
////                            view_pager_indicator.onPageSelected(currentPosition);
//                    } catch (NullPointerException e) {
//                        Log.w(TAG, "WARNING NPE IN CHANGE PAGE");
//                    }
//                }
//            });
//        } catch (NullPointerException e) {
//            Log.w(TAG, "WARNING NPE ON CHANGE PAGE", e);
//        }
//    }
    
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
        Log.i(TAG, "ON RESUME");
        super.onResume();
        mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        if (mCompleteProduct == null) {
            if (JumiaApplication.mIsBound && !"".equals(mCompleteProductUrl)) {
                Bundle bundle = new Bundle();
                bundle.putString(GetProductHelper.PRODUCT_URL, mCompleteProductUrl);
                triggerContentEvent(new GetProductHelper(), bundle, responseCallback);
            } else {
                showFragmentRetry(this);
            }

        } else {
            createViewPager();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Persist isZoomAvailable
        outState.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, isZoomAvailable);
        super.onSaveInstanceState(outState);
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
        FragmentCommunicatorForProduct.getInstance().setCurrentImagePosition(getViewPagerPosition());
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
    
    /**
     * Set the close button
     * @modified sergiopereira
     */
    private void setCloseButton(View closeButton) {
        if (closeButton != null && isZoomAvailable) {
            closeButton.setOnClickListener(this);
            closeButton.setVisibility(View.VISIBLE);
        }
    }
    
    
    /**
     * Set the indicators
     
    private void setIndicators() {
        if(isZoomAvailable) {
            mIndicatorLeftView.setVisibility(View.VISIBLE);
            mIndicatorRightView.setVisibility(View.VISIBLE);
        }
    }*/
    

    private void createViewPager() {
        // Setted in order to show the no image placeholder on PDV view
        if (CollectionUtils.isEmpty(mCompleteProduct.getImageList())) {
            ArrayList<String> temp = new ArrayList<String>();
            temp.add("");
            mCompleteProduct.setImageList(temp);
        }
         
        // Clone image list - TODO Validate if this is necessary
        imagesList = new ArrayList<String>(mCompleteProduct.getImageList());
        
        if (galleryAdapter != null) {
            /* Necessary for adding virtual positions (older implementation)
            imagesList.add(0, imagesList.get(imagesList.size() - 1));
            imagesList.add(imagesList.get(1));*/
            galleryAdapter.replaceAll(imagesList);
        } else {
            /* Necessary for adding virtual positions (older implementation)
            imagesList.add(0, imagesList.get(imagesList.size() - 1));
            imagesList.add(imagesList.get(1));
             */
            galleryAdapter = new GalleryPagerAdapter(getActivity(), imagesList, isZoomAvailable);
        }

        if (mViewPager == null) {
            mViewPager = (JumiaViewPagerWithZoom) mainView.findViewById(R.id.viewpager);
            mViewPager.setPageMargin((int) getActivity().getResources().getDimension(R.dimen.margin_large));
        }
        
        
        if(imagesList.size() > 1){
            InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(galleryAdapter);
            infinitePagerAdapter.setOneItemMode();
            mViewPager.setAdapter(infinitePagerAdapter);
            setIndicatorForViewPager();
        } else {
            mViewPager.setAdapter(galleryAdapter);
        }

        mViewPager.setCurrentItem(FragmentCommunicatorForProduct.getInstance().getCurrentImagePosition());

        final GestureDetector tapGestureDetector = new GestureDetector(getActivity(), new TapGestureListener(mViewPager));
        mViewPager.setOnTouchListener(new OnTouchListener() {
            /**
             * Handle on touch and when user lifts finger from viewPager show normal asset
             */
            public boolean onTouch(View v, MotionEvent event) {
                if (tapGestureDetector.onTouchEvent(event)) {
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mViewPager.setSelected(false);
                }

                return false;
            }
        });
            
        hideImageLoading();

    }

    private void setIndicatorForViewPager() {
        InfiniteCirclePageIndicator view_pager_indicator = (InfiniteCirclePageIndicator)getView().findViewById(R.id.view_pager_indicator);
        if (isZoomAvailable) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view_pager_indicator.getLayoutParams();
            p.setMargins(0, 0, 0, (int) getView().getResources().getDimension(R.dimen.dimen_78px));
            view_pager_indicator.requestLayout();
        }
        view_pager_indicator.setViewPager(mViewPager);
    }

    private void showImageLoading() {
        mProductImageLoading.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
    }

    private void hideImageLoading() {
        mProductImageLoading.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
    }

//    private void updateImage(int index) {
//        if (mViewPager != null && mViewPager.getAdapter() != null && mViewPager.getAdapter().getCount() > 0) {
//            mViewPager.setCurrentItem(index, true);
//        }
//    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//        // mPagerWrapper.setCurrentItem(position, true);
//        // mViewPager.setCurrentItem(position, true);
//    }

    /**
     * GestureListener to respond to tap on viewPager<br>
     * Added setSelected(true) when viewPager is being pressed in order to trigger selector<br>
     * <br>
     * http://stackoverflow.com/questions/2089552/android-how-to-detect-when-a-scroll-has-ended/
     * 3818124#3818124
     * 
     * @modified Andre Lopes
     */
    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {
        View mViewPager;

        public TapGestureListener(View mViewPager) {
            this.mViewPager = mViewPager;
        }

        /**
         * show selected asset
         */
        @Override
        public boolean onDown(MotionEvent e) {
            Log.i(TAG, "onShowPress");
//            mViewPager.setSelected(true);

            return false;
        }

        /**
         * show normal asset
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "onScroll");
            // i'm only scrolling along the X axis
//            mViewPager.setSelected(false);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i(TAG, "onSingleTap");
            if(!errorLoadingImages){
                if (!isZoomAvailable) {
                    Log.i(TAG, "onSingleTapConfirmed");
                    FragmentCommunicatorForProduct.getInstance().setCurrentImagePosition(getViewPagerPosition());
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
                    // bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, getViewPagerPosition());
                    bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, true);
                    bundle.putBoolean(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, false);
                    getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_GALLERY, bundle, FragmentController.ADD_TO_BACK_STACK);
                } else {
                    getBaseActivity().onBackPressed();
                }
            }
            return true;
        }
    }

//    @Override
//    public void onPageScrollStateChanged(int position) {
//        // noop
//    }
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        // noop
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        Log.d(TAG, "onPageSelected position = " + position);
//        // mImagesList.setSelectedItem(position, HorizontalListView.MOVE_TO_SCROLLED);
//        // update current selected viewpager position
//        currentPosition = position;
//    }

    @Override
    public void notifyFragment(Bundle bundle) {
        Log.i(TAG, "NOTIFY FRAGMENT");
        // Validate if fragment is on the screen
        if (!isVisible()) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        if (bundle.containsKey(ProductDetailsFragment.LOADING_PRODUCT)) {
            showImageLoading();
            return;
        }

        isZoomAvailable = bundle.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);

        mCompleteProduct = (CompleteProduct) FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        
        if (mCompleteProduct == null) {
            Log.e(TAG, "NO COMPLETE PRODUCT - SWITCHING TO HOME");
            restartAllFragments();
            return;
        }
        
        Log.i(TAG, "UPDATE GALLERY FOR PRODUCT: " + mCompleteProduct.getName());

        createViewPager();

        if (currentPosition <= 0) currentPosition = 0;
        
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // Get id
        int id = v.getId();
        // Case retry
        if (id == R.id.fragment_root_retry_button) {
            Log.d(TAG,"RETRY");
            getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_GALLERY, getArguments(), FragmentController.ADD_TO_BACK_STACK);
        }
        // Case close button
        else if (id == R.id.gallery_button_close) onClickCloseButton();
        // Unknown
        else Log.w(TAG, "WARNING: UNEXPECTED CLICK EVENT");
    }
    
    /**
     * Process the click on close button
     * @author sergiopereira
     */
    private void onClickCloseButton() {
        Log.i(TAG, "ON CLICK CLOSE BUTTON");
        FragmentCommunicatorForProduct.getInstance().setCurrentImagePosition(getViewPagerPosition());
        getBaseActivity().onBackPressed();
    }

    
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

    public void onSuccessEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        super.handleSuccessEvent(bundle);

        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case GET_PRODUCT_EVENT:
            if (((CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY)).getName() == null) {
                Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();
                getActivity().onBackPressed();
                return;
            } else {
                mCompleteProduct = (CompleteProduct) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
                createViewPager();
                // Waiting for the fragment comunication
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFragmentContentContainer();
                    }
                }, 300);
            }

            break;
        default:
            break;
        }
    }

    public void onErrorEvent(Bundle bundle) {

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (super.handleErrorEvent(bundle)) {
            return;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {

        case GET_PRODUCT_EVENT:
            if (!errorCode.isNetworkError()) {
                Toast.makeText(getBaseActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_LONG).show();

                showFragmentContentContainer();

                try {
                    getBaseActivity().onBackPressed();
                } catch (IllegalStateException e) {
                    getBaseActivity().popBackStackUntilTag(FragmentType.HOME.toString());
                }
                return;
            }
        default:
            break;
        }
    }

    private int getViewPagerPosition() {
        return mViewPager.getAdapter() instanceof InfinitePagerAdapter ? ((InfinitePagerAdapter) mViewPager
                .getAdapter()).getVirtualPosition(mViewPager.getCurrentItem()) : mViewPager.getCurrentItem();

    }

}
