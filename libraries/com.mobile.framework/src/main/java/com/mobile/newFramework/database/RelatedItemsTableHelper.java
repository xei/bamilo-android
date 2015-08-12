package com.mobile.newFramework.database;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.mobile.newFramework.database.DarwinDatabaseHelper.TableType;
import com.mobile.newFramework.objects.product.NewProductPartial;
import com.mobile.newFramework.utils.output.Print;

import java.util.ArrayList;

/**
 * This class is a helper to manage the Related Items on database.
 * @author Manuel Silva
 *
 */
public class RelatedItemsTableHelper extends BaseTable {
	
	private static final String TAG = RelatedItemsTableHelper.class.getSimpleName();
	
	// Table Name
	public static final String TABLE_NAME = "related_items";
	
	private static final int MAX_SAVED_PRODUCTS = 20;
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _PRODUCT_SKU = "product_sku";
	public static final String _PRODUCT_BRAND = "product_brand";
	public static final String _PRODUCT_NAME = "product_name";
	public static final String _PRODUCT_PRICE = "product_price";
	public static final String _PRODUCT_URL = "product_url";
	public static final String _IMAGE_URL = "image_url";
	
	/*
	 * (non-Javadoc)
	 * @see com.mobile.newFramework.database.BaseTable#getUpgradeType()
	 */
    @Override
    public TableType getUpgradeType() {
        return TableType.PERSIST;
    }

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
     * @see com.mobile.newFramework.database.BaseTable#create(java.lang.String)
     */
    @Override
    public String create(String table) {
        return "CREATE TABLE " + table + " (" + 
                _ID +               " INTEGER PRIMARY KEY, " +
                _PRODUCT_SKU +      " TEXT," +
                _PRODUCT_BRAND +    " TEXT," + 
                _PRODUCT_NAME +     " TEXT," + 
                _PRODUCT_PRICE +    " TEXT," + 
                _PRODUCT_URL +      " TEXT," + 
                _IMAGE_URL +        " TEXT" + 
                 ")";
    }
    
    /*
     * ################## CRUD ##################  
     */

    /**
     * Insert related items into database
     */
    public static void insertRelatedItem(SQLiteDatabase db, String sku, String brand, String name, String price, String url, String image){
    	// Validate sku
    	if(TextUtils.isEmpty(sku)) {
    		Print.w(TAG, "WARNING ON INSERT RELATED ITEM: SKU IS EMPTY");
    		return;
    	}
    	// Insert
		ContentValues values = new ContentValues();
		values.put(RelatedItemsTableHelper._PRODUCT_SKU, sku);
		values.put(RelatedItemsTableHelper._PRODUCT_BRAND, brand);
		values.put(RelatedItemsTableHelper._PRODUCT_NAME, name);
		values.put(RelatedItemsTableHelper._PRODUCT_PRICE, price);
		values.put(RelatedItemsTableHelper._PRODUCT_URL, url);
		values.put(RelatedItemsTableHelper._IMAGE_URL, image);
		db.insertWithOnConflict(RelatedItemsTableHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }
    
    /**
     * TODO
     * @param ctx
     * @param mProducts
     * @throws InterruptedException 
     */
	/*-public synchronized static void insertRelatedItems(Context ctx, ArrayList<Product> mProducts) throws InterruptedException {

	    DarwinDatabaseSemaphore.getInstance().getLock();
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		
		try {
			db.beginTransaction();
			
			int count = 19;
			for (Product product : mProducts) {
				Log.d(TAG, "RELATED ITEM: " + product.getBrand());
				insertRelatedItem(db, 
						product.getSKU(), 
						product.getBrand(), 
						product.getName(), 
						product.getSpecialPrice(),
						product.getUrl(),
						(product.getImages().size() == 0) ? "" : product.getImages().get(0).getUrl());
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
		DarwinDatabaseSemaphore.getInstance().releaseLock();

	}*/

	/**
	 *
	 * @throws InterruptedException 
	 */
	public synchronized static void insertRelatedItemsAndClear(ArrayList<NewProductPartial> mProducts) throws InterruptedException {
		Print.d(TAG, "ON CLEAN AND INSERT: START");
		SQLiteDatabase db = null;
		
		// DarwinDatabaseSemaphore.getInstance().getLock();
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
			Print.d(TAG, "ON CLEAN AND INSERT: FINISH");
			// Validate helper
			if (db != null) {
				db.endTransaction();
				db.close();
			} 
		}
		// DarwinDatabaseSemaphore.getInstance().releaseLock();
	}
	
	private synchronized static void cleanAndInsert(SQLiteDatabase db, ArrayList<NewProductPartial> mProducts) {
		clearRelatedItems(db);
		int count = 1;
		Print.d(TAG, "RELATED ITEMS COUNT: " + mProducts.size());
		for (NewProductPartial product : mProducts) {
			Print.d(TAG, "RELATED ITEM: " + product.getBrand());
			insertRelatedItem(db, product.getSku(), product.getBrand(), product.getName(),
					String.valueOf(product.getSpecialPrice()), "", product.getImageUrl());
			// product.getUrl()
			// Validate counter
			if(count == MAX_SAVED_PRODUCTS) break;
			// Inc counter
			count++;
		}
	}

    /**
     * Delete all LastViewes
     */
    public static void deleteAllRelatedItems() { 
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

	 /**
	  * Clears the Last Viewed table
	  * 
	  * @param db
	  */
	 public static void clearRelatedItems(SQLiteDatabase db) {
		 Print.d(TAG, "ON CLEAN TABLE");
		 db.delete(TABLE_NAME, null, null);
	 }
    
}
