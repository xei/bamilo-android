/**
 * 
 */
package pt.rocket.framework.tracking;

import pt.rocket.framework.R;

/**
 * @author sergiopereira
 */
public enum TrackingEvents {
	
	SEARCH_SUGGESTIONS(R.string.gaccount, R.string.gsearchsuggestions),
	
	ADD_TO_WISHLIST(R.string.gfavourites, R.string.gaddtofavorites),
	
	REMOVE_FROM_WISHLIST(R.string.gfavourites, R.string.garemovefromfavorites),
	
	SUBSCRIBE_NEWSLETTER(R.string.gaccount, R.string.garemovefromfavorites),
	
	UNSUBSCRIBE_NEWSLETTER(R.string.gaccount, R.string.gunsubscribenewsletter),
	
	SHOW_CAMPAIGN(R.string.ghomepage, R.string.gcampaign)
	
	;
	
	private int category;
    private int action;
    
    TrackingEvents(int category, int action) {
		this.category = category;
		this.action = action;
	}
    
    public int getCategory() {
		return category;
	}
    
    public int getAction() {
		return action;
	}
}
