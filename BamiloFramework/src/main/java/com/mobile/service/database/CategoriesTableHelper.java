package com.mobile.service.database;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mobile.service.utils.output.Print;

/**
 * Category table helper.
 * @author sergiopereira
 * @modified Paulo Carvalho
 */
public class CategoriesTableHelper extends BaseTable {

    private static final String TAG = CategoriesTableHelper.class.getSimpleName();

    public static final String TABLE_NAME = "categories";

    public interface Columns {
        String URL_KEY = "url_key";
        String NAME = "category_name";
        String VIEW_COUNT = "category_view_counter";
    }
    
    
    /*
     * (non-Javadoc)
     * @see com.mobile.service.database.BaseTable#getType()
     */
    @Override
    @DarwinDatabaseHelper.UpgradeType
    public int getUpgradeType() {
        return DarwinDatabaseHelper.CACHE;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.service.database.BaseTable#getName()
     */
    @Override
    public String getName() {        
        return TABLE_NAME;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.service.database.BaseTable#create(java.lang.String)
     */
    @Override
    public String create() {
        return new StringBuilder()
                .append("CREATE TABLE %s")
                .append(" (")
                .append(Columns.URL_KEY).append(" TEXT PRIMARY KEY, ")
                .append(Columns.NAME).append(" TEXT, ")
                .append(Columns.VIEW_COUNT).append(" INTEGER NOT NULL DEFAULT 1")
                .append(" )")
                .toString();
        
    }

    /**
     * Method used to increment the counter for respective category.
     *
     * @param id hash that will be used as identifier
     * @param categoryName category name
     */
    public static void updateCategoryCounter(String id, String categoryName) {
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        try {

            // Create query
            String query = new StringBuilder("select ").append(Columns.VIEW_COUNT).append(" from ").append(TABLE_NAME)
                    .append(" where ").append(Columns.URL_KEY).append(" = ?").toString();
            Print.i(TAG, "SQL RESULT query :  " + query);
            Cursor cursor = db.rawQuery(query, new String[]{"'"+id+"'"});

            int count = 0;
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }

            // Create query
            String insertOrReplace = new StringBuilder()
                    .append("INSERT OR REPLACE INTO ").append(TABLE_NAME)
                    .append("(" + Columns.NAME + "," + Columns.URL_KEY + "," + Columns.VIEW_COUNT + ") ")
                    .append("VALUES ( ")
                    .append("(").append("?").append("), ")
                    .append("(").append("?").append("), ")
                    .append(count + 1)
                    .append(" )")
                    .toString();
            // Execute
            db.execSQL(insertOrReplace, new String[]{DatabaseUtils.sqlEscapeString(categoryName), DatabaseUtils.sqlEscapeString(id)});
            Print.i(TAG, "ON INCREASE COUNTER: " + categoryName);
        } catch (SQLException e) {
            Print.w(TAG, "WARNING: SQE ON INCREASE COUNTER", e);
        } finally {
            db.close();
        }
    }

    /**
     * returns the category with the top view count
     *
     * @return Category
     * @throws InterruptedException
     */
    public synchronized static String getTopCategory() throws InterruptedException {

        //DarwinDatabaseSemaphore.getInstance().getLock();

        // Get readable access
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        // Data
        String category = "";
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
                category = cursor.getString(0);
                counter = cursor.getString(1);
            }
            // Close cursor
            if (cursor != null) cursor.close();

        } catch (SQLException e) {
            Print.w(TAG, "WARNING: SQE ON GET TOP VIEWED CATEGORY", e);
        } finally {
            db.close();
        }
        //DarwinDatabaseSemaphore.getInstance().releaseLock();
        Print.i(TAG, "TOP CATEGORY: " + category + " " + counter);
        // Return name
        return category;
    }

    /**
     * Remove all rows
     */
    public static void clearCategories() {
        Print.d(TAG, "ON CLEAN TABLE");
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

}