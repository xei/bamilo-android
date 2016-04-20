package com.mobile.newFramework.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Class used to represent the redirect information.
 * @author spereira
 */
public class RedirectInfo implements Parcelable {

    @SerializedName("html")
    private String mHtml;
    @SerializedName("android_link")
    private String mLink;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public RedirectInfo() {
        // ...
    }

    public String getHtml() {
        return mHtml;
    }

    public String getLink() {
        return mLink;
    }

    /*
     * ###### PARCELABLE ######
     */

    public RedirectInfo(Parcel in) {
        mHtml = in.readString();
        mLink = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mHtml);
        dest.writeString(mLink);
    }

    public static final Creator<RedirectInfo> CREATOR = new Creator<RedirectInfo>() {
        @Override
        public RedirectInfo createFromParcel(Parcel in) {
            return new RedirectInfo(in);
        }

        @Override
        public RedirectInfo[] newArray(int size) {
            return new RedirectInfo[size];
        }
    };
}
