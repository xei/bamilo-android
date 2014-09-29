//package pt.rocket.controllers;
//
//import java.util.ArrayList;
//
//import org.holoeverywhere.widget.TextView;
//
//import pt.rocket.constants.ConstantsIntentExtra;
//import pt.rocket.controllers.fragments.FragmentController;
//import pt.rocket.controllers.fragments.FragmentType;
//import pt.rocket.framework.objects.LastViewed;
//import pt.rocket.utils.imageloader.RocketImageLoader;
//import pt.rocket.view.BaseActivity;
//import pt.rocket.view.R;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.view.PagerAdapter;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//
//public class RelatedItemsAdapter extends PagerAdapter {
//    public static final String TAG = RelatedItemsAdapter.class.getName();
//    Context mContext;
//    ArrayList<LastViewed> mRelatedItems;
//    LayoutInflater mLayoutInflater;
//    private int partialSize = 3;
//
//    public RelatedItemsAdapter(Context ctx, ArrayList<LastViewed> lastViewed,
//            LayoutInflater layoutInflater) {
//        this.mContext = ctx;
//        this.mRelatedItems = lastViewed;
//        this.mLayoutInflater = layoutInflater;
//    }
//
//    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((View) object);
//    }
//
//    @Override
//    public int getCount() {
//        int count = 0;
//        if (mRelatedItems.size() == 0) {
//            count = 0;
//        } else if (mRelatedItems.size() / this.partialSize == 0
//                || (mRelatedItems.size() / this.partialSize == 1 && mRelatedItems.size()
//                        % this.partialSize == 0)) {
//            count = 1;
//        } else if (mRelatedItems.size() / this.partialSize == 1
//                || (mRelatedItems.size() / this.partialSize == 2 && mRelatedItems.size()
//                        % this.partialSize == 0)) {
//            count = 2;
//        } else if (mRelatedItems.size() / this.partialSize == 2
//                || (mRelatedItems.size() / this.partialSize == 3 && mRelatedItems.size()
//                        % this.partialSize == 0)) {
//            count = 3;
//        } else if (mRelatedItems.size() / this.partialSize == 3
//                || (mRelatedItems.size() / this.partialSize == 4 && mRelatedItems.size()
//                        % this.partialSize == 0)) {
//            count = 4;
//        } else if (mRelatedItems.size() / this.partialSize == 4
//                || (mRelatedItems.size() / this.partialSize == 5 && mRelatedItems.size()
//                        % this.partialSize == 0)) {
//            count = 5;
//        } else if (mRelatedItems.size() / this.partialSize == 5
//                || (mRelatedItems.size() / this.partialSize == 6 && mRelatedItems.size()
//                        % this.partialSize == 0)) {
//            count = 6;
//        } else if (mRelatedItems.size() / this.partialSize >= 6) {
//            count = 7;
//        }
//        return count;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, final int position) {
//        View view = mLayoutInflater.inflate(R.layout.element_related_items, container, false);
//        if (position * 3 < this.mRelatedItems.size()) {
//            RelativeLayout mElement1 = (RelativeLayout) view.findViewById(R.id.element_1);
//            mElement1.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, mRelatedItems.get(position * 3).getProductUrl());
//                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserrecentlyviewed_prefix);
//                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
//                    // inform PDV that Related Items should be shown
//                    bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
//                    ((BaseActivity) mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
//                }
//            });
//            ImageView img_1 = (ImageView) mElement1.findViewById(R.id.img_1);
//            TextView name_1 = (TextView) mElement1.findViewById(R.id.name_1);
//            TextView price_1 = (TextView) mElement1.findViewById(R.id.price_1);
//            name_1.setText(mRelatedItems.get(position * 3).getProductName());
//            price_1.setText(mRelatedItems.get(position * 3).getProductPrice());
//            
//            RocketImageLoader.instance.loadImage(mRelatedItems.get(position * 3).getImageUrl(), img_1, null, R.drawable.no_image_small);
//        }
//        if ((position * 3 + 1) < this.mRelatedItems.size()) {
//            RelativeLayout mElement2 = (RelativeLayout) view.findViewById(R.id.element_2);
//            mElement2.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, mRelatedItems.get(position * 3 + 1).getProductUrl());
//                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
//                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
//                    // inform PDV that Related Items should be shown
//                    bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
//                    ((BaseActivity) mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
//                }
//            });
//            ImageView img_2 = (ImageView) mElement2.findViewById(R.id.img_2);
//            TextView name_2 = (TextView) mElement2.findViewById(R.id.name_2);
//            TextView price_2 = (TextView) mElement2.findViewById(R.id.price_2);
//            // Log.i(TAG, "code1last generating : "+mLastViewedList.get(position).getProductName());
//            name_2.setText(mRelatedItems.get(position * 3 + 1).getProductName());
//            price_2.setText(mRelatedItems.get(position * 3 + 1).getProductPrice());
//            
//            RocketImageLoader.instance.loadImage(mRelatedItems.get(position * 3 + 1).getImageUrl(), img_2, null, R.drawable.no_image_small);
//        }
//        if ((position * 3 + 2) < this.mRelatedItems.size()) {
//            RelativeLayout mElement3 = (RelativeLayout) view.findViewById(R.id.element_3);
//            mElement3.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString(ConstantsIntentExtra.CONTENT_URL, mRelatedItems.get(position * 3 + 2).getProductUrl());
//                    bundle.putInt(ConstantsIntentExtra.NAVIGATION_SOURCE, R.string.gteaserprod_prefix);
//                    bundle.putString(ConstantsIntentExtra.NAVIGATION_PATH, "");
//                    // inform PDV that Related Items should be shown
//                    bundle.putBoolean(ConstantsIntentExtra.SHOW_RELATED_ITEMS, true);
//                    ((BaseActivity) mContext).onSwitchFragment(FragmentType.PRODUCT_DETAILS, bundle, FragmentController.ADD_TO_BACK_STACK);
//                }
//            });
//            ImageView img_3 = (ImageView) mElement3.findViewById(R.id.img_3);
//            TextView name_3 = (TextView) mElement3.findViewById(R.id.name_3);
//            TextView price_3 = (TextView) mElement3.findViewById(R.id.price_3);
//            // Log.i(TAG, "code1last generating : "+mLastViewedList.get(position).getProductName());
//            name_3.setText(mRelatedItems.get(position * 3 + 2).getProductName());
//            price_3.setText(mRelatedItems.get(position * 3 + 2).getProductPrice());
//            
//            RocketImageLoader.instance.loadImage(mRelatedItems.get(position * 3 + 2).getImageUrl(), img_3, null, R.drawable.no_image_small);
//        }
//
//        container.addView(view);
//        return view;
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object obj) {
//        return view == (View) obj;
//    }
//
//}
