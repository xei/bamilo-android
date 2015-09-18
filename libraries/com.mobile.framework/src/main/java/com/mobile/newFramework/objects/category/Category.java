package com.mobile.newFramework.objects.category;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

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
public class Category implements IJSONSerializable, Parcelable {

    private String mType;

    private String mName;

    private String mPath;

    private String mUrlKey;

    private String mApiUrl;

    private String mVertical;

    private String mImage;

    private boolean mHasChildren;

    private Category mParent;

    private ArrayList<Category> mChildren;

    private boolean mIsHeader;
    /**
     * Category empty constructor.
     */
    public Category() {
        mType = "";
        mName = "defaultName";
        mUrlKey = "";
        mApiUrl = "";
        mVertical = "";
        mChildren = new ArrayList<>();
        mParent = null;
        mHasChildren = false;
        mIsHeader = true;
        mImage = "";
    }

    /**
     * @return returns a boolean indicating if the category has any
     *         subcategories.
     */
    public boolean getHasChildrenInArray() {
        return mChildren.size() > 0;
    }

    /**
     * Validate the flag has children
     * @return true or false
     */
    public boolean hasChildren(){
        return mHasChildren;
    }


    /**
     * Set if has childre
     */
    public void setHasChildren(boolean hasChildren){
        this.mHasChildren = hasChildren;
    }


    // Getters

    public void addChild(Category child) {
        mChildren.add(child);
    }

    /**
     * @return the type
     */
    public String getType() {
        return mType;
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
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
        return mChildren;
    }

    /**
     * @return the apiUrl
     */
    public String getApiUrl() {
        return mApiUrl;
    }

    /**
     * @return the parent category
     */
    public Category getParent() {
        return mParent;
    }



    public void setChildren(ArrayList<Category> children ) {
        this.mChildren = children;
    }


    /**
     *
     * @return vertical value
     */
    public String getVertical() {
        return mVertical;
    }

    /**
     *
     * @return isHeader value
     */
    public Boolean getIsHeader() {
        return mIsHeader;
    }

    /**
     * set is Header
     */
    public void setIsHeader(boolean isHeader) {
        this.mIsHeader =  isHeader;
    }

    /**
     *
     * @return iamge string
     */
    public String getImage() {
        return mImage;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        mChildren.clear();
        try {
            mType = jsonObject.optString(RestConstants.JSON_CATEGORY_TYPE_TAG);
            if(getIsHeader()){
                mName = jsonObject.optString(RestConstants.JSON_CATEGORY_LABEL_TAG).toUpperCase();
            } else {
                mName = jsonObject.optString(RestConstants.JSON_CATEGORY_LABEL_TAG);
            }
            mImage = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);
            mUrlKey = jsonObject.optString(RestConstants.JSON_URL_KEY_TAG);
            mApiUrl = jsonObject.optString(RestConstants.JSON_API_URL_TAG);
            mHasChildren = jsonObject.optBoolean(RestConstants.JSON_HAS_CHILDREN);
            mUrlKey = jsonObject.optString(RestConstants.JSON_URL_KEY_TAG);
            mVertical = jsonObject.optString(RestConstants.JSON_CATEGORY_VERTICAL);
            mPath = jsonObject.optString(RestConstants.JSON_CATEGORY_URL_TAG);
            if (TextUtils.isEmpty(mPath)) mPath = calcCategoryPath();
            JSONArray childrenArray = jsonObject.optJSONArray(RestConstants.JSON_CHILDREN_TAG);
            if (childrenArray != null) {
                mChildren = new ArrayList<>();
                for (int i = 0; i < childrenArray.length(); ++i) {
                    JSONObject childObject = childrenArray.getJSONObject(i);
                    Category child = new Category();
                    child.mParent = this;
                    child.setIsHeader(false);
                    child.initialize(childObject);
                    mChildren.add(child);
                }
            }
            if (mParent == null) {

            }
        } catch (JSONException e) {
            // Log.w(TAG, "WARNING: ON INIT CATEGORY" , e);
            return false;
        }
        return true;
    }

    private String calcCategoryPath() {
        if ( mParent == null) return "/" + mUrlKey;
        else return mParent.calcCategoryPath() + "/" + mUrlKey;
    }

    @Override
    public JSONObject toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_CATEGORY_TYPE_TAG, mType);
            jsonObject.put(RestConstants.JSON_CATEGORY_LABEL_TAG, mName);
            jsonObject.put(RestConstants.JSON_URL_KEY_TAG, mUrlKey);

            JSONArray childrenArray = new JSONArray();

            for (Category child : mChildren) {
                childrenArray.put(child.toJSON());
            }

            jsonObject.put(RestConstants.JSON_CHILDREN_TAG, childrenArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
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
        dest.writeString(mApiUrl);
        dest.writeList(mChildren);
        dest.writeValue(mParent);
        dest.writeString(mVertical);
        dest.writeString(mImage);
    }

    /**
     * Parcel constructor
     * @param in
     */
    protected Category(Parcel in) {
        mType = in.readString();
        mName = in.readString();
        mUrlKey = in.readString();
        mApiUrl = in.readString();
        mChildren = new ArrayList<>();
        in.readList(mChildren, Category.class.getClassLoader());
        mParent = (Category) in.readValue(Category.class.getClassLoader());
        mVertical = in.readString();
        mImage = in.readString();
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


}
