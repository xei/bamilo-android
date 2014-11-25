package pt.rocket.utils.receivers;


import java.util.HashMap;
import java.util.Map;

import pt.rocket.framework.tracking.GTMManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;

import com.ad4screen.sdk.ReferrerHandler;
import com.adjust.sdk.ReferrerReceiver;
import com.google.android.gms.analytics.CampaignTrackingReceiver;

import de.akquinet.android.androlog.Log;

public class InstallReceiver extends BroadcastReceiver {
    
    private static final String TAG = InstallReceiver.class.getSimpleName();
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received INSTALL_REFERRAL");
        new ReferrerReceiver().onReceive(context, intent);
                
        Bundle bundle = intent.getExtras();
        
        if(bundle != null) {
            String referrerStr = bundle.getString("referrer");
            if(!TextUtils.isEmpty(referrerStr)) {
                try {
                    Map<String, String> queryMap = getQueryMap(referrerStr);
                    String utmSource = queryMap.get("utm_source");
                    String utmMedium = queryMap.get("utm_medium");
                    String utmCampaign = queryMap.get("utm_campaign");
                    Log.d(TAG,"utm_source:"+utmSource);
                    Log.d(TAG,"utm_medium:"+utmMedium);
                    Log.d(TAG,"utm_campaign:"+utmCampaign);
                    Log.d("BETA", "InstallReceiver utm_campaign:"+utmCampaign);
                    Log.d("BETA", "InstallReceiver utm_medium:"+utmMedium);
                    Log.d("BETA", "InstallReceiver utm_source:"+ utmSource);
                    saveCampaignParameters(context, GTMManager.CAMPAIGN_ID_KEY, utmCampaign);
                    saveCampaignParameters(context, GTMManager.CAMPAIGN_SOURCE, utmSource);
                    saveCampaignParameters(context, GTMManager.CAMPAIGN_MEDIUM, utmMedium);
                    saveCampaignParameters(context, GTMManager.IS_GA_CAMPAIGN_SET, "No");                   
                    saveCampaignParameters(context, GTMManager.IS_GTM_CAMPAIGN_SET, "YES");        
                    saveCampaignParameters(context, GTMManager.IS_REFERRER_CAMPAIGN_SET, "Yes");   
                } catch (Exception e) {
                    Log.e(TAG, "Exception while getting utm parameters : ", e);
                }
            }
        } else {
            Log.d(TAG, "BUNDLE == NULL");
        }
        
       
        // Google Analytics
        new CampaignTrackingReceiver().onReceive(context, intent);
        new ReferrerHandler().onReceive(context, intent);
    }
    
    public static Map<String, String> getQueryMap(String query) {  
        String[] params = query.split("&");  
        Map<String, String> map = new HashMap<String, String>();  
        for (String param : params) {  
            String name = param.split("=")[0];  
            String value = param.split("=")[1];  
            map.put(name, value);  
        }  
        return map;  
    }
    
    private void saveCampaignParameters(Context context, String key, String value) {
        Log.d(TAG, "saving INSTALL_REFERRAL params, key: " + key + ", value : " + value);
        Log.d("BETA", "saving INSTALL_REFERRAL params, key: " + key + ", value : " + value);
        SharedPreferences settings = context.getSharedPreferences(GTMManager.GA_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }
    
}