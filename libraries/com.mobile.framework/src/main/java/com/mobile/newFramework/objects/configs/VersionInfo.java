package com.mobile.newFramework.objects.configs;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class VersionInfo implements IJSONSerializable, Parcelable {

    private final static String TAG = VersionInfo.class.getSimpleName();

    //private final static String JSON_VERSION_TAG = "version";

    private final HashMap<String, Version> mVersions;

    public VersionInfo() {
        mVersions = new HashMap<>();
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        try {

            JSONObject versionInfo = jsonObject.getJSONObject(RestConstants.JSON_VERSION_TAG);

            @SuppressWarnings("unchecked")
            Iterator<String> iter = versionInfo.keys();
            while( iter.hasNext()) {
                String packageName = iter.next();
                JSONObject packageVersion = versionInfo.getJSONObject( packageName );
                Version version = new Version();
                version.initialize(packageVersion);
                mVersions.put( packageName, version);
            }
        } catch (JSONException e ) {
//            Log.e(TAG, "error parsing json: ", e);
            return false;
        }

        return true;
    }
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    public void addEntry(String key, Version version) {
        mVersions.put(key, version);
    }

    public HashMap<String, Version> getMap() {
        return mVersions;
    }

    public Version getEntryByKey(String key) {
        return mVersions.get(key);
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(mVersions);
    }

    private VersionInfo(Parcel in) {
        mVersions = new HashMap<>();
        in.readMap(mVersions, Version.class.getClassLoader());
    }

    public static final Parcelable.Creator<VersionInfo> CREATOR = new Parcelable.Creator<VersionInfo>() {
        public VersionInfo createFromParcel(Parcel in) {
            return new VersionInfo(in);
        }

        public VersionInfo[] newArray(int size) {
            return new VersionInfo[size];
        }
    };
}

