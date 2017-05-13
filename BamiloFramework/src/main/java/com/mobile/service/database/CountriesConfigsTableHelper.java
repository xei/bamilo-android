package com.mobile.service.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.mobile.service.objects.configs.CountryObject;
import com.mobile.service.objects.configs.Languages;
import com.mobile.service.utils.TextUtils;

import java.util.ArrayList;

/**
 * This class is a helper to manage the Countries Configurations on the database.
 * @author Manuel Silva
 *
 */
public class CountriesConfigsTableHelper extends BaseTable {
	
	private static final String TAG = CountriesConfigsTableHelper.class.getSimpleName();
	
	// Table Name
	public static final String TABLE_NAME = "countries_configs";
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _COUNTRY_NAME = "country_name";
	public static final String _COUNTRY_URL = "country_url";
	public static final String _COUNTRY_FLAG = "country_flag";
	public static final String _COUNTRY_ISO = "country_iso";
	public static final String _COUNTRY_FORCE_HTTPS = "country_force_https";
	public static final String _COUNTRY_IS_LIVE = "country_is_live";
	public static final String _COUNTRY_LANGUAGES = "country_languages";
	
	/*
	 * (non-Javadoc)
	 * @see com.mobile.newFramework.database.BaseTable#getName()
	 */
    @Override
    public String getName() {
        return TABLE_NAME;
    }
	
    /*
     * (non-Javadoc)
     * @see com.mobile.newFramework.database.BaseTable#getUpgradeType()
     */
    @Override
	@DarwinDatabaseHelper.UpgradeType
    public int getUpgradeType() {
        return DarwinDatabaseHelper.PERSIST;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.newFramework.database.BaseTable#create(java.lang.String)
     */
    @Override
    public String create() {
        return "CREATE TABLE %s (" +
                _ID +           " INTEGER PRIMARY KEY, " +
                _COUNTRY_NAME +     " TEXT," + 
                _COUNTRY_URL +      " TEXT," + 
                _COUNTRY_FLAG +     " TEXT," +
                _COUNTRY_ISO +      " TEXT," + 
                _COUNTRY_FORCE_HTTPS +      " INTEGER," +
                _COUNTRY_IS_LIVE +      " INTEGER," +
				_COUNTRY_LANGUAGES + " TEXT" +
                 ")";
    }
    
    /*
     * ################## CRUD ##################  
     */
    
    /**
     * Insert a Country into Country Configurations Table
     */
    public static void insertCountry(SQLiteDatabase db, String name, String url, String flag, String iso, boolean force_https, boolean is_live, String languages){
        	ContentValues values = new ContentValues();
            values.put(CountriesConfigsTableHelper._COUNTRY_NAME, name);
            values.put(CountriesConfigsTableHelper._COUNTRY_URL, url);
            values.put(CountriesConfigsTableHelper._COUNTRY_FLAG, flag);
            values.put(CountriesConfigsTableHelper._COUNTRY_ISO, iso);
            values.put(CountriesConfigsTableHelper._COUNTRY_FORCE_HTTPS, force_https ? 1 : 0);
            values.put(CountriesConfigsTableHelper._COUNTRY_IS_LIVE, is_live ? 1 : 0);
			values.put(CountriesConfigsTableHelper._COUNTRY_LANGUAGES, languages);
			db.insert(CountriesConfigsTableHelper.TABLE_NAME, null, values);
    }
    
    /**
     * Insert a set of Countries into Countries Configs DB
     */
    public static void insertCountriesConfigs(ArrayList<CountryObject> countries){
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance()
				.getWritableDatabase();
    	try {
			db.beginTransaction();
			
			for (CountryObject mCountryObject : countries) {
				insertCountry(db, mCountryObject.getCountryName(), mCountryObject.getCountryUrl(),
						mCountryObject.getCountryFlag(), mCountryObject.getCountryIso(), mCountryObject.isCountryForceHttps(),
						mCountryObject.isCountryIsLive(), new Gson().toJson(mCountryObject.getLanguages()));
			}
			
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
    } 
    
    public static ArrayList<CountryObject> getCountriesList(){
    	ArrayList<CountryObject> countries = new ArrayList<>();
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance()
				.getWritableDatabase();
    	
    	String query = "select * from " + TABLE_NAME;
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor != null && cursor.getCount() >0 ) {
    		while (cursor.moveToNext()) {
    			CountryObject mCountry = new CountryObject();
    			mCountry.setCountryName(cursor.getString(1));
//    			//Print.i(TAG, "code1mcountry : " + mCountry.getCountryName());
    			mCountry.setCountryUrl(cursor.getString(2));
    			mCountry.setCountryFlag(cursor.getString(3));
    			mCountry.setCountryIso(cursor.getString(4));
    			mCountry.setCountryForceHttps(cursor.getInt(5) == 1);
    			mCountry.setCountryIsLive(cursor.getInt(6) == 1);
				String languages = cursor.getString(7);
				mCountry.setLanguages(TextUtils.isEmpty(languages) ? null : new Gson().fromJson(languages, Languages.class));
    			countries.add(mCountry);
    		}
		}
    	
		// Validate cursor
		if(cursor != null){
			cursor.close();
		}
		
		db.close();
    	return countries;
    }
    
    /**
     * Delete all Countries Configurations
     */
    public static void deleteAllCountriesConfigs() { 
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
    
}
