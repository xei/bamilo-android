package com.mobile.newFramework.objects.product;

import android.os.Parcel;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by spereira on 8/4/15.
 */
public class NewProductAddableToCart extends NewProductBase {

    private ArrayList<ProductSimple> mSimples;

    public NewProductAddableToCart() {
        //...
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Base product
        super.initialize(jsonObject);
        // Simples
        JSONArray simpleArray = jsonObject.getJSONArray(RestConstants.JSON_SIMPLES_TAG);
        int size = simpleArray.length();
        if (size > 0) {
            mSimples = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject simpleObject = simpleArray.getJSONObject(i);
                ProductSimple simple = new ProductSimple();
                if (simple.initialize(simpleObject)) {
                    mSimples.add(simple);
                }
            }
        }
        return true;
    }

    public ArrayList<ProductSimple> getSimples() {
        return mSimples;
    }

        /*
	 * ############ PARCELABLE ############
	 */

    protected NewProductAddableToCart(Parcel in) {
        // TODO
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unused")
    public static final Creator<NewProductAddableToCart> CREATOR = new Creator<NewProductAddableToCart>() {
        @Override
        public NewProductAddableToCart createFromParcel(Parcel in) {
            return new NewProductAddableToCart(in);
        }

        @Override
        public NewProductAddableToCart[] newArray(int size) {
            return new NewProductAddableToCart[size];
        }
    };
}
