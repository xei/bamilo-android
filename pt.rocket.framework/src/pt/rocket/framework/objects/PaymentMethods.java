/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.rest.RestConstants;

import de.akquinet.android.androlog.Log;

/**
 * @author nutzer2
 * 
 */
public class PaymentMethods implements IJSONSerializable, Parcelable {

	private static final String TAG = PaymentMethods.class.getSimpleName();

	
	// Form
	// Cart
	// Messages
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}

	



}
