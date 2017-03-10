package com.wb.nextgenlibrary.data;

import android.content.pm.ActivityInfo;

import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.parser.cpestyle.BackgroundOverlayAreaType;
import com.wb.nextgenlibrary.parser.cpestyle.BackgroundType;
import com.wb.nextgenlibrary.parser.cpestyle.ButtonType;
import com.wb.nextgenlibrary.parser.cpestyle.CompatibilityDeviceType;
import com.wb.nextgenlibrary.parser.cpestyle.ExperienceMenuMapType;
import com.wb.nextgenlibrary.parser.cpestyle.NodeStyleType;
import com.wb.nextgenlibrary.parser.cpestyle.ThemeType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryAudioType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.InventoryVideoType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.PictureGroupType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.PictureType;
import com.wb.nextgenlibrary.parser.manifest.schema.v1_4.PresentationType;
import com.wb.nextgenlibrary.util.Size;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.data.MovieMetaData.PictureImageData;
import com.wb.nextgenlibrary.data.MovieMetaData.PictureItem;

import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gzcheng on 9/19/16.
 */
public class StyleData {


    public final static int PHONE_PORTRAIT = 0;
    public final static int PHONE_LANDSCAPE = 1;
    public final static int TABLET_PORTRAIT = 2;
    public final static int TABLE_LANDSCAPE = 3;

    private static final String Tablet = "Tablet";
    private static final String Phone = "Phone";
    private static final String Landscape = "Landscape";
    private static final String Portrait = "Portrait";

    public static class ExperienceStyle{

        private String experienceId;
        final NodeStyleData[] nodeStyles = new NodeStyleData[4];
        public ExperienceStyle(ExperienceMenuMapType experienceMenuMapType, HashMap<String, StyleData.NodeStyleData> nodeStyleTypeHashMap){
            experienceId = experienceMenuMapType.getExperienceID().get(0).toString();
            for (ExperienceMenuMapType.NodeStyleRef nodeStyleRef : experienceMenuMapType.getNodeStyleRef()){

                String nodeStyleId = nodeStyleRef.getNodeStyleID();
                NodeStyleData nodeStyleData = nodeStyleTypeHashMap.get(nodeStyleId);

                String orientation = nodeStyleRef.getOrientation();
                if (orientation == null) {
                    nodeStyles[0] = nodeStyleData;
                }else if (nodeStyleRef.getDeviceTarget() != null && nodeStyleRef.getDeviceTarget().size() > 0){
                    for (CompatibilityDeviceType deviceType : nodeStyleRef.getDeviceTarget()){
                        int nodeStyleIndex = 0;
                        if (Phone.equals(deviceType.getSubClass().get(0))){
                            nodeStyleIndex = 0;
                        } else if (Tablet.equals(deviceType.getSubClass().get(0)))
                            nodeStyleIndex = 2;

                        nodeStyleIndex += orientation.equals(Portrait) ? 0 : 1;

                        nodeStyles[nodeStyleIndex] = nodeStyleData;
                    }
                }

            }
        }

        public String getExperienceId(){
            return experienceId;
        }

        public NodeStyleData getNodeStyleData(int orientation){
            int nodeStyleIndex = TabletUtils.isTablet() ? 2 :0;
            if (orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || orientation == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
                nodeStyleIndex = nodeStyleIndex + 1;
            NodeStyleData styleData = nodeStyles[nodeStyleIndex];
            if (styleData != null)
                return styleData;
            else if (nodeStyleIndex == 1 || nodeStyleIndex == 3){
                if (nodeStyles[nodeStyleIndex - 1] != null)
                    return nodeStyles[nodeStyleIndex - 1];
                else if (nodeStyles[0] != null)
                    return nodeStyles[0];
            }
            return null;
        }

        public NodeBackground getBackground(){
            if (nodeStyles[0] != null && nodeStyles[0].background != null){
                return nodeStyles[0].background;
            } else
                return null;
        }

        public String getBackgroundVideoUrl(){
            if (nodeStyles[0] != null && nodeStyles[0].background != null){
                return nodeStyles[0].background.getVideoUrl();
            } else
                return null;
        }

        public String getBackgroundAudioUrl(){
            if (nodeStyles[0] != null && nodeStyles[0].background != null){
                return nodeStyles[0].background.getAudioUrl();
            } else
                return null;
        }

        public Size getBackgroundVideoSize(){
            if (nodeStyles[0] != null && nodeStyles[0].background != null){
                return nodeStyles[0].background.getVideoPresetSize();
            } else
                return null;
        }

        public PictureImageData getBackgroundImage(){
            if (nodeStyles[0] != null && nodeStyles[0].background != null){
                return nodeStyles[0].background.getImage();
            } else
                return null;
        }

    }

