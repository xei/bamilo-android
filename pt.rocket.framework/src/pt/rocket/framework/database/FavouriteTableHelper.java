package pt.rocket.framework.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.Variation;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

/**
 * This class is a helper to manage the Favourite on database.
 * 
 * @author Andre Lopes
 * 
 */
public class FavouriteTableHelper {

	private static final String TAG = FavouriteTableHelper.class.getSimpleName();

	// Table Name
	public static final String TABLE = "favourite";

	// TODO : CONSTANTS
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _FAVOURITE_SKU = "favourite_sku";
	public static final String _FAVOURITE_BRAND = "favourite_brand";
	public static final String _FAVOURITE_NAME = "favourite_name";
	public static final String _FAVOURITE_PRICE = "favourite_price";
	public static final String _FAVOURITE_SPECIAL_PRICE = "favourite_special_price";
	public static final String _FAVOURITE_DISCOUNT_PERCENTAGE = "favourite_discount_percentage";
	public static final String _FAVOURITE_URL = "favourite_url";
	public static final String _FAVOURITE_IMAGE_URL = "favourite_image_url";
	public static final String _FAVOURITE_IS_NEW = "favourite_is_new";
	public static final String _FAVOURITE_SIMPLES_JSON = "favourite_simples_json";
	public static final String _FAVOURITE_VARIATIONS_JSON = "favourite_variations_json";
	public static final String _FAVOURITE_KNOWN_VARIATIONS_LIST = "favourite_known_variations_list";
	public static final String _FAVOURITE_IS_COMPLETE = "favourite_is_complete";

	private static String DELIMITER = ":::::";
	
	// Create table
	public static final String CREATE =
			new StringBuilder("CREATE TABLE ").append(TABLE).append(" (")
				.append(_ID).append(" INTEGER PRIMARY KEY, ")
				.append(_FAVOURITE_SKU).append(" TEXT,")
				.append(_FAVOURITE_BRAND).append(" TEXT,")
				.append(_FAVOURITE_NAME).append(" TEXT,")
				.append(_FAVOURITE_PRICE).append(" TEXT,")
				.append(_FAVOURITE_SPECIAL_PRICE).append(" TEXT,")
				.append(_FAVOURITE_DISCOUNT_PERCENTAGE).append(" DOUBLE, ")
				.append(_FAVOURITE_URL).append(" TEXT,")
				.append(_FAVOURITE_IMAGE_URL).append(" TEXT, ")
				.append(_FAVOURITE_IS_NEW).append(" TEXT, ")
				.append(_FAVOURITE_SIMPLES_JSON).append(" TEXT, ")
				.append(_FAVOURITE_VARIATIONS_JSON).append(" TEXT, ")
				.append(_FAVOURITE_KNOWN_VARIATIONS_LIST).append(" TEXT, ")
				.append(_FAVOURITE_IS_COMPLETE).append(" TEXT")
				.append(")").toString();

