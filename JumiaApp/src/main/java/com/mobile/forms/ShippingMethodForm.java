package com.mobile.forms;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.checkout.ShippingMethodFormHolder;
import com.mobile.utils.ShippingRadioGroupList;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class that represents an Shipping Method form.
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class ShippingMethodForm extends ShippingMethodFormHolder implements Parcelable {

	private final static String TAG = ShippingMethodForm.class.getSimpleName();

    private ShippingRadioGroupList mShippingRadioGroupList;

    public ShippingMethodForm(){

    }

    public ShippingMethodForm(ShippingMethodFormHolder shippingMethodFormHolder){
        this.id = shippingMethodFormHolder.id;
        this.key = shippingMethodFormHolder.key;
        this.label = shippingMethodFormHolder.label;
        this.name = shippingMethodFormHolder.name;
        this.required = shippingMethodFormHolder.required;
        this.type = shippingMethodFormHolder.type;
        this.value = shippingMethodFormHolder.value;
        this.optionsShippingMethod= shippingMethodFormHolder.optionsShippingMethod;
        this.options = shippingMethodFormHolder.options;
        this.shippingMethodsSubForms = shippingMethodFormHolder.shippingMethodsSubForms;

    }

    public ShippingRadioGroupList generateForm(Context context){
        this.mShippingRadioGroupList = new ShippingRadioGroupList(context);
        this.mShippingRadioGroupList.setItems(this, value);
        return this.mShippingRadioGroupList;
    }
    
    public ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        values.put(this.name, mShippingRadioGroupList.getSelectedFieldName());
        values.putAll(this.mShippingRadioGroupList.getValues());
        return values;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(value);
        dest.writeString(label);
        dest.writeString(type);
        dest.writeBooleanArray(new boolean[] {required});
        dest.writeList(options);
        dest.writeMap(optionsShippingMethod);
        dest.writeList(shippingMethodsSubForms);
    }
    
    /**
     * Parcel constructor
     */
    private ShippingMethodForm(Parcel in) {
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
