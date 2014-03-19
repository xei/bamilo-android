///**
// * @author Manuel Silva
// * 
// * 
// * 2014/01/08
// * 
// * Copyright (c) Rocket Internet All Rights Reserved
// */
//package pt.rocket.helpers;
//
//import java.util.ArrayList;
//import java.util.EnumSet;
//import java.util.List;
//import java.util.Set;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.shouldit.proxy.lib.APLConstants;
//
//import pt.rocket.framework.ErrorCode;
//import pt.rocket.framework.database.CategoriesTableHelper;
//import pt.rocket.framework.database.DarwinDatabaseHelper;
//import pt.rocket.framework.database.ImageResolutionTableHelper;
//import pt.rocket.framework.database.SectionsTablesHelper;
//import pt.rocket.framework.objects.Section;
//import pt.rocket.framework.objects.VersionInfo;
//import pt.rocket.framework.rest.RestConstants;
//import pt.rocket.framework.utils.Constants;
//import pt.rocket.framework.utils.EventType;
//import pt.rocket.framework.utils.LogTagHelper;
//import pt.rocket.interfaces.IResponseCallback;
//import pt.rocket.utils.JumiaApplication;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Parcelable;
//import android.util.Log;
//
///**
// * Helper responsible for requesting the server api version.
// * 
// * @author Manuel Silva
// */
//public class ApiService {
//
//	private static final String TAG = LogTagHelper.create(ApiService.class);
//
//	private VersionInfo mVersionInfo;
//
//	public ApiService() {
////		super(EnumSet.noneOf(EventType.class), EnumSet.of(EventType.INIT_SHOP,
////				EventType.GET_API_INFO));
//		mVersionInfo = new VersionInfo();
//	}
//
////	@Override
////	public void onInit(Context context) {
////		super.onInit(context);
////	}
//
//	/**
//	 * Gets the categories from the api.
//	 * 
//	 * @param event
//	 * 
//	 * @param url
//	 * @param onGetCategoriesListener
//	 */
//	private void getVersionFromApi(final EventType event) {
//		/*
//		 * Version mVersion = new Version(10068, 10070); mVersionInfo.addEntry(
//		 * Darwin.getContext().getPackageName(), mVersion );
//		 * EventManager.getSingleton().triggerResponseEvent(new
//		 * ResponseResultEvent<VersionInfo>(event, mVersionInfo, ""));
//		 */
//	    JumiaApplication.INSTANCE.sendRequest(new GetApiInfoHelper(), null, new IResponseCallback() {
//            
//            @Override
//            public void onRequestError(Bundle bundle) {
//                // TODO Auto-generated method stub
//                
//            }
//            
//            @Override
//            public void onRequestComplete(Bundle bundle) {
//                mVersionInfo = bundle.getParcelable(Constants.BUNDLE_RESPONSE_KEY);
//                ArrayList<Section> outDatedSections = bundle.getParcelableArrayList(GetApiInfoHelper.API_INFO_OUTDATEDSECTIONS);
//                if (outDatedSections != null
//                        && outDatedSections.size() != 0) {
//                    clearOutDatedMainSections(outDatedSections, event);
//                }
//                
//            }
//        });
//	    
//	    
////		RestServiceHelper.requestGet(Uri.parse(event.eventType.action),
////				new ResponseReceiver<VersionInfo>(event) {
////					List<Section> outDatedSections;
////
////					@Override
////					public VersionInfo parseResponse(JSONObject metadataObject)
////							throws JSONException {
////						// Log.i(TAG, "code1 VersionInfo");
////						// VersionInfo info = new VersionInfo();
////						// info.initialize(metadataObject);
////						// mVersionInfo = info;
////						// Log.i(TAG,
////						// "code1 VersionInfo is: "+mVersionInfo.toString());
////						// return mVersionInfo;
////
////						JSONArray sessionJSONArray = metadataObject
////								.optJSONArray(RestConstants.JSON_DATA_TAG);
////						if (sessionJSONArray != null) {
////							List<Section> oldSections = SectionsTablesHelper
////									.getSections();
////							List<Section> sections = parseSections(sessionJSONArray);
////
////							outDatedSections = checkSections(oldSections,
////									sections);
////
////							SectionsTablesHelper.saveSections(sections);
////						}
////						VersionInfo info = new VersionInfo();
////						info.initialize(metadataObject);
////						mVersionInfo = info;
////
////						if (outDatedSections != null
////								&& outDatedSections.size() != 0) {
////							ignoreTrigger = true;
////							clearOutDatedMainSections(outDatedSections, event);
////						}
////						return mVersionInfo;
////					}
////				}, event.metaData);
//	}
//
//
//
//
//	
//
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * pt.rocket.framework.event.EventListener#handleEvent(pt.rocket.framework
//	 * .event.IEvent)
//	 */
//	
//	public void handleEvent(EventType event) {
//		if (event == EventType.GET_API_INFO) {
//			getVersionFromApi(event);
//		} else if (event == EventType.INIT_SHOP) {
//			mVersionInfo = new VersionInfo();
//		}
//	}
//
//	public VersionInfo getVersionInfo() {
//		return mVersionInfo;
//	}
//
//	/**
//	 * class used to await for events
//	 * 
//	 * @author Guilherme Silva
//	 * 
//	 */
//	private abstract static class EventWatcher implements ResponseListener {
//
//		private Set<EventType> eventsToAwait;
//		private ErrorCode errorCode = ErrorCode.NO_ERROR;
//		private boolean finalizing = false;
//
//		private EventWatcher(Set<EventType> eventsToAwait) {
//			this.eventsToAwait = eventsToAwait;
//
//			if (eventsToAwait.size() == 0) {
//				onCompete(errorCode);
//			}
//		}
//
//		@Override
//		public void handleEvent(ResponseEvent event) {
//			if (!event.getSuccess()) {
//				errorCode = event.errorCode;
//				Log.e(TAG, "There was an error in event "
//						+ event.getType().toString());
//			}
//
//			eventsToAwait.remove(event.getType());
//
//			// Log.d(TAG, "Still Awaiting for " + eventsToAwait.toString());
//			if (eventsToAwait.isEmpty() && !finalizing) {
//				EventManager.getSingleton().removeResponseListener(this);
//				onCompete(errorCode);
//				finalizing = true;
//			}
//		}
//
//		protected abstract void onCompete(ErrorCode errorCode);
//
//		@Override
//		public boolean removeAfterHandlingEvent() {
//			// TODO Auto-generated method stub
//			return false;
//		}
//
//		@Override
//		public String getMD5Hash() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//	}
//
//}
