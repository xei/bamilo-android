package com.mobile.utils.dialogfragments;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
//import org.holoeverywhere.widget.DatePicker;

/**
 *
 * @author sergiopereira
 *
 */
public class DialogDatePickerFragment extends DialogFragment implements OnClickListener {

    private final static String TAG = DialogDatePickerFragment.class.getSimpleName();

    private String mTitle;
    private String mId;
    private Activity mActivity;
    private OnDatePickerDialogListener mListener;
    //private Dialog mDialog;
    private static DatePicker mDatePicker;  //mobapi 1.8 change
    private int mDay;
    private int mMonth;
    private int mYear;

    private Boolean isSetOnce = false;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public interface OnDatePickerDialogListener {
        void onDatePickerDialogSelect(int year, int month, int day);

    }

    /**
     * Empty Constructor
     */
    public DialogDatePickerFragment() { }


    /**
     *
     * @param activity
     * @param id
     * @param title
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static DialogDatePickerFragment newInstance(Activity activity, String id, String title, int year, int month, int day){
        Print.d(TAG, "NEW INSTANCE");
        DialogDatePickerFragment dialogDatePickerFragment = new DialogDatePickerFragment();
        dialogDatePickerFragment.mActivity = activity;
        if (dialogDatePickerFragment.mActivity instanceof OnDatePickerDialogListener) {
            dialogDatePickerFragment.mListener = (OnDatePickerDialogListener) dialogDatePickerFragment.mActivity;
        }
        dialogDatePickerFragment.mId = id;
        dialogDatePickerFragment.mTitle = title;
        dialogDatePickerFragment.mDay = day;
        dialogDatePickerFragment.mMonth = month;
        dialogDatePickerFragment.mYear = year;
        return dialogDatePickerFragment;
    }


    /**
     *
     * @param activity
     * @param listener
     * @param id
     * @param title
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static DialogDatePickerFragment newInstance(Activity activity, OnDatePickerDialogListener listener, String id, String title, int year, int month, int day){
        Print.d(TAG, "NEW INSTANCE");
        DialogDatePickerFragment dialogDatePickerFragment = new DialogDatePickerFragment();
        dialogDatePickerFragment.mActivity = activity;
        dialogDatePickerFragment.mListener = listener;
        dialogDatePickerFragment.mId = id;
        dialogDatePickerFragment.mTitle = title;
        dialogDatePickerFragment.mDay = day;
        dialogDatePickerFragment.mMonth = month;
        dialogDatePickerFragment.mYear = year;
        return dialogDatePickerFragment;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.Theme_Jumia_Dialog_NoTitle_DatePicker, R.style.Theme_Jumia_Dialog_NoTitle_DatePicker);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.dialog_datepicker_content, container);

        TextView titleView = (TextView) view.findViewById(R.id.datepicker_title);
        titleView.setText(this.mTitle);

        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);    //mobapi 1.8 change: date fields with picker
        if (this.mYear != 0){
            mDatePicker.updateDate(this.mYear, this.mMonth, this.mDay);
            Calendar mCal =Calendar.getInstance();

            int currentYear = mCal.get(Calendar.YEAR) - 1;
            int currentMonth = mCal.get(Calendar.MONTH);
            int currentDay = mCal.get(Calendar.DAY_OF_MONTH);

            mDatePicker.setMaxDate(new GregorianCalendar(currentYear, currentMonth, currentDay).getTimeInMillis());
        } else {
            Calendar cal=Calendar.getInstance();

            this.mYear = cal.get(Calendar.YEAR) - 1;
            this.mMonth = cal.get(Calendar.MONTH);
            this.mDay = cal.get(Calendar.DAY_OF_MONTH);

            mDatePicker.updateDate(this.mYear, this.mMonth, this.mDay);
            mDatePicker.setMaxDate(new GregorianCalendar(this.mYear, this.mMonth, this.mDay).getTimeInMillis());
        }

        view.findViewById(R.id.button1).setOnClickListener(this);
        view.findViewById(R.id.button2).setOnClickListener(this);

        return view;
    }

    /**
     *
     * @return
     */
    public boolean isSetOnce() {
        return isSetOnce;
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button1) {
            this.dismiss();
        } else if (id == R.id.button2) {
            isSetOnce = true;
            mDay = mDatePicker.getDayOfMonth(); //mobapi 1.8 change uncomment
            mMonth = mDatePicker.getMonth();
            mYear = mDatePicker.getYear();
            if (mListener != null) {
                mListener.onDatePickerDialogSelect(mYear, mMonth, mDay);
            }
            this.dismiss();
        }
    }

    public int getYear(){
        return mYear;
    }

    public int getMonth(){
        return mMonth;
    }

    public int getDay(){
        return mDay;
    }

    public void setDate(String dateString) {

        Date date;
        try {
            if(dateString==null){
                dateString = "2013-01-01";
            }
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            // if the date has the wrong format
            // there cant be more done
            Print.d(TAG, "setDate: cant parse date: " + dateString);
            return;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

//        if (mDatePicker != null){
//            mDatePicker.updateDate(mYear, mMonth, mDay);
//            Calendar mCal =Calendar.getInstance();
//
//            int currentYear = mCal.get(Calendar.YEAR) - 1;
//            int currentMonth = mCal.get(Calendar.MONTH);
//            int currentDay = mCal.get(Calendar.DAY_OF_MONTH);
//
//            mDatePicker.setMaxDate(new GregorianCalendar(currentYear, currentMonth, currentDay).getTimeInMillis());
//        }
        isSetOnce = true;
    }

    public void setDate(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;

//        if (mDatePicker != null){
//            mDatePicker.updateDate(mYear, mMonth, mDay);
//            Calendar mCal =Calendar.getInstance();
//
//            int currentYear = mCal.get(Calendar.YEAR) - 1;
//            int currentMonth = mCal.get(Calendar.MONTH);
//            int currentDay = mCal.get(Calendar.DAY_OF_MONTH);
//
//            mDatePicker.setMaxDate(new GregorianCalendar(currentYear, currentMonth, currentDay).getTimeInMillis());
//        }
    }

    public String getDate() {
        if (mYear == 0)
            return null;

        Calendar cal = new GregorianCalendar(mYear, mMonth, mDay);
        return sdf.format(cal.getTime());
    }

    @SuppressWarnings("deprecation")
    public void resizeDialog(DialogDatePickerFragment dialog) {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getActivity().getWindow().getAttributes());
        lp.width = (int) (dialog.getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth() * 0.9f);
        lp.horizontalMargin = 0;
        lp.verticalMargin = 0;
        dialog.getActivity().getWindow().setAttributes(lp);
        dialog.getActivity().getWindow().setBackgroundDrawable(new ColorDrawable(0));

    }


    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }
}
