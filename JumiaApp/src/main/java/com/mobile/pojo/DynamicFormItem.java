package com.mobile.pojo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.mobile.app.JumiaApplication;
import com.mobile.components.absspinner.IcsAdapterView;
import com.mobile.components.absspinner.IcsSpinner;
import com.mobile.components.customfontviews.Button;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.components.customfontviews.TextView;
import com.mobile.forms.FieldValidation;
import com.mobile.forms.Form;
import com.mobile.forms.FormField;
import com.mobile.forms.IFormField;
import com.mobile.framework.Darwin;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.utils.InputType;
import com.mobile.utils.RadioGroupLayout;
import com.mobile.utils.RadioGroupLayoutVertical;
import com.mobile.utils.Toast;
import com.mobile.utils.dialogfragments.DialogDatePickerFragment;
import com.mobile.utils.dialogfragments.DialogDatePickerFragment.OnDatePickerDialogListener;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.akquinet.android.androlog.Log;

/**
 * This Class defines the representation of each control on a dynamic form
 * <p/>
 * <br>
 * <p/>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 *
 * @author Nuno Castro
 * @version 1.05
 *          <p/>
 *          2012/06/15
 */
public class DynamicFormItem {
    private final static String TAG = LogTagHelper.create(DynamicFormItem.class);

    private final static int ERRORTEXTSIZE = 14;
    private final static int MANDATORYSIGNALSIZE = 18;
    private final static int MANDATORYSIGNALMARGIN = 15;

    private Context context;
    private DynamicForm parent;
    private float scale = 1;
    private IFormField entry = null;
    private View errorControl;
    private View dataControl;
    private View control;
    private TextView errorTextControl;
    private TextView mandatoryControl;
    private String errorText;
    private OnFocusChangeListener editFocusListener;
    private IcsAdapterView.OnItemSelectedListener spinnerSelectedListener;
    private TextWatcher textWatcher;
    private DialogDatePickerFragment dialogDate;
    private int errorColor;
    private ArrayList<DynamicForm> childDynamicForm;
    private SharedPreferences mSharedPrefs;

    /**
     * The constructor for the DynamicFormItem
     *
     * @param parent  The parent of this control ( an instance of a DynamicForm )
     * @param context The context where this control is to be inserted. If the FormItem type date should
     *                be used, an activity needs to be given, as the date type wants to open a dialog.
     * @param entry   The entry that corresponds to this control on the form return by the framework
     * @return
     */
    public DynamicFormItem(DynamicForm parent, Context context, IFormField entry) {
        this.context = context;
        this.parent = parent;
        this.entry = entry;
        this.control = null;
        this.errorControl = null;
        this.dataControl = null;
        this.errorTextControl = null;
        this.mandatoryControl = null;
        this.errorText = context.getString(R.string.dynamic_errortext);
        this.scale = context.getResources().getDisplayMetrics().density;
        this.errorColor = context.getResources().getColor(R.color.red_basic);
        buildControl();
    }

    public IFormField getEntry() {
        return this.entry;
    }

    /**
     * Gets the name of the control
     *
     * @return The string that represents the name of the control
     */
    public String getName() {
        return this.entry.getName();
    }

    public String getKey() {
        return this.entry.getKey();
    }

    public String getMessage() {
        return this.entry.getValidation().message;
    }

    /**
     * Gets the control.
     *
     * @return The View that represents the control
     */
    public View getControl() {
        return control;
    }

    /**
     * Gets the editable control. This is the part of the control that handles the user input
     *
     * @return The View that represents the control
     */
    public View getEditControl() {
        return dataControl;
    }

    /**
     * Gets the Mandatory View control. This is the part of the control that handles the user input
     *
     * @return The View that represents the control
     */
    public View getMandatoryControl() {
        return mandatoryControl;
    }

    /**
     * Gets if the control has a mandatory flag, This flag indicates that the user must fill this
     * field before submiting the form.
     *
     * @return true or false depending of the control is mandatory or not
     */
    public boolean getMandatory() {
        return this.entry.getValidation().isRequired();
    }

    /**
     * Gets the error text associated to this control
     *
     * @return the text that is displayed if the control has an error with its filling
     */
    public String getErrorText() {
        return errorText;
    }

    /**
     * Sets the error text for this control, concerning the input of the user
     *
     * @param value The error message to display in case of a validation error
     */
    public void setErrorText(String value) {
        errorText = value;
        if (null != errorTextControl) {
            errorTextControl.setText(errorText);
        }
    }

    /**
     * Gets the type of the edit control that is displayed to the user. This type is defined in the
     * com.mobile.framework.forms namespace
     *
     * @return the type of the user control represented
     */
    public InputType getType() {
        return this.entry.getInputType();
    }

    public boolean isRadioGroupLayoutVertical() {
        return this.dataControl instanceof RadioGroupLayoutVertical;
    }

    public ContentValues getSubFormsValues() {
        return ((RadioGroupLayoutVertical) this.dataControl).getSubFieldParameters();
    }

    public int getSubFormsSelectedIndex() {
        return ((RadioGroupLayoutVertical) this.dataControl).getSelectedIndex();
    }

    public String getRadioGroupLayoutVerticalSelectedFieldName() {
        return ((RadioGroupLayoutVertical) this.dataControl).getSelectedFieldName();
    }

