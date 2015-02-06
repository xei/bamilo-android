package com.mobile.view.fragments;
///**
// * 
// */
//package com.mobile.view.fragments;
//
//import java.util.ArrayList;
//
//import com.mobile.components.HorizontalListView;
//import com.mobile.components.HorizontalListView.OnSelectedItemListener;
//import com.mobile.constants.ConstantsIntentExtra;
//import com.mobile.constants.ConstantsSharedPrefs;
//import com.mobile.controllers.ProductVariationsListAdapter;
//import com.mobile.framework.objects.CompleteProduct;
//import com.mobile.framework.objects.Variation;
//import com.mobile.framework.utils.LogTagHelper;
//import com.mobile.utils.FragmentCommunicatorForProduct;
//import com.mobile.view.R;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import de.akquinet.android.androlog.Log;
//
///**
// * @author manuelsilva
// * 
// */
//public class ProductVariationsFragment extends BaseFragment implements OnSelectedItemListener {
//
//    private static final String TAG = LogTagHelper.create(ProductVariationsFragment.class);
//
//    private View mVariationsContainer;
//
//    private CompleteProduct mCompleteProduct;
//    
//    private HorizontalListView mList;
//
//    private int mVariationsListPosition = -1;
//    
//    private SharedPreferences sharedPreferences;
//
//    /**
//     * 
//     * @param dynamicForm
//     * @return
//     */
//    public static ProductVariationsFragment getInstance() {
//        ProductVariationsFragment productVariationsFragment = new ProductVariationsFragment();
//        return productVariationsFragment;
//    }
//
//    /**
//     * Empty constructor
//     */
//    public ProductVariationsFragment() {
//        super(IS_NESTED_FRAGMENT, BaseFragment.NO_INFLATE_LAYOUT);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i(TAG, "ON CREATE");
//        sharedPreferences = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
//     * android.view.ViewGroup, android.os.Bundle)
//     */
//    @Override
//    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
//        super.onCreateView(mInflater, viewGroup, savedInstanceState);
//        Log.i(TAG, "ON CREATE VIEW");
//        return mInflater.inflate(R.layout.product_details_fragment_variations, viewGroup, false);
//    }
//    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
//     */
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        Log.i(TAG, "ON VIEW CREATED");
//        // XXX
//        mVariationsContainer = view.findViewById(R.id.variations_container);
//        mList = (HorizontalListView) view.findViewById(R.id.variations_list);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onStart()
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.i(TAG, "ON START");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onResume()
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.i(TAG, "ON RESUME");
//        displayVariations();
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onPause()
//     */
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.i(TAG, "ON PAUSE");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onStop()
//     */
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.i(TAG, "ON STOP");
//    }
//    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
//     */
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        Log.i(TAG, "ON DESTROY VIEW");
//    }
//
//    /**
//     * Show the variations
//     * @author sergiopereira
//     */
//    private void displayVariations() {
//        Log.i(TAG, "ON DISPLAY VARIATIONS");
//        
//        // Validate complete product
//        CompleteProduct completeProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
//        if (completeProduct == null) {
//            Log.i(TAG, "mCompleteProduct is null -- XXX verify and fix!!!");
//            return;
//        }
//        
//        // Save complete product
//        mCompleteProduct = completeProduct;
//        // Validate variations
//        if (isNotValidVariation(mCompleteProduct.getVariations())) {
//            if (mVariationsContainer != null) mVariationsContainer.setVisibility(View.GONE);
//            return;
//        }
//        
//        // Validate adapter
//        if (mList.getAdapter() == null) {
//            Log.i(TAG, "NEW ADAPTER");
//            int position = findIndexOfSelectedVariation();
//            ProductVariationsListAdapter adapter = new ProductVariationsListAdapter(mCompleteProduct.getVariations());
//            mList.setHasFixedSize(true);
//            mList.setAdapter(adapter);
//            mList.setSelecetedItem(position);
//            mList.setOnItemSelectedListener(this);
//        }
//        
//        // ProductImagesAdapter.createImageList(mCompleteProduct.getVariations())
//        
////        // Set and force measure to set selected item
////        mList.measure(0, 0);
////        mList.setOnItemClickListener(this);
////        mList.post(new Runnable() {
////            @Override
////            public void run() {
////                //mList.setSelectedItem(0, HorizontalListView.MOVE_TO_DIRECTLY);
////                //mList.setSelectedItem(findIndexOfSelectedVariation(), HorizontalListView.MOVE_TO_SCROLLED);
////                mList.setSelectedItem(findIndexOfSelectedVariation(), HorizontalListView.MOVE_TO_DIRECTLY);
////                //mList.refreshDrawableState();
////                //mList.requestLayout();
////            }
////        });
//            
//        // Show container
//        mVariationsContainer.setVisibility(View.VISIBLE);
//    }
//    
//    @Override
//    public void onSelectedItem(View view, int position, String string) {
//        
//        Log.i(TAG, "ON SECLECTED ITEM: " + position);
//         
//        mVariationsListPosition = position;
//        Editor eD = sharedPreferences.edit();
//        eD.putInt(ProductDetailsFragment.VARIATION_LIST_POSITION, mVariationsListPosition);
//        eD.commit();
//        Bundle bundle = new Bundle();
//        bundle.putInt(ProductDetailsFragment.LOADING_PRODUCT_KEY, position);
//        bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
//        FragmentCommunicatorForProduct.getInstance().notifyTarget(this, bundle, 0);        
//    }
//
//    
////    /*
////     * (non-Javadoc)
////     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
////     */
////    @Override
////    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
////        Log.i(TAG, "ON ITEM CLICKED: " + position);
////
//////        if (mVariationsListPosition != position) {
//////            mVariationsListPosition = position;
//////            Editor eD = sharedPreferences.edit();
//////            eD.putInt(ProductDetailsFragment.VARIATION_LIST_POSITION, mVariationsListPosition);
//////            eD.commit();
//////            Bundle bundle = new Bundle();
//////            bundle.putInt(ProductDetailsFragment.LOADING_PRODUCT_KEY, position);
//////            bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
//////            mList.setSelectedItem(position, HorizontalListView.MOVE_TO_SCROLLED);
//////            FragmentCommunicatorForProduct.getInstance().notifyTarget(this, bundle, 0);
//////        }
////    }
//
//    private boolean isNotValidVariation(ArrayList<Variation> variations) {
//        if (variations == null || variations.size() == 0) {
//            return true;
//        } else if (variations.size() == 1 && variations.get(0).getSKU().equals(mCompleteProduct.getSku())) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private int findIndexOfSelectedVariation() {
//        ArrayList<Variation> var = mCompleteProduct.getVariations();
//        int idx;
//        for (idx = 0; idx < var.size(); idx++) {
//            if (var.get(idx).getSKU().equals(mCompleteProduct.getSku()))
//                return idx;
//        }
//
//        return -1;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.view.fragments.BaseFragment#notifyFragment(android.os.Bundle)
//     */
//    @Override
//    public void notifyFragment(Bundle bundle) {
//        Log.i(TAG, "ON NOTIFY FRAGMENT");
////      this.mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
//        // Validate if fragment is on the screen
//        if (!isVisible()) {
//            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
//            return;
//        }
////      if(bundle.containsKey(ConstantsIntentExtra.VARIATION_LISTPOSITION)){
////          mVariationsListPosition = bundle.getInt(ConstantsIntentExtra.VARIATION_LISTPOSITION);
////      }
////      Log.i(TAG, "on notifyFragment : " + mCompleteProduct != null ? "not null" : "null");
////      displayVariations();
////      if(mList != null) {
////          int indexOfSelectionVariation = findIndexOfSelectedVariation();
////          mList.setSelectedItem(indexOfSelectionVariation, HorizontalListView.MOVE_TO_SCROLLED);
////      }
//    }
//
//
//
//}
