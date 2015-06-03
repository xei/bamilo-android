package com.mobile.forms;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.utils.LogTagHelper;
import com.mobile.newFramework.requests.checkout.ShippingMethodFormHolder;
import com.mobile.utils.ShippingRadioGroupList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents an Shipping Method form.
 * 
 * @author Manuel Silva
 * 
 */
public class ShippingMethodForm implements Parcelable {
	private final static String TAG = LogTagHelper.create( ShippingMethodForm.class );

    private ShippingRadioGroupList mShippingRadioGroupList;

    public ShippingMethodFormHolder shippingMethodFormHolder;

    public ShippingMethodForm(){}

    public ShippingRadioGroupList generateForm(Context context){
        this.mShippingRadioGroupList = new ShippingRadioGroupList(context);
        this.mShippingRadioGroupList.setItems(this, shippingMethodFormHolder.value);
        return this.mShippingRadioGroupList;
    }
    
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(this.shippingMethodFormHolder.name, mShippingRadioGroupList.getSelectedFieldName());
        values.putAll(this.mShippingRadioGroupList.getValues());
        return values;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shippingMethodFormHolder.id);
        dest.writeString(shippingMethodFormHolder.key);
        dest.writeString(shippingMethodFormHolder.name);
        dest.writeString(shippingMethodFormHolder.value);
        dest.writeString(shippingMethodFormHolder.label);
        dest.writeString(shippingMethodFormHolder.type);
        dest.writeBooleanArray(new boolean[] {shippingMethodFormHolder.required});
        dest.writeList(shippingMethodFormHolder.options);
        dest.writeMap(shippingMethodFormHolder.optionsShippingMethod);
        dest.writeList(shippingMethodFormHolder.shippingMethodsSubForms);
    }
    
    /**
     * Parcel constructor
     * @param in
     */
    private ShippingMethodForm(Parcel in) {
        shippingMethodFormHolder.id = in.readString();
        shippingMethodFormHolder.key = in.readString();
        shippingMethodFormHolder.name = in.readString();
        shippingMethodFormHolder.value = in.readString();
        shippingMethodFormHolder.label = in.readString();
        shippingMethodFormHolder.type = in.readString();
        in.readBooleanArray(new boolean[] {shippingMethodFormHolder.required});
        shippingMethodFormHolder.options = new ArrayList<>();
        in.readArrayList(null);
        shippingMethodFormHolder.optionsShippingMethod = new HashMap<>();
        in.readMap(shippingMethodFormHolder.optionsShippingMethod, ShippingMethod.class.getClassLoader());
        shippingMethodFormHolder.shippingMethodsSubForms = new ArrayList<>();
        in.readArrayList(ShippingMethodSubForm.class.getClassLoader());
    }
    
    /**
     * Create parcelable 
     */
    public static final Parcelable.Creator<ShippingMethodForm> CREATOR = new Parcelable.Creator<ShippingMethodForm>() {
        public ShippingMethodForm createFromParcel(Parcel in) {
            return new ShippingMethodForm(in);
        }

        public ShippingMethodForm[] newArray(int size) {
            return new ShippingMethodForm[size];
        }
    };
    
}
