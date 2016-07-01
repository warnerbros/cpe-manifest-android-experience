package com.wb.nextgen.data;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.gson.annotations.SerializedName;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.parser.ManifestXMLParser;
import com.wb.nextgen.parser.appdata.AppDataLocationType;
import com.wb.nextgen.parser.appdata.AppDataType;
import com.wb.nextgen.parser.appdata.AppNVPairType;
import com.wb.nextgen.parser.appdata.ManifestAppDataSetType;
import com.wb.nextgen.parser.manifest.schema.v1_4.AudiovisualType;
import com.wb.nextgen.parser.manifest.schema.v1_4.ExperienceAppType;
import com.wb.nextgen.parser.manifest.schema.v1_4.ExperienceChildType;
import com.wb.nextgen.parser.manifest.schema.v1_4.ExperienceType;
import com.wb.nextgen.parser.manifest.schema.v1_4.GalleryType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryImageType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryMetadataType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryTextObjectType;
import com.wb.nextgen.parser.manifest.schema.v1_4.InventoryVideoType;
import com.wb.nextgen.parser.manifest.schema.v1_4.MediaManifestType;
import com.wb.nextgen.parser.manifest.schema.v1_4.OtherIDType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PictureGroupType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PictureType;
import com.wb.nextgen.parser.manifest.schema.v1_4.PresentationType;
import com.wb.nextgen.parser.manifest.schema.v1_4.TimecodeType;
import com.wb.nextgen.parser.manifest.schema.v1_4.TimedEventSequenceType;
import com.wb.nextgen.parser.manifest.schema.v1_4.TimedEventType;
import com.wb.nextgen.parser.md.schema.v2_3.BasicMetadataInfoType;
import com.wb.nextgen.parser.md.schema.v2_3.BasicMetadataPeopleType;
import com.wb.nextgen.parser.md.schema.v2_3.ContentIdentifierType;
import com.wb.nextgen.parser.md.schema.v2_3.PersonIdentifierType;
import com.wb.nextgen.util.NextGenUtils;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;
import com.wb.nextgen.util.utils.StringHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.xml.datatype.Duration;

/**
 * Created by gzcheng on 3/21/16.
 */
public class MovieMetaData {

    final public static String THE_TAKE_MANIFEST_IDENTIFIER = "thetake.com";
    final public static String BASELINE_NAMESPACE = "baselineapi.com";
    final public static String SHARE_CLIP_KEY = ":clipshare";

    final public static String OTHER_APP_DATA_ID = "AppID";
    final public static String OTHER_PEOPLE_ID = "PeopleOtherID";

    private static String ITEM_DISPLAY_ORDER = "display_order";
    private static String ITEM_TYPE = "type";
    private static String ITEM_LOCATION = "location";
    private static String ITEM_ZOOM = "zoom";
    private static String ITEM_TEXT = "text";
    private static String ITEM_VIDEO_ID = "video_id";
    private static String ITEM_VIDEO_THUMBNAIL = "video_thumbnail";
    private static String ITEM_LOCATION_THUMBNAIL = "location_thumbnail";
    private static String ITEM_GALLERY_ID = "video_id";
    private static String ITEM_GALLERY_THUMBNAIL = "gallery_thumbnail";


    //final private List<ECGroupData> movieExperiences = new ArrayList<ECGroupData>();
    final private List<ExperienceData> extraECGroups = new ArrayList<ExperienceData>();
    final private List<ExperienceData> imeECGroups = new ArrayList<ExperienceData>();
    final private List<IMEElementsGroup> imeElementGroups = new ArrayList<IMEElementsGroup>();
    final private List<CastData> castsList = new ArrayList<CastData>();
    final private List<CastData> actorsList = new ArrayList<CastData>();
    final private HashMap<String, ExperienceData> experienceIdToExperienceMap = new HashMap<String, ExperienceData>();

    private ExperienceData rootExperience;
    private boolean hasCalledBaselineAPI = false;
    private IMEElementsGroup shareClipIMEGroup;

    private List<List<IMEElement<CastData>>> castIMEElements = new ArrayList<List<IMEElement<CastData>>>() ;

    final static public String movieTitleText = "Man of Steel";

    public String getMainMovieUrl(){
        if (rootExperience != null && rootExperience.audioVisualItems.size() > 0){
            return rootExperience.audioVisualItems.get(0).videoUrl;
        }
        return "";
    }

    public boolean isHasCalledBaselineAPI(){
        return hasCalledBaselineAPI;
    }

    public void setHasCalledBaselineAPI(boolean bCalled){
        hasCalledBaselineAPI = bCalled;
    }

    public static MovieMetaData process(ManifestXMLParser.NextGenManifestData manifest){

        MediaManifestType mediaManifest = manifest.mainManifest;
        ManifestAppDataSetType appDataManifest = manifest.appDataManifest;


        MovieMetaData result = new MovieMetaData();

        HashMap<String, InventoryMetadataType> metaDataAssetsMap = new HashMap<String, InventoryMetadataType>();
        HashMap<String, InventoryVideoType> videoAssetsMap = new HashMap<String, InventoryVideoType>();
        HashMap<String, PresentationType> presentationAssetMap = new HashMap<String, PresentationType>();
        HashMap<String, InventoryImageType> imageAssetsMap = new HashMap<String, InventoryImageType>();
        HashMap<String, PictureGroupType> pictureGroupAssetsMap = new HashMap<String, PictureGroupType>();
        HashMap<String, PictureType> pictureTypeAssetsMap = new HashMap<String, PictureType>();
        HashMap<String, AudioVisualItem> presentationIdToAVItemMap = new HashMap<String, AudioVisualItem>();
        HashMap<String, ECGalleryItem> galleryIdToGalleryItemMap = new HashMap<String, ECGalleryItem>();
        HashMap<BigInteger, String> indexToTextMap = new HashMap<BigInteger, String>();

        HashMap<String, ExperienceData> timeSequenceIdToECGroup = new HashMap<String, ExperienceData>();

        HashMap<String, CastData> peopleIdToCastData = new HashMap<String, CastData>();


        HashMap<String, ExperienceData> experienceChildrenToParentMap = new HashMap<String, ExperienceData>();
        HashMap<String, AppDataType> appDataIdTpAppDataMap = new HashMap<String, AppDataType>();

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
                if (pictureGroup.getPicture() != null && pictureGroup.getPicture().size() > 0){
                    for (PictureType picture : pictureGroup.getPicture()){
                        pictureTypeAssetsMap.put(picture.getPictureID(), picture);
                    }
                }
            }
        }

        if (mediaManifest.getInventory().getTextObject()!= null && mediaManifest.getInventory().getTextObject().size() > 0 &&
                mediaManifest.getInventory().getTextObject().get(0).getTextString() != null){
            for (int i = 0 ; i< mediaManifest.getInventory().getTextObject().get(0).getTextString().size(); i++){
                InventoryTextObjectType.TextString textString = mediaManifest.getInventory().getTextObject().get(0).getTextString().get(i);
                BigInteger index = textString.getIndex();
                if (index != null) {
                    indexToTextMap.put(textString.getIndex(), textString.getValue());
                }else {
                    indexToTextMap.put(BigInteger.valueOf(i+1), textString.getValue());
                }
            }
        }

        if (appDataManifest.getManifestAppData() != null && appDataManifest.getManifestAppData().size() > 0){
            for (AppDataType appData : appDataManifest.getManifestAppData()){
                appDataIdTpAppDataMap.put(appData.getAppID(), appData);

            }
        }

        /**************Experiences ***************/
        if (mediaManifest.getExperiences() != null && mediaManifest.getExperiences().getExperience() != null) {

            //List<ExperienceType> exprienceParentList = new ArrayList<ExperienceType>();
            ExperienceData rootData = null;

            for (ExperienceType experience : mediaManifest.getExperiences().getExperience()) {
                List<ECGalleryItem> galleryItems = new ArrayList<ECGalleryItem>();
                List<AudioVisualItem> avItems = new ArrayList<AudioVisualItem>();
                List<LocationItem> locationItems = new ArrayList<LocationItem>();
                InventoryMetadataType experienceMetaData = metaDataAssetsMap.get(experience.getContentID());

                if (experience.getGallery() != null && experience.getGallery().size() > 0) {
                    for(GalleryType gallery : experience.getGallery()) {
                        String pictureGroupId = gallery.getPictureGroupID();
                        PictureGroupType pictureGroup = pictureGroupAssetsMap.get(pictureGroupId);
                        InventoryMetadataType galleryMataData = metaDataAssetsMap.get(gallery.getContentID());
                        List<PictureItem> pictureItems = new ArrayList<PictureItem>();
                        if (pictureGroup != null) {
                            for (PictureType picture : pictureGroup.getPicture()) {
                                InventoryImageType fullImageData = imageAssetsMap.get(picture.getImageID());
                                InventoryImageType thumbNailImageData = imageAssetsMap.get(picture.getThumbnailImageID());
                                PictureItem pictureItem = new PictureItem(experience.getExperienceID(), galleryMataData, fullImageData, thumbNailImageData);
                                pictureItems.add(pictureItem);
                            }
                        }
                        ECGalleryItem thisItem = new ECGalleryItem(experience.getExperienceID(), galleryMataData, pictureItems);
                        galleryItems.add(thisItem);
                        galleryIdToGalleryItemMap.put(gallery.getGalleryID(), thisItem);
                    }
                }

                if (experience.getAudiovisual() != null){
                    InventoryVideoType video = null;
                    String presentationId = "";
                    if (experience.getAudiovisual().size() > 0) {                           // for video Asset
                        for (AudiovisualType audioVisual : experience.getAudiovisual()) {
                            InventoryMetadataType avMetaData = metaDataAssetsMap.get(audioVisual.getContentID());        // get Video asset by ContentID of its AudioVisual
                            List<ExternalApiData> externalApiDatas = new ArrayList<ExternalApiData>();
                            if (avMetaData.getBasicMetadata() != null && avMetaData.getBasicMetadata().getAltIdentifier() != null){
                                for (ContentIdentifierType identifier : avMetaData.getBasicMetadata().getAltIdentifier()){
                                    externalApiDatas.add(new ExternalApiData(identifier.getNamespace(), identifier.getIdentifier()));
                                }
                            }

                            if (avMetaData.getBasicMetadata() != null && avMetaData.getBasicMetadata().getPeople() != null && avMetaData.getBasicMetadata().getPeople().size() > 0){
                                for (BasicMetadataPeopleType people : avMetaData.getBasicMetadata().getPeople()){
                                    CastData cast = new CastData(people);

                                    if (!StringHelper.isEmpty(cast.getOtherPeopleId()))
                                        peopleIdToCastData.put(cast.getOtherPeopleId(), cast);

                                    result.castsList.add(cast);
                                    if (cast.isActor())
                                        result.actorsList.add(cast);
                                }
                            }

                            presentationId = audioVisual.getPresentationID();
                            PresentationType presentation = presentationAssetMap.get(presentationId);  // get Presentation by presentation id
                            if (presentation.getTrackMetadata().size() > 0 &&
                                    presentation.getTrackMetadata().get(0).getVideoTrackReference().size() > 0 &&
                                    presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().size() > 0) {                                           // get the video id from presentation
                                video = videoAssetsMap.get(presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().get(0));
                            }
                            if (video != null) {
                                //ExperienceData ecData = new ExperienceData(experience, metaData, video, null);


                                AudioVisualItem item = new AudioVisualItem(experience.getExperienceID(), avMetaData, video, externalApiDatas);
                                avItems.add(item);
                                presentationIdToAVItemMap.put(presentationId, item);
                            } else {

                            }
                        }
                    }
                }

                if (experience.getApp() != null && experience.getApp().size() > 0){   // scene locations
                    List<ExperienceAppType> appList = experience.getApp();
                    for (ExperienceAppType appType : appList){
                        String appId = appType.getAppID();
                        String appGroupId = appType.getAppGroupID();


                        LocationItem locationItem = getLocationItemfromMap(appDataIdTpAppDataMap, presentationIdToAVItemMap, galleryIdToGalleryItemMap, imageAssetsMap, appId);
                        if (locationItem != null)
                            locationItems.add(locationItem);
                    }

                }

                if (experience.getTimedSequenceID() != null){

                }

                ExperienceData thisExperience = new ExperienceData(experience, experienceMetaData, avItems, galleryItems, locationItems);

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

            }

            if (rootData != null && rootData.childrenExperience.size() == 2) {
                result.extraECGroups.addAll(rootData.childrenExperience.get(0).childrenExperience);
                for(ExperienceData exp : result.extraECGroups){
                    if (exp.getECGroupType() == MovieMetaData.ECGroupType.LOCATIONS){
                        exp.computeSceneLocationFeature();
                    }
                }
                result.imeECGroups.addAll(rootData.childrenExperience.get(1).childrenExperience);
                result.rootExperience = rootData;
            }


        }



        /*****************End of Experiences****************************/

        /*****************Time Sequence Events****************************/
        if (mediaManifest.getTimedEventSequences() != null && mediaManifest.getTimedEventSequences().getTimedEventSequence().size() > 0){
            for (TimedEventSequenceType timedEventSequence : mediaManifest.getTimedEventSequences().getTimedEventSequence()){
                ExperienceData timedECGroup = null;
                boolean isCast = false;
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
                        String pictureID = timedEvent.getPictureID();
                        OtherIDType otherID = timedEvent.getOtherID();
                        String initialization = timedEvent.getInitialization();

                        TimedEventType.TextGroupID textGroupId = timedEvent.getTextGroupID();

                        PresentationDataItem presentationData = null;
                        if (!StringHelper.isEmpty(eventPID)) {
                            if (presentationIdToAVItemMap.containsKey(eventPID)) {
                                presentationData = presentationIdToAVItemMap.get(eventPID);

                            }
                        }else if (!StringHelper.isEmpty(galleryId)){
                            if (galleryIdToGalleryItemMap.containsKey(galleryId)){
                                presentationData = galleryIdToGalleryItemMap.get(galleryId);

                            }
                        }else if (textGroupId != null && !StringHelper.isEmpty(textGroupId.getValue())){
                            BigInteger index = textGroupId.getIndex();
                            String triviaText = indexToTextMap.get(index);
                            if (!StringHelper.isEmpty(initialization)){                 //this is trivia
                                PictureType picture = pictureTypeAssetsMap.get(initialization);
                                InventoryImageType fullImageData = imageAssetsMap.get(picture.getImageID());
                                InventoryImageType thumbNailImageData = imageAssetsMap.get(picture.getThumbnailImageID());

                                presentationData = new TriviaItem(index, triviaText, fullImageData, thumbNailImageData);
                            }else
                                presentationData = new TextItem(index, triviaText);
                        } else if (!StringHelper.isEmpty(pictureID)){
                            PictureType picture = pictureTypeAssetsMap.get(pictureID);
                            InventoryImageType fullImageData = imageAssetsMap.get(picture.getImageID());
                            InventoryImageType thumbNailImageData = imageAssetsMap.get(picture.getThumbnailImageID());
                            //String imageText= picture.getAlternateText()
                            presentationData = new PictureItem(null, null, fullImageData, thumbNailImageData);
                        } else if (otherID != null) {
                            if (OTHER_APP_DATA_ID.equals(otherID.getNamespace())){
                                String locationId = otherID.getIdentifier();

                                presentationData = getLocationItemfromMap(appDataIdTpAppDataMap, presentationIdToAVItemMap, galleryIdToGalleryItemMap, imageAssetsMap, locationId);

                            } else if (OTHER_PEOPLE_ID.equals(otherID.getNamespace())){
                                isCast = true;
                                CastData cast = peopleIdToCastData.get(otherID.getIdentifier());
                                presentationData = cast;
                            }
                        }/*else if (timedEvent.getLocation() != null){
                            presentationData = new LocationItem(timedEvent.getLocation());
                        }*/


                        if (timedEvent.getProductID() != null ){
                            ExternalApiData data = new ExternalApiData(timedEvent.getProductID().getNamespace(), timedEvent.getProductID().getIdentifier());
                            imeGroup.setExtenralApiData(data);
                        }

                        if (presentationData != null){
                            IMEElement<PresentationDataItem> element = new IMEElement((long)startTime, (long)endTime, presentationData);
                            imeGroup.addElement(element);
                        }

                    }
                }
                Collections.sort(imeGroup.imeElementsList, new Comparator<IMEElement>() {
                    @Override
                    public int compare(IMEElement lhs, IMEElement rhs) {

                        return (int)(lhs.startTimecode - rhs.startTimecode);
                    }
                });



                if (imeGroup.linkedExperience != null && imeGroup.linkedExperience.experienceId.endsWith(SHARE_CLIP_KEY)){
                    result.shareClipIMEGroup = imeGroup;
                }else if (isCast){
                    result.reGroupCastIMEEventGroup(imeGroup);
                } else
                    result.imeElementGroups.add(imeGroup);

            }
        }



        /*****************End of Time Sequence Events****************************/

      //  result.computeExtraECGroups();

        return result;
    }

    private static LocationItem getLocationItemfromMap(HashMap<String, AppDataType> appDataMap, HashMap<String, AudioVisualItem> presentationIdToAVItemMap,
                                                       HashMap<String, ECGalleryItem> galleryIdToGalleryItemMap,
                                                       HashMap<String, InventoryImageType> imageAssetsMap, String appId){
        AppDataType appData = appDataMap.get(appId);
        if(appData == null)
            return null;

        int displayOrder = 0;
        String type = "";
        int zoom = 0;
        AppDataLocationType location = null;
        String text = "";

        ECGalleryItem galleryItem = null;
        AudioVisualItem avItem = null;

        PictureImageData videoThumbnail = null, galleryThumbnail = null, locationThumbnail = null, pinImage = null;

        if (appData.getNVPair() != null && appData.getNVPair().size() > 0){
            for (AppNVPairType pair : appData.getNVPair()){
                if (ITEM_TYPE.equals(pair.getName())){
                    type = pair.getText();
                } else if (ITEM_LOCATION.equals(pair.getName())){
                    location = pair.getLocationSet();
                    if (location.getLocation() != null && location.getLocation().size() > 0){
                        String imageId = location.getLocation().get(0).getIcon();
                        if (!StringHelper.isEmpty(imageId)){
                            InventoryImageType imageType = imageAssetsMap.get(imageId);
                            pinImage = new PictureImageData(imageType.getContainerReference().getContainerLocation(), imageType.getWidth(), imageType.getHeight());
                        }
                    }
                } else if (ITEM_ZOOM.equals(pair.getName())){
                    zoom = pair.getInteger().intValue();
                } else if (ITEM_DISPLAY_ORDER.equals(pair.getName())){
                    displayOrder = pair.getInteger().intValue();
                } else if (ITEM_TEXT.equals(pair.getName())){
                    text = pair.getText();
                } else if (ITEM_VIDEO_ID.equals(pair.getName())){
                    avItem = presentationIdToAVItemMap.get(pair.getPresentationID());
                } else if (ITEM_VIDEO_THUMBNAIL.equals(pair.getName())){
                    String imageId = pair.getPictureID();
                    InventoryImageType imageType = imageAssetsMap.get(imageId);
                    videoThumbnail = new PictureImageData(imageType.getContainerReference().getContainerLocation(), imageType.getWidth(), imageType.getHeight());
                } else if (ITEM_GALLERY_ID.equals(pair.getName())){
                    galleryItem = galleryIdToGalleryItemMap.get(pair.getGallery().getGalleryID());
                } else if (ITEM_GALLERY_THUMBNAIL.equals(pair.getName())){
                    String imageId = pair.getPictureID();
                    InventoryImageType imageType = imageAssetsMap.get(imageId);
                    galleryThumbnail = new PictureImageData(imageType.getContainerReference().getContainerLocation(), imageType.getWidth(), imageType.getHeight());
                } else if (ITEM_LOCATION_THUMBNAIL.equals(pair.getName())){
                    String imageId = pair.getPictureID();
                    InventoryImageType imageType = imageAssetsMap.get(imageId);
                    locationThumbnail = new PictureImageData(imageType.getContainerReference().getContainerLocation(), imageType.getWidth(), imageType.getHeight());

                }
            }
        }
        return new LocationItem(displayOrder, type, location, zoom, text, avItem, galleryItem, videoThumbnail, galleryThumbnail, locationThumbnail, pinImage);
    }

    public List<ExperienceData> getExtraECGroups(){
        return extraECGroups;
    }

    public List<IMEElementsGroup> getImeElementGroups(){
        return imeElementGroups;
    }

    public List<CastData> getActorsList(){
        return  actorsList;
    }

    public ExperienceData findExperienceDataById(String id){
        if (!StringHelper.isEmpty(id)) {
            return experienceIdToExperienceMap.get(id);

        }
        return null;
    }

    public IMEElementsGroup getShareClipIMEGroup(){
        return shareClipIMEGroup;
    }

    public List<List<IMEElement<CastData>>> getCastIMEElements(){
        return castIMEElements;
    }

    /*
    "CHARACTER_NAME": "Kate",
	"PROJECT_ID": 4789621,
	"PROJECT_NAME": "Serving Sara",
	"SORT_ORDER": 9,
	"PROJECT_TYPE": "Feature Film",
	"ROLE": "Actor",
	"ROLE_GROUP": "Cast"
     */

    static public class Filmography{
        @SerializedName("CHARACTER_NAME")
        public String characterName;
        @SerializedName("PROJECT_NAME")
        public String title;
        @SerializedName("PROJECT_ID")
        public String projectId;
        @SerializedName("ROLE")
        public String role;
        @SerializedName("SORT_ORDER")
        public int sortOrder;
        @SerializedName("ROLE_GROUP")
        public String roleGroup;

        private FilmPoster filmPoster;
        public void setFilmPoster(FilmPoster poster){
            filmPoster = poster;
        }

        public String getFilmPosterImageUrl(){
            if (filmPoster !=  null)
                return filmPoster.mediumUrl;
            else
                return "";
        }

        public boolean isFilmPosterRequest(){
            return filmPoster != null;
        }

        public String movieInfoUrl;
    }

    static public class BaselineCastData{

        private static final String FACEBOOK_KEY = "facebook.com";

        public static enum SOCIAL_MEDIA_KEY{
            FACEBOOK_KEY("www.facebook.com"), INSTAGRAM_KEY("www.instagram.com"), TWITTER_KEY("www.twitter.com");
            String keyValue;
            SOCIAL_MEDIA_KEY(String keyValue){
                this.keyValue = keyValue;
            }
        }

        public CastHeadShot headShot;
        public String biography;
        public List<Filmography> filmogrphies;
        private List<CastSocialMedia> socialMedium;

        public String getThumbnailImageUrl(){
            if (headShot != null){
                return headShot.largeUrl;
            }else
                return null;
        }

        public String getIconImageUrl(){
            if (headShot != null){
                return headShot.iconUrl;
            }else
                return null;
        }

        public String getFullImageUrl(){
            if (headShot != null){
                return headShot.fullSizeUrl;
            }else
                return null;
        }
        public void setSocialMedium(List<CastSocialMedia> socialMedium){
            this.socialMedium = socialMedium;
        }

        public List<CastSocialMedia> getSocialMedium(){ return socialMedium;}

        public String getSocialMediaUrl(SOCIAL_MEDIA_KEY Key){
            if (socialMedium != null && socialMedium.size() > 0){
                for (CastSocialMedia socialMedia : socialMedium){
                    if (socialMedia.url.contains(Key.keyValue)){
                        return socialMedia.url;
                    }
                }
            }
            return null;

        }
    }

    static public class FilmPoster{

        @SerializedName("IMAGE_ID")	//Integer	933554
        public String imageId;
        @SerializedName("HEIGHT")//Integer	3388348
        public int hegiht;
        @SerializedName("WEIGHT")//Integer	3388348
        public int weight;
        @SerializedName("CAPTION")//String	Laurence Fishburne
        public String name;
        @SerializedName("FULL_URL")//String	http://media.baselineresearch.com/images/933554/933554_full.jpg
        public String fullSizeUrl;
        @SerializedName("LARGE_URL")//String	http://media.baselineresearch.com/images/933554/933554_large.jpg
        public String largeUrl;
        @SerializedName("MEDIUM_URL")//String	http://media.baselineresearch.com/images/933554/933554_medium.jpg
        public String mediumUrl;
        @SerializedName("SMALL_URL")//String	http://media.baselineresearch.com/images/933554/933554_small.jpg
        public String smallUrl;
        @SerializedName("LARGE_THUMBNAIL_URL")  //String	http://media.baselineresearch.com/images/933554/933554_gThumb.jpg
        public String largeThumbnailUrl;
        @SerializedName("THUMBNAIL_URL")	//String	http://media.baselineresearch.com/images/933554/933554_sThumb.jpg
        public String thumbnailUrl;
        @SerializedName("ICON_URL")	//String	http://media.baselineresearch.com/images/933554/933554_icon.jpg
        public String iconUrl;

        public static FilmPoster getDefaultEmptyPoster(){
            FilmPoster empty = new FilmPoster();
            empty.fullSizeUrl = empty.largeUrl = empty.mediumUrl = empty.smallUrl = empty.largeThumbnailUrl = empty.thumbnailUrl = empty.iconUrl = "android.resource://com.wb.nextgen/drawable/poster_blank";
            empty.imageId = "0";
            return empty;
        }

    }

    static public class CastHeadShot{

        @SerializedName("IMAGE_ID")	//Integer	933554
        public String imageId;
        @SerializedName("PERSON_ID")//Integer	3388348
        public String personId;
        @SerializedName("PERSON_NAME")//String	Laurence Fishburne
        public String name;
        @SerializedName("FULL_URL")//String	http://media.baselineresearch.com/images/933554/933554_full.jpg
        public String fullSizeUrl;
        @SerializedName("LARGE_URL")//String	http://media.baselineresearch.com/images/933554/933554_large.jpg
        public String largeUrl;
        @SerializedName("MEDIUM_URL")//String	http://media.baselineresearch.com/images/933554/933554_medium.jpg
        public String mediumUrl;
        @SerializedName("SMALL_URL")//String	http://media.baselineresearch.com/images/933554/933554_small.jpg
        public String smallUrl;
        @SerializedName("LARGE_THUMBNAIL_URL")  //String	http://media.baselineresearch.com/images/933554/933554_gThumb.jpg
        public String largeThumbnailUrl;
        @SerializedName("THUMBNAIL_URL")	//String	http://media.baselineresearch.com/images/933554/933554_sThumb.jpg
        public String thumbnailUrl;
        @SerializedName("ICON_URL")	//String	http://media.baselineresearch.com/images/933554/933554_icon.jpg
        public String iconUrl;
    }

    static public class CastSocialMedia{
        @SerializedName("HANDLE")	//Integer	933554
        public String handle;
        @SerializedName("URL")//Integer	3388348
        public String url;
    }


    static public class CastData extends PresentationDataItem{

        final public String displayName;
        final public String charactorName;
        final public String firstGivenName;
        final public String secondGivenName;
        final public String lastName;
        final public String gender;
        private String baselineApiActorId;
        private String peopleOtherId;
        final public String job;
        public BaselineCastData baselineCastData;

        public CastData(BasicMetadataPeopleType castInfo){
            super(null, null, null);
            if (castInfo.getJob() != null && castInfo.getJob().size() > 0){     // this may have multiple values
                job = castInfo.getJob().get(0).getJobFunction().getValue();
                if (castInfo.getJob().get(0).getCharacter() != null && castInfo.getJob().get(0).getCharacter().size() > 0)
                    charactorName = castInfo.getJob().get(0).getCharacter().get(0);
                else
                    charactorName = "";

            }else{
                job = "";
                charactorName = "";
            }

            if (castInfo.getName() != null){
                lastName = castInfo.getName().getFamilyName();
                firstGivenName = castInfo.getName().getFirstGivenName();
                secondGivenName = castInfo.getName().getSecondGivenName();
                if (castInfo.getName().getDisplayName() != null && castInfo.getName().getDisplayName().size() > 0) {
                    displayName = castInfo.getName().getDisplayName().get(0).getValue();
                }else{
                    displayName = firstGivenName + " " + lastName;
                }

            }else{
                lastName = "";
                firstGivenName = "";
                secondGivenName = "";
                displayName = "";
            }

            if (castInfo.getGender() != null){
                gender = castInfo.getGender();
            }else{
                gender = "";
            }

            if (castInfo.getIdentifier() != null){
                for (PersonIdentifierType personId : castInfo.getIdentifier()) {
                    if (BASELINE_NAMESPACE.equals(personId.getNamespace()) ) {
                        baselineApiActorId = personId.getIdentifier();
                    } else if (OTHER_PEOPLE_ID.equals(personId.getNamespace())){
                        peopleOtherId = personId.getIdentifier();
                    }
                }
            }
            id = peopleOtherId;
            title = displayName;
            parentExperienceId = null;
        }

        public boolean isActor(){
            return "Actor".equalsIgnoreCase(job);
        }

        public BaselineCastData getBaselineCastData() {
            return baselineCastData;
        }

        public String getBaselineActorId(){
            return baselineApiActorId;
        }

        public String getOtherPeopleId() {
            return peopleOtherId;
        }

        public String getPosterImgUrl(){
            return "";
        }

    }

    public static enum ECGroupType{
        FEATURETTES, VISUAL_EFFECT, GALLERY, MIX, EXTERNAL_APP, LOCATIONS, UNKNOWN
    }

    static public class PictureImageData{

        final public String url;
        final public int width;
        final public int height;
        public PictureImageData(String url, int width, int height){
            this.url = url;
            this.width = width;
            this.height = height;
        }
    }

    static public class PresentationImageData{
        public final String imageUrl;
        public final int width;
        public final int height;
        public PresentationImageData(BasicMetadataInfoType.ArtReference artReference){
            imageUrl = artReference.getValue();
            String dimensionStr = artReference.getResolution();
            if (!StringHelper.isEmpty(dimensionStr)){
                int tempW = 0, tempH = 0;
                try {
                    int xPostion = dimensionStr.indexOf("x");
                    String w = dimensionStr.substring(0, xPostion);
                    String h = dimensionStr.substring(xPostion + 1, dimensionStr.length() );
                    tempW = Integer.parseInt(w);
                    tempH = Integer.parseInt(h);
                }catch(Exception ex){}
                width = tempW;
                height = tempH;
            }else{
                width = height = 0;
            }
        }
    }

    static public abstract class PresentationDataItem{
        protected String id;
        protected String title;
        protected String parentExperienceId;
        protected String posterImgUrl;

        public PresentationDataItem(InventoryMetadataType metaData, String parentExperienceId){
            BasicMetadataInfoType localizedInfo = null;
            if (metaData != null && metaData.getBasicMetadata().getLocalizedInfo() != null && metaData.getBasicMetadata().getLocalizedInfo().size() > 0) {
                localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
                title = localizedInfo.getTitleDisplayUnlimited();
                this.id = metaData.getContentID();
            } else{
                title = "";
                this.id = "";
            }
            this.parentExperienceId = parentExperienceId;
        }

        public PresentationDataItem(String id, String title, String parentExperienceId){
            this.id = id;
            this.title = title;
            this.parentExperienceId = parentExperienceId;
        }

        public String getId(){
            return id;
        }

        public String getTitle(){
            return title;
        }

        public String getParentExperienceId(){
            return parentExperienceId;
        }

        public abstract String getPosterImgUrl();
    }

    static public class TextItem extends PresentationDataItem{
        final public BigInteger index;

        public TextItem(BigInteger index, String text){
            super("",text, null);
            this.index = index;
        }
        public String getPosterImgUrl(){
            return "";
        }
    }

    static public class TriviaItem extends PresentationDataItem{
        public final PictureItem pictureItem;
        public final TextItem textItem;
        public TriviaItem(BigInteger index, String text, InventoryImageType fullImg, InventoryImageType thumbnailImg){
            super("",text, null);
            textItem = new TextItem(index, text);
            pictureItem = new PictureItem(null, null, fullImg, thumbnailImg);
        }
        public String getTitle(){
            if (textItem != null)
                return textItem.getTitle();
            else
                return "";
        }

        public String getPosterImgUrl(){
            if (pictureItem != null)
                return pictureItem.getPosterImgUrl();
            else
                return "";
        }
    }

    static public class LocationItem extends PresentationDataItem{
        //public final boolean isFrictional;
        public final String address;
        public final float longitude;
        public final float latitude;
        public final String description;
        public final int zoom;
        public final AudioVisualItem avItem;
        public final ECGalleryItem galleryItem;
        public final PictureImageData videoThumbnail;
        public final PictureImageData galleryThumbnail;
        public final PictureImageData locationThumbnail;
        public final String greaterTitle;
        public final PictureImageData pinImage;

        public LocationItem(int displayOrder, String type, AppDataLocationType appDataLocation, int zoom, String text,
                            AudioVisualItem avItem, ECGalleryItem galleryItem, PictureImageData videoThumbnail, PictureImageData galleryThumbnail, PictureImageData locationThumbnail, PictureImageData pinImage){
            super("", "", null);
            description = text;
            this.zoom = zoom;
            greaterTitle = type;
            AppDataLocationType.Location location = appDataLocation.getLocation().get(0);
            this.address = location.getAddress().toString();
            this.latitude = location.getEarthCoordinate().getLatitude().floatValue();
            this.longitude = location.getEarthCoordinate().getLongitude().floatValue();
            this.title = location.getName();
            this.avItem = avItem;
            this.galleryItem = galleryItem;
            this.videoThumbnail = videoThumbnail;
            this.galleryThumbnail = galleryThumbnail;
            this.locationThumbnail = locationThumbnail;
            this.pinImage = pinImage;
        }

        public String getPosterImgUrl(){
            return NextGenUtils.getPacakageImageUrl(R.drawable.mos_grid_default_logo);
        }
    }

    static public class PictureItem extends PresentationDataItem{
        final public PictureImageData fullImage;
        final public PictureImageData thumbnail;

        public PictureItem(String parentExperienceId, InventoryMetadataType metadata, InventoryImageType fullImage, InventoryImageType thumbnail){
            super(metadata, parentExperienceId);
            this.fullImage = new PictureImageData(fullImage.getContainerReference().getContainerLocation(), fullImage.getWidth(), fullImage.getHeight());
            this.thumbnail = new PictureImageData(thumbnail.getContainerReference().getContainerLocation(), thumbnail.getWidth(), thumbnail.getHeight());
        }

        public String getPosterImgUrl(){
            return thumbnail.url;
        }
    }

    static public class ECGalleryItem extends PresentationDataItem{
        final public List<PictureItem> galleryImages = new ArrayList<PictureItem>();
        public ECGalleryItem(String parentExperienceId, InventoryMetadataType metaData, List<PictureItem> galleryImages){
            super(metaData, parentExperienceId);
            this.galleryImages.addAll(galleryImages);
        }

        @Override
        public String getPosterImgUrl(){
            if (StringHelper.isEmpty(posterImgUrl) ) {
                if (galleryImages.size() > 0){
                    posterImgUrl = galleryImages.get(0).thumbnail.url;
                }
            }
            return posterImgUrl;
        }

    }

    static public class ExternalApiData{
        final public String externalApiName;
        final public String apiUniqueProjectId;
        public ExternalApiData(String apiName, String uniqueId){
            externalApiName = apiName;
            apiUniqueProjectId = uniqueId;
        }
    }

    static public class AudioVisualItem extends PresentationDataItem{
        final public String videoUrl;
        final public String duration;
        final public List<ExternalApiData> externalApiDataList = new ArrayList<ExternalApiData>();
        final PresentationImageData[] images;
        final Duration durationObject;
        public AudioVisualItem(String parentExperienceId, InventoryMetadataType metaData, InventoryVideoType videoData){
            super(metaData, parentExperienceId);
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            if (videoData != null) {
                videoUrl = videoData.getContainerReference().getContainerLocation();

                if (localizedInfo.getArtReference() != null && localizedInfo.getArtReference().size() > 0) {
                    posterImgUrl = localizedInfo.getArtReference().get(0).getValue();
                    images = new PresentationImageData[localizedInfo.getArtReference().size()];
                    for (int i = 0; i< localizedInfo.getArtReference().size(); i++){
                        images[i] = new PresentationImageData(localizedInfo.getArtReference().get(i));
                    }
                }else {
                    images = null;
                    posterImgUrl = null;
                }

                String dValue = "";
                durationObject = metaData.getBasicMetadata().getRunLength();
                try {
                    dValue = metaData.getBasicMetadata().getRunLength().toString();
                }catch ( Exception ex){
                    NextGenLogger.d(F.TAG, ex.getLocalizedMessage());
                }
                duration = dValue;
            }else{
                videoUrl = null;
                posterImgUrl = null;
                duration = null;
                images = null;
                durationObject = null;
            }
        }

        public String getFullDurationString(){
            if (durationObject == null)
                return "";
            int h = durationObject.getHours();
            int m = durationObject.getMinutes();
            int s = durationObject.getSeconds();
            String retString = "";
            if (s > 0)
                retString = s + " sec";
            if (m > 0)
                retString = m + " min " + retString;
            if (h > 0)
                retString = h + " hour " + retString;

            return  retString;
        }

        public AudioVisualItem(String parentExperienceId, InventoryMetadataType metaData, InventoryVideoType videoData, List<ExternalApiData> apiDataList){
            this(parentExperienceId, metaData, videoData);
            if (apiDataList != null)
                externalApiDataList.addAll(apiDataList);
        }

        public String getPreviewImageUrl(){
            if (images != null){
                return images[images.length-1].imageUrl;
            }else
                return null;
        }

        @Override
        public String getPosterImgUrl(){

            return posterImgUrl;
        }
    }


    static public class SceneLocation{
        public final List<AudioVisualItem> avItems = new ArrayList<AudioVisualItem>();
        public final List<ECGalleryItem> galleryItems = new ArrayList<ECGalleryItem>();
        public final List<SceneLocation> childrenSceneLocations = new ArrayList<SceneLocation>();
        public final LocationItem location;
        public final String name;

        public SceneLocation(String name, LocationItem location){
            this.name = name;
            this.location = location;
        }

        public LocationItem getRepresentativeLocationItem(){
            if (location != null){
                return location;
            }else if (childrenSceneLocations != null && childrenSceneLocations.size() > 0){
                return childrenSceneLocations.get(0).getRepresentativeLocationItem();
            }else
                return null;
        }

        public List<LocationItem> getAllSubLocationItems(){
            List<LocationItem> resultList = new ArrayList<LocationItem>();
            if (location != null){
                resultList.add(location);
            }else if (childrenSceneLocations.size() > 0){
                for (SceneLocation child : childrenSceneLocations){
                    resultList.addAll(child.getAllSubLocationItems());
                }
            }
            return resultList;
        }
    }

    static public class ExperienceData {
        final public String title;
        private String posterImgUrl;
        //final public String ecVideoUrl;
        private ECGroupType type = ECGroupType.UNKNOWN;
        final private List<ExperienceData> childrenExperience = new ArrayList<ExperienceData>();
        //final public List<ECGalleryImageItem> galleryItems = new ArrayList<ECGalleryImageItem>();
        final public List<ECGalleryItem> galleryItems = new ArrayList<ECGalleryItem>();
        final public List<AudioVisualItem> audioVisualItems = new ArrayList<AudioVisualItem>();
        final public List<LocationItem> locationItems = new ArrayList<LocationItem>();
        final public String experienceId;
        final private HashMap<String, Integer> childIdToSequenceNumber = new HashMap<String, Integer>();
        private ExternalApiData externalApp;
        final public String timeSequenceId;

        public ExperienceData(ExperienceType experience, InventoryMetadataType metaData, List<AudioVisualItem> avItems, List<ECGalleryItem> galleryItems, List<LocationItem> locationItems){
            experienceId = experience.getExperienceID();     // or experience Id
            if (metaData == null){
                title = null;
                if (experience.getTimedSequenceID() != null && experience.getTimedSequenceID().size() > 0)
                    timeSequenceId = experience.getTimedSequenceID().get(0);
                else
                    timeSequenceId = null;
            }else {
                timeSequenceId = null;
                BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
                title = localizedInfo.getTitleDisplayUnlimited(); // should loop the list and look for the correct language code

                if (experience.getApp() != null && experience.getApp().size() > 0 && experience.getApp().get(0).getAppName() != null && experience.getApp().get(0).getAppName().size() > 0) {
                    externalApp = new ExternalApiData(experience.getApp().get(0).getAppName().get(0).getValue(), "");
                }


                if (localizedInfo != null && localizedInfo.getArtReference() != null && localizedInfo.getArtReference().size() > 0) {
                    posterImgUrl = localizedInfo.getArtReference().get(0).getValue();
                } else if (this.galleryItems.size() > 0) {
                    posterImgUrl = null;
                }
                if (experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0) {

                    for (ExperienceChildType childType : experience.getExperienceChild()) {
                        if (childType.getSequenceInfo() != null) {
                            childIdToSequenceNumber.put(childType.getExperienceID(), childType.getSequenceInfo().getNumber());
                        }
                    }
                }
            }
            if (avItems != null && avItems.size() > 0) {
                audioVisualItems.addAll(avItems);
            }

            if (galleryItems != null && galleryItems.size() > 0) {
                this.galleryItems.addAll(galleryItems);
            }

            if (locationItems != null && locationItems.size() > 0) {
                this.locationItems.addAll(locationItems);
            }
        }

        public String getDuration(){
            if (audioVisualItems.size() > 0){
                return audioVisualItems.get(0).duration;
            }
            return null;
        }

        public ExternalApiData getExternalApp(){
            return externalApp;
        }

        public ECGroupType getECGroupType(){
            if (type == ECGroupType.UNKNOWN ){
                if (galleryItems.size() > 0)
                    type = ECGroupType.GALLERY;
                else if (audioVisualItems.size() > 0)
                    type = ECGroupType.FEATURETTES;
                else if (locationItems.size() > 0)
                    type = ECGroupType.LOCATIONS;
                else if (externalApp != null)
                    type = ECGroupType.EXTERNAL_APP;
                else if (childrenExperience.size() > 0)




                    type = childrenExperience.get(0).getECGroupType();

            }
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
            else if (galleryItems.size() > 0) {
                posterImgUrl = galleryItems.get(0).getPosterImgUrl();
            }else if (audioVisualItems.size() > 0){
                posterImgUrl = audioVisualItems.get(0).getPosterImgUrl();
            }else if (StringHelper.isEmpty(posterImgUrl) && childrenExperience.size() > 0) {
                for (ExperienceData ec : childrenExperience) {
                    if (!StringHelper.isEmpty(ec.getPosterImgUrl())) {
                        posterImgUrl = ec.getPosterImgUrl();
                        break;
                    }
                }
            }
            return posterImgUrl;
        }

        private final List<SceneLocation> sceneLocations = new ArrayList<SceneLocation>();
        public void computeSceneLocationFeature(){
            HashMap<String, SceneLocation> sceneLocationMap = new HashMap<String, SceneLocation>();
            for (ExperienceData child : childrenExperience){
                LocationItem location = child.locationItems.get(0);
                SceneLocation group;
                if (sceneLocationMap.containsKey(location.greaterTitle)){
                    group = sceneLocationMap.get(location.greaterTitle);
                }else{
                    group = new SceneLocation(location.greaterTitle, null);
                    sceneLocations.add(group);
                    sceneLocationMap.put(location.greaterTitle, group);
                }
                SceneLocation finalSceneLocation = new SceneLocation(location.getTitle(), location);

                group.childrenSceneLocations.add(finalSceneLocation);
            }
        }

        public List<SceneLocation> getSceneLocations(){
            return  sceneLocations;
        }
    }

    public static class IMEElementsGroup<T>{
        public final ExperienceData linkedExperience;
        private final List<IMEElement<T>> imeElementsList = new ArrayList<IMEElement<T>>();
        private ExternalApiData externalApiData;
        public void addElement(IMEElement element){
            imeElementsList.add(element);
        }
        public IMEElementsGroup(ExperienceData ecGroupData){
            this.linkedExperience = ecGroupData;
        }

        public void setExtenralApiData(ExternalApiData data){
            externalApiData = data;
        }

        public ExternalApiData getExternalApiData(){
            return externalApiData;
        }

        public List<IMEElement<T>> getIMEElementesList(){
            return  imeElementsList;
        }
        public void init(){};

    }

    public static class IMEElement<T>{
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

    public String getIdentifierForExternalAPI(String key){
        if (!StringHelper.isEmpty(key) && rootExperience != null && rootExperience.audioVisualItems != null && rootExperience.audioVisualItems.size() > 0 &&
                rootExperience.audioVisualItems.get(0).externalApiDataList != null){
            for (ExternalApiData data : rootExperience.audioVisualItems.get(0).externalApiDataList){
                if (key.equals(data.externalApiName))
                    return data.apiUniqueProjectId;
            }
        }
        return null;
    }

    private void reGroupCastIMEEventGroup(IMEElementsGroup<CastData> castCombinedGroup){
        if (castCombinedGroup == null || castCombinedGroup.getIMEElementesList() == null || castCombinedGroup.getIMEElementesList().size() == 0)
            return;
        HashMap<String, List<IMEElement<CastData>>> peopleIDToImeListMap = new HashMap<String, List<IMEElement<CastData>>>();
        for (IMEElement<CastData> castIMEElement : castCombinedGroup.getIMEElementesList()){
            String peopleId = castIMEElement.imeObject.getOtherPeopleId();
            if (!StringHelper.isEmpty(peopleId)){
                List<IMEElement<CastData>> thisIMEList;
                if (peopleIDToImeListMap.containsKey(peopleId)){
                    thisIMEList = peopleIDToImeListMap.get(peopleId);
                }else{
                    thisIMEList = new ArrayList<IMEElement<CastData>>();
                    peopleIDToImeListMap.put(peopleId, thisIMEList);
                    castIMEElements.add(thisIMEList);
                }
                thisIMEList.add(castIMEElement);
            }
        }



        //castIMEGroups
    }
}
