package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import android.util.Log;

public class GetTeasersTest extends ApiBaseTest {

	
	public final static String GENERIC_MARKET = "GM";
	public final static String FASHION_MARKET = "fashion";
	
	  public void test_01_getTeasers_success() throws Throwable {
	      print("Starting Teasers test - successs");
	      boolean hasTeasers=false;
	      boolean has5Teasers=false;
	      boolean onlyGMNFashion=false;
//	      13 10 3
	      String result = executeGetRequest(Services.GETTEASERS_URL);
	      Log.e("Newsletter Validate FAIL", ":"+result);
	      try {
	          JSONObject jsonObject = new JSONObject(result);
//	          // Check if the status is true
	          Assert.assertTrue("Success is true \r\n" + result, jsonObject.getBoolean(JsonConstants.JSON_SUCCESS_TAG));
	//
	          // check if the data content is valid
	          JSONObject metdataObject= jsonObject.getJSONObject(JsonConstants.JSON_METADATA_TAG);
	          Assert.assertNotNull("The json does not contain the metadata object", metdataObject);
	          
	          JSONArray dataArray= metdataObject.getJSONArray(JsonConstants.JSON_DATA_TAG);
	          Assert.assertNotNull("The json does not contain the data array", dataArray);
	          if(dataArray.length()>0){
	        	  hasTeasers=true;
	          } 

	          Assert.assertTrue("Theres no Teasers" , hasTeasers);

	          
	          if(dataArray.length()==5){
	        	  has5Teasers=true;
	          }
	          
	          Assert.assertTrue("Does not have 5 teasers" , has5Teasers);
	          JSONObject dataObject= new JSONObject();
	          int count=0;
	          for (int i = 0; i < dataArray.length(); i++) {
	        	  dataObject = dataArray.getJSONObject(i);

	        	  if(FASHION_MARKET.equals(dataObject.getString(JsonConstants.JSON_HOMEPAGE_TAG)) || GENERIC_MARKET.equals(dataObject.getString(JsonConstants.JSON_HOMEPAGE_TAG))){
	        		  count++;
	        	  }
			}
	          
	          if(count==dataArray.length()){
	        	  onlyGMNFashion=true;
	          }
	          
	          Assert.assertTrue("incorrect homepage style" , onlyGMNFashion);
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	
	
}
