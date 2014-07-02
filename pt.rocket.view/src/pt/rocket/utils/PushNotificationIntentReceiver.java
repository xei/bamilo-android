package pt.rocket.utils;



import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.view.SplashScreenActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class PushNotificationIntentReceiver extends BroadcastReceiver {
    
    private static final String TAG = "PushNotificationIntentReceiver";
    
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();   

        android.util.Log.d(TAG, "RECEIVED NOTIFICATION: " + action);
        
		if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {   
		    android.util.Log.d(TAG, "PROCESS RECEIVED NOTIFICATION");
		    String productUrl = intent.getStringExtra(ConstantsIntentExtra.CONTENT_URL);
			// for example, you can start an activity and send the msg as an extra.
            Intent launch = new Intent(intent);
            launch.setAction(Intent.ACTION_MAIN);
            launch.setClass(UAirship.shared().getApplicationContext(), SplashScreenActivity.class);
            launch.putExtra(ConstantsIntentExtra.CONTENT_URL, productUrl);
			launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			
			UAirship.shared().getApplicationContext().startActivity(launch);   
		}
	}

}