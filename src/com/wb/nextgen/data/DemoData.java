package com.wb.nextgen.data;

import com.wb.nextgen.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 3/2/16.
 */
public class DemoData {

    public static enum ECGroupType{
        FEATURETTES, VISUAL_EFFECT, GALLERY
    }

    static public class ECGroupData{
        final public String title;
        final public String id;
        private String posterImgUrl;
        final public ECGroupType type;
        final public List<ECContentData>  ecContents = new ArrayList<ECContentData>();
        public ECGroupData(String id, String title, ECGroupType type){
            this.id = id;
            this.title = title;
            //this.posterImgUrl = posterImgUrl;
            this.type = type;
        }
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
        public ECGalleryImageItem(String name, ECGalleryImageData fullImage, ECGalleryImageData thumbnail){
            this.name = name;
            this.fullImage = fullImage;
            this.thumbnail = thumbnail;
        }
    }

    static public class ECContentData{
        final public String title;
        final public String posterImgUrl;
        final public String ecVideoUrl;
        final public List<ECGalleryImageItem>  galleryItems = new ArrayList<ECGalleryImageItem>();
        public ECContentData(String title, String posterImgUrl, String videoUrl){
            this.title = title;
            this.posterImgUrl = posterImgUrl;
            this.ecVideoUrl = videoUrl;
        }
    }

    final static public List<ECGroupData> DEMO_MAN_OF_STEEL_EC_GROUPS = new ArrayList<ECGroupData>();

    final static public List<ECContentData> DEMODATA_MAN_OF_STEEL_EC_LIST = new ArrayList<ECContentData>();
    final static public String DEMODATA_MAN_OF_STEEL_TITLE_TEXT = "Man of Steel";

    static private DemoJSONData dataFromJSON;

    public static DemoJSONData getDemoJSONData(){
        return dataFromJSON;
    }

    public static void parseDemoJSONData(){
        dataFromJSON = DemoJSONData.createFromUri("abc");

    }

    public static ECGroupData findECGroupDataById(String id){
        if (!StringHelper.isEmpty(id)) {
            for (ECGroupData group : DEMO_MAN_OF_STEEL_EC_GROUPS) {
                if (id.equals(group.id))
                    return group;
            }
        }
        return null;
    }