    public static class ButtonData{
        public final static int BASE = 0;
        public final static int HIGHLIGHT = 1;
        public final static int DEFOCUS = 2;
        public final String label;
        private final MovieMetaData.PictureImageData[] buttomImageList = new MovieMetaData.PictureImageData[3];

        public ButtonData(ButtonType buttonType, HashMap<String, MovieMetaData.PictureImageData> pictureImageMap){

            label = buttonType.getLabel();
            boolean bLocalizedFound = false;
            if (buttonType.getLocalized() != null && buttonType.getLocalized().size() > 0){
                for(ButtonType.Localized localized : buttonType.getLocalized()){
                    if (NextGenExperience.matchesClientLocale(localized.getLanguage())){

                        buttomImageList[BASE] = pictureImageMap.get(localized.getBaseImage());
                        buttomImageList[HIGHLIGHT] = pictureImageMap.get(localized.getHighlightImage());
                        buttomImageList[DEFOCUS] = pictureImageMap.get(localized.getDefocusImage());
                        bLocalizedFound = true;
                    }
                }
            }

            if (!bLocalizedFound && buttonType.getDefault() != null){
                buttomImageList[BASE] = pictureImageMap.get(buttonType.getDefault().getBaseImage());
                buttomImageList[HIGHLIGHT] = pictureImageMap.get(buttonType.getDefault().getHighlightImage());
                buttomImageList[DEFOCUS] = pictureImageMap.get(buttonType.getDefault().getDefocusImage());
            }
        }
    }

    public static class ThemeData{

        public final static String EXTRA_BUTTON = "Extras";
        public final static String PURCHASE_BUTTON = "Buy";
        public final static String PLAY_BUTTON = "Play";
        public final String themeId;
        public HashMap<String, ButtonData> buttonDatas = new HashMap<>();

        public ThemeData(ThemeType themeType, HashMap<String, MovieMetaData.PictureImageData> pictureImageMap){
            themeId = themeType.getThemeID();
            if (themeType.getButtonImageSet() != null && themeType.getButtonImageSet().getButton() != null && themeType.getButtonImageSet().getButton().size() > 0){
                for (ButtonType buttonType : themeType.getButtonImageSet().getButton()){
                    ButtonData buttonData = new ButtonData(buttonType, pictureImageMap);
                    buttonDatas.put(buttonData.label, buttonData);
                }
            }
        }

        public PictureImageData getImageData(String label){
            ButtonData buttonData = buttonDatas.get(label);
            if (buttonData != null)
                return buttonData.buttomImageList[0];
            else
                return null;
        }
    }

    public static class NodeStyleData{
        //public static
        public final NodeBackground background;
        public final ThemeData theme;
        String themeId;
        public final String nodeStyleId;
        public NodeStyleData(NodeStyleType nodeStyleType, HashMap<String, ThemeData> themeTypeHashMap,
                             HashMap<String, PictureGroupType> pictureGroupAssetsMap,
                             HashMap<String, MovieMetaData.PictureImageData> pictureImageMap,
                             HashMap<String, PresentationType> presentationAssetMap,
                             HashMap<String, InventoryVideoType> videoAssetsMap,
                             HashMap<String, InventoryAudioType> audioAssetsMap){
            nodeStyleId = nodeStyleType.getNodeStyleID();
            themeId = nodeStyleType.getThemeID();
            if (!StringHelper.isEmpty(themeId)){
                theme = themeTypeHashMap.get(themeId);
            }else
                theme = null;

            if (nodeStyleType.getBackground() != null) {
                background = new NodeBackground(nodeStyleType.getBackground(), pictureGroupAssetsMap, pictureImageMap, presentationAssetMap, videoAssetsMap, audioAssetsMap);
            }else
                background = null;
        }

        public BackgroundOverlayAreaType getButtonOverlayArea(){
            if (background != null)
                return background.buttonOverlayArea;
            else
                return null;
        }

        public BackgroundOverlayAreaType getTitleOverlayArea(){
            if (background != null)
                return background.titleOverlayArea;
            else
                return null;
        }
    }

    public static enum ScaleMethod{
        BestFit, Full;
    }

