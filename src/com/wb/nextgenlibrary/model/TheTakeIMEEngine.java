package com.wb.nextgenlibrary.model;

import com.wb.nextgenlibrary.data.TheTakeData.TheTakeProductFrame;
import com.wb.nextgenlibrary.network.TheTakeApiDAO;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;

import java.util.List;

/**
 * Created by gzcheng on 4/12/16.
 */
public class TheTakeIMEEngine extends IMEEngine<TheTakeProductFrame> {
    long earliestTimecode = 0L;
    long lastTimecode = 0L;
    int currentStart = -1;
    private boolean hasMore = true;

    //private List<TheTakeData.TheTakeProductFrame> productFrames = new ArrayList<TheTakeData.TheTakeProductFrame>();
    final static int FRAME_GROUP_ITEM_LIMIT = 4000;

    public TheTakeIMEEngine(){
        //requestNextGroupOfData();
    }

    public int compareCurrentTimeWithItemAtIndex(long timecode, int index){
        TheTakeProductFrame frameAtLocation = imeElements.get(index);

        if (timecode < frameAtLocation.frameTime)
            return -1;

        if (index >= imeElements.size()-1){
            //fetch next patch?
            //if (frameAtLocation.frameTime <= timecode)
                return 0;
        }else{
            TheTakeProductFrame next = imeElements.get(index + 1);
            if (frameAtLocation.frameTime <= timecode && next.frameTime >= timecode)
                return 0;
            else
                return 1;

        }
    }

    private void requestNextGroupOfData(final long timecode){
        if (currentStart == imeElements.size()){
            return;
        }else{
            currentStart = imeElements.size();
        }
        TheTakeApiDAO.fetchProductFrames(timecode + 3000, imeElements.size(), FRAME_GROUP_ITEM_LIMIT, new ResultListener<List<TheTakeProductFrame>>() {
            @Override
            public void onResult(List<TheTakeProductFrame> result) {
                synchronized (imeElements){
                    hasMore = result.size() == FRAME_GROUP_ITEM_LIMIT;

                    if (result != null && result.size() > 0) {
                        lastTimecode = result.get(result.size() - 1).frameTime;
                        imeElements.addAll(result);
                    }
                }/*
                if (timecode + 3000 > lastTimecode && hasMore){
                    requestNextGroupOfData(timecode);
                }*/
            }

            @Override
            public <E extends Exception> void onException(E e) {

            }
        });
    }

    //static int counter = 0;
    // returns true if there is an update of the current item
    long lastGetTimecode = 0L;
    @Override
    public boolean computeCurrentIMEElement(long timecode) {
        if (timecode + 3000 > lastTimecode && hasMore){
            requestNextGroupOfData(timecode);
        }
        if (lastGetTimecode + 15000 > timecode && lastGetTimecode < timecode ){

            return false;
        }else {

            lastGetTimecode = timecode;
            synchronized (imeElements) {

                boolean result = super.linearSearch(timecode);
                return result;
            }
        }
    }


}
