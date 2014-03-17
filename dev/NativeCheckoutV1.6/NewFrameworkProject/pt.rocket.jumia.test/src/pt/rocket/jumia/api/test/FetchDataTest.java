package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.util.Log;

public class FetchDataTest extends ApiBaseTest {

	  public void test_01_FetchCatalog_success() throws Throwable {
	      print("Starting FETCH CATALOG test - successs");
	      boolean hasData=false;
	      boolean hasDataData=false;

	      
	      String result = executeGetRequest(Services.FETCH_CATALOG_URL);
	      Log.e("FETCH CATALOG", ":"+result);
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
	        	  hasData=true;
	          }else{
	        	  hasData=false;
	          }		          
	          Assert.assertTrue("Theres no data" , hasData);
	          
	          JSONObject dataObject= dataArray.getJSONObject(0);
	          JSONArray dataDataArray=dataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          
	          if(dataDataArray.length()>0){
	        	  hasDataData=true;
	          }else{
	        	  hasDataData=false;
	          }		          
	          Assert.assertTrue("Theres no Data" , hasDataData);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_02_FetchSearch_success() throws Throwable {
	      print("Starting FETCH SEARCH test - successs");
	      boolean hasData=false;
	      boolean hasDataData=false;

	      
	      String result = executeGetRequest(Services.FETCH_SEARCH_URL);
	      Log.e("FETCH SEARCH ", ":"+result);
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
	        	  hasData=true;
	          }else{
	        	  hasData=false;
	          }		          
	          Assert.assertTrue("Theres no data" , hasData);
	          
	          JSONObject dataObject= dataArray.getJSONObject(0);
	          JSONArray dataDataArray=dataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          
	          if(dataDataArray.length()>0){
	        	  hasDataData=true;
	          }else{
	        	  hasDataData=false;
	          }		          
	          Assert.assertTrue("Theres no Data" , hasDataData);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_03_FetchMain_success() throws Throwable {
	      print("Starting Fetch Main test - successs");
	      boolean hasData=false;
	      boolean hasDataData=false;

	      
	      String result = executeGetRequest(Services.FETCH_MAIN_URL);
	      Log.e("FETCH MAIN", ":"+result);
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
	        	  hasData=true;
	          }else{
	        	  hasData=false;
	          }		          
	          Assert.assertTrue("Theres no data" , hasData);
	          
	          JSONObject dataObject= dataArray.getJSONObject(0);
	          JSONArray dataDataArray=dataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          
	          if(dataDataArray.length()>0){
	        	  hasDataData=true;
	          }else{
	        	  hasDataData=false;
	          }		          
	          Assert.assertTrue("Theres no Data" , hasDataData);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_04_FetchNewsletter_success() throws Throwable {
	      print("Starting FETCH NEWSLETTER test - successs");
	      boolean hasData=false;
	      boolean hasDataData=false;

	      
	      String result = executeGetRequest(Services.FETCH_NEWSLETTER_URL);
	      Log.e("FETCH NEWSLETTER", ":"+result);
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
	        	  hasData=true;
	          }else{
	        	  hasData=false;
	          }		          
	          Assert.assertTrue("Theres no data" , hasData);
	          
	          JSONObject dataObject= dataArray.getJSONObject(0);
	          JSONArray dataDataArray=dataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          
	          if(dataDataArray.length()>0){
	        	  hasDataData=true;
	          }else{
	        	  hasDataData=false;
	          }		          
	          Assert.assertTrue("Theres no Data" , hasDataData);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_05_FetchCustomer_success() throws Throwable {
	      print("Starting Fetch customer test - successs");
	      boolean hasData=false;
	      boolean hasDataData=false;

	      
	      String result = executeGetRequest(Services.FETCH_CUSTOMER_URL);
	      Log.e("FETCH CUSTOMER", ":"+result);
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
	        	  hasData=true;
	          }else{
	        	  hasData=false;
	          }		          
	          Assert.assertTrue("Theres no data" , hasData);
	          
	          JSONObject dataObject= dataArray.getJSONObject(0);
	          JSONArray dataDataArray=dataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          
	          if(dataDataArray.length()>0){
	        	  hasDataData=true;
	          }else{
	        	  hasDataData=false;
	          }		          
	          Assert.assertTrue("Theres no Data" , hasDataData);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_06_FetchRating_success() throws Throwable {
	      print("Starting FECTH RATING test - successs");
	      boolean hasData=false;
	      boolean hasDataData=false;

	      
	      String result = executeGetRequest(Services.FETCH_RATING_URL);
	      Log.e("FECTH RATING", ":"+result);
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
	        	  hasData=true;
	          }else{
	        	  hasData=false;
	          }		          
	          Assert.assertTrue("Theres no data" , hasData);
	          
	          JSONObject dataObject= dataArray.getJSONObject(0);
	          JSONArray dataDataArray=dataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          
	          if(dataDataArray.length()>0){
	        	  hasDataData=true;
	          }else{
	        	  hasDataData=false;
	          }		          
	          Assert.assertTrue("Theres no Data" , hasDataData);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_07_FetchOrder_success() throws Throwable {
	      print("Starting FECTH ORDER test - successs");
	      boolean hasData=false;
	      boolean hasDataData=false;

	      
	      String result = executeGetRequest(Services.FETCH_ORDER_URL);
	      Log.e("FECTH ORDER", ":"+result);
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
	        	  hasData=true;
	          }else{
	        	  hasData=false;
	          }		          
	          Assert.assertTrue("Theres no data" , hasData);
	          
	          JSONObject dataObject= dataArray.getJSONObject(0);
	          JSONArray dataDataArray=dataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          
	          if(dataDataArray.length()>0){
	        	  hasDataData=true;
	          }else{
	        	  hasDataData=false;
	          }		          
	          Assert.assertTrue("Theres no Data" , hasDataData);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
}
