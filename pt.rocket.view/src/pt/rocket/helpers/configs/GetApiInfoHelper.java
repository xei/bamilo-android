/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.helpers.configs;

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
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetApiInfoHelper extends BaseHelper {
    
    private static String TAG = GetApiInfoHelper.class.getSimpleName();
    
    public static final String API_INFO_OUTDATEDSECTIONS = "outDatedSections";

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
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
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "ON PARSE RESPONSE");
        JSONArray sessionJSONArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
        ArrayList<Section> outDatedSections = null;
        if (sessionJSONArray != null) {
            List<Section> oldSections = SectionsTablesHelper.getSections();
            List<Section> sections = parseSections(sessionJSONArray);
            outDatedSections = checkSections(oldSections,sections);
            SectionsTablesHelper.saveSections(sections);
        }
        
        // VERSION
        VersionInfo info = new VersionInfo();
        try {
            info.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        if (outDatedSections != null && outDatedSections.size() != 0) {
            clearOutDatedMainSections(outDatedSections, bundle);
        }
        
        JumiaApplication.INSTANCE.setVersionInfo(info);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, info);
        // bundle.putParcelableArrayList(API_INFO_OUTDATEDSECTIONS, outDatedSections);
        
        return bundle;
    }

    /**
     * Clears the database of outdated sections
     * @param bundle 
     */
    private void clearOutDatedMainSections(List<Section> sections, Bundle bundle) {
        Log.d(TAG, "ON CLEAR OUT DATED SECTIONS");

        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();
        Set<EventType> eventsToAwait = EnumSet.noneOf(EventType.class);

        for (Section section : sections) {
            // Case teasers
            if (section.getName().equals(Section.SECTION_NAME_TEASERS)) {
                 // IntroTeasersTableHelper.clearTeasers(db);
            }
            // Case brands
            else if (section.getName().equals(Section.SECTION_NAME_BRANDS)) {
                 // BrandsTableHelper.clearBrands(db);
            }
            // Case categories
            else if (section.getName().equals(Section.SECTION_NAME_CATEGORIES)) {
                CategoriesTableHelper.clearCategories(db);
                eventsToAwait.add(EventType.GET_CATEGORIES_EVENT);
            }
            // Case segments
            else if (section.getName().equals(Section.SECTION_NAME_SEGMENTS)) {
                // SegmentTeasersTableHelper.clearSegmentTeasers(db);
            }
            // Case static blocks
            else if (section.getName().equals(Section.SECTION_NAME_STATIC_BLOCKS)) {
                //StaticBlocksTableHelper.clearStaticBlocks(db);
                //eventsToAwait.add(EventType.GET_STATIC_BLOCKS_EVENT);
            }
            // Case image resolutions
            else if (section.getName().equals(Section.SECTION_NAME_IMAGE_RESOLUTIONS)) {
                ImageResolutionTableHelper.clearImageResolutions(db);
                eventsToAwait.add(EventType.GET_RESOLUTIONS);
            }
            // Case zip codes
            else if (section.getName().equals(Section.SECTION_NAME_GET_3_HOUR_DELIVERY_ZIPCODES)) {
                // ZipCodesTableHelper.clearZipCodes(db);
            }
            // Case country configs
            else if(section.getName().equals(Section.SECTION_NAME_COUNTRY_CONFIGS)) {
                bundle.putBoolean(Section.SECTION_NAME_COUNTRY_CONFIGS, true);
            }
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
        Log.d(TAG, "ON PARSE SECTIONS");
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
    public ArrayList<Section> checkSections(List<Section> oldSections, List<Section> newSections) {
        Log.d(TAG, "ON CHECK SECTIONS");
        ArrayList<Section> outdatedSections = new ArrayList<Section>();
//        if (!oldSections.isEmpty()) {
//            for (Section oldSection : oldSections) {
//                Log.d(TAG, "OLD SECTION: " + oldSection.getName() + " " + oldSection.getMd5());
//                Section newSection = getSection(oldSection.getName(), newSections);
//                // Case MD5 is different
//                if (newSection != null && !oldSection.getMd5().equals(newSection.getMd5())) {
//                    outdatedSections.add(newSection);
//                }
//            }

        // 
        if (!oldSections.isEmpty()) {
            // For each new section
            for (Section newSection : newSections) {
                // Get the saved section
                Section savedSection = getSection(newSection.getName(), oldSections);
                // Case MD5 is different
                if (savedSection != null && !savedSection.getMd5().equals(newSection.getMd5())) {
                    Log.d(TAG, "SECTION IS OUT DATED: " + newSection.getName() + " " + newSection.getMd5());
                    outdatedSections.add(newSection);
                // Case section is not present    
                } else if (savedSection == null){
                    Log.d(TAG, "NEW SECTION IS NOT PRESENT: " + newSection.getName() + " " + newSection.getMd5());
                    ArrayList<Section> temp = new ArrayList<Section>();
                    temp.add(newSection);
                    SectionsTablesHelper.saveSections(temp);
                    outdatedSections.add(newSection);
                // Case section MD5 is the same
                } else {
                    Log.d(TAG, "SECTION IS DATED: " + newSection.getName() + " " + newSection.getMd5());
                }
            }
        } else {
            outdatedSections.addAll(newSections);
        }
        
        for (Section section : outdatedSections) {
            Log.d(TAG, "OUT DATED SECTIONS: " + section.getName());
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
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON RESPONSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
