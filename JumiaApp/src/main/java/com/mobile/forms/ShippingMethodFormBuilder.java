package com.mobile.forms;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.constants.ConstantsIntentExtra;
import com.mobile.newFramework.objects.checkout.ShippingMethodFormBuilderHolder;
import com.mobile.newFramework.objects.checkout.ShippingMethodFormHolder;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.utils.ShippingRadioGroupList;

import java.util.ArrayList;

public class ShippingMethodFormBuilder implements Parcelable  {

    public final static String TAG = ShippingMethodFormBuilder.class.getName();

    private ShippingRadioGroupList mRadioGroup;

    public ShippingMethodFormBuilderHolder shippingMethodFormBuilderHolder;

    /**
     * Empty constructor
     */
    public ShippingMethodFormBuilder(){
        super();
    }

    /**
     * Generate a form using the view parent
     */
    public View generateForm(Context context, ViewGroup parent) {
        if (CollectionUtils.isNotEmpty(shippingMethodFormBuilderHolder.fields)) {
            int i = IntConstants.DEFAULT_POSITION;
            ShippingMethodForm field = new ShippingMethodForm(shippingMethodFormBuilderHolder.fields.get(i));
            mRadioGroup = field.generateForm(context);
            parent.addView(mRadioGroup);
            shippingMethodFormBuilderHolder.fields.remove(i);
            shippingMethodFormBuilderHolder.fields.add(i, field);
        }
        return parent;
    }

    public void saveState(@NonNull Bundle bundle) {
        try {
            // Get selected position from group
            int itemId = mRadioGroup.getSelectedIndex();
            // Validate selection
            if (itemId != IntConstants.INVALID_POSITION) {
                // Save it
                bundle.putInt(ConstantsIntentExtra.ARG_1, itemId);
                // Case PUS get sub selection
                int subItemId = mRadioGroup.getSubSelection(itemId);
                bundle.putInt(ConstantsIntentExtra.ARG_2, subItemId);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // ...
        }
    }

    public void loadSavedState(@Nullable Bundle bundle) {
        try {
            if (bundle != null) {
                int selection = bundle.getInt(ConstantsIntentExtra.ARG_1, IntConstants.INVALID_POSITION);
                int subSelection = bundle.getInt(ConstantsIntentExtra.ARG_2, IntConstants.INVALID_POSITION);
                mRadioGroup.setSelection(selection, subSelection);
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            // ...
        }
    }

    public ContentValues getValues(){
        ContentValues values = new ContentValues();
        for (ShippingMethodFormHolder element : shippingMethodFormBuilderHolder.fields) {
            values.putAll(((ShippingMethodForm)element).getContentValues());
        }
        return values;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shippingMethodFormBuilderHolder.id);
        dest.writeString(shippingMethodFormBuilderHolder.name);
        dest.writeString(shippingMethodFormBuilderHolder.method);
        dest.writeString(shippingMethodFormBuilderHolder.action);
        dest.writeList(shippingMethodFormBuilderHolder.fields);
    }
    
    /**
     * Parcel constructor
     */
    private ShippingMethodFormBuilder(Parcel in) {
        shippingMethodFormBuilderHolder.id = in.readString();
        shippingMethodFormBuilderHolder.name = in.readString();
        shippingMethodFormBuilderHolder.method = in.readString();
        shippingMethodFormBuilderHolder.action = in.readString();
        shippingMethodFormBuilderHolder.fields = new ArrayList<>();
        in.readArrayList(ShippingMethodForm.class.getClassLoader());
    }
    
    /**
     * Create parcelable 
     */
    public static final Parcelable.Creator<ShippingMethodFormBuilder> CREATOR = new Parcelable.Creator<ShippingMethodFormBuilder>() {
        public ShippingMethodFormBuilder createFromParcel(Parcel in) {
            return new ShippingMethodFormBuilder(in);
        }

        public ShippingMethodFormBuilder[] newArray(int size) {
            return new ShippingMethodFormBuilder[size];
        }
    };
}
