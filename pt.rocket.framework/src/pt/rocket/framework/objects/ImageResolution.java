package pt.rocket.framework.objects;

import pt.rocket.framework.utils.LogTagHelper;

/**
 * 
 * @author sergiopereira
 *
 */
public class ImageResolution {
	
	protected final static String TAG = LogTagHelper.create( ImageResolution.class );
	
	// JSON
    public static final String JSON_IDENTIFIER_TAG = "identifier";
    public static final String JSON_WIDTH_TAG = "width";
    public static final String JSON_HEIGHT_TAG = "height";
    public static final String JSON_EXTENSION_TAG = "extension";

	private String identifier;
	private int width;
	private int heigth;
	private String extension;


    /**
     * Constructor
     * @param identifier
     * @param width
     * @param heigth
     * @param extension
     */
    public ImageResolution(String identifier, int width, int heigth, String extension) {
		this.identifier = identifier;
		this.width = width;
		this.heigth = heigth;
		this.extension = extension;
	}


    /*
     * ############################ GETTERS #################################
     */

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}



	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}



	/**
	 * @return the heigth
	 */
	public int getHeigth() {
		return heigth;
	}



	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

    /*
     * ############################ SETTERS #################################
     */

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}



	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}



	/**
	 * @param heigth the heigth to set
	 */
	public void setHeigth(int heigth) {
		this.heigth = heigth;
	}



	/**
	 * @param extension the extension to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

    /*
     * ############################ JSON #################################
     */

	
}
