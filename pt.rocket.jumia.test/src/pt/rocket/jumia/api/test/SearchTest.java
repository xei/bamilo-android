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

	      String query="?q=ad";
	      
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
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the data array", resultArray);
	          if(resultArray.length()>0){
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no Forms" , hasForms);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	  
	  public void test_02_SearchItemsForPage_success() throws Throwable {
	      print("Starting SEARCH ITEMS test - successs");
	      boolean hasPaging=false;

	      int product_for_page=6;
	      
	      String query="?q=ad";
	      String items="&maxitems="+product_for_page;
	      
	      String result = executeGetRequest(Services.SEARCH_SEARCH_URL+query+items);
	      Log.e("SEARCH ITEMS", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the results array", resultArray);
	          if(resultArray.length()<=product_for_page){
	        	  hasPaging=true;
	          }else{
	        	  hasPaging=false;
	          }		          
	          Assert.assertTrue("Theres no pagging" , hasPaging);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	  public void test_03_SearchPaged_success() throws Throwable {
	      print("Starting SEARCH Pagging test - successs");
	      boolean hasPaging=false;

	      int product_for_page=6;
	      int page_num=2;
	      
	      String query="?q=ad";
	      String items="&maxitems="+product_for_page;
	      String paged="&page="+page_num;
	      
	      String result = executeGetRequest(Services.SEARCH_SEARCH_URL+query+items+paged);
	      Log.e("SEARCH Pagging", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the result array", resultArray);
	          if(resultArray.length()<=product_for_page){
	        	  hasPaging=true;
	          }else{
	        	  hasPaging=false;
	          }		          
	          Assert.assertTrue("Theres no results" , hasPaging);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_04_SearchSortPriceAsc_success() throws Throwable {
	      print("Starting SEARCH SORT PRICR ASC test - successs");
	      boolean hasPaging=false;

	      int product_for_page=6;
	      int page_num=1;
	      String sort="price";
	      String dir="asc";
	      
	      String query="?q=nik";
	      String items="&maxitems="+product_for_page;
	      String paged="&page="+page_num;
	      String sorted="&sort="+sort;
	      String direction="&dir="+dir;
	      
	      String result = executeGetRequest(Services.SEARCH_SEARCH_URL+query+items+paged+sorted+direction);
	      Log.e("SEARCH SORT PRICR ASC", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the results array", resultArray);
	          if(resultArray.length()<=product_for_page){
	        	  hasPaging=true;
	          }else{
	        	  hasPaging=false;
	          }		          
	          Assert.assertTrue("Theres no results" , hasPaging);
	          JSONObject resultObject= new JSONObject();
	          resultObject= resultArray.getJSONObject(0);
        	  Double lowerPrice= Double.parseDouble(resultObject.getJSONObject(JsonConstants.JSON_DATA_TAG).getString(JsonConstants.JSON_PRICE_TAG));
	          for (int i = 0; i < resultArray.length(); i++) {
	        	  resultObject= resultArray.getJSONObject(i);
	        	  
	        	  if( Double.parseDouble(resultObject.getJSONObject(JsonConstants.JSON_DATA_TAG).getString(JsonConstants.JSON_PRICE_TAG))<lowerPrice){
	        		  Assert.assertTrue("The products aer not ordered by price asc" , false);
	        	  }

			}
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  
	  public void test_05_SearchSortPriceDesc_success() throws Throwable {
	      print("Starting SEARCH SORT PRICR DESC test - successs");
	      boolean hasPaging=false;

	      int product_for_page=6;
	      int page_num=1;
	      String sort="price";
	      String dir="desc";
	      
	      String query="?q=nik";
	      String items="&maxitems="+product_for_page;
	      String paged="&page="+page_num;
	      String sorted="&sort="+sort;
	      String direction="&dir="+dir;
	      
	      String result = executeGetRequest(Services.SEARCH_SEARCH_URL+query+items+paged+sorted+direction);
	      Log.e("SEARCH SORT PRICR DESC", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the result array", resultArray);
	          if(resultArray.length()<=product_for_page){
	        	  hasPaging=true;
	          }else{
	        	  hasPaging=false;
	          }		          
	          Assert.assertTrue("Theres no results" , hasPaging);
	          JSONObject resultObject= new JSONObject();
	          resultObject= resultArray.getJSONObject(0);
        	  Double higherPrice= Double.parseDouble(resultObject.getJSONObject(JsonConstants.JSON_DATA_TAG).getString(JsonConstants.JSON_PRICE_TAG));
	          for (int i = 0; i < resultArray.length(); i++) {
	        	  resultObject= resultArray.getJSONObject(i);
	        	  
	        	  if( Double.parseDouble(resultObject.getJSONObject(JsonConstants.JSON_DATA_TAG).getString(JsonConstants.JSON_PRICE_TAG))>higherPrice){
	        		  Assert.assertTrue("The products aer not ordered by price desc" , false);
	        	  }

			}
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  
	  public void test_06_SuggestItemsForPage_success() throws Throwable {
	      print("Starting SUGGEST test - successs");
	      boolean hasPaging=false;

	      int product_for_page=11;
	      
	      String query="?q=ni";
	      String items="&maxitems="+product_for_page;
	      
	      String result = executeGetRequest(Services.SEARCH_SUGESTION_URL+query+items);
	      Log.e("SUGGEST", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray suggestArray= metadataObject.getJSONArray(JsonConstants.JSON_SUGGESTION_TAG);
	          Assert.assertNotNull("The json does not contain the suggestions array", suggestArray);
	          if(suggestArray.length()<=product_for_page){
	        	  hasPaging=true;
	          }else{
	        	  hasPaging=false;
	          }		          
	          Assert.assertTrue("Theres no suggestions" , hasPaging);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_07_SearchAllProducts_success() throws Throwable {
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
	          Assert.assertNotNull("The json does not contain the result array", resultArray);
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
	  
	  
	  public void test_08_SearchSpecialPrice_success() throws Throwable {
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
	          Assert.assertNotNull("The json does not contain the result array", resultArray);
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
	  
	  public void test_09_SearchCategory_success() throws Throwable {
	      print("Starting SEARCH CATEGORY test - successs");
	      boolean hasResult=false;

	      String category="/womens-dresses"; 
	      
	      String result = executeGetRequest(Services.SEARCH_CATEGORIES_URL+category);
	      Log.e("SEARCH CATEGORY", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray dataArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the result array", dataArray);
	          if(dataArray.length()>0){
	        	  hasResult=true;
	          }else{
	        	  hasResult=false;
	          }		          
	          Assert.assertTrue("Theres no category "+ category , hasResult);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }

	  public void test_10_SearchBrand_success() throws Throwable {
	      print("Starting SEARCH BRAND test - successs");
	      boolean hasResult=false;
	      
	      String brand="/nike"; 
	      
	      String result = executeGetRequest(Services.SEARCH_CATEGORIES_URL+brand);
	      Log.e("SEARCH BRAND", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the result array", resultArray);
	          if(resultArray.length()>0){
	        	  hasResult=true;
	          }else{
	        	  hasResult=false;
	          }		          
	          Assert.assertTrue("Theres no brand "+brand  , hasResult);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }

	  
	  public void test_11_SearchDirectBrand_success() throws Throwable {
	      print("Starting SEARCH BRAND test - successs");
	      boolean hasResult=false;
	      
	      String brand="/nike"; 
	      
	      String result = executeGetRequest(brand);
	      Log.e("SEARCH BRAND", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          JSONArray resultArray= metadataObject.getJSONArray(JsonConstants.JSON_RESULTS_TAG);
	          Assert.assertNotNull("The json does not contain the result array", resultArray);
	          if(resultArray.length()>0){
	        	  hasResult=true;
	          }else{
	        	  hasResult=false;
	          }		          
	          Assert.assertTrue("Theres no brand "+brand  , hasResult);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_12_SearchCategoryCount_success() throws Throwable {
	      print("Starting SEARCH CATEGORY test - successs");
	      boolean hasResult=false;

	      String category="/womens-dresses/count"; 
	      
	      String result = executeGetRequest(Services.SEARCH_CATEGORIES_URL+category);
	      Log.e("SEARCH COUNT CATEGORY", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          String count=metadataObject.getString(JsonConstants.JSON_PRODUCT_COUNT_TAG);
	          
	          if(Integer.parseInt(count)>0){
	        	  hasResult=true;
	          }else{
	        	  hasResult=false;
	          }		          
	          Assert.assertTrue("Theres no category "+ category , hasResult);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }

	  public void test_13_SearchBrandCount_success() throws Throwable {
	      print("Starting SEARCH BRAND test - successs");
	      boolean hasResult=false;
	      
	      String brand="/nike/count"; 
	      
	      String result = executeGetRequest(Services.SEARCH_CATEGORIES_URL+brand);
	      Log.e("SEARCH COUNT BRAND", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is false \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metadataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the Metadata object", metadataObject);
	          
	          String count=metadataObject.getString(JsonConstants.JSON_PRODUCT_COUNT_TAG);
	          
	          if(Integer.parseInt(count)>0){
	        	  hasResult=true;
	          }else{
	        	  hasResult=false;
	          }		          
	          Assert.assertTrue("Theres no brand "+brand  , hasResult);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
}
