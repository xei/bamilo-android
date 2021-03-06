package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.helpers.campaign.GetCampaignHelper;
import com.bamilo.android.appmodule.bamiloapp.helpers.cart.ShoppingCartAddItemHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.models.MainEventModel;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.HeaderFooterGridView;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.HeaderFooterInterface;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.DeepLinkManager;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogSimpleListFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.product.UIProductUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.ErrorLayoutFactory;
import com.bamilo.android.appmodule.modernbamilo.tracking.EventTracker;
import com.bamilo.android.framework.components.absspinner.IcsAdapterView;
import com.bamilo.android.framework.components.absspinner.IcsAdapterView.OnItemSelectedListener;
import com.bamilo.android.framework.components.recycler.DividerItemDecoration;
import com.bamilo.android.framework.service.objects.campaign.Campaign;
import com.bamilo.android.framework.service.objects.campaign.CampaignItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.objects.catalog.Banner;
import com.bamilo.android.framework.service.objects.home.TeaserCampaign;
import com.bamilo.android.framework.service.objects.product.pojo.ProductSimple;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.tracking.gtm.GTMValues;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * Class used to show campaign page
 *
 * @author sergiopereira
 */
public class CampaignPageFragment extends BaseFragment implements IResponseCallback,
        DialogSimpleListFragment.OnDialogListListener {

    public static final String TAG = CampaignPageFragment.class.getSimpleName();

    private final static String COUNTER_START_TIME = "start_time";
    private final static String BANNER_STATE = "banner_state";
    private final static String TIMER_ENDED = "00:00:00";

    public int NAME = R.id.name;
    public int BRAND = R.id.brand;
    public int PRICE = R.id.price;
    public int SKU = R.id.product;
    public int SIZE = R.id.size;
    public int STOCK = R.id.stock;
    public int DISCOUNT = R.id.discount;
    private TeaserCampaign mTeaserCampaign;
    private Campaign mCampaign;

    private HeaderFooterGridView mGridView;

    private boolean isAddingProductToCart;

    private long mStartTimeInMilliseconds;

    @BannerVisibility
    private int bannerState;

    public static final int DEFAULT = 0;
    public static final int VISIBLE = 1;
    public static final int HIDDEN = 2;
    private MainEventModel addToCartEventModel;

    @IntDef({DEFAULT, VISIBLE, HIDDEN})
    @Retention(RetentionPolicy.SOURCE)
    @interface BannerVisibility {
    }

    /**
     * Empty constructor
     */
    public CampaignPageFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.campaign_fragment_pager_item);
        bannerState = DEFAULT;
    }

    /**
     * Constructor via bundle
     *
     * @return CampaignFragment
     * @author sergiopereira
     */
    public static CampaignPageFragment getInstance(Bundle bundle) {
        CampaignPageFragment campaignPageFragment = new CampaignPageFragment();
        campaignPageFragment.setArguments(bundle);
        return campaignPageFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        long GABeginRequestMillis = System.currentTimeMillis();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get campaigns from arguments
        mTeaserCampaign = getArguments().getParcelable(TAG);
        // Validate the saved state
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(TAG)) {
                mCampaign = savedInstanceState.getParcelable(TAG);
            }
            // Restore startTime
            if (savedInstanceState.containsKey(COUNTER_START_TIME)) {
                mStartTimeInMilliseconds = savedInstanceState
                        .getLong(COUNTER_START_TIME, SystemClock.elapsedRealtime());
            }
            if (savedInstanceState.containsKey(BANNER_STATE))
            //noinspection ResourceType
            {
                bannerState = savedInstanceState.getInt(BANNER_STATE);
            }
        }

        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(
                getString(TrackingPage.CAMPAIGN_PAGE.getName()), getString(R.string.gaScreen), "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get grid view
        mGridView = view.findViewById(R.id.campaign_grid);
        mGridView.setGridLayoutManager(
                getBaseActivity().getResources().getInteger(R.integer.campaign_num_columns));
        mGridView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        mGridView.addItemDecoration(
                new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mGridView.setHasFixedSize(true);
        // Validate the current state
        getAndShowCampaign();
    }

    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();

        // Product name and image container
        if (id == R.id.image_container || id == R.id.campaign_item_name) {
            onClickProduct(view);
        }
        // Parent view
        else {
            super.onClick(view);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        getAndShowCampaign();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(TAG, mCampaign);
        outState.putLong(COUNTER_START_TIME, mStartTimeInMilliseconds);
        outState.putSerializable(BANNER_STATE, bannerState);
    }

    /**
     * Get and show the campaign
     *
     * @author sergiopereira
     */
    private void getAndShowCampaign() {
        // Get the campaign id
        String id = (mTeaserCampaign != null) ? mTeaserCampaign.getId() : null;
        // Validate the current state
        if (mCampaign == null) {
            triggerGetCampaign(id);
        } else {
            showCampaign();
        }
    }

    /**
     * Show campaign
     *
     * @author sergiopereira
     */
    private void showCampaign() {
        // Get banner and show items
        getBannerView();
    }

    /**
     * Get the banner view to add the header view
     *
     * @author sergiopereira
     */
    private void getBannerView() {
        String url = (getResources().getBoolean(R.bool.isTablet)) ? mCampaign.getTabletBanner()
                : mCampaign.getMobileBanner();
        mGridView.setHeaderView(url);
        bannerState = VISIBLE;
        mGridView.showHeaderView();
        showContent();
    }

    /**
     * Show only the content view
     *
     * @author sergiopereira
     */
    private synchronized void showContent() {
        // Validate the current data
        if (mGridView.getAdapter() == null) {
            // Set adapter
            CampaignAdapter mArrayAdapter = new CampaignAdapter(mCampaign.getItems(),
                    mCampaign.getName(), mIOnProductClick);
            mGridView.setAdapter(mArrayAdapter);
            // Add banner to header
            if (bannerState != HIDDEN) {
                // Load the bitmap
                String url =
                        (getResources().getBoolean(R.bool.isTablet)) ? mCampaign.getTabletBanner()
                                : mCampaign.getMobileBanner();
                mGridView.setHeaderView(url);
                mGridView.showHeaderView();
            }
        }
        // Show content
        mGridView.refreshDrawableState();
        showFragmentContentContainer();
    }


    interface IOnProductClick {
        void onClickAddProduct(View view, CampaignItem campaignItem);
        void onClickOpenProduct(View view);
    }

    final IOnProductClick mIOnProductClick = new IOnProductClick() {
        @Override
        public void onClickAddProduct(View view, CampaignItem campaignItem) {
            onClickBuyButton(campaignItem);
        }

        @Override
        public void onClickOpenProduct(View view) {
            onClickProduct(view);
        }
    };


    /**
     * Show the error view for campaigns
     */
    private void showCampaignUnavailable() {
        showErrorFragment(ErrorLayoutFactory.CAMPAIGN_UNAVAILABLE_LAYOUT,
                v -> getBaseActivity()
                        .onSwitchFragment(FragmentType.HOME, FragmentController.NO_BUNDLE,
                                FragmentController.ADD_TO_BACK_STACK));
    }

    @Override
    public void onDialogListItemSelect(int position) {

    }

    @Override
    public void onDialogListClickView(View view) {

    }

    @Override
    public void onDialogSizeListClickView(int position, CampaignItem item) {
        addItemToCart(item);
    }

    @Override
    public void onDialogListDismiss() {

    }

    /**
     * Process the click on the sendPurchaseRecommend button
     *
     * @author sergiopereira
     */
    private void onClickBuyButton(CampaignItem campaignItem) {
        if (!campaignItem.hasUniqueSize()) {
            showVariantsDialog(campaignItem);
        } else {
            addItemToCart(campaignItem);
        }

    }

    private void addItemToCart(CampaignItem campaignItem) {
        // Validate the current selection
        if (campaignItem.getSelectedSimple() == null) {
            return;
        }
        //
        String sku = campaignItem.getSelectedSimple().getSku();
        String size = campaignItem.getSelectedSimple().getVariationValue();
        Boolean hasStock = campaignItem.hasStock();
        String name = campaignItem.getName();
        String brand = campaignItem.getBrandName();
        double price = campaignItem.getPriceForTracking();
        int discount = campaignItem.getMaxSavingPercentage();
        // Validate the remain stock
        if (!hasStock) {
            showWarningErrorMessage(getString(R.string.campaign_stock_alert));
        }
        // Validate click
        else if (!isAddingProductToCart) {
            // Create values to add to cart
            triggerAddToCart(sku);
            // Tracking
            trackAddToCart(sku, name, brand, price, discount);
        }
    }

    protected void showVariantsDialog(CampaignItem item) {

        try {
            DialogSimpleListFragment dialog = DialogSimpleListFragment.newInstance(
                    getBaseActivity(),
                    getString(R.string.product_variance_choose),
                    item,
                    this);
            dialog.show(getFragmentManager(), null);
        } catch (NullPointerException ignored) {
        }
    }

    /**
     * Track item added to cart
     *
     * @author sergiopereira
     */
    private void trackAddToCart(String sku, String name, String brand, double price,
            double discount) {
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

            addToCartEventModel = new MainEventModel(
                    getString(TrackingPage.CAMPAIGN_PAGE.getName()),
                    EventActionKeys.ADD_TO_CART, sku, (long) price, null);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the click on the item
     *
     * @param view The product button with some tags
     * @author sergiopereira
     */
    private void onClickProduct(View view) {
        String size = (String) view.getTag(SIZE);
        String sku = (String) view.getTag(SKU);
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putString(ConstantsIntentExtra.CONTENT_ID, sku);
        bundle.putString(DeepLinkManager.PDV_SIZE_TAG, size);
        bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gcampaign);
        bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
        bundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
        getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle,
                FragmentController.ADD_TO_BACK_STACK);
    }

    /**
     * Trigger to get the campaign via id
     *
     * @param id The campaign id
     * @author sergiopereira
     */
    private void triggerGetCampaign(String id) {
        // Create request
        triggerContentEvent(new GetCampaignHelper(), GetCampaignHelper.createBundle(id), this);
    }

    /**
     * Trigger to add item to cart
     *
     * @author sergiopereira
     */
    private void triggerAddToCart(String sku) {
        triggerContentEventProgress(new ShoppingCartAddItemHelper(),
                ShoppingCartAddItemHelper.createBundle(sku), this);
    }

    /**
     * Filter the success response
     */
    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Update cart info
        super.handleSuccessEvent(baseResponse);
        // Validate type
        switch (eventType) {
            case GET_CAMPAIGN_EVENT:
                // Get and show campaign
                mCampaign = (Campaign) baseResponse.getContentData();
                /*--
                 * Don't apply Timer if there are no products with remainingTime defined
                 */
                // Set startTime after getting request
                mStartTimeInMilliseconds = SystemClock.elapsedRealtime();
                showCampaign();
                //DROID-10
//                TrackerDelegator.trackScreenLoadTiming(R.string.gaCampaignPage, mGABeginRequestMillis, "");

                // Track screen Timing
                BaseScreenModel screenModel = new BaseScreenModel(
                        getString(TrackingPage.CAMPAIGN.getName()), getString(R.string.gaScreen),
                        mCampaign.getName(), getLoadTime());
                TrackerManager.trackScreenTiming(getContext(), screenModel);

                break;
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                isAddingProductToCart = false;
                hideActivityProgress();
                if (addToCartEventModel != null) {
                    PurchaseEntity cart = BamiloApplication.INSTANCE.getCart();
                    if (cart != null && cart.getTotal() > 0) {
                        addToCartEventModel.customAttributes = MainEventModel
                                .createAddToCartEventModelAttributes(addToCartEventModel.label,
                                        (long) cart.getTotal(), true);
                    } else {
                        addToCartEventModel.customAttributes = MainEventModel
                                .createAddToCartEventModelAttributes(addToCartEventModel.label, 0,
                                        true);
                    }
                    TrackerManager.trackEvent(getContext(), EventConstants.AddToCart,
                            addToCartEventModel);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Filter the error response
     */
    @Override
    public void onRequestError(BaseResponse baseResponse) {
        EventType eventType = baseResponse.getEventType();
        int errorCode = baseResponse.getError().getCode();

        // Validate fragment visibility
        if (isOnStoppingProcess) {
            return;
        }
        // Generic errors
        if (super.handleErrorEvent(baseResponse)) {
            return;
        }
        // Validate type
        switch (eventType) {
            case GET_CAMPAIGN_EVENT:
                // Show campaign not available screen
                showCampaignUnavailable();
                break;
            case ADD_ITEM_TO_SHOPPING_CART_EVENT:
                isAddingProductToCart = false;
                hideActivityProgress();
                break;
            default:
                break;
        }

    }

    /**
     * ########### ADAPTER ###########
     */

    public class CampaignAdapter extends RecyclerView.Adapter<CampaignItemHolder> implements
            OnClickListener, OnItemSelectedListener, HeaderFooterInterface {

        private static final int ITEM_VIEW_TYPE_HEADER = 0;

        static final int ITEM_VIEW_TYPE_LIST = 1;

        private static final int GREEN_PERCENTAGE = 64;

        private static final int ORANGE_PERCENTAGE = 34;

        private static final int HEADER_POSITION = 0;

        private final IOnProductClick mOnClickListener;

        private final ArrayList<CampaignItem> mItems;

        private boolean isToShowHeader = false;

        private String mBannerImage;
        private String mCampaignTitle;

        /**
         * Constructor
         */
        CampaignAdapter(ArrayList<CampaignItem> items, String mCampaignTitle,
                IOnProductClick onClickBuyListener) {
            mItems = items;
            this.mCampaignTitle = mCampaignTitle;
            mOnClickListener = onClickBuyListener;
        }

        public CampaignItem getItem(int position) {
            return mItems.get(position);
        }

        /**
         * Set the campaign data
         *
         * @author sergiopereira
         */
        private void setData(final CampaignItemHolder view, final CampaignItem item,
                final int position) {
            //Log.d(TAG, "SET DATA");
            // Set stock off
            setStockOff(view, item);
            // Set brand
            view.mBrand.setText(item.getBrandName());
            // Set name
            view.mName.setText(item.getName());
            setClickableView(view.mName, position);
            // Set image container
            setClickableView(view.mImageContainer, position);
            // Set image
            //RocketImageLoader.instance.loadImage(item.getImageUrl(), view.mImage, view.progress, R.drawable.no_image_large);
            ImageManager.getInstance().loadImage(item.getImageUrl(), view.mImage, view.progress,
                    R.drawable.no_image_large, false);
            // Set size
            setSizeContainer(view, item);
            // Set price and special price
            setPriceContainer(view, item);
            // Set save value
            setSaveContainer(view, item);
            // Set stock bar
            setStockBar(view.mStockBar, item.getStockPercentage());
            // Set stock percentage
            view.mStockPercentage
                    .setText(getString(R.string.percentage_placeholder, item.getStockPercentage()));
            view.mStockPercentage.setSelected(true);
            // Set sendPurchaseRecommend button
            setClickableView(view.mButtonBuy, position);
            // Set timer
            int remainingTime = item.getRemainingTime();
            // Set itemView's remainingTime to be used by handler
            view.mTimer.setTag(item.getName());
            // update Timer
            updateTimer(view, view.mTimer, view.mTimerContainer, view.mButtonBuy, view.mOfferEnded,
                    view.mName, view.mImage, remainingTime, view.mImageContainer);
        }

        private class CampaignRunnable implements Runnable {

            private final CampaignItemHolder mView;
            private final int mRemainingTime;
            private final String mName;

            CampaignRunnable(final CampaignItemHolder view, final int remainingTime) {
                this.mView = view;
                this.mName = this.mView.mName.getText().toString();
                this.mRemainingTime = remainingTime;
            }

            public void run() {
                if (TextUtils.equalsIgnoreCase(this.mView.mName.getText().toString(), this.mName)) {
                    // update Timer
                    updateTimer(this.mView, this.mView.mTimer, this.mView.mTimerContainer,
                            this.mView.mButtonBuy, this.mView.mOfferEnded, this.mView.mName,
                            this.mView.mImage, this.mRemainingTime, this.mView.mImageContainer);
                }

            }
        }

        /**
         * TODO: Try use a SimpleDateFormat
         *
         * Update Timer with remaining Time or show "Offer Ended" when time remaining reaches 0
         */
        private void updateTimer(final CampaignItemHolder view, final TextView timer,
                final View timerContainer, final View buttonBuy, final View offerEnded,
                final View name, final View image, final int remainingTime,
                final View imageContainer) {
            // start handler processing
            if (remainingTime > 0) {

                timer.postDelayed(new CampaignRunnable(view, remainingTime), 1000);
            }

//            Print.d(TAG, "updateTimer");
            if (remainingTime > 0) {
//                Print.d(TAG, "Product with remainingTime");
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
                    showOfferEnded(timerContainer, buttonBuy, offerEnded, timer, name, image,
                            imageContainer);
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
         * reached the remaining time
         */
        private String getRemainingTime(int remainingTime) {
            long currentTimeInMilliseconds = SystemClock.elapsedRealtime();
            int remainingSeconds = (int) (remainingTime - (
                    (currentTimeInMilliseconds - mStartTimeInMilliseconds) / 1000));
//            Print.d(TAG, "Remaining seconds: " + remainingSeconds);

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
        private void showOfferEnded(View timerContainer, View buttonBuy, View offerEnded,
                TextView timer, View name, View image, View imageContainer) {
            timerContainer.setVisibility(View.VISIBLE);
            buttonBuy.setEnabled(false);
            offerEnded.setVisibility(View.VISIBLE);
            timer.setText(TIMER_ENDED);
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
         *
         * @author sergiopereira
         */
        private void setPriceContainer(CampaignItemHolder view, CampaignItem item) {
            // Set price
            view.mPrice.setSelected(true);
            // Validate special price
            UIProductUtils.setPriceRules(item, view.mPrice, view.mDiscount);
        }

        /**
         * Set the save value
         *
         * @author sergiopereira
         */
        private void setSaveContainer(CampaignItemHolder view, CampaignItem item) {
            if (item.hasDiscount()) {
                String value = CurrencyFormatter.formatCurrency("" + item.getSavePrice());
                view.mSave.setVisibility(View.VISIBLE);
                view.mSaveValue.setVisibility(View.VISIBLE);
                view.mSaveValue.setText(value);
                view.mSave.setSelected(true);
            } else {
                // Set as invisible to occupy the its space
                view.mSave.setVisibility(View.INVISIBLE);
                view.mSaveValue.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * Set a view as clickable saving the position
         *
         * @author sergiopereira
         */
        private void setClickableView(View view, int position) {
            // Save position and add the listener
            view.setTag(position);
            view.setOnClickListener(this);
        }

        /**
         * Hide or show the stock off
         *
         * @author ricardosoares
         */
        private void setStockOff(CampaignItemHolder view, CampaignItem item) {
            if (item.getMaxSavingPercentage() == 0) {
                view.mStockOff.setVisibility(View.GONE);

            } else {
                view.mStockOff.setVisibility(View.VISIBLE);
                view.mStockOff.setText(
                        getString(R.string.percentage_placeholder, item.getMaxSavingPercentage()));
            }
        }

        /**
         * Hide or show the size container
         *
         * @author sergiopereira
         */
        private void setSizeContainer(final CampaignItemHolder view, final CampaignItem item) {
            // Campaign has sizes except itself (>1)
            if (!item.hasUniqueSize() && CollectionUtils.isNotEmpty(item.getSimples())) {
                // Show container
                view.mSizeContainer.setVisibility(View.VISIBLE);
                view.mSizesValue.setText(String.format(getString(R.string.size_placeholder),
                        item.getVariationsAvailable()));

            } else {
                // Hide the size container
                view.mSizeContainer.setVisibility(View.INVISIBLE);
                // Set itself as selected size
                item.setSelectedSimplePosition(0);
            }
        }

        /**
         * Set the stock bar color
         *
         * @author sergiopereira
         */
        private void setStockBar(ProgressBar view, int stock) {
            Rect bounds = view.getProgressDrawable().getBounds();
            // Case GREEN:
            if (stock >= GREEN_PERCENTAGE) {
                view.setProgressDrawable(ContextCompat
                        .getDrawable(view.getContext(), R.drawable.campaign_green_bar));
            }
            // Case YELLOW:
            else if (ORANGE_PERCENTAGE < stock && stock < GREEN_PERCENTAGE) {
                view.setProgressDrawable(ContextCompat
                        .getDrawable(view.getContext(), R.drawable.campaign_yellow_bar));
            }
            // Case ORANGE:
            else {
                view.setProgressDrawable(
                        ContextCompat.getDrawable(view.getContext(), R.drawable.campaign_red_bar));
            }
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
            CampaignItem campaignItem = getItem(Integer.valueOf(parentPosition));
            campaignItem.setSelectedSimplePosition(position);
        }

        @Override
        public void onNothingSelected(IcsAdapterView<?> parent) {
        }

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

            int id = view.getId();
            // Get selected size
            ProductSimple selectedSize = item.getSelectedSimple();
            // Sku
            String sku = TargetLink.getIdFromTargetLink(item.getTarget());
            // Add new tags
            view.setTag(SKU, sku);
            view.setTag(SIZE, (selectedSize != null) ? selectedSize.getVariationValue() : "");
            view.setTag(STOCK, item.hasStock());
            view.setTag(NAME, item.getName());
            view.setTag(BRAND, item.getBrandName());
            view.setTag(PRICE, item.getPriceForTracking());
            view.setTag(DISCOUNT, item.getMaxSavingPercentage());
            if (mOnClickListener != null) {
                // Send to listener
                if (id == R.id.campaign_item_button_buy) {
                    mOnClickListener.onClickAddProduct(view, item);
                } else {
                    mOnClickListener.onClickOpenProduct(view);
                }
            }
        }

        @NonNull
        @Override
        public CampaignItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = R.layout.campaign_fragment_list_item;
            if (viewType == ITEM_VIEW_TYPE_HEADER) {
                layout = R.layout._def_campaign_fragment_header;
            }
            return new CampaignItemHolder(
                    LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final CampaignItemHolder holder, final int position) {
            // Case header
            if (isHeader(position)) {
                setHeader(holder);
                return;
            }
            // get position - 1 to ignore header position.
            setData(holder, mItems.get(position - 1), position - 1);
        }

        @Override
        public int getItemViewType(int position) {
            // Case header
            if (isHeader(position)) {
                return ITEM_VIEW_TYPE_HEADER;
            }
            return ITEM_VIEW_TYPE_LIST;
        }

        /**
         * Validate if the current position is the header view.
         *
         * @param position - the current position
         * @return true or false
         */
        private boolean isHeader(int position) {
            return isToShowHeader && position == HEADER_POSITION;
        }

        @Override
        public int getItemCount() {
            // increment item count to include header.
            return mItems.size() + 1;
        }

        @Override
        public void showHeaderView() {
            isToShowHeader = true;
        }

        @Override
        public void hideHeaderView() {
        }

        @Override
        public void showFooterView() {

        }

        @Override
        public void hideFooterView() {
        }

        @Override
        public void setHeader(@Nullable Banner banner) {
        }

        @Override
        public void setHeader(@Nullable String banner) {
            if (banner == null) {
                hideHeaderView();
            } else {
                mBannerImage = banner;
                showHeaderView();
            }

        }

        private void setHeader(final CampaignItemHolder holder) {
            holder.mCampaignTitle.setText(mCampaignTitle);
            if (!TextUtils.isEmpty(mBannerImage)) {
                // just in order to have a position tag in order to not crash on the onCLick
                holder.itemView.setTag(R.id.position, -1);
                holder.mBannerImageView.setVisibility(View.VISIBLE);

                ImageManager.getInstance()
                        .loadImage(mBannerImage, holder.mBannerImageView, null, -1, false,
                                new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e,
                                            Object model,
                                            Target<Drawable> target,
                                            boolean isFirstResource) {
                                        holder.mBannerImageView.setVisibility(View.GONE);
                                        mGridView.hideHeaderView();
                                        bannerState = HIDDEN;
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model,
                                            Target<Drawable> target,
                                            DataSource dataSource, boolean isFirstResource) {
                                        holder.mBannerImageView.setVisibility(View.VISIBLE);
                                        bannerState = VISIBLE;
                                        return false;
                                    }
                                });
            }
        }
    }

    /**
     * A representation of each item on the list
     */
    class CampaignItemHolder extends RecyclerView.ViewHolder {

        private final TextView mStockOff;
        private final TextView mBrand;
        private final TextView mName;
        private final View mImageContainer;
        private final ImageView mImage;
        private final View progress;
        private final View mSizeContainer;
        private final TextView mSizesValue;
        private final TextView mPrice;
        private final TextView mDiscount;
        private final TextView mSave;
        private final TextView mSaveValue;
        private final ProgressBar mStockBar;
        private final TextView mStockPercentage;
        private final View mButtonBuy;
        private final TextView mOfferEnded;
        private final View mTimerContainer;
        private final TextView mTimer;
        private final AppCompatImageView mBannerImageView;
        private final TextView mCampaignTitle;

        CampaignItemHolder(final View itemView) {
            super(itemView);
            // Get stock off
            mStockOff = itemView.findViewById(R.id.campaign_item_stock_off);
            // Get brand
            mBrand = itemView.findViewById(R.id.campaign_item_brand);
            // Get name
            mName = itemView.findViewById(R.id.campaign_item_name);
            // Get image container
            mImageContainer = itemView.findViewById(R.id.image_container);
            // Get image
            mImage = itemView.findViewById(R.id.image_view);
            // Get Progress
            progress = itemView.findViewById(R.id.campaign_loading_progress);
            // Get size container
            mSizeContainer = itemView.findViewById(R.id.campaign_item_size_container);
            // Get size spinner
            mSizesValue = itemView.findViewById(R.id.campaign_item_size_label);
            // Get price
            mPrice = itemView.findViewById(R.id.campaign_item_price);
            // Get discount
            mDiscount = itemView.findViewById(R.id.campaign_item_discount);
            // Get save
            mSave = itemView.findViewById(R.id.campaign_item_save_label);
            // Get save value
            mSaveValue = itemView.findViewById(R.id.campaign_item_save_value);
            // Get stock bar
            mStockBar = itemView.findViewById(R.id.campaign_item_stock_bar);
            // Get stock %
            mStockPercentage = itemView.findViewById(R.id.campaign_item_stock_value);
            // Get button
            mButtonBuy = itemView.findViewById(R.id.campaign_item_button_buy);
            // Get Offer Ender image
            mOfferEnded = itemView.findViewById(R.id.campaign_item_offer_ended);
            // Get timer container
            mTimerContainer = itemView.findViewById(R.id.campaign_item_stock_timer_container);
            // Get timer
            mTimer = itemView.findViewById(R.id.campaign_item_stock_timer);
            // Get banner
            mBannerImageView = itemView.findViewById(R.id.campaign_header_image);
            // Get campaign title
            mCampaignTitle = itemView.findViewById(R.id.tvCampaignTitle);
        }
    }

}