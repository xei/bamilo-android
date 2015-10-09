/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.configs;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.database.CategoriesTableHelper;
import com.mobile.newFramework.database.ImageResolutionTableHelper;
import com.mobile.newFramework.database.SectionsTablesHelper;
import com.mobile.newFramework.objects.configs.ApiInformation;
import com.mobile.newFramework.objects.configs.Section;
import com.mobile.newFramework.objects.configs.Sections;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import com.mobile.framework.rest.RestClientSingleton;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetApiInfoHelper extends SuperBaseHelper {
    
    private static String TAG = GetApiInfoHelper.class.getSimpleName();

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getApiInformation);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_API_INFO;
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        // Get api info
        ApiInformationStruct apiInformation = new ApiInformationStruct((ApiInformation) baseResponse.getMetadata().getData());
        baseResponse.getMetadata().setData(apiInformation);

        //TODO move to observable
        // Save mob api version
        JumiaApplication.INSTANCE.setMobApiVersionInfo(apiInformation.getVersionInfo());
        // Get md5 sections
        Sections sections = apiInformation.getSections();
        // Get old sections
        List<Section> oldSections = SectionsTablesHelper.getSections();
        // Get out dated sections
        ArrayList<Section> outDatedSections = checkSections(oldSections, sections);
        // Save all new sections
        SectionsTablesHelper.saveSections(sections);

        // Validate out dated sections
        if (CollectionUtils.isNotEmpty(outDatedSections)) {
            clearOutDatedMainSections(outDatedSections, apiInformation);
        }

    }



//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_API_INFO.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
//        return bundle;
//    }
    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE RESPONSE");
//        // Validate sections
//        JSONArray sessionJSONArray = jsonObject.optJSONArray(RestConstants.JSON_DATA_TAG);
//        ArrayList<Section> outDatedSections = null;
//        if (sessionJSONArray != null) {
//            // Get old sections
//            List<Section> oldSections = SectionsTablesHelper.getSections();
//            // Get new sections
//            List<Section> sections = parseSections(sessionJSONArray);
//            // Get out dated sections
//            outDatedSections = checkSections(oldSections, sections);
//            // Save all new sections
//            SectionsTablesHelper.saveSections(sections);
//        }
//        // Validate out dated sections
//        if (CollectionUtils.isNotEmpty(outDatedSections)) {
//            clearOutDatedMainSections(outDatedSections, bundle);
//        }
//        // VERSION
//        VersionInfo info = new VersionInfo();
//        try {
//            info.initialize(jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JumiaApplication.INSTANCE.setMobApiVersionInfo(info);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, info);
//
//        return bundle;
//    }

    /**
     * Clears the database of outdated sections
     * @param apiInformationStruct
     */
    private void clearOutDatedMainSections(List<Section> sections, ApiInformationStruct apiInformationStruct) {
        Print.d(TAG, "ON CLEAR OUT DATED SECTIONS");
        // Update each outdated section
        for (Section section : sections) {
            // Case teasers
            switch (section.getName()) {
                /*
                // Case zip codes
                case Section.SECTION_NAME_GET_3_HOUR_DELIVERY_ZIPCODES:
                    ZipCodesTableHelper.clearZipCodes(db);
                    break;
                // Case static blocks
                case Section.SECTION_NAME_STATIC_BLOCKS:
                    StaticBlocksTableHelper.clearStaticBlocks(db);
                    break;
                // Case segments
                case Section.SECTION_NAME_SEGMENTS:
                    SegmentTeasersTableHelper.clearSegmentTeasers(db);
                    break;
                // Case brands
                case Section.SECTION_NAME_BRANDS:
                    BrandsTableHelper.clearBrands(db);
                    break;
                 */
                case Section.SECTION_NAME_TEASERS:

                    // TODO: REMOVE FROM NEW FRAMEWORK
                    //RestClientSingleton.getSingleton(JumiaApplication.INSTANCE).removeEntry(section.getUrl());

                    break;
                // Case categories
                case Section.SECTION_NAME_CATEGORIES:
                    CategoriesTableHelper.clearCategories();
                    break;
                // Case image resolutions
                case Section.SECTION_NAME_IMAGE_RESOLUTIONS:
                    ImageResolutionTableHelper.clearImageResolutions();
                    break;
                // Case country configs
                case Section.SECTION_NAME_CONFIGURATIONS:
                    apiInformationStruct.setSectionNameConfigurations(true);
                    break;
            }
        }
    }

    /**
     * Parses the json array containing sections.
     * @param jsonArray The section json array
     * @return The list of section
     */
    private ArrayList<Section> parseSections(JSONArray jsonArray) {
        Print.d(TAG, "ON PARSE SECTIONS");
        int arrayLength = jsonArray.length();
        ArrayList<Section> sections = new ArrayList<>();
        for (int i = 0; i < arrayLength; ++i) {
            JSONObject sessionObject = jsonArray.optJSONObject(i);
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
        Print.i(TAG, "ON CHECK SECTIONS");
        ArrayList<Section> outdatedSections = new ArrayList<>();
        // Case is first time
        if (CollectionUtils.isEmpty(oldSections)) {
            outdatedSections.addAll(newSections);
        }
        // Default case
        else {
            // For each new section
            for (Section newSection : newSections) {
                // Get the saved section
                Section savedSection = getSection(newSection.getName(), oldSections);
                // Case MD5 is different
                if (savedSection != null && !savedSection.getMd5().equals(newSection.getMd5())) {
                    Print.i(TAG, "SECTION IS OUT DATED: " + newSection.getName() + " " + newSection.getMd5());
                    outdatedSections.add(newSection);
                }
                // Case section is not present
                else if (savedSection == null) {
                    Print.i(TAG, "NEW SECTION IS NOT PRESENT: " + newSection.getName() + " " + newSection.getMd5());
                    ArrayList<Section> temp = new ArrayList<>();
                    temp.add(newSection);
                    SectionsTablesHelper.saveSections(temp);
                    outdatedSections.add(newSection);

                }
                // Case section MD5 is the same
                else {
                    Print.i(TAG, "SECTION IS DATED: " + newSection.getName() + " " + newSection.getMd5());
                }
            }
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
    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON PARSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON RESPONSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_API_INFO);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }

    public class ApiInformationStruct extends ApiInformation {
        private boolean sectionNameConfigurations;

        public ApiInformationStruct(){
        }

        public ApiInformationStruct(ApiInformation apiInformation){
            super(apiInformation);
        }


        public boolean isSectionNameConfigurations() {
            return sectionNameConfigurations;
        }

        public void setSectionNameConfigurations(boolean sectionNameConfigurations) {
            this.sectionNameConfigurations = sectionNameConfigurations;
        }
    }
}
