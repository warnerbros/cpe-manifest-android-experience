package com.wb.nextgen.data;

import com.wb.nextgen.parser.manifest.schema.v1_4.ExperienceChildType;
import com.wb.nextgen.parser.manifest.schema.v1_4.ExperienceType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryImageType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryMetadataType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryVideoType;
import com.wb.nextgen.parser.manifest.schema.v1_4.MediaManifestType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PictureGroupType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PictureType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PresentationType;
import com.wb.nextgen.parser.md.schema.v2_3.BasicMetadataInfoType;
import com.wb.nextgen.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gzcheng on 3/21/16.
 */
public class MovieMetaData {


    final private List<ECGroupData> movieExperiences = new ArrayList<ECGroupData>();
    final private List<ECGroupData> extraECGroups = new ArrayList<ECGroupData>();

    final static public List<ECContentData> ecList = new ArrayList<ECContentData>();
    final static public String movieTitleText = "Man of Steel";

    public static MovieMetaData process(MediaManifestType mediaManifest){
        MovieMetaData result = new MovieMetaData();

        HashMap<String, InventoryMetadataType> metaDataAssetsMap = new HashMap<String, InventoryMetadataType>();
        HashMap<String, InventoryVideoType> videoAssetsMap = new HashMap<String, InventoryVideoType>();
        HashMap<String, PresentationType> presentationAssetMap = new HashMap<String, PresentationType>();
        HashMap<String, InventoryImageType> imageAssetsMap = new HashMap<String, InventoryImageType>();
        HashMap<String, PictureGroupType> pictureGroupAssetsMap = new HashMap<String, PictureGroupType>();

        HashMap<String, ECGroupData> experienceChildrenToParentMap = new HashMap<String, ECGroupData>();
        //HashMap<String, ECGroupData> experienceIdMap = new HashMap<String, ECGroupData>();


        if (mediaManifest.getInventory() != null){
            if (mediaManifest.getInventory().getMetadata().size() > 0) {
                for (InventoryMetadataType metaData : mediaManifest.getInventory().getMetadata()){
                    metaDataAssetsMap.put(metaData.getContentID(), metaData);
                }
            }

            if (mediaManifest.getInventory().getVideo().size() > 0){
                for (InventoryVideoType videoData : mediaManifest.getInventory().getVideo()){
                    videoAssetsMap.put(videoData.getVideoTrackID(), videoData);
                }
            }

            if (mediaManifest.getInventory().getImage().size() > 0){
                for (InventoryImageType imageData : mediaManifest.getInventory().getImage()){
                    imageAssetsMap.put(imageData.getImageID(), imageData);
                }
            }
        }

        if (mediaManifest.getPresentations().getPresentation().size() > 0){
            for(PresentationType presentation : mediaManifest.getPresentations().getPresentation()){
                presentationAssetMap.put(presentation.getPresentationID(), presentation);
            }
        }

        if (mediaManifest.getPictureGroups().getPictureGroup().size() > 0){
            for(PictureGroupType pictureGroup : mediaManifest.getPictureGroups().getPictureGroup()){
                pictureGroupAssetsMap.put(pictureGroup.getPictureGroupID(), pictureGroup);
            }
        }

        /**************Experiences ***************/
        if (mediaManifest.getExperiences() != null && mediaManifest.getExperiences().getExperience() != null) {

            for (ExperienceType experience : mediaManifest.getExperiences().getExperience()) {
                InventoryMetadataType metaData = metaDataAssetsMap.get(experience.getContentID());

                if (!experienceChildrenToParentMap.containsKey(experience.getExperienceID())) {

                    ECGroupData groupData = new ECGroupData(experience, metaData);
                    result.movieExperiences.add(groupData);
                    for (ExperienceChildType child : experience.getExperienceChild()) {
                        experienceChildrenToParentMap.put(child.getExperienceID(), groupData);    //skip these IDs when encounter.
                        /*

                        if (!result.experienceIdMap.containsKey(experience.getExperienceID())) {
                            result.experienceIdMap.put(experience.getExperienceID(), new HashMap<String, ExperienceChildType>());
                        }

                        result.experienceIdMap.get(experience.getExperienceID()).put(child.getExperienceID(), child);*/

                    }


                    //experienceIdMap.put(experience.getExperienceID(), groupData);
                }else {
                    ECGroupData parentGroup = experienceChildrenToParentMap.get(experience.getExperienceID());

                    ECContentData ecData = null;
                    if (experience.getGallery() != null && experience.getGallery().size() > 0) {
                        String pictureGroupId = experience.getGallery().get(0).getPictureGroupID();
                        PictureGroupType pictureGroup = pictureGroupAssetsMap.get(pictureGroupId);
                        List<ECGalleryImageItem> items = new ArrayList<ECGalleryImageItem>();
                        if (pictureGroup != null) {
                            for (PictureType picture : pictureGroup.getPicture()) {
                                InventoryImageType fullImageData = imageAssetsMap.get(picture.getImageID());
                                InventoryImageType thumbNailImageData = imageAssetsMap.get(picture.getThumbnailImageID());
                                ECGalleryImageItem galleryItem = new ECGalleryImageItem(metaData, fullImageData, thumbNailImageData);
                                items.add(galleryItem);
                            }

                        }


                        ecData = new ECContentData(metaData, items);
                    }else{
                        InventoryVideoType video = null;
                        if (experience.getAudiovisual().size() > 0) {                           // for video Asset
                            metaData = metaDataAssetsMap.get(experience.getAudiovisual().get(0).getContentID());        // get Video asset by ContentID of its AudioVisual
                            PresentationType presentation = presentationAssetMap.get(experience.getAudiovisual().get(0).getPresentationID());  // get Presentation by presentation id
                            if (presentation.getTrackMetadata().size() > 0 &&
                                    presentation.getTrackMetadata().get(0).getVideoTrackReference().size() > 0 &&
                                    presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().size() > 0) {                                           // get the video id from presentation
                                video = videoAssetsMap.get(presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().get(0));
                            }
                        }
                        if (video != null) {
                            ecData = new ECContentData(metaData, video);
                        } else {

                        }
                    }
                    if (ecData != null)
                        parentGroup.addChildren(ecData);

                }
            }



        }

        result.computeExtraECGroups();

        return result;
    }

