package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines the product image.
 * @author GuilhermeSilva
 *
 */
public class ProductImage implements IJSONSerializable {
    private static final String JSON_ID_CATALOG_PRODUCT_IMAGE_TAG = "id_catalog_product_image";
    private static final String JSON_FK_CATALOG_CONFIG_TAG = "fk_catalog_config";
    private static final String JSON_FK_CATALOG_SIMPLE_TAG = "fk_catalog_simple";
    private static final String JSON_IMAGE_TAG = "image";
    private static final String JSON_MAIN_TAG = "main";
    private static final String JSON_UPDATED_AT_TAG = "updated_at";
    private static final String JSON_UPDATED_AT_TS_TAG = "updated_at_ts";
    private static final String JSON_SKU_TAG = "sku";
    private static final String JSON_FK_CATALOG_BRAND_TAG = "fk_catalog_brand";
    private static final String JSON_SKU_SIMPLE_TAG = "sku_simple";
    private static final String JSON_ID_CATALOG_BRAND_TAG = "id_catalog_brand";
    private static final String JSON_BRAND_NAME_TAG = "brand_name";
    private static final String JSON_BRAND_URL_KEY_TAG = "brand_url_key";
    private static final String JSON_URL_TAG = "url";
    private static final String JSON_PATH_TAG = "path";
    private static final String JSON_SPRITE_TAG = "sprite";

    private String idCatalogProductImage;
    private Integer fkCatalogConfig;
    private Integer fkCatalogSimple;
    private String image;
    private String main;
    private String updatedAt;
    private String updatedAtTs;
    private String sku;
    private String fkCatalogBrand;
    private String skuSimple;
    private String idCatalogBrand;
    private String brandName;
    private String brandUrlKey;
    private String url;
    private String path;
    private String sprite;

    /**
     * @return the idCatalogProductImage
     */
    public String getIdCatalogProductImage() {
        return idCatalogProductImage;
    }

    /**
     * @return the fkCatalogConfig
     */
    public Integer getFkCatalogConfig() {
        return fkCatalogConfig;
    }

    /**
     * @return the fkCatalogSimple
     */
    public Integer getFkCatalogSimple() {
        return fkCatalogSimple;
    }

    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }

    /**
     * @return the main
     */
    public String getMain() {
        return main;
    }

    /**
     * @return the updatedAt
     */
    public String getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @return the updatedAtTs
     */
    public String getUpdatedAtTs() {
        return updatedAtTs;
    }

    /**
     * @return the sku
     */
    public String getSku() {
        return sku;
    }

    /**
     * @return the fkCatalogBrand
     */
    public String getFkCatalogBrand() {
        return fkCatalogBrand;
    }

    /**
     * @return the skuSimple
     */
    public String getSkuSimple() {
        return skuSimple;
    }

    /**
     * @return the idCatalogBrand
     */
    public String getIdCatalogBrand() {
        return idCatalogBrand;
    }

    /**
     * @return the brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * @return the brandUrlKey
     */
    public String getBrandUrlKey() {
        return brandUrlKey;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the sprite
     */
    public String getSprite() {
        return sprite;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {

        idCatalogProductImage = jsonObject.optString(JSON_ID_CATALOG_PRODUCT_IMAGE_TAG);
        fkCatalogConfig = jsonObject.optInt(JSON_FK_CATALOG_CONFIG_TAG);
        fkCatalogSimple = jsonObject.optInt(JSON_FK_CATALOG_SIMPLE_TAG);
        image = jsonObject.optString(JSON_IMAGE_TAG);
        main = jsonObject.optString(JSON_MAIN_TAG);
        updatedAt = jsonObject.optString(JSON_UPDATED_AT_TAG);
        updatedAtTs = jsonObject.optString(JSON_UPDATED_AT_TS_TAG);
        sku = jsonObject.optString(JSON_SKU_TAG);
        fkCatalogBrand = jsonObject.optString(JSON_FK_CATALOG_BRAND_TAG);
        skuSimple = jsonObject.optString(JSON_SKU_SIMPLE_TAG);
        idCatalogBrand = jsonObject.optString(JSON_ID_CATALOG_BRAND_TAG);
        brandName = jsonObject.optString(JSON_BRAND_NAME_TAG);
        brandUrlKey = jsonObject.optString(JSON_BRAND_URL_KEY_TAG);
        url = jsonObject.optString(JSON_URL_TAG);
        path = jsonObject.optString(JSON_PATH_TAG);
        sprite = jsonObject.optString(JSON_SPRITE_TAG);

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put(JSON_ID_CATALOG_PRODUCT_IMAGE_TAG, idCatalogProductImage);
            jsonObject.put(JSON_FK_CATALOG_CONFIG_TAG, fkCatalogConfig);
            jsonObject.put(JSON_FK_CATALOG_SIMPLE_TAG, fkCatalogSimple);
            jsonObject.put(JSON_IMAGE_TAG, image);
            jsonObject.put(JSON_UPDATED_AT_TAG, updatedAt);
            jsonObject.put(JSON_UPDATED_AT_TS_TAG, updatedAtTs);
            jsonObject.put(JSON_SKU_TAG, sku);
            jsonObject.put(JSON_FK_CATALOG_BRAND_TAG, fkCatalogBrand);
            jsonObject.put(JSON_SKU_SIMPLE_TAG, skuSimple);
            jsonObject.put(JSON_ID_CATALOG_BRAND_TAG, idCatalogBrand);
            jsonObject.put(JSON_BRAND_NAME_TAG, brandName);
            jsonObject.put(JSON_BRAND_URL_KEY_TAG, brandUrlKey);
            jsonObject.put(JSON_URL_TAG, url);
            jsonObject.put(JSON_PATH_TAG, path);
            jsonObject.put(JSON_SPRITE_TAG, sprite);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

}
