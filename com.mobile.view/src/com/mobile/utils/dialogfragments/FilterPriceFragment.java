package com.mobile.utils.dialogfragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.mobile.components.RangeSeekBar;
import com.mobile.components.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.objects.CatalogFilter;
import com.mobile.framework.objects.CatalogFilterOption;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
class FilterPriceFragment extends Fragment implements OnRangeSeekBarChangeListener<Integer>, OnClickListener {

    private static final String TAG = FilterPriceFragment.class.getSimpleName();
    
    private static int mBackButtonId = R.id.dialog_filter_header_title;
    
    private static int mClearButtonId = R.id.dialog_filter_header_clear;
    
    private static int mCancelButtonId = R.id.dialog_filter_button_cancel;
    
    private static int mDoneButtonId = R.id.dialog_filter_button_done;

    private DialogFilterFragment mParent;

    private TextView mRangeValues;

    private CheckBox mDiscountBox;

    private CatalogFilter mPriceFilter;

    private int mMin;

    private int mMax;

    private int mCurrMinValue;

    private int mCurrMaxValue;

    private RangeSeekBar<Integer> mRangeBar;

    private int mInterval;

    /**
     * 
     * @param parent
     * @param bundle
     * @return
     */
    public static FilterPriceFragment newInstance(DialogFilterFragment parent, Bundle bundle) {
        Log.d(TAG, "NEW INSTANCE: PRICE");
        FilterPriceFragment frag = new FilterPriceFragment();
        frag.mParent = parent;
        frag.setArguments(bundle);
        return frag;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mPriceFilter = bundle.getParcelable(DialogFilterFragment.FILTER_TAG);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter_fragment_price, container, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get data from filter
        CatalogFilterOption filterOption = mPriceFilter.getFilterOption();
        // Get min and max
        mCurrMinValue = mMin = filterOption.getMin();
        mCurrMaxValue = mMax = filterOption.getMax();
        mInterval = filterOption.getInterval();
        Log.d(TAG, "FILTER RANGE: " + mMin + " " +  mMax + " " + mInterval);
        
        // Title
        ((TextView) view.findViewById(R.id.dialog_filter_header_title)).setText(mPriceFilter.getName());
        // Get back button
        view.findViewById(R.id.dialog_filter_header_title).setOnClickListener(this);
        // Get clear button
        view.findViewById(R.id.dialog_filter_header_clear).setOnClickListener(this);
        // Get cancel button
        view.findViewById(R.id.dialog_filter_button_cancel).setOnClickListener(this);
        // Get save button
        view.findViewById(R.id.dialog_filter_button_done).setOnClickListener(this);
        // Get check box
        mDiscountBox = (CheckBox) view.findViewById(R.id.dialog_filter_check_discount);
        // Get range text
        mRangeValues = (TextView) view.findViewById(R.id.dialog_filter_range_text);
        
        // Get range bar
        mRangeBar = new RangeSeekBar<Integer>(getMinIntervalValue(mMin), getMaxIntervalValue(mMax), getActivity());
        mRangeBar.setNotifyWhileDragging(true);
        mRangeBar.setOnRangeSeekBarChangeListener(this);
        ((ViewGroup) view.findViewById(R.id.dialog_filter_range_bar)).addView(mRangeBar);
        
        // Get range values from pre selection
        if(mPriceFilter.hasRangeValues()) {
            mCurrMinValue = mPriceFilter.getMinRangeValue();
            mCurrMaxValue = mPriceFilter.getMaxRangeValue();
        }
        // Set init range values
        mRangeBar.setSelectedMinValue(getMinIntervalValue(mCurrMinValue));
        mRangeBar.setSelectedMaxValue(getMaxIntervalValue(mCurrMaxValue));
        // Set current range
        mRangeValues.setText( mCurrMinValue + " - " + mCurrMaxValue);
        // Set discount box
        mDiscountBox.setChecked(mPriceFilter.isRangeWithDiscount());
        
        Log.d(TAG, "FILTER CURRENT RANGE: " + mCurrMinValue + " " +  mCurrMaxValue);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.components.RangeSeekBar.OnRangeSeekBarChangeListener#onRangeSeekBarValuesChanged(com.mobile.components.RangeSeekBar, java.lang.Object, java.lang.Object)
     */
    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
        mCurrMinValue = getMinRealValue(minValue);
        mCurrMaxValue = getMaxRealValue(maxValue);
        mRangeValues.setText( mCurrMinValue + " - " + mCurrMaxValue );
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Back && Cancel
        if(id == mBackButtonId || id == mCancelButtonId) mParent.allowBackPressed();
        // Clean
        else if(id == mClearButtonId) processOnClickClean();
        // Save
        else if(id == mDoneButtonId) processOnClickDone();
    }
    
    /**
     * Process the click on clean button
     * @author sergiopereira
     */
    private void processOnClickClean() {
        // Reset current values
        mCurrMinValue = mMin;
        mCurrMaxValue = mMax;
        // Reset range view
        mRangeBar.setSelectedMinValue(getMinIntervalValue(mCurrMinValue));
        mRangeBar.setSelectedMaxValue(getMaxIntervalValue(mCurrMaxValue));
        // Reset text
        mRangeValues.setText(mCurrMinValue + " - " + mCurrMaxValue);
        // Reset discount box
        mDiscountBox.setChecked(false);
        // Clean saved values
        mPriceFilter.cleanRangeValues();
        mPriceFilter.setRangeWithDiscount(false);
        Log.d(TAG, "FILTER: CLEAN " + mMin + " " + mMax + " " + mCurrMinValue + " " + mCurrMaxValue);
    }

    /**
     * Process the click on save button
     * @author sergiopereira
     */
    private void processOnClickDone() {
        Log.d(TAG, "FILTER: DONE " + mCurrMinValue + " " + mCurrMaxValue);
        // Validate current values
        if(getMinIntervalValue(mCurrMinValue) == getMinIntervalValue(mMin) && getMaxIntervalValue(mCurrMaxValue) == getMaxIntervalValue(mMax)) {
            // Clean saved values
            mPriceFilter.cleanRangeValues();
        } else {
            // Save current values
            mPriceFilter.setRangeValues(mCurrMinValue, mCurrMaxValue);
        }
        // Validate discount check
        mPriceFilter.setRangeWithDiscount(mDiscountBox.isChecked());
        // Goto back
        mParent.allowBackPressed();
    }
    
    /**
     * Get the max value using the respective interval
     * @param max
     * @return max interval value
     * @author sergiopereira
     */
    private int getMaxIntervalValue(int max){
        return (mInterval != 0) ? max / mInterval : max;   
    }

    /**
     * Get the min value using the respective interval
     * @param min
     * @return min interval value
     * @author sergiopereira
     */
    private int getMinIntervalValue(int min){
        return (mInterval != 0) ? min / mInterval : min;   
    }
    
    /**
     * Get the real min value using the respective interval
     * @param min
     * @return min real value
     * @author sergiopereira
     */
    private int getMinRealValue(int min){
        return (mInterval != 0) ? min * mInterval : min;   
    }
    
    /**
     * Get the real max value using the respective interval
     * @param min
     * @return min real value
     * @author sergiopereira
     */
    private int getMaxRealValue(int max){
        return (mInterval != 0) ? max * mInterval : max;   
    }

}
