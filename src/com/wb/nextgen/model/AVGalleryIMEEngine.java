package com.wb.nextgen.model;

import com.wb.nextgen.data.MovieMetaData;

import java.util.List;

/**
 * Created by gzcheng on 4/12/16.
 */
public class AVGalleryIMEEngine extends NextGenIMEEngine<MovieMetaData.IMEElement<MovieMetaData.PresentationDataItem>> {

    public AVGalleryIMEEngine(List<MovieMetaData.IMEElement<MovieMetaData.PresentationDataItem>> elements){
        imeElements = elements;
    }

    public int compareCurrentTimeWithItemAtIndex(long timecode, int index){
        return imeElements.get(index).compareTimeCode(timecode);
    }
}
