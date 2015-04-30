package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.framework.objects.home.group.BaseTeaserGroupType;
import com.mobile.framework.objects.home.object.BaseTeaserObject;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

/**
 *
 */
public class HomeShopWeekTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    // Data
    public View container;
    public ImageView left;
    public ViewGroup leftC;
    public ViewGroup rightC;
    public View leftP;
    public ImageView right;
    public View rightP;

    public HomeShopWeekTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        container = view.findViewById(R.id.home_teaser_shop_week_container);
        leftC = (ViewGroup) view.findViewById(R.id.home_teaser_shop_week_image_left);
        left = (ImageView) leftC.findViewById(R.id.home_teaser_item_image);
        leftP = leftC.findViewById(R.id.home_teaser_item_progress);
        rightC = (ViewGroup) view.findViewById(R.id.home_teaser_shop_week_image_right);
        right = (ImageView) rightC.findViewById(R.id.home_teaser_item_image);
        rightP = rightC.findViewById(R.id.home_teaser_item_progress);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        BaseTeaserObject leftX = group.getData().get(0);
        BaseTeaserObject rightX = group.getData().get(1);
        RocketImageLoader.instance.loadImage(leftX.getImage(), left, leftP, R.drawable.no_image_large);
        RocketImageLoader.instance.loadImage(rightX.getImage(), right, rightP, R.drawable.no_image_large);
        TeaserViewFactory.setClickableView(leftC, leftX, mParentClickListener);
        TeaserViewFactory.setClickableView(rightC, rightX, mParentClickListener);
    }

    @Override
    public void onUpdate() {

    }
}

