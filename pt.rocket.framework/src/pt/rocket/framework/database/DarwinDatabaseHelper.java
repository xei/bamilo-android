package pt.rocket.framework.database;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author sergiopereira
 *
 */
public class DarwinDatabaseHelper extends SQLiteOpenHelper {
	
	public static final String TAG = DarwinDatabaseHelper.class.getName();
	
	private static final int DATABASE_VERSION = 3;

    public static final String DB_NAME = "darwin.db";
    
    private static final String SQL_CREATE_MAIN = ImageResolutionTableHelper.CREATE;
    
    private static final String SQL_DROP_MAIN = "DROP TABLE IF EXISTS " + ImageResolutionTableHelper._NAME;
    
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";

	private static DarwinDatabaseHelper db;

	private static Context CONTEXT;
    
	
	/**
	 * Initialize
	 * @param context
	 */
	public static void init(Context context) {
		DarwinDatabaseHelper.CONTEXT = context;
	}
	
	/**
	 * Get instance
	 * @return
	 */
    public static DarwinDatabaseHelper getInstance() {
    	int version = DATABASE_VERSION;
    	try {
    		version = CONTEXT.getPackageManager().getPackageInfo(CONTEXT.getPackageName(), 0).versionCode;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
    	
    	return (db == null) ? db = new DarwinDatabaseHelper(version) : db;
    }
    
    /**
	 * Get instance
	 * @return
	 */
    public static DarwinDatabaseHelper getInstance(Context ctx) {
    	int version = DATABASE_VERSION;
    	if(CONTEXT == null){
    		CONTEXT = ctx;
    	}
    	try {
    		version = CONTEXT.getPackageManager().getPackageInfo(CONTEXT.getPackageName(), 0).versionCode;
			
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    	
    	return (db == null) ? db = new DarwinDatabaseHelper(version) : db;
    }
    
    /**
     * Constructor
     * @throws NameNotFoundException 
     */
    DarwinDatabaseHelper(int version) {
        super(CONTEXT, DB_NAME, null, version);
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MAIN);
        db.execSQL(SectionsTablesHelper.CREATE);
        db.execSQL(CategoriesTableHelper.CREATE);
        db.execSQL(LastViewedTableHelper.CREATE);
        db.execSQL(RelatedItemsTableHelper.CREATE);
        db.execSQL(SearchRecentQueriesTableHelper.CREATE);
        db.execSQL(CountriesConfigsTableHelper.CREATE);
        db.execSQL(FavouriteTableHelper.CREATE);
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	// Drop older table if existed
        db.execSQL(SQL_DROP_MAIN);
        db.execSQL(SQL_DROP_TABLE + CategoriesTableHelper.TABLE);
        db.execSQL(SQL_DROP_TABLE + SectionsTablesHelper.TABLE);
        db.execSQL(SQL_DROP_TABLE + LastViewedTableHelper.TABLE);
        db.execSQL(SQL_DROP_TABLE + RelatedItemsTableHelper.TABLE_RELATED);
        db.execSQL(SearchRecentQueriesTableHelper.DROP);
        db.execSQL(CountriesConfigsTableHelper.DROP);
        db.execSQL(SQL_DROP_TABLE + FavouriteTableHelper.TABLE);
        // Create tables again
        onCreate(db);
    }
    
    public boolean exists(String table) {
        try {
             db.getReadableDatabase().execSQL("SELECT * FROM " + table);
             return true;
        } catch (SQLException e) {
             return false;
        }
    }
    
    public void forceDatabaseUpdate(){
    	//Drop tables
    	db.getWritableDatabase().execSQL(SQL_DROP_MAIN);
        db.getWritableDatabase().execSQL(SQL_DROP_TABLE + CategoriesTableHelper.TABLE);
        db.getWritableDatabase().execSQL(SQL_DROP_TABLE + SectionsTablesHelper.TABLE);
        db.getWritableDatabase().execSQL(SQL_DROP_TABLE + LastViewedTableHelper.TABLE);
        db.getWritableDatabase().execSQL(SQL_DROP_TABLE + RelatedItemsTableHelper.TABLE_RELATED);
        db.getWritableDatabase().execSQL(SearchRecentQueriesTableHelper.DROP);
        db.getWritableDatabase().execSQL(CountriesConfigsTableHelper.DROP);
        db.getWritableDatabase().execSQL(SQL_DROP_TABLE + FavouriteTableHelper.TABLE);
        // Create
    	db.getWritableDatabase().execSQL(SQL_CREATE_MAIN);
        db.getWritableDatabase().execSQL(SectionsTablesHelper.CREATE);
        db.getWritableDatabase().execSQL(CategoriesTableHelper.CREATE);
        db.getWritableDatabase().execSQL(LastViewedTableHelper.CREATE);
        db.getWritableDatabase().execSQL(RelatedItemsTableHelper.CREATE);
        db.getWritableDatabase().execSQL(SearchRecentQueriesTableHelper.CREATE);
        db.getWritableDatabase().execSQL(CountriesConfigsTableHelper.CREATE);
        db.getWritableDatabase().execSQL(FavouriteTableHelper.CREATE);
    }
}