/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ProductDetailsDescriptionFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ProductDetailsDescriptionFragment.class);
    
    private static ProductDetailsDescriptionFragment sProductDetailsDescriptionFragment;

    private TextView mProductName;
    private TextView mProductResultPrice;
    private TextView mProductNormalPrice;
    private TextView mProductFeaturesText;
    private TextView mProductDescriptionText;
    private CompleteProduct mCompleteProduct;
    private View mainView;
    

    /**
     * Get instance
     * 
     * @return
     */
    public static ProductDetailsDescriptionFragment getInstance() {
        sProductDetailsDescriptionFragment = new ProductDetailsDescriptionFragment();
        return sProductDetailsDescriptionFragment;
    }

    /**
     * Empty constructor
     */
    public ProductDetailsDescriptionFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                R.layout.product_description_fragment,
                0,
                KeyboardState.NO_ADJUST_CONTENT);
        // R.string.product_details_title
        this.mCompleteProduct = JumiaApplication.INSTANCE.getCurrentProduct();
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
        // Retain this fragment across configuration changes.
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        mainView = view;
        mProductName = (TextView) mainView.findViewById( R.id.product_name );
        mProductResultPrice = (TextView) mainView.findViewById( R.id.product_price_result );
        mProductNormalPrice = (TextView) mainView.findViewById( R.id.product_price_normal );
        mProductFeaturesText = (TextView) mainView.findViewById( R.id.product_features_text );
        mProductDescriptionText = (TextView) mainView.findViewById( R.id.product_description_text );
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
        mCompleteProduct = JumiaApplication.INSTANCE.getCurrentProduct();
        /**
         * Validate product
         * If null is assumed that the system clean some data
         */
        if(mCompleteProduct != null && mainView != null) {
            getViews();
            displayProductInformation(mainView);
        }else{
            if(getActivity() != null) Toast.makeText(getActivity(), getString(R.string.product_could_not_retrieved), Toast.LENGTH_SHORT).show();
            restartAllFragments();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(TAG, "ON DESTROY VIEW");
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
        mainView = null;
        mCompleteProduct = null;
        System.gc();
    }
    
    
    private void getViews(){
        mProductName = (TextView) mainView.findViewById( R.id.product_name );
        mProductResultPrice = (TextView) mainView.findViewById( R.id.product_price_result );
        mProductNormalPrice = (TextView) mainView.findViewById( R.id.product_price_normal );
        mProductFeaturesText = (TextView) mainView.findViewById( R.id.product_features_text );
        mProductDescriptionText = (TextView) mainView.findViewById( R.id.product_description_text );
    }
    
    private void displayProductInformation(View view ) {
        mProductName.setText( mCompleteProduct.getBrand() + " " + mCompleteProduct.getName());
        displayPriceInformation();
        displaySpecification();
        displayDescription();
    }
    
    private void displayPriceInformation() {
        String unitPrice = mCompleteProduct.getPrice();
        if ( unitPrice == null) {
            unitPrice = mCompleteProduct.getMaxPrice();
        }
        String specialPrice = mCompleteProduct.getSpecialPrice(); 
        if ( specialPrice == null)
            specialPrice = mCompleteProduct.getMaxSpecialPrice();
        
        displayPriceInfo(unitPrice, specialPrice);
    }
    
    private void displayPriceInfo(String unitPrice, String specialPrice) {
        
        if ( specialPrice == null && unitPrice == null ) {
          mProductNormalPrice.setVisibility(View.GONE);
          mProductResultPrice.setVisibility(View.GONE);  
        } else if (specialPrice == null || ( unitPrice.equals( specialPrice ))) {
            // display only the normal price
            mProductResultPrice.setText( unitPrice );
            mProductResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            mProductNormalPrice.setVisibility(View.GONE);
        } else {
            // display reduced and special price
            mProductResultPrice.setText( specialPrice );
            mProductResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            mProductNormalPrice.setText( unitPrice );
            mProductNormalPrice.setVisibility(View.VISIBLE);
            mProductNormalPrice.setPaintFlags(mProductNormalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
    
    private void displaySpecification() {
        String shortDescription = mCompleteProduct.getShortDescription();
//        if (TextUtils.isEmpty(shortDescription)) {
//            Log.i(TAG, "shortDescription : empty");
//            if(mProductFeaturesContainer!=null){
//                mProductFeaturesContainer.setVisibility(View.GONE);
//            }
//            return;
//        } else {
//            mProductFeaturesContainer.setVisibility(View.VISIBLE);
//        }        
        
        String translatedDescription = shortDescription.replace("\r", "<br>");
        Log.d(TAG, "displaySpecification: *" + translatedDescription + "*");
        
        Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
        // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);                
            MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
            for (MetricAffectingSpan span: spans) {
                htmlText.removeSpan(span);                
            }
        }
        mProductFeaturesText.setText(htmlText);
        
//        mProductFeaturesText.setText(Html.fromHtml(translatedDescription));
    }
    
    private void displayDescription() {
        String longDescription = mCompleteProduct.getDescription();
        String translatedDescription = longDescription.replace("\r", "<br>");
        Spannable htmlText = (Spannable) Html.fromHtml(translatedDescription);
        // Issue with ICS (4.1) TextViews giving IndexOutOfBoundsException when passing HTML with bold tags
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Log.d(TAG, "REMOVE STYLE TAGS: " + translatedDescription);                
            MetricAffectingSpan spans[] = htmlText.getSpans(0, htmlText.length(), MetricAffectingSpan.class);
            for (MetricAffectingSpan span: spans) {
                htmlText.removeSpan(span);                
            }
        }
        mProductDescriptionText.setText(htmlText);
        
//        mProductDescriptionText.setText( Html.fromHtml(translatedDescription));
    }
    

}
