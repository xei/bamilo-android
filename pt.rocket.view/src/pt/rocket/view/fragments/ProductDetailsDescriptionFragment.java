/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class ProductDetailsDescriptionFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ProductDetailsDescriptionFragment.class);

    private TextView mProductName;
    private TextView mProductResultPrice;
    private TextView mProductNormalPrice;
    private TextView mProductFeaturesText;
    private TextView mProductDescriptionText;
    private TextView mProductDetailsText;
    private View mProductFeaturesContainer;
    private CompleteProduct mCompleteProduct;
    private View mainView;
    private static ProductDetailsDescriptionFragment productDetailsDescriptionFragment;

    /**
     * Get instance
     * 
     * @return
     */
    public static ProductDetailsDescriptionFragment getInstance() {
        if (productDetailsDescriptionFragment == null)
            productDetailsDescriptionFragment = new ProductDetailsDescriptionFragment();
        return productDetailsDescriptionFragment;
    }

    /**
     * Empty constructor
     */
    public ProductDetailsDescriptionFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
        this.mCompleteProduct = ServiceManager.SERVICES.get(ProductService.class).getCurrentProduct();
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        mainView = inflater.inflate(R.layout.product_details_description_frame, container, false);
        
        mProductName = (TextView) mainView.findViewById( R.id.product_name );
        mProductResultPrice = (TextView) mainView.findViewById( R.id.product_price_result );
        mProductNormalPrice = (TextView) mainView.findViewById( R.id.product_price_normal );
        
        mProductFeaturesContainer = mainView.findViewById(R.id.product_features_container);
        mProductFeaturesText = (TextView) mainView.findViewById( R.id.product_features_text );
        mProductDescriptionText = (TextView) mainView.findViewById( R.id.product_description_text );
        mProductDetailsText = (TextView) mainView.findViewById( R.id.product_details_text );
        
        displayProductInformation(mainView);
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
        this.mCompleteProduct = ServiceManager.SERVICES.get(ProductService.class).getCurrentProduct();
        displayProductInformation(mainView);
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
        Log.i(TAG, "ON DESTROY");
    }
    
    
    private void displayProductInformation(View view ) {
        mProductName.setText( mCompleteProduct.getName());
        displayPriceInformation();
        displaySpecification();
        displayDescription();
        displayDetails(view);
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
    
        if (TextUtils.isEmpty(shortDescription)) {
            mProductFeaturesContainer.setVisibility(View.GONE);
            return;
        } else {
            mProductFeaturesText.setVisibility(View.VISIBLE);
        }        
        
        String translatedDescription = shortDescription.replace("\r", "<br>");
        Log.d(TAG, "displaySpecification: *" + translatedDescription + "*");
        mProductFeaturesText.setText(Html.fromHtml(translatedDescription));
    }
    
    private void displayDescription() {
        String longDescription = mCompleteProduct.getDescription();
        String translatedDescription = longDescription.replace("\r", "<br>");
        mProductDescriptionText.setText( Html.fromHtml(translatedDescription));
    }
    
    private void displayDetails(View view) {
        // TODO: where do the details come from
        // The database for complete product delivers two similar texts.
        // It looks strange to show them both as "long description" and as details
        view.findViewById( R.id.product_details_container).setVisibility( View.GONE );
    }

    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        // TODO Auto-generated method stub
        return false;
    }


}
