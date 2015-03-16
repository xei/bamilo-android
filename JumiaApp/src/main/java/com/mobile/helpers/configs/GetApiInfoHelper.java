/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.configs;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.database.CategoriesTableHelper;
import com.mobile.framework.database.DarwinDatabaseHelper;
import com.mobile.framework.database.ImageResolutionTableHelper;
import com.mobile.framework.database.SectionsTablesHelper;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.interfaces.IMetaData;
import com.mobile.framework.objects.Section;
import com.mobile.framework.objects.VersionInfo;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    
    private static final EventType EVENT_TYPE = EventType.GET_API_INFO;
    
    public static final String API_INFO_OUTDATEDSECTIONS = "outDatedSections";

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_API_INFO.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
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
        
        if (outDatedSections != null && outDatedSections.size() != 0) {
            clearOutDatedMainSections(outDatedSections, bundle);
        }

        
        // VERSION
        VersionInfo info = new VersionInfo();
        try {
            info.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        /**
         * FIXME : Created a new new method. Needs more tests.
         * @author sergiopereira
         */ 
//      JSONArray sessionJSONArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
//      // Get new sections
//      List<Section> sections = new ArrayList<Section>();
//      if (sessionJSONArray != null) sections = parseSections(sessionJSONArray);
//      // Get old sections
//      List<Section> oldSections = SectionsTablesHelper.getSections();
//      // Get out dated sections
//      ArrayList<Section> outDatedSections = checkSections(oldSections, sections);
//      
//      // Validate sections out of dated
//      if (CollectionUtils.isEmpty(oldSections)) {
//          Log.i(TAG, "SECTIONS: EMPTY");
//          SectionsTablesHelper.saveSections(sections);
//      } else if (CollectionUtils.isNotEmpty(outDatedSections)) {
//          Log.i(TAG, "SECTIONS: OUT DATED");
//          clearOutDatedMainSections(outDatedSections, bundle);
//      } else {
//          Log.i(TAG, "SECTIONS: DATED");
//      }
        
        JumiaApplication.INSTANCE.setMobApiVersionInfo(info);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, info);
        
        return bundle;
    }

    /**
     * Clears the database of outdated sections
     * @param bundle 
     */
    private void clearOutDatedMainSections(List<Section> sections, Bundle bundle) {
        Log.d(TAG, "ON CLEAR OUT DATED SECTIONS");

        SQLiteDatabase db = DarwinDatabaseHelper.getInstance().getReadableDatabase();

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
            }
            // Case segments
            else if (section.getName().equals(Section.SECTION_NAME_SEGMENTS)) {
                // SegmentTeasersTableHelper.clearSegmentTeasers(db);
            }
            // Case static blocks
            else if (section.getName().equals(Section.SECTION_NAME_STATIC_BLOCKS)) {
                //StaticBlocksTableHelper.clearStaticBlocks(db);
            }
            // Case image resolutions
            else if (section.getName().equals(Section.SECTION_NAME_IMAGE_RESOLUTIONS)) {
                ImageResolutionTableHelper.clearImageResolutions(db);
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
        
        /**
         * Used with new method
         * @author sergiopereira
         */ 
        //else outdatedSections.addAll(newSections);
        //for (Section section : outdatedSections) {
        //    Log.d(TAG, "OUT DATED SECTIONS: " + section.getName());
        //}

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
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
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
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON RESPONSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
