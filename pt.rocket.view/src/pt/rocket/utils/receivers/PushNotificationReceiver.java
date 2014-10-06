//package pt.rocket.utils.receivers;
//
//
//
//import pt.rocket.constants.BundleConstants;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import de.akquinet.android.androlog.Log;
//
///**
// * TODO: Implement this receiver if you need catch data from Ad4Push message
// */
//public class PushNotificationReceiver extends BroadcastReceiver {
//	
//    private static final String TAG = PushNotificationReceiver.class.getSimpleName();
//    
//    private static final String UTM_PREFIX = "utm_campaign=push_";
//    
//    //private static final String COMPLETE_UTM = "utm_campaign=%s&utm_source=push&utm_medium=referrer";
//
//    /*
//     * (non-Javadoc)
//     * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
//     */
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Log.i(TAG, "ON RECEIVE");
//        // Get action
//        String action = intent.getAction();
//        Log.i(TAG, "INTENT ACTION: " + action);
//        // Get data
//        //Bundle bundle = intent.getExtras();
//        //parseBundle(bundle);
//    }
//    
//    
//    @SuppressWarnings("unused")
//    private void parseBundle(Bundle bundle) {
//        if (null != bundle) {       
//            
//            //There are two alternative keys for the utm
//            if (bundle.containsKey(BundleConstants.UTM) || bundle.containsKey(BundleConstants.UTM_ALTERNATIVE)) {
//                String utm;
//                if(bundle.containsKey(BundleConstants.UTM)) {
//                    utm = bundle.getString(BundleConstants.UTM);
//                } else {
//                    utm = bundle.getString(BundleConstants.UTM_ALTERNATIVE);
//                }
//                String id = getCapaignString(utm);
//                Log.i("deeplinking", "BUNDLE -> The original UTM is " + utm + " and the parsed one is " + id);
//            }
//
//            String deepLinkUrl = bundle.getString(BundleConstants.DEEPLINKING_PAGE_INDICATION);
//            Log.i("deeplinking", "BUNDLE ->  navRequest "+deepLinkUrl);
//        }
//    }
//    
//
//    private String getCapaignString(String utm) {
//        String campaignId;
//        if (utm.contains(UTM_PREFIX)) {
//            campaignId = utm.replaceFirst(UTM_PREFIX, "");
//        } else {
//            campaignId = utm;
//        }
//
//        //return String.format(COMPLETE_UTM, campaignId);
//        return campaignId;
//    }	
//	
//}