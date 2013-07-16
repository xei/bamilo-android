package pt.rocket.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Object that deals with the parsing of a homepage
 * @author josedourado
 *
 */

public class Homepage implements IJSONSerializable{
	
	private int homepageId;
	private String homepageTitle;
	private boolean defaultHomepage;
	private ArrayList<TeaserSpecification<?>> teaserSpecifications;
	private String homepageLayout;
	protected static final String JSON_HOMEPAGE_ID_TAG = "homepage_id";
	protected static final String JSON_HOMEPAGE_TITLE_TAG = "homepage_title";
	protected static final String JSON_HOMEPAGE_DEFAULT_TAG = "homepage_default";
	protected static final String JSON_HOMEPAGE_LAYOUT_TAG= "homepage_layout";
	
	/**
	 * Constructor
	 */
	public Homepage(){
		homepageId=-1;
		homepageTitle="";
		defaultHomepage=false;
		teaserSpecifications= new ArrayList<TeaserSpecification<?>>();
	}
	
	/**
	 * 
	 * @return homepage id
	 */
	public int getHomepageId(){
		return homepageId;
	}
	
	/**
	 * 
	 * @return homepage title
	 */
	public String getHomepageTitle(){
		return homepageTitle;
	}
	
	/**
	 * 
	 * @return true if default, false otherwise
	 */
	public boolean isDefaultHomepage(){
		return defaultHomepage;
	}
	
	/**
	 * 
	 * @return teaser specification
	 */
	public ArrayList<TeaserSpecification<?>> getTeaserSpecification(){
		return teaserSpecifications;
	}
	
	
	/**
	 * 
	 * @return homepage layout
	 */
	public String getHomepageLayout(){
	    return homepageLayout;
	}
	
	
	

	@Override
	public boolean initialize(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
		homepageId = jsonObject.getInt(JSON_HOMEPAGE_ID_TAG);
		homepageTitle=jsonObject.getString(JSON_HOMEPAGE_TITLE_TAG);
		defaultHomepage=jsonObject.getInt(JSON_HOMEPAGE_DEFAULT_TAG)==1?true:false;
		homepageLayout = jsonObject.getString(JSON_HOMEPAGE_LAYOUT_TAG);
		JSONArray dataArray = jsonObject.getJSONArray(JSON_DATA_TAG);
		int dataArrayLenght = dataArray.length();
		ArrayList<TeaserSpecification<?>> teaserSpecifications = new ArrayList<TeaserSpecification<?>>();
		for (int i = 0; i < dataArrayLenght; ++i) {
			teaserSpecifications.add(TeaserSpecification.parse(dataArray
					.getJSONObject(i)));
		}
		return true;
	}

	@Override
	public JSONObject toJSON() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
