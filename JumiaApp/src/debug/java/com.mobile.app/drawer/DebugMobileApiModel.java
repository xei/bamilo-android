package com.mobile.app.drawer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.view.R;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.palaima.debugdrawer.base.DebugModule;

/**
 * Model used to simulate some mobile api errors.
 * @author spereira
 */
public class DebugMobileApiModel implements DebugModule, CompoundButton.OnCheckedChangeListener {

    private ResponseCodeInterceptor mInterceptor;
    private SwitchCompat mMaintenance;
    private SwitchCompat mOverload;

    public DebugMobileApiModel(Context context) {
        // Create interceptor
        mInterceptor = new ResponseCodeInterceptor();
        // Add interceptor
        AigHttpClient.getInstance(context).addDebugNetworkInterceptors(mInterceptor);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.dd_debug_drawer_module_mob_api, parent, false);
        mMaintenance = ((SwitchCompat) view.findViewById(R.id.dd_debug_network_maintenance));
        mMaintenance.setOnCheckedChangeListener(this);
        mOverload = ((SwitchCompat) view.findViewById(R.id.dd_debug_network_overload));
        mOverload.setOnCheckedChangeListener(this);
        return view;
    }

    @Override
    public void onOpened() {
        // ...
    }

    @Override
    public void onClosed() {
        // ...
    }

    @Override
    public void onResume() {
        // ...
    }

    @Override
    public void onPause() {
        // ...
    }

    @Override
    public void onStart() {
        // ...
    }

    @Override
    public void onStop() {
        // ...
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.dd_debug_network_maintenance:
                setAction(isChecked, ErrorCode.SERVER_IN_MAINTENANCE);
                disableOthers(mOverload);
                break;
            case R.id.dd_debug_network_overload:
                setAction(isChecked, ErrorCode.SERVER_OVERLOAD);
                disableOthers(mMaintenance);
                break;
            default:
                break;
        }
    }

    private void setAction(boolean isChecked, int code) {
        if(mInterceptor == null) {
            return;
        }
        if (isChecked) {
            mInterceptor.setHttpCode(code);
        } else {
            mInterceptor.removeHttpCode();
        }
    }

    private void disableOthers(@Nullable SwitchCompat switchCompat) {
        if (switchCompat != null && switchCompat.isChecked()) {
            switchCompat.setOnCheckedChangeListener(null);
            switchCompat.setChecked(false);
            switchCompat.setOnCheckedChangeListener(this);
        }
    }


    private static class ResponseCodeInterceptor implements Interceptor {

        private int code = -1;

        public void setHttpCode(int code) {
            this.code = code;
        }

        public void removeHttpCode() {
            this.code = -1;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if (code != -1) {
                response = response.newBuilder().code(code).build();
            }
            return response;
        }
    }
}
