package com.mobile.utils.pushwoosh;

import android.content.Context;
import android.net.ConnectivityManager;

import com.mobile.app.JumiaApplication;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.pushwoosh.inapp.InAppFacade;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by shahrooz on 4/12/17.
 */

public class PushWooshTracker {

    public static HashMap<String, Object> setDefaultAttributes()
    {

        HashMap<String, Object> values = new HashMap<>();
        values.put(PushWooshEvent.AppVersion,android.os.Build.VERSION.RELEASE);
        values.put(PushWooshEvent.Platform,"android");
        values.put(PushWooshEvent.Connection,UIUtils.networkType(JumiaApplication.INSTANCE.getApplicationContext()));
        values.put(PushWooshEvent.Date,new Date());
        return values;
    }

    public static void login(BaseActivity activity ,String method , boolean success , String email){
        HashMap<String, Object> values = setDefaultAttributes();
        sendEvent(activity,"Login",values);
    }

    public static void signUp(BaseActivity activity ,String method , boolean success , String email){
        HashMap<String, Object> values = setDefaultAttributes();
        sendEvent(activity,"SignUp",values);
    }

    public static void openApp(BaseActivity activity, boolean success){
        HashMap<String, Object> values = setDefaultAttributes();
        sendEvent(activity,"OpenApp",values);
    }

    public static void addToFavorites(BaseActivity activity, boolean success , String categoryUrlKey){
        HashMap<String, Object> values = setDefaultAttributes();
        sendEvent(activity,"AddToFavorites",values);
    }
    public static void purchase(BaseActivity activity, boolean success , String categories , Long basketValue){
        HashMap<String, Object> values = setDefaultAttributes();
        sendEvent(activity,"Purchase",values);
    }

    public static void search(BaseActivity activity,String categoryUrlKey,String keyword){
        HashMap<String, Object> values = setDefaultAttributes();
        sendEvent(activity,"Search",values);
    }

    public static void viewProduct(BaseActivity activity,String categoryUrlKey,Long price){
        HashMap<String, Object> values = setDefaultAttributes();
        sendEvent(activity,"ViewProduct",values);
    }

    public static void logOut(BaseActivity activity, boolean success) {
        HashMap<String, Object> values = setDefaultAttributes();
        sendEvent(activity,"LogOut",values);
    }







    private static void sendEvent( BaseActivity activity , String eventName,HashMap<String, Object> attributes ){
        InAppFacade.postEvent(activity,eventName,attributes);
    }
}
