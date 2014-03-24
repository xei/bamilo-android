/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.database.CategoriesTableHelper;
import pt.rocket.framework.database.DarwinDatabaseHelper;
import pt.rocket.framework.database.ImageResolutionTableHelper;
import pt.rocket.framework.database.SectionsTablesHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.objects.Section;
import pt.rocket.framework.objects.VersionInfo;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetApiInfoHelper extends BaseHelper {
    
    private static String TAG = GetApiInfoHelper.class.getSimpleName();
    
    public static final String API_INFO_OUTDATEDSECTIONS = "outDatedSections";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_API_INFO.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        android.util.Log.i(TAG, "code1 parseResponseBundle GetApiInfoHelper "+ jsonObject.toString());
        
        JSONArray sessionJSONArray = jsonObject
                .optJSONArray(RestConstants.JSON_DATA_TAG);
//        android.util.Log.i(TAG, "code1  GetApiInfoHelper "+ sessionJSONArray.toString());
        ArrayList<Section> outDatedSections = null;
        if (sessionJSONArray != null) {
            List<Section> oldSections = SectionsTablesHelper
                    .getSections();
            List<Section> sections = parseSections(sessionJSONArray);

            outDatedSections = checkSections(oldSections,
                    sections);

            SectionsTablesHelper.saveSections(sections);
        }
        
        VersionInfo info = new VersionInfo();
        try {
            info.initialize(jsonObject);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        if (outDatedSections != null
                && outDatedSections.size() != 0) {
            clearOutDatedMainSections(outDatedSections, EventType.GET_API_INFO);
        }
        JumiaApplication.INSTANCE.setVersionInfo(info);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, info);
        bundle.putParcelableArrayList(API_INFO_OUTDATEDSECTIONS, outDatedSections);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.GET_API_INFO);
//        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }

    /**
     * Clears the database of outdated sections
     */
    private void clearOutDatedMainSections(List<Section> sections,
            final EventType event) {

        SQLiteDatabase db = DarwinDatabaseHelper.getInstance()
                .getReadableDatabase();

        final Set<EventType> eventsToAwait = EnumSet.noneOf(EventType.class);

        // Log.d(TAG, "#Sections = " + sections.size());

        for (Section section : sections) {
//            Log.i(TAG, "code1md5 outdated section : "+section.getName());
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
    public ArrayList<Section> checkSections(List<Section> oldSections,
            List<Section> newSections) {
        ArrayList<Section> outdatedSections = new ArrayList<Section>();

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
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetTeasersHelper");
     
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
