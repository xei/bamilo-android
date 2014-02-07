/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.FragmentCommunicatorForProduct;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.MetricAffectingSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class ProductSpecificationsFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ProductSpecificationsFragment.class);

    private TextView mProductSpecText;

    private View mProductSpecContainer;

    private TextView mProductSpecSku;
    
    private RelativeLayout mLoading;

    private CompleteProduct mCompleteProduct;
    
    private int CURRENT_IMAGE_INDEX = 0;
    private View mainView;
    
    private OnFragmentActivityInteraction mCallback;
    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductSpecificationsFragment getInstance() {
        ProductSpecificationsFragment productImageShowOffFragment = new ProductSpecificationsFragment();
        return productImageShowOffFragment;
    }

    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public ProductSpecificationsFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class), EnumSet.of(MyMenuItem.SHARE), 
                NavigationAction.Products, 
                R.string.product_details_title);
        this.setRetainInstance(true);
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
        this.mCompleteProduct = (CompleteProduct) values;
        if(identifier==1){
            displaySpecification();
        }
    }

    @Override
    public void sendPositionToFragment(int position){

        /**
         * if still loading the product info, show image loading.
         */
        if(position < 0){
            showContentLoading();
        }
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
//        try {
//            mCallback = (OnFragmentActivityInteraction) getActivity();
//        } catch (ClassCastException e) {
//            throw new ClassCastException(getActivity().toString()
//                    + " must implement OnActivityFragmentInteraction");
//        }
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

        mainView = mInflater.inflate(R.layout.productdetails_specification_fragment, viewGroup, false);
        mProductSpecContainer = mainView.findViewById(R.id.product_specifications_frame);
        mProductSpecContainer.setOnClickListener(this);
        mProductSpecText = (TextView) mainView.findViewById(R.id.product_specifications_text);
        mProductSpecSku = (TextView) mainView.findViewById(R.id.product_sku_text);
        mLoading = (RelativeLayout) mProductSpecContainer
                .findViewById(R.id.loading_specifications);
        displaySpecification();
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
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(ProductDetailsActivityFragment.PRODUCT_COMPLETE))
            mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
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
 
    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
        BaseActivity activity = ((BaseActivity) getActivity());
        if (null == activity) {
            activity = mainActivity;
        }
        activity.onSwitchFragment(FragmentType.PRODUCT_DESCRIPTION, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }
    
    private void showContentLoading(){
        mLoading.setVisibility(View.VISIBLE);
        mProductSpecSku.setVisibility(View.GONE);
        mProductSpecText.setVisibility(View.GONE);
    }
    
    private void hideContentLoading(){
        mLoading.setVisibility(View.GONE);
        mProductSpecSku.setVisibility(View.VISIBLE);
        mProductSpecText.setVisibility(View.VISIBLE);
    }
    
    private void displaySpecification() {
        this.mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        String shortDescription = (this.mCompleteProduct != null && this.mCompleteProduct.getShortDescription() != null) ? this.mCompleteProduct.getShortDescription() : "" ;
        
        if(mProductSpecSku!=null && this.mCompleteProduct != null){
            mProductSpecSku.setText(this.mCompleteProduct.getSku() != null ? this.mCompleteProduct.getSku() : "");
        }
                
        if (TextUtils.isEmpty(shortDescription)) {
            mProductSpecText.setText("");
            mProductSpecText.setVisibility(View.GONE);
        } else {
            mProductSpecText.setVisibility(View.VISIBLE);
            String translatedDescription = shortDescription.replace("\r", "<br>");
            Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
            // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);                
                MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
                for (MetricAffectingSpan span: spans) {
                    htmlText.removeSpan(span);                
                }
            }
            mProductSpecText.setText(htmlText);
//            mProductSpecText.setText(Html.fromHtml(translatedDescription));
        }
        hideContentLoading();
    }

    @Override
    public void notifyFragment(Bundle bundle) {
//        Log.i(TAG, "code1 notifyFragment Specification");
        // Validate if fragment is on the screen
        if(!isVisible()){
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }
        
        if(bundle.containsKey(ProductDetailsActivityFragment.LOADING_PRODUCT)){
            showContentLoading();
        }
        
        mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        
        displaySpecification();
    }
}
