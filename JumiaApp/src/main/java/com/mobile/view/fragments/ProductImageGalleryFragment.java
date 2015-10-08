package com.mobile.view.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.components.viewpager.JumiaViewPagerWithZoom;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.GalleryPagerAdapter;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.ProductImageGalleryActivity;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * @author manuelsilva
 * @modified sergiopereria
 * 
 */
public class ProductImageGalleryFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    public static final String TAG = ProductImageGalleryFragment.class.getSimpleName();

    private JumiaViewPagerWithZoom mViewPager;

    private ArrayList<String> mImageList;

    private HorizontalScrollView mHorizontalScrollView;

    private ViewGroup mThumbnailContainer;

    private boolean enabledInfiniteSlide;

    /**
     * Constructor using a nested flag
     */
    private static ProductImageGalleryFragment getInstance(Bundle bundle, boolean isNested) {
        // Validate if is nested or not
        //ProductImageGalleryFragment fragment = isNested ? new ProductImageGalleryFragment(true) : new ProductImageGalleryFragment();
        ProductImageGalleryFragment fragment = new ProductImageGalleryFragment();
        // Save arguments
        fragment.setArguments(bundle);
        // Return instance
        return fragment;
    }

    /**
     * Constructor with arguments, called from {@link BaseActivity#onSwitchFragment(FragmentType, Bundle, Boolean)}.
     */
    public static ProductImageGalleryFragment getInstance(Bundle bundle) {
        return getInstance(bundle, IS_NOT_NESTED_FRAGMENT);
    }
    
    /**
     * Constructor as nested fragment, called from {@link ProductDetailsFragment#}.
     */
    public static ProductImageGalleryFragment getInstanceAsNested(Bundle bundle) {
        return getInstance(bundle, IS_NESTED_FRAGMENT);
    }
    
