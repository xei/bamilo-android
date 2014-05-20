/**
 * 
 */
package pt.rocket.view.fragments;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.GalleryPagerAdapter;
import pt.rocket.controllers.ProductImagesAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.FragmentCommunicatorForProduct;
import pt.rocket.utils.HorizontalListView;
import pt.rocket.utils.JumiaCatalogViewPager;
import pt.rocket.utils.JumiaViewPagerWithZoom;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.ProductDetailsActivityFragment;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class ProductImageGalleryFragment extends BaseFragment implements OnItemClickListener,
        OnPageChangeListener {

    private static final String TAG = LogTagHelper.create(ProductImageGalleryFragment.class);

    private static ProductImageGalleryFragment productImageGalleryFragment;

    private ProductImagesAdapter mImageListAdapter;

    // private NormalizingViewPagerWrapper mPagerWrapper;

    private JumiaViewPagerWithZoom mViewPager;

    private GalleryPagerAdapter galleryAdapter;

    private RelativeLayout mProductImageLoading;

    private CompleteProduct mCompleteProduct;

    private String mCompleteProductUrl;

    private View mainView;

    private boolean showHorizontalListView = false;

    private boolean isZoomAvailable = false;

    private int mVariationsListPosition = 1;

    private HorizontalListView mImagesList;

    private int currentPosition = 1;
    
    private ArrayList<String> imagesList;
    


    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductImageGalleryFragment getInstance() {
        productImageGalleryFragment = new ProductImageGalleryFragment();
        return productImageGalleryFragment;
    }

    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductImageGalleryFragment getInstance(Bundle bundle) {

        productImageGalleryFragment = new ProductImageGalleryFragment();

        productImageGalleryFragment.mCompleteProductUrl = bundle
                .getString(ConstantsIntentExtra.CONTENT_URL);
        productImageGalleryFragment.mVariationsListPosition = bundle.getInt(
                ConstantsIntentExtra.VARIATION_LISTPOSITION, 1);
        productImageGalleryFragment.currentPosition = bundle.getInt(
                ConstantsIntentExtra.CURRENT_LISTPOSITION, 1);

        if (productImageGalleryFragment.currentPosition <= 0)
            productImageGalleryFragment.currentPosition = 1;
        productImageGalleryFragment.isZoomAvailable = bundle.getBoolean(
                ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);

        if (!productImageGalleryFragment.isZoomAvailable)
            productImageGalleryFragment.showHorizontalListView = false;
        return productImageGalleryFragment;
    }

    /**
     * Empty constructor
     * 
     * @param arrayList
     */
    public ProductImageGalleryFragment() {
        super(EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(EventType.class),
                EnumSet.noneOf(MyMenuItem.class),
                NavigationAction.Products, 0, WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.setRetainInstance(true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(TAG, "ON ATTACH");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ON CREATE");
        // Retain this fragment across configuration changes.
        // setRetainInstance(true);
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup,
            Bundle savedInstanceState) {
        super.onCreateView(mInflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        
        mainView = mInflater.inflate(R.layout.product_showoff_viewpager_frame, viewGroup, false);
        mImagesList = (HorizontalListView) mainView.findViewById(R.id.images_list);
        mProductImageLoading = (RelativeLayout) mainView.findViewById(R.id.loading_gallery);
        mViewPager = (JumiaViewPagerWithZoom) mainView.findViewById(R.id.viewpager);

        // set page listener to handler infinite scrool event.
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                currentPosition = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

                int pageCount = galleryAdapter.getCount();

                if (arg0 == ViewPager.SCROLL_STATE_SETTLING) {
                    if (mViewPager != null)
                        mViewPager.setPagingEnabled(false);
                }

                if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
                    new ChangePageTask().execute(arg0);
                    /*
                     * mViewPager.setPagingEnabled(true); mViewPager.toggleJumiaScroller(true);
                     * 
                     * // if (currentPosition == 0) { mViewPager.toggleJumiaScroller(false);
                     * mViewPager.setCurrentItem(pageCount - 2);
                     * 
                     * // } else if (currentPosition == pageCount - 1) {
                     * mViewPager.toggleJumiaScroller(false); mViewPager.setCurrentItem(1); }
                     */
                }

            }
        });
        return mainView;
    }

    private class ChangePageTask extends AsyncTask<Integer, String, Boolean> {

        @Override
        protected Boolean doInBackground(Integer... params) {

            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    int pageCount = galleryAdapter.getCount();

                    mViewPager.setPagingEnabled(true);
                    mViewPager.toggleJumiaScroller(true);

                    //
                    if (currentPosition == 0) {
                        mViewPager.toggleJumiaScroller(false);
                        mViewPager.setCurrentItem(pageCount - 2);

                        //
                    } else if (currentPosition == pageCount - 1) {
                        mViewPager.toggleJumiaScroller(false);
                        mViewPager.setCurrentItem(1);
                    }
                }
            });

            return true;

        }

        /**
         * The system calls this to perform work in the UI thread and delivers the result from
         * doInBackground()
         */
        protected void onPostExecute(Boolean result) {
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStart()
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "ON START");

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "ON RESUME");
        mCompleteProduct = FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        if (mCompleteProduct == null) {
        	getBaseActivity().onBackPressed();
            return;
        }
        Log.i(TAG, "ON RESUME");

        createViewPager();
      ((BaseActivity) getActivity()).findViewById(R.id.totalProducts).setVisibility(View.VISIBLE);
        // updateImage(productImageGalleryFragment.mVariationsListPosition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "ON PAUSE");
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "ON STOP");
    }

    private void createViewPager() {
        if(mCompleteProduct.getImageList().size() <=0 ){
            return;
        }
        if (galleryAdapter != null) {
            imagesList = (ArrayList<String>) mCompleteProduct.getImageList().clone();

            imagesList.add(0, imagesList.get(imagesList.size() - 1));
            imagesList.add(imagesList.get(1));

            galleryAdapter.replaceAll(imagesList);
        } else {
            imagesList = (ArrayList<String>) mCompleteProduct.getImageList().clone();
            imagesList.add(0, imagesList.get(imagesList.size() - 1));
            imagesList.add(imagesList.get(1));

            galleryAdapter = new GalleryPagerAdapter(getActivity(), imagesList, isZoomAvailable);

        }

        if (mViewPager == null) {
            mViewPager = (JumiaViewPagerWithZoom) mainView.findViewById(R.id.viewpager);
            mViewPager.setPageMargin((int) getActivity().getResources().getDimension(
                    R.dimen.margin_large));

        }

        mViewPager.setAdapter(galleryAdapter);

        mViewPager.setCurrentItem(currentPosition);

        if (!showHorizontalListView) {
            final GestureDetector tapGestureDetector = new GestureDetector(getActivity(),
                    new TapGestureListener());
            mViewPager.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    tapGestureDetector.onTouchEvent(event);
                    return false;
                }
            });
        }
        hideImageLoading();

    }

    //
    // private void updateAdapter() {
    // mImageListAdapter.replaceAll(mCompleteProduct.getImageList());
    // createViewPager();
    // hideImageLoading();
    // }

    private void showImageLoading() {
        mProductImageLoading.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
    }

    private void hideImageLoading() {
        mProductImageLoading.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
    }

    private void updateImage(int index) {
        mViewPager.setCurrentItem(index, true);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
        // mPagerWrapper.setCurrentItem(position, true);
        // mViewPager.setCurrentItem(position, true);
    }

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (!isZoomAvailable) {
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
                bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION,
                        currentPosition);
                bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION,
                        mVariationsListPosition);
                bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, true);
                bundle.putBoolean(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, false);
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_GALLERY,
                        bundle, FragmentController.ADD_TO_BACK_STACK);
                
            } else {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }

            return true;
        }
    }


    @Override
    public boolean allowBackPressed() {
        Log.d(TAG, "ALLOW BACK PRESSED");
        if (mCompleteProduct != null) {

            int listPosition;
            if (mImagesList != null)
                listPosition = mImagesList.getPosition();
            else
                listPosition = 0;

            ProductDetailsFragment.updateVariantionListPosition(mCompleteProduct.getUrl(),
                    listPosition);
        }

        return super.allowBackPressed();
    }

    @Override
    public void onPageScrollStateChanged(int position) {
        // noop
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // noop
    }

    @Override
    public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected position = " + position);
        // mImagesList.setSelectedItem(position, HorizontalListView.MOVE_TO_SCROLLED);

        // update current selected viewpager position
        currentPosition = position;
    }

    @Override
    public void notifyFragment(Bundle bundle) {

        // Validate if fragment is on the screen
        if (!isVisible()) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (bundle.containsKey(ProductDetailsActivityFragment.LOADING_PRODUCT)) {
            showImageLoading();
            return;
        }

        productImageGalleryFragment.mCompleteProductUrl = bundle
                .getString(ConstantsIntentExtra.CONTENT_URL);
        productImageGalleryFragment.mVariationsListPosition = bundle.getInt(
                ConstantsIntentExtra.VARIATION_LISTPOSITION, 1);

        productImageGalleryFragment.currentPosition = bundle.getInt(
                ConstantsIntentExtra.CURRENT_LISTPOSITION, 1);
        productImageGalleryFragment.isZoomAvailable = bundle.getBoolean(
                ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);

        mCompleteProduct = (CompleteProduct) FragmentCommunicatorForProduct.getInstance()
                .getCurrentProduct();
        // displayGallery(mCompleteProduct);
        if (mCompleteProduct == null) {
            getActivity().finish();
            return;
        }

//        Log.i(TAG, "code1 notifyFragment" + productImageGalleryFragment.currentPosition);
        mImagesList = (HorizontalListView) mainView.findViewById(R.id.images_list);

        if (!showHorizontalListView) {
            mImagesList.setVisibility(View.GONE);
        }

        createViewPager();
        if (productImageGalleryFragment.currentPosition <= 0)
            productImageGalleryFragment.currentPosition = 1;
        updateImage(productImageGalleryFragment.currentPosition);
    }
}
