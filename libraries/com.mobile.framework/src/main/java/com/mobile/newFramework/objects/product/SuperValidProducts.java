package com.mobile.newFramework.objects.product;


import android.database.sqlite.SQLiteException;

import com.mobile.newFramework.database.LastViewedTableHelper;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to save the newsletter subscription
 * @author sergiopereira
 */
public class SuperValidProducts extends ArrayList<LastViewedAddableToCart> implements IJSONSerializable {

    protected static final String TAG = SuperValidProducts.class.getSimpleName();

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
                            Print.d("initialize" + 5);
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
     */
    private void updateRecentlyViewedDatabase(ArrayList<LastViewedAddableToCart> validProducts) {
        try {
            if (CollectionUtils.isEmpty(validProducts)) {
                LastViewedTableHelper.deleteAllLastViewed();
            } else {
                LastViewedTableHelper.updateLastViewed(validProducts);
            }
        } catch (IllegalStateException | SQLiteException e) {
            //...
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
