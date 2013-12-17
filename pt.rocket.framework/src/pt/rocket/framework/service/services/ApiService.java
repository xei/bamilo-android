/**
 * @author Michael Kroez
 * 
 * 
 * 2013/05/14
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package pt.rocket.framework.service.services;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.database.DarwinDatabaseHelper;
import pt.rocket.framework.database.ImageResolutionTableHelper;
import pt.rocket.framework.database.SectionsTablesHelper;
import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseErrorEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.event.ResponseResultEvent;
import pt.rocket.framework.event.events.GetCategoriesEvent;
import pt.rocket.framework.event.events.GetResolutionsEvent;
import pt.rocket.framework.objects.Section;
import pt.rocket.framework.objects.VersionInfo;
import pt.rocket.framework.rest.ResponseReceiver;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.rest.RestServiceHelper;
import pt.rocket.framework.service.DarwinService;
import pt.rocket.framework.utils.LogTagHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Service responsible for requesting the server api version.
 * 
 * @author Michael Kroez
 */
public class ApiService extends DarwinService {

	private static final String TAG = LogTagHelper.create(ApiService.class);

	private VersionInfo mVersionInfo;

	public ApiService() {
		super(EnumSet.noneOf(EventType.class), EnumSet.of(EventType.INIT_SHOP,
				EventType.GET_API_INFO));
		mVersionInfo = new VersionInfo();
	}

	@Override
	public void onInit(Context context) {
		super.onInit(context);
	}

