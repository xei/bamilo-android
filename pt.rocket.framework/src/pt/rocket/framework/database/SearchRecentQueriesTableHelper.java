package pt.rocket.framework.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import pt.rocket.framework.objects.SearchSuggestion;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;
import de.akquinet.android.androlog.Log;

/**
 * This class is an helper to manage the recent search queries on the database.
 * @author sergiopereira
 *
 */
public class SearchRecentQueriesTableHelper {
	
	private static final String TAG = SearchRecentQueriesTableHelper.class.getSimpleName();
	
	private static final String NUMBER_OF_SUGGESTIONS = "5";
	
	// Table Name
	public static final String _NAME = "search_recent";
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _QUERY = "query";
	public static final String _TIME_STAMP = "timestamp";
	
	// Create table
    public static final String CREATE = 
    				"CREATE TABLE " + _NAME + " (" + 
    				_ID +			" INTEGER PRIMARY KEY, " + 
    				_QUERY +		" TEXT," +  
    				_TIME_STAMP +	" TIMESTAMP DEFAULT CURRENT_TIMESTAMP" + 
    				 ")";
    // Drop table
    public static final String DROP = "DROP TABLE IF EXISTS " + _NAME;
    
    /*
     * ######################### CRUD Operations ######################### 
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
        ContentValues values = new ContentValues();
        values.put(SearchRecentQueriesTableHelper._QUERY, query);
        long result = db.insert(SearchRecentQueriesTableHelper._NAME, null, values);
        db.close();
        return (result == -1) ? false : true;
    }
     
    /**
     * Get the recent queries
     * @param searchText
     * @return List of searchSuggestion
     * @author sergiopereira
     */
    public static synchronized ArrayList<SearchSuggestion> getAllRecentQueries(){
		Log.d(TAG, "GET LAST " + NUMBER_OF_SUGGESTIONS + " RECENT QUERIES");
		// Select the best resolution
		String query =	"SELECT DISTINCT " + _QUERY + " " +
			    		"FROM " + _NAME + " " +
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
     */
    public static synchronized ArrayList<SearchSuggestion> getFilteredRecentQueries(String searchText){
		Log.d(TAG, "GET RECENT QUERIES FOR: " + searchText);
		// Select the best resolution
		String query =	"SELECT DISTINCT " + _QUERY + " " +
			    		"FROM " + _NAME + " " +
			    		"WHERE " + _QUERY + " LIKE '%" + searchText + "%' " + 
						"ORDER BY " + _TIME_STAMP + " DESC " +
						"LIMIT " + NUMBER_OF_SUGGESTIONS;
		Log.i(TAG, "SQL QUERY: " + query);
		// Return
		return getRecentQueries(query);
    }
    
    
    public static synchronized boolean updateRecentQuery(String query) {
    	Log.d(TAG, "UPDATE RECENT QUERIES FOR: " + query);
    	// Validate arguments
    	if(query == null) return false;
    	// Get current time stamp
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    	String timestamp = dateFormat.format(new Date());
    	Log.d(TAG, "UPDATE TIMESTAMP: " + timestamp);
    	// Update
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SearchRecentQueriesTableHelper._TIME_STAMP, timestamp);
        long result = db.update(_NAME, values, _QUERY + " LIKE ?", new String[] {query});
        db.close();
        return (result == -1) ? false : true;
    }
    
    
    /**
     * Get the recent queries
     * @param searchText
     * @return List of searchSuggestion
     * @author sergiopereira
     */
    public static synchronized ArrayList<SearchSuggestion> getRecentQueries(String query){
    	Log.i(TAG, "SQL QUERY: " + query);
		// Permission
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		
//		Cursor cursor = db.query(true, 
//				_NAME, 
//				new String[] { _QUERY }, 
//				null, 
//				null, 
//				null, 
//				null, 
//				_TIME_STAMP + " DESC", 
//				NUMBER_OF_SUGGESTIONS);
//		
//		if(query != null) {
//			cursor = db.query(true, 
//					_NAME, 
//					new String[] { _QUERY }, 
//					_QUERY + " LIKE ?", 
//					new String[] { "%" + query + "%" }, 
//					null, 
//					null, 
//					_TIME_STAMP + " DESC", 
//					NUMBER_OF_SUGGESTIONS);
//		}
		
		// Get results
		ArrayList<SearchSuggestion> recentSuggestions = new ArrayList<SearchSuggestion>();
		if (cursor != null && cursor.getCount() >0 ) {
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
		if(cursor != null) cursor.close();
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
	    String query = "SELECT count(*) FROM " + _NAME;
	    Cursor cursor = db.rawQuery(query, null);
	    cursor.moveToFirst();
	    int count = cursor.getInt(0);
		cursor.close();
	    Log.d(TAG, "COUNT: " + count);
	    return count;
	}
 
}
