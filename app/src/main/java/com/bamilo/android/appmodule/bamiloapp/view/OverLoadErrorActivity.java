package com.bamilo.android.appmodule.bamiloapp.view;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.bamilo.android.R;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;

/**
 * Activity to show the Server Overload error.
 *
 * Created by Paulo Carvalho on 4/13/15.
 */
public class OverLoadErrorActivity extends FragmentActivity {

    private final static String TAG = OverLoadErrorActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kickout_page);
        // control whether to allow the activity to rotate or not
        if(DeviceInfoHelper.isTabletDevice(getApplicationContext())){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
