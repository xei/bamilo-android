package pt.rocket.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.holoeverywhere.widget.TextView;
import org.holoeverywhere.widget.Toast;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.objects.Favourite;
import pt.rocket.framework.objects.ProductSimple;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CurrencyFormatter;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.helpers.GetShoppingCartAddItemHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.RocketImageLoader;
import pt.rocket.utils.dialogfragments.DialogGenericFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment;
import pt.rocket.utils.dialogfragments.DialogListFragment.OnDialogListListener;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import pt.rocket.view.fragments.FavouritesFragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import de.akquinet.android.androlog.Log;

/**
 * This Class is used to create an adapter for the list of favourites. It is called by FavouritesFragment
 * 
 * @author Andre Lopes
 * 
 */
public class FavouritesListAdapter extends BaseAdapter {
    private final static String TAG = LogTagHelper.create(FavouritesListAdapter.class);

    private final static int NO_SIMPLE_SELECTED = -1;
    private final static String VARIATION_PICKER_ID = "variation_picker";

    private DialogFragment mDialogAddedToCart;
    private DialogFragment dialog;
    private ArrayList<String> mSimpleVariants;
    private ArrayList<String> mSimpleVariantsAvailable;
    private ArrayList<String> variations;

    public boolean isAddingProductToCart = false;

    public interface OnSelectedItemsChange {
        public void SelectedItemsChange(int numSelectedItems);
    }

    private ArrayList<Favourite> favourites;

    private LayoutInflater inflater;

    private FavouritesFragment mFragment;
    
    private Context context;

    View mFavouritesNotFound;
    View mAddAllToCartButton;

    /**
     * A representation of each item on the list
     */
    public static class Item {
        private ImageView image;
        private View progress;
        private TextView name;
        private TextView discount;
        private TextView price;
        private TextView discountPercentage;
        private TextView brand;
        private ImageView isNew;

        private ViewGroup mVarianceContainer;
        private TextView mVarianceText;
        private Button mVarianceButton;
        private View mVariantPriceContainer;
        private TextView mVariantNormPrice;
        private TextView mVariantSpecPrice;
        private TextView mVariantChooseError;
        private Button mAddToCartButton;

        private Button deleteButton;
        private View container;
    }

    /**
     * the constructor for this adapter
     * 
     * @param activity
     * @param showList show list (or grid)
     * @param numColumns 
     */
    public FavouritesListAdapter(FavouritesFragment fragment, Context context, ArrayList<Favourite> favourites) {
        this.mFragment = fragment;

        this.context = context.getApplicationContext();

        this.inflater = LayoutInflater.from(context);
        
        this.favourites = favourites;
        
        mFavouritesNotFound = mFragment.getView().findViewById(R.id.favourites_not_found);
        mAddAllToCartButton = mFragment.getView().findViewById(R.id.favourites_shop_all);
    }

    @Override
    public int getCount() {
        return this.favourites.size();
    }
    
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        Favourite favourite = null;

        favourite = this.favourites.get(position);

        return favourite;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setFavouriteVariations(Favourite favourite, boolean hasVariations) {
        favourite.hasVariations = hasVariations;
    }

    private boolean hasVariationsFavourite (Favourite favourite) {
        return favourite.hasVariations;
    }
    
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Favourite favourite = favourites.get(position);

        final View itemView;
        final Item prodItem;

        // Validate view
        if (convertView != null) {
            // if the view already exists there is no need to inflate it again
            itemView = convertView;
        } else {
            itemView = inflater.inflate(R.layout.favourite_item, parent, false);
        }

