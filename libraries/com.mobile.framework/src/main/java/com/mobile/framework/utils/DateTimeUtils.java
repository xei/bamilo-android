package com.mobile.framework.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by spereira on 3/27/15.
 */
public class DateTimeUtils {

    /**
     * Return the current date and time as string "yyyy-MM-dd HH:mm:ss"
     * @return String
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
