package pt.rocket.utils.dialogfragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.holoeverywhere.widget.DatePicker;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 
 * @author sergiopereira
 *
 */
public class DialogDatePickerFragment extends DialogFragment implements OnClickListener {
    
    private final static String TAG = LogTagHelper.create(DialogDatePickerFragment.class);
    
    private String mTitle;
    private String mId;
    private Activity mActivity;
    private OnDatePickerDialogListener mListener;
    //private Dialog mDialog;
    private DatePicker mDatePicker;
    private int mDay;
    private int mMonth;
    private int mYear;

    private Boolean isSetOnce = false;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public interface OnDatePickerDialogListener {
        public void onDatePickerDialogSelect(String id, int year, int month, int day);

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
        Log.d(TAG, "NEW INSTANCE");
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
        Log.d(TAG, "NEW INSTANCE");
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
        setStyle(R.style.Theme_Jumia_Dialog_Blue_NoTitle_DatePicker, R.style.Theme_Jumia_Dialog_Blue_NoTitle_DatePicker);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.dialog_datepicker_content, container);
        
        TextView titleView = (TextView) view.findViewById(R.id.title);
        titleView.setText(this.mTitle);

        this.mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        Log.d(TAG, "show: year = " + mYear + " month = " + mMonth + " day = " + mDay);
        if (this.mYear != 0)
            mDatePicker.updateDate(this.mYear, this.mMonth, this.mDay);

        view.findViewById(R.id.button1).setOnClickListener(this);
        view.findViewById(R.id.button2).setOnClickListener(this);

//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
//            resizeDialog(this);
        
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
            mDay = mDatePicker.getDayOfMonth();
            mMonth = mDatePicker.getMonth();
            mYear = mDatePicker.getYear();
            if (mListener != null) {
                mListener.onDatePickerDialogSelect(mId, mYear, mMonth, mDay);
            }
            this.dismiss();
        }
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
            Log.d(TAG, "setDate: cant parse date: " + dateString);
            return;
        }
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        if (mDatePicker != null)
            mDatePicker.updateDate(mYear, mMonth, mDay);
    }

    public void setDate(int year, int month, int day) {
        mYear = year;
        mMonth = month;
        mDay = day;

        if (mDatePicker != null)
            mDatePicker.updateDate(mYear, mMonth, mDay);
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
        this.dismiss();
    }
}
