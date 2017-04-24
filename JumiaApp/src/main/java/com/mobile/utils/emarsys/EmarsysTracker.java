package com.mobile.utils.emarsys;

import com.mobile.app.JumiaApplication;
import com.mobile.libraries.emarsys.EmarsysMobileEngage;
import com.mobile.libraries.emarsys.EmarsysMobileEngageResponse;
import com.mobile.utils.pushwoosh.PushWooshEvent;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.pushwoosh.inapp.InAppFacade;

import java.util.Date;
import java.util.HashMap;

import static com.mobile.newFramework.Darwin.context;

/**
 * Created by shahrooz on 4/12/17.
 */

public class EmarsysTracker {

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
        sendToEmarsys("Login", values);
    }

    public static void signUp(BaseActivity activity ,String method , boolean success , String email) {
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Method, method);
        values.put(PushWooshEvent.Success, success);
        values.put(PushWooshEvent.EmailDomain, email);
        sendToEmarsys("SignUp", values);
    }

    public static void openApp(BaseActivity activity, boolean success){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        sendToEmarsys("OpenApp",values);
    }

    public static void addToCart(BaseActivity activity,  boolean success , String sku , Long basketValue){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        values.put(PushWooshEvent.SKU,sku);
        values.put(PushWooshEvent.BasketValue,basketValue);
        sendToEmarsys("AddToCart",values);
    }

    public static void addToFavorites(BaseActivity activity, boolean success , String categoryUrlKey){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        values.put(PushWooshEvent.CategoryUrlKey,categoryUrlKey);
        sendToEmarsys("AddToFavorites",values);

    }
    public static void purchase(BaseActivity activity, boolean success , String categories , Long basketValue){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        values.put(PushWooshEvent.Categories,categories);
        values.put(PushWooshEvent.BasketValue,basketValue);
        sendToEmarsys("Purchase",values);

    }

    public static void search(BaseActivity activity,String categoryUrlKey,String keyword){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Keywords,keyword);
        values.put(PushWooshEvent.CategoryUrlKey,categoryUrlKey);
        sendToEmarsys("Search",values);
    }

    public static void viewProduct(BaseActivity activity,String categoryUrlKey,Long price){
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Price,price);
        values.put(PushWooshEvent.CategoryUrlKey,categoryUrlKey);
        sendToEmarsys("ViewProduct",values);

    }

    public static void logOut(BaseActivity activity, boolean success) {
        HashMap<String, Object> values = setDefaultAttributes();
        values.put(PushWooshEvent.Success,success);
        sendToEmarsys("Logout",values);

    }



    private static void sendToEmarsys(String event ,HashMap<String, Object> attributes )
    {
        EmarsysMobileEngageResponse emarsysMobileEngageResponse = new EmarsysMobileEngageResponse() {
            @Override
            public void EmarsysMobileEngageResponse(boolean success) {

            }
        };
        EmarsysMobileEngage emarsysMobileEngage = new EmarsysMobileEngage(context);
        emarsysMobileEngage.sendCustomEvent(event,attributes,emarsysMobileEngageResponse);
    }
}
