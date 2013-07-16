package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.content.ContentValues;
import android.util.Log;

public class CustomerTest extends ApiBaseTest {

  private static final String CUSTOMER_USER= "rica@pt.pt";
  private static String CUSTOMER_PASS= "123456";
//    private final String customerEmail = "android_test_user@rocket-internet.pt";
//    private final String customerPassword = "foodpanda";

    
  /**
   * 
   * @throws Throwable
   */
  public void test_01_userLogin_success() throws Throwable {
      print("Starting user Login test - successs");

      ContentValues values = new ContentValues();
      values.put("email", CUSTOMER_USER);
//      values.put("password", encryptMD5(customerPassword));
      values.put("password", CUSTOMER_PASS);

      String result = executePostRequest(Services.LOGIN_URL, values);
      Log.e("LOGIN", ":"+result);
      try {
          JSONObject jsonObject = new JSONObject(result);
//          // Check if the status is true
          Assert.assertFalse("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
//
//          // check if the data content is valid
//          JSONObject jsonCustomerObject = jsonObject.optJSONObject(JsonConstants.JSON_DATA_TAG);
//          Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
      } catch (JSONException e) {
          Assert.fail(e.getMessage());
      }
  }
  
  
    /**
     * 
     * @throws Throwable
     */
    public void test_09_userRegister_success() throws Throwable {
        print("Starting user register test - successs");

        ContentValues values = new ContentValues();
        values.put("first_name", "Paulo");
        values.put("last_name", "Silva");
        values.put("day", "16");
        values.put("month", "12");
        values.put("year", "1985");
        values.put("gender", "male");
//      values.put("newsletter", "1");
        values.put("email", "jumia.test@test.tt");     
        values.put("password", "123456");
        values.put("password2", "123456");
//        values.put("newsletter_categories_subscribed", "");
//        values.put("password", encryptMD5(customerPassword));


        String result = executePostRequest(Services.REGISTER_URL, values);

        Log.e("REGISTER",":"+result);
        try {
            JSONObject jsonObject = new JSONObject(result);
            // Check if the status is true
            Assert.assertTrue("Status is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));

//            // check if the data content is valid
//            JSONObject jsonCustomerObject = jsonObject.optJSONObject(JsonConstants.JSON_DATA_TAG);
//            Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
        } catch (JSONException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * function md5 encryption for passwords
     * 
     * @param password
     * @return passwordEncrypted
     */
   
    
    
//    private Customer getCustomer() {
//        ContentValues values = new ContentValues();
//
//        values.put("email", customerEmail);
//        values.put("password", encryptMD5(customerPassword));
//
//        String result = executeRequest(LoginApiCall.API_URL, values);
//        //Assert.fail(result);
//        try {
//            JSONObject jsonObject = new JSONObject(result);
//            // Check if the status is true
//            Assert.assertTrue("Status is false \r\n" + result, jsonObject.getBoolean(Constants.JSON_STATUS_TAG));
//
//            // check if the data content is valid
//            JSONObject jsonCustomerObject = jsonObject.optJSONObject(Constants.JSON_DATA_TAG);
//            Assert.assertNotNull("The json does not contain the customer object", jsonCustomerObject);
//
//            Customer customer = new Customer();
//            Assert.assertTrue("Error initialiazing the customer", initializeCustomer(jsonCustomerObject));
//            return customer;
//
//            // Assert.fail(customer.getCode());
//        } catch (JSONException e) {
//            Assert.fail(e.getMessage());
//        }
//
//        return null;
//    }
//
//    
//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
//     * )
//     */
//    public boolean initializeCustomer(JSONObject jsonObject) {
//        try {
//            jsonObject.getString(Constants.JSON_CODE_TAG);
//            jsonObject.optInt(Constants.JSON_LANGUAGE_ID_TAG, 0);
//            jsonObject.getString(Constants.JSON_EMAIL_TAG);
//            jsonObject.getString(Constants.JSON_FIRST_NAME_TAG);
//            jsonObject.getString(Constants.JSON_PASSWORD_TAG);
//            jsonObject.getString(Constants.JSON_LAST_NAME_TAG);
//            if ( ! jsonObject.isNull(Constants.JSON_MOBILE_NUMBER_TAG) ) {
//                jsonObject.optString(Constants.JSON_MOBILE_NUMBER_TAG,"");
//            }
//            jsonObject.getString(Constants.JSON_MOBILE_COUNTRY_CODE);
//            jsonObject.getInt(Constants.JSON_MOBILE_CONFIRMATION_CODE_TAG);
//            jsonObject.getString(Constants.JSON_MOBILE_VERIFIED_TAG).equals("1");
//
//            jsonObject.getString(Constants.JSON_REFERENCE_CODE_TAG);
//            jsonObject.getString(Constants.JSON_INTERNAL_COMMENT_TAG);
//            jsonObject.getString(Constants.JSON_DELETED_TAG).equals("1");
//            jsonObject.getString(Constants.JSON_CREATED_AT_TAG);
//            jsonObject.getString(Constants.JSON_UPDATED_AT_TAG);
//            jsonObject.getString(Constants.JSON_UPDATED_BY_TAG);
//            jsonObject.getString(Constants.JSON_SESSION_TOKEN_TAG);
//
//            
//            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String lastLoginString = jsonObject.getString(Constants.JSON_LAST_LOGIN_TAG);
//            formatter.parse(lastLoginString);
//
//            Address address; 
//            JSONArray addressArray = jsonObject.optJSONArray(Constants.JSON_CUSTOMER_ADDRESSESES_TAG);
//            if(addressArray != null) {
//                final int addressesArrayLength = addressArray.length();
//                for (int i = 0; i < addressesArrayLength; ++i) {
//                    address = new Address();
//                    address.initialize(addressArray.getJSONObject(i));
//                }
//            }
//        } catch (JSONException e) {
//            Assert.fail(e.getMessage());
//            return false;
//        } catch (ParseException e) {
//            Assert.fail(e.getMessage());
//            return false;
//        }
//        return true;
//    }
    
}
