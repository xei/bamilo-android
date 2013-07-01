package pt.rocket.framework.objects;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

public class SearchSuggestion implements IJSONSerializable {
	private final static String TAG = SearchSuggestion.class.getSimpleName();
	

	private String result;
	private int value;
	
	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		try {
			@SuppressWarnings("unchecked")
			Iterator<String> keys = (Iterator<String>)jsonObject.keys();
			result = (String) keys.next();
			value = jsonObject.getInt(result);
		
		} catch ( JSONException e ) {
			Log.d( TAG, "error parsing json: ", e);
			return false;
		}
		return true;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}
	
	public String getResult() {
		return result;
	}
	
	public int getResultValue() {
		return value;
	}
	
}
