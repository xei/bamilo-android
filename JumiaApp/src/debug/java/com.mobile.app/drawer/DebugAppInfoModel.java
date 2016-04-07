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

/**
 * Model used to show app info.
 * @author spereira
 */
public class DebugAppInfoModel extends BaseDebugModel {

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
        View view = inflater.inflate(R.layout.dd_debug_drawer_module_app_info, parent, false);
        this.appName = (TextView) view.findViewById(R.id.dd_debug_build_app_name);
        this.nameLabel = (TextView) view.findViewById(R.id.dd_debug_build_name);
        this.packageLabel = (TextView) view.findViewById(R.id.dd_debug_build_package);
        this.preInstall = (TextView) view.findViewById(R.id.dd_debug_build_pre_install);
        showInfo();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void showInfo() {
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

}
