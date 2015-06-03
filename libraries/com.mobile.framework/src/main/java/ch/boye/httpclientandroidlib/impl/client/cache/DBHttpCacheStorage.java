//package ch.boye.httpclientandroidlib.impl.client.cache;
//
//import android.content.Context;
//
//import com.mobile.framework.network.HttpCacheDatabaseHelper;
//
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//
//import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
//import ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage;
//import ch.boye.httpclientandroidlib.client.cache.HttpCacheUpdateCallback;
//import de.akquinet.android.androlog.Log;
//
///**
// * Extended {@link ManagedHttpCacheStorage} implementation that saves
// * {@link HttpCacheEntry}s in an database.
// *
// * @author Ralph Holland-Moritz
// *
// */
//public class DBHttpCacheStorage implements HttpCacheStorage {
//
//	/** Logging tag. */
//	private static final String TAG = DBHttpCacheStorage.class.getSimpleName();
//	private static Boolean logDebugEnabled = true;
//	private HttpCacheDatabaseHelper dbHelper;
//	private DefaultHttpCacheEntrySerializer serializer;
//	private final CacheMap entries;
//
//	/**
//	 * @param config
//	 *            Cache configuration.
//	 */
//	public DBHttpCacheStorage(Context context, CacheConfig config) {
//		dbHelper = HttpCacheDatabaseHelper.getInstance(context);
//		serializer = new DefaultHttpCacheEntrySerializer();
//		entries = new CacheMap(config.getMaxCacheEntries());
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage
//	 * #getEntry(java.lang .String)
//	 */
//	@Override
//	public HttpCacheEntry getEntry(String url) throws IOException {
//		if (logDebugEnabled) {
//			Log.d(TAG, "Searching entry for key " + url);
//		}
//		HttpCacheEntry entry = entries.get(url);
//		if (entry == null) {
//			byte[] serializedEntry = dbHelper.getCacheEntry(url);
//			if (serializedEntry != null) {
//				entry = serializer.readFrom(new ByteArrayInputStream(serializedEntry));
//				if (entry != null) {
//					entries.put(url, entry);
//				}
//			}
//		}
//		return entry;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage
//	 * #putEntry(java.lang .String,
//	 * ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry)
//	 */
//	@Override
//	public void putEntry(String url, HttpCacheEntry entry) throws IOException {
//		if (logDebugEnabled) {
//			Log.d(TAG, "Putting entry for key " + url + "\n" + entry);
//		}
//		if (entries.put(url, entry) == null) {
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			serializer.writeTo(entry, baos);
//			dbHelper.insert(url, baos.toByteArray());
//		}
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage
//	 * #removeEntry(java.lang .String)
//	 */
//	@Override
//	public void removeEntry(String url) throws IOException {
//		if (logDebugEnabled) {
//			Log.d(TAG, "Removing entry for key " + url);
//		}
//		if (entries.remove(url) != null) {
//            dbHelper.delete(url);
//		}
//	}
//
//    public void removeEntryDB(String url) throws IOException{
//        if (logDebugEnabled) {
//            Log.d(TAG, "Removing entry from DB for key " + url);
//        }
//        entries.remove(url);
//        dbHelper.delete(url);
//    }
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see
//	 * ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage
//	 * #updateEntry(java.lang .String,
//	 * ch.boye.httpclientandroidlib.client.cache.HttpCacheUpdateCallback)
//	 */
//	@Override
//	public void updateEntry(String url, HttpCacheUpdateCallback callback) throws IOException {
//		if (logDebugEnabled) {
//			Log.d(TAG, "Updating entry for key " + url);
//		}
//		HttpCacheEntry exitingEntry = getEntry(url);
//		HttpCacheEntry updatedEntry = callback.update(exitingEntry);
//		if (exitingEntry != updatedEntry) {
//			if (logDebugEnabled) {
//				Log.d(TAG, "Updating entry for key " + url + "\nexiting: " + exitingEntry + "\nupdated: "
//						+ updatedEntry);
//			}
//			putEntry(url, updatedEntry);
//		}
//	}
//
//}
