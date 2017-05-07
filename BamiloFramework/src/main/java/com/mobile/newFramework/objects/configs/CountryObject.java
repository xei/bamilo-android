package com.mobile.newFramework.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Country Object class. Holds all the fields of the Country Object.
 */
public class CountryObject implements IJSONSerializable, Parcelable {

    protected static final String TAG = CountryObject.class.getName();

    private String name;
    private String url;
    private String flag;
    private String iso;
    private boolean forceHttps;
    private boolean isLive;
    private String userAgentToAccessDevServers;
    private Languages languages;
    /**
     * Empty Constructor
     */
    public CountryObject() {
        this.setCountryName("");
        this.setCountryUrl("");
        this.setCountryFlag("");
        this.setCountryIso("");
        this.setCountryForceHttps(false);
        this.setCountryIsLive(false);
    }

    @Override
    public String toString() {
        return "####################" +
                "\nname: " + name +
                "\nurl: " + url +
                "\nflag: " + flag +
                "\niso: " + iso +
                "\nhttps: " + forceHttps +
                "\nlive: " + isLive
                ;
    }

    /**
     * ########### Parcelable ###########
     */

	/*
     * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#describeContents()
	 */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(flag);
        dest.writeString(iso);
        dest.writeBooleanArray(new boolean[]{forceHttps, isLive});
        dest.writeString(userAgentToAccessDevServers);
    }

    /**
     * Parcel constructor
     */
    private CountryObject(Parcel in) {
        name = in.readString();
        url = in.readString();
        flag = in.readString();
        iso = in.readString();
        in.readBooleanArray(new boolean[]{forceHttps, isLive});
        userAgentToAccessDevServers = in.readString();
    }

    public String getUserAgentToAccessDevServers() {
        return userAgentToAccessDevServers;
    }

    /**
     * @return the country_name
     */
    public String getCountryName() {
        return name;
    }

    /**
     * @param country_name the country_name to set
     */
    public void setCountryName(String country_name) {
        this.name = country_name;
    }

    /**
     * @return the country_url
     */
    public String getCountryUrl() {
        return url;
    }

    /**
     * @param country_url the country_url to set
     */
    public void setCountryUrl(String country_url) {
        this.url = country_url;
    }

    /**
     * @return the country_flag
     */
    public String getCountryFlag() {
        return flag;
    }

    /**
     * @param country_flag the country_flag to set
     */
    public void setCountryFlag(String country_flag) {
        this.flag = country_flag;
    }

    /**
     * @return the country_iso
     */
    public String getCountryIso() {
        return iso;
    }

    /**
     * @param country_iso the country_iso to set
     */
    public void setCountryIso(String country_iso) {
        this.iso = country_iso;
    }

    /**
     * @return the country_force_https
     */
    public boolean isCountryForceHttps() {
        return forceHttps;
    }

    /**
     * @param country_force_https the country_force_https to set
     */
    public void setCountryForceHttps(boolean country_force_https) {
        this.forceHttps = country_force_https;
    }

    /**
     * @return the country_is_live
     */
    public boolean isCountryIsLive() {
        return isLive;
    }

    /**
     * @param country_is_live the country_is_live to set
     */
    public void setCountryIsLive(boolean country_is_live) {
        this.isLive = country_is_live;
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<CountryObject> CREATOR = new Parcelable.Creator<CountryObject>() {
        public CountryObject createFromParcel(Parcel in) {
            return new CountryObject(in);
        }

        public CountryObject[] newArray(int size) {
            return new CountryObject[size];
        }
    };


    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        name = jsonObject.optString(RestConstants.NAME);
        url = jsonObject.optString(RestConstants.URL);
        if (url != null) {
            // This is necessary otherwise Uri.Builder will encode the authority
            url = url.replace("/mobapi/", "");
        }
        flag = jsonObject.optString(RestConstants.FLAG);
        iso = jsonObject.optString(RestConstants.COUNTRY_ISO);
        forceHttps = jsonObject.optBoolean(RestConstants.FORCE_HTTPS, false);
        isLive = jsonObject.optInt(RestConstants.IS_LIVE, 0) == 1;
        // Used only for access dev servers
        userAgentToAccessDevServers = jsonObject.optString(RestConstants.USER_AGENT);
        try {
            languages = new Languages(jsonObject);
        }catch (JSONException ex){
            Print.e(ex.getMessage());
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

    public Languages getLanguages() {
        return languages;
    }

    public void setLanguages(Languages languages) {
        this.languages = languages;
    }
}

