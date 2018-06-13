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
import android.widget.RelativeLayout;

import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.components.viewpager.JumiaViewPagerWithZoom;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.GalleryPagerAdapter;
import com.mobile.service.objects.product.ImageUrls;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.output.Print;
import com.mobile.service.utils.shop.ShopSelector;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.utils.ui.UIUtils;
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

    private RelativeLayout mOutOfStockOverlay;
    
    private ArrayList<ImageUrls> mImageList;

    private HorizontalScrollView mHorizontalScrollView;

    private ViewGroup mThumbnailContainer;

    private boolean enabledInfiniteSlide;

    private boolean mIsOutOfStock;

    /**
     * Constructor as nested
     */
    @SuppressLint("ValidFragment")
    public ProductImageGalleryFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.product_gallery_fragment);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get arguments
        Bundle arguments = savedInstanceState != null ? savedInstanceState : getArguments();
        if(arguments != null) {
            enabledInfiniteSlide = arguments.getBoolean(ConstantsIntentExtra.INFINITE_SLIDE_SHOW, true);
            mImageList = arguments.getParcelableArrayList(ConstantsIntentExtra.IMAGE_LIST);
            mIsOutOfStock = arguments.getBoolean(ConstantsIntentExtra.OUT_OF_STOCK);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get pager
        mViewPager = view.findViewById(R.id.pdv_view_pager);
        // Get Overlay
        mOutOfStockOverlay = view.findViewById(R.id.pdv_image_oos_overlay);
        // HorizontalScrollView
        mHorizontalScrollView = view.findViewById(R.id.pdv_thumbnail_indicator_scroll);
        // Get thumbnail indicator
        mThumbnailContainer = view.findViewById(R.id.pdv_thumbnail_indicator_container);
        // Set view pager
        createGallery();
    }

    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        // Show default
        onUpdateThumbnailIndicator(OldProductDetailsFragment.sSharedSelectedPosition);
        mViewPager.setCurrentItem(OldProductDetailsFragment.sSharedSelectedPosition, true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE");
        outState.putParcelableArrayList(ConstantsIntentExtra.IMAGE_LIST, mImageList);
        outState.putBoolean(ConstantsIntentExtra.INFINITE_SLIDE_SHOW, enabledInfiniteSlide);
        outState.putBoolean(ConstantsIntentExtra.OUT_OF_STOCK, mIsOutOfStock);
    }

    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        OldProductDetailsFragment.sSharedSelectedPosition = getViewPagerPosition();
    }

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


    /**
     * Set product image gallery
     */
    private void createGallery() {
        // Validate stock
        mOutOfStockOverlay.setVisibility(mIsOutOfStock ? View.VISIBLE : View.GONE);
        // Setted in order to show the no image placeholder on PDV view
        if (CollectionUtils.isEmpty(mImageList)) {
            mImageList = new ArrayList<>();
            mImageList.add(new ImageUrls());
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
        // Add pager to ThumbnailIndicator
        setThumbnailIndicatorForViewPager(size);
        // Set listener
        setOnPageItemClicked();
        // Slide the horizontal scroll view to the end to show the first element
        if(ShopSelector.isRtl()){
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

    @Override
    public void onClick(View view) {
        // Get id
        int id = view.getId();
        // Case thumbnail indicator
        if (id == R.id.image_container) onClickThumbnailIndicator(view);
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

    private void setThumbnailIndicatorForViewPager(int size) {
        Print.i(TAG, "ON SHOW THUMBNAIL INDICATOR");
        // Validate the current size
        if (size > 1) {
            LayoutInflater inflater = (LayoutInflater) getBaseActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < mImageList.size(); i++) {
                View holder = inflater.inflate(R.layout.pdv_fragment_gallery_item, mThumbnailContainer, false);
                View loading = holder.findViewById(R.id.loading_progress);
                ImageView image = holder.findViewById(R.id.image);

                ImageManager.getInstance().loadImage(mImageList.get(i).getUrl(), image, loading, R.drawable.no_image_large, true);

                holder.setTag(R.id.target_position, i);
                holder.setOnClickListener(this);
                mThumbnailContainer.addView(holder);
            }
            mViewPager.addOnPageChangeListener(this);
        } else {
            UIUtils.setVisibility(mHorizontalScrollView, false);
        }
    }

    private int previous = -1;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Print.i(TAG, "ON PAGE SELECTED: " + position);
        // Update indicator
        onUpdateThumbnailIndicator(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
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
