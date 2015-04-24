package com.mobile.framework.objects.home.object;

import com.mobile.framework.rest.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class TeaserBrandObject extends BaseTeaserObject {


    /**
     * Empty constructor
     */
    public TeaserBrandObject() {
        // ...
    }

    /*
     * ########## JSON ##########
     */

    /**
     * TODO
     *
     * @param jsonObject JSONObject containing the parameters of the object
     * @return
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        // Get image
        mImagePhone = mImageTablet = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);
        return true;
    }

}
