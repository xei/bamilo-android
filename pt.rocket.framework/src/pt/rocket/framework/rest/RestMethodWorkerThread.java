package pt.rocket.framework.rest;

import pt.rocket.framework.Darwin;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import de.akquinet.android.androlog.Log;

/**
 * Worker responsible for fetching the data from the remote server.
 * 
 * @author Jacob Zschunke
 * 
 */
class RestMethodWorkerThread implements Runnable {

	private static final String TAG = RestMethodWorkerThread.class.getSimpleName();

	private Uri uri;
	private int type;
	// private HashMap<String, String> formData;
	private ContentValues formData;
	private final ResultReceiver resultReceiver;

	private Bundle metaData;

	public RestMethodWorkerThread(Uri uri, ContentValues formData, int type, ResultReceiver resultReceiver,
			Bundle metaData) {
		this.uri = uri;
		this.type = type;
		this.formData = formData;
		this.resultReceiver = resultReceiver;
		this.metaData = metaData;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		if(Darwin.logDebugEnabled) {
			Log.d(TAG, "Start executing uri: " + uri);
		}
			
		
		switch (type) {
		case RestContract.METHOD_GET:
			// Constants.LogDebug("Going to perform a get");
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					RestClientSingleton.getSingleton().executeGetRestUrlString(uri, resultReceiver, metaData);
					
				}
			}).run();
			
			break;
		case RestContract.METHOD_POST:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					if(Darwin.logDebugEnabled) {
						Log.i("METHOD_POST", "METHOD_POST : " + uri  );
					}
					
					RestClientSingleton.getSingleton().executePostRestUrlString(uri, formData, resultReceiver, metaData);
					
				}
			}).run();
			
			break;
		case RestContract.METHOD_PUT:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					RestClientSingleton.getSingleton().executePutRestUrlString(uri, formData, resultReceiver, metaData);
					
				}
			}).run();
			
			break;
		case RestContract.METHOD_DELETE:
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					RestClientSingleton.getSingleton().executeDeleteRestUrlString(uri, resultReceiver, metaData);
				}
			}).run();
			
			break;
		}
		if(Darwin.logDebugEnabled) {
			Log.d(TAG, "Finished executing uri: " + uri);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formData == null) ? 0 : formData.hashCode());
		result = prime * result + ((resultReceiver == null) ? 0 : resultReceiver.hashCode());
		result = prime * result + type;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RestMethodWorkerThread other = (RestMethodWorkerThread) obj;
		if (formData == null) { 
			if (other.formData != null) {
				return false;
			}
		} else if (!formData.equals(other.formData)) {
			return false;
		}
		if (resultReceiver == null) {
			if (other.resultReceiver != null) {
				return false;
			}
		} else if (!resultReceiver.equals(other.resultReceiver)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		if (uri == null) {
			if (other.uri != null) {
				return false;
			}
		} else if (!uri.equals(other.uri)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RestMethodWorkerThread [uri=" + uri + ", type=" + type + ", formData=" + formData + "]";
	}

}
