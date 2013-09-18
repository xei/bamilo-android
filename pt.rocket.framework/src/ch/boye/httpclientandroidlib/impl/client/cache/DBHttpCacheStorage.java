package ch.boye.httpclientandroidlib.impl.client.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import pt.rocket.framework.Darwin;
import pt.rocket.framework.network.HttpCacheDatabaseHelper;

import android.content.Context;

import ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheStorage;
import ch.boye.httpclientandroidlib.client.cache.HttpCacheUpdateCallback;
import ch.boye.httpclientandroidlib.impl.client.cache.CacheConfig;
import ch.boye.httpclientandroidlib.impl.client.cache.DefaultHttpCacheEntrySerializer;
import ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage;
import de.akquinet.android.androlog.Log;

/**
 * Extended {@link ManagedHttpCacheStorage} implementation that saves
 * {@link HttpCacheEntry}s in an database.
 * 
 * @author Ralph Holland-Moritz
 * 
 */
public class DBHttpCacheStorage implements HttpCacheStorage {

	/** Logging tag. */
	private static final String TAG = DBHttpCacheStorage.class.getSimpleName();
	private HttpCacheDatabaseHelper dbHelper;
	private DefaultHttpCacheEntrySerializer serializer;
	private final CacheMap entries;

	/**
	 * @param config
	 *            Cache configuration.
	 */
	public DBHttpCacheStorage(Context context, CacheConfig config) {
		dbHelper = HttpCacheDatabaseHelper.getInstance(context);
		serializer = new DefaultHttpCacheEntrySerializer();
		entries = new CacheMap(config.getMaxCacheEntries());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage
	 * #getEntry(java.lang .String)
	 */
	@Override
	public HttpCacheEntry getEntry(String url) throws IOException {
		if (Darwin.logDebugEnabled) {
			Log.d(TAG, "Searching entry for key " + url);
		}
		HttpCacheEntry entry = entries.get(url);
		if (entry == null) {
			byte[] serializedEntry = dbHelper.getCacheEntry(url);
			if (serializedEntry != null) {
				entry = serializer.readFrom(new ByteArrayInputStream(serializedEntry));
				if (entry != null) {
					if (Darwin.logDebugEnabled) {
						Log.d(TAG, "Adding entry for key " + url);
					}
					entries.put(url, entry);
				}
			}
		}
		return entry;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage
	 * #putEntry(java.lang .String,
	 * ch.boye.httpclientandroidlib.client.cache.HttpCacheEntry)
	 */
	@Override
	public void putEntry(String url, HttpCacheEntry entry) throws IOException {
		if (entries.put(url, entry) == null ) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			serializer.writeTo(entry, baos);
			dbHelper.insert(url, baos.toByteArray());
		}
		
		if (Darwin.logDebugEnabled) {
			Log.d(TAG, "Putting entry for key |" + entries.size() + "| " + url +  "\n" + entry);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage
	 * #removeEntry(java.lang .String)
	 */
	@Override
	public void removeEntry(String url) throws IOException {
		if (Darwin.logDebugEnabled) {
			Log.d(TAG, "Removing entry for key " + url);
		}
		if (entries.remove(url) != null) {
			dbHelper.delete(url);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.boye.httpclientandroidlib.impl.client.cache.ManagedHttpCacheStorage
	 * #updateEntry(java.lang .String,
	 * ch.boye.httpclientandroidlib.client.cache.HttpCacheUpdateCallback)
	 */
	@Override
	public void updateEntry(String url, HttpCacheUpdateCallback callback) throws IOException {
		if (Darwin.logDebugEnabled) {
			Log.d(TAG, "Updating entry for key " + url);
		}
		HttpCacheEntry existingEntry = getEntry(url);
		HttpCacheEntry updatedEntry = callback.update(existingEntry);
		if (existingEntry != updatedEntry) {
			if (Darwin.logDebugEnabled) {
				Log.d(TAG, "Updating entry for key " + url + " existing: " + existingEntry + " updated: "
						+ updatedEntry);
			}
			putEntry(url, updatedEntry);
		}
	}

}
