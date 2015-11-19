package com.mobile.newFramework.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 5/27/15.
 */
public class ApiInformation implements IJSONSerializable, Parcelable {
    private Sections sections;
    private VersionInfo versionInfo;

    public ApiInformation(){
        sections = new Sections();
        versionInfo = new VersionInfo();
    }

    public ApiInformation(ApiInformation apiInformation){
        sections = apiInformation.sections;
        versionInfo = apiInformation.versionInfo;
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        sections.initialize(jsonObject);
        versionInfo.initialize(jsonObject);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public Sections getSections() {
        return sections;
    }

    public VersionInfo getVersionInfo() {
        return versionInfo;
    }

    protected ApiInformation(Parcel in) {
        sections = (Sections) in.readValue(Sections.class.getClassLoader());
        versionInfo = (VersionInfo) in.readValue(VersionInfo.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(sections);
        dest.writeValue(versionInfo);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ApiInformation> CREATOR = new Parcelable.Creator<ApiInformation>() {
        @Override
        public ApiInformation createFromParcel(Parcel in) {
            return new ApiInformation(in);
        }

        @Override
        public ApiInformation[] newArray(int size) {
            return new ApiInformation[size];
        }
    };
}
