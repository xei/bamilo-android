package com.mobile.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;

import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;
import net.hockeyapp.android.UpdateManager;
import net.hockeyapp.android.UpdateManagerListener;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.security.auth.x500.X500Principal;

public class HockeyStartup {
    private static final String TAG = HockeyStartup.class.getSimpleName();
    private static final String MESSAGEDIGEST = "SHA1";
    private static final int HOCKEY_APP_TOKEN_RES = R.string.hockey_app_id;
    
    private static final int HOCKEY_APP_DEV_BOOL = R.bool.hockeyapp_enable_dev;
    
    private static final int SPLASH_DISPLAY_TIME = 5000;
    private static final X500Principal KEY_PRINCIPAL_DEBUG = new X500Principal("CN=Android Debug, O=Android, C=US");
    private static final String KEY_FINGERPRINT_HOCKEY = "CA:2B:59:48:72:0E:1F:8F:36:38:20:D8:B0:94:37:82:6F:23:0E:B6";

    private static final int RESULT_KEY_OTHER = 0;
    private static final int RESULT_KEY_DEBUG = 1;
    private static final int RESULT_KEY_HOCKEY = 2;

    private static Handler sHandler = new Handler();
    private static boolean sDialogWasStarted;
    private static Runnable sStartRunnable;
    private static Activity sActivity;

    public static void start(Activity activity, Runnable startRunnable) {
        Print.i(TAG, "start: executing key check and hockey - with app startup");
        sActivity = activity;
        sStartRunnable = startRunnable;
        sDialogWasStarted = false;

        sActivity.getWindow().setCallback(sCallback);

        register(activity);

        sHandler.postDelayed(sStartRunnable, SPLASH_DISPLAY_TIME);
    }

    public static void register(Activity activity) {
        int resultCheckSignature = checkSignatureForUpdate(activity);
        Print.d(TAG, "resultCheckSignature = " + resultCheckSignature);
        
        String hockeyTocken = activity.getString(HOCKEY_APP_TOKEN_RES);

        /**
         * ###### HOCKEY APP: ENABLED DEV ######
         */
        Boolean enableDevVersion = activity.getResources().getBoolean(HOCKEY_APP_DEV_BOOL);
        
        Print.d(TAG, "HOCKEY_TOKEN = " + hockeyTocken);
        if (resultCheckSignature == RESULT_KEY_OTHER || resultCheckSignature == RESULT_KEY_HOCKEY || enableDevVersion) {
            Print.d(TAG, "start: starting CrashManager");
            CrashManager.register(activity, hockeyTocken, sCml);
        }
        if (resultCheckSignature == RESULT_KEY_HOCKEY) {
            Print.d(TAG, "start: starting UpdateManager");
            UpdateManager.register(activity, hockeyTocken);
        }
    }

    public static boolean isDevEnvironment( Context context ) {
        int resultCheckSignature = checkSignatureForUpdate(context);
        return !(resultCheckSignature == RESULT_KEY_HOCKEY || resultCheckSignature == RESULT_KEY_OTHER);
    }
    
    public static boolean isSplashRequired( Context context ) {
        int resultCheckSignature = checkSignatureForUpdate(context);
        return resultCheckSignature == RESULT_KEY_HOCKEY || resultCheckSignature == RESULT_KEY_DEBUG;
    }

