package pt.rocket.utils;



import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.view.SplashScreen;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.analytics.tracking.android.Log;
import com.urbanairship.UAirship;
import com.urbanairship.push.PushManager;

public class PushNotificationIntentReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();   

		Log.d("received");
		
		if (action.equals(PushManager.ACTION_NOTIFICATION_OPENED)) {   
		    String productUrl = intent.getStringExtra(ConstantsIntentExtra.CONTENT_URL);
			// for example, you can start an activity and send the msg as an extra.
            Intent launch = new Intent(intent);
            launch.setAction(Intent.ACTION_MAIN);
            launch.setClass(UAirship.shared().getApplicationContext(), SplashScreen.class);
            launch.putExtra(ConstantsIntentExtra.CONTENT_URL, productUrl);
			launch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			
			UAirship.shared().getApplicationContext().startActivity(launch);   
		}
	}

}