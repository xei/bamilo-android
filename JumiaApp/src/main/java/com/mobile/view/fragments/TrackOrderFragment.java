package com.mobile.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.mobile.app.JumiaApplication;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.components.loading.LoadingBarView;
import com.mobile.constants.ConstantsCheckout;
import com.mobile.helpers.checkout.GetTrackOrderHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.orders.OrderTracker;
import com.mobile.newFramework.objects.orders.OrderTrackerItem;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import java.util.ArrayList;

/**
 * @author Manuel Silva
 * 
 */
public class TrackOrderFragment extends BaseFragment implements IResponseCallback {

    private static final String TAG = TrackOrderFragment.class.getSimpleName();

    private LoadingBarView loadingTrackBarView;

    private EditText mEditText;

    private OrderTracker mOrderTracker;
    
    private boolean mOrderTrackerError = false;
    
    private static String order_number = "";
    
    private String instanceOrder = "";
    
    Editable text ;

    /**
     * Get instance
     */
    public static TrackOrderFragment getInstance(Bundle bundle) {
        TrackOrderFragment fragment = new TrackOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * Empty constructor
     */
    public TrackOrderFragment() {
        super(IS_NESTED_FRAGMENT, R.layout.track_order_fragment);
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
        // Get arguments
        Bundle arguments = getArguments();
        if(arguments != null) {
            order_number = arguments.getString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR);
        }

        // Get saved arguments
        if(savedInstanceState != null && savedInstanceState.containsKey("track")){
            if(TextUtils.isEmpty(order_number)){
                mOrderTracker = savedInstanceState.getParcelable("track");
                instanceOrder = savedInstanceState.getString("order_num");
                Print.i(TAG, "onCreate mOrderTracker: " + mOrderTracker.getId());
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
//        if(parentFragment != null)
//            MyOrdersFragment.mPositionToStart = 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
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
        triggerGetTrackOrder(order_number);
     //   setupView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Print.i(TAG, "ON PAUSE");
        if(mEditText != null){
            text = mEditText.getText();
            instanceOrder = text.toString();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.view.fragments.MyFragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Print.i(TAG, "ON STOP");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onDestroyView()
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Print.i(TAG, "ON DESTROY");
    }

    private void setupView() {
        
        mEditText = (EditText) getView().findViewById(R.id.order_nr);
        mEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && getBaseActivity() != null)
                    getBaseActivity().hideKeyboard();
                
            }
        });
        Button mButton = (Button) getView().findViewById(R.id.btn_track_order);
        mButton.setOnClickListener(trackOrderClickListener);
        if (!TextUtils.isEmpty(order_number)) {
            mEditText.setText(order_number);
            if(mOrderTracker != null && !mOrderTracker.getId().equalsIgnoreCase(order_number)){
                mOrderTracker = null;
                mOrderTrackerError = false;
            }
        } else if(!TextUtils.isEmpty(instanceOrder)){
            mEditText.setText(instanceOrder);
        }
        text = mEditText.getText();
        if (text != null && text.length() > 0 && mOrderTracker != null) {
            processSuccess();
        } 
        else if (text != null && text.length() > 0 && mOrderTrackerError) {
            if (TextUtils.isEmpty(order_number)){
                processError();
            } else {
                if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
                    showStatusContainer();
                    setTipVisibility(true);
                }
            }
        } else {
            if (DeviceInfoHelper.isTabletInLandscape(getBaseActivity())){
                showStatusContainer();
                setTipVisibility(true);   
            }
        }
    }

    OnClickListener trackOrderClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            onClickTrackOrder();
        }
    };

    private void onClickTrackOrder(){
        getBaseActivity().hideKeyboard();
        String orderNumber = mEditText.getText().toString();
        if (TextUtils.isNotEmpty(orderNumber)) {
            // set status container visible from this point on
            showStatusContainer();
            setTipVisibility(false);
            order_number = "";
            showLoading();
            Bundle args = new Bundle();
            args.putString(GetTrackOrderHelper.ORDER_NR, orderNumber);
            args.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.SMALL_TASK);
            JumiaApplication.INSTANCE.sendRequest(new GetTrackOrderHelper(), args, new IResponseCallback() {
                @Override
                public void onRequestError(BaseResponse baseResponse) {
                    onErrorEvent(baseResponse);
                }
                
                @Override
                public void onRequestComplete(BaseResponse baseResponse) {
                    onSuccessEvent(baseResponse);
                }
            });
        } else {
            showTip();
        }
    }
    
    private void showTip(){
        showStatusContainer();
        if(mOrderTracker == null)
            setTipVisibility(true);
    }
    
    private void setTipVisibility(boolean isToShow) {
        if (!isToShow) {
            getView().findViewById(R.id.tip_tracking_order).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.tip_tracking_order).setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onClickRetryButton(View view) {
        super.onClickRetryButton(view);
        showFragmentContentContainer();
    }

    /**
     * 
     */
    private void showStatusContainer() {
        getView().findViewById(R.id.track_order_status_container).setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        getView().findViewById(R.id.track_order_status_container).setVisibility(View.VISIBLE);

        getView().findViewById(R.id.order_status_container).setVisibility(View.GONE);
        // HEADER CONTAINER getView().findViewById(R.id.title_status_container).setVisibility(View.GONE);
        getView().findViewById(R.id.error_tracking_order_container).setVisibility(View.GONE);
        getView().findViewById(R.id.loading_status).setVisibility(View.VISIBLE);
        loadingTrackBarView = (LoadingBarView) getView().findViewById(R.id.fragment_root_loading_gif);
        if (loadingTrackBarView != null) {
            loadingTrackBarView.startRendering();
        }
        ((Button) getView().findViewById(R.id.btn_track_order)).setText(R.string.track_order);
        // ((TextView) getView().findViewById(R.id.title_text)).setText(getString(R.string.track_your_order));
    }

    private void inflateItemsList(ArrayList<OrderTrackerItem> items) {
        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mLinearLayout = (LinearLayout) getView().findViewById(R.id.products_items_container);
        try {
            mLinearLayout.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        int numberItems = items.size();
        for (int i = 0; i < numberItems; i++) {
            OrderTrackerItem orderTrackerItem = items.get(i);
            LinearLayout view = (LinearLayout) mInflater.inflate(R.layout.track_order_item, null, false);
            TextView orderItemName = (TextView) view.findViewById(R.id.order_item_name);
            orderItemName.setText(orderTrackerItem.getName());
            orderItemName.setSelected(true);
            ((TextView) view.findViewById(R.id.order_item_quantity)).setText(getString(R.string.shoppingcart_quantity) + ": " + orderTrackerItem.getQuantity());
            if(!TextUtils.isEmpty(orderTrackerItem.getStatus())) {
                TextView orderStatusTextView = ((TextView) view.findViewById(R.id.order_status_text));
                orderStatusTextView.setVisibility(View.VISIBLE);
                orderStatusTextView.setText(orderTrackerItem.getStatus());
            }

            // REMOVED ON NAFAMZ-7271
            // ((TextView) view.findViewById(R.id.order_last_update)).setText(orderTrackerItem.getUpdateDate());

            // add divider to top of item after first item
//            if (i > 0) {
//                view.findViewById(R.id.order_item_divider).setVisibility(View.VISIBLE);
//            }
            mLinearLayout.addView(view);
        }
    }

    private void processSuccess() {
        showStatusContainer();
        setTipVisibility(false);
        ((TextView) getView().findViewById(R.id.title_status_text)).setText("# " + mOrderTracker.getId());
        ((TextView) getView().findViewById(R.id.order_creation_date_text)).setText(mOrderTracker.getDate());
        ((TextView) getView().findViewById(R.id.order_payment_method_title)).setText(TextUtils.placeHolderText(getString(R.string.payment_method), mOrderTracker.getPaymentMethod()));

        inflateItemsList(mOrderTracker.getOrderTrackerItems());
        if (loadingTrackBarView != null) {
            loadingTrackBarView.stopRendering();
        }
        getView().findViewById(R.id.loading_status).setVisibility(View.GONE);
        // HEADER CONTAINER getView().findViewById(R.id.title_status_container).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.order_status_container).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.error_tracking_order_container).setVisibility(View.GONE);
    }

    private void processError() {
        Print.e(TAG, "processError");
        showStatusContainer();
        setTipVisibility(false);
        mOrderTracker = null;
        String orderNumber = mEditText.getText().toString();
        ((TextView) getView().findViewById(R.id.title_status_text)).setText("# " + orderNumber);
        if (loadingTrackBarView != null) {
            loadingTrackBarView.stopRendering();
        }
        getView().findViewById(R.id.loading_status).setVisibility(View.GONE);
        // HEADER CONTAINER getView().findViewById(R.id.title_status_container).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.error_tracking_order_container).setVisibility(View.VISIBLE);
    }

    protected boolean onSuccessEvent(BaseResponse baseResponse) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        Print.d(TAG, "ON SUCCESS EVENT");
        mOrderTracker = (OrderTracker) baseResponse.getMetadata().getData();
        showFragmentContentContainer();
        processSuccess();
        return true;
    }

    protected boolean onErrorEvent(BaseResponse baseResponse) {
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return true;
        }
        Print.d(TAG, "ON ERROR EVENT");
        mOrderTrackerError = true;
        if(TextUtils.isEmpty(order_number))
            processError();

        super.handleErrorEvent(baseResponse);
        
        return true;
    }

    @Override
    public void notifyFragment(Bundle bundle) {

    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "onSaveInstanceState TRACK");
        if(null != mOrderTracker){
            if(text != null && text.toString().length() > 0)
                outState.putString("order_num",text.toString());
            
            outState.putParcelable("track",mOrderTracker);
        }
        super.onSaveInstanceState(outState);

    }



    /**
     * Proceses the trigger to get the order detailed info (order status)
     * @param orderNumber - The order's number
     * */
    private void triggerGetTrackOrder(String orderNumber) {
        triggerContentEventProgress(new GetTrackOrderHelper(), GetTrackOrderHelper.createBundle(orderNumber), this);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.d(TAG, "ON SUCCESS EVENT");
        // Validate fragment visibility
        if (isOnStoppingProcess) {
            Print.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        // Hide dialog progress
        hideActivityProgress();

        if(super.handleSuccessEvent(baseResponse))
            return;

        onSuccessEvent(baseResponse);


    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        // Hide dialog progress
        hideActivityProgress();

        if(super.handleSuccessEvent(baseResponse))
            return;

        onErrorEvent(baseResponse);

    }
}