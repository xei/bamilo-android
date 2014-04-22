package pt.rocket.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.holoeverywhere.widget.TextView;

import com.actionbarsherlock.internal.widget.IcsSpinner;

import pt.rocket.forms.ShippingMethodForm;
import pt.rocket.forms.ShippingMethodSubForm;
import pt.rocket.framework.objects.PickUpStationObject;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

public class ShippingRadioGroupList extends RadioGroup {
    private final static String TAG = LogTagHelper.create(ShippingRadioGroupList.class);

    public interface OnRadioGroupSelected {
        public void onRadioGroupItemSelected(int position);
    }

    public static final int NO_DEFAULT_SELECTION = -1;

    private ArrayList<String> mItems;
    private ShippingMethodForm mForm;

    private HashMap<String, ArrayList<ShippingMethodSubForm>> subForms;

    private String mDefaultSelected;
    private int mDefaultSelectedId;
    private RadioGroup mGroup;
    private LayoutInflater mInflater;
    Context mContext;

    public ShippingRadioGroupList(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ShippingRadioGroupList(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        mGroup = this;
    }

    public void setItems(ShippingMethodForm form, String defaultSelected) {
        Log.d(TAG, "setItems: items size = " + form.key + " defaultSelected = "
                + defaultSelected);
        mForm = form;
        mItems = mForm.options;
        mDefaultSelected = defaultSelected;
        subForms = new HashMap<String, ArrayList<ShippingMethodSubForm>>();
        updateRadioGroup();
    }

    private void updateRadioGroup() {

        try {
            mGroup.removeAllViews();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        for (int idx = 0; idx < mItems.size(); idx++) {

            Log.d(TAG, "updateRadioGroup: inserting idx = " + idx + " name = " + mItems.get(idx));

            /**
             * Global Container
             */
            final LinearLayout mLinearLayout = (LinearLayout) mInflater.inflate(
                    R.layout.form_radiobutton_with_extra, null,
                    false);

            /**
             * Button Container
             */
            final LinearLayout buttonContainer = (LinearLayout) mLinearLayout
                    .findViewById(R.id.radio_container);

            /**
             * Extras Container
             */
            final LinearLayout extras = (LinearLayout) mLinearLayout.findViewById(R.id.extras);
            
            RelativeLayout.LayoutParams mParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);

            ArrayList<ShippingMethodSubForm> tmpSubForms = new ArrayList<ShippingMethodSubForm>();

            /**
             * For each element verify if it has extras if so add them to the view
             */
            for (int i = 0; i < mForm.subForms.size(); i++) {

                Log.i(TAG, "code1generate subForms : " + mForm.subForms.get(i).scenario);

                if (mForm.subForms.get(i).scenario.equalsIgnoreCase(mItems.get(idx))) {
                    Log.i(TAG, "code1generate subForms : " + mForm.subForms.get(i).name);
                    tmpSubForms.add(mForm.subForms.get(i));

                    View mView = mForm.subForms.get(i).generateForm(mContext, mParams);
                    if (mView != null) {
                        extras.addView(mView);
                    }
                }
            }

            if (tmpSubForms != null && tmpSubForms.size() > 0) {
                subForms.put(mItems.get(idx), tmpSubForms);
            }

            mLinearLayout.setId(idx);

            final RadioButton button = (RadioButton) mInflater.inflate(R.layout.form_radiobutton,
                    null,
                    false);
            button.setId(idx);
            button.setText(mItems.get(idx));
            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            button.setText(mItems.get(idx));
            if (mItems.get(idx).equalsIgnoreCase(mDefaultSelected)) {
                button.setChecked(true);
                mDefaultSelectedId = idx;
            }
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (button.isChecked()) {
                        extras.setVisibility(View.VISIBLE);
                        if (subForms.get(mItems.get(v.getId())) != null) {
                            if (mItems.get(v.getId()).equalsIgnoreCase("pickupstation")) {
                                TextView title = (TextView) extras.findViewById(R.id.payment_text);
                                if(mForm.label != null){
                                    title.setText(mForm.label.trim());    
                                }
                                
                                title.setVisibility(View.VISIBLE);
                            }
                            for (ShippingMethodSubForm element : subForms.get(mItems.get(v.getId()))) {
                                ((ShippingMethodSubForm) element).dataControl
                                        .setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        extras.setVisibility(View.GONE);
                        if (subForms.get(mItems.get(v.getId())) != null) {
                            if (mItems.get(v.getId()).equalsIgnoreCase("pickupstation")) {
                                TextView title = (TextView) extras.findViewById(R.id.payment_text);
                                title.setVisibility(View.GONE);
                            }
                            for (ShippingMethodSubForm element : subForms.get(mItems.get(v.getId()))) {
                                ((ShippingMethodSubForm) element).dataControl
                                        .setVisibility(View.GONE);
                            }
                        }
                    }
                    setSelection(mLinearLayout.getId());
                    mGroup.check(mLinearLayout.getId());
                }
            });

            buttonContainer.addView(button, layoutParams);

            mGroup.addView(mLinearLayout);
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
            Log.i(TAG, "code1selection : id is : " + idx);
            if (mGroup.getChildAt(idx).findViewById(R.id.radio_container).findViewById(idx) instanceof RadioButton) {
                RadioButton button = (RadioButton) mGroup.getChildAt(idx)
                        .findViewById(R.id.radio_container).findViewById(idx);
                button.setChecked(true);
                Log.i(TAG, "code1selection : id is : " + idx + " second");
            }
            cleanOtherSelections(idx);
        }
    }
    
    
    public void setSubSelection(int groupId, int subId) throws NullPointerException, IndexOutOfBoundsException {
        if (subForms.containsKey(mItems.get(groupId)) && subForms.get(mItems.get(groupId)).size() > 0) {
            for (ShippingMethodSubForm element : subForms.get(mItems.get(groupId))) {
                if (element.options != null && element.options.size() > 0) {
                    ((IcsSpinner) element.dataControl).setSelection(subId);
                }
            }
        }
    }
    
