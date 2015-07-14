package com.mobile.wear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.wear.R;
import com.mobile.wear.receiver.JumiaDataReceiverService;

import java.util.List;

/**
 * Class responsible for showing the main screen where user can search by voice input, opening the handheld app with a search
 * with the search term recognized on the wear.
 */
public class MainWearActivity extends BaseActivity {

    private static final String TAG = MainWearActivity.class.getSimpleName();
    public TextView mTextView;
    public ImageView mImageView;
    public RelativeLayout mRelativeLayout;

    private static final int SPEECH_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mImageView = (ImageView) stub.findViewById(R.id.image);
                mTextView = (TextView) stub.findViewById(R.id.text);
                mRelativeLayout = (RelativeLayout) stub.findViewById(R.id.main_container);
                mRelativeLayout.setVisibility(View.GONE);
                mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        displaySpeechRecognizer();
                    }
                });
            }
        });

    }

    @Override
    public void handleStateChaged(Bundle item) {

    }


    /**
     * function responsible for showing the voice input function
     */
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
//        JumiaDataReceiverService.performSearch("green");
    }


    /**
     * method that receives the input from the user speeching to the wearable
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // This callback is invoked when the Speech Recognizer returns.
        // This is where you process the intent and extract the speech text from the intent.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0).replaceAll(" ", "%20");
            JumiaDataReceiverService.performSearch(spokenText);
        }
    }

}