    public List<ECGroupData> getExtraECGroups(){
        return extraECGroups;
    }

    private void computeExtraECGroups(){
        if (extraECGroups.size() > 0)
            return;

        for(ECGroupData groupData : movieExperiences) {
            if (groupData.ecContents == null || groupData.ecContents.size() == 0) {
                // not extra EC
            }else{
                extraECGroups.add(groupData);
            }
        }
    }

    public ECGroupData findECGroupDataById(String id){
        if (!StringHelper.isEmpty(id)) {
            for (ECGroupData group : movieExperiences) {
                if (id.equals(group.id))
                    return group;
            }
        }
        return null;
    }
/*

    public InventoryVideoType getVideoAssetByExperienceId(String experienceId){
        InventoryVideoType video = videoAssetsMap.get(experienceId + VIDEO_ASSETS_ID_EXTENTION);

        return video;

    }*/

    public static enum ECGroupType{
        FEATURETTES, VISUAL_EFFECT, GALLERY
    }

    static public class ECGroupData{
        final public String title;
        final public String id;
        private String posterImgUrl;
        private ECGroupType type;
        final public List<ECContentData>  ecContents = new ArrayList<ECContentData>();

        public ECGroupData(ExperienceType experience, InventoryMetadataType metaData){
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            title = localizedInfo.getTitleDisplayUnlimited(); // should loop the list and look for the correct language code
            id = experience.getContentID();     // or experience Id
            //this.type = (experience.getGallery() != null && experience.getGallery().size() > 0) ? ECGroupType.GALLERY : ECGroupType.FEATURETTES;
            if (localizedInfo.getArtReference().size() > 0)
                posterImgUrl = localizedInfo.getArtReference().get(0).getValue();   // should parse for different resolutions
            else{

            }
        }

