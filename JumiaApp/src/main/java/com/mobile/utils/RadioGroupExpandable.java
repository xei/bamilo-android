package com.mobile.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.mobile.components.customfontviews.TextView;
import com.mobile.constants.FormConstants;
import com.mobile.factories.FormFactory;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.IFormField;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.pojo.DynamicForm;
import com.mobile.view.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class is used to manage and generate the RadioGroupExpandable for the Dynamic Forms
 */
public class RadioGroupExpandable extends RadioGroup {
    private ArrayList<IFormField> mItems;
    private HashMap<Integer, DynamicForm> generatedForms;
    private int mDefaultSelected;
    private RadioGroup mGroup;
    private LayoutInflater mInflater;
    private WeakReference<View.OnClickListener> mClickListener;
    Context mContext;

    public RadioGroupExpandable(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RadioGroupExpandable(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mGroup = this;
    }

    /**
     * Add click listener callback
     * @param clickListener
     */
    public void addClickListener(@NonNull OnClickListener clickListener){
        mClickListener = new WeakReference<>(clickListener);
    }

    /**
     * Method used to set values from the options map with respective default value.<br>
     * - With right style
     */
    public void setItems(@NonNull ArrayList<IFormField> items, String defaultValue) {
        // Find the default value
        int defaultSelect = IntConstants.INVALID_POSITION;
        int count = defaultSelect;
        for (IFormField entryValue : items) {
            count++;
            if (TextUtils.equals(entryValue.getLabel(), defaultValue)) {
                defaultSelect = count;
                break;
            }
        }

        // set and show items
        setItems(items, defaultSelect);
    }

    /**
     * Set items list
     * @param items
     * @param defaultSelected
     */
    public void setItems(ArrayList<IFormField> items, int defaultSelected) {
        mItems = items;
        mDefaultSelected = defaultSelected;
        updateRadioGroup();
    }

    /**
     * Update Radio Group list and layout
     */
    private void updateRadioGroup() {
        try {
            mGroup.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        generatedForms = new HashMap<>();
        for (int idx = 0; idx < mItems.size(); idx++) {
            createRadioButton(idx, mItems.get(idx));
        }
        // Set the default radio button checked
        if(mDefaultSelected > IntConstants.INVALID_POSITION){
            checkDefaultOption(mDefaultSelected);
        }
    }

    /**
     * Method used to check the default option.<br>
     * The runnable is a hack for Android Marshmallow (API 23)
     */
    private void checkDefaultOption(final int defaultSelected) {
        post(new Runnable() {
            @Override
            public void run() {
                RadioButton button = (RadioButton) mGroup.getChildAt(defaultSelected).findViewById(R.id.radio_shipping);
                if(button != null){
                    button.setChecked(true);
                    mGroup.check(button.getId());
                }
            }
        });
    }

    /**
     * Create a Radio button with an extra LinearLayout for content
     *
     * @param idx index of label
     */
    private void createRadioButton(int idx, IFormField field) {
        // Get views
        final LinearLayout container = (LinearLayout) mInflater.inflate(R.layout.form_radioexpandable, null, false);
        final LinearLayout extras = (LinearLayout) container.findViewById(R.id.radio_extras_container);
        final RadioButton button = (RadioButton) container.findViewById(R.id.radio_shipping);

        if (TextUtils.isNotEmpty(field.getLinkText()) || TextUtils.isNotEmpty(field.getText())) {
            addTextLayout(field, extras);
        }

        if (TextUtils.isNotEmpty(field.getSubText())) {
            addSubTextLayout(field.getSubText(), extras);
        }

        // Hide  divider
        container.findViewById(R.id.radio_divider).setVisibility(View.GONE);

        RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        container.setId(idx);
        container.setLayoutParams(mParams);

        button.setText(field.getLabel());

        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (button.isChecked()) {
                        extras.setVisibility(View.VISIBLE);
                    } else {
                        extras.setVisibility(View.GONE);
                    }
                } catch (StackOverflowError e) {
                    e.printStackTrace();
                }

                setSelection(container.getId());
            }
        });

        // Set default
        button.setSelected(idx == mDefaultSelected);

        if(CollectionUtils.isNotEmpty(field.getSubForms()) && field.getSubForms().containsKey(field.getLabel())){
            Form subForm = field.getSubForms().get(field.getLabel());
            DynamicForm mReturnFormGenerator = FormFactory.create(FormConstants.RETURN_METHOD_FORM, getContext(), subForm);
            generatedForms.put(idx,mReturnFormGenerator);
            extras.addView(mReturnFormGenerator.getContainer());
        }


        // Add radio option
        mGroup.addView(container);
    }

    /**
     * Add text Layout
     */
    private void addTextLayout(final @Nullable IFormField field, @NonNull ViewGroup extraSubtext){
        String text = field.getText();
        String link = TextUtils.isNotEmpty(field.getLinkHtml()) ? field.getLinkHtml() : field.getLinkTarget();
        String linklabel = field.getLinkText();

        String completeText = null;
        if(TextUtils.isNotEmpty(link)){
            completeText = linklabel;
        }

        if(TextUtils.isNotEmpty(text) && TextUtils.isNotEmpty(link)){
            completeText = getContext().getString(R.string.first_space_second_placeholder, linklabel,  text);
        } else if(TextUtils.isNotEmpty(text)){
            completeText = text;
        }

        if(TextUtils.isNotEmpty(link)){
            SpannableString spannableString = new SpannableString(completeText);

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    onClickLinkSpannable(field, textView);
                }
                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };
            spannableString.setSpan(clickableSpan, 0, linklabel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.blue_1)), 0, linklabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            ((TextView) extraSubtext.findViewById(R.id.radio_expandable_text)).setText(spannableString);
            ((TextView) extraSubtext.findViewById(R.id.radio_expandable_text)).setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            ((TextView) extraSubtext.findViewById(R.id.radio_expandable_text)).setText(text);
        }
        extraSubtext.findViewById(R.id.radio_expandable_text).setVisibility(VISIBLE);

    }

    /**
     * Handle click on the item link
     */
    private void onClickLinkSpannable(final @Nullable IFormField field, @NonNull View view){
        if(mClickListener != null && mClickListener.get() != null) {
            if (TextUtils.isNotEmpty(field.getLinkTarget())) {
                view.setTag(R.id.target_link, field.getLinkTarget());
                view.setTag(R.id.target_title, field.getLabel());
                mClickListener.get().onClick(view);

            } else {
                view.setTag(R.id.html_link, field.getLinkHtml());
                view.setTag(R.id.target_title, field.getLabel());
                mClickListener.get().onClick(view);
            }
        }
    }

    /**
     * Add the Sub Text section to the element layout
     */
    private void addSubTextLayout(@NonNull String subText, ViewGroup extraSubtext){
        ((TextView) extraSubtext.findViewById(R.id.sub_text)).setText(subText);
        extraSubtext.findViewById(R.id.sub_text).setVisibility(VISIBLE);
    }

    /**
     * Get selected index
     */
    public int getSelectedIndex() {
        int radioButtonID = mGroup.getCheckedRadioButtonId();
        View radioButton = mGroup.findViewById(radioButtonID);
        return mGroup.indexOfChild(radioButton);
    }

    /**
     * Validate Form state for Global and child Forms
     */
    public boolean validate(){
        if(getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION && generatedForms.containsKey(getSelectedIndex())){
            return generatedForms.get(getSelectedIndex()).validate();

        } else if(getSelectedIndex() != RadioGroupLayout.NO_DEFAULT_SELECTION ) {
            return true;
        }

        return false;
    }

    /**
     * Save values from sub forms
     * @param contentValues
     * @return
     */
    public ContentValues save(@NonNull String parentKey, @NonNull ContentValues contentValues){
        if(CollectionUtils.isNotEmpty(mItems)){
            contentValues.put(parentKey, mItems.get(getSelectedIndex()).getValue());
            if(generatedForms.containsKey(getSelectedIndex())) {
                contentValues.putAll(generatedForms.get(getSelectedIndex()).save());
            }
        }

        return contentValues;
    }

    public Bundle saveState(@NonNull String parentKey, @NonNull Bundle outState){
        if(CollectionUtils.isNotEmpty(mItems)){
            outState.putInt(parentKey, getSelectedIndex());
            if(generatedForms.containsKey(getSelectedIndex())) {
                generatedForms.get(getSelectedIndex()).saveFormState(outState);
            }
        }

        return outState;
    }

    public void loadState(@NonNull String parentKey, @NonNull Bundle inStat){
        if(inStat.containsKey(parentKey)){
            mDefaultSelected = inStat.getInt(parentKey);
            setSelection(mDefaultSelected);
            if(generatedForms.containsKey(inStat.getInt(parentKey))) {
                generatedForms.get(getSelectedIndex()).loadSaveFormState(inStat);
            }
        }

    }
    /**
     * Show Global message if dynamic Form has form childs
     */
    public boolean showGlobalMessage(){
        return getSelectedIndex() == RadioGroupLayout.NO_DEFAULT_SELECTION;

    }

    /**
     * Set current selection
     * @param idx
     */
    public void setSelection(final int idx) {
        if (idx >= 0) {
            RadioButton button = (RadioButton) mGroup.getChildAt(idx).findViewById(R.id.radio_shipping);
            if(button != null){
                // To avoid stackoverflow from addFocusables
                button.setChecked(true);
                button.setFocusable(true);
                button.setFocusableInTouchMode(true);

                mGroup.getChildAt(idx).findViewById(R.id.radio_extras_container).setVisibility(View.VISIBLE);
                mGroup.check(idx);
            }
        }
        cleanOtherSelections(idx);
    }

    /**
     * Clear older selections
     * @param idx
     */
    private void cleanOtherSelections(final int idx) {
        for (int i = 0; i < mGroup.getChildCount(); i++) {
            if (i != idx) {
                RadioButton button = (RadioButton) mGroup.getChildAt(i).findViewById(R.id.radio_shipping);
                if(button != null){
                    // To avoid stackoverflow from addFocusables
                    button.setFocusable(false);
                    button.setFocusableInTouchMode(false);

                    button.setChecked(false);
                    mGroup.getChildAt(i).findViewById(R.id.radio_extras_container).setVisibility(View.GONE);
                }
            }
        }
    }

}
