package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class FeaturedStoresTeaserGroup extends BaseTeaserType implements android.os.Parcelable {

    public FeaturedStoresTeaserGroup() {
    }

    /*
     * ########## GETTERS ##########
     */

    @Override
    public EnumTeaserGroupType getType() {
        return EnumTeaserGroupType.FEATURED_STORES;
    }

    /*
     * ########## JSON ##########
     */

    public FeaturedStoresTeaserGroup(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    /*
    @Override
    protected BaseTeaserObject createTeaserObject(JSONObject object) {
        return null;
    }
    */


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    private FeaturedStoresTeaserGroup(Parcel in) {
        super(in);
    }

    public static final Creator<FeaturedStoresTeaserGroup> CREATOR = new Creator<FeaturedStoresTeaserGroup>() {
        public FeaturedStoresTeaserGroup createFromParcel(Parcel source) {
            return new FeaturedStoresTeaserGroup(source);
        }

        public FeaturedStoresTeaserGroup[] newArray(int size) {
            return new FeaturedStoresTeaserGroup[size];
        }
    };
}