    static {
        DEMO_MAN_OF_STEEL_EC_GROUPS.add(new ECGroupData("experience.bonus.4", "Legacy", ECGroupType.FEATURETTES));
        DEMO_MAN_OF_STEEL_EC_GROUPS.add(new ECGroupData("experience.bonus.8", "Out of this World", ECGroupType.FEATURETTES));
        DEMO_MAN_OF_STEEL_EC_GROUPS.add(new ECGroupData("experience.bonus.11", "X-Ray Vision", ECGroupType.FEATURETTES));
        DEMO_MAN_OF_STEEL_EC_GROUPS.add(new ECGroupData("experience.bonus.17", "Additional Features", ECGroupType.VISUAL_EFFECT));
        DEMO_MAN_OF_STEEL_EC_GROUPS.add(new ECGroupData("experience.bonus.20", "Galleries", ECGroupType.GALLERY));


        // legacy
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(0).ecContents.add(new ECContentData("Strong Characters, Legendary Roles",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_StrongCharacters_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_TheMilitaryMightOfManOfSteel_HD_2ch_en-US.m3u8"));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(0).ecContents.add(new ECContentData("Superman 75th Anniversary Animated Short",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/ManOfSteel_EC_Superman75thAnniversary_HD_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/MOS_4548967020816_6Superman75thAnniversary.m3u8"));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(0).ecContents.add(new ECContentData("The Iconic Characters of MAN OF STEEL",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_IconicCharacters_of_MOS_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_TheIconicCharactersOfManOfSteel_HD_2ch_en-US.m3u8"));


        //Out of this world
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(1).ecContents.add(new ECContentData("Planet Krypton",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_PlanetKrypton_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/MOS_883316669075_2PlanetKrypton.m3u8"));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(1).ecContents.add(new ECContentData("Krypton Decoded",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_KryptonDecoded_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/MOS_883316669075_5KryptonDecoded.m3u8"));

        //X-Ray Vision
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(2).ecContents.add(new ECContentData("The Creation and Destruction of Krypton",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_CreationDestruction_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_CreationAndDestructionOfKrypton_HD_2ch_en-US.m3u8"));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(2).ecContents.add(new ECContentData("Clark Discovers the Scout Ship",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_ScoutShip_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_ClarkDiscoversTheScoutShip_HD_2ch_en-US.m3u8"));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(2).ecContents.add(new ECContentData("Battle on the Streets of Smallville",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_BattleSmallville_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_BattleOnTheStreetsOfSmallville_HD_2ch_en-US.m3u8"));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(2).ecContents.add(new ECContentData("The Military Might of MAN OF STEEL",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_MilitaryMight_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_TheMilitaryMightOfManOfSteel_HD_2ch_en-US.m3u8"));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(2).ecContents.add(new ECContentData("Attack on Metropolis",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_AttackMetropolis_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/MOSNG_111_AttackonMetropolis.m3u8"));

        //Additional Features
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(3).ecContents.add(new ECContentData("All-Out Action",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_215_ExposingZodsArmor_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/MOSNG_215_ExposingZodsArmor.m3u8"));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(3).ecContents.add(new ECContentData("The Sonic Landscape of MAN OF STEEL",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_311_LiquidGeoDefense_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/MOSNG_311_LiquidGeoDefense.m3u8"));

        //Galleries
        ECContentData gallery1 = new ECContentData("Kryptonian Council Wardrobe Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_701_KryptonianCouncilWardrobe_SML.jpg",
                "");
        gallery1.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_701_KryptonianCouncilWardrobe_010.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_701_KryptonianCouncilWardrobe_010.jpg", 352, 198)) );

        gallery1.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_701_KryptonianCouncilWardrobe_020.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_701_KryptonianCouncilWardrobe_020.jpg", 352, 198)) );

        gallery1.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_701_KryptonianCouncilWardrobe_030.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_701_KryptonianCouncilWardrobe_030.jpg", 352, 198)) );

        gallery1.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_701_KryptonianCouncilWardrobe_040.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_701_KryptonianCouncilWardrobe_040.jpg", 352, 198)) );

        gallery1.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_701_KryptonianCouncilWardrobe_050.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_701_KryptonianCouncilWardrobe_050.jpg", 352, 198)) );

        ECContentData gallery2 = new ECContentData("Kryptonian Weapons Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_702_KryptonianWeapons_SML.jpg",
                "");

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_010.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_010.jpg<", 352, 198)) );

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_020.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_020.jpg", 352, 198)) );

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_030.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_030.jpg", 352, 198)) );

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_040.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_040.jpg", 352, 198)) );

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_050.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_050.jpg", 352, 198)) );

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_060.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_060.jpg", 352, 198)) );

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_070.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_070.jpg", 352, 198)) );

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_080.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_080.jpg", 352, 198)) );

        gallery2.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_702_KryptonianWeapons_090.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_702_KryptonianWeapons_090.jpg", 352, 198)) );


        ECContentData gallery3 = new ECContentData("Kryptonian Noble Wardrobe Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_704_KryptonianNoble_SML.jpg",
                "");

        gallery3.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_703_KryptonianArmor_010.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_703_KryptonianArmor_010.jpg", 352, 198)) );

        gallery3.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_703_KryptonianArmor_020.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_703_KryptonianArmor_020.jpg", 352, 198)) );

        gallery3.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_703_KryptonianArmor_030.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_703_KryptonianArmor_030.jpg", 352, 198)) );

        gallery3.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_703_KryptonianArmor_040.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_703_KryptonianArmor_040.jpg", 352, 198)) );

        gallery3.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_703_KryptonianArmor_050.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_703_KryptonianArmor_050.jpg", 352, 198)) );

        gallery3.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_703_KryptonianArmor_060.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_703_KryptonianArmor_060.jpg", 352, 198)) );

        gallery3.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_703_KryptonianArmor_070.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_703_KryptonianArmor_070.jpg", 352, 198)) );



        ECContentData gallery4 =new ECContentData("Superman Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_705_SupermanGallery_SML.jpg",
                "");

        gallery4.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_704_KryptonianNoble_010.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_704_KryptonianNoble_010.jpg", 352, 198)) );

        gallery4.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_704_KryptonianNoble_020.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_704_KryptonianNoble_020.jpg", 352, 198)) );

        gallery4.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_704_KryptonianNoble_030.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_704_KryptonianNoble_030.jpg", 352, 198)) );

        gallery4.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_704_KryptonianNoble_040.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_704_KryptonianNoble_040.jpg", 352, 198)) );

        gallery4.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_704_KryptonianNoble_050.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_704_KryptonianNoble_050.jpg", 352, 198)) );

        gallery4.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_704_KryptonianNoble_060.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_704_KryptonianNoble_060.jpg", 352, 198)) );

        gallery4.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_704_KryptonianNoble_070.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_704_KryptonianNoble_070.jpg", 352, 198)) );

        ECContentData gallery5 = new ECContentData("Earth Armor Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_706_EarthArmorGallery_SML.jpg",
                "");

        gallery5.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_705_SupermanGallery_010.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_705_SupermanGallery_010.jpg", 352, 198)) );

        gallery5.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_705_SupermanGallery_020.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_705_SupermanGallery_020.jpg", 352, 198)) );

        gallery5.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_705_SupermanGallery_030.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_705_SupermanGallery_030.jpg", 352, 198)) );

        gallery5.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_705_SupermanGallery_040.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_705_SupermanGallery_040.jpg", 352, 198)) );

        gallery5.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_705_SupermanGallery_050.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_705_SupermanGallery_050.jpg", 352, 198)) );

        gallery5.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_705_SupermanGallery_060.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_705_SupermanGallery_060.jpg", 352, 198)) );

        gallery5.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_705_SupermanGallery_070.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_705_SupermanGallery_070.jpg", 352, 198)) );


        ECContentData gallery6 = new ECContentData("Interior Black Zero Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_707_InteriorBlackZero_SML.jpg",
                "");

        gallery6.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_706_EarthArmorGallery_010.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_706_EarthArmorGallery_010.jpg", 352, 198)) );

        gallery6.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_706_EarthArmorGallery_020.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_706_EarthArmorGallery_020.jpg", 352, 198)) );

        gallery6.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_706_EarthArmorGallery_030.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_706_EarthArmorGallery_030.jpg", 352, 198)) );

        gallery6.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_706_EarthArmorGallery_040.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_706_EarthArmorGallery_040.jpg", 352, 198)) );

        gallery6.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_706_EarthArmorGallery_050.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_706_EarthArmorGallery_050.jpg", 352, 198)) );


        ECContentData gallery7 = new ECContentData("Black Zero Phantom Zone Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_708_BlackZeroPhantomZone_SML.jpg",
                "");

        gallery7.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_707_InteriorBlackZero_010.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_707_InteriorBlackZero_010.jpg", 352, 198)) );

        gallery7.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_707_InteriorBlackZero_020.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_707_InteriorBlackZero_020.jpg", 352, 198)) );

        gallery7.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_707_InteriorBlackZero_030.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_707_InteriorBlackZero_030.jpg", 352, 198)) );

        gallery7.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_707_InteriorBlackZero_040.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_707_InteriorBlackZero_040.jpg", 352, 198)) );

        gallery7.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_707_InteriorBlackZero_050.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_707_InteriorBlackZero_050.jpg", 352, 198)) );

        gallery7.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_707_InteriorBlackZero_060.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_707_InteriorBlackZero_060.jpg", 352, 198)) );

        ECContentData gallery8 = new ECContentData("Black Zero Phantom Zone Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_708_BlackZeroPhantomZone_SML.jpg",
                "");

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_010.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_010.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_020.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_020.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_030.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_030.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_040.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_040.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_050.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_050.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_060.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_060.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_070.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_070.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_080.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_080.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_090.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_090.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_100.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_100.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_110.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_110.jpg", 352, 198)) );

        gallery8.galleryItems.add(new ECGalleryImageItem("",
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/MOSNG_708_BlackZeroPhantomZone_120.jpg", 1920, 1080),
                new ECGalleryImageData("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/Galleries/thumbnails/MOSNG_708_BlackZeroPhantomZone_0120.jpg", 352, 198)) );


        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(gallery1);
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(gallery2);
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(gallery3);
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(gallery4);
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(gallery5);
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(gallery6);
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(gallery7);
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(gallery8);
    }

}
