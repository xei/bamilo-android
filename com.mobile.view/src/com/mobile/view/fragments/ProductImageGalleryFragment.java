/**
 * 
 */
package com.mobile.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import org.apache.commons.collections4.CollectionUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.mobile.components.infiniteviewpager.InfiniteCirclePageIndicator;
import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.GalleryPagerAdapter;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.JumiaViewPagerWithZoom;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * @modified sergiopereria
 * 
 */
public class ProductImageGalleryFragment extends BaseFragment {

    public static final String TAG = LogTagHelper.create(ProductImageGalleryFragment.class);

    private static ProductImageGalleryFragment sProductImageGalleryFragment;

    private JumiaViewPagerWithZoom mViewPager;

    private GalleryPagerAdapter galleryAdapter;

    private boolean isZoomAvailable = false;

    public static int sSharedSelectedPosition = 0;

    private ArrayList<String> imagesList;

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
        sProductImageGalleryFragment.isZoomAvailable = bundle.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
        sProductImageGalleryFragment.imagesList = bundle.getStringArrayList(ConstantsIntentExtra.IMAGE_LIST);
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
        super(EnumSet.of(MyMenuItem.HIDE_AB), NavigationAction.Products,R.layout.product_gallery_fragment, 0, KeyboardState.NO_ADJUST_CONTENT);
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
        // Restore state after rotation
        if (savedInstanceState != null) {
            isZoomAvailable = savedInstanceState.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
            imagesList = savedInstanceState.getStringArrayList(ConstantsIntentExtra.IMAGE_LIST);
            isNestedFragment = savedInstanceState.getBoolean(ConstantsIntentExtra.IS_NESTED_FRAGMENT);
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
        mViewPager = (JumiaViewPagerWithZoom) view.findViewById(R.id.viewpager);
        View closeView = view.findViewById(R.id.gallery_button_close);
        // Set view pager
        createGallery();
        // Set close button
        setCloseButton(closeView);
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
        Log.i(TAG, "ON RESUME");
        // Set the current position
        if(mViewPager != null) mViewPager.setCurrentItem(ProductImageGalleryFragment.sSharedSelectedPosition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onSaveInstanceState()
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE");
        outState.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, isZoomAvailable);
        outState.putStringArrayList(ConstantsIntentExtra.IMAGE_LIST, imagesList);
        outState.putBoolean(ConstantsIntentExtra.IS_NESTED_FRAGMENT, isNestedFragment);
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
        ProductImageGalleryFragment.sSharedSelectedPosition = getViewPagerPosition();
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
     * Set product image gallery
     */
    private void createGallery() {
        // Setted in order to show the no image placeholder on PDV view
        if (CollectionUtils.isEmpty(imagesList)) {
            imagesList = new ArrayList<String>();
            imagesList.add("");
        }
        
        if (galleryAdapter != null) {
            galleryAdapter.replaceAll(imagesList);
        } else {
            galleryAdapter = new GalleryPagerAdapter(getActivity(), imagesList, isZoomAvailable);
        }

        if(imagesList.size() > 1){
            InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(galleryAdapter);
            infinitePagerAdapter.setOneItemMode();
            mViewPager.setAdapter(infinitePagerAdapter);
            setIndicatorForViewPager();
        } else {
            mViewPager.setAdapter(galleryAdapter);
        }

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

    }

    private void setIndicatorForViewPager() {
        InfiniteCirclePageIndicator viewPagerIndicator = (InfiniteCirclePageIndicator)getView().findViewById(R.id.view_pager_indicator);
        if (isZoomAvailable) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) viewPagerIndicator.getLayoutParams();
            p.setMargins(0, 0, 0, (int) getView().getResources().getDimension(R.dimen.dimen_78px));
            viewPagerIndicator.requestLayout();
        }
        viewPagerIndicator.setViewPager(mViewPager);
    }

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
            if(!errorLoadingImages){
                if (!isZoomAvailable) {
                    Log.i(TAG, "onSingleTapConfirmed");
                    ProductImageGalleryFragment.sSharedSelectedPosition = getViewPagerPosition();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList(ConstantsIntentExtra.IMAGE_LIST, imagesList);
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

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#notifyFragment(android.os.Bundle)
     */
    @Override
    public void notifyFragment(Bundle bundle) {
        Log.i(TAG, "NOTIFY FRAGMENT: " + sSharedSelectedPosition);
        // Validate if fragment is on the screen
        if (!isVisible()) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Validate the shared current position
        if (sSharedSelectedPosition <= 0) sSharedSelectedPosition = 0;
        // Get arguments 
        isZoomAvailable = bundle.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
        imagesList = bundle.getStringArrayList(ConstantsIntentExtra.IMAGE_LIST);
        // Create view pager
        createGallery();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // Get id
        int id = v.getId();
        // Case close button
        if (id == R.id.gallery_button_close) onClickCloseButton();
        // Unknown
        else Log.w(TAG, "WARNING: UNEXPECTED CLICK EVENT");
    }
    
    /**
     * Process the click on close button
     * @author sergiopereira
     */
    private void onClickCloseButton() {
        Log.i(TAG, "ON CLICK CLOSE BUTTON");
        ProductImageGalleryFragment.sSharedSelectedPosition = getViewPagerPosition();
        getBaseActivity().onBackPressed();
    }

    /**
     * Get the current view pager position
     * @return int - the current pager position or 0 case exception
     * @modified sergiopereira
     */
    private int getViewPagerPosition() {
        try {
            return mViewPager.getAdapter() instanceof InfinitePagerAdapter 
                    ? ((InfinitePagerAdapter) mViewPager.getAdapter()).getVirtualPosition(mViewPager.getCurrentItem()) 
                    : mViewPager.getCurrentItem();            
        } catch (NullPointerException e) {
            Log.i(TAG, "WARNING: NPE ON GET CURRENT PAGER POSITION");
            return 0;
        } catch (ClassCastException e) {
            Log.i(TAG, "WARNING: CCE ON GET CURRENT PAGER POSITION");
            return 0;
        }
    }

}
