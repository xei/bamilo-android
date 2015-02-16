package com.mobile.utils.ui;

import java.util.ArrayList;

import com.mobile.framework.objects.CompleteProduct;
import com.mobile.framework.objects.Variation;

public class CompleteProductUtils {

    /**
     * ################# RELATED ITEMS #################
     */
    
    /**
     * ################# BUNDLE ITEMS #################
     */

    /**
     * ################# SIMPLE ITEMS #################
     */
    
    
    /**
     * ################# VARIATION ITEMS #################
     */
    
    /**
     * Find the index of selected variation
     * @return int
     * @author manuel
     */
    public static int findIndexOfSelectedVariation(CompleteProduct product) {
        ArrayList<Variation> var = product.getVariations();
        int idx;
        for (idx = 0; idx < var.size(); idx++) {
            if (var.get(idx).getSKU().equals(product.getSku()))
                return idx;
        }
        return -1;
    }

}
