package com.mobile.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.mobile.components.infiniteviewpager.InfinitePagerAdapter;
import com.mobile.components.viewpager.JumiaViewPagerWithZoom;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.GalleryPagerAdapter;
import com.mobile.service.objects.product.ImageUrls;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.service.utils.output.Print;
import com.mobile.view.fragments.ProductDetailsFragment;

import java.util.ArrayList;

/**
 * Activity to show the the product images gallery.
 * Created by Paulo Carvalho on 4/13/15.
 */
public class ProductImageGalleryActivity extends FragmentActivity implements View.OnClickListener {

    private final static String TAG = ProductImageGalleryActivity.class.getSimpleName();

    private JumiaViewPagerWithZoom mViewPager;

    private GalleryPagerAdapter galleryAdapter;

    private ArrayList<ImageUrls> mImagesList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        setContentView(R.layout.product_gallery_full_activity);

        // control whether to allow the activity to rotate or not
        if(DeviceInfoHelper.isTabletDevice(getApplicationContext())){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Intent intent = getIntent();
        if(intent != null) {
            mImagesList = intent.getParcelableArrayListExtra(ConstantsIntentExtra.IMAGE_LIST);
        }
        // Restore state after rotation
        if (savedInstanceState != null) {
            mImagesList = savedInstanceState.getParcelableArrayList(ConstantsIntentExtra.IMAGE_LIST);
        }
        setContent();
    }

    /**
     * set activity content
     */
    private void setContent(){
        mViewPager = (JumiaViewPagerWithZoom) findViewById(R.id.pdv_view_pager);
        // Close button
        View closeView = findViewById(R.id.gallery_button_close);
        // Set view pager
        createGallery();
        // Set close button
        setCloseButton(closeView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        if(mViewPager != null) mViewPager.setCurrentItem(ProductDetailsFragment.sSharedSelectedPosition);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        ProductDetailsFragment.sSharedSelectedPosition = getViewPagerPosition();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /**
     * Set the close button
     * @modified sergiopereira
     */
    private void setCloseButton(View closeButton) {
        if (closeButton != null) {
            closeButton.setOnClickListener(this);
            closeButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set product image gallery
     */
    private void createGallery() {
        // Setted in order to show the no image placeholder on PDV view
        if (CollectionUtils.isEmpty(mImagesList)) {
            mImagesList = new ArrayList<>();
            mImagesList.add(new ImageUrls());
        }

        // Validate current adapter
        if (galleryAdapter != null) galleryAdapter.replaceAll(mImagesList);
        else galleryAdapter = new GalleryPagerAdapter(this, mImagesList, true);
        // Get size
        int size = mImagesList.size();
        // Create infinite view pager using current gallery
        InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(galleryAdapter);
        infinitePagerAdapter.setOneItemMode();
        infinitePagerAdapter.enableInfinitePages(size > 1);
        // Add infinite adapter to pager
        mViewPager.setAdapter(infinitePagerAdapter);
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
        outState.putParcelableArrayList(ConstantsIntentExtra.IMAGE_LIST, mImagesList);
    }

    @Override
    public void onClick(View v) {
        // Get id
        int id = v.getId();
        // Case close button
        if (id == R.id.gallery_button_close) onClickCloseButton();
        // Case unknown
        else Print.w(TAG, "WARNING: UNEXPECTED CLICK EVENT");
    }

    /**
     * Process the click on close button
     * @author sergiopereira
     */
    private void onClickCloseButton() {
        Print.i(TAG, "ON CLICK CLOSE BUTTON");
        ProductDetailsFragment.sSharedSelectedPosition = getViewPagerPosition();
        finish();
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
