///**
// * 
// */
//package pt.rocket.view.fragments;
//
//import org.holoeverywhere.widget.TextView;
//
//import pt.rocket.framework.objects.ITargeting;
//import pt.rocket.framework.objects.ProductTeaserGroup;
//import pt.rocket.framework.objects.ProductTeaserGroup.TeaserProduct;
//import pt.rocket.framework.utils.LogTagHelper;
//import pt.rocket.framework.utils.WindowHelper;
//import pt.rocket.view.R;
//import android.app.Activity;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.androidquery.AQuery;
//import com.androidquery.callback.AjaxStatus;
//import com.androidquery.callback.BitmapAjaxCallback;
//
//import de.akquinet.android.androlog.Log;
//
///**
// * @author manuelsilva
// * 
// */
//public class ProducTeaserListFragment extends BaseFragment {
//
//    private static final String TAG = LogTagHelper.create(ProducTeaserListFragment.class);
//
//    private static ProductTeaserGroup productTeaserGroup;
//
//    private OnClickListener onTeaserClickListener;
//
//    private LayoutInflater inflater;
//
//    /**
//     * 
//     * @param dynamicForm
//     * @return
//     */
//    public static ProducTeaserListFragment getInstance() {
//        ProducTeaserListFragment producTeaserListFragment = new ProducTeaserListFragment();
//        return producTeaserListFragment;
//    }
//
//    /**
//     * Empty constructor
//     * 
//     * @param arrayList
//     */
//    public ProducTeaserListFragment() {
//        super(IS_NESTED_FRAGMENT);
//        this.setRetainInstance(true);
//    }
//
//    @Override
//    public void sendValuesToFragment(int identifier, Object values) {
//        this.productTeaserGroup = (ProductTeaserGroup) values;
//    }
//
//    @Override
//    public void sendListener(int identifier, OnClickListener onTeaserClickListener) {
//        this.onTeaserClickListener = onTeaserClickListener;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
//     */
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        Log.i(TAG, "ON ATTACH");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i(TAG, "ON CREATE");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
//     * android.view.ViewGroup, android.os.Bundle)
//     */
//    @Override
//    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup,
//            Bundle savedInstanceState) {
//        super.onCreateView(mInflater, viewGroup, savedInstanceState);
//        Log.i(TAG, "ON CREATE VIEW");
//        inflater = mInflater;
//        View view = mInflater.inflate(R.layout.teaser_products_group, null, false);
//
//        return view;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onStart()
//     */
//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.i(TAG, "ON START");
//    }
//
//    private void setLayout() {
//        ViewGroup container = (ViewGroup) getView()
//                .findViewById(R.id.teaser_group_container);
//
//        if (productTeaserGroup != null) {
//            ((TextView) getView().findViewById(R.id.teaser_group_title))
//                    .setText(productTeaserGroup.getTitle());
//            for (TeaserProduct product : productTeaserGroup.getTeasers()) {
//                container.addView(createProductTeaserView(product, container, inflater));
//            }
//        }
//
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onResume()
//     */
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.i(TAG, "ON RESUME");
//        setLayout();
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onPause()
//     */
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.i(TAG, "ON PAUSE");
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see android.support.v4.app.Fragment#onStop()
//     */
//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.i(TAG, "ON STOP");
//        // FlurryTracker.get().end();
//    }
//
//    private View createProductTeaserView(TeaserProduct product, ViewGroup vg,
//            LayoutInflater mInflater) {
//        View productTeaserView = mInflater.inflate(R.layout.product_item_small,
//                vg, false);
//        if (product.getImages().size() > 0) {
//            setImageToLoad(product.getImages().get(0).getUrl(),
//                    productTeaserView);
//        }
//        ((TextView) productTeaserView.findViewById(R.id.item_title))
//                .setText(product.getName());
//        String price;
//        if (!TextUtils.isEmpty(product.getSpecialPrice())) {
//            price = product.getSpecialPrice();
//        } else {
//            price = product.getPrice();
//        }
//        ((TextView) productTeaserView.findViewById(R.id.item_price))
//                .setText(price);
//        attachTeaserListener(product, productTeaserView);
//        return productTeaserView;
//    }
//
//    private void attachTeaserListener(ITargeting targeting, View view) {
//        view.setTag(R.id.target_url, targeting.getTargetUrl());
//        view.setTag(R.id.target_type, targeting.getTargetType());
//        view.setTag(R.id.target_title, targeting.getTargetTitle());
//        view.setOnClickListener(onTeaserClickListener);
//    }
//
//    private void setImageToLoad(String imageUrl, View imageTeaserView) {
//        final ImageView imageView = (ImageView) imageTeaserView
//                .findViewById(R.id.image_view);
//        final View progressBar = imageTeaserView
//                .findViewById(R.id.image_loading_progress);
//
//        int mainContentWidth = (int) (WindowHelper.getWidth(getActivity()) * getResources().getFraction(R.dimen.navigation_menu_offset,1,1));
//        imageTeaserView.getLayoutParams().width = mainContentWidth / productTeaserGroup.getTeasers().size();
//
//        if (!TextUtils.isEmpty(imageUrl)) {
//            AQuery aq = new AQuery(getBaseActivity());
//            aq.id(imageView).image(imageUrl, true, true, 0, 0, new BitmapAjaxCallback() {
//
//                        @Override
//                        public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
//                            imageView.setImageBitmap(bm);
//                            imageView.setVisibility(View.VISIBLE);
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    });
////            ImageLoader.getInstance().displayImage(imageUrl, imageView,
////                    new SimpleImageLoadingListener() {
////
////                        /*
////                         * (non-Javadoc)
////                         * 
////                         * @see
////                         * com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener
////                         * #onLoadingComplete(java.lang.String, android.view.View,
////                         * android.graphics.Bitmap)
////                         */
////                        @Override
////                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
////                            progressBar.setVisibility(View.GONE);
////                            imageView.setVisibility(View.VISIBLE);
////                        }
////                    });
//        }
//
//    }
//}
