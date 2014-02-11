package pt.rocket.pojo;

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

import org.holoeverywhere.FontLoader;
import org.holoeverywhere.widget.Button;
import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.EditText;
import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.TextView;

import pt.rocket.forms.Form;
import pt.rocket.forms.FormField;
import pt.rocket.forms.IFormField;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.InputType;
import pt.rocket.utils.RadioGroupLayout;
import pt.rocket.utils.RadioGroupLayoutVertical;
import pt.rocket.utils.UIUtils;
import pt.rocket.utils.dialogfragments.DialogDatePickerFragment;
import pt.rocket.utils.dialogfragments.DialogDatePickerFragment.OnDatePickerDialogListener;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsSpinner;

import de.akquinet.android.androlog.Log;

/**
 * This Class defines the representation of each control on a dynamic form <p/><br> 
 *
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Nuno Castro
 * @version 1.05
 * 
 * 2012/06/15
 * 
 */
public class DynamicFormItem {
	private final static String TAG = LogTagHelper.create(DynamicFormItem.class);

    private final static int ERRORTEXTSIZE = 14;
    private final static int MANDATORYSIGNALSIZE = 18;
        
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
	
	private float screenDensity;

    /**
     * The constructor for the DynamicFormItem
     * 
     * @param parent
     *            The parent of this control ( an instance of a DynamicForm )
     * @param context
     *            The context where this control is to be inserted. If the FormItem type
     *            date should be used, an activity needs to be given, as the date type
     *            wants to open a dialog.
     * @param entry
     *            The entry that corresponds to this control on the form return by the framework
     * @return
     */
    public DynamicFormItem(DynamicForm parent, Context context, IFormField entry) {
        this.context = context;
        this.parent = parent;
        this.screenDensity = context.getResources().getDisplayMetrics().density;
        this.entry = entry;
        this.control = null;
        this.errorControl = null;
        this.dataControl = null;
        this.errorTextControl = null;
        this.mandatoryControl = null;

        this.errorText = context.getString(R.string.dynamic_errortext);
        
        this.scale = context.getResources().getDisplayMetrics().density;
        this.errorColor = context.getResources().getColor( R.color.red_basic );
        
        
        buildControl();
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

    /**
     * Gets the control.
     * 
     * @return The View that represents the control
     */
    public View getControl() {
        return control;
    }

    /**
     * Gets the editable control. 
     * This is the part of the control that handles the user input
     * 
     * @return The View that represents the control
     */
    public View getEditControl() {
        return dataControl;
    }    
    
    /**
     * Gets if the control has a mandatory flag, 
     * This flag indicates that the user must fill this field before submiting the form.
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
     * @param value
     *            The error message to display in case of a validation error
     */
    public void setErrorText(String value) {
        errorText = value;
        if ( null != errorTextControl) {
            errorTextControl.setText(errorText);
        }
    }
    
    /**
     * Gets the type of the edit control that is displayed to the user. This type is defined in the pt.rocket.framework.forms namespace
     * 
     * @return the type of the user control represented
     */
    public InputType getType() {
        return this.entry.getInputType();
    }

    /**
     * Stores the value inputed by the user on the control and acts accordingly
     * 
     * @param value
     *            The value to fill the control with
     */
    public void setValue(Object value) {

        switch (this.entry.getInputType()) {
        case checkBox:
            boolean checked = false;
            if ( null != value ) {
                if ( value instanceof String ) {
                    checked = value.equals("1") ? true : false ;
                } else if ( value instanceof Boolean ) {
                    checked = (Boolean) value;                
                }  else if ( value instanceof Integer ) {
                    checked = ((Integer)value) == 1 ? true : false ;                
                }                                
            }
            ((CheckBox) this.dataControl).setChecked(checked);
            
            break;
        
        case radioGroup:
        	int position = findPositionForKey(value);
        	        	
        	if ( this.dataControl instanceof IcsSpinner) {
        		int selection;
        		if ( position == -1 )
        			selection = IcsSpinner.INVALID_POSITION;
        		else
        			selection = position;
        		
        		((IcsSpinner) this.dataControl).setSelection( selection );	
        	} else {
        		int selection;
        		if ( position == -1 )
        			selection = RadioGroupLayout.NO_DEFAULT_SELECTION;
        		else
        			selection = position;
        		((RadioGroupLayout) this.dataControl).setSelection( selection );
        	}
            break;
        case metadate:
        case date:
        	dialogDate.setDate( (String)value );
        	break;
        case email:
        case text:
        case password:
        case number:
            String text = null == value ? "" : (String) value;
            ((EditText) this.dataControl).setText(text);
            this.errorControl.setVisibility(View.GONE);
            ((View) this.dataControl).setContentDescription(this.entry.getId());

            if (text.length() == 0) {
                this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);
            } else {
                this.mandatoryControl.setVisibility(View.GONE);
            }

            break;
        }
    }

