package pt.rocket.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.holoeverywhere.widget.DatePicker;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import de.akquinet.android.androlog.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

@SuppressLint("SimpleDateFormat")
public class DialogDatePicker implements OnClickListener {
	private final static String TAG = LogTagHelper.create(DialogDatePicker.class);
	private String mTitle;
	private String mId;
	private Activity mActivity;
	private OnDatePickerDialogListener mListener;
	private Dialog mDialog;
	private DatePicker mDatePicker;
	private int mDay;
	private int mMonth;
	private int mYear;

	private Boolean isSetOnce = false;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public interface OnDatePickerDialogListener {
		public void onDatePickerDialogSelect(String id, int year, int month, int day);

	}

	public DialogDatePicker(Activity activity, String id, String title, int year, int month, int day) {
		mActivity = activity;
		if (mActivity instanceof OnDatePickerDialogListener) {
			mListener = (OnDatePickerDialogListener) mActivity;
		}

		mId = id;
		mTitle = title;
		mDay = day;
		mMonth = month;
		mYear = year;
	}

	public DialogDatePicker(Activity activity, OnDatePickerDialogListener listener, String id,
			String title, int year, int month, int day) {
		mActivity = activity;
		mListener = listener;

		mId = id;
		mTitle = title;
		mDay = day;
		mMonth = month;
		mYear = year;
	}

	public void show() {
		mDialog = new Dialog(mActivity, R.style.Theme_Jumia_Dialog_Blue_NoTitle_DatePicker);
		mDialog.setContentView(R.layout.dialog_datepicker_content);

		TextView titleView = (TextView) mDialog.findViewById(R.id.title);
		titleView.setText(mTitle);

		mDatePicker = (DatePicker) mDialog.findViewById(R.id.datePicker);
		Log.d(TAG, "show: year = " + mYear + " month = " + mMonth + " day = " + mDay);
		if (mYear != 0)
			mDatePicker.updateDate(mYear, mMonth, mDay);

		mDialog.findViewById(R.id.button1).setOnClickListener(this);
		mDialog.findViewById(R.id.button2).setOnClickListener(this);

		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1)
			resizeDialog(mDialog);
		mDialog.show();
	}

	public boolean isSetOnce() {
		return isSetOnce;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.button1) {
			mDialog.dismiss();
		} else if (id == R.id.button2) {
			isSetOnce = true;
			mDay = mDatePicker.getDayOfMonth();
			mMonth = mDatePicker.getMonth();
			mYear = mDatePicker.getYear();
			if (mListener != null) {
				mListener.onDatePickerDialogSelect(mId, mYear, mMonth, mDay);
			}
			mDialog.dismiss();
		}
	}

	public void setDate(String dateString) {

		Date date;
		try {
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
	public void resizeDialog(Dialog dialog) {
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = (int) (dialog.getWindow().getWindowManager().getDefaultDisplay().getWidth() * 0.9f);
		lp.horizontalMargin = 0;
		lp.verticalMargin = 0;
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

	}
}
