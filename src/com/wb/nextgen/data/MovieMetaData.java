package com.wb.nextgen.data;

import com.wb.nextgen.parser.manifest.schema.v1_4.AudiovisualType;
import com.wb.nextgen.parser.manifest.schema.v1_4.ExperienceChildType;
import com.wb.nextgen.parser.manifest.schema.v1_4.ExperienceType;
import com.wb.nextgen.parser.manifest.schema.v1_4.GalleryType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryImageType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryMetadataType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryVideoType;
import com.wb.nextgen.parser.manifest.schema.v1_4.MediaManifestType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PictureGroupType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PictureType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PresentationType;
import com.wb.nextgen.parser.manifest.schema.v1_4.TimecodeType;
import com.wb.nextgen.parser.manifest.schema.v1_4.TimedEventSequenceType;
import com.wb.nextgen.parser.manifest.schema.v1_4.TimedEventType;
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
    final private List<ECGroupData> imeECGroups = new ArrayList<ECGroupData>();
    final private List<IMEElementsGroup> imeElementGroups = new ArrayList<IMEElementsGroup>();

    final static public String movieTitleText = "Man of Steel";



    public static MovieMetaData process(MediaManifestType mediaManifest){
        MovieMetaData result = new MovieMetaData();

        HashMap<String, InventoryMetadataType> metaDataAssetsMap = new HashMap<String, InventoryMetadataType>();
        HashMap<String, InventoryVideoType> videoAssetsMap = new HashMap<String, InventoryVideoType>();
        HashMap<String, PresentationType> presentationAssetMap = new HashMap<String, PresentationType>();
        HashMap<String, InventoryImageType> imageAssetsMap = new HashMap<String, InventoryImageType>();
        HashMap<String, PictureGroupType> pictureGroupAssetsMap = new HashMap<String, PictureGroupType>();
        HashMap<String, ECContentData> presentationIdToECMap = new HashMap<String, ECContentData>();
        HashMap<String, ECContentData> galleryIdToECMap = new HashMap<String, ECContentData>();

        HashMap<String, ECGroupData> experienceIdToECGroupMap = new HashMap<String, ECGroupData>();
        //HashMap<String, ExperienceType>

        HashMap<String, ECGroupData> experienceChildrenToParentMap = new HashMap<String, ECGroupData>();
        //HashMap<String, ECGroupData> experienceIdMap = new HashMap<String, ECGroupData>();

        String mainMovieExperienceId = mediaManifest.getALIDExperienceMaps().getALIDExperienceMap().get(0).getExperienceID().get(0).getValue();


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

            List<ExperienceType> exprienceParentList = new ArrayList<ExperienceType>();

            for (ExperienceType experience : mediaManifest.getExperiences().getExperience()) {
                InventoryMetadataType metaData = metaDataAssetsMap.get(experience.getContentID());
                ECGroupData thisGroupData = null;
                ECGroupData parentGroup = experienceChildrenToParentMap.get(experience.getExperienceID());
                if ((experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0) || (experience.getAudiovisual() != null && experience.getAudiovisual().size() > 0) ) {

                    ECGroupData groupData = new ECGroupData(experience, metaData);
                    //result.movieExperiences.add(groupData);
                    if (experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0) {
                        for (ExperienceChildType child : experience.getExperienceChild()) {
                            experienceChildrenToParentMap.put(child.getExperienceID(), groupData);    //skip these IDs when encounter.
                        }
                    }

                    if ((experience.getAudiovisual() != null && experience.getAudiovisual().size() > 0) && !mainMovieExperienceId.equals(experience.getExperienceID())){
                        thisGroupData = groupData;
                    }

                    if (experienceChildrenToParentMap.containsKey(experience.getExperienceID())){
                        parentGroup.ecGroups.add(groupData);
                    }

                    if (groupData != null) {
                        experienceIdToECGroupMap.put(experience.getExperienceID(), groupData);
                        exprienceParentList.add(experience);
                    }
                    //experienceIdMap.put(experience.getExperienceID(), groupData);
                }

                //ECGroupData parentGroup = experienceChildrenToParentMap.get(experience.getExperienceID());
                if (thisGroupData != null){
                    //parentGroup.ecGroups.add(thisGroupData);
                    parentGroup = thisGroupData;
                }

                if (parentGroup != null && thisGroupData != null){
                    List<ECContentData> ecGalleryDatas = new ArrayList<ECContentData>();
                    List<ECContentData> ecVideoDatas = new ArrayList<ECContentData>();
                    if (experience.getGallery() != null && experience.getGallery().size() > 0) {
                        for(GalleryType gallery : experience.getGallery()) {
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

                            ECContentData ecData = new ECContentData(metaData, items);
                            ecGalleryDatas.add(ecData);
                            galleryIdToECMap.put(experience.getGallery().get(0).getGalleryID(), ecData);
                        }
                    }

                    if (experience.getAudiovisual() != null){
                        InventoryVideoType video = null;
                        String presentationId = "";
                        if (experience.getAudiovisual().size() > 0) {                           // for video Asset
                            for (AudiovisualType audioviual : experience.getAudiovisual()) {
                                metaData = metaDataAssetsMap.get(audioviual.getContentID());        // get Video asset by ContentID of its AudioVisual
                                presentationId = audioviual.getPresentationID();
                                PresentationType presentation = presentationAssetMap.get(presentationId);  // get Presentation by presentation id
                                if (presentation.getTrackMetadata().size() > 0 &&
                                        presentation.getTrackMetadata().get(0).getVideoTrackReference().size() > 0 &&
                                        presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().size() > 0) {                                           // get the video id from presentation
                                    video = videoAssetsMap.get(presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().get(0));
                                }
                                if (video != null) {
                                    ECContentData ecData = new ECContentData(metaData, video);
                                    presentationIdToECMap.put(presentationId, ecData);
                                    ecVideoDatas.add(ecData);
                                } else {

                                }
                            }
                        }
                    }
                    if (ecGalleryDatas.size() > 0) {
                        parentGroup.addChildren(ecGalleryDatas);
                    }
                    if (ecVideoDatas.size() > 0) {
                        parentGroup.addChildren(ecVideoDatas);
                    }
                    if (ecGalleryDatas.size() > 0 && ecVideoDatas.size() > 0){
                        parentGroup.setMixGroupType();
                    }
                }
            }
            if (exprienceParentList.size() > 0){
                ExperienceType rootElement = exprienceParentList.get(0);
                ECGroupData rootGroup = experienceIdToECGroupMap.get(rootElement.getExperienceID());
                if (rootGroup.ecGroups.size() == 2) {
                    result.extraECGroups.addAll(rootGroup.ecGroups.get(0).ecGroups);
                    result.imeECGroups.addAll(rootGroup.ecGroups.get(1).ecGroups);
                }

            }
        }




        /*****************End of Experiences****************************/

        /*****************Time Sequence Events****************************/
        if (mediaManifest.getTimedEventSequences() != null && mediaManifest.getTimedEventSequences().getTimedEventSequence().size() > 0){
            for (TimedEventSequenceType timedEventSequence : mediaManifest.getTimedEventSequences().getTimedEventSequence()){
                IMEElementsGroup imeGroup = new IMEElementsGroup();     // need to figure out the type of this group
                String presentationId = timedEventSequence.getPresentationID(); // this should be the main movie presentation ID
                if (timedEventSequence.getTimedEvent() != null && timedEventSequence.getTimedEvent().size() > 0){
                    for (TimedEventType timedEvent : timedEventSequence.getTimedEvent()){

                        TimecodeType startTimeCode = timedEvent.getStartTimecode();
                        TimecodeType endTimeCode = timedEvent.getEndTimecode();
                        float startTime = Float.parseFloat(startTimeCode.getValue()) * 1000F;
                        float endTime = Float.parseFloat(endTimeCode.getValue()) * 1000F;

                        String eventPID = timedEvent.getPresentationID();
                        String galleryId = timedEvent.getGalleryID();

                        ECContentData ecData = null;
                        if (!StringHelper.isEmpty(eventPID)) {
                            if (presentationIdToECMap.containsKey(eventPID)) {
                                ecData = presentationIdToECMap.get(eventPID);


                            }
                        }else if (!StringHelper.isEmpty(galleryId)){
                            if (galleryIdToECMap.containsKey(galleryId)){
                                ecData = galleryIdToECMap.get(galleryId);
                            }
                        }

                        if (ecData != null){
                            IMEElement<ECContentData> element = new IMEElement((long)startTime, (long)endTime, ecData);
                            imeGroup.addElement(element);
                        }

                    }
                }

                result.imeElementGroups.add(imeGroup);

            }
        }



        /*****************End of Time Sequence Events****************************/

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
        FEATURETTES, VISUAL_EFFECT, GALLERY, MIX
    }

    static public class ECGroupData{
        final public String title;
        final public String id;
        private String posterImgUrl;
        private ECGroupType type;
        final public List<ECContentData>  ecContents = new ArrayList<ECContentData>();
        final public List<ECGroupData>  ecGroups = new ArrayList<ECGroupData>();

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

        private void setMixGroupType(){
            type = ECGroupType.MIX;
        }

        private void addChildren(List<ECContentData> ecContent){
            ecContents.addAll(ecContent);
            if (type == null)
                type = ecContent.get(0).galleryItems.size() > 0 ? ECGroupType.GALLERY : ECGroupType.FEATURETTES;
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
    }

    public static class IMEElementsGroup<T>{
        private final List<IMEElement<T>> imeElementsList = new ArrayList<IMEElement<T>>();
        public void addElement(IMEElement<T> element){
            imeElementsList.add(element);
        }

    }

    public static class IMEElement<T> {
        public final long startTimecode;
        public final long endTimecode;
        public final T imeObject;
        public IMEElement(long startTimecode, long endTimeCode, T imeObject){   // in millisecond
            this.startTimecode = startTimecode;
            this.endTimecode = endTimeCode;
            this.imeObject = imeObject;
        }

        /*
            returns: 0 = within
            returns: >0 = after
            returns: <0 = before
         */
        public int compareTimeCode(long timeCode){
            long beforeTime = timeCode - startTimecode;
            long afterTime = timeCode - endTimecode;
            if (beforeTime >=0 && afterTime <= 0)
                return 0;
            else if (afterTime > 0)
                return 1;
            else
                return -1;
        }
    }
}
