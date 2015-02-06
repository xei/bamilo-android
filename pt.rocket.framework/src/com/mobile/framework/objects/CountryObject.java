/**
 * @author Manuel Silva
 * 
 * 
 */
package com.mobile.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * Country Object class. Holds all the fields of the Country Object.
 * 
 * name: "CÃ´te d'Ivoire", 
 * url: "https://www.jumia.ci/mobapi/", 
 * flag: "http://jumia.com/images/mobapi/flag_ivorycoast.png", 
 * map_images: { 
 * 	mdpi: "http://jumia.com/images/mobapi/map_mdpi_ci.png", 
 * 	hdpi: "http://jumia.com/images/mobapi/map_hdpi_ci.png", 
 * 	xhdpi:"http://jumia.com/images/mobapi/map_xhdpi_ci.png" 
 * },
 * country_iso: "CI",
 * force_https: true, 
 * is_live: 1
 * 
 * @author Manuel Silva
 */
public class CountryObject implements IJSONSerializable, Parcelable {

	protected static final String TAG = CountryObject.class.getName();
	
	private String country_id;
	private String country_name;
	private String country_url;
	private String country_flag;
	//private String country_map_mdpi;
	//private String country_map_hdpi;
	//private String country_map_xhdpi;
	private String country_iso;
	private boolean country_force_https;
	private boolean country_is_live;

	/**
	 * Empty Constructor
	 */
	public CountryObject(){
		this.setCountryId("");
		this.setCountryName("");
		this.setCountryUrl("");
		this.setCountryFlag("");
		//this.setCountryMapMdpi("");
		//this.setCountryMapHdpi("");
		//this.setCountryMapXhdpi("");
		this.setCountryIso("");
		this.setCountryForceHttps(false);
		this.setCountryIsLive(false);
	}
	
	/**
	 * Full Constructor
	 * 
	 * @param id
	 * @param name
	 * @param url
	 * @param flag
	 * @param iso
	 * @param force_https
	 * @param is_live
	 */
	public CountryObject(String id, String name, String url, String flag, String iso, boolean force_https, boolean is_live) {
		this.setCountryId(id);
		this.setCountryName(name);
		this.setCountryUrl(url);
		this.setCountryFlag(flag);
		//this.setCountryMapMdpi(map_mdpi);
		//this.setCountryMapHdpi(map_hdpi);
		//this.setCountryMapXhdpi(map_xhdpi);
		this.setCountryIso(iso);
		this.setCountryForceHttps(force_https);
		this.setCountryIsLive(is_live);
	}

	/**
	 * ########### Parcelable ###########
	 * 
	 * @author Manuel Silva
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(country_id);
		dest.writeString(country_name);
		dest.writeString(country_url);
		dest.writeString(country_flag);
		//dest.writeString(country_map_mdpi);
		//dest.writeString(country_map_hdpi);
		//dest.writeString(country_map_xhdpi);
		dest.writeString(country_iso);
		dest.writeBooleanArray(new boolean[] { country_force_https, country_is_live });
	}

	/**
	 * Parcel constructor
	 * 
	 * @param in
	 */
	private CountryObject(Parcel in) {
		country_id = in.readString();
		country_name = in.readString();
		country_url = in.readString();
		country_flag = in.readString();
		//country_map_mdpi = in.readString();
		//country_map_hdpi = in.readString();
		//country_map_xhdpi = in.readString();
		country_iso = in.readString();
		in.readBooleanArray(new boolean[] { country_force_https, country_is_live });
	}

	/**
	 * @return the country_name
	 */
	public String getCountryName() {
		return country_name;
	}

	/**
	 * @param country_name
	 *            the country_name to set
	 */
	public void setCountryName(String country_name) {
		this.country_name = country_name;
	}

	/**
	 * @return the country_url
	 */
	public String getCountryUrl() {
		return country_url;
	}

	/**
	 * @param country_url
	 *            the country_url to set
	 */
	public void setCountryUrl(String country_url) {
		this.country_url = country_url;
	}

	/**
	 * @return the country_flag
	 */
	public String getCountryFlag() {
		return country_flag;
	}

