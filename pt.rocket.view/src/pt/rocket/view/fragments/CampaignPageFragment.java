/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;

import pt.rocket.app.JumiaApplication;
import pt.rocket.components.customfontviews.TextView;
import pt.rocket.components.absspinner.IcsAdapterView;
import pt.rocket.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import pt.rocket.components.absspinner.IcsSpinner;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.components.HeaderGridView;
import pt.rocket.framework.objects.Campaign;
import pt.rocket.framework.objects.CampaignItem;
import pt.rocket.framework.objects.CampaignItemSize;
import pt.rocket.framework.objects.TeaserCampaign;
import pt.rocket.framework.tracking.GTMEvents.GTMValues;
import pt.rocket.framework.tracking.TrackingPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.campaign.GetCampaignHelper;
import pt.rocket.helpers.cart.GetShoppingCartAddItemHelper;
import pt.rocket.helpers.search.GetSearchProductHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.Toast;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.UIUtils;
import pt.rocket.utils.deeplink.DeepLinkManager;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.imageloader.RocketImageLoader;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show campaign page
 * @author sergiopereira
 */
public class CampaignPageFragment extends BaseFragment implements OnClickListener, OnScrollListener, IResponseCallback {

    public static final String TAG = LogTagHelper.create(CampaignPageFragment.class);
    
    private final static String COUNTER_START_TIME = "start_time";

    private final static String BANNER_STATE = "banner_state";

    private TeaserCampaign mTeaserCampaign;
    
    public int NAME = R.id.name;

    public int BRAND = R.id.brand;

    public int PRICE = R.id.price;
    
    public int PROD = R.id.product;
    
    public int SKU = R.id.sku;
    
    public int SIZE = R.id.size;
    
    public int STOCK = R.id.stock;
    
    public int DISCOUNT = R.id.discount;
    
    private Campaign mCampaign;

    private HeaderGridView mGridView;

    private View mBannerView;

    private DialogGenericFragment mDialogAddedToCart;

    private boolean isAddingProductToCart;

    private DialogGenericFragment mDialogErrorToCart;
    
    private long mStartTimeInMilliseconds;

    private boolean isScrolling;
    
    private enum BannerVisibility{
        DEFAULT,
        VISIBLE,
        HIDDEN
    }

    private BannerVisibility bannerState;
    
    /**
     * Constructor via bundle
     * @return CampaignFragment
     * @author sergiopereira
     */
    public static CampaignPageFragment getInstance(Bundle bundle) {
        CampaignPageFragment campaignPageFragment = new CampaignPageFragment();
        campaignPageFragment.setArguments(bundle);
        return campaignPageFragment;
    }

