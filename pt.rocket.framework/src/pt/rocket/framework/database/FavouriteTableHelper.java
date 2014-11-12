package pt.rocket.framework.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.database.DarwinDatabaseHelper.TableType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.objects.Product;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.Variation;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

/**
 * This class is a helper to manage the Favourite on database.
 * 
 * @author Andre Lopes
 * 
 */
public class FavouriteTableHelper extends BaseTable {

	private static final String TAG = FavouriteTableHelper.class.getSimpleName();

	// Table Name
	public static final String TABLE_NAME = "favourite";
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _FAVOURITE_SKU = "favourite_sku";
	public static final String _FAVOURITE_BRAND = "favourite_brand";
	public static final String _FAVOURITE_NAME = "favourite_name";
	public static final String _FAVOURITE_PRICE_ORIG = "favourite_price_orig";
	public static final String _FAVOURITE_PRICE = "favourite_price";
	public static final String _FAVOURITE_PRICE_CONVERTED = "favourite_price_converted";
	public static final String _FAVOURITE_SPECIAL_PRICE_ORIG = "favourite_special_price_orig";
	public static final String _FAVOURITE_SPECIAL_PRICE = "favourite_special_price";
	public static final String _FAVOURITE_SPECIAL_PRICE_CONVERTED = "favourite_special_price_converted";
	public static final String _FAVOURITE_DISCOUNT_PERCENTAGE = "favourite_discount_percentage";
	public static final String _FAVOURITE_URL = "favourite_url";
	public static final String _FAVOURITE_IMAGE_URL = "favourite_image_url";
	public static final String _FAVOURITE_IS_NEW = "favourite_is_new";
	public static final String _FAVOURITE_SIMPLES_JSON = "favourite_simples_json";
	public static final String _FAVOURITE_VARIATIONS_JSON = "favourite_variations_json";
	public static final String _FAVOURITE_KNOWN_VARIATIONS_LIST = "favourite_known_variations_list";
	public static final String _FAVOURITE_IS_COMPLETE = "favourite_is_complete";
	public static final String _FAVOURITE_SIZE_GUIDE = "favourite_size_guide";
//	public static final String _FAVOURITE_SIMPLE_POSITION = "favourite_simple_postion";
	

	private static String DELIMITER = ":::::";

