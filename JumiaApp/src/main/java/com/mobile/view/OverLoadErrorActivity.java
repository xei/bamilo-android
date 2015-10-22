package com.mobile.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.output.Print;

/**
 * Activity to show the Server Overload error.
 *
 * Created by Paulo Carvalho on 4/13/15.
 */
public class OverLoadErrorActivity extends FragmentActivity {

    private final static String TAG = OverLoadErrorActivity.class.getSimpleName();
//    private boolean IsconfigChange ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        setContentView(R.layout.kickout_page);

        // control whether to allow the activity to rotate or not
        if(DeviceInfoHelper.isTabletDevice(getApplicationContext())){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

//        IsconfigChange = true ;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Print.i(TAG, "ON START");
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
        Print.i(TAG, "ON PAUSE");
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
        Print.i(TAG, "ON DESTROY");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Print.i(TAG, "ON SAVE INSTANCE");
    }

    @Override
    public void onBackPressed() {
        Print.i(TAG, "ON BACK PRESSED");
    }
}
