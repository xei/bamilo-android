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
 *
 */
public class Category implements IJSONSerializable, Parcelable {

    private String mId;

    private String mName;

    private String mPath;

    private String mUrlKey;

    private String mApiUrl;

    private boolean mHasChildren;

    private Category mParent;

    private ArrayList<Category> mChildren;

    /**
     * Category empty constructor.
     */
    public Category() {
        mId = "-1";
        mName = "defaultName";
        mUrlKey = "-1";
        mApiUrl = "";
        mChildren = new ArrayList<>();
        mParent = null;
        mHasChildren = false;
    }

    /**
     * Category constructor with parameters.
     * @param id
     * @param name
     * @param lft
     * @param rgt
     * @param urlKey
     * @param segments
     * @param infoUrl
     * @param apiUrl
     * @param children
     * @param parent
     */
    public Category(String id, String name, String lft, String rgt, String urlKey, String segments, String infoUrl, String apiUrl, ArrayList<Category> children, Category parent, boolean hasChildren) {
        this.mId = id;
        this.mName = name;
        this.mUrlKey = urlKey;
        this.mApiUrl = apiUrl;
        this.mChildren = children;
        this.mParent = parent;
        this.mHasChildren = hasChildren;
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


    // Getters

    public void addChild(Category child) {
        mChildren.add(child);
    }

    /**
     * @return the id
     */
    public String getId() {
        return mId;
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

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        mChildren.clear();
        try {
            mId = jsonObject.optString(RestConstants.JSON_CATEGORY_ID_TAG);
            mName = jsonObject.optString(RestConstants.JSON_CATEGORY_NAME_TAG);
            mUrlKey = jsonObject.optString(RestConstants.JSON_URL_KEY_TAG);
            mApiUrl = jsonObject.optString(RestConstants.JSON_API_URL_TAG, "");
            mHasChildren = jsonObject.optBoolean(RestConstants.JSON_HAS_CHILDREN);
            mPath = jsonObject.optString(RestConstants.JSON_CATEGORY_URL_TAG);
            if (TextUtils.isEmpty(mPath)) mPath = calcCategoryPath();
            JSONArray childrenArray = jsonObject.optJSONArray(RestConstants.JSON_CHILDREN_TAG);
            if (childrenArray != null) {
                mChildren = new ArrayList<>();
                for (int i = 0; i < childrenArray.length(); ++i) {
                    JSONObject childObject = childrenArray.getJSONObject(i);
                    Category child = new Category();
                    child.mParent = this;
                    child.initialize(childObject);
                    mChildren.add(child);
                }
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

    public static Category findCategoryFromTopLevel( String id, ArrayList<Category> categories ) {
        for( Category cat: categories ) {
            Category result = cat.findCategoryInChildren(id);
            if ( result == null)
                continue;
            else
                return result;
        }

        return null;
    }

    public Category findCategoryInChildren( String id ) {
        if ( this.getId().equals( id ))
            return this;

        for( Category cat: getChildren()) {
            Category result = cat.findCategoryInChildren( id );
            if ( result == null)
                continue;
            else
                return result;

        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_CATEGORY_ID_TAG, mId);
            jsonObject.put(RestConstants.JSON_CATEGORY_NAME_TAG, mName);
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
        dest.writeString(mId);
        dest.writeString(mName);
        dest.writeString(mUrlKey);
        dest.writeString(mApiUrl);
        dest.writeList(mChildren);
        dest.writeValue(mParent);
    }

    /**
     * Parcel constructor
     * @param in
     */
    protected Category(Parcel in) {
        mId = in.readString();
        mName = in.readString();
        mUrlKey = in.readString();
        mApiUrl = in.readString();
        mChildren = new ArrayList<>();
        in.readList(mChildren, Category.class.getClassLoader());
        mParent = (Category) in.readValue(Category.class.getClassLoader());
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
