//package pt.rocket.view;
//
//import java.util.ArrayList;
//import java.util.EnumSet;
//
//import pt.rocket.constants.ConstantsIntentExtra;
//import pt.rocket.constants.ConstantsSharedPrefs;
//import pt.rocket.framework.event.EventType;
//import pt.rocket.framework.event.ResponseEvent;
//import pt.rocket.framework.event.ResponseResultEvent;
//import pt.rocket.framework.event.events.GetProductEvent;
//import pt.rocket.framework.objects.CompleteProduct;
//import pt.rocket.framework.objects.Variation;
//import pt.rocket.framework.utils.LogTagHelper;
//import pt.rocket.utils.HorizontalListView;
//import pt.rocket.utils.MyMenuItem;
//import pt.rocket.utils.NavigationAction;
//import pt.rocket.utils.OnActivityFragmentInteraction;
//import pt.rocket.view.fragments.FragmentType;
//import pt.rocket.view.fragments.ProductImageGalleryFragment;
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Toast;
//import de.akquinet.android.androlog.Log;
///**
// * 
// * <p>
// * This class displays the product gallery screen
// * </p>
// * <p>
// * Its uses the HorizontalListView to display the product images and the variations for that product
// * </p>
// * 
// * <p>
// * Copyright (C) 2013 Smart Mobile Factory GmbH - All Rights Reserved
// * </p>
// * <p/>
// * <p>
// * Unauthorized copying of this file, via any medium is strictly prohibited
// * Proprietary and confidential.
// * </p>
// * 
// * @project WhiteLabelRocket
// * 
// * @version 1.00
// * 
// * @author Michael Kroez
// * @modified Manuel Silva
// * 
// * @date 4/1/2013
// * 
// * @description This class displays the product gallery screen
// * 
// */
//public class ProductsGalleryActivityFragment extends BaseActivity {
//    private final static String TAG = LogTagHelper.create(ProductsGalleryActivityFragment.class);
//
//    public static final String EXTRA_CURRENT_VARIANT = "pt.rocket.view.CURRENT_VARIANT";
//    public static final String EXTRA_CURRENT_LISTPOSITION = "pt.rocket.view.CURRENT_LISTPOSITION";
//
//    public final static int REQUEST_CODE_GALLERY = 1;
//
//    private String mCompleteProductUrl;
//    
//    private CompleteProduct mCompleteProduct;
//
//    private HorizontalListView mList;
//
//    private int LOADING_PRODUCT = -1;
//
//    
//    
////    private Fragment productVariationsFragment;
//    private Fragment productImagesViewPagerFragment;
//    
////    private OnActivityFragmentInteraction mCallbackProductVariationsFragment;
//    private OnActivityFragmentInteraction mCallbackProductImagesViewPagerFragment;
//    public ProductsGalleryActivityFragment() {
//        super(NavigationAction.Products, 
//                EnumSet.of(MyMenuItem.SHARE),
//                EnumSet.of(EventType.GET_PRODUCT_EVENT),
//                EnumSet.noneOf(EventType.class),
//                0, R.layout.productgallery_fragment);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        init(intent);
//    }    
//    
//    private void init(Intent intent) {
//        mCompleteProductUrl = intent.getStringExtra(ConstantsIntentExtra.CONTENT_URL);
//        //mVariationsListPosition = intent.getIntExtra(EXTRA_CURRENT_LISTPOSITION, 0);
//        loadProduct();
//    }    
//    
//    @Override
//    public void onFragmentElementSelected(int position){
//        LOADING_PRODUCT = position;
//        /**
//         * Send LOADING_PRODUCT to show loading views.
//         */
//        String url = "";
//        if(mCompleteProduct.getVariations().size()>LOADING_PRODUCT){
//            url = mCompleteProduct.getVariations().get(LOADING_PRODUCT).getLink();
//        } else {
//            return;
//        }
//            
//        if (TextUtils.isEmpty(url))
//            return;
//
//        if (url.equals(mCompleteProduct.getUrl()))
//            return;
//
//        Log.d(TAG, "onItemClick: loading url = " + url);
//        mCompleteProductUrl = url;
//        mCallbackProductImagesViewPagerFragment.sendPositionToFragment(-1);
//        loadProductPartial();
//    }
//    
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate");
//
//        final SharedPreferences sharedPrefs = this.getSharedPreferences(
//                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        
//        boolean showGalleryTips = sharedPrefs.getBoolean(ConstantsSharedPrefs.KEY_SHOW_GALLERY_TIPS, true);
//        if(showGalleryTips){
//           findViewById(R.id.tip_gallery).setVisibility(View.VISIBLE);
//           findViewById(R.id.tip_gallery).setOnClickListener(new OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                findViewById(R.id.tip_gallery).setVisibility(View.GONE);
//                Editor eD = sharedPrefs.edit();
//                eD.putBoolean(ConstantsSharedPrefs.KEY_SHOW_GALLERY_TIPS, false);
//                eD.commit();
//            }
//        });
//        }
//        
//        init(getIntent());
//    }
//    
//    @Override
//    protected void onDestroy() {
//        unbindDrawables(findViewById( R.id.gallery_container));
//        
//        mCompleteProductUrl = null;
//        
//        mCompleteProduct = null;
//
//        mList = null;
//
////        mCallbackProductVariationsFragment = null;
//        mCallbackProductImagesViewPagerFragment = null;
////        productVariationsFragment = null;
//        productImagesViewPagerFragment = null;
//        super.onDestroy();
//    }
//
//    private void loadProduct() {
//        triggerContentEvent(new GetProductEvent(mCompleteProductUrl));
//    }
//    
//    private void loadProductPartial() {
//        triggerContentEventWithNoLoading(new GetProductEvent(mCompleteProductUrl));
//    }
//
//    private void displayGallery(CompleteProduct product) {
//        mCompleteProduct = product;
//        setShareIntent(createShareIntent());
//        setTitle(mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
////        displayVariations();
//        displayImages();
//    }
//
//    private void displayVariations() {
//        if (isNotValidVariation(mCompleteProduct.getVariations())) {
//            return;
//        }
//        
////        if(productVariationsFragment == null){
////            productVariationsFragment = ProductVariationsFragment.getInstance();
////            startFragmentVariationCallbacks();
////            mCallbackProductVariationsFragment.sendValuesToFragment(0, mCompleteProduct);
////            mCallbackProductVariationsFragment.sendPositionToFragment(LOADING_PRODUCT);
////            fragmentManagerTransition(R.id.variations_container, productVariationsFragment, false, true);
////        } else {
////            mCallbackProductVariationsFragment.sendValuesToFragment(1, mCompleteProduct);
////        }
//    }
//
//    private boolean isNotValidVariation(ArrayList<Variation> variations) {
//        if (variations.size() == 0)
//            return true;
//        else if (variations.size() == 1
//                && variations.get(0).getSKU().equals(mCompleteProduct.getSku()))
//            return true;
//        else
//            return false;
//    }
//
//    private void displayImages() {
//        if(productImagesViewPagerFragment == null){
//            productImagesViewPagerFragment = ProductImageGalleryFragment.getInstance();
//            startFragmentGalleryCallbacks();
//            mCallbackProductImagesViewPagerFragment.sendValuesToFragment(3, true);
//            mCallbackProductImagesViewPagerFragment.sendValuesToFragment(0, mCompleteProduct);
//            mCallbackProductImagesViewPagerFragment.sendValuesToFragment(2, mCompleteProduct);
//            fragmentManagerTransition(R.id.image_gallery_container, productImagesViewPagerFragment, false, true);
//        } else {
//            mCallbackProductImagesViewPagerFragment.sendValuesToFragment(3, true);
//            mCallbackProductImagesViewPagerFragment.sendValuesToFragment(1, mCompleteProduct);
//            mCallbackProductImagesViewPagerFragment.sendValuesToFragment(2, mCompleteProduct);
//            
//        }
//    }
//     
////    private void startFragmentVariationCallbacks() {
////        // This makes sure that the container activity has implemented
////        // the callback interface. If not, it throws an exception
////        try {
////            mCallbackProductVariationsFragment = (OnActivityFragmentInteraction) productVariationsFragment;
////        } catch (ClassCastException e) {
////            throw new ClassCastException(productVariationsFragment.toString()
////                    + " must implement OnActivityFragmentInteraction");
////        }
////
////    }
//
//    private void startFragmentGalleryCallbacks() {
//        // This makes sure that the container activity has implemented
//        // the callback interface. If not, it throws an exception
//        try {
//            mCallbackProductImagesViewPagerFragment = (OnActivityFragmentInteraction) productImagesViewPagerFragment;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(productImagesViewPagerFragment.toString()
//                    + " must implement OnActivityFragmentInteraction");
//        }
//
//    }
//    
//    @Override
//    public void onBackPressed() {
//        if (mCompleteProduct != null) {
//            Intent intent = new Intent();
//            intent.putExtra(EXTRA_CURRENT_VARIANT, mCompleteProduct.getUrl());
//            int listPosition;
//            if (mList != null)
//                listPosition = mList.getPosition();
//            else
//                listPosition = 0;
//            intent.putExtra(EXTRA_CURRENT_LISTPOSITION, listPosition);
//            setResult(Activity.RESULT_OK, intent);
//        }
//        super.onBackPressed();
//    }
//
//    
//
//    /* (non-Javadoc)
//     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
//     */
//    @Override
//    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
//        displayGallery((CompleteProduct) event.result);
//        return true;
//    }
//    
//    /* (non-Javadoc)
//     * @see pt.rocket.utils.MyActivity#onErrorEvent(pt.rocket.framework.event.ResponseEvent)
//     */
//    @Override
//    protected boolean onErrorEvent(ResponseEvent event) {
//        if (!event.errorCode.isNetworkError()) {
//            Toast.makeText(this, getString(R.string.product_could_not_retrieved),
//                    Toast.LENGTH_LONG).show();
//            finish();
//            return true;
//        }
//        return super.onErrorEvent(event);
//    }
//
//    @Override
//    public void onSwitchFragment(FragmentType type, Boolean addToBackStack) {
//        // TODO Auto-generated method stub
//        
//    }
//
//}
