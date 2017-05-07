package com.mobile.newFramework.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a section of the api.
 * For each section, there is a service.
 * Each section contain name, url.
 * @author GuilhermeSilva
 * @modified Manuel Silva
 *
 */
public class Section implements IJSONSerializable, Parcelable {

    public static final String TAG = Section.class.getSimpleName();

    public static final String SECTION_NAME_CATEGORIES = "categories";
    public static final String SECTION_NAME_IMAGE_RESOLUTIONS = "imageresolutions";
    public static final String SECTION_NAME_CONFIGURATIONS = "configurations";

    /**
     * Name of the section.
     */
    private String name;

    /**
     * Url of the newest version of the section.
     */
    private String url;

    private String md5;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    public String getMd5() {
        return md5;
    }

    /**
     * Empty constructor
     */
    public Section() {
    }

    public Section(String name, String md5, String url) {
        init(name, md5, url);
    }

    private void init(String name, String md5, String url) {
        this.name = name;
        this.md5 = md5;
        this.url = url;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString(RestConstants.SECTION_NAME);
            /**
             * HACK - while API dont return md5 for each section,
             * use System.currentTimeMillis in the mean time to "replace" md5.
             */
            String md5 = ""+System.currentTimeMillis();
            String url = jsonObject.getString(RestConstants.URL);
            if(jsonObject.has(RestConstants.SECTION_MD5)){
                md5 = jsonObject.getString(RestConstants.SECTION_MD5);
            }
            init(name, md5, url);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.NAME, name);
            jsonObject.put(RestConstants.URL, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(md5);
        dest.writeString(url);
    }

    private Section(Parcel in){
        name = in.readString();
        md5 = in.readString();
        url = in.readString();
    }

    public static final Parcelable.Creator<Section> CREATOR = new Parcelable.Creator<Section>() {
        public Section createFromParcel(Parcel in) {
            return new Section(in);
        }

        public Section[] newArray(int size) {
            return new Section[size];
        }
    };
}

