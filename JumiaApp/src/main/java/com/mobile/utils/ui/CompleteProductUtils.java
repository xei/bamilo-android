//package com.mobile.utils.ui;
//
//import com.mobile.newFramework.objects.product.Variation;
//import com.mobile.newFramework.objects.product.pojo.ProductComplete;
//
//import java.util.ArrayList;
//
//public class CompleteProductUtils {
//
//    /**
//     * ################# RELATED ITEMS #################
//     */
//
//    /**
//     * ################# BUNDLE ITEMS #################
//     */
//
//    /**
//     * ################# SIMPLE ITEMS #################
//     */
//
//
//    /**
//     * ################# VARIATION ITEMS #################
//     */
//
//    /**
//     * Find the index of selected variation
//     * @return int
//     * @author manuel
//     */
//    public static int findIndexOfSelectedVariation(ProductComplete product) {
//        ArrayList<Variation> var = product.getVariations();
//        int idx;
//        for (idx = 0; idx < var.size(); idx++) {
//            if (var.get(idx).getSKU().equals(product.getSku()))
//                return idx;
//        }
//        return -1;
//    }
//
//}
