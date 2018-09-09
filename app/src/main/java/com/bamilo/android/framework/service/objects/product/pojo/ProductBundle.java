package com.bamilo.android.framework.service.objects.product.pojo;


import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that manages the full representation of a given product bundle.
 *
 * @author Paulo Carvalho
 *
 * // TODO TRY DELETE THIS CLASS TO USE ProductMultiple
 *
 */
public class ProductBundle extends ProductMultiple implements IJSONSerializable, Parcelable {

    public static final String TAG = ProductBundle.class.getSimpleName();

    private boolean isChecked = true;

    /**
     * Complete product bundle empty constructor.
     */
    public ProductBundle() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        return super.initialize(jsonObject);
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
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /*
     * ############ PARCELABLE ############
     */

    protected ProductBundle(Parcel in) {
        super(in);
        isChecked = in.readByte() == 1;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @SuppressWarnings("unused")
    public static final Creator<ProductBundle> CREATOR = new Creator<ProductBundle>() {
        @Override
        public ProductBundle createFromParcel(Parcel in) {
            return new ProductBundle(in);
        }

        @Override
        public ProductBundle[] newArray(int size) {
            return new ProductBundle[size];
        }
    };

}