    /**
     * Creates the control with all the validation settings and mandatory representations
     */
    private void buildControl() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int controlWidth = RelativeLayout.LayoutParams.MATCH_PARENT;
        if (null != this.entry) {
            this.control = new RelativeLayout(this.context);
            this.control.setId(parent.getNextId());
            switch (this.entry.getInputType()) {
                case checkBox:
                    buildCheckBoxInflated(params, controlWidth);
                    break;
                case checkBoxList:
                    buildCheckBoxForTerms(params, controlWidth);
                    break;
                case radioRelated:
                    buildRelatedRadioGroup();
                    break;
                case radioGroup:
                    buildRadioGroup(params, controlWidth);
                    break;
                case list:
                    buildList(params, controlWidth);
                    break;
                case metadate:
                case date:
                    buildDate(params, controlWidth);
                    break;
                case number:
                    boolean datePart = isDatePart();
                    controlWidth = (!datePart) ? RelativeLayout.LayoutParams.MATCH_PARENT : context.getResources().getDimensionPixelSize(R.dimen.form_date_width);
                    params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    if (datePart) {
                        params.setMargins(0, 0, (int) (10 * scale), 0);
                    }
                case email:
                case text:
                case password:
                    buildText(params, controlWidth);
                    break;
                case hide:
                    buildHide(params, controlWidth);
                    break;
                case rating:
                    buildRatingOptionsTerms(params, controlWidth);
                    break;
                default:
                    Log.w(TAG, "buildControl: Field type not supported (" + this.entry.getInputType() + ") - " + this.entry.getInputType());
                    break;
            }
        }
    }

    /**
     * Stores the value inputed by the user on the control and acts accordingly
     *
     * @param value The value to fill the control with
     */
    public void setValue(Object value) {

        switch (this.entry.getInputType()) {
            case checkBox:
                boolean checked = false;
                if (null != value) {
                    if (value instanceof String) {
                        checked = value.equals("1");
                    } else if (value instanceof Boolean) {
                        checked = (Boolean) value;
                    } else if (value instanceof Integer) {
                        checked = ((Integer) value) == 1;
                    }
                }
                ((CheckBox) this.dataControl).setChecked(checked);

                break;

            case radioGroup:
                int position = findPositionForKey(value);

                if (this.dataControl instanceof IcsSpinner) {
                    int selection;
                    if (position == -1)
                        selection = IcsSpinner.INVALID_POSITION;
                    else
                        selection = position;

                    ((IcsSpinner) this.dataControl).setSelection(selection);
                } else {
                    int selection;
                    if (position == -1)
                        selection = RadioGroupLayout.NO_DEFAULT_SELECTION;
                    else
                        selection = position;
                    if (this.dataControl instanceof RadioGroupLayoutVertical) {
                        ((RadioGroupLayoutVertical) this.dataControl).setSelection(selection);
                    } else {
                        ((RadioGroupLayout) this.dataControl).setSelection(selection);
                    }

                }
                break;
            case metadate:
            case date:
                dialogDate.setDate((String) value);
                break;
            case email:
            case text:
            case password:
            case number:
                String text = null == value ? "" : (String) value;
                ((EditText) this.dataControl).setText(text);
                //java.lang.NoSuchMethodError: com.mobile.components.customfontviews.EditText.setLayoutDirection
                try {
                    //#RTL
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (context.getResources().getBoolean(R.bool.is_bamilo_specific) && currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        this.dataControl.setLayoutDirection(LayoutDirection.LOCALE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.errorControl.setVisibility(View.GONE);
                this.dataControl.setContentDescription(this.entry.getId());

                if (text.length() == 0) {
                    this.mandatoryControl
                            .setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE
                                    : View.GONE);
                } else {
                    this.mandatoryControl.setVisibility(View.GONE);
                }

                break;
            case hide:
                String text1 = (null == value) ? "" : (String) value;
                ((EditText) this.dataControl).setText(text1);
                this.dataControl.setVisibility(View.GONE);
                break;
            case rating:
                break;
            default:
                break;
        }
    }

    private int findPositionForKey(Object value) {
        if (value == null)
            return -1;

        int position;
        position = 0;
        if (this.entry.getDataSet().containsKey(value.toString())) {
            Iterator<Entry<String, String>> iter = this.entry.getDataSet().entrySet().iterator();
            boolean found = false;
            while (iter.hasNext()) {
                if (iter.next().getKey() == value) {
                    found = true;
                    break;
                }
                position++;
            }

            if (!found) {
                position = -1;
            }
        }
        return position;
    }

    /**
     * Resets the value of the field to it's original value
     */
    public void resetValue() {
        setValue(this.entry.getValue());
    }

    /**
     * Loads a previously saved state of the control. This is useful in an orientation changed
     * scenario, for example.
     *
     * @param inStat the Bundle that contains the stored information of the control
     */
    public void loadState(Bundle inStat) {
        switch (this.entry.getInputType()) {
            case meta:
                break;
            case checkBox:
                boolean checked = inStat.getBoolean(getKey());
                ((CheckBox) this.dataControl).setChecked(checked);

                break;
            case checkBoxList:
                boolean checkedList = inStat.getBoolean(getKey());
                ((CheckBox) this.dataControl.findViewWithTag("checkbox")).setChecked(checkedList);

                break;
            case radioGroup:
                int position = inStat.getInt(getKey());
                if (this.dataControl instanceof IcsSpinner) {
                    ((IcsSpinner) this.dataControl).setSelection(position);
                } else if (this.dataControl instanceof RadioGroupLayoutVertical) {
                    ((RadioGroupLayoutVertical) this.dataControl).setSelection(position);
                } else {
                    ((RadioGroupLayout) this.dataControl).setSelection(position);
                }

                break;

            case metadate:
            case date:
                String date = inStat.getString(getKey());
                this.dialogDate.setDate(date);
                GregorianCalendar cal = new GregorianCalendar(this.dialogDate.getYear(),
                        this.dialogDate.getMonth(), this.dialogDate.getDay());
                Date d = new Date(cal.getTimeInMillis());
                String dateFormated = DateFormat.getDateInstance(DateFormat.LONG).format(d);
                ((Button) this.dataControl).setText(dateFormated);
                this.mandatoryControl.setVisibility(View.GONE);
                break;
            case email:
            case text:
            case password:
            case number:
                String text = inStat.getString(getKey());

                ((EditText) this.dataControl).setText(text);
                this.errorControl.setVisibility(View.GONE);

                if (text.length() == 0) {
                    if (this.mandatoryControl != null) {
                        this.mandatoryControl
                                .setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE
                                        : View.GONE);
                    }
                } else {
                    if (this.mandatoryControl != null) {
                        this.mandatoryControl.setVisibility(View.GONE);
                    }

                }

                break;
            case hide:
                String text1 = inStat.getString(getKey());
                ((EditText) this.dataControl).setText(text1);
                this.dataControl.setVisibility(View.GONE);
                break;
            case rating:
                break;
            default:
                break;
        }

    }

    public void setSelectedPaymentMethod(int index) {
        ((RadioGroupLayoutVertical) this.dataControl).setPaymentSelection(index);
    }

    /**
     * Loads a previously saved state of the control. This is useful in an orientation changed
     * scenario, for example.
     *
     * @param inStat the Bundle that contains the stored information of the control
     */
    public void loadState(ContentValues inStat) {

        switch (this.entry.getInputType()) {
            case meta:
                break;
            case checkBox:
                boolean checked = inStat.getAsBoolean(getName());
                ((CheckBox) this.dataControl).setChecked(checked);

                break;
            case checkBoxList:
                boolean checkedList = inStat.getAsBoolean(getName());
                ((CheckBox) this.dataControl.findViewWithTag("checkbox")).setChecked(checkedList);

                break;
            case radioGroup:
                int position = inStat.getAsInteger(getName());
                if (this.dataControl instanceof IcsSpinner) {
                    ((IcsSpinner) this.dataControl).setSelection(position);
                } else if (this.dataControl instanceof RadioGroupLayoutVertical) {
                    ((RadioGroupLayoutVertical) this.dataControl).setSelection(position);
                } else {
                    ((RadioGroupLayout) this.dataControl).setSelection(position);
                }

                break;

            case metadate:
            case date:
                String date = inStat.getAsString(getName());
                this.dialogDate.setDate(date);
                GregorianCalendar cal = new GregorianCalendar(this.dialogDate.getYear(),
                        this.dialogDate.getMonth(), this.dialogDate.getDay());
                Date d = new Date(cal.getTimeInMillis());
                String dateFormated = DateFormat.getDateInstance(DateFormat.LONG).format(d);
                ((Button) this.dataControl).setText(dateFormated);
                this.mandatoryControl.setVisibility(View.GONE);
                break;
            case email:
            case text:
            case password:
            case number:
                String text = inStat.getAsString(getName());

                ((EditText) this.dataControl).setText(text);
                this.errorControl.setVisibility(View.GONE);

                if (text.length() == 0) {
                    if (this.mandatoryControl != null) {
                        this.mandatoryControl
                                .setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE
                                        : View.GONE);
                    }
                } else {
                    if (this.mandatoryControl != null) {
                        this.mandatoryControl.setVisibility(View.GONE);
                    }

                }

                break;
            case hide:
                String text1 = inStat.getAsString(getName());
                ((EditText) this.dataControl).setText(text1);
                this.dataControl.setVisibility(View.GONE);
                break;
            case rating:
                loadRatingState(inStat);
                break;
            default:
                break;
        }

    }

    /**
     * fill rating bar with saved values
     *
     * @param inStat
     */
    private void loadRatingState(ContentValues inStat) {
        Iterator it = this.entry.getDateSetRating().entrySet().iterator();
        int count = 1;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            float value = Float.parseFloat(inStat.getAsString(pairs.getKey().toString()));
            ((RatingBar) this.dataControl.findViewById(count).findViewById(R.id.option_stars)).setRating(value);
            count++;
        }
    }

    /**
     * Gets the value that the control currently holds
     *
     * @return The value that the control contains
     */
    public String getValue() {
        String result = null;
        switch (this.entry.getInputType()) {
            case checkBox:
                result = ((CheckBox) this.dataControl).isChecked() ? "1" : "0";
                break;
            case checkBoxList:
                result = ((CheckBox) this.dataControl.findViewWithTag("checkbox")).isChecked() ? "1" : "0";
                break;
            case radioRelated:
                int selectedIndex = ((RadioGroupLayout) this.dataControl).getSelectedIndex();
                result = entry.getRelatedFieldOptions().get(selectedIndex).getValue();
                break;
            case radioGroup:
                String value;
                if (this.dataControl instanceof IcsSpinner) {
                    value = ((IcsSpinner) this.dataControl).getSelectedItem().toString();
                } else if (this.dataControl instanceof RadioGroupLayoutVertical) {
                    int idx = ((RadioGroupLayoutVertical) this.dataControl).getSelectedIndex();
                    value = ((RadioGroupLayoutVertical) this.dataControl).getItemByIndex(idx);
                } else {
                    int idx = ((RadioGroupLayout) this.dataControl).getSelectedIndex();
                    value = ((RadioGroupLayout) this.dataControl).getItemByIndex(idx);
                }
                if (this.entry.getDataSet().containsValue(value)) {
                    Iterator<Entry<String, String>> iterator = this.entry.getDataSet().entrySet().iterator();
                    Entry<String, String> pair;
                    while (iterator.hasNext()) {
                        pair = iterator.next();
                        if (pair.getValue().equals(value)) {
                            result = pair.getKey();
                            break;
                        }
                    }
                }
                break;
            case metadate:
            case date:
                result = dialogDate.getDate();
                break;
            case hide:
            case email:
            case text:
            case password:
            case number:
                result = ((EditText) this.dataControl).getText().toString();
                break;
            case rating:
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    /**
     * Saves the state of the current field to a bundle object
     *
     * @param outState The Bundle object that will hold the state of the object
     */
    public void saveState(Bundle outState) {
        switch (this.entry.getInputType()) {
            case meta:
                break;
            case checkBox:
                outState.putBoolean(getKey(), ((CheckBox) this.dataControl).isChecked());
                break;
            case checkBoxList:
                outState.putBoolean(getKey(), ((CheckBox) this.dataControl.findViewWithTag("checkbox")).isChecked());
                break;
            case radioGroup:
                if (this.dataControl instanceof IcsSpinner) {
                    outState.putInt(getKey(), ((IcsSpinner) this.dataControl).getSelectedItemPosition());
                } else {
                    outState.putInt(getKey(), ((RadioGroupLayout) this.dataControl).getSelectedIndex());
                }
                break;
            case metadate:
            case date:
                outState.putString(getKey(), dialogDate.getDate());
                break;
            case hide:
            case email:
            case text:
            case password:
            case number:
                outState.putString(getKey(), ((EditText) this.dataControl).getText().toString());
                break;
            case rating:
                break;
            default:
                break;
        }
    }

    /**
     * Validates the data entered onto the control according to the validation rules defined and if
     * the field is mandatory or not
     *
     * @return true, if there is no errors; false, if there are errors
     * @modified ricardosoares
     */
    public boolean validate() {
        boolean result = true;
        if (hasRules() && this.errorControl != null) {
            switch (this.entry.getInputType()) {
                case checkBox:
                    if (this.entry.getValidation().isRequired())
                        result = ((CheckBox) this.dataControl).isChecked();
                    break;
                case checkBoxList:
                    result = ((CheckBox) this.dataControl.findViewWithTag("checkbox")).isChecked();
                    break;
                case radioRelated:
                case radioGroup:
                    boolean valid;
                    if (this.dataControl instanceof IcsSpinner) {
                        valid = ((IcsSpinner) this.dataControl).getSelectedItemPosition() != Spinner.INVALID_POSITION;
                    } else if (this.dataControl instanceof RadioGroupLayoutVertical) {
                        if (childDynamicForm != null && childDynamicForm.size() > 0) {
                            for (int i = 0; i < childDynamicForm.size(); i++) {
                                if (!childDynamicForm.get(i).validate()) {
                                    valid = false;
                                    break;
                                }
                            }
                        }
                        Log.i(TAG, "code1validate validating  : instanceof RadioGroupLayoutVertical");
                        valid = ((RadioGroupLayoutVertical) this.dataControl).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
                        // validate if accepted terms of payment method
                        if (valid) {
                            valid = ((RadioGroupLayoutVertical) this.dataControl).validateSelected();
                            if (!valid) {
                                result = !this.entry.getValidation().isRequired();
                                if (!result) {
                                    String errorMsg = ((RadioGroupLayoutVertical) this.dataControl).getErrorMessage();
                                    if (errorMsg != null && !errorMsg.equalsIgnoreCase("")) {
                                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                                    }
                                }
                                break;
                            }
                        }
                    } else {
                        Log.i(TAG, "code1validate validating  : instanceof RadioGroupLayout");
                        valid = ((RadioGroupLayout) this.dataControl).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
                    }

                    if (!valid) {
                        result = !this.entry.getValidation().isRequired();
                        setErrorText(context.getString(R.string.error_ismandatory) + " " + this.entry.getLabel());
                    }
                    break;
                case metadate:
                case date:
                    result = dialogDate.isSetOnce();
                    break;
                case email:
                case text:
                case password:
                case number: {
                    // Get value
                    String value = ((EditText) this.dataControl).getText().toString();
                    // TODO: REMOVE THIS APPROACH TO SET THE RELATED regex (TEMPORARY SOLUTION)
                    String regex = (String) this.dataControl.getTag(R.id.regex);
                    // Validate the value
                    result = validateStringToPattern(value, regex);
                    break;
                }
                case hide:
                    break;
                case rating:
                    result = validateRatingSet();
                    break;
                default:
                    break;
            }
            this.errorControl.setVisibility(!result ? View.VISIBLE : View.GONE);
        }

        return result;
    }

    private boolean validateStringToPattern(String value, String regexFromRelatedField) {
        boolean result;

        // SHOP: added empty space to prevent string from being cutted on burmese
        String space = "";
        if (context != null && context.getResources().getBoolean(R.bool.is_shop_specific))
            space = " ";

        if (value.length() == 0) {
            result = !this.entry.getValidation().isRequired();
            setErrorText(context.getString(R.string.error_ismandatory) + " " + this.entry.getLabel() + space);
        } else {
            if (this.entry.getValidation().min > 0 && value.length() < this.entry.getValidation().min) {
                setErrorText(this.entry.getLabel() + " " + context.getResources().getString(R.string.form_texttoshort) + space);
                result = false;
            } else if (this.entry.getValidation().max > 0 && value.length() > this.entry.getValidation().max) {
                setErrorText(this.entry.getLabel() + " " + context.getResources().getString(R.string.form_texttolong) + space);
                result = false;
            } else {
                // TODO: REMOVE THIS regexFromRelatedField (TEMPORARY SOLUTION)
                String regex = !TextUtils.isEmpty(regexFromRelatedField) ? regexFromRelatedField : this.entry.getValidation().regex;
                //Log.i(TAG, "RADIO RELATED: " + this.entry.getKey() + " regex validate " + regex);
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                setErrorText("The " + this.entry.getLabel() + " " + context.getString(R.string.dynamic_errortext) + space);
                Matcher matcher = pattern.matcher(value);
                result = matcher.find();
            }
        }
        return result;
    }

    /**
     * Displays the controls error message to the user
     *
     * @param message The error message to be displayed on the control
     */
    public void ShowError(String message) {
        if (null != errorControl) {
            setErrorText(message);
            this.errorControl.setVisibility(message.equals("") ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Validates if the field is required and if it is filled
     *
     * @return true, if the field if OK; false, if the field is empty and is required
     */
    public boolean validateRequired() {
        boolean result = true;

        switch (this.entry.getInputType()) {
            case checkBox:
                if (this.entry.getValidation().isRequired())
                    result = ((CheckBox) this.dataControl).isChecked();
                break;
            case checkBoxList:

                if (!((CheckBox) this.dataControl.findViewWithTag("checkbox")).isChecked())
                    result = !this.entry.getValidation().isRequired();
                else
                    result = ((CheckBox) this.dataControl.findViewWithTag("checkbox")).isChecked();
                break;
            case radioGroup:
                boolean valid;

                if (this.dataControl instanceof IcsSpinner)
                    valid = ((IcsSpinner) this.dataControl).getSelectedItemPosition() != Spinner.INVALID_POSITION;
                else if (this.dataControl instanceof RadioGroupLayoutVertical) {
                    valid = ((RadioGroupLayoutVertical) this.dataControl).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
                } else {
                    valid = ((RadioGroupLayout) this.dataControl).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
                }

                if (!valid) {
                    result = !this.entry.getValidation().isRequired();
                } else {
                    result = valid;
                }
                break;

            case metadate:
            case date:

                valid = dialogDate.isSetOnce();

                if (!valid)
                    result = !this.entry.getValidation().isRequired();
                else
                    result = valid;
                break;
            case email:
            case text:
            case password:
            case number:
                String value = ((EditText) this.dataControl).getText().toString();

                if (value.length() == 0) {
                    result = !this.entry.getValidation().isRequired();
                }

                break;
            case hide:
                break;
            case rating:
                break;
            default:
                break;
        }

        return result;
    }


    /**
     * Validate if all ratings options are filled
     *
     * @return boolean is valid or not
     */
    private boolean validateRatingSet() {

        boolean areAllFilled = true;

        LinearLayout ratingList = ((LinearLayout) this.dataControl);

        Iterator it = this.entry.getDateSetRating().entrySet().iterator();
        int count = 1;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();

            float rate = ((RatingBar) ratingList.findViewById(count).findViewById(R.id.option_stars)).getRating();

            if (rate == 0.0)
                areAllFilled = false;

            count++;
        }
        return areAllFilled;
    }

    /**
     * Sets the listener for the focus change of the edit part of the component
     *
     * @param listener The listener to be fired when the focus changes
     */
    public void setOnFocusChangeListener(OnFocusChangeListener listener) {
        editFocusListener = listener;
    }

    /**
     * Sets the listener for the seleted item changes of the edit part of the component
     *
     * @param listener The listener to be fired when the focus changes
     */
    public void setOnItemSelectedListener(IcsAdapterView.OnItemSelectedListener listener) {
        spinnerSelectedListener = listener;
    }

    /**
     * Sets the TextWatcher listener to be used by this control
     *
     * @param watcher The listener to be fired every time the text of an component changes
     */
    public void setTextWatcher(TextWatcher watcher) {
        textWatcher = watcher;
    }

    private void buildCheckBoxForTerms(RelativeLayout.LayoutParams params, int controlWidth) {

        this.control.setLayoutParams(params);

        // data controls
        params = new RelativeLayout.LayoutParams(controlWidth,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(controlWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        this.dataControl = new LinearLayout(this.context);

        CheckBox mCheckBox = new CheckBox(this.context);

        mCheckBox.setTextColor(context.getResources().getColor(
                R.color.form_text));
        mCheckBox.setId(parent.getNextId());
        this.dataControl.setId(parent.getNextId());

        lParams.setMargins((int) (3 * this.scale), 0, 0, 0);
        this.dataControl.setLayoutParams(lParams);
        mCheckBox.setTag("checkbox");
        mCheckBox.setContentDescription(this.entry.getKey());
        mCheckBox.setButtonDrawable(R.drawable.selector_btn_check_holo_orange);
        mCheckBox.setFocusable(false);
        mCheckBox.setFocusableInTouchMode(false);
        mCheckBox.setTextColor(this.context.getResources().getColor(
                R.color.grey_middle));
        mCheckBox.setTextSize(14);
        mCheckBox.setText(this.entry.getLabel().length() > 0 ? this.entry
                .getLabel() : this.context.getString(R.string.register_text_terms_a) + " ");
        mCheckBox.setPadding((int) (25 * scale),
                mCheckBox.getPaddingTop(), mCheckBox.getPaddingRight(),
                mCheckBox.getPaddingBottom());

        if (this.entry.getValue().equals("1")) {
            mCheckBox.setChecked(true);
        }

        mCheckBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked() && entry.getValidation().isRequired()) {
                    mandatoryControl.setVisibility(View.GONE);
                } else if (!((CheckBox) v).isChecked() && entry.getValidation().isRequired()) {
                    mandatoryControl.setVisibility(View.VISIBLE);
                }

            }
        });

        ((LinearLayout) this.dataControl).addView(mCheckBox);

        TextView mLinkTextView = new TextView(this.context);
        Log.i(TAG, "code1link : " + this.entry.getLinkText());
        mLinkTextView.setText(this.entry.getLinkText());
        mLinkTextView.setId(parent.getNextId());
        mLinkTextView.setTag(this.entry.getKey());
        mLinkTextView.setTextColor(this.context.getResources().getColor(
                R.color.blue_basic));
        this.dataControl.setVisibility(View.VISIBLE);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, mCheckBox.getId());
        mLinkTextView.setLayoutParams(params);
        ((LinearLayout) this.dataControl).addView(mLinkTextView);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;

        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_ffa200));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE
                : View.GONE);

        ((ViewGroup) this.control).addView(this.dataControl);

        ((ViewGroup) this.control).addView(this.mandatoryControl);

        ((ViewGroup) this.control).addView(dataContainer);

    }

    private void buildCheckBoxInflated(RelativeLayout.LayoutParams params, int controlWidth) {

        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        this.control.setLayoutParams(params);
        //#RTL
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific) && currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            // data controls
            params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }

        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);


        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);

        int formPadding = context.getResources().getDimensionPixelOffset(R.dimen.form_check_padding);
        params.leftMargin = formPadding;
        params.rightMargin = formPadding;
        this.dataControl = View.inflate(this.context, R.layout.form_checkbox, null);
        this.dataControl.setId(parent.getNextId());

        params.addRule(RelativeLayout.CENTER_VERTICAL);
        this.dataControl.setLayoutParams(params);
        this.dataControl.setContentDescription(this.entry.getKey());
        this.dataControl.setFocusable(false);
        this.dataControl.setFocusableInTouchMode(false);
        ((CheckBox) this.dataControl).setText(this.entry.getLabel().length() > 0 ? this.entry.getLabel() : this.context.getString(R.string.register_text_terms_a) + " " + this.context.getString(R.string.register_text_terms_b));

        if (this.entry.getValue().equals("1")) {
            ((CheckBox) this.dataControl).setChecked(true);
        }

        this.dataControl.setVisibility(View.VISIBLE);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        //#RTL
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }

        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_ffa200));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);

        dataContainer.addView(this.dataControl);
        dataContainer.addView(this.mandatoryControl);

        ((ViewGroup) this.control).addView(dataContainer);
    }

    private void buildRadioGroup(RelativeLayout.LayoutParams params, int controlWidth) {
        this.control.setLayoutParams(params);

        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        //#RTL
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        } else {
            params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }

        if (this.entry.getDataSet().size() > 2 || this.parent.getForm().fields.get(0).getPaymentMethodsField() != null) {
            Log.d("createRadioGroup", "createRadioGroup: Radio Group ORIENTATION_VERTICAL");
            createRadioGroupVertical(MANDATORYSIGNALSIZE, params, dataContainer);
        } else {
            Log.d("createRadioGroup", "createRadioGroup: Radio Group ORIENTATION_HORIZONTAL");
            createRadioGroup(MANDATORYSIGNALSIZE, params, dataContainer);
        }

        if (hasRules()) {
            this.errorControl = createErrorControl(dataContainer.getId(), controlWidth);
            //#RTL
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.errorControl.setLayoutDirection(LayoutDirection.RTL);
            }
            ((ViewGroup) this.control).addView(this.errorControl);
        }
    }

    /**
     * Build a related RadioGroup that applies a new validation for respective related form field.
     * TODO: This is a temporary solution
     */
    private void buildRelatedRadioGroup() {
        createRelatedRadioGroup();
    }

    private void buildList(RelativeLayout.LayoutParams params, int controlWidth) {
        this.control.setLayoutParams(params);
        // data controls
        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);

        createSpinnerForRadioGroup(MANDATORYSIGNALSIZE, params, dataContainer);
    }

    private void buildDate(RelativeLayout.LayoutParams params, int controlWidth) {
        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 6;
        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.dataControl = View.inflate(this.context, R.layout.form_button, null);
        this.dataControl.setId(parent.getNextId());
        this.dataControl.setLayoutParams(params);

        this.dataControl.setContentDescription(this.entry.getKey());

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_ffa200));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);
        dataContainer.addView(this.dataControl);
        dataContainer.addView(this.mandatoryControl);

        if (null != this.entry.getLabel() && this.entry.getLabel().trim().length() > 0) {
            String text = this.entry.getLabel();
            ((Button) this.dataControl).setText(text);
            ((Button) this.dataControl).setTextColor(context.getResources().getColor(R.color.form_text));
        } else if (this.entry.getKey().equals("birthday")) {
            Log.i("ENTERED BIRTHDAY", " HERE ");
            String text = context.getString(R.string.register_birthday);
            ((Button) this.dataControl).setHint(text);
            ((Button) this.dataControl).setHintTextColor(context.getResources().getColor(R.color.form_text_hint));
            ((Button) this.dataControl).setTextColor(context.getResources().getColor(R.color.form_text));
            this.dataControl.setPadding(UIUtils.dpToPx(13, scale), 0, 0, 10);
            // ((Button)
            // this.dataControl).setTextColor(context.getResources().getColor(R.color.form_text));

        }

        String dialogTitle;
        if (this.entry.getKey().equals("birthday"))
            dialogTitle = context.getString(R.string.register_dialog_birthday);
        else
            dialogTitle = this.entry.getLabel();

        OnDatePickerDialogListener pickerListener = new OnDatePickerDialogListener() {

            @Override
            public void onDatePickerDialogSelect(int year, int month, int day) {
                Log.i(TAG, "code1date : year : " + year);
                GregorianCalendar cal = new GregorianCalendar(year, month, day);
                Date d = new Date(cal.getTimeInMillis());
                String date = DateFormat.getDateInstance(DateFormat.LONG).format(d);
                ((Button) DynamicFormItem.this.dataControl).setText(date);
                Log.i(TAG, "code1date : date : " + date);
                DynamicFormItem.this.mandatoryControl.setVisibility(View.GONE);
            }
        };

        this.dialogDate = DialogDatePickerFragment.newInstance((BaseActivity) context,
                pickerListener, String.valueOf(dataControl.getId()), dialogTitle, 0, 0, 0);
        this.dataControl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!dialogDate.isVisible())
                    dialogDate.show(((BaseActivity) context).getSupportFragmentManager(), null);
            }
        });

        ((ViewGroup) this.control).addView(dataContainer);
    }

    private void buildText(RelativeLayout.LayoutParams params, int controlWidth) {
        this.control.setLayoutParams(params);

        ViewGroup dataContainer = createTextDataContainer(controlWidth);
        int dataControlId = dataContainer.getId();
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        if (hasRules()) {
            this.errorControl = createErrorControl(dataControlId, controlWidth);

            //#RTL
            if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.errorControl.setLayoutDirection(LayoutDirection.RTL);
            }
            ((ViewGroup) this.control).addView(this.errorControl);
        }
        //#RTL
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific) && currentApiVersion < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((EditText) this.dataControl).setGravity(Gravity.RIGHT);
        }

        ((ViewGroup) this.control).addView(dataContainer);

        this.dataControl.setContentDescription(this.entry.getKey());
        // Listeners

        //TODO
        //FIXME to be fixed on previous rating reform, uncomment crashes app

