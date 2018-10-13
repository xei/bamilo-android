package com.bamilo.android.appmodule.bamiloapp.utils.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.adjust.sdk.AdjustReferrerReceiver;
import com.google.android.gms.analytics.CampaignTrackingReceiver;
import com.bamilo.android.framework.service.tracking.gtm.GTMManager;

import java.util.HashMap;
import java.util.Map;

public class InstallReceiver extends BroadcastReceiver {
    
    private static final String TAG = InstallReceiver.class.getSimpleName();
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        
        if(bundle != null) {
            String referrerStr = bundle.getString("referrer");
            if(!TextUtils.isEmpty(referrerStr)) {
                try {
                    Map<String, String> queryMap = getQueryMap(referrerStr);
                    String utmSource = queryMap.get("utm_source");
                    String utmMedium = queryMap.get("utm_medium");
                    String utmCampaign = queryMap.get("utm_campaign");

                    saveCampaignParameters(context, GTMManager.CAMPAIGN_ID_KEY, utmCampaign);
                    saveCampaignParameters(context, GTMManager.CAMPAIGN_SOURCE, utmSource);
                    saveCampaignParameters(context, GTMManager.CAMPAIGN_MEDIUM, utmMedium);
                    saveCampaignParameters(context, GTMManager.IS_GA_CAMPAIGN_SET, "No");                   
                    saveCampaignParameters(context, GTMManager.IS_GTM_CAMPAIGN_SET, "YES");        
                    saveCampaignParameters(context, GTMManager.IS_REFERRER_CAMPAIGN_SET, "Yes");   
                } catch (Exception ignored) {
                }
            }
        }
        
       
        // Google Analytics
        new CampaignTrackingReceiver().onReceive(context, intent);
        // Adjust
        new AdjustReferrerReceiver().onReceive(context, intent);
    }
    
    public static Map<String, String> getQueryMap(String query) {  
        String[] params = query.split("&");  
        Map<String, String> map = new HashMap<>();
        for (String param : params) {  
            String name = param.split("=")[0];  
            String value = param.split("=")[1];  
            map.put(name, value);  
        }  
        return map;  
    }
    
    private void saveCampaignParameters(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
    }
}