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
package pt.rocket.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.interfaces.IJSONSerializable;
import pt.rocket.framework.rest.RestConstants;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents an input form.
 * 
 * @author GuilhermeSilva
 * 
 */
public class Form implements IJSONSerializable {
	private final static String TAG = "";
	
//    private static final String JSON_NAME_TAG = "form";
//    private static final String JSON_METHOD_TAG = "method";
//    private static final String JSON_ACTION_TAG = "action";
//    private static final String JSON_SUBMIT_TAG = "submit";
//    private static final String JSON_FIELDS_TAG = "fields";

    public String id;
    public String name;
    public String method;
    public String action;
    public String submit;

    public ArrayList<FormField> fields;
    public Map<String, Integer> fieldMapping;

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
//                android.util.Log.e("FIELD FORM",":"+fieldsArray.getJSONObject(i).toString());
                if (field.initialize(fieldsArray.getJSONObject(i))) {
                    fields.add(field);
                }
            }

            if (null != fieldMapping) {
            	
                printContentValues(fieldMapping);
                // Remove unsorted fields.
                FormsMapping.removeUnsortedFields(this, fieldMapping);

//                for (int i = 0; i < fields.size(); i++) {
//               	 android.util.Log.e("FIELD AFTER REMOVE",":"+fields.get(i).toJSON().toString());
//				}
                
                Log.d( TAG, "initialize: Sorting fields" );
                Collections.sort(fields, new FormsMapping.byFieldOrder());
                
//                for (int i = 0; i < fields.size(); i++) {
//                	 android.util.Log.e("FIELD AFTER COLLECTIONS",":"+fields.get(i).toJSON().toString());
//				}
            }
            
        } catch (JSONException e) {
        	Log.e(TAG, "initialize: error parsing jsonobject", e );
            return false;
        }

        return true;
    }

    
    
	public void printContentValues(Map<String, Integer> vals)
	{
	   Set<Entry<String, Integer>> s=vals.entrySet();
	   Iterator itr = s.iterator();

	   Log.d("DatabaseSync", "ContentValue Length :: " +vals.size());

	   while(itr.hasNext())
	   {
	        Map.Entry me = (Map.Entry)itr.next(); 
	        String key = me.getKey().toString();
	        Object value =  me.getValue();

//	        android.util.Log.e("FIELD MAPPING", "Key :: " +key  +" ::: values : "+value.toString());
	   }
	}
    
    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    
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
}
