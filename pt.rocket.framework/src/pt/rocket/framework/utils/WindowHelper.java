package pt.rocket.framework.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Message;
import android.util.DisplayMetrics;
import de.akquinet.android.androlog.Log;
import android.view.Display;
import android.view.WindowManager;

/**
 * 
 * @author sergiopereira
 *
 */
public class WindowHelper {
    
	private static final String TAG = WindowHelper.class.getSimpleName();
	
	/**
	 * Get the window width
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