	private int findPositionForKey(Object value) {
		if ( value == null)
			return -1;
		
		int position;
		position = 0;
		if ( this.entry.getDataSet().containsKey( value ) ) {
		    Iterator<Entry<String, String>> iter = this.entry.getDataSet().entrySet().iterator();
		    boolean found = false;
		    while ( iter.hasNext() ) {
		        if ( iter.next().getKey() == value ) {
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
        setValue( this.entry.getValue() );
    }
    
    /**
     * Loads a previously saved state of the control. This is useful in an orientation changed scenario, for example.
     * 
     * @param inStat the Bundle that contains the stored information of the control 
     */
    public void loadState( Bundle inStat ) {

        switch (this.entry.getInputType()) {
        case meta:
        	break;
        case checkBox:
            boolean checked = inStat.getBoolean( getKey() );
            ((CheckBox) this.dataControl).setChecked(checked);
            
            break;
        case radioGroup:
            int position = inStat.getInt( getKey() );
        	if ( this.dataControl instanceof IcsSpinner) {
        		((IcsSpinner) this.dataControl).setSelection(position);
        	} else {
        		((RadioGroupLayout) this.dataControl).setSelection(position);
        	}
        	
        	break;
            
        case metadate:
        case date:
        	String date = inStat.getString(getKey());
        	this.dialogDate.setDate( date );        	
        	break;
        case email:            
        case text:
        case password:
        case number:
            String text = inStat.getString( getKey() );;
            
            ((EditText) this.dataControl).setText(text);
            this.errorControl.setVisibility(View.GONE);

            if (text.length() == 0) {
                this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);
            } else {
                this.mandatoryControl.setVisibility(View.GONE);
            }

            break;
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
        case radioGroup:
        	String value;
        	if ( this.dataControl instanceof IcsSpinner) {
        		value = ((IcsSpinner) this.dataControl).getSelectedItem().toString();
        	} else {
        		int idx = ((RadioGroupLayout)this.dataControl).getSelectedIndex();
        		value = ((RadioGroupLayout)this.dataControl).getItemByIndex(idx);
        	}
            if ( this.entry.getDataSet().containsValue( value ) ) {
                Iterator<Entry<String, String>> iter = this.entry.getDataSet().entrySet().iterator();
                Entry<String, String> pair;
                
                while ( iter.hasNext() ) {
                    pair = iter.next(); 
                    if ( pair.getValue() == value ) {
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
        case email:            
        case text:
        case password:
        case number:
            result = ((EditText) this.dataControl).getText().toString();
            break;
            
        default:
            result = "";
            break;
        }

        return result;
    }
    
    /**
     * Stores the value of the current field on the value property of the server side form field
     */
    public void storeValueInField() {
        storeValueInField (( String ) getValue());
    }

    /**
     * Stores a value on the value property of the server side form field
     * 
     * @param value The value to store on the field
     */
    public void storeValueInField( String value) {
        this.entry.setValue( value );
    }
    
    
    /**
     * Saves the state of the current field to a bundle object
     * 
     * @param outState The Bundle object that will hold the state of the object
     * 
     */
    public void saveState( Bundle outState ) {

        switch (this.entry.getInputType()) {
        case meta:
        	break;
        case checkBox:
            outState.putBoolean( getKey(), ((CheckBox) this.dataControl).isChecked() );
            break;
        
        case radioGroup:
        	if ( this.dataControl instanceof IcsSpinner) {
        		outState.putInt(getKey(), ((IcsSpinner) this.dataControl).getSelectedItemPosition() );
        	} else {
        		outState.putInt(getKey(), ((RadioGroupLayout) this.dataControl).getSelectedIndex());
        	}
        	break;
        case metadate:        	
        case date:
        	outState.putString(getKey(), dialogDate.getDate());
        	break;
        case email:            
        case text:
        case password:
        case number:
            outState.putString(getKey(), ((EditText) this.dataControl).getText().toString() );
            break;
        }

    }
    

    /**
     * Validates the data entered onto the control according to the validation rules defined and if the field is mandatory or not 
     * 
     * @return true, if there is no errors; false, if there are errors
     */
    public boolean Validate() {
        boolean result = true;

        switch (this.entry.getInputType()) {
        case checkBox:
            
            break;
        case radioGroup:
        	boolean valid;
        	
        	if ( this.dataControl instanceof IcsSpinner) {
        		valid = ((IcsSpinner)this.dataControl).getSelectedItemPosition() != Spinner.INVALID_POSITION;
        	} else if (this.dataControl instanceof RadioGroupLayoutVertical ) {
        		valid = ((RadioGroupLayoutVertical) this.dataControl).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
        	} else {
        	    valid = ((RadioGroupLayout) this.dataControl).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
        	}
        		
        		
            if ( !valid ) {
                result = !this.entry.getValidation().isRequired();
                setErrorText(context.getString(R.string.error_ismandatory) + " " + this.entry.getLabel() );
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
            String value = ((EditText) this.dataControl).getText().toString();
            result = validateStringToPattern(value);

            break;
        }
        }

        if ( null != errorControl ) {
            this.errorControl.setVisibility(!result ? View.VISIBLE : View.GONE);
        }
        

        return result;
    }

	private boolean validateStringToPattern(String value) {
		boolean result;
		if (value.length() == 0) {
		    result = !this.entry.getValidation().isRequired();
		    setErrorText(context.getString(R.string.error_ismandatory) + " " + this.entry.getLabel() );
		} else {
		    if ( this.entry.getValidation().min > 0 && value.length() < this.entry.getValidation().min ) {
		        setErrorText( this.entry.getLabel() + " " + context.getResources().getString( R.string.form_texttoshort ) );
		        result = false;
		    } else if ( this.entry.getValidation().max > 0 && value.length() > this.entry.getValidation().max ) {
		        setErrorText( this.entry.getLabel() + " " + context.getResources().getString( R.string.form_texttolong ) );
		        result = false;
		    } else {
		    
		        Pattern pattern = Pattern.compile(this.entry.getValidation().regex, Pattern.CASE_INSENSITIVE);
		        setErrorText("The " + this.entry.getLabel() + " " + context.getString(R.string.dynamic_errortext));
   
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
    public void ShowError( String message ) {
        if ( null != errorControl ) {
            setErrorText( message );
            this.errorControl.setVisibility( message.equals("") ? View.GONE :  View.VISIBLE);
        }        
    }
    
    /**
     * Validates if the field is required and if it is filled 
     * 
     * @return true, if the field if OK; false, if the field is empty and is required  
     */
    public boolean ValidateRequired() {
        boolean result = true;

        switch (this.entry.getInputType()) {
        case checkBox:
            
            break;
            
        case radioGroup:
            boolean valid;
            
            if ( this.dataControl instanceof IcsSpinner )
            	valid = ((IcsSpinner)this.dataControl).getSelectedItemPosition() != Spinner.INVALID_POSITION;
            else if ( this.dataControl instanceof RadioGroupLayoutVertical ){
                valid = ((RadioGroupLayoutVertical) this.dataControl).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
            } else {
                valid = ((RadioGroupLayout)this.dataControl).getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION;
            }
            	
            
            if ( !valid ) {
                result = !this.entry.getValidation().isRequired();
            } else {
            	result = valid;
            }
            break;
            
        case metadate:
        case date:
            
        	valid = dialogDate.isSetOnce();
        	
        	if ( !valid )
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
        }

        return result;        
    }
    
    /**
     * Sets the listener for the focus change of the edit part of the component
     * 
     * @param listener The listener to be fired when the focus changes
     */
    public void setOnFocusChangeListener ( OnFocusChangeListener listener ) {
        editFocusListener = listener;
    }
    
    /**
     * Sets the listener for the seleted item changes of the edit part of the component
     * 
     * @param listener The listener to be fired when the focus changes
     */
    public void setOnItemSelectedListener ( IcsAdapterView.OnItemSelectedListener listener ) {
        spinnerSelectedListener = listener;
    }
    
    /**
     * Sets the TextWatcher listener to be used by this control 
     * 
     * @param watcher  The listener to be fired every time the text of an component changes
     */
    public void setTextWatcher ( TextWatcher watcher ) {
        textWatcher = watcher;
    }
   

    /**
     * Creates the control with all the validation settings and mandatory representations
     */
    private void buildControl() {
        
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        boolean datePart = false;
        int controlWidth = RelativeLayout.LayoutParams.MATCH_PARENT;
        
        Log.i("CONTROL", " => "+this.entry.getLabel()+" "+this.entry.getInputType());
       
        if (null != this.entry) {
            
            RelativeLayout dataContainer;

            // form item container
            this.control = (View) new RelativeLayout(this.context);
            this.control.setId(parent.getNextId());

            switch (this.entry.getInputType()) {
            case checkBox:

                this.control.setLayoutParams(params);
                
                // data controls
                params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                dataContainer = new RelativeLayout(this.context);
                dataContainer.setId(parent.getNextId());
                dataContainer.setLayoutParams(params);
                
                params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                this.dataControl = new CheckBox(this.context);
                ((CheckBox)this.dataControl).setTextColor(context.getResources().getColor(R.color.form_text));
                this.dataControl.setId( parent.getNextId() );
                
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                params.setMargins((int)(3 * this.scale), 0, 0, 0);
                this.dataControl.setLayoutParams(params);
                ((View) this.dataControl).setContentDescription(this.entry.getKey());
                ((CheckBox) this.dataControl).setButtonDrawable(R.drawable.selector_checkbox);
                ((CheckBox) this.dataControl).setFocusable(false);
                ((CheckBox) this.dataControl).setFocusableInTouchMode(false);
                ((CheckBox) this.dataControl).setTextColor(context.getResources().getColor(android.R.color.black));
                ((CheckBox) this.dataControl).setTextSize(14);                
                ((CheckBox) this.dataControl).setText( this.entry.getLabel() );
                ((CheckBox) this.dataControl).setPadding( (int)(25 * scale), this.dataControl.getPaddingTop(), this.dataControl.getPaddingRight(), this.dataControl.getPaddingBottom());

                if ( this.entry.getValue().equals("1") ) {
                    ((CheckBox) this.dataControl).setChecked(true);
                }
                
                this.dataControl.setVisibility(View.VISIBLE); 
                
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.CENTER_VERTICAL); 
                params.rightMargin = 10;
                this.mandatoryControl = new TextView(this.context);
                this.mandatoryControl.setLayoutParams(params);
                this.mandatoryControl.setText("*");
                this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_basic));
                this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

                this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);                
                dataContainer.addView( this.dataControl );  
                dataContainer.addView( this.mandatoryControl );

                ((ViewGroup) this.control).addView(dataContainer);       
                
                break;
            case radioGroup:
                this.control.setLayoutParams(params);
                // data controls
                params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                dataContainer = new RelativeLayout(this.context);
                dataContainer.setId(parent.getNextId());
                dataContainer.setLayoutParams(params);
                
                params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (this.entry.getDataSet().size() > 2) {
                    Log.d("createRadioGroup", "createRadioGroup: Radio Group ORIENTATION_VERTICAL");
                    createRadioGroupVertical(  MANDATORYSIGNALSIZE, params, dataContainer);
                } else {
                    Log.d("createRadioGroup", "createRadioGroup: Radio Group ORIENTATION_HORIZONTAL");
                    createRadioGroup(  MANDATORYSIGNALSIZE, params, dataContainer);
                }
            
                // TODO: Validate this method for poll    
//                Log.d("Poll", "Type: Radio Group");
//                if(entry.getKey().contains("poll")){
//                    Log.d("Poll", "Key: Poll");
//                    createPollRadioGroup(MANDATORYSIGNALSIZE, params, dataContainer);
//                }
                
                	
                break;
                
            case list:
                this.control.setLayoutParams(params);
                // data controls
                params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                dataContainer = new RelativeLayout(this.context);
                dataContainer.setId(parent.getNextId());
                dataContainer.setLayoutParams(params);
                
                params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                
                
                createSpinnerForRadioGroup(MANDATORYSIGNALSIZE, params, dataContainer);
                
            
                // TODO: Validate this method for poll    
                Log.d("Poll", "Type: Radio Group");
                if(entry.getKey().contains("poll")){
                    Log.d("Poll", "Key: Poll");
                    createPollRadioGroup(MANDATORYSIGNALSIZE, params, dataContainer);
                }
                
                break;
            case metadate:
            case date:
            	params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
            	params.topMargin = 6;
                dataContainer = new RelativeLayout(this.context);
                dataContainer.setId(parent.getNextId());
                dataContainer.setLayoutParams(params);

                params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
                this.dataControl = View.inflate(this.context, R.layout.form_button, null);
                this.dataControl.setId( parent.getNextId() );
                this.dataControl.setLayoutParams(params);
                
                ((View) this.dataControl).setContentDescription(this.entry.getKey());
                
                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.CENTER_VERTICAL);
                params.rightMargin = 10; 
                this.mandatoryControl = new TextView(this.context);
        		this.mandatoryControl.setLayoutParams(params);
        		this.mandatoryControl.setText("*");
        		this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_basic));
        		this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

        		this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);                
        		dataContainer.addView( this.dataControl );   
        		dataContainer.addView( this.mandatoryControl );
                
                                
                String text ="";
                
                if (null != this.entry.getLabel() && this.entry.getLabel().trim().length() > 0) {
                	text = this.entry.getLabel();
                	((Button) this.dataControl).setText(text);
                    ((Button) this.dataControl).setTextColor(context.getResources().getColor(R.color.form_text));
                } else if ( this.entry.getKey().equals( "birthday" )){
                    Log.i("ENTERED BIRTHDAY"," HERE ");
                	text = context.getString( R.string.register_birthday );
                	((Button) this.dataControl).setHint(text);
                	((Button) this.dataControl).setHintTextColor(context.getResources().getColor(R.color.form_text_hint));
                	((Button) this.dataControl).setTextColor(context.getResources().getColor(R.color.form_text));
                	this.dataControl.setPadding(UIUtils.dpToPx(13, screenDensity), 0, 0, 10);
//                    ((Button) this.dataControl).setTextColor(context.getResources().getColor(R.color.form_text));
                	
                	
                }
                
                

                                
                String dialogTitle;
                if ( this.entry.getKey().equals( "birthday" ))
                	dialogTitle = context.getString( R.string.register_dialog_birthday);
                else
                	dialogTitle = this.entry.getLabel();
                
                OnDatePickerDialogListener pickerListener = new OnDatePickerDialogListener() {
					
					@Override
					public void onDatePickerDialogSelect(String id, int year, int month, int day) {
						GregorianCalendar cal = new GregorianCalendar(year, month, day);
						Date d = new Date( cal.getTimeInMillis());
						String date = DateFormat.getDateInstance( DateFormat.LONG ).format( d );								
						((Button)DynamicFormItem.this.dataControl).setText( date );
						DynamicFormItem.this.mandatoryControl.setVisibility( View.GONE );
					}
				};
                
                this.dialogDate = DialogDatePickerFragment.newInstance((BaseActivity) context, pickerListener, String.valueOf( dataControl.getId()), dialogTitle, 0, 0, 0);
                this.dataControl.setOnClickListener( new OnClickListener() {
					
					@Override
					public void onClick(View v) {
					    if(!dialogDate.isVisible())
					        dialogDate.show(((BaseActivity)context).getSupportFragmentManager(), null);						
					}
				});
        
                ((ViewGroup) this.control).addView(dataContainer);
                
                break;
            case number:
            case email:            	
            case text:
            case password:
                
                this.control.setLayoutParams(params);
                dataContainer = createTextDataContainer( controlWidth);
                int dataControlId = dataContainer.getId();
                this.errorControl = createErrorControl(dataControlId, controlWidth);
                ((ViewGroup) this.control).addView(dataContainer);
                ((ViewGroup) this.control).addView(this.errorControl);
                
                final CharSequence editText = ((EditText) this.dataControl).getHint();             
                ((View) this.dataControl).setContentDescription(this.entry.getKey());
                // Listeners
                this.dataControl.setOnFocusChangeListener(new OnFocusChangeListener() {
 
                    @Override 
                    public void onFocusChange(View v, boolean hasFocus) {
                        EditText dataCtrl = (EditText) v;
                        if (hasFocus) {
                            errorControl.setVisibility(View.GONE); 
                            mandatoryControl.setVisibility(View.GONE);
                            dataCtrl.setHint(" ");
                            dataCtrl.setCursorVisible(true);
                        } else {
                            if (entry.getValidation().isRequired()) {
                                mandatoryControl.setVisibility(dataCtrl.getText().toString().length() == 0 ? View.VISIBLE : View.GONE);
                            }
                            if(dataCtrl.getHint().toString().equals(" ")){
                                dataCtrl.setHint(editText);
                            }

                        } 
                        
                        if ( null != editFocusListener ) {
                            editFocusListener.onFocusChange( v, hasFocus );
                           
                        }
                    }
                });

                ((EditText)this.dataControl).addTextChangedListener( new TextWatcher(){

                    @Override
                    public void afterTextChanged(Editable s) {
                        if ( null != textWatcher ) {
                            textWatcher.afterTextChanged( s );
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if ( null != textWatcher ) {
                            textWatcher.beforeTextChanged(s, start, count, after);
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if ( null != textWatcher ) {
                            textWatcher.onTextChanged(s, start, before, count);
                        }                        
                    }
                    
                });
//                }
                break;
                
            default:
                Log.w(TAG, "buildControl: Field type not suported (" + this.entry.getInputType() + ") - " + this.entry.getInputType() );
                break;
            }
        }
    }

	private void createSpinnerForRadioGroup(final int MANDATORYSIGNALSIZE, RelativeLayout.LayoutParams params,
			RelativeLayout dataContainer) {
		this.dataControl = View.inflate(this.context, R.layout.form_icsspinner, null);                
		this.dataControl.setId( parent.getNextId() );
		this.dataControl.setLayoutParams(params);

		if (this.entry.getDataSet().size() > 0) {
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.form_spinner_item, new ArrayList<String>(this.entry.getDataSet().values()));
		    adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
		    ((IcsSpinner) this.dataControl).setAdapter(adapter);
		    
		}
		else{
		    ArrayList<String> default_string = new ArrayList<String>();
		    default_string.add("Empty");
		    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.form_spinner_item,default_string);
		    adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
		    ((IcsSpinner) this.dataControl).setAdapter(adapter);
		}
		
		//sets the spinner value
		int position = 0;
		if ( null != this.entry.getValue() && !this.entry.getValue().trim().equals("") ) {
		    for ( String item : new ArrayList<String>(this.entry.getDataSet().values()) ) {
		        if ( item.equals( this.entry.getValue() ) ) {
		            ((IcsSpinner)this.dataControl).setSelection(position);
		            break;
		        }
		    }
		    position++;                    
		}
		
		this.dataControl.setVisibility(View.VISIBLE); 
		
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.CENTER_VERTICAL); 
		params.rightMargin = 10;
		this.mandatoryControl = new TextView(this.context);
		this.mandatoryControl.setLayoutParams(params);
		this.mandatoryControl.setText("*");
		this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_basic));
		this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);

		this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);                
		dataContainer.addView( this.dataControl );  
		dataContainer.addView( this.mandatoryControl );

		((ViewGroup) this.control).addView(dataContainer);       
		
		FontLoader.applyDefaultFont( dataContainer );
		// Listeners
		((IcsSpinner)this.dataControl).setOnItemSelectedListener(new IcsAdapterView.OnItemSelectedListener() {

		    @Override
		    public void onItemSelected(IcsAdapterView<?> parent, View view, int position, long id) {
		        if (entry.getValidation().isRequired()) {
		            mandatoryControl.setVisibility(position == Spinner.INVALID_POSITION ? View.VISIBLE : View.GONE);
		        }
		        
		        if ( null != spinnerSelectedListener ) {
		            spinnerSelectedListener.onItemSelected(parent, view, position, id);
		        }                          
		    }

		    @Override
		    public void onNothingSelected(IcsAdapterView<?> parent) {
		        if (entry.getValidation().isRequired()) {
		            mandatoryControl.setVisibility( View.VISIBLE );
		        }

		        if ( null != spinnerSelectedListener ) {
		            spinnerSelectedListener.onNothingSelected(parent);
		        }                          
		    }
		});
		
		this.entry.setOnDataSetReceived( new FormField.OnDataSetReceived() {
		    
		    @Override
		    public void DataSetReceived(Map<String, String> dataSet) {
		        if (dataSet.size() > 0) {
		            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.form_spinner_item, new ArrayList<String>( dataSet.values() ));
		            adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
		            ((IcsSpinner) dataControl).setAdapter(adapter);
		            
		            
		        }
		        else{
		            ArrayList<String> default_string = new ArrayList<String>();
		            default_string.add("Empty");
		            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.form_spinner_item,default_string);
		            adapter.setDropDownViewResource(R.layout.form_spinner_dropdown_item);
		            ((IcsSpinner) dataControl).setAdapter(adapter);
		        }
		        
		        //sets the spinner value
		        int position = 0;
		        if ( null != entry.getValue() && !entry.getValue().trim().equals("") ) {
		            for ( String item : new ArrayList<String>( dataSet.values()) ) {
		                if ( item.equals( entry.getValue() ) ) {
		                    ((IcsSpinner)dataControl).setSelection(position);
		                    break;
		                }
		            }
		            position++;                    
		        }                                                
		    }
		} );
	}
	
	// TODO: Validate this method for poll
	private void createPollRadioGroup( final int MANDATORYSIGNALSIZE, RelativeLayout.LayoutParams params, RelativeLayout dataContainer ) {
        RadioGroupLayout radioGroup = (RadioGroupLayout) View.inflate(this.context, R.layout.form_radiolayout, null );
        radioGroup.setItems(new ArrayList<String>( this.entry.getDataValues().values() ), RadioGroupLayout.NO_DEFAULT_SELECTION );
        radioGroup.setOnCheckedChangeListener( new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DynamicFormItem.this.mandatoryControl.setVisibility( View.GONE );
            }
        });
        
        this.dataControl = radioGroup;
        this.dataControl.setId( parent.getNextId() );
        this.dataControl.setLayoutParams(params);
        dataContainer.addView( this.dataControl );
        
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL); 
        params.rightMargin = 10;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_basic));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);                
        dataContainer.addView( this.mandatoryControl );
        
        ((ViewGroup) this.control).addView(dataContainer);
    }
	
	
	
	private void createRadioGroup( final int MANDATORYSIGNALSIZE, RelativeLayout.LayoutParams params, RelativeLayout dataContainer ) {
	    
	    RadioGroupLayout  radioGroup = (RadioGroupLayout) View.inflate(this.context, R.layout.form_radiolayout, null );    
	    
	    
		radioGroup.setItems(new ArrayList<String>( this.entry.getDataSet().values() ), RadioGroupLayout.NO_DEFAULT_SELECTION );
		radioGroup.setOnCheckedChangeListener( new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				DynamicFormItem.this.mandatoryControl.setVisibility( View.GONE );
			}
		});
		
		this.dataControl = radioGroup;
		this.dataControl.setId( parent.getNextId() );
		this.dataControl.setLayoutParams(params);
		dataContainer.addView( this.dataControl );
		
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		params.addRule(RelativeLayout.CENTER_VERTICAL); 
		params.rightMargin = 10;
		this.mandatoryControl = new TextView(this.context);
		this.mandatoryControl.setLayoutParams(params);
		this.mandatoryControl.setText("*");
		this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_basic));
		this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
		this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);                
		dataContainer.addView( this.mandatoryControl );
		    
        ((ViewGroup) this.control).addView(dataContainer);
	}
	
	/**
	 * Generates a Vertical RadioGroup
	 * @param MANDATORYSIGNALSIZE - tells if is optional or mandatory
	 * @param params
	 * @param dataContainer
	 */
	private void createRadioGroupVertical( final int MANDATORYSIGNALSIZE, RelativeLayout.LayoutParams params, RelativeLayout dataContainer ) {
        
        RadioGroupLayoutVertical  radioGroup = (RadioGroupLayoutVertical) View.inflate(this.context, R.layout.form_radiolistlayout, null );    
        HashMap<String, Form> formsMap = new HashMap<String, Form>();
        Iterator<String> it =  this.entry.getDataSet().values().iterator();
        while(it.hasNext()){
            
            String key = it.next();
            if(this.parent.getForm().fields.get(0).getPaymentMethodsField().get(key).fields.size()>0){
                formsMap.put(key, this.parent.getForm().fields.get(0).getPaymentMethodsField().get(key));
            }
            Log.i(TAG, "code1checked "+key+" createRadioGroupVertical: "+this.parent.getForm().fields.get(0).getPaymentMethodsField().get(key).fields.size());           
        }

        
        
        radioGroup.setItems(new ArrayList<String>( this.entry.getDataSet().values() ), formsMap,  RadioGroupLayoutVertical.NO_DEFAULT_SELECTION );
        radioGroup.setOnCheckedChangeListener( new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    if(i != checkedId){
                        if(group.getChildAt(i).findViewById(R.id.extras) != null){
                            group.getChildAt(i).findViewById(R.id.extras).setVisibility(View.GONE);    
                        }
                    } else if(i == checkedId){
                        if(group.getChildAt(i).findViewById(R.id.extras) != null){
                            group.getChildAt(i).findViewById(R.id.extras).setVisibility(View.VISIBLE);    
                        }
                    }
                }
                DynamicFormItem.this.mandatoryControl.setVisibility( View.GONE );
            }
        });
        
        this.dataControl = radioGroup;
        this.dataControl.setId( parent.getNextId() );
        this.dataControl.setLayoutParams(params);
        dataContainer.addView( this.dataControl );
        
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL); 
        params.rightMargin = 10;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_basic));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);                
        dataContainer.addView( this.mandatoryControl );
        
        ((ViewGroup) this.control).addView(dataContainer);
    }

	@SuppressLint("SimpleDateFormat")
	public void addSubFormFieldValues(ContentValues model) {
		if ( this.entry.getInputType() == InputType.metadate) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dateString = this.getValue().toString();
			Date date;
			try {
				date = sdf.parse( dateString );
			} catch (ParseException e) {
				// if the date has the wrong format
				// there cant be more done
				Log.d( TAG, "setDate: cant parse date: " + dateString );
				return;
			}
			Calendar cal = new GregorianCalendar();
			cal.setTime( date );
			int year = cal.get( Calendar.YEAR);
			int month = cal.get( Calendar.MONTH ) + 1;
			int day = cal.get( Calendar.DAY_OF_MONTH);			
			
			Map<String, IFormField> subFields = this.entry.getSubFormFields();
			model.put( subFields.get( "year" ).getName(), String.valueOf( year ));
			model.put( subFields.get( "month" ).getName(), String.valueOf (month ));
			model.put( subFields.get( "day" ).getName(), String.valueOf( day ));
//			model.put( subFields.get( "year" ).getName(), "");
//			model.put( subFields.get( "month" ).getName(), "");
//			model.put( subFields.get( "day" ).getName(), "");
		}
	}
    
    private View createErrorControl( int dataControlId, int controlWidth ) {
        ViewGroup errorControl;
        
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, dataControlId );
        errorControl = new RelativeLayout(this.context);
        errorControl.setId(parent.getNextId());
        errorControl.setLayoutParams(params);
        errorControl.setVisibility(View.GONE);
        
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule( RelativeLayout.CENTER_VERTICAL );
        params.addRule( RelativeLayout.ALIGN_LEFT, dataControlId );
        params.setMargins((int)context.getResources().getDimension(R.dimen.form_errormessage_margin), 0, 0, 0);
        ImageView errImage = new ImageView(this.context);
        errImage.setId(parent.getNextId());
        errImage.setLayoutParams(params);
        errImage.setImageResource(R.drawable.indicator_input_error);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.RIGHT_OF, errImage.getId());
        params.setMargins( 5, 0, 0, 0);
        this.errorTextControl = new TextView(this.context);
        this.errorTextControl.setId(parent.getNextId());
        this.errorTextControl.setText(this.errorText);
        this.errorTextControl.setLayoutParams(params);
        this.errorTextControl.setTextColor(errorColor);
        this.errorTextControl.setTextSize(ERRORTEXTSIZE);

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
            textDataControl.setFilters(new InputFilter[] { new InputFilter.LengthFilter(this.entry.getValidation().max) });
        }
        
        //specifics
        if (this.entry.getInputType() == InputType.number) {
            int inputType = android.text.InputType.TYPE_CLASS_NUMBER;
            textDataControl.setInputType(inputType);
        } else if (this.entry.getInputType() == InputType.password) {
            int inputType = android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
            textDataControl.setInputType( inputType );
            textDataControl.setTextAppearance(context, R.style.form_edittext_style);
        } else if (this.entry.getInputType() == InputType.email) {
            int inputType = android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
            textDataControl.setInputType( inputType );
            textDataControl.setTextAppearance(context, R.style.form_edittext_style);
        } else {
            int inputType = android.text.InputType.TYPE_CLASS_TEXT;
            textDataControl.setInputType( inputType );
            textDataControl.setTextAppearance(context, R.style.form_edittext_style);
        }
        
        FontLoader.apply( textDataControl, FontLoader.ROBOTO_REGULAR );
        
        return textDataControl;
    }
    
    private RelativeLayout createTextDataContainer( int controlWidth ) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout dataContainer = new RelativeLayout(this.context);
        dataContainer.setId(parent.getNextId());
        dataContainer.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams( controlWidth, RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.dataControl = createTextDataControl();
        this.dataControl.setId( parent.getNextId() );
        this.dataControl.setLayoutParams(params);

        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        params.rightMargin = 10;
        this.mandatoryControl = new TextView(this.context);
        this.mandatoryControl.setLayoutParams(params);
        this.mandatoryControl.setText("*");
        this.mandatoryControl.setTextColor(context.getResources().getColor(R.color.orange_basic));
        this.mandatoryControl.setTextSize(MANDATORYSIGNALSIZE);
        this.mandatoryControl.setVisibility(this.entry.getValidation().isRequired() ? View.VISIBLE : View.GONE);

        dataContainer.addView(this.dataControl);
        dataContainer.addView(this.mandatoryControl);
        
        return dataContainer;
    }

}
