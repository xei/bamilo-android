package pt.rocket.utils;

import java.util.ArrayList;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import de.akquinet.android.androlog.Log;

public class RadioGroupLayout extends LinearLayout {
    private final static String TAG = LogTagHelper.create(RadioGroupLayout.class);

    public interface OnRadioGroupSelected {
        public void onRadioGroupItemSelected(int position);
    }

    public static final int NO_DEFAULT_SELECTION = -1;

    private ArrayList<String> mItems;
    private int mDefaultSelected;
    private RadioGroup mGroup;
    private LayoutInflater mInflater;

    public RadioGroupLayout(Context context) {
        super(context);
        init();
    }

    public RadioGroupLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("NewApi")
    public RadioGroupLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mGroup = (RadioGroup) mInflater.inflate(R.layout.form_radiogroup, this, false);
        this.addView(mGroup);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mGroup.setOnCheckedChangeListener(listener);
    }

    public void setItems(ArrayList<String> items, int defaultSelected) {
        Log.d(TAG, "setItems: items size = " + items.size() + " defaultSelected = "
                + defaultSelected);
        mItems = items;
        mDefaultSelected = defaultSelected;
        updateRadioGroup();
    }

    private void updateRadioGroup() {
        mGroup.removeAllViews();

        int idx;
        for (idx = 0; idx < mItems.size(); idx++) {
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

    public int getSelectedIndex() {
        int radioButtonID = mGroup.getCheckedRadioButtonId();
        View radioButton = mGroup.findViewById(radioButtonID);
        int idx = mGroup.indexOfChild(radioButton);

        return idx;
    }

    public String getItemByIndex(int idx) {
        if (mItems == null)
            return null;

        return mItems.get(idx);
    }

    public void setSelection(int idx) {
        RadioButton button = (RadioButton) mGroup.getChildAt(idx);
        button.setChecked(true);
    }

}
