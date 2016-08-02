package com.wb.nextgen.data;

/**
 * Created by gzcheng on 7/14/16.
 */
public class NextGenStyle {

    public enum NextGenAppearanceType {
        Main, InMovie, OutOfMovie
    }

    private static class NGDMAppearanceTitle {
        static final String ManOfSteel = "md:experiencedid:eidr-x:C80F-C561-EE65-C5E4-C039-U:feature";
        static final String BvS = "md:experiencedid:eidr-x:B257-8696-871C-A12B-B8C1-S:feature";
        static final String Minions = "md:experiencedid:eidr-x:F1F8-3CDA-0844-0D78-E520-Q:main.1";
        static final String Sisters = "md:experiencedid:eidr-x:3026-0E8C-3824-6701-A2FC-8:main.1";
        static final String SistersUnrated = "md:experiencedid:eidr-x:D2E8-4520-9446-BFAD-B106-4:main.1";
    }

    private String movieId;
    public NextGenStyle(String movieId){
        this.movieId = movieId;
    }

    //private NextGenAppearanceType type = NextGenAppearanceType.Main;

    public String getBackgroundImageURL(NextGenAppearanceType type) {

        if (movieId.equals(NGDMAppearanceTitle.ManOfSteel)) {
            switch (type) {
                case InMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/backgrounds/MOS_IME_bg.jpg";
                case OutOfMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/backgrounds/MOS_Extras_bg_notitle.jpg";
            }

        }else if (movieId.equals(NGDMAppearanceTitle.BvS)){
            switch (type) {
                case InMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/backgrounds/MOS_IME_bg.jpg";
                case OutOfMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/prod/Manifest/BatmanvSuperman/artwork/backgrounds/BVS_EXTRAS_BG.jpg";
            }

        }

        // FIXME: This appears to be the way Comcast defines background images
        if (type == NextGenAppearanceType.Main) {
            return "";//NGDMManifest.sharedInstance.outOfMovieExperience?.metadata?.imageURL
        }

        return null;

    }

    public String getBackgroundVideoURL() {
        if (movieId.equals(NGDMAppearanceTitle.ManOfSteel)) {
            return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/backgrounds/MOS_INTRO_BG.mp4";
        }else if (movieId.equals(NGDMAppearanceTitle.BvS)){
            return "http://wb-extras.warnerbros.com/extrasplus/prod/Manifest/BatmanvSuperman/artwork/backgrounds/BVS_INTRO_BG.mp4";
        }else
            return null;

    }

    public double getBackgroundVideoFadeTime() {
        if (movieId.equals(NGDMAppearanceTitle.ManOfSteel)) {
            return 8.5;
        }else if (movieId.equals(NGDMAppearanceTitle.BvS)){
            return 2;
        }else
            return 0;
    }

    public double getBackgroundVideoLoopTime() {

        return 14;

    }

    public String getInterstitialVideoURL(){
        return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/backgrounds/MOS_INTERSTITIAL_v2.mp4";
    }

    public String getTitleImageURL(NextGenAppearanceType type) {
        if (movieId.equals(NGDMAppearanceTitle.ManOfSteel)) {
            switch (type) {
                case InMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/backgrounds/MOS_title_treatment.png";
                case OutOfMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/backgrounds/MOS_Extras_title.png";
                default:
                    return null;
            }
        } else if (movieId.equals(NGDMAppearanceTitle.BvS)) {
            return null;

        } else
            return null;


            // FIXME: This appears to be the way Comcast defines title treatment
            /*if type == .Main {
                return NGDMManifest.sharedInstance.inMovieExperience?.metadata?.imageURL
            }

            return nil*/

    }

    public static class NGScreenOffSetRatio{
        public final double horizontalRatio;
        public final double verticalRatio;
        public NGScreenOffSetRatio(double xRatio, double yRatio){
            horizontalRatio = xRatio;
            verticalRatio = yRatio;
        }
    }

    public NGScreenOffSetRatio getTitleImageCenterOffset() {
        if (movieId.equals(NGDMAppearanceTitle.ManOfSteel)) {
            return new NGScreenOffSetRatio(0.5, 0.08);
        }else
            return null;
    }

    public NGScreenOffSetRatio getTitleImageSizeOffset(NextGenAppearanceType type){
        if (type == NextGenAppearanceType.Main){
            if (movieId.equals(NGDMAppearanceTitle.ManOfSteel))
                return new NGScreenOffSetRatio(0.36, 0.16);

        }
        return null;
    }

    public String getButtonImageURL(NextGenAppearanceType type){
        if (movieId.equals(NGDMAppearanceTitle.ManOfSteel)) {
            switch (type) {
                case InMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/buttons/MOS_Play_button.png";
                case OutOfMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/staging/Manifest/MOS/artwork/buttons/MOS_Extras_button.png";
            }

        }else if (movieId.equals(NGDMAppearanceTitle.BvS)){
            switch (type) {
                case InMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/prod/Manifest/BatmanvSuperman/artwork/buttons/BVS_BTN_PLAYMOVIE.png";
                case OutOfMovie:
                    return "http://wb-extras.warnerbros.com/extrasplus/prod/Manifest/BatmanvSuperman/artwork/buttons/BVS_BTN_EXTRAS.png";
            }

        }

        // FIXME: This appears to be the way Comcast defines background images
        if (type == NextGenAppearanceType.Main) {
            return "";//NGDMManifest.sharedInstance.outOfMovieExperience?.metadata?.imageURL
        }

        return null;
    }

    public NGScreenOffSetRatio getButtonCenterOffset(NextGenAppearanceType type) {

        if (movieId.equals(NGDMAppearanceTitle.ManOfSteel)) {
            switch (type) {
                case InMovie:
                    return new NGScreenOffSetRatio(0.29, 0.46);
                case OutOfMovie:
                    return new NGScreenOffSetRatio(0.29, 0.56);
            }

        }else if (movieId.equals(NGDMAppearanceTitle.BvS)){
            switch (type) {
                case InMovie:
                    return new NGScreenOffSetRatio(260.0 / 1024.0, 515.0 / 768.0);
                case OutOfMovie:
                    return new NGScreenOffSetRatio(260.0 / 1024.0, 570.0 / 768.0);
            }

        } else if (type == NextGenAppearanceType.InMovie) {
            return new NGScreenOffSetRatio(0.5, 0.2);
        }else if (type == NextGenAppearanceType.OutOfMovie) {
            return new NGScreenOffSetRatio(0.5, 0.28);
        }

        return null;

    }

    public NGScreenOffSetRatio getButtonSizeOffset(NextGenAppearanceType type) {

        if (movieId.equals(NGDMAppearanceTitle.ManOfSteel)) {
            switch (type) {
                case InMovie:
                    return new NGScreenOffSetRatio(0.36, 0.13);
                case OutOfMovie:
                    return new NGScreenOffSetRatio(0.2, 0.1);
            }

        }else if (movieId.equals(NGDMAppearanceTitle.BvS)){
            switch (type) {
                case InMovie:
                    return new NGScreenOffSetRatio(274.0 / 1024.0, 58 / 768.0);
                case OutOfMovie:
                    return new NGScreenOffSetRatio(174.0 / 1024.0, 48 / 768.0);
            }

        } else if (type == NextGenAppearanceType.InMovie) {
            return new NGScreenOffSetRatio(0.36, 0.08);
        }else if (type == NextGenAppearanceType.OutOfMovie) {
            return new NGScreenOffSetRatio(0.2, 0.06);
        }

        return null;

    }

}
