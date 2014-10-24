package pt.rocket.framework.utils;

import java.io.File;
import java.io.IOException;

import pt.rocket.framework.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import de.akquinet.android.androlog.Log;

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
	 * @param context
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
            Log.i(TAG, "PRE INSTALLED: YES");
            return true;
        }
    	
    	/**
    	 * Use a list of base paths to find the pre installed apk.<br>
    	 * - /system/app/pt.rocket.jumia.dev-1.apk
    	 * @author sergiopereira
    	 */
        // Validate specific folders
		String[] paths = context.getResources().getStringArray(R.array.pre_install_folders);
		for (String folder : paths) 
			if (existSpecificFile(context, folder + "/" + app.packageName + "-1.apk") 
					|| existSpecificFile(context, folder + "/" + app.packageName + ".apk")
					|| existSpecificFile(context, folder + "/Jumia-release.apk")) 
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
        
        Log.i(TAG, "PRE INSTALLED: NO");
    	return false;
    }
    
    /**
     * Validate if exist the pre install file with the specific path.
     * @param context
     * @param path
     * @return true or false
     */
    private static boolean existSpecificFile(Context context, String path) {
	 	File file = new File(path);
	 	if (file.exists()) {
            Log.i(TAG, "PRE INSTALLED: YES IN " + file.getAbsolutePath());
            return true;
	 	}
	 	return false;
    }
    
    /**
     * Validate if exist the pre install file in internal storage.
     * @param context
     * @return true  or false
     */
    private static boolean existPreInstallFile(Context context) {
    	// Validate if exist pre-install file as flag
    	File file = new File(context.getFilesDir(), PRE_INSTALL_FILE);
	 	if (file.exists()) {
            Log.i(TAG, "FIND PRE INSTALLED: YES IN " + file.getAbsolutePath());
            return true;
	 	}
	 	return false;
    }
    
    /**
     * Create pre install file in internal storage.
     * @param context
     * @return true
     * @author sergiopereira
     */
    private static boolean createPreInstallFile(Context context) {
		try {
    		File file = new File(context.getFilesDir(), PRE_INSTALL_FILE);
    		file.createNewFile();
    		Log.i(TAG, "CREATE PRE INSTALLED: YES IN " + file.getAbsolutePath());
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
    	Log.i(TAG, "GET BRAND: " + Build.BRAND);
    	return Build.BRAND;
    }
    
    /*
     * ############### SIM CARD #################
     */
    
    /**
     * Get the SIM card operator.
     * @param context
     * @return String
     * @author sergiopereira
     */
    private static String getSimOperator(Context context) {
    	TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	Log.i(TAG, "GET SIM OPERATOR: " + tel.getSimOperatorName());
    	return tel.getSimOperatorName();
    }
    
    /*
     * ############### SCREEN #################
     */
    
    /**
	 * Get the window width.
	 * @return
	 */
	public static int getWidth(Context context){
		int width = getMeasures(context).arg1;
		Log.d(TAG, "GET WINDOW MEASURE: WIDTH " + width);
		return width;
	}
	
	/**
	 * Get the window height
	 * @return
	 */
	public static int getHeight(Context context){
		int height = getMeasures(context).arg2;
		Log.d(TAG, "GET WINDOW MEASURE: HEIGHT " + height);
		return height;
	}	
	
    /**
     * Get the window measures
     * @param context
     * @return
     */
    @SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
    public static Message getMeasures(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        int width;
        int height;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
            width = size.x;
            height = size.y;
            Log.i(TAG, "GET WINDOW MEASURES FROM SYSTEM >= HONEYCOMB: " + width + " " + height);
        } else {
            width = display.getWidth();
            height = display.getHeight();
            Log.i(TAG, "GET WINDOW MEASURES FROM SYSTEM: " + width + " " + height);
        }
        // Create
        Message msg = new Message();
        msg.arg1 = width;
        msg.arg2 = height;
        // Return
        return msg;
    }
    
    /**
     * Get the Screen size inches
     * @param context
     * @return float
     * @author sergiopereira
     */
    public static Float getScreenSizeInches(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels / dm.xdpi, 2);
        double y = Math.pow(dm.heightPixels / dm.ydpi, 2);
        double screenInches = Math.sqrt(x + y);
        return (float) Math.round(screenInches * 10) / 10;
    }

}