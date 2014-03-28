package pt.rocket.framework.database;

import java.util.ArrayList;

import pt.rocket.framework.objects.SearchSuggestion;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    public static synchronized ArrayList<SearchSuggestion> getRecentQueries(String searchText){
		Log.d(TAG, "GET RECENT QUERIES FOR: " + searchText);
		// Permission
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		// Select the best resolution
		String query =	"SELECT DISTINCT " + _QUERY + " " +
			    		"FROM " + _NAME + " " +
			    		"WHERE " + _QUERY + " LIKE '" + searchText + "%' " + 
						"ORDER BY " + _TIME_STAMP + " DESC " +
						"LIMIT " + NUMBER_OF_SUGGESTIONS;
		Log.i(TAG, "SQL QUERY: " + query);
		Cursor cursor = db.rawQuery(query, null);
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
