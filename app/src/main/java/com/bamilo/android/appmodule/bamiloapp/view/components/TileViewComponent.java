package com.bamilo.android.appmodule.bamiloapp.view.components;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventActionKeys;
import com.bamilo.android.appmodule.bamiloapp.constants.tracking.EventConstants;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.SimpleEventModel;
import com.bamilo.android.appmodule.bamiloapp.utils.ColorSequenceHolder;
import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import com.bamilo.android.appmodule.modernbamilo.util.storage.SharedPreferencesHelperKt;
import java.util.List;
import java.util.Locale;

public class TileViewComponent extends BaseViewComponent<List<TileViewComponent.TileItem>> {

    private List<TileItem> tileItems;
    private ColorSequenceHolder colorSequenceHolder;
    private OnTileClickListener onTileClickListener;

    @Override
    public View getView(Context context) {
        if (tileItems != null) {
            View rootView = LayoutInflater.from(context).inflate(R.layout.component_tiles, null);
            if (tileItems.size() == 1) {
                tileItems.get(0).position = 0;
                enableHorizontalRow(rootView, tileItems.get(0));
            } else if (tileItems.size() == 3) {
                tileItems.get(0).position = 0;
                tileItems.get(2).position = 2;
                tileItems.get(1).position = 1;

                enableThreeTilesState(rootView, tileItems.get(0), tileItems.get(2),
                        tileItems.get(1));
            } else if (tileItems.size() == 5) {
                tileItems.get(1).position = 1;
                tileItems.get(0).position = 0;
                tileItems.get(2).position = 2;
                tileItems.get(4).position = 4;
                tileItems.get(3).position = 3;

                enableFiveTilesState(rootView, tileItems.get(1), tileItems.get(0), tileItems.get(2),
                        tileItems.get(4), tileItems.get(3));
            }
            return rootView;
        }
        return null;
    }

    private void enableFiveTilesState(View rootView, TileItem topLeftTile, TileItem topRightTile,
            TileItem horizontalTile, TileItem bottomLeftItem, TileItem bottomRightTile) {
        enableTile(rootView, topLeftTile, R.id.cvFirstRowLeftCard, R.id.imgFirstRowLeftTile);
        enableTile(rootView, topRightTile, R.id.cvFirstRowRightCard, R.id.imgFirstRowRightTile);
        enableThreeTilesState(rootView, horizontalTile, bottomLeftItem, bottomRightTile);
    }

    private void enableThreeTilesState(View rootView, TileItem horizontalTile, TileItem leftTile,
            TileItem rightTile) {
        enableHorizontalRow(rootView, horizontalTile);
        enableTile(rootView, leftTile, R.id.cvThirdRowLeftCard, R.id.imgThirdRowLeftTile);
        enableTile(rootView, rightTile, R.id.cvThirdRowRightCard, R.id.imgThirdRowRightTile);
    }


    private void enableHorizontalRow(View rootView, final TileItem item) {
        enableTile(rootView, item, R.id.cvSecondRowCard, R.id.imgSecondRowTile);
    }

    private void enableTile(View rootView, final TileItem item, @IdRes int cardViewResId,
            @IdRes int imageViewResId) {
        rootView.findViewById(cardViewResId).setVisibility(View.VISIBLE);
        rootView.findViewById(cardViewResId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTileClickListener != null) {
                    onTileClickListener.onTileClicked(v, item);
                }
                if (mPage != null) {
                    String category = String.format(Locale.US, "%s+%s_%d_%d", mPage,
                            BaseViewComponent.ComponentType.Tile.toString(), item.teaserId,
                            item.position);

                    String action = EventActionKeys.TEASER_TAPPED;
                    String label = item.targetLink;
                    SimpleEventModel sem = new SimpleEventModel(category, action, label,
                            SimpleEventModel.NO_VALUE);
                    TrackerManager.trackEvent(v.getContext(), EventConstants.TeaserTapped, sem);
                    SharedPreferencesHelperKt
                            .setHomePageItemsPurchaseTrack(v.getContext(), category, label,true);
                }
            }
        });
        ImageView img = rootView.findViewById(imageViewResId);
        img.setBackgroundColor(colorSequenceHolder.getNextColor());
        setImageToLoad(item.imageUrl, img);
    }

    private void setImageToLoad(String imageUrl, ImageView imageView) {
        ImageManager.getInstance().loadImage(imageUrl, imageView, null, 0, false);
    }

    @Override
    public void setContent(List<TileItem> content) {
        this.tileItems = content;
    }

    public OnTileClickListener getOnTileClickListener() {
        return onTileClickListener;
    }

    public void setOnTileClickListener(OnTileClickListener onTileClickListener) {
        this.onTileClickListener = onTileClickListener;
    }

    public void setColorSequenceHolder(ColorSequenceHolder colorSequenceHolder) {
        this.colorSequenceHolder = colorSequenceHolder;
    }

    public static class TileItem {

        public String imageUrl;
        public String targetLink;

        public int teaserId;
        public int position;

        TileItem(String imageUrl, String targetLink, int teaserId) {
            this.imageUrl = imageUrl;
            this.targetLink = targetLink;
            this.teaserId = teaserId;
        }
    }

    public interface OnTileClickListener {

        void onTileClicked(View v, TileItem item);
    }
}
