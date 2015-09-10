package com.mobile.pojo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

/**
 * Created by alexandrapires on 8/31/15.
 */
public class ComboProductItem {

    private ProductBundle productBundle;
    private Context c;
    private ImageView separator;

    public ComboProductItem(Context c,ProductBundle productBundle )
    {
        this.c = c;
        this.productBundle = productBundle;
    }


    public ComboProductItem(Context c, int imageResource)
    {
        Drawable res = c.getResources().getDrawable(imageResource);

        if(separator == null)
            separator = new ImageView(c);
        separator.setImageDrawable(res);

    }

    public ProductBundle getProductBundle() {

        return productBundle;

    }



    public View setView(View convertView) {

        ImageView imLoaded = (ImageView)convertView.findViewById(R.id.image_view);
        CheckBox cb = (CheckBox)convertView.findViewById(R.id.image_view);
        TextView itemBrand = (TextView) convertView.findViewById(R.id.item_brand);
        TextView itemTitle = (TextView) convertView.findViewById(R.id.item_title);
        TextView itemPrice = (TextView) convertView.findViewById(R.id.item_price);
        // Set image
        ProgressBar imageL = (ProgressBar)  convertView.findViewById(R.id.image_loading_progress);
        RocketImageLoader.instance.loadImage(productBundle.getImageUrl(),imLoaded, imageL, R.drawable.no_image_large);

        cb.setChecked(productBundle.isChecked());
        itemBrand.setText(productBundle.getBrand());
        itemTitle.setText(productBundle.getName());
        itemPrice.setText(CurrencyFormatter.formatCurrency(productBundle.getPrice()));

        return convertView;
    }




}
