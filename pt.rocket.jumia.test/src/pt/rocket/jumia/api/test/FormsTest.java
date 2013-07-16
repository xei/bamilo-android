package pt.rocket.jumia.api.test;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.jumia.api.constants.JsonConstants;
import pt.rocket.jumia.api.constants.Services;
import pt.rocket.jumia.api.constants.StaticJson;
import android.util.Log;

public class FormsTest extends ApiBaseTest {
	private final static int NUM_RATING_FIELDS=3;
	private final static int NUM_REGISTER_FIELDS=12;
	private final static int NUM_EDIT_FIELDS=6;
	private final static int NUM_LOGIN_FIELDS=2;
	private final static int NUM_FORGOT_FIELDS=1;
	private final static int NUM_CONFIRM_FIELDS=1;
	private final static int NUM_CHANGE_FIELDS=2;
	private final static int NUM_ADDRESSCREATE_FIELDS=17;
	private final static int NUM_ADDRESSEDIT_FIELDS=17;
	
	private final static int NUM_PAYMENT_TEST_FIELDS=1;
	private final static int NUM_PAYMENT_RECURRING_FIELDS=2;
	private final static int NUM_PAYMENT_CC_FIELDS=6;
	
	  public void test_01_FormsIndex_success() throws Throwable {
	      print("Starting FORMS INDEX test - successs");
	      boolean hasForms=false;

	      String result = executeGetRequest(Services.FORM_INDEX_URL);
	      Log.e("FORM INDEX", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no Forms" , hasForms);
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_02_FormsRating_success() throws Throwable {
	      print("Starting FORMS INDEX test - successs");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_RATING_URL);
	      Log.e("FORM RATING", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no Rating Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no Rating Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
	        	  Assert.assertTrue("JSon was modified" , true);
	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_RATING_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }

	  
	  public void test_03_FormsRegister_success() throws Throwable {
	      print("Starting FORMS INDEX test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_REGISTER_URL);
	      Log.e("FORM REGISTER", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no Rating Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no Register Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
//	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
//	        	  Assert.assertTrue("JSon was modified" , true);
//	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_REGISTER_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
//	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  
	  public void test_04_FormsLogin_success() throws Throwable {
	      print("Starting FORMS LOGIN test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_LOGIN_URL);
	      Log.e("FORM LOGIN", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no Login Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no Login Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
//	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
//	        	  Assert.assertTrue("JSon was modified" , true);
//	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_LOGIN_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
//	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  
	  public void test_05_FormsEdit_success() throws Throwable {
	      print("Starting FORMS EDIT test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_EDIT_URL);
	      Log.e("FORM EDIT", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no edit Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no edit Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
//	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
//	        	  Assert.assertTrue("JSon was modified" , true);
//	          }else{
	        	  boolean allFileds= false;
//	        	  Log.e("#########", ":"+filedsArray.length());
//	        	  for (int i = 0; i < filedsArray.length(); i++) {
//	        		  Log.e("$$$$$$$$$", ":"+((JSONObject)filedsArray.getJSONObject(i)).getString("label"));
//				}
	        	  if(filedsArray.length()==NUM_EDIT_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
//	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  
	  public void test_06_FormsForgotPassword_success() throws Throwable {
	      print("Starting FORMS FORGOT test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_FORGOTPASS_URL);
	      Log.e("FORM FORGOT", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no forgot password Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no forgot password Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
//	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
//	        	  Assert.assertTrue("JSon was modified" , true);
//	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_FORGOT_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
//	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  
	  public void test_07_FormsConfirmRegister_success() throws Throwable {
	      print("Starting FORMS REGISTER test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_CONFIRMREG_URL);
	      Log.e("FORM CONFIRM REGISTER", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no confirm Register Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no confirm Register Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
//	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
//	        	  Assert.assertTrue("JSon was modified" , true);
//	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_CONFIRM_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
//	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  
	  public void test_08_FormsAddressCreate_success() throws Throwable {
	      print("Starting FORMS ADDRESS CREATE test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_ADDRESSCREATE_URL);
	      Log.e("FORM ADDRESS CREATE", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no create address Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no create address Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
//	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
//	        	  Assert.assertTrue("JSon was modified" , true);
//	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_ADDRESSCREATE_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
//	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  
	  public void test_09_FormsAddressEdit_success() throws Throwable {
	      print("Starting FORMS REGISTER test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_ADDRESSEDIT_URL);
	      Log.e("FORM ADDRESS EDIT", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no address edit Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no address edit Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
//	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
//	        	  Assert.assertTrue("JSon was modified" , true);
//	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_ADDRESSEDIT_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
//	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  

	  
	  public void test_10_FormsChangePassword_success() throws Throwable {
	      print("Starting FORMS CHANGE PASS test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_CHANGEPASS_URL);
	      Log.e("FORM CHANGE PASS", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no change password Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no change password Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
//	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
//	        	  Assert.assertTrue("JSon was modified" , true);
//	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_CHANGE_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
//	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_11_PaymentMethodTest_success() throws Throwable {
	      print("Starting FORMS PAYMENT test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_PAYMENTMETHODS_URL);
	      Log.e("FORM PAYMENT TEST METHOD", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no payment test method Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(0);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no payment test method Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
	        	  Assert.assertTrue("JSon was modified" , true);
	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_PAYMENT_TEST_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_12_PaymentMethodRecurring_success() throws Throwable {
	      print("Starting FORMS RECURRING test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_PAYMENTMETHODS_URL);
	      Log.e("FORM PAYMENT RECURRING METHOD", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no payment recurring method Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(1);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no payment recurring method Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
	        	  Assert.assertTrue("JSon was modified" , true);
	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_PAYMENT_RECURRING_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
	  public void test_13_PaymentMethodCreditCard_success() throws Throwable {
	      print("Starting FORMS PAYMENT CC test - success");
	      boolean hasForms=false;
	      boolean hasFields=false;

	      String result = executeGetRequest(Services.FORM_PAYMENTMETHODS_URL);
	      Log.e("FORM PAYMENT CC METHOD", ":"+result);
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
	        	  hasForms=true;
	          }else{
	        	  hasForms=false;
	          }		          
	          Assert.assertTrue("Theres no payment CC method Forms" , hasForms);
	          
	          JSONObject dataObject = dataArray.getJSONObject(2);
	          JSONArray filedsArray = dataObject.getJSONArray("fields");
	          
	          if(filedsArray.length()>0){
	        	  hasFields=true;
	          }else{
	        	  hasFields=false;
	          }		  
	          Assert.assertTrue("Theres no payment CC method Fields" , hasFields);
	          
	          Log.e("FIELDS:", ":"+filedsArray.toString());
	          
	          if(filedsArray.toString().equalsIgnoreCase(StaticJson.RATING_FIELD_JSON)){
	        	  Assert.assertTrue("JSon was modified" , true);
	          }else{
	        	  boolean allFileds= false;
	        	  if(filedsArray.length()==NUM_PAYMENT_CC_FIELDS){
	        		  allFileds=true;
	        	  }
	        	  Assert.assertTrue("Theres missing some fields" , allFileds);
	          }
	          
	          
	      } catch (JSONException e) {
	          Assert.fail(e.getMessage());
	      }
	  }
	  
}
