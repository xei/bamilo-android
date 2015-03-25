package com.mobile.framework.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobile.framework.database.DarwinDatabaseHelper.TableType;
import com.mobile.framework.objects.SearchSuggestion;

import de.akquinet.android.androlog.Log;

/**
 * This class is an helper to manage the recent search queries on the database.
 * @author sergiopereira
 *
 */
public class SearchRecentQueriesTableHelper extends BaseTable {
	
	private static final String TAG = SearchRecentQueriesTableHelper.class.getSimpleName();
	
	private static final String NUMBER_OF_SUGGESTIONS = "5";
	
	// Table Name
	public static final String TABLE_NAME = "search_recent";
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _QUERY = "query";
	public static final String _TIME_STAMP = "timestamp";
    
	/*
	 * (non-Javadoc)
	 * @see com.mobile.framework.database.BaseTable#getUpgradeType()
	 */
    @Override
    public TableType getUpgradeType() {
        return TableType.PERSIST;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.database.BaseTable#getName()
     */
    @Override
    public String getName() {
        return TABLE_NAME;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.database.BaseTable#create(java.lang.String)
     */
    @Override
    public String create(String table) {
        return "CREATE TABLE " + table + " (" + 
                _ID +           " INTEGER PRIMARY KEY, " + 
                _QUERY +        " TEXT," +  
                _TIME_STAMP +   " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + 
                 ")";
    }
    
    /*
     * ################## CRUD ##################  
     */

    /**
     * Insert current search query
     * @param query
     * @return true or false
     * @author sergiopereira
     */
    public static synchronized boolean insertQuery(String query) {
    	Log.d(TAG, "INSERT INTO SEARCH RECENT: " + query);
    	// Validate arguments
    	if(query == null) return false;
    	// Insert
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        // Delete old entries
        db.delete(TABLE_NAME, _QUERY + " LIKE ?", new String[] {query});
	    // Insert
        ContentValues values = new ContentValues();
        values.put(SearchRecentQueriesTableHelper._QUERY, query);
        long result = db.insert(SearchRecentQueriesTableHelper.TABLE_NAME, null, values);
        db.close();
        return (result == -1) ? false : true;
    }
     
    /**
     * Get the recent queries
     * @param searchText
     * @return List of searchSuggestion
     * @author sergiopereira
     * @throws InterruptedException 
     */
    public static synchronized ArrayList<SearchSuggestion> getAllRecentQueries() throws InterruptedException{
		Log.d(TAG, "GET LAST " + NUMBER_OF_SUGGESTIONS + " RECENT QUERIES");
		// Select the best resolution
		String query =	"SELECT DISTINCT " + _QUERY + " " +
			    		"FROM " + TABLE_NAME + " " +
						"ORDER BY " + _TIME_STAMP + " DESC " +
						"LIMIT " + NUMBER_OF_SUGGESTIONS;
		Log.i(TAG, "SQL QUERY: " + query);
		// Return
		return getRecentQueries(query);
    }
    
    /**
     * Get the recent queries
     * @param searchText
     * @return List of searchSuggestion
     * @author sergiopereira
     * @throws InterruptedException 
     */
    public static synchronized ArrayList<SearchSuggestion> getFilteredRecentQueries(String searchText) throws InterruptedException{
		Log.d(TAG, "GET RECENT QUERIES FOR: " + searchText);
		// Select the best resolution
		String query =	"SELECT DISTINCT " + _QUERY + " " +
			    		"FROM " + TABLE_NAME + " " +
			    		"WHERE " + _QUERY + " LIKE '%" + searchText + "%' " + 
						"ORDER BY " + _TIME_STAMP + " DESC " +
						"LIMIT " + NUMBER_OF_SUGGESTIONS;
		Log.i(TAG, "SQL QUERY: " + query);
		// Return
		return getRecentQueries(query);
    }

    /**
     * Update the recent query
     * @param query
     * @return 
     * @author sergiopereira
     */
    public static synchronized boolean updateRecentQuery(String query) {
    	Log.d(TAG, "UPDATE RECENT QUERIES FOR: " + query);
    	// Validate arguments
    	if(query == null) return false;
    	// Get current time stamp
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    	String timestamp = dateFormat.format(new Date());
    	Log.d(TAG, "UPDATE RECENT TIMESTAMP: " + timestamp);
    	// Update
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SearchRecentQueriesTableHelper._TIME_STAMP, timestamp);
        long result = db.update(TABLE_NAME, values, _QUERY + " LIKE ?", new String[] {query});
        db.close();
        return (result == -1) ? false : true;
    }
    
    
    /**
     * Get the recent queries
     * @param searchText
     * @return List of searchSuggestion
     * @author sergiopereira
     * @throws InterruptedException 
     */
    public static synchronized ArrayList<SearchSuggestion> getRecentQueries(String query) throws InterruptedException{
    	Log.i(TAG, "SQL QUERY: " + query);
    	// Lock access
    	DarwinDatabaseSemaphore.getInstance().getLock();
		// Permission
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		// Get results
		ArrayList<SearchSuggestion> recentSuggestions = new ArrayList<SearchSuggestion>();
		// 
        try {
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                Log.d(TAG, "QUERY RESULT SIZE: " + cursor.getCount());
                // Move to first position
                cursor.moveToFirst();
                // Get items
                while (!cursor.isAfterLast()) {
                    String recentSuggestion = cursor.getString(0);
                    Log.d(TAG, "QUERY: " + recentSuggestion);
                    SearchSuggestion searchSuggestion = new SearchSuggestion();
                    searchSuggestion.setResult(recentSuggestion);
                    searchSuggestion.setIsRecentSearch(true);
                    // Save product
                    recentSuggestions.add(searchSuggestion);
                    // Next
                    cursor.moveToNext();
                }
            }
            // Validate cursor
            if (cursor != null) cursor.close();
        } catch (IllegalStateException e) {
            Log.w(TAG, "WARNING: ISE ON GET RECENT QUERIES", e);
        }
        // Unlock access
		DarwinDatabaseSemaphore.getInstance().releaseLock();
		// Return
		return recentSuggestions;
    }
    
    
	/**
	 * Get the number of items on database
	 * @return int
	 * @author sergiopereira
	 */
	public static int getCount(){
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
	    String query = "SELECT count(*) FROM " + TABLE_NAME;
	    Cursor cursor = db.rawQuery(query, null);
	    cursor.moveToFirst();
	    int count = cursor.getInt(0);
		cursor.close();
	    Log.d(TAG, "COUNT: " + count);
	    return count;
	}
    
    /**
     * Delete all Recent Queries
     */
    public static void deleteAllRecentQueries() { 
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
 
}
