package com.mobile.forms;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.ViewGroup;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.ShippingRadioGroupList;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import de.akquinet.android.androlog.Log;

/**
 * Class that represents an Shipping Method form.
 * 
 * @author Manuel Silva
 * 
 */
public class ShippingMethodForm implements IJSONSerializable, Parcelable {
	private final static String TAG = LogTagHelper.create( ShippingMethodForm.class );

    public String id;
    public String key;
    public String name;
    public String value;
    public String label;
    public String type;
    public boolean required;
    public ArrayList<String> options;
    public HashMap<String, ShippingMethod> optionsShippingMethod;
    public ArrayList<ShippingMethodSubForm> shippingMethodsSubForms;
    private ShippingRadioGroupList mShippingRadioGroupList;
        /**
     * Form empty constructor.
     */
    public ShippingMethodForm() {
        this.id = "";
        this.key = "";
        this.name = "";
        this.value = "";
        this.label = "";
        this.type = "";
        this.required = false;
        this.options = new ArrayList<String>();
        this.optionsShippingMethod = new HashMap<String, ShippingMethod>();
        this.shippingMethodsSubForms = new ArrayList<ShippingMethodSubForm>();
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
            id = jsonObject.optString(RestConstants.JSON_ID_TAG);
            name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
            key = jsonObject.optString(RestConstants.JSON_KEY_TAG);
            value = jsonObject.optString(RestConstants.JSON_VALUE_TAG);
            label = jsonObject.optString(RestConstants.JSON_LABEL_TAG);
            type = jsonObject.optString(RestConstants.JSON_TYPE_TAG);
            
            if(jsonObject.has(RestConstants.JSON_VALIDATION_TAG)){
                required = jsonObject.getJSONObject(RestConstants.JSON_VALIDATION_TAG).optBoolean(RestConstants.JSON_REQUIRED_TAG, false);
            }
            
            JSONObject optionsObject = jsonObject.getJSONObject(RestConstants.JSON_OPTIONS_TAG);
            //Log.i(TAG, "options jsonobject: " +  optionsObject.toString());
             
            Iterator<?> opts = optionsObject.keys();
            while (opts.hasNext()) {
               String key = opts.next().toString();
               options.add(key);
               ShippingMethod shippingMethod = new ShippingMethod();
               shippingMethod.initialize(key, optionsObject.optJSONObject(key));
               optionsShippingMethod.put(key, shippingMethod);
            }
            
        } catch (JSONException e) {
        	Log.e(TAG, "initialize: error parsing jsonobject", e );
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

    public ShippingRadioGroupList generateForm(Context context, ViewGroup parent){
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
     * @param in
     */
    private ShippingMethodForm(Parcel in) {
        id = in.readString();
        key = in.readString();        
        name = in.readString();
        value = in.readString();
        label = in.readString();
        type = in.readString();
        in.readBooleanArray(new boolean[] {required});
        options = new ArrayList<String>(); 
        in.readArrayList(null);
        optionsShippingMethod = new HashMap<String, ShippingMethod>();
        in.readMap(optionsShippingMethod, ShippingMethod.class.getClassLoader());
        shippingMethodsSubForms = new ArrayList<ShippingMethodSubForm>();
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
