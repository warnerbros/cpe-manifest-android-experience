package com.wb.nextgen.data;

import com.google.gson.annotations.SerializedName;
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
import com.wb.nextgen.parser.md.schema.v2_3.BasicMetadataPeopleType;
import com.wb.nextgen.parser.md.schema.v2_3.ContentIdentifierType;
import com.wb.nextgen.parser.md.schema.v2_3.PersonIdentifierType;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;
import com.wb.nextgen.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    final private List<CastData> castsList = new ArrayList<CastData>();
    final private List<CastData> actorsList = new ArrayList<CastData>();
    final private HashMap<String, ExperienceData> experienceIdToExperienceMap = new HashMap<String, ExperienceData>();
    private ExperienceData rootExperience;
    private boolean hasCalledBaselineAPI = false;

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

    public static MovieMetaData process(MediaManifestType mediaManifest){
        MovieMetaData result = new MovieMetaData();

        HashMap<String, InventoryMetadataType> metaDataAssetsMap = new HashMap<String, InventoryMetadataType>();
        HashMap<String, InventoryVideoType> videoAssetsMap = new HashMap<String, InventoryVideoType>();
        HashMap<String, PresentationType> presentationAssetMap = new HashMap<String, PresentationType>();
        HashMap<String, InventoryImageType> imageAssetsMap = new HashMap<String, InventoryImageType>();
        HashMap<String, PictureGroupType> pictureGroupAssetsMap = new HashMap<String, PictureGroupType>();
        HashMap<String, AudioVisualItem> presentationIdToAVItemMap = new HashMap<String, AudioVisualItem>();
        HashMap<String, ECGalleryItem> galleryIdToGalleryItemMap = new HashMap<String, ECGalleryItem>();

        HashMap<String, ExperienceData> timeSequenceIdToECGroup = new HashMap<String, ExperienceData>();


        HashMap<String, ExperienceData> experienceChildrenToParentMap = new HashMap<String, ExperienceData>();

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
                List<ECGalleryItem> galleryItems = new ArrayList<ECGalleryItem>();
                List<AudioVisualItem> avItems = new ArrayList<AudioVisualItem>();
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

                ExperienceData thisExperience = new ExperienceData(experience, experienceMetaData, avItems, galleryItems);
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
                result.imeECGroups.addAll(rootData.childrenExperience.get(1).childrenExperience);
                result.rootExperience = rootData;
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

                        PresentationDataItem presentationData = null;
                        if (!StringHelper.isEmpty(eventPID)) {
                            if (presentationIdToAVItemMap.containsKey(eventPID)) {
                                presentationData = presentationIdToAVItemMap.get(eventPID);

                            }
                        }else if (!StringHelper.isEmpty(galleryId)){
                            if (galleryIdToGalleryItemMap.containsKey(galleryId)){
                                presentationData = galleryIdToGalleryItemMap.get(galleryId);

                            }
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

    public List<CastData> getActorsList(){
        return  actorsList;
    }

    public ExperienceData findExperienceDataById(String id){
        if (!StringHelper.isEmpty(id)) {
            return experienceIdToExperienceMap.get(id);

        }
        return null;
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


        public CastHeadShot headShot;
        public String biography;
        public List<Filmography> filmogrphies;

        public String getThumbnailImageUrl(){
            if (headShot != null){
                return headShot.smallUrl;
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

    static public class CastData{

        final public String displayName;
        final public String charactorName;
        final public String firstGivenName;
        final public String secondGivenName;
        final public String lastName;
        final public String gender;
        private String baselineApiActorId;
        final public String job;
        public BaselineCastData baselineCastData;

        public CastData(BasicMetadataPeopleType castInfo){
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
                    if ("baseline".equals(personId.getNamespace()) ) {
                        baselineApiActorId = personId.getIdentifier();
                        break;
                    }
                }
            }

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
    }

    public static enum ECGroupType{
        FEATURETTES, VISUAL_EFFECT, GALLERY, MIX, EXTERNAL_APP, UNKNOWN
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

    static public abstract class PresentationDataItem{
        final public String id;
        final public String title;
        final public String parentExperienceId;
        protected String posterImgUrl;

        public PresentationDataItem(InventoryMetadataType metaData, String parentExperienceId){
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            this.id = metaData.getContentID();
            title = localizedInfo.getTitleDisplayUnlimited();
            this.parentExperienceId = parentExperienceId;
        }

        public abstract String getPosterImgUrl();
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
        public AudioVisualItem(String parentExperienceId, InventoryMetadataType metaData, InventoryVideoType videoData){
            super(metaData, parentExperienceId);
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            if (videoData != null) {
                videoUrl = videoData.getContainerReference().getContainerLocation();
                if (localizedInfo.getArtReference() != null && localizedInfo.getArtReference().size() > 0)
                    posterImgUrl = localizedInfo.getArtReference().get(0).getValue();
                else
                    posterImgUrl = null;


                String dValue = "";
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
            }
        }

        public AudioVisualItem(String parentExperienceId, InventoryMetadataType metaData, InventoryVideoType videoData, List<ExternalApiData> apiDataList){
            this(parentExperienceId, metaData, videoData);
            if (apiDataList != null)
                externalApiDataList.addAll(apiDataList);
        }

        @Override
        public String getPosterImgUrl(){

            return posterImgUrl;
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
        final public String experienceId;
        final private HashMap<String, Integer> childIdToSequenceNumber = new HashMap<String, Integer>();
        private ExternalApiData externalApp;

        public ExperienceData(ExperienceType experience, InventoryMetadataType metaData, List<AudioVisualItem> avItems, List<ECGalleryItem> galleryItems){
            BasicMetadataInfoType localizedInfo = metaData.getBasicMetadata().getLocalizedInfo().get(0);
            title = localizedInfo.getTitleDisplayUnlimited(); // should loop the list and look for the correct language code
            experienceId = experience.getExperienceID();     // or experience Id

            if (experience.getApp() != null && experience.getApp().size() > 0 && experience.getApp().get(0).getSubType() != null && experience.getApp().get(0).getSubType().size() > 0){
                externalApp = new ExternalApiData(experience.getApp().get(0).getType(), experience.getApp().get(0).getSubType().get(0));
            }

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
    }

    public static class IMEElementsGroup<T>{
        public final ExperienceData linkedExperience;
        private final List<IMEElement<T>> imeElementsList = new ArrayList<IMEElement<T>>();
        public void addElement(IMEElement element){
            imeElementsList.add(element);
        }
        public IMEElementsGroup(ExperienceData ecGroupData){
            this.linkedExperience = ecGroupData;
        }
        public List<IMEElement<T>> getIMEElementesList(){
            return  imeElementsList;
        }

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
}
