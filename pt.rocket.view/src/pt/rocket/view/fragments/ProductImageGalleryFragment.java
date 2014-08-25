/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.ArrayList;
import java.util.EnumSet;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.GalleryPagerAdapter;
import pt.rocket.controllers.fragments.FragmentController;
import pt.rocket.controllers.fragments.FragmentType;
import pt.rocket.framework.objects.CompleteProduct;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.FragmentCommunicatorForProduct;
import pt.rocket.utils.JumiaViewPagerWithZoom;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class ProductImageGalleryFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create(ProductImageGalleryFragment.class);

    private static ProductImageGalleryFragment sProductImageGalleryFragment;

    private JumiaViewPagerWithZoom mViewPager;

    private GalleryPagerAdapter galleryAdapter;

    private RelativeLayout mProductImageLoading;

    private CompleteProduct mCompleteProduct;

    private View mainView;

    private boolean isZoomAvailable = false;

    private int mVariationsListPosition = 1;

    private int currentPosition = 1;
    
    private ArrayList<String> imagesList;

    private View mCloseView;
    
    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductImageGalleryFragment getInstance() {
        sProductImageGalleryFragment = new ProductImageGalleryFragment();
        return sProductImageGalleryFragment;
    }

    /**
     * 
     * @param dynamicForm
     * @return
     */
    public static ProductImageGalleryFragment getInstance(Bundle bundle) {
        sProductImageGalleryFragment = new ProductImageGalleryFragment();
        sProductImageGalleryFragment.mVariationsListPosition = bundle.getInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, 1);
        sProductImageGalleryFragment.currentPosition = bundle.getInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, 1);
        if (sProductImageGalleryFragment.currentPosition <= 0) sProductImageGalleryFragment.currentPosition = 1;
        sProductImageGalleryFragment.isZoomAvailable = bundle.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);
        return sProductImageGalleryFragment;
    }

    /**
     * Empty constructor
     */
    public ProductImageGalleryFragment() {
        super(EnumSet.of(MyMenuItem.SEARCH_VIEW, MyMenuItem.MY_PROFILE),
                NavigationAction.Products,
                0,
                KeyboardState.NO_ADJUST_CONTENT);
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
    }
    
    
    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater mInflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        super.onCreateView(mInflater, viewGroup, savedInstanceState);
        Log.i(TAG, "ON CREATE VIEW");
        return mInflater.inflate(R.layout.product_gallery_fragment, viewGroup, false);
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.view.fragments.BaseFragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(TAG, "ON VIEW CREATED");
        mainView = view;
        mProductImageLoading = (RelativeLayout) view.findViewById(R.id.loading_gallery);
        mViewPager = (JumiaViewPagerWithZoom) view.findViewById(R.id.viewpager);
        mCloseView = view.findViewById(R.id.gallery_button_close);

        // Set close button
        setCloseButton();
        
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

                //int pageCount = galleryAdapter.getCount();

                if (arg0 == ViewPager.SCROLL_STATE_SETTLING) {
                    if (mViewPager != null)
                        mViewPager.setPagingEnabled(false);
                }

                if (arg0 == ViewPager.SCROLL_STATE_IDLE) {
                    //new ChangePageTask().execute(arg0);
                    changePage();
                }

            }
        });
    }
    
    private void changePage() {
        try {
            getBaseActivity().runOnUiThread(new Runnable() {
                public void run() {
                    int pageCount = galleryAdapter.getCount();
                    try {
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
                    } catch (NullPointerException e) {
                        Log.w(TAG, "WARNING NPE IN CHANGE PAGE");
                    }
                }
            });
        } catch (NullPointerException e) {
            Log.w(TAG, "WARNING NPE ON CHANGE PAGE", e);
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
    
    /**
     * Set the close button
     */
    private void setCloseButton() {
        if(mCloseView != null && isZoomAvailable) {
            //mCloseView.setOnClickListener((OnClickListener) this);
            mCloseView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getBaseActivity().onBackPressed();
                }
            });
            mCloseView.setVisibility(View.VISIBLE);
        }
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
            mViewPager.setPageMargin((int) getActivity().getResources().getDimension(R.dimen.margin_large));
        }

        mViewPager.setAdapter(galleryAdapter);

        mViewPager.setCurrentItem(currentPosition);

        final GestureDetector tapGestureDetector = new GestureDetector(getActivity(), new TapGestureListener(mViewPager));
        mViewPager.setOnTouchListener(new OnTouchListener() {
            /**
             * Handle on touch and when user lifts finger from viewPager show normal asset
             */
            public boolean onTouch(View v, MotionEvent event) {
                if (tapGestureDetector.onTouchEvent(event)) {
                    return true;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mViewPager.setSelected(false);
                }

                return false;
            }
        });
            
        hideImageLoading();

    }


    private void showImageLoading() {
        mProductImageLoading.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.GONE);
    }

    private void hideImageLoading() {
        mProductImageLoading.setVisibility(View.GONE);
        mViewPager.setVisibility(View.VISIBLE);
    }

    private void updateImage(int index) {
        if (mViewPager != null && mViewPager.getAdapter() != null && mViewPager.getAdapter().getCount() > 0) {
            mViewPager.setCurrentItem(index, true);
        }
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
//        // mPagerWrapper.setCurrentItem(position, true);
//        // mViewPager.setCurrentItem(position, true);
//    }

    /**
     * GestureListener to respond to tap on viewPager<br>
     * Added setSelected(true) when viewPager is being pressed in order to trigger selector<br>
     * <br>
     * http://stackoverflow.com/questions/2089552/android-how-to-detect-when-a-scroll-has-ended/
     * 3818124#3818124
     * 
     * @modified Andre Lopes
     */
    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {
        View mViewPager;

        public TapGestureListener(View mViewPager) {
            this.mViewPager = mViewPager;
        }

        /**
         * show selected asset
         */
        @Override
        public boolean onDown(MotionEvent e) {
            Log.i(TAG, "onShowPress");
            mViewPager.setSelected(true);

            return false;
        }

        /**
         * show normal asset
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.i(TAG, "onScroll");
            // i'm only scrolling along the X axis
            mViewPager.setSelected(false);

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (!isZoomAvailable) {
                Log.i(TAG, "onSingleTapConfirmed");
                Bundle bundle = new Bundle();
                bundle.putString(ConstantsIntentExtra.CONTENT_URL, mCompleteProduct.getUrl());
                bundle.putInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, currentPosition);
                bundle.putInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, mVariationsListPosition);
                bundle.putBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, true);
                bundle.putBoolean(ConstantsIntentExtra.SHOW_HORIZONTAL_LIST_VIEW, false);
                ((BaseActivity) getActivity()).onSwitchFragment(FragmentType.PRODUCT_GALLERY, bundle, FragmentController.ADD_TO_BACK_STACK);
            } else {
                if (getActivity() != null) getActivity().onBackPressed();
            }

            return true;
        }
    }

//    @Override
//    public void onPageScrollStateChanged(int position) {
//        // noop
//    }
//
//    @Override
//    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        // noop
//    }
//
//    @Override
//    public void onPageSelected(int position) {
//        Log.d(TAG, "onPageSelected position = " + position);
//        // mImagesList.setSelectedItem(position, HorizontalListView.MOVE_TO_SCROLLED);
//        // update current selected viewpager position
//        currentPosition = position;
//    }

    @Override
    public void notifyFragment(Bundle bundle) {

        // Validate if fragment is on the screen
        if (!isVisible()) {
            Log.w(TAG, "RECEIVED CONTENT IN BACKGROUND WAS DISCARDED!");
            return;
        }

        if (bundle.containsKey(ProductDetailsFragment.LOADING_PRODUCT)) {
            showImageLoading();
            return;
        }

        //productImageGalleryFragment.mCompleteProductUrl = bundle.getString(ConstantsIntentExtra.CONTENT_URL);
        mVariationsListPosition = bundle.getInt(ConstantsIntentExtra.VARIATION_LISTPOSITION, 1);

        currentPosition = bundle.getInt(ConstantsIntentExtra.CURRENT_LISTPOSITION, 1);
        isZoomAvailable = bundle.getBoolean(ConstantsIntentExtra.IS_ZOOM_AVAILABLE, false);

        mCompleteProduct = (CompleteProduct) FragmentCommunicatorForProduct.getInstance().getCurrentProduct();
        // displayGallery(mCompleteProduct);
        if (mCompleteProduct == null) {
            Log.e(TAG, "NO COMPLETE PRODUCT - SWITCHING TO HOME");
            restartAllFragments();
            // getActivity().finish();
            return;
        }

        createViewPager();
        if (currentPosition <= 0) currentPosition = 1;
        updateImage(currentPosition);
    }

}
