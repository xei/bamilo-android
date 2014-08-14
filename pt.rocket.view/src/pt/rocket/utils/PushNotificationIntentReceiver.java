package pt.rocket.utils;



import pt.rocket.constants.BundleConstants;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.view.SplashScreenActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

public class PushNotificationIntentReceiver extends BroadcastReceiver {
	
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		String action = intent.getAction();   
//
//        android.util.Log.d(TAG, "RECEIVED NOTIFICATION: " + action);
//        
//		if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {   
//		    android.util.Log.d(TAG, "PROCESS RECEIVED NOTIFICATION");
//		    String productUrl = intent.getStringExtra(ConstantsIntentExtra.CONTENT_URL);
//			// for example, you can start an activity and send the msg as an extra.
//            Intent launch = new Intent(intent);
//            launch.setAction(Intent.ACTION_MAIN);
//            launch.setClass(UAirship.shared().getApplicationContext(), SplashScreenActivity.class);
//            launch.putExtra(ConstantsIntentExtra.CONTENT_URL, productUrl);
//			launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
//			
//			UAirship.shared().getApplicationContext().startActivity(launch);   
//		}
//	}

	
	
    private static final String TAG = "PushNotificationIntentReceiver";
    private static final String UTM_PREFIX = "utm_campaign=push_";
//  private static final String COMPLETE_UTM = "utm_campaign=%s&utm_source=push&utm_medium=referrer";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.i(TAG, "BUNDLE -> RECEIVED NOTIFICATION: " + action);

        Bundle bundle = intent.getExtras();
        
        

        if (null != bundle) {       
            
            //There are two alternative keys for the utm
            if (bundle.containsKey(BundleConstants.UTM) || bundle.containsKey(BundleConstants.UTM_ALTERNATIVE)) {
                String utm;
                
                if(bundle.containsKey(BundleConstants.UTM)) {
                    utm = bundle.getString(BundleConstants.UTM);
                } else {
                    utm = bundle.getString(BundleConstants.UTM_ALTERNATIVE);
                }

                String id = getCapaignString(utm);

                Log.i("deeplinking", "BUNDLE -> The original UTM is " + utm + " and the parsed one is " + id);
//              GoogleAnalyticsManager.get(context).setCampaign(id);
//                CustomApplication.INSTANCE.setCampaignId(id);
            }

            
            String deepLinkUrl = bundle.getString(BundleConstants.DEEPLINKING_PAGE_INDICATION);
//            NavigationRequest navRequest = null;
//            
            Log.i("deeplinking", "BUNDLE ->  navRequest "+deepLinkUrl);
//            if (deepLinkUrl != null){
////                navRequest = DeepLinkManager.parse(deepLinkUrl);
//                Intent launch = new Intent(intent);
//                launch.setAction(Intent.ACTION_MAIN);
//                launch.setClass(context, SplashScreenActivity.class);
//                launch.putExtra(ConstantsIntentExtra.CONTENT_URL, deepLinkUrl);
//                launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
//            
//                context.startActivity(launch);
//            }
//        
//            // Go through here to pass the bundle to the mainfragment and process the 
//            //received information
//            CustomApplication.INSTANCE.setDeepLinkRequest(navRequest);
            
        }
    }
    
    

    private static final String getCapaignString(String utm) {
        String campaignId;
        if (utm.contains(UTM_PREFIX)) {
            campaignId = utm.replaceFirst(UTM_PREFIX, "");
        } else {
            campaignId = utm;
        }

        //return String.format(COMPLETE_UTM, campaignId);
        return campaignId;
    }	
	
}