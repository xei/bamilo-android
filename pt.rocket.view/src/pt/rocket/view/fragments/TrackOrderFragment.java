/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsCheckout;
import pt.rocket.framework.objects.OrderTracker;
import pt.rocket.framework.objects.OrderTrackerItem;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.LoadingBarView;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.checkout.GetTrackOrderHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

    private static OrderTracker mOrderTracker;
    private static boolean mOrderTrackerError = false;
    
    private boolean mOrderTrackingClicked = false;

    /**
     * Get instance
     * 
     * @return
     */
    public static TrackOrderFragment getInstance() {
        mTrackOrderFragment = new TrackOrderFragment();
        return mTrackOrderFragment;
    }

    /**
     * Empty constructor
     */
    public TrackOrderFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.BASKET, MyMenuItem.MY_PROFILE),
                NavigationAction.TrackOrder,
                R.layout.track_order_fragment,
                R.string.order_status_label,
                KeyboardState.ADJUST_CONTENT);
        // R.string.nav_track_order
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
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View,
     * android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
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
        // Show status container if Button "Track Order" was clicked, or if in landscape mode
        if (mOrderTrackingClicked || BaseActivity.isTabletInLandscape(getBaseActivity())) {
            showStatusContainer();
            // set tip visible if Button "Track Order" wasn't clicked yet
            setTipVisibility();
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
        Log.i(TAG, "ON DESTROY");
    }

    private void setupView() {
        mEditText = (EditText) getView().findViewById(R.id.order_nr);
        Button mButton = (Button) getView().findViewById(R.id.btn_track_order);
        mButton.setOnClickListener(trackOrderClickListener);

        Bundle args = this.getArguments();
        String order_number = null;
        if (args != null) {
            order_number = args.getString(ConstantsCheckout.CHECKOUT_THANKS_ORDER_NR);
        }
        if (order_number != null) {
            mEditText.setText(order_number);
        }
        Editable text = mEditText.getText();
        if (text != null && text.length() > 0 && mOrderTracker != null) {
            proccessSuccess();
        } else if (text != null && text.length() > 0 && mOrderTrackerError) {
            proccessError();
        }
    }

    OnClickListener trackOrderClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            getBaseActivity().hideKeyboard();
            String orderNumber = mEditText.getText().toString();
            if (orderNumber != null && orderNumber.length() > 0) {
                mOrderTrackingClicked = true;
                // set status container visible from this point on
                showStatusContainer();
                setTipVisibility();

                showLoading();
                Bundle args = new Bundle();
                args.putString(GetTrackOrderHelper.ORDER_NR, orderNumber);
                JumiaApplication.INSTANCE.sendRequest(new GetTrackOrderHelper(), args, new IResponseCallback() {
                    @Override
                    public void onRequestError(Bundle bundle) {
                        onErrorEvent(bundle);
                    }
                    
                    @Override
                    public void onRequestComplete(Bundle bundle) {
                        onSuccessEvent(bundle);
                    }
                });
            }
        }
    };

    private void setTipVisibility() {
        if (mOrderTrackingClicked) {
            getView().findViewById(R.id.tip_tracking_order).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.tip_tracking_order).setVisibility(View.VISIBLE);
        }
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
            ((TextView) view.findViewById(R.id.order_item_quantity)).setText(" - " + getString(R.string.my_order_qty) + ". " + orderTrackerItem.getQuantity());
            ((TextView) view.findViewById(R.id.order_status_text)).setText(orderTrackerItem.getStatus());

            // REMOVED ON NAFAMZ-7271
            // ((TextView) view.findViewById(R.id.order_last_update)).setText(orderTrackerItem.getUpdateDate());

            // add divider to top of item after first item
            if (i > 0) {
                view.findViewById(R.id.order_item_divider).setVisibility(View.VISIBLE);
            }
            mLinearLayout.addView(view);
        }
    }

    private void proccessSuccess() {
        ((TextView) getView().findViewById(R.id.title_status_text)).setText("# " + mOrderTracker.getId());
        ((TextView) getView().findViewById(R.id.order_creation_date_text)).setText(mOrderTracker.getDate());
        ((TextView) getView().findViewById(R.id.order_payment_method_text)).setText(mOrderTracker.getPaymentMethod());

        inflateItemsList(mOrderTracker.getOrderTrackerItems());
        if (loadingTrackBarView != null) {
            loadingTrackBarView.stopRendering();
        }
        getView().findViewById(R.id.loading_status).setVisibility(View.GONE);
        // HEADER CONTAINER getView().findViewById(R.id.title_status_container).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.order_status_container).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.error_tracking_order_container).setVisibility(View.GONE);
    }

    private void proccessError() {
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

    protected boolean onSuccessEvent(Bundle bundle) {
        if (!isVisible()) {
            return true;
        }
        Log.d(TAG, "ON SUCCESS EVENT");
        mOrderTracker = (OrderTracker) bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
        proccessSuccess();
        return true;
    }

    protected boolean onErrorEvent(Bundle bundle) {
        if (!isVisible()) {
            return true;
        }
        Log.d(TAG, "ON ERROR EVENT");
        mOrderTrackerError = true;
        proccessError();
        return true;
    }

    @Override
    public void notifyFragment(Bundle bundle) {

    }
}