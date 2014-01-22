package pt.rocket.testapp.utils;



import pt.rocket.framework.errormanager.ErrorCode;
import pt.rocket.testapp.R;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

/**
 * Class that helps with the creation of required dialogs
 * All methods regarding dialog creation should be declared and implemented here.
 * @author josedourado
 *
 */
public class DialogGenerator {
	
	/**
	 * Method that generates error dialog regarding a specific error code.
	 * Usefull for network connection errors.
	 * @param context
	 * @param errorCode
	 * @return
	 */
	public static Dialog generateDialog(Context context, ErrorCode errorCode){
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.generic_dialog);
		String errorMessage = AppUtils.generateErrorString(context, errorCode);
		((TextView) dialog.findViewById(R.id.generic_dialog_message)).setText(errorMessage);
		return dialog;
	}
	
	/**
	 * Method that generates error dialog regarding an API error
	 * @param context
	 * @param errorMessage
	 * @return
	 */
	public static Dialog generateDialog(Context context, String errorMessage){
		Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.generic_dialog);
		((TextView) dialog.findViewById(R.id.generic_dialog_message)).setText(errorMessage);
		return dialog;
	}
	
	
	

}
