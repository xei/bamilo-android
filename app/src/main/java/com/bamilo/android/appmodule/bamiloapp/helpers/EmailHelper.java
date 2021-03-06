package com.bamilo.android.appmodule.bamiloapp.helpers;

import android.util.Log;

/**
 * Created by Narbeh M. on 4/29/17.
 */

public final class EmailHelper {
    public static String getHost(String email) {
        assert email.length() > 0;

        try {
            String[] emailComponents = email.split("@");
            return emailComponents[1].split("\\.")[0];
        } catch (Exception e) {
            return null;
        }
    }
}
