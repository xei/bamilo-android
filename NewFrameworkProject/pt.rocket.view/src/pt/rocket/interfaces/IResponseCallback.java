package pt.rocket.interfaces;
import android.os.Bundle;

    /**
     * Callback for the response
     * 
     * @author Guilherme Silva
     * 
     */
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