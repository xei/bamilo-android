package pt.rocket.framework.database;

import java.util.ArrayList;

import pt.rocket.framework.objects.LastViewed;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class is a helper to manage the Last Viewed products on database.
 * @author Manuel Silva
 *
 */
public class LastViewedTableHelper {
	
	private static final String TAG = LastViewedTableHelper.class.getSimpleName();
	
	// Table Name
	public static final String TABLE = "last_viewed";
	
	private static final int MAX_SAVED_PRODUCTS = 9;
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _PRODUCT_SKU = "product_sku";
	public static final String _PRODUCT_NAME = "product_name";
	public static final String _PRODUCT_PRICE = "product_price";
	public static final String _PRODUCT_URL = "product_url";
	public static final String _IMAGE_URL = "image_url";
	
	// Create table
    public static final String CREATE = 
    		"CREATE TABLE " + TABLE + " (" + 
    				_ID +			" INTEGER PRIMARY KEY, " +
    				_PRODUCT_SKU +		" TEXT," + 
    				_PRODUCT_NAME +		" TEXT," + 
    				_PRODUCT_PRICE +		" TEXT," + 
    				_PRODUCT_URL +		" TEXT," + 
    				_IMAGE_URL +	" TEXT" + 
    				 ")";

    /**
     * Insert viewed product into database
     * @param product_sku
     * @param product_name
     * @param product_price
     * @param product_url
     * @param image_url
     */
    public static void insertViewedProduct(Context ctx, String product_sku, String product_name, String product_price, String product_url, String image_url){
        
        if(!verifyIfExist(product_sku)){
        	if(getLastViewedEntriesCount() == MAX_SAVED_PRODUCTS){
        		removeOldestEntry();
        	}
        	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        	ContentValues values = new ContentValues();
            values.put(LastViewedTableHelper._PRODUCT_SKU, product_sku);
            values.put(LastViewedTableHelper._PRODUCT_NAME, product_name);
            values.put(LastViewedTableHelper._PRODUCT_PRICE, product_price);
            values.put(LastViewedTableHelper._PRODUCT_URL, product_url);
            values.put(LastViewedTableHelper._IMAGE_URL, image_url);
            db.insert(LastViewedTableHelper.TABLE, null, values);	
            db.close();
        }
    }
    
    /**
     * Insert viewed product into database
     * @param values
     */
    public static void insertViewedProduct(ContentValues values){
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        db.insert(LastViewedTableHelper.TABLE, null, values);
        db.close();
    }

    /**
     * Verifies if the product is already on the last viewed list
     * @param product_sku
     * @return
     */
    public static boolean verifyIfExist(String product_sku){
    	boolean result = false;
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
    	String query = "select count(*) from "+TABLE+" where "+_PRODUCT_SKU + " = '"+product_sku+"'";
    	Log.i(TAG, "SQL RESULT query :  "+query);

		Cursor cursor = db.rawQuery(query, null);
    	if (cursor != null && cursor.getCount() >0 ) {
			cursor.moveToFirst();
			if(cursor.getInt(0)>= 1){
				result = true;
			} else {
				result = false;
			}
			// Log result
			Log.i(TAG, "SQL RESULT: " + cursor.getInt(0)+ " result is : "+result );
		}
    	
		// Validate cursor
		if(cursor != null){
			cursor.close();
		}
		
		db.close();
    	
		return result;
    }
    
    /**
     * Get number of entries
     * @return
     */
    public static int getLastViewedEntriesCount(){
    	int result = 0;
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
    	String query = "select count(*) from "+TABLE;
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor != null && cursor.getCount() >0 ) {
			cursor.moveToFirst();
			result = cursor.getInt(0);
			// Log result
			Log.i(TAG, "SQL RESULT: " + result);
		}
		// Validate cursor
		if(cursor != null){
			cursor.close();
		}
		
		db.close();
		return result;
    }

    /**
     * Get the last viewed list of entries
     * @return
     */
    public static ArrayList<LastViewed> getLastViewedList(){
    	ArrayList<LastViewed> lastViewed = new ArrayList<LastViewed>();
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
    	String query = "select * from "+TABLE+" order by "+_ID+" desc";
    	Cursor cursor = db.rawQuery(query, null);
    	if (cursor != null && cursor.getCount() >0 ) {
    		while (cursor.moveToNext()) {
    			LastViewed lViewed = new LastViewed();
    			lViewed.setProductSku(cursor.getString(1));
    			lViewed.setProductName(cursor.getString(2));
    			lViewed.setProductPrice(cursor.getString(3));
    			lViewed.setProductUrl(cursor.getString(4));
    			lViewed.setImageUrl(cursor.getString(5));
    			lastViewed.add(lViewed);
    		}
		}
    	
		// Validate cursor
		if(cursor != null){
			cursor.close();
		}
		
		db.close();
		return lastViewed;
    }
    
    /**
     * Remove oldest entry from database
     */
    public static void removeOldestEntry(){
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
    	String query = "delete from "+TABLE+" where id in (select id FROM "+TABLE+" order by id asc limit 1)";
    	db.execSQL(query);
    	db.close();
    }
    
    /**
     * Delete all LastViewes
     */
    public static void deleteAllLastViewed() { 
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLE, null, null);
        db.close();
    }

	 /**
	  * Clears the Last Viewed table
	  * 
	  * @param db
	  */
	 public static void clearLastViewed(SQLiteDatabase db) {
		 db.delete(TABLE, null, null);
	 }
    
}
