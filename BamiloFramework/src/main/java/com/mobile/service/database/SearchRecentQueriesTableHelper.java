package com.mobile.service.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobile.service.objects.search.Suggestion;
import com.mobile.service.utils.output.Print;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
	public static final String _RESULT = "result";
	public static final String _TYPE = "type";
	public static final String _TARGET = "target";
	public static final String _TIME_STAMP = "timestamp";

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
    public String create() {
        return "CREATE TABLE %s (" +
                _ID +           " INTEGER PRIMARY KEY, " + 
                _QUERY +        " TEXT," +
                _RESULT +       " TEXT," +
                _TYPE +         " INTEGER DEFAULT " + Suggestion.SUGGESTION_OTHER + "," +
                _TARGET +       " TEXT," +
                _TIME_STAMP +   " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                 ")";
    }
    
    /*
     * ################## CRUD ##################  
     */

    /**
     * Insert current search query
     * @return true or false
     * @author sergiopereira
     */
    public static synchronized boolean insertQuery(Suggestion suggestion) throws InterruptedException, NullPointerException {
    	// Validate arguments
    	if(suggestion == null) return false;
        Print.i(TAG, "INSERT INTO SEARCH RECENT: " + suggestion.getResult());
        // Insert
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        // Delete old entries
        int rows = db.delete(TABLE_NAME, _RESULT + " LIKE ?", new String[] {suggestion.getResult()});
        // Old database version, < v3.0
        if(rows == 0) {
            db.delete(TABLE_NAME, _QUERY + " LIKE ?", new String[] {suggestion.getResult()});
        }
	    // Insert
        ContentValues values = new ContentValues();
        values.put(SearchRecentQueriesTableHelper._QUERY, suggestion.getQuery());
        values.put(SearchRecentQueriesTableHelper._RESULT, suggestion.getResult());
        values.put(SearchRecentQueriesTableHelper._TYPE, suggestion.getType());
        values.put(SearchRecentQueriesTableHelper._TARGET, suggestion.getTarget());
        long result = db.insert(SearchRecentQueriesTableHelper.TABLE_NAME, null, values);
        db.close();
        return result == -1;
    }
     
    /**
     * Get the recent queries
     * @return List of searchSuggestion
     * @author sergiopereira
     * @throws InterruptedException 
     */
    public static synchronized ArrayList<Suggestion> getAllRecentQueries() throws InterruptedException{
		Print.d(TAG, "GET LAST " + NUMBER_OF_SUGGESTIONS + " RECENT QUERIES");
		// Select the best resolution
		String query =	"SELECT DISTINCT * " +
			    		"FROM " + TABLE_NAME + " " +
						"ORDER BY " + _TIME_STAMP + " DESC " +
						"LIMIT " + NUMBER_OF_SUGGESTIONS;
		Print.i(TAG, "SQL QUERY: " + query);
		// Return
		return getRecentQueries(query, null);
    }
    
    /**
     * Get the recent queries
     * @return List of searchSuggestion
     * @author sergiopereira
     * @throws InterruptedException 
     */
    public static synchronized ArrayList<Suggestion> getFilteredRecentQueries(String searchText) throws InterruptedException{
		Print.d(TAG, "GET RECENT QUERIES FOR: " + searchText);
		// Select the best resolution
		String query =	"SELECT DISTINCT * " +
			    		"FROM " + TABLE_NAME + " " +
			    		"WHERE " + _QUERY + " LIKE ? " +
						"ORDER BY " + _TIME_STAMP + " DESC " +
						"LIMIT " + NUMBER_OF_SUGGESTIONS;
		Print.i(TAG, "SQL QUERY: " + query);
		// Return
        return getRecentQueries(query, new String[]{"%"+searchText+"%"});
    }

    /**
     * Update the recent query
     * @author sergiopereira
     */
    public static synchronized boolean updateRecentQuery(String query) {
    	Print.d(TAG, "UPDATE RECENT QUERIES FOR: " + query);
    	// Validate arguments
    	if(query == null) return false;
    	// Get current time stamp
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    	String timestamp = dateFormat.format(new Date());
    	Print.d(TAG, "UPDATE RECENT TIMESTAMP: " + timestamp);
    	// Update
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SearchRecentQueriesTableHelper._TIME_STAMP, timestamp);
        long result = db.update(TABLE_NAME, values, _QUERY + " LIKE ?", new String[] {query});
        db.close();
        return result == -1;
    }
    
    
    /**
     * Get the recent queries
     * @return List of searchSuggestion
     * @author sergiopereira
     * @throws InterruptedException 
     */
    private static synchronized ArrayList<Suggestion> getRecentQueries(String query, String[] queryParams) throws InterruptedException{
    	Print.i(TAG, "SQL QUERY: " + query);
    	// Lock access
    	// DarwinDatabaseSemaphore.getInstance().getLock();
		// Permission
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		// Get results
		ArrayList<Suggestion> recentSuggestions = new ArrayList<>();
        try {
            Cursor cursor = db.rawQuery(query, queryParams);
            if (cursor != null && cursor.getCount() > 0) {
                Print.d(TAG, "QUERY RESULT SIZE: " + cursor.getCount());
                // Move to first position
                cursor.moveToFirst();
                // Get items
                while (!cursor.isAfterLast()) {
                    String recentSuggestion = cursor.getString(1);
                    String recentResult = cursor.getString(2);
                    @Suggestion.SuggestionType int recentType = cursor.getInt(3);
                    String recentTarget = cursor.getString(4);
                    Suggestion searchSuggestion = new Suggestion();
                    searchSuggestion.setQuery(recentSuggestion);
                    searchSuggestion.setResult(recentResult);
                    searchSuggestion.setType(recentType);
                    searchSuggestion.setTarget(recentTarget);
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
            Print.w(TAG, "WARNING: ISE ON GET RECENT QUERIES", e);
        }
        // Unlock access
		// DarwinDatabaseSemaphore.getInstance().releaseLock();
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
	    Print.d(TAG, "COUNT: " + count);
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
