package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class ShopWeekTeaserGroup extends BaseTeaserGroupType implements android.os.Parcelable {

    public ShopWeekTeaserGroup() {
    }

    /*
     * ########## GETTERS ##########
     */

    @Override
    public EnumTeaserGroupType getType() {
        return EnumTeaserGroupType.SHOP_WEEK_TEASERS;
    }

    /*
     * ########## JSON ##########
     */

    public ShopWeekTeaserGroup(JSONObject jsonObject) throws JSONException {
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

    private ShopWeekTeaserGroup(Parcel in) {
        super(in);
    }

    public static final Creator<ShopWeekTeaserGroup> CREATOR = new Creator<ShopWeekTeaserGroup>() {
        public ShopWeekTeaserGroup createFromParcel(Parcel source) {
            return new ShopWeekTeaserGroup(source);
        }

        public ShopWeekTeaserGroup[] newArray(int size) {
            return new ShopWeekTeaserGroup[size];
        }
    };
}
