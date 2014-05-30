/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;

import org.holoeverywhere.widget.AdapterView;
import org.holoeverywhere.widget.AdapterView.OnItemSelectedListener;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.components.HeaderGridView;
import pt.rocket.framework.objects.Campaign;
import pt.rocket.framework.objects.CampaignItem;
import pt.rocket.framework.objects.CampaignItem.CampaignItemSize;
import pt.rocket.framework.objects.TeaserCampaign;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetSearchProductHelper;
import pt.rocket.helpers.GetShoppingCartAddItemHelper;
import pt.rocket.helpers.campaign.GetCampaignHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.DeepLinkManager;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidquery.AQuery;

import de.akquinet.android.androlog.Log;

/**
 * Class used to show campaign page
 * @author sergiopereira
 */
public class CampaignFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    public static final String TAG = LogTagHelper.create(CampaignFragment.class);
    
    private static CampaignFragment sCampaignFragment;

    private TeaserCampaign mTeaserCampaign;
    
    public int PROD = R.id.product;
    
    public int SKU = R.id.sku;
    
    public int SIZE = R.id.size;
    
    public int STOCK = R.id.stock;
    
    private Campaign mCampaign;

    private HeaderGridView mGridView;

    private CampaignAdapter mArrayAdapter;

    private View mBannerView;

    private DialogGenericFragment mDialogAddedToCart;

    private boolean isAddingProductToCart;

    private View mLoadingView;

    private View mRetryView;

    private DialogGenericFragment mDialogErrorToCart;
    
    
    /**
     * Constructor via object
     * @return CampaignFragment
     * @author sergiopereira
     */
    public static CampaignFragment getInstance(TeaserCampaign teaserCampaign) {
        sCampaignFragment = new CampaignFragment();
        sCampaignFragment.mTeaserCampaign = teaserCampaign;
        return sCampaignFragment;
    }
    
    /**
     * Constructor via bundle
     * @return CampaignFragment
     * @author sergiopereira
     */
    public static CampaignFragment getInstance(Bundle bundle) {
        sCampaignFragment = new CampaignFragment();
        sCampaignFragment.setArguments(bundle);
        return sCampaignFragment;
    }

    /**
     * Empty constructor
     */
    public CampaignFragment() {
        super(IS_NESTED_FRAGMENT);
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
        if(savedInstanceState != null && savedInstanceState.containsKey(TAG)){
            Log.i(TAG, "ON GET SAVED STATE");
            mCampaign = savedInstanceState.getParcelable(TAG);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(inflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return inflater.inflate(R.layout.campaign_fragment_pager_item, viewGroup, false);
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
        // Get loading view
        mLoadingView = view.findViewById(R.id.loading_bar);
        // Get retry view
        mRetryView = view.findViewById(R.id.campaign_retry);
        // Get the retry button
        view.findViewById(R.id.campaign_retry_button).setOnClickListener(this);
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
        if(mCampaign == null)
            triggerGetCampaign(id);
        else
            showCampaign();
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
        mGridView.addHeaderView(mBannerView);
        // Validate the current data
        mArrayAdapter = (CampaignAdapter) mGridView.getAdapter();
        if(mArrayAdapter == null){
            // Set adapter
            mArrayAdapter = new CampaignAdapter(getBaseActivity(), mCampaign.getItems(), (OnClickListener) this);
            mGridView.setAdapter(mArrayAdapter);
        }
        // Show content
        showContent();
    }
    
    /**
     * Get the banner view to add the header view
     * @return View
     * @author sergiopereira
     */
    private View getBannerView(){
        // Inflate the banner layout
        View bannerView = LayoutInflater.from(getActivity()).inflate(R.layout.campaign_fragment_banner, mGridView, false);
        // Get the image view
        ImageView imageView = (ImageView) bannerView.findViewById(R.id.campaign_banner);
        // Load the bitmap
        String url = (getResources().getBoolean(R.bool.isTablet)) ? mCampaign.getTabletBanner() : mCampaign.getMobileBanner();
        new AQuery(getBaseActivity()).id(imageView).image(url);
        // Return the banner
        return bannerView;
    }
    
    /**
     * Show only the loading view
     * @author sergiopereira
     */
    private void showLoading(){
        mGridView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
        mRetryView.setVisibility(View.GONE);
    }
    
    /**
     * Show only the content view
     * @author sergiopereira
     */
    private void showContent() {
        mGridView.setVisibility(View.VISIBLE);
        mLoadingView.setVisibility(View.GONE);
        mRetryView.setVisibility(View.GONE);
    }
    
    /**
     * Show only the retry view
     * @author sergiopereira
     */
    private void showRetry() {
        mGridView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mRetryView.setVisibility(View.VISIBLE);
    }
    
    /**
     * ############# CLICK LISTENER #############
     */
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Buy button
        if(id == R.id.campaign_item_button_buy) onClickBuyButton(view);
        // Product name and image
        else if (id == R.id.image_view || id == R.id.campaign_item_name) onClickProduct(view);
        // Retry button
        else if(id == R.id.campaign_retry_button) onClickRetryButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on the retry button
     * @author sergiopereira
     */
    private void onClickRetryButton(){
        getAndShowCampaign();
    }
    
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
        Log.i(TAG, "TRIGGER TO GET CAMPAIGN: " + id);
        // Show loading 
        showLoading();
        // Create request
        Bundle bundle = new Bundle();
        bundle.putString(GetCampaignHelper.CAMPAIGN_ID, id);
        triggerContentEventWithNoLoading(new GetCampaignHelper(), bundle, this);
    }
    
    /**
     * Trigger to add item to cart
     * @param values
     * @author sergiopereira
     */
    private void triggerAddToCart(ContentValues values){
        Log.i(TAG, "TRIGGER ADD TO CART");
        getBaseActivity().showProgress();
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);
        triggerContentEventWithNoLoading(new GetShoppingCartAddItemHelper(), bundle, this);
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
        Log.i(TAG, "ON SUCCESS EVENT");
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.i(TAG, "ON SUCCESS EVENT: " + eventType);
        
        switch (eventType) {
        case GET_CAMPAIGN_EVENT:
            Log.d(TAG, "RECEIVED GET_CAMPAIGN_EVENT");
            // Get and show campaign
            mCampaign = (Campaign) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            showCampaign();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            Log.d(TAG, "RECEIVED ADD_ITEM_TO_SHOPPING_CART_EVENT");
            isAddingProductToCart = false;
            getBaseActivity().updateCartInfo();
            getBaseActivity().dismissProgress();
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
        
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case GET_CAMPAIGN_EVENT:
            Log.d(TAG, "RECEIVED GET_CAMPAIGN_EVENT");
            // Show retry
            showRetry();
            break;
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            getBaseActivity().dismissProgress();
            showErrorCartDialog();
            break;
        default:
            break;
        }
        
        return false;
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
        
        public int PROD = R.id.product;
        
        public int SKU = R.id.sku;
        
        public int SIZE = R.id.size;
        
        public int STOCK = R.id.stock;
        
        private LayoutInflater mInflater;
        
        private OnClickListener mOnClickParentListener;
        
        /**
         * A representation of each item on the list
         */
        private class ItemView {
            private TextView mStockOff;
            private TextView mName;
            private ImageView mImage;
            private View mSizeContainer;
            private Spinner mSizeSpinner;
            private TextView mPrice;
            private TextView mDiscount;
            private TextView mSave;
            private ProgressBar mStockBar;
            private TextView mStockPercentage;
            private View mButtonBuy;
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
                // Get image
                item.mImage = (ImageView) view.findViewById(R.id.image_view);
                // Get size container
                item.mSizeContainer = view.findViewById(R.id.campaign_item_size_container);
                // Get size spinner
                item.mSizeSpinner = (Spinner) view.findViewById(R.id.campaign_item_size_spinner);
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
        private void setData(ItemView view, CampaignItem item, int position){
            //Log.d(TAG, "SET DATA");
            // Set stock off
            if(getString(R.string.off_label).equals("-"))
                view.mStockOff.setText(getString(R.string.off_label) + item.getMaxSavingPercentage() + "%");
            else
                view.mStockOff.setText(item.getMaxSavingPercentage() + "%\n" + getString(R.string.off_label));
            // Set name
            view.mName.setText(item.getName());
            setClickableView(view.mName, position);
            // Set image
            new AQuery(getBaseActivity()).id(view.mImage).image(item.getImage());
            setClickableView(view.mImage, position);
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
        }
        
        /**
         * Set the price and special price view
         * @param view
         * @param item
         * @author sergiopereira
         */
        private void setPriceContainer(ItemView view, CampaignItem item){
            // Set price
            view.mPrice.setText(CurrencyFormatter.formatCurrency(""+item.getPrice()));
            view.mPrice.setPaintFlags(view.mPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            view.mPrice.setSelected(true);
            // Validate special price
            if(item.getSpecialPrice() != 0) {
                // Set discount
                view.mDiscount.setText(CurrencyFormatter.formatCurrency(""+item.getSpecialPrice()));
            } else {
                // Set discount
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
            String label = getString(R.string.campaign_save);
            String value = CurrencyFormatter.formatCurrency( "" + item.getSavePrice());
            String mainText = label + " " + value;
            SpannableString greenValue = new SpannableString(mainText);
            greenValue.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.grey_middle)), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            greenValue.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.green_campaign_bar)), label.length() + 1, mainText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            view.mSave.setText(greenValue);
            view.mSave.setSelected(true);
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
                if(item.hasSelectedSize())
                    view.mSizeSpinner.setSelection(item.getSelectedSizePosition());
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
        /*
         * (non-Javadoc)
         * @see org.holoeverywhere.widget.AdapterView.OnItemSelectedListener#onItemSelected(org.holoeverywhere.widget.AdapterView, android.view.View, int, long)
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String parentPosition = parent.getTag().toString();
            CampaignItemSize size = (CampaignItemSize) parent.getItemAtPosition(position);
            //Log.d(TAG, "CAMPAIGN ON ITEM SELECTED: " + size.simpleSku + " " +  position + " " + parentPosition);
            CampaignItem campaignItem = getItem(Integer.valueOf(parentPosition));
            campaignItem.setSelectedSizePosition(position);
            campaignItem.setSelectedSize(size);
        }
        
        /*
         * (non-Javadoc)
         * @see org.holoeverywhere.widget.AdapterView.OnItemSelectedListener#onNothingSelected(org.holoeverywhere.widget.AdapterView)
         */
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // ...
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
            // Get selected size
            CampaignItemSize selectedSize = item.getSelectedSize();
            // Add new tags
            view.setTag(PROD, item.getSku());
            view.setTag(SKU, (selectedSize != null) ? selectedSize.simpleSku : item.getSku());
            view.setTag(SIZE, (selectedSize != null) ? selectedSize.size : "");
            view.setTag(STOCK, item.hasStock());
            //Log.d(TAG, "CAMPAIGN ON CLICK: " + item.getSku() + " " + selectedSize.simpleSku + " " +  selectedSize.size);
            // Send to listener
            if(mOnClickParentListener != null)
                mOnClickParentListener.onClick(view);
        }
        
    }
    
}
