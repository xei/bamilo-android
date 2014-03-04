/**
 * 
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author nutzer2
 *
 */
public class ProductsPage implements IJSONSerializable, Parcelable {
	
	private static final String TAG = ProductsPage.class.getSimpleName();
		
	private int totalProducts;
	private ArrayList<Product> products;
	private ArrayList<Category> categories;

	private String mPageName;
	
	public ProductsPage() {
	
	}

	/* (non-Javadoc)
	 * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
	 */
	@Override
	public boolean initialize(JSONObject metadataObject) throws JSONException {
		products = new ArrayList<Product>();
		categories = new ArrayList<Category>();
		totalProducts = metadataObject.optInt(RestConstants.JSON_PRODUCT_COUNT_TAG, 0);
		mPageName = metadataObject.optString(RestConstants.JSON_CATALOG_NAME_TAG, "");

		JSONArray productObjectArray = metadataObject.getJSONArray(RestConstants.JSON_RESULTS_TAG);

		for (int i = 0; i < productObjectArray.length(); ++i) {
			JSONObject productObject = productObjectArray.getJSONObject(i);
			Product product = new Product();
			product.initialize(productObject);
			products.add(product);
		}

		if (!metadataObject.isNull(RestConstants.JSON_CATEGORIES_TAG)) {
			JSONArray categoriesArray = metadataObject.getJSONArray(RestConstants.JSON_CATEGORIES_TAG);
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
	
	public String getName(){
		return mPageName;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeValue(totalProducts);
		dest.writeList(products);
		dest.writeList(categories);
	}
	
	private ProductsPage(Parcel in){
		totalProducts = in.readInt();
		
		products = new ArrayList<Product>();
		in.readList(products, Product.class.getClassLoader());
		
		categories = new ArrayList<Category>();
		in.readList(categories, Category.class.getClassLoader());
	}
	
    public static final Parcelable.Creator<ProductsPage> CREATOR = new Parcelable.Creator<ProductsPage>() {
        public ProductsPage createFromParcel(Parcel in) {
            return new ProductsPage(in);
        }

        public ProductsPage[] newArray(int size) {
            return new ProductsPage[size];
        }
    };

}
