package com.wb.nextgen.model;

import com.wb.nextgen.data.TheTakeData.TheTakeProductFrame;
import com.wb.nextgen.network.TheTakeApiDAO;
import com.wb.nextgen.util.concurrent.ResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 4/12/16.
 */
public class TheTakeIMEEngine extends NextGenIMEEngine<TheTakeProductFrame>{
    long earliestTimecode = 0L;
    long lastTimecode = 0L;
    private boolean hasMore = true;

    //private List<TheTakeData.TheTakeProductFrame> productFrames = new ArrayList<TheTakeData.TheTakeProductFrame>();
    final static int FRAME_GROUP_ITEM_LIMIT = 400;

    public TheTakeIMEEngine(){
        requestNextGroupOfData();
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

    private void requestNextGroupOfData(){
        TheTakeApiDAO.fetchProductFrames(imeElements.size(), FRAME_GROUP_ITEM_LIMIT, new ResultListener<List<TheTakeProductFrame>>() {
            @Override
            public void onResult(List<TheTakeProductFrame> result) {
                synchronized (imeElements){
                    hasMore = result.size() == FRAME_GROUP_ITEM_LIMIT;

                    imeElements.addAll(result);
                }
            }

            @Override
            public <E extends Exception> void onException(E e) {

            }
        });
    }

    // returns true if there is an update of the current item
    @Override
    public boolean computeCurrentIMEElement(long timecode){


        boolean result = super.computeCurrentIMEElement(timecode);


        return result;
    }

}