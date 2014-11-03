package pt.rocket.framework.database;

import java.util.ArrayList;
import org.apache.commons.collections4.CollectionUtils;
import de.akquinet.android.androlog.Log;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper.
 * @author sergiopereira
 */
public class DarwinDatabaseHelper extends SQLiteOpenHelper {
	
	public static final String TAG = DarwinDatabaseHelper.class.getName();
	
	public static final String DB_NAME = "darwin.db";
	
	private static final int DATABASE_VERSION = 44;
	
    private static final String DROP_TABLE = "DROP TABLE IF EXISTS %1$s";
    
    private static final String RENAME_TABLE = "ALTER TABLE %1$s RENAME TO %2$s";
    
    private static final String COPY_TABLE = "INSERT OR IGNORE INTO %1$s (%2$s) SELECT %2$s FROM %3$s";
    
    private static final String INFO_TABLE = "PRAGMA table_info('%1$s')";
    
    public enum TableType { CACHE, PERSIST, FREEZE };
    
    private static DarwinDatabaseHelper db;

    private static Context CONTEXT;
    
    private BaseTable mTables[] = {
            new ImageResolutionTableHelper(),
            new CategoriesTableHelper(),
            new SectionsTablesHelper(),
            new LastViewedTableHelper(),
            new RelatedItemsTableHelper(),
            new SearchRecentQueriesTableHelper(),
            new CountriesConfigsTableHelper(),
            new FavouriteTableHelper()
            };
	
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
    	return (db == null) ? db = new DarwinDatabaseHelper(DATABASE_VERSION) : db;
    }
    
    /**
	 * Get instance
	 * @return
	 */
    public static DarwinDatabaseHelper getInstance(Context ctx) {
        // Update context
        if(CONTEXT == null) CONTEXT = ctx;
    	return (db == null) ? db = new DarwinDatabaseHelper(DATABASE_VERSION) : db;
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
        for (BaseTable table : mTables) {
            Log.i(TAG, "ON CREATE TABLE: " + table.getName());
            db.execSQL(table.create(table.getName()));
        }
    }

    /* (non-Javadoc)
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (BaseTable table : mTables) {
            // Validate table type
            switch (table.getUpgradeType()) {
            case PERSIST:
                onUpgradePersistTable(db, newVersion, table);
                break;
            case CACHE:
                onUpgradeCacheTable(db, table);
                break;
            case FREEZE:
            default:
                onUpgradeFreezeTable(table);
                break;
            }
        }
    }

    /**
     * Not upgrade table.
     * @param table
     * @author sergiopereira
     */
    private void onUpgradeFreezeTable(BaseTable table){
        Log.i(TAG, "NOT UPGRADE TABLE: " + table.getName());
    }
    
    /**
     * Create a new empty table. (Drop and Create)
     * @param db
     * @param table
     * @author sergiopereira
     */
    private void onUpgradeCacheTable(SQLiteDatabase db, BaseTable table) {
        // Get table name
        String tableName = table.getName();
        Log.i(TAG, "ON UPGRADE CACHE TABLE: " + tableName);
        // Drop table
        db.execSQL(String.format(DROP_TABLE, tableName));
        // Create table
        db.execSQL(table.create(tableName));
    }
    
    /**
     * Upgrade the table trying copy the old content for the new table schema.<br>
     * WARNING: Not support table rename, change table type.<br>
     * @param db
     * @param newVersion
     * @param table
     */
    private void onUpgradePersistTable(SQLiteDatabase db, int newVersion, BaseTable table) {
        // Get table name
        String currentTableName = table.getName();
        Log.i(TAG, "ON UPGRADE PERSIST TABLE: " + currentTableName);
        // Get temporary table name
        String tempTableName = currentTableName + "_temp_" + newVersion;
        // Create temporary table
        db.execSQL(table.create(tempTableName));
        // Match columns
        ArrayList<String> common = matchColumns(db, tempTableName, currentTableName);
        // Validate columns and copy
        if(CollectionUtils.isNotEmpty(common)) copyContent(db, common, tempTableName, currentTableName);
        // Drop old
        db.execSQL(String.format(DROP_TABLE, currentTableName));
        // Rename new
        db.execSQL(String.format(RENAME_TABLE, tempTableName, currentTableName));
    }
    
    /**
     * Copy data from table2 to table1.
     * @param db
     * @param common
     * @param table1
     * @param table2
     * @author sergiopereira
     */
    private void copyContent(SQLiteDatabase db, ArrayList<String> common, String table1, String table2) {
        try {
            // Create columns string
            String columns = "";
            for (int i = 0; i < common.size(); i++) columns += common.get(i) + (i < common.size() - 1 ? ", " : "");
            //Log.i(TAG, "MATCH COLUMNS: " + columns);
            // Insert content from table2 to table1
            db.execSQL(String.format(COPY_TABLE, table1, columns, table2));
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING: NPE ON COPY CONTENT FROM " + table2 + " FOR " +  table1, e);
        } catch (SQLException e) {
            Log.w(TAG, "WARNING: SQLE ON COPY CONTENT FROM " + table2 + " FOR " +  table1, e);
        }
    }
    
    /**
     * Compare tables and return a list of same columns.
     * @param db
     * @param table1
     * @param trable2
     * @return ArrayList<String>
     * @author sergiopereira
     */
    private ArrayList<String> matchColumns(SQLiteDatabase db, String table1, String trable2) {
        ArrayList<String> columns1 = getColumns(db, table1);
        ArrayList<String> columns2 = getColumns(db, trable2);
        return (ArrayList<String>) CollectionUtils.retainAll(columns1, columns2); 
    }
    
    /**
     * Get table columns.
     * @param db
     * @param table
     * @return ArrayList<String>
     * @author sergiopereira
     */
    private ArrayList<String> getColumns(SQLiteDatabase db, String table) {
        ArrayList<String> array = new ArrayList<String>();
        Cursor cursor = db.rawQuery(String.format(INFO_TABLE, table), null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
          String name = cursor.getString(1);
          //Log.i(TAG, "TABLE: " + table + " COLUMN: " + name);
          array.add(name);
        }
        cursor.close();
        return array;
    }

}