package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

import de.akquinet.android.androlog.Log;

public class Version implements IJSONSerializable {
	private final static String TAG = Version.class.getSimpleName();

//	private final static String JSON_MIN_VERSION_TAG = "min_version";
//	private final static String JSON_CUR_VERSION_TAG = "cur_version";

	private int minimumVersion;
	private int currentVersion;
	
	
	public Version() {
		minimumVersion = 0;
		currentVersion = 0;
	}
	
	public Version(int minVersion, int crrVersion) {
		minimumVersion = minVersion;
		currentVersion = crrVersion;
	}

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		try {

		minimumVersion = jsonObject.getInt(RestConstants.JSON_MIN_VERSION_TAG);
		currentVersion = jsonObject.getInt(RestConstants.JSON_CUR_VERSION_TAG);

		} catch (JSONException e ) {
			Log.e(TAG, "error parsing json: ", e);
			return false;
		}
		
		return true;
	}
	@Override
	public JSONObject toJSON() {
		return null;
	}

	public int getMinimumVersion() {
		return minimumVersion;
	}
	
	public int getCurrentVersion() {
		return currentVersion;
	}
	

}
