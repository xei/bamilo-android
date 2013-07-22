package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.content.ContentValues;
import android.util.Log;

public class RatingTest extends ApiBaseTest {

	public static final String APPEARANCE_STARS ="8";
	public static final String PRICE_STARS ="3";
	public static final String QUALITY_STARS ="13";
	public static final String RATING_COMMENT ="comment";
	public static final String RATING_TITLE ="title";
	public static final String RATING_NAME ="Teste";
	
	
	ACustomerTest customer = new ACustomerTest();
	  /**
	   * 
	   * @throws Throwable
	   */
	  public void test_01_addRatingNotLogged_success() throws Throwable {
	      print("Starting user Logout - success");
	      boolean hasSuccess= false;
	      boolean hasProduct= false;

	      
	      ContentValues values = new ContentValues();
	      values.put(JsonConstants.JSON_RATING_SKU_TAG, PRODUCT);
	      values.put(JsonConstants.JSON_RATING_APPEARANCE_TAG, APPEARANCE_STARS);
	      values.put(JsonConstants.JSON_RATING_PRICE_TAG, PRICE_STARS);
	      values.put(JsonConstants.JSON_RATING_QUALITY_TAG, QUALITY_STARS);
	      values.put(JsonConstants.JSON_RATING_COMMENT_TAG,  RATING_COMMENT);
	      values.put(JsonConstants.JSON_RATING_NAME_TAG, RATING_NAME );
	      values.put(JsonConstants.JSON_RATING_TITLE_TAG, RATING_TITLE );


	      String result = executePostRequest(Services.RATING_URL,values);
	      Log.e("ADD RATING", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          JSONObject messageObject = jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
	          Assert.assertNotNull("The json does not contain the message object", messageObject );
	          JSONArray successArray = messageObject.getJSONArray(JsonConstants.JSON_SUCCESS_TAG);
	          
	          if(successArray.length()>0){
	        	  hasSuccess =true;
	          }
	         
	          Assert.assertTrue("Theres No success", hasSuccess);
	          
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	
	  public void test_02_addRatingLogged_success() throws Throwable {
	      print("Starting user Logout - success");
	      
	      customer.test_01_userLogin_success();
	      
	      boolean hasSuccess= false;
	      boolean hasProduct= false;

	      
	      ContentValues values = new ContentValues();
	      values.put(JsonConstants.JSON_RATING_SKU_TAG, PRODUCT);
	      values.put(JsonConstants.JSON_RATING_APPEARANCE_TAG, APPEARANCE_STARS);
	      values.put(JsonConstants.JSON_RATING_PRICE_TAG, PRICE_STARS);
	      values.put(JsonConstants.JSON_RATING_QUALITY_TAG, QUALITY_STARS);
	      values.put(JsonConstants.JSON_RATING_COMMENT_TAG,  RATING_COMMENT);
	      values.put(JsonConstants.JSON_RATING_NAME_TAG, RATING_NAME );
	      values.put(JsonConstants.JSON_RATING_TITLE_TAG, RATING_TITLE );


	      String result = executePostRequest(Services.RATING_URL,values);
	      Log.e("ADD RATING", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          JSONObject messageObject = jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
	          Assert.assertNotNull("The json does not contain the message object", messageObject );
	          JSONArray successArray = messageObject.getJSONArray(JsonConstants.JSON_SUCCESS_TAG);
	          
	          if(successArray.length()>0){
	        	  hasSuccess =true;
	          }
	         
	          Assert.assertTrue("Theres No success", hasSuccess);
	          
	          
	          customer.test_04_userLogout_success();
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
}
