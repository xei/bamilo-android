package pt.rocket.testapp.utils;

import android.content.Context;
import pt.rocket.framework.errormanager.ErrorCode;
import pt.rocket.testapp.R;

public class AppUtils {

	/**
	 * Method responsible for building response error messages
	 * @param context
	 * @param errorCode
	 * @return
	 */
	public static String generateErrorString(Context context, ErrorCode errorCode) {
		String errorString;
		switch (errorCode) {
		case TIME_OUT:
			errorString = context.getResources().getString(
					R.string.STRING_ERROR_TIMEOUT);
			break;
		default:
			errorString = context.getResources().getString(
					R.string.STRING_ERROR_GENERIC);
			break;

		}
		return errorString;

	}

}
