package pt.rocket.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.holoeverywhere.FontLoader;

import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import pt.rocket.view.ShoppingCartActivity;
import pt.rocket.view.ShoppingCartActivity.CartItemValues;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.view.ActionMode;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;


/**
 * This Class implements the Basket Adapter
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Manuel Silva
 * @version 1.01
 * 
 *          2012/06/19
 * 
 */
public class ShoppingBasketListAdapter extends BaseAdapter {

	protected final String TAG = LogTagHelper.create(ShoppingBasketListAdapter.class);

	public int currentOrientation;

	ActionMode contextMenu;
	List<CartItemValues> itemValuesList;

	ProgressBar pBar;
	ImageView productView;
	//
	Dialog dialog;
	//

	int shop_cart_size = 0;
	public boolean isScrolling;

	public boolean multiSelect = false;

	public boolean firstTime = true;

	private LayoutInflater inflater;

    private ShoppingCartActivity activity;

	public ShoppingBasketListAdapter(ShoppingCartActivity activity, 
			List<CartItemValues> itemValuesList ) {

		this.activity = activity;
		// initialize the image loader service
		this.itemValuesList = itemValuesList;
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return itemValuesList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemValuesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void deleteSelectedProductsFromList() {
		notifyDataSetChanged();
	}

	/**
	 * Update Adapter Arrays
	 * 
	 * @param is_in_wish
	 * @param is_chek
	 * @param product_n
	 * @param price
	 * @param product_id
	 * @param unities
	 * @param images
	 */
	public void updateAllArrays( ArrayList<CartItemValues> itemValuesList ) {
		this.itemValuesList = itemValuesList;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View item = convertView;
		if (item == null) {
			item = inflater.inflate(R.layout.shopping_basket_product_element_container, parent, false);
		}
		
		final CartItemValues itemValues = itemValuesList.get(position);
		// Log.d( TAG, "getView: productName = " + itemValues.product_name);
		
		TextView tV = (TextView) item.findViewById(R.id.item_name);
		TextView priceView = (TextView) item.findViewById(R.id.item_regprice);

		tV.setText( itemValues.product_name );
		tV.setSelected(true);
		
		final ImageView productView = (ImageView) item.findViewById(R.id.image_view);

		final View pBar = item.findViewById(R.id.image_loading_progress);
		String url = itemValues.image;

        ImageLoader.getInstance().displayImage(url, productView, new SimpleImageLoadingListener() {

            /*
             * (non-Javadoc)
             * 
             * @see com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener#
             * onLoadingComplete(java.lang.String, android.view.View, android.graphics.Bitmap)
             */
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                productView.setVisibility(View.VISIBLE);
                pBar.setVisibility(View.GONE);
            }

        });

		

		TextView discountPercentage = (TextView) item.findViewById(R.id.discount_percentage);
		TextView priceDisc = (TextView) item.findViewById(R.id.item_discount);
		final ImageView promoImg = (ImageView) item.findViewById(R.id.item_promotion);

		if (!itemValues.price.equals(itemValues.price_disc)) {
			priceDisc.setText(itemValues.price_disc);
			priceDisc.setVisibility(View.VISIBLE);
			
	        priceView.setText(itemValues.price);
	        priceView.setVisibility(View.VISIBLE);
			priceView.setPaintFlags(priceView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			priceView.setTextColor(activity.getResources().getColor(R.color.grey_middlelight));
			
			discountPercentage.setText("-" + itemValues.discount_value.intValue()+ "%");
            discountPercentage.setVisibility(View.VISIBLE);
	        promoImg.setVisibility(View.VISIBLE);
		} else {
		    priceDisc.setText(itemValues.price);
		    priceView.setVisibility(View.INVISIBLE);
			promoImg.setVisibility(View.GONE);
			discountPercentage.setVisibility(View.GONE);
		}

		LinearLayout variancesContainer = (LinearLayout) item.findViewById(R.id.variances_container);
		variancesContainer.removeAllViews();
		if ( itemValues.simpleData != null) {
			variancesContainer.setVisibility(View.VISIBLE);
			Map<String, String> simpleData = itemValues.simpleData;

			for (Entry<String, String> entry : simpleData.entrySet()) {
				TextView variances = (TextView) inflater.inflate(R.layout.shopping_basket_variance_text, variancesContainer, false );
				// Log.d( TAG, "getView: entryKey = " + entry.getKey() +
				// " entryValue = " + entry.getValue());
				variances.setText(entry.getValue());
				variancesContainer.addView(variances);
			}
		}
		
		TextView stockInfo = (TextView) item.findViewById( R.id.item_stock );
		if ( itemValues.stock > 0 ) {
			stockInfo.setText( activity.getString( R.string.shoppingcart_instock ));
			stockInfo.setTextColor( activity.getResources().getColor( R.color.green_stock ));
		} else {
			stockInfo.setText( activity.getString( R.string.shoppingcart_notinstock ));
			stockInfo.setTextColor( activity.getResources().getColor( R.color.red_basic ));
		}
		

		Button deleteBtn = (Button) item.findViewById(R.id.delete_button);
		deleteBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				itemValues.is_checked = true;
				activity.deleteSelectedElements();
			}
		});
		
		
		Button quantityBtn = (Button) item.findViewById( R.id.changequantity_button );
        quantityBtn.setText( "  " + String.valueOf( itemValues.quantity ) + "  ");        
        quantityBtn.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				itemValues.is_checked = true;
				activity.changeQuantityOfItem( position );				
			}
		});

        /*
         * 
		// DELIVERY
//		TextView productDeliveryTime = (TextView) item.findViewById(R.id.cart_delivery_time);
//		TextView productDeliveryHint = (TextView) item.findViewById(R.id.cart_delivery_hint);
		if (stock.get(position) > 0) {
			productDeliveryHint.setText(R.string.in_stock);
			productDeliveryHint.setTextColor(activity.getResources().getColor(R.color.green_stock));
			productDeliveryTime.setVisibility(View.VISIBLE);
			String minDeliveryTime = itemValues.min_delivery_time.toString();
			String maxDeliveryTime = itemValues.max_delivery_time.toString();
			String deliveryText = String.format(item.getResources().getString(R.string.delivery_time) + " %s - %s "
					+ item.getResources().getString(R.string.business_days), minDeliveryTime, maxDeliveryTime);
			productDeliveryTime.setText(deliveryText);
		} else {
			productDeliveryHint.setText(R.string.out_of_stock);
			productDeliveryHint.setTextColor(activity.getResources().getColor(R.color.red_basic));
			productDeliveryTime.setVisibility(View.GONE);
		}

		Log.d(TAG, "productDeliveryTime = " + productDeliveryTime.getText());
		Log.d(TAG, "productDeliveryTime is VISIBLE = " + (productDeliveryTime.getVisibility()==View.VISIBLE));
		*/
		
		FontLoader.apply(item);
		return item;
	}
	

	/**
	 * This function round a number to a specific precision using a predefine
	 * rounding mode
	 * 
	 * @param unrounded
	 *            The value to round
	 * @param precision
	 *            The number of decimal places we want
	 * @param roundingMode
	 *            The type of rounding we want done. Please refer to the
	 *            java.math.BigDecimal class for more info
	 * @return The number rounded according to the specifications we established
	 */
	public double roundValue(double unrounded, int precision, int roundingMode) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);

		return rounded.doubleValue();
	}

}