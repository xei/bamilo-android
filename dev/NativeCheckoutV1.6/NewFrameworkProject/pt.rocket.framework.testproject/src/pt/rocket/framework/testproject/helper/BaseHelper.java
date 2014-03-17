package pt.rocket.framework.testproject.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.rocket.framework.testshell.test.R;

import pt.rocket.framework.testproject.objects.JSONConstants;
import pt.rocket.framework.testproject.objects.XMLObject;
import pt.rocket.framework.testproject.utils.XMLUtils;
import pt.rocket.framework.utils.Constants;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

/**
 * Base helper for the test app. The helper is responsible for generating the
 * bundle for the api call and parse the http response
 * 
 * @author Guilherme Silva
 * 
 */
public abstract class BaseHelper {
    private static String TAG = BaseHelper.class.getSimpleName();
    protected Context mContext;
    protected String failedParameterMessage;

    /**
     * Creates the bundle for the request
     * 
     * @return
     */
    public abstract Bundle generateRequestBundle();

    /**
     * Checks the response status of the response that came in a bundle in order
     * to evaluate if its a valid response or not
     * 
     * @param bundle
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Bundle checkResponseForStatus(Bundle bundle, Context mContext) {
        String response = bundle.getString(Constants.BUNDLE_RESPONSE_KEY);
        this.mContext = mContext;
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean validation = true;

            try {
                XMLObject generalRules = XMLUtils.xmlParser(mContext, R.xml.general_rules);
                validation = XMLUtils.jsonObjectAssertion(jsonObject , generalRules);
                bundle.putBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY, validation);
                Log.i(TAG," received validation "+validation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (validation) {
                jsonObject = jsonObject.getJSONObject(XMLReadingConfiguration.XML_METADATA_CONTAINER_TAG);
                
                return parseResponseBundle(bundle, jsonObject);

            } else {
                Log.i(TAG," Failed validation ");
                Log.i(TAG,  " failedParameterMessage "+XMLUtils.getMessage());
                bundle.putString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY, XMLUtils.getMessage());
                return parseResponseErrorBundle(bundle);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return parseResponseErrorBundle(bundle);
        }
    }

    /**
     * In case there as a valid json response, but that contains an error
     * indication, be it due to wrong parameters or something else this is the
     * method used to parse that error
     * 
     * @return
     */
    public abstract Bundle parseResponseErrorBundle(Bundle bundle);

    /**
     * Parses a correct json response
     * 
     * @param bundle
     * @return
     */
    public abstract Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject);

    /**
     * Parses the bundle from the error response - This parses errors of generic
     * factors: TIMEOUT, PROTOCOL ERROR, SOCKET ERROR among others
     * 
     * @param bundle
     * @return
     */
    public abstract Bundle parseErrorBundle(Bundle bundle);

   
    
    
}
