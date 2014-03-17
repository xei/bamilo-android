package pt.rocket.framework.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author sergiopereira
 *
 */
public class DarwinDatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;

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
    	return (db == null) ? db = new DarwinDatabaseHelper() : db;
    }
    
    /**
     * Constructor
     */
    DarwinDatabaseHelper() {
        super(CONTEXT, DB_NAME, null, DATABASE_VERSION);
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MAIN);
        db.execSQL(SectionsTablesHelper.CREATE);
        db.execSQL(CategoriesTableHelper.CREATE);
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
        // Create tables again
        onCreate(db);
    }
    
}