package com.mobile.app.drawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.view.R;

import io.palaima.debugdrawer.base.DebugModule;

/**
 * Model used to show trackers ids and enable/disable the respective log.
 *
 * @author spereira
 */
public class DebugAppInfoModel implements DebugModule {

    private final Context context;

    private TextView nameLabel;
    private TextView packageLabel;
    private TextView preInstall;
    private TextView appName;

    public DebugAppInfoModel(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.dd_debug_drawer_item_app_info, parent, false);
        this.appName = (TextView) view.findViewById(R.id.dd_debug_build_app_name);
        this.nameLabel = (TextView) view.findViewById(R.id.dd_debug_build_name);
        this.packageLabel = (TextView) view.findViewById(R.id.dd_debug_build_package);
        this.preInstall = (TextView) view.findViewById(R.id.dd_debug_build_pre_install);
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void refresh() {
        try {
            PackageInfo info = this.context.getPackageManager().getPackageInfo(this.context.getPackageName(), 0);
            this.appName.setText(context.getString(context.getApplicationInfo().labelRes));
            this.nameLabel.setText(info.versionName + " / " + info.versionCode);
            this.packageLabel.setText(info.packageName);
            this.preInstall.setVisibility(DeviceInfoHelper.getInfo(context).getBoolean(Constants.INFO_PRE_INSTALL) ? View.VISIBLE : View.GONE);
        } catch (Exception var2) {
            // ...
        }
    }

    @Override
    public void onOpened() {
        refresh();
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

}