    public static int checkSignatureForUpdate(Context context) {
        context = context.getApplicationContext();
        PackageManager pM = context.getPackageManager();
        Signature signature;
        try {
            signature  = pM.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES).signatures[0];
        } catch (NameNotFoundException e) {
            Print.e(TAG, "Can't find signature for app", e);
            return RESULT_KEY_OTHER;
        }
        
        
        CertificateFactory cf;
        X509Certificate cert;
        try {
            cf = CertificateFactory.getInstance("X.509" );
            cert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(signature.toByteArray()));
        } catch (CertificateException e) {
            Print.e(TAG, "Cant get x509certificate", e);
            return RESULT_KEY_OTHER;
        }
        
        String fingerPrint = calcFingerPrint( cert );
        
        // keytool -list -v -keystore ${1} -keypass android -storepass android
        Print.d(TAG, "X500Principal: " + cert.getSubjectX500Principal().toString());
        // Log.d( TAG, "certificate fingerPrint(" +MESSAGEDIGEST+ ") = " + fingerPrint );
      
        if ( fingerPrint != null && KEY_FINGERPRINT_HOCKEY.equals( fingerPrint.toUpperCase(Locale.US))) {
            cf = null;
            cert = null;
            fingerPrint = null;
            return RESULT_KEY_HOCKEY;
        } else if ( cert.getSubjectX500Principal().equals(KEY_PRINCIPAL_DEBUG)) {
            cf = null;
            cert = null;
            fingerPrint = null;
            return RESULT_KEY_DEBUG;
        } else {
            cf = null;
            cert = null;
            fingerPrint = null;
            return RESULT_KEY_OTHER;
        }
    }

    private static String calcFingerPrint(X509Certificate cert) {
        try {
            // Log.d( TAG, "calcFingerPrint: publicKey algorithm = " + cert.toString());
            MessageDigest md = MessageDigest.getInstance(MESSAGEDIGEST);
            byte[] publicKey = md.digest(cert.getEncoded());

            boolean delimiter = false;
            StringBuilder hexString = new StringBuilder();
            for (byte aPublicKey : publicKey) {
                String appendString = Integer.toHexString(0xFF & aPublicKey);
                if (delimiter)
                    hexString.append(":");
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);

                delimiter = true;
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            Print.e(TAG, "failing calculation of fingerprint: ", e);
            return null;
        } catch (CertificateEncodingException e) {
            Print.e(TAG, "failing calculation of fingerprint: ", e);
            return null;
        }
    }

    private static UpdateManagerListener sUml = new UpdateManagerListener() {

        @Override
        public void onNoUpdateAvailable() {
            super.onNoUpdateAvailable();
            Print.d(TAG, "No update found");
        }

        @Override
        public void onUpdateAvailable() {
            Print.d(TAG, "Update found - waiting for dialog to finish");
            sHandler.removeCallbacks(sStartRunnable);
            sDialogWasStarted = true;
        }
    };

    private static CrashManagerListener sCml = new CrashManagerListener() {
        
        public boolean ignoreDefaultHandler() {
            return false;
        }

        @Override
        public boolean onCrashesFound() {
            Print.d(TAG, "Crashes found - waiting for dialog to finish");
            sHandler.removeCallbacks(sStartRunnable);
            sDialogWasStarted = true;
            return true;
        }

        @Override
        public void onCrashesSent() {

        }

        @Override
        public void onCrashesNotSent() {
        }
    };

    private static Window.Callback sCallback = new Window.Callback() {

        @Override
        public ActionMode onWindowStartingActionMode(Callback callback) {
            return sActivity.onWindowStartingActionMode(callback);
        }

        @Nullable
        @Override
        public ActionMode onWindowStartingActionMode(Callback callback, int type) {
            return sActivity.onWindowStartingActionMode(callback, type);
        }

        @Override
        public void onWindowFocusChanged(boolean hasFocus) {
            sActivity.onWindowFocusChanged(hasFocus);
            if (sDialogWasStarted && hasFocus) {
                Print.d(TAG, "we have focus again - start the app");
                sDialogWasStarted = false;
                sStartRunnable.run();
            }
        }

        @Override
        public void onWindowAttributesChanged(LayoutParams attrs) {
            sActivity.onWindowAttributesChanged(attrs);
        }

        @Override
        public boolean onSearchRequested() {
            return sActivity.onSearchRequested();
        }

        @Override
        public boolean onSearchRequested(SearchEvent searchEvent) {
            return sActivity.onSearchRequested(searchEvent);
        }

        @Override
        public boolean onPreparePanel(int featureId, View view, Menu menu) {
            return sActivity.onPreparePanel(featureId, view, menu);
        }

        @Override
        public void onPanelClosed(int featureId, Menu menu) {
            sActivity.onPanelClosed(featureId, menu);
        }

        @Override
        public boolean onMenuOpened(int featureId, Menu menu) {
            return sActivity.onMenuOpened(featureId, menu);
        }

        @Override
        public boolean onMenuItemSelected(int featureId, MenuItem item) {
            return sActivity.onMenuItemSelected(featureId, item);
        }

        @Override
        public void onDetachedFromWindow() {
            sActivity.onDetachedFromWindow();
        }

        @Override
        public View onCreatePanelView(int featureId) {
            return sActivity.onCreatePanelView(featureId);
        }

        @Override
        public boolean onCreatePanelMenu(int featureId, Menu menu) {
            return sActivity.onCreatePanelMenu(featureId, menu);
        }

        @Override
        public void onContentChanged() {
            sActivity.onContentChanged();
        }

        @Override
        public void onAttachedToWindow() {
            sActivity.onAttachedToWindow();
        }

        @Override
        public void onActionModeStarted(ActionMode mode) {
            sActivity.onActionModeStarted(mode);
        }

        @Override
        public void onActionModeFinished(ActionMode mode) {
            sActivity.onActionModeFinished(mode);
        }

        @Override
        public boolean dispatchTrackballEvent(MotionEvent event) {
            return sActivity.dispatchTrackballEvent(event);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            return sActivity.dispatchTouchEvent(event);
        }

        @Override
        public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
            return sActivity.dispatchPopulateAccessibilityEvent(event);
        }

        @Override
        public boolean dispatchKeyShortcutEvent(KeyEvent event) {
            return sActivity.dispatchKeyShortcutEvent(event);
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            return sActivity.dispatchKeyEvent(event);
        }

        @Override
        public boolean dispatchGenericMotionEvent(MotionEvent event) {
            return sActivity.dispatchGenericMotionEvent(event);
        }
    };

}
