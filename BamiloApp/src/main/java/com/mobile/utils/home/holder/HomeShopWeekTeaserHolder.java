package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.service.objects.home.group.BaseTeaserGroupType;
import com.mobile.service.objects.home.object.BaseTeaserObject;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.R;

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
            //RocketImageLoader.instance.loadImage(leftX.getImage(), leftImage, leftProgress, R.drawable.no_image_large);
            ImageManager.getInstance().loadImage(leftX.getImage(), leftImage, leftProgress, R.drawable.no_image_slider, false);
            //RocketImageLoader.instance.loadImage(rightX.getImage(), rightImage, rightProgress, R.drawable.no_image_large);
            ImageManager.getInstance().loadImage(rightX.getImage(), rightImage, rightProgress, R.drawable.no_image_slider, false);
            TeaserViewFactory.setClickableView(leftContainer, leftX, mParentClickListener, 0);
            TeaserViewFactory.setClickableView(rightContainer, rightX, mParentClickListener, 1);
        } catch (IndexOutOfBoundsException e) {
            UIUtils.showOrHideViews(View.GONE, leftContainer, rightContainer);
        }
    }

}

