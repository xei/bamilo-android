/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.service.ServiceManager;
import pt.rocket.framework.service.services.ProductService;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.OnFragmentActivityInteraction;
import pt.rocket.utils.ProductDetailsFragmentCommunicator;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import org.holoeverywhere.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class ProductBasicInfoFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = LogTagHelper.create(ProductBasicInfoFragment.class);

    private TextView mProductName;

    private TextView mProductResultPrice;

    private TextView mProductNormalPrice;
    
    private TextView mStockInfo; 
    
    private RelativeLayout mLoading;

    private CompleteProduct mCompleteProduct;
    
    private int CURRENT_IMAGE_INDEX = 0;
    private View mainView;
    
    private OnFragmentActivityInteraction mCallback;
    
    private String unitPrice;
    private String specialPrice;
    private int discountPercentage;
    private int stockQuantity;
    
    public static final String DEFINE_UNIT_PRICE = "unit_price";
    public static final String DEFINE_SPECIAL_PRICE = "special_price";
    public static final String DEFINE_DISCOUNT_PERCENTAGE = "discount_percentage";
    public static final String DEFINE_STOCK = "stock";
    
    
    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductBasicInfoFragment getInstance() {
        ProductBasicInfoFragment mProductBasicInfoFragment = new ProductBasicInfoFragment();
        return mProductBasicInfoFragment;
    }

    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public ProductBasicInfoFragment() {
        super(EnumSet.of(EventType.GET_PRODUCT_EVENT), EnumSet.noneOf(EventType.class), EnumSet.of(MyMenuItem.SHARE), 
                NavigationAction.Products, 
                R.string.product_details_title);
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
//        if(identifier == 0){
//            this.mCompleteProduct = (CompleteProduct) values;   
//        } else if(identifier==1){
//            this.mCompleteProduct = (CompleteProduct) values;
//            setBasicInfo();
//        } else if(identifier == DEFINE_UNIT_PRICE){
//            unitPrice = (String) values;
//        } else if(identifier == DEFINE_SPECIAL_PRICE){
//            specialPrice = (String) values;
//        } else if(identifier == DEFINE_DISCOUNT_PERCENTAGE){
//            discountPercentage = (Integer) values;
//        } else if(identifier == DEFINE_STOCK){
//            stockQuantity = (Integer) values;
//            updateStockInfo();
//        }
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

        mainView = mInflater.inflate(R.layout.productdetails_basic_info_fragment, viewGroup, false);
        mProductName = (TextView) mainView.findViewById(R.id.product_name);
        mProductResultPrice = (TextView) mainView.findViewById(R.id.product_price_result);
        mProductNormalPrice = (TextView) mainView.findViewById(R.id.product_price_normal);
        mProductNormalPrice.setPaintFlags(mProductNormalPrice.getPaintFlags()
                | Paint.STRIKE_THRU_TEXT_FLAG);
        mStockInfo = (TextView) mainView.findViewById(R.id.product_instock);
        mLoading = (RelativeLayout) mainView
                .findViewById(R.id.loading_specifications);
        
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

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }


    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        return false;
    }
 
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        mCallback.onFragmentSelected(FragmentType.PRODUCT_BASIC_INFO);
    }
    
    private void showContentLoading(){
        mLoading.setVisibility(View.VISIBLE);
    }
    
    private void updateStockInfo() {

       if(mStockInfo == null){
           return;
       }
        
      if (stockQuantity < 0) {
          mStockInfo.setVisibility(View.GONE);
          return;
      } else
          mStockInfo.setVisibility(View.VISIBLE);


      if (stockQuantity > 0) {
          mStockInfo.setText(getActivity().getString(R.string.shoppingcart_instock));
          mStockInfo.setTextColor(getActivity().getResources().getColor(R.color.green_stock));
      } else {
          mStockInfo.setText(getActivity().getString(R.string.shoppingcart_notinstock));
          mStockInfo.setTextColor(getActivity().getResources().getColor(R.color.red_basic));
      }
  }
    
    
    private void hideContentLoading(){
        mLoading.setVisibility(View.GONE);
    }
    
    private void setBasicInfo(){
        if(mCompleteProduct!=null){
            Log.i(TAG, "code1 is diferent from null!!!");
            Log.i(TAG, "code1 name is : " + mCompleteProduct.getName());
            if(mProductName!= null)
                mProductName.setText(mCompleteProduct.getBrand()!= null ? mCompleteProduct.getBrand() : "" + " " + mCompleteProduct.getName());
            updateStockInfo();
            displayPriceInfo();    
        }
    }
    
    private void displayPriceInfo() {
        Log.d(TAG, "displayPriceInfo: unitPrice = " + unitPrice + " specialPrice = " + specialPrice);
        if (specialPrice == null || specialPrice.equals(unitPrice)) {
            // display only the normal price
            mProductResultPrice.setText(unitPrice);
            mProductResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            mProductNormalPrice.setVisibility(View.GONE);
        } else {
            // display reduced and special price
            mProductResultPrice.setText(specialPrice);
            mProductResultPrice.setTextColor(getResources().getColor(R.color.red_basic));
            mProductNormalPrice.setText(unitPrice);
            mProductNormalPrice.setVisibility(View.VISIBLE);
        }
        hideContentLoading();
    }

    @Override
    public void notifyFragment(Bundle bundle) {
        Log.i(TAG, "code1 notifyFragment basic info");
        if(bundle == null){
            return;
        }
        
        if(!bundle.containsKey("start")){
            return;
        }
        
        if(bundle.containsKey(ProductDetailsActivityFragment.PRODUCT_COMPLETE)){
            mCompleteProduct = ProductDetailsFragmentCommunicator.getInstance().getCurrentProduct();
            setBasicInfo();
        }
        
       if(bundle.containsKey(ProductDetailsActivityFragment.LOADING_PRODUCT_KEY)){
           /**
            * if still loading the product info, show image loading.
            */
           if(bundle.getInt(ProductDetailsActivityFragment.LOADING_PRODUCT_KEY) < 0){
               showContentLoading();
           }
       }
       
       if(bundle.containsKey(ProductBasicInfoFragment.DEFINE_STOCK)){
           stockQuantity = bundle.getInt(ProductBasicInfoFragment.DEFINE_STOCK);
           updateStockInfo();  
       } else{
           unitPrice = bundle.getString(ProductBasicInfoFragment.DEFINE_UNIT_PRICE);
           specialPrice = bundle.getString(ProductBasicInfoFragment.DEFINE_SPECIAL_PRICE);
           discountPercentage = bundle.getInt(ProductBasicInfoFragment.DEFINE_DISCOUNT_PERCENTAGE);
       }
        
    }
}
