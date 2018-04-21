package com.mobile.utils.dialogfragments;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.mobile.components.customfontviews.HoloFontLoader;
import com.mobile.utils.JalaliCalendar;
import com.mobile.view.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by mohsen on 3/5/18.
 */

public class PersianDatePickerDialogHelper {
    private AlertDialog mDialog;
    private View mRootView;
    private JalaliCalendar.YearMonthDate selectedDate;
    private Integer upperBoundYear, lowerBoundYear;
    private boolean viewInitialized = false;
    private OnDateSelectedListener onDateSelectedListener;

    private PersianDatePickerDialogHelper(AlertDialog dialog, View rootView,
                                          JalaliCalendar.YearMonthDate selectedDate) {
        this.mDialog = dialog;
        this.mRootView = rootView;
        this.selectedDate = selectedDate;
    }

    public static PersianDatePickerDialogHelper newInstance(@NonNull Context context,
                                                            @Nullable JalaliCalendar.YearMonthDate selectedDate,
                                                            @Nullable Integer upperBoundYear,
                                                            @Nullable Integer lowerBoundYear) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_date_picker, null);
        HoloFontLoader.applyDefaultFont(dialogView);

        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        dialog = builder.create();

        Calendar now = Calendar.getInstance(Locale.US);
        JalaliCalendar.YearMonthDate gregDate = new JalaliCalendar.YearMonthDate(now.get(Calendar.YEAR),
                now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        JalaliCalendar.YearMonthDate jalaliNow = JalaliCalendar.gregorianToJalali(gregDate);

        if (selectedDate == null) {
            selectedDate = jalaliNow;
        }
        PersianDatePickerDialogHelper helper = new PersianDatePickerDialogHelper(dialog, dialogView, selectedDate);
        if (upperBoundYear != null) {
            helper.upperBoundYear = upperBoundYear;
        } else {
            helper.upperBoundYear = jalaliNow.getYear();
        }

        if (lowerBoundYear != null) {
            helper.lowerBoundYear = lowerBoundYear;
        } else {
            helper.lowerBoundYear = jalaliNow.getYear() - 100;
        }

        return helper;
    }

    public void showDialog() {
        if (!viewInitialized) {
            initView();
        }

        mDialog.show();
    }

    private void initView() {
        final NumberPicker nbYear, nbMonth, nbDay;
        nbYear = (NumberPicker) mRootView.findViewById(R.id.nbYear);
        nbMonth = (NumberPicker) mRootView.findViewById(R.id.nbMonth);
        nbDay = (NumberPicker) mRootView.findViewById(R.id.nbDay);

        nbYear.setMinValue(lowerBoundYear);
        nbYear.setMaxValue(upperBoundYear);
        nbYear.setValue(selectedDate.getYear());

        nbMonth.setMinValue(0);
        nbMonth.setMaxValue(11);
        nbMonth.setDisplayedValues(nbMonth.getResources().getStringArray(R.array.jalali_month_names));
        nbMonth.setValue(selectedDate.getMonth());

        nbDay.setMinValue(1);
        if (selectedDate.getMonth() < 11) {
            nbDay.setMaxValue(JalaliCalendar.jalaliDaysInMonth[selectedDate.getMonth()]);
        } else {
            if (JalaliCalendar.isLeepYear(selectedDate.getYear())) {
                nbDay.setMaxValue(30);
            } else {
                nbDay.setMaxValue(29);
            }
        }
        nbDay.setValue(selectedDate.getDate());

        nbMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                if (newValue < 11) {
                    nbDay.setMaxValue(JalaliCalendar.jalaliDaysInMonth[newValue]);
                } else {
                    if (JalaliCalendar.isLeepYear(nbYear.getValue())) {
                        nbDay.setMaxValue(30);
                    } else {
                        nbDay.setMaxValue(29);
                    }
                }
            }
        });
        nbYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                if (nbMonth.getValue() == 11) {
                    if (JalaliCalendar.isLeepYear(newValue)) {
                        nbDay.setMaxValue(30);
                    } else {
                        nbDay.setMaxValue(29);
                    }
                }
            }
        });

        mRootView.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDateSelectedListener != null) {
                    onDateSelectedListener.onDateSelected(nbYear.getValue(), nbMonth.getValue() + 1, nbDay.getValue());
                }
                mDialog.dismiss();
            }
        });
        viewInitialized = true;
    }

    public JalaliCalendar.YearMonthDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(JalaliCalendar.YearMonthDate selectedDate) {
        this.selectedDate = selectedDate;
        viewInitialized = false;
    }

    public Integer getUpperBoundYear() {
        return upperBoundYear;
    }

    public void setUpperBoundYear(Integer upperBoundYear) {
        this.upperBoundYear = upperBoundYear;
        viewInitialized = false;
    }

    public Integer getLowerBoundYear() {
        return lowerBoundYear;
    }

    public void setLowerBoundYear(Integer lowerBoundYear) {
        this.lowerBoundYear = lowerBoundYear;
        viewInitialized = false;
    }

    public OnDateSelectedListener getOnDateSelectedListener() {
        return onDateSelectedListener;
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(int year, int month, int day);
    }
}
