/** 
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.event.events;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;

/**
 * Get the products review for a certain product.
 * @author GuilhermeSilva
 *
 */
public class GetProductReviewsEvent extends RequestEvent {
    private static final EventType type = EventType.GET_PRODUCT_REVIEWS_EVENT;
    
    public final String productUrl;
    public final int pageNumber;
    
    /**
     * @param productUrl url of the complete  product (same used to get the completed product
     */
    public GetProductReviewsEvent(String productUrl, int pageNumber ) {
    	super(type);
        this.productUrl = productUrl;
        this.pageNumber = pageNumber;        
    }

}
