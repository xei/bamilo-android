package com.bamilo.android.framework.service.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by spereira on 3/27/15.
 */
public class DateTimeUtils {

    public static final int UNIT_SEC_TO_MILLIS = 1000;


    /**
     * Return the current date and time as string "yyyy-MM-dd HH:mm:ss"
     * @return String
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * Return the time as string "HH:mm:ss" from millis
     * @return String
     */
    public static String getTimeFromMillis(long millis) {
        // Remove millis
        millis /= 1000;
        // Get hours
        long hours = millis / 3600;
        millis %= 3600;
        // Get minutes
        long minutes = millis / 60;
        millis %= 60;
        // Get seconds
        long seconds = millis;
        // Build string
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
