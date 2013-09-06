/**
 * @author GuilhermeSilva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.LogTagHelper;
import de.akquinet.android.androlog.Log;

/**
 * Class that represents the server side product. Contains id, name,
 * description, price(deprecated), stock(deprecated), list of images, brand and
 * list of category id.
 * 
 * @author GuilhermeSilva
 * 
 */
public class Product implements IJSONSerializable {
	private final static String TAG = LogTagHelper.create( Product.class );

//    private static final String JSON_ATTRIBUTES_TAG = "data";
//    private static final String JSON_ATTRIBUTES_TWO_TAG = "attributes";
//    private static final String JSON_IMAGES_TAG = "images";

    private String id;
    private ProductAttributes attributes;
    
    private ArrayList<Image> images;

    /**
     * simple product constructor.
     */
    public Product() {
        id = "";
        attributes = new ProductAttributes();
        images = new ArrayList<Image>();
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the attributes
     */
    public ProductAttributes getAttributes() {
        return attributes;
    }

    /**
     * @return the images of the simple product.
     */
    public ArrayList<Image> getImages() {
        return images;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            id = jsonObject.getString(RestConstants.JSON_ID_TAG);
            
            JSONObject attributesObject = jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG);
            if(attributesObject != null){
                attributes.initialize(attributesObject);
            }
            
            JSONObject attributes2Object = jsonObject.optJSONObject(RestConstants.JSON_PROD_ATTRIBUTES_TAG);
            if(attributes2Object != null) {
                attributes.initialize(attributes2Object);
            }
            
            images.clear();
            JSONArray imageArray = jsonObject.optJSONArray(RestConstants.JSON_IMAGES_TAG);
            if (imageArray != null) {
                
                int imageArrayLenght = imageArray.length();
                for (int i = 0; i < imageArrayLenght; ++i) {
                    JSONObject imageObject = imageArray.getJSONObject(i);
                    Image image = new Image();
                    image.initialize(imageObject);
                    images.add(image);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RestConstants.JSON_ID_TAG, id);
            jsonObject.put(RestConstants.JSON_DATA_TAG, attributes.toJSON());
            jsonObject.put(RestConstants.JSON_PROD_ATTRIBUTES_TAG, attributes.toJSON());
            
            JSONArray imageArray = new JSONArray();
            for(Image image : images) {
                imageArray.put(image.toJSON());
            }
            jsonObject.put(RestConstants.JSON_IMAGES_TAG, imageArray);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

    /**
     * Class that holds the attributes of the product
     * 
     * @author GuilhermeSilva
     * 
     */
    public class ProductAttributes implements IJSONSerializable {
//        private static final String JSON_SKU_TAG = "sku";
//        private static final String JSON_NAME_TAG = "name";
//        private static final String JSON_URL_TAG = "url";
//        private static final String JSON_DESCRIPTION_TAG = "description";
//        private static final String JSON_BRAND_TAG = "brand";
//        private static final String JSON_MAX_PRICE_TAG = "max_price";
//        private static final String JSON_PRICE_TAG = "price";
//
//        private static final String JSON_SPECIAL_PRICE_TAG = "special_price";
//        private static final String JSON_MAX_SPECIAL_PRICE_TAG = "max_special_price";
//        private static final String JSON_MAX_SAVING_PERCENTAGE_TAG = "max_saving_percentage";
//        
//        private static final String JSON_RATINGS = "ratings_total";
//        private static final String JSON_RATING_AVR = "avr";
//        private static final String JSON_RATING_SUM = "sum";

        private String sku;
        private String name;
        private String url;
        private String description;
        private String brand;
        private String maxPrice;
        private String price;

        private String specialPrice;
        private String maxSpecialPrice;
        private Double maxSavingPercentage;
        
        private Integer reviews;
        private Double rating;

        /**
         * ProductAttributes empty constructor
         */
        public ProductAttributes() {
            sku = "";
            name = "";
            url = "";
            description = "";
            brand = "";

            maxPrice = "";
            price = "";

            specialPrice = "";
            maxSpecialPrice = "";
            maxSavingPercentage = 0.0;
            
            reviews = 0;
            rating = .0;
        }

        /**
		 * @return the reviews
		 */
		public Integer getReviews() {
			return reviews;
		}

		/**
		 * @return the rating
		 */
		public Double getRating() {
			return rating;
		}

		/**
         * @return the sku
         */
        public String getSku() {
            return sku;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return the brand
         */
        public String getBrand() {
            return brand;
        }

        /**
         * @return the maxPrice
         */
        public String getMaxPrice() {
            return maxPrice;
        }

        /**
         * @return the price
         */
        public String getPrice() {
            return price;
        }
        
        /**
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * @return the specialPrice
         */
        public String getSpecialPrice() {
            return specialPrice;
        }

        /**
         * @return the maxSpecialPrice
         */
        public String getMaxSpecialPrice() {
            return maxSpecialPrice;
        }

        /**
         * @return the discountPercentage
         */
        public Double getMaxSavingPercentage() {
            return maxSavingPercentage;
        }

        /* (non-Javadoc)
         * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
        @Override
        public boolean initialize(JSONObject jsonObject) {
                        
            try {
                sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
                name = jsonObject.optString(RestConstants.JSON_PROD_NAME_TAG);
                url = jsonObject.optString(RestConstants.JSON_PROD_URL_TAG);
                description = jsonObject.optString(RestConstants.JSON_DESCRIPTION_TAG, "");
                brand = jsonObject.optString(RestConstants.JSON_BRAND_TAG);
                
                String priceString = jsonObject.optString(RestConstants.JSON_PRICE_TAG);
                double priceDouble = Double.parseDouble(priceString);
                price = CurrencyFormatter.formatCurrency(priceDouble);
                
                String maxPriceString = jsonObject.optString(RestConstants.JSON_MAX_PRICE_TAG, price);
                if(!maxPriceString.equals("")) {
                    maxPrice = price;
                } else {
                    double maxPriceDouble = Double.parseDouble(jsonObject.optString(RestConstants.JSON_MAX_PRICE_TAG, price));
                    maxPrice = CurrencyFormatter.formatCurrency(maxPriceDouble);
                }
                
                double specialPriceDouble;
                if(!jsonObject.isNull(RestConstants.JSON_SPECIAL_PRICE_TAG)) {
                    specialPriceDouble = Double.parseDouble(jsonObject.getString(RestConstants.JSON_SPECIAL_PRICE_TAG));
                } else {
                    specialPriceDouble = priceDouble;
                }
                specialPrice = CurrencyFormatter.formatCurrency(specialPriceDouble);
                
                if(!jsonObject.isNull(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG)) {
                    maxSpecialPrice = CurrencyFormatter.formatCurrency(Double.parseDouble(jsonObject.getString(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG)));
                } else {
                    maxSpecialPrice = specialPrice;
                }
                
                maxSavingPercentage = jsonObject.optDouble(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG, 0);
                
                if (maxSavingPercentage == 0 && !price.equals(specialPrice)) {
                    maxSavingPercentage = (double) Math.round(specialPriceDouble / priceDouble) ;
                }
                
                JSONObject ratings = jsonObject.optJSONObject(RestConstants.JSON_RATINGS_TOTAL_TAG);
                if(ratings != null) {
                	reviews = ratings.optInt(RestConstants.JSON_RATINGS_TOTAL_SUM_TAG);
                	rating = ratings.optDouble(RestConstants.JSON_RATINGS_TOTAL_AVG_TAG);
                }
                
            } catch (JSONException e) {
            	Log.e(TAG, "Error Parsing the product json", e);
                return false;
            }
            
            return true;
        }

        /* (non-Javadoc)
         * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
         */
        @Override
        public JSONObject toJSON() {
            JSONObject jsonObject = new JSONObject();

            try {
                jsonObject.put(RestConstants.JSON_SKU_TAG, sku);
                jsonObject.put(RestConstants.JSON_PROD_NAME_TAG, name);
                jsonObject.put(RestConstants.JSON_PROD_URL_TAG, url);
                jsonObject.put(RestConstants.JSON_DESCRIPTION_TAG, description);
                jsonObject.put(RestConstants.JSON_MAX_PRICE_TAG, maxPrice);
                jsonObject.put(RestConstants.JSON_PRICE_TAG, price);

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return jsonObject;
        }
    }
    
    /**
     * @return the product sku
     */
    public String getSKU() {
        return attributes.getSku();
    }

    /**
     * @return the product name
     */
    public String getName() {
        return attributes.getName();
    }
    
    /**
     * @return the product brand
     */
    public String getBrand() {
        return attributes.getBrand();
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return attributes.getDescription();
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return attributes.getPrice();
    }

    /**
     * @return the suggested price
     */
    public String getSuggestedPrice() {
        return attributes.getMaxPrice();
    }

    /**
     * @return the max saving percentage
     */
    public Double getMaxSavingPercentage() {
        return attributes.getMaxSavingPercentage();
    }

    /**
     * @return the rating.
     */
    public Double getRating() {
        return attributes.getRating();
    }
    
    public Integer getReviews() {
    	return attributes.getReviews();
    }

    public String getUrl() {
        return attributes.getUrl();
    }

    /**
     * @return the specialPrice
     */
    public String getSpecialPrice() {
        return attributes.getSpecialPrice();
    }

    /**
     * @return the maxSpecialPrice
     */
    public String getMaxSpecialPrice() {
        return attributes.getMaxSpecialPrice();
    }
}
