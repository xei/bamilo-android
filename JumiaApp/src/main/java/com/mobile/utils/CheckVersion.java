package com.mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.mobile.app.JumiaApplication;
import com.mobile.controllers.ActivitiesWorkFlow;
import com.mobile.newFramework.objects.configs.Version;
import com.mobile.newFramework.objects.configs.VersionInfo;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.dialogfragments.DialogGenericFragment;
import com.mobile.view.R;

public class CheckVersion {

    private static final String TAG = CheckVersion.class.getSimpleName();

    private static final String PLAY_MARKET_QUERY = "market://details?id=";

    private final static String VERSION_UNWANTED_KEY = "version_unwanted";
    private static final String DIALOG_SEEN_AFTER_THIS_LAUNCH_KEY = "update_seen";

    private static long sLastUpdate = 0;
    private static final long UPDATE_INTERVAL_MILLIS = 60 * 60 * 1000;

    enum UpdateStatus {
        NOT_AVAILABLE,
        FORCED_AVAILABLE,
        OPTIONAL_AVAILABLE,
        OPTIONAL_AVAILABLE_IGNORED
    }

    private static UpdateStatus checkResult;

    private static final int NO_VERSION_UNWANTED = -1;

    private static Context sContext;

    private static int unwantedVersion = NO_VERSION_UNWANTED;

    private static DialogFragment sDialog;

    private static SharedPreferences sSharedPrefs;
    
    private static boolean sNeedsToShowDialog;

    private static FragmentManager fm;

    private static boolean isEnabled = false;
    
    public static void init(Context context) {
        // Get if is enabled
        isEnabled  = context.getResources().getBoolean(R.bool.check_version_enabled);
        // Validate flag
        if(isEnabled) {
            Print.i(TAG, "CHECK VERSION: ENABLED");
            sLastUpdate = 0;
            runEvents();
        } else Print.i(TAG, "CHECK VERSION: DISABLED");
    }

