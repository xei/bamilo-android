//package com.mobile.framework.objects;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import com.mobile.newFramework.pojo.JsonConstants;
//
///**
// * Defines the product image.
// * @author GuilhermeSilva
// *
// */
//public class ProductImage implements IJSONSerializable, Parcelable {
//
//    private String idCatalogProductImage;
//    private Integer fkCatalogConfig;
//    private Integer fkCatalogSimple;
//    private String image;
//    private String main;
//    private String updatedAt;
//    private String updatedAtTs;
//    private String sku;
//    private String fkCatalogBrand;
//    private String skuSimple;
//    private String idCatalogBrand;
//    private String brandName;
//    private String brandUrlKey;
//    private String url;
//    private String path;
//    private String sprite;
//
//    /**
//     * @return the idCatalogProductImage
//     */
//    public String getIdCatalogProductImage() {
//        return idCatalogProductImage;
//    }
//
//    /**
//     * @return the fkCatalogConfig
//     */
//    public Integer getFkCatalogConfig() {
//        return fkCatalogConfig;
//    }
//
//    /**
//     * @return the fkCatalogSimple
//     */
//    public Integer getFkCatalogSimple() {
//        return fkCatalogSimple;
//    }
//
//    /**
//     * @return the image
//     */
//    public String getImage() {
//        return image;
//    }
//
//    /**
//     * @return the main
//     */
//    public String getMain() {
//        return main;
//    }
//
//    /**
//     * @return the updatedAt
//     */
//    public String getUpdatedAt() {
//        return updatedAt;
//    }
//
//    /**
//     * @return the updatedAtTs
//     */
//    public String getUpdatedAtTs() {
//        return updatedAtTs;
//    }
//
//    /**
//     * @return the sku
//     */
//    public String getSku() {
//        return sku;
//    }
//
//    /**
//     * @return the fkCatalogBrand
//     */
//    public String getFkCatalogBrand() {
//        return fkCatalogBrand;
//    }
//
//    /**
//     * @return the skuSimple
//     */
//    public String getSkuSimple() {
//        return skuSimple;
//    }
//
//    /**
//     * @return the idCatalogBrand
//     */
//    public String getIdCatalogBrand() {
//        return idCatalogBrand;
//    }
//
//    /**
//     * @return the brandName
//     */
//    public String getBrandName() {
//        return brandName;
//    }
//
//    /**
//     * @return the brandUrlKey
//     */
//    public String getBrandUrlKey() {
//        return brandUrlKey;
//    }
//
//    /**
//     * @return the url
//     */
//    public String getUrl() {
//        return url;
//    }
//
//    /**
//     * @return the path
//     */
//    public String getPath() {
//        return path;
//    }
//
//    /**
//     * @return the sprite
//     */
//    public String getSprite() {
//        return sprite;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see
//     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
//     * )
//     */
//    @Override
//    public boolean initialize(JSONObject jsonObject) {
//
//        idCatalogProductImage = jsonObject.optString(JsonConstants.JSON_ID_CATALOG_PRODUCT_IMAGE_TAG);
//        fkCatalogConfig = jsonObject.optInt(JsonConstants.JSON_FK_CATALOG_CONFIG_TAG);
//        fkCatalogSimple = jsonObject.optInt(JsonConstants.JSON_FK_CATALOG_SIMPLE_TAG);
//        image = jsonObject.optString(JsonConstants.JSON_IMAGE_TAG);
//        main = jsonObject.optString(JsonConstants.JSON_MAIN_TAG);
//        updatedAt = jsonObject.optString(JsonConstants.JSON_UPDATED_AT_TAG);
//        updatedAtTs = jsonObject.optString(JsonConstants.JSON_UPDATED_AT_TS_TAG);
//        sku = jsonObject.optString(JsonConstants.JSON_SKU_TAG);
//        fkCatalogBrand = jsonObject.optString(JsonConstants.JSON_FK_CATALOG_BRAND_TAG);
//        skuSimple = jsonObject.optString(JsonConstants.JSON_SKU_SIMPLE_TAG);
//        idCatalogBrand = jsonObject.optString(JsonConstants.JSON_ID_CATALOG_BRAND_TAG);
//        brandName = jsonObject.optString(JsonConstants.JSON_BRAND_NAME_TAG);
//        brandUrlKey = jsonObject.optString(JsonConstants.JSON_BRAND_URL_KEY_TAG);
//        url = jsonObject.optString(JsonConstants.JSON_URL_TAG);
//        path = jsonObject.optString(JsonConstants.JSON_PATH_TAG);
//        sprite = jsonObject.optString(JsonConstants.JSON_SPRITE_TAG);
//
//        return false;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
//     */
//    @Override
//    public JSONObject toJSON() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//
//            jsonObject.put(JsonConstants.JSON_ID_CATALOG_PRODUCT_IMAGE_TAG, idCatalogProductImage);
//            jsonObject.put(JsonConstants.JSON_FK_CATALOG_CONFIG_TAG, fkCatalogConfig);
//            jsonObject.put(JsonConstants.JSON_FK_CATALOG_SIMPLE_TAG, fkCatalogSimple);
//            jsonObject.put(JsonConstants.JSON_IMAGE_TAG, image);
//            jsonObject.put(JsonConstants.JSON_UPDATED_AT_TAG, updatedAt);
//            jsonObject.put(JsonConstants.JSON_UPDATED_AT_TS_TAG, updatedAtTs);
//            jsonObject.put(JsonConstants.JSON_SKU_TAG, sku);
//            jsonObject.put(JsonConstants.JSON_FK_CATALOG_BRAND_TAG, fkCatalogBrand);
//            jsonObject.put(JsonConstants.JSON_SKU_SIMPLE_TAG, skuSimple);
//            jsonObject.put(JsonConstants.JSON_ID_CATALOG_BRAND_TAG, idCatalogBrand);
//            jsonObject.put(JsonConstants.JSON_BRAND_NAME_TAG, brandName);
//            jsonObject.put(JsonConstants.JSON_BRAND_URL_KEY_TAG, brandUrlKey);
//            jsonObject.put(JsonConstants.JSON_URL_TAG, url);
//            jsonObject.put(JsonConstants.JSON_PATH_TAG, path);
//            jsonObject.put(JsonConstants.JSON_SPRITE_TAG, sprite);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return jsonObject;
//    }
//
//    /**
//     * ########### Parcelable ###########
//     * @author sergiopereira
//     */
//
//    /*
//     * (non-Javadoc)
//     * @see android.os.Parcelable#describeContents()
//     */
//	@Override
//	public int describeContents() {
//		return 0;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
//	 */
//	@Override
//	public void writeToParcel(Parcel dest, int flags) {
//	    dest.writeString(idCatalogProductImage);
//	    dest.writeInt(fkCatalogConfig);
//	    dest.writeInt(fkCatalogSimple);
//	    dest.writeString(image);
//	    dest.writeString(main);
//	    dest.writeString(updatedAt);
//	    dest.writeString(updatedAtTs);
//	    dest.writeString(sku);
//	    dest.writeString(fkCatalogBrand);
//	    dest.writeString(skuSimple);
//	    dest.writeString(idCatalogBrand);
//	    dest.writeString(brandName);
//	    dest.writeString(brandUrlKey);
//	    dest.writeString(url);
//	    dest.writeString(path);
//	    dest.writeString(sprite);
//	}
//
//	/**
//	 * Parcel constructor
//	 * @param in
//	 */
//	private ProductImage(Parcel in) {
//	    idCatalogProductImage = in.readString();
//	    fkCatalogConfig = in.readInt();
//	    fkCatalogSimple = in.readInt();
//	    image = in.readString();
//	    main = in.readString();
//	    updatedAt = in.readString();
//	    updatedAtTs = in.readString();
//	    sku = in.readString();
//	    fkCatalogBrand = in.readString();
//	    skuSimple = in.readString();
//	    idCatalogBrand = in.readString();
//	    brandName = in.readString();
//	    brandUrlKey = in.readString();
//	    url = in.readString();
//	    path = in.readString();
//	    sprite = in.readString();
//    }
//
//	/**
//	 * Create parcelable
//	 */
//	public static final Parcelable.Creator<ProductImage> CREATOR = new Parcelable.Creator<ProductImage>() {
//        public ProductImage createFromParcel(Parcel in) {
//            return new ProductImage(in);
//        }
//
//        public ProductImage[] newArray(int size) {
//            return new ProductImage[size];
//        }
//    };
//
//
//}
