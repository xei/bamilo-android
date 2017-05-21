package com.mobile.service.objects.home.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.home.group.BaseTeaserGroupType;
import com.mobile.service.objects.home.type.TeaserGroupType;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent a teaser that gets it's info through Rich Relevance.
 * @author Paulo Carvalho
 */
public class TeaserRichRelevanceObject implements IJSONSerializable, Parcelable {

    private TeaserGroupType mType = TeaserGroupType.UNKNOWN;
    private BaseTeaserGroupType richTeaserGroupType;

    /**
     * Empty Constructor
     */
    public TeaserRichRelevanceObject() {
    }

    /*
     * ########## GETTERS ##########
     */

    public BaseTeaserGroupType getRichTeaserGroupType() {
        return richTeaserGroupType;
    }

    /*
     * ########## JSON ##########
     */

    /**
     * Initialize
     * @param jsonObject JSONObject containing the parameters of the object
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get group type
        String typeString = jsonObject.getString(RestConstants.TYPE);
        mType = TeaserGroupType.byString(typeString);
        // Parse and create group
        richTeaserGroupType = new BaseTeaserGroupType(mType, jsonObject);
        return true;
    }

    /**
     *
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    /*
     * ########## PARCELABLE ##########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mType);
        dest.writeValue(richTeaserGroupType);
    }

    private TeaserRichRelevanceObject(Parcel in) {
        mType = (TeaserGroupType) in.readValue(TeaserGroupType.class.getClassLoader());
        richTeaserGroupType = (BaseTeaserGroupType) in.readValue(BaseTeaserGroupType.class.getClassLoader());
    }

    public static final Creator<TeaserRichRelevanceObject> CREATOR = new Creator<TeaserRichRelevanceObject>() {
        public TeaserRichRelevanceObject createFromParcel(Parcel source) {
            return new TeaserRichRelevanceObject(source);
        }

        public TeaserRichRelevanceObject[] newArray(int size) {
            return new TeaserRichRelevanceObject[size];
        }
    };

}
