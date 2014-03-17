package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.util.Log;

public class StaticBlockTest extends ApiBaseTest {

	private final static int NUM_NAVIGATION_MENUS=6;
	
	  public void test_01_StaticBlocks_success() throws Throwable {
	      print("Starting STATIC BLOCKS test - successs");
	      boolean hasBlocks=false;

	      String result = executeGetRequest(Services.STATIC_BLOCK_URL);
	      Log.e("FULL STATIC BLOCKS", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray dataArray= metadataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataArray);
	          if(dataArray.length()>0){
	        	  hasBlocks=true;
	          }else{
	        	  hasBlocks=false;
	          }		          
	          Assert.assertTrue("Theres no Static Blocks" , hasBlocks);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_02_MobileNavigation_success() throws Throwable {
	      print("Starting Navigation test - successs");
	      boolean hasMenus=false;

	      String result = executeGetRequest(Services.STATIC_NAV_URL);
	      Log.e("FULL MOBILE NAVIGATION", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray dataArray= metadataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataArray);
	          if(dataArray.length()>0){
	        	  hasMenus=true;
	          }else{
	        	  hasMenus=false;
	          }		          
	          Assert.assertTrue("Theres no mobile navigation menus." , hasMenus);
	          int count=0;
	          boolean allMenus=false;
	          JSONObject attributes = new JSONObject();
	          for (int i = 0; i < dataArray.length(); i++) {
	        	  
	        	 attributes=  ((JSONObject)dataArray.getJSONObject(i)).getJSONObject("attributes");
	        	 
				if(attributes.getString("description").equalsIgnoreCase("Cart")){
					count++;
				}
				if(attributes.getString("description").equalsIgnoreCase("Home")){
					count++;
				}
				if(attributes.getString("description").equalsIgnoreCase("Categories")){
					count++;
				}
				if(attributes.getString("description").equalsIgnoreCase("Search")){
					count++;
				}
				if(attributes.getString("description").equalsIgnoreCase("My Account")){
					count++;
				}
				if(attributes.getString("description").equalsIgnoreCase("Choose Country")){
					count++;
				}
					

			}
				if(count==NUM_NAVIGATION_MENUS){
					allMenus=true;
				}
				 Assert.assertTrue("Theres missing some default menu" , allMenus);
				 
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_03_CvvInfo_success() throws Throwable {
	      print("Starting CvvInfo test - successs");
	      boolean hasCvvInfo=false;

	      String result = executeGetRequest(Services.STATIC_CVV_URL);
	      Log.e("FULL CATEGORY", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray dataArray= metadataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataArray);
	          if(dataArray.length()>0){
	        	  hasCvvInfo=true;
	          }else{
	        	  hasCvvInfo=false;
	          }		          
	          Assert.assertTrue("Theres no CVV info" , hasCvvInfo);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	  public void test_04_AboutList_success() throws Throwable {
	      print("Starting CvvInfo test - successs");
	      boolean hasCvvInfo=false;

	      String result = executeGetRequest(Services.STATIC_ABOUT_URL);
	      Log.e("FULL ABOUTLIST", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray dataArray= metadataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataArray);
	          if(dataArray.length()>0){
	        	  hasCvvInfo=true;
	          }else{
	        	  hasCvvInfo=false;
	          }		          
	          Assert.assertTrue("Theres no about info" , hasCvvInfo);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_05_AboutListv2_success() throws Throwable {
	      print("Starting CvvInfo test - successs");
	      boolean hasCvvInfo=false;

	      String result = executeGetRequest(Services.STATIC_ABOUTV2_URL);
	      Log.e("FULL ABOUTLISTV2", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray dataArray= metadataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataArray);
	          if(dataArray.length()>0){
	        	  hasCvvInfo=true;
	          }else{
	        	  hasCvvInfo=false;
	          }		          
	          Assert.assertTrue("Theres no aboutv2 info" , hasCvvInfo);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_06_terms_success() throws Throwable {
	      print("Starting CvvInfo test - successs");
	      boolean hasCvvInfo=false;

	      String result = executeGetRequest(Services.STATIC_TERMS_URL);
	      Log.e("FULL TERMS", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray dataArray= metadataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataArray);
	          if(dataArray.length()>0){
	        	  hasCvvInfo=true;
	          }else{
	        	  hasCvvInfo=false;
	          }		          
	          Assert.assertTrue("Theres no terms info" , hasCvvInfo);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
}