//        this.dataControl.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                EditText dataCtrl = (EditText) v;
//                if (hasFocus) {
//                    errorControl.setVisibility(View.GONE);
//                    mandatoryControl.setVisibility(View.GONE);
//                    // Uncomment the below line if you want hide the hint when the focus changes
//                    // dataCtrl.setHint(" ");
//                    dataCtrl.setCursorVisible(true);
//                } else {
//                    if (entry.getValidation().isRequired()) {
//                        mandatoryControl
//                                .setVisibility(dataCtrl.getText().toString().length() == 0 ? View.VISIBLE
//                                        : View.GONE);
//                    }
//                    if (dataCtrl.getHint().toString().equals(" ")) {
//                        dataCtrl.setHint(editText);
//                    }
//
//                }
//
//                if (null != editFocusListener) {
//                    editFocusListener.onFocusChange(v, hasFocus);
//
//                }
//            }
//        });
//
//        ((EditText) this.dataControl).addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (null != textWatcher) {
//                    textWatcher.afterTextChanged(s);
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                if (null != textWatcher) {
//                    textWatcher.beforeTextChanged(s, start, count, after);
//                }
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (null != textWatcher) {
//                    textWatcher.onTextChanged(s, start, before, count);
//                }
//            }
//
//        });

    }

    /**
     * Creates the control with hidden value
     */
    private void buildHide(RelativeLayout.LayoutParams params,
                           int controlWidth) {
        this.control.setLayoutParams(params);
        // Don't allow an hidden control to take visual space
        this.control.setVisibility(View.GONE);
        createTextDataContainer(controlWidth);
        EditText editText = ((EditText) this.dataControl);
        editText.setText(this.entry.getValue());
        editText.setContentDescription(this.entry.getKey());
        editText.setVisibility(View.GONE);
    }

    private void createSpinnerForRadioGroup(final int MANDATORYSIGNALSIZE, RelativeLayout.LayoutParams params, RelativeLayout dataContainer) {
        this.dataControl = View.inflate(this.context, R.layout.form_icsspinner, null);
        this.dataControl.setId(parent.getNextId());
        this.dataControl.setLayoutParams(params);

        if (this.entry.getDataSet().size() > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    R.layout.form_spinner_item, new ArrayList<>(this.entry.getDataSet()
                    .values()));
            adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
            ((IcsSpinner) this.dataControl).setAdapter(adapter);

        } else {
            ArrayList<String> default_string = new ArrayList<>();
            default_string.add("Empty");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                    R.layout.form_spinner_item, default_string);
            adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
            ((IcsSpinner) this.dataControl).setAdapter(adapter);
        }

        // sets the spinner value
        int position = 0;
        if (null != this.entry.getValue() && !this.entry.getValue().trim().equals("")) {
            for (String item : new ArrayList<>(this.entry.getDataSet().values())) {
                if (item.equals(this.entry.getValue())) {
                    ((IcsSpinner) this.dataControl).setSelection(position);
                    break;
                }
            }
            position++;
        }

        this.dataControl.setVisibility(View.VISIBLE);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //#RTL
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
        }

        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_ffa200));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE
                : View.GONE);
        dataContainer.addView(this.dataControl);
        dataContainer.addView(this.mandatoryControl);

        ((ViewGroup) this.control).addView(dataContainer);

        HoloFontLoader.applyDefaultFont(dataContainer);
        // Listeners
        ((IcsSpinner) this.dataControl)
                .setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
                        if (entry.getValidation().isRequired()) {
                            mandatoryControl
                                    .setVisibility(position == Spinner.INVALID_POSITION ? View.VISIBLE
                                            : View.GONE);
                        }

                        if (null != spinnerSelectedListener) {
                            spinnerSelectedListener.onItemSelected(parent, view, position, id);
                        }
                    }

                    @Override
                    public void onNothingSelected(IcsAdapterView<?> parent) {
                        if (entry.getValidation().isRequired()) {
                            mandatoryControl.setVisibility(View.VISIBLE);
                        }

                        if (null != spinnerSelectedListener) {
                            spinnerSelectedListener.onNothingSelected(parent);
                        }
                    }
                });

        this.entry.setOnDataSetReceived(new FormField.OnDataSetReceived() {

            @Override
            public void DataSetReceived(Map<String, String> dataSet) {
                if (dataSet.size() > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                            R.layout.form_spinner_item, new ArrayList<>(dataSet.values()));
                    adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
                    ((IcsSpinner) dataControl).setAdapter(adapter);

                } else {
                    ArrayList<String> default_string = new ArrayList<>();
                    default_string.add("Empty");
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                            R.layout.form_spinner_item, default_string);
                    adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
                    ((IcsSpinner) dataControl).setAdapter(adapter);
                }

                // sets the spinner value
                int position = 0;
                if (null != entry.getValue() && !entry.getValue().trim().equals("")) {
                    for (String item : new ArrayList<>(dataSet.values())) {
                        if (item.equals(entry.getValue())) {
                            ((IcsSpinner) dataControl).setSelection(position);
                            break;
                        }
                    }
                    position++;
                }
            }
        });
    }

    /**
     * Create an horizontal radio group
     *
     * @param MANDATORYSIGNALSIZE
     * @param params
     * @param dataContainer
     */
    private void createRadioGroup(final int MANDATORYSIGNALSIZE, RelativeLayout.LayoutParams params, RelativeLayout dataContainer) {
        //Preselection
        int defaultSelect = 0;
        boolean foundDefaultSelect = false;
        for (Entry<String, String> entryValue : this.entry.getDataSet().entrySet()) {
            if (!foundDefaultSelect) {
                if (!entry.getValue().equals(entryValue.getKey())) {
                    defaultSelect++;
                } else {
                    foundDefaultSelect = true;
                }
            }
        }

        if (!foundDefaultSelect) {
            defaultSelect = RadioGroupLayoutVertical.NO_DEFAULT_SELECTION;
        }

        // Force the match the parent
        RadioGroupLayout radioGroup = (RadioGroupLayout) View.inflate(this.context, R.layout.form_radiolayout, null);
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        radioGroup.setLayoutParams(params);

        this.dataControl = radioGroup;
        this.dataControl.setId(parent.getNextId());
        dataContainer.addView(this.dataControl);

        // Create mandatory field
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int formPadding = context.getResources().getDimensionPixelOffset(R.dimen.form_check_padding);
        params.leftMargin = formPadding;
        params.rightMargin = formPadding;
        //#RTl
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_ffa200));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);
        dataContainer.addView(this.mandatoryControl);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DynamicFormItem.this.mandatoryControl.setVisibility(View.GONE);
            }
        });
        radioGroup.setItems(new ArrayList<>(this.entry.getDataSet().values()), defaultSelect);

        ((ViewGroup) this.control).addView(dataContainer);
    }

    /**
     * Generates a related RadioGroup that applies a new validation for respective related form field.
     * TODO: This is a temporary solution
     */
    private void createRelatedRadioGroup() {
        Log.i(TAG, "CREATE RELATED RADIO GROUP");
        RadioGroupLayout radioGroup = (RadioGroupLayout) View.inflate(this.context, R.layout.form_radiolayout, null);
        radioGroup.setItems(this.entry.getRelatedFieldOptions(), RadioGroupLayoutVertical.NO_DEFAULT_SELECTION);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    View view = group.getChildAt(i);
                    if (view.getId() == checkedId) {
                        DynamicFormItem related = parent.getItemByKey(entry.getRelatedFieldKey());
                        if (related != null) {
                            // TODO: UPDATE THIS FORM FIELD (TEMPORARY SOLUTION)
                            related.dataControl.setTag(R.id.regex, entry.getRelatedFieldOptions().get(i).getRules().regex);
                            //Log.i(TAG, "RADIO RELATED: " + related.entry.getKey() + " new regex " + related.entry.getValidation().regex);
                        }
                        break;
                    }
                }
            }
        });
        // Set selected as default
        radioGroup.setSelection(entry.getPreSelectedRelatedOptionPosition());
        // Add group
        this.dataControl = radioGroup;
        this.dataControl.setId(parent.getNextId());
        ((ViewGroup) this.control).addView(dataControl);
    }

    /**
     * Generates a Vertical RadioGroup
     *
     * @param MANDATORYSIGNALSIZE - tells if is optional or mandatory
     * @param params
     * @param dataContainer
     */
    private void createRadioGroupVertical(final int MANDATORYSIGNALSIZE, RelativeLayout.LayoutParams params, RelativeLayout dataContainer) {

        RadioGroupLayoutVertical radioGroup = (RadioGroupLayoutVertical) View.inflate(this.context, R.layout.form_radiolistlayout, null);
        //Preselection
        int defaultSelect = 0;
        boolean foundDefaultSelect = false;
        HashMap<String, Form> formsMap = new HashMap<>();
        for (Entry<String, String> entryValue : this.entry.getDataSet().entrySet()) {
            String key = entryValue.getValue();
            // Verify if current value is to preselect
            if (!foundDefaultSelect) {
                if (!entry.getValue().equals(entryValue.getKey())) {
                    defaultSelect++;
                } else {
                    foundDefaultSelect = true;
                }
            }
            if (this.parent.getForm().fields != null && this.parent.getForm().fields.size() > 0) {
                HashMap<String, Form> paymentMethodsField = this.parent.getForm().fields.get(0).getPaymentMethodsField();
                if (paymentMethodsField != null) {
                    Log.i(TAG, "code1subForms : " + key + " --> " + paymentMethodsField.toString());
                    if (paymentMethodsField.containsKey(key) && (paymentMethodsField.get(key).fields.size() > 0 || paymentMethodsField.get(key).subForms.size() > 0)) {
                        formsMap.put(key, paymentMethodsField.get(key));
                    }
                }
            }
        }

        //If not found
        if (!foundDefaultSelect) {
            defaultSelect = RadioGroupLayoutVertical.NO_DEFAULT_SELECTION;
        }

        this.dataControl = radioGroup;
        this.dataControl.setId(parent.getNextId());
        this.dataControl.setLayoutParams(params);
        dataContainer.addView(this.dataControl);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_ffa200));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);

        // in order to position the mandatory signal on the payment method screen in the requested position, we don't inflate the dynamic form mandatory sign,
        // we use a hardcode mandatory signal since the  payment method is always a mandatory section        
        if (!this.getKey().equalsIgnoreCase("payment_method"))
            dataContainer.addView(this.mandatoryControl);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (i != checkedId) {
                        if (group.getChildAt(i).findViewById(R.id.extras) != null) {
                            group.getChildAt(i).findViewById(R.id.extras).setVisibility(View.GONE);
                            ((RadioButton) group.getChildAt(i).findViewById(R.id.radio_container).findViewById(i)).setChecked(false);
                        }
                    } else {
                        if (group.getChildAt(i).findViewById(R.id.extras) != null) {
                            group.getChildAt(i).findViewById(R.id.extras).setVisibility(View.VISIBLE);
                            ((RadioButton) group.getChildAt(i).findViewById(R.id.radio_container).findViewById(i)).setChecked(true);
                        }
                    }
                }
                DynamicFormItem.this.mandatoryControl.setVisibility(View.GONE);
            }
        });
        radioGroup.setItems(new ArrayList<>(this.entry.getDataSet().values()), formsMap, defaultSelect);

        ((ViewGroup) this.control).addView(dataContainer);
    }


    /**
     * Function responsible for constructing the ratings form layout
     *
     * @param params
     * @param controlWidth
     */
    private void buildRatingOptionsTerms(RelativeLayout.LayoutParams params, int controlWidth) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(param);

        Iterator it = this.entry.getDateSetRating().entrySet().iterator();
        int count = 1;
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();

            RelativeLayout ratingLine = (RelativeLayout) View.inflate(this.context, R.layout.rating_bar_component, null);
            ratingLine.setId(count);
            count++;
            TextView label = (TextView) ratingLine.findViewById(R.id.option_label);

            RatingBar starts = (RatingBar) ratingLine.findViewById(R.id.option_stars);
            starts.setTag(pairs.getKey().toString());
            label.setText("" + pairs.getValue());
            linearLayout.addView(ratingLine);
        }

        int id = count - 1;
        //add error message to ratings form
        this.errorText = context.getString(R.string.rating_option_error_message);
        if (hasRules()) {
            this.errorControl = createErrorControl(id, controlWidth);

            //#RTL
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.errorControl.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }

            linearLayout.addView(this.errorControl);
        }
        /**
         * IMPORTANT
         * this verification is made in order to not show the checkbox for changing form rating to review form,
         * because that option is only available on the  write product review screen and not on write seller review screen.
         */

        if (!JumiaApplication.INSTANCE.getIsSellerReview() && getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) && getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)) {
            addCustomRatingCheckbox(linearLayout, params, controlWidth);
        }
        this.dataControl = linearLayout;

        ((ViewGroup) this.control).addView(this.dataControl);

    }

    private SharedPreferences getSharedPref() {
        if (mSharedPrefs == null) {
            //Validate if country configs allows rating and review, only show write review fragment if both are allowed
            mSharedPrefs = JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        }
        return mSharedPrefs;
    }

    /**
     * function that adds a checkbox to the rating layout, checkbox that control the swithcing of forms
     *
     * @param linearLayout
     * @param params
     * @param controlWidth
     */
    private void addCustomRatingCheckbox(LinearLayout linearLayout, RelativeLayout.LayoutParams params, int controlWidth) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;


        if (context.getResources().getBoolean(R.bool.is_bamilo_specific) && currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        } else {
            params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }

        CheckBox checkWriteFull = (CheckBox) View.inflate(this.context, R.layout.form_checkbox, null);

        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.height = context.getResources().getDimensionPixelOffset(R.dimen.checkbox_rating_height);

        //#RTL
        if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            checkWriteFull.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT | Gravity.START);
        }

        checkWriteFull.setLayoutParams(params);
        checkWriteFull.setContentDescription(this.entry.getKey());
        checkWriteFull.setFocusable(false);
        checkWriteFull.setFocusableInTouchMode(false);

        checkWriteFull.setText(context.getString(R.string.write_full_review));

        linearLayout.addView(checkWriteFull);
    }

    @SuppressLint("SimpleDateFormat")
    public void addSubFormFieldValues(ContentValues model) {
        if (this.entry.getInputType() == InputType.metadate) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = this.getValue();
            Date date;
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e) {
                // if the date has the wrong format
                // there cant be more done
                Log.d(TAG, "setDate: cant parse date: " + dateString);
                return;
            }
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            Map<String, IFormField> subFields = this.entry.getSubFormFields();
            model.put(subFields.get("year").getName(), String.valueOf(year));
            model.put(subFields.get("month").getName(), String.valueOf(month));
            model.put(subFields.get("day").getName(), String.valueOf(day));
        }
    }

    private View createErrorControl(int dataControlId, int controlWidth) {
        ViewGroup errorControl;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, dataControlId);

