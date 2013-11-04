/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.TrackOrderEvent;
import pt.rocket.framework.objects.OrderTracker;
import pt.rocket.framework.objects.OrderTrackerItem;
import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author Manuel Silva
 * 
 */
public class TrackOrderFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(TrackOrderFragment.class);
    
    private static TrackOrderFragment mTrackOrderFragment;
    
    private LoadingBarView loadingTrackBarView;
    
    private EditText mEditText;
    
    /**
     * Get instance
     * 
     * @return
     */
    public static TrackOrderFragment getInstance() {
        if (mTrackOrderFragment == null)
            mTrackOrderFragment = new TrackOrderFragment();
        return mTrackOrderFragment;
    }

    /**
     * Empty constructor
     */
    public TrackOrderFragment() {
        super(EnumSet.of(EventType.TRACK_ORDER_EVENT),
        EnumSet.noneOf(EventType.class));
        this.setRetainInstance(true);
    }

    @Override
    public void sendValuesToFragment(int identifier, Object values) {
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
        View view = inflater.inflate(R.layout.track_order_fragment, container, false);
        return view;
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
        setupView();
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
    
    private void setupView() {
        mEditText = (EditText) getView().findViewById(R.id.order_nr_edittext);
        Button mButton = (Button) getView().findViewById(R.id.btn_track_order);
        mButton.setOnClickListener(trackOrderClickListener);
    }
    
    OnClickListener trackOrderClickListener = new OnClickListener() {
        
        @Override
        public void onClick(View v) {
            ((BaseActivity) getActivity()).hideKeyboard();
            String orderNumber = mEditText.getText().toString();
            if(orderNumber != null && orderNumber.length()>0){
                showLoading();
                EventManager.getSingleton().triggerRequestEvent(new TrackOrderEvent(orderNumber));
            }    

        }
    };

    private void showLoading(){
        getView().findViewById(R.id.order_status_container).setVisibility(View.GONE);
        getView().findViewById(R.id.title_status_container).setVisibility(View.GONE); 
        getView().findViewById(R.id.error_trakcing_order).setVisibility(View.GONE);
        getView().findViewById(R.id.loading_status).setVisibility(View.VISIBLE); 
        loadingTrackBarView = (LoadingBarView) getView().findViewById(R.id.loading_bar_view);
        if(loadingTrackBarView != null){
            loadingTrackBarView.startRendering();
        }
        ((Button) getView().findViewById(R.id.btn_track_order)).setText(R.string.track_order);
        ((TextView) getView().findViewById(R.id.title_text)).setText(getActivity().getString(R.string.track_your_order));
        ((BaseActivity) getActivity()).showContentContainer();
    }
    
    private void inflateItemsList(ArrayList<OrderTrackerItem> items){
        LayoutInflater mInflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout mLinearLayout = (LinearLayout) getView().findViewById(R.id.products_items_container);
                
        mLinearLayout.removeAllViews();
        for (OrderTrackerItem orderTrackerItem : items) {
            LinearLayout view = (LinearLayout) mInflater.inflate(R.layout.track_order_item, null, false);
            ((TextView) view.findViewById(R.id.order_item_name)).setText(orderTrackerItem.getName());
            ((TextView) view.findViewById(R.id.order_item_name)).setSelected(true);
            ((TextView) view.findViewById(R.id.order_item_quantity)).setText("Qty. "+orderTrackerItem.getQuantity());
            ((TextView) view.findViewById(R.id.order_status_text)).setText(orderTrackerItem.getStatus());
            ((TextView) view.findViewById(R.id.order_last_update)).setText(orderTrackerItem.getUpdateDate());
            mLinearLayout.addView(view);
        }
    }
    
    private void proccessSuccess(OrderTracker mOrderTracker){
        ((TextView) getView().findViewById(R.id.title_status_text)).setText("# "+mOrderTracker.getId());
        ((TextView) getView().findViewById(R.id.order_creation_date_text)).setText(mOrderTracker.getDate());
        ((TextView) getView().findViewById(R.id.order_payment_method_text)).setText(mOrderTracker.getPaymentMethod());
        
        inflateItemsList(mOrderTracker.getOrderTrackerItems());
        if(loadingTrackBarView != null){
            loadingTrackBarView.stopRendering();
        }
        getView().findViewById(R.id.loading_status).setVisibility(View.GONE);
        getView().findViewById(R.id.title_status_container).setVisibility(View.VISIBLE); 
        getView().findViewById(R.id.order_status_container).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.error_trakcing_order).setVisibility(View.GONE);
    }
    
    private void proccessError(){
        String orderNumber = mEditText.getText().toString();
        ((TextView) getView().findViewById(R.id.title_status_text)).setText("# "+orderNumber);
        if(loadingTrackBarView != null){
            loadingTrackBarView.stopRendering();
        }
        getView().findViewById(R.id.loading_status).setVisibility(View.GONE);
        getView().findViewById(R.id.title_status_container).setVisibility(View.VISIBLE); 
        getView().findViewById(R.id.error_trakcing_order).setVisibility(View.VISIBLE);
    }
    
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        proccessSuccess((OrderTracker) event.result);
        return true;
    }
    
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        proccessError();
        return true;
    }
}
