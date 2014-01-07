package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.util.Log;

public class BrandTest extends ApiBaseTest {

	

	
	  /**
	   * 
	   * @throws Throwable
	   */
	  public void test_01_Brands_success() throws Throwable {
	      print("Starting Brand test - successs");
	      boolean hasBrands=false;

	      String result = executeGetRequest(Services.BRAND_URL);
	      Log.e("BRAND", ":"+result);
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
	        	  hasBrands=true;
	          }else{
	        	  hasBrands=false;
	          }		          
	          Assert.assertTrue("Theres no Brands" , hasBrands);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	  
	  public void test_02_infoBrand_success() throws Throwable {
	      print("Starting Brand test - successs");
	      boolean hasBrands=false;
	      boolean hasDetail=false;

	      String result = executeGetRequest(Services.BRAND_URL);
	      Log.e("CATEGORY", ":"+result);
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
	        	  hasBrands=true;
	          }else{
	        	  hasBrands=false;
	          }		          
	          Assert.assertTrue("Theres no Brands" , hasBrands);
	          
	          //random number between 1 and dataArray.length
	          String urlKey="";
	          int rd=randomNum(1,dataArray.length()-1);
	          Log.e("RANDOM NUM", ":"+rd);
//	          for (int i = 0; i < dataArray.length(); i++) {
	        	  urlKey=((JSONObject)dataArray.getJSONObject(rd)).getJSONObject(JsonConstants.JSON_BRAND_BRAND_TAG).getString(JsonConstants.JSON_BRAND_URL_KEY_TAG);
	        	  Log.e("RANDOM URL KEY", ":"+urlKey);
//			}
	          String resultDetail= executeGetRequest(Services.BRAND_URL+urlKey);
	          
	          JSONObject jsonDetailObject = new JSONObject(resultDetail);
	          
	          Assert.assertTrue("Success is false \r\n" + result, jsonDetailObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	          
	          // check if the data content is valid
	          JSONObject metadataDetailObject= jsonDetailObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataDetailObject);
	          
	          JSONArray dataDetailArray= metadataDetailObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataDetailArray);
	          if(dataDetailArray.length()>0){
	        	  hasDetail=true;
	          }else{
	        	  hasDetail=false;
	          }		          
	          Assert.assertTrue("Theres no Brand Detail" , hasDetail);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
}
