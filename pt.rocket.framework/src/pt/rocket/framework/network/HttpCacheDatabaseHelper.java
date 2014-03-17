/**
 * 
 */
package pt.rocket.framework.network;

import de.akquinet.android.androlog.Log;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.akquinet.android.androlog.Log;

/**
 * @author nutzer2
 * @modified by Manuel Silva - 30/01/2014
 * 
 */
public class HttpCacheDatabaseHelper extends SQLiteOpenHelper {
	private final static String TAG = HttpCacheDatabaseHelper.class.getSimpleName();

	private static HttpCacheDatabaseHelper sInstance = null;

	/**
	 * The database version. Increment on modification!
	 */
	public static final String COLUMN_ID = "_id";
//	public static final String COLUMN_URI = "uri";
	public static final String COLUMN_PAYLOAD = "payload";
	public static final String COLUMN_TIMESTAMP = "timestamp";

	/** Database name. */
	public static final String DB_NAME = "http_cache";
	/** Database cache entries table name. */
	public static final String TABLE_NAME = "httpCacheEntries";
	private static final String SQL_CREATE_MAIN = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID
			+ " INTEGER PRIMARY KEY, " + COLUMN_PAYLOAD + " BLOB, " + COLUMN_TIMESTAMP + " NUMERIC )";

//	private SQLiteDatabase readableDatabase;
//
//	private SQLiteDatabase writableDatabase;

	/**
	 * Returns the singleton instance.
	 * 
	 * @param context
	 *            The context to use for initialization.
	 * @return The initialized instance.
	 */
	public static HttpCacheDatabaseHelper getInstance(Context context) {
		if (sInstance == null) {
			try {
				sInstance = new HttpCacheDatabaseHelper(context.getApplicationContext());
			} catch (NameNotFoundException e) {
				// should never happen
			}
		}
		return sInstance;
	}

	/**
	 * @param context
	 *            The context.
	 * @throws NameNotFoundException 
	 */
	private HttpCacheDatabaseHelper(Context context) throws NameNotFoundException {
		super(context, DB_NAME, null,
				context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
//		readableDatabase = getReadableDatabase();
//		writableDatabase = getWritableDatabase();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_MAIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                 + newVersion + ", which will destroy all old data");
		 db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		 onCreate(db);
	}

	/**
	 * Inserts a cache entry into the db.
	 * 
	 * @param key
	 *            The key (url) to be used.
	 * @param cacheEntry
	 *            The serialized cache entry.
	 * @return The id of the inserted entry.
	 */
	public long insert(String key, byte[] cacheEntry) {
		ContentValues values = new ContentValues();
		values.put(COLUMN_ID, key.hashCode());
//		values.put(COLUMN_URI, key);
		values.put(COLUMN_PAYLOAD, cacheEntry);
		values.put(COLUMN_TIMESTAMP, System.currentTimeMillis());
		return getWritableDatabase().insert(TABLE_NAME, null, values);
	}

	/**
	 * Returns the cache entry for the key.
	 * 
	 * @param key
	 *            The key (url) for which the cache entry is returned.
	 * @return The found serialized cache entry if exist, else <code>null</code>.
	 */
	public byte[] getCacheEntry(String key) {
		byte[] cacheEntry = null;
		
		Cursor entryCursor = getReadableDatabase().query(TABLE_NAME, new String[] { COLUMN_PAYLOAD },
				COLUMN_ID + " = " + key.hashCode(), null, null, null, null);
		if (entryCursor != null) {
			if (entryCursor.getCount() > 0) {
				entryCursor.moveToFirst();
				cacheEntry = entryCursor.getBlob(entryCursor
						.getColumnIndexOrThrow(COLUMN_PAYLOAD));
			}
			entryCursor.close();
		}
		return cacheEntry;
	}

	/**
	 * Deletes an entry for the given key.
	 * 
	 * @param key
	 *            The key under which the entry is saved.
	 */
	public void delete(String key) {
		SQLiteDatabase writableDatabase = getWritableDatabase();
		writableDatabase.delete(TABLE_NAME, COLUMN_ID + " = " + key.hashCode(), null);
	}
}