	/**
	 * Insert favourite into database
	 * 
	 * @param sku
	 * @param brand
	 * @param name
	 * @param price
	 * @param special_price
	 * @param discount_percentage
	 * @param url
	 * @param isNew
	 * @param image_url
	 * @param simples
	 * @param known_variations
	 */
	public static void insertFavouriteProduct(String sku, String brand, String name, String price,
			String special_price, Double discount_percentage, String url, String image_url, boolean isNew,
			ArrayList<ProductSimple> simples, List<Variation> variations, ArrayList<String> knownVariations) {
		if (!verifyIfFavourite(sku)) {
			SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FavouriteTableHelper._FAVOURITE_SKU, sku);
			values.put(FavouriteTableHelper._FAVOURITE_BRAND, brand);
			values.put(FavouriteTableHelper._FAVOURITE_NAME, name);
			values.put(FavouriteTableHelper._FAVOURITE_PRICE, price);
			values.put(FavouriteTableHelper._FAVOURITE_SPECIAL_PRICE, special_price);
			values.put(FavouriteTableHelper._FAVOURITE_DISCOUNT_PERCENTAGE, discount_percentage);
			values.put(FavouriteTableHelper._FAVOURITE_URL, url);
			values.put(FavouriteTableHelper._FAVOURITE_IMAGE_URL, image_url);
			values.put(FavouriteTableHelper._FAVOURITE_IS_NEW, isNew);

			String simplesJSON = "";
			if (simples != null && !simples.isEmpty()) {
				JSONArray simplesJSONArray = new JSONArray();
				for (ProductSimple productSimple : simples) {
					simplesJSONArray.put(productSimple.toJSON());
				}
				simplesJSON = simplesJSONArray.toString();
			}
			values.put(FavouriteTableHelper._FAVOURITE_SIMPLES_JSON, simplesJSON);

			String variationsJSON = "";
			if (variations != null && !variations.isEmpty()) {
				JSONArray variationsJSONArray = new JSONArray();
				for (Variation variation : variations) {
					variationsJSONArray.put(variation.toJSON());
				}
				variationsJSON = variationsJSONArray.toString();
			}
			values.put(FavouriteTableHelper._FAVOURITE_VARIATIONS_JSON, variationsJSON);

			String knownVariationsString = "";
			if (knownVariations != null && !knownVariations.isEmpty()) {
				StringBuilder knownVariationsStringBuilder = new StringBuilder();
				for (String knownVariation : knownVariations) {
					knownVariationsStringBuilder.append(knownVariation);
					knownVariationsStringBuilder.append(DELIMITER);
				}
				knownVariationsString = knownVariationsStringBuilder.toString();
			}
			values.put(FavouriteTableHelper._FAVOURITE_KNOWN_VARIATIONS_LIST, knownVariationsString);

			values.put(FavouriteTableHelper._FAVOURITE_IS_COMPLETE, true);

			db.insert(FavouriteTableHelper.TABLE, null, values);
			db.close();
		}
	}

	/**
	 * Insert favourite into database without variations values
	 * 
	 * @param sku
	 * @param brand
	 * @param name
	 * @param price
	 * @param special_price
	 * @param discount_percentage
	 * @param url
	 * @param image_url
	 * @param isNew
	 */
	public static void insertPartialFavouriteProduct(String sku, String brand, String name, String price,
			String special_price, Double discount_percentage, String url, String image_url, boolean isNew) {
		if (!verifyIfFavourite(sku)) {
			SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FavouriteTableHelper._FAVOURITE_SKU, sku);
			values.put(FavouriteTableHelper._FAVOURITE_BRAND, brand);
			values.put(FavouriteTableHelper._FAVOURITE_NAME, name);
			values.put(FavouriteTableHelper._FAVOURITE_PRICE, price);
			values.put(FavouriteTableHelper._FAVOURITE_SPECIAL_PRICE, special_price);
			values.put(FavouriteTableHelper._FAVOURITE_DISCOUNT_PERCENTAGE, discount_percentage);
			values.put(FavouriteTableHelper._FAVOURITE_URL, url);
			values.put(FavouriteTableHelper._FAVOURITE_IMAGE_URL, image_url);
			values.put(FavouriteTableHelper._FAVOURITE_IS_NEW, isNew);
			values.put(FavouriteTableHelper._FAVOURITE_SIMPLES_JSON, "");
			values.put(FavouriteTableHelper._FAVOURITE_VARIATIONS_JSON, "");
			values.put(FavouriteTableHelper._FAVOURITE_KNOWN_VARIATIONS_LIST, "");
			values.put(FavouriteTableHelper._FAVOURITE_IS_COMPLETE, false);

			db.insert(FavouriteTableHelper.TABLE, null, values);
			db.close();
		}
	}

	/**
	 * Update favourite with variations values
	 * 
	 * @param sku
	 * @param simples
	 * @param variations
	 * @param knownVariations
	 */
	public static void updateFavouriteProduct(String sku, ArrayList<ProductSimple> simples, List<Variation> variations, ArrayList<String> knownVariations) {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		ContentValues values = new ContentValues();

		String simplesJSON = "";
		if (simples != null && !simples.isEmpty()) {
			JSONArray simplesJSONArray = new JSONArray();
			for (ProductSimple productSimple : simples) {
				simplesJSONArray.put(productSimple.toJSON());
			}
			simplesJSON = simplesJSONArray.toString();
		}
		values.put(FavouriteTableHelper._FAVOURITE_SIMPLES_JSON, simplesJSON);

		String variationsJSON = "";
		if (variations != null && !variations.isEmpty()) {
			JSONArray variationsJSONArray = new JSONArray();
			for (Variation variation : variations) {
				variationsJSONArray.put(variation.toJSON());
			}
			variationsJSON = variationsJSONArray.toString();
		}
		values.put(FavouriteTableHelper._FAVOURITE_VARIATIONS_JSON, variationsJSON);

		String knownVariationsString = "";
		if (knownVariations != null && !knownVariations.isEmpty()) {
			StringBuilder knownVariationsStringBuilder = new StringBuilder();
			for (String knownVariation : knownVariations) {
				knownVariationsStringBuilder.append(knownVariation);
				knownVariationsStringBuilder.append(DELIMITER);
			}
			knownVariationsString = knownVariationsStringBuilder.toString();
		}
		values.put(FavouriteTableHelper._FAVOURITE_KNOWN_VARIATIONS_LIST, knownVariationsString);
		
		values.put(FavouriteTableHelper._FAVOURITE_IS_COMPLETE, true);

		db.update(FavouriteTableHelper.TABLE, values, FavouriteTableHelper._FAVOURITE_SKU + "=?", new String[]{sku});
		db.close();
	}

	/**
	 * Verifies if the product is already on the favourite list
	 * 
	 * @param sku
	 * @return
	 */
	public static boolean verifyIfFavourite(String sku) {
		boolean result = false;
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		String query = new StringBuilder("select count(*) from ").append(TABLE)
				.append(" where ").append(_FAVOURITE_SKU).append(" = '").append(sku).append("'").toString();
		Log.i(TAG, "SQL RESULT query :  " + query);
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			if (cursor.getInt(0) >= 1) {
				result = true;
			} else {
				result = false;
			}
			// Log result
			Log.i(TAG, "SQL RESULT: " + cursor.getInt(0) + " result is : " + result);
		}

		// Validate cursor
		if (cursor != null) {
			cursor.close();
		}

		db.close();

		return result;
	}

	public static ArrayList<String> getIncompleteFavouriteList() { // XXX
		ArrayList<String> incomplete = new ArrayList<String>();
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		String query = "SELECT " + _FAVOURITE_URL + " FROM " + TABLE + " WHERE " + _FAVOURITE_IS_COMPLETE + " == '0'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				incomplete.add(cursor.getString(0));
			}
		}

		// Validate cursor
		if (cursor != null) {
			cursor.close();
		}

		db.close();
		return incomplete;
	}

	/**
	 * Get the favourite list of entries
	 * 
	 * @return
	 * @throws JSONException 
	 */
	public static ArrayList<Favourite> getFavouriteList() {
		ArrayList<Favourite> favourites = new ArrayList<Favourite>();
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		String query = new StringBuilder("select * from ").append(TABLE).append(" order by ").append(_ID).append(" desc").toString();
		Log.i(TAG, "SQL RESULT query :  " + query);
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				int index = 1;
				Favourite favourite = new Favourite();
				favourite.setSku(cursor.getString(index++));
				favourite.setBrand(cursor.getString(index++));
				favourite.setName(cursor.getString(index++));
				favourite.setPrice(cursor.getString(index++));
				favourite.setSpecialPrice(cursor.getString(index++));
				favourite.setMaxSavingPercentage(cursor.getDouble(index++));
				favourite.setUrl(cursor.getString(index++));
				favourite.getImageList().add(cursor.getString(index++));
				favourite.setNew(cursor.getInt(index++) == 1);

				// convert simples from JSON to ArrayList
				String simplesJSON = cursor.getString(index++);
				if (!TextUtils.isEmpty(simplesJSON)) {
					try {
						ArrayList<ProductSimple> simples = new ArrayList<ProductSimple>();
						JSONArray simpleArray;
						simpleArray = new JSONArray(simplesJSON);
						for (int i = 0; i < simpleArray.length(); ++i) {
							ProductSimple simple = new ProductSimple();
							JSONObject simpleObject = simpleArray.getJSONObject(i);
							simple.initialize(simpleObject);

							// String simpleSKU = simple.getAttributes().get(RestConstants.JSON_SKU_TAG);
							simples.add(simple);
						}
						favourite.setSimples(simples);
						// NO_SIMPLE_SELECTED @ FavouritesListAdapter
						favourite.setSelectedSimple(-1); // NO_SIMPLE_SELECTED
					} catch (JSONException e) {
						Log.e(TAG, "JSONException on sku: " + favourite.getSku() + "\nsimplesJSON = " + simplesJSON);
					}
				}


				// convert variations from JSON to ArrayList
				String variationsJSON = cursor.getString(index++);
				if (!TextUtils.isEmpty(variationsJSON)) {
					try {
						ArrayList<Variation> variations = new ArrayList<Variation>();
						JSONArray variationArray = new JSONArray(variationsJSON);
						for (int i = 0; i < variationArray.length(); ++i) {
							Variation variation = new Variation();
							JSONObject variationObject = variationArray.getJSONObject(i);
							variation.initialize(variationObject);

							variations.add(variation);
						}
						favourite.setVariations(variations);
					} catch (JSONException e) {
						Log.e(TAG, "JSONException on sku: " + favourite.getSku() + "\nvariationsJSON = " + variationsJSON);
					}
				}

				// convert knownVariations from Joined String to ArrayList
				String knownVariationsString = cursor.getString(index++);
				if (!TextUtils.isEmpty(knownVariationsString)) {
					String[] knownVariationsSplit = knownVariationsString.split(DELIMITER);
					if (knownVariationsSplit != null && knownVariationsSplit.length > 0) {
						favourite.setKnownVariations(new ArrayList<String>(Arrays.asList(knownVariationsSplit)));
					}
				}

				favourite.setComplete(cursor.getInt(index++) == 1);

				favourites.add(favourite);
			}
		}

		// Validate cursor
		if (cursor != null) {
			cursor.close();
		}

		db.close();
		return favourites;
	}

	/**
	 * Remove favourite product into database
	 * 
	 * @param sku
	 */
	public static void removeFavouriteProduct(String sku) {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		String query = new StringBuilder("delete from ").append(TABLE)
				.append(" where ").append(_FAVOURITE_SKU).append(" = '").append(sku).append("'").toString();
		Log.i(TAG, "SQL RESULT query :  " + query);
		db.execSQL(query);
		db.close();
	}

	/**
	 * Delete all Favourites
	 */
	public static void deleteAllFavourite() {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		db.delete(TABLE, null, null);
		db.close();
	}

	/**
	 * Clears the Favourite table
	 * 
	 * @param db
	 */
	public static void clearFavourite(SQLiteDatabase db) {
		db.delete(TABLE, null, null);
	}

}
