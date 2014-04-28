/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;

import org.holoeverywhere.widget.TextView;

import com.actionbarsherlock.internal.widget.IcsSpinner;
import com.androidquery.AQuery;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.components.HeaderGridView;
import pt.rocket.framework.objects.Campaign;
import pt.rocket.framework.objects.CampaignItem;
import pt.rocket.framework.objects.TeaserCampaign;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.campaign.GetCampaignHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.view.R;
import pt.rocket.view.R.drawable;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class CampaignFragment extends BaseFragment implements OnClickListener, IResponseCallback {

    private static final String TAG = LogTagHelper.create(CampaignFragment.class);
    
    private static CampaignFragment sCampaignFragment;

    private TeaserCampaign mTeaserCampaign;
    
    /**
     * 
     * @return
     */
    public static CampaignFragment getInstance(TeaserCampaign teaserCampaign) {
        sCampaignFragment = new CampaignFragment();
        sCampaignFragment.mTeaserCampaign = teaserCampaign;
        return sCampaignFragment;
    }

    private Campaign mCampaign;

    private HeaderGridView mGridView;

    private CampaignAdapter mArrayAdapter;

    private View mBannerView;

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
                
        // Get campaign from extras
        //String id = "deals-of-the-day";// "http://www.jumia.com.ng/mobapi/v1.0/campaign/get/?campaign_slug=deals-of-the-day";
        
        Log.d(TAG, "TEASER CAMPAIGN: " + mTeaserCampaign.getTargetTitle() + " " + mTeaserCampaign.getTargetUrl());
        
        String id = mTeaserCampaign.getTargetUrl();
        
        mGridView = (HeaderGridView) view.findViewById(R.id.campaign_grid);
        
        if(mCampaign == null)
            triggerGetCampaign(id);
        else
            loadCampaign();         
            
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
     * Load the dynamic form
     * @param form
     */
    private void loadCampaign() {
        Log.i(TAG, "LOAD CAMPAIGN");

        // Set banner        
        mBannerView = LayoutInflater.from(getActivity()).inflate(R.layout.campaign_fragment_banner, mGridView, false);
        
        ImageView imageView = (ImageView) mBannerView.findViewById(R.id.campaign_banner);
        //String imageUrl = "http://static.jumia.com.ng/cms/images/mobile_site/Mobile_dealofday_18022014_480x330.jpg";
        String imageUrl = mCampaign.getBanner();
        
        //RocketImageLoader.instance.loadImage(imageUrl, imageView);
        new AQuery(getBaseActivity()).id(imageView).image(imageUrl);
        
        //if(mGridView.getHeaderViewCount() == 0)
        mGridView.addHeaderView(mBannerView);
        
        mArrayAdapter = (CampaignAdapter) mGridView.getAdapter();
        if(mArrayAdapter == null){
            // Set adapter
            mArrayAdapter = new CampaignAdapter(getActivity(), mCampaign.getItems());
            mGridView.setAdapter(mArrayAdapter);
        }

        // 
        // Show
        //getBaseActivity().showContentContainer();
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
        // Next button
        if(id == R.id.checkout_poll_button_enter) onClickPollAnswerButton();
        // Unknown view
        else Log.i(TAG, "ON CLICK: UNKNOWN VIEW");
    }
    
    /**
     * Process the click on the next step button
     */
    private void onClickPollAnswerButton() {
        Log.i(TAG, "ON CLICK: POLL ANSWER");
    }
    
    /**
     * ############# REQUESTS #############
     */
    /**
     * Trigger to get the address form
     * @param id 
     */
    private void triggerGetCampaign(String id){
        Log.i(TAG, "TRIGGER TO GET CAMPAIGN: " + id);
        Bundle bundle = new Bundle();
        bundle.putString(GetCampaignHelper.CAMPAIGN_ID, id);
        sendRequest(new GetCampaignHelper(), bundle, this);
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
            // Get campaign
            mCampaign = (Campaign) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
            // Show campaign
            loadCampaign();
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
        
        // Generic error
        if (getBaseActivity() != null && getBaseActivity().handleErrorEvent(bundle)) {
            Log.d(TAG, "BASE ACTIVITY HANDLE ERROR EVENT");
            return true;
        }
        
        getBaseActivity().showContentContainer();
        
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "ON ERROR EVENT: " + eventType.toString() + " " + errorCode);
        
        switch (eventType) {
        case GET_CAMPAIGN_EVENT:
            Log.d(TAG, "RECEIVED GET_CAMPAIGN_EVENT");
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

    
    
    public class CampaignAdapter extends ArrayAdapter<CampaignItem> {
        
        /**
         * A representation of each item on the list
         */
        private class Item {
            private TextView mStockOff;
            private TextView mName;
            private ImageView mImage;
            private View mSizeContainer;
            private IcsSpinner mSizeSpinner;
            private TextView mPrice;
            private TextView mDiscount;
            private TextView mSave;
            private ProgressBar mStockBar;
            private TextView mStockPercentage;
            private View mButtonBuy;
        }
        
        
        private LayoutInflater mInflater;

        public CampaignAdapter(Context context, ArrayList<CampaignItem> items) {
            super(context, R.layout.campaign_fragment_list_item, items);
            mInflater = LayoutInflater.from(context);
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
            Item itemView = getItem(view);
            // Get current campaign item
            CampaignItem item = getItem(position);
            // Set data
            setData(itemView, item);
            // Return the filled view
            return view;
        }
        
        
        private void setData(Item view, CampaignItem item){
            Log.d(TAG, "SET DATA");
            view.mStockOff.setText(item.getMaxSavingPercentage() + "%\nOFF");
            view.mName.setText(item.getName());
            //RocketImageLoader.instance.loadImage(item.getImage(), view.mImage);
            new AQuery(getBaseActivity()).id(view.mImage).image(item.getImage());
            //view.sizeContainer;
            //view.sizeSpinner;
            view.mPrice.setText(CurrencyFormatter.formatCurrency(""+item.getPrice()));
            view.mPrice.setPaintFlags(view.mPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            view.mDiscount.setText(CurrencyFormatter.formatCurrency(""+item.getSpecialPrice()));
            view.mSave.setText(CurrencyFormatter.formatCurrency(""+item.getSavePrice()));
            setStockBar(view.mStockBar, item.getStockPercentage());
            view.mStockPercentage.setText(item.getStockPercentage() + "%");
            //view.buttonBuy;
        }
        
        
        private void setSizeContainer(){
            // TODO
        }
        
        
        // private static final int YELLOW_PERCENTAGE = 34 < X < 64
        private static final int GREEN_PERCENTAGE = 64;
        private static final int ORANGE_PERCENTAGE = 34;
        
        
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
        
        private Item getItem(View view){
            Item item;
            if ((Item) view.getTag() == null) {
                item = new Item();
                // Get stock off
                item.mStockOff = (TextView) view.findViewById(R.id.campaign_item_stock_off);
                // Get name
                item.mName = (TextView) view.findViewById(R.id.campaign_item_name);
                // Get image
                item.mImage = (ImageView) view.findViewById(R.id.image_view);
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
                // Stores the item representation on the tag of the view for later retrieval
                view.setTag(item);
            } else {
                item = (Item) view.getTag();
            }
            return item;
        }
        
    }
    
    
}
