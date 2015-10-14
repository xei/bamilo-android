package com.mobile.newFramework.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.mobile.framework.R;
import com.mobile.newFramework.utils.output.Print;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * Class used to get the device info.
 * @author spereira
 *
 */
public class DeviceInfoHelper {
	
    private static final String TAG = DeviceInfoHelper.class.getSimpleName();
    
    private static final String PRE_INSTALL_FILE = "is_pre_install";
    
    /*
     * ############### ALL INFO #################
     */

	/**
	 * Get some info about device:<br>
	 * - is pre-installed app<br>
	 * - brand<br>
	 * - sim operator<br>
	 * @param context The application context
	 * @return bundle
	 * @author sergiopereira
	 */
    public static Bundle getInfo(Context context) {
    	// Create new content values
    	Bundle mDeviceInfo = new Bundle();
    	// Get pre installed app
    	mDeviceInfo.putBoolean(Constants.INFO_PRE_INSTALL, isPreInstall(context));
    	// Get brand
    	mDeviceInfo.putString(Constants.INFO_BRAND, getBrand());
    	// Get operator
    	mDeviceInfo.putString(Constants.INFO_SIM_OPERATOR, getSimOperator(context));
        // Get operator
        mDeviceInfo.putString(Constants.INFO_BUNDLE_VERSION, getVersionName(context));
    	// Return values
    	return mDeviceInfo;
    }
    
    /*
     * ############### APPLICATION #################
     */

	/**
     * Normal apps:<br>
     * - /data/data/<package_name> (Internal storage)<br>
     * - /mnt/sdcard/Android/data/<package_name> (External)<br>
     * <br>
     * Pre-installed apps:<br>
     * - all other folders<br>
     * @author sergiopereira
     * @return true or false
     */
    private static boolean isPreInstall(Context context) {
    	// Validate context
    	if(context == null) return false;
    	// Get app info
    	ApplicationInfo app =  context.getApplicationInfo();
    	
    	/**
    	 * Use flags from system.
    	 * @author sergiopereira
    	 */
    	// Validate the system flag
        if ((ApplicationInfo.FLAG_SYSTEM & app.flags) != 0) {
            Print.i(TAG, "PRE INSTALLED: YES");
            return true;
        }
    	
    	/**
    	 * Use a list of base paths to find the pre installed apk.<br>
    	 * - /system/app/com.mobile.jumia.dev-1.apk
    	 * @author sergiopereira
         * TODO: Improve this method to get apk file name
    	 */
        // Validate specific folders
		String[] paths = context.getResources().getStringArray(R.array.pre_install_folders);
		for (String folder : paths) 
			if (existSpecificFile(folder + "/" + app.packageName + "-1.apk")
					|| existSpecificFile(folder + "/" + app.packageName + ".apk")
                    || existSpecificFile(folder + "/JumiaApp-jumia-release.apk")
                    || existSpecificFile(folder + "/JumiaApp-daraz-release.apk")
                    || existSpecificFile(folder + "/JumiaApp-bamilo-release.apk")
                    || existSpecificFile(folder + "/JumiaApp-shop-release.apk"))
				return true;
        
    	/**
    	 * Use a file as a flag, this approach has two problems:<br>
    	 * 1 - System app never been launched<br>
    	 * 2 - User clear data<br>
    	 * @author sergiopereira
    	 */
        // Get flag to create a pre install file
        boolean isToCreatePreInstallFile = context.getResources().getBoolean(R.bool.create_pre_install_file);
        // Case create the pre-install file
        if(isToCreatePreInstallFile) return createPreInstallFile(context);
        // Case find the pre-install file    
        else if (existPreInstallFile(context)) return true;
        
        Print.i(TAG, "PRE INSTALLED: NO");
    	return false;
    }
    
    /**
     * Validate if exist the pre install file with the specific path.
     * @param path The file path
     * @return true or false
     */
    private static boolean existSpecificFile(String path) {
	 	File file = new File(path);
	 	if (file.exists()) {
            Print.i(TAG, "PRE INSTALLED: YES IN " + file.getAbsolutePath());
            return true;
	 	}
	 	return false;
    }
    
    /**
     * Validate if exist the pre install file in internal storage.
     * @param context The application context
     * @return true  or false
     */
    private static boolean existPreInstallFile(Context context) {
    	// Validate if exist pre-install file as flag
    	File file = new File(context.getFilesDir(), PRE_INSTALL_FILE);
	 	if (file.exists()) {
            Print.i(TAG, "FIND PRE INSTALLED: YES IN " + file.getAbsolutePath());
            return true;
	 	}
	 	return false;
    }
    
