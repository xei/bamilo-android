package com.mobile.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.mobile.framework.utils.LogTagHelper;

import de.akquinet.android.androlog.Log;

/**
 * Activity to show the Server Overload error.
 *
 * Created by Paulo Carvalho on 4/13/15.
 */
public class OverLoadErrorActivity extends FragmentActivity {

    private final static String TAG = LogTagHelper.create(OverLoadErrorActivity.class);
//    private boolean IsconfigChange ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        setContentView(R.layout.kickout_page);
//        IsconfigChange = true ;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }


//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    @Override
//    public boolean isChangingConfigurations() {
//        if(android.os.Build.VERSION.SDK_INT >= 11){
//            Log.i("DEBUG", "Orientation changed api >= 11 ");
//            return super.isChangingConfigurations();
//        }else {
//            Log.i("DEBUG", "Orientation changed api < 11 ");
//            return IsconfigChange;
//        }
//    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onStop() {
        super.onStop();
//        if(isChangingConfigurations()){
//            Log.i("DEBUG", "isChangingConfirgurations OnStop Called");
//        }  else{
//            finish();
//            Log.i("DEBUG", "OnStop Called");
//        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "ON DESTROY");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "ON SAVE INSTANCE");
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "ON BACK PRESSED");
    }
}
