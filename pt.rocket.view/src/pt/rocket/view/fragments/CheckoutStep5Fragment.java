/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import de.akquinet.android.androlog.Log;

/**
 * @author sergiopereira
 * 
 */
public class CheckoutStep5Fragment extends BaseFragment implements OnClickListener{

    private static final String TAG = LogTagHelper.create(CheckoutStep5Fragment.class);

    private static CheckoutStep5Fragment checkoutStep5Fragment;

    /**
     * Get instance
     * 
     * @return
     */
    public static CheckoutStep5Fragment getInstance() {
        if (checkoutStep5Fragment == null)
            checkoutStep5Fragment = new CheckoutStep5Fragment();
        return checkoutStep5Fragment;
    }

    /**
     * Empty constructor
     */
    public CheckoutStep5Fragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));
        this.setRetainInstance(true);
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
        View view = inflater.inflate(R.layout.checkout_step5, container, false);
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
        AnalyticsGoogle.get().trackPage(R.string.gcheckoutfinal);
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
        getView().findViewById(R.id.btn_checkout_continue).setOnClickListener(this);
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
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        ActivitiesWorkFlow.homePageActivity(getActivity());
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onSuccessEvent(pt.rocket.framework.event.ResponseResultEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        return false;
    }



}
