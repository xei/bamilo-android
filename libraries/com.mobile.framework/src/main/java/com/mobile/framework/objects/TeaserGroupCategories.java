/**
 *
 */
package com.mobile.framework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.TeaserGroupCategories.TeaserCategory;

import org.json.JSONObject;

/**
 * @author nutzer2
 */
@Deprecated
public class TeaserGroupCategories extends TeaserSpecification<TeaserCategory> {

    /**
     * @param type
     */
    public TeaserGroupCategories() {
        super(TeaserGroupType.CATEGORIES);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.TeaserSpecification#parseData(org.json.JSONObject
     * )
     */
    @Override
    protected TeaserCategory parseData(JSONObject object) {
        TeaserCategory cat = new TeaserCategory();
        cat.initialize(object);
        return cat;
    }

    public static class TeaserCategory extends Category implements ITargeting, Parcelable {

        /*
         * (non-Javadoc)
         *
         * @see com.mobile.framework.objects.ITargetting#getTargetUrl()
         */
        @Override
        public String getTargetUrl() {
            return super.getApiUrl();
        }

        /*
         * (non-Javadoc)
         *
         * @see com.mobile.framework.objects.ITargeting#getTargetType()
         */
        @Override
        public TargetType getTargetType() {
            return TargetType.CATALOG;
        }

        /* (non-Javadoc)
         * @see com.mobile.framework.objects.ITargeting#getTargetTitle()
         */
        @Override
        public String getTargetTitle() {
            return super.getName();
        }

        public TeaserCategory() {

        }

        private TeaserCategory(Parcel in) {
            super(in);
        }

        /**
         * Create parcelable
         */
        public static final Parcelable.Creator<TeaserCategory> CREATOR = new Parcelable.Creator<TeaserCategory>() {
            public TeaserCategory createFromParcel(Parcel in) {
                return new TeaserCategory(in);
            }

            public TeaserCategory[] newArray(int size) {
                return new TeaserCategory[size];
            }
        };

    }


    /**
     * ########### Parcelable ###########
     *
     * @author sergiopereira
     */
    
    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return super.describeContents();
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }


    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */

    /**
     * Parcel constructor
     *
     * @param in
     */
    public TeaserGroupCategories(Parcel in) {
        super(in);
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<TeaserGroupCategories> CREATOR = new Parcelable.Creator<TeaserGroupCategories>() {
        public TeaserGroupCategories createFromParcel(Parcel in) {
            return new TeaserGroupCategories(in);
        }

        public TeaserGroupCategories[] newArray(int size) {
            return new TeaserGroupCategories[size];
        }
    };

}
