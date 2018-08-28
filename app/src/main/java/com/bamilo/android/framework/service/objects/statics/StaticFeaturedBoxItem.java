package com.bamilo.android.framework.service.objects.statics;


import com.bamilo.android.framework.service.objects.home.object.TeaserTopSellerObject;
import com.bamilo.android.framework.service.objects.home.type.TeaserTargetType;

/**
 * Class used to fill the suggestions screen when no results are found after a
 * search
 *
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class StaticFeaturedBoxItem extends TeaserTopSellerObject {

    public static final String TAG = StaticFeaturedBoxItem.class.getSimpleName();

    /**
     * Constructor
     */
    public StaticFeaturedBoxItem() {
        super(TeaserTargetType.PRODUCT_DETAIL.ordinal());
        mTargetLink = TeaserTargetType.PRODUCT_DETAIL.name();
    }
}
