package pt.rocket.framework.database;

import java.util.ArrayList;

import pt.rocket.framework.objects.LastViewed;
import pt.rocket.framework.objects.Product;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

/**
 * This class is a helper to manage the Related Items on database.
 * @author Manuel Silva
 *
 */
public class RelatedItemsTableHelper {
	
	private static final String TAG = RelatedItemsTableHelper.class.getSimpleName();
	
	// Table Name
	public static final String TABLE_RELATED = "related_items";
	
	private static final int MAX_SAVED_PRODUCTS = 20;
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _PRODUCT_SKU = "product_sku";
	public static final String _PRODUCT_NAME = "product_name";
	public static final String _PRODUCT_PRICE = "product_price";
	public static final String _PRODUCT_URL = "product_url";
	public static final String _IMAGE_URL = "image_url";
	
	// Create table
    public static final String CREATE = 
    		"CREATE TABLE " + TABLE_RELATED + " (" + 
    				_ID +			" INTEGER PRIMARY KEY, " +
    				_PRODUCT_SKU +		" TEXT," + 
    				_PRODUCT_NAME +		" TEXT," + 
    				_PRODUCT_PRICE +		" TEXT," + 
    				_PRODUCT_URL +		" TEXT," + 
    				_IMAGE_URL +	" TEXT" + 
    				 ")";

    /**
     * Insert related items into database
     * @param product_sku
     * @param product_name
     * @param product_price
     * @param product_url
     * @param image_url
     */
    public static void insertRelatedItem(SQLiteDatabase db, String product_sku, String product_name, String product_price, String product_url, String image_url){
    	// Validate sku
    	if(TextUtils.isEmpty(product_sku)) {
    		Log.w(TAG, "WARNING ON INSERT RELATED ITEM: SKU IS EMPTY");
    		return;
    	}
    	Log.i(TAG, "ON INSERT ITEM: " + product_sku);
    	// Insert
		ContentValues values = new ContentValues();
		values.put(RelatedItemsTableHelper._PRODUCT_SKU, product_sku);
		values.put(RelatedItemsTableHelper._PRODUCT_NAME, product_name);
		values.put(RelatedItemsTableHelper._PRODUCT_PRICE, product_price);
		values.put(RelatedItemsTableHelper._PRODUCT_URL, product_url);
		values.put(RelatedItemsTableHelper._IMAGE_URL, image_url);
		db.insertWithOnConflict(RelatedItemsTableHelper.TABLE_RELATED, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }
    
    /**
     * TODO
     * @param ctx
     * @param mProducts
     */
	public static void insertRelatedItems(Context ctx, ArrayList<Product> mProducts) {

		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		
		try {
			db.beginTransaction();
			
			int count = 19;
			for (Product product : mProducts) {
				insertRelatedItem(db, product.getSKU(), product.getBrand()
						+ " " + product.getName(), product.getSpecialPrice(),
						product.getUrl(),
						(product.getImages().size() == 0) ? "" : product
								.getImages().get(0).getUrl());
				if(count == MAX_SAVED_PRODUCTS)
					break;
				count++;
			}
			
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}

	}

	/**
	 * TODO
	 * @param ctx
	 * @param mProducts
	 */
	public static void insertRelatedItemsAndClear(Context ctx, ArrayList<Product> mProducts) {
		Log.d(TAG, "ON CLEAN AND INSERT: START");
		SQLiteDatabase db = null;
		try {
			db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
			// Begin
			db.beginTransaction();
			// Sync accesses
			synchronized (db) { cleanAndInsert(db, mProducts); }
			// Success
			db.setTransactionSuccessful();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Log.d(TAG, "ON CLEAN AND INSERT: FINISH");
			// Validate helper
			if (db != null) {
				db.endTransaction();
				db.close();
			} 
		}
	}
	
	private static void cleanAndInsert(SQLiteDatabase db, ArrayList<Product> mProducts) {
		clearRelatedItems(db);
		int count = 1;
		for (Product product : mProducts) {
			insertRelatedItem(db, 
							product.getSKU(), 
							product.getBrand() + " " + product.getName(), 
							product.getSpecialPrice(),
							product.getUrl(),
							(product.getImages().size() == 0) ? "" : product.getImages().get(0).getUrl());
			// Validate counter
			if(count == MAX_SAVED_PRODUCTS) break;
			// Inc counter
			count++;
		}
	}
	
    /**
     * Get the Related Items list of entries
     * @return
     */
    public static ArrayList<LastViewed> getRelatedItemsList(){
    	ArrayList<LastViewed> lastViewed = new ArrayList<LastViewed>();
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
    	String query ="select * from "+TABLE_RELATED+" order by "+_ID+" desc";
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
     * Delete all LastViewes
     */
    public static void deleteAllRelatedItems() { 
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLE_RELATED, null, null);
        db.close();
    }

	 /**
	  * Clears the Last Viewed table
	  * 
	  * @param db
	  */
	 public static void clearRelatedItems(SQLiteDatabase db) {
		 Log.d(TAG, "ON CLEAN TABLE");
		 db.delete(TABLE_RELATED, null, null);
	 }
    
}
