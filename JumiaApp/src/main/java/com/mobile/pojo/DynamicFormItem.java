package com.mobile.pojo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
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
import com.mobile.components.absspinner.PromptSpinnerAdapter;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.FormConstants;
import com.mobile.helpers.address.PhonePrefixesHelper;
import com.mobile.helpers.order.GetReturnReasonsHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.forms.FieldValidation;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.FormField;
import com.mobile.newFramework.forms.FormInputType;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.forms.NewsletterOption;
import com.mobile.newFramework.forms.PaymentInfo;
import com.mobile.newFramework.objects.addresses.FormListItem;
import com.mobile.newFramework.objects.addresses.PhonePrefix;
import com.mobile.newFramework.objects.addresses.PhonePrefixes;
import com.mobile.newFramework.objects.addresses.ReturnReason;
import com.mobile.newFramework.objects.addresses.ReturnReasons;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.rest.errors.ErrorCode;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.ShopSelector;
import com.mobile.pojo.fields.CheckBoxField;
import com.mobile.pojo.fields.ListNumberField;
import com.mobile.pojo.fields.RadioExpandableField;
import com.mobile.pojo.fields.ScreenRadioField;
import com.mobile.pojo.fields.ScreenTitleField;
import com.mobile.pojo.fields.SectionTitleField;
import com.mobile.pojo.fields.SwitchRadioField;
import com.mobile.utils.RadioGroupExpandable;
import com.mobile.utils.RadioGroupLayout;
import com.mobile.utils.RadioGroupLayoutVertical;
import com.mobile.utils.Toast;
import com.mobile.utils.datepicker.DatePickerDialog;
import com.mobile.utils.ui.KeyboardUtils;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;
import com.mobile.view.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public final static String RELATED_RADIO_GROUP_TAG = "related_radio_group";
    public final static String RELATED_LIST_GROUP_TAG = "related_list_group";
    public final static String RATING_BAR_TAG = "rating_bar";
    public final static String BIRTHDATE_TAG = "birthday_tag";
    public final static String RELATED_GROUP_SEPARATOR = "::";
    private final static String TAG = DynamicFormItem.class.getSimpleName();
    public static final String ICON_PREFIX = "ic_form_";
    protected final static int ERRORTEXTSIZE = 14;
    protected final static int MANDATORYSIGNALSIZE = 18;
    protected final static int MANDATORYSIGNALMARGIN = 15;
    private final static int TWO_OPTIONS = 2;
    protected static String DATE_FORMAT = "dd-MM-yyyy";
    protected final Context context;
    protected final DynamicForm parent;
    protected final int errorColor;
    protected final boolean hideAsterisks;
    protected int mPreSelectedPosition = IntConstants.INVALID_POSITION;
    protected IFormField entry = null;
    protected View errorControl;
    protected View dataControl;
    protected ViewGroup control;
    protected TextView errorTextControl;
    protected TextView mandatoryControl;
    protected String errorText;
    protected IcsAdapterView.OnItemSelectedListener spinnerSelectedListener;
    protected DatePickerDialog dialogDate;
    protected SharedPreferences mSharedPrefs;


    /**
     * Dynamic form item factory pattern.
     */
    public static DynamicFormItem newInstance(DynamicForm parent, Context context, IFormField entry) {
        switch (entry.getInputType()) {
            case screenTitle:
                return new ScreenTitleField(parent, context, entry);
            case sectionTitle:
                return new SectionTitleField(parent, context, entry);
            case switchRadio:
                return new SwitchRadioField(parent, context, entry);
            case screenRadio:
                return new ScreenRadioField(parent, context, entry);
            case checkBox:
                return new CheckBoxField(parent, context, entry);
            case listNumber:
                return new ListNumberField(parent, context, entry);
            case radioExpandable:
                return new RadioExpandableField(parent, context, entry);
            default:
                return new DynamicFormItem(parent, context, entry);
        }
    }

    /**
     * The constructor for the DynamicFormItem
     *
     * @param parent  The parent of this control ( an instance of a DynamicForm )
     * @param context The context where this control is to be inserted. If the FormItem type date should
     *                be used, an activity needs to be given, as the date type wants to open a dialog.
     * @param entry   The entry that corresponds to this control on the form return by the framework
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
        this.errorColor = ContextCompat.getColor(context, R.color.red_basic);
        this.hideAsterisks = parent.getForm().isToHideAsterisks();
        // Build TODO: Remove validation when all items are IDynamicFormItemField.
        if (this instanceof IDynamicFormItemField) {
            build();
        } else {
            buildControl();
        }
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
    public ViewGroup getControl() {
        return control;
    }

    /**
     * Gets the editable control. This is the part of the control that handles the user input
     *
     * @return The View that represents the control
     */
    public View getDataControl() {
        return dataControl;
    }

    public void setDataControl(@NonNull View view) {
        this.dataControl = view;
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
    public FormInputType getType() {
        return this.entry.getInputType();
    }

    /**
     * Creates the control with all the validation settings and mandatory representations.
     */
    private void build() {
        if (null != this.entry) {
            // Build base
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            this.control = new RelativeLayout(this.context);
            this.control.setLayoutParams(params);
            this.control.setId(parent.getNextId());
            ((IDynamicFormItemField) this).build(params);
        }
    }

    /**
     * Creates the control with all the validation settings and mandatory representations.
     * @deprecated Use the form item as IDynamicFormItemField and use {@link #build()}
     * There is ticket to improve all form fields, NAFAMZ-16058.
     */
    @Deprecated
    private void buildControl() {
        // Validate entry
        if (null != this.entry) {
            // Build base
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            this.control = new RelativeLayout(this.context);
            this.control.setLayoutParams(params);
            this.control.setId(parent.getNextId());
            //
            switch (this.entry.getInputType()) {
                case checkBoxLink:
                    buildCheckBoxForTerms(params, RelativeLayout.LayoutParams.MATCH_PARENT);
                    break;
                case radioGroup:
                    buildRadioGroup(params, RelativeLayout.LayoutParams.MATCH_PARENT);
                    break;
                case list:
                    buildList(params, RelativeLayout.LayoutParams.MATCH_PARENT, this.entry.getFormType() == FormConstants.NEWSLETTER_FORM);
                    break;
                case metadata:
                case date:
                    buildDate(RelativeLayout.LayoutParams.MATCH_PARENT);
                    break;
                case number:
                case email:
                case text:
                case password:
                    buildEditableTextField(this.control);
                    break;
                case relatedNumber:
                    buildRelatedNumber();
                    break;
                case hide:
                    buildHide();
                    break;
                case rating:
                    buildRatingOptionsTerms(RelativeLayout.LayoutParams.MATCH_PARENT);
                    break;
                case infoMessage:
                case errorMessage:
                    buildMessage(params);
                    break;
                default:
                    Print.w(TAG, "buildControl: Field type not supported (" + this.entry.getInputType() + ") - " + this.entry.getInputType());
                    break;
            }
        }
    }

    /**
     * Create a related number composed by a text and radio group.
     */
    private void buildRelatedNumber() {
        // Validate related field
        FormInputType type = entry.getRelatedField().getInputType();
        // Case radio group
        if(type == FormInputType.radioGroup) buildRelatedNumberWithRadioGroup();
        // Case list
        else if(type == FormInputType.list) buildRelatedNumberWithListGroup();
        // Case unknown
        else Print.w(TAG, "WARNING: UNKNOWN RELATED NUMBER");
    }

    private void buildRelatedNumberWithListGroup() {
        // Create text field
        View group = buildEditableTextField(this.control);
        // Create list with api call
        buildRelatedListGroup(group, this.entry.getRelatedField());
    }


    private void buildRelatedListGroup(View container, IFormField entry) {
        // Get spinner
        int viewId = entry.isPrefixField() ? R.id.text_field_spinner_prefix : R.id.text_field_spinner_suffix;
        final IcsSpinner spinner = (IcsSpinner) container.findViewById(viewId);
        // value that comes on the form from the server
        final String valueFromServer = entry.getValue();
        spinner.setVisibility(View.VISIBLE);
        spinner.setTag(RELATED_LIST_GROUP_TAG);
        // Get api call
        String url = entry.getApiCall();
        // Validate url
        if (!TextUtils.isEmpty(url)) {
            // Get prefixes
            JumiaApplication.INSTANCE.sendRequest(new PhonePrefixesHelper(), PhonePrefixesHelper.createBundle(url), new IResponseCallback() {
                @Override
                public void onRequestComplete(BaseResponse baseResponse) {
                    PhonePrefixes prefixes = (PhonePrefixes) baseResponse.getContentData();
                    ArrayAdapter<PhonePrefix> adapter = new ArrayAdapter<>(context, R.layout.form_spinner_item, prefixes);
                    adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    int serverPosition = prefixes.getPositionFromValue(valueFromServer);
                    // mPreSelectedPosition holds the position of the value when the user rotates screen
                    if(mPreSelectedPosition != IntConstants.INVALID_POSITION){
                        spinner.setSelection(mPreSelectedPosition);
                    } else {
                        spinner.setSelection(serverPosition != IntConstants.INVALID_POSITION ? serverPosition : prefixes.getDefaultPosition());
                    }
                }
                @Override
                public void onRequestError(BaseResponse baseResponse) {
                    parent.onRequestError(baseResponse);
                }
            });
            // Set touch listener
            spinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    KeyboardUtils.hide(view);
                    return false;
                }
            });
        }
    }

    private void buildRelatedNumberWithRadioGroup() {
        // Create container
        LinearLayout container = new LinearLayout(this.context);
        container.setOrientation(LinearLayout.VERTICAL);
        this.control.addView(container);
        // Create text field
        buildEditableTextField(container);
        // Create radio group
        buildRelatedRadioGroup(container, entry.getRelatedField());
    }

    /**
     * Create an horizontal radio group
     */
    private void buildRelatedRadioGroup(ViewGroup container, final IFormField entry) {
        // Create radio group
        ViewGroup group = (ViewGroup) View.inflate(this.context, R.layout.form_radiolayout, null);
        RadioGroupLayout radioGroup = (RadioGroupLayout) group.findViewById(R.id.radio_group_container);
        radioGroup.setTag(RELATED_RADIO_GROUP_TAG);
        // Set check listener
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                IFormField parent = entry.getParentField();
                FieldValidation validation = entry.getOptions().get(checkedId).getValidation();
                if (parent != null && validation != null) {
                    validation.isRequired = parent.getValidation().isRequired;
                    parent.setValidation(validation);
                    Print.i(TAG, "RELATED FIELD APPLY VALIDATION: " + validation.regex);
                }
            }
        });
        // Set options
        radioGroup.setItems(new ArrayList<>(entry.getOptions()), 0, null);
        // Set selection that comes from server
        for (int i = 0; i < entry.getOptions().size(); i++) {
            if(entry.getOptions().get(i).isChecked()){
                radioGroup.setSelection(i);
            }
        }
        // Add group to container
        container.addView(group);
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
    public void loadState(@NonNull Bundle inStat) {
        switch (this.entry.getInputType()) {
            case checkBoxLink:
                boolean checkedList = inStat.getBoolean(getKey());
                ((CheckBox) this.dataControl.findViewWithTag("checkbox")).setChecked(checkedList);
                break;
            case list:
                mPreSelectedPosition = inStat.getInt(getKey());
                IcsSpinner spinner = (IcsSpinner) this.dataControl;
                if(spinner.getAdapter() != null && spinner.getAdapter().getCount() > mPreSelectedPosition){
                    spinner.setSelection(mPreSelectedPosition);
                }
                break;
            case radioGroup:
                int position = inStat.getInt(getKey());
                if (this.dataControl instanceof IcsSpinner) {
                    ((IcsSpinner) this.dataControl).setSelection(position);
                } else if (this.dataControl instanceof RadioGroupLayoutVertical) {
                    ((RadioGroupLayoutVertical) this.dataControl).setPaymentSelection(position);
                    ((RadioGroupLayoutVertical) this.dataControl).loadSubFieldState(inStat);
                } else {
                    ((RadioGroupLayout) this.dataControl.findViewById(R.id.radio_group_container)).setSelection(position);
                }
                break;
            case metadata:
            case date:
                String date = inStat.getString(getKey());
                ((TextView) this.dataControl.findViewById(R.id.form_button)).setText(date);
                setDialogDate(date);
                break;
            case email:
            case text:
            case password:
            case number:
                String text = inStat.getString(getKey());
                ((EditText) this.dataControl).setText(text);
                break;
            case relatedNumber:
                String relatedValue = inStat.getString(getKey());
                String[] values = !TextUtils.isEmpty(relatedValue) ? relatedValue.split(RELATED_GROUP_SEPARATOR) : new String[]{};
                // eg. 915436837::5
                if(values.length == 2) {
                    String main = values[0];
                    int related = Integer.parseInt(values[1]);
                    // set main text
                    ((EditText) this.dataControl).setText(main);
                    // Validate related field
                    if (this.entry.getRelatedField() != null && this.entry.getRelatedField().getInputType() != null) {
                        // Get related field
                        FormInputType type = entry.getRelatedField().getInputType();
                        // Case radio group
                        if (type == FormInputType.radioGroup) {
                            RadioGroupLayout radioGroup = (RadioGroupLayout) control.findViewWithTag(RELATED_RADIO_GROUP_TAG);
                            radioGroup.setSelection(related);
                            Print.i("VALUE", "RADIO GROUP SELECTED POS:" + related);
                        }
                        // Case list
                        else if (type == FormInputType.list) {
                            mPreSelectedPosition = related;
                            Print.i("VALUE", "LIST GROUP SELECTED POS:" + related);
                        }
                    }
                }
                break;
            case rating:
                if (CollectionUtils.isNotEmpty(getEntry().getDateSetRating())) {
                    for (String key : getEntry().getDateSetRating().keySet()) {
                        float value = inStat.getFloat(RATING_BAR_TAG + key);
                        ((RatingBar) this.dataControl.findViewWithTag(RATING_BAR_TAG + key)).setRating(value);
                    }
                }
                break;
            case hide:
                break;

            default:
                break;
        }
    }

    /**
     * Saves form item values to submit the form.
     */
    public void save(ContentValues values) {
        switch (this.entry.getInputType()) {
            case metadata:
                addSubFormFieldValues(values);
                values.put(getName(), getValue());
                break;
            case radioGroup:
                if (this.dataControl instanceof RadioGroupLayoutVertical) {
                    ContentValues mValues = ((RadioGroupLayoutVertical) dataControl).getSubFieldParameters();
                    if (mValues != null) {
                        values.putAll(mValues);
                        values.put(RestConstants.NAME, ((RadioGroupLayoutVertical) dataControl).getSelectedFieldName());
                    }
                }
                values.put(getName(), getValue());
                break;
            case list:
                View view = this.dataControl;
                if(view instanceof IcsSpinner){
                    IcsSpinner spinner = (IcsSpinner) view;
                    if (spinner.getSelectedItem() instanceof FormListItem) {

                        FormListItem item = (FormListItem) spinner.getSelectedItem();
                        if (item != null) {
                            values.put(getName(), item.getValueAsString());
                        }
                    }
                    // Case HomeNewsletter
                    else if (com.mobile.newFramework.utils.TextUtils.isNotEmpty((String) spinner.getSelectedItem())) {
                        for (String key : this.entry.getDataSet().keySet()) {
                            if (com.mobile.newFramework.utils.TextUtils.equals(this.entry.getDataSet().get(key), (String) spinner.getSelectedItem())) {
                                values.put(getName(), key);
                                break;
                            }
                        }
                    }
                }

                break;
            case relatedNumber:
                // Get number
                String number = getValue();
                if (com.mobile.newFramework.utils.TextUtils.isNotEmpty(number)) {
                    values.put(getName(), number);
                    // Get related option
                    IFormField related = getEntry().getRelatedField();
                    // Validate related type
                    FormInputType relatedType = related.getInputType();
                    // Only send the related info if the main is filled
                    if (relatedType == FormInputType.radioGroup) {
                        RadioGroupLayout group = (RadioGroupLayout) getControl().findViewWithTag(DynamicFormItem.RELATED_RADIO_GROUP_TAG);
                        // Get selected position
                        int idx = group.getSelectedIndex();
                        // Get option value from related item
                        String value = related.getOptions().get(idx).getValue();
                        values.put(related.getName(), value);
                    } else if (relatedType == FormInputType.list) {
                        IcsSpinner icsSpinner = (IcsSpinner) getControl().findViewWithTag(DynamicFormItem.RELATED_LIST_GROUP_TAG);
                        FormListItem item = (FormListItem) icsSpinner.getSelectedItem();
                        if (item != null) {
                            values.put(related.getName(), item.getValue());
                        }
                    }
                }
                break;
            case rating:
                if (CollectionUtils.isNotEmpty(getEntry().getDateSetRating())) {
                    for (String key : getEntry().getDateSetRating().keySet()) {
                        RatingBar bar = (RatingBar) this.dataControl.findViewWithTag(RATING_BAR_TAG + key);
                        values.put(key, (int) bar.getRating());
                    }
                }
                break;

            default:
                getDefaultValue(values);
                break;
        }
    }

    /**
     * The common method to save the value from field.
     */
    protected final void getDefaultValue(ContentValues values) {
        String name = getName();
        String value = getValue();
        if (!TextUtils.isEmpty(name) && value != null) {
            values.put(name, value);
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
            case checkBoxLink:
                result = ((CheckBox) this.dataControl.findViewWithTag("checkbox")).isChecked() ? "1" : "0";
                break;
            case radioGroup:
                String value;
                if (this.dataControl instanceof IcsSpinner) {
                    value = ((IcsSpinner) this.dataControl).getSelectedItem().toString();
                } else if (this.dataControl instanceof RadioGroupLayoutVertical) {
                    value = ((RadioGroupLayoutVertical) this.dataControl).getSelectedFieldValue();
                } else {
                    value = ((RadioGroupLayout) this.dataControl.findViewById(R.id.radio_group_container)).getSelectedFieldValue();
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
            case metadata:
            case date:
                // Case selected a date
                if(this.dataControl != null && this.dataControl.findViewWithTag(BIRTHDATE_TAG) != null){
                    String dateValue = ((TextView) this.dataControl.findViewWithTag(BIRTHDATE_TAG)).getText().toString();
                    if(!TextUtils.isEmpty(dateValue)){
                        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT,Locale.ENGLISH);
                        try {
                            Date date = dateFormat.parse(dateValue);
                            result = dateFormat.format(date.getTime());
                        } catch (IllegalArgumentException | ParseException | NullPointerException e ) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case hide:
                result = this.entry.getValue();
                break;
            case email:
            case text:
            case password:
            case relatedNumber:
            case number:
                result = ((EditText) this.dataControl).getText().toString();
                break;
            case rating:
                break;
            case list:
                result = (String) ((IcsSpinner) this.dataControl).getSelectedItem();
                break;
            default:
                result = "";
                break;
        }
        return result;
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
                    if (position == -1) {
                        selection = IcsSpinner.INVALID_POSITION;
                    } else {
                        selection = position;
                    }

                    ((IcsSpinner) this.dataControl).setSelection(selection);
                } else {
                    int selection;
                    if (position == -1) {
                        selection = RadioGroupLayout.NO_DEFAULT_SELECTION;
                    } else {
                        selection = position;
                    }
                    if (this.dataControl instanceof RadioGroupLayoutVertical) {
                        ((RadioGroupLayoutVertical) this.dataControl).setSelection(selection);
                    } else {
                        ((RadioGroupLayout) this.dataControl.findViewById(R.id.radio_group_container)).setSelection(selection);
                    }
                }
                break;
            case metadata:
            case date:
                String date = (String) value;
                setDialogDate(date);
                break;
            case email:
            case text:
            case password:
            case relatedNumber:
            case number:
                String text = null == value ? "" : (String) value;
                ((EditText) this.dataControl).setText(text);
                this.errorControl.setVisibility(View.GONE);
                this.dataControl.setContentDescription(this.entry.getId());
                if (text.length() == 0) {
                    this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() && !hideAsterisks ? View.VISIBLE : View.GONE);
                } else {
                    this.mandatoryControl.setVisibility(View.GONE);
                } //reset checkbox control state
                CheckBox checkBox = (CheckBox) this.control.findViewById(R.id.text_field_password_check_box);
                if(checkBox != null) checkBox.setChecked(false);
                break;
            case hide:
                break;
            case rating:
                break;
            default:
                break;
        }
    }

    /**
     * Saves the state of the current field to a bundle object
     *
     * @param outState The Bundle object that will hold the state of the object
     */
    public void saveState(@NonNull Bundle outState) {
        switch (this.entry.getInputType()) {
            case checkBoxLink:
                outState.putBoolean(getKey(), ((CheckBox) this.dataControl.findViewWithTag("checkbox")).isChecked());
                break;
            case list:
                if (this.dataControl instanceof IcsSpinner) {
                    IcsSpinner spinner = (IcsSpinner) this.dataControl;
                    if (!(spinner.getAdapter() instanceof PromptSpinnerAdapter) || spinner.getSelectedItemPosition() > IntConstants.DEFAULT_POSITION) {
                        outState.putInt(getKey(), spinner.getSelectedItemPosition());
                    }
                }
                break;
            case radioGroup:
                if (this.dataControl instanceof RadioGroupLayoutVertical) {
                    ((RadioGroupLayoutVertical) this.dataControl).saveSubFieldState(outState);
                    outState.putInt(getKey(), ((RadioGroupLayoutVertical) this.dataControl).getSelectedIndex());
                } else if (this.dataControl instanceof IcsSpinner) {
                    outState.putInt(getKey(), ((IcsSpinner) this.dataControl).getSelectedItemPosition());
                } else {
                    outState.putInt(getKey(), ((RadioGroupLayout) this.dataControl.findViewById(R.id.radio_group_container)).getSelectedIndex());
                }
                break;
            case metadata:
            case date:
                if (this.dataControl.findViewById(R.id.form_button) instanceof TextView) {
                    outState.putString(getKey(), ((TextView) this.dataControl.findViewById(R.id.form_button)).getText().toString());
                }
                break;
            case hide:
                break;
            case email:
            case text:
            case password:
            case number:
                outState.putString(getKey(), ((EditText) this.dataControl).getText().toString());
                break;
            case relatedNumber:
                //main field
                String value = ((EditText) this.dataControl).getText().toString();
                //related field
                if (this.entry.getRelatedField() != null && this.entry.getRelatedField().getInputType() != null) {
                    FormInputType type = entry.getRelatedField().getInputType();
                    // Case radio
                    if (type == FormInputType.radioGroup) {
                        RadioGroupLayout radioGroup = (RadioGroupLayout) control.findViewWithTag(RELATED_RADIO_GROUP_TAG);
                        int selected = radioGroup.getSelectedIndex();
                        Print.i("VALUE", "SELECTED POS:" + selected);
                        value += RELATED_GROUP_SEPARATOR + selected;
                    }
                    // Case list
                    else if (type == FormInputType.list) {
                        final IcsSpinner spinner = (IcsSpinner) control.findViewWithTag(RELATED_LIST_GROUP_TAG);
                        int selected = spinner.getSelectedItemPosition();
                        Print.i("VALUE", "SELECTED POS:" + selected);
                        value += RELATED_GROUP_SEPARATOR + selected;
                    }
                    outState.putString(getKey(), value);
                }
                break;
            case rating:
                if (CollectionUtils.isNotEmpty(getEntry().getDateSetRating())) {
                    for (String key : getEntry().getDateSetRating().keySet()) {
                        RatingBar bar = (RatingBar) this.dataControl.findViewWithTag(RATING_BAR_TAG + key);
                        outState.putFloat(RATING_BAR_TAG + key, bar.getRating());
                    }
                }
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

            // Use the new approach IDynamicFormItemField
            if(this instanceof IDynamicFormItemField) {
                result = ((IDynamicFormItemField) this).validate(true);
            }

            // Deprecated: Old approach
            switch (this.entry.getInputType()) {
                case checkBoxLink:
                    if (this.entry.getValidation().isRequired())
                        result = ((CheckBox) this.dataControl.findViewWithTag("checkbox")).isChecked();
                    break;
                case radioGroup:
                    boolean valid;
                    if (this.dataControl instanceof IcsSpinner) {
                        valid = ((IcsSpinner) this.dataControl).getSelectedItemPosition() != Spinner.INVALID_POSITION;
                    } else if (this.dataControl instanceof RadioGroupLayoutVertical) {
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
                        valid = ((RadioGroupLayout) this.dataControl.findViewById(R.id.radio_group_container)).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
                    }

                    if (!valid) {
                        result = !this.entry.getValidation().isRequired();
                        setErrorText(context.getString(R.string.error_ismandatory) + " " + this.entry.getLabel());
                    }
                    break;
                case metadata:
                case date:
                    if (this.entry.getValidation().isRequired()) {
                        if (TextUtils.isEmpty(((TextView) this.dataControl.findViewById(R.id.form_button)).getText().toString())) {
                            result = false;
                        }
                    }
                    break;
                case email:
                case text:
                case password:
                case relatedNumber:
                case number:
                    result = validateStringToPattern(((EditText) this.dataControl).getText().toString());
                    break;
                case hide:
                    break;
                case rating:
                    if (CollectionUtils.isNotEmpty(getEntry().getDateSetRating())) {
                        for (String key : getEntry().getDateSetRating().keySet()) {
                            RatingBar bar = (RatingBar) this.dataControl.findViewWithTag(RATING_BAR_TAG + key);
                            result &= bar.getRating() != 0.0;
                        }
                    }
                    break;
                case list:
                    result = this.dataControl instanceof IcsSpinner;
                    if (result) {
                        IcsSpinner spinner = (IcsSpinner) this.dataControl;
                        if (spinner.getAdapter() instanceof PromptSpinnerAdapter) {
                            result = spinner.getSelectedItemPosition() > IntConstants.DEFAULT_POSITION;
                        }
                    }
                    break;
                default:
                    break;
            }
            this.errorControl.setVisibility(!result ? View.VISIBLE : View.GONE);

        }

        return result;
    }

    /**
     * If Sub Forms have no message to display but parent form is not valid, show global message.
     */
    public boolean showGlobalMessage() {
        boolean result = false;
        if (hasRules()) {

            switch (this.entry.getInputType()) {
                case radioExpandable:
                    result = ((RadioGroupExpandable) this.dataControl).showGlobalMessage();
                    break;
            }
        }

        return result;
    }


    private boolean validateStringToPattern(String text) {
        boolean result;

        // SHOP: added empty space to prevent string from being cutted on burmese
        String space = "";
        if (context.getResources().getBoolean(R.bool.is_shop_specific))
            space = " ";

        // Case empty
        if (TextUtils.isEmpty(text)) {
            result = !this.entry.getValidation().isRequired();
            String errorMessage = this.entry.getValidation().getMessage();
            if(TextUtils.isEmpty(errorMessage)){
                errorMessage = context.getString(R.string.error_ismandatory) + " " + this.entry.getLabel();
            }

            setErrorText(errorMessage + space);
        }
        // Case too short
        else if (this.entry.getValidation().min > 0 && text.length() < this.entry.getValidation().min) {
            setErrorText(this.entry.getLabel() + " " + context.getResources().getString(R.string.form_texttoshort) + space);
            result = false;
        }
        // Case too long
        else if (this.entry.getValidation().max > 0 && text.length() > this.entry.getValidation().max) {
            setErrorText(this.entry.getLabel() + " " + context.getResources().getString(R.string.form_texttolong) + space);
            result = false;
        }
        // Case no match regex
        else {
            String regex = this.entry.getValidation().regex;
            Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
            String errorMessage = this.entry.getValidation().getRegexErrorMessage();
            /**
             * This is a fallback in case API don't return the error message
             * for the Regex. Will be fixed in https://jira.africainternetgroup.com/browse/NAFAMZ-16927
             */
            if(com.mobile.newFramework.utils.TextUtils.isEmpty(errorMessage)){
                errorMessage = context.getString(R.string.error_ismandatory) + " " + this.entry.getLabel();
            }
            setErrorText(errorMessage + space);
            Matcher matcher = pattern.matcher(text);
            result = matcher.find();
        }
        return result;
    }

    /**
     * Displays the controls error message to the user
     *
     * @param message The error message to be displayed on the control
     */
    public void showErrorMessage(String message) {
        if (null != errorControl) {
            setErrorText(message);
            this.errorControl.setVisibility(message.equals("") ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Hide the controls error message to the user
     */
    public void hideErrorMessage() {
        if (null != errorControl) {
            this.errorControl.setVisibility(View.GONE);
        }
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
     */
    private void buildCheckBoxForTerms(RelativeLayout.LayoutParams params, int controlWidth) {
        this.control.setLayoutParams(params);
        // data controls
        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);

        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        this.dataControl = View.inflate(this.context, R.layout.form_checkbox_terms, null);
        this.dataControl.setId(parent.getNextId());
        this.dataControl.setVisibility(View.VISIBLE);

        CheckBox mCheckBox = (CheckBox) this.dataControl.findViewById(R.id.checkbox_terms);
        mCheckBox.setTag("checkbox");
        mCheckBox.setContentDescription(this.entry.getKey());

        mCheckBox.setText(this.entry.getLabel().trim().length() > 0 ? this.entry.getLabel() : this.context.getString(R.string.register_text_terms_a) + " ");

        if (this.entry.getValue().equals("1")) {
            mCheckBox.setChecked(true);
        }

        TextView mLinkTextView = (TextView) this.dataControl.findViewById(R.id.textview_terms);
        mLinkTextView.setText(this.entry.getLinkText());
        mLinkTextView.setTag(this.entry.getLinkTarget());
        mLinkTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                parent.onClick(v);
            }
        });

        //needed: change parent layout to match_parent to be able to align this component to right
        if (ShopSelector.isRtl() && android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            LinearLayout.LayoutParams paramsAux = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            this.dataControl.setLayoutParams(paramsAux);
        }

         this.control.addView(this.dataControl);

        if (hasRules()) {
            //mandatory control
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            if (ShopSelector.isRtl()) {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.leftMargin = MANDATORYSIGNALMARGIN;
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.rightMargin = MANDATORYSIGNALMARGIN;
            }
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            // Mandatory field
            if (this.entry.getValidation().isRequired() && !hideAsterisks) {
                this.mandatoryControl = new TextView(this.context);
                this.mandatoryControl.setLayoutParams(params);
                this.mandatoryControl.setText("*");
                this.mandatoryControl.setTextColor(ContextCompat.getColor(context, R.color.orange_1));
                this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
                this.mandatoryControl.setVisibility(View.VISIBLE);
                this.control.addView(this.mandatoryControl);
                mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mandatoryControl.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    }
                });
            }
            //error control
            this.errorControl = createErrorControl(dataContainer.getId(), controlWidth);
            //#RTL
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.errorControl.setLayoutDirection(LayoutDirection.RTL);
            }
            RelativeLayout.LayoutParams errorControlParams = (RelativeLayout.LayoutParams) this.errorControl.getLayoutParams();
            errorControlParams.addRule(RelativeLayout.BELOW, this.dataControl.getId());
            this.control.addView(this.errorControl);
        }

        this.control.addView(dataContainer);
    }

    private void buildRadioGroup(RelativeLayout.LayoutParams params, int controlWidth) {
        this.control.setLayoutParams(params);

        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        //#RTL
        if (ShopSelector.isRtl()) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        } else {
            params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }

        if (this.entry.isVerticalOrientation() ||
                this.entry.getDataSet().size() > TWO_OPTIONS ||
                this.parent.getForm().getFields().get(IntConstants.DEFAULT_POSITION).getSubForms() != null) {
            Print.d("createRadioGroup", "createRadioGroup: Radio Group ORIENTATION_VERTICAL");
            createRadioGroupVertical(params, dataContainer);
        } else {
            Print.d("createRadioGroup", "createRadioGroup: Radio Group ORIENTATION_HORIZONTAL");
            createRadioGroup(dataContainer);
        }

        if (hasRules()) {
            this.errorControl = createErrorControl(dataContainer.getId(), controlWidth);
            //#RTL
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.errorControl.setLayoutDirection(LayoutDirection.RTL);
            }
            this.control.addView(this.errorControl);
        }
    }

    /**
     * Build list field.<br>
     * - The isAlternativeLayout flag is used to load the HomeNewsletter layout
     */
    private void buildList(RelativeLayout.LayoutParams params, int controlWidth, boolean isAlternativeLayout) {
        this.control.setLayoutParams(params);
        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);
        createSpinnerForRadioGroup(MANDATORYSIGNALSIZE, params, dataContainer, isAlternativeLayout);
    }


    private void buildDate(int controlWidth) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.topMargin = 6;
        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.dataControl = View.inflate(this.context, R.layout.form_button, null);
        this.dataControl.setId(parent.getNextId());
        this.dataControl.setLayoutParams(params);
        // set Date field icon
        ImageView icon = (ImageView) this.dataControl.findViewById(R.id.button_field_icon);
        // Get mandatory
        TextView mandatory = (TextView)  this.dataControl.findViewById(R.id.button_field_mandatory);

        if(this.parent.getForm().getType() == FormConstants.REGISTRATION_FORM || this.parent.getForm().getType() == FormConstants.USER_DATA_FORM) {
            UIUtils.setDrawableByString(icon, ICON_PREFIX + this.entry.getKey());
            icon.setVisibility(View.VISIBLE);
        }

        this.dataControl.setContentDescription(this.entry.getKey());

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        if(!com.mobile.newFramework.utils.TextUtils.isEmpty(this.entry.getFormat())){
            DATE_FORMAT = this.entry.getFormat();
        }
        dataContainer.addView(this.dataControl);

        Print.i("ENTERED BIRTHDAY", " HERE ");
        String text = context.getString(R.string.register_birthday);
        final TextView spinnerButton = ((TextView) this.dataControl.findViewById(R.id.form_button));
        spinnerButton.setHint(text);
        spinnerButton.setTag(BIRTHDATE_TAG);

        // Add *
        mandatory.setVisibility(this.entry.getValidation().isRequired() && !hideAsterisks ? View.VISIBLE : View.GONE);
        // Is required
        if (entry.getValidation() != null && entry.getValidation().isRequired()) {
            this.errorControl = createErrorControl(dataContainer.getId(), controlWidth);
            //#RTL
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.errorControl.setLayoutDirection(LayoutDirection.RTL);
            }
            this.control.addView(this.errorControl);
        }

        final DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
                Calendar currentCalendar = Calendar.getInstance();
                GregorianCalendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
                Print.i(TAG, "selected Date : year: " + year +" month: "+ monthOfYear +" day: "+ dayOfMonth);

                int controlValue =  cal.compareTo(currentCalendar);
                Print.i(TAG, "controlValue:" +controlValue);
                if(controlValue == 0 || controlValue == 1){
                    Toast.makeText(context, context.getString(R.string.birthday_error),Toast.LENGTH_SHORT).show();
                } else {
                    Date d = new Date(cal.getTimeInMillis());
                    String date = DateFormat.format(DATE_FORMAT, d).toString();
                    spinnerButton.setText(date);
                    if(mandatoryControl != null)
                        DynamicFormItem.this.mandatoryControl.setVisibility(View.GONE);

                    dialogDate.dismiss();
                }

            }

        };

        // Set default value
        if (!TextUtils.isEmpty(this.entry.getValue())) {
            spinnerButton.setText(this.entry.getValue());
        }
        Calendar c = Calendar.getInstance();
        this.dialogDate = DatePickerDialog.newInstance(pickerListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        this.dialogDate.setYearRange(DatePickerDialog.DEFAULT_START_YEAR, c.get(Calendar.YEAR));

        spinnerButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!dialogDate.isVisible()) {
                    Calendar c = Calendar.getInstance();
                    if(com.mobile.newFramework.utils.TextUtils.isEmpty(spinnerButton.getText().toString())){
                        dialogDate.setDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                        dialogDate.setYearRange(DatePickerDialog.DEFAULT_START_YEAR, c.get(Calendar.YEAR));
                    } else {
                        dialogDate.setYearRange(DatePickerDialog.DEFAULT_START_YEAR, c.get(Calendar.YEAR));
                        setDialogDate(spinnerButton.getText().toString());
                    }
                    dialogDate.show(((BaseActivity) context).getSupportFragmentManager(), null);
                }
            }
        });

        this.control.addView(dataContainer);
    }


    private void buildMessage(RelativeLayout.LayoutParams params) {
        this.control.setLayoutParams(params);
        this.control.setPadding(0, 10, 0, 10);
        ((RelativeLayout)this.control).setGravity(Gravity.CENTER);
        TextView textView = (TextView) View.inflate(this.context, R.layout.text_view_info, null);
        textView.setText(entry.getLabel());
        textView.setTag(entry.getInputType());
        this.control.addView(textView);
    }

    /**
     * Creates the control with hidden value
     */
    private void buildHide() {
        // Don't allow an hidden control to take visual space
        this.control.setVisibility(View.GONE);
    }


    /**
     * Spinner
     */
    private void createSpinnerRequester() {
        // Get spinner
        final IcsSpinner spinner = (IcsSpinner) this.dataControl;
        // Get api call
        String url = entry.getApiCall();
        // Validate url
        if (!TextUtils.isEmpty(url)) {
            // Get prefixes
            JumiaApplication.INSTANCE.sendRequest(new GetReturnReasonsHelper(), GetReturnReasonsHelper.createBundle(url), new IResponseCallback() {
                @Override
                public void onRequestComplete(BaseResponse baseResponse) {
                    ReturnReasons items = (ReturnReasons) baseResponse.getContentData();
                    ArrayAdapter<ReturnReason> adapter = new ArrayAdapter<>(context, R.layout.form_spinner_item, items);
                    adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
                    PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, context);
                    promptAdapter.setPrompt(entry.getLabel());
                    spinner.setAdapter(promptAdapter);
                    if(mPreSelectedPosition > IntConstants.DEFAULT_POSITION) {
                        spinner.setSelection(mPreSelectedPosition);
                    }
                    // Notify parent
                    parent.onRequestComplete(baseResponse);
                }
                @Override
                public void onRequestError(BaseResponse baseResponse) {
                    // Notify parent
                    parent.onRequestError(baseResponse);
                }
            });
            // Set touch listener
            spinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    KeyboardUtils.hide(view);
                    return false;
                }
            });
        } else {
            // Notify parent
            parent.onRequestError(new BaseResponse<>(EventType.GET_RETURN_REASONS, ErrorCode.REQUEST_ERROR));
        }
    }

    private void createSpinnerForRadioGroup(final int MANDATORYSIGNALSIZE, RelativeLayout.LayoutParams params, RelativeLayout dataContainer, boolean isAlternativeLayout) {
        this.dataControl = View.inflate(this.context, R.layout._def_gen_form_spinner, null);
        this.dataControl.setId(parent.getNextId());
        this.dataControl.setLayoutParams(params);

        // Case Filled
        if (CollectionUtils.isNotEmpty(this.entry.getDataSet())) {
            int layout = R.layout.form_spinner_item;
            // Case HomeNewsletter
            if (isAlternativeLayout) {
                layout = R.layout.form_alternative_spinner_item;
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, layout, new ArrayList<>(this.entry.getDataSet().values()));
            adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
            PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, context);
            promptAdapter.setPrompt(this.entry.getPlaceHolder());
            ((IcsSpinner) this.dataControl).setAdapter(promptAdapter);
        }
        // Case Empty
        else {
            /**
             * TODO: NAFAMZ-15492 - This should be a generic component for forms.
             */
            // Case ORDER_RETURN_REASON_FORM
            if (this.parent.getForm().getType() == FormConstants.ORDER_RETURN_REASON_FORM) {
                createSpinnerRequester();
            }
            // Case Others (CREATE ADDRESS)
            else {
                // Add a dummy item to show the prompt
                ArrayList<String> array = new ArrayList<>();
                array.add("");
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.form_spinner_item, array);
                adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
                PromptSpinnerAdapter promptAdapter = new PromptSpinnerAdapter(adapter, R.layout.form_spinner_prompt, context);
                promptAdapter.setPrompt(this.entry.getPlaceHolder());
                ((IcsSpinner) this.dataControl).setAdapter(promptAdapter);
                // Disabled spinner that has a parent (regions->cities->postal)
                this.dataControl.setEnabled(false);
            }
        }
        // Sets the spinner value
        if(TextUtils.isEmpty(this.entry.getPlaceHolder())) {
            ((IcsSpinner) this.dataControl).setSelection(IntConstants.DEFAULT_POSITION);
        }
        // Case HomeNewsletter
        if (isAlternativeLayout) {
            int position = 0;
            if (CollectionUtils.isNotEmpty(((FormField) this.entry).getNewsletterOptions())) {
                for (NewsletterOption item : ((FormField) this.entry).getNewsletterOptions()) {
                    position++;
                    if (item.isDefault) {
                        ((IcsSpinner) this.dataControl).setSelection(position);
                    }
                }
            }
        }

        this.dataControl.setVisibility(View.VISIBLE);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //#RTL
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
        }

        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.margin_padding_xxl);
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(ContextCompat.getColor(context, R.color.orange_1));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() && !hideAsterisks ? View.VISIBLE : View.GONE);
        dataContainer.addView(this.dataControl);
        dataContainer.addView(this.mandatoryControl);

        if (hasRules()) {
            this.setErrorText(this.entry.getValidation().getMessage());
            this.errorControl = createErrorControl(dataContainer.getId(), RelativeLayout.LayoutParams.MATCH_PARENT);
            //#RTL
            int currentApiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                this.errorControl.setLayoutDirection(LayoutDirection.RTL);
            }
            this.control.addView(this.errorControl);
        }


        this.control.addView(dataContainer);

        HoloFontLoader.applyDefaultFont(dataContainer);
        // Listeners
        ((IcsSpinner) this.dataControl).setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
                if (entry.getValidation().isRequired()) {
                    mandatoryControl.setVisibility(position == Spinner.INVALID_POSITION ? View.VISIBLE : View.GONE);
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
    }

    /**
     * Create an horizontal radio group
     */
    private void createRadioGroup(RelativeLayout dataContainer) {
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
            defaultSelect = IntConstants.INVALID_POSITION;
        }

        // Force the match the parent
        ViewGroup radioGroupContainer = (ViewGroup) View.inflate(this.context, R.layout.form_radiolayout, null);
        RadioGroupLayout radioGroup = (RadioGroupLayout) radioGroupContainer.findViewById(R.id.radio_group_container);

        ImageView icon = (ImageView) radioGroupContainer.findViewById(R.id.radio_field_icon);

        if(this.parent.getForm().getType() == FormConstants.REGISTRATION_FORM || this.parent.getForm().getType() == FormConstants.USER_DATA_FORM) {
            UIUtils.setDrawableByString(icon, ICON_PREFIX + this.entry.getKey());
            icon.setVisibility(View.VISIBLE);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        radioGroupContainer.setLayoutParams(params);

        this.dataControl = radioGroupContainer;
        this.dataControl.setId(parent.getNextId());
        dataContainer.addView(this.dataControl);

        // Create mandatory field
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int formPadding = context.getResources().getDimensionPixelOffset(R.dimen.form_check_padding);
        params.leftMargin = formPadding;
        params.rightMargin = formPadding;
        //#RTl
        if (ShopSelector.isRtl()) {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = MANDATORYSIGNALMARGIN;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(ContextCompat.getColor(context, R.color.orange_1));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() && !hideAsterisks ? View.VISIBLE : View.GONE);
        dataContainer.addView(this.mandatoryControl);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DynamicFormItem.this.mandatoryControl.setVisibility(View.GONE);
            }
        });

        ArrayList<String> keySet = null;
        if (this.parent.getForm().getType() == FormConstants.NEWSLETTER_FORM &&
                CollectionUtils.isNotEmpty(((FormField) this.entry).getNewsletterOptions())) {
            keySet = new ArrayList<>();
            for (NewsletterOption nOption : ((FormField) this.entry).getNewsletterOptions()) {
                keySet.add(nOption.key);
            }
        }

        radioGroup.setItems(new ArrayList<>(this.entry.getDataSet().values()), defaultSelect, keySet);

        this.control.addView(dataContainer);
    }

    /**
     * Generates a Vertical RadioGroup
     */
    private ViewGroup createRadioGroupVertical(RelativeLayout.LayoutParams params, ViewGroup dataContainer) {

        RadioGroupLayoutVertical radioGroup = (RadioGroupLayoutVertical) View.inflate(this.context, R.layout.form_radiolistlayout, null);

        // Case this forms show dividers
        if (parent.getForm().getType() == FormConstants.NEWSLETTER_PREFERENCES_FORM ||
            parent.getForm().getType() == FormConstants.NEWSLETTER_UN_SUBSCRIBE_FORM) {
            // For RTL
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            // Set dividers
            radioGroup.setDividerDrawable(ContextCompat.getDrawable(this.context, R.drawable._gen_divider_horizontal_black_400));
            radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            radioGroup.enableRightStyle();
        }

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
            if (this.parent.getForm().getFields() != null && this.parent.getForm().getFields().size() > 0) {
                HashMap<String, Form> paymentMethodsField = this.parent.getForm().getFields().get(0).getSubForms();
                if (paymentMethodsField != null) {
                    if (paymentMethodsField.containsKey(key) && (paymentMethodsField.get(key).getFields().size() > 0 || paymentMethodsField.get(key).getSubFormsMap().size() > 0)) {
                        formsMap.put(key, paymentMethodsField.get(key));
                    }
                }
            }
        }

        //If not found
        if (!foundDefaultSelect) {
            defaultSelect = IntConstants.INVALID_POSITION;
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
        this.mandatoryControl.setTextColor(ContextCompat.getColor(context, R.color.orange_1));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() && !hideAsterisks ? View.VISIBLE : View.GONE);

        // in order to position the mandatory signal on the payment method screen in the requested position, we don't inflate the dynamic form mandatory sign,
        // we use a hardcode mandatory signal since the  payment method is always a mandatory section
        if (!this.getKey().equalsIgnoreCase(RestConstants.PAYMENT_METHOD))
            dataContainer.addView(this.mandatoryControl);

        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    // Get extras
                    View view = group.getChildAt(i).findViewById(R.id.radio_extras_container);
                    if ( view != null) {
                        if (i != checkedId) {
                            view.setVisibility(View.GONE);
                            ((RadioButton) group.getChildAt(i).findViewById(R.id.radio_shipping)).setChecked(false);
                        } else {
                            view.setVisibility(View.VISIBLE);
                            ((RadioButton) group.getChildAt(i).findViewById(R.id.radio_shipping)).setChecked(true);
                        }
                    }
                }
                DynamicFormItem.this.mandatoryControl.setVisibility(View.GONE);
            }
        });

        HashMap<String, PaymentInfo> paymentInfoMap = null;
        try {
            // Get payment info from form
            paymentInfoMap = this.parent.getForm().getFieldKeyMap().get(RestConstants.PAYMENT_METHOD).getPaymentInfoList();
        } catch (NullPointerException e) {
            Print.w("WARNING: NPE ON BUILD PAYMENT RADIO GROUP VERTICAL");
        }

        // Create options
        radioGroup.setItems(new ArrayList<>(this.entry.getDataSet().values()), formsMap, paymentInfoMap, defaultSelect);
        // Add view
        this.control.addView(dataContainer);

        return radioGroup;
    }


    /**
     * Function responsible for constructing the ratings form layout
     */
    private void buildRatingOptionsTerms(int controlWidth) {
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
            UIUtils.setProgressForRTLPreJellyMr2(starts);
            starts.setTag(RATING_BAR_TAG + pairs.getKey());
            starts.setTag(R.id.rating_bar_id, pairs.getKey().toString());
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

        if (getSharedPref().getBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true) && getSharedPref().getBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true)) {
            addCustomRatingCheckbox(linearLayout, controlWidth);
        }
        this.dataControl = linearLayout;

        this.control.addView(this.dataControl);

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
     */
    private void addCustomRatingCheckbox(LinearLayout linearLayout, int controlWidth) {
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;

        RelativeLayout.LayoutParams params;
        if (ShopSelector.isRtl() && currentApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        } else {
            params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }

        CheckBox checkWriteFull = (CheckBox) View.inflate(this.context, R.layout.gen_form_check_box, null);

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
        if (this.entry.getInputType() == FormInputType.metadata) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            String dateString = this.getValue();
            Date date;
            try {
                date = sdf.parse(dateString);
            } catch (ParseException e) {
                // if the date has the wrong format
                // there cant be more done
                Print.d(TAG, "setDate: cant parse date: " + dateString);
                return;
            }
            Calendar cal = new GregorianCalendar();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            model.put(this.entry.getId(), dateString);
            Print.d(TAG, "setDate: " + this.entry.getId() + "->" + String.valueOf(day) + "-" + String.valueOf(month) + "-" + String.valueOf(year));

        }
    }

    private View createErrorControl(int dataControlId, int controlWidth) {
        ViewGroup errorControl;

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, dataControlId);

        errorControl = new RelativeLayout(this.context);
        errorControl.setId(parent.getNextId());
        errorControl.setLayoutParams(params);
        errorControl.setVisibility(View.GONE);


        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);

        //#RTL
        if (ShopSelector.isRtl()) {
            params.addRule(RelativeLayout.RIGHT_OF, dataControlId);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 0, (int) context.getResources().getDimension(R.dimen.form_errormessage_margin), 0);
        } else {
            params.addRule(RelativeLayout.LEFT_OF, dataControlId);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins((int) context.getResources().getDimension(R.dimen.form_errormessage_margin), 0, 0, 0);
        }

        ImageView errImage = new ImageView(this.context);
        errImage.setId(parent.getNextId());
        errImage.setLayoutParams(params);
        errImage.setImageResource(R.drawable.indicator_input_error);

        //ErrorText params
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        //#RTL
        if (ShopSelector.isRtl()) {
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
        if (ShopSelector.isRtl()) {
            this.errorTextControl.setSingleLine(true);
            this.errorTextControl.setEllipsize(TruncateAt.END);
        }

        errorControl.addView(this.errorTextControl);
        errorControl.addView(errImage);

        return errorControl;

    }

    /**
     * Build editable text field
     */
    private View buildEditableTextField(ViewGroup group) {
        // Get text field container
        View container = View.inflate(this.context, R.layout.gen_form_text_field, null);
        // Get text
        EditText text = (EditText) container.findViewById(R.id.text_field);
        // Get mandatory
        TextView mandatory = (TextView) container.findViewById(R.id.text_field_mandatory);
        // Get icon
        ImageView icon = (ImageView) container.findViewById(R.id.text_field_icon);
        // Get password eye
        CheckBox box = (CheckBox) container.findViewById(R.id.text_field_password_check_box);
        // Set disabled
        if (this.parent.getForm().getType() == FormConstants.USER_DATA_FORM && this.entry.isDisabledField()) {
            disableView(text);
        }
        // Set icon
        if(this.parent.getForm().getType() == FormConstants.REGISTRATION_FORM
                || this.parent.getForm().getType() == FormConstants.LOGIN_FORM
                || this.parent.getForm().getType() == FormConstants.USER_DATA_FORM) {
            UIUtils.setDrawableByString(icon, ICON_PREFIX + this.entry.getKey());
            icon.setVisibility(View.VISIBLE);
        }
        // Set hint
        if (null != this.entry.getLabel() && this.entry.getLabel().trim().length() > 0) {
            text.setHint(this.entry.getLabel());
            text.setFloatingLabelText(this.entry.getLabel());
        }
        // Set filters
        if (null != this.entry.getValidation() && this.entry.getValidation().max > 0) {
            text.setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.entry.getValidation().max)});
        }
        // Set default value
        if (!TextUtils.isEmpty(this.entry.getValue())) {
            text.setText(this.entry.getValue());
        }
        // Set input type
        switch (this.entry.getInputType()) {
            case relatedNumber:
            case number:
                int inputTypeNumber = this.entry.getKey().contains(RestConstants.PHONE) ? InputType.TYPE_CLASS_PHONE : InputType.TYPE_CLASS_NUMBER;
                text.setInputType(inputTypeNumber);
                break;
            case password:
                text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                setTextWatcher(text, box);
                setPasswordEye(box, text);
                break;
            case email:
                int inputTypeEmail = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
                text.setInputType(inputTypeEmail);
                break;
            default:
                text.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                break;
        }
        // Set mandatory
        mandatory.setVisibility(this.entry.getValidation().isRequired() && !hideAsterisks ? View.VISIBLE : View.GONE);
        // Set next id to parent
        container.setId(parent.getNextId());
        // Save
        this.dataControl = text;
        this.mandatoryControl = mandatory;
        // Add description
        this.dataControl.setContentDescription(this.entry.getKey());
        // Add container
        group.addView(container);
        // Set error control
        if (hasRules()) {
            this.errorControl = createErrorControl(container.getId(), RelativeLayout.LayoutParams.MATCH_PARENT);
            group.addView(this.errorControl);
        }
        // Return the view
        return container;
    }

    private void setTextWatcher(final EditText text, final View view) {
        // Set text watcher
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ...
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int size = s.length();
                // Case empty
                if (size == 0) view.setVisibility(View.GONE);
                    // Case filled
                else if (size >= 1) UIUtils.showViewFadeIn(view);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // ...
            }
        });
    }

    private void setPasswordEye(final CheckBox eye, final EditText text) {
        // Set password eye
        eye.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    text.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    text.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
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

    /**
     * Function that sets date on the date dialog picker
     */
    private void setDialogDate(String date){

        if(dialogDate != null){
            int dayMonth;
            int monthYear;
            int year;

            if (date != null && date.length() > 0 && date.split("-").length > 1) {
                dayMonth = Integer.parseInt(date.split("-")[2]);
                monthYear = Integer.parseInt(date.split("-")[1]);
                year = Integer.parseInt(date.split("-")[0]);
            } else {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                monthYear = c.get(Calendar.MONTH);
                dayMonth = c.get(Calendar.DAY_OF_MONTH);
            }
            dialogDate.setDate(dayMonth, monthYear - 1, year);
        }

    }

    /**
     * Disable a view.
     */
    protected final void disableView(View view) {
        view.setEnabled(false);
        view.setClickable(false);
        view.setFocusable(false);
        if(view instanceof android.widget.TextView) ((android.widget.TextView) view).setTextColor(ContextCompat.getColor(context, R.color.black_700));
    }

}
