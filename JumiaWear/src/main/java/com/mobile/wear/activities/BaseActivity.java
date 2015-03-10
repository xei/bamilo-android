package com.mobile.wear.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.mobile.wear.R;
import com.mobile.wear.receiver.JumiaDataReceiverService;
import com.mobile.wear.service.IDataServiceCallback;

/**
 * Activity that serves as base for all wear activities.
 * Has the service callback definition, and the dismiss handling by long press.
 */
public abstract class BaseActivity extends Activity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    public GoogleApiClient mGoogleApiClient;
    public DismissOverlayView mDismissOverlayView;
    public GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .build();
        }

        // show dismiss overlay on long press
        mDismissOverlayView = (DismissOverlayView) findViewById(R.id.dismiss_overlay_view);
        mDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                if (mDismissOverlayView != null)
                    mDismissOverlayView.show();
            }
        });

        JumiaDataReceiverService.getInstance().setCallback(mCallback);

    }

    // Capture long presses
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    /**
     * Data service Callback
     */
    IDataServiceCallback mCallback = new IDataServiceCallback() {
        @Override
        public void dataItemReceived(Bundle bundle) throws RemoteException {
            Bundle dataItem = bundle.getParcelable(JumiaDataReceiverService.KEY_BUNDLE_DATA);
            Log.i(TAG, "code1wear received dataitem : " + bundle.toString());
            handleStateChaged(bundle);
        }

        @Override
        public void dataMapItem(Bundle bundle) throws RemoteException {

        }

        @Override
        public void openActivity(Intent intent) throws RemoteException {
            startActivity(intent);
        }


        @Override
        public IBinder asBinder() {
            return null;
        }
    };

    public abstract void handleStateChaged(Bundle item);

}
