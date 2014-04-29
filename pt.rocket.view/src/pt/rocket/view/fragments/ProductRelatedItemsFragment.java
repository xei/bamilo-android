package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import de.akquinet.android.androlog.Log;
import pt.rocket.app.JumiaApplication;
import pt.rocket.controllers.LastViewedAdapter;
import pt.rocket.controllers.RelatedItemsAdapter;
import pt.rocket.framework.database.RelatedItemsTableHelper;
import pt.rocket.framework.objects.LastViewed;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.helpers.GetProductsHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class ProductRelatedItemsFragment extends BaseFragment {
    private final static String TAG = ProductRelatedItemsFragment.class.getName();
    
    private final int PAGE_NUMBER = 0;
    private final int MAX_PAGE_ITEMS = 20;
    
    /**
     * List of Related Items
     * We use exactly the same structure of the Last Viewed object,
     *  so there is no need to create a new object
     */
    private ArrayList<LastViewed> relatedItemsList;
    
    private ViewPager mViewPager;
    
    public static ProductRelatedItemsFragment getInstance() {
        ProductRelatedItemsFragment relatedItemsFragment = new ProductRelatedItemsFragment();
        return relatedItemsFragment;
    }
    
    public ProductRelatedItemsFragment() {
        super(true);
        this.setRetainInstance(true);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        getRelatedItems();
    }
   
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.related_items_view, null, false);
    }
    
    @Override
    public void onPause() {
        super.onPause();
    }
   
    
    private void getRelatedItems(){
        
        relatedItemsList = RelatedItemsTableHelper.getRelatedItemsList();
        if(relatedItemsList != null && relatedItemsList.size() > 0){
            for(int i = 0; i< relatedItemsList.size(); i++){
                if(relatedItemsList.get(i).getProductSku().equalsIgnoreCase(JumiaApplication.INSTANCE.getCurrentProduct().getSku())){
                    relatedItemsList.remove(i);
                    break;
                }
            }
            generateLastViewedLayout();
        }
    }
    
    private void generateLastViewedLayout(){
        View mLoading = getView().findViewById(R.id.loading_related);
        mViewPager = (ViewPager) getView().findViewById(R.id.last_viewed_viewpager);
        RelatedItemsAdapter mRelatedItemsAdapter = new RelatedItemsAdapter(getBaseActivity(), relatedItemsList, LayoutInflater.from(getActivity()));
        mViewPager.setAdapter(mRelatedItemsAdapter);
        mViewPager.setVisibility(View.VISIBLE);
        mLoading.setVisibility(View.GONE);
    }
        
}
