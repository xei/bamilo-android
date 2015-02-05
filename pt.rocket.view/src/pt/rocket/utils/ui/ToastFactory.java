package pt.rocket.utils.ui;

import pt.rocket.utils.Toast;
import pt.rocket.view.R;
import android.content.Context;

/**
 * Class used to manage all toasts.
 * 
 * @author sergiopereira
 */
public enum ToastFactory {

    SUCCESS_LOGIN(R.string.succes_login, Toast.LENGTH_LONG),
    ERROR_FB_PERMISSION(R.string.error_fb_permission_not_granted, Toast.LENGTH_LONG);

    
    private int string;
    private int duration;

    /**
     * Constructor
     * @param string
     * @param duration
     */
    ToastFactory(int string, int duration) {
        this.string = string;
        this.duration = duration;
    }

    /**
     * Show a toast.
     * 
     * @param context
     * @param type
     * @author sergiopereira
     */
    public void show(Context context) {
        Toast.makeText(context, context.getString(string), duration).show();
    }
}