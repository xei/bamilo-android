package pt.rocket.view.fragments;

import java.util.ArrayList;

import pt.rocket.controllers.RelatedItemsAdapter;
import pt.rocket.framework.database.RelatedItemsTableHelper;
import pt.rocket.framework.objects.LastViewed;
import pt.rocket.view.R;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show the related products 
 * @author sergiopereira
 *
 */
public class ProductRelatedItemsFragment extends BaseFragment {
    
    private final static String TAG = ProductRelatedItemsFragment.class.getName();
    
    /**
     * List of Related Items
     * We use exactly the same structure of the Last Viewed object,
     *  so there is no need to create a new object
     */
    private ArrayList<LastViewed> relatedItemsList;
    
    private ViewPager mViewPager;

    private String mCurrentSku;
    
    /**
     * Get new instance of this fragment
     * @param currentSku 
     * @return ProductRelatedItemsFragment
     */
    public static ProductRelatedItemsFragment getInstance(String currentSku) {
        ProductRelatedItemsFragment relatedItemsFragment = new ProductRelatedItemsFragment();
        relatedItemsFragment.mCurrentSku = (TextUtils.isEmpty(currentSku)) ? "" : currentSku;
        return relatedItemsFragment;
    }
    
    /**
     * Constructor
     */
    public ProductRelatedItemsFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.related_items_view);
        this.setRetainInstance(true);
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        getRelatedItems();
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }
   
    /**
     * Method used to get the related products
     */
    private void getRelatedItems(){
        Log.d(TAG, "ON GET RELATED ITEMS FOR: " + mCurrentSku);
        relatedItemsList = RelatedItemsTableHelper.getRelatedItemsList();
        if(relatedItemsList != null && relatedItemsList.size() > 0){
            for(int i = 0; i< relatedItemsList.size(); i++){
                String itemSku = relatedItemsList.get(i).getProductSku();
                if(!TextUtils.isEmpty(itemSku) && itemSku.equalsIgnoreCase(mCurrentSku)){
                    relatedItemsList.remove(i);
                    break;
                }
            }
            generateLastViewedLayout();
        }
    }
    
    /**
     * Method used to create the view
     */
    private void generateLastViewedLayout(){
        View mLoading = getView().findViewById(R.id.loading_related);
        mViewPager = (ViewPager) getView().findViewById(R.id.last_viewed_viewpager);
        RelatedItemsAdapter mRelatedItemsAdapter = new RelatedItemsAdapter(getBaseActivity(), relatedItemsList, LayoutInflater.from(getActivity()));
        mViewPager.setAdapter(mRelatedItemsAdapter);
        mViewPager.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
    }
        
}
