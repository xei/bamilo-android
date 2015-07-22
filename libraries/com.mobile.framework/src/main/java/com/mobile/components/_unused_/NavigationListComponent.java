//package com.mobile.components;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.framework.interfaces.IJSONSerializable;
//import com.mobile.newFramework.pojo.JsonConstants;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
///**
// * Navigation List Component
// *
// * This class is used to create the components received from the server for each
// * element of the Navigation List.
// *
// * @author manuelsilva
// *
// */
//public class NavigationListComponent implements IJSONSerializable, Parcelable {
//
//    // private final static String TAG = LogTagHelper.create(NavigationListComponent.class);
//
//    private int element_id;
//	private String element_text;
//	private String element_url;
//	private String element_image_url;
//
//	public NavigationListComponent() {
//		// empty constructor
//	}
//
//	public NavigationListComponent(int id, String text, String url, String imgurl) {
//		this.element_id = id;
//		this.element_text = text;
//		this.element_url = url;
//		this.element_image_url = imgurl;
//	}
//
//	/**
//	 * @param text of the element.
//	 */
//	public void setElementText(String text) {
//		this.element_text = text;
//	}
//
//	/**
//	 * @param url of the image of the element.
//	 */
//	public void setElementImageUrl(String url) {
//		this.element_image_url = url;
//	}
//
//	/**
//	 * @param url of the element.
//	 */
//	public void setElementUrl(String url) {
//		this.element_url = url;
//	}
//
//	/**
//	 * @param id of the element
//	 */
//	public void setElementId(int id) {
//		this.element_id = id;
//	}
//
//	/**
//	 * @return the id of the element
//	 */
//	public int getElementId() {
//		return element_id;
//	}
//
//	/**
//	 * @return the text of the element.
//	 */
//	public String getElementText() {
//		return this.element_text;
//	}
//
//	/**
//	 * @return the url of the image.
//	 */
//	public String getElementImageUrl() {
//		return this.element_image_url;
//	}
//
//	/**
//	 * @return the url of the navigation list.
//	 */
//	public String getElementUrl() {
//		return this.element_url;
//	}
//
//    /* (non-Javadoc)
//     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) {
//        try {
//
//            element_id = Integer.parseInt(jsonObject.getString(JsonConstants.JSON_ID_TAG));
//            JSONObject attributesObject = jsonObject.getJSONObject(JsonConstants.JSON_ATTRIBUTES_TAG);
//            element_text = attributesObject.getString(JsonConstants.JSON_NAV_LIST_NAME_TAG);
//
//            element_url = attributesObject.getString(JsonConstants.JSON_NAVIGATION_URL_TAG);
//            JSONObject imageObject = attributesObject.getJSONObject(JsonConstants.JSON_IMAGE_TAG);
//            element_image_url = imageObject.getString(JsonConstants.JSON_IMAGE_URL_TAG);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
//    }
//
//    /* (non-Javadoc)
//     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//     */
//
//    public JSONObject toJSON() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put(JsonConstants.JSON_ID_TAG, element_id);
//
//            JSONObject attributesObject = new JSONObject();
//            attributesObject.put(JsonConstants.JSON_NAV_LIST_NAME_TAG, element_text);
//            attributesObject.put(JsonConstants.JSON_NAVIGATION_URL_TAG, element_url);
//
//            JSONObject imageObject = new JSONObject();
//            imageObject.put(JsonConstants.JSON_IMAGE_URL_TAG, element_image_url);
//            attributesObject.put(JsonConstants.JSON_IMAGE_TAG, imageObject);
//
//            jsonObject.put(JsonConstants.JSON_ATTRIBUTES_TAG, attributesObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return jsonObject;
//    }
//
//    @Override
//    public int describeContents() {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        // TODO Auto-generated method stub
//
//    }
//
//
//}