	/*
	 * (non-Javadoc)
	 * @see pt.rocket.framework.database.BaseTable#getUpgradeType()
	 */
    @Override
    public TableType getUpgradeType() {
        return TableType.PERSIST;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.framework.database.BaseTable#getName()
     */
    @Override
    public String getName() {
        return TABLE_NAME;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.framework.database.BaseTable#create(java.lang.String)
     */
    @Override
    public String create(String table) {
        return new StringBuilder("CREATE TABLE ").append(table).append(" (")
                .append(_ID).append(" INTEGER PRIMARY KEY, ")
                .append(_FAVOURITE_SKU).append(" TEXT UNIQUE,")
                .append(_FAVOURITE_BRAND).append(" TEXT,")
                .append(_FAVOURITE_NAME).append(" TEXT,")
                .append(_FAVOURITE_PRICE).append(" TEXT,")
                .append(_FAVOURITE_PRICE_ORIG).append(" TEXT,")
                .append(_FAVOURITE_PRICE_CONVERTED).append(" TEXT,")
                .append(_FAVOURITE_SPECIAL_PRICE).append(" TEXT,")
                .append(_FAVOURITE_SPECIAL_PRICE_ORIG).append(" TEXT,")
                .append(_FAVOURITE_SPECIAL_PRICE_CONVERTED).append(" TEXT,")
                .append(_FAVOURITE_DISCOUNT_PERCENTAGE).append(" DOUBLE, ")
                .append(_FAVOURITE_URL).append(" TEXT,")
                .append(_FAVOURITE_IMAGE_URL).append(" TEXT, ")
                .append(_FAVOURITE_IS_NEW).append(" TEXT, ")
                .append(_FAVOURITE_SIMPLES_JSON).append(" TEXT, ")
                .append(_FAVOURITE_VARIATIONS_JSON).append(" TEXT, ")
                .append(_FAVOURITE_KNOWN_VARIATIONS_LIST).append(" TEXT, ")
                .append(_FAVOURITE_IS_COMPLETE).append(" TEXT, ")
                .append(_FAVOURITE_SIZE_GUIDE).append(" TEXT")
//                .append(_FAVOURITE_SIMPLE_POSITION).append(" INTEGER DEFAULT -1")
                .append(")").toString();
    }

    /*
     * ################## CRUD ##################  
     */

	/**
	 * Insert favourite into database
	 * 
	 * @param completeProduct
	 */
	public synchronized static void insertFavouriteProduct(CompleteProduct completeProduct) {
		if (completeProduct != null) {
			SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FavouriteTableHelper._FAVOURITE_SKU, completeProduct.getSku());
			values.put(FavouriteTableHelper._FAVOURITE_BRAND, completeProduct.getBrand());
			values.put(FavouriteTableHelper._FAVOURITE_NAME, completeProduct.getName());
			values.put(FavouriteTableHelper._FAVOURITE_PRICE, completeProduct.getPrice());
			values.put(FavouriteTableHelper._FAVOURITE_PRICE_ORIG, completeProduct.getPriceAsDouble());
			values.put(FavouriteTableHelper._FAVOURITE_PRICE_CONVERTED, completeProduct.getPriceConverted());
			values.put(FavouriteTableHelper._FAVOURITE_SPECIAL_PRICE, completeProduct.getSpecialPrice());
			values.put(FavouriteTableHelper._FAVOURITE_SPECIAL_PRICE_ORIG, completeProduct.getSpecialPriceAsDouble());
			values.put(FavouriteTableHelper._FAVOURITE_SPECIAL_PRICE_CONVERTED, completeProduct.getSpecialPriceConverted());
			values.put(FavouriteTableHelper._FAVOURITE_DISCOUNT_PERCENTAGE, completeProduct.getMaxSavingPercentage());
			values.put(FavouriteTableHelper._FAVOURITE_URL, completeProduct.getUrl());
			values.put(FavouriteTableHelper._FAVOURITE_IMAGE_URL, completeProduct.getImageList().size() == 0 ? "" : completeProduct.getImageList().get(0));
			values.put(FavouriteTableHelper._FAVOURITE_IS_NEW, completeProduct.isNew());
			values.put(FavouriteTableHelper._FAVOURITE_SIZE_GUIDE, completeProduct.getSizeGuideUrl());
//			values.put(FavouriteTableHelper._FAVOURITE_SIMPLE_POSITION, completeProduct.getSimpleSkuPosition());

			String simplesJSON = "";
			ArrayList<ProductSimple> simples = completeProduct.getSimples();
			if (simples != null && !simples.isEmpty()) {
				JSONArray simplesJSONArray = new JSONArray();
				for (ProductSimple productSimple : simples) {
					simplesJSONArray.put(productSimple.toJSON());
				}
				simplesJSON = simplesJSONArray.toString();
			}
			values.put(FavouriteTableHelper._FAVOURITE_SIMPLES_JSON, simplesJSON);

			String variationsJSON = "";
			ArrayList<Variation> variations = completeProduct.getVariations();
			if (variations != null && !variations.isEmpty()) {
				JSONArray variationsJSONArray = new JSONArray();
				for (Variation variation : variations) {
					variationsJSONArray.put(variation.toJSON());
				}
				variationsJSON = variationsJSONArray.toString();
			}
			values.put(FavouriteTableHelper._FAVOURITE_VARIATIONS_JSON, variationsJSON);

			String knownVariationsString = "";
			ArrayList<String> knownVariations = completeProduct.getKnownVariations();
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

			db.insert(FavouriteTableHelper.TABLE_NAME, null, values);
			db.close();
		}
	}

	/**
	 * Insert favourite into database without variations values
	 * 
	 * @param product
	 */
	public synchronized static void insertPartialFavouriteProduct(Product product) {
		if (product != null) {
			SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(FavouriteTableHelper._FAVOURITE_SKU, product.getSKU());
			values.put(FavouriteTableHelper._FAVOURITE_BRAND, product.getBrand());
			values.put(FavouriteTableHelper._FAVOURITE_NAME, product.getName());
			values.put(FavouriteTableHelper._FAVOURITE_PRICE, product.getPrice());
			values.put(FavouriteTableHelper._FAVOURITE_PRICE_ORIG, product.getPriceAsDouble());
			values.put(FavouriteTableHelper._FAVOURITE_PRICE_CONVERTED, product.getPriceConverted());
			values.put(FavouriteTableHelper._FAVOURITE_SPECIAL_PRICE, product.getSpecialPrice());
			values.put(FavouriteTableHelper._FAVOURITE_SPECIAL_PRICE_ORIG, product.getSpecialPriceAsDouble());
			values.put(FavouriteTableHelper._FAVOURITE_SPECIAL_PRICE_CONVERTED, product.getSpecialPriceConverted());
			values.put(FavouriteTableHelper._FAVOURITE_DISCOUNT_PERCENTAGE, product.getMaxSavingPercentage());
			values.put(FavouriteTableHelper._FAVOURITE_URL, product.getUrl());
			values.put(FavouriteTableHelper._FAVOURITE_IMAGE_URL, (product.getImages().size() == 0) ? "" : product.getImages().get(0).getUrl());
			values.put(FavouriteTableHelper._FAVOURITE_IS_NEW, product.getAttributes().isNew());
			values.put(FavouriteTableHelper._FAVOURITE_SIMPLES_JSON, "");
			values.put(FavouriteTableHelper._FAVOURITE_VARIATIONS_JSON, "");
			values.put(FavouriteTableHelper._FAVOURITE_KNOWN_VARIATIONS_LIST, "");
			values.put(FavouriteTableHelper._FAVOURITE_IS_COMPLETE, false);
//			values.put(FavouriteTableHelper._FAVOURITE_SIMPLE_POSITION, -1);
			db.insert(FavouriteTableHelper.TABLE_NAME, null, values);
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
	public static void updateFavouriteProduct(CompleteProduct completeProduct) {
	    
	    // Get data 
        String sku = completeProduct.getSku();
        ArrayList<ProductSimple> simples = completeProduct.getSimples();
        ArrayList<Variation> variations = completeProduct.getVariations();
        ArrayList<String> knownVariations = completeProduct.getKnownVariations();
        String sizeGuideUrl = completeProduct.getSizeGuideUrl();
	    
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
		
		values.put(FavouriteTableHelper._FAVOURITE_SIZE_GUIDE, sizeGuideUrl);

		db.update(FavouriteTableHelper.TABLE_NAME, values, FavouriteTableHelper._FAVOURITE_SKU + "=?", new String[]{sku});
		db.close();
	}

	/**
	 * Verifies if the product is already on the favourite list
	 * 
	 * @param sku
	 * @return
	 * @throws InterruptedException 
	 */
	public synchronized static boolean verifyIfFavourite(String sku) throws InterruptedException {
		boolean result = false;
		DarwinDatabaseSemaphore.getInstance().getLock();
		
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		String query = new StringBuilder("select count(*) from ").append(TABLE_NAME)
				.append(" where ").append(_FAVOURITE_SKU).append(" = '").append(sku).append("'").toString();
		Log.i(TAG, "SQL RESULT query :  " + query);
		Cursor cursor = db.rawQuery(query, null);
		if (db.isOpen() && cursor != null && cursor.getCount() > 0) {
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

		if (db.isOpen())
		    db.close();

		DarwinDatabaseSemaphore.getInstance().releaseLock();
		
		return result;
	}

	public static ArrayList<String> getIncompleteFavouriteList() {
		ArrayList<String> incomplete = new ArrayList<String>();
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		String query = "SELECT " + _FAVOURITE_URL + " FROM " + TABLE_NAME + " WHERE " + _FAVOURITE_IS_COMPLETE + " == '0'";
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				incomplete.add(cursor.getString(0));
			}
		}
		// Validate cursor
		if (cursor != null) cursor.close();
		// Close access
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
		String query = new StringBuilder("select * from ").append(TABLE_NAME).append(" order by ").append(_ID).append(" desc").toString();
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
				favourite.setPriceAsDouble(cursor.getDouble(index++));
				favourite.setPriceConverted(cursor.getDouble(index++));
				favourite.setSpecialPrice(cursor.getString(index++));
				favourite.setSpecialPriceDouble(cursor.getDouble(index++));
				favourite.setSpecialPriceConverted(cursor.getDouble(index++));
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
						favourite.setSelectedSimple((simples.size() == 1) ? 0 : -1); // NO_SIMPLE_SELECTED
						
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
//				favourite.setSelectedSimple(cursor.getInt(index++));
				
				favourite.setSizeGuideUrl(cursor.getString(index++));
				

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

	public static ArrayList<String> getFavouriteSKUList() {
        ArrayList<String> favourites = new ArrayList<String>();
        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        String query = new StringBuilder("select ").append(_FAVOURITE_SKU).append(" from ").append(TABLE_NAME).append(" order by ").append(_ID).append(" desc").toString();
        Log.i(TAG, "SQL RESULT query :  " + query);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                favourites.add(cursor.getString(0));
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
	public synchronized static void removeFavouriteProduct(String sku) {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		String query = new StringBuilder("delete from ").append(TABLE_NAME)
				.append(" where ").append(_FAVOURITE_SKU).append(" = '").append(sku).append("'").toString();
		Log.i(TAG, "SQL RESULT query :  " + query);
		db.execSQL(query);
		db.close();
	}

	/**
	 * Get number of entries
	 * 
	 * @return number of favourites
	 */
	public static int getTotalFavourites() {
		int result = 0;
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		String query = "select count(*) from " + TABLE_NAME;
		Log.i(TAG, "SQL RESULT query :  " + query);
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();
			result = cursor.getInt(0);
			// Log result
			Log.i(TAG, "SQL RESULT: " + result);
		}
		// Validate cursor
		if (cursor != null) {
			cursor.close();
		}

		db.close();
		return result;
	}

	/**
	 * Delete all Favourites
	 */
	public static void deleteAllFavourite() {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}

}
