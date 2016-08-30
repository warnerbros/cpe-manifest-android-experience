package com.wb.nextgenlibrary.model;

/**
 * Created by gzcheng on 1/27/16.
 */
public class Presentation {
    public final String imageURL;
    public final String title;

    public final static Presentation PRESENTATION_A = new Presentation("A", "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_superman101_MED.jpg");
    public final static Presentation PRESENTATION_B = new Presentation("B", "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOS_NG_StrongCharacters_MED.jpg");
    public final static Presentation PRESENTATION_C = new Presentation("A", "http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/thumbnails/MOSNG_701_KryptonianCouncilWardrobe_MED.jpg");


    public Presentation(String title, String imageURL){
        this.imageURL = imageURL;
        this.title = title;
    }
}
