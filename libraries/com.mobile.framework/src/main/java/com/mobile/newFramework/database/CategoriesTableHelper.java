package com.mobile.newFramework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.mobile.newFramework.database.DarwinDatabaseHelper.TableType;
import com.mobile.newFramework.objects.category.Category;
import com.mobile.newFramework.utils.output.Print;

import java.util.ArrayList;

/**
 * Category table helper.
 * @author sergiopereira
 *
 */
public class CategoriesTableHelper extends BaseTable {

    private static final String TAG = CategoriesTableHelper.class.getSimpleName();

    public static final String TABLE_NAME = "categories";

    public interface Columns {
        String ID = "id";
        String ID_CATALOG = "category_id";
        String NAME = "category_name";
        String VIEW_COUNT = "category_view_counter";
    }
    
    
    /*
     * (non-Javadoc)
     * @see com.mobile.newFramework.database.BaseTable#getType()
     */
    @Override
    public TableType getUpgradeType() {
        return TableType.CACHE;
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
                .append(Columns.ID_CATALOG).append(" TEXT, ")
                .append(Columns.NAME).append(" TEXT, ")
                .append(Columns.VIEW_COUNT).append(" INTEGER NOT NULL DEFAULT 1")
                .append(" )")
                .toString();
        
    }
    
    /*
     * ################## CRUD ##################  
     */

    /**
     * Save category trees recursively.
     * @param categories
     * @author sergiopereira
     */
    @Deprecated
    public static void saveCategories(ArrayList<Category> categories) {
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        try {
            saveCategories(db, categories);
        } catch (SQLException e) {
            Print.e(e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /**
     * Save category and sub categories in database.
     * @param db
     * @param categories
     * @author sergiopereira
     */
    @Deprecated
    private static void saveCategories(SQLiteDatabase db, ArrayList<Category> categories) {
        for (Category category : categories) {
            saveCategory(db, category);
            if(category.getHasChildrenInArray()) 
                saveCategories(category.getChildren());
        }
    }

    /**
     * Save category in database.
     * @param db
     * @param category
     * @author sergiopereira
     */
    @Deprecated
    private synchronized static void saveCategory(SQLiteDatabase db, Category category) {
        ContentValues values = new ContentValues();
        values.put(Columns.ID_CATALOG, category.getId());
        values.put(Columns.NAME, category.getName());
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        Print.i(TAG, "ON SAVE CATEGORY: " + values.toString());
    }

    /**
     * Method used to insert the category or increment the counter for respective category.
     * @param categoryId
     * @param categoryName
     * @author sergiopereira
     */
    public static void updateCategoryCounter(String categoryId, String categoryName) {
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        try {
            // Select id to replace
            String QUERY_ID = "SELECT " + Columns.ID + " " +
            		            "FROM " + TABLE_NAME + " " +
    		            		"WHERE " + Columns.ID_CATALOG + " = " + categoryId;
            // Select counter to replace and increment            
            String QUERY_CT = "SELECT " + Columns.VIEW_COUNT + " " +
            		            "FROM " + TABLE_NAME + " " +
    		            		"WHERE " + Columns.ID_CATALOG + " = " + categoryId;
            // Create query
            String insertOrReplace = new StringBuilder()
            .append("INSERT OR REPLACE INTO ").append(TABLE_NAME)
            .append("(" + Columns.ID + "," + Columns.ID_CATALOG + "," + Columns.NAME + "," + Columns.VIEW_COUNT + ") ")
            .append("VALUES ( ")
            .append("(").append(QUERY_ID).append("), ")
            .append("(").append(categoryId).append("), ")
            .append("(").append(DatabaseUtils.sqlEscapeString(categoryName)).append("), ")
            .append("(").append(QUERY_CT).append(") + 1")
            .append(" )")
            .toString();
            // Execute
            db.execSQL(insertOrReplace);
            Print.i(TAG, "ON INCREASE COUNTER: " + categoryId);
        } catch (SQLException e) {
            Print.w(TAG, "WARNING: SQE ON INCREASE COUNTER", e);
        } finally {
            db.close();
        }
    }
    
    /**
     * returns the view count for a category Assumes the category is already
     * present in database
     * 
     * @param categoryUrl
     *            api url of category
     * @return the viewCount
     * @throws InterruptedException 
     */
    public synchronized static String getTopCategory() throws InterruptedException {
        
        DarwinDatabaseSemaphore.getInstance().getLock();
        
        // Get readable access
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        // Data
        String category = "";
        String counter = "";
        // Get top category    
        try {
            // Create query
            Cursor cursor = db.query(TABLE_NAME, 
                    new String[] { Columns.NAME, Columns.VIEW_COUNT }, 
                    null, null, null, null, 
                    Columns.VIEW_COUNT + " DESC",
                    "1");
            // Get data
            if (cursor != null && cursor.getCount() > 0 ) {
                cursor.moveToFirst();
                category = cursor.getString(0);
                counter = cursor.getString(1);
            }
            // Close cursor
            if(cursor != null) cursor.close();
            
        } catch (SQLException e) {
            Print.w(TAG, "WARNING: SQE ON GET TOP VIEWED CATEGORY", e);
        } finally {
            db.close();
        }
        DarwinDatabaseSemaphore.getInstance().releaseLock();
        Print.i(TAG, "TOP CATEGORY: " + category + " " + counter);
        // Return name
        return category;
    }

    /**
     * Remove all rows
     *
     *            writeable database
     */
    public static void clearCategories() {
        Print.d(TAG, "ON CLEAN TABLE");
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        db.delete(TABLE_NAME, null, null);
    }

}