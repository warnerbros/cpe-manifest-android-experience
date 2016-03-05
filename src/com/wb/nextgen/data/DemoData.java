package com.wb.nextgen.data;

import com.wb.nextgen.NextGenECView;
import com.wb.nextgen.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 3/2/16.
 */
public class DemoData {

    static enum ECGroupType{
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

    static public class ECContentData{
        final public String title;
        final public String posterImgUrl;
        final public String ecVideoUrl;
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
        DEMO_MAN_OF_STEEL_EC_GROUPS.add(new ECGroupData("experience.bonus.20", "Galleries", ECGroupType.FEATURETTES));


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
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_CreationAndDestructionOfKrypton_HD_2ch_en-US.m3u8<"));
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
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(new ECContentData("Kryptonian Council Wardrobe Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_701_KryptonianCouncilWardrobe_SML.jpg",
                ""));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(new ECContentData("Kryptonian Weapons Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_702_KryptonianWeapons_SML.jpg",
                ""));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(new ECContentData("Kryptonian Noble Wardrobe Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_704_KryptonianNoble_SML.jpg",
                ""));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(new ECContentData("Superman Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_705_SupermanGallery_SML.jpg",
                ""));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(new ECContentData("Earth Armor Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_706_EarthArmorGallery_SML.jpg",
                ""));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(new ECContentData("Interior Black Zero Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_707_InteriorBlackZero_SML.jpg",
                ""));
        DEMO_MAN_OF_STEEL_EC_GROUPS.get(4).ecContents.add(new ECContentData("Black Zero Phantom Zone Gallery",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_708_BlackZeroPhantomZone_SML.jpg",
                ""));
    }

}
