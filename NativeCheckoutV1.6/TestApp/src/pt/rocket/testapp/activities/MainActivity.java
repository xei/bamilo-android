package pt.rocket.testapp.activities;

import pt.rocket.framework.service.IRemoteService;
import pt.rocket.framework.service.RemoteService;
import pt.rocket.framework.utils.Constants;
import pt.rocket.testapp.R;
import pt.rocket.testapp.helper.GetCategoriesHelper;
import pt.rocket.testapp.singelton.ServiceSingelton;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private IRemoteService mService;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		bindService(new Intent(this, RemoteService.class), mConnection, Context.BIND_AUTO_CREATE);
		
		findViewById(R.id.button1).setOnClickListener(
				new View.OnClickListener() {
					/* (non-Javadoc)
					 * @see android.view.View.OnClickListener#onClick(android.view.View)
					 */
					@Override
					public void onClick(View v) {
						proceedToListOfChoices();
					}
				});
	}
	
	public ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service. We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            Log.i(TAG, "onServiceConnected");
            mService = IRemoteService.Stub.asInterface(service);
            ServiceSingelton.getInstance().setService(mService);

//            try {
//                mService.registerCallback(mCallback);
//            } catch (RemoteException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }

            
        }
    };
    
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i(TAG,"Unbind Service called");
        unbindService(mConnection);
    }
	
	public void proceedToListOfChoices() {
	    Intent intent = new Intent(MainActivity.this,
                CategoriesActivity.class);
        startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
