package pt.rocket.view;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.GalleryPagerAdapter;
import pt.rocket.controllers.NormalizingViewPagerWrapper;
import pt.rocket.controllers.ProductImagesAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetProductEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Variation;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * 
 * <p>
 * This class displays the product gallery screen
 * </p>
 * <p>
 * Its uses the HorizontalListView to display the product images and the variations for that product
 * </p>
 * 
 * <p>
 * Copyright (C) 2013 Smart Mobile Factory GmbH - All Rights Reserved
 * </p>
 * <p/>
 * <p>
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and
 * confidential.
 * </p>
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.00
 * 
 * @author Michael Kroez
 * 
 * 
 * @date 4/1/2013
 * 
 * @description This class displays the product gallery screen
 * 
 */
public class ProductsGalleryActivityNew extends MyActivity implements
        OnItemClickListener, OnPageChangeListener {
    private final static String TAG = LogTagHelper.create(ProductsGalleryActivityNew.class);

    public static final String EXTRA_CURRENT_VARIANT = "pt.rocket.view.CURRENT_VARIANT";
    public static final String EXTRA_CURRENT_LISTPOSITION = "pt.rocket.view.CURRENT_LISTPOSITION";

    public final static int REQUEST_CODE_GALLERY = 1;

    private String mCompleteProductUrl;
    private Dialog mNoNetworkDialog;
    private CompleteProduct mCompleteProduct;
    private View mVariationsContainer;
    private ProductImagesAdapter mImageListAdapter;

    private ProductImagesAdapter mAdapter;

    private HorizontalListView mList;
    private int mVariationsListPosition;

    private int mVariationIndex;

    private ViewPager mViewPager;

    private HorizontalListView mImagesList;

    private NormalizingViewPagerWrapper mPagerWrapper;

    public ProductsGalleryActivityNew() {
        super(NavigationAction.Products,
                EnumSet.of(MyMenuItem.SHARE),
                EnumSet.of(EventType.GET_PRODUCT_EVENT),
                EnumSet.noneOf(EventType.class),
                0, R.layout.productgallerynew);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setAppContentLayout();
        init(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        init(intent);
    }

    private void init(Intent intent) {
        mCompleteProductUrl = intent.getStringExtra(ConstantsIntentExtra.CONTENT_URL);
        mVariationsListPosition = intent.getIntExtra(EXTRA_CURRENT_LISTPOSITION, 0);
        loadProduct();
    }

    @Override
    protected void onDestroy() {
        unbindDrawables(findViewById(R.id.gallery_container));
        super.onDestroy();
    }

    private void loadProduct() {
        triggerContentEvent(new GetProductEvent(mCompleteProductUrl));
    }

    /**
     * Set the Products layout using inflate
     */
    private void setAppContentLayout() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mVariationsContainer = findViewById(R.id.variations_container);
    }

    private void displayGallery(CompleteProduct product) {
        mCompleteProduct = product;
        setShareIntent(createShareIntent());
        setTitle(mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
        displayVariations();
        displayImages();
    }

    private void displayVariations() {
        if (isNotValidVariation(mCompleteProduct.getVariations())) {
            mVariationsContainer.setVisibility(View.GONE);
            return;
        }

        mList = (HorizontalListView) findViewById(R.id.variations_list);
        if (mAdapter == null) {
            mAdapter = new ProductImagesAdapter(this,
                    ProductImagesAdapter.createImageList(mCompleteProduct.getVariations()));
            mList.setAdapter(mAdapter);
        } else {
            mAdapter.replaceAll(ProductImagesAdapter.createImageList(mCompleteProduct
                    .getVariations()));
        }

        mList.setOnItemClickListener(this);
        mVariationIndex = findIndexOfSelectedVariation();

        Log.d(TAG, "displayVariations: selected variation index = " + mVariationIndex);
        mList.setSelectedItem(mVariationIndex, HorizontalListView.MOVE_TO_SCROLLED);
        Log.d(TAG, "displayVariations: mVariationsListPosition = " + mVariationsListPosition);
        mList.setPosition(mVariationsListPosition);
    }

    private boolean isNotValidVariation(ArrayList<Variation> variations) {
        if (variations.size() == 0)
            return true;
        else if (variations.size() == 1
                && variations.get(0).getSKU().equals(mCompleteProduct.getSku()))
            return true;
        else
            return false;
    }

    private int findIndexOfSelectedVariation() {
        ArrayList<Variation> var = mCompleteProduct.getVariations();
        int idx;
        for (idx = 0; idx < var.size(); idx++) {
            if (var.get(idx).getSKU().equals(mCompleteProduct.getSku()))
                return idx;
        }

        return -1;
    }

    private void displayImages() {
        // mImagesList = (HorzListView) findViewById(R.id.images_list);
        mImagesList = (HorizontalListView) findViewById(R.id.images_list);
        mImageListAdapter = new ProductImagesAdapter(this, mCompleteProduct.getImageList());
        mImagesList.setAdapter(mImageListAdapter);
        mImagesList.setOnItemClickListener(this);
        createViewPager();
        updateImage(0);
    }

    private void createViewPager() {
        ArrayList<String> imagesList = mCompleteProduct.getImageList();
        GalleryPagerAdapter adapter = new GalleryPagerAdapter(this, imagesList);
        mViewPager.setPageMargin((int) getResources().getDimension(R.dimen.margin_large));
        mViewPager.setAdapter(adapter);
        mPagerWrapper = new NormalizingViewPagerWrapper(this, mViewPager, adapter, this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        if (adapterView.getId() == R.id.variations_list) {
            Log.d(TAG, "onItemClick: showing variation with index " + position);
            String url = mCompleteProduct.getVariations().get(position).getLink();
            if (url.equals(mCompleteProduct.getUrl()))
                return;

            Log.d(TAG, "onItemClick: loading product url = " + url);
            mCompleteProductUrl = url;
            mVariationsListPosition = mList.getPosition();
            loadProduct();
        } else if (adapterView.getId() == R.id.images_list) {
            Log.d(TAG, "onItemClick: showing image with index " + position);
            mPagerWrapper.setCurrentItem(position, true);
        }
    }

    private void updateImage(int index) {
        Log.d(TAG, "updateImage index = " + index);
        mImagesList.setSelectedItem(index, HorizontalListView.MOVE_TO_SCROLLED);
        mPagerWrapper.setCurrentItem(index, true);
        Log.d(TAG,
                "updateImage: index = " + index + " currentItem = " + mViewPager.getCurrentItem());
    }

    @Override
    public void onBackPressed() {
        if (mCompleteProduct != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_CURRENT_VARIANT, mCompleteProduct.getUrl());
            int listPosition;
            if (mList != null)
                listPosition = mList.getPosition();
            else
                listPosition = 0;
            intent.putExtra(EXTRA_CURRENT_LISTPOSITION, listPosition);
            setResult(Activity.RESULT_OK, intent);
        }
        super.onBackPressed();
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

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        displayGallery((CompleteProduct) event.result);
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
            Toast.makeText(this, getString(R.string.product_could_not_retrieved),
                    Toast.LENGTH_LONG).show();
            finish();
            return true;
        }
        return super.onErrorEvent(event);
    }

}
