package com.mobile.service.objects.home;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Class that represents a teaser campaign
 *
 * @author sergiopereira
 */
public class TeaserCampaign implements Parcelable {

    private String name;

    private String id;

    /**
     * Empty constructor
     */
    public TeaserCampaign(@NonNull String title, @NonNull String id) {
        this.name = title;
        this.id = id;
    }

    public String getTitle() {
        return name;
    }

    public String getId() {
        return id;
    }

    /**
     * ########### Parcelable ###########
     */
	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    /**
     * Parcel constructor
     */
    public TeaserCampaign(Parcel in) {
        name = in.readString();
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<TeaserCampaign> CREATOR = new Parcelable.Creator<TeaserCampaign>() {
        public TeaserCampaign createFromParcel(Parcel in) {
            return new TeaserCampaign(in);
        }

        public TeaserCampaign[] newArray(int size) {
            return new TeaserCampaign[size];
        }
    };

}
