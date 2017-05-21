package com.mobile.service.objects.statics;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.catalog.FeaturedItem;
import com.mobile.service.objects.home.object.BaseTeaserObject;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to fill the suggestions screen when no results are found after a
 * search
 *
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class StaticFeaturedBox implements IJSONSerializable, Parcelable {

    public static final String TAG = StaticFeaturedBox.class.getSimpleName();

    private String title;

    private ArrayList<BaseTeaserObject> items;

    /**
     * Empty constructor
     */
    public StaticFeaturedBox() {
    }

    @Override
    public boolean initialize(JSONObject metadataObject) throws JSONException {
        // Get title
        title = metadataObject.optString(RestConstants.TITLE);
        // Get items
        JSONArray array = metadataObject.getJSONArray(RestConstants.DATA);
        if(array.length() > 0 ) {
            items = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject productObject = array.getJSONObject(i);
                StaticFeaturedBoxItem product = new StaticFeaturedBoxItem();
                product.initialize(productObject);
                items.add(product);
            }
        } else {
            throw new JSONException("The Json array " + RestConstants.DATA + " is empty");
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<BaseTeaserObject> getItems() {
        return items;
    }

    /*
     * ########### Parcelable ###########
     */

    protected StaticFeaturedBox(Parcel in) {
        title = in.readString();
        if (in.readByte() == 0x01) {
            items = new ArrayList<>();
            in.readList(items, FeaturedItem.class.getClassLoader());
        } else {
            items = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (items == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(items);
        }
    }

    public static final Creator<StaticFeaturedBox> CREATOR = new Creator<StaticFeaturedBox>() {
        @Override
        public StaticFeaturedBox createFromParcel(Parcel in) {
            return new StaticFeaturedBox(in);
        }

        @Override
        public StaticFeaturedBox[] newArray(int size) {
            return new StaticFeaturedBox[size];
        }
    };

}
