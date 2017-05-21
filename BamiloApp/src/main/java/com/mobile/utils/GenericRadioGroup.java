package com.mobile.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mobile.service.utils.output.Print;

import java.util.ArrayList;


/**
 * This class represents a radio group that can be fill with some custom views containing a radio button 
 * @author sergiopereira
 * @see {@link RadioGroup} </br> 
 * <a href='http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/1.5_r4/android/widget/RadioGroup.java'>source code</a>
 */
public class GenericRadioGroup extends RadioGroup implements View.OnClickListener{
    
    private final static String TAG = GenericRadioGroup.class.getSimpleName();
    
    private static final String RADIO_BUTTON_TAG = "_radio_button_";
    
    private static final String RADIO_GROUP_TAG = "radio_group_";

    private ArrayList<RadioButton> mRadioButtons;
    
    private int mCurrentCheckedButtonPos;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    private CheckedStateTracker mRadioChildOnCheckedChangeListener;
    
    /**
     * Constructor
     * @param context
     */
    public GenericRadioGroup(Context context) {
        super(context);
        init();
    }
    
    /**
     * Constructor
     * @param context
     * @param attrs
     */
    public GenericRadioGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    /**
     * Initialize the radio group
     */
    private void init() {
        this.mRadioButtons = new ArrayList<>();
        this.mCurrentCheckedButtonPos = -1;
        this.mOnCheckedChangeListener = null;
        this.mRadioChildOnCheckedChangeListener = new CheckedStateTracker();
    }
    
