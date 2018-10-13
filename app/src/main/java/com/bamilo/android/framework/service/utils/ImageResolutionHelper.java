package com.bamilo.android.framework.service.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Message;
import com.bamilo.android.R;
import com.bamilo.android.framework.service.database.ImageResolutionTableHelper;
import com.bamilo.android.framework.service.objects.configs.ImageResolution;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to create the image URL with a new image resolution
 *
 * @author sergiopereira
 */
public class ImageResolutionHelper {

    private final static String TAG = ImageResolutionHelper.class.getSimpleName();

    public final static String REGEX_RESOLUTION = DarwinRegex.REGEX_IMAGE_RESOLUTION;

    private static WeakHashMap<String, ImageResolution> resolutionMap = new WeakHashMap<>();

    protected static Context CONTEXT;

    public static final int DEVICE_WEAK_RESOURCES = 0;
    public static final int DEVICE_NORMAL_RESOURCES = 1;
    public static final int DEVICE_HIGH_RESOURCES = 2;

    /**
     * Initialize helper
     */
    public static int init(Context context) {

        // Get screen size
        Message wm = DeviceInfoHelper.getMeasures(context);
        // Get heap size available
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        int memoryHeapSize = am.getMemoryClass();

        // Validate heap size
        if (memoryHeapSize <= 32) {
            disableHelper();
            return DEVICE_WEAK_RESOURCES;
        } else if (memoryHeapSize <= 48 && !context.getResources().getBoolean(R.bool.isTablet)) {
            enableHelper(context);
            return DEVICE_NORMAL_RESOURCES;
        } else {
            enableHelper(context);
            return DEVICE_HIGH_RESOURCES;
        }

    }


    private static void enableHelper(Context context) {
        CONTEXT = context;
    }

    private static void disableHelper() {
        CONTEXT = null;
    }

    /**
     * Get the best resolution from cache or database using the screen resolution
     *
     * @return ImageResolution
     * @deprecated
     */
    private static ImageResolution getBestResolution(int width, int height) {
        // Validate hash map
        if (resolutionMap == null) {
            resolutionMap = new WeakHashMap<>();
        }
        // Get from hash map
        ImageResolution imageResolution = resolutionMap.get(width + "x" + height);
        // Validate resolution
        if (imageResolution == null) {
            // Get best image resolution
            imageResolution = ImageResolutionTableHelper.getBestImageResolution(width, height);
            // Save resolution
            resolutionMap.put(width + "x" + height, imageResolution);
        }
        return imageResolution;
    }

    /**
     * Get the best resolution from cache or database using the current resolution tag
     *
     * @return ImageResolution
     */
    private static ImageResolution getBestResolution(String resolutionTag) {
        // Validate hash map
        if (resolutionMap == null) {
            resolutionMap = new WeakHashMap<>();
        }
        // Resolution
        ImageResolution imageResolution = null;
        // Check cache
        if (resolutionMap.containsKey(resolutionTag)) {
            // Get resolution from cache
            imageResolution = resolutionMap.get(resolutionTag);
        } else {
            try {
                // Get resolution from database
                imageResolution = ImageResolutionTableHelper.getBestImageResolution(resolutionTag);
                // Save resolution on cache
                resolutionMap.put(resolutionTag, imageResolution);
            } catch (IllegalStateException e) {
                // ...
            }
        }

        return imageResolution;
    }


    /**
     * Get the tag of the best resolution from the current resolution
     *
     * @return Tag or null
     */
    public static String getResolutionIdentifier(String resolutionTag) {
        // Get best resolution
        ImageResolution imageResolution = getBestResolution(resolutionTag);
        // Validate result
        if (imageResolution == null) {
            return null;
        }
        // Return
        return imageResolution.getIdentifier() + "." + imageResolution.getExtension();
    }


    /**
     * Method used to return the identifier and extension of the best image resolution
     *
     * @return String
     * @deprecated
     */
    public static String getResolutionIdentifier(int width, int height) {
        // Get best resolution
        ImageResolution imageResolution = getBestResolution(width, height);
        // Return
        return imageResolution.getIdentifier() + "." + imageResolution.getExtension();
    }


    /**
     * Creates a new image URL replacing the current resolution with the best resolution
     *
     * @return String or null
     */
    public static String replaceResolution(String url) {
        // Validate Helper
        if (CONTEXT == null) {
            return null;
        }
        // Get the current resolution
        String resolution = getCurrentResolution(url);
        if (resolution == null) {
            return null;
        }
        // Get the best resolution
        String resolutionTag = getResolutionIdentifier(resolution);
        if (resolutionTag == null) {
            return null;
        }
        // Create new URL with the new resolution
        Pattern pattern = Pattern.compile(REGEX_RESOLUTION);
        Matcher matcher = pattern.matcher(url);
        return matcher.replaceAll("-" + resolutionTag);
    }

    /**
     * Get the current resolution from the URL FIXME - Update pattern compile to get only the
     * resolution tag
     *
     * @return String or null
     */
    private static String getCurrentResolution(String url) {
        String resolution = null;
        Pattern pattern = Pattern.compile(DarwinRegex.REGEX_RESOLUTION_TAG);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            resolution = matcher.group(0).replace("-", "").replace(".", "");
        }
        return resolution;
    }

}