        if ((Item) itemView.getTag() == null) {
            prodItem = new Item();
            prodItem.image = (ImageView) itemView.findViewById(R.id.image_view);
            prodItem.progress = itemView.findViewById(R.id.image_loading_progress);
            prodItem.name = (TextView) itemView.findViewById(R.id.item_name);
            prodItem.price = (TextView) itemView.findViewById(R.id.item_regprice);
            prodItem.discount = (TextView) itemView.findViewById(R.id.item_discount);
            prodItem.discountPercentage = (TextView) itemView.findViewById(R.id.discount_percentage);

            prodItem.brand = (TextView) itemView.findViewById(R.id.item_brand);
            prodItem.isNew = (ImageView) itemView.findViewById(R.id.image_is_new);
            
            prodItem.mVarianceContainer = (ViewGroup) itemView.findViewById(R.id.product_variant_container);
            prodItem.mVarianceText = (TextView) itemView.findViewById(R.id.product_variant_text);
            prodItem.mVarianceButton = (Button) itemView.findViewById(R.id.product_variant_button);

            prodItem.mVariantPriceContainer = itemView.findViewById(R.id.product_variant_price_container);
            prodItem.mVariantNormPrice = (TextView) itemView.findViewById(R.id.product_variant_normprice);
            prodItem.mVariantSpecPrice = (TextView) itemView.findViewById(R.id.product_variant_specprice);
            prodItem.mVariantChooseError = (TextView) itemView.findViewById(R.id.product_variant_choose_error);

            prodItem.mAddToCartButton = (Button) itemView.findViewById(R.id.shop);
            
            prodItem.deleteButton = (Button) itemView.findViewById(R.id.delete_button);

            prodItem.container = itemView.findViewById(R.id.container);

            // stores the item representation on the tag of the view for later
            // retrieval
            itemView.setTag(prodItem);
        } else {
            prodItem = (Item) itemView.getTag();
        }

        Log.d(TAG, "ITEM on position " + position);
        setupVariants(prodItem, position);

