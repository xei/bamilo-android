package com.mobile.newFramework.forms;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    public Map<String, Form> subForms;
    public Map<String, Integer> fieldMapping;
    
    public Map<String, FormField> mFieldKeyMap;

    private EventType eventType;

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

    /**
     * Form empty constructor.
     */
    public Form() {
        this.id = "";
        this.name = "";
        this.method = "";
        this.action = "";
        this.submit = "";

        this.fields = new ArrayList<>();
        this.subForms = new HashMap<>();
        this.mFieldKeyMap = new HashMap<>();
        this.fieldMapping = null;
        this.setEventType(null);
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
        this.subForms = new HashMap<>();
        this.mFieldKeyMap = new HashMap<>();
        this.fieldMapping = null;
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

            jsonObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG).getJSONObject(0);

            id = jsonObject.optString(RestConstants.JSON_ID_TAG);
            name = jsonObject.optString(RestConstants.JSON_FORM_TAG);
            method = jsonObject.optString(RestConstants.JSON_METHOD_TAG);
            action = jsonObject.optString(RestConstants.JSON_ACTION_TAG);
            submit = jsonObject.optString(RestConstants.JSON_SUBMIT_TAG);

            fields.clear();
            subForms.clear();
            
            if (eventType != null && FormsMapping.genericMapping.containsKey(eventType.toString())) {
                fieldMapping = FormsMapping.genericMapping.get(eventType.toString());
            } else if (FormsMapping.genericMapping.containsKey(id)) {
                fieldMapping = FormsMapping.genericMapping.get(id);
            }

            // Case FIELDS
            JSONArray fieldsArray = null;
            if(jsonObject.has(RestConstants.JSON_FIELDS_TAG)){
                fieldsArray = jsonObject.getJSONArray(RestConstants.JSON_FIELDS_TAG);
            }
            // Case OPTIONS
            else if(jsonObject.has(RestConstants.JSON_OPTIONS_TAG)) {
                fieldsArray = jsonObject.getJSONArray(RestConstants.JSON_OPTIONS_TAG);
                System.out.println("code1subForms: fieldsArray :  "+fieldsArray.length()+" name : "+name);
            }
            // Validate array
            if(fieldsArray != null){
                for (int i = 0; i < fieldsArray.length(); ++i) {
                    if(!fieldsArray.getJSONObject(i).has(RestConstants.JSON_SCENARIO_TAG)){
                        FormField field = new FormField(this);
                        if (field.initialize(fieldsArray.getJSONObject(i))) {
                            fields.add(field);
                            mFieldKeyMap.put(field.getKey(), field);
                        }
                    } else {
                        Form subForm = new Form();
                        subForm.initialize(fieldsArray.getJSONObject(i));
                        System.out.println("code1subForms : subForm :  " + subForm.name + " " + subForm.toString());
                        subForms.put(fieldsArray.getJSONObject(i).getString(RestConstants.JSON_SCENARIO_TAG), subForm);
                    }
                }
            }
            
            if(subForms != null && subForms.size() > 0){
                for (int i = 0; i < fields.size(); i++) {
                    if(fields.get(i).getDataSet().size()>0){
                        Set<String> keys = fields.get(i).getDataSet().keySet();
                        fields.get(i).setPaymentMethodsField(new HashMap<String, Form>());
                        for (String key : keys) {
                            if(subForms.containsKey(key)){
                                System.out.println("code1subForms : " + key + " : " + subForms.get(key).toString());
                                fields.get(i).getPaymentMethodsField().put(key, subForms.get(key));
                            }
                        }
                    }
                }
                subForms.clear();
                subForms = null;
            }
            
            if (null != fieldMapping) {
                // Remove unsorted fields.
                FormsMapping.removeUnsortedFields(this, fieldMapping);

                System.out.println("initialize: Sorting fields");
                Collections.sort(fields, new FormsMapping.byFieldOrder());
                
            }
            
        } catch (JSONException e) {
            System.out.println("initialize: error parsing jsonobject"+ e);
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
            jsonObject.put(RestConstants.JSON_SUBMIT_TAG, submit);

            JSONArray fieldArray = new JSONArray();
            for (FormField field : fields) {
                fieldArray.put(field.toJSON());
            }

            jsonObject.put(RestConstants.JSON_FIELDS_TAG, fieldArray);

        } catch (JSONException e) {
            System.out.println("trying to create json objects failed"+ e);
        }
        return jsonObject;
    }

    
    public Map<String, FormField> getFieldKeyMap(){
        return mFieldKeyMap;
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
        dest.writeString(submit);
        dest.writeList(fields);
        dest.writeMap(fieldMapping);
        dest.writeSerializable(eventType);
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
        fields = new ArrayList<>();
        in.readArrayList(FormField.class.getClassLoader());
        in.readMap(fieldMapping, null);
        eventType = (EventType) in.readSerializable();
    }
    
    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Create parcelable 
     */
    public static final Creator<Form> CREATOR = new Creator<Form>() {
        public Form createFromParcel(Parcel in) {
            return new Form(in);
        }

        public Form[] newArray(int size) {
            return new Form[size];
        }
    };
}
