/**
 * 
 */
package pt.rocket.framework.utils;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONObject;

import pt.rocket.framework.objects.CurrencyConversionInput;
import pt.rocket.framework.objects.ConversionResult;
import android.app.Activity;
import android.os.AsyncTask;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.util.EntityUtils;

/**
 * Asynchronous task to apply a money value conversion based on an input value, an origin currency
 * code and a target currency code.
 * The class allows to register a listener interface to be called with the converted output value
 * when the conversion has finished.
 */
public class CurrencyConversionTask extends AsyncTask<CurrencyConversionInput, Void, ArrayList<ConversionResult>> {

    /**
     * Completion listener being called on finishing the conversion task
     */
    private OnCompleteListener<ArrayList<ConversionResult>> _listener = null;

    /**
     * A temporary storage of the conversion output to bridge listener un-/registration
     */
    private ArrayList<ConversionResult> _result = null;

    /**
     * Override default constructor to be private to restrict instantiation to convenience
     * constructor
     */
    private CurrencyConversionTask() {}


    /**
     * Convenience constructor to instantiate a conversion task. It takes an activity object to
     * dispatch the listener operation on the UI thread of the activity. Next it takes the listener
     * itself.
     *
     * @param activity <code>Activity</code> that the listener will be dispatched to
     * @param listener Listener to register.
     */
    public CurrencyConversionTask(Activity activity, OnCompleteListener<ArrayList<ConversionResult>> listener) {
        this();
        register(activity, listener);
    }

    /**
     * Dedicated exception to the task used internally to resolve errors during the conversion task
     */
    @SuppressWarnings("serial")
    public class ConversionException extends Exception {
        public ConversionException(String message) {
            super(message);
        }
    }

    /**
     * Interface for the completion listener. This is the thing that we'll call when things get
     * completed.
     *
     * @param <ConversionResult>
     */
    @SuppressWarnings("hiding")
    public interface OnCompleteListener<ConversionResult> {
        public void onComplete(ConversionResult result);
    }

    /* (non-Javadoc)
    * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
    */
    @Override
    protected void onPostExecute(ArrayList<ConversionResult> result) {
        if(_listener != null) {
            _listener.onComplete(result);
        } else {
            // save the result so we can defer calling the completion
            // listener when (and if) it re-registers
            _result = result;
        }
    }

    /**
     * Register the listener to be notified with the conversion output when the task completes.
     *
     * @param listener the listener to call
     */
    public void register(Activity activity, OnCompleteListener<ArrayList<ConversionResult>> listener) {
        _listener = listener;
        // see if we had a deferred result available
        if (_result != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _listener.onComplete(_result);
                    _result = null;
                }
            });
        }
    }

    /**
     * Unregister the registered listener.
     */
    public void unregister() {
        _listener = null;
    }

    //private static String URL_FORMAT = "https://openexchangerates.org/api/convert/%f/%s/%s?app_id=%s";
    //private static String URL_FORMAT = "https://finance.yahoo.com/d/quotes.csv?e=.csv&f=l1&s=%s%s=X";
    private static String URL_FORMAT = "http://rate-exchange.appspot.com/currency?from=%s&to=%s";


    @Override
    protected ArrayList<ConversionResult> doInBackground(CurrencyConversionInput... currencyConversionInputs) {
        Double value = 0.0;
        Error error = null;
        String currentCurrency = "";
        String currentTargetCurrency = "";
        Double rate = 1.0;
 
        _result = new ArrayList<ConversionResult>();
        
        ConversionResult result;
        String currencyCode;
        String targetCurrencyCode;
        String errMsgFormat = "";
        String errMsg = "";
        for(CurrencyConversionInput input : currencyConversionInputs) {
            try {
                
                // Validate conversion operation input parameters
                currencyCode = input.getCurrencyCode();
                if (currencyCode == null) {
                    throw new ConversionException("Null currency code not allowed");
                }
                if (currencyCode.length() < 3) {
                    errMsgFormat = "Invalid currency code '%s', must have 3 characters";
                    errMsg = String.format(errMsgFormat, currencyCode);
                    throw new ConversionException(errMsg);
                }
                targetCurrencyCode = input.getTargetCurrencyCode();
                if (targetCurrencyCode == null) {
                    throw new ConversionException("Null target currency code not allowed");
                }
                if (targetCurrencyCode.length() < 3) {
                    errMsgFormat = "Invalid target currency code '%s', must have 3 characters";
                    errMsg = String.format(errMsgFormat, targetCurrencyCode);
                    throw new ConversionException(errMsg);
                }
                
                value = input.getValue();
                
                // Handle trivial value 0 without web service interaction
                if (value != 0.0f) {
                    // Only call the Yahoo API if the currencies are different from the previous ones
                    if (!currentCurrency.equals(currencyCode) || !currentTargetCurrency.equals(targetCurrencyCode)) {
                        currentCurrency = currencyCode;
                        currentTargetCurrency = targetCurrencyCode;
                        rate = getCurrencyConvertionRate(currencyCode, targetCurrencyCode);
                    }
                    
                    // Calculate the output value by multiplying the input value with the rate. Cast the
                    // result to float precision.
                    value = Double.valueOf(value * rate);
                    
                } else {
                    result = new ConversionResult(value, error);
                }
                
            } catch (ConversionException e) {
                 error = new Error(e.getMessage());
            } finally {
                result = new ConversionResult(value, error);
            }
            _result.add(result);
        }
            
        return _result;
    }
    
    
    /**
     * Consults the Yahoo API in order to return the ratio between two currencies
     * 
     * @param currencyCode the currency code from where we want to convert
     * @param targetCurrencyCode the currency code to where we want to convert
     * @return the ratio betweem the two currency 
     * @throws ConversionException
     */
    private Double getCurrencyConvertionRate(String currencyCode, String targetCurrencyCode) throws ConversionException {
        
        HttpClient client = new DefaultHttpClient();
        String url = String.format(URL_FORMAT, currencyCode, targetCurrencyCode);
        HttpGet httpGet = new HttpGet(url);
        // Parse the result and validate the retrieved value to be greater than zero
        JSONObject responseObj = null;
        try {
            HttpResponse response = client.execute(httpGet);
            
            responseObj = new JSONObject(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Double rate = 0.0;
        if(responseObj != null){
             rate = responseObj.optDouble("rate");
        }
        if (rate <= 0) {
            String errFormat = "Invalid rate %f, must be greater than zero";
            throw new ConversionException(String.format(errFormat, rate));
        }
        return rate;
    }
}
