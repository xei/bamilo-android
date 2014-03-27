package pt.rocket.view.fragments;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

import org.holoeverywhere.widget.Button;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.CatalogPageModel;
import pt.rocket.framework.objects.CatalogFilter;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.JumiaCatalogViewPager;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import pt.rocket.utils.dialogfragments.DialogFilterFragment;
import pt.rocket.view.BaseActivity;
import pt.rocket.view.R;
import android.content.ContentValues;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

public class Catalog extends BaseFragment implements OnClickListener{
    
    private static final String TAG = LogTagHelper.create(Catalog.class);

    private static Catalog mCatalogFragment;
    private CatalogPagerAdaper mCatalogPagerAdapter;
    private JumiaCatalogViewPager mViewPager;
    private PagerTabStrip pagerTabStrip;

    private final int TAB_PREV_ID = 0;
    private final int TAB_CURR_ID = 1;
    private final int TAB_NEXT_ID = 2;

    private final int TAB_INDICATOR_HEIGHT = 0;
    private final int TAB_UNDERLINE_HEIGHT = 1;
    private final int TAB_STRIP_COLOR = android.R.color.transparent;

    
    private static final int PAGE_MIDDLE = 1;
    

    private LayoutInflater mInflater;
    private int mSelectedPageIndex = 1;
    private int mLastSelectedPageIndex = 1;
    // we save each page in a model
    private ArrayList<String> mSortOptions;
    private CatalogPageModel[] mCatalogPageModel;

    public static String productsURL;
    public static String searchQuery;
    public static String navigationPath;
    public static String title;
    public static int navigationSource;
    private int currentPosition = 1;
    private View mFilterButton;
    private ArrayList<CatalogFilter> mCatalogFilter;
    private ContentValues mCatalogFilterValues = new ContentValues();

    public Catalog() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class), EnumSet
                .of(MyMenuItem.SEARCH), NavigationAction.Products, R.string.products);
    }

    public static Catalog getInstance() {
        // if (mProductsViewFragment == null) {
        mCatalogFragment = new Catalog();
        // }
        return mCatalogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSortOptions = new ArrayList<String>(Arrays.asList(getResources()
                .getStringArray(R.array.products_picker)));
        mCatalogPageModel = new CatalogPageModel[mSortOptions.size()];
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        View view = inflater.inflate(R.layout.products_frame, container, false);
        mViewPager = (JumiaCatalogViewPager) view.findViewById(R.id.viewpager_products_list);
        pagerTabStrip = (PagerTabStrip) view.findViewById(R.id.products_list_titles);
        
        // XXX
        mFilterButton = view.findViewById(R.id.products_list_filter_button);
        mFilterButton.setSelected((mCatalogFilterValues.size() == 0) ? false : true);

        // Initialize the catalog model with fresh data
        initPageModel();

        return view;
    }

    // XXX
    public void setFilter(ArrayList<CatalogFilter> filters){
        // Validate    
        if(mFilterButton == null){ Log.w(TAG, "FILTER VIEW IS NULL"); return; }
        // Validate
        if(mCatalogFilter != null) { Log.w(TAG, "DISCARTED: FILTER IS NOT NULL"); return; }
        // Save filters
        mCatalogFilter = filters;
        Log.i(TAG, "SAVED THE FILTER");
        setFilterAction();
    }
    
    
    private void setFilterAction(){
        // Set listener
        mFilterButton.setOnClickListener(null);
        mFilterButton.setOnClickListener(this);
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // Get the view id
        int id = v.getId();
        // Validate the click
        if(id == R.id.products_list_filter_button) {
            Log.d(TAG, "ON CLICK: FILTER BUTTON");
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(DialogFilterFragment.FILTER_TAG, mCatalogFilter);
            DialogFilterFragment newFragment = DialogFilterFragment.newInstance(bundle, this);
            newFragment.show(getBaseActivity().getSupportFragmentManager(), "dialog");
        }
    }
    
    // XXX
    /**
     * 
     * @param filterValues
     */
    public void onSubmitFilterValues(ContentValues filterValues) {
        Log.d(TAG, "FILTER VALUES: " + filterValues.toString());
        mCatalogFilterValues = filterValues;
        mFilterButton.setSelected((mCatalogFilterValues.size() == 0) ? false : true);
        // Update the current view pager
        Log.d(TAG, "FILTER UPDATE PAGEVALUES: " + filterValues.toString());
        getCurrentCatalogPageModel(mSelectedPageIndex).setVariables(productsURL, searchQuery, navigationPath, title, navigationSource, mCatalogFilterValues);
        getCurrentCatalogPageModel(mSelectedPageIndex-1).setVariables(productsURL, searchQuery, navigationPath, title, navigationSource, mCatalogFilterValues);
        getCurrentCatalogPageModel(mSelectedPageIndex+1).setVariables(productsURL, searchQuery, navigationPath, title, navigationSource, mCatalogFilterValues);
    }
    
    
    
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "FILTER ON RESUME");
        
        // http://www.jumia.co.ke:80/mobapi/womens-casual-shoes/
        
        title = getArguments().getString(ConstantsIntentExtra.CONTENT_TITLE);
        
        ((BaseActivity) getActivity()).setTitle(title);
        
        productsURL = getArguments().getString(ConstantsIntentExtra.CONTENT_URL);
        
        searchQuery = getArguments().getString(ConstantsIntentExtra.SEARCH_QUERY);
        
        navigationSource = getArguments().getInt(ConstantsIntentExtra.NAVIGATION_SOURCE, -1);
        
        navigationPath = getArguments().getString(ConstantsIntentExtra.NAVIGATION_PATH);
        
        
