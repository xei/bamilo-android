/**
 * 
 */
package pt.rocket.app;

import pt.rocket.framework.ErrorCode;
import android.content.Context;

/**
 * @author nutzer2
 * 
 */
public abstract class ApplicationComponent {
    
    private ErrorCode result;
    
    public ErrorCode init(Context context) {
        if(result != ErrorCode.NO_ERROR) {
            result = initInternal(context);
        }
        return result;
    }

    protected abstract ErrorCode initInternal(Context context);

}
