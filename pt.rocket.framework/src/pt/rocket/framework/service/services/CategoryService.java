/**
 * @author GuilhermeSilva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.service.services;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetCategoriesEvent;
import pt.rocket.framework.objects.Category;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import pt.rocket.framework.utils.LogTagHelper;
import android.net.Uri;
import android.os.AsyncTask;

/**
 * Service responsible for the category service. Responds to the
 * GET_CATEGORIES_EVENT and GET_SUB_CATEGORIES_EVENT
 * 
 * @author GuilhermeSilva
 * @modified ivanschuetz
 */
public class CategoryService extends DarwinService {

	private static final String TAG = LogTagHelper
			.create(CategoryService.class);

	private static final boolean DB_CACHE_ENABLED = true;

	private ArrayList<Category> categories;

	public CategoryService() {
		super(EnumSet.noneOf(EventType.class), EnumSet
				.of(EventType.GET_CATEGORIES_EVENT));
	}

	/**
	 * Gets the categories from the api.
	 * 
	 * @param event
	 * 
	 * @param url
	 * @param onGetCategoriesListener
	 */
	public void getCategories(final GetCategoriesEvent event) {

		if (categories != null) {
			EventManager.getSingleton().triggerResponseEvent(
					new ResponseResultEvent<ArrayList<Category>>(event,
							categories, null, null));

		} else {
			new AsyncTask<Void, Void, ArrayList<Category>>() {
				protected ArrayList<Category> doInBackground(Void... params) {
					if (DB_CACHE_ENABLED) {
						return CategoriesTableHelper.getCategories();
					} else {
						return new ArrayList<Category>();
					}
				}

				protected void onPostExecute(ArrayList<Category> result) {
					onGetCategoriesFromDB(event, result);
				};

			}.execute();
		}
	}

	/**
	 * Callback for get categories from db
	 * 
	 * @param event
	 * @param categories
	 *            . Will be empty if not cached yet
	 */
	private void onGetCategoriesFromDB(final GetCategoriesEvent event,
			ArrayList<Category> categories) {
		if (!categories.isEmpty()) {
			this.categories = categories;
			EventManager.getSingleton().triggerResponseEvent(
					new ResponseResultEvent<ArrayList<Category>>(event,
							categories, null, null));

		} else {

			String action = event.value != null ? event.value
					: event.eventType.action;

			RestServiceHelper.requestGet(Uri.parse(action),
					new ResponseReceiver<ArrayList<Category>>(event) {

						@Override
						public ArrayList<Category> parseResponse(
								JSONObject metadataObject) throws JSONException {
							JSONArray categoriesArray = metadataObject
									.getJSONArray(RestConstants.JSON_DATA_TAG);
							int categoriesArrayLenght = categoriesArray
									.length();
							ArrayList<Category> categories = new ArrayList<Category>();
							for (int i = 0; i < categoriesArrayLenght; ++i) {
								JSONObject categoryObject = categoriesArray
										.getJSONObject(i);
								Category category = new Category();
								category.initialize(categoryObject);
								categories.add(category);
							}

							CategoriesTableHelper.saveCategories(categories);
							return categories;
						}
					}, event.metaData);
		}
	}

	/**
	 * Interface when the services finishes getting the categories.
	 * 
	 * @author GuilhermeSilva
	 */
	public interface OnGetCategoriesListener {
		/**
		 * Update called after getting the categories.
		 * 
		 * @param categories
		 */
		public void onGetCategories(ArrayList<Category> categories);
	}

	// private void sendCategoryResult(RequestEvent event) {
	// EventManager.getSingleton().triggerResponseEvent(
	// new ResponseResultEvent<List<Category>>(event, categories, null));
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework
	 * .event.IEvent)
	 */
	@Override
	public void handleEvent(RequestEvent event) {
		if (event.getType() == EventType.GET_CATEGORIES_EVENT) {
			getCategories((GetCategoriesEvent) event);
		}
	}

}
