package com.mobile.wear.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.mobile.wear.R;
import com.mobile.wear.receiver.JumiaDataReceiverService;

/**
 * Class responsible for showing a error screen on the wear app.
 * <p/>
 * Created by pcarvalho on 3/6/15.
 */
public class ErrorActivity extends BaseActivity {
    private static final String TAG = ErrorActivity.class.getSimpleName();
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_activity);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                Log.i(TAG, "code1wearerrormessage ErrorActivity");
                errorTextView = (TextView) stub.findViewById(R.id.error_message);
                if (getIntent() != null && getIntent().getExtras() != null) {
                    String errorMessage = getIntent().getExtras().getString(JumiaDataReceiverService.EXTRA_ERROR_MESSAGE);
                    if (errorMessage != null)
                        errorTextView.setText(errorMessage);
                }
                handlerCloseActivity.sendEmptyMessageDelayed(0, 5000);
            }
        });
    }

    @Override
    public void handleStateChaged(Bundle item) {
        String errorMessage = item.getString(JumiaDataReceiverService.EXTRA_ERROR_MESSAGE);
        errorTextView.setText(errorMessage);
    }

    Handler handlerCloseActivity = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            finish();
        }
    };

}