        prodItem.deleteButton.setTag(position);
        prodItem.deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && tag instanceof Integer) {
                    int position = (Integer) tag;

                    Favourite favourite = favourites.get(position);

                    favourite.setFavoriteSelected(position);

                    removeFromFavourites(favourite);
                } else {
                    Log.d(TAG, "deleteButton onClick - no Tag");
                }
            }
        });

        prodItem.container.setTag(position);
        prodItem.container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && tag instanceof Integer) {
                    int position = (Integer) tag;

                    Favourite favourite = favourites.get(position);

                    favourite.setFavoriteSelected(position);

                    JumiaApplication.INSTANCE.showRelatedItemsGlobal = true;
                    Bundle bundle = new Bundle();
                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, favourite.getUrl());
                    //bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, navigationSource);
                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
                    mFragment.getBaseActivity().onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
                } else {
                    Log.d(TAG, "container onClick - no Tag");
                }
            }
        });

        String imageURL = "";
        if (favourite.getImageList().size() > 0) {
            imageURL = favourite.getImageList().get(0);
        }

        // hide varianceContainer always
        //prodItem.mVarianceContainer.setVisibility(View.GONE);
        if (favourite.isComplete()) {
            RocketImageLoader.instance.loadImage(imageURL, prodItem.image, prodItem.progress, R.drawable.no_image_small);

            if (favourite.hasVariations == null || favourite.hasVariations) {
                // needed for next step
                mSimpleVariants = createSimpleVariants(favourite);
                Log.i(TAG, "scanSimpleForKnownVariations : updateVariants "+mSimpleVariants.size());

                prodItem.mVariantChooseError.setVisibility(View.INVISIBLE);
                prodItem.mVarianceButton.setText("...");

                preselectASimpleItem(prodItem, favourite);
            } else {
                Log.d(TAG, "HIDE VARIATIONS");
                prodItem.mVarianceContainer.setVisibility(View.GONE);
            }
        } else {
            prodItem.image.setImageResource(R.drawable.no_image_small);
        }

        // Set is new image
        if (favourite.isNew()) {
            prodItem.isNew.setVisibility(View.VISIBLE);
        } else {
            prodItem.isNew.setVisibility(View.GONE);
        }

        // Set brand
        prodItem.brand.setText(favourite.getBrand().toUpperCase());

        prodItem.name.setText(favourite.getName());
        prodItem.price.setText(favourite.getPrice());

        if (null != favourite.getSpecialPrice() && !favourite.getSpecialPrice().equals(favourite.getPrice())) {
            prodItem.discount.setText(favourite.getSpecialPrice());
            prodItem.discountPercentage.setText("-" + favourite.getMaxSavingPercentage().intValue() + "%");
            prodItem.discount.setVisibility(View.VISIBLE);
            prodItem.discountPercentage.setVisibility(View.VISIBLE);
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            prodItem.price.setSelected(true);
            prodItem.price.setTextColor(context.getResources().getColor(R.color.grey_light));
            prodItem.price.setTextAppearance(context.getApplicationContext(), R.style.text_normal);
        } else {
            prodItem.discount.setVisibility(View.GONE);
            prodItem.discountPercentage.setVisibility(View.GONE);
            prodItem.price.setPaintFlags(prodItem.price.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            prodItem.price.setTextAppearance(context.getApplicationContext(), R.style.text_bold);
            prodItem.price.setTextColor(context.getResources().getColor(R.color.red_basic));
        }

        updateTags(prodItem, position);

        return itemView;
    }

    private void updateTags(Item prodItem, int position) {
        prodItem.deleteButton.setTag(position);

        prodItem.container.setTag(position);

        prodItem.mVarianceButton.setTag(position);

        prodItem.mAddToCartButton.setTag(position);
    }

    private void removeFromFavourites(Favourite favourite) {
        FavouriteTableHelper.removeFavouriteProduct(favourite.getSku());
        Toast.makeText(context, "Item removed from My Favourites", Toast.LENGTH_SHORT).show();

        favourites.remove(favourite.getFavoriteSelected());

        if (!favourites.isEmpty()) {
            for (Favourite fav : favourites) {
                if (fav.hasVariations) {
                    fav.hasVariations = null;
                }
            }
        }
        notifyDataSetChanged();

        if (favourites.isEmpty()) {
            mFavouritesNotFound.setVisibility(View.VISIBLE);

            mAddAllToCartButton.setVisibility(View.GONE);
        }
    }

    private void setupVariants(final Item prodItem, int position) {
        prodItem.mVarianceButton.setTag(position);
        prodItem.mVarianceButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && tag instanceof Integer) {
                    int position = (Integer) tag;

                    Favourite favourite = favourites.get(position);

                    favourite.setFavoriteSelected(position);

                    updateVariants(prodItem, favourite);

                    showVariantsDialog(prodItem, position);
                } else {
                    Log.d(TAG, "mVarianceButton onClick - no Tag");
                }
            }
        });

        prodItem.mAddToCartButton.setSelected(true);
        prodItem.mAddToCartButton.setTag(position);
        prodItem.mAddToCartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null && tag instanceof Integer) {
                    int position = (Integer) tag;

                    Favourite favourite = favourites.get(position);

                    favourite.setFavoriteSelected(position);

                    if (!isAddingProductToCart) {
                        isAddingProductToCart = true;
                        executeAddProductToCart(prodItem, favourite);
                    }
                } else {
                    Log.d(TAG, "mVarianceButton onClick - no Tag");
                }
            }
        });
    }
    
    /**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946 
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if(observer !=null){
            super.unregisterDataSetObserver(observer);    
        }
    }

    public ProductSimple getSelectedSimple(Favourite favourite) {
        if ( null == favourite ) {
            return null;
        }
        
        int selectedSimple = favourite.getSelectedSimple();
        
        if (selectedSimple >= favourite.getSimples().size())
            return null;
        if (selectedSimple == NO_SIMPLE_SELECTED)
            return null;

        return favourite.getSimples().get(selectedSimple);
    }

    private void showVariantsDialog(Item prodItem, int favouritePosition) {
        BaseActivity activity = mFragment.getBaseActivity();
        
        activity.showWarningVariation(false);
        String title = context.getString(R.string.product_variance_choose);
        /*-DialogListFragment dialogListFragment = DialogListFragment.newInstance(mFragment, VARIATION_PICKER_ID,
                title, mSimpleVariants, mSimpleVariantsAvailable,
                favourite.selectedSimple);*/

        Favourite favourite = favourites.get(favouritePosition);

        DialogListFragment dialogListFragment = DialogListFragment.newInstance(activity,
                new FavouriteOnDialogListListener(prodItem, favouritePosition), VARIATION_PICKER_ID, title,
                mSimpleVariants, mSimpleVariantsAvailable, favourite.getSelectedSimple());
        
        dialogListFragment.show(mFragment.getFragmentManager(), null);
    }
    
    class FavouriteOnDialogListListener implements OnDialogListListener {
        Item prodItem;
        int favouritePosition;

        public FavouriteOnDialogListListener(Item prodItem, int favouritePosition) {
            this.prodItem = prodItem;
            this.favouritePosition = favouritePosition;
        }

        @Override
        public void onDialogListItemSelect(String id, int position, String value) {
            Favourite favourite = favourites.get(favouritePosition);

            favourite.setSelectedSimple(position);
            Log.i(TAG, "size selected! onDialogListItemSelect : " + position);
            updateVariants(prodItem, favourite);
            // updateStockInfo();
            //displayPriceInfoOverallOrForSimple();
        }
    }

    public void showChooseReminder(Item prodItem) {
        // prodItem.mVarianceText.setTextColor(getResources().getColor(R.color.red_basic));
        prodItem.mVariantChooseError.setVisibility(View.VISIBLE);
        
        isAddingProductToCart = false;
    }

    private void executeAddProductToCart(Item prodItem, Favourite favourite) {
        ProductSimple simple = getSelectedSimple(favourite);
        if (simple == null) {
            showChooseReminder(prodItem);
            
            return;
        }

        mFragment.getBaseActivity().showProgress();

        ContentValues values = new ContentValues();

        String sku = simple.getAttributeByKey(ProductSimple.SKU_TAG);
        values.put("p", favourite.getSku());
        values.put("sku", sku);
        values.put("quantity", "" + 1);
        Bundle bundle = new Bundle();
        bundle.putParcelable(GetShoppingCartAddItemHelper.ADD_ITEM, values);

        mFragment.addToCart(new GetShoppingCartAddItemHelper(), bundle, responseCallback);
    }

    IResponseCallback responseCallback = new IResponseCallback() {
        @Override
        public void onRequestError(Bundle bundle) {
            onErrorEvent(bundle);
        }

        @Override
        public void onRequestComplete(Bundle bundle) {
            onSuccessEvent(bundle);
        }
    };

    public void updateVariants(Item prodItem, Favourite favourite) {
        int selectedSimple = favourite.getSelectedSimple();

        if (selectedSimple == NO_SIMPLE_SELECTED) {
            prodItem.mVarianceButton.setText("...");
        }

        mSimpleVariants = createSimpleVariants(favourite);
        Log.i(TAG, "scanSimpleForKnownVariations : updateVariants "+mSimpleVariants.size());
        ProductSimple simple = getSelectedSimple(favourite);
        prodItem.mVariantChooseError.setVisibility(View.INVISIBLE);
//        Log.i(TAG, "code1stock size selected!" + mSelectedSimple);
        if (simple == null) {
            prodItem.mVariantPriceContainer.setVisibility(View.GONE);
        } else {
            Log.i(TAG, "size is : " + mSimpleVariants.get(selectedSimple));
            prodItem.mVariantPriceContainer.setVisibility(View.VISIBLE);
            String normPrice = simple.getAttributeByKey(ProductSimple.PRICE_TAG);
            String specPrice = simple.getAttributeByKey(ProductSimple.SPECIAL_PRICE_TAG);

            if (TextUtils.isEmpty(specPrice)) {
                normPrice = currencyFormatHelper(normPrice);
                prodItem.mVariantSpecPrice.setVisibility(View.GONE);
                prodItem.mVariantNormPrice.setText(normPrice);
                prodItem.mVariantNormPrice.setPaintFlags(prodItem.mVariantNormPrice.getPaintFlags()
                        & ~Paint.STRIKE_THRU_TEXT_FLAG);

                prodItem.mVariantNormPrice.setTextColor(context.getResources().getColor(R.color.red_basic));
                prodItem.mVariantNormPrice.setVisibility(View.VISIBLE);
            } else {
                normPrice = currencyFormatHelper(normPrice);
                specPrice = currencyFormatHelper(specPrice);
                prodItem.mVariantSpecPrice.setText(specPrice);
                prodItem.mVariantSpecPrice.setVisibility(View.VISIBLE);

                prodItem.mVariantNormPrice.setText(normPrice);
                prodItem.mVariantNormPrice.setPaintFlags(prodItem.mVariantNormPrice.getPaintFlags()
                        | Paint.STRIKE_THRU_TEXT_FLAG);
                prodItem.mVariantNormPrice.setTextColor(context.getResources().getColor(R.color.grey_light));
                prodItem.mVariantNormPrice.setVisibility(View.VISIBLE);
            }
            
//            if(mLastSelectedVariance!= null && !mLastSelectedVariance.equalsIgnoreCase(mSimpleVariants.get(favourite.selectedSimple))){
//                prodItem.mVarianceButton.setText("...");
//                favourite.selectedSimple = NO_SIMPLE_SELECTED;
//                mLastSelectedVariance = null;
//            } else {
                //mLastSelectedVariance = mSimpleVariants.get(favourite.selectedSimple);
            prodItem.mVarianceButton.setText(mSimpleVariants.get(selectedSimple));
//            }
            
            prodItem.mVarianceText.setTextColor(context.getResources().getColor(R.color.grey_middle));
        }

    }

    private String currencyFormatHelper(String number) {
        return CurrencyFormatter.formatCurrency(Double.parseDouble(number));
    }


    private ArrayList<String> createSimpleVariants(Favourite favourite) {
        Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants" + favourite.getName());
        ArrayList<ProductSimple> simples = (ArrayList<ProductSimple>) favourite.getSimples()
                .clone();
        variations = favourite.getKnownVariations();
        if(variations == null || variations.size() == 0){
            variations = new ArrayList<String>();
            variations.add("size");
            variations.add("color");
            variations.add("variation");
        }
        Set<String> foundKeys = scanSimpleAttributesForKnownVariants(favourite.getSimples());

        mSimpleVariantsAvailable = new ArrayList<String>();
        ArrayList<String> variationValues = new ArrayList<String>();
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : createSimpleVariants in");
            String value = calcVariationStringForSimple(simple, foundKeys);
            String quantity = simple.getAttributeByKey(ProductSimple.QUANTITY_TAG);

            if (quantity != null && Long.parseLong(quantity) > 0) {
                variationValues.add(value);
                mSimpleVariantsAvailable.add(value);
            } else {
                variationValues.add(value);
            }

        }

        return variationValues;
    }

    private Set<String> scanSimpleAttributesForKnownVariants(ArrayList<ProductSimple> simples) {
        Set<String> foundVariations = new HashSet<String>();
        Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants");
        for (ProductSimple simple : simples) {
            Log.i(TAG, "scanSimpleForKnownVariations : scanSimpleAttributesForKnownVariants in");
            scanSimpleForKnownVariants(simple, foundVariations);
        }

        return foundVariations;
    }

    private void scanSimpleForKnownVariants(ProductSimple simple, Set<String> foundVariations) {

        for (String variation : variations) {
            String attr = simple.getAttributeByKey(variation);
            Log.i(TAG, "scanSimpleForKnownVariations: variation = " +  variation + " attr = " + attr);
            if (attr == null)
                continue;
            foundVariations.add(variation);
        }
    }

    private String calcVariationStringForSimple(ProductSimple simple, Set<String> keys) {
        String delim = ";";
        String loopDelim = "";
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = simple.getAttributeByKey(key);
            if (value != null) {
                sb.append(loopDelim);
                sb.append(value);
                loopDelim = delim;
            }
        }

        return sb.toString();
    }

    private void preselectASimpleItem(Item prodItem, Favourite favourite) {
        if (favourite.getSelectedSimple() != NO_SIMPLE_SELECTED)
            return;
        // Editor eD = sharedPreferences.edit();
        // eD.putInt(VARIATION_LIST_POSITION, prodItem.mVariationsListPosition);
        // eD.commit();
        ArrayList<ProductSimple> ps = favourite.getSimples();
        Set<String> knownVariations = scanSimpleAttributesForKnownVariants(ps);

        boolean hideVariationSelection = true;
        if (ps.size() == 1) {
            if (knownVariations.size() <= 1) {
                favourite.setSelectedSimple(0);
                setFavouriteVariations(favourite, false);
                hideVariationSelection = true;
            } else {
                favourite.setSelectedSimple(NO_SIMPLE_SELECTED);
                setFavouriteVariations(favourite, true);
            }
        } else {
            hideVariationSelection = false;
            favourite.setSelectedSimple(NO_SIMPLE_SELECTED);
            setFavouriteVariations(favourite, true);
        }

        if (hideVariationSelection) {
            Log.d(TAG, "HIDE VARIATIONS");
            prodItem.mVarianceContainer.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "SHOW VARIATIONS");
            prodItem.mVarianceContainer.setVisibility(View.VISIBLE);
        }

    }

    protected void onSuccessEvent(Bundle bundle) {
        BaseActivity activity = mFragment.getBaseActivity();
       
        if(!mFragment.isVisible()){
            if(activity != null){
                activity.setProcessShow(true);    
            }
            
            return;
        }
        if(activity == null)
            return;
        activity.handleSuccessEvent(bundle);
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        Log.d(TAG, "onSuccessEvent: type = " + eventType);
        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            activity.updateCartInfo();
            activity.dismissProgress();

            String skuAddedItem = (String) bundle.getSerializable(Constants.BUNDLE_METADATA_KEY);
            if (!TextUtils.isEmpty(skuAddedItem)) {
                for (Favourite favourite : favourites) {
                    if (skuAddedItem.equals(favourite.getSku())) {
                        executeAddToShoppingCartCompleted(favourite);
                    }
                }
            }
            break;
        }
    }

    private void executeAddToShoppingCartCompleted(Favourite favourite) {
        final BaseActivity activity = mFragment.getBaseActivity();

        String msgText = "1 " + context.getString(R.string.added_to_shop_cart_dialog_text);

        removeFromFavourites(favourite);

        mDialogAddedToCart = DialogGenericFragment.newInstance(
                false,
                false,
                true,
                context.getString(R.string.your_cart),
                msgText,
                context.getString(R.string.go_to_cart), context.getString(R.string.continue_shopping),
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            if(activity != null){
                                activity.onSwitchFragment(
                                        FragmentType.SHOPPING_CART, FragmentController.NO_BUNDLE,
                                        FragmentController.ADD_TO_BACK_STACK);    
                            }
                            if(mDialogAddedToCart != null){
                                mDialogAddedToCart.dismiss();    
                            }
                            
                        } else if (id == R.id.button2) {
                            mDialogAddedToCart.dismiss();
                        }
                    }
                });

        mDialogAddedToCart.show(mFragment.getFragmentManager(), null);
    }

    protected void onErrorEvent(Bundle bundle) {
        BaseActivity activity = mFragment.getBaseActivity();
        if(mFragment.getActivity() != null){
            activity.setProcessShow(true);
        }
        /*--// Validate fragment visibility
        if (isOnStoppingProcess) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }*/
        
        if(activity.handleErrorEvent(bundle)){
            return;
        }
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        Log.d(TAG, "onErrorEvent: type = " + eventType);
        switch (eventType) {
        case ADD_ITEM_TO_SHOPPING_CART_EVENT:
            isAddingProductToCart = false;
            activity.dismissProgress();
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                HashMap<String, List<String>> errorMessages = (HashMap<String, List<String>>) bundle
                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);

                if (errorMessages != null) {
                    int titleRes = R.string.error_add_to_cart_failed;
                    int msgRes = -1;

                    String message = null;
                    if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(
                            Errors.CODE_ORDER_PRODUCT_SOLD_OUT)) {
                        msgRes = R.string.product_outof_stock;
                    } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(
                            Errors.CODE_PRODUCT_ADD_OVERQUANTITY)) {
                        msgRes = R.string.error_add_to_shopping_cart_quantity;
                    } else if (errorMessages.get(RestConstants.JSON_ERROR_TAG).contains(
                            Errors.CODE_ORDER_PRODUCT_ERROR_ADDING)) {
                        List<String> validateMessages = errorMessages
                                .get(RestConstants.JSON_VALIDATE_TAG);
                        if (validateMessages != null && validateMessages.size() > 0) {
                            message = validateMessages.get(0);
                        } else {
                            msgRes = R.string.error_add_to_cart_failed;
                        }
                    }

                    if (msgRes != -1) {
                        message = context.getString(msgRes);
                    } else if (message == null) {
                        return;
                    }

                    FragmentManager fm = mFragment.getFragmentManager();
                    dialog = DialogGenericFragment.newInstance(true, true, false,
                            context.getString(titleRes),
                            message,
                            context.getString(R.string.ok_label), "", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    int id = v.getId();
                                    if (id == R.id.button1) {
                                        dialog.dismiss();
                                    }
                                }
                            });
                    dialog.show(fm, null);
                    return;
                }
            }
            if (!errorCode.isNetworkError()) {
                addToShoppingCartFailed();
                return;
            }
        }
    }

    private void addToShoppingCartFailed() {
        mDialogAddedToCart = DialogGenericFragment.newInstance(false, false, true, null,
                context.getString(R.string.error_add_to_shopping_cart), context
                        .getString(R.string.ok_label), "", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.button1) {
                            mDialogAddedToCart.dismiss();
                        } else if (id == R.id.button2) {

                        }
                    }
                });

        mDialogAddedToCart.show(mFragment.getFragmentManager(), null);
    }

}
