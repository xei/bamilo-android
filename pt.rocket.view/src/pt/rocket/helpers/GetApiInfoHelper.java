/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.database.SectionsTablesHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Section;
import pt.rocket.framework.objects.VersionInfo;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;

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
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log.d("TRACK", "parseResponseBundle GetApiInfoHelper");
        
        JSONArray sessionJSONArray = jsonObject
                .optJSONArray(RestConstants.JSON_DATA_TAG);
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
        
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, info);
        bundle.putParcelableArrayList(API_INFO_OUTDATEDSECTIONS, outDatedSections);

        return bundle;
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
    	android.util.Log.d("TRACK", "parseErrorBundle GetApiInfoHelper");
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetCategories");
        return bundle;
    }
}
