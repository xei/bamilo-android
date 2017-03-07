package com.mobile.utils.catalog.filters;

import android.os.Bundle;
import android.support.annotation.IntRange;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.mobile.components.RangeSeekBar;
import com.mobile.components.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.EditText;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.catalog.filters.CatalogPriceFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogPriceFilterOption;
import com.mobile.newFramework.utils.NumberTextWatcher;
import com.mobile.newFramework.utils.PersinConvertor;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.Toast;
import com.mobile.view.R;
import com.mobile.view.fragments.FilterMainFragment;

import java.text.DecimalFormat;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterPriceFragment extends FilterFragment implements OnRangeSeekBarChangeListener<Integer> {

    private static final String TAG = FilterPriceFragment.class.getSimpleName();

    private TextView mRangeValues;
    public EditText mMinValueTxt;
    public EditText mMaxValueTxt;

    private CheckBox mDiscountBox;

    private int mMin;

    private int mMax;

    public int mCurrMinValue ,mMinRang;

    public int mCurrMaxValue, mMaxRang;

    private RangeSeekBar<Integer> mRangeBar;

    private int mInterval;

    private CatalogPriceFilter mFilter;

    /**
     *
     * @param bundle
     * @return
     */
    public static FilterPriceFragment newInstance(Bundle bundle) {
        Print.d(TAG, "NEW INSTANCE: BRAND");
        FilterPriceFragment frag = new FilterPriceFragment();
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
        mCatalogFilter = bundle.getParcelable(FilterMainFragment.FILTER_TAG);
        mFilter = (CatalogPriceFilter)mCatalogFilter;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_fragment_price, container, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get data from filter
        CatalogPriceFilterOption filterOption = mFilter.getOption();
        // Get min and max
        mCurrMinValue = mMin = filterOption.getMin();
        mCurrMaxValue = mMax = filterOption.getMax();
        mInterval = filterOption.getInterval();
        Print.d(TAG, "FILTER RANGE: " + mMin + " " + mMax + " " + mInterval);

        // Get check box
        mDiscountBox = (CheckBox) view.findViewById(R.id.dialog_filter_check_discount);
        mMinValueTxt = (EditText) view.findViewById(R.id.min);
        mMaxValueTxt = (EditText) view.findViewById(R.id.max);

        mMinValueTxt.addTextChangedListener(new NumberTextWatcher(mMinValueTxt));
        mMaxValueTxt.addTextChangedListener(new NumberTextWatcher(mMaxValueTxt));


        mMaxValueTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Long mymin= Long.parseLong(PersinConvertor.toEnglishNumber(mMinValueTxt.getText().toString()).replaceAll(",","").replaceAll("٬",""));
                Long mymax= Long.parseLong(PersinConvertor.toEnglishNumber(mMaxValueTxt.getText().toString()).replaceAll(",","").replaceAll("٬",""));
                if(!hasFocus){
                    if(mymax>mymin){


                    if (mymax<=mMaxRang &&Integer.MAX_VALUE>mymax) {

                        //mMaxValueTxt.setText(PersinConvertor.toPersianNumber(mMaxValueTxt.getText().toString()).toString());
                        setIntervalText(mCurrMaxValue,(int) mymax.longValue());
                        mRangeBar.setSelectedMaxValue(getMaxIntervalValue((int) mymax.longValue()));
                        mCurrMaxValue =mymax.intValue() ;
                        mFilter.getOption().setRangeMin(mCurrMinValue);
                        mFilter.getOption().setRangeMax(mCurrMaxValue);
                    }
                    else {
                        mCurrMaxValue = mMax;
                        mFilter.getOption().setRangeMin(mCurrMinValue);
                        mFilter.getOption().setRangeMax(mCurrMaxValue);
                    }
                }
                    else{
                        mRangeBar.setSelectedMaxValue(getMaxIntervalValue(mCurrMaxValue));
                        mMaxValueTxt.setText(mCurrMaxValue+"");
                        mFilter.getOption().setRangeMin(mCurrMinValue);
                        mFilter.getOption().setRangeMax(mCurrMaxValue);
                    }
            }
            }
        });

        mMinValueTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Long mymin , mintemp;
                mymin =mintemp= Long.parseLong(PersinConvertor.toEnglishNumber(mMinValueTxt.getText().toString()).replaceAll(",","").replaceAll("٬",""));
                Long mymax= Long.parseLong(PersinConvertor.toEnglishNumber(mMaxValueTxt.getText().toString()).replaceAll(",","").replaceAll("٬",""));
                if(!hasFocus){
                    if(mymax>mymin) {
                        if (mymin <= mMaxRang && Integer.MAX_VALUE > mymin) {
                            setIntervalText((int) mymin.longValue(), mCurrMaxValue);
                            mRangeBar.setSelectedMinValue(getMaxIntervalValue((int) mymin.longValue()));
                            mCurrMinValue = mymin.intValue();
                            mFilter.getOption().setRangeMin(mCurrMinValue);
                            mFilter.getOption().setRangeMax(mCurrMaxValue);
                        } else {
                            mCurrMinValue = mMin;
                            mFilter.getOption().setRangeMin(mCurrMinValue);
                            mFilter.getOption().setRangeMax(mCurrMaxValue);
                        }
                    }
                    else
                    {
                        mRangeBar.setSelectedMinValue(getMinIntervalValue(mCurrMinValue));
                        mFilter.getOption().setRangeMin(mCurrMinValue);
                        mFilter.getOption().setRangeMax(mCurrMaxValue);
                        mMinValueTxt.setText(mCurrMinValue+"");

                    }
                }

            }
        });

        // Get range text
        mRangeValues = (TextView) view.findViewById(R.id.dialog_filter_range_text);
        
        // Get range bar
        mRangeBar = new RangeSeekBar<>(getMinIntervalValue(mMin), getMaxIntervalValue(mMax), getActivity());
        mRangeBar.setNotifyWhileDragging(true);
        mRangeBar.setOnRangeSeekBarChangeListener(this);
        ((ViewGroup) view.findViewById(R.id.dialog_filter_range_bar)).addView(mRangeBar);
        
        // Get range values from pre selection
