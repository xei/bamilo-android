package com.bamilo.android.appmodule.bamiloapp.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Narbeh M. on 4/30/17.
 */

public final class DateUtils {
    public static String getWebNormalizedDateTimeString(Date date) {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.ENGLISH);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return String.valueOf(dateFormatGmt.format(date));
    }
}
