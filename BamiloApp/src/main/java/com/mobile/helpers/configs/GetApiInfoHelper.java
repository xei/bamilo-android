/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.configs;

import com.mobile.app.BamiloApplication;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Get Product Information helper
 * 
 * @author Manuel Silva
 * @modified sergiopereira
 * 
 */
public class GetApiInfoHelper extends SuperBaseHelper {
    
    private static final String TAG = GetApiInfoHelper.class.getSimpleName();

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
        BamiloApplication.INSTANCE.setMobApiVersionInfo(apiInformation.getVersionInfo());
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

    /**
     * Clears the database of outdated sections
     */
    private void clearOutDatedMainSections(List<Section> sections, ApiInformationStruct apiInformationStruct) {
        Print.d(TAG, "ON CLEAR OUT DATED SECTIONS");
        // Update each outdated section
        for (Section section : sections) {
            // Case teasers
            switch (section.getName()) {
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
     * Checks the sections and returns a list of sections that need to be
     * updated
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
     */
    private Section getSection(String sectionName, List<Section> sections) {
        for (Section section : sections) {
            if (sectionName.equals(section.getName())) {
                return section;
            }
        }
        return null;
    }

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
