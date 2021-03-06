package com.bamilo.android.appmodule.bamiloapp.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bamilo.android.appmodule.bamiloapp.utils.imageloader.ImageManager;
import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.appmodule.modernbamilo.util.extension.StringExtKt;
import com.bamilo.android.framework.service.objects.cart.PurchaseCartItem;
import com.bamilo.android.framework.service.objects.cart.PurchaseEntity;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.utils.TextUtils;
import com.bamilo.android.framework.service.utils.shop.CurrencyFormatter;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;

import java.util.List;
import java.util.Map;

/**
 * Created by Arash on 1/24/2017.
 */

public class CartItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IResponseCallback {
    private List<PurchaseCartItem> itemsList;
    public BaseFragment baseFragment;
    private boolean mIsCheckout;
    private View.OnClickListener mOnAddQuantityClickListener;
    private View.OnClickListener mOnRemoveItemClickListener;
    private View.OnClickListener mOnChangeFavouriteClickListener;
    private View.OnClickListener mOnProductClickListener;
    PurchaseEntity mCart;
    Context mContext;


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView cart_item_name, cart_item_brand, cart_item_option1_value, cart_item_option2_value, cart_item_price_value, cart_item_price_old_value, cart_item_count;
        public TextView cart_item_option1_label, cart_item_option2_label, cart_fav_label;
        public ImageView cart_item_remove, cart_item_plus, cart_item_minus, cart_fav_icon, cart_item_image;
        public CardView root;
        public RelativeLayout cart_item_minus_rl, cart_item_plus_rl, cart_item_remove_rl;


