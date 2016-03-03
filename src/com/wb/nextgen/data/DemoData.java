package com.wb.nextgen.data;

import com.wb.nextgen.NextGenECView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 3/2/16.
 */
public class DemoData {
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

    final static public List<ECContentData> DEMODATA_MAN_OF_STEEL_EC_LIST = new ArrayList<ECContentData>();
    final static public String DEMODATA_MAN_OF_STEEL_TITLE_TEXT = "Man of Steel";


    final static public List<ECContentData> DEMODATA_LEGACY_EC_LIST = new ArrayList<ECContentData>();
    final static public String DEMODATA_LEGACY_TITLE_TEXT = "Legacy";

    static private DemoJSONData dataFromJSON;

    public static DemoJSONData getDemoJSONData(){
        return dataFromJSON;
    }

    public static void parseDemoJSONData(){
        dataFromJSON = DemoJSONData.createFromUri("abc");

    }

    static {
        DEMODATA_MAN_OF_STEEL_EC_LIST.add(new ECContentData("Batman v Superman",
                "http://resizing.flixster.com/Jj6wWGt6lyF3lvez2mPrgekAjpA=/320x474/dkpu1ddg7pbsk.cloudfront.net/movie/11/19/11/11191102_ori.jpg",
                "http://www.flixster.com/movie/batman-v-superman-dawn-of-justice/"));
        DEMODATA_MAN_OF_STEEL_EC_LIST.add(new ECContentData("The Man From U.N.C.L.E.",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/2083346825265035E05314345B0AD4F9",
                "https://video.flixster.com/movies/the-man-from-u-n-c-l-e/urn:dece:cid:eidr-s:AA29-CCC8-B64E-9647-6627-N"));
        DEMODATA_MAN_OF_STEEL_EC_LIST.add(new ECContentData("Immortals",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/1007F33E67B6A988E05315345B0A63A6",
                "https://video.flixster.com/movies/immortals/urn:dece:cid:org:relativitymedia:Immortals_2012"));
        DEMODATA_MAN_OF_STEEL_EC_LIST.add(new ECContentData( "Whatever Works",
                "https://d19p213wjrwt85.cloudfront.net/uvvu-images/02933A178B3F8076E0505A0A15346353",
                "https://video.flixster.com/movies/whatever-works/urn:dece:cid:eidr-s:2653-F7B7-1F65-BE16-306C-S"));
        DEMODATA_MAN_OF_STEEL_EC_LIST.add(new ECContentData("Stardust",
                "https://resizing.flixster.com/lhmSvFu-P5Gdkjeg15aAkHe2adE=/180x240/dkpu1ddg7pbsk.cloudfront.net/movie/11/37/75/11377594_ori.jpg",
                "https://video.flixster.com/movies/stardust/urn:dece:cid:org:ppc:000003475600100003753"));

        DEMODATA_LEGACY_EC_LIST.add(new ECContentData("Strong Characters, Legendary Roles",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_StrongCharacters_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_TheMilitaryMightOfManOfSteel_HD_2ch_en-US.m3u8"));
        DEMODATA_LEGACY_EC_LIST.add(new ECContentData("Superman 75th Anniversary Animated Short",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/ManOfSteel_EC_Superman75thAnniversary_HD_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/MOS_4548967020816_6Superman75thAnniversary.m3u8"));
        DEMODATA_LEGACY_EC_LIST.add(new ECContentData("The Iconic Characters of MAN OF STEEL",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_IconicCharacters_of_MOS_SML.jpg",
                "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/ManOfSteel_EC_TheIconicCharactersOfManOfSteel_HD_2ch_en-US.m3u8"));
    }

}
