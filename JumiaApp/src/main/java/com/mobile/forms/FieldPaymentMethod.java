///**
// * FieldValidation.java
// * form field validation class. Manages if the form field is required, the max and minimum number of characters and the regular expression.
// *
// * @author Guilherme Silva
// *
// * @version 1.01
// *
// * 2012/06/18
// *
// * Copyright (c) Rocket Internet All Rights Reserved
// */
//package com.mobile.forms;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.newFramework.forms.FormField;
//import com.mobile.newFramework.objects.IJSONSerializable;
//import com.mobile.newFramework.objects.RequiredJson;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * Class that represents the form fiel validation parameters.
// * @author GuilhermeSilva
// *
// */
//public class FieldPaymentMethod implements IJSONSerializable, Parcelable {
//
//    private String name;
//    private ArrayList<FormField> fields;
//    private int value;
//
//	/**
//	 * FormValidation empty constructor.
//	 */
//	public FieldPaymentMethod(String name, JSONObject jsonObject) {
//	    this.name = name;
//	    this.fields = new ArrayList<>();
//	    initialize(jsonObject);
//	}
//
//
//	/**
//     * @return the name
//     */
//    public String getName() {
//        return name;
//    }
//
//
//
//
//
//    /**
//     * @return the fields
//     */
//    public ArrayList<FormField> getFields() {
//        return fields;
//    }
//
//
//
//
//
//    /**
//     * @return the value
//     */
//    public int getValue() {
//        return value;
//    }
//
//
//
//
//
//    /**
//     * @param name the name to set
//     */
//    public void setName(String name) {
//        this.name = name;
//    }
//
//
//
//
//
//    /**
//     * @param fields the fields to set
//     */
//    public void setFields(ArrayList<FormField> fields) {
//        this.fields = fields;
//    }
//
//
//
//
//
//    /**
//     * @param value the value to set
//     */
//    public void setValue(int value) {
//        this.value = value;
//    }
//
//
////  "Mtc_MobiCash": {
////  "fields": [
////      {
////          "key": "tc",
////          "type": "checkbox",
////          "rules": {
////              "required": {
////                  "requiredValue": true,
////                  "message": "You must agree to the terms and conditions of sale\n"
////              }
////          },
////          "value": "",
////          "label": "",
////          "name": "paymentMethodForm[Mtc_MobiCash][tc]",
////          "id": "paymentMethodForm_Mtc_MobiCash_tc"
////      }
////  ],
////  "value": "8"
////}
//
//
//    /* (non-Javadoc)
//	 * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
//	 */
//	@Override
//	public boolean initialize(JSONObject jsonObject) {
//	    // Get value
//	    if(jsonObject.has("value"))
//	        this.value = jsonObject.optInt("value");
//	    // Get fields
//	    if(jsonObject.has("fields")) {
//	        JSONArray fields = jsonObject.optJSONArray("fields");
//	        for (int i = 0; i < fields.length(); i++) {
//	            FormField field = new FormField(null);
//	            field.initialize(fields.optJSONObject(i));
//	            this.fields.add(field);
//	        }
//	    }
//	    return true;
//	}
//
//	/*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//     */
//	@Override
//	public JSONObject toJSON() {
//	    // TODO
//		return null;
//	}
//
//    @Override
//    public RequiredJson getRequiredJson() {
//        return null;
//    }
//
//
//    @Override
//    public int describeContents() {
//        // TODO Auto-generated method stub
//        return 0;
//    }
//
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(name);
//        dest.writeInt(value);
//        dest.writeTypedList(fields);
//    }
//
//    /**
//     * Parcel constructor
//     * @param in
//     */
//    private FieldPaymentMethod(Parcel in) {
//        name = in.readString();
//        value = in.readInt();
//    }
//
//    /**
//     * Create parcelable
//     */
//    public static final Parcelable.Creator<FieldPaymentMethod> CREATOR = new Parcelable.Creator<FieldPaymentMethod>() {
//        public FieldPaymentMethod createFromParcel(Parcel in) {
//            return new FieldPaymentMethod(in);
//        }
//
//        public FieldPaymentMethod[] newArray(int size) {
//            return new FieldPaymentMethod[size];
//        }
//    };
//}
