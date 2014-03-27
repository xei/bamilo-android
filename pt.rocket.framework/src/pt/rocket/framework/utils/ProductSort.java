package pt.rocket.framework.utils;

/**
 * Defines the ProductSort Enum. The values are used when requesting products
 * from the api.
 * 
 * @author GuilhermeSilva
 * 
 */
public enum ProductSort {
    NONE(-1), POPULARITY(0), NAME(1), PRICE(2), BRAND(3), NEWIN(4), BESTRATING(5);
    
    public final int id;
    
    ProductSort(int id){
    	this.id = id;
    }
}