//        Log.i(TAG, "code1 title is : " + title);
        Log.i(TAG, "ON RESUME");
        AnalyticsGoogle.get().trackPage(R.string.gproductlist);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mSelectedPageIndex = position;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {

                    if(mSelectedPageIndex < 1 ){
                        Log.i(TAG, "getCurrentCatalogPageModel lower :  mSelectedPageIndex is : "+mSelectedPageIndex+" mLastSelectedPageIndex id : "+mLastSelectedPageIndex);
                     // moving each page content one page to the right
                        updateCatalogPageModelIdexes(1);
                        
                    } else if(mSelectedPageIndex > 1){
                        Log.i(TAG, "getCurrentCatalogPageModel higher :  mSelectedPageIndex is : "+mSelectedPageIndex+" mLastSelectedPageIndex id : "+mLastSelectedPageIndex);
                        updateCatalogPageModelIdexes(-1);
                    }
                    
                } else if(state == ViewPager.SCROLL_STATE_DRAGGING){
                   
                }
            }
            
            
        });

        if (mCatalogPagerAdapter == null) {
            Log.d(TAG, "FILTER: ADAPTER IS NULL");
            mCatalogPagerAdapter = new CatalogPagerAdaper();

        } else {
            mCatalogPagerAdapter.notifyDataSetChanged();
           mCatalogPageModel[0].setTotalItemLable();
        }
        
        mViewPager.setAdapter(mCatalogPagerAdapter);
        mViewPager.setCurrentItem(1);
        try {
            setLayoutSpec();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        
        // XXX Set catalog filters
        if(mCatalogFilter != null) setFilterAction();
        
        AnalyticsGoogle.get().trackPage(R.string.gproductlist);
        getBaseActivity().setProcessShow(true);
        getBaseActivity().showContentContainer();
        
    }


    
    /**
     * Set some layout parameters that aren't possible by xml
     * 
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private void setLayoutSpec() throws NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {
        // Get text
        final TextView currTextView = (TextView) pagerTabStrip.getChildAt(TAB_CURR_ID);
        final TextView nextTextView = (TextView) pagerTabStrip.getChildAt(TAB_NEXT_ID);
        final TextView prevTextView = (TextView) pagerTabStrip.getChildAt(TAB_PREV_ID);

        // Set Color
        currTextView.setPadding(0, 0, 0, 1);

        // Calculate the measures
        final float density = this.getResources().getDisplayMetrics().density;
        int mIndicatorHeight = (int) (TAB_INDICATOR_HEIGHT * density + 0.5f);
        int mFullUnderlineHeight = (int) (TAB_UNDERLINE_HEIGHT * density + 0.5f);

        // Set the indicator height
        Field field;
        field = pagerTabStrip.getClass().getDeclaredField("mIndicatorHeight");
        field.setAccessible(true);
        field.set(pagerTabStrip, mIndicatorHeight);
        // Set the underline height
        field = pagerTabStrip.getClass().getDeclaredField("mFullUnderlineHeight");
        field.setAccessible(true);
        field.set(pagerTabStrip, mFullUnderlineHeight);
        // Set the color of indicator
        Paint paint = new Paint();
        paint.setShader(new LinearGradient(0, 0, 0, mIndicatorHeight, getResources().getColor(
                TAB_STRIP_COLOR), getResources().getColor(
                TAB_STRIP_COLOR), Shader.TileMode.CLAMP));
        field = pagerTabStrip.getClass().getDeclaredField("mTabPaint");
        field.setAccessible(true);
        field.set(pagerTabStrip, paint);

    }

    private void setContent(int index) {
        final CatalogPageModel model = getCurrentCatalogPageModel(index);
        model.setRelativeLayout(model.getRelativeLayout());
    }

    private void initPageModel() {
        for (int i = 0; i < mCatalogPageModel.length; i++) {
            mCatalogPageModel[i] = new CatalogPageModel(i, getBaseActivity(), this);
            mCatalogPageModel[i].setTitle(mSortOptions.get(i));
            
        }
    }
    
    private CatalogPageModel getCurrentCatalogPageModel(int position){
        for(int i = 0; i<mCatalogPageModel.length; i++){
            if(mCatalogPageModel[i].getIndex() == position){
                return mCatalogPageModel[i];
            }
           
        }
        
        return mCatalogPageModel[position];
    }
    
    private void updateCatalogPageModelIdexes(int val){
        for (int i = 0; i < mCatalogPageModel.length; i++) {
            int index = mCatalogPageModel[i].getIndex();
            if(index+val < 0){
                mCatalogPageModel[i].setIndex(4);
            } else if(index+val == 5){
                mCatalogPageModel[i].setIndex(0);    
            } else{
                mCatalogPageModel[i].setIndex(index + val);
            }

            Log.i(TAG, "updateCatalogPageModelIdexes "+mCatalogPageModel[i].getTitle()+" "+mCatalogPageModel[i].getIndex());
        }
//        setContent(PAGE_LEFT);
//        setContent(PAGE_MIDDLE);
//        setContent(PAGE_RIGHT);
        mCatalogPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(PAGE_MIDDLE, false);
    }

    private class CatalogPagerAdaper extends PagerAdapter {
        
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            // we only need three pages
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return getCurrentCatalogPageModel(position).getTitle().toUpperCase();
        }
     
        
        private CatalogPageModel getCurrentCatalogPageModel(int position){
            for(int i = 0; i<mCatalogPageModel.length; i++){
                if(mCatalogPageModel[i].getIndex() == position){
                    return mCatalogPageModel[i];
                }
            }
            
            return mCatalogPageModel[position];
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final CatalogPageModel currentPage = getCurrentCatalogPageModel(position);
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    if(currentPage.getRelativeLayout() == null || ((BaseActivity) getActivity()).isTabletInLandscape(getBaseActivity()) != currentPage.isLandScape() ){
                        RelativeLayout mRelativeLayout = (RelativeLayout) mInflater.inflate(R.layout.products,
                            null);
                        currentPage.setRelativeLayout(mRelativeLayout);
                        currentPage.setTextViewSpnf((org.holoeverywhere.widget.TextView) currentPage.getRelativeLayout().findViewById(R.id.search_products_not_found));
                        currentPage.setButtonRavb((Button) currentPage.getRelativeLayout().findViewById(R.id.retry_alert_view_button));
                        currentPage.setRelativeLayoutPc((RelativeLayout) currentPage.getRelativeLayout().findViewById(R.id.products_content));
                        currentPage.setLinearLayoutLm((LinearLayout) currentPage.getRelativeLayout().findViewById(R.id.loadmore));
                        if(getBaseActivity().isTabletInLandscape(getBaseActivity())){
                            try {
                                currentPage.setGridView((GridView) currentPage.getRelativeLayout().findViewById(R.id.middle_productslist_list));    
                            } catch (ClassCastException e) {
                                currentPage.setListView((ListView) currentPage.getRelativeLayout().findViewById(R.id.middle_productslist_list));    
                            }
                                
                        } else {
                            try {
                                currentPage.setListView((ListView) currentPage.getRelativeLayout().findViewById(R.id.middle_productslist_list));    
                            } catch (ClassCastException e) {
                                currentPage.setGridView((GridView) currentPage.getRelativeLayout().findViewById(R.id.middle_productslist_list));
                            }
                        }
                        
                        
                        Log.d(TAG, "XXXXXX FILTER");
                        currentPage.setLinearLayoutLb((LinearLayout) currentPage.getRelativeLayout().findViewById(R.id.loading_view_pager));
                        currentPage.setRelativeLayoutPt((RelativeLayout) currentPage.getRelativeLayout().findViewById(R.id.products_tip));
                        currentPage.setVariables(productsURL, searchQuery, navigationPath, title, navigationSource, mCatalogFilterValues);
                    }
                    
                }
            }).run();
            
            container.addView(currentPage.getRelativeLayout());
            return currentPage.getRelativeLayout();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == (View) obj;
        }
    }




}
