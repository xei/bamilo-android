package com.mobile.newFramework.objects.product;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that manages the full representation of a given product bundle.
 *
 * @author Paulo Carvalho
 *
 */
public class ProductBundleProduct extends NewProductAddableToCart implements IJSONSerializable, Parcelable {

    public static final String TAG = ProductBundleProduct.class.getSimpleName();

    private int bundleProductLeaderPos;

    private ArrayList<ProductBundleSimple> bundleSimples;

    private boolean isChecked = true;

    private int simpleSelectedPos;

    /**
     * Complete product bundle empty constructor.
     */
    public ProductBundleProduct() {
        super();
        bundleProductLeaderPos = 0;
        isChecked = true;
        simpleSelectedPos = 0;
        bundleSimples = new ArrayList<>();
    }

    public ProductBundleProduct(JSONObject jsonObject) {
        this();
        initialize(jsonObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            // Base
            super.initialize(jsonObject);
            // Leader
            bundleProductLeaderPos = jsonObject.getInt(RestConstants.JSON_BUNDLE_PRODUCT_LEADER_POS);
            //
            JSONArray productsSimpleArray = jsonObject.optJSONArray(RestConstants.JSON_SIMPLES_TAG);
            if (productsSimpleArray != null && productsSimpleArray.length() > 0) {
                for (int i = 0; i < productsSimpleArray.length(); i++) {
                    JSONObject simpleJson = productsSimpleArray.getJSONObject(i);
                    ProductBundleSimple bundleSimpleProduct = new ProductBundleSimple(bundleProductLeaderPos, simpleJson);
                    bundleSimples.add(bundleSimpleProduct);
                }
            }
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    public int getBundleProductLeaderPos() {
        return bundleProductLeaderPos;
    }

    public ArrayList<ProductBundleSimple> getBundleSimples() {
        return bundleSimples;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getSimpleSelectedPos() {
        return simpleSelectedPos;
    }

    public void setSimpleSelectedPos(int simpleSelectedPos) {
        this.simpleSelectedPos = simpleSelectedPos;
    }


    /*
     * ############ PARCELABLE ############
     */

    protected ProductBundleProduct(Parcel in) {
        bundleProductLeaderPos = in.readInt();
        if (in.readByte() == 0x01) {
            bundleSimples = new ArrayList<>();
            in.readList(bundleSimples, ProductBundleSimple.class.getClassLoader());
        } else {
            bundleSimples = null;
        }
        isChecked = in.readByte() == 1;
        simpleSelectedPos = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bundleProductLeaderPos);
        if (bundleSimples == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bundleSimples);
        }
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeInt(simpleSelectedPos);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductBundleProduct> CREATOR = new Parcelable.Creator<ProductBundleProduct>() {
        @Override
        public ProductBundleProduct createFromParcel(Parcel in) {
            return new ProductBundleProduct(in);
        }

        @Override
        public ProductBundleProduct[] newArray(int size) {
            return new ProductBundleProduct[size];
        }
    };

}

