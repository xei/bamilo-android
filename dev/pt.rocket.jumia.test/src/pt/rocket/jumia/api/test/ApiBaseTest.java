package pt.rocket.jumia.api.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.test.RestClientSingletonTest;
import pt.rocket.framework.rest.RestService;
import pt.rocket.jumia.api.constants.JsonConstants;

import android.content.ContentValues;
import android.content.Context;
import android.test.ServiceTestCase;
import android.util.Log;

public class ApiBaseTest extends ServiceTestCase<RestService>{
    
	  
		public static final String SKU  ="SA948EL97LGKNGAMZ-7846";
		public static final String QUANTITY  ="1";
		public static final String PRODUCT  ="SA948EL97LGKNGAMZ";
	
		Context mCtx = null;
		
	public ApiBaseTest() {
        super(RestService.class);
    }
    
//	@Override
//	public void testAndroidTestCaseSetupProperly (){
//		Log.e("#############", "testAndroidTestCaseSetupProperly");
//	}
//	
//	@Override
//	public void testServiceTestCaseSetUpProperly() throws Exception{
//		Log.e("#############", "testServiceTestCaseSetUpProperly");
//	}
	
	  @Override
	  protected void setUp() throws Exception {
	    super.setUp();
	    mCtx = getSystemContext();
	  }
	
	
    protected static String sessionToken = "";

    /**
     * 
     * @throws Throwable
     */
//    public void test_00_webServiceLogin() throws Throwable {
//        ContentValues values = new ContentValues();
//
//        values.put("username", "rocket");
//        values.put("password", "rock4me");
//        
//        String result = executePostRequest(JsonConstants.API_MD5, values);
//        
//        try {
////        	 Log.e("RESULT", ":"+result);
//            JSONObject jsonObject = new JSONObject(result);
//            Log.e("RESULT", ":"+result);
////            
//            // Check if the status is true
//            Assert.assertTrue("SUCCESS is false", jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
////
////            // check if the session id is valid
////            JSONObject dataObject = jsonObject.getJSONObject(JSON_DATA_TAG);
////            Assert.assertFalse("The sessionId is not valid", dataObject.optString(JSON_SESSION_TOKEN_TAG).equals(""));
////            
////            sessionToken = dataObject.getString(JSON_SESSION_TOKEN_TAG);
////            
//        } catch (JSONException e) {
//            Assert.fail(e.getMessage());
//        }
//    }
    
    protected String executePostRequest(String serviceURL, ContentValues values) {
        
    	Log.e("POST SERIVCE", ":"+serviceURL);
        return RestClientSingletonTest.getSingleton().executePostRestUrlString(getContext(), JsonConstants.API_URL.concat(serviceURL)+JsonConstants.JSON_DEVICE_TAG,
                values);
        
    }
    
    protected String executeGetRequest(String serviceURL) {
        
    	Log.e("GET SERIVCE", ":"+serviceURL);
        return RestClientSingletonTest.getSingleton().executeGetRestUrlString(getContext(), JsonConstants.API_URL.concat(serviceURL));
        
    }
    
    protected static final String encryptMD5(final String password) {
        try {

            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    

    protected void print(String message) {
        System.out.println(message);
    }
    
    
    protected int randomNum(int min,int max){
    	Random r= new Random();
    	int Low = min;
    	int High = max;
    	int random = r.nextInt(High-Low) + Low;
    	return random;
    }
}