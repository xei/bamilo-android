package pt.rocket.framework.database;

import java.util.ArrayList;

import pt.rocket.framework.objects.CountryObject;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.akquinet.android.androlog.Log;

/**
 * This class is a helper to manage the Countries Configurations on the database.
 * @author Manuel Silva
 *
 */
public class CountriesConfigsTableHelper {
	
	private static final String TAG = CountriesConfigsTableHelper.class.getSimpleName();
	
	// Table Name
	public static final String TABLE = "countries_configs";
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _COUNTRY_NAME = "country_name";
	public static final String _COUNTRY_URL = "country_url";
	public static final String _COUNTRY_FLAG = "country_flag";
	public static final String _COUNTRY_MAP_IMAGE_MDPI = "country_image_mdpi";
	public static final String _COUNTRY_MAP_IMAGE_HDPI = "country_image_hdpi";
	public static final String _COUNTRY_MAP_IMAGE_XHDPI = "country_image_xhdpi";
	public static final String _COUNTRY_ISO = "country_iso";
	public static final String _COUNTRY_FORCE_HTTPS = "country_force_https";
	public static final String _COUNTRY_IS_LIVE = "country_is_live";
	
	// Create table
    public static final String CREATE = 
    		"CREATE TABLE " + TABLE + " (" + 
    				_ID +			" INTEGER PRIMARY KEY, " +
    				_COUNTRY_NAME +		" TEXT," + 
    				_COUNTRY_URL +		" TEXT," + 
    				_COUNTRY_FLAG +		" TEXT," + 
    				_COUNTRY_MAP_IMAGE_MDPI +		" TEXT," + 
    				_COUNTRY_MAP_IMAGE_HDPI +	" TEXT," + 
    				_COUNTRY_MAP_IMAGE_XHDPI +		" TEXT," +
    				_COUNTRY_ISO +		" TEXT," + 
    				_COUNTRY_FORCE_HTTPS +		" INTEGER," +
    				_COUNTRY_IS_LIVE +		" INTEGER" +
    				 ")";

    // Drop table
    public static final String DROP = "DROP TABLE IF EXISTS " + TABLE;
    
    /**
     * Insert a Country into Country Configurations Table
     * 
     * @param ctx
     * @param name
     * @param url
     * @param flag
     * @param map_image_mdpi
     * @param map_image_hdpi
     * @param map_image_xhdpi
     * @param iso
     * @param force_https
     * @param is_live
     */
    public static void insertCountry(SQLiteDatabase db, String name, String url, String flag, String map_image_mdpi, String map_image_hdpi
    		, String map_image_xhdpi, String iso, boolean force_https, boolean is_live){

        	ContentValues values = new ContentValues();
            values.put(CountriesConfigsTableHelper._COUNTRY_NAME, name);
            values.put(CountriesConfigsTableHelper._COUNTRY_URL, url);
            values.put(CountriesConfigsTableHelper._COUNTRY_FLAG, flag);
            values.put(CountriesConfigsTableHelper._COUNTRY_MAP_IMAGE_MDPI, map_image_mdpi);
            values.put(CountriesConfigsTableHelper._COUNTRY_MAP_IMAGE_HDPI, map_image_hdpi);
            values.put(CountriesConfigsTableHelper._COUNTRY_MAP_IMAGE_XHDPI, map_image_xhdpi);
            values.put(CountriesConfigsTableHelper._COUNTRY_ISO, iso);
            values.put(CountriesConfigsTableHelper._COUNTRY_FORCE_HTTPS, force_https ? 1 : 0);
            values.put(CountriesConfigsTableHelper._COUNTRY_IS_LIVE, is_live ? 1 : 0);
            db.insert(CountriesConfigsTableHelper.TABLE, null, values);
    }
    
    /**
     * Insert a set of Countries into Countries Configs DB
     * @param countries
     */
    public static void insertCountriesConfigs(ArrayList<CountryObject> countries){
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance()
				.getWritableDatabase();
    	try {
			db.beginTransaction();
			
			for (CountryObject mCountryObject : countries) {
				insertCountry(db, mCountryObject.getCountryName(), mCountryObject.getCountryUrl(),
                            mCountryObject.getCountryFlag(), mCountryObject.getCountryMapMdpi(), mCountryObject.getCountryMapHdpi(),
                            mCountryObject.getCountryMapXhdpi(), mCountryObject.getCountryIso(), mCountryObject.isCountryForceHttps(),
                            mCountryObject.isCountryIsLive());
			}
			
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
    } 
    
    public static ArrayList<CountryObject> getCountriesList(){
    	ArrayList<CountryObject> countries = new ArrayList<CountryObject>();
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance()
				.getWritableDatabase();
    	
    	String query = "select * from "+TABLE;
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor != null && cursor.getCount() >0 ) {
    		while (cursor.moveToNext()) {
    			CountryObject mCountry = new CountryObject();
    			mCountry.setCountryName(cursor.getString(1));
    			Log.i(TAG, "code1mcountry : "+mCountry.getCountryName());
    			mCountry.setCountryUrl(cursor.getString(2));
    			mCountry.setCountryFlag(cursor.getString(3));
    			mCountry.setCountryMapMdpi(cursor.getString(4));
    			mCountry.setCountryMapHdpi(cursor.getString(5));
    			mCountry.setCountryMapXhdpi(cursor.getString(6));
    			mCountry.setCountryIso(cursor.getString(7));
    			mCountry.setCountryForceHttps(cursor.getInt(8) == 1 ? true : false);
    			mCountry.setCountryIsLive(cursor.getInt(9) == 1 ? true : false);
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
        db.delete(TABLE, null, null);
        db.close();
    }

	 /**
	  * Clears the Countries Configs table
	  * 
	  * @param db
	  */
	 public static void clearCountriesConfigs(SQLiteDatabase db) {
		 db.delete(TABLE, null, null);
	 }
    
}