    public int getSubSelection(int groupId) throws NullPointerException, IndexOutOfBoundsException {
        if (subForms.containsKey(mItems.get(groupId)) && subForms.get(mItems.get(groupId)).size() > 0) {
            for (ShippingMethodSubForm element : subForms.get(mItems.get(groupId))) {
                if (element.options != null && element.options.size() > 0) {
                    return ((IcsSpinner) element.dataControl).getSelectedItemPosition();
                }
            }
        }
        return -1;
    }

    private void cleanOtherSelections(int idx) {
        Log.i(TAG, "code1selection : id is : " + idx + " cleaning");
        for (int i = 0; i < mGroup.getChildCount(); i++) {
            if (i != idx) {
                if (mGroup.getChildAt(i).findViewById(R.id.radio_container).findViewById(i) instanceof RadioButton) {
                    RadioButton button = (RadioButton) mGroup.getChildAt(i)
                            .findViewById(R.id.radio_container).findViewById(i);
                    button.setChecked(false);
                    mGroup.getChildAt(i).findViewById(R.id.extras).setVisibility(View.GONE);
                    Log.i(TAG, "code1selection : id is : " + idx + " cleaning 2 : " + i);
                }
            }
        }
    }

    public boolean validateSelected() {
        boolean result = false;
        mGroup.getCheckedRadioButtonId();

        return result;
    }

    // public String getErrorMessage(){
    // String result = mContext.getString(R.string.register_required_text);
    //
    // result = ((DynamicFormItem)
    // generatedForms.get(mGroup.getCheckedRadioButtonId()).getItem(0)).getMessage();
    //
    // return result;
    // }
    //
    // public ContentValues getSubFieldParameters(){
    // ContentValues result = null;
    // if(generatedForms != null && generatedForms.get(mGroup.getCheckedRadioButtonId()) != null){
    // result = generatedForms.get(mGroup.getCheckedRadioButtonId()).save();
    // }
    //
    //
    // return result;
    // }
    //
    public String getSelectedFieldName() {
        String result = null;
        if (mGroup.getCheckedRadioButtonId() >= 0) {
            result = mItems.get(mGroup.getCheckedRadioButtonId());
        } else {
            result = mItems.get(mDefaultSelectedId);
        }

        return result;
    }

    public ContentValues getValues() {
        Log.i(TAG, "code1values : adding valeus " + subForms.toString());
        ContentValues mContentValues = new ContentValues();
        int idx = mGroup.getCheckedRadioButtonId();
        if (idx < 0) {
            idx = mDefaultSelectedId;
        }

        if (subForms.containsKey(mItems.get(idx)) && subForms.get(mItems.get(idx)).size() > 0) {
            PickUpStationObject selectedPickup = null;
            Log.i(TAG, "code1values : adding ");
            for (ShippingMethodSubForm element : subForms.get(mItems.get(idx))) {
                if (element.options != null && element.options.size() > 0) {
                    selectedPickup = element.options.get(((IcsSpinner) element.dataControl)
                            .getSelectedItemPosition());
                    mContentValues.put(
                            element.name,
                            element.options.get(
                                    ((IcsSpinner) element.dataControl).getSelectedItemPosition())
                                    .getPickupId());
                    Log.i(TAG, "code1values : element.name : " + element.name);
                } else {
                    if (selectedPickup.getRegions() != null
                            && selectedPickup.getRegions().size() > 0) {
                        mContentValues
                                .put(element.name, selectedPickup.getRegions().get(0).getId());
                    }
                }
            }
        }
        return mContentValues;

    }

}
