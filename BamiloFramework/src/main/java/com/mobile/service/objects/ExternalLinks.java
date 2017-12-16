package com.mobile.service.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/*
 * Created by msilva on 4/19/16.
 * This Class will hold the External Links Section elements that currently will be displayed on the NavigationCategoryFragment
*/

public class ExternalLinks implements IJSONSerializable, Parcelable {

    // The label
    private String mLabel;
    // The external link
    private String mLink;
    // The image
    private String mImage;

    public ExternalLinks(){}


    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mLabel = jsonObject.getString(RestConstants.LABEL);
        mLink = jsonObject.getString(RestConstants.EXTERNAL_LINK_ANDROID);
        mImage = jsonObject.getString(RestConstants.IMAGE);

        return true;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getLink() {
        return mLink;
    }

    public String getImage() {
        return mImage;
    }


    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected ExternalLinks(Parcel in) {
        mLabel = in.readString();
        mLink = in.readString();
        mImage = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mLabel);
        dest.writeString(mLink);
        dest.writeString(mImage);
    }

    public static final Creator<ExternalLinks> CREATOR = new Creator<ExternalLinks>() {
        @Override
        public ExternalLinks createFromParcel(Parcel in) {
            return new ExternalLinks(in);
        }

        @Override
        public ExternalLinks[] newArray(int size) {
            return new ExternalLinks[size];
        }
    };


}
