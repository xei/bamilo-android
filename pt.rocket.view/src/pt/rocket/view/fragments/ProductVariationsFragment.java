/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.controllers.ProductImagesAdapter;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Variation;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.FragmentCommunicatorForProduct;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
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
    private ProductImagesAdapter mAdapter;
    private int mVariationsListPosition = -1;
    private View mainView;

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
     * 
     * @param arrayList
     */
    public ProductVariationsFragment() {
        super(EnumSet.of(EventType.GET_PRODUCT_EVENT), 
                EnumSet.noneOf(EventType.class), 
                EnumSet.of(MyMenuItem.SHARE, MyMenuItem.SEARCH, MyMenuItem.SEARCH_BAR),
                NavigationAction.Products,
                R.string.product_details_title, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.setRetainInstance(true);
    }

    @Override
    public void sendListener(int identifier, OnClickListener onTeaserClickListener) {

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
        sharedPreferences = getActivity().getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup,
            Bundle savedInstanceState) {
        super.onCreateView(mInflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");

        mainView = mInflater.inflate(R.layout.variations_fragment, viewGroup, false);

        mVariationsContainer = mainView.findViewById(R.id.variations_container);

        return mainView;
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
        //
        // AnalyticsGoogle.get().trackPage(R.string.gteaser_prefix);
        //
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

    private void displayVariations() {
        mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        if (mCompleteProduct == null) {
            Log.i(TAG, "mCompleteProduct is null -- XXX verify and fix!!!");
            return;
        }

        if (isNotValidVariation(mCompleteProduct.getVariations())) {
            if (mVariationsContainer != null) {
                mVariationsContainer.setVisibility(View.GONE);
            }

            return;
        }
        
        mVariationsContainer.setVisibility(View.VISIBLE);
        mList = (HorizontalListView) mainView.findViewById(R.id.variations_list);
        if (mAdapter == null) {
            mAdapter = new ProductImagesAdapter(this.getActivity(),
                    ProductImagesAdapter.createImageList(mCompleteProduct.getVariations()));
            
        } else {
            Log.i(TAG, "replacing adapter");
            mAdapter.replaceAll(ProductImagesAdapter.createImageList(mCompleteProduct
                    .getVariations()));
        }
        
        mList.setAdapter(mAdapter);
        int indexOfSelectionVariation = findIndexOfSelectedVariation();

        mList.setOnItemClickListener(this);
        mList.setSelectedItem(indexOfSelectionVariation, HorizontalListView.MOVE_TO_DIRECTLY);
        Log.d(TAG, "displayVariations: list position = " + mVariationsListPosition);
        mList.setPosition(mVariationsListPosition);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (mVariationsListPosition != position) {
            mVariationsListPosition = position;
            Editor eD = sharedPreferences.edit();
            eD.putInt(ProductDetailsActivityFragment.VARIATION_LIST_POSITION, mVariationsListPosition);
            eD.commit();
            Bundle bundle = new Bundle();
            bundle.putInt(ProductDetailsActivityFragment.LOADING_PRODUCT_KEY, position);
            bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
            FragmentCommunicatorForProduct.getInstance().notifyTarget(this, bundle, 0);
            mList.setSelectedItem(position, HorizontalListView.MOVE_TO_DIRECTLY);
        }
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

    @Override
    public void notifyFragment(Bundle bundle) {

        this.mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();

        // Validate if fragment is on the screen
        if (!isVisible()) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        if(bundle.containsKey(ConstantsIntentExtra.VARIATION_LISTPOSITION)){
            mVariationsListPosition = bundle.getInt(ConstantsIntentExtra.VARIATION_LISTPOSITION);
        }
        Log.i(TAG, "on notifyFragment : " + mCompleteProduct != null ? "not null" : "null");
        displayVariations();
    }

}
