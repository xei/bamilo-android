package pt.rocket.framework.worker;

import de.akquinet.android.androlog.Log;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.framework.utils.Constants;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ResultReceiver;

/**
 * Worker class that deals with the request call of the RestClientSingleton
 * @author josedourado
 *
 */
public class RequestWorker implements Runnable{
	
	private static final String TAG=RequestWorker.class.getSimpleName();
	private Uri uri;
	private RequestType type;
	private ContentValues formData;
	private Handler mHandler;
	private Bundle mBundle;

	
	
	public RequestWorker(Bundle bundle,Handler handler){
		uri = Uri.parse(bundle.getString(Constants.BUNDLE_URL_KEY));
		Log.i(TAG,"Executing => "+uri.toString());
		type =(RequestType) bundle.getSerializable(Constants.BUNDLE_TYPE_KEY);
		if(type==RequestType.POST)
			formData = bundle.getParcelable(Constants.BUNDLE_FORM_DATA_KEY);
		mHandler = handler;
		mBundle=bundle;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		RestClientSingleton client = RestClientSingleton.getSingleton();
		switch(type){
		case GET:
			client.executeGetRestUrlString(uri, mHandler, mBundle);
			break;
		case POST:
			client.executePostRestUrlString(uri,formData, mHandler, mBundle);
			break;
		default:
			//TODO return response in case the above cases do not apply
			break;
		}
		
	}
	

}