        public ItemViewHolder(View view) {
            super(view);
            cart_item_name = view.findViewById(R.id.cart_item_name);
            cart_item_brand = view.findViewById(R.id.cart_item_brand);
            cart_item_option1_value = view.findViewById(R.id.cart_item_option1_value);
            cart_item_option2_value = view.findViewById(R.id.cart_item_option2_value);
            cart_item_option1_label = view.findViewById(R.id.cart_item_option1_label);
            cart_item_option2_label = view.findViewById(R.id.cart_item_option2_label);
            cart_item_price_value = view.findViewById(R.id.cart_item_price_value);
            cart_item_price_old_value = view.findViewById(R.id.cart_item_price_old_value);
            cart_item_count = view.findViewById(R.id.cart_item_count);
            cart_fav_label = view.findViewById(R.id.cart_fav_label);
            cart_item_remove = view.findViewById(R.id.cart_item_remove);
            cart_item_remove_rl = view.findViewById(R.id.cart_item_remove_rl);
            cart_item_plus = view.findViewById(R.id.cart_item_plus);
            cart_item_minus = view.findViewById(R.id.cart_item_minus);
            cart_fav_icon = view.findViewById(R.id.cart_fav_icon);
            cart_item_image = view.findViewById(R.id.cart_item_image);
            cart_item_plus_rl = view.findViewById(R.id.cart_item_plus_rl);
            cart_item_minus_rl = view.findViewById(R.id.cart_item_minus_rl);

            root = view.findViewById(R.id.card_view);
        }
    }

    public class DiscountViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout root;

        public DiscountViewHolder(View view) {
            super(view);
            root = view.findViewById(R.id.cart_item_discount_container);
        }
    }


    public CartItemAdapter(Context context, List<PurchaseCartItem> itemsList, View.OnClickListener onAddQuantityClickListener,
                           View.OnClickListener onRemoveItemClickListener, PurchaseEntity cart,
                           View.OnClickListener onChangeFavouriteClickListener,
                           View.OnClickListener onProductClickListener) {
        this.itemsList = itemsList;
        mContext = context;
        mOnAddQuantityClickListener = onAddQuantityClickListener;
        mOnRemoveItemClickListener = onRemoveItemClickListener;
        mCart = cart;
        mOnChangeFavouriteClickListener = onChangeFavouriteClickListener;
        mOnProductClickListener = onProductClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_shopping_cart_item, parent, false);

            holder = new ItemViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_shopping_cart_discount, parent, false);

            holder = new DiscountViewHolder(view);
        }

        return holder;
    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == 0) {
            ItemViewHolder vh = (ItemViewHolder)holder;
            PurchaseCartItem item = itemsList.get(position);
            vh.cart_item_name.setText(item.getName());
            vh.cart_item_name.setTag(R.id.target_sku, item.getSku());
            vh.cart_item_image.setTag(R.id.target_sku, item.getSku());
            if (android.text.TextUtils.isEmpty(item.getBrandName()))
                vh.cart_item_brand.setVisibility(View.INVISIBLE);
            else {
                vh.cart_item_brand.setVisibility(View.VISIBLE);
                vh.cart_item_brand.setText(item.getBrandName());
            }
            vh.cart_item_name.setOnClickListener(mOnProductClickListener);
            vh.cart_item_image.setOnClickListener(mOnProductClickListener);
            vh.cart_item_option1_value.setText("");
            vh.cart_item_option2_value.setText("");
            double price = item.getSpecialPrice();
            double oldPrice = item.getPrice();
            if (Double.isNaN(price))
            {
                price = oldPrice;
                vh.cart_item_price_old_value.setVisibility(View.INVISIBLE);
            }
            else
            {
                vh.cart_item_price_old_value.setPaintFlags(vh.cart_item_price_old_value.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }

            vh.cart_item_price_value.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(price)));
            vh.cart_item_price_old_value.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(oldPrice)));
            vh.cart_item_count.setText(StringExtKt.persianizeDigitsInString(String.valueOf(item.getQuantity())));
            vh.cart_item_remove.setTag(R.id.item_position, position);
            vh.cart_item_remove.setOnClickListener(mOnRemoveItemClickListener);
            vh.cart_item_remove_rl.setTag(R.id.item_position, position);
            vh.cart_item_remove_rl.setOnClickListener(mOnRemoveItemClickListener);
            vh.cart_item_plus.setTag(R.id.item_max, item.getMaxQuantity());
            vh.cart_item_plus.setTag(R.id.item_position, position);
            vh.cart_item_plus.setTag(R.id.item_change, 1);
            vh.cart_item_plus.setTag(R.id.item_quantity, item.getQuantity());
            vh.cart_item_plus.setOnClickListener(mOnAddQuantityClickListener);
            vh.cart_item_plus.setImageResource(item.getQuantity() == item.getMaxQuantity()? R.drawable.ic_categories_plus: R.drawable.ic_categories_plus_n);
            vh.cart_item_plus_rl.setTag(R.id.item_max, item.getMaxQuantity());
            vh.cart_item_plus_rl.setTag(R.id.item_position, position);
            vh.cart_item_plus_rl.setTag(R.id.item_change, 1);
            vh.cart_item_plus_rl.setTag(R.id.item_quantity, item.getQuantity());
            vh.cart_item_plus_rl.setOnClickListener(mOnAddQuantityClickListener);
            vh.cart_item_minus.setTag(R.id.item_max, item.getMaxQuantity());
            vh.cart_item_minus.setTag(R.id.item_position, position);
            vh.cart_item_minus.setTag(R.id.item_change, -1);
            vh.cart_item_minus.setTag(R.id.item_quantity, item.getQuantity());
            vh.cart_item_minus.setOnClickListener(mOnAddQuantityClickListener);
            vh.cart_item_minus.setImageResource(item.getQuantity() == 1 ? R.drawable.ic_categories_minus: R.drawable.ic_categories_minus_n);
            vh.cart_item_minus_rl.setTag(R.id.item_max, item.getMaxQuantity());
            vh.cart_item_minus_rl.setTag(R.id.item_position, position);
            vh.cart_item_minus_rl.setTag(R.id.item_change, -1);
            vh.cart_item_minus_rl.setTag(R.id.item_quantity, item.getQuantity());
            vh.cart_item_minus_rl.setOnClickListener(mOnAddQuantityClickListener);
            vh.cart_fav_icon.setSelected(item.isWishList());
            vh.cart_fav_icon.setTag(R.id.sku, item.getSku());
            vh.cart_fav_icon.setTag(R.id.cart_fav_icon, vh.cart_fav_icon);
            vh.cart_fav_icon.setOnClickListener(mOnChangeFavouriteClickListener);

            vh.cart_fav_label.setSelected(item.isWishList());
            vh.cart_fav_label.setTag(R.id.sku, item.getSku());
            vh.cart_fav_label.setTag(R.id.cart_fav_icon, vh.cart_fav_icon);
            vh.cart_fav_label.setOnClickListener(mOnChangeFavouriteClickListener);

            //vh.cart_item_image.setImageBitmap(item.getImageUrl());
            String imageUrl = item.getImageUrl().replace("-cart.jpg","-catalog_grid_3.jpg");
            if (TextUtils.isNotEmpty(imageUrl)) {
                //RocketImageLoader.instance.loadImage(imageUrl, vh.cart_item_image, null, R.drawable.no_image_tiny);
                ImageManager.getInstance().loadImage(imageUrl, vh.cart_item_image, null, R.drawable.no_image_large, false);
            } else {
                vh.cart_item_image.setVisibility(View.GONE);
            }
            if (item.getVariationName().toLowerCase().compareTo("size")==0)
            {
                vh.cart_item_option1_value.setVisibility(View.VISIBLE);
                vh.cart_item_option1_label.setVisibility(View.VISIBLE);
                vh.cart_item_option1_value.setText(StringExtKt.persianizeDigitsInString(item.getVariationValue()));
            }
            else
            {
                vh.cart_item_option1_label.setVisibility(View.INVISIBLE);
                vh.cart_item_option1_value.setVisibility(View.INVISIBLE);
            }
            //vh.cart_fav_icon.setImageResource(R.drawable.cart_item_like);
            //if (item.isWishList())vh.cart_fav_icon.setImageResource(R.drawable.cart_item_like_selected);

        }
        else
        {

            DiscountViewHolder vh = (DiscountViewHolder) holder;
            vh.root.removeAllViews();
            View totalView = LayoutInflater.from(mContext).inflate(R.layout.new_shopping_basket_discount_element, vh.root, false);
            TextView labelT = totalView.findViewById(R.id.discount_label);
            TextView valueT = totalView.findViewById(R.id.discount_amount);
            labelT.setText(R.string.cart_total_amount);
            valueT.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(mCart.getSubTotalUnDiscounted())));
            vh.root.addView(totalView);

            View discountView = LayoutInflater.from(mContext).inflate(R.layout.new_shopping_basket_discount_element, vh.root, false);
            TextView label = discountView.findViewById(R.id.discount_label);
            TextView value = discountView.findViewById(R.id.discount_amount);
            label.setText(R.string.cart_product_discount);
            value.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(mCart.getSubTotalUnDiscounted()-mCart.getSubTotal())));
            vh.root.addView(discountView);


            if (!(mCart.getPriceRules() == null || mCart.getPriceRules().size()==0))
            {
                for (Map.Entry<String, String> entry : mCart.getPriceRules().entrySet()) {
                    discountView = LayoutInflater.from(mContext).inflate(R.layout.new_shopping_basket_discount_element, vh.root, false);
                    label = discountView.findViewById(R.id.discount_label);
                    value = discountView.findViewById(R.id.discount_amount);
                    label.setText(entry.getKey());
                    value.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(entry.getValue())));
                    vh.root.addView(discountView);
                }
            }

            if (mCart.getCouponDiscount() != 0)
            {
                discountView = LayoutInflater.from(mContext).inflate(R.layout.new_shopping_basket_discount_element, vh.root, false);
                label = discountView.findViewById(R.id.discount_label);
                value = discountView.findViewById(R.id.discount_amount);
                label.setText(R.string.coupon_label);
                value.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(mCart.getCouponDiscount())));
                vh.root.addView(discountView);
            }

            if (mCart.getShippingValue() != 0)
            {
                discountView = LayoutInflater.from(mContext).inflate(R.layout.new_shopping_basket_discount_element, vh.root, false);
                label = discountView.findViewById(R.id.discount_label);
                value = discountView.findViewById(R.id.discount_amount);
                label.setText(R.string.order_summary_shipping_fee_label);
                value.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(mCart.getShippingValue())));
                vh.root.addView(discountView);
            }
            View finalView = LayoutInflater.from(mContext).inflate(R.layout.new_shopping_basket_total_element_adapter, vh.root, false);
            TextView totalValue = finalView.findViewById(R.id.total_value);
            TextView quantityValue = finalView.findViewById(R.id.total_quantity);
            // Set views
            totalValue.setText(StringExtKt.persianizeDigitsInString(CurrencyFormatter.formatCurrency(mCart.getTotal())));
            quantityValue.setText(StringExtKt.persianizeDigitsInString(TextUtils.getResourceString(mContext, R.string.cart_total_quantity, mCart.getCartCount())));
            vh.root.addView(finalView);
        }
    }

    /**
     * Process the click on edit button.</br>
     * Gets the address id from view tag.
     */
    View.OnClickListener onClickEditAddressButton =  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int addressId = (int) view.getTag();
           // Print.i(TAG, "ON CLICK: EDIT ADDRESS " + addressId);
            // Goto edit address
            Bundle bundle = new Bundle();
            bundle.putInt(ConstantsIntentExtra.ARG_1, addressId);
            baseFragment.getBaseActivity().onSwitchFragment(mIsCheckout?FragmentType.CHECKOUT_EDIT_ADDRESS:FragmentType.MY_ACCOUNT_EDIT_ADDRESS, bundle, FragmentController.ADD_TO_BACK_STACK);
        }
    } ;


    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount()-1) return 1;
        return 0;

    }

    @Override
    public int getItemCount() {
        return itemsList.size()+1;
    }



    @Override
    public void onRequestComplete(BaseResponse baseResponse) {

    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {

    }

}
