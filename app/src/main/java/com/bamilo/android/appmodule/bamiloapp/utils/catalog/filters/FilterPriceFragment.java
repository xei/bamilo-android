package com.bamilo.android.appmodule.bamiloapp.utils.catalog.filters;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bamilo.android.appmodule.modernbamilo.customview.XeiEditText;
import com.bamilo.android.framework.components.RangeSeekBar;
import com.bamilo.android.framework.components.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.bamilo.android.framework.components.customfontviews.EditText;
import android.widget.TextView;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogPriceFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogPriceFilterOption;
import com.bamilo.android.framework.service.utils.NumberTextWatcher;
import com.bamilo.android.framework.service.utils.convertor.PersinConvertor;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.FilterMainFragment;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterPriceFragment extends FilterFragment implements OnRangeSeekBarChangeListener<Long> {

    private static final String TAG = FilterPriceFragment.class.getSimpleName();

    private TextView mRangeValues;
    private XeiEditText mMinValueTxt;
    private XeiEditText mMaxValueTxt;

    private long mMin;

    private long mMax;

    private long mCurrMinValue ,mMinRang;

    private long mCurrMaxValue, mMaxRang;

    private RangeSeekBar<Long> mRangeBar;

    private long mInterval;

    private CatalogPriceFilter mFilter;

    public static FilterPriceFragment newInstance(Bundle bundle) {
        FilterPriceFragment frag = new FilterPriceFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mCatalogFilter = bundle.getParcelable(FilterMainFragment.FILTER_TAG);
        mFilter = (CatalogPriceFilter)mCatalogFilter;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.filter_fragment_price, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get data from filter
        CatalogPriceFilterOption filterOption = mFilter.getOption();
        // Get min and max
        mMin = filterOption.getMin();
        mCurrMinValue = filterOption.getRangeMin();
        mMax = filterOption.getMax();
        mCurrMaxValue = filterOption.getRangeMax();
        mInterval = filterOption.getInterval();

        // Get check box
        mMinValueTxt = view.findViewById(R.id.min);
        mMaxValueTxt = view.findViewById(R.id.max);

        mMinValueTxt.addTextChangedListener(new NumberTextWatcher(mMinValueTxt));
        mMaxValueTxt.addTextChangedListener(new NumberTextWatcher(mMaxValueTxt));

        mMaxValueTxt.setOnFocusChangeListener((v, hasFocus) -> {
            Long mymin= Long.parseLong(PersinConvertor.toEnglishNumber(mMinValueTxt.getText().toString()).replaceAll(",","").replaceAll("٬",""));
            Long mymax= Long.parseLong(PersinConvertor.toEnglishNumber(mMaxValueTxt.getText().toString()).replaceAll(",","").replaceAll("٬",""));
            if(!hasFocus){
                if(mymax>mymin){

                if (mymax<=mMaxRang &&Integer.MAX_VALUE>mymax) {

                    setIntervalText(mCurrMaxValue,(int) mymax.longValue());
                    mRangeBar.setSelectedMaxValue(getMaxIntervalValue(mymax.longValue()));
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
                    mMaxValueTxt.setText(String.valueOf(mCurrMaxValue));
                    mFilter.getOption().setRangeMin(mCurrMinValue);
                    mFilter.getOption().setRangeMax(mCurrMaxValue);
                }
        }});

        mMinValueTxt.setOnFocusChangeListener((v, hasFocus) -> {
            Long mymin;
            mymin = Long.parseLong(PersinConvertor.toEnglishNumber(mMinValueTxt.getText().toString()).replaceAll(",","").replaceAll("٬",""));
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
                    mMinValueTxt.setText(String.valueOf(mCurrMinValue));
                }
            }
        });

        // Get range text
        mRangeValues = view.findViewById(R.id.dialog_filter_range_text);
        
        // Get range bar
        mRangeBar = new RangeSeekBar<>(getMinIntervalValue(mMin), getMaxIntervalValue(mMax), getActivity());
        mRangeBar.setNotifyWhileDragging(true);
        mRangeBar.setOnRangeSeekBarChangeListener(this);
        ((ViewGroup) view.findViewById(R.id.dialog_filter_range_bar)).addView(mRangeBar);
        
        // Get range values from pre selection
        mMinRang = filterOption.getMin();
        mMaxRang = filterOption.getMax();
        mCurrMinValue = filterOption.getRangeMin();
        mCurrMaxValue = filterOption.getRangeMax();
        mMinValueTxt.setText(String.valueOf(mCurrMinValue));
        mMaxValueTxt.setText(String.valueOf(mCurrMaxValue));
//        }
        // Set init range values
        mRangeBar.setSelectedMinValue(getMinIntervalValue(mCurrMinValue));
        mRangeBar.setSelectedMaxValue(getMaxIntervalValue(mCurrMaxValue));

        // Set current range
        setIntervalText(mCurrMinValue, mCurrMaxValue);
        // Set discount box
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Long minValue, Long maxValue) {
        mCurrMinValue = getMinRealValue(minValue);
        mCurrMaxValue = getMaxRealValue(maxValue);
        setIntervalText(mCurrMinValue, mCurrMaxValue);
        mMinValueTxt.setText(String.valueOf(mCurrMinValue));
        mMaxValueTxt.setText(String.valueOf(mCurrMaxValue));
        processOnClickDone();
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
        setIntervalText(mCurrMinValue, mCurrMaxValue);

        // Clean saved values
        mFilter.cleanFilter();
    }

    /**
     * Process the click on save button
     * @author sergiopereira
     */
    private void processOnClickDone() {
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
    private long getMaxIntervalValue(long max){
        return (mInterval != 0) ? max / mInterval : max;   
    }

    /**
     * Get the min value using the respective interval
     * @param min
     * @return min interval value
     * @author sergiopereira
     */
    private long getMinIntervalValue(long min){
        return (mInterval != 0) ? min / mInterval : min;   
    }
    
    /**
     * Get the real min value using the respective interval
     * @param min
     * @return min real value
     * @author sergiopereira
     */
    private long getMinRealValue(long min){
        return (mInterval != 0) ? min * mInterval : min;   
    }
    
    /**
     * Get the real max value using the respective interval
     * @param max
     * @return min real value
     * @author sergiopereira
     */
    private long getMaxRealValue(long max){
        return (mInterval != 0) ? max * mInterval : max;   
    }

    @Override
    public void cleanValues() {
        processOnClickClean();
    }

    protected void setIntervalText(@IntRange(from = 0) long minValue, long maxValue) {
        mRangeValues.setText(CurrencyFormatter.formatCurrencyPattern(minValue + " - " + maxValue));
    }

    @Override
    public void onDestroyView() {
        mMaxValueTxt.clearFocus();
        mMinValueTxt.clearFocus();
        super.onDestroyView();
    }

    public void clearFilterValues() {
        mMaxValueTxt.clearFocus();
        mMinValueTxt.clearFocus();
        mMinValueTxt.setText(String.valueOf(mMinRang));
        mMaxValueTxt.setText(String.valueOf(mMaxRang));
    }
}