    public static enum PositionMethod{
        upperleft, upperright, bottomleft, bottomright, centered;
    }

    public static class NodeBackground{

        private static final String TITLE_TAG = "title";
        private static final String BUTTON_TAG = "button";

        String videoPresentationID;
        String imagePictureGroupID;
        String colorHex;
        ScaleMethod scaleMethod;
        PositionMethod positionMethod;
        double videoLoopingPoint;
        String videoUrl;
        String audioUrl;
        Size videoSize = null;
        List<PictureImageData> bgImages = new ArrayList<>();

        BackgroundOverlayAreaType buttonOverlayArea, titleOverlayArea;

        public NodeBackground(BackgroundType backgroundType,
                              HashMap<String, PictureGroupType> pictureGroupAssetsMap,
                              HashMap<String, MovieMetaData.PictureImageData> pictureImageMap,
                              HashMap<String, PresentationType> presentationAssetMap,
                              HashMap<String, InventoryVideoType> videoAssetsMap ,
                              HashMap<String, InventoryAudioType> audioAssetsMap ){
            if (backgroundType != null){
                colorHex = backgroundType.getColor();
				if (backgroundType.getAudioLoop() != null && !StringHelper.isEmpty(backgroundType.getAudioLoop().getAudioTrackID())){
					String audioAssetId = backgroundType.getAudioLoop().getAudioTrackID();

						InventoryAudioType audio = audioAssetsMap.get(backgroundType.getAudioLoop().getAudioTrackID());
						if (audio != null)
							audioUrl = audio.getContainerReference().getContainerLocation();

				}
                if (backgroundType.getVideo() != null) {
                    videoPresentationID = backgroundType.getVideo().getPresentationID();
                    PresentationType presentation = presentationAssetMap.get(videoPresentationID);
                    InventoryVideoType video = videoAssetsMap.get(presentation.getTrackMetadata().get(0).getVideoTrackReference().get(0).getVideoTrackID().get(0));

                    if (video != null) {
                        videoUrl = video.getContainerReference().getContainerLocation();
                        if (video.getPicture() != null){
                            videoSize = new Size(video.getPicture().getWidthPixels(), video.getPicture().getHeightPixels());
                        }
                    }


                    if (video != null) {
                        videoUrl = video.getContainerReference().getContainerLocation();
                        if (video.getPicture() != null){
                            videoSize = new Size(video.getPicture().getWidthPixels(), video.getPicture().getHeightPixels());
                        }
                    }


                    if (backgroundType.getVideo().getLoopTimecode() != null)
                        videoLoopingPoint = Double.parseDouble(backgroundType.getVideo().getLoopTimecode().getValue());
                    else
                        videoLoopingPoint = 0;
                }
                if (backgroundType.getImage() != null) {
                    imagePictureGroupID = backgroundType.getImage().getPictureGroupID();

                    PictureGroupType pictureGroup = pictureGroupAssetsMap.get(imagePictureGroupID);
                    if (pictureGroup != null) {
                        for (PictureType picture : pictureGroup.getPicture()) {
                            PictureImageData imageData = pictureImageMap.get(picture.getImageID());
                            bgImages.add(imageData);
                        }
                    }

                }
                if (backgroundType.getAdaptation() != null){
                    scaleMethod = ScaleMethod.valueOf(backgroundType.getAdaptation().getScaleMethod());
                    positionMethod = PositionMethod.valueOf(backgroundType.getAdaptation().getPositioningMethod());
                }
                if (backgroundType.getOverlayArea() != null && backgroundType.getOverlayArea().size() > 0) {
                    for (BackgroundOverlayAreaType overlayArea : backgroundType.getOverlayArea()){
                        if (TITLE_TAG.equals(overlayArea.getTag()))
                            titleOverlayArea = overlayArea;
                        else
                            buttonOverlayArea = overlayArea;
                    }
                }
                //backgroundType.getAdaptation().
            }
        }

        public int getVideoLoopingPoint(){
            return (int)(videoLoopingPoint * 1000.0);
        }

        public String getVideoUrl(){
            return videoUrl;
        }

        public String getAudioUrl(){
            return audioUrl;
        }

        public Size getVideoPresetSize(){
            return videoSize;
        }

        public PictureImageData getImage(){
            if (bgImages.size() > 0)
                return bgImages.get(0);
            else
                return null;
        }

        public boolean hasBGImage(){
            return !StringHelper.isEmpty(imagePictureGroupID);
        }
    }


}
