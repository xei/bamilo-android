package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class SmallTeaserGroup extends BaseTeaserGroupType implements android.os.Parcelable {

    public SmallTeaserGroup() {
    }

    /*
     * ########## GETTERS ##########
     */

    @Override
    public EnumTeaserGroupType getType() {
        return EnumTeaserGroupType.SMALL_TEASERS;
    }

    /*
     * ########## JSON ##########
     */

    public SmallTeaserGroup(JSONObject jsonObject) throws JSONException {
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

    private SmallTeaserGroup(Parcel in) {
        super(in);
    }

    public static final Creator<SmallTeaserGroup> CREATOR = new Creator<SmallTeaserGroup>() {
        public SmallTeaserGroup createFromParcel(Parcel source) {
            return new SmallTeaserGroup(source);
        }

        public SmallTeaserGroup[] newArray(int size) {
            return new SmallTeaserGroup[size];
        }
    };
}