	/**
	 * @param country_flag
	 *            the country_flag to set
	 */
	public void setCountryFlag(String country_flag) {
		this.country_flag = country_flag;
	}

//	/**
//	 * @return the country_map_mdpi
//	 */
//	public String getCountryMapMdpi() {
//		return country_map_mdpi;
//	}
//
//	/**
//	 * @param country_map_mdpi
//	 *            the country_map_mdpi to set
//	 */
//	public void setCountryMapMdpi(String country_map_mdpi) {
//		this.country_map_mdpi = country_map_mdpi;
//	}
//
//	/**
//	 * @return the country_map_hdpi
//	 */
//	public String getCountryMapHdpi() {
//		return country_map_hdpi;
//	}
//
//	/**
//	 * @param country_map_hdpi
//	 *            the country_map_hdpi to set
//	 */
//	public void setCountryMapHdpi(String country_map_hdpi) {
//		this.country_map_hdpi = country_map_hdpi;
//	}
//
//	/**
//	 * @return the country_map_xhdpi
//	 */
//	public String getCountryMapXhdpi() {
//		return country_map_xhdpi;
//	}
//
//	/**
//	 * @param country_map_xhdpi
//	 *            the country_map_xhdpi to set
//	 */
//	public void setCountryMapXhdpi(String country_map_xhdpi) {
//		this.country_map_xhdpi = country_map_xhdpi;
//	}

	/**
	 * @return the country_iso
	 */
	public String getCountryIso() {
		return country_iso;
	}

	/**
	 * @param country_iso
	 *            the country_iso to set
	 */
	public void setCountryIso(String country_iso) {
		this.country_iso = country_iso;
	}

	/**
	 * @return the country_force_https
	 */
	public boolean isCountryForceHttps() {
		return country_force_https;
	}

	/**
	 * @param country_force_https
	 *            the country_force_https to set
	 */
	public void setCountryForceHttps(boolean country_force_https) {
		this.country_force_https = country_force_https;
	}

	/**
	 * @return the country_is_live
	 */
	public boolean isCountryIsLive() {
		return country_is_live;
	}

	/**
	 * @param country_is_live
	 *            the country_is_live to set
	 */
	public void setCountryIsLive(boolean country_is_live) {
		this.country_is_live = country_is_live;
	}

	/**
	 * @return the country_id
	 */
	public String getCountryId() {
		return country_id;
	}

	/**
	 * @param country_id the country_id to set
	 */
	public void setCountryId(String country_id) {
		this.country_id = country_id;
	}

	/**
	 * Create parcelable
	 */
	public static final Parcelable.Creator<CountryObject> CREATOR = new Parcelable.Creator<CountryObject>() {
		public CountryObject createFromParcel(Parcel in) {
			return new CountryObject(in);
		}

		public CountryObject[] newArray(int size) {
			return new CountryObject[size];
		}
	};
	

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		country_name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
		country_url = jsonObject.optString(RestConstants.JSON_URL_TAG);
		if(country_url != null ){
			country_url = country_url.replace("http://www", "www");
			country_url = country_url.replace("http://alice-staging", "alice-staging");
			country_url = country_url.replace("https://www", "www");
			country_url = country_url.replace("https://alice-staging", "alice-staging");
			country_url = country_url.replace("/mobapi/", "");
		}
		country_flag = jsonObject.optString(RestConstants.JSON_FLAG_TAG);
//		JSONObject mapImages = jsonObject.optJSONObject(RestConstants.JSON_MAP_IMAGES_TAG);
//		country_map_mdpi = mapImages.optString(RestConstants.JSON_MDPI_TAG);
//		country_map_hdpi = mapImages.optString(RestConstants.JSON_HDPI_TAG);
//		country_map_xhdpi = mapImages.optString(RestConstants.JSON_XHDPI_TAG);
		country_iso = jsonObject.optString(RestConstants.JSON_COUNTRY_ISO);
		country_force_https = jsonObject.optBoolean(RestConstants.JSON_FORCE_HTTPS, false);
		country_is_live = jsonObject.optInt(RestConstants.JSON_IS_LIVE, 0) == 1 ? true : false;
		return true;
	}

	@Override
	public JSONObject toJSON() {
		return null;
	}
}