    public static boolean run(Context context) {
        // Validate check version
        if(!isEnabled) return false;
        else Print.i(TAG, "RUN CHECK VERSION");
        
        sContext = context;
        if (runEvents())
            return false;

        sSharedPrefs = context.getSharedPreferences( Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        updateUnwantedVersionFromPrefs();
        if (!checkVersionInfo())
            return false;
        Print.i(TAG, "code1checkversion checkResult : " + checkResult);
        if (checkResult == UpdateStatus.FORCED_AVAILABLE) {
            sNeedsToShowDialog = true;
        } else if ( checkResult == UpdateStatus.OPTIONAL_AVAILABLE) {
            if (getRemindMeLater()) {
                sNeedsToShowDialog = false;
            } else {
                sNeedsToShowDialog = true;
            }
        } else if ( checkResult == UpdateStatus.OPTIONAL_AVAILABLE_IGNORED) {
            sNeedsToShowDialog = false;
        } else {
            sNeedsToShowDialog = false;
        }
        return sNeedsToShowDialog;
    }
    
    public static boolean needsToShowDialog() {
        return sNeedsToShowDialog;
    }

    public static void showDialog(FragmentActivity activity) { 
        
        if (checkResult == UpdateStatus.FORCED_AVAILABLE) {
            sDialog = createForcedUpdateDialog(activity);
        } else {
            sDialog = createOptionalUpdateDialog(activity);
        }
        
        fm = activity.getSupportFragmentManager();
        
        try {
            // Validate activity state
            if(!activity.isFinishing()){
                // Show dialog
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sDialog.show(fm, null);
                    }
                }, 1000l);
            }
        } catch (IllegalStateException e) {
            Print.w(TAG, "WARNING: ISE ON SHOW VERSION DIALOG", e);
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
        Print.d(TAG, "runEvents: lastUpdate = " + sLastUpdate + " passed = " + (now - sLastUpdate) + " intervall = " + UPDATE_INTERVAL_MILLIS);
        if (sLastUpdate == 0 || (now - sLastUpdate) > UPDATE_INTERVAL_MILLIS) {
            Print.d(TAG, "runEvents: init or intervall passed - triggering");
            sLastUpdate = now;
            return true;
        }
        return false;
    }    

    private static DialogFragment createOptionalUpdateDialog(Activity activity) {
        return DialogGenericFragment.newInstance(true, false,
                sContext.getString(R.string.upgrade_optional_title),
                sContext.getString(R.string.upgrade_optional_text, sContext.getString(R.string.app_name_placeholder)),
                sContext.getString(R.string.no_label),
                sContext.getString(R.string.upgrade_remindmelater),
                sContext.getString(R.string.upgrade_proceed),
                new OptionalUpdateClickListener(activity));
    }

    private static DialogFragment createForcedUpdateDialog(Activity activity) {
        DialogFragment dialog = DialogGenericFragment.newInstance(true, false,
                sContext.getString(R.string.upgrade_forced_title),
                sContext.getString(R.string.upgrade_forced_text, sContext.getString(R.string.app_name_placeholder)),
                sContext.getString(R.string.close_app),
                sContext.getString(R.string.upgrade_proceed),
                new ForceUpdateClickListener(activity));
        dialog.setCancelable(false);
        return dialog;
    }

    private static boolean checkVersionInfo() {
        PackageInfo packageInfo;
        try {
            packageInfo = sContext.getPackageManager().getPackageInfo(sContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            Print.e(TAG, "No package name for the current package. This should not occur");
            return false;
        }
        int crrAppVersion = packageInfo.versionCode;

        Version infoVersion = getVersion(sContext);
        if (infoVersion == null) {
            Print.w(TAG, "No version info available - terminating version check");
            return false;
        }

        if (crrAppVersion < infoVersion.getMinimumVersion()) {
            checkResult = UpdateStatus.FORCED_AVAILABLE;
        } else if (crrAppVersion < infoVersion.getCurrentVersion()) {
            if (unwantedVersion == infoVersion.getCurrentVersion()) {
                checkResult = UpdateStatus.OPTIONAL_AVAILABLE_IGNORED;
            } else {
                checkResult = UpdateStatus.OPTIONAL_AVAILABLE;
            }
        } else {
            checkResult = UpdateStatus.NOT_AVAILABLE;
        }

        Print.d(TAG, "checkVersionInfo: appVersion = " + crrAppVersion);
        Print.d(TAG, "provided minimumVersion = " + infoVersion.getMinimumVersion()
                + " currentVersion = " + infoVersion.getCurrentVersion());
        Print.d(TAG, "customer preference: "
                + (unwantedVersion == infoVersion.getCurrentVersion() ? "Ignore" : "Accept"));
        Print.d(TAG, "checkResult = " + checkResult);
        return true;
    }

    private static Version getVersion(Context context) {
        VersionInfo vInfo = JumiaApplication.INSTANCE.getMobApiVersionInfo();
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

    private static String createUpdateUrl() {
        String packageName = sContext.getPackageName();

        String url;
        if (packageName.startsWith("com.mobile.jumia.dev")
                || packageName.startsWith("com.mobile.jumia.weekly"))
            packageName = "com.jumia.android";

        url = PLAY_MARKET_QUERY + packageName;

        return url;
    }

    private static void goShop(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(createUpdateUrl()));
        activity.startActivity(intent);
        ActivitiesWorkFlow.addStandardTransition(activity);
    }

    private static class OptionalUpdateClickListener implements View.OnClickListener {
        private Activity mActivity;
        
        public OptionalUpdateClickListener( Activity activity) {
            mActivity = activity;
        }

        @Override
        public void onClick(View v) {
            sDialog.dismissAllowingStateLoss();

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
    };
    
    private static class ForceUpdateClickListener implements View.OnClickListener {
        private Activity mActivity;
        
        public ForceUpdateClickListener( Activity activity) {
            mActivity = activity;
        }
        
        @Override
        public void onClick(View v) {
            sDialog.dismissAllowingStateLoss();

            int id = v.getId();
            if (id == R.id.button1) {
                mActivity.finish();
                ActivitiesWorkFlow.addStandardTransition(mActivity);
            } else if (id == R.id.button2) {
                storeRemindMeLater();
                mActivity.finish();
                goShop(mActivity);
            }
        }
    };
}