    /**
     * Add view with respective tag</br>
     * The that is associated to view and the radio child
     * @param child
     * @param tag
     * @author sergiopereira
     */
    public void addView(View child, String tag){
        child.setTag(tag);
        addView(child);
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#addView(android.view.View)
     */
    @Override
    public void addView(View parent) {
        // Get and save the radio button
        if(parent instanceof ViewGroup) {
            // Locate a radio button
            View view = locateRadioButtons((ViewGroup) parent, null);
            // Associate to parent view
            if(view != null) {
                // Add to child the parent tag
                if(parent.getTag() != null) view.setTag(parent.getTag());
                // Add to parent the child tag
                else parent.setTag(view.getTag());
                // Add listener for parent
                parent.setOnClickListener(this);
            }
        }
        super.addView(parent);
    }
    
    /**
     * Uncheck all items
     * @author sergiopereira
     */
    public void clearCheckGroup(){
        Print.d(TAG, "UNCHECK GROUP: " + getId());
        mCurrentCheckedButtonPos = -1;
        for (RadioButton radio : mRadioButtons) setChecked(radio, false);
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.ViewGroup#removeAllViews()
     */
    @Override
    public void removeAllViews() {
        super.removeAllViews();
        this.mRadioButtons.clear();
        this.mCurrentCheckedButtonPos = -1;
    }
    
    /**
     * Locate the radio button on a view group recursively
     * @param child a view group
     * @author sergiopereira
     */
    private View locateRadioButtons(ViewGroup child, View result) {
        // Locate
        int size = child.getChildCount();
        for (int i = 0; i < size; i++) {
            View viewChild = child.getChildAt(i);
            // Current level
            if (viewChild instanceof RadioButton) {
                result = stampRadioButton((RadioButton) viewChild);
                break;
                // Sub level
            } else if (viewChild instanceof ViewGroup) {
                result = locateRadioButtons((ViewGroup) viewChild, result);
                if (result != null) break;
            }
        }
        return result;
    }
    
    /**
     * Stamp the current radio button and save it
     * @param radioButton
     * @author sergiopereira
     */
    private RadioButton stampRadioButton(RadioButton radioButton){
        String tag = generateViewTag();
        radioButton.setTag(tag);
        radioButton.setOnCheckedChangeListener(mRadioChildOnCheckedChangeListener);
        // Validate id setCheckedItem was called before fill
        if(mCurrentCheckedButtonPos == mRadioButtons.size()) radioButton.setChecked(true);
        mRadioButtons.add(radioButton);
        return radioButton;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.RadioGroup#setOnCheckedChangeListener(android.widget.RadioGroup.OnCheckedChangeListener)
     */
    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        Print.d(TAG, "SET LISTENER ON GROUP ID: " + getId());
        mOnCheckedChangeListener = listener;
    }
    
    /**
     * Generate a custom tag for a radio button
     * @return a string representing the radio children
     * @author sergiopereira
     */
    private String generateViewTag(){
        return RADIO_GROUP_TAG + getId() + RADIO_BUTTON_TAG + mRadioButtons.size();
    }
    
    /**
     * Returns the number of radio children in the group.
     * @return a positive integer representing the number of radio children in the group
     * @author sergiopereira
     */
    public int getRadioChildCount(){
        return mRadioButtons.size();
    }
    
    /**
     * Get the position on the layout of the checked radio button
     * @return
     */
    public int getCheckedPosition(){
        return mCurrentCheckedButtonPos;
    }
    
    /**
     * Get the tag associated to the main view and radio button
     * @return Object
     * @author sergiopereira
     */
    public Object getCheckedTag(){
        try {
            return mRadioButtons.get(mCurrentCheckedButtonPos).getTag();
        } catch (IndexOutOfBoundsException e) {
            Print.w(TAG, "Get checked radio button tag!");
            return null;
        }
    }
    
    /**
     * Class used to receive the on check changed associated to the radio buttons
     * @author sergiopereira
     */
    private class CheckedStateTracker implements CompoundButton.OnCheckedChangeListener {
        /*
         * (non-Javadoc)
         * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
         */
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Print.d(TAG, "ON CHECK CHANGE CHILD: " + buttonView.getTag());
            if(isChecked && getRadioChildCount() > 0 ) {
                // Update the other buttons
                checkRadioWithTag(buttonView.getTag().toString());
                // Notify the listener that checked change
                notifyOnCheckedChangeListener();
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View parent) {
        Print.d(TAG, "CLICKED ON " + parent.getTag().toString());
        // Update the other buttons
        checkRadioWithTag(parent.getTag().toString());
        // Notify the listener that checked change
        notifyOnCheckedChangeListener();
    }
    
    /**
     * Method used to check or uncheck the radio children.
     * @author sergiopereira
     */
    private void checkRadioWithTag(String checkedTag){
        Print.d(TAG, "RADIO CHECKED: " + checkedTag);
        int size = mRadioButtons.size();
        for (int i = 0; i < size; i++) {
            RadioButton radioButton = mRadioButtons.get(i);
            String tag = radioButton.getTag().toString();
            if(!tag.equals(checkedTag)) {
                setChecked(radioButton, false);
            } else { 
                mCurrentCheckedButtonPos = i; 
                setChecked(radioButton, true);
            }
        }
    }
    
    /**
     * Set this radio checked
     * @param radio
     * @param check
     * @author sergiopereira
     */
    private void setChecked(RadioButton radio, boolean check){
        /**
         * Removed the listener to not trigger the listener when you set checked value
         */
        radio.setOnCheckedChangeListener(null);
        radio.setChecked(check);
        radio.setOnCheckedChangeListener(mRadioChildOnCheckedChangeListener);
    }
    
    /**
     * Method used to notify the listener when checked changed
     * @author sergiopereira
     */
    private void notifyOnCheckedChangeListener(){
        if(mOnCheckedChangeListener != null) mOnCheckedChangeListener.onCheckedChanged(this, mCurrentCheckedButtonPos);
        else Print.w(TAG, "CheckedChangeListener is null");
    }
    
    /**
     * Set the radio button for position checked.</br>
     * Checked before fill the parent is validated on {@link #stampRadioButton(RadioButton radioButton)}.</br>
     * Checked after fill the parent is called {@link #checkRadioWithTag(String checkedTag)}.</br>
     * @param position
     * @author sergiopereira
     */
    public void setCheckedItem(int position){
        try {
            // Set before fill the parent
            if(mRadioButtons.size() == 0) mCurrentCheckedButtonPos = position;
            // Set after fill the parent
            else checkRadioWithTag(mRadioButtons.get(position).getTag().toString());
        } catch (IndexOutOfBoundsException e) {
            Print.w(TAG, "Radio buttons count: " + mRadioButtons.size() + " check position: " + position);
        }
    }
    
    /**
     * Validate if the group is checked
     * @return true or false
     * @author sergiopereira
     */
    public boolean isValidateGroup(){
        return (mCurrentCheckedButtonPos != -1);
    }
    
}
