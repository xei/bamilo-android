package com.mobile.newFramework.objects.configs;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class Version implements IJSONSerializable, Parcelable {
    private final static String TAG = Version.class.getSimpleName();

    // private final static String JSON_MIN_VERSION_TAG = "min_version";
    // private final static String JSON_CUR_VERSION_TAG = "cur_version";

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

            minimumVersion = jsonObject
                    .getInt(RestConstants.JSON_MIN_VERSION_TAG);
            currentVersion = jsonObject
                    .getInt(RestConstants.JSON_CUR_VERSION_TAG);

        } catch (JSONException e) {
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
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    public int getMinimumVersion() {
        return minimumVersion;
    }

    public int getCurrentVersion() {
        return currentVersion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(minimumVersion);
        dest.writeInt(currentVersion);

    }

    private Version(Parcel in) {
        minimumVersion = in.readInt();
        currentVersion = in.readInt();
    }

    public static final Parcelable.Creator<Version> CREATOR = new Parcelable.Creator<Version>() {
        public Version createFromParcel(Parcel in) {
            return new Version(in);
        }

        public Version[] newArray(int size) {
            return new Version[size];
        }
    };

}
