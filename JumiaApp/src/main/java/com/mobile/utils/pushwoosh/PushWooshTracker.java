package com.mobile.utils.pushwoosh;

import android.content.Context;
import android.net.ConnectivityManager;

import com.mobile.app.JumiaApplication;
import com.mobile.libraries.emarsys.EmarsysMobileEngage;
import com.mobile.libraries.emarsys.EmarsysMobileEngageResponse;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.pushwoosh.inapp.InAppFacade;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static com.mobile.newFramework.Darwin.context;

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
        values.put(PushWooshEvent.Method,method);
        values.put(PushWooshEvent.Success,success);
        values.put(PushWooshEvent.EmailDomain,email);
        sendEvent(activity,"Login",values);
        sendToEmarsys("Login", values);
    }

    public static void signUp(BaseActivity activity ,String method , boolean success , String email) {
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Method, method);
        values.put(PushWooshEvent.Success, success);
        values.put(PushWooshEvent.EmailDomain, email);
        sendEvent(activity, "SignUp", values);
        sendToEmarsys("SignUp", values);
    }

    public static void openApp(BaseActivity activity, boolean success){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        sendEvent(activity,"OpenApp",values);
        sendToEmarsys("OpenApp",values);
    }

    public static void addToCart(BaseActivity activity,  boolean success , String sku , Long basketValue){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        values.put(PushWooshEvent.SKU,sku);
        values.put(PushWooshEvent.BasketValue,basketValue);
        sendEvent(activity,"AddToCart",values);
        sendToEmarsys("AddToCart",values);
    }

    public static void addToFavorites(BaseActivity activity, boolean success , String categoryUrlKey){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        values.put(PushWooshEvent.CategoryUrlKey,categoryUrlKey);
        sendEvent(activity,"AddToFavorites",values);
        sendToEmarsys("AddToFavorites",values);

    }
    public static void purchase(BaseActivity activity, boolean success , String categories , Long basketValue){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        values.put(PushWooshEvent.Categories,categories);
        values.put(PushWooshEvent.BasketValue,basketValue);
        sendEvent(activity,"Purchase",values);
        sendToEmarsys("Purchase",values);

    }

    public static void search(BaseActivity activity,String categoryUrlKey,String keyword){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Keywords,keyword);
        values.put(PushWooshEvent.CategoryUrlKey,categoryUrlKey);
        sendEvent(activity,"Search",values);
        sendToEmarsys("Search",values);
    }

    public static void viewProduct(BaseActivity activity,String categoryUrlKey,Long price){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Price,price);
        values.put(PushWooshEvent.CategoryUrlKey,categoryUrlKey);
        sendEvent(activity,"ViewProduct",values);
        sendToEmarsys("ViewProduct",values);
    }

    public static void logOut(BaseActivity activity, boolean success) {
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        sendEvent(activity,"Logout",values);
        sendToEmarsys("Logout",values);
    }

    private static void sendEvent( BaseActivity activity , String eventName,HashMap<String, Object> attributes ) {
        InAppFacade.postEvent(activity,eventName,attributes);
    }

    private static void sendToEmarsys(String event ,HashMap<String, Object> attributes ) {
        EmarsysMobileEngageResponse emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
            @Override
            public void EmarsysMobileEngageResponse(boolean success) {}
        };
        EmarsysMobileEngage.getInstance(context).sendCustomEvent(event,attributes,emarsysMobileEngageResponse);
    }
}
