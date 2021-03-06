package com.bamilo.android.appmodule.bamiloapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.controllers.ActivitiesWorkFlow;
import com.bamilo.android.framework.service.objects.configs.Version;
import com.bamilo.android.framework.service.objects.configs.VersionInfo;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.appmodule.bamiloapp.utils.dialogfragments.DialogGenericFragment;
import com.bamilo.android.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CheckVersion {

    private static final String TAG = CheckVersion.class.getSimpleName();

    private static final String VERSION_UNWANTED_KEY = "version_unwanted";
    private static final String DIALOG_SEEN_AFTER_THIS_LAUNCH_KEY = "update_seen";

    private static long sLastUpdate = 0;
    private static final long UPDATE_INTERVAL_MILLIS = 60 * 60 * 1000;

    private static final int NOT_AVAILABLE = 0;
    private static final int FORCED_AVAILABLE = 1;
    private static final int OPTIONAL_AVAILABLE = 2;
    private static final int OPTIONAL_AVAILABLE_IGNORED = 3;
    @IntDef({
            NOT_AVAILABLE,
            FORCED_AVAILABLE,
            OPTIONAL_AVAILABLE,
            OPTIONAL_AVAILABLE_IGNORED
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface UpdateStatus{}

    private static @UpdateStatus int checkResult;

    private static final int NO_VERSION_UNWANTED = -1;

    @SuppressLint("StaticFieldLeak")
    private static Context sContext;

    private static int unwantedVersion = NO_VERSION_UNWANTED;

    private static SharedPreferences sSharedPrefs;
    
    private static boolean sNeedsToShowDialog;

    private static boolean isEnabled = false;
    
    public static void init(Context context) {
        // Get if is enabled
        isEnabled  = context.getResources().getBoolean(R.bool.check_version_enabled);
        // Validate flag
        if(isEnabled) {
            sLastUpdate = 0;
            runEvents();
        }
    }

    public static boolean run(Context context) {
        // Validate check version
        if(!isEnabled) return false;

        sContext = context;
        if (runEvents())
            return false;

        sSharedPrefs = context.getSharedPreferences( Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        updateUnwantedVersionFromPrefs();
        if (!checkVersionInfo())
            return false;

        switch (checkResult) {
            case FORCED_AVAILABLE:
                sNeedsToShowDialog = true;
                break;
            case OPTIONAL_AVAILABLE:
                sNeedsToShowDialog = !getRemindMeLater();
                break;
            case OPTIONAL_AVAILABLE_IGNORED:
            case NOT_AVAILABLE:
            default:
                sNeedsToShowDialog = false;
                break;
        }
        return sNeedsToShowDialog;
    }
    
    public static boolean needsToShowDialog() {
        return sNeedsToShowDialog;
    }

    public static void showDialog(FragmentActivity activity) {

        final DialogFragment dialogFragment;
        if (checkResult == FORCED_AVAILABLE) {
            dialogFragment = createForcedUpdateDialog(activity);
        } else {
            dialogFragment = createOptionalUpdateDialog(activity);
        }
        
        final FragmentManager fm = activity.getSupportFragmentManager();
        
        try {
            // Validate activity state
            if(!activity.isFinishing()){
                // Show dialog
                new Handler().postDelayed(() -> dialogFragment.show(fm, null), 1000L);
            }
        } catch (IllegalStateException ignored) {
        }
    }
    
    public static void clearDialogSeenInLaunch(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putBoolean(DIALOG_SEEN_AFTER_THIS_LAUNCH_KEY, false);
        editor.apply();
    }

    private static boolean runEvents() {
        long now = System.currentTimeMillis();
        if (sLastUpdate == 0 || (now - sLastUpdate) > UPDATE_INTERVAL_MILLIS) {
            sLastUpdate = now;
            return true;
        }
        return false;
    }    

    private static DialogFragment createOptionalUpdateDialog(Activity activity) {
        DialogGenericFragment dialog = DialogGenericFragment.newInstance(true, false,
                sContext.getString(R.string.upgrade_optional_title),
                sContext.getString(R.string.upgrade_optional_text, sContext.getString(R.string.app_name_placeholder)),
                sContext.getString(R.string.no_label),
                sContext.getString(R.string.upgrade_remindmelater),
                sContext.getString(R.string.upgrade_proceed),
                null);
        dialog.setOnClickListener(new OptionalUpdateClickListener(activity, dialog));
        return dialog;
    }

    private static DialogFragment createForcedUpdateDialog(Activity activity) {
        DialogGenericFragment dialog = DialogGenericFragment.newInstance(true, false,
                sContext.getString(R.string.upgrade_forced_title),
                sContext.getString(R.string.upgrade_forced_text, sContext.getString(R.string.app_name_placeholder)),
                sContext.getString(R.string.close_app),
                sContext.getString(R.string.upgrade_proceed),
                null);
        dialog.setOnClickListener(new ForceUpdateClickListener(activity, dialog));
        dialog.setCancelable(false);
        return dialog;
    }

    private static boolean checkVersionInfo() {
        PackageInfo packageInfo;
        try {
            packageInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            return false;
        }
        int crrAppVersion = packageInfo.versionCode;

        Version infoVersion = getVersion(sContext);
        if (infoVersion == null) {
            return false;
        }

        if (crrAppVersion < infoVersion.getMinimumVersion()) {
            checkResult = FORCED_AVAILABLE;
        } else if (crrAppVersion < infoVersion.getCurrentVersion()) {
            if (unwantedVersion == infoVersion.getCurrentVersion()) {
                checkResult = OPTIONAL_AVAILABLE_IGNORED;
            } else {
                checkResult = OPTIONAL_AVAILABLE;
            }
        } else {
            checkResult = NOT_AVAILABLE;
        }

        return true;
    }

    private static Version getVersion(Context context) {
        VersionInfo vInfo = BamiloApplication.INSTANCE.getMobApiVersionInfo();
        if (vInfo == null) {
            return null;
        }
        return vInfo.getEntryByKey(context.getPackageName());
    }

    private static void updateUnwantedVersionFromPrefs() {
        unwantedVersion = sSharedPrefs.getInt(VERSION_UNWANTED_KEY, NO_VERSION_UNWANTED);
    }

    private static void storeUpdateAsUnwanted() {
        Version version = getVersion(sContext);
        if ( version == null)
            return;

        sNeedsToShowDialog = false;
        unwantedVersion = version.getCurrentVersion();
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.putInt(VERSION_UNWANTED_KEY, unwantedVersion);
        editor.apply();
    }

    private static boolean getRemindMeLater() {
        return sSharedPrefs.getBoolean(DIALOG_SEEN_AFTER_THIS_LAUNCH_KEY, false);
    }

    private static void storeRemindMeLater() {
        sNeedsToShowDialog = false;
        SharedPreferences.Editor editor = sSharedPrefs.edit();
        editor.putBoolean(DIALOG_SEEN_AFTER_THIS_LAUNCH_KEY, true);
        editor.apply();
    }

    private static void goShop(Activity activity) {
        ActivitiesWorkFlow.startMarketActivity(activity, sContext.getPackageName());
    }

    private static class OptionalUpdateClickListener implements View.OnClickListener {
        private Activity mActivity;
        private DialogGenericFragment mDialog;
        
        public OptionalUpdateClickListener(Activity activity, DialogGenericFragment dialog) {
            mActivity = activity;
            mDialog = dialog;
        }

        @Override
        public void onClick(View v) {
            mDialog.dismissAllowingStateLoss();
            int id = v.getId();
            if (id == R.id.button1) {
                storeUpdateAsUnwanted();
            } else if (id == R.id.button2) {
                storeRemindMeLater();
            } else if (id == R.id.button3) {
                storeRemindMeLater();
                goShop(mActivity);
            }

        }
    }

    private static class ForceUpdateClickListener implements View.OnClickListener {
        private Activity mActivity;
        private DialogGenericFragment mDialog;
        
        public ForceUpdateClickListener(Activity activity, DialogGenericFragment dialog) {
            mActivity = activity;
            mDialog = dialog;
        }
        
        @Override
        public void onClick(View v) {
            mDialog.dismissAllowingStateLoss();
            int id = v.getId();
            if (id == R.id.button1) {
                mActivity.finish();
            } else if (id == R.id.button2) {
                storeRemindMeLater();
                mActivity.finish();
                goShop(mActivity);
            }
        }
    }
}
