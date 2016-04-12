package com.mobile.app.drawer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.rest.configs.AigRestContract;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.view.R;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Model used to simulate some mobile api errors.
 * @author sergiopereira
 */
public class DebugMobileApiModel extends BaseDebugModel implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private final WeakReference<Activity> mWeakDebugActivity;
    private final ResponseCodeInterceptor mInterceptor;
    private SwitchCompat mMaintenance;
    private SwitchCompat mOverload;
    private AppCompatEditText mEditHost;
    private AppCompatButton mEditButton;
    private View mWarningView;

    public interface ICustomMobileApi {
        void restartAppFlow();
    }

    public DebugMobileApiModel(@NonNull Activity activity) {
        // Save debug activity
        mWeakDebugActivity = new WeakReference<>(activity);
        // Create interceptor
        mInterceptor = new ResponseCodeInterceptor();
        // Add interceptor
        AigHttpClient.getInstance(activity.getApplicationContext()).addDebugNetworkInterceptors(mInterceptor);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.dd_debug_drawer_module_mob_api, parent, false);
        mEditHost = (AppCompatEditText) view.findViewById(R.id.dd_debug_network_host);
        mEditButton = (AppCompatButton) view.findViewById(R.id.dd_debug_network_button_host);
        mWarningView = view.findViewById(R.id.dd_debug_network_text_warning);
        mMaintenance = (SwitchCompat) view.findViewById(R.id.dd_debug_network_maintenance);
        mOverload = (SwitchCompat) view.findViewById(R.id.dd_debug_network_overload);
        showInfo();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Remove maintenance/overload page
        mInterceptor.removeHttpCode();
    }

    @SuppressLint("SetTextI18n")
    private void showInfo() {
        try {
            disableEditHost();
            mEditButton.setOnClickListener(this);
            mMaintenance.setOnCheckedChangeListener(this);
            mOverload.setOnCheckedChangeListener(this);
        } catch (Exception e) {
            // ...
        }
    }

    private void disableEditHost() {
        mEditHost.setText(AigRestContract.REQUEST_HOST);
        mEditHost.setEnabled(false);
        mEditHost.setInputType(InputType.TYPE_NULL);
        mEditButton.setText("Edit");
        mWarningView.setVisibility(View.GONE);
    }

    private void enableEditHost() {
        mEditHost.setEnabled(true);
        mEditHost.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        mEditButton.setText("Ok");
        mWarningView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (mEditHost.isEnabled()) {
            // Restart the app
            CountryPersistentConfigs.saveDebugHost(view.getContext(), mEditHost.getText().toString());
            // Debug activity
            if (mWeakDebugActivity.get() instanceof ICustomMobileApi) {
                ((ICustomMobileApi) mWeakDebugActivity.get()).restartAppFlow();
            }
        } else {
            enableEditHost();
        }
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
        if (mInterceptor != null) {
            if (isChecked) {
                mInterceptor.setHttpCode(code);
            } else {
                mInterceptor.removeHttpCode();
            }
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

        private final static int INVALID_CODE  = -1;

        private int code = INVALID_CODE;

        public void setHttpCode(int code) {
            this.code = code;
        }

        public void removeHttpCode() {
            this.code = INVALID_CODE;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            if (code != INVALID_CODE) response = response.newBuilder().code(code).build();
            return response;
        }
    }

}
