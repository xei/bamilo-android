/**
 * 
 */
package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.mobile.components.infiniteviewpager.InfiniteCirclePageIndicator;
import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.GalleryPagerAdapter;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.LogTagHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.JumiaViewPagerWithZoom;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;
import com.mobile.view.ProductImageGalleryActivity;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * @author manuelsilva
 * @modified sergiopereria
 * 
 */
public class ProductImageGalleryFragment extends BaseFragment {

    public static final String TAG = LogTagHelper.create(ProductImageGalleryFragment.class);

    private JumiaViewPagerWithZoom mViewPager;

    private GalleryPagerAdapter galleryAdapter;

    public static int sSharedSelectedPosition = 0;

    private ArrayList<String> imagesList;

    private InfiniteCirclePageIndicator mViewPagerIndicator;
    
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
        ProductImageGalleryFragment fragment = isNested ? new ProductImageGalleryFragment(isNested) : new ProductImageGalleryFragment();
        // Save arguments
        fragment.setArguments(bundle);
        // Return instance
        return fragment;
    }

    /**
     * Construtor with arguments, called from {@link BaseActivity#onSwitchFragment(FragmentType, Bundle, Boolean)}.
     * @param bundle
     * @return ProductImageGalleryFragment 
     * @author sergiopereira
     */
    public static ProductImageGalleryFragment getInstance(Bundle bundle) {
        return getInstance(bundle, IS_NOT_NESTED_FRAGMENT);
    }
    
    /**
     * Constructor as nested fragment, called from {@link ProductDetailsFragment#}.
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
        super(EnumSet.of(MyMenuItem.HIDE_AB), NavigationAction.Product,R.layout.product_gallery_fragment, 0, KeyboardState.NO_ADJUST_CONTENT);
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
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = getArguments();
        if(arguments != null) {
            imagesList = arguments.getStringArrayList(ConstantsIntentExtra.IMAGE_LIST);
        }
        // Restore state after rotation
        if (savedInstanceState != null) {
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
        Print.i(TAG, "ON VIEW CREATED");
        mViewPager = (JumiaViewPagerWithZoom) view.findViewById(R.id.viewpager);
        mViewPagerIndicator = (InfiniteCirclePageIndicator) getView().findViewById(R.id.view_pager_indicator);
        // Set view pager
        createGallery();
        // Set close button
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
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
        Print.i(TAG, "ON SAVE INSTANCE");
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
        Print.i(TAG, "ON PAUSE");
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
        Print.i(TAG, "ON STOP");
    }


    /**
     * Set product image gallery
     */
    private void createGallery() {
        // Setted in order to show the no image placeholder on PDV view
        if (CollectionUtils.isEmpty(imagesList)) {
            imagesList = new ArrayList<>();
            imagesList.add("");
        }
        
        // Validate current adapter
        if (galleryAdapter != null) galleryAdapter.replaceAll(imagesList);
        else galleryAdapter = new GalleryPagerAdapter(getActivity(), imagesList, false);
        // Get size
        int size = imagesList.size();
        // Create infinite view pager using current gallery
        InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(galleryAdapter);
        infinitePagerAdapter.setOneItemMode();
        infinitePagerAdapter.enableInfinitePages(size > 1);
        // Add infinite adapter to pager 
        mViewPager.setAdapter(infinitePagerAdapter);
        // Add pager to indicator
        setIndicatorForViewPager(size);

        // 
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
    
    /**
     * Set the pager indicator validating the size.<br>
     * @param size
     * @author ricardo
     * @modified sergiopereira
     */
    private void setIndicatorForViewPager(int size) {
        // Validate the current size
        if(size > 1) {
            mViewPagerIndicator.setVisibility(View.VISIBLE);
        } else {
            mViewPagerIndicator.setVisibility(View.INVISIBLE);
        }
        mViewPagerIndicator.setViewPager(mViewPager);
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
            Print.i(TAG, "onShowPress");
//            mViewPager.setSelected(true);

            return false;
        }

        /**
         * show normal asset
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Print.i(TAG, "onScroll");
            // i'm only scrolling along the X axis
//            mViewPager.setSelected(false);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            launchProductGalleryActivity();
            return true;
        }
    }

    /**
     *
     */
    private void launchProductGalleryActivity(){
        ProductImageGalleryFragment.sSharedSelectedPosition = getViewPagerPosition();

        Intent intent = new Intent(getBaseActivity().getApplicationContext(), ProductImageGalleryActivity.class);
        intent.putExtra(ConstantsIntentExtra.IMAGE_LIST, imagesList);
        intent.putExtra(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, true);
        intent.putExtra(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, false);
        intent.putExtra(ConstantsIntentExtra.PRODUCT_GALLERY_POS, sSharedSelectedPosition);
        getBaseActivity().startActivity(intent);
        getBaseActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#notifyFragment(android.os.Bundle)
     */
    @Override
    public void notifyFragment(Bundle bundle) {
        Print.i(TAG, "NOTIFY FRAGMENT: " + sSharedSelectedPosition);
        // Validate if fragment is on the screen
        if (!isVisible()) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        // Validate the shared current position
        if (sSharedSelectedPosition <= 0) sSharedSelectedPosition = 0;
        // Get arguments 
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
        else Print.w(TAG, "WARNING: UNEXPECTED CLICK EVENT");
    }
    
    /**
     * Process the click on close button
     * @author sergiopereira
     */
    private void onClickCloseButton() {
        Print.i(TAG, "ON CLICK CLOSE BUTTON");
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
            Print.i(TAG, "WARNING: NPE ON GET CURRENT PAGER POSITION");
            return 0;
        } catch (ClassCastException e) {
            Print.i(TAG, "WARNING: CCE ON GET CURRENT PAGER POSITION");
            return 0;
        }
    }

}