//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        errorControl = new RelativeLayout(this.context);
        errorControl.setId(parent.getNextId());
        errorControl.setLayoutParams(params);
        errorControl.setVisibility(View.GONE);


        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        //#RTL
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {

            params.addRule(RelativeLayout.RIGHT_OF, dataControlId);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 0, (int) context.getResources().getDimension(R.dimen.form_errormessage_margin), 0);

        } else {
            params.addRule(RelativeLayout.LEFT_OF, dataControlId);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(
                    (int) context.getResources().getDimension(R.dimen.form_errormessage_margin), 0, 0,
                    0);
        }


        ImageView errImage = new ImageView(this.context);
        errImage.setId(parent.getNextId());
        errImage.setLayoutParams(params);
        errImage.setImageResource(R.drawable.indicator_input_error);

        //ErrorText params
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        //#RTL
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {
            params.addRule(RelativeLayout.LEFT_OF, errImage.getId());
            params.setMargins(0, 0, 5, 0);
        } else {
            params.addRule(RelativeLayout.RIGHT_OF, errImage.getId());
            params.setMargins(5, 0, 0, 0);
        }


        this.errorTextControl = new TextView(this.context);
        this.errorTextControl.setId(parent.getNextId());
        this.errorTextControl.setText(this.errorText);
        this.errorTextControl.setLayoutParams(params);
        this.errorTextControl.setTextColor(errorColor);
        this.errorTextControl.setTextSize(ERRORTEXTSIZE);

        //#RTL
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {
            this.errorTextControl.setSingleLine(true);
            this.errorTextControl.setEllipsize(TruncateAt.END);
        }

        errorControl.addView(this.errorTextControl);
        errorControl.addView(errImage);

        return errorControl;

    }

    private View createTextDataControl() {

        EditText textDataControl = (EditText) View.inflate(this.context, R.layout.form_edittext, null);

        if (null != this.entry.getLabel() && this.entry.getLabel().trim().length() > 0) {
            textDataControl.setHint(this.entry.getLabel());
        }
        if (null != this.entry.getValidation() && this.entry.getValidation().max > 0) {
            textDataControl.setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.entry.getValidation().max)});
        }

        //#RTL
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        // specifics
        if (this.entry.getInputType() == InputType.number) {
            int inputType = android.text.InputType.TYPE_CLASS_NUMBER;
            if (this.entry.getKey().contains(RestConstants.JSON_PHONE_TAG)) {
                inputType = android.text.InputType.TYPE_CLASS_PHONE;
            }
            textDataControl.setInputType(inputType);
        } else if (this.entry.getInputType() == InputType.password) {
            int inputType = android.text.InputType.TYPE_CLASS_TEXT
                    | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
            textDataControl.setInputType(inputType);
            //#RTL
            if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {
                textDataControl.setGravity(Gravity.RIGHT);
                if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textDataControl.setGravity(Gravity.END);
                }
            }


            textDataControl.setTextAppearance(context, R.style.form_edittext_style);
        } else if (this.entry.getInputType() == InputType.email) {
            int inputType = android.text.InputType.TYPE_CLASS_TEXT
                    | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    | android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            textDataControl.setInputType(inputType);
            //#RTL
            if (context.getResources().getBoolean(R.bool.is_bamilo_specific))
                textDataControl.setGravity(Gravity.RIGHT);

            textDataControl.setTextAppearance(context, R.style.form_edittext_style);
        } else {
            int inputType = android.text.InputType.TYPE_CLASS_TEXT;
            textDataControl.setInputType(inputType);
            //#RTL
            if (context.getResources().getBoolean(R.bool.is_bamilo_specific))
                textDataControl.setGravity(Gravity.RIGHT);

            textDataControl.setTextAppearance(context, R.style.form_edittext_style);
        }

        HoloFontLoader.apply(textDataControl, HoloFontLoader.ROBOTO_REGULAR);

        return textDataControl;
    }

    private RelativeLayout createTextDataContainer(int controlWidth) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.dataControl = createTextDataControl();
        this.dataControl.setId(parent.getNextId());
        this.dataControl.setLayoutParams(params);
        int formPadding = context.getResources().getDimensionPixelSize(R.dimen.form_padding);
        this.dataControl.setPadding(formPadding, 0, formPadding, 0);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;
        //#RTL
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {
            if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.setMarginEnd(MANDATORYSIGNALMARGIN);
            } else {
                //<4.2
                params.leftMargin = MANDATORYSIGNALMARGIN;
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
        }

        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_ffa200));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);

        dataContainer.addView(this.dataControl);
        dataContainer.addView(this.mandatoryControl);
        //#RTL
        if (context.getResources().getBoolean(R.bool.is_bamilo_specific)) {
            if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                dataContainer.setLayoutDirection(LayoutDirection.RTL);
            }
        }

        if (isDatePart()) {
            dataContainer.setPadding(0, 0, 10, 0);
        }

        return dataContainer;
    }

    /**
     * @return the childDynamicForm
     */
    public ArrayList<DynamicForm> getChildDynamicForm() {
        if (childDynamicForm == null) {
            childDynamicForm = new ArrayList<>();
        }
        return childDynamicForm;
    }

    /**
     * @param childDynamicForm the childDynamicForm to set
     */
    public void setChildDynamicForm(ArrayList<DynamicForm> childDynamicForm) {
        this.childDynamicForm = childDynamicForm;
    }

    /**
     * Determines if this field is a part of a date
     *
     * @return True, if the field is a part of a date; False, otherwise
     */
    public boolean isDatePart() {
        boolean result = (this.entry.getKey().contains("day") && !this.entry.getKey().contains("birthday"));
        result |= this.entry.getKey().contains("month");
        result |= this.entry.getKey().contains("year");
        return result;
    }

    /**
     * Determines if this field is a <code>meta</code> field
     *
     * @return True, if the type of the field is <code>meta<code>
     */
    public boolean isMeta() {
        return InputType.meta.equals(this.entry.getInputType());
    }

    /**
     * Determines if this field doesn't have a <code>type</code>
     *
     * @return True, if the type of the field is <code>null</code>
     */
    public boolean hasNoType() {
        return this.entry.getInputType() == null;
    }

    /**
     * Check if item has rules.
     *
     * @return True if item has at least one rule. False otherwise.
     */
    public boolean hasRules() {
        boolean hasRules = false;
        FieldValidation validation = entry.getValidation();
        if (validation != null) {
            hasRules = validation.isRequired();
            hasRules |= !validation.regex.equals(FieldValidation.DEFAULT_REGEX);
            hasRules |= validation.min != FieldValidation.MIN_CHARACTERS;
        }
        return hasRules;
    }

}