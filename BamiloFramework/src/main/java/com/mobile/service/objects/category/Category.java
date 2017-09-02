package com.mobile.service.objects.category;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.catalog.filters.MultiFilterOptionInterface;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;
import com.mobile.service.utils.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents an Category. Composed by id, name and
 * hasChildren
 *
 * @author GuilhermeSilva
 * @modified Paulo Carvalho
 *
 */
public class Category implements IJSONSerializable, Parcelable, MultiFilterOptionInterface {

    private String mType;

    private String mName;

    private String mPath;

    private String mUrlKey;

    private String mTargetLink;

    private String mImage;

    private ArrayList<Category> mSubCategories;

    private boolean isSection;

    private boolean isExternalLinkType;

    private int mLevel;

    private String mMainCategory;   //content category


    protected boolean selected;
    /**
     * Category empty constructor.
     */
    public Category() {
        // ...
    }

    public void markAsSection() {
        isSection = true;
    }

    /**
     * Validate the flag has children
     * @return true or false
     */
    public boolean hasChildren(){
        return CollectionUtils.isNotEmpty(mSubCategories);
    }

    /**
     * @return the type
     */
    public String getType() {
        return mType;
    }

    public boolean isExternalLinkType(){
        return isExternalLinkType;
    }

    public void  setIsExternalLinkType(boolean isExternal){
        this.isExternalLinkType = isExternal;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getCategoryPath() {
        return mPath;
    }

    /**
     * @return the urlKey
     */
    public String getUrlKey() {
        return mUrlKey;
    }

    /**
     * @return the children
     */
    public ArrayList<Category> getChildren() {
        return mSubCategories;
    }

    /**
     *
     */
    public void setChildren(ArrayList<Category> categories) {
        mSubCategories = categories;
    }

    /**
     * @return the target link
     */
    public String getTargetLink() {
        return mTargetLink;
    }

    public void setTargetLink(String targetLink){
        this.mTargetLink= targetLink;
    }

    /**
     *
     * @return isHeader value
     */
    public Boolean isSection() {
        return isSection;
    }

    /**
     *
     * @return iamge string
     */
    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

    /**
     *
     * @return content category string
     */
    public String getMainCategory() {
        return mMainCategory;
    }

    public int getLevel() {
        return mLevel;
    }

    public void setLevel(int level) {
        this.mLevel = level;
    }
    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mType = jsonObject.optString(RestConstants.TYPE);
        mName = jsonObject.optString(RestConstants.LABEL);
        if(isSection && TextUtils.isNotEmpty(mName)) {
            mName = mName.toUpperCase();
        }
        mImage = jsonObject.optString(RestConstants.IMAGE);
        mUrlKey = jsonObject.optString(RestConstants.URL_KEY);
        mTargetLink = jsonObject.optString(RestConstants.TARGET);
        mUrlKey = jsonObject.optString(RestConstants.URL_KEY);
        mPath = jsonObject.optString(RestConstants.URL);
        mMainCategory = jsonObject.optString(RestConstants.MAIN_CATEGORY);
        // Get sub categories
        JSONArray childrenArray = jsonObject.optJSONArray(RestConstants.CHILDREN);
        if (childrenArray != null) {
            mSubCategories = new ArrayList<>();
            for (int i = 0; i < childrenArray.length(); ++i) {
                JSONObject childObject = childrenArray.getJSONObject(i);
                Category child = new Category();
                child.initialize(childObject);
                child.isSection = false;
                mSubCategories.add(child);
            }
        }
        isExternalLinkType = false;
        return true;
    }

    @Override
    public JSONObject toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.TYPE, mType);
            jsonObject.put(RestConstants.LABEL, mName);
            jsonObject.put(RestConstants.URL_KEY, mUrlKey);

            JSONArray childrenArray = new JSONArray();

            for (Category child : mSubCategories) {
                childrenArray.put(child.toJSON());
            }

            jsonObject.put(RestConstants.CHILDREN, childrenArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }


    /**
     * ########### Parcelable ###########
     * @author sergiopereira
     */

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mType);
        dest.writeString(mName);
        dest.writeString(mUrlKey);
        dest.writeString(mTargetLink);
        dest.writeList(mSubCategories);
        dest.writeString(mImage);
        dest.writeString(mMainCategory);
        dest.writeByte((byte) (isExternalLinkType ? 0x01 : 0x00));
    }

    /**
     * Parcel constructor
     */
    protected Category(Parcel in) {
        mType = in.readString();
        mName = in.readString();
        mUrlKey = in.readString();
        mTargetLink = in.readString();
        mSubCategories = new ArrayList<>();
        in.readList(mSubCategories, Category.class.getClassLoader());
        mImage = in.readString();
        mMainCategory = in.readString();
        isExternalLinkType = in.readByte() != 0x00;
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };


    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @NonNull
    @Override
    public String getLabel() {
        return getName();
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @NonNull
    @Override
    public String getVal() {
        return getUrlKey();
    }
}
