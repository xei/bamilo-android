/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author nutzer2
 *
 */
public class ProductsPage implements IJSONSerializable{
	
	private static final String TAG = ProductsPage.class.getSimpleName();
	
	private static final String JSON_RESULTS_TAG = "results";
	private static final String JSON_PRODUCT_COUNT_TAG = "product_count";
	private static final String JSON_CATEGORIES_TAG = "categories";
	
	private int totalProducts;
	private ArrayList<Product> products;
	private ArrayList<Category> categories;
	
	

	/* (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject metadataObject) throws JSONException {
		products = new ArrayList<Product>();
		categories = new ArrayList<Category>();
		totalProducts = metadataObject.optInt(JSON_PRODUCT_COUNT_TAG, 0);

		JSONArray productObjectArray = metadataObject.getJSONArray(JSON_RESULTS_TAG);

		for (int i = 0; i < productObjectArray.length(); ++i) {
			JSONObject productObject = productObjectArray.getJSONObject(i);
			Product product = new Product();
			product.initialize(productObject);
			products.add(product);
		}

		if (!metadataObject.isNull(JSON_CATEGORIES_TAG)) {
			JSONArray categoriesArray = metadataObject.getJSONArray(JSON_CATEGORIES_TAG);
			Log.d(TAG, " # of categories " + categoriesArray.length());
			for (int i = 0; i < categoriesArray.length(); ++i) {
				JSONObject categoryObject = categoriesArray.getJSONObject(i);
				Category category = new Category();
				category.initialize(categoryObject);
				categories.add(category);
			}
		} else {
			Log.d(TAG, " there are no categories");
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
	 */
	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ArrayList<Product> getProducts() {
		return products;
	}

	/**
	 * @return the categories
	 */
	public ArrayList<Category> getCategories() {
		return categories;
	}

	/**
	 * @return the totalProducts
	 */
	public int getTotalProducts() {
		return totalProducts;
	}

}
