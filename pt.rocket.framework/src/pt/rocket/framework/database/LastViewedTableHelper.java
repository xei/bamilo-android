package pt.rocket.framework.database;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.database.DarwinDatabaseHelper.TableType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.objects.LastViewedAddableToCart;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.objects.Variation;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

/**
 * This class is a helper to manage the Last Viewed products on database.
 * 
 * @author Manuel Silva
 * @modified Andre Lopes
 *
 */
public class LastViewedTableHelper extends BaseTable {
	
	private static final String TAG = LastViewedTableHelper.class.getSimpleName();
	
	// Table Name
	public static final String TABLE_NAME = "last_viewed";
	
	private static final int MAX_SAVED_PRODUCTS = 15;
	
	// Table Rows
	public static final String _ID = "id";
	public static final String _PRODUCT_SKU = "product_sku";
	public static final String _PRODUCT_NAME = "product_name";
	public static final String _PRODUCT_PRICE = "product_price";
	public static final String _PRODUCT_PRICE_ORIG = "product_price_orig";
	public static final String _PRODUCT_PRICE_CONVERTED = "product_price_converted";
	public static final String _PRODUCT_URL = "product_url";
	public static final String _PRODUCT_IMAGE_URL = "image_url";
	public static final String _PRODUCT_BRAND = "product_brand";
	public static final String _PRODUCT_SPECIAL_PRICE = "product_special_price";
	public static final String _PRODUCT_SPECIAL_PRICE_ORIG = "product_special_price_orig";
	public static final String _PRODUCT_SPECIAL_PRICE_CONVERTED = "product_special_price_converted";
	public static final String _PRODUCT_DISCOUNT_PERCENTAGE = "product_discount_percentage";
	public static final String _PRODUCT_IS_NEW = "product_is_new";
	public static final String _PRODUCT_SIMPLES_JSON = "product_simples_json";
	public static final String _PRODUCT_VARIATIONS_JSON = "product_variations_json";
	public static final String _PRODUCT_KNOWN_VARIATIONS_LIST = "product_known_variations_list";
	public static final String _PRODUCT_IS_COMPLETE = "product_is_complete";
	public static final String _PRODUCT_SIZE_GUIDE = "product_size_guide";

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
        return new StringBuilder("CREATE TABLE ").append(table)
                .append(" (")
                .append(_ID).append(" INTEGER PRIMARY KEY, ")
                .append(_PRODUCT_SKU).append(" TEXT, ")
                .append(_PRODUCT_NAME).append(" TEXT, ")
                .append(_PRODUCT_PRICE).append(" TEXT ,")
                .append(_PRODUCT_PRICE_ORIG).append(" TEXT ,")
                .append(_PRODUCT_PRICE_CONVERTED).append(" TEXT ,")
                .append(_PRODUCT_URL).append(" TEXT, ")
                .append(_PRODUCT_IMAGE_URL).append(" TEXT, ")
                .append(_PRODUCT_BRAND).append(" TEXT, ")
                .append(_PRODUCT_SPECIAL_PRICE).append(" TEXT, ")
                .append(_PRODUCT_SPECIAL_PRICE_ORIG).append(" TEXT ,")
                .append(_PRODUCT_SPECIAL_PRICE_CONVERTED).append(" TEXT ,")
                .append(_PRODUCT_DISCOUNT_PERCENTAGE).append(" DOUBLE, ")
                .append(_PRODUCT_IS_NEW).append(" TEXT, ")
                .append(_PRODUCT_SIMPLES_JSON).append(" TEXT, ")
                .append(_PRODUCT_VARIATIONS_JSON).append(" TEXT, ")
                .append(_PRODUCT_KNOWN_VARIATIONS_LIST).append(" TEXT, ")
                .append(_PRODUCT_IS_COMPLETE).append(" TEXT, ")
                .append(_PRODUCT_SIZE_GUIDE).append(" TEXT ")
                .append(")").toString();
    }
    
    /*
     * ################## CRUD ##################  
     */

	/**
	 * Insert viewed product into database
	 * 
	 * @param completeProduct
	 */
	public static void insertLastViewedProduct(CompleteProduct completeProduct) {
		if (completeProduct != null) {
			String sku = completeProduct.getSku();
			if (!verifyIfExist(sku)) {
				if (getLastViewedEntriesCount() == MAX_SAVED_PRODUCTS) {
					removeOldestEntry();
				}
				SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put(LastViewedTableHelper._PRODUCT_SKU, sku);
				values.put(LastViewedTableHelper._PRODUCT_BRAND, completeProduct.getBrand());
				values.put(LastViewedTableHelper._PRODUCT_NAME, completeProduct.getName());
				values.put(LastViewedTableHelper._PRODUCT_PRICE, completeProduct.getPrice());
				values.put(LastViewedTableHelper._PRODUCT_PRICE_ORIG, completeProduct.getPriceAsDouble());
				values.put(LastViewedTableHelper._PRODUCT_PRICE_CONVERTED, completeProduct.getPriceConverted());
				values.put(LastViewedTableHelper._PRODUCT_SPECIAL_PRICE, completeProduct.getSpecialPrice());
				values.put(LastViewedTableHelper._PRODUCT_SPECIAL_PRICE_ORIG, completeProduct.getSpecialPriceAsDouble());
				values.put(LastViewedTableHelper._PRODUCT_SPECIAL_PRICE_CONVERTED, completeProduct.getSpecialPriceConverted());
				values.put(LastViewedTableHelper._PRODUCT_DISCOUNT_PERCENTAGE, completeProduct.getMaxSavingPercentage());
				values.put(LastViewedTableHelper._PRODUCT_URL, completeProduct.getUrl());
				values.put(LastViewedTableHelper._PRODUCT_IMAGE_URL, completeProduct.getImageList().size() == 0 ? "" : completeProduct.getImageList().get(0));
				values.put(LastViewedTableHelper._PRODUCT_IS_NEW, completeProduct.isNew());
				values.put(LastViewedTableHelper._PRODUCT_SIZE_GUIDE, completeProduct.getSizeGuideUrl());

				String simplesJSON = "";
				ArrayList<ProductSimple> simples = completeProduct.getSimples();
				if (simples != null && !simples.isEmpty()) {
					JSONArray simplesJSONArray = new JSONArray();
					for (ProductSimple productSimple : simples) {
						simplesJSONArray.put(productSimple.toJSON());
					}
					simplesJSON = simplesJSONArray.toString();
				}
				values.put(LastViewedTableHelper._PRODUCT_SIMPLES_JSON, simplesJSON);

				String variationsJSON = "";
				ArrayList<Variation> variations = completeProduct.getVariations();
				if (variations != null && !variations.isEmpty()) {
					JSONArray variationsJSONArray = new JSONArray();
					for (Variation variation : variations) {
						variationsJSONArray.put(variation.toJSON());
					}
					variationsJSON = variationsJSONArray.toString();
				}
				values.put(LastViewedTableHelper._PRODUCT_VARIATIONS_JSON, variationsJSON);

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
				values.put(LastViewedTableHelper._PRODUCT_KNOWN_VARIATIONS_LIST, knownVariationsString);

				values.put(LastViewedTableHelper._PRODUCT_IS_COMPLETE, true);

				db.insert(LastViewedTableHelper.TABLE_NAME, null, values);
				db.close();
			}
		}
	}

	/**
	 * Verifies if the product is already on the last viewed list
	 * 
	 * @param sku
	 * @return
	 */
	public static boolean verifyIfExist(String sku) {
		boolean result = false;
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		String query = new StringBuilder("select count(*) from ").append(TABLE_NAME)
				.append(" where ").append(_PRODUCT_SKU).append(" = '").append(sku).append("'").toString();
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

	/**
	 * Get number of entries
	 * 
	 * @return
	 */
	public static int getLastViewedEntriesCount() {
		int result = 0;
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		String query = "select count(*) from " + TABLE_NAME;
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

//	/**
//	 * Get the last viewed list of entries
//	 * 
//	 * @return
//	 */
//	public static ArrayList<LastViewed> getLastViewedList() {
//		ArrayList<LastViewed> listLastViewed = new ArrayList<LastViewed>();
//		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
//		String query = new StringBuilder("select ")
//				.append(_PRODUCT_SKU).append(", ")
//				.append(_PRODUCT_NAME).append(", ")
//				.append(_PRODUCT_PRICE).append(", ")
//				.append(_PRODUCT_URL).append(", ")
//				.append(_PRODUCT_IMAGE_URL)
//				.append(" from ").append(TABLE)
//				.append(" order by ").append(_ID).append(" desc").toString();
//		Log.i(TAG, "SQL RESULT query :  " + query);
//		Cursor cursor = db.rawQuery(query, null);
//		if (cursor != null && cursor.getCount() > 0) {
//			while (cursor.moveToNext()) {
//				int index = 0; // columnIndex is zero-based index
//				LastViewed lastViewed = new LastViewed();
//				lastViewed.setProductSku(cursor.getString(index++));
//				lastViewed.setProductName(cursor.getString(index++));
//				lastViewed.setProductPrice(cursor.getString(index++));
//				lastViewed.setProductUrl(cursor.getString(index++));
//				lastViewed.setImageUrl(cursor.getString(index++));
//				listLastViewed.add(lastViewed);
//			}
//		}
//
//		// Validate cursor
//		if (cursor != null) {
//			cursor.close();
//		}
//
//		db.close();
//		return listLastViewed;
//	}

	/**
	 * Get the last viewed list of entries for list of addable Last Viewed
	 * 
	 * @return
	 */
	public static ArrayList<LastViewedAddableToCart> getLastViewedAddableToCartList() {
		ArrayList<LastViewedAddableToCart> listLastViewed = new ArrayList<LastViewedAddableToCart>();
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
		String query = new StringBuilder("select * from ").append(TABLE_NAME).append(" order by ").append(_ID).append(" desc").toString();
		Log.i(TAG, "SQL RESULT query :  " + query);
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				int index = 1;
				LastViewedAddableToCart lastViewed = new LastViewedAddableToCart();
				lastViewed.setSku(cursor.getString(index++));
				lastViewed.setName(cursor.getString(index++));
				lastViewed.setPrice(cursor.getString(index++));
				lastViewed.setPriceAsDouble(cursor.getDouble(index++));
				lastViewed.setPriceConverted(cursor.getDouble(index++));
				lastViewed.setUrl(cursor.getString(index++));
				lastViewed.getImageList().add(cursor.getString(index++));
				lastViewed.setBrand(cursor.getString(index++));
				lastViewed.setSpecialPrice(cursor.getString(index++));
				lastViewed.setSpecialPriceDouble(cursor.getDouble(index++));
				lastViewed.setSpecialPriceConverted(cursor.getDouble(index++));
				lastViewed.setMaxSavingPercentage(cursor.getDouble(index++));
				lastViewed.setNew(cursor.getInt(index++) == 1);
				
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
						lastViewed.setSimples(simples);
						// NO_SIMPLE_SELECTED @ LastViewedListAdapter
						lastViewed.setSelectedSimple((simples.size() == 1) ? 0 : -1); // NO_SIMPLE_SELECTED

					} catch (JSONException e) {
						Log.e(TAG, "JSONException on sku: " + lastViewed.getSku() + "\nsimplesJSON = " + simplesJSON);
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
						lastViewed.setVariations(variations);
					} catch (JSONException e) {
						Log.e(TAG, "JSONException on sku: " + lastViewed.getSku() + "\nvariationsJSON = " + variationsJSON);
					}
				}

				// convert knownVariations from Joined String to ArrayList
				String knownVariationsString = cursor.getString(index++);
				if (!TextUtils.isEmpty(knownVariationsString)) {
					String[] knownVariationsSplit = knownVariationsString.split(DELIMITER);
					if (knownVariationsSplit != null && knownVariationsSplit.length > 0) {
						lastViewed.setKnownVariations(new ArrayList<String>(Arrays.asList(knownVariationsSplit)));
					}
				}

				// Complete product
				lastViewed.setComplete(true);
				index++;
				// Size guide
				lastViewed.setSizeGuideUrl(cursor.getString(index++));
				// Add item
				listLastViewed.add(lastViewed);
			}
		}

		// Validate cursor
		if (cursor != null) {
			cursor.close();
		}

		db.close();
		return listLastViewed;
	}

	/**
	 * Remove lastViewed from database
	 * 
	 * @param sku
	 */
	public static void removeLastViewed(String sku) {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		String query = new StringBuilder("delete from ").append(TABLE_NAME)
				.append(" where ").append(_PRODUCT_SKU).append(" = '").append(sku).append("'").toString();
		Log.i(TAG, "SQL RESULT query :  " + query);
		db.execSQL(query);
		db.close();
	}

	/**
	 * Remove oldest entry from database
	 */
	public static void removeOldestEntry() {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		String query = new StringBuilder("delete from ").append(TABLE_NAME).append(" where id in ")
				.append("(select id FROM ").append(TABLE_NAME).append(" order by id asc limit 1)").toString();
		db.execSQL(query);
		db.close();
	}

	/**
	 * Delete all LastViewes
	 */
	public static void deleteAllLastViewed() {
		SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getWritableDatabase();
		db.delete(TABLE_NAME, null, null);
		db.close();
	}


}
