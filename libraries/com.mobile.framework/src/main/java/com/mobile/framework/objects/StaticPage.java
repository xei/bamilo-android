package com.mobile.framework.objects;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class represents a static page that will display a CMS block. it can be static block, or shops in shop type
 * Created by Paulo Carvalho on 4/10/15.
 */
public class StaticPage implements IJSONSerializable {

    private String mHtml;
    private String mStaticPageType;
    public static final String SHOPS_IN_SHOP = "shop";
    public static final String STATIC_PAGE = "static_page";


    public StaticPage() {
        this.setHtml("");
        this.setStaticPageType("");

    }

    public String getHtml() {
        return mHtml;
    }

    public void setHtml(String html) {
        this.mHtml = html;
    }

    public String getStaticPageType() {
        return mStaticPageType;
    }

    public void setStaticPageType(String staticPageType) {
        this.mStaticPageType = staticPageType;
    }



    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONObject dataObject = jsonObject.getJSONObject(Constants.BUNDLE_DATA_KEY);
        if(dataObject != null){
            mStaticPageType = dataObject.optString(RestConstants.JSON_TYPE_TAG);
            mHtml = dataObject.optString(RestConstants.JSON_HTML_TAG);
        } else {
            return false;
        }

        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

}
