package pt.rocket.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.widget.TextView;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.FormConstants;
import pt.rocket.factories.FormFactory;
import pt.rocket.forms.Form;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.pojo.DynamicForm;
import pt.rocket.pojo.DynamicFormItem;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;

import de.akquinet.android.androlog.Log;

public class RadioGroupLayoutVertical extends RadioGroup {
    private final static String TAG = LogTagHelper.create(RadioGroupLayoutVertical.class);

    public interface OnRadioGroupSelected {
        public void onRadioGroupItemSelected(int position);
    }

    public static final int NO_DEFAULT_SELECTION = -1;

    private ArrayList<String> mItems;
    private HashMap<String, Form> formsMap;
    private HashMap<Integer, DynamicForm> generatedForms;
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

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mGroup = this;
    }

    public void setItems(ArrayList<String> items, HashMap<String, Form> map, int defaultSelected) {
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
        generatedForms = new HashMap<Integer, DynamicForm>();
        for (idx = 0; idx < mItems.size(); idx++) {
            Log.i(TAG, "code1subForms updateRadioGroup : " + mItems.get(idx) + " formsMap size : "
                    + formsMap.size());
            if (formsMap.containsKey(mItems.get(idx))) {
                Log.i(TAG, "code1subForms updateRadioGroup contains : " + mItems.get(idx));
                DynamicForm formGenerator = FormFactory.getSingleton()
                        .CreateForm(FormConstants.PAYMENT_DETAILS_FORM, mContext,
                                formsMap.get(mItems.get(idx)));
                generatedForms.put(idx, formGenerator);

                Log.d(TAG,
                        "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));
                final LinearLayout mLinearLayout = (LinearLayout) mInflater.inflate(
                        R.layout.form_radiobutton_with_extra, null,
                        false);

                final LinearLayout buttonContainer = (LinearLayout) mLinearLayout
                        .findViewById(R.id.radio_container);
                final LinearLayout extras = (LinearLayout) mLinearLayout.findViewById(R.id.extras);
                extras.addView(formGenerator.getContainer());
                mLinearLayout.setId(idx);
                
                if (JumiaApplication.INSTANCE.getPaymentsInfoList() != null
                        && JumiaApplication.INSTANCE.getPaymentsInfoList().size() > 0
                        && JumiaApplication.INSTANCE.getPaymentsInfoList().containsKey(mItems.get(idx))) {
                    
                    
                    if(JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getText() != null && JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getText().length() > 0){
                        TextView mTextView = (TextView) extras.findViewById(R.id.payment_text);
                        mTextView.setText(JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getText());
                        mTextView.setVisibility(View.VISIBLE);
                    }
                    
                    if(JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getImages() != null && JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getImages().size() > 0){
                        ImageView mImageView = (ImageView) extras.findViewById(R.id.payment_img);
                        AQuery aq = new AQuery(mContext);
                        aq.id(mImageView).image(JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getImages().get(0), true, true, 0, 0, new BitmapAjaxCallback() {

                                    @Override
                                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                                        iv.setImageBitmap(bm);
                                        iv.setVisibility(View.VISIBLE); 
                                    }
                                });
                    }
                }

                final RadioButton button = (RadioButton) mInflater.inflate(
                        R.layout.form_radiobutton, null,
                        false);
                button.setId(idx);
                button.setText(mItems.get(idx));
                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                button.setText(mItems.get(idx));
                if (idx == mDefaultSelected)
                    button.setChecked(true);
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
                        
                        setSelection(mLinearLayout.getId());
                        mGroup.check(mLinearLayout.getId());
                    }
                });

                buttonContainer.addView(button, layoutParams);

                mGroup.addView(mLinearLayout);

            } else if (JumiaApplication.INSTANCE.getPaymentsInfoList() != null
                    && JumiaApplication.INSTANCE.getPaymentsInfoList().size() > 0
                    && JumiaApplication.INSTANCE.getPaymentsInfoList().containsKey(mItems.get(idx))) {
                final LinearLayout mLinearLayout = (LinearLayout) mInflater.inflate(
                        R.layout.form_radiobutton_with_extra, null,
                        false);

                final LinearLayout buttonContainer = (LinearLayout) mLinearLayout
                        .findViewById(R.id.radio_container);
                final LinearLayout extras = (LinearLayout) mLinearLayout.findViewById(R.id.extras);
                mLinearLayout.setId(idx);
                
                if (JumiaApplication.INSTANCE.getPaymentsInfoList() != null
                        && JumiaApplication.INSTANCE.getPaymentsInfoList().size() > 0
                        && JumiaApplication.INSTANCE.getPaymentsInfoList().containsKey(mItems.get(idx))) {
                    
                    
                    if(JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getText() != null && JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getText().length() > 0){
                        TextView mTextView = (TextView) extras.findViewById(R.id.payment_text);
                        mTextView.setText(JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getText());
                        mTextView.setVisibility(View.VISIBLE);
                    }
                    
                    if(JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getImages() != null && JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getImages().size() > 0){
                        ImageView mImageView = (ImageView) extras.findViewById(R.id.payment_img);
                        AQuery aq = new AQuery(mContext);
                        aq.id(mImageView).image(JumiaApplication.INSTANCE.getPaymentsInfoList().get(mItems.get(idx)).getImages().get(0), true, true, 0, 0, new BitmapAjaxCallback() {

                                    @Override
                                    public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                                        iv.setImageBitmap(bm);
                                        iv.setVisibility(View.VISIBLE); 
                                    }
                                });
                    }
                }

                final RadioButton button = (RadioButton) mInflater.inflate(
                        R.layout.form_radiobutton, null,
                        false);
                button.setId(idx);
                button.setText(mItems.get(idx));
                RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                button.setText(mItems.get(idx));
                if (idx == mDefaultSelected)
                    button.setChecked(true);
                button.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (button.isChecked()) {
                            extras.setVisibility(View.VISIBLE);
                        } else {
                            extras.setVisibility(View.GONE);
                        }
                        setSelection(mLinearLayout.getId());
                        mGroup.check(mLinearLayout.getId());
                    }
                });

                buttonContainer.addView(button, layoutParams);

                mGroup.addView(mLinearLayout);
            } else {

                Log.d(TAG,
                        "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));
                RadioButton button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton,
                        null,
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
        Log.i(TAG, "code1validate radioButtonId : " + radioButtonID + " idx : " + idx);
        return idx;
    }

    public String getItemByIndex(int idx) {
        if (mItems == null)
            return null;
        if (idx < 0)
            return null;
        return mItems.get(idx);
    }

    public void setSelection(int idx) {
        if (idx >= 0) {
            if (mGroup.getChildAt(idx) instanceof RadioButton) {
                RadioButton button = (RadioButton) mGroup.getChildAt(idx);
                button.setChecked(true);
            } else if (mGroup.getChildAt(idx).findViewById(R.id.radio_container).findViewById(idx) instanceof RadioButton) {
                RadioButton button = (RadioButton) mGroup.getChildAt(idx)
                        .findViewById(R.id.radio_container).findViewById(idx);
                button.setChecked(true);
            }
        }
    }
    
    public void setPaymentSelection(int idx) {
        if (idx >= 0) {
            if (mGroup.getChildAt(idx) instanceof RadioButton) {
                RadioButton button = (RadioButton) mGroup.getChildAt(idx);
                button.setChecked(true);
                setSelection(idx);
                mGroup.check(idx);
            } else if (mGroup.getChildAt(idx).findViewById(R.id.radio_container).findViewById(idx) instanceof RadioButton) {
                RadioButton button = (RadioButton) mGroup.getChildAt(idx)
                        .findViewById(R.id.radio_container).findViewById(idx);
                button.setChecked(true);
                setSelection(idx);
                mGroup.check(idx);
            }
        }
    }

    public boolean validateSelected() {
        boolean result = false;
        if (mGroup.getChildAt(mGroup.getCheckedRadioButtonId()) instanceof RadioButton) {
            result = true;
        } else if(!(mGroup.getChildAt(mGroup.getCheckedRadioButtonId()) instanceof RadioButton) && !generatedForms.containsKey(mGroup.getCheckedRadioButtonId())){
            result = true;
        } else {
            result = generatedForms.get(mGroup.getCheckedRadioButtonId()).validate();
        }
        return result;
    }

    public String getErrorMessage() {
        String result = mContext.getString(R.string.register_required_text);

        result = ((DynamicFormItem) generatedForms.get(mGroup.getCheckedRadioButtonId()).getItem(0))
                .getMessage();

        return result;
    }

    public ContentValues getSubFieldParameters() {
        ContentValues result = null;
        if (generatedForms != null && generatedForms.get(mGroup.getCheckedRadioButtonId()) != null) {
            result = generatedForms.get(mGroup.getCheckedRadioButtonId()).save();
        }

        return result;
    }

    public String getSelectedFieldName() {
        String result = mItems.get(mGroup.getCheckedRadioButtonId());
        return result;
    }

}
