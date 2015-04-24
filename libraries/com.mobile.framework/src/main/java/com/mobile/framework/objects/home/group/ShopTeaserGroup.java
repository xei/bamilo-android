package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class ShopTeaserGroup extends BaseTeaserGroupType implements android.os.Parcelable {

    public ShopTeaserGroup() {
    }

    /*
     * ########## GETTERS ##########
     */

    @Override
    public EnumTeaserGroupType getType() {
        return EnumTeaserGroupType.SHOP_TEASERS;
    }

    /*
     * ########## JSON ##########
     */

    public ShopTeaserGroup(JSONObject jsonObject) throws JSONException {
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

    private ShopTeaserGroup(Parcel in) {
        super(in);
    }

    public static final Creator<ShopTeaserGroup> CREATOR = new Creator<ShopTeaserGroup>() {
        public ShopTeaserGroup createFromParcel(Parcel source) {
            return new ShopTeaserGroup(source);
        }

        public ShopTeaserGroup[] newArray(int size) {
            return new ShopTeaserGroup[size];
        }
    };
}