//    /**
//     * Default constructor
//     */
//    public ProductImageGalleryFragment() {
//        super(EnumSet.of(MyMenuItem.HIDE_AB), NavigationAction.Product, R.layout.product_gallery_fragment, 0, KeyboardState.NO_ADJUST_CONTENT);
//    }

    /**
     * Constructor as nested
     */
    @SuppressLint("ValidFragment")
    public ProductImageGalleryFragment() { //Boolean isNested) {
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
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if(arguments != null) {
            enabledInfiniteSlide = arguments.getBoolean(ConstantsIntentExtra.INFINITE_SLIDE_SHOW, true);
            mImageList = arguments.getStringArrayList(ConstantsIntentExtra.IMAGE_LIST);
            isNestedFragment = arguments.getBoolean(ConstantsIntentExtra.IS_NESTED_FRAGMENT);
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
        // Get pager
        mViewPager = (JumiaViewPagerWithZoom) view.findViewById(R.id.viewpager);
        // HorizontalScrollView
        mHorizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.pdv_thumbnail_indicator_scroll);
        // Get thumbnail indicator
        mThumbnailContainer = (ViewGroup) view.findViewById(R.id.pdv_thumbnail_indicator_container);
        Print.i(TAG,"mHorizontalScrollView:"+mHorizontalScrollView);
        // Set view pager
        createGallery();
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
        // Show default
        onUpdateThumbnailIndicator(ProductDetailsFragment.sSharedSelectedPosition);
        mViewPager.setCurrentItem(ProductDetailsFragment.sSharedSelectedPosition, true);
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
        outState.putStringArrayList(ConstantsIntentExtra.IMAGE_LIST, mImageList);
        outState.putBoolean(ConstantsIntentExtra.IS_NESTED_FRAGMENT, isNestedFragment);
        outState.putBoolean(ConstantsIntentExtra.INFINITE_SLIDE_SHOW, enabledInfiniteSlide);
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
        ProductDetailsFragment.sSharedSelectedPosition = getViewPagerPosition();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY VIEW");
        // Remove listener
        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /*
     * ############## LAYOUT ##############
     */

    /**
     * Set product image gallery
     */
    private void createGallery() {
        // Setted in order to show the no image placeholder on PDV view
        if (CollectionUtils.isEmpty(mImageList)) {
            mImageList = new ArrayList<>();
            mImageList.add("");
        }
        // Get size
        int size = mImageList.size();
        // Create infinite view pager using current gallery
        GalleryPagerAdapter galleryAdapter = new GalleryPagerAdapter(getActivity(), mImageList, false);
        InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(galleryAdapter);
        infinitePagerAdapter.setOneItemMode();
        infinitePagerAdapter.enableInfinitePages(size > 1 && enabledInfiniteSlide);
        // Add infinite adapter to pager 
        mViewPager.setAdapter(infinitePagerAdapter);
        // Add pager to indicator
        //setIndicatorForViewPager(size); // PageIndicator
        // Add pager to ThumbnailIndicator
        setThumbnailIndicatorForViewPager(size);
        // Set listener
        setOnPageItemClicked();

        if(ShopSelector.isRtl()){
            // slide the horizontal scroll view to the end to show the first element
            mHorizontalScrollView.postDelayed(new Runnable() {
                public void run() {
                    mHorizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                }
            }, 100L);
        }
    }

    /**
     * Set the click listener
     */
    private void setOnPageItemClicked() {
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

//    /**
//     * Set the pager indicator validating the size.<br>
//     * @param size
//     * @author ricardo
//     * @modified sergiopereira
//     */
//    private void setIndicatorForViewPager(int size) {
//        // Validate the current size
//        if(size > 1) {
//            mViewPagerIndicator.setVisibility(View.VISIBLE);
//        } else {
//            mViewPagerIndicator.setVisibility(View.INVISIBLE);
//        }
//        mViewPagerIndicator.setViewPager(mViewPager);
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

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
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
        Intent intent = new Intent(getBaseActivity().getApplicationContext(), ProductImageGalleryActivity.class);
        intent.putExtra(ConstantsIntentExtra.IMAGE_LIST, mImageList);
        intent.putExtra(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, true);
        intent.putExtra(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, false);
        getBaseActivity().startActivity(intent);
        getBaseActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /*
     * ######## LISTENER ########
     */

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case close button
        if (id == R.id.gallery_button_close) onClickCloseButton();
        // Case thumbnail indicator
        else if (id == R.id.image_container) onClickThumbnailIndicator(view);
        // Unknown
        else Print.w(TAG, "WARNING: UNEXPECTED CLICK EVENT");
    }

    /**
     * Process the click on close button
     * @author sergiopereira
     */
    private void onClickThumbnailIndicator(View view) {
        // Get real position
        int position = (int) view.getTag(R.id.target_position);
        Print.i(TAG, "ON CLICK INDICATOR POS: " + position);
        int n = ((InfinitePagerAdapter) mViewPager.getAdapter()).getVirtualPosition(position);
        mViewPager.setCurrentItem(n, true);
    }
    
    /**
     * Process the click on close button
     * @author sergiopereira
     */
    private void onClickCloseButton() {
        Print.i(TAG, "ON CLICK CLOSE BUTTON");
        //sSharedSelectedPosition = getViewPagerPosition();
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


    /*
     * ########### THUMBNAIL INDICATOR ###########
     */

    private void setThumbnailIndicatorForViewPager(int size) {
        Print.i(TAG, "ON SHOW THUMBNAIL INDICATOR");
        // Validate the current size
        if (size > 1) {
            LayoutInflater inflater = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < mImageList.size(); i++) {
                View holder = inflater.inflate(R.layout.pdv_fragment_gallery_item, mThumbnailContainer, false);
                View loading = holder.findViewById(R.id.loading_progress);
                ImageView im = (ImageView) holder.findViewById(R.id.image);
                RocketImageLoader.instance.loadImage(mImageList.get(i), im, loading, R.drawable.no_image_small);
                holder.setTag(R.id.target_position, i);
                holder.setOnClickListener(this);
                mThumbnailContainer.addView(holder);
            }
            mViewPager.addOnPageChangeListener(this);
        } else {
            UIUtils.setVisibility(mThumbnailContainer, false);
        }
    }

    private int previous = -1;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // ...
    }

    @Override
    public void onPageSelected(int position) {
        Print.i(TAG, "ON PAGE SELECTED: " + position);
        // Update indicator
        onUpdateThumbnailIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // ...
    }

    private void onUpdateThumbnailIndicator(int position) {
        // Unselected the previous item
        View old = mThumbnailContainer.getChildAt(previous);
        if (old != null) {
            old.setSelected(false);
        }
        // Scroll and select the new item
        View view = mThumbnailContainer.getChildAt(position);
        if (view != null) {
            // Get scroll parent
            HorizontalScrollView horizontalScrollView = (HorizontalScrollView) mThumbnailContainer.getParent();
            // Validate if view is visible
            Rect scrollBounds = new Rect();
            horizontalScrollView.getHitRect(scrollBounds);
            // Case imageView is not within or only partially within the visible window
            if (!view.getLocalVisibleRect(scrollBounds) || scrollBounds.width() < view.getWidth()) {
                // Case moving to the right
                if (position > previous) {
                    horizontalScrollView.smoothScrollTo(view.getRight(), 0);
                }
                // Case moving to the left
                else if (position < previous) {
                    horizontalScrollView.smoothScrollTo(view.getLeft(), 0);
                }
            }
            // Select and save position
            view.setSelected(true);
            previous = position;
        }
    }

}
