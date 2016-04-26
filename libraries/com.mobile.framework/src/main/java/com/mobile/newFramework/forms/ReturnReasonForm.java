package com.mobile.newFramework.forms;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by alexandrapires on 10/14/15.
 * <p/>
 * This class is used to keep two separate Form instances when creating addresses
 */
public class ReturnReasonForm extends ArrayList<Form> implements IJSONSerializable {

    private JSONObject mJson;

    /**
     * Empty constructor
     */
    public ReturnReasonForm() {
        super();
    }

    @Override
    @RequiredJson.JsonStruct
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    /**
     * Initialize both instances from a jsonObject Form
     */
    @Override
    public boolean initialize(JSONObject json) throws JSONException {
        // Save json string to build others
        mJson = json;
        // Create form
        Form form = new Form();
        form.initialize(json);
        // Save form
        add(form);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    /**
     * Generate forms
     * @throws JSONException
     */
    public void generateForms(int number) throws JSONException {
        // Validate number
        while (number > 1) {
            // Create form
            Form form = new Form();
            form.initialize(mJson);
            // Save form
            add(form);
            // Decrease
            number--;
        }
    }

}
