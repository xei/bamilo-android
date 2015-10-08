package com.mobile.newFramework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobile.newFramework.database.DarwinDatabaseHelper.TableType;
import com.mobile.newFramework.objects.product.pojo.ProductComplete;
import com.mobile.newFramework.utils.output.Print;

import java.util.ArrayList;

/**
 * This class is a helper to manage the Last Viewed products on database.
 *
 * @author Manuel Silva
 * @modified sergiopereira
 */
public class LastViewedTableHelper extends BaseTable {

    private static final String TAG = LastViewedTableHelper.class.getSimpleName();

    // Table Name
    public static final String TABLE_NAME = "last_viewed";

    private static final int MAX_SAVED_PRODUCTS = 15;

    // Table Rows
    public static final String _ID = "id";

    public static final String _PRODUCT_SKU = "product_sku";

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
        return new StringBuilder("CREATE TABLE ").append(table)
                .append(" (")
                .append(_ID).append(" INTEGER PRIMARY KEY, ")
                .append(_PRODUCT_SKU).append(" TEXT ")
                .append(")").toString();
    }
    
    /*
     * ################## CRUD ##################  
     */

    /**
     * Insert viewed product into database
     */
    public static void insertLastViewedProduct(ProductComplete completeProduct) {
        try {
            if (completeProduct != null) {
                String sku = completeProduct.getSku();
                // TODO database new approach to validate and limit number of items
                if (!verifyIfExist(sku)) {
                    if (getLastViewedEntriesCount() == MAX_SAVED_PRODUCTS) {
                        removeOldestEntry();
                    }
                    SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(LastViewedTableHelper._PRODUCT_SKU, sku);
                    db.insert(LastViewedTableHelper.TABLE_NAME, null, values);
                    db.close();
                }
            }
        } catch (IllegalStateException | SQLiteException e) {
            // ...
        }
    }

    /**
     * Verifies if the product is already on the last viewed list
     */
    private static boolean verifyIfExist(String sku) {
        boolean result = false;
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        String query = "SELECT count(*) FROM " + TABLE_NAME + " WHERE " + _PRODUCT_SKU + " = '" + sku + "'";
        Print.i(TAG, "SQL RESULT query :  " + query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0) >= 1;
            // Log result
            Print.i(TAG, "SQL RESULT: " + cursor.getInt(0) + " result is : " + result);
        }
        // Validate cursor
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return result;
    }

    /**
     * Get number of entries
     */
    private static int getLastViewedEntriesCount() {
        int result = 0;
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        String query = "select count(*) from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            result = cursor.getInt(0);
            // Log result
            Print.i(TAG, "SQL RESULT: " + result);
        }
        // Validate cursor
        if (cursor != null) {
            cursor.close();
        }

        db.close();
        return result;
    }

    /**
     * Get the last viewed list of entries for list of addable Last Viewed
     */
    public static ArrayList<String> getLastViewedAddableToCartList() {
        ArrayList<String> listLastViewed = new ArrayList<>();
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        String query = "SELECT " + _PRODUCT_SKU + " FROM " + TABLE_NAME + " ORDER BY " + _ID + " DESC";
        Print.i(TAG, "SQL RESULT query :  " + query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                listLastViewed.add(cursor.getString(0));
            }
        }
        // Validate cursor
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return listLastViewed;
    }

    /**
     * Remove lastViewed from database
     */
    public static void removeLastViewed(String sku) {
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + _PRODUCT_SKU + " = '" + sku + "'";
        Print.i(TAG, "SQL RESULT query :  " + query);
        db.execSQL(query);
        db.close();
    }

    /**
     * Remove oldest entry from database
     */
    public static void removeOldestEntry() {
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + _ID + " IN " +
                        "( SELECTED " + _ID + " FROM " + TABLE_NAME +
                        " ORDER BY " + _ID + " ASC LIMIT 1 )";
        db.execSQL(query);
        db.close();
    }

    /**
     * Delete all LastViewes
     */
    public static void deleteAllLastViewed() {
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }


}
