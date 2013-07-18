package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.content.ContentValues;
import android.util.Log;

public class AACustomerTest extends ApiBaseTest {

	public void CustomerTest(){
		
	}
  private static final String CUSTOMER_USER= "jumiatest@test.tt";
  private static String CUSTOMER_PASS= "123456";

  protected String user_id= "";
  protected String session_id= "";

    
  
  
  
  
  /**
   * 
   * @throws Throwable
   */
  public void test_00_test_success() throws Throwable {
	     print("Starting user Logout - success");
	      boolean hasSuccess= false;


	      String result = executeGetRequest(Services.USER_LOGOUT_URL);
	      Log.e("LOGOUT", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//


	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
  }
  
  
  /**
   * 
   * @throws Throwable
   */
  public void test_01_userLogin_success() throws Throwable {
      print("Starting user Login test - successs");

      ContentValues values = new ContentValues();
      values.put(JsonConstants.JSON_LOGIN_EMAIL_TAG, CUSTOMER_USER);
//      values.put("password", encryptMD5(customerPassword));
      values.put(JsonConstants.JSON_LOGIN_PASSWORD_TAG, CUSTOMER_PASS);

      String result = executePostRequest(Services.USER_LOGIN_URL, values);
      Log.e("LOGIN", ":"+result);
      try {
          JSONObject jsonObject = new JSONObject(result);
//          // Check if the status is true
          Assert.assertTrue("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//
//          // check if the data content is valid
          JSONObject jsonSessionObject = jsonObject.optJSONObject(JsonConstants.JSON_SESSION_TAG);
          Assert.assertNotNull("The json does not contain the session object", jsonSessionObject);
          session_id= jsonSessionObject.getString(JsonConstants.JSON_ID_TAG);
          Log.e("SESSION ID", ":"+session_id);
          
          JSONObject metadataObject = jsonObject.optJSONObject(JsonConstants.JSON_METADATA_TAG);
          Assert.assertNotNull("The json does not contain the metadata object", metadataObject);
          JSONObject userObject = metadataObject.optJSONObject(JsonConstants.JSON_USER_TAG);
          Assert.assertNotNull("The json does not contain the user object", userObject);
          user_id=userObject.getString(JsonConstants.JSON_USER_ID_TAG);
          Log.e("USER ID", ":"+user_id);
          
//          Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
      
      
      } catch (JSONException e) {
         // Assert.fail(e.getMessage());
       //   createDefaultUser();
      }
  }
  
  public void test_01_userSession_success() throws Throwable {
      print("Starting user Login test - successs");

      boolean sameSession=false;
      
      String result = executeGetRequest(Services.USER_LOGIN_URL+user_id);
      Log.e("LOGIN", ":"+result);
      try {
          JSONObject jsonObject = new JSONObject(result);
//          // Check if the status is true
          Assert.assertTrue("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//
//          // check if the data content is valid
          JSONObject jsonSessionObject = jsonObject.optJSONObject(JsonConstants.JSON_SESSION_TAG);
          Assert.assertNotNull("The json does not contain the session object", jsonSessionObject);
          String session= jsonSessionObject.getString(JsonConstants.JSON_ID_TAG);
          Log.e("SESSION ID1", ":"+session_id);
          Log.e("SESSION1", ":"+session);
//          if(session.equals(session_id)){
//        	  sameSession=true;
//          }
//
//          Assert.assertTrue("The json does not contain the same session", sameSession);
      
      
      } catch (JSONException e) {
         // Assert.fail(e.getMessage());
       //   createDefaultUser();
      }
  }
  
 
  public void test_02_userDetails_success() throws Throwable {
      print("Starting user edit test - successs");


      boolean sameEmail=false;
      String result = executeGetRequest(Services.USER_DETAILS_URL);

      Log.e("DETAIL",":"+result);
      try {
          JSONObject jsonObject = new JSONObject(result);
//          // Check if the status is true
          Assert.assertTrue("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//
//          // check if the data content is valid
          JSONObject metadataObject = jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
          Assert.assertNotNull("The json does not contain the metadata object", metadataObject);

          JSONObject dataObject = metadataObject.getJSONObject(JsonConstants.JSON_DATA_TAG);
          Assert.assertNotNull("The json does not contain the metadata object", dataObject);
          
          String userEmail= dataObject.getString(JsonConstants.JSON_EMAIL_TAG);
          
          if(userEmail.equals(CUSTOMER_USER)){
          	sameEmail=true;
          }
          Assert.assertTrue("did not get the correct details", sameEmail);
          
          
          
//          Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
      
      
      } catch (JSONException e) {
          Assert.fail(e.getMessage());
      }
  }
  
  

  
  
  public void test_03_userChangePass_success() throws Throwable {
      print("Starting user edit test - successs");

      
      
      ContentValues values = new ContentValues();
      values.put(JsonConstants.JSON_CHANGE_PASS2_TAG, "123456");
      values.put(JsonConstants.JSON_CHANGE_PASS_TAG, "123456");
      values.put(JsonConstants.JSON_CHANGE_EMAIL_TAG, CUSTOMER_USER);

      String result = executePostRequest(Services.USER_CHANGEPASS_URL, values);

      Log.e("EDIT",":"+result);
      try {
          JSONObject jsonObject = new JSONObject(result);
//          // Check if the status is true
          Assert.assertTrue("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//
//          // check if the data content is valid
          JSONObject jsonSessionObject = jsonObject.optJSONObject(JsonConstants.JSON_SESSION_TAG);
          Assert.assertNotNull("The json does not contain the session object", jsonSessionObject);
          session_id= jsonSessionObject.getString(JsonConstants.JSON_ID_TAG);
        
          
          
//          Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
      
      
      } catch (JSONException e) {
          Assert.fail(e.getMessage());
      }
  }
  
  

  
  
  
  /**
   * 
   * @throws Throwable
   */
  public void test_04_userLogout_success() throws Throwable {
      print("Starting user Logout - success");
      boolean hasSuccess= false;


      String result = executeGetRequest(Services.USER_LOGOUT_URL);
      Log.e("LOGOUT", ":"+result);
      try {
          JSONObject jsonObject = new JSONObject(result);
//          // Check if the status is true
          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//
          JSONObject messageObject = jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
          Assert.assertNotNull("The json does not contain the messa object", messageObject );
          JSONArray successArray = messageObject.getJSONArray(JsonConstants.JSON_SUCCESS_TAG);
          
          if(successArray.length()>0){
        	  hasSuccess =true;
          }
         
          Assert.assertTrue("Theres No success", hasSuccess);

      } catch (JSONException e) {
          Assert.fail(e.getMessage());
      }
  }
  
  
  
  
  
  /**
   * 
   * @throws Throwable
   */
  public void test_05_userLogin_fail() throws Throwable {
      print("Starting user Login test - fail");
      boolean hasError= false;
      ContentValues values = new ContentValues();
      values.put(JsonConstants.JSON_LOGIN_EMAIL_TAG, "asda@fsf.ff");
//      values.put("password", encryptMD5(customerPassword));
      values.put(JsonConstants.JSON_LOGIN_PASSWORD_TAG, "1334");

      String result = executePostRequest(Services.USER_LOGIN_URL, values);
      Log.e("LOGIN FAIL", ":"+result);
      try {
          JSONObject jsonObject = new JSONObject(result);
//          // Check if the status is true
          Assert.assertFalse("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//
          JSONObject messageObject = jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
          Assert.assertNotNull("The json does not contain the messa object", messageObject );
          JSONArray errorArray = messageObject.getJSONArray(JsonConstants.JSON_ERROR_TAG);
          
          if(errorArray.length()>0){
        	  hasError =true;
          }
         
          Assert.assertTrue("Theres No errors", hasError);

      } catch (JSONException e) {
          Assert.fail(e.getMessage());
      }
  }
  
  
    /**
     * 
     * @throws Throwable
     */
    public void test_06_userRegister_success() throws Throwable {
        print("Starting user register test - successs");

        boolean sameEmail=false;
        
        ContentValues values = new ContentValues();
        int rd=randomNum(1, 999999999);
        String email="jumiatest"+rd+"@test.tt";
        values.put(JsonConstants.JSON_REG_FIRSTNAME_TAG, "Paulo");
        values.put(JsonConstants.JSON_REG_LASTNAME_TAG, "Silva");
        values.put(JsonConstants.JSON_REG_DAY_TAG, "16");
        values.put(JsonConstants.JSON_REG_MONTH_TAG, "12");
        values.put(JsonConstants.JSON_REG_YEAR_TAG, "1985");
        values.put(JsonConstants.JSON_REG_GENDER_TAG, "male");
        values.put(JsonConstants.JSON_REG_EMAIL_TAG, email);     
        values.put(JsonConstants.JSON_REG_PASS_TAG, "123456");
        values.put(JsonConstants.JSON_REG_PASS2_TAG, "123456");


        String result = executePostRequest(Services.USER_REGISTER_URL, values);

        Log.e("REGISTER",":"+result);
        try {
            JSONObject jsonObject = new JSONObject(result);
//            // Check if the status is true
            Assert.assertTrue("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
  //
//            // check if the data content is valid
            JSONObject jsonSessionObject = jsonObject.getJSONObject(JsonConstants.JSON_SESSION_TAG);
            Assert.assertNotNull("The json does not contain the session object", jsonSessionObject);
            session_id= jsonSessionObject.getString(JsonConstants.JSON_ID_TAG);
            Log.e("SESSION ID", ":"+session_id);
            
            JSONObject metadataObject = jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
            Assert.assertNotNull("The json does not contain the metadata object", metadataObject);
            
            String userEmail = metadataObject.getString(JsonConstants.JSON_EMAIL_TAG);
         
            if(userEmail.equals(email)){
            	sameEmail=true;
            }
            
            Assert.assertTrue("the register is valid", sameEmail);
         
            
//            Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
        
            test_04_userLogout_success();
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    
    public void test_07_userRegister_fail() throws Throwable {
        print("Starting user register test - successs");

        ContentValues values = new ContentValues();
        values.put("first_name", "Paulo");
        values.put("last_name", "Silva");
        values.put("day", "16");
        values.put("month", "12");
        values.put("year", "1985");
        values.put("gender", "male");
//      values.put("newsletter", "1");
        values.put("email", CUSTOMER_USER);     
        values.put("password", "123456");
        values.put("password2", "123456");
//        values.put("newsletter_categories_subscribed", "");
//        values.put("password", encryptMD5(customerPassword));


        String result = executePostRequest(Services.USER_REGISTER_URL, values);

        Log.e("REGISTER",":"+result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            // Check if the status is true
            Assert.assertFalse("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));

//            // check if the data content is valid
//            JSONObject jsonCustomerObject = jsonObject.optJSONObject(JsonConstants.JSON_DATA_TAG);
//            Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
        }
    }
    
    

    
    
    private void createDefaultUser(){
  	     print("Starting user register test - successs");

  	        boolean sameEmail=false;
  	        
  	        ContentValues values = new ContentValues();

  	        values.put(JsonConstants.JSON_REG_FIRSTNAME_TAG, "Paulo");
  	        values.put(JsonConstants.JSON_REG_LASTNAME_TAG, "Silva");
  	        values.put(JsonConstants.JSON_REG_DAY_TAG, "16");
  	        values.put(JsonConstants.JSON_REG_MONTH_TAG, "12");
  	        values.put(JsonConstants.JSON_REG_YEAR_TAG, "1985");
  	        values.put(JsonConstants.JSON_REG_GENDER_TAG, "male");
  	        values.put(JsonConstants.JSON_REG_EMAIL_TAG, CUSTOMER_USER);     
  	        values.put(JsonConstants.JSON_REG_PASS_TAG, "123456");
  	        values.put(JsonConstants.JSON_REG_PASS2_TAG, "123456");
//  	        values.put("newsletter_categories_subscribed", "");
//  	        values.put("password", encryptMD5(customerPassword));


  	        String result = executePostRequest(Services.USER_REGISTER_URL, values);

  	        Log.e("REGISTER",":"+result);
  	        try {
  	            JSONObject jsonObject = new JSONObject(result);
//  	            // Check if the status is true
  	            Assert.assertTrue("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
  	  //
//  	            // check if the data content is valid
  	            JSONObject jsonSessionObject = jsonObject.getJSONObject(JsonConstants.JSON_SESSION_TAG);
  	            Assert.assertNotNull("The json does not contain the session object", jsonSessionObject);
  	            session_id= jsonSessionObject.getString(JsonConstants.JSON_ID_TAG);
  	            Log.e("SESSION ID", ":"+session_id);
  	            
  	            JSONObject metadataObject = jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
  	            Assert.assertNotNull("The json does not contain the metadata object", metadataObject);
  	            
  	            String userEmail = metadataObject.getString(JsonConstants.JSON_EMAIL_TAG);
  	         

  	         
  	            
//  	            Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
  	        
  	        } catch (JSONException e) {
  	            Assert.fail(e.getMessage());
  	        }
    }    


    
}
