package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alexandrapires on 10/21/15.
 * This class stores url and zoom url for image gallery in PDV
 */
public class ImageUrls  implements Parcelable, IJSONSerializable {

    private static final String TAG = ImageUrls.class.getSimpleName();

    private String url;
    private String zoom;
    private boolean hasZoom;

    /**
     * Empty constructor
     * */
    public ImageUrls() {}

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {

        try {

            hasZoom = false;
            setUrl(jsonObject.getString(RestConstants.URL));
            setZoom(jsonObject.optString(RestConstants.ZOOM)); //this is not coming in all servers yet, so it's considered optional

            if(!TextUtils.isEmpty(zoom))
                setHasZoom(true);

        }catch(Exception e){
            Print.e(TAG, "Error initializing the complete product", e);
            return false;
        }

        return true;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    public boolean hasZoom() {
        return hasZoom;
    }

    public void setHasZoom(boolean hasZoom) {
        this.hasZoom = hasZoom;
    }




    @Override
    public JSONObject toJSON() {
        return null;
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
        dest.writeString(getUrl());
        dest.writeString(getZoom());
        dest.writeByte((byte) (hasZoom ? 1 : 0));

    }

    protected ImageUrls(Parcel in) {
        url = in.readString();
        zoom = in.readString();
        hasZoom = in.readByte() == 1;
    }



    public static final Creator<ImageUrls> CREATOR = new Creator<ImageUrls>() {
        public ImageUrls createFromParcel(Parcel in) {
            return new ImageUrls(in);
        }

        public ImageUrls[] newArray(int size) {
            return new ImageUrls[size];
        }
    };



}
