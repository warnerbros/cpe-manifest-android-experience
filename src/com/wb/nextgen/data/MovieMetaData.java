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


    //final private List<ECGroupData> movieExperiences = new ArrayList<ECGroupData>();
    final private List<ExperienceData> extraECGroups = new ArrayList<ExperienceData>();
    final private List<ExperienceData> imeECGroups = new ArrayList<ExperienceData>();
    final private List<IMEElementsGroup> imeElementGroups = new ArrayList<IMEElementsGroup>();
    final private HashMap<String, ExperienceData> experienceIdToExperienceMap = new HashMap<String, ExperienceData>();

    final static public String movieTitleText = "Man of Steel";



    public static MovieMetaData process(MediaManifestType mediaManifest){
        MovieMetaData result = new MovieMetaData();

        HashMap<String, InventoryMetadataType> metaDataAssetsMap = new HashMap<String, InventoryMetadataType>();
        HashMap<String, InventoryVideoType> videoAssetsMap = new HashMap<String, InventoryVideoType>();
        HashMap<String, PresentationType> presentationAssetMap = new HashMap<String, PresentationType>();
        HashMap<String, InventoryImageType> imageAssetsMap = new HashMap<String, InventoryImageType>();
        HashMap<String, PictureGroupType> pictureGroupAssetsMap = new HashMap<String, PictureGroupType>();
        HashMap<String, ECAudioVisualItem> presentationIdToAVItemMap = new HashMap<String, ECAudioVisualItem>();
        HashMap<String, ECGalleryImageItem> galleryIdToGalleryItemMap = new HashMap<String, ECGalleryImageItem>();

        HashMap<String, ExperienceData> timeSequenceIdToECGroup = new HashMap<String, ExperienceData>();


        //HashMap<String, ExperienceType>

        HashMap<String, ExperienceData> experienceChildrenToParentMap = new HashMap<String, ExperienceData>();
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

            //List<ExperienceType> exprienceParentList = new ArrayList<ExperienceType>();
            ExperienceData rootData = null;

            for (ExperienceType experience : mediaManifest.getExperiences().getExperience()) {
                List<ECGalleryImageItem> galleryItems = new ArrayList<ECGalleryImageItem>();
                List<ECAudioVisualItem> avItems = new ArrayList<ECAudioVisualItem>();
                InventoryMetadataType metaData = metaDataAssetsMap.get(experience.getContentID());

                if (experience.getGallery() != null && experience.getGallery().size() > 0) {
                    for(GalleryType gallery : experience.getGallery()) {
                        String pictureGroupId = experience.getGallery().get(0).getPictureGroupID();
                        PictureGroupType pictureGroup = pictureGroupAssetsMap.get(pictureGroupId);
                        if (pictureGroup != null) {
                            for (PictureType picture : pictureGroup.getPicture()) {
                                InventoryImageType fullImageData = imageAssetsMap.get(picture.getImageID());
                                InventoryImageType thumbNailImageData = imageAssetsMap.get(picture.getThumbnailImageID());
                                ECGalleryImageItem galleryItem = new ECGalleryImageItem(experience.getExperienceID(), metaData, fullImageData, thumbNailImageData);
                                galleryItems.add(galleryItem);
                                galleryIdToGalleryItemMap.put(gallery.getGalleryID(), galleryItem);
                            }
                        }
                    }
                }

                if (experience.getAudiovisual() != null){
                    InventoryVideoType video = null;
                    String presentationId = "";
                    if (experience.getAudiovisual().size() > 0) {                           // for video Asset
                        for (AudiovisualType audioVisual : experience.getAudiovisual()) {
                            metaData = metaDataAssetsMap.get(audioVisual.getContentID());        // get Video asset by ContentID of its AudioVisual
                            presentationId = audioVisual.getPresentationID();
                            PresentationType presentation = presentationAssetMap.get(presentationId);  // get Presentation by presentation id
                            if (presentation.getTrackMetadata().size() > 0 &&
                                    presentation.getTrackMetadata().get(0).getVideoTrackReference().size() > 0 &&
                                    presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().size() > 0) {                                           // get the video id from presentation
                                video = videoAssetsMap.get(presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().get(0));
                            }
                            if (video != null) {
                                //ExperienceData ecData = new ExperienceData(experience, metaData, video, null);
                                ECAudioVisualItem item = new ECAudioVisualItem(experience.getExperienceID(), metaData, video);
                                avItems.add(item);
                                presentationIdToAVItemMap.put(presentationId, item);
                            } else {

                            }
                        }
                    }
                }

                ExperienceData thisExperience = new ExperienceData(experience, metaData, avItems, galleryItems);
                result.experienceIdToExperienceMap.put(experience.getExperienceID(), thisExperience);
                ExperienceData parentGroup = experienceChildrenToParentMap.get(experience.getExperienceID());
                if (parentGroup != null){
                    parentGroup.addChild(thisExperience);
                }
                if (rootData == null){
                    rootData = thisExperience;
                }
                if (experience.getTimedSequenceID() != null && experience.getTimedSequenceID().size() > 0 && !StringHelper.isEmpty(experience.getTimedSequenceID().get(0)) ){
                    timeSequenceIdToECGroup.put(experience.getTimedSequenceID().get(0), thisExperience);
                }



                if ((experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0) || (experience.getAudiovisual() != null && experience.getAudiovisual().size() > 0) ) {

                    //result.movieExperiences.add(groupData);
                    if (experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0) {
                        for (ExperienceChildType child : experience.getExperienceChild()) {
                            experienceChildrenToParentMap.put(child.getExperienceID(), thisExperience);    //skip these IDs when encounter.
                        }
                    }

                    //experienceIdMap.put(experience.getExperienceID(), groupData);
                }

                /*
                //ExperienceData thisExperience = null;
                if ((experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0) || (experience.getAudiovisual() != null && experience.getAudiovisual().size() > 0) ) {



                    //result.movieExperiences.add(groupData);
                    if (experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0) {
                        for (ExperienceChildType child : experience.getExperienceChild()) {
                            experienceChildrenToParentMap.put(child.getExperienceID(), groupData);    //skip these IDs when encounter.
                        }
                    }

                    if ((experience.getAudiovisual() != null && experience.getAudiovisual().size() > 0) && !mainMovieExperienceId.equals(experience.getExperienceID())){
                        thisGroupData = groupData;
                    }

                    if (parentGroup != null){
                        parentGroup.addChild(groupData);
                    }

                    if (groupData != null) {
                        result.experienceIdToECGroupMap.put(experience.getExperienceID(), groupData);
                        exprienceParentList.add(experience);
                    }
                    //experienceIdMap.put(experience.getExperienceID(), groupData);
                }


                if (parentGroup != null && thisGroupData != null){
                    List<ExperienceData> ecGalleryDatas = new ArrayList<ExperienceData>();
                    List<ExperienceData> ecVideoDatas = new ArrayList<ExperienceData>();
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

                            ExperienceData ecData = new ExperienceData(experience, metaData, null, items);
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
                                    ExperienceData ecData = new ExperienceData(experience, metaData, video, null);
                                    presentationIdToECMap.put(presentationId, ecData);
                                    ecVideoDatas.add(ecData);
                                } else {

                                }
                            }
                        }
                    }
                }*/
            }


            //ExperienceType rootElement = exprienceParentList.get(0);
            //ExperienceData rootGroup = result.experienceIdToExperienceMap.get(rootData.getExperienceID());
            if (rootData != null && rootData.childrenExperience.size() == 2) {
                result.extraECGroups.addAll(rootData.childrenExperience.get(0).childrenExperience);
                result.imeECGroups.addAll(rootData.childrenExperience.get(1).childrenExperience);
            }


        }




        /*****************End of Experiences****************************/

        /*****************Time Sequence Events****************************/
        if (mediaManifest.getTimedEventSequences() != null && mediaManifest.getTimedEventSequences().getTimedEventSequence().size() > 0){
            for (TimedEventSequenceType timedEventSequence : mediaManifest.getTimedEventSequences().getTimedEventSequence()){
                ExperienceData timedECGroup = null;
                if (timeSequenceIdToECGroup.containsKey(timedEventSequence.getTimedSequenceID())){
                    timedECGroup = timeSequenceIdToECGroup.get(timedEventSequence.getTimedSequenceID());
                }
                IMEElementsGroup imeGroup = new IMEElementsGroup(timedECGroup);     // need to figure out the type of this group
                String presentationId = timedEventSequence.getPresentationID(); // this should be the main movie presentation ID
                if (timedEventSequence.getTimedEvent() != null && timedEventSequence.getTimedEvent().size() > 0){
                    for (TimedEventType timedEvent : timedEventSequence.getTimedEvent()){

                        TimecodeType startTimeCode = timedEvent.getStartTimecode();
                        TimecodeType endTimeCode = timedEvent.getEndTimecode();
                        float startTime = Float.parseFloat(startTimeCode.getValue()) * 1000F;
                        float endTime = Float.parseFloat(endTimeCode.getValue()) * 1000F;

                        String eventPID = timedEvent.getPresentationID();
                        String galleryId = timedEvent.getGalleryID();

                        ExperienceData ecData = null;
                        if (!StringHelper.isEmpty(eventPID)) {
                            if (presentationIdToAVItemMap.containsKey(eventPID)) {
                                ECAudioVisualItem avItem = null;
                                avItem = presentationIdToAVItemMap.get(eventPID);
                                if (!StringHelper.isEmpty(avItem.parentExperienceId) && result.experienceIdToExperienceMap.containsKey(avItem.parentExperienceId)){
                                    ecData = result.experienceIdToExperienceMap.get(avItem.parentExperienceId);
                                }
                            }
                        }else if (!StringHelper.isEmpty(galleryId)){
                            if (galleryIdToGalleryItemMap.containsKey(galleryId)){
                                ECGalleryImageItem item = galleryIdToGalleryItemMap.get(galleryId);
                                String experienceId = item.parentExperienceId;
                                if (!StringHelper.isEmpty(experienceId) && result.experienceIdToExperienceMap.containsKey(experienceId)){
                                    ecData = result.experienceIdToExperienceMap.get(experienceId);
                                }
                            }
                        }

                        if (ecData != null){
                            IMEElement<ExperienceData> element = new IMEElement((long)startTime, (long)endTime, ecData);
                            imeGroup.addElement(element);
                        }

                    }
                }

                result.imeElementGroups.add(imeGroup);

            }
        }



        /*****************End of Time Sequence Events****************************/

      //  result.computeExtraECGroups();

        return result;
    }

    public List<ExperienceData> getExtraECGroups(){
        return extraECGroups;
    }

    public List<IMEElementsGroup> getImeElementGroups(){
        return imeElementGroups;
    }
/*
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
    }*/

    public ExperienceData findExperienceDataById(String id){
        if (!StringHelper.isEmpty(id)) {
            return experienceIdToExperienceMap.get(id);

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
        final public String title;
        final public String parentExperienceId;
        /*public ECGalleryImageItem(String name, ECGalleryImageData fullImage, ECGalleryImageData thumbnail){
            this.name = name;
            this.fullImage = fullImage;
            this.thumbnail = thumbnail;
        }*/
        public ECGalleryImageItem(String parentExperienceId, InventoryMetadataType metaData, InventoryImageType fullImage, InventoryImageType thumbnail){
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            title = localizedInfo.getTitleDisplayUnlimited();
            this.parentExperienceId = parentExperienceId;
            this.fullImage = new ECGalleryImageData(fullImage.getContainerReference().getContainerLocation(), fullImage.getWidth(), fullImage.getHeight());
            this.thumbnail = new ECGalleryImageData(thumbnail.getContainerReference().getContainerLocation(), thumbnail.getWidth(), thumbnail.getHeight());
        }
    }

    static public class ECAudioVisualItem{
        final public String videoUrl;
        private String posterImgUrl;
        final public String title;
        final public String parentExperienceId;
        public ECAudioVisualItem(String parentExperienceId, InventoryMetadataType metaData, InventoryVideoType videoData){
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            title = localizedInfo.getTitleDisplayUnlimited();
            this.parentExperienceId = parentExperienceId;
            if (videoData != null) {
                videoUrl = videoData.getContainerReference().getContainerLocation();
                if (localizedInfo.getArtReference() != null && localizedInfo.getArtReference().size() > 0)
                    posterImgUrl = localizedInfo.getArtReference().get(0).getValue();
                else
                    posterImgUrl = null;
            }else{
                videoUrl = null;
                posterImgUrl = null;
            }
        }
    }


    static public class ExperienceData {
        final public String title;
        private String posterImgUrl;
        //final public String ecVideoUrl;
        private ECGroupType type;
        final private List<ExperienceData> childrenExperience = new ArrayList<ExperienceData>();
        final public List<ECGalleryImageItem> galleryItems = new ArrayList<ECGalleryImageItem>();
        final public List<ECAudioVisualItem> audioVisualItems = new ArrayList<ECAudioVisualItem>();
        final public String experienceId;
        final private HashMap<String, Integer> childIdToSequenceNumber = new HashMap<String, Integer>();

        public ExperienceData(ExperienceType experience, InventoryMetadataType metaData, List<ECAudioVisualItem> avItems, List<ECGalleryImageItem> galleryItems){
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            title = localizedInfo.getTitleDisplayUnlimited(); // should loop the list and look for the correct language code
            experienceId = experience.getExperienceID();     // or experience Id

            if (avItems != null && avItems.size() > 0){
                audioVisualItems.addAll(avItems);
            }

            if (galleryItems != null && galleryItems.size() > 0){
                this.galleryItems.addAll(galleryItems);
            }

            if (localizedInfo != null && localizedInfo.getArtReference() != null && localizedInfo.getArtReference().size() > 0){
                posterImgUrl = localizedInfo.getArtReference().get(0).getValue();
            } else if (this.galleryItems.size() > 0) {
                posterImgUrl = null;
            }
            if (experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0){

                for(ExperienceChildType childType : experience.getExperienceChild()){
                    if (childType.getSequenceInfo() != null) {
                        childIdToSequenceNumber.put(childType.getExperienceID(), childType.getSequenceInfo().getNumber());
                    }
                }
            }

        }

        private void setMixGroupType(){
            type = ECGroupType.MIX;
        }

        public ECGroupType getECGroupType(){
            return type;
        }

        private void addChild(ExperienceData ecContent){
            if (childrenExperience.size() == 0 && childIdToSequenceNumber.size() > 0){
                childrenExperience.add(ecContent);
            }else if (childIdToSequenceNumber.containsKey(ecContent.experienceId)) {
                int childSequenceNumber = childIdToSequenceNumber.get(ecContent.experienceId);

                for(int i=childrenExperience.size()-1; i >=0 ; i--){
                    String checkExpId = childrenExperience.get(i).experienceId;

                    int checkSequenceNumber = childIdToSequenceNumber.get(checkExpId);
                    if (childSequenceNumber > checkSequenceNumber){
                        childrenExperience.add(i+1, ecContent);
                        return;
                    }
                }
                childrenExperience.add(ecContent);
            }else{
                childrenExperience.add(ecContent);
            }

        }

        public List<ExperienceData> getChildrenContents(){
            return childrenExperience;
        }


        public String getPosterImgUrl(){
            if (!StringHelper.isEmpty(posterImgUrl))
                return posterImgUrl;
            else if (galleryItems.size() > 0){
                return  galleryItems.get(0).thumbnail.url;
            }else if (StringHelper.isEmpty(posterImgUrl) && childrenExperience.size() > 0) {
                for (ExperienceData ec : childrenExperience) {
                    if (!StringHelper.isEmpty(ec.getPosterImgUrl())) {
                        posterImgUrl = ec.getPosterImgUrl();
                        break;
                    }
                }
                return posterImgUrl;
            }else
                return null;
        }
    }

    public static class IMEElementsGroup{
        private final ExperienceData linkedExperience;
        private final List<IMEElement> imeElementsList = new ArrayList<IMEElement>();
        public void addElement(IMEElement element){
            imeElementsList.add(element);
        }
        public IMEElementsGroup(ExperienceData ecGroupData){
            this.linkedExperience = ecGroupData;
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
