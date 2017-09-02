package com.mobile.service.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by rsoares on 5/25/15.
 */
public class Sections extends LinkedList<Section> implements IJSONSerializable, Parcelable {

    public Sections(){}

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray sectionsJSONArray = jsonObject.optJSONArray(RestConstants.DATA);
        for (int i = 0; i < sectionsJSONArray.length(); ++i) {
            JSONObject sessionObject = sectionsJSONArray.optJSONObject(i);
            Section section = new Section();
            section.initialize(sessionObject);
            this.add(section);
        }
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

    protected Sections(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Sections> CREATOR = new Parcelable.Creator<Sections>() {
        @Override
        public Sections createFromParcel(Parcel in) {
            return new Sections(in);
        }

        @Override
        public Sections[] newArray(int size) {
            return new Sections[size];
        }
    };
}
