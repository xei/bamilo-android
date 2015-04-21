package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class BrandsTeaserGroup extends BaseTeaserType implements android.os.Parcelable {

    public BrandsTeaserGroup() {
    }

    public BrandsTeaserGroup(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    /*
    @Override
    protected BaseTeaserObject createTeaserObject(JSONObject object) {
        return null;
    }
    */

    @Override
    public EnumTeaserGroupType getType() {
        return EnumTeaserGroupType.BRAND_TEASERS;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    private BrandsTeaserGroup(Parcel in) {
        super(in);
    }

    public static final Creator<BrandsTeaserGroup> CREATOR = new Creator<BrandsTeaserGroup>() {
        public BrandsTeaserGroup createFromParcel(Parcel source) {
            return new BrandsTeaserGroup(source);
        }

        public BrandsTeaserGroup[] newArray(int size) {
            return new BrandsTeaserGroup[size];
        }
    };
}
