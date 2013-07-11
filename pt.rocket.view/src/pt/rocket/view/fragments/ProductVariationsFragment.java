/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.controllers.ProductImagesAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Variation;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.utils.OnFragmentActivityInteraction;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

    private ProductDetailsActivityFragment parentActivity;

    private View mVariationsContainer;

    private CompleteProduct mCompleteProduct;
    private HorizontalListView mList;
    private ProductImagesAdapter mAdapter;
    private int mVariationsListPosition = -1;
    private OnFragmentActivityInteraction mCallback;
    private View mainView;
    
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
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
        this.mCompleteProduct= (CompleteProduct) values;
        if(identifier == 1){
            displayVariations();
        }
    }

    @Override
    public void sendPositionToFragment(int position){
        this.mVariationsListPosition = position;
    }
    
    @Override
    public void sendListener(int identifier, OnClickListener onTeaserClickListener) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnFragmentActivityInteraction) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnActivityFragmentInteraction");
        }
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
        displayVariations();
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
        // FlurryTracker.get().begin();
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
        // FlurryTracker.get().end();
    }

    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        return true;
    }

    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        return false;
    }

    private void displayVariations() {

        if (isNotValidVariation(mCompleteProduct.getVariations())) {
            mVariationsContainer.setVisibility(View.GONE);
            return;
        }
        if(mList == null)
            mList = (HorizontalListView) mainView.findViewById(R.id.variations_list);
        if (mAdapter == null) {
            mAdapter = new ProductImagesAdapter(getActivity(),
                    ProductImagesAdapter.createImageList(mCompleteProduct.getVariations()));
            mList.setAdapter(mAdapter);
        } else {
            mAdapter.replaceAll(ProductImagesAdapter.createImageList(mCompleteProduct
                    .getVariations()));
        }

        int indexOfSelectionVariation = findIndexOfSelectedVariation();

        mList.setOnItemClickListener(this);
        mList.setSelectedItem(indexOfSelectionVariation, HorizontalListView.MOVE_TO_DIRECTLY);
        Log.d(TAG, "displayVariations: list position = " + mVariationsListPosition);
        mList.setPosition(mVariationsListPosition);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mVariationsListPosition = position;
        mCallback.onFragmentElementSelected(position);
        mList.setSelectedItem(position, HorizontalListView.MOVE_TO_DIRECTLY);
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

}
