package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.content.ContentValues;
import android.util.Log;

public class OrderTest extends ApiBaseTest {

	public static final String SKU  ="SA948EL97LGKNGAMZ-7846";
	public static final String QUANTITY  ="1";
	public static final String PRODUCT  ="SA948EL97LGKNGAMZ";
	
	CustomerTest customer= new CustomerTest();  
	
	  /**
	   * 
	   * @throws Throwable
	   */
	  public void test_01_addToCartNotLogged_success() throws Throwable {
	      print("Starting user Logout - success");
	      boolean hasSuccess= false;
	      boolean hasProduct= false;

	      ContentValues values = new ContentValues();
	      values.put(JsonConstants.JSON_ORDER_SKU_TAG, SKU);
	      values.put(JsonConstants.JSON_ORDER_QUANTITY_TAG, QUANTITY );
	      values.put(JsonConstants.JSON_ORDER_SKU_PRODUCT_TAG, PRODUCT);


	      String result = executePostRequest(Services.ORDER_ADD_URL,values);
	      Log.e("ADD ORDER", ":"+result);
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

	          JSONObject metadataObjec= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the metadata object", messageObject );
	          
	          JSONObject cartitemObject= metadataObjec.getJSONObject(JsonConstants.JSON_CARTITEM_TAG);
	          Assert.assertNotNull("The json does not contain the cart item object", cartitemObject );
	          
	          JSONObject skuObject= cartitemObject.getJSONObject(SKU);
	          Assert.assertNotNull("The json does not contain the sku object", skuObject );
	          
	          String configsku= skuObject.getString(JsonConstants.JSON_CONFIGSKU_TAG);
	          
	          if(configsku.equalsIgnoreCase(PRODUCT)){
	        	  hasProduct=true;
	          }
	          Assert.assertTrue("The json does not product object", hasProduct );
	          
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	  
	  
//	  /**
//	   * 
//	   * @throws Throwable
//	   */
//	  public void test_02_SeeCartNotLogged_success() throws Throwable {
//	      print("Starting user Logout - success");
//	      boolean hasSuccess= false;
//
//
//
//
//	      String result = executeGetRequest(Services.ORDER_INFO_URL);
//	      Log.e("SEE CART", ":"+result);
//	      try {
//	          JSONObject jsonObject = new JSONObject(result);
////	          // Check if the status is true
//	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
////	//
////	          JSONObject messageObject = jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
////	          Assert.assertNotNull("The json does not contain the messa object", messageObject );
////	          JSONArray successArray = messageObject.getJSONArray(JsonConstants.JSON_SUCCESS_TAG);
////	          
////	          if(successArray.length()>0){
////	        	  hasSuccess =true;
////	          }
////	         
////	          Assert.assertTrue("Theres No success", hasSuccess);
//
//	      } catch (JSONException e) {
//	          Assert.fail(e.getMessage());
//	      }
//	  }
	  
	  /**
	   * 
	   * @throws Throwable
	   */
	  public void test_03_removeCartNotLogged_success() throws Throwable {
	      print("Starting user Logout - success");
	      boolean hasSuccess= false;

	      ContentValues values = new ContentValues();
	      values.put(JsonConstants.JSON_ORDER_SKU_TAG, SKU);



	      String result = executePostRequest(Services.ORDER_REMOVE_URL,values);
	      Log.e("REMOVE ORDER", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
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
	
	  
	  
	  public void test_04_addToCartLogged_success() throws Throwable {
		  
		  customer.test_01_userLogin_success();
		  
	      print("Starting user Logout - success");
	      boolean hasSuccess= false;
	      boolean hasProduct= false;

	      ContentValues values = new ContentValues();
	      values.put(JsonConstants.JSON_ORDER_SKU_TAG, SKU);
	      values.put(JsonConstants.JSON_ORDER_QUANTITY_TAG, QUANTITY );
	      values.put(JsonConstants.JSON_ORDER_SKU_PRODUCT_TAG, PRODUCT);


	      String result = executePostRequest(Services.ORDER_ADD_URL,values);
	      Log.e("ADD ORDER", ":"+result);
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

	          JSONObject metadataObjec= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the metadata object", messageObject );
	          
	          JSONObject cartitemObject= metadataObjec.getJSONObject(JsonConstants.JSON_CARTITEM_TAG);
	          Assert.assertNotNull("The json does not contain the cart item object", cartitemObject );
	          
	          JSONObject skuObject= cartitemObject.getJSONObject(SKU);
	          Assert.assertNotNull("The json does not contain the sku object", skuObject );
	          
	          String configsku= skuObject.getString(JsonConstants.JSON_CONFIGSKU_TAG);
	          
	          if(configsku.equalsIgnoreCase(PRODUCT)){
	        	  hasProduct=true;
	          }
	          Assert.assertTrue("The json does not product object", hasProduct );
	          
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	  
	  
//	  /**
//	   * 
//	   * @throws Throwable
//	   */
//	  public void test_05_SeeCartLogged_success() throws Throwable {
//	      print("Starting user Logout - success");
//	      boolean hasSuccess= false;
//
//
//
//
//	      String result = executeGetRequest(Services.ORDER_INFO_URL);
//	      Log.e("SEE CART", ":"+result);
//	      try {
//	          JSONObject jsonObject = new JSONObject(result);
////	          // Check if the status is true
//	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
////	//
////	          JSONObject messageObject = jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
////	          Assert.assertNotNull("The json does not contain the messa object", messageObject );
////	          JSONArray successArray = messageObject.getJSONArray(JsonConstants.JSON_SUCCESS_TAG);
////	          
////	          if(successArray.length()>0){
////	        	  hasSuccess =true;
////	          }
////	         
////	          Assert.assertTrue("Theres No success", hasSuccess);
//
//	      } catch (JSONException e) {
//	          Assert.fail(e.getMessage());
//	      }
//	  }
//	  
	  /**
	   * 
	   * @throws Throwable
	   */
	  public void test_06_removeCartLogged_success() throws Throwable {
	      print("Starting user Logout - success");
	      boolean hasSuccess= false;

	      
	      ContentValues values = new ContentValues();
	      values.put(JsonConstants.JSON_ORDER_SKU_TAG, SKU);



	      String result = executePostRequest(Services.ORDER_REMOVE_URL,values);
	      Log.e("REMOVE ORDER", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          JSONObject messageObject = jsonObject.getJSONObject(JsonConstants.JSON_MESSAGES_TAG);
	          Assert.assertNotNull("The json does not contain the messa object", messageObject );
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
