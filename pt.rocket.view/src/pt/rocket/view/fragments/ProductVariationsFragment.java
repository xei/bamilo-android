/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ProductImagesAdapter;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Variation;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.FragmentCommunicatorForProduct;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.view.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class ProductVariationsFragment extends BaseFragment implements OnItemClickListener {

    private static final String TAG = LogTagHelper.create(ProductVariationsFragment.class);

    private View mVariationsContainer;

    private CompleteProduct mCompleteProduct;
    
    private HorizontalListView mList;

    private int mVariationsListPosition = -1;
    
    private SharedPreferences sharedPreferences;

    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductVariationsFragment getInstance() {
        ProductVariationsFragment productVariationsFragment = new ProductVariationsFragment();
        return productVariationsFragment;
    }

    /**
     * Empty constructor
     */
    public ProductVariationsFragment() {
        super(IS_NESTED_FRAGMENT, BaseFragment.NO_INFLATE_LAYOUT);
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
        sharedPreferences = getActivity().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(mInflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return mInflater.inflate(R.layout.product_details_fragment_variations, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        mVariationsContainer = view.findViewById(R.id.variations_container);
        mList = (HorizontalListView) view.findViewById(R.id.variations_list);

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
        displayVariations();
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
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }

    /**
     * Show the variations
     * @author sergiopereira
     */
    private void displayVariations() {
        Log.i(TAG, "ON DISPLAY VARIATIONS");
        
        // Validate complete product
        CompleteProduct completeProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        if (completeProduct == null) {
            Log.i(TAG, "mCompleteProduct is null -- XXX verify and fix!!!");
            return;
        }
        
        // Save complete product
        mCompleteProduct = completeProduct;
        // Validate variations
        if (isNotValidVariation(mCompleteProduct.getVariations())) {
            if (mVariationsContainer != null) mVariationsContainer.setVisibility(View.GONE);
            return;
        }
        
        // Validate adapter
        if (mList.getAdapter() == null) {
            Log.i(TAG, "NEW ADAPTER");
            ProductImagesAdapter mAdapter = new ProductImagesAdapter(this.getActivity(), ProductImagesAdapter.createImageList(mCompleteProduct.getVariations()));
            mList.setAdapter(mAdapter);
        }
        // Set and force measure to set selected item
        mList.measure(0, 0);
        mList.setOnItemClickListener(this);
        mList.post(new Runnable() {
            @Override
            public void run() {
                //mList.setSelectedItem(0, HorizontalListView.MOVE_TO_DIRECTLY);
                //mList.setSelectedItem(findIndexOfSelectedVariation(), HorizontalListView.MOVE_TO_SCROLLED);
                mList.setSelectedItem(findIndexOfSelectedVariation(), HorizontalListView.MOVE_TO_DIRECTLY);
                //mList.refreshDrawableState();
                //mList.requestLayout();
            }
        });
            
        // Show container
        mVariationsContainer.setVisibility(View.VISIBLE);
        //Log.d(TAG, "displayVariations: list position = " + mVariationsListPosition);
        //mList.setPosition(mVariationsListPosition);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i(TAG, "ON ITEM CLICKED: " + position);

        if (mVariationsListPosition != position) {
            mVariationsListPosition = position;
            Editor eD = sharedPreferences.edit();
            eD.putInt(ProductDetailsFragment.VARIATION_LIST_POSITION, mVariationsListPosition);
            eD.commit();
            Bundle bundle = new Bundle();
            bundle.putInt(ProductDetailsFragment.LOADING_PRODUCT_KEY, position);
            bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
            mList.setSelectedItem(position, HorizontalListView.MOVE_TO_SCROLLED);
            FragmentCommunicatorForProduct.getInstance().notifyTarget(this, bundle, 0);
        }
    }

    private boolean isNotValidVariation(ArrayList<Variation> variations) {
        if (variations == null || variations.size() == 0) {
            return true;
        } else if (variations.size() == 1 && variations.get(0).getSKU().equals(mCompleteProduct.getSku())) {
            return true;
        } else {
            return false;
        }
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

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#notifyFragment(android.os.Bundle)
     */
    @Override
    public void notifyFragment(Bundle bundle) {
        Log.i(TAG, "ON NOTIFY FRAGMENT");
//      this.mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        // Validate if fragment is on the screen
        if (!isVisible()) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
//      if(bundle.containsKey(ConstantsIntentExtra.VARIATION_LISTPOSITION)){
//          mVariationsListPosition = bundle.getInt(ConstantsIntentExtra.VARIATION_LISTPOSITION);
//      }
//      Log.i(TAG, "on notifyFragment : " + mCompleteProduct != null ? "not null" : "null");
//      displayVariations();
//      if(mList != null) {
//          int indexOfSelectionVariation = findIndexOfSelectedVariation();
//          mList.setSelectedItem(indexOfSelectionVariation, HorizontalListView.MOVE_TO_SCROLLED);
//      }
    }

}
