package com.bamilo.android.framework.service.objects.statics;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;
import com.bamilo.android.framework.service.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This class represents a static page that will display a CMS block. it can be static block, or shops in shop type
 * Created by Paulo Carvalho on 4/10/15.
 *
 * @modified sergiopereira
 */
public class StaticPage implements IJSONSerializable, Parcelable {

    private static final String TAG = StaticPage.class.getSimpleName();

    private String mHtml;

    private String mType;

    private ArrayList<StaticFeaturedBox> mFeaturedBoxes;

    /**
     * Empty constructor for JSON converter
     */
    @SuppressWarnings("unused")
    public StaticPage() {
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get html
        mHtml = jsonObject.optString(RestConstants.HTML);
        mType = jsonObject.getString(RestConstants.TYPE);
        // Get featured box (optional)
        JSONArray array = jsonObject.optJSONArray(RestConstants.FEATURED_BOX);
        if(array != null && array.length() > 0) {
            mFeaturedBoxes = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject featuredBoxObject = array.getJSONObject(i);
                    StaticFeaturedBox featuredBox = new StaticFeaturedBox();
                    featuredBox.initialize(featuredBoxObject);
                    mFeaturedBoxes.add(featuredBox);
                } catch (JSONException e) {
                }
            }
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.OBJECT_DATA;
    }

    public String getHtml() {
        return mHtml;
    }

    public ArrayList<StaticFeaturedBox> getFeaturedBoxes() {
        return mFeaturedBoxes;
    }

    public boolean hasFeaturedBoxes() {
        return CollectionUtils.isNotEmpty(mFeaturedBoxes);
    }

    public boolean hasHtml() {
        return !TextUtils.isEmpty(mHtml);
    }

    public String getType() {
        return mType;
    }

    /*
     * ########### Parcelable ###########
     */

    protected StaticPage(Parcel in) {
        mHtml = in.readString();
        if (in.readByte() == 0x01) {
            mFeaturedBoxes = new ArrayList<>();
            in.readList(mFeaturedBoxes, StaticFeaturedBox.class.getClassLoader());
        } else {
            mFeaturedBoxes = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mHtml);
        if (mFeaturedBoxes == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mFeaturedBoxes);
        }
    }

    public static final Creator<StaticPage> CREATOR = new Creator<StaticPage>() {
        @Override
        public StaticPage createFromParcel(Parcel in) {
            return new StaticPage(in);
        }

        @Override
        public StaticPage[] newArray(int size) {
            return new StaticPage[size];
        }
    };
}
