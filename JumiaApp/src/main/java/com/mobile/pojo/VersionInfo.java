//package com.mobile.pojo;
//
//import com.mobile.framework.interfaces.IJSONSerializable;
//import com.mobile.framework.rest.RestConstants;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Iterator;
//
//import de.akquinet.android.androlog.Log;
//
//public class VersionInfo implements IJSONSerializable {
//
//	private final static String TAG = VersionInfo.class.getSimpleName();
//
//	//private final static String JSON_VERSION_TAG = "version";
//
//	private final HashMap<String, Version> mVersions;
//
//	public VersionInfo() {
//		mVersions = new HashMap<>();
//	}
//
//	@Override
//	public boolean initialize(JSONObject jsonObject) {
//		try {
//
//			JSONObject versionInfo = jsonObject.getJSONObject(RestConstants.JSON_VERSION_TAG);
//
//			@SuppressWarnings("unchecked")
//			Iterator<String> iter = versionInfo.keys();
//			while( iter.hasNext()) {
//				String packageName = iter.next();
//				JSONObject packageVersion = versionInfo.getJSONObject( packageName );
//				Version version = new Version();
//				version.initialize(packageVersion);
//				mVersions.put( packageName, version);
//			}
//		} catch (JSONException e ) {
//			Log.e(TAG, "error parsing json: ", e);
//			return false;
//		}
//
//		return true;
//	}
//
//	public JSONObject toJSON() {
//		return null;
//	}
//
//	public void addEntry(String key, Version version) {
//		mVersions.put(key, version);
//	}
//
//	public HashMap<String, Version> getMap() {
//		return mVersions;
//	}
//
//	public Version getEntryByKey(String key) {
//		return mVersions.get(key);
//	}
//
//}
