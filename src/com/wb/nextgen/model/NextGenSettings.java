package com.wb.nextgen.model;

/**
 * Created by gzcheng on 1/15/16.
 */
public class NextGenSettings {
    boolean mSubtitleOn = false;
    String mAudioLang = "en_US";
    String mSubtitleLang = "en_US";

    public void setSubtitleOn(boolean bOn){
        mSubtitleOn = true;
    }

    public boolean getSubtitleOn(){
        return mSubtitleOn;
    }

}
