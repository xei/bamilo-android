package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class MainTeaserGroup extends BaseTeaserGroupType implements android.os.Parcelable {

    public MainTeaserGroup() {
        // ...
    }

    /*
     * ########## GETTERS ##########
     */

    @Override
    public EnumTeaserGroupType getType() {
        return EnumTeaserGroupType.MAIN_TEASERS;
    }

    /*
     * ########## JSON ##########
     */

    public MainTeaserGroup(JSONObject jsonObject) throws JSONException {
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

    private MainTeaserGroup(Parcel in) {
        super(in);
    }

    public static final Creator<MainTeaserGroup> CREATOR = new Creator<MainTeaserGroup>() {
        public MainTeaserGroup createFromParcel(Parcel source) {
            return new MainTeaserGroup(source);
        }

        public MainTeaserGroup[] newArray(int size) {
            return new MainTeaserGroup[size];
        }
    };
}
