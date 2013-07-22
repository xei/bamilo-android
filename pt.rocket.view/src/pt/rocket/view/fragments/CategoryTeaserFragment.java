/**
 * 
 */
package pt.rocket.view.fragments;

import java.util.EnumSet;

import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.objects.CategoryTeaserGroup;
import pt.rocket.framework.objects.CategoryTeaserGroup.TeaserCategory;
import pt.rocket.framework.objects.ITargeting;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import pt.rocket.view.HomeFragmentActivity;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.akquinet.android.androlog.Log;

/**
 * @author manuelsilva
 * 
 */
public class CategoryTeaserFragment extends BaseFragment {

    private static final String TAG = LogTagHelper.create( CategoryTeaserFragment.class );
    
    private HomeFragmentActivity parentActivity;
    
    private static CategoryTeaserGroup teaserCategoryGroup;
    
    private OnClickListener onTeaserClickListener;

    /**
     * 
     * @param dynamicForm
     * @return
     */
	public static CategoryTeaserFragment getInstance() {
        CategoryTeaserFragment categoryTeaserFragment = new CategoryTeaserFragment();
        return categoryTeaserFragment;
    }
    
    /**
     * Empty constructor
     * @param arrayList 
     */
    public CategoryTeaserFragment() {
        super(EnumSet.noneOf(EventType.class), EnumSet.noneOf(EventType.class));

    }

    @Override
    public void sendValuesToFragment(int identifier, Object values){
        this.teaserCategoryGroup = (CategoryTeaserGroup) values;
    }
    
    @Override
    public void sendListener(int identifier, OnClickListener onTeaserClickListener){
        this.onTeaserClickListener = onTeaserClickListener;
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
        parentActivity = (HomeFragmentActivity) activity;
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
        
        View view = mInflater.inflate(R.layout.teaser_categories_group, viewGroup, false);
        
        ViewGroup container = (ViewGroup) view
                .findViewById(R.id.teaser_group_container);
     
        ((TextView) view.findViewById(R.id.teaser_group_title))
                .setText(teaserCategoryGroup.getTitle());
        container.addView(createCategoryAllTeaserView(container, mInflater));
        for (TeaserCategory category : teaserCategoryGroup.getTeasers()) {
            container
                    .addView(createCategoryTeaserView(category, container, mInflater));
        }
        return view;
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
//        FlurryTracker.get().begin();
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
        //
//        AnalyticsGoogle.get().trackPage(R.string.gcategory_prefix);
        //
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
//        FlurryTracker.get().end();
    }

    @Override
    protected boolean onSuccessEvent(final ResponseResultEvent<?> event) {
        return true;
    }
        
    @Override
    protected boolean onErrorEvent(ResponseEvent event) {
        return false;
    }
    
    
    private View createCategoryAllTeaserView(ViewGroup container, LayoutInflater mInflater) {
        View view = mInflater.inflate(
                R.layout.category_inner_currentcat, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText(getActivity().getString(R.string.categories_toplevel_title));
        view.setOnClickListener(onTeaserClickListener);
        view.setTag(R.id.target_url, null);
        view.setTag(R.id.target_type, ITargeting.TargetType.CATEGORY);
        return view;
    }
    
    private View createCategoryTeaserView(TeaserCategory cat, ViewGroup vg, LayoutInflater mInflater) {
        View categoryTeaserView;
        categoryTeaserView = mInflater.inflate(R.layout.category_inner_childcat, vg, false);
        TextView textView = (TextView) categoryTeaserView.findViewById(R.id.text);
        textView.setText(cat
                .getName());
        attachTeaserListener(cat, categoryTeaserView);
        return categoryTeaserView;
    }
    
    
    private void attachTeaserListener(ITargeting targeting, View view) {
        view.setTag(R.id.target_url, targeting.getTargetUrl());
        view.setTag(R.id.target_type, targeting.getTargetType());
        view.setTag(R.id.target_title, targeting.getTargetTitle());
        view.setOnClickListener(onTeaserClickListener);
    }

}
