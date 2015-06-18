package com.mobile.utils.home.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.home.group.BaseTeaserGroupType;
import com.mobile.newFramework.objects.home.object.BaseTeaserObject;
import com.mobile.utils.home.TeaserViewFactory;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 *
 */
public class HomeShopTeaserHolder extends BaseTeaserViewHolder {

    private static final String TAG = TeaserViewFactory.class.getSimpleName();

    // Data
    public ViewGroup container;
    public ViewGroup left;
    public ViewGroup middle;
    public ViewGroup right;

    public HomeShopTeaserHolder(Context context, View view, View.OnClickListener listener) {
        super(context, view, listener);
        container = (ViewGroup) view.findViewById(R.id.home_teaser_shop_container);
        left = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_left);
        middle = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_middle);
        right = (ViewGroup) view.findViewById(R.id.home_teaser_shop_image_right);
    }

    @Override
    public void onBind(BaseTeaserGroupType group) {
        Log.i(TAG, "SHOP_TEASERS: TAG IS NULL");
        for (int i = 0; i < group.getData().size(); i++) {
            BaseTeaserObject object = group.getData().get(i);
            ViewGroup parent;
            switch (i) {
                case 0:
                    parent = left;
                    break;
                case 1:
                    parent = middle;
                    break;
                case 2:
                default:
                    parent = right;
                    break;
            }
            ((TextView) parent.findViewById(R.id.home_teaser_shop_title)).setText(object.getTitle());
            ((TextView) parent.findViewById(R.id.home_teaser_shop_sub_title)).setText(object.getSubTitle());
            RocketImageLoader.instance.loadImage(object.getImage(), (ImageView) parent.findViewById(R.id.home_teaser_item_image), parent.findViewById(R.id.home_teaser_item_progress), R.drawable.no_image_large);
            TeaserViewFactory.setClickableView(parent, object, mParentClickListener);
        }
    }

    @Override
    public void onUpdate() {

    }
}