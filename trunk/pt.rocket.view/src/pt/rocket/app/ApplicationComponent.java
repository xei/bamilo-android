/**
 * 
 */
package pt.rocket.app;

import pt.rocket.framework.ErrorCode;
import android.app.Application;

/**
 * @author nutzer2
 * 
 */
public abstract class ApplicationComponent {
    
    private ErrorCode result;
    
    public ErrorCode init(Application app) {
        if(result != ErrorCode.NO_ERROR) {
            result = initInternal(app);
        }
        return result;
    }

    protected abstract ErrorCode initInternal(Application app);

}
