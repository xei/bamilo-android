/**
 * @author GuilhermeSilva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.pojo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.interfaces.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * Class that represents an Category. Composed by id, name and
 * hasChildren
 * 
 * @author GuilhermeSilva
 * 
 */
public class Category implements IJSONSerializable, Parcelable {
	private final static String TAG = "";

    private String id;
    private String name;
    private String path;
    private String lft;
    private String rgt;
    private String urlKey;
    private String segments;

    private String infoUrl;
    private String apiUrl;

    private Category parent;
    private ArrayList<Category> children;

    /**
     * Category empty constructor.
     */
    public Category() {
        id = "-1";
        name = "defaultName";
        lft = "0";
        rgt = "0";
        urlKey = "-1";
        segments = "";

        infoUrl = "";
        apiUrl = "";

        children = new ArrayList<Category>();

        parent = null;
    }

    /**
     * Category constructor
     * 
     * @param id
     *            of the category
     * @param name
     *            of the category.
     */
    public Category(String id, String name) {
        this.id = id;
        this.name = name;
        lft = "0";
        rgt = "0";
        urlKey = "-1";
        segments = "";

        children = new ArrayList<Category>();
        parent = null;
    }

    /**
     * Category constructor
     * 
     * @param id
     *            of the category.
     * @param name
     *            name of the category.
     * 
     * @deprecated because a category should be initializes using either a
     *             jsonobject or a by passing a string id.
     */
    @Deprecated
    public Category(int id, String name) {
        this.id = "" + id;
        this.name = name;
        lft = "0";
        rgt = "0";
        urlKey = "-1";
        segments = "";
        children = new ArrayList<Category>();
        parent = null;
    }

    /**
     * @return returns a boolean indicating if the category has any
     *         subcategories.
     */
    public boolean getHasChildren() {
        return children.size() > 0;
    }

    // Getters

    public void addChild(Category child) {
        children.add(child);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    public String getCategoryPath() {
    	return path;
    }

    /**
     * @return the lft
     */
    public String getLft() {
        return lft;
    }

    /**
     * @return the rgt
     */
    public String getRgt() {
        return rgt;
    }

    /**
     * @return the urlKey
     */
    public String getUrlKey() {
        return urlKey;
    }

    /**
     * @return the segments
     */
    public String getSegments() {
        return segments;
    }

    /**
     * @return the children
     */
    public ArrayList<Category> getChildren() {
        return children;
    }

    /**
     * @return the infoUrl
     */
    public String getInfoUrl() {
        return infoUrl;
    }

    /**
     * @return the apiUrl
     */
    public String getApiUrl() {
        return apiUrl;
    }

    /**
     * @return the parent category
     */
    public Category getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        children.clear();
        try {

            id = jsonObject.optString(RestConstants.JSON_CATEGORY_ID_TAG);
            name = jsonObject.optString(RestConstants.JSON_CATEGORY_NAME_TAG);
            lft = jsonObject.optString(RestConstants.JSON_LEFT_TAG);
            rgt = jsonObject.optString(RestConstants.JSON_RIGHT_TAG);
            urlKey = jsonObject.optString(RestConstants.JSON_URL_KEY_TAG);
            segments = jsonObject.optString(RestConstants.JSON_SEGMENTS_TAG);

            path = jsonObject.optString(RestConstants.JSON_CATEGORY_URL_TAG);
            if ( TextUtils.isEmpty( path ))
                path = calcCategoryPath();

            infoUrl = jsonObject.getString(RestConstants.JSON_INFO_URL_TAG);
            apiUrl = jsonObject.getString(RestConstants.JSON_API_URL_TAG);
            
            JSONArray childrenArray = jsonObject.optJSONArray(RestConstants.JSON_CHILDREN_TAG);

            if (childrenArray != null) {
                children = new ArrayList<Category>();
                for (int i = 0; i < childrenArray.length(); ++i) {
                    Category child = new Category();
                    child.parent = this;
                    JSONObject childObject = childrenArray.getJSONObject(i);
                    if (child.initialize(childObject)) {
                        children.add(child);
                    } else {
                        // Constants.LogError("Critical error in Category.initialize() :  parsing an category");
                    }
                }
            }
        } catch (JSONException e) {
            // Constants.LogError("Critical error in Category.initialize(): initializing the category using json");
            // e.printStackTrace();
            return false;
        }
        return true;
    }
    
    private String calcCategoryPath() {
    	if ( parent == null)
    		return "/" + urlKey;
    	else
    		return parent.calcCategoryPath() + "/" + urlKey;
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
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    
    public JSONObject toJSON() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_CATEGORY_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_CATEGORY_NAME_TAG, name);
            jsonObject.put(RestConstants.JSON_LEFT_TAG, lft);
            jsonObject.put(RestConstants.JSON_RIGHT_TAG, rgt);
            jsonObject.put(RestConstants.JSON_URL_KEY_TAG, urlKey);
            jsonObject.put(RestConstants.JSON_SEGMENTS_TAG, segments);

            JSONArray childrenArray = new JSONArray();

            for (Category child : children) {
                childrenArray.put(child.toJSON());
            }

            jsonObject.put(RestConstants.JSON_CHILDREN_TAG, childrenArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
    
    
    
    protected Category(Parcel in) {
        id = in.readString();
        name = in.readString();
        path = in.readString();
        lft = in.readString();
        rgt = in.readString();
        urlKey = in.readString();
        segments = in.readString();
        infoUrl = in.readString();
        apiUrl = in.readString();
        parent = (Category)in.readValue(null);
        children = new ArrayList<Category>();
        in.readList(children, null);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeString(lft);
        dest.writeString(rgt);
        dest.writeString(urlKey);
        dest.writeString(segments);
        dest.writeString(infoUrl);
        dest.writeString(apiUrl);
        dest.writeValue(parent);
        dest.writeList(children);
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
    
}
