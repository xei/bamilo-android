package com.mobile.app.drawer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

import java.io.IOException;

import io.palaima.debugdrawer.base.DebugModule;

/**
 * Created by spereira on 2/23/16.
 */
public class CountryConfigsModel implements DebugModule {


    private final Context context;
    private ViewGroup rootView;
    private ResponseCodeInterceptor mMaintenancePageInterceptor;

    public CountryConfigsModel(@NonNull Activity activity) {
        context = activity;
        rootView = (ViewGroup) activity.findViewById(android.R.id.content);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {

//        ViewGroup contentView = (ViewGroup) rootView.getChildAt(0);
//        ViewGroup scrimInsets = (ViewGroup) contentView.getChildAt(0);
//        View contentRelativeView = scrimInsets.getChildAt(0);
//
//        scrimInsets.removeView(contentRelativeView);
//
//        final ScalpelFrameLayout scalpelFrameLayout = new ScalpelFrameLayout(context);
//        scalpelFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        scrimInsets.addView(scalpelFrameLayout);
//        scalpelFrameLayout.addView(contentRelativeView);

        View view = inflater.inflate(R.layout.dd_debug_drawer_item_scalpel, parent, false);
        Switch debugEnableScalpel = (Switch) view.findViewById(R.id.dd_debug_enable_scalpel);
        debugEnableScalpel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ResponseCodeInterceptor mMaintenancePageInterceptor = new ResponseCodeInterceptor(ErrorCode.SERVER_IN_MAINTENANCE);
                    AigHttpClient.getInstance(context).addDebugNetworkInterceptors(mMaintenancePageInterceptor);
                } else {
//                    AigHttpClient.getInstance().removeDebugNetworkInterceptors(mMaintenancePageInterceptor);
                }
            }
        });
        Switch debugDisableGraphics = (Switch) view.findViewById(R.id.dd_debug_disable_graphics);
        debugDisableGraphics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                scalpelFrameLayout.setDrawViews(!isChecked);
            }
        });
        Switch debugShowIds = (Switch) view.findViewById(R.id.dd_debug_show_ids);
        debugShowIds.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                scalpelFrameLayout.setDrawIds(isChecked);
            }
        });

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


    private static class ResponseCodeInterceptor implements Interceptor {

        private int code;

        public ResponseCodeInterceptor(@ErrorCode.Code int code) {
            this.code = code;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Print.w(AigHttpClient.TAG, ">>>>>>>>>>>>>>> OK HTTP: RESPONSE CODE INTERCEPTOR = " + code);
            Response response = chain.proceed(chain.request());
            response.newBuilder().code(code).build();
            return response;
        }
    }
}
