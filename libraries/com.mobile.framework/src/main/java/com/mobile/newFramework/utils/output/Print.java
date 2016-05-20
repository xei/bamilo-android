package com.mobile.newFramework.utils.output;

import android.content.Context;

import de.akquinet.android.androlog.Log;

/**
 * Created by rsoares on 6/4/15.
 */
public class Print {

    public static final int TESTING_MODE = 1;

    public static final int ANDROID_MODE = 2;

    private static int defaultLogMode = ANDROID_MODE;

    public static void initializeTestingMode() {
        defaultLogMode = TESTING_MODE;
    }

    public static void initializeAndroidMode(Context context) {
        Log.init(context);
    }

    public static void i(String message) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.i(message);
        } else {
            System.out.println(message);
        }
    }

    public static void i(String tag, String message) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.i(tag, message);
        } else {
            System.out.println(tag + ": " + message);
        }
    }

    public static void i(String tag, String message, Throwable exception) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.i(tag, message, exception);
        } else {
            System.out.println(message);
            System.err.println(exception.toString());
        }
    }

    public static void d(String message) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.d(message);
        } else {
            System.out.println(message);
        }
    }

    public static void d(String tag, String message) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.d(tag, message);
        } else {
            System.out.println(tag + ": " + message);
        }
    }

    public static void d(String tag, String message, Throwable exception) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.d(tag, message, exception);
        } else {
            System.out.println(message);
            System.err.println(exception.toString());

        }
    }

    public static void w(String message) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.w(message);
        } else {
            System.out.println(message);
        }
    }

    public static void w(String tag, String message) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.w(tag, message);
        } else {
            System.out.println(message);
        }
    }

    public static void w(String message, Throwable exception) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.w(message, exception);
        } else {
            System.out.println(message);
            System.err.println(exception.toString());

        }
    }

    public static void w(String tag, String message, Throwable exception) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.w(tag, message, exception);
        } else {
            System.out.println(message);
            System.err.println(exception.toString());

        }
    }

    public static void e(String message) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.e(message);
        } else {
            System.out.println(message);
        }
    }

    public static void e(String tag, String message) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.e(tag, message);
        } else {
            System.out.println(message);
        }
    }

    public static void e(String tag, String message, Throwable exception) {
        if (defaultLogMode == ANDROID_MODE) {
            Log.e(tag, message, exception);
        } else {
            System.out.println(message);
            System.err.println(exception.toString());
        }
    }

}