	/**
	 * Gets the categories from the api.
	 * 
	 * @param event
	 * 
	 * @param url
	 * @param onGetCategoriesListener
	 */
	private void getVersionFromApi(final RequestEvent event) {
		/*
		 * Version mVersion = new Version(10068, 10070); mVersionInfo.addEntry(
		 * Darwin.getContext().getPackageName(), mVersion );
		 * EventManager.getSingleton().triggerResponseEvent(new
		 * ResponseResultEvent<VersionInfo>(event, mVersionInfo, ""));
		 */
		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
				new ResponseReceiver<VersionInfo>(event) {
					List<Section> outDatedSections;

					@Override
					public VersionInfo parseResponse(JSONObject metadataObject)
							throws JSONException {
						// Log.i(TAG, "code1 VersionInfo");
						// VersionInfo info = new VersionInfo();
						// info.initialize(metadataObject);
						// mVersionInfo = info;
						// Log.i(TAG,
						// "code1 VersionInfo is: "+mVersionInfo.toString());
						// return mVersionInfo;

						JSONArray sessionJSONArray = metadataObject
								.optJSONArray(RestConstants.JSON_DATA_TAG);
						if (sessionJSONArray != null) {
							List<Section> oldSections = SectionsTablesHelper
									.getSections();
							List<Section> sections = parseSections(sessionJSONArray);

							outDatedSections = checkSections(oldSections,
									sections);

							SectionsTablesHelper.saveSections(sections);
						}
						VersionInfo info = new VersionInfo();
						info.initialize(metadataObject);
						mVersionInfo = info;

						if (outDatedSections != null
								&& outDatedSections.size() != 0) {
							ignoreTrigger = true;
							clearOutDatedMainSections(outDatedSections, event);
						}
						return mVersionInfo;
					}
				}, event.metaData);
	}

	/**
	 * Parses the json array containing the
	 * 
	 * @param sessionJSONArray
	 * @return
	 */
	private ArrayList<Section> parseSections(JSONArray sessionJSONArray) {
		int arrayLength = sessionJSONArray.length();
		ArrayList<Section> sections = new ArrayList<Section>();

		for (int i = 0; i < arrayLength; ++i) {
			JSONObject sessionObject = sessionJSONArray.optJSONObject(i);

			Section section = new Section();
			section.initialize(sessionObject);
			sections.add(section);
		}
		return sections;
	}

	/**
	 * Checks the sections and returns a list of sections that need to be
	 * updated
	 * 
	 * @param oldSections
	 * @param newSections
	 * @return
	 */
	public List<Section> checkSections(List<Section> oldSections,
			List<Section> newSections) {
		List<Section> outdatedSections = new ArrayList<Section>();

		if (!oldSections.isEmpty()) {
			for (Section oldSection : oldSections) {
				Section newSection = getSection(oldSection.getName(),
						newSections);
				if (newSection != null
						&& !oldSection.getMd5().equals(newSection.getMd5())) {
					outdatedSections.add(newSection);
				}
			}
		} else {
			outdatedSections.addAll(newSections);
		}

		return outdatedSections;
	}

	/**
	 * Clears the database of outdated sections
	 */
	private void clearOutDatedMainSections(List<Section> sections,
			final RequestEvent event) {

		SQLiteDatabase db = DarwinDatabaseHelper.getInstance()
				.getReadableDatabase();

		final Set<EventType> eventsToAwait = EnumSet.noneOf(EventType.class);

		// Log.d(TAG, "#Sections = " + sections.size());

		for (Section section : sections) {
			// Log.d(TAG, "Going to clear db for " + section.getName());
			// if (section.getName().equals(Section.SECTION_NAME_TEASERS)) {
			// IntroTeasersTableHelper.clearTeasers(db);
			// }

			// if (section.getName().equals(Section.SECTION_NAME_BRANDS)) {
			// BrandsTableHelper.clearBrands(db);
			// }

			if (section.getName().equals(Section.SECTION_NAME_CATEGORIES)) {
				CategoriesTableHelper.clearCategories(db);
				eventsToAwait.add(EventType.GET_CATEGORIES_EVENT);
			}

			// if (section.getName().equals(Section.SECTION_NAME_SEGMENTS)) {
			// SegmentTeasersTableHelper.clearSegmentTeasers(db);
			// }

			// if (section.getName().equals(Section.SECTION_NAME_STATIC_BLOCKS))
			// {
			// StaticBlocksTableHelper.clearStaticBlocks(db);
			// eventsToAwait.add(EventType.GET_STATIC_BLOCKS_EVENT);
			// }

			if (section.getName()
					.equals(Section.SECTION_NAME_IMAGE_RESOLUTIONS)) {
				ImageResolutionTableHelper.clearImageResolutions(db);
				eventsToAwait.add(EventType.GET_RESOLUTIONS);
			}
			//
			// if (section.getName().equals(
			// Section.SECTION_NAME_GET_3_HOUR_DELIVERY_ZIPCODES)) {
			// ZipCodesTableHelper.clearZipCodes(db);
			// }
		}

		Log.d(TAG, "Events to watch " + eventsToAwait.toString());

		EventWatcher watcher = new EventWatcher(eventsToAwait) {

			@Override
			protected void onCompete(ErrorCode errorCode) {

				Log.d(TAG,
						"Going to send with error code " + errorCode.toString());
				if (errorCode == ErrorCode.NO_ERROR) {
					EventManager.getSingleton().triggerResponseEvent(
							new ResponseResultEvent<VersionInfo>(event,
									mVersionInfo, null, null));
				} else {
					EventManager.getSingleton().triggerResponseEvent(
							new ResponseErrorEvent(event, errorCode));
				}
			}
		};

		EventManager.getSingleton().addResponseListener(watcher, eventsToAwait);

		// Trigger the events
		for (Section section : sections) {
			if (section.getName().equals(Section.SECTION_NAME_CATEGORIES)) {
				EventManager.getSingleton().triggerRequestEvent(
						new GetCategoriesEvent(null));
			}

			// if (section.getName().equals(Section.SECTION_NAME_STATIC_BLOCKS))
			// {
			// EventManager.getSingleton().triggerRequestEvent(
			// new RequestEvent(EventType.GET_STATIC_BLOCKS_EVENT));
			// }

			if (section.getName()
					.equals(Section.SECTION_NAME_IMAGE_RESOLUTIONS)) {
				EventManager.getSingleton().triggerRequestEvent(
						new GetResolutionsEvent());
			}
		}
	}
	
	/**
	 * Returns the section of a given name or null if no section is found
	 * 
	 * @param sectionName
	 * @param sections
	 * @return
	 */
	private Section getSection(String sectionName, List<Section> sections) {
		for (Section section : sections) {
			if (sectionName.equals(section.getName())) {
				return section;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework
	 * .event.IEvent)
	 */
	@Override
	public void handleEvent(RequestEvent event) {
		if (event.getType() == EventType.GET_API_INFO) {
			getVersionFromApi(event);
		} else if (event.getType() == EventType.INIT_SHOP) {
			mVersionInfo = new VersionInfo();
		}
	}

	public VersionInfo getVersionInfo() {
		return mVersionInfo;
	}

	/**
	 * class used to await for events
	 * 
	 * @author Guilherme Silva
	 * 
	 */
	private abstract static class EventWatcher implements ResponseListener {

		private Set<EventType> eventsToAwait;
		private ErrorCode errorCode = ErrorCode.NO_ERROR;
		private boolean finalizing = false;

		private EventWatcher(Set<EventType> eventsToAwait) {
			this.eventsToAwait = eventsToAwait;

			if (eventsToAwait.size() == 0) {
				onCompete(errorCode);
			}
		}

		@Override
		public void handleEvent(ResponseEvent event) {
			if (!event.getSuccess()) {
				errorCode = event.errorCode;
				Log.e(TAG, "There was an error in event "
						+ event.getType().toString());
			}

			eventsToAwait.remove(event.getType());

			// Log.d(TAG, "Still Awaiting for " + eventsToAwait.toString());
			if (eventsToAwait.isEmpty() && !finalizing) {
				EventManager.getSingleton().removeResponseListener(this);
				onCompete(errorCode);
				finalizing = true;
			}
		}

		protected abstract void onCompete(ErrorCode errorCode);

		@Override
		public boolean removeAfterHandlingEvent() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getMD5Hash() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
