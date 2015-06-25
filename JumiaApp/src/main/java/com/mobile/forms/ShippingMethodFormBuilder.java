package com.mobile.forms;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.mobile.newFramework.objects.checkout.ShippingMethodFormBuilderHolder;
import com.mobile.newFramework.objects.checkout.ShippingMethodFormHolder;
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

        generateForm(context, parent);
                
        return parent;
    }
    
    
    /**
     * Generate a form using the view parent
     */
    public View generateForm(Context context, ViewGroup parent){
        if(shippingMethodFormBuilderHolder.fields != null && shippingMethodFormBuilderHolder.fields.size() > 0){
            for(int i = 0; i<shippingMethodFormBuilderHolder.fields.size(); i++){
                ShippingMethodForm field = new ShippingMethodForm(shippingMethodFormBuilderHolder.fields.get(i));
                ShippingRadioGroupList mGroup = field.generateForm(context);
                groupList.add(mGroup);
                parent.addView(mGroup);
                shippingMethodFormBuilderHolder.fields.remove(i);
                shippingMethodFormBuilderHolder.fields.add(i, field);
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
     * @param in
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
