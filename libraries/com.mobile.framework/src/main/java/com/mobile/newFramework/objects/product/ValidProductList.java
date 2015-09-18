package com.mobile.newFramework.objects.product;


import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to save the newsletter subscription
 *
 * @author sergiopereira
 */
public class ValidProductList extends ArrayList<ProductMultiple> implements IJSONSerializable {

    protected static final String TAG = ValidProductList.class.getSimpleName();

    /**
     * Empty constructor
     */
    public ValidProductList() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get valid products
        JSONArray validProductArray = jsonObject.getJSONArray(RestConstants.JSON_VALID_TAG);
        for (int i = 0; i < validProductArray.length(); i++) {
            ProductMultiple validProduct = new ProductMultiple();
            validProduct.initialize(validProductArray.getJSONObject(i));
            add(validProduct);
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

}
