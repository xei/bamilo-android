package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.util.Log;

public class SearchTest extends ApiBaseTest {

	
	  public void test_01_Search_success() throws Throwable {
	      print("Starting SEARCH test - successs");
	      boolean hasForms=false;

	      String query="?q=nik";
	      
	      String result = executeGetRequest(Services.SEARCH_SEARCH_URL+query);
	      Log.e("SEARCH", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray dataArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataArray);
	          if(dataArray.length()>0){
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no Forms" , hasForms);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	
	  public void test_02_SearchAllProducts_success() throws Throwable {
	      print("Starting ALL PRODUCTS test - successs");
	      boolean hasProducts=false;

	      String result = executeGetRequest(Services.SEARCH_ALL_PRODUCTS_URL);
	      Log.e("ALL PRODUCTS", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the data array", resultArray);
	          if(resultArray.length()>0){
	        	  hasProducts=true;
	          }else{
	        	  hasProducts=false;
	          }		          
	          Assert.assertTrue("Theres no Products" , hasProducts);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_03_SearchSpecialPrice_success() throws Throwable {
	      print("Starting SPECIAL PRICES test - successs");
	      boolean hasSpecialPrice=false;

	      String result = executeGetRequest(Services.SEARCH_SPECIAL_PRODUCTS_URL);
	      Log.e("SPECIAL PRICES", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the data array", resultArray);
	          if(resultArray.length()>0){
	        	  hasSpecialPrice=true;
	          }else{
	        	  hasSpecialPrice=false;
	          }		          
	          Assert.assertTrue("Theres no products with special price" , hasSpecialPrice);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
}
