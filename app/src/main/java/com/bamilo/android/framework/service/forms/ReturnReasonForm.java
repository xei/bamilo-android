package com.bamilo.android.framework.service.forms;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Return reason form
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
        // Discard json
        mJson = null;
    }

}