    /**
     * Create pre install file in internal storage.
     * @param context The application context
     * @return true
     * @author sergiopereira
     */
    private static boolean createPreInstallFile(Context context) {
		try {
    		File file = new File(context.getFilesDir(), PRE_INSTALL_FILE);
            //noinspection ResultOfMethodCallIgnored
    		file.createNewFile();
    		Print.i(TAG, "CREATE PRE INSTALLED: YES IN " + file.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
    }
    
    /*
     * ############### BUILD #################
     */
    
    /**
     * Get the device brand.
     * @return String
     * @author sergiopereira
     */
    private static String getBrand() {
    	Print.i(TAG, "GET BRAND: " + Build.BRAND);
    	return Build.BRAND;
    }
    
    /**
     * Get the version name.
     * @param context The application context
     * @return version or n.a.
     * @author sergiopereira
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        Print.i(TAG, "GET VERSION NAME: " + versionName);
        return versionName;
    }
    
    
    /*
     * ############### SIM CARD #################
     */
    
    /**
     * Get the SIM card operator.
     * @param context The application context
     * @return String
     * @author sergiopereira
     */
    private static String getSimOperator(Context context) {
    	TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	Print.i(TAG, "GET SIM OPERATOR: " + tel.getSimOperatorName());
    	return tel.getSimOperatorName();
    }
    
    /**
     * Get the SIM card country code.
     * @param context The The application context
     * @return Country code or empty
     * @author sergiopereira
     */
    public static String getSimCountryIso(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iso = tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA ? tm.getSimOperatorName().toUpperCase(Locale.getDefault()) : "";
        Print.i(TAG, "GET SIM CONTRY CODE: " + iso);
        return iso;
    }
    
    /*
     * ############### NETWORK #################
     */
    
    /**
     * Get the network country code.
     * @param context The application context
     * @return Country code or empty
     * @author sergiopereira
     */
    public static String getNetworkCountryIso(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iso = tm.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA ? tm.getNetworkCountryIso().toUpperCase(Locale.getDefault()) : "";
        Print.i(TAG, "GET NETWORK COUNTRY CODE: " + iso);
        return iso;
    }
    
    /*
     * ############### SCREEN #################
     */
    
    /**
	 * Get the window width.
	 * @return width
	 */
	public static int getWidth(Context context){
		int width = getMeasures(context).arg1;
		Print.d(TAG, "GET WINDOW MEASURE: WIDTH " + width);
		return width;
	}
	
	/**
	 * Get the window height
	 * @return height
	 */
	public static int getHeight(Context context){
		int height = getMeasures(context).arg2;
		Print.d(TAG, "GET WINDOW MEASURE: HEIGHT " + height);
		return height;
	}	
	
    /**
     * Get the window measures
     * @param context The application context
     * @return Message with arg1 and arg2 (width and height)
     */
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
    public static Message getMeasures(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        int width;
        int height;
        display.getSize(size);
        width = size.x;
        height = size.y;
        Print.i(TAG, "GET WINDOW MEASURES FROM SYSTEM >= HONEYCOMB: " + width + " " + height);
        // Create
        Message msg = new Message();
        msg.arg1 = width;
        msg.arg2 = height;
        // Return
        return msg;
    }
    
//    /**
//     * Get the Screen size inches
//     * @param context The application context
//     * @return float
//     * @author sergiopereira
//     */
//    public static Float getScreenSizeInches(Context context) {
//        DisplayMetrics dm = new DisplayMetrics();
//        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        windowManager.getDefaultDisplay().getMetrics(dm);
//        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
//        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
//        double screenInches = Math.sqrt(x + y);
//        return (float) Math.round(screenInches * 10) / 10;
//    }

    /**
     * ############### ORIENTATION #################
     */

    /**
     * Used other approach:<br>
     * - Created a new Activity with specific screen orientation
     */
    public static void setOrientationForHandsetDevices(Activity activity) {
        // Validate if is phone and force portrait orientation
        if (!activity.getResources().getBoolean(R.bool.isTablet)) {
            Print.i(TAG, "IS PHONE: FORCE PORTRAIT ORIENTATION");
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * Verifies if the current screen orientation is Landscape 
     * @return true if yes, false otherwise
     */
    public static boolean isTabletInLandscape(Context context) {
        return context != null && context.getResources().getBoolean(R.bool.isTablet) && context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
    /**
     * method that verifies if the device is tablet or phone
     *
     * @param context The application context
     * @return true or false
     */
    public static boolean isTabletDevice(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }

    public interface IDeviceVersionBasedCode{
        void highVersionCallback();
        void lowerVersionCallback();
    }

    /**
     * Execute callbacks based on version code of device.
     *
     * @param version
     * @param iDeviceVersionBasedCode
     */
    public static void executeCodeBasedOnVersion(int version, IDeviceVersionBasedCode iDeviceVersionBasedCode){
        if(iDeviceVersionBasedCode != null) {
            if (android.os.Build.VERSION.SDK_INT >= version) {
                iDeviceVersionBasedCode.highVersionCallback();
            } else {
                iDeviceVersionBasedCode.lowerVersionCallback();
            }
        }
    }

    /**
     * Execute callback excluding version.
     *
     * @param version
     * @param runnable
     */
    public static void executeCodeExcludingVersion(int version, Runnable runnable){
        if(runnable != null && android.os.Build.VERSION.SDK_INT != version){
            runnable.run();
        }
    }

    public static void executeCodeExcludingJellyBeanMr2Version(Runnable run){
        executeCodeExcludingVersion(Build.VERSION_CODES.JELLY_BEAN_MR2, run);
    }

    /**
     * Execute callbacks based on Jelly Bean version.
     *
     * @param iDeviceVersionBasedCode
     */
    public static void executeCodeBasedOnJellyBeanVersion(IDeviceVersionBasedCode iDeviceVersionBasedCode) {
        executeCodeBasedOnVersion(android.os.Build.VERSION_CODES.JELLY_BEAN, iDeviceVersionBasedCode);
    }

    public static void executeCodeBasedOnJellyBeanMr1Version(IDeviceVersionBasedCode iDeviceVersionBasedCode) {
        executeCodeBasedOnVersion(Build.VERSION_CODES.JELLY_BEAN_MR1, iDeviceVersionBasedCode);
    }

}
