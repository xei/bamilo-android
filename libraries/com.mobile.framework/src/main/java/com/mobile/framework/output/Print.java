package com.mobile.framework.output;

import android.content.Context;
/**
 * Created by rsoares on 6/4/15.
 */
public class Print {

    public static final int TESTING_MODE = 1;
    public static final int ANDROID_MODE = 2;

    private int mode;

    private static Print log;

    private Print(int mode){
        this.mode = mode;
    }

    public static void initializeTestingMode(){
        log = new Print(TESTING_MODE);
    }

    public static void initializeAndroidMode(Context context){
        log = new Print(ANDROID_MODE);
        de.akquinet.android.androlog.Log.init(context);
    }

    public static void i(String tag, String message){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.i(tag,message);
        } else {
            System.out.println(message);
        }
    }

    public static void i(String tag, String message, Throwable exception){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.i(tag, message, exception);
        } else {
            System.out.println(message);
            System.out.println(exception);

        }
    }

    public static void d( String message){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.d(message);
        } else {
            System.out.println(message);
        }
    }

    public static void d(String tag, String message){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.d(tag, message);
        } else {
            System.out.println(message);
        }
    }

    public static void d(String tag, String message, Throwable exception){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.d(tag, message, exception);
        } else {
            System.out.println(message);
            System.out.println(exception);

        }
    }

    public static void w( String message){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.w(message);
        } else {
            System.out.println(message);
        }
    }

    public static void w(String tag, String message){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.w(tag, message);
        } else {
            System.out.println(message);
        }
    }

    public static void w(String tag, String message, Throwable exception){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.w(tag, message, exception);
        } else {
            System.out.println(message);
            System.out.println(exception);

        }
    }

    public static void e( String message){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.e(message);
        } else {
            System.out.println(message);
        }
    }

    public static void e(String tag, String message){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.e(tag, message);
        } else {
            System.out.println(message);
        }
    }

    public static void e(String tag, String message, Throwable exception){
        if(log.mode == ANDROID_MODE){
            de.akquinet.android.androlog.Log.e(tag, message, exception);
        } else {
            System.out.println(message);
            System.out.println(exception);
        }
    }

}
