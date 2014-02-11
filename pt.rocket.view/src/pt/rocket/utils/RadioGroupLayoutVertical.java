package pt.rocket.utils;

import java.util.ArrayList;
import java.util.HashMap;

import pt.rocket.constants.FormConstants;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.view.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import de.akquinet.android.androlog.Log;

public class RadioGroupLayoutVertical extends LinearLayout {
    private final static String TAG = LogTagHelper.create(RadioGroupLayoutVertical.class);

    public interface OnRadioGroupSelected {
        public void onRadioGroupItemSelected(int position);
    }

    public static final int NO_DEFAULT_SELECTION = -1;

    private ArrayList<String> mItems;
    private HashMap<String, Form> formsMap;
    private int mDefaultSelected;
    private RadioGroup mGroup;
    private LayoutInflater mInflater;
    Context mContext;
    public RadioGroupLayoutVertical(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RadioGroupLayoutVertical(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    @SuppressLint("NewApi")
    public RadioGroupLayoutVertical(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mGroup = (RadioGroup) mInflater.inflate(R.layout.form_radiogroup_vertical, this, false);
        this.addView(mGroup);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mGroup.setOnCheckedChangeListener(listener);
    }

    public void setItems(ArrayList<String> items, HashMap<String, Form> map,int defaultSelected) {
        Log.d(TAG, "setItems: items size = " + items.size() + " defaultSelected = "
                + defaultSelected);
        mItems = items;
        formsMap = map;
        mDefaultSelected = defaultSelected;
        updateRadioGroup();
    }

    private void updateRadioGroup() {
        try {
            mGroup.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        

        int idx;
        for (idx = 0; idx < mItems.size(); idx++) {
            if(formsMap.containsKey(mItems.get(idx))){
                DynamicForm formGenerator = FormFactory.getSingleton().CreateForm(FormConstants.PAYMENT_DETAILS_FORM, mContext, formsMap.get(mItems.get(idx)));
                
                
                
                Log.d(TAG, "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));
                LinearLayout mLinearLayout = (LinearLayout) mInflater.inflate(R.layout.form_radiobutton_with_extra, mGroup,
                        false);
                
                final RadioButton button = (RadioButton) mLinearLayout.findViewById(R.id.radio_button);
                final LinearLayout extras = (LinearLayout) mLinearLayout.findViewById(R.id.extras);
                extras.addView(formGenerator.getContainer());
                button.setId(idx);
                button.setText(mItems.get(idx));
                if (idx == mDefaultSelected)
                    button.setChecked(true);
                button.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View v) {
                       if(button.isChecked()){
                           extras.setVisibility(View.VISIBLE);
                       } else {
                           extras.setVisibility(View.GONE);
                       }
                       mGroup.check(button.getId());
                    }
                });
                
                mGroup.addView(mLinearLayout, idx);
                
            } else {
                
                Log.d(TAG, "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));
                RadioButton button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton, mGroup,
                        false);
                button.setId(idx);
                button.setText(mItems.get(idx));
                if (idx == mDefaultSelected)
                    button.setChecked(true);
                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                mGroup.addView(button, idx, layoutParams);
            }
            
            
        }

    }

    public int getSelectedIndex() {
        int radioButtonID = mGroup.getCheckedRadioButtonId();
        View radioButton = mGroup.findViewById(radioButtonID);
        int idx = mGroup.indexOfChild(radioButton);

        return idx;
    }

    public String getItemByIndex(int idx) {
        if (mItems == null)
            return null;
        if( idx < 0 )
            return null;
        return mItems.get(idx);
    }

    public void setSelection(int idx) {
        if(idx>0){
            RadioButton button = (RadioButton) mGroup.getChildAt(idx);
            button.setChecked(true);    
        }
        
    }

}
