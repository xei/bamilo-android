package com.mobile.framework.objects.home.group;

import android.os.Parcel;

import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.framework.objects.home.object.TeaserTopSellerObject;
import com.mobile.framework.objects.home.type.EnumTeaserGroupType;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class TopSellersTeaserGroup extends BaseTeaserType {

    public TopSellersTeaserGroup() {
    }

    public TopSellersTeaserGroup(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }


    /*
     * ########## GETTERS ##########
     */

    @Override
    public EnumTeaserGroupType getType() {
        return EnumTeaserGroupType.TOP_SELLERS;
    }

    /*
     * ########## JSON ##########
     */

    @Override
    protected BaseTeaserObject createTeaserObject(JSONObject object) {
        TeaserTopSellerObject teaser = new TeaserTopSellerObject();
        try {
            teaser.initialize(object);
        } catch (JSONException e) {
            e.printStackTrace();
            teaser = null;
        }
        return teaser;
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
        super.writeToParcel(dest, flags);
    }

    private TopSellersTeaserGroup(Parcel in) {
        super(in);
    }

    public static final Creator<TopSellersTeaserGroup> CREATOR = new Creator<TopSellersTeaserGroup>() {
        public TopSellersTeaserGroup createFromParcel(Parcel source) {
            return new TopSellersTeaserGroup(source);
        }

        public TopSellersTeaserGroup[] newArray(int size) {
            return new TopSellersTeaserGroup[size];
        }
    };
}
