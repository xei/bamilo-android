package pt.rocket.framework.objects;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.rest.RestConstants;

/**
 * Represents a section of the api.
 * For each section, there is a service.
 * Each section contain name, url. 
 * @author GuilhermeSilva
 *
 */
public class Section implements IJSONSerializable {
	
	public static final String SECTION_NAME_TEASERS = "teasers";
	public static final String SECTION_NAME_BRANDS = "brands";
	public static final String SECTION_NAME_CATEGORIES = "categories";
	public static final String SECTION_NAME_SEGMENTS = "segments";
	public static final String SECTION_NAME_STATIC_BLOCKS = "static_blocks";
	public static final String SECTION_NAME_FORMS = "forms";
	public static final String SECTION_NAME_FETCHDATA = "fetchdata";
	public static final String SECTION_NAME_SLIDER = "slider";
	public static final String SECTION_NAME_IMAGE_RESOLUTIONS = "imageresolutions";
	public static final String SECTION_NAME_GET_3_HOUR_DELIVERY_ZIPCODES = "get3hourdeliveryzipcodes";
	
    /**
     * Name of the section.
     */
    private String name;
    
    /**
     * Url of the newest version of the section.
     */
    private String url;
    
    private String md5;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    
    public String getMd5() {
    	return md5;
    }
    
    /**
     * Empty constructor
     */
    public Section() {
    }
    
    public Section(String name, String md5, String url) {
    	init(name, md5, url);
    }

    private void init(String name, String md5, String url) {
    	this.name = name;
    	this.md5 = md5;
    	this.url = url;
    }
    
    /* (non-Javadoc)
     * @see pt.rocket.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            String name = jsonObject.getString(RestConstants.JSON_SECTION_NAME_TAG);
            
            /**
             * HACK - while API dont return md5 for each section, 
             * use System.currentTimeMillis in the mean time to "replace" md5.
             */
            String md5 = ""+System.currentTimeMillis();
            		
            if(jsonObject.has(RestConstants.JSON_SECTION_MD5_TAG)){
            	md5 = jsonObject.getString(RestConstants.JSON_SECTION_MD5_TAG);
            }
             
            String url = jsonObject.getString(RestConstants.JSON_SECTION_URL_TAG);
            
        	init(name, md5, url);

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
            jsonObject.put(RestConstants.JSON_NAME_TAG, name);
            jsonObject.put(RestConstants.JSON_URL_TAG, url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
