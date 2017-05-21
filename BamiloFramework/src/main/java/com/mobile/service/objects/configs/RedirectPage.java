package com.mobile.service.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Class used to represent the redirect information.
 * @author spereira
 */
public class RedirectPage implements Parcelable {

    @SerializedName("html")
    private String mHtml;
    @SerializedName("android_link")
    private String mLink;

    /**
     * Empty constructor
     */
    public RedirectPage() {
        mHtml = "";
        mLink = "";
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

    public RedirectPage(Parcel in) {
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

    public static final Creator<RedirectPage> CREATOR = new Creator<RedirectPage>() {
        @Override
        public RedirectPage createFromParcel(Parcel in) {
            return new RedirectPage(in);
        }

        @Override
        public RedirectPage[] newArray(int size) {
            return new RedirectPage[size];
        }
    };
}
