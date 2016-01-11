package com.mobile.newFramework.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mobile.newFramework.objects.configs.ImageResolution;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is a helper to manage the image resolution on database.
 * @author sergiopereira
 *
 */
public class ImageResolutionTableHelper extends BaseTable {
	
	private static final String TAG = ImageResolutionTableHelper.class.getSimpleName();
	
	// Table Name
	public static final String TABLE_NAME = "image_resolutions";
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _IDENTIFIER = "identifier";
	public static final String _WIDTH = "width";
	public static final String _HEIGHT = "height";
	public static final String _EXTENSION = "extension";
	
    /*
     * (non-Javadoc)
     * @see com.mobile.newFramework.database.BaseTable#getType()
     */
   @Override
   @DarwinDatabaseHelper.UpgradeType
   public int getUpgradeType() {
       return DarwinDatabaseHelper.CACHE;
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
       return "CREATE TABLE ? (" +
               _ID +           " INTEGER PRIMARY KEY, " + 
               _IDENTIFIER +   " TEXT," + 
               _WIDTH +        " INTEGER," + 
               _HEIGHT +       " INTEGER," + 
               _EXTENSION +    " TEXT" + 
                ")";
   }
   
   
   /*
    * ################## CRUD ##################  
    */
    
    /**
     * Method used to save image resolutions on database.
     * Delete old data and save new data.
     * @param resolutionsArray
     * @throws JSONException
     */
    public static void replaceAllImageResolutions(JSONArray resolutionsArray) throws JSONException {
    	// Get permission
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
    	// Delete old data
    	db.delete(TABLE_NAME, null, null);
    	// Insert new resolution
		for (int i = 0; i < resolutionsArray.length(); ++i) {
			// Get object
			JSONObject resolutionObject = resolutionsArray.getJSONObject(i);
			// Get resolution
			String identifier = resolutionObject.getString(ImageResolution.JSON_IDENTIFIER_TAG);
			int width = resolutionObject.getInt(ImageResolution.JSON_WIDTH_TAG);
			int height = resolutionObject.getInt(ImageResolution.JSON_HEIGHT_TAG);
			String extension = resolutionObject.getString(ImageResolution.JSON_EXTENSION_TAG);
			// Save resolution 
			ContentValues values = new ContentValues();
	        values.put(ImageResolutionTableHelper._IDENTIFIER, identifier);
	        values.put(ImageResolutionTableHelper._WIDTH, width);
	        values.put(ImageResolutionTableHelper._HEIGHT, height);
	        values.put(ImageResolutionTableHelper._EXTENSION, extension);
	        // Insert resolution
	        db.insert(ImageResolutionTableHelper.TABLE_NAME, null, values);
			Print.i(TAG, "RESOLUTION: " + identifier + " " + width + " " + height + " " + extension);
		}
		db.close();
    }
    
    
    /**
     * Insert resolution
     * @param identifier
     * @param width
     * @param height
     * @param extension
     */
    public static void insertImageResolution(String identifier, int width, int height, String extension) {
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ImageResolutionTableHelper._IDENTIFIER, identifier);
        values.put(ImageResolutionTableHelper._WIDTH, width);
        values.put(ImageResolutionTableHelper._HEIGHT, height);
        values.put(ImageResolutionTableHelper._EXTENSION, extension);
        db.insert(ImageResolutionTableHelper.TABLE_NAME, null, values);
        db.close();
    }
    
    /**
     * Delete all resolutions
     */
    public static void deleteAllImageResolutions() { 
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
        
    /**
     * Get the next best resolution (prefer webp) from the actual resolution excluding the "related", "cart" and "list" resolutions.
     * @see "http://theiconic.bugfoot.de/mobile-api/main/imageresolutions/
     * @param resolutionTag
     * @return Resolution
     */
	public static ImageResolution getBestImageResolution(String resolutionTag ) {
		// Permission
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		// Exclude this resolution
		String exceptResolutions[] = {"related", "cart", "list"};
		// Select the best resolution
		String query =     "SELECT " + _IDENTIFIER + ", " + _WIDTH + ", " + _HEIGHT + ", " + _EXTENSION + ", " +
							"(" + _WIDTH + "*" + _HEIGHT + ") AS mul " + 
			    			"FROM " + TABLE_NAME + " " +
		    				"WHERE mul > ( 	SELECT (" + _WIDTH + "*" + _HEIGHT + ") " + 
		    								"FROM " + TABLE_NAME + " " + 
    										"WHERE " + _IDENTIFIER + " = ? ) " + 
							"AND " + _IDENTIFIER + " != ? " + 
							"AND " + _IDENTIFIER + " != ? " + 
							"AND " + _IDENTIFIER + " != ? " + 
							"ORDER BY mul ASC, " + _EXTENSION + " DESC " +
							"LIMIT 1";
		// Print query
		Print.i(TAG, "SQL QUERY: " + query);
		// Perform query
		Cursor cursor = db.rawQuery(query, new String[] { resolutionTag , exceptResolutions[0], exceptResolutions[1], exceptResolutions[2] });
		// Get result
		ImageResolution imageResolution = null;
		if (cursor != null && cursor.getCount() >0 ) {
			cursor.moveToFirst();
			// Save
			imageResolution = new ImageResolution(	cursor.getString(0), Integer.parseInt(cursor.getString(1)),
													Integer.parseInt(cursor.getString(2)), cursor.getString(3));
			// Log result
			Print.i(TAG, "SQL RESULT: " + imageResolution.getIdentifier());
		}
		// Validate cursor
		if(cursor != null)
			cursor.close();
		// Return
		return imageResolution;
	}
    
    
    /**
     * Get the next best resolution using the screen resolution
     * @param width
     * @param heigth
     * @return Resolution
     * @deprecated
     */
	public static ImageResolution getBestImageResolution(int width, int heigth) {
		ImageResolution imageResolution = null;
		// Permission
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		// Select the best resolution
		String query = "SELECT " + _IDENTIFIER + "," + _WIDTH + "," + _HEIGHT + "," + _EXTENSION + ", " +
						"ABS((" + _WIDTH + " - ?) + ( " + _HEIGHT + " - ?)) AS calc, " +
						"MAX(" + _WIDTH + " * " + _HEIGHT + ") AS mul " +
						"FROM " + TABLE_NAME + " " +
			 			"GROUP BY calc " +
			 			"ORDER BY calc ASC " +
			 			"LIMIT 1";
		Print.i(TAG, "SQL QUERY: " + query);
		// Perform query
		Cursor cursor = db.rawQuery(query, new String[] { String.valueOf(width), String.valueOf(heigth) });
		// Get result
		if (cursor != null) {
			cursor.moveToFirst();
			// Save
			imageResolution = new ImageResolution(
					cursor.getString(0), Integer.parseInt(cursor.getString(1)),
					Integer.parseInt(cursor.getString(2)), cursor.getString(3));
			// Log result
			Print.i(TAG, "SQL RESULT: " + imageResolution.getIdentifier());
			// Validate cursor
			cursor.close();
		} else {
			Print.i(TAG, "NO SQL RESULT");
		}
		// Return
		return imageResolution;
	}

	 /**
	  * Clears the image resolutions table
	  */
	 public static void clearImageResolutions() {
		 Print.d(TAG, "ON CLEAN TABLE");
		 SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		 db.delete(TABLE_NAME, null, null);
	 }    
    
}
