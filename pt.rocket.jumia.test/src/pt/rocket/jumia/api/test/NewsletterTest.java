package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.util.Log;

public class NewsletterTest extends ApiBaseTest {

	
	
//	  public void test_01_NewsletterValidate_success() throws Throwable {
//	      print("Starting Newsletter Validate test - successs");
//	      boolean hasError=false;
//
//	      
//	      String result = executeGetRequest(Services.NEWSLETTER_VALIDATE_URL);
//	      Log.e("FETCH CATALOG", ":"+result);
//	      try {
//	          JSONObject jsonObject = new JSONObject(result);
////	          // Check if the status is true
//	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//	//
//	          // check if the data content is valid
//	          JSONObject messageObject= jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
//	          Assert.assertNotNull("The json does not contain the message object", messageObject);
//	          
//	          JSONArray errorArray= messageObject.getJSONArray(JsonConstants.JSON_ERROR_TAG);
//	          Assert.assertNotNull("The json does not contain the error array", errorArray);
//	          if(errorArray.length()>0){
//	        	  hasError=true;
//	          }else{
//	        	  hasError=false;
//	          }		          
//	          Assert.assertTrue("Theres no Error" , hasError);
//	          
//	          
//	      } catch (JSONException e) {
//	          Assert.fail(e.getMessage());
//	      }
//	  }
	  
	  public void test_02_NewsletterValidate_fail() throws Throwable {
	      print("Starting Newsletter Validate test - successs");
	      boolean hasError=false;

	      
	      String result = executeGetRequest(Services.NEWSLETTER_VALIDATE_URL);
	      Log.e("Newsletter Validate FAIL", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertFalse("Success is true \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject messageObject= jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
	          Assert.assertNotNull("The json does not contain the message object", messageObject);
	          
	          JSONArray errorArray= messageObject.getJSONArray(JsonConstants.JSON_ERROR_TAG);
	          Assert.assertNotNull("The json does not contain the error array", errorArray);
	          if(errorArray.length()>0){
	        	  hasError=true;
	          }else{
	        	  hasError=false;
	          }		          
	          Assert.assertTrue("Theres no Error" , hasError);
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
//	  public void test_03_NewsletterUnsign_success() throws Throwable {
//	      print("Starting NEWSLETTER USIGN FAIL test - successs");
//	      boolean hasError=false;
//
//	      
//	      String result = executeGetRequest(Services.NEWSLETTER_UNSIGN_URL);
//	      Log.e("NEWSLETTER USIGN", ":"+result);
//	      try {
//	          JSONObject jsonObject = new JSONObject(result);
////	          // Check if the status is true
//	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//	//
//	          // check if the data content is valid
//	          JSONObject messageObject= jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
//	          Assert.assertNotNull("The json does not contain the message object", messageObject);
//	          
//	          JSONArray errorArray= messageObject.getJSONArray(JsonConstants.JSON_ERROR_TAG);
//	          Assert.assertNotNull("The json does not contain the error array", errorArray);
//	          if(errorArray.length()>0){
//	        	  hasError=true;
//	          }else{
//	        	  hasError=false;
//	          }		          
//	          Assert.assertTrue("Theres no Error" , hasError);
//	          
//	          
//	      } catch (JSONException e) {
//	          Assert.fail(e.getMessage());
//	      }
//	  }
	  
	  public void test_04_NewsletterUnsign_fail() throws Throwable {
	      print("Starting Newsletter Unsign test - successs");
	      boolean hasError=false;

	      
	      String result = executeGetRequest(Services.NEWSLETTER_UNSIGN_URL);
	      Log.e("NEWSLETTER USIGN FAIL", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertFalse("Success is true \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject messageObject= jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
	          Assert.assertNotNull("The json does not contain the message object", messageObject);
	          
	          JSONArray errorArray= messageObject.getJSONArray(JsonConstants.JSON_ERROR_TAG);
	          Assert.assertNotNull("The json does not contain the error array", errorArray);
	          if(errorArray.length()>0){
	        	  hasError=true;
	          }else{
	        	  hasError=false;
	          }		          
	          Assert.assertTrue("Theres no Error" , hasError);
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	
}
