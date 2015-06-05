package com.mobile.newFramework.objects.product;


import com.mobile.framework.database.LastViewedTableHelper;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.apache.commons.collections4.CollectionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to save the newsletter subscription
 * @author sergiopereira
 */
public class SuperValidProducts extends ArrayList<LastViewedAddableToCart> implements IJSONSerializable {

    private static final String TAG = SuperValidProducts.class.getSimpleName();

    /**
     * Empty constructor
     */
    public SuperValidProducts() {
        // ...
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {

        boolean status;
        try {
            JSONArray validProductsArray = jsonObject.optJSONArray(RestConstants.JSON_VALID_TAG);
            if (validProductsArray != null) {
                if (validProductsArray.length() > 0) {
                    for (int i = 0; i < validProductsArray.length(); i++) {
                        LastViewedAddableToCart lastViewedAddableToCart = new LastViewedAddableToCart();
                        status = lastViewedAddableToCart.initialize(validProductsArray.getJSONObject(i));
                        lastViewedAddableToCart.setComplete(true);
                        add(lastViewedAddableToCart);

                        if (!status) {
                            System.out.println("initialize"+5);
                            //FIXME
//                            return parseErrorBundle(bundle);
                            return false;
                        }
                    }
                }
                updateRecentViewedDatabaseThread(this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }



    /**
     * using a new Thread to update the database data in a background thread, not blocking the UI thread
     */
    private void updateRecentViewedDatabaseThread(final ArrayList<LastViewedAddableToCart> validProducts) {

        new Thread(new Runnable() {
            public void run() {
                updateRecentlyViewedDatabase(validProducts);
            }
        }).start();

    }


    /**
     * update recentely viewed products on database
     *
     * @param validProducts
     * @return
     */
    private boolean updateRecentlyViewedDatabase(ArrayList<LastViewedAddableToCart> validProducts) {

        if (CollectionUtils.isEmpty(validProducts)) {
            LastViewedTableHelper.deleteAllLastViewed();
            return false;
        } else {
            LastViewedTableHelper.updateLastViewed(validProducts);
            return true;
        }
    }

//    public ArrayList<LastViewedAddableToCart> getValidProducts() {
//        return validProducts;
//    }
//
//    public void setValidProducts(ArrayList<LastViewedAddableToCart> validProducts) {
//        this.validProducts = validProducts;
//    }
}
