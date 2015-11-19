package com.mobile.newFramework.database;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.mobile.newFramework.utils.output.Print;

/**
 * Brands table helper.
 */
public class BrandsTableHelper extends BaseTable {

    private static final String TAG = BrandsTableHelper.class.getSimpleName();

    public static final String TABLE_NAME = "brands";

    public interface Columns {
        String ID = "id";
        String NAME = "brand_name";
        String VIEW_COUNT = "brand_view_counter";
    }


    /*
     * (non-Javadoc)
     * @see com.mobile.newFramework.database.BaseTable#getType()
     */
    @Override
    @DarwinDatabaseHelper.UpgradeType
    public int getUpgradeType() {
        return DarwinDatabaseHelper.PERSIST;
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
    public String create(String tableName) {
        return new StringBuilder()
                .append("CREATE TABLE ").append(tableName)
                .append(" (")
                .append(Columns.ID).append(" INTEGER PRIMARY KEY, ")
                .append(Columns.NAME).append(" TEXT, ")
                .append(Columns.VIEW_COUNT).append(" INTEGER NOT NULL DEFAULT 1")
                .append(" )")
                .toString();

    }

    /**
     * Method used to increment the counter for respective brand.
     *
     * @param brandName
     */
    public static void updateBrandCounter(String brandName) {
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        try {
            // Create query
            String query = new StringBuilder("select ").append(Columns.VIEW_COUNT).append(" from ").append(TABLE_NAME)
                    .append(" where ").append(Columns.NAME).append(" = '").append(brandName).append("'").toString();
            Print.i(TAG, "SQL RESULT query :  " + query);
            Cursor cursor = db.rawQuery(query, null);

            int count = 0;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }

            // Create query
            String insertOrReplace = new StringBuilder()
                    .append("INSERT OR REPLACE INTO ").append(TABLE_NAME)
                    .append("(" + Columns.NAME + "," + Columns.VIEW_COUNT + ") ")
                    .append("VALUES ( ")
                    .append("(").append(DatabaseUtils.sqlEscapeString(brandName)).append("), ")
                    .append(count + 1)
                    .append(" )")
                    .toString();
            // Execute
            db.execSQL(insertOrReplace);
            Print.i(TAG, "ON INCREASE COUNTER: " + brandName);
        } catch (IllegalStateException | SQLiteException e) {
            Print.w(TAG, "WARNING: SQE ON INCREASE COUNTER", e);
        } finally {
            db.close();
        }
    }

    /**
     * returns the brand with the top view count
     *
     * @return Brand
     * @throws InterruptedException
     */
    public synchronized static String getTopBrand() throws InterruptedException, IllegalStateException {

        //DarwinDatabaseSemaphore.getInstance().getLock();

        // Get readable access
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        // Data
        String brand = "";
        String counter = "";
        // Get top category    
        try {
            // Create query
            Cursor cursor = db.query(TABLE_NAME,
                    new String[]{Columns.NAME, Columns.VIEW_COUNT},
                    null, null, null, null,
                    Columns.VIEW_COUNT + " DESC",
                    "1");
            // Get data
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                brand = cursor.getString(0);
                counter = cursor.getString(1);
            }
            // Close cursor
            if (cursor != null) cursor.close();

        } catch (SQLException e) {
            Print.w(TAG, "WARNING: SQE ON GET TOP VIEWED BRAND", e);
        } finally {
            db.close();
        }
        //DarwinDatabaseSemaphore.getInstance().releaseLock();
        Print.i(TAG, "TOP BRAND: " + brand + " " + counter);
        // Return name
        return brand;
    }

    /**
     * Remove all rows
     * <p/>
     * writeable database
     */
    public static void clearBrands() {
        Print.d(TAG, "ON CLEAN TABLE");
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

}