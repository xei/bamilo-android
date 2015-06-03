package com.mobile.forms;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.mobile.newFramework.requests.checkout.ShippingMethodFormBuilderHolder;
import com.mobile.newFramework.requests.checkout.ShippingMethodFormHolder;
import com.mobile.utils.ShippingRadioGroupList;
import com.mobile.view.R;

import java.util.ArrayList;

public class ShippingMethodFormBuilder implements Parcelable  {
    private final static String TAG = ShippingMethodFormBuilder.class.getName();

    private ArrayList<ShippingRadioGroupList> groupList;
    public ShippingMethodFormBuilderHolder shippingMethodFormBuilderHolder;

    public ShippingMethodFormBuilder(){
        super();
        this.groupList = new ArrayList<>();
    }

    public View generateForm(Context context){
        LinearLayout parent;

        parent = new LinearLayout(context);
        LinearLayout.LayoutParams frmParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(frmParams);
        
        if(shippingMethodFormBuilderHolder.fields != null && shippingMethodFormBuilderHolder.fields.size() > 0){
            for (ShippingMethodFormHolder field : shippingMethodFormBuilderHolder.fields) {
                ShippingMethodForm shippingMethodForm = new ShippingMethodForm();
                shippingMethodForm.shippingMethodFormHolder = field;
                ShippingRadioGroupList mGroup = shippingMethodForm.generateForm(context);
                groupList.add(mGroup);
                parent.addView(mGroup);
            }
        }
        
                
        return parent;
    }
    
    
    /**
     * Generate a form using the view parent
     */
    public View generateForm(Context context, ViewGroup parent){
        if(shippingMethodFormBuilderHolder.fields != null && shippingMethodFormBuilderHolder.fields.size() > 0){
            for (ShippingMethodFormHolder field : shippingMethodFormBuilderHolder.fields) {
                ShippingMethodForm shippingMethodForm = new ShippingMethodForm();
                shippingMethodForm.shippingMethodFormHolder = field;
                ShippingRadioGroupList mGroup = shippingMethodForm.generateForm(context);
                groupList.add(mGroup);
                parent.addView(mGroup);
            }
        }
        return parent;
    }
    
    
    public int getSelectionId(int groupId){
        try {
            return groupList.get(groupId).getSelectedIndex();
        } catch (Exception e) {
            return -1;
        }
    }
    
    public int getSubSelectionId(int groupId, int itemId){
        try {
            return groupList.get(groupId).getSubSelection(itemId);
        } catch (Exception e) {
            return -1;
        }
    }
    
    public void setSelections(int groupId, int itemId, int subItemId){
        try {
            if (groupList.get(groupId).findViewById(R.id.radio_container).findViewById(itemId) instanceof RadioButton) {
                groupList.get(groupId).findViewById(R.id.radio_container).findViewById(itemId).performClick();
                if(subItemId != -1)
                    groupList.get(groupId).setSubSelection(itemId, subItemId);
            }
        } catch (Exception ignored) {
        }
    }
    
    public ContentValues getValues(){
        ContentValues values = new ContentValues();
        for (ShippingMethodFormHolder element : shippingMethodFormBuilderHolder.fields) {
            ShippingMethodForm shippingMethodForm = new ShippingMethodForm();
            shippingMethodForm.shippingMethodFormHolder = element;
            values.putAll(shippingMethodForm.getContentValues());
        }
        return values;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /* TODO
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(method);
        dest.writeString(action);
        dest.writeList(fields);
        */
    }
    
    /**
     * Parcel constructor
     * @param in
     */
    private ShippingMethodFormBuilder(Parcel in) {
        /* TODO
        id = in.readString();
        name = in.readString();
        method = in.readString();
        action = in.readString();
        fields = new ArrayList<>();
        in.readArrayList(ShippingMethodForm.class.getClassLoader());
        */
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
