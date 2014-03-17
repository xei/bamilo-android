package pt.rocket.framework.database;

import de.akquinet.android.androlog.Log;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author sergiopereira
 *
 */
public class DarwinDatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 3;

    public static final String DB_NAME = "darwin.db";
    
    private static final String SQL_CREATE_MAIN = ImageResolutionTableHelper.CREATE;
    
    private static final String SQL_DROP_MAIN = "DROP TABLE IF EXISTS " + ImageResolutionTableHelper._NAME;
    
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS ";

	private static final String TAG = DarwinDatabaseHelper.class.getName();

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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
        // Create tables again
        onCreate(db);
    }
    
}