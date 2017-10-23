package com.mobile.view.components;

import android.content.Context;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.mobile.service.objects.home.model.BaseComponent;
import com.mobile.service.objects.home.model.TileComponent;
import com.mobile.utils.ColorSequenceHolder;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

public class TileViewComponent extends BaseViewComponent<List<TileViewComponent.TileItem>> {
    private List<TileItem> tileItems;
    private ColorSequenceHolder colorSequenceHolder;
    private OnTileClickListener onTileClickListener;

    @Override
    public View getView(Context context) {
        if (tileItems != null) {
            View rootView = LayoutInflater.from(context).inflate(R.layout.component_tiles, null);
            if (tileItems.size() == 1) {
                enableHorizontalRow(rootView, tileItems.get(0));
            } else if (tileItems.size() == 3) {
                enableThreeTilesState(rootView, tileItems.get(0), tileItems.get(2), tileItems.get(1));
            } else if (tileItems.size() == 5) {
                enableFiveTilesState(rootView, tileItems.get(1), tileItems.get(0), tileItems.get(2), tileItems.get(4), tileItems.get(3));
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

    private void enableTile(View rootView, final TileItem item, @IdRes int cardViewResId, @IdRes int imageViewResId) {
        rootView.findViewById(cardViewResId).setVisibility(View.VISIBLE);
        rootView.findViewById(cardViewResId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTileClickListener != null) {
                    onTileClickListener.onTileClicked(v, item);
                }
            }
        });
        ImageView img = (ImageView) rootView.findViewById(imageViewResId);
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

    @Override
    public void setComponent(BaseComponent component) {
        if (!(component instanceof TileComponent)) {
            return;
        }
        TileComponent tileComponent = (TileComponent) component;
        List<TileItem> tileItems = new ArrayList<>();

        for (TileComponent.Tile tile : tileComponent.getTiles()) {
            TileItem tempTile = new TileItem(tile.getPortraitImage(), tile.getTarget());
            tileItems.add(tempTile);
        }

        setContent(tileItems);
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

        public TileItem(String imageUrl, String targetLink) {
            this.imageUrl = imageUrl;
            this.targetLink = targetLink;
        }
    }

    public interface OnTileClickListener {
        void onTileClicked(View v, TileItem item);
    }
}
