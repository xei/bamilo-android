package com.bamilo.android.appmodule.bamiloapp.utils.home.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bamilo.android.framework.service.objects.home.group.BaseTeaserGroupType;
import com.bamilo.android.framework.service.objects.home.object.BaseTeaserObject;
import com.bamilo.android.appmodule.bamiloapp.utils.home.TeaserViewFactory;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.bamiloapp.utils.ui.UIUtils;
import com.bamilo.android.R;

/**
 * Class used to set the shop week teaser.
 */
public class HomeShopWeekTeaserHolder extends BaseTeaserViewHolder {

    public static final String TAG = TeaserViewFactory.class.getSimpleName();

    public ImageView leftImage;
    public ViewGroup leftContainer;
    public View leftProgress;
    public ViewGroup rightContainer;
    public ImageView rightImage;
    public View rightProgress;

    /**
     * Constructor
     */
    public HomeShopWeekTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        leftContainer = (ViewGroup) view.findViewById(R.id.home_teaser_shop_week_image_left);
        leftImage = (ImageView) leftContainer.findViewById(R.id.home_teaser_item_image);
        leftProgress = leftContainer.findViewById(R.id.home_teaser_item_progress);
        rightContainer = (ViewGroup) view.findViewById(R.id.home_teaser_shop_week_image_right);
        rightImage = (ImageView) rightContainer.findViewById(R.id.home_teaser_item_image);
        rightProgress = rightContainer.findViewById(R.id.home_teaser_item_progress);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        try {
            BaseTeaserObject leftX = group.getData().get(0);
            BaseTeaserObject rightX = group.getData().get(1);
            ImageManager.getInstance().loadImage(leftX.getImage(), leftImage, leftProgress, R.drawable.no_image_slider, false);
            ImageManager.getInstance().loadImage(rightX.getImage(), rightImage, rightProgress, R.drawable.no_image_slider, false);
            TeaserViewFactory.setClickableView(leftContainer, leftX, mParentClickListener, 0);
            TeaserViewFactory.setClickableView(rightContainer, rightX, mParentClickListener, 1);
        } catch (IndexOutOfBoundsException e) {
            UIUtils.showOrHideViews(View.GONE, leftContainer, rightContainer);
        }
    }

}

