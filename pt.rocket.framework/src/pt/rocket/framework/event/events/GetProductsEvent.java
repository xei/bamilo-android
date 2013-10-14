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
import pt.rocket.framework.utils.Direction;
import pt.rocket.framework.utils.ProductSort;

/**
 * Event used to get products from the remove api.
 * @author guilherme
 * 
 */
/**
 * @author GuilhermeSilva
 *
 */
public class GetProductsEvent extends RequestEvent {

    public static final EventType type = EventType.GET_PRODUCTS_EVENT;
    
    public final String productUrl;
    public final String searchQuery;
    public final int pageNumber;
    public final int totalCount;
    public final ProductSort sort;
    public final Direction direction;
    
    /**
     * @param searchQuery
     */
    public GetProductsEvent(String productUrl, String searchQuery, String md5) {
    	this(productUrl, searchQuery, 1, 10, md5);
    }
    
    /**
     * @param searchQuery
     * @param pageNumber
     * @param numOfProducts
     */
    public GetProductsEvent(String productUrl, String searchQuery , int pageNumber, int numOfProducts, String md5) {
    	this(productUrl, searchQuery, pageNumber, numOfProducts, ProductSort.NONE, Direction.ASCENDENT, md5);
    }
    
    public GetProductsEvent(String productUrl, String searchQuery, int pageNumber, int numOfProducts, ProductSort sort, Direction direction, String md5) {
    	super(type, md5);
		this.productUrl = productUrl;
        this.searchQuery = searchQuery;
        this.pageNumber = pageNumber;
        this.totalCount = numOfProducts;
        this.sort = sort;
        this.direction = direction;
    }
}
