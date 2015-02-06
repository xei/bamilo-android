package com.mobile.framework.worker;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.rest.RestClientSingleton;
import com.mobile.framework.utils.Constants;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import de.akquinet.android.androlog.Log;
/**
 * Worker class that deals with the request call of the RestClientSingleton
 * @author josedourado
 *
 */
public class RequestWorker implements Runnable{
	
	private static final String TAG = RequestWorker.class.getSimpleName();
	private Uri uri;
	private RequestType type;
	private ContentValues formData;
	private Handler mHandler;
	private Bundle mBundle;
	private Context context;

	
	
	public RequestWorker(Bundle bundle,Handler handler, Context context){
		this.context = context;
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
		RestClientSingleton client = RestClientSingleton.getSingleton(context);	
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
