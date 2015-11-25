package com.mobile.view.fragments;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.IntDef;
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

import com.mobile.components.HeaderGridView;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.controllers.fragments.FragmentType;
import com.mobile.helpers.campaign.GetCampaignHelper;
import com.mobile.helpers.cart.ShoppingCartAddItemHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.campaign.Campaign;
import com.mobile.newFramework.objects.campaign.CampaignItem;
import com.mobile.newFramework.objects.campaign.CampaignItemSize;
import com.mobile.newFramework.objects.home.TeaserCampaign;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.tracking.TrackingPage;
import com.mobile.newFramework.tracking.gtm.GTMValues;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.deeplink.DeepLinkManager;
import com.mobile.utils.dialogfragments.DialogCampaignItemSizeListFragment;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.utils.ui.ProductUtils;
import com.mobile.utils.ui.WarningFactory;
import com.mobile.view.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Class used to show campaign page
 * @author sergiopereira
 */
public class CampaignPageFragment extends BaseFragment implements OnScrollListener, IResponseCallback {

    public static final String TAG = CampaignPageFragment.class.getSimpleName();
    
    private final static String COUNTER_START_TIME = "start_time";

    private final static String BANNER_STATE = "banner_state";
    public int NAME = R.id.name;
    public int BRAND = R.id.brand;
    public int PRICE = R.id.price;
    public int PROD = R.id.product;
    public int SKU = R.id.sku;
    public int SIZE = R.id.size;
    public int STOCK = R.id.stock;
    public int DISCOUNT = R.id.discount;
    private TeaserCampaign mTeaserCampaign;
    private Campaign mCampaign;

    private HeaderGridView mGridView;

    private boolean isAddingProductToCart;

    private DialogGenericFragment mDialogErrorToCart;
    
    private long mStartTimeInMilliseconds;

    private boolean isScrolling;

    @BannerVisibility
    private int bannerState;

