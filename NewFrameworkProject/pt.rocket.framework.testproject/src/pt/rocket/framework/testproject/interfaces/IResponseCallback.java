package pt.rocket.framework.testproject.interfaces;

import android.os.Bundle;

public interface IResponseCallback {
    /**
     * Handles the success request
     * 
     * @param bundle
     */
    public void onRequestComplete(Bundle bundle);

    /**
     * Handles the error request
     * 
     * @param bundle
     */
    public void onRequestError(Bundle bundle);
}
