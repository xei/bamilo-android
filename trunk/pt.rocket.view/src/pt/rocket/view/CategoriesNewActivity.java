package pt.rocket.view;

import java.util.EnumSet;
import java.util.List;

import pt.rocket.constants.ConstantsIntentExtra;
import pt.rocket.controllers.ActivitiesWorkFlow;
import pt.rocket.controllers.CategoriesAdapter;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetCategoriesEvent;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.utils.DialogGeneric;
import pt.rocket.utils.MyActivity;
import pt.rocket.utils.MyMenuItem;
import pt.rocket.utils.NavigationAction;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import de.akquinet.android.androlog.Log;

/**
 * 
 * <p>This class is used when the application wants to display the toplevel categories</p>
 * 
 * <p>Copyright (C) 2013 Rocket Internet - All Rights Reserved</p>
 * <p/>
 * <p>Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.</p>
 * 
 * @project WhiteLabelRocket
 * 
 * @version 1.00
 * 
 * @author michaelkroez
 * 
 * 
 * @date 3/14/2013
 * 
 * @description This Class shows all categories.
 * 
 */
public class CategoriesNewActivity extends MyActivity implements OnItemClickListener {

	private final String TAG = LogTagHelper.create( CategoriesNewActivity.class );

    public List<Category> categories;

    private CategoriesAdapter catAdapter;

    private ListView categoriesList;

    private long beginRequestMillis;
	
	/**
	 * 
	 */
    public CategoriesNewActivity() {
        super(NavigationAction.Categories, EnumSet.of(MyMenuItem.SEARCH), EnumSet
                .of(EventType.GET_CATEGORIES_EVENT), EnumSet.noneOf(EventType.class),
                R.string.categories_title, R.layout.categories);
    }
		
	/*
	 * (non-Javadoc)
	 * @see pt.rocket.utils.MyActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    init( intent );
	}
	
	private void init(Intent intent) {
	    String categoryUrl = intent.getStringExtra(ConstantsIntentExtra.CONTENT_URL);
	    beginRequestMillis = System.currentTimeMillis();
        triggerContentEvent(new GetCategoriesEvent(categoryUrl));

	}
	
	@Override
	public void onResume() {
		super.onResume();
		AnalyticsGoogle.get().trackPage(R.string.gcategories);
	}

    /**
     * Creates the list with all the categories
     */
    private void createList() {
        categoriesList = (ListView) findViewById(R.id.categories_grid);
        catAdapter = new CategoriesAdapter(this, categories);
        categoriesList.setAdapter(catAdapter);
        categoriesList.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Category category = categories.get(position);
        if (!category.getHasChildren()) {
            ActivitiesWorkFlow.productsActivity(this, category.getApiUrl(), category.getName(),
                    null, R.string.gcategory_prefix, category.getCategoryPath());
        } else {
            ActivitiesWorkFlow.categorySubLisActivity(this, position);
        }
    }

    /* (non-Javadoc)
     * @see pt.rocket.utils.MyActivity#handleTriggeredEvent(pt.rocket.framework.event.ResponseEvent)
     */
    @Override
    protected boolean onSuccessEvent(ResponseResultEvent<?> event) {
        AnalyticsGoogle.get().trackLoadTiming(R.string.gcategories, beginRequestMillis);
        categories = (List<Category>) event.result;
        // Update accordion
        Log.d(TAG, "handleEvent: categories size = " + categories.size());
        createList();
        return true;
    }

}