    /**
     * Empty constructor
     */
    public CampaignPageFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.campaign_fragment_pager_item);
        bannerState = BannerVisibility.DEFAULT;
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
        // Get campaigns from arguments
        mTeaserCampaign = getArguments().getParcelable(TAG);
        // Validate the saved state
        if(savedInstanceState != null ) {
            Log.i(TAG, "ON GET SAVED STATE");
            if(savedInstanceState.containsKey(TAG))
                mCampaign = savedInstanceState.getParcelable(TAG);
            // Restore startTime
            if(savedInstanceState.containsKey(COUNTER_START_TIME)) 
                mStartTimeInMilliseconds = savedInstanceState.getLong(COUNTER_START_TIME, SystemClock.elapsedRealtime());
            if(savedInstanceState.containsKey(BANNER_STATE)) 
                bannerState = (BannerVisibility)savedInstanceState.getSerializable(BANNER_STATE);
        }
        
        // Tracking
        TrackerDelegator.trackCampaignView(mTeaserCampaign != null ? mTeaserCampaign.getTargetTitle() : "n.a.");
        
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        Log.d(TAG, "TEASER CAMPAIGN: " + mTeaserCampaign.getTargetTitle() + " " + mTeaserCampaign.getTargetUrl());
        // Get grid view
        mGridView = (HeaderGridView) view.findViewById(R.id.campaign_grid);
        // Set onScrollListener to signal adapter's Handler when user is scrolling
        mGridView.setOnScrollListener(this);
        // Validate the current state
        getAndShowCampaign();
        
    }
        
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onStart()
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
        isScrolling = false;
        // Track page
        TrackerDelegator.trackPage(TrackingPage.CAMPAIGNS,getLoadTime(), false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE STATE: CAMPAIGN");
        outState.putParcelable(TAG, mCampaign);
        outState.putLong(COUNTER_START_TIME, mStartTimeInMilliseconds);
        outState.putSerializable(BANNER_STATE, bannerState);
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
        isScrolling = true;
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
        Log.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }
    
    /**
     * Get and show the campaign 
     * @author sergiopereira
     */
    private void getAndShowCampaign() {
        // Get the campaign id
        String id = (mTeaserCampaign != null) ? mTeaserCampaign.getTargetUrl() : null;
        // Validate the current state
        if(mCampaign == null) triggerGetCampaign(id);
        else showCampaign();
    }
    
    /**
     * Load the dynamic form
     * @param form
     * @author sergiopereira
     */
    private void showCampaign() {
        Log.i(TAG, "LOAD CAMPAIGN");
        // Get banner
        mBannerView = getBannerView();
		// Add banner to header
        if (BannerVisibility.HIDDEN != bannerState) mGridView.addHeaderView(mBannerView);
        // Validate the current data
        if (mGridView.getAdapter() == null) {
            // Set adapter
            CampaignAdapter mArrayAdapter = new CampaignAdapter(getBaseActivity(), mCampaign.getItems(), (OnClickListener) this);
            mGridView.setAdapter(mArrayAdapter);
        }
        // Show content
        if (BannerVisibility.HIDDEN == bannerState) showContent();
		// else show when is loaded the banner
    }
    
    /**
     * Get the banner view to add the header view
     * @return View
     * @author sergiopereira
     */
    private View getBannerView(){
        // Inflate the banner layout
        final View bannerView = LayoutInflater.from(getActivity()).inflate(R.layout.campaign_fragment_banner, mGridView, false);
        if (BannerVisibility.HIDDEN != bannerState) {
            // Get the image view
            final ImageView imageView = (ImageView) bannerView.findViewById(R.id.campaign_banner);
            // Load the bitmap
            String url = (getResources().getBoolean(R.bool.isTablet)) ? mCampaign.getTabletBanner() : mCampaign.getMobileBanner();
            RocketImageLoader.instance.loadImage(url, imageView, false, new RocketImageLoader.RocketImageLoaderListener() {
                
                @Override
                public void onLoadedSuccess(Bitmap bitmap) {
                    // Show content
                    imageView.setImageBitmap(bitmap);
                    bannerState = BannerVisibility.VISIBLE;
                    showContent();                
                }
                
                @Override
                public void onLoadedError() {
                    bannerView.setVisibility(View.GONE);
                    mGridView.removeHeaderView(bannerView);
                    bannerState = BannerVisibility.HIDDEN;
                    // Show content
                    showContent();                
                }
                
                @Override
                public void onLoadedCancel(String imageUrl) {
                    bannerView.setVisibility(View.GONE);
                    mGridView.removeHeaderView(bannerView);
                    bannerState = BannerVisibility.HIDDEN;
                    // Show content
                    showContent();                
                }
            });
        }
        
        // Return the banner
        return bannerView;
    }
    
    /**
     * Show only the content view
     * @author sergiopereira
     */
    private synchronized void showContent() {
        mGridView.refreshDrawableState();
        showFragmentContentContainer();
    }
    
    /**
     * Show only the retry view
     * @author sergiopereira
     */
    private void showRetry() {
        showFragmentRetry((OnClickListener) this);
    }
    
    /**
     * ############# LISTENERS #############
     */
    
    /*
     * (non-Javadoc)
     * @see android.widget.AbsListView.OnScrollListener#onScrollStateChanged(android.widget.AbsListView, int)
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_FLING) isScrolling = true;
        else isScrolling = false;
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // ...
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        // Get view id
        int id = view.getId();
        // Buy button
        if(id == R.id.campaign_item_button_buy) onClickBuyButton(view);
        // Product name and image container
        else if (id == R.id.image_container || id == R.id.campaign_item_name) onClickProduct(view);
//        // Retry button
//        else if(id == R.id.fragment_root_retry_button) onClickRetryButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
//    /**
//     * Process the click on the retry button
//     * @author sergiopereira
//     */
//    private void onClickRetryButton(){
//        getAndShowCampaign();
//    }
    
    /**
     * Process the click on the buy button
     * @param view 
     * @author sergiopereira
     */
    private void onClickBuyButton(View view) {
        String prod = (String) view.getTag(PROD);
        String sku = (String) view.getTag(SKU);
        String size = (String) view.getTag(SIZE);
        Boolean hasStock = (Boolean) view.getTag(STOCK);
        String name = (String) view.getTag(NAME);
        String brand = (String) view.getTag(BRAND);
        double price = (Double) view.getTag(PRICE);
        double discount = (Double) view.getTag(DISCOUNT);
        
        Log.i(TAG, "ON CLICK BUY " + sku + " " + size + " " + hasStock);
        // Validate the remain stock
        if(!hasStock)
            Toast.makeText(getBaseActivity(), getString(R.string.campaign_stock_alert), Toast.LENGTH_LONG).show();
        // Validate click
        else if(!isAddingProductToCart) {
            // Create values to add to cart
            ContentValues values = new ContentValues();
            values.put(GetShoppingCartAddItemHelper.PRODUCT_TAG, prod);
            values.put(GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
            values.put(GetShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
            triggerAddToCart(values);
            // Tracking
            trackAddtoCart(sku, name, brand, price, discount);
        } 
    }
    
    /**
     * Track item added to cart
     * @author sergiopereira
     */
    private void trackAddtoCart(String sku, String name, String brand, double price, double discount){
        try {
            // Tracking
            Bundle bundle = new Bundle();
            bundle.putString(TrackerDelegator.SKU_KEY, sku);
            bundle.putDouble(TrackerDelegator.PRICE_KEY, price);
            bundle.putString(TrackerDelegator.NAME_KEY, name);
            bundle.putString(TrackerDelegator.BRAND_KEY, brand);
            bundle.putDouble(TrackerDelegator.RATING_KEY, -1d);
            bundle.putDouble(TrackerDelegator.DISCOUNT_KEY, discount);
            bundle.putString(TrackerDelegator.LOCATION_KEY, GTMValues.CAMPAINGS);
            bundle.putString(TrackerDelegator.CATEGORY_KEY, "");
            bundle.putString(TrackerDelegator.SUBCATEGORY_KEY, "");
            
            TrackerDelegator.trackProductAddedToCart(bundle);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Process the click on the item
     * @param view
     * @author sergiopereira
     */
    private void onClickProduct(View view){
        String prod = (String) view.getTag(PROD);
        // String sku = (String) view.getTag(SKU);
        String size = (String) view.getTag(SIZE);
        Log.d(TAG, "ON CLICK PRODUCT " + prod + " " + size);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(GetSearchProductHelper.SKU_TAG, prod);
        bundle.putString(DeepLinkManager.PDV_SIZE_TAG, size);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcampaign);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to get the campaign via id
     * @param id
     * @author sergiopereira
     */
    private void triggerGetCampaign(String id){
      //Validate is service is available
      if(JumiaApplication.mIsBound){
            Log.i(TAG, "TRIGGER TO GET CAMPAIGN: " + id);
            // Create request
            Bundle bundle = new Bundle();
            bundle.putString(GetCampaignHelper.CAMPAIGN_ID, id);
            triggerContentEvent(new GetCampaignHelper(), bundle, this);
       } else {
          showFragmentRetry(this);
       }
    }
    
    /**
     * Trigger to add item to cart
     * @param values
     * @author sergiopereira
     */
    private void triggerAddToCart(ContentValues values){
        Log.i(TAG, "TRIGGER ADD TO CART");
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        triggerContentEventProgress(new GetShoppingCartAddItemHelper(), bundle, this);
    }
   
    /**
     * ############# RESPONSE #############
     */
    /**
     * Filter the success response
     * @param bundle
     * @return boolean
     */
    protected boolean onSuccessEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        switch (eventType) {
        case GET_CAMPAIGN_EVENT:
            Log.d(TAG, "RECEIVED GET_CAMPAIGN_EVENT");
            // Get and show campaign
            mCampaign = (Campaign) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            /*--TODO
             * Don't apply Timer if there are no products with remainingTime defined
             */
            // Set startTime after getting request
            mStartTimeInMilliseconds = SystemClock.elapsedRealtime();
            showCampaign();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED ADD_ITEM_TO_SHOPPING_CART_EVENT");
            isAddingProductToCart = false;
            getBaseActivity().updateCartInfo();
            hideActivityProgress();
            showSuccessCartDialog();
            break;
        default:
            break;
        }
        return true;
    }
    
    /**
     * Filter the error response
     * @param bundle
     * @return boolean
     */
    protected boolean onErrorEvent(Bundle bundle) {
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        if(eventType != null){
//            if(errorCode == ErrorCode.NO_NETWORK){
//                ((CatalogFragment) getParentFragment()).disableCatalogButtons();
//                showFragmentRetry(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        getAndShowCampaign();
//                    }
//                }, R.string.no_connect_dialog_content);
//                return true;
//            } else 
                if (errorCode == ErrorCode.HTTP_STATUS) {
                showContinueShopping(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                        onClickContinueButton();
                    }
                });
                return true;
            }
        }
        
        // Generic errors
        if(super.handleErrorEvent(bundle)) return true;
        
        switch (eventType) {
        case GET_CAMPAIGN_EVENT:
            Log.d(TAG, "RECEIVED GET_CAMPAIGN_EVENT");
            // Show retry
            showRetry();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            hideActivityProgress();
            showErrorCartDialog();
            break;
        default:
            break;
        }
        
        return false;
    }
    
   protected void onRetryRequest(EventType eventType){
       getAndShowCampaign();
   }
    
    /**
     * ########### RESPONSE LISTENER ###########  
     */
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestError(android.os.Bundle)
     */
    @Override
    public void onRequestError(Bundle bundle) {
        onErrorEvent(bundle);
    }
       
    /*
     * (non-Javadoc)
     * @see pt.rocket.interfaces.IResponseCallback#onRequestComplete(android.os.Bundle)
     */
    @Override
    public void onRequestComplete(Bundle bundle) {
        onSuccessEvent(bundle);
    }
    
    /**
     * ########### DIALOGS ###########  
     */    

    
    private void showSuccessCartDialog() {

        String msgText = "1 " + getResources().getString(R.string.added_to_shop_cart_dialog_text);

        mDialogAddedToCart = DialogGenericFragment.newInstance(
                false,
                false,
                true,
                getString(R.string.your_cart),
                msgText,
                getString(R.string.go_to_cart), getString(R.string.continue_shopping),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            if(getBaseActivity() != null){
                                getBaseActivity().onSwitchFragment(
                                        FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE,
                                        FragmentController.ADD_TO_BACK_STACK);    
                            }
                            if(mDialogAddedToCart != null){
                                mDialogAddedToCart.dismiss();    
                            }
                            
                        } else if (id == R.id.button2) {
                            mDialogAddedToCart.dismiss();
                        }
                    }
                });

        mDialogAddedToCart.show(getFragmentManager(), null);
    }
    
    
    private void showErrorCartDialog (){
        FragmentManager fm = getFragmentManager();
        mDialogErrorToCart = DialogGenericFragment.newInstance(true, true, false,
                getString(R.string.error_add_to_cart_failed),
                getString(R.string.error_add_to_cart_failed),
                getString(R.string.ok_label), "", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            mDialogErrorToCart.dismiss();
                        }
                    }
                });
        mDialogErrorToCart.show(fm, null);
        return;
    }
    
    /**
     * ########### ADAPTER ###########  
     */    
    
    public class CampaignAdapter extends ArrayAdapter<CampaignItem> implements OnClickListener, OnItemSelectedListener{
        
        // private static final int YELLOW_PERCENTAGE = 34 < X < 64
        
        private static final int GREEN_PERCENTAGE = 64;
        
        private static final int ORANGE_PERCENTAGE = 34;

        public int NAME = R.id.name;

        public int BRAND = R.id.brand;

        public int PRICE = R.id.price;
        
        public int PROD = R.id.product;
        
        public int SKU = R.id.sku;
        
        public int SIZE = R.id.size;
        
        public int STOCK = R.id.stock;
        
        public int DISCOUNT = R.id.discount;
        
        private LayoutInflater mInflater;
        
        private OnClickListener mOnClickParentListener;
        
        /**
         * A representation of each item on the list
         */
        private class ItemView {
            private TextView mStockOff;
            private TextView mName;
            private View mImageContainer;
            private ImageView mImage;
            private View progress;
            private View mSizeContainer;
            private IcsSpinner mSizeSpinner;
            private TextView mPrice;
            private TextView mDiscount;
            private TextView mSave;
            private ProgressBar mStockBar;
            private TextView mStockPercentage;
            private View mButtonBuy;
            private TextView mOfferEnded;
            private View mTimerContainer;
            private TextView mTimer;
            private int mRemaingTime;

            /**
             * Handler used to update Timer every second, when user is not scrolling
             */
            private Handler mHandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    // only update if is not detected a fling (fast scrolling) on gridview
                    if (!isScrolling) {
                        updateTimer(mTimer, mTimerContainer, mButtonBuy, mOfferEnded, mName, mImage, mRemaingTime, mImageContainer);
                    }
                    this.sendEmptyMessageDelayed(0, 1000);
                };
            };
        }
        
        /**
         * 
         * @param context
         * @param items
         * @param parentListener
         */
        public CampaignAdapter(Context context, ArrayList<CampaignItem> items, OnClickListener parentListener) {
            super(context, R.layout.campaign_fragment_list_item, items);
            mInflater = LayoutInflater.from(context);
            mOnClickParentListener = parentListener;
        }
        
        /*
         * (non-Javadoc)
         * @see android.widget.ArrayAdapter#getItem(int)
         */
        @Override
        public CampaignItem getItem(int position) {
            return super.getItem(position);
        }
        
        /*
         * (non-Javadoc)
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            // Validate current view
            if (convertView == null) view = mInflater.inflate(R.layout.campaign_fragment_list_item, parent, false);
            else view = convertView;
            // Get the class associated to the view
            ItemView itemView = getItemView(view);
            // Get current campaign item
            CampaignItem item = getItem(position);
            // Set data
            setData(itemView, item, position);
            // Return the filled view
            return view;
        }
        
        /**
         * Get the recycled view
         * @param view
         * @return ItemView
         * @author sergiopereira
         */
        private ItemView getItemView(View view){
            ItemView item;
            if ((ItemView) view.getTag() == null) {
                item = new ItemView();
                // Get stock off
                item.mStockOff = (TextView) view.findViewById(R.id.campaign_item_stock_off);
                // Get name
                item.mName = (TextView) view.findViewById(R.id.campaign_item_name);
                // Get image container
                item.mImageContainer = view.findViewById(R.id.image_container);
                // Get image
                item.mImage = (ImageView) view.findViewById(R.id.image_view);
                // Get Progress
                item.progress = view.findViewById(R.id.campaign_loading_progress);
                // Get size container
                item.mSizeContainer = view.findViewById(R.id.campaign_item_size_container);
                // Get size spinner
                item.mSizeSpinner = (IcsSpinner) view.findViewById(R.id.campaign_item_size_spinner);
                // Get price
                item.mPrice = (TextView) view.findViewById(R.id.campaign_item_price);
                // Get discount
                item.mDiscount = (TextView) view.findViewById(R.id.campaign_item_discount);
                // Get save
                item.mSave = (TextView) view.findViewById(R.id.campaign_item_save_value);
                // Get stock bar
                item.mStockBar = (ProgressBar) view.findViewById(R.id.campaign_item_stock_bar);
                // Get stock %
                item.mStockPercentage = (TextView) view.findViewById(R.id.campaign_item_stock_value);
                // Get button
                item.mButtonBuy = view.findViewById(R.id.campaign_item_button_buy);
                // Get Offer Ender image
                item.mOfferEnded = (TextView) view.findViewById(R.id.campaign_item_offer_ended);
                // Get timer container
                item.mTimerContainer = view.findViewById(R.id.campaign_item_stock_timer_container);
                // Get timer
                item.mTimer = (TextView) view.findViewById(R.id.campaign_item_stock_timer);
                // Stores the item representation on the tag of the view for later retrieval
                view.setTag(item);
            } else {
                item = (ItemView) view.getTag();
            }
            return item;
        }
        
        /**
         * Set the campaign data 
         * @param view
         * @param item
         * @author sergiopereira
         */
        private void setData(ItemView view, CampaignItem item, int position) {
            //Log.d(TAG, "SET DATA");
            // Set stock off
            setStockOff(view, item);
            
            // Set name
            view.mName.setText(item.getName());
            setClickableView(view.mName, position);
            // Set image container
            setClickableView(view.mImageContainer, position);
            // Set image
            RocketImageLoader.instance.loadImage(item.getImage(), view.mImage, view.progress, R.drawable.no_image_large);
            // RocketImageLoader.instance.loadImage(item.getImage(), view.mImage, null, R.drawable.no_image_large);
            // Set size
            setSizeContainer(view, item, position);
            // Set price and special price
            setPriceContainer(view, item);
            // Set save value
            setSaveContainer(view, item);
            // Set stock bar
            setStockBar(view.mStockBar, item.getStockPercentage());
            // Set stock percentage
            view.mStockPercentage.setText(item.getStockPercentage() + "%");
            view.mStockPercentage.setSelected(true);
            // Set buy button
            setClickableView(view.mButtonBuy, position);
            // Set timer
            int remainingTime = item.getRemainingTime();
            // Set itemView's remainingTime to be used by handler
            view.mRemaingTime = remainingTime;

            // start handler processing
            if(remainingTime > 0) view.mHandler.sendEmptyMessageDelayed(0, 1000);

            // update Timer
            updateTimer(view.mTimer, view.mTimerContainer, view.mButtonBuy, view.mOfferEnded, view.mName, view.mImage, remainingTime, view.mImageContainer);
        }

        /**
         * 
         * TODO: Try use a SimpleDateFormat
         * 
         * Update Timer with remaining Time or show "Offer Ended" when time remaining reaches 0
         * 
         * @param timer
         * @param timerContainer
         * @param buttonBuy
         * @param offerEnded
         * @param name
         * @param image
         * @param remainingTime
         */
        private void updateTimer(TextView timer, View timerContainer, View buttonBuy, View offerEnded, View name, View image, int remainingTime, View imageContainer) {
            Log.d(TAG, "updateTimer");
            if (remainingTime > 0) {
                Log.d(TAG, "Product with remainingTime");
                // calculate remaining time relatively to mStartTime
                String remaingTimeString = getRemainingTime(remainingTime);
                // Set remaing time on Timer
                if (remaingTimeString != null) {
                    timer.setText(remaingTimeString);

                    timerContainer.setVisibility(View.VISIBLE);
                    buttonBuy.setEnabled(true);
                    offerEnded.setVisibility(View.INVISIBLE);

                    // Set full opacity to image
                    UIUtils.setAlpha(image, 1F);
                // show "Offer Ended" and disable product
                } else {
                    Log.d(TAG, "Product expired!");
                    showOfferEnded(timerContainer, buttonBuy, offerEnded, timer, name, image, imageContainer);
                }
            // show product normally without timers
            } else {
                timerContainer.setVisibility(View.INVISIBLE);
                buttonBuy.setEnabled(true);
                offerEnded.setVisibility(View.INVISIBLE);

                // Set full opacity to image
                UIUtils.setAlpha(image, 1F);
            }
        }

        /**
         * calculate remainingTime based on <code>mStartTimeInMilliseconds</code>(time of the API
         * request) and return it with the format "hh:mm:ss"
         * 
         * @param remainingTime
         * @return <code>String</code> with remaining time properly formatted or null if product
         *         reached the remaining time
         */
        private String getRemainingTime(int remainingTime) {
            long currentTimeInMilliseconds = SystemClock.elapsedRealtime();
            int remainingSeconds = (int) (remainingTime - ((currentTimeInMilliseconds - mStartTimeInMilliseconds) / 1000));
            Log.d(TAG, "Remaining seconds: " + remainingSeconds);

            if (remainingSeconds > 0) {
                // Format remaingSeconds to "dd:hh:mm:ss"
                int days = remainingSeconds / (24 * 3600);
                remainingSeconds %= (24 * 3600);
                int hours = remainingSeconds / 3600;
                remainingSeconds %= 3600;
                int minutes = remainingSeconds / 60;
                remainingSeconds %= 60;
                int seconds = remainingSeconds;

                StringBuilder time = new StringBuilder();

                // Only add days if more than 1 day
                // Format days with 2 or more digits
                if (days > 0) {
                    if (days < 10) {
                        time.append(twoDigitString(days));
                    } else {
                        time.append(days);
                    }
                    time.append(":");
                }
                time.append(twoDigitString(hours));
                time.append(":");
                time.append(twoDigitString(minutes));
                time.append(":");
                time.append(twoDigitString(seconds));

                return time.toString();
            } else {
                return null;
            }
        }

        /**
         * convert value of time in a two digit <code>String</code>
         * 
         * @param number
         * @return
         */
        private String twoDigitString(int number) {
            if (number == 0) {
                return "00";
            }
            if (number / 10 == 0) {
                return "0" + number;
            }
            return String.valueOf(number);
        }

        /**
         * Show Timer with text "00:00:00" and disable buttons and redirects to PDV
         * 
         * @param timerContainer
         * @param buttonBuy
         * @param offerEnded
         * @param timer
         * @param name
         * @param image
         */
        private void showOfferEnded(View timerContainer, View buttonBuy, View offerEnded, TextView timer, View name, View image, View imageContainer) {
            timerContainer.setVisibility(View.VISIBLE);
            buttonBuy.setEnabled(false);
            offerEnded.setVisibility(View.VISIBLE);

            timer.setText("00:00:00");

            // Disable onClickListeners
            name.setOnClickListener(null);
            image.setOnClickListener(null);
            buttonBuy.setOnClickListener(null);
            imageContainer.setOnClickListener(null);

            // Set product image as defocused
            UIUtils.setAlpha(image, 0.5F);
        }

        /**
         * Set the price and special price view
         * @param view
         * @param item
         * @author sergiopereira
         */
        private void setPriceContainer(ItemView view, CampaignItem item){
            // Set price
            view.mPrice.setSelected(true);
            // Validate special price
            if(item.getSpecialPrice() != 0) {
                view.mPrice.setVisibility(View.VISIBLE);
                // Set discount
                view.mDiscount.setText(CurrencyFormatter.formatCurrency(""+item.getSpecialPrice()));
                view.mPrice.setText(CurrencyFormatter.formatCurrency(""+item.getPrice()));
                view.mPrice.setPaintFlags(view.mPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                // Set discount
                view.mPrice.setVisibility(View.GONE);
                view.mDiscount.setText(CurrencyFormatter.formatCurrency(""+item.getPrice()));
            }
        }
        
        /**
         * Set the save value
         * @param view
         * @param item
         * @author sergiopereira
         */
        private void setSaveContainer(ItemView view, CampaignItem item){
            if(item.getSpecialPrice()>0){
                String label = getString(R.string.campaign_save);
                String value = CurrencyFormatter.formatCurrency( "" + item.getSavePrice());
                String mainText = label + " " + value;
                SpannableString greenValue = new SpannableString(mainText);
                greenValue.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey_middle)), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                greenValue.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green_campaign_bar)), label.length() + 1, mainText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                view.mSave.setVisibility(View.VISIBLE);
                view.mSave.setText(greenValue);
                view.mSave.setSelected(true);
            } else {
            	// Set as invisible to occupy the its space
                view.mSave.setVisibility(View.INVISIBLE);
            }
        }
        
        /**
         * Set a view as clickable saving the position
         * @param view
         * @param item
         * @author sergiopereira
         */
        private void setClickableView(View view, int position) {
            // Save position and add the listener
            view.setTag(position);
            view.setOnClickListener(this);
        }
 
        /**
         * Hide or show the stock off
         * @param view
         * @param item
         * @author ricardosoares
         */
        private void setStockOff(ItemView view, CampaignItem item){
            if(item.getMaxSavingPercentage() == 0){
                view.mStockOff.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 0);
                view.mName.setLayoutParams(params);
                
            }else{
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.setMargins((int)getResources().getDimension(R.dimen.margin_large), 0, 0, 0);
                view.mName.setLayoutParams(params);
                view.mStockOff.setVisibility(View.VISIBLE);
                if(getString(R.string.off_label).equals("-"))
                    view.mStockOff.setText(getString(R.string.off_label) + item.getMaxSavingPercentage() + "%");
                else
                    view.mStockOff.setText(item.getMaxSavingPercentage() + "%\n" + getString(R.string.off_label));
            }
        }
        
        /**
         * Hide or show the size container
         * @param container
         * @param spinner
         * @param item
         * @author sergiopereira
         */
        private void setSizeContainer(ItemView view, CampaignItem item, int position){
            // Campaign has sizes except itself (>1)
            if(!item.hasUniqueSize() && item.hasSizes()) {
                // Show container
                view.mSizeContainer.setVisibility(View.VISIBLE);
                // Get sizes
                ArrayList<CampaignItemSize> sizes = item.getSizes();
                // Create an ArrayAdapter using the sizes values
                ArrayAdapter<CampaignItemSize> adapter = new ArrayAdapter<CampaignItemSize>(getContext(), R.layout.campaign_spinner_item, sizes);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(R.layout.campaign_spinner_dropdown_item);
                // Apply the adapter to the spinner
                view.mSizeSpinner.setAdapter(adapter);
                // Save position in spinner
                view.mSizeSpinner.setTag(position);
                // Check pre selection
                if(item.hasSelectedSize()) {
                    view.mSizeSpinner.setSelection(item.getSelectedSizePosition());
                }
                // Force reload content to redraw the default selection value
                adapter.notifyDataSetChanged();
                // Apply the select listener
                view.mSizeSpinner.setOnItemSelectedListener(this);
            } else {
                // Hide the size container
                view.mSizeContainer.setVisibility(View.GONE);
                // Set itself as selected size
                CampaignItemSize size = null;
                try {
                    size = item.getSizes().get(0);
                } catch (IndexOutOfBoundsException e) {
                    Log.w(TAG, "WARNING: IOBE ON SET SIZE SELECTION: 0");
                } catch (NullPointerException e) {
                    Log.w(TAG, "WARNING: NPE ON SET SELECTED SIZE: 0");
                }
                item.setSelectedSizePosition(0);
                item.setSelectedSize(size);
            }
        }
                
        /**
         * Set the stock bar color
         * @param view
         * @param stock
         * @author sergiopereira
         */
        private void setStockBar(ProgressBar view, int stock) {
            Rect bounds = view.getProgressDrawable().getBounds();
            // Case GREEN:
            if(stock >= GREEN_PERCENTAGE)
                view.setProgressDrawable(getResources().getDrawable(R.drawable.campaign_green_bar));
            // Case YELLOW:
            else if(ORANGE_PERCENTAGE < stock && stock < GREEN_PERCENTAGE)
                view.setProgressDrawable(getResources().getDrawable(R.drawable.campaign_yellow_bar));
            // Case ORANGE:
            else  view.setProgressDrawable(getResources().getDrawable(R.drawable.campaign_orange_bar));
            // Set value
            view.getProgressDrawable().setBounds(bounds);
            view.setProgress(stock);
        }
        
        /**
         * ######### LISTENERS #########
         */
        
        @Override
        public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
            String parentPosition = parent.getTag().toString();
            CampaignItemSize size = (CampaignItemSize) parent.getItemAtPosition(position);
            //Log.d(TAG, "CAMPAIGN ON ITEM SELECTED: " + size.simpleSku + " " +  position + " " + parentPosition);
            CampaignItem campaignItem = getItem(Integer.valueOf(parentPosition));
            campaignItem.setSelectedSizePosition(position);
            campaignItem.setSelectedSize(size);
        }

        @Override
        public void onNothingSelected(IcsAdapterView<?> parent) {
            // ...
            
        }
        
