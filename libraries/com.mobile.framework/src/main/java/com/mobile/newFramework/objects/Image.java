package com.mobile.newFramework.objects;


import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.interfaces.IJSONSerializable;
import com.mobile.framework.rest.RestConstants;

/**
 * Image for products, brands, and categories. contains path, width, height, format
 * @author GuilhermeSilva
 * @modified Manuel Silva
 *
 */

public class Image implements IJSONSerializable, Parcelable {
    private final static String TAG = "";

    // private String JSON_URL_TAG = "path";
    // private String JSON_FORMAT_TAG = "format";
    // private String JSON_WIDTH_TAG = "width";
    // private String JSON_HEIGHT_TAG = "height";

    private String url;
    private String format;
    private String width;
    private String height;

    /**
     * Image empty constructor.
     */
    public Image() {
        url = "";
        format = "";
        width = "";
        height = "";
    }

    /**
     * Image param constructor
     *
     * @param url
     *            . Url of the image.
     * @param format
     *            . format of the image.
     * @param width
     *            . width of the image.
     * @param height
     *            . height of the image.
     */
    public Image(String url, String format, String width, String height) {
        this.url = url;
        this.format = format;
        this.width = width;
        this.height = height;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            url = jsonObject.getString(RestConstants.JSON_PATH_TAG);
            format = jsonObject.optString(RestConstants.JSON_FORMAT_TAG);
            width = jsonObject.optString(RestConstants.JSON_WIDTH_TAG);
            height = jsonObject.optString(RestConstants.JSON_HEIGHT_TAG);
        } catch (JSONException e) {

//            try {
//                for (int i = 0; i < jsonObject.names().length(); ++i) {
//                    Log.d(TAG, jsonObject.names().getString(i));
//                }
//
//            } catch (JSONException e1) {
//                Log.e(TAG, "error during debug output", e1);
//            }

//            Log.e(TAG, "error during debug output", e);
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_PATH_TAG, url);
            jsonObject.put(RestConstants.JSON_FORMAT_TAG, format);
            jsonObject.put(RestConstants.JSON_WIDTH_TAG, width);
            jsonObject.put(RestConstants.JSON_HEIGHT_TAG, height);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(format);
        dest.writeString(width);
        dest.writeString(height);

    }

    private Image(Parcel in) {
        url = in.readString();
        format = in.readString();
        width = in.readString();
        height = in.readString();
    }

    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