    public static final int DEFAULT = 0;
    public static final int VISIBLE = 1;
    public static final int HIDDEN = 2;
    @IntDef({DEFAULT, VISIBLE, HIDDEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BannerVisibility{}

    /**
     * Empty constructor
     */
    public CampaignPageFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.campaign_fragment_pager_item);
        bannerState = DEFAULT;
    }
        
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

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Print.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        // Get campaigns from arguments
        mTeaserCampaign = getArguments().getParcelable(TAG);
        // Validate the saved state
        if(savedInstanceState != null ) {
            Print.i(TAG, "ON GET SAVED STATE");
            if(savedInstanceState.containsKey(TAG))
                mCampaign = savedInstanceState.getParcelable(TAG);
            // Restore startTime
            if (savedInstanceState.containsKey(COUNTER_START_TIME))
                mStartTimeInMilliseconds = savedInstanceState.getLong(COUNTER_START_TIME, SystemClock.elapsedRealtime());
            if (savedInstanceState.containsKey(BANNER_STATE))
                //noinspection ResourceType
                bannerState = savedInstanceState.getInt(BANNER_STATE);
        }
        // Tracking
        TrackerDelegator.trackCampaignView(mTeaserCampaign);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        // Get grid view
        mGridView = (HeaderGridView) view.findViewById(R.id.campaign_grid);
        // Set onScrollListener to signal adapter's Handler when user is scrolling
        mGridView.setOnScrollListener(this);
        // Validate the current state
        getAndShowCampaign();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Print.i(TAG, "ON RESUME");
        isScrolling = false;
        // Track page
        TrackerDelegator.trackPage(TrackingPage.CAMPAIGNS, getLoadTime(), false);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
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
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        Print.i(TAG, "ON DESTROY VIEW");
        super.onDestroyView();

    }

    /*
     * (non-Javadoc)
     * @see com.mobile.view.fragments.BaseFragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Print.i(TAG, "ON DESTROY");
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Buy button
        if (id == R.id.campaign_item_button_buy) {
            onClickBuyButton(view);
        }
        // Product name and image container
        else if (id == R.id.image_container || id == R.id.campaign_item_name) {
            onClickProduct(view);
        }
        // Parent view
        else {
            super.onClick(view);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        Print.i(TAG, "ON CLICK ERROR BUTTON");
        super.onClickRetryButton(view);
        getAndShowCampaign();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onSaveInstanceState(android.os.Bundle)
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE STATE: CAMPAIGN");
        outState.putParcelable(TAG, mCampaign);
        outState.putLong(COUNTER_START_TIME, mStartTimeInMilliseconds);
        outState.putSerializable(BANNER_STATE, bannerState);
    }
    
    /**
     * Get and show the campaign
     * @author sergiopereira
     */
    private void getAndShowCampaign() {
        Print.i(TAG, "VALIDATE CAMPAIGN STATE");
        // Get the campaign id
        String id = (mTeaserCampaign != null) ? mTeaserCampaign.getId() : null;
        // Validate the current state
        if(mCampaign == null) triggerGetCampaign(id);
        else showCampaign();
    }
    
    /**
     * Show campaign
     * @author sergiopereira
     */
    private void showCampaign() {
        Print.i(TAG, "LOAD CAMPAIGN");
//        // Get banner
//        View mBannerView = getBannerView();
//		// Add banner to header
//        if (BannerVisibility.HIDDEN != bannerState) mGridView.addHeaderView(mBannerView);
//        // Validate the current data
//        if (mGridView.getAdapter() == null) {
//            // Set adapter
//            CampaignAdapter mArrayAdapter = new CampaignAdapter(getBaseActivity(), mCampaign.getItems(), this);
//            mGridView.setAdapter(mArrayAdapter);
//        }
//        // Show content
//        if (BannerVisibility.HIDDEN == bannerState) showContent();
//		// else show when is loaded the banner

        // Get banner and show items
        getBannerView();
    }
    
    /**
     * Get the banner view to add the header view
     * @return View
     * @author sergiopereira
     */
    private View getBannerView(){
        // Inflate the banner layout
        final View bannerView = LayoutInflater.from(getActivity()).inflate(R.layout.campaign_fragment_banner, mGridView, false);
        //if (BannerVisibility.HIDDEN != bannerState) {
            // Get the image view
            final ImageView imageView = (ImageView) bannerView.findViewById(R.id.campaign_banner);
            // Load the bitmap
            String url = (getResources().getBoolean(R.bool.isTablet)) ? mCampaign.getTabletBanner() : mCampaign.getMobileBanner();
            RocketImageLoader.instance.loadImage(url, imageView, false, new RocketImageLoader.RocketImageLoaderListener() {

                @Override
                public void onLoadedSuccess(String url, Bitmap bitmap) {
                    // Show content
                    imageView.setImageBitmap(bitmap);
                    bannerState = VISIBLE;
                    showContent(bannerView);
                }

                @Override
                public void onLoadedError() {
                    bannerView.setVisibility(View.GONE);
                    mGridView.removeHeaderView(bannerView);
                    bannerState = HIDDEN;
                    // Show content
                    showContent(bannerView);
                }

                @Override
                public void onLoadedCancel() {
                    bannerView.setVisibility(View.GONE);
                    mGridView.removeHeaderView(bannerView);
                    bannerState = HIDDEN;
                    // Show content
                    showContent(bannerView);
                }
            });
        //}

        // Return the banner
        return bannerView;
    }
    
    /**
     * Show only the content view
     * @author sergiopereira
     */
    private synchronized void showContent(View bannerView) {
        // Validate the current data
        if (mGridView.getAdapter() == null) {
            // Add banner to header
            if (bannerState != HIDDEN) mGridView.addHeaderView(bannerView);
            // Set adapter
            CampaignAdapter mArrayAdapter = new CampaignAdapter(getBaseActivity(), mCampaign.getItems(), this);
            mGridView.setAdapter(mArrayAdapter);
        }
        // Show content
        mGridView.refreshDrawableState();
        showFragmentContentContainer();
    }
    
    /**
     * Show only the retry view
     * @author sergiopereira
     */
    private void showRetry() {
        showFragmentErrorRetry();
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
        isScrolling = scrollState == OnScrollListener.SCROLL_STATE_FLING;
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.AbsListView.OnScrollListener#onScroll(android.widget.AbsListView, int, int, int)
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // ...
    }
    
    /**
     * Process the click on the buy button
     * @param view The buy button with some tags
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
        int discount = (Integer) view.getTag(DISCOUNT);

        Print.i(TAG, "ON CLICK BUY " + sku + " " + size + " " + hasStock);
        // Validate the remain stock
        if(!hasStock)
            getBaseActivity().showWarningMessage(WarningFactory.ERROR_MESSAGE, getString(R.string.campaign_stock_alert));
        // Validate click
        else if(!isAddingProductToCart) {
            // Create values to add to cart
            ContentValues values = new ContentValues();
            values.put(ShoppingCartAddItemHelper.PRODUCT_TAG, prod);
            values.put(ShoppingCartAddItemHelper.PRODUCT_SKU_TAG, sku);
            values.put(ShoppingCartAddItemHelper.PRODUCT_QT_TAG, "1");
            triggerAddToCart(values);
            // Tracking
            trackAddToCart(sku, name, brand, price, discount);
        }
    }
    
    /**
     * Track item added to cart
     * @author sergiopereira
     */
    private void trackAddToCart(String sku, String name, String brand, double price, double discount){
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
            bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
            TrackerDelegator.trackProductAddedToCart(bundle);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Process the click on the item
     * @param view The product button with some tags
     * @author sergiopereira
     */
    private void onClickProduct(View view){
        String prod = (String) view.getTag(PROD);
        String size = (String) view.getTag(SIZE);
        Print.d(TAG, "ON CLICK PRODUCT " + prod + " " + size);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, prod);
        bundle.putString(DeepLinkManager.PDV_SIZE_TAG, size);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcampaign);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
    }
    
    /**
     * Trigger to get the campaign via id
     * @param id The campaign id
     * @author sergiopereira
     */
    private void triggerGetCampaign(String id){
        Print.i(TAG, "TRIGGER TO GET CAMPAIGN: " + id);
        // Create request
        triggerContentEvent(new GetCampaignHelper(), GetCampaignHelper.createBundle(id), this);
    }
    
    /**
     * ############# REQUESTS #############
     */

    /**
     * Trigger to add item to cart
     * @author sergiopereira
     */
    private void triggerAddToCart(ContentValues values){
        Print.i(TAG, "TRIGGER ADD TO CART");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        triggerContentEventProgress(new ShoppingCartAddItemHelper(), bundle, this);
    }

    /**
     * Filter the success response
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        Print.i(TAG, "ON SUCCESS EVENT: " + eventType);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Update cart info
        super.handleSuccessEvent(baseResponse);

        switch (eventType) {
        case GET_CAMPAIGN_EVENT:
            Print.d(TAG, "RECEIVED GET_CAMPAIGN_EVENT");
            // Get and show campaign
            mCampaign = (Campaign)baseResponse.getMetadata().getData();
            /*--
             * Don't apply Timer if there are no products with remainingTime defined
             */
            // Set startTime after getting request
            mStartTimeInMilliseconds = SystemClock.elapsedRealtime();
            showCampaign();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            Print.d(TAG, "RECEIVED ADD_ITEM_TO_SHOPPING_CART_EVENT");
            isAddingProductToCart = false;
            hideActivityProgress();
            showAddToCartCompleteMessage(baseResponse);
            break;
            default:
            break;
        }
    }

    /**
     * ############# RESPONSE #############
     */

    /**
     * Filter the error response
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();
        Print.d(TAG, "ON ERROR EVENT: " + eventType + " " + errorCode);

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        /*
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
                showContinueShopping();
                return true;
            }
        }
        */

        // Generic errors
        if(super.handleErrorEvent(baseResponse)) return;

        switch (eventType) {
        case GET_CAMPAIGN_EVENT:
            Print.d(TAG, "RECEIVED GET_CAMPAIGN_EVENT");
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

    }

    /**
     * ########### DIALOGS ###########
     */

    private void showErrorCartDialog (){
        FragmentManager fm = getFragmentManager();
        mDialogErrorToCart = DialogGenericFragment.newInstance(true, false,
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
    }
    
    /**
     * ########### ADAPTER ###########  
     */    
    
    public class CampaignAdapter extends ArrayAdapter<CampaignItem> implements OnClickListener, OnItemSelectedListener, DialogCampaignItemSizeListFragment.OnDialogListListener {
        
        // private static final int YELLOW_PERCENTAGE = 34 < X < 64
        
        private static final int GREEN_PERCENTAGE = 64;
        
        private static final int ORANGE_PERCENTAGE = 34;

        private final LayoutInflater mInflater;

        private final OnClickListener mOnClickParentListener;

        /**
         * Constructor
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
         */
        private ItemView getItemView(View view){
            ItemView item;
            if (view.getTag() == null) {
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
                item.mSizeSpinner = (Button) view.findViewById(R.id.campaign_item_size_spinner);
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
            RocketImageLoader.instance.loadImage(item.getImageUrl(), view.mImage, view.progress, R.drawable.no_image_large);
            // Set size
            setSizeContainer(view, item, position);
            // Set price and special price
            setPriceContainer(view, item);
            // Set save value
            setSaveContainer(view, item);
            // Set stock bar
            setStockBar(view.mStockBar, item.getStockPercentage());
            // Set stock percentage TODO placeholder
            view.mStockPercentage.setText(item.getStockPercentage() + "%");
            view.mStockPercentage.setSelected(true);
            // Set buy button
            setClickableView(view.mButtonBuy, position);
            // Set timer
            int remainingTime = item.getRemainingTime();
            // Set itemView's remainingTime to be used by handler
            view.mRemainingTime = remainingTime;
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
         */
        private void updateTimer(TextView timer, View timerContainer, View buttonBuy, View offerEnded, View name, View image, int remainingTime, View imageContainer) {
            Print.d(TAG, "updateTimer");
            if (remainingTime > 0) {
                Print.d(TAG, "Product with remainingTime");
                // calculate remaining time relatively to mStartTime
                String remaingTimeString = getRemainingTime(remainingTime);
                // Set remaing time on Timer
                if (remaingTimeString != null) {
                    timer.setText(remaingTimeString);

                    timerContainer.setVisibility(View.VISIBLE);
                    buttonBuy.setEnabled(true);
                    offerEnded.setVisibility(View.INVISIBLE);

                    // Set full opacity to image
                    image.setAlpha(1F);
                // show "Offer Ended" and disable product
                } else {
                    Print.d(TAG, "Product expired!");
                    showOfferEnded(timerContainer, buttonBuy, offerEnded, timer, name, image, imageContainer);
                }
            // show product normally without timers
            } else {
                timerContainer.setVisibility(View.INVISIBLE);
                buttonBuy.setEnabled(true);
                offerEnded.setVisibility(View.INVISIBLE);

                // Set full opacity to image
                image.setAlpha(1F);
            }
        }

        /**
         * calculate remainingTime based on <code>mStartTimeInMilliseconds</code>(time of the API
         * request) and return it with the format "hh:mm:ss"
         *
         * @return <code>String</code> with remaining time properly formatted or null if product
         *         reached the remaining time
         */
        private String getRemainingTime(int remainingTime) {
            long currentTimeInMilliseconds = SystemClock.elapsedRealtime();
            int remainingSeconds = (int) (remainingTime - ((currentTimeInMilliseconds - mStartTimeInMilliseconds) / 1000));
            Print.d(TAG, "Remaining seconds: " + remainingSeconds);

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
            image.setAlpha(0.5F);
        }

        /**
         * Set the price and special price view
         * @author sergiopereira
         */
        private void setPriceContainer(ItemView view, CampaignItem item){
            // Set price
            view.mPrice.setSelected(true);
            // Validate special price
            ProductUtils.setPriceRules(item, view.mPrice, view.mDiscount);
        }

        /**
         * Set the save value
         * @author sergiopereira
         */
        private void setSaveContainer(ItemView view, CampaignItem item){
            if(item.hasDiscount()){
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
         * @author sergiopereira
         */
        private void setClickableView(View view, int position) {
            // Save position and add the listener
            view.setTag(position);
            view.setOnClickListener(this);
        }

        /**
         * Hide or show the stock off
         * @author ricardosoares
         */
        private void setStockOff(ItemView view, CampaignItem item){
            if(item.getMaxSavingPercentage() == 0){
                view.mStockOff.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                params.setMargins(0, 0, 0, 0);
                view.mName.setLayoutParams(params);

            }else{
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
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
         * @author sergiopereira
         */
        private void setSizeContainer(final ItemView view, final CampaignItem item, int position){
            // Campaign has sizes except itself (>1)
            if(!item.hasUniqueSize() && item.hasSizes()) {
                // Show container
                view.mSizeContainer.setVisibility(View.VISIBLE);
                // Get sizes
                ArrayList<CampaignItemSize> sizes = item.getSizes();
                // Create an ArrayAdapter using the sizes values
                ArrayAdapter<CampaignItemSize> adapter = new ArrayAdapter<>(getContext(), R.layout.campaign_spinner_item, sizes);
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(R.layout.campaign_spinner_dropdown_item);
                // Apply the adapter to the spinner
//                view.mSizeSpinner.setAdapter(adapter);

                // Checks if product has only one size to select (S, M, L - only available L)
                if(sizes.size() == 1){
//                    ViewGroup.LayoutParams lp = view.mSizeSpinner.getLayoutParams();
//                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    view.mSizeSpinner.setEnabled(false);
                } else {
//                    ViewGroup.LayoutParams lp = view.mSizeSpinner.getLayoutParams();
//                    lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    view.mSizeSpinner.setEnabled(true);
                }

                // Save position in spinner
                view.mSizeSpinner.setTag(position);
                // Check pre selection
                if(item.hasSelectedSize()) {
                    view.mSizeSpinner.setText(item.getSizes().get(item.getSelectedSizePosition()).size);
                }
                // Force reload content to redraw the default selection value
                adapter.notifyDataSetChanged();
                // Apply the select listener
                view.mSizeSpinner.setOnClickListener(this);

            } else {
                // Hide the size container
                view.mSizeContainer.setVisibility(View.GONE);
                // Set itself as selected size
                CampaignItemSize size = null;
                try {
                    size = item.getSizes().get(0);
                } catch (IndexOutOfBoundsException e) {
                    Print.w(TAG, "WARNING: IOBE ON SET SIZE SELECTION: 0");
                } catch (NullPointerException e) {
                    Print.w(TAG, "WARNING: NPE ON SET SELECTED SIZE: 0");
                }
                item.setSelectedSizePosition(0);
                item.setSelectedSize(size);
            }
        }

        /**
         * Set the stock bar color
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
//            this.notifyDataSetChanged();
            Print.d(TAG, "selected simple");
        }

        @Override
        public void onNothingSelected(IcsAdapterView<?> parent) {
            // ...

        }

        protected void showVariantsDialog(CampaignItem item) {

            try {
                DialogCampaignItemSizeListFragment dialog = DialogCampaignItemSizeListFragment.newInstance(
                        getBaseActivity(),
                        getString(R.string.product_variance_choose),
                        item,
                        this);
                dialog.show(getFragmentManager(), null);
            } catch (NullPointerException e) {
                Print.w(TAG, "WARNING: NPE ON SHOW VARIATIONS DIALOG");
            }
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

        @Override
        public void onDialogListItemSelect(int position) {
            notifyDataSetChanged();
        }        /*
         * (non-Javadoc)
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View view) {
            // Get the position from tag
            String position = view.getTag().toString();
            // Get the campaign
            CampaignItem item = getItem(Integer.valueOf(position));

            int id = view.getId();
            if(id == R.id.campaign_item_size_spinner){
                showVariantsDialog(item);
            } else {

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
                if (mOnClickParentListener != null)
                    mOnClickParentListener.onClick(view);
            }
        }

        @Override
        public void onDialogListClickView(View view) {

        }

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
            private Button mSizeSpinner;
            private TextView mPrice;
            private TextView mDiscount;
            private TextView mSave;
            private ProgressBar mStockBar;
            private TextView mStockPercentage;
            private View mButtonBuy;
            private TextView mOfferEnded;
            private View mTimerContainer;
            private TextView mTimer;
            private int mRemainingTime;

            /**
             * Handler used to update Timer every second, when user is not scrolling
             */
            private final Handler mHandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    // only update if is not detected a fling (fast scrolling) on gridview
                    if (!isScrolling) {
                        updateTimer(mTimer, mTimerContainer, mButtonBuy, mOfferEnded, mName, mImage, mRemainingTime, mImageContainer);
                    }
                    this.sendEmptyMessageDelayed(0, 1000);
                }
            };
        }


    }
    
}
