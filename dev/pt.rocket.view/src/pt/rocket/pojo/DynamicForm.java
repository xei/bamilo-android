package pt.rocket.pojo;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt.rocket.framework.forms.Form;
import pt.rocket.framework.forms.InputType;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.ContentValues;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.actionbarsherlock.internal.widget.IcsAdapterView;

import de.akquinet.android.androlog.Log;

/**
 * This Class defines the representation of a dynamic form
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * @version 1.01
 * 
 *          2012/06/15
 * 
 */
public class DynamicForm implements Iterable<DynamicFormItem>{
    private final static String TAG = LogTagHelper.create(DynamicForm.class);

    private ViewGroup base;
    private List<DynamicFormItem> controls;
    private int lastID = 0x7f096000;
    private Form form;

    private OnFocusChangeListener focus_listener;
    private IcsAdapterView.OnItemSelectedListener itemSelected_listener;
    private TextWatcher text_watcher;

    /**
     * The constructor for the DynamicForm
     * 
     * @param base
     *            where the form is to be implemented. This is a ViewGroup (Layout) that will hold
     *            the form
     */
    public DynamicForm(ViewGroup base) {
        this.base = base;
        this.controls = new LinkedList<DynamicFormItem>();

        this.focus_listener = null;
        this.itemSelected_listener = null;
        this.text_watcher = null;

    }

    /**
     * Adds a control to the dynamic form
     * 
     * @param ctrl
     *            an instance of a DynamicFormItem to be added to the form
     */
    public void addControl(DynamicFormItem ctrl) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        addControl(ctrl, params);
    }

    /**
     * Adds a control to the dynamic form
     * 
     * @param ctrl
     *            An instance of a DynamicFormItem to be added to the form
     * @param params
     *            A LayoutParams instance to be used when inserting the control into the form
     */
    public void addControl(DynamicFormItem ctrl, ViewGroup.LayoutParams params) {
        View controlView = ctrl.getControl();

        if (null != controlView) {
            ctrl.setOnFocusChangeListener(focus_listener);
            ctrl.setOnItemSelectedListener(itemSelected_listener);

            controls.add(ctrl);

            base.addView(ctrl.getControl(), params);
        }
    }

    /**
     * Adds a control to the dynamic form
     * 
     * @param ctrl
     *            An instance of a DynamicFormItem to be added to the form
     * @param params
     *            A LayoutParams instance to be used when inserting the control into the form
     */
    public void addGroupedControl(ViewGroup group, DynamicFormItem ctrl,
            ViewGroup.LayoutParams params) {
        View controlView = ctrl.getControl();

        if (null != controlView && null != group) {
            ctrl.setOnFocusChangeListener(focus_listener);
            ctrl.setOnItemSelectedListener(itemSelected_listener);
            controls.add(ctrl);

            group.addView(controlView);
            if (null == base.findViewById(group.getId())) {
                base.addView(group);
            }
        }
    }

    /**
     * Gets an iterator to all the controls on the form
     * 
     * @return The iterator to the HashMap that contains all the controls
     */
    public Iterator<DynamicFormItem> getIterator() {
        return controls.iterator();
    }

//    /**
//     * Gets one of the form controls with a specific key
//     * 
//     * @param key
//     *            The key of the item to return
//     * @return The instance of the DynamicFormItem that has the given key
//     */
//    public DynamicFormItem getItem(String key) {
//        return controls.get(key);
//    }

    /**
     * Gets one of the form controls with a specific index
     * 
     * @param index
     *            The index of the item to return
     * @return The instance of the DynamicFormItem that has the given key
     */
    public DynamicFormItem getItem(int index) {
        return controls.get(index);
    }

    /**
     * Gets one of the form controls with a specific id
     * 
     * @param id
     *            The id of the item to return
     * @return The instance of the DynamicFormItem that has the given id, null if no control has the
     *         given id
     */
    public DynamicFormItem getItemById(int id) {
        DynamicFormItem control = null;
        boolean found = false;

        // gets an iterator to the hashmap
        Iterator<DynamicFormItem> iterator = iterator();
        Map.Entry<?, ?> pairs;

        while (iterator.hasNext() && !found) {

            control = iterator.next();
            // checks if there is a control with the same key as the one for the given value.
            if (null != control && id == control.getEditControl().getId()) {
                found = true;
            }
        }

        if (!found) {
            control = null;
        }

        return control;
    }

    public DynamicFormItem getItemByKey(String key) {
        DynamicFormItem control = null;
        // gets an iterator to the hashmap
        Iterator<DynamicFormItem> iterator = iterator();

        while (iterator.hasNext()) {
            control = iterator.next();
            if (null != control && key.equals(control.getKey())) {
                return control;
            }
        }

        return null;
    }

    /**
     * Gets the dynamic form container
     * 
     * @return The view that contains the form
     */
    public ViewGroup getContainer() {
        return base;
    }

    /**
     * Gets the next available id for the creation of controls
     * 
     * @return The next ID
     */
    public int getNextId() {
        return ++lastID;
    }

    /**
     * Gets the last used ID
     * 
     * @return the last id used to create a control
     */
    public int getLastId() {
        return lastID;
    }

    /**
     * Get the form object that is return from the framework containing all the information of the
     * API
     * 
     * @return the form object
     */
    public Form getForm() {
        return form;
    }

    /**
     * Sets the local version of the framework form object
     * 
     * @param value
     *            the form object
     */
    public void setForm(Form value) {
        form = value;
    }

