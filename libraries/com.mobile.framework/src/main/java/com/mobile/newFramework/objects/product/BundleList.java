package com.mobile.newFramework.objects.product;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

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
public class BundleList implements IJSONSerializable, Parcelable {

    protected static final String TAG = BundleList.class.getSimpleName();

    private String bundleName;
    private String bundleId;
    private String bundlePrice;
    private double bundlePriceDouble;
    private double bundlePriceConverted;
    private int bundleLeaderPos;
    private ArrayList<ProductBundle> bundleProducts;
    private ProductBundle selectedBundle;
    private int selectedBundlePosition = -1;

    /**
     * Complete product bundle empty constructor.
     */
    @SuppressWarnings("unused")
    public BundleList() {
        bundleName = "";
        bundleId = "";
        bundlePrice = "";
        bundlePriceDouble = 0.0;
        bundlePriceConverted = 0.0;
        bundleLeaderPos = 0;
        bundleProducts = new ArrayList<>();
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
            bundleId = jsonObject.getString(RestConstants.JSON_BUNDLE_ID);
            bundleName = jsonObject.getString(RestConstants.JSON_BUNDLE_NAME);
            String priceJSON = jsonObject.getString(RestConstants.JSON_BUNDLE_PRICE);
            if (!CurrencyFormatter.isNumber(priceJSON)) {
                throw new JSONException("Price is not a number!");
            }
            bundlePriceDouble = Double.parseDouble(priceJSON);
            bundlePrice = priceJSON;
            bundlePriceConverted = jsonObject.getDouble(RestConstants.JSON_BUNDLE_PRICE_CONVERTED);
            bundleLeaderPos = jsonObject.getInt(RestConstants.JSON_BUNDLE_LEADER_POS);
            JSONArray bundleProductsArray = jsonObject.optJSONArray(RestConstants.JSON_BUNDLE_PRODUCTS);
            if (bundleProductsArray != null && bundleProductsArray.length() > 0) {
                for (int i = 0; i < bundleProductsArray.length(); i++) {
                    JSONObject productJson = bundleProductsArray.getJSONObject(i);
                    ProductBundle bundleProduct = new ProductBundle(productJson);
                    bundleProducts.add(bundleProduct);
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
        return RequiredJson.OBJECT_DATA;
    }

    public String getBundleId() {
        return bundleId;
    }

    public ArrayList<ProductBundle> getBundleProducts() {
        return bundleProducts;
    }

    /*
     * ############ PARCELABLE ############
     */

    protected BundleList(Parcel in) {
        bundleName = in.readString();
        bundleId = in.readString();
        bundlePrice = in.readString();
        bundlePriceDouble = in.readDouble();
        bundlePriceConverted = in.readDouble();
        bundleLeaderPos = in.readInt();
        if (in.readByte() == 0x01) {
            bundleProducts = new ArrayList<>();
            in.readList(bundleProducts, ProductBundle.class.getClassLoader());
        } else {
            bundleProducts = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bundleName);
        dest.writeString(bundleId);
        dest.writeString(bundlePrice);
        dest.writeDouble(bundlePriceDouble);
        dest.writeDouble(bundlePriceConverted);
        dest.writeInt(bundleLeaderPos);
        if (bundleProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bundleProducts);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BundleList> CREATOR = new Parcelable.Creator<BundleList>() {
        @Override
        public BundleList createFromParcel(Parcel in) {
            return new BundleList(in);
        }

        @Override
        public BundleList[] newArray(int size) {
            return new BundleList[size];
        }
    };


    public double getBundlePriceDouble()
    {
        return bundlePriceDouble;
    }

    public void setBundlePriceDouble(double bundlePriceDouble)
    {
        this.bundlePriceDouble= bundlePriceDouble;
    }



    /**
     * Change a bundle product state and update total combo's price when checking/unchecking a bundle product
     * */
    public void updateTotalPriceWhenChecking(int bundlePosition)
    {
        if(CollectionUtils.isNotEmpty(bundleProducts))
        {
            //get selected bundle
            ProductBundle productBundle = bundleProducts.get(bundlePosition);
            //change for the oposite state
            productBundle.setChecked(!productBundle.isChecked());
            //update total price
            if(productBundle.isChecked())
            {
                if(productBundle.hasDiscount())
                    bundlePriceDouble += productBundle.getSpecialPrice();
                else
                    bundlePriceDouble += productBundle.getPrice();

            }else
            {
                if(productBundle.hasDiscount())
                    bundlePriceDouble -= productBundle.getSpecialPrice();
                else
                    bundlePriceDouble -= productBundle.getPrice();
            }

            //update item in bundle array
            bundleProducts.set(bundlePosition,productBundle);


        }
    }

    public ProductBundle getSelectedBundle(int bundlePosition)
    {
        if(CollectionUtils.isNotEmpty(bundleProducts))
        {
            return bundleProducts.get(bundlePosition);
        }

        return null;
    }


    public void setSelectedBundlePosition(int bundlePosition) { this.selectedBundlePosition = bundlePosition; }

}
