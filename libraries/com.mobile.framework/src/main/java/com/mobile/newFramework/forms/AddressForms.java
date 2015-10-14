package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONObject;

/**
 * Created by alexandrapires on 10/14/15.
 */
public class AddressForms implements IJSONSerializable, Parcelable {

    private Form shippingForm;

    private Form billingForm;


    public AddressForms()
    {
        shippingForm = new Form();
        billingForm = new Form();
    }


    public Form getShippingForm()
    {
        return shippingForm;
    }

    public Form getBillingForm()
    {
        return billingForm;
    }


    @Override
    public boolean initialize(JSONObject jsonObject) {

        if(shippingForm.initialize(jsonObject) &&  billingForm.initialize(jsonObject))
            return true;

        return true;
    }



    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.ARRAY_DATA_FIRST;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    protected AddressForms(Parcel in) {
    }

    public static final Creator<AddressForms> CREATOR = new Creator<AddressForms>() {
        @Override
        public AddressForms createFromParcel(Parcel in) {
            return new AddressForms(in);
        }

        @Override
        public AddressForms[] newArray(int size) {
            return new AddressForms[size];
        }
    };
}