//        if(mFilter.hasRangeValues()) {
        mMinRang = mCurrMinValue = filterOption.getRangeMin();
        mMaxRang = mCurrMaxValue = filterOption.getRangeMax();
        mMinValueTxt.setText(( mCurrMinValue)+"");
        mMaxValueTxt.setText(mCurrMaxValue+"");
//        }
        // Set init range values
        mRangeBar.setSelectedMinValue(getMinIntervalValue(mCurrMinValue));
        mRangeBar.setSelectedMaxValue(getMaxIntervalValue(mCurrMaxValue));

        // Set current range
        setIntervalText(mCurrMinValue, mCurrMaxValue);
        // Set discount box
//        mDiscountBox.setChecked(mFilter.isRangeWithDiscount());
        
        Print.d(TAG, "FILTER CURRENT RANGE: " + mCurrMinValue + " " + mCurrMaxValue);
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.components.RangeSeekBar.OnRangeSeekBarChangeListener#onRangeSeekBarValuesChanged(com.mobile.components.RangeSeekBar, java.lang.Object, java.lang.Object)
     */
    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {
        mCurrMinValue = getMinRealValue(minValue);
        mCurrMaxValue = getMaxRealValue(maxValue);
        setIntervalText(mCurrMinValue, mCurrMaxValue);
        mMinValueTxt.setText(mCurrMinValue+"");
        mMaxValueTxt.setText(mCurrMaxValue+"");
        processOnClickDone();
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
//    @Override
//    public void onClick(View view) {
//         Get view id
//        int id = view.getId();
        // Back && Cancel
//        if(id == mBackButtonId || id == mCancelButtonId) mParent.allowBackPressed();
//        // Clean
//        else if(id == mClearButtonId) processOnClickClean();
//        // Save
//        else if(id == mDoneButtonId) processOnClickDone();
//    }
    
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
        setIntervalText(mCurrMinValue, mCurrMaxValue);

        // Clean saved values
        mFilter.cleanFilter();
//        mFilter.setRangeWithDiscount(false);
        Print.d(TAG, "FILTER: CLEAN " + mMin + " " + mMax + " " + mCurrMinValue + " " + mCurrMaxValue);
    }

    /**
     * Process the click on save button
     * @author sergiopereira
     */
    private void processOnClickDone() {
        Print.d(TAG, "FILTER: DONE " + mCurrMinValue + " " + mCurrMaxValue);
        // Validate current values
        if(getMinIntervalValue(mCurrMinValue) == getMinIntervalValue(mMin) && getMaxIntervalValue(mCurrMaxValue) == getMaxIntervalValue(mMax)) {
            // Clean saved values
            mFilter.cleanFilter();
        } else {
            // Save current values
            mFilter.getOption().setRangeMin(mCurrMinValue);
            mFilter.getOption().setRangeMax(mCurrMaxValue);
        }
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
     * @param max
     * @return min real value
     * @author sergiopereira
     */
    private int getMaxRealValue(int max){
        return (mInterval != 0) ? max * mInterval : max;   
    }

    @Override
    public void cleanValues() {
        processOnClickClean();
    }

    protected void setIntervalText(@IntRange(from = 0) int minValue, int maxValue) {
        mRangeValues.setText(CurrencyFormatter.formatCurrencyPattern(minValue + " - " + maxValue));
    }
}
