package com.bamilo.android.framework.service.objects.product;


import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.product.pojo.ProductMultiple;
import com.bamilo.android.framework.service.pojo.RestConstants;

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
        JSONArray validProductArray = jsonObject.getJSONArray(RestConstants.VALID);
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
