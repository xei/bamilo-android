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
        /*TODO
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(value);
        dest.writeString(label);
        dest.writeString(type);
        dest.writeBooleanArray(new boolean[] {required});
        dest.writeList(options);
        dest.writeMap(optionsShippingMethod);
        dest.writeList(shippingMethodsSubForms);*/
    }
    
    /**
     * Parcel constructor
     * @param in
     */
    private ShippingMethodForm(Parcel in) {
        /* TODO
        id = in.readString();
        key = in.readString();        
        name = in.readString();
        value = in.readString();
        label = in.readString();
        type = in.readString();
        in.readBooleanArray(new boolean[] {required});
        options = new ArrayList<>();
        in.readArrayList(null);
        optionsShippingMethod = new HashMap<>();
        in.readMap(optionsShippingMethod, ShippingMethod.class.getClassLoader());
        shippingMethodsSubForms = new ArrayList<>();
        in.readArrayList(ShippingMethodSubForm.class.getClassLoader()); */
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
