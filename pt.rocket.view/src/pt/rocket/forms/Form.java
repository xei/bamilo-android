/**
 * Form.java
 * form class used to create dynamic forms. Contains an id, name, and array of form entries.
 * 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

import pt.rocket.framework.objects.Brand;
import pt.rocket.framework.objects.BrandImage;
import pt.rocket.framework.objects.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.LogTagHelper;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents an input form.
 * 
 * @author GuilhermeSilva
 * 
 */
public class Form implements IJSONSerializable, Parcelable {
	private final static String TAG = LogTagHelper.create( Form.class );

    public String id;
    public String name;
    public String method;
    public String action;
    public String submit;

    public ArrayList<FormField> fields;
    public Map<String, Integer> fieldMapping;
    
    public Map<String, FormField> theRealFieldMapping;
    /**
     * Form empty constructor.
     */
    public Form() {
        this.id = "";
        this.name = "";
        this.method = "";
        this.action = "";
        this.submit = "";

        this.fields = new ArrayList<FormField>();
        this.theRealFieldMapping = new HashMap<String, FormField>();
        this.fieldMapping = null;
    }

    /**
     * Form param constructor.
     * 
     * @param id of the form.
     * @param name of the form.
     * @param method of the form.
     * @param action url of the form.
     * @param submit url of the form.
     * @param fields of the form
     */
    public Form(String id, String name, String method, String action, String submit, ArrayList<FormField> fields) {
        this.id = id;
        this.name = name;
        this.method = method;
        this.action = action;
        this.submit = submit;

        this.fields = fields;
        this.theRealFieldMapping = new HashMap<String, FormField>();
        this.fieldMapping = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.optString(RestConstants.JSON_ID_TAG);
            name = jsonObject.optString(RestConstants.JSON_FORM_TAG);
            method = jsonObject.optString(RestConstants.JSON_METHOD_TAG);
            action = jsonObject.optString(RestConstants.JSON_ACTION_TAG);
            submit = jsonObject.optString(RestConstants.JSON_SUBMIT_TAG);

            fields.clear();

            if (FormsMapping.genericMapping.containsKey(id)) {
                fieldMapping = FormsMapping.genericMapping.get(id);
            }

            JSONArray fieldsArray = jsonObject.getJSONArray(RestConstants.JSON_FIELDS_TAG);
            
            for (int i = 0; i < fieldsArray.length(); ++i) {
                FormField field = new FormField(this);
                if (field.initialize(fieldsArray.getJSONObject(i))) {
                    fields.add(field);
                    
                    /**
                     * TODO: Validate if is necessary this map
                     */
                    theRealFieldMapping.put(field.getKey(), field);
                    
                }
            }
            
            if (null != fieldMapping) {
                // Remove unsorted fields.
                FormsMapping.removeUnsortedFields(this, fieldMapping);

                Log.d( TAG, "initialize: Sorting fields" );
                Collections.sort(fields, new FormsMapping.byFieldOrder());
                
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
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_FORM_TAG, name);
            jsonObject.put(RestConstants.JSON_METHOD_TAG, method);
            jsonObject.put(RestConstants.JSON_ACTION_TAG, action);
            jsonObject.put(RestConstants.JSON_SUBMIT_TAG, submit);

            JSONArray fieldArray = new JSONArray();
            for (FormField field : fields) {
                fieldArray.put(field.toJSON());
            }

            jsonObject.put(RestConstants.JSON_FIELDS_TAG, fieldArray);

        } catch (JSONException e) {
        	Log.e(TAG, "trying to create json objects failed", e );
        }
        return jsonObject;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(method);
        dest.writeString(action);
        dest.writeString(submit);
        dest.writeList(fields);
        dest.writeMap(fieldMapping);
        
    }
    
    /**
     * Parcel constructor
     * @param in
     */
    private Form(Parcel in) {
        id = in.readString();
        name = in.readString();
        method = in.readString();
        action = in.readString();
        submit = in.readString();
        fields = new ArrayList<FormField>();
        in.readArrayList(FormField.class.getClassLoader());
        in.readMap(fieldMapping, null);
        
        
    }
    
    /**
     * Create parcelable 
     */
    public static final Parcelable.Creator<Form> CREATOR = new Parcelable.Creator<Form>() {
        public Form createFromParcel(Parcel in) {
            return new Form(in);
        }

        public Form[] newArray(int size) {
            return new Form[size];
        }
    };
}
