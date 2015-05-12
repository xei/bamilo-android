package com.mobile.forms;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.framework.rest.RestConstants;
import com.mobile.utils.ShippingRadioGroupList;
import com.mobile.view.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.akquinet.android.androlog.Log;

public class ShippingMethodFormBuilder implements IJSONSerializable, Parcelable  {
    private final static String TAG = ShippingMethodFormBuilder.class.getName();

    public String id;
    public String name;
    public String method;
    public String action;
    // private float scale = 1;

    public ArrayList<ShippingMethodForm> fields;
    private ArrayList<ShippingRadioGroupList> groupList;
    /**
     * Empty constructor.
     */
    public ShippingMethodFormBuilder() {
        this.id = "";
        this.name = "";
        this.method = "";
        this.action = "";
        this.groupList = new ArrayList<>();
        this.fields = new ArrayList<>();
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
            name = jsonObject.optString(RestConstants.JSON_FORM_TAG);
            method = jsonObject.optString(RestConstants.JSON_METHOD_TAG);
            action = jsonObject.optString(RestConstants.JSON_ACTION_TAG);

            fields.clear();


            
            JSONArray fieldsArray = jsonObject.getJSONArray(RestConstants.JSON_FIELDS_TAG);
            if(fieldsArray != null){
                for (int i = 0; i < fieldsArray.length(); ++i) {
                    if(!fieldsArray.getJSONObject(i).has(RestConstants.JSON_SCENARIO_TAG)){
                        ShippingMethodForm field = new ShippingMethodForm();
                        if (field.initialize(fieldsArray.getJSONObject(i))) {
                            fields.add(field);
                        }
                    } else {
                        ShippingMethodSubForm subForm = new ShippingMethodSubForm();
                        subForm.initialize(fieldsArray.getJSONObject(i));
                        
                        Log.i(TAG, "code1subForms : subForm :  "+subForm.name+" "+subForm.toString());
                        for ( int j = 0; j < fields.size(); j++) {
                            if(fields.get(j).options.contains(subForm.scenario)){
                                fields.get(j).shippingMethodsSubForms.add(subForm);   
                            }
                        }
                    }
                }
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_FORM_TAG, name);
            jsonObject.put(RestConstants.JSON_METHOD_TAG, method);
            jsonObject.put(RestConstants.JSON_ACTION_TAG, action);

            JSONArray fieldArray = new JSONArray();
            for (ShippingMethodForm field : fields) {
                fieldArray.put(field.toJSON());
            }

            jsonObject.put(RestConstants.JSON_FIELDS_TAG, fieldArray);

        } catch (JSONException e) {
            Log.e(TAG, "trying to create json objects failed", e );
        }
        return jsonObject;
    }
    
    
    public View generateForm(Context context){
        LinearLayout parent;

        parent = new LinearLayout(context);
        LinearLayout.LayoutParams frmParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setLayoutParams(frmParams);
        
        if(fields != null && fields.size() > 0){
            for (ShippingMethodForm field : fields) {
                ShippingRadioGroupList mGroup = field.generateForm(context);
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
        if(fields != null && fields.size() > 0){
            for (ShippingMethodForm field : fields) {
                ShippingRadioGroupList mGroup = field.generateForm(context);
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
        for (ShippingMethodForm element : fields) {
            values.putAll(element.getContentValues());
        }
        return values;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(method);
        dest.writeString(action);
        dest.writeList(fields);
        
    }
    
    /**
     * Parcel constructor
     * @param in
     */
    private ShippingMethodFormBuilder(Parcel in) {
        id = in.readString();
        name = in.readString();
        method = in.readString();
        action = in.readString();
        fields = new ArrayList<>();
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