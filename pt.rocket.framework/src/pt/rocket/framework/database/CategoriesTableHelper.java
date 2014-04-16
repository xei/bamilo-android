package pt.rocket.framework.database;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.rest.RestContract;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.akquinet.android.androlog.Log;

/**
 * @author ivanschuetz
 */
public class CategoriesTableHelper {
	
	private static final String TAG = CategoriesTableHelper.class.getSimpleName();
	
	public static final String TABLE = "categories";
	
	public static interface Columns {
		String ID = "id";
		String ID_CATALOG = "id_catalog_category"; //backend id- note: not unique across different segments
		String NAME = "name_name";
		String SUBSECTION = "subsection";
		String TEASER_IMAGE = "teaser_image";
		String TEASER_POS = "teaser_pos";
		String TEASER_VISIBLE = "teaser_visible";
		String SEGMENTS = "segments"; //TODO is this really a list?
		String API_URL = "api_url";
		String PARENT_ID_CATALOG = "parent_id";
		String API_URL_KEY = "url_key";
		String INFO_URL_KEY = "info_url";
		String PRODUCT_COUNT = "product_count";

		//not used
//		String IN_NAVIGATION = "in_navigation";
//		String SHOPS = "shop";
//		String AGES = "ages";
//		String GENDERS = "genders";
//		String COMBINATIONS = "female-shop";
	}
    
    public static final String CREATE = 
    		"CREATE TABLE " + TABLE + " (" + 
    				Columns.ID +			" INTEGER PRIMARY KEY, " + 
    				Columns.ID_CATALOG +			" TEXT, " +
    				Columns.NAME +			" TEXT," + 
    				Columns.SUBSECTION +			" TEXT, " + 
    				Columns.TEASER_IMAGE +			" TEXT, " + 
    				Columns.TEASER_POS +			" INTEGER, " + 
    				Columns.TEASER_VISIBLE +			" INTEGER, " + 
    				Columns.SEGMENTS +			" TEXT, " + 
    				Columns.API_URL +			" TEXT , " + 
    				Columns.PARENT_ID_CATALOG +			" INTEGER, " +
    				Columns.API_URL_KEY +			" TEXT, " + 
    				Columns.INFO_URL_KEY +			" TEXT, " + 
    				Columns.PRODUCT_COUNT +			" INTEGER NULL" + 
    				")";
    
    public static interface Projection {
    	int ID = 0;
    	int ID_CATALOG_CATEGORY = 1;
    	int NAME = 2;
    	int SUBSECTION = 3;
    	int TEASER_IMAGE = 4;
    	int TEASER_POS = 5;
    	int TEASER_VISIBLE = 6;
    	int SEGMENTS = 7;
    	int API_URL = 8;
    	int PARENT = 9;
    	int API_URL_KEY = 10;
    	int INFO_URL_KEY = 11;
    }
    
    /**
     * Save category trees recursively
     * 
     * Existing categories (indentified by API_URL) will not be updated
     * 
     * @param categories
     */
    public static void saveCategories(ArrayList<Category> categories) {
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();

    	try {
			db.beginTransaction();
	
	    	saveCategories(db, categories, -1);
	    	
	    	db.setTransactionSuccessful();

		} catch (SQLException e) {
			Log.e(e.getMessage());
			e.printStackTrace();
			
		}
		finally {
			db.endTransaction();
		}
    }
    
    private static void saveCategories(SQLiteDatabase db, ArrayList<Category> categories, long parentId) {
    	for (Category category : categories) {
    		saveCategory(db, category, parentId);
    	}
    }
    
    private static void saveCategory(SQLiteDatabase db, Category category, long parentId) {
    	long rowId = db.insertWithOnConflict(TABLE, null, getContentValues(category, parentId), SQLiteDatabase.CONFLICT_IGNORE);
    	ArrayList<Category> children = category.getChildren();
    	if (children != null) {
    		saveCategories(db, children, rowId);
    	}
    }
    
    /**
     * Saves the product count for a category 
     * Assumes the category is already present in database
     * @param categoryUrl api url of category
     * @param productCount
     */
    public static void saveProductCount(String categoryUrl, int productCount) {
    	SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();

    	ContentValues contentValues = new ContentValues();
    	contentValues.put(Columns.PRODUCT_COUNT, productCount);
    	
    	db.update(TABLE, contentValues, Columns.API_URL + " =?", new String[] {categoryUrl});
    }
    
    /**
     * Remove all rows
     * @param db writeable database
     */
    public static void clearCategories(SQLiteDatabase db) {
		db.delete(TABLE, null, null);
    }

    /**
     * Get all category trees
     */
	public static ArrayList<Category> getCategories() {
		return loadCategories(-1, null);
	}
	
	/**
	 * Get children category trees recursively
	 * 
	 * @param parentRowId 
	 * @return
	 */
	public static ArrayList<Category> loadCategories(long parentRowId, Category parent) {
		
		/**
		 * Fix the crash report caused by NullPointerException at SQLiteOpenHelper.getDatabaseLocked
		 * The Context passed to sqlite helper is null.
		 */
		SQLiteDatabase db = null;
    	try {
    		db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		} catch (NullPointerException e) {
			db = DarwinDatabaseHelper.getInstance(Darwin.context).getReadableDatabase();
		}
		
		Cursor cursor = db.query(TABLE, new String[] {
				Columns.ID,
				Columns.ID_CATALOG,
				Columns.NAME,
				Columns.SUBSECTION,
				Columns.TEASER_IMAGE,
				Columns.TEASER_POS,
				Columns.TEASER_VISIBLE,
				Columns.SEGMENTS,
				Columns.API_URL,
				Columns.PARENT_ID_CATALOG,
				Columns.API_URL_KEY,
				Columns.INFO_URL_KEY
		}, 
		Columns.PARENT_ID_CATALOG + " =?", new String[] {parentRowId + ""}, null, null, null);
		
		ArrayList<Category> categories = new ArrayList<Category>();
		
		while (cursor.moveToNext()) {
			long rowId = cursor.getLong(Projection.ID);
			String catalogIdLoaded = cursor.getString(Projection.ID_CATALOG_CATEGORY);
			String name = cursor.getString(Projection.NAME);
			String subsection = cursor.getString(Projection.SUBSECTION);
			String teaserImage = cursor.getString(Projection.TEASER_IMAGE);
			int teaserPosition = cursor.getInt(Projection.TEASER_POS);
			int teaserVisibleInt = cursor.getInt(Projection.TEASER_VISIBLE);
			boolean teaserVisible = teaserVisibleInt == 1 ? true : false;
			String segments = cursor.getString(Projection.SEGMENTS);
			String apiFileName = cursor.getString(Projection.API_URL);
			String urlKey = cursor.getString(Projection.API_URL_KEY);
			String infoUrl = cursor.getString(Projection.INFO_URL_KEY);
//			long parent = cursor.getLong(Projection.PARENT);
			
			String apiUrl = getAbsoluteCategoryUrl(apiFileName);
			
	    	Category category = new Category(catalogIdLoaded, name, null, null, urlKey, segments, infoUrl, apiUrl, null, parent);
//	    	Category category = new Category(catalogIdLoaded, name, subsection, teaserImage, teaserPosition, teaserVisible, , , , );
	    	
	    	ArrayList<Category> children = loadCategories(rowId, category);
	    	
	    	category.setChildren(children);
	    	
	    	categories.add(category);
		}
		cursor.close();
		
		return categories;		
	}

    /**
     * Returns remote category file name
     * 
     * Normally the category url is delivered by the server as an absolute url
     * 
     * @param categoryUrl
     * valid formats:
     * * complete: 
     *   protocol://host/apipath/categoryfile
     * * file name under mobile-api:
     *   categoryfile
     * @return remote category file name
     * 
     * Note: Assumes the category is stored directly under mobile-api - no subpath supported
     * TODO store everything after mobile-api, in the case subpaths become necessary, refactor with ProductTableHelper
     * 
     */
	
//	private static Pattern pattern = Pattern.compile(".*/(.*)(/)?$");
	private static Pattern pattern = Pattern.compile(".*/(.*)$");

	
    public static String getCategoryUrlForDB(String categoryUrl) {
    	String productUrlForDB;
    	
    	if (categoryUrl.substring(categoryUrl.length() - 1, categoryUrl.length()).equals("/")) { //remove possible / at the end e.g. in new arrivals url FIXME solve with regex 
    		categoryUrl = categoryUrl.substring(0, categoryUrl.length() - 1);
    	}
    	
    	if (categoryUrl.contains("/")) { //with path
    		
    		String fileName;
    		
    		Matcher matcher = pattern.matcher(categoryUrl);
    		if (matcher.find()) {
    			fileName = matcher.group(1);
    		} else {
    			throw new IllegalStateException("no group found");
    		}
    		
    		productUrlForDB = fileName;
    		
    	} else { //no path
    		productUrlForDB = categoryUrl;
    	}
    	
    	return productUrlForDB;
    }
    
    public static String getAbsoluteCategoryUrl(String relativeProductUrl) {
    	return getCategoryUrlPrefix() + relativeProductUrl;
    }
    
    private static String getCategoryUrlPrefix() {
    	return RestContract.HTTPS_PROTOCOL + "://" + RestContract.REQUEST_HOST + "/" + RestContract.REST_BASE_PATH + "/";
    }
    
    private static ContentValues getContentValues(Category category, long parentId) {
    	ContentValues contentValues = new ContentValues();
    	contentValues.put(Columns.NAME, category.getName());
    	contentValues.put(Columns.ID_CATALOG, category.getId());
    	String firstSegment = category.getSegments();
    	contentValues.put(Columns.SEGMENTS, firstSegment);
    	contentValues.put(Columns.API_URL, getCategoryUrlForDB(category.getApiUrl()));
    	contentValues.put(Columns.PARENT_ID_CATALOG, parentId);
    	contentValues.put(Columns.API_URL_KEY, category.getUrlKey());
    	contentValues.put(Columns.INFO_URL_KEY, category.getInfoUrl());
    	
    	return contentValues;
    }
    
    /**
     * Clears this table
     * @param db
     */
    public static void clear(SQLiteDatabase db) {
    	Log.i(TAG, "codedb deleting table "+TABLE);
		db.delete(TABLE, null, null);
    }
}