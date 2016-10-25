package com.wb.nextgenlibrary.data;

import com.google.gson.annotations.SerializedName;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.parser.LocalizableMetaDataInterface;
import com.wb.nextgenlibrary.parser.ManifestXMLParser;
import com.wb.nextgenlibrary.parser.appdata.AppDataLocationType;
import com.wb.nextgenlibrary.parser.appdata.AppDataType;
import com.wb.nextgenlibrary.parser.appdata.AppNVPairType;
import com.wb.nextgenlibrary.parser.appdata.ManifestAppDataSetType;
import com.wb.nextgenlibrary.parser.cpestyle.CPEStyleSetType;
import com.wb.nextgenlibrary.parser.cpestyle.ExperienceMenuMapType;
import com.wb.nextgenlibrary.parser.cpestyle.NodeStyleType;
import com.wb.nextgenlibrary.parser.cpestyle.ThemeType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.AppGroupType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.AudioClipRefType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.AudiovisualClipRefType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.AudiovisualType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.ExperienceAppType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.ExperienceChildType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.ExperienceType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.GalleryType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryAudioType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryImageType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryInteractiveType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryMetadataType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryTextObjectType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryVideoType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.MediaManifestType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.OtherIDType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.PictureGroupType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.PictureType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.PlayableSequenceListType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.PlayableSequenceType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.PresentationType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.TimecodeType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.TimedEventSequenceType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.TimedEventType;
import com.wb.nextgenlibrary.parser.md.schema.v2_3.BasicMetadataInfoType;
import com.wb.nextgenlibrary.parser.md.schema.v2_3.BasicMetadataPeopleType;
import com.wb.nextgenlibrary.parser.md.schema.v2_3.ContentIdentifierType;
import com.wb.nextgenlibrary.parser.md.schema.v2_3.PersonIdentifierType;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.datatype.Duration;

/**
 * Created by gzcheng on 3/21/16.
 */
public class MovieMetaData {

	final public static String THE_TAKE_MANIFEST_IDENTIFIER = "thetake.com";
    final public static String BASELINE_NAMESPACE = "baselineapi.com";
    final public static String SHARE_CLIP_KEY = "clipshare";
	final public static String SHARE_CLIP_SUBTYPE = "clip share";

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
    private static String ITEM_GALLERY_ID = "gallery_id";
    private static String ITEM_GALLERY_THUMBNAIL = "gallery_thumbnail";
    private static String ITEM_EXPERIENCE_ID = "experience_id";


    //final private List<ECGroupData> movieExperiences = new ArrayList<ECGroupData>();
    final private List<ExperienceData> extraECGroups = new ArrayList<ExperienceData>();
    final private List<ExperienceData> imeECGroups = new ArrayList<ExperienceData>();
    final private List<IMEElementsGroup> imeElementGroups = new ArrayList<IMEElementsGroup>();
    final private List<CastData> castsList = new ArrayList<CastData>();
    final private List<CastData> actorsList = new ArrayList<CastData>();
    final private HashMap<String, ExperienceData> experienceIdToExperienceMap = new HashMap<String, ExperienceData>();

    private ExperienceData rootExperience;
    private boolean hasCalledBaselineAPI = false;

    private List<List<IMEElement<CastData>>> castIMEElements = new ArrayList<List<IMEElement<CastData>>>() ;

    private HashMap<String, StyleData.ExperienceStyle> experienceStyleMap = new HashMap<String, StyleData.ExperienceStyle>();

    final static public String movieTitleText = "Man of Steel";

    public String getMainMovieUrl(){
        if (rootExperience != null && rootExperience.audioVisualItems.size() > 0){
            return rootExperience.audioVisualItems.get(0).videoUrl;
        }
        return "";
    }

    public String getMoiveId(){
        if (rootExperience != null)
            return rootExperience.experienceId;
        else
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
        CPEStyleSetType styleSetType = manifest.cpeStyle;

        MovieMetaData result = new MovieMetaData();

        HashMap<String, InventoryMetadataType> metaDataAssetsMap = new HashMap<String, InventoryMetadataType>();
        HashMap<String, InventoryVideoType> videoAssetsMap = new HashMap<String, InventoryVideoType>();
        HashMap<String, InventoryAudioType> audioAssetsMap = new HashMap<String, InventoryAudioType>();
        HashMap<String, PresentationType> presentationAssetMap = new HashMap<String, PresentationType>();
        HashMap<String, PictureImageData> pictureImageMap = new HashMap<String, PictureImageData>();
        HashMap<String, InventoryInteractiveType> inventoryAssetsMap = new HashMap<String, InventoryInteractiveType>();
        HashMap<String, PictureGroupType> pictureGroupAssetsMap = new HashMap<String, PictureGroupType>();
        HashMap<String, PictureType> pictureTypeAssetsMap = new HashMap<String, PictureType>();
        HashMap<String, AudioVisualItem> presentationIdToAVItemMap = new HashMap<String, AudioVisualItem>();
        HashMap<String, ECGalleryItem> galleryIdToGalleryItemMap = new HashMap<String, ECGalleryItem>();
        HashMap<BigInteger, String> indexToTextMap = new HashMap<BigInteger, String>();

        HashMap<String, AppGroupType> appGroupIdToAppGroupMap = new HashMap<String, AppGroupType>();

        HashMap<String, ExperienceData> timeSequenceIdToECGroup = new HashMap<String, ExperienceData>();

        HashMap<String, CastData> peopleIdToCastData = new HashMap<String, CastData>();

        HashMap<String, PlayableSequenceType> playableSequenceTypeHashMap = new HashMap<String, PlayableSequenceType>();


        HashMap<String, List<ExperienceData>> experienceChildrenToParentMap = new HashMap<String, List<ExperienceData>>();
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

            if (mediaManifest.getInventory().getAudio().size() > 0){
                for (InventoryAudioType audioData : mediaManifest.getInventory().getAudio()){
                    audioAssetsMap.put(audioData.getAudioTrackID(), audioData);
                }
            }

            if (mediaManifest.getInventory().getImage().size() > 0){
                for (InventoryImageType imageData : mediaManifest.getInventory().getImage()){
                    PictureImageData pictureImageData = new PictureImageData(imageData);
                    pictureImageMap.put(pictureImageData.imageId, pictureImageData);
                }
            }

            if (mediaManifest.getInventory().getInteractive() != null && mediaManifest.getInventory().getInteractive().size() > 0){
                for (InventoryInteractiveType interactiveData : mediaManifest.getInventory().getInteractive()){
                    inventoryAssetsMap.put(interactiveData.getInteractiveTrackID(), interactiveData);
                }
            }

            if (mediaManifest.getPlayableSequences() != null && mediaManifest.getPlayableSequences().getPlayableSequence() != null && mediaManifest.getPlayableSequences().getPlayableSequence().size() > 0){
                for (PlayableSequenceListType.PlayableSequence playableSequence : mediaManifest.getPlayableSequences().getPlayableSequence()){
                    playableSequenceTypeHashMap.put(playableSequence.getPlayableSequenceID(), playableSequence);
                }
            }
        }

        if (mediaManifest.getAppGroups().getAppGroup().size() > 0){
            for(AppGroupType appGroup : mediaManifest.getAppGroups().getAppGroup()){
                appGroupIdToAppGroupMap.put(appGroup.getAppGroupID(), appGroup);
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

        if (mediaManifest.getInventory().getTextObject()!= null && mediaManifest.getInventory().getTextObject().size() > 0){
            InventoryTextObjectType textObjectType = getMatchingLocalizableObject(mediaManifest.getInventory().getTextObject());

            if (NextGenExperience.matchesClientLocale(textObjectType.getLanguage())){
                if (textObjectType.getTextString() != null){
                    for (int i = 0 ; i< mediaManifest.getInventory().getTextObject().get(0).getTextString().size(); i++){
                        InventoryTextObjectType.TextString textString = textObjectType.getTextString().get(i);
                        BigInteger index = textString.getIndex();
                        if (index != null) {
                            indexToTextMap.put(textString.getIndex(), textString.getValue());
                        }else {
                            indexToTextMap.put(BigInteger.valueOf(i+1), textString.getValue());
                        }
                    }
                }

            }


        }

        if (appDataManifest != null && appDataManifest.getManifestAppData() != null && appDataManifest.getManifestAppData().size() > 0){
            for (AppDataType appData : appDataManifest.getManifestAppData()){
                appDataIdTpAppDataMap.put(appData.getAppID(), appData);

            }
        }

        if (styleSetType != null) {

            HashMap<String, StyleData.ThemeData> themeDataHashMap = new HashMap<>();
            if (styleSetType.getTheme() != null && styleSetType.getTheme().size() > 0){
                for (ThemeType theme : styleSetType.getTheme()) {
                    StyleData.ThemeData themeData = new StyleData.ThemeData(theme, pictureImageMap);
                    themeDataHashMap.put(theme.getThemeID(), themeData);
                }
            }

            HashMap<String, StyleData.NodeStyleData> nodeStyleTypeHashMap = new HashMap<>();
            if (styleSetType.getNodeStyle() != null && styleSetType.getNodeStyle().size() > 0) {
                for (NodeStyleType nodeStyle : styleSetType.getNodeStyle()){
                    StyleData.NodeStyleData thisStyle = new StyleData.NodeStyleData(nodeStyle, themeDataHashMap,
                            pictureGroupAssetsMap, pictureImageMap, presentationAssetMap, videoAssetsMap, audioAssetsMap);
                    nodeStyleTypeHashMap.put(thisStyle.nodeStyleId, thisStyle);
                }
            }

            if (styleSetType.getExperienceStyleMap() != null && styleSetType.getExperienceStyleMap().size() > 0) {
                for (ExperienceMenuMapType menuMapType : styleSetType.getExperienceStyleMap()) {
                    StyleData.ExperienceStyle experienceStyle = new StyleData.ExperienceStyle(menuMapType, nodeStyleTypeHashMap);
                    result.experienceStyleMap.put(experienceStyle.getExperienceId(), experienceStyle);
                }
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
                List<InteractiveItem> interactiveItems = new ArrayList<InteractiveItem>();
                boolean isRootExperience = false;
                String subtype = null;
                InventoryMetadataType experienceMetaData = metaDataAssetsMap.get(experience.getContentID());

                if (experience.getExperienceID().contains(SHARE_CLIP_KEY))
                    subtype = SHARE_CLIP_KEY;

                if (experience.getGallery() != null && experience.getGallery().size() > 0) {
                    for(GalleryType gallery : experience.getGallery()) {
                        String pictureGroupId = gallery.getPictureGroupID();
                        PictureGroupType pictureGroup = pictureGroupAssetsMap.get(pictureGroupId);
                        InventoryMetadataType galleryMataData = metaDataAssetsMap.get(gallery.getContentID());
                        List<PictureItem> pictureItems = new ArrayList<PictureItem>();
                        if (pictureGroup != null) {
                            for (PictureType picture : pictureGroup.getPicture()) {
                                PictureImageData fullImageData = pictureImageMap.get(picture.getImageID());
                                PictureImageData thumbNailImageData = pictureImageMap.get(picture.getThumbnailImageID());
                                PictureItem pictureItem = new PictureItem(experience.getExperienceID(), galleryMataData, fullImageData, thumbNailImageData);
                                pictureItems.add(pictureItem);
                            }
                        }

                        if (gallery.getSubType() != null && gallery.getSubType().size() > 0){
                            subtype = gallery.getSubType().get(0);
                        }

                        ECGalleryItem thisItem = new ECGalleryItem(experience.getExperienceID(), galleryMataData, pictureItems, subtype);
                        galleryItems.add(thisItem);

                        galleryIdToGalleryItemMap.put(gallery.getGalleryID(), thisItem);
                    }
                }

                if (experience.getAudiovisual() != null){
                    InventoryVideoType video = null;
                    InventoryAudioType audio = null;
                    if (experience.getAudiovisual().size() > 0) {                           // for video Asset
                        for (AudiovisualType audioVisual : experience.getAudiovisual()) {
                            if (audioVisual.getType().equals("Main"))       // root experience check
                                isRootExperience = true;

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
                                Collections.sort(result.actorsList, new Comparator<CastData>() {
                                    @Override
                                    public int compare(CastData lhs, CastData rhs) {

                                        return (int)(lhs.order - rhs.order);
                                    }
                                });
                            }

                            List<String> presentationIds = new ArrayList<>();
                            if (!StringHelper.isEmpty(audioVisual.getPresentationID()) ){
                                presentationIds.add(audioVisual.getPresentationID());
                            }

                            if (presentationIds.size() == 0 && !StringHelper.isEmpty(audioVisual.getPlayableSequenceID())){
                                PlayableSequenceType playableSequenceType = playableSequenceTypeHashMap.get(audioVisual.getPlayableSequenceID());
                                if (playableSequenceType != null && playableSequenceType.getClip() != null && playableSequenceType.getClip().size() > 0){
                                    for (AudiovisualClipRefType clipRefType : playableSequenceType.getClip()) {
                                        presentationIds.add(clipRefType.getPresentationID());

                                    }
                                }
                            }

                            for(String presentationId : presentationIds) {

                                if (!StringHelper.isEmpty(presentationId)) {
                                    PresentationType presentation = presentationAssetMap.get(presentationId);  // get Presentation by presentation id
                                    if (presentation.getTrackMetadata() != null && presentation.getTrackMetadata().size() > 0 &&
                                            presentation.getTrackMetadata().get(0).getVideoTrackReference().size() > 0){
                                        if (presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().size() > 0)                                          // get the video id from presentation
                                            video = videoAssetsMap.get(presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().get(0));

                                        if (presentation.getTrackMetadata().get(0).getAudioTrackReference().get(0).getAudioTrackID().size() > 0)                                          // get the video id from presentation
                                            audio = audioAssetsMap.get(presentation.getTrackMetadata().get(0).getAudioTrackReference().get(0).getAudioTrackID().get(0));
                                    }
                                }

                                if (!StringHelper.isEmpty(presentationId) && video != null) {
                                    //ExperienceData ecData = new ExperienceData(experience, metaData, video, null);
                                    if (audioVisual.getSubType() != null && audioVisual.getSubType().size() > 0)
                                        subtype = audioVisual.getSubType().get(0);
                                    AudioVisualItem item = new AudioVisualItem(experience.getExperienceID(), avMetaData, video, audio, externalApiDatas, subtype);
                                    avItems.add(item);
                                    presentationIdToAVItemMap.put(presentationId, item);
                                }
                            }

                        }
                    }
                }

                if (experience.getApp() != null && experience.getApp().size() > 0){   // scene locations
                    List<ExperienceAppType> appList = experience.getApp();
                    for (ExperienceAppType appType : appList){
                        String appId = appType.getAppID();
                        String appGroupId = appType.getAppGroupID();

                        if (!StringHelper.isEmpty(appId)) {     // appData
                            LocationItem locationItem = getLocationItemfromMap(appDataIdTpAppDataMap, pictureImageMap, appId);
                            if (locationItem != null)
                                locationItems.add(locationItem);
                        } else if (!StringHelper.isEmpty(appGroupId)){      // interactive
                            AppGroupType appGroupType = appGroupIdToAppGroupMap.get(appGroupId);
                            InteractiveItem interactiveItem = new InteractiveItem(appGroupType, inventoryAssetsMap);
                            interactiveItems.add(interactiveItem);
                        }
                    }

                }

                if (experience.getTimedSequenceID() != null){

                }

                StyleData.ExperienceStyle expStyle = result.experienceStyleMap.containsKey(experience.getExperienceID()) ?
                                                    result.experienceStyleMap.get(experience.getExperienceID()) : null;
                ExperienceData thisExperience = new ExperienceData(experience, experienceMetaData, avItems, galleryItems, locationItems, interactiveItems, expStyle);


                result.experienceIdToExperienceMap.put(experience.getExperienceID(), thisExperience);
                List<ExperienceData> parentGroups = experienceChildrenToParentMap.get(experience.getExperienceID());
                if (parentGroups != null && parentGroups.size() > 0){
                    for (ExperienceData parentGroup : parentGroups)
                        parentGroup.addChild(thisExperience);
                }

                if (isRootExperience){
                    rootData = thisExperience;
                }
                if (experience.getTimedSequenceID() != null && experience.getTimedSequenceID().size() > 0 && !StringHelper.isEmpty(experience.getTimedSequenceID().get(0)) ){
                    timeSequenceIdToECGroup.put(experience.getTimedSequenceID().get(0), thisExperience);
                }

                if (experience.getExperienceChild() != null && experience.getExperienceChild().size() > 0) {
                    for (ExperienceChildType child : experience.getExperienceChild()) {
                        if (result.experienceIdToExperienceMap.containsKey(child.getExperienceID())){   // if child experience has already been created.
                            thisExperience.addChild(result.experienceIdToExperienceMap.get(child.getExperienceID()));
                        }else if (experienceChildrenToParentMap.containsKey(child.getExperienceID())){  // if child exp has not been created, but children list in the hash table already exist
                            List<ExperienceData> parents =  experienceChildrenToParentMap.get(child.getExperienceID());
                            parents.add(thisExperience);
                        } else {                    // if the children list has not been created in the hash table yet
                            List<ExperienceData> parents = new ArrayList<ExperienceData>();
                            parents.add(thisExperience);
                            experienceChildrenToParentMap.put(child.getExperienceID(), parents);    //skip these IDs when encounter.

                        }
                    }
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
                if (rootData.audioVisualItems != null && rootData.audioVisualItems.size() > 0) {
                    String inter = rootData.audioVisualItems.get(0).videoUrl;
                }
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
                    for (int i = 0; i < timedEventSequence.getTimedEvent().size(); i++){
                        TimedEventType timedEvent = timedEventSequence.getTimedEvent().get(i);

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
                                PictureImageData fullImageData = pictureImageMap.get(picture.getImageID());
                                PictureImageData thumbNailImageData = pictureImageMap.get(picture.getThumbnailImageID());

                                presentationData = new TriviaItem(index, triviaText, fullImageData, thumbNailImageData);
                            }else
                                presentationData = new TextItem(index, triviaText);
                        } else if (!StringHelper.isEmpty(pictureID)){
                            PictureType picture = pictureTypeAssetsMap.get(pictureID);
                            PictureImageData fullImageData = pictureImageMap.get(picture.getImageID());
                            PictureImageData thumbNailImageData = pictureImageMap.get(picture.getThumbnailImageID());
                            //String imageText= picture.getAlternateText()
                            presentationData = new PictureItem(null, null, fullImageData, thumbNailImageData);
                        } else if (otherID != null) {
                            if (OTHER_APP_DATA_ID.equals(otherID.getNamespace())){
                                String locationId = otherID.getIdentifier();

                                presentationData = getLocationItemfromMap(appDataIdTpAppDataMap, pictureImageMap, locationId);

                            } else if (OTHER_PEOPLE_ID.equals(otherID.getNamespace())){
                                isCast = true;
                                CastData cast = peopleIdToCastData.get(otherID.getIdentifier());
                                presentationData = cast;
                            }
                        }


                        if (timedEvent.getProductID() != null ){
                            ExternalApiData data = new ExternalApiData(timedEvent.getProductID().getNamespace(), timedEvent.getProductID().getIdentifier());
                            imeGroup.setExtenralApiData(data);
                        }

                        if (presentationData != null){
                            IMEElement<PresentationDataItem> element = new IMEElement((long)startTime, (long)endTime, presentationData, i);
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



                if (isCast){
                    result.reGroupCastIMEEventGroup(imeGroup);
                } else
                    result.imeElementGroups.add(imeGroup);

            }
        }

        /*****************End of Time Sequence Events****************************/

        return result;
    }


    public static <T extends LocalizableMetaDataInterface>T getMatchingLocalizableObject(List<T> localizableObjects){
        if (localizableObjects != null && localizableObjects.size() > 0) {
            Locale clientLocale = NextGenExperience.getClientLocale();
            int matchingIndex = -1, secondaryMatch = -1, engLangIndex = -1, emptyLocaleIndex = -1;
            for (int i = 0; i < localizableObjects.size() ; i++){
                T localizableObject = localizableObjects.get(i);

                String thisLang = localizableObject.getLanguage();
                if (thisLang == null || !StringHelper.isEmpty(thisLang))
                    emptyLocaleIndex = i;
                else if (thisLang.contains("-")){           // this is a locale
                    thisLang = thisLang.replace("-", "_");
                    Locale thisLocale = new Locale(thisLang);

                    if (clientLocale.equals(thisLocale)) {       // this is a perfect match
                        matchingIndex = i;
                        break;
                    } else if (clientLocale.getLanguage().equals(thisLocale.getLanguage())){
                        secondaryMatch = i;
                    }else if (Locale.US.equals(thisLocale)) {
                        engLangIndex = i;
                    }
                }else{
                    if (clientLocale.getLanguage().equals(thisLang)){
                        secondaryMatch = i;
                    } else if (Locale.US.getLanguage().equals(thisLang)){
                        engLangIndex = i;
                    }
                }

            }

            if (matchingIndex != -1)
                return localizableObjects.get(matchingIndex);
            else if (secondaryMatch != -1)
                return localizableObjects.get(secondaryMatch);
            else if (emptyLocaleIndex != -1)
                return localizableObjects.get(emptyLocaleIndex);
            else if (engLangIndex != -1)
                return localizableObjects.get(engLangIndex);
            else
                return localizableObjects.get(0);

        }
        return null;
    }

    private static LocationItem getLocationItemfromMap(HashMap<String, AppDataType> appDataMap,
                                                       HashMap<String, PictureImageData> imageAssetsMap,
                                                       String appId){
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
        String experienceId = null;

        PictureImageData videoThumbnail = null, locationThumbnail = null, pinImage = null;

        if (appData.getNVPair() != null && appData.getNVPair().size() > 0){
            for (AppNVPairType pair : appData.getNVPair()){
                if (ITEM_TYPE.equals(pair.getName())){
                    type = pair.getText();
                } else if (ITEM_LOCATION.equals(pair.getName())){
                    location = pair.getLocationSet();
                    if (location.getLocation() != null && location.getLocation().size() > 0){
                        String imageId = location.getLocation().get(0).getIcon();
                        if (!StringHelper.isEmpty(imageId)){
                            pinImage = imageAssetsMap.get(imageId);
                        }
                    }
                } else if (ITEM_ZOOM.equals(pair.getName())){
                    zoom = pair.getInteger().intValue();
                } else if (ITEM_DISPLAY_ORDER.equals(pair.getName())){
                    displayOrder = pair.getInteger().intValue();
                } else if (ITEM_TEXT.equals(pair.getName())){
                    text = pair.getText();
                } else if (ITEM_LOCATION_THUMBNAIL.equals(pair.getName())){
                    String imageId = pair.getPictureID();
                    locationThumbnail = imageAssetsMap.get(imageId);

                } else if (ITEM_EXPERIENCE_ID.equals(pair.getName())){
                    experienceId = pair.getExperienceID();
                }
            }
        }

        return new LocationItem(appId, displayOrder, type, location, zoom, text, locationThumbnail, pinImage, experienceId);
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

        @SerializedName("POSTERS")
        public List<FilmPoster> filmPosters;

        public String getFilmPosterImageUrl(){
            if (filmPosters !=  null && filmPosters.size() > 0)
                return filmPosters.get(0).mediumUrl;
            else
                return "";
        }

        public boolean isFilmPosterRequest(){
            return filmPosters != null;
        }

        public String movieInfoUrl;
    }

    static public class BaselineCastData{

        private static final String FACEBOOK_KEY = "facebook.com";

        public static enum SOCIAL_MEDIA_KEY{
            FACEBOOK_KEY("facebook.com"), INSTAGRAM_KEY("instagram.com"), TWITTER_KEY("twitter.com");
            String keyValue;
            SOCIAL_MEDIA_KEY(String keyValue){
                this.keyValue = keyValue;
            }
        }

        public List<CastHeadShot> headShots;
        @SerializedName("SHORT_BIO")
        public String biography;
        @SerializedName("FILMOGRAPHY")
        public List<Filmography> filmogrphies;
        @SerializedName("SOCIAL_ACCOUNTS")
        private List<CastSocialMedia> socialMedium;

        public String getThumbnailImageUrl(){
            if (headShots != null && headShots.size() > 0){
                return headShots.get(0).largeUrl;
            }else
                return null;
        }

        public String getIconImageUrl(){
            if (headShots != null && headShots.size() > 0){
                return headShots.get(0).iconUrl;
            }else
                return null;
        }

        public String getFullImageUrl(){
            if (headShots != null && headShots.size() > 0){
                return headShots.get(0).fullSizeUrl;
            }else
                return null;
        }

        public List<CastHeadShot> getGallery(){
            return headShots;
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

        public boolean hasGotAllFilmPictures(){
            if (filmogrphies != null && filmogrphies.size() > 0){
                for(Filmography film : filmogrphies){
                    if (!film.isFilmPosterRequest())
                        return false;
                }
            }
            return true;
        }

        public void filterText() {
            if (biography != null) {
                biography = biography.replaceAll("&amp;", "&");
            }
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
        @SerializedName(value="PERSON_NAME", alternate={"CAPTION"})//String	Laurence Fishburne
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
        final public int order;
        private String baselineApiActorId;
        private String peopleOtherId;
        final public String job;
        public BaselineCastData baselineCastData;

        public CastData(BasicMetadataPeopleType castInfo){
            super(null, null, null);
            if (castInfo!= null && castInfo.getJob() != null && castInfo.getJob().size() > 0){     // this may have multiple values
                job = castInfo.getJob().get(0).getJobFunction().getValue();
                if (castInfo.getJob().get(0).getCharacter() != null && castInfo.getJob().get(0).getCharacter().size() > 0)
                    charactorName = castInfo.getJob().get(0).getCharacter().get(0);
                else
                    charactorName = "";
                if (castInfo.getJob().get(0).getBillingBlockOrder() != null)
                    order = castInfo.getJob().get(0).getBillingBlockOrder();
                else
                    order = 0;
            }else{
                order = 0;
                job = "";
                charactorName = "";
            }

            if (castInfo!= null && castInfo.getName() != null){
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

            if (castInfo!= null && castInfo.getGender() != null){
                gender = castInfo.getGender();
            }else{
                gender = "";
            }

            if (castInfo!= null && castInfo.getIdentifier() != null){
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
        FEATURETTES, VISUAL_EFFECT, GALLERY, MIX, EXTERNAL_APP, LOCATIONS, INTERACTIVE, UNKNOWN, ACTORS
    }

    static public class PictureImageData{

        final public String url;
        final public int width;
        final public int height;
        final public String imageId;

        public PictureImageData(InventoryImageType inventoryImageType){
            imageId = inventoryImageType.getImageID();
            url = inventoryImageType.getContainerReference().getContainerLocation();
            width = inventoryImageType.getWidth();
            height = inventoryImageType.getHeight();
        }
        /*
        public PictureImageData(String url, int width, int height){
            this.url = url;
            this.width = width;
            this.height = height;
        }*/
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
        protected String summary = "";

        public PresentationDataItem(InventoryMetadataType metaData, String parentExperienceId){
            BasicMetadataInfoType localizedInfo = null;
            if (metaData != null && metaData.getBasicMetadata().getLocalizedInfo() != null && metaData.getBasicMetadata().getLocalizedInfo().size() > 0) {


                localizedInfo = getMatchingLocalizableObject(metaData.getBasicMetadata().getLocalizedInfo());


                if (localizedInfo == null)
                    localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);

                title = localizedInfo.getTitleDisplayUnlimited();
                if (localizedInfo.getSummary190() != null && !StringHelper.isEmpty(localizedInfo.getSummary190().getValue())){
                    summary = localizedInfo.getSummary190().getValue();
                } else if (localizedInfo.getSummary400() != null && !StringHelper.isEmpty(localizedInfo.getSummary400().getValue())){
                    summary = localizedInfo.getSummary400().getValue();
                } else if (localizedInfo.getSummary4000() != null && !StringHelper.isEmpty(localizedInfo.getSummary4000().getValue())){
                    summary = localizedInfo.getSummary4000().getValue();
                }
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

        public String getSummary(){
            return  summary;
        }
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
        public TriviaItem(BigInteger index, String text, PictureImageData fullImg, PictureImageData thumbnailImg){
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
        private PictureImageData locationThumbnail;
        public final String greaterTitle;
        public final PictureImageData pinImage;
        public final String experienceId;
        private ExperienceData experienceData;
        private List<PresentationDataItem> presentationDataItems = new ArrayList<PresentationDataItem>();
        private LocationItem parentSceneLocation;

        public LocationItem(String id, int displayOrder, String type, AppDataLocationType appDataLocation, int zoom, String text,
                            PictureImageData locationThumbnail, PictureImageData pinImage, String experienceId){
            super(id, "", null);
            description = text;
            this.zoom = zoom;
            greaterTitle = type;
            AppDataLocationType.Location location = appDataLocation.getLocation().get(0);
            this.address = location.getAddress().toString();
            this.latitude = location.getEarthCoordinate().getLatitude().floatValue();
            this.longitude = location.getEarthCoordinate().getLongitude().floatValue();
            this.title = location.getName();
            this.locationThumbnail = locationThumbnail;
            this.pinImage = pinImage;
            this.experienceId = experienceId;

        }

        public String getPosterImgUrl(){
            String retImgUrl = null;
            if (locationThumbnail != null)
                retImgUrl = locationThumbnail.url;
            else if (experienceId != null){
                computeFromExperience();
                if (experienceData != null) {
                        retImgUrl =  experienceData.getPosterImgUrl();
                }
            }
            if (!StringHelper.isEmpty(retImgUrl))
                return retImgUrl;
            else
                return getGoogleMapImageUrl(320, 180);
        }

        final static String GOOGLE_MAP_IMAGE_URL = "http://maps.google.com/maps/api/staticmap?center=%s,%s&zoom=%s&size=%sx%s&sensor=false";
        final static String PIN_DEFAULT = "&markers=color:blue|%s,%s";
        final static String PIN_CUSTOM = "&markers=icon:%s|%s,%s";
        public String getGoogleMapImageUrl(int width, int height){
            String pinPortion = "";
            if (pinImage != null && !StringHelper.isEmpty(pinImage.url)){
                pinPortion = String.format(PIN_CUSTOM, pinImage.url, latitude, longitude);

            }else{
                pinPortion = String.format(PIN_DEFAULT, latitude, longitude);
            }
            return String.format(GOOGLE_MAP_IMAGE_URL, latitude, longitude, zoom, width, height) + pinPortion;
        }

        public List<PresentationDataItem> getPresentationDataItems(){
            computeFromExperience();
            return presentationDataItems;
        }

        private void computeFromExperience(){
            if (!StringHelper.isEmpty(experienceId) && experienceData == null){
                MovieMetaData metaData = NextGenExperience.getMovieMetaData();
                if (metaData != null) {
                    experienceData = metaData.experienceIdToExperienceMap.get(experienceId);
                    presentationDataItems = new ArrayList<PresentationDataItem>();

                    if (experienceData.childIdToSequenceNumber.size() > 0){

                        for (String childExpId : experienceData.childIdToSequenceNumber.keySet()){
                            ExperienceData child = metaData.experienceIdToExperienceMap.get(childExpId);
                            experienceData.addChild(child);
                        }

                        for (ExperienceData child : experienceData.getChildrenContents()){
                            if (child.audioVisualItems != null && child.audioVisualItems.size() > 0){
                                presentationDataItems.addAll(child.audioVisualItems);
                            }
                            if (child.galleryItems != null && child.galleryItems.size() > 0){
                                presentationDataItems.addAll(child.galleryItems);
                            }/*
                            if (child.locationItems != null && child.locationItems.size() > 0){
                                child.addAll(child.audioVisualItems);
                            }*/
                        }
                    }
                }
            }
        }

        public String getTitle(){
            if (!StringHelper.isEmpty(experienceId)){
                computeFromExperience();
                if (experienceData != null && !StringHelper.isEmpty(experienceData.title))
                    return experienceData.title;
            }
            return title;
        }

        private String getLocationThumbnailUrl(){
            computeFromExperience();
            if (locationThumbnail != null){
                return locationThumbnail.url;
            }else {
                computeFromExperience();
                if (experienceData != null) {
                    return experienceData.getPosterImgUrl();
                }else
                    return "";
            }
        }
    }

    static public class InteractiveItem {
        final public String type;
        final public String encoding;
        final public String assetLocation;
        final public String runtimeEnvironment;

        public InteractiveItem(AppGroupType appGroupType, HashMap<String, InventoryInteractiveType> inventoryAssetsMap) {
            if (appGroupType.getInteractiveTrackReference() != null &&
                    appGroupType.getInteractiveTrackReference().size() > 0) {
                String interactiveId = appGroupType.getInteractiveTrackReference().get(0).getInteractiveTrackID();

                InventoryInteractiveType inventoryType = inventoryAssetsMap.get(interactiveId);

                type = inventoryType.getType();
                encoding = (inventoryType.getEncoding() != null && inventoryType.getEncoding().size() > 0) ? inventoryType.getEncoding().get(0).getRuntimeEnvironment() : "";
                assetLocation = inventoryType.getContainerReference() != null ? inventoryType.getContainerReference().getContainerLocation() : "";

                if (appGroupType.getInteractiveTrackReference().get(0).getCompatibility().size() > 0) {
                    runtimeEnvironment = appGroupType.getInteractiveTrackReference().get(0).getCompatibility().get(0).getRuntimeEnvironment();
                } else
                    runtimeEnvironment = "";
            } else {
                type = "";
                encoding = "";
                assetLocation = "";
                runtimeEnvironment = "";
            }
        }

        public boolean isAppDataItem(){
            return "other".equals(type);
        }
    }

    static public class PictureItem extends PresentationDataItem{
        final public PictureImageData fullImage;
        final public PictureImageData thumbnail;

        public PictureItem(String parentExperienceId, InventoryMetadataType metadata, PictureImageData fullImage, PictureImageData thumbnail){
            super(metadata, parentExperienceId);
            this.fullImage = fullImage;
            this.thumbnail = thumbnail;
        }

        public String getPosterImgUrl(){
            return thumbnail.url;
        }
    }

    static public class ECGalleryItem extends PresentationDataItem{
        final public List<PictureItem> galleryImages = new ArrayList<PictureItem>();
        final public String subType;
        public ECGalleryItem(String parentExperienceId, InventoryMetadataType metaData, List<PictureItem> galleryImages, String subType){
            super(metaData, parentExperienceId);
            this.galleryImages.addAll(galleryImages);
            this.subType = subType;
            if (metaData != null) {
                BasicMetadataInfoType localizedInfo = metaData != null ? metaData.getBasicMetadata().getLocalizedInfo().get(0) : null;
                if (localizedInfo != null && localizedInfo.getArtReference() != null && localizedInfo.getArtReference().size() > 0) {
                    posterImgUrl = localizedInfo.getArtReference().get(0).getValue();
                }
            }
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

        public boolean isTurnTable(){
            return "Turntable".equalsIgnoreCase(subType);
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
        final public String audioUrl;
        final public String duration;
        final public List<ExternalApiData> externalApiDataList = new ArrayList<ExternalApiData>();
        final PresentationImageData[] images;
        final Duration durationObject;
        final String subtype;

        boolean isWatched = false;

        public AudioVisualItem(String parentExperienceId, InventoryMetadataType metaData, InventoryVideoType videoData, InventoryAudioType audioData, String subtype){
            super(metaData, parentExperienceId);
            this.subtype = subtype;
            BasicMetadataInfoType localizedInfo = metaData!= null ? metaData.getBasicMetadata().getLocalizedInfo().get(0): null;
            if (videoData != null) {
                videoUrl = videoData.getContainerReference().getContainerLocation();

                if (audioData != null)
                    audioUrl = audioData.getContainerReference().getContainerLocation();
                else
                    audioUrl = null;

                if (localizedInfo != null && localizedInfo.getArtReference() != null && localizedInfo.getArtReference().size() > 0) {
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
                if (metaData != null)
                    durationObject = metaData.getBasicMetadata().getRunLength();
                else
                    durationObject = null;
                try {
                    dValue = metaData.getBasicMetadata().getRunLength().toString();
                }catch ( Exception ex){
                    NextGenLogger.d(F.TAG, ex.getLocalizedMessage());
                }
                duration = dValue;
            }else{
                videoUrl = null;
                audioUrl = null;
                posterImgUrl = null;
                duration = null;
                images = null;
                durationObject = null;
            }
        }

        public AudioVisualItem(InventoryVideoType videoData, String subtype){
            super(null, null);
            this.subtype = subtype;
            if (videoData != null) {
                videoUrl = videoData.getContainerReference().getContainerLocation();

            }else{
                videoUrl = null;
            }
            audioUrl = null;
            duration = null;
            images = null;
            durationObject = null;
        }

        public void setLocationMetaData(String title, String url){
            this.title = title;
            posterImgUrl = url;
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

        public AudioVisualItem(String parentExperienceId, InventoryMetadataType metaData, InventoryVideoType videoData, InventoryAudioType audioData, List<ExternalApiData> apiDataList, String subtype){
            this(parentExperienceId, metaData, videoData, audioData, subtype);
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

        public boolean getIsWatched(){
            return isWatched;
        }

        public void setWatched(){
            isWatched = true;
        }

        public boolean isShareClip() {
			if (SHARE_CLIP_SUBTYPE.equalsIgnoreCase(subtype))
				return true;	// man of steel
			else {
				// travel up the parent to see if it's a sharable clip
				ExperienceData exp = NextGenExperience.getMovieMetaData().findExperienceDataById(parentExperienceId);
				do {
					if (exp.isShareClip())
						return true;
				} while ((exp = exp.parent) != null);
				return false;
			}
        }
    }

    static public class ExperienceData {
        final public String title;
        protected String posterImgUrl;
        //final public String ecVideoUrl;
        protected ECGroupType type = ECGroupType.UNKNOWN;
        final protected List<ExperienceData> childrenExperience = new ArrayList<ExperienceData>();
        final public List<ECGalleryItem> galleryItems = new ArrayList<ECGalleryItem>();
        final public List<AudioVisualItem> audioVisualItems = new ArrayList<AudioVisualItem>();
        final protected List<LocationItem> locationItems = new ArrayList<LocationItem>();
        final public List<InteractiveItem> interactiveItems = new ArrayList<InteractiveItem>();
        final public String experienceId;
        final protected HashMap<String, Integer> childIdToSequenceNumber = new HashMap<String, Integer>();
        protected ExternalApiData externalApp;
        final public String timeSequenceId;
        final public StyleData.ExperienceStyle style;
        private ExperienceData parent;

        public ExperienceData(String title, String experienceId, String timeSequenceId){
            this.title = title;
            this.experienceId = experienceId;
            this.timeSequenceId = timeSequenceId;
            this.style = null;
        }

        public ExperienceData(ExperienceType experience, InventoryMetadataType metaData, List<AudioVisualItem> avItems,
                              List<ECGalleryItem> galleryItems, List<LocationItem> locationItems, List<InteractiveItem> interactiveItems,
                              StyleData.ExperienceStyle style){
            experienceId = experience.getExperienceID();     // or experience Id
            this.style = style;
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

            if (interactiveItems != null && interactiveItems.size() > 0){
                this.interactiveItems.addAll(interactiveItems);
            }
        }

        public String getDuration(){
            if (audioVisualItems.size() > 0){
                return audioVisualItems.get(0).duration;
            }
            return null;
        }

        public boolean getIsWatched(){
            if (audioVisualItems.size() > 0){
                return audioVisualItems.get(0).isWatched;
            }
            return false;
        }

        public void setWatched(){
            if (audioVisualItems.size() > 0){
                audioVisualItems.get(0).setWatched();
            }
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
                else if (interactiveItems.size() > 0)
                    type = ECGroupType.INTERACTIVE;
                else if (externalApp != null)
                    type = ECGroupType.EXTERNAL_APP;
                else if (childrenExperience.size() > 0)
                    type = childrenExperience.get(0).getECGroupType();

            }
            return type;
        }

        private void addChild(ExperienceData ecContent){
			ecContent.parent = this;
			// skip if children has been added already
			if (childrenExperience.contains(ecContent))
				return;

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
            }else if (audioVisualItems.size() > 0) {
                posterImgUrl = audioVisualItems.get(0).getPosterImgUrl();
            }else if (locationItems.size() > 0){
                posterImgUrl = locationItems.get(0).getPosterImgUrl();
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

        private final List<LocationItem> sceneLocations = new ArrayList<LocationItem>();
        public void computeSceneLocationFeature(){
            HashMap<String, LocationItem> sceneLocationMap = new HashMap<String, LocationItem>();
            for (ExperienceData child : childrenExperience){
                LocationItem location = child.locationItems.get(0);
                if (!sceneLocationMap.containsKey(location.getId())){
                    sceneLocations.add(location);
                    sceneLocationMap.put(location.getId(), location);
                }
            }
        }

        public List<LocationItem> getSceneLocations(){
            return  sceneLocations;
        }

        public StyleData.ExperienceStyle getStyle(){
            if (style == null){
                ExperienceData nextCheck = parent;
                while (parent != null){
                    if (parent.style != null)
                        return style;
                    else
                        nextCheck = nextCheck.parent;
                }
            }
            return style;
        }

		public boolean isShareClip() {
			return experienceId != null && experienceId.contains(SHARE_CLIP_KEY);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ExperienceData that = (ExperienceData) o;

			return experienceId.equals(that.experienceId);

		}

		@Override
		public int hashCode() {
			return experienceId.hashCode();
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
        public final int itemIndex;
        public IMEElement(long startTimecode, long endTimeCode, T imeObject, int itemIndex){   // in millisecond
            this.startTimecode = startTimecode;
            this.endTimecode = endTimeCode;
            this.imeObject = imeObject;
            this.itemIndex = itemIndex;
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

    public String getInterstitialVideoURL(){
        if (rootExperience.audioVisualItems != null && rootExperience.audioVisualItems.size() > 1){
            return rootExperience.audioVisualItems.get(0).videoUrl;
        }else{
            return "http://wb-extras.warnerbros.com/extrasplus/prod/Manifest/BatmanvSuperman/streams/BVS_final_v2_H264_HDX_Stream_6750K_23976p.mp4";
        }
    }

    public StyleData.ExperienceStyle getRootExperienceStyle(){
        return rootExperience.style;
    }

    public String getTitletreatmentImageUrl(){
        return rootExperience.childrenExperience.get(1).posterImgUrl;
    }

    public StyleData.ExperienceStyle getIMEExperienceStyle(){
        if (rootExperience.childrenExperience.size() > 1)
            return rootExperience.childrenExperience.get(1).style;
        else
            return null;
    }

    public ExperienceData getExtraExperience(){

        if (rootExperience.childrenExperience.size() > 0)
            return rootExperience.childrenExperience.get(0);
        else return null;
    }

    public ExperienceData getInMovieExperience(){

        if (rootExperience.childrenExperience.size() > 1)
            return rootExperience.childrenExperience.get(1);
        else return null;
    }

    public StyleData.ExperienceStyle getStyleData(String experienceId){
        return experienceStyleMap.get(experienceId);
    }
}