        private void addChildren(ECContentData ecContent){
            ecContents.add(ecContent);
            if (type == null)
                type = ecContent.galleryItems.size() > 0 ? ECGroupType.GALLERY : ECGroupType.FEATURETTES;
        }

        public ECGroupType getECGroupType(){
            return type;
        }
        /*
        public ECGroupData(String id, String title, ECGroupType type){
            this.id = id;
            this.title = title;
            //this.posterImgUrl = posterImgUrl;
            this.type = type;
        }*/
        public String getPosterImgUrl(){
            if (StringHelper.isEmpty(posterImgUrl) && ecContents.size() > 0){
                for (ECContentData ec : ecContents){
                    if (!StringHelper.isEmpty(ec.posterImgUrl) ){
                        posterImgUrl = ec.posterImgUrl;
                        break;
                    }
                }
            }
            return posterImgUrl;
        }
    }

    static public class ECGalleryImageData{

        final public String url;
        final public int width;
        final public int height;
        public ECGalleryImageData(String url, int width, int height){
            this.url = url;
            this.width = width;
            this.height = height;
        }
    }

    static public class ECGalleryImageItem{
        final public ECGalleryImageData fullImage;
        final public ECGalleryImageData thumbnail;
        final public String name;
        /*public ECGalleryImageItem(String name, ECGalleryImageData fullImage, ECGalleryImageData thumbnail){
            this.name = name;
            this.fullImage = fullImage;
            this.thumbnail = thumbnail;
        }*/
        public ECGalleryImageItem(InventoryMetadataType metaData, InventoryImageType fullImage, InventoryImageType thumbnail){
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            name = localizedInfo.getTitleDisplayUnlimited();
            this.fullImage = new ECGalleryImageData(fullImage.getContainerReference().getContainerLocation(), fullImage.getWidth(), fullImage.getHeight());
            this.thumbnail = new ECGalleryImageData(thumbnail.getContainerReference().getContainerLocation(), thumbnail.getWidth(), thumbnail.getHeight());
        }
    }

    static public class ECContentData{
        final public String title;
        final public String posterImgUrl;
        final public String ecVideoUrl;
        final public List<ECGalleryImageItem>  galleryItems = new ArrayList<ECGalleryImageItem>();
        public ECContentData(InventoryMetadataType metaData, InventoryVideoType videoData){
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            title = localizedInfo.getTitleDisplayUnlimited(); // should loop the list and look for the correct language code
            if (videoData != null) {
                ecVideoUrl = videoData.getContainerReference().getContainerLocation();
                posterImgUrl = localizedInfo.getArtReference().get(0).getValue();
            }else {  //is gallery
                ecVideoUrl = null;
                posterImgUrl = null;
            }   // should parse for different resolutions
        }


        public ECContentData(InventoryMetadataType metaData, List<ECGalleryImageItem> galleryItems) {
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            title = localizedInfo.getTitleDisplayUnlimited(); // should loop the list and look for the correct language code
            ecVideoUrl = null;
            if (galleryItems != null && galleryItems.size() > 0) {
                this.galleryItems.addAll(galleryItems);

            }
            posterImgUrl = this.galleryItems.get(0).thumbnail.url;

        }


        public String getPosterImgUrl(){
            if (!StringHelper.isEmpty(posterImgUrl))
                return posterImgUrl;
            else if (galleryItems.size() > 0){
                return  galleryItems.get(0).thumbnail.url;
            }else
                return null;
        }


        /*public ECContentData(String title, String posterImgUrl, String videoUrl){
            this.title = title;
            this.posterImgUrl = posterImgUrl;
            this.ecVideoUrl = videoUrl;
        }*/
    }
}