//    /**
//     * Fills the form with a given data set
//     * 
//     * @param values
//     *            An HashMap that contains the data set to be displayed on the form, where the key
//     *            of the hashmap matches the key of each control.
//     */
//    public void fill(HashMap<String, String> values) {
//        DynamicFormItem control;
//
//        // gets an iterator to the hashmap
//        Iterator<Entry<String, String>> it = values.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<?, ?> pairs = (Map.Entry<?, ?>) it.next();
//
//            control = controls.get((String) pairs.getKey());
//            // checks if there is a control with the same key as the one for the given value.
//            if (null != control) {
//                control.setValue(pairs.getValue());
//            }
//        }
//    }

    /**
     * Validates the information on the form by querying all the controls and calling their
     * validation routines
     * 
     * @return If the form contains validates correctly (true: no errors; false: has errors)
     */
    public boolean validate() {
        boolean result = true;

        Iterator<DynamicFormItem> iterator = iterator();

        while (iterator.hasNext()) {
            result &= iterator.next().Validate();
            // Log.d( TAG, "validate: validated " + entry.getKey().toString() + " result = " +
            // result );
        }

        return result;
    }

    /**
     * Checks if all the required fields are filled
     * 
     * @return True, if all the required fields are filled; False, if the are unfilled required
     *         fields
     */
    public boolean checkRequired() {
        boolean result = true;

        Iterator<DynamicFormItem> iterator = iterator();

        while (iterator.hasNext()) {
            result &= iterator.next().ValidateRequired();
        }

        return result;
    }

    /**
     * Fills a hashmap with the values from the form
     * 
     * @return A hashmap containing the values from the form
     */
    public ContentValues save() {
        ContentValues model = new ContentValues();
        DynamicFormItem control;

        Iterator<DynamicFormItem> it = iterator();
        while (it.hasNext()) {

            control = it.next();
            Log.i(TAG,"code1 control: "+control.getName().toString());
            if (control != null && control.getType() == InputType.metadate) {
                control.addSubFormFieldValues(model);
                model.put(control.getName().toString(), control.getValue().toString());
            } else if (null != control && null != control.getValue()) {
                model.put(control.getName().toString(), control.getValue().toString());
            } else {
                model.put(control.getName().toString(), "");
            }
        }

        return model;
    }

//    /**
//     * Fills the hashmap with the data model, matching each key from the hash map with the keys from
//     * the from controls
//     * 
//     * @param model
//     *            The hash map to be filled
//     * @return The hash map with all the values present in the form
//     */
//    public HashMap<String, String> save(HashMap<String, String> model) {
//        DynamicFormItem control;
//
//        Iterator<Entry<String, String>> it = model.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
//
//            control = controls.get((String) pairs.getKey());
//            if (null != control) {
//                pairs.setValue((String) control.getValue());
//            }
//        }
//
//        return model;
//    }

    /**
     * Stores the current values of the edit controls on the server fields
     * 
     * @return The server form with the values stored in the fields
     */
    public Form storeInServerForm() {
        DynamicFormItem control;

        Iterator<DynamicFormItem> it = iterator();
        while (it.hasNext()) {

            control = it.next();
            if (null != control && null != control.getValue()) {
                control.storeValueInField();
            } else {
                control.storeValueInField("");
            }
        }

        return this.form;
    }

    /**
     * Resets all the fields on the form to their original values.
     */
    public void reset() {
        Iterator<DynamicFormItem> iterator = iterator();

        while (iterator.hasNext()) {
            iterator.next().resetValue();
        }

    }

    /**
     * Clears all the data on each control on the form.
     */
    public void clear() {
        Iterator<DynamicFormItem> iterator = iterator();

        while (iterator.hasNext()) {
            iterator.next().setValue(null);
        }

    }

    /**
     * Returns the number of controls visible in the form
     * 
     * @return The number of visible controls
     */
    public int getControlsCount() {
        return controls.size();
    }

    /**
     * Sets the OnFocusChanged listener to all the edit controls of the form
     * 
     * @param listener
     *            The listener to be fired every time the focus of an component changes
     */
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        focus_listener = listener;

        Iterator<DynamicFormItem> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next().setOnFocusChangeListener(focus_listener);
        }

    }

    /**
     * Sets the OnItemSelected listener to all the spinner controls of the form
     * 
     * @param listener
     *            The listener to be fired every time the selected item changes
     */
    public void setOnItemSelectedListener(IcsAdapterView.OnItemSelectedListener listener) {
        itemSelected_listener = listener;

        Iterator<DynamicFormItem> iterator = iterator();

        while (iterator.hasNext()) {
            iterator.next().setOnItemSelectedListener(itemSelected_listener);
        }

    }

    /**
     * Sets the TextWatcher listener to all the edit controls of the form
     * 
     * @param listener
     *            The listener to be fired every time the text of an component changes
     */
    public void setTextWatcher(TextWatcher watcher) {
        text_watcher = watcher;

        Iterator<DynamicFormItem> iterator = iterator();

        while (iterator.hasNext()) {
            iterator.next().setTextWatcher(text_watcher);
        }

    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<DynamicFormItem> iterator() {
        return controls.iterator();
    }

}