//        /*
//         * (non-Javadoc)
//         * @see org.holoeverywhere.widget.AdapterView.OnItemSelectedListener#onItemSelected(org.holoeverywhere.widget.AdapterView, android.view.View, int, long)
//         */
//        @Override
//        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//            String parentPosition = parent.getTag().toString();
//            CampaignItemSize size = (CampaignItemSize) parent.getItemAtPosition(position);
//            //Log.d(TAG, "CAMPAIGN ON ITEM SELECTED: " + size.simpleSku + " " +  position + " " + parentPosition);
//            CampaignItem campaignItem = getItem(Integer.valueOf(parentPosition));
//            campaignItem.setSelectedSizePosition(position);
//            campaignItem.setSelectedSize(size);
//        }
//        
//        /*
//         * (non-Javadoc)
//         * @see org.holoeverywhere.widget.AdapterView.OnItemSelectedListener#onNothingSelected(org.holoeverywhere.widget.AdapterView)
//         */
//        @Override
//        public void onNothingSelected(AdapterView<?> parent) {
//            // ...
//        }

        /*
         * (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View view) {
            // Get the position from tag
            String position = view.getTag().toString();
            // Get the campaign
            CampaignItem item = getItem(Integer.valueOf(position));
            // Get selected size
            CampaignItemSize selectedSize = item.getSelectedSize();
            // Add new tags
            view.setTag(PROD, item.getSku());
            view.setTag(SKU, (selectedSize != null) ? selectedSize.simpleSku : item.getSku());
            view.setTag(SIZE, (selectedSize != null) ? selectedSize.size : "");
            view.setTag(STOCK, item.hasStock());
            view.setTag(NAME, item.getName());
            view.setTag(BRAND, item.getBrand());
            view.setTag(PRICE, item.getPriceForTracking());
            view.setTag(DISCOUNT, item.getMaxSavingPercentage());
            //Log.d(TAG, "CAMPAIGN ON CLICK: " + item.getSku() + " " + selectedSize.simpleSku + " " +  selectedSize.size);
            // Send to listener
            if(mOnClickParentListener != null)
                mOnClickParentListener.onClick(view);
        }


        
    }
    
}
