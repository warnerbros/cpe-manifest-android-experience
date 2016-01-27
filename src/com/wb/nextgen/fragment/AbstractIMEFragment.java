package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/25/16.
 */
public abstract class AbstractIMEFragment<T> extends NextGenActorListFragment implements NextGenPlaybackStatusListener{

    public class NextGenIMEElement {
        public final long startTimecode;
        public final long endTimecode;
        public final T imeObject;
        public NextGenIMEElement(long startTimecode, long endTimeCode, T imeObject){
            this.startTimecode = startTimecode;
            this.endTimecode = endTimeCode;
            this.imeObject = imeObject;
        }

        /*
            returns: 0 = within
            returns: >0 = after
            returns: <0 = before
         */
        public int compareTimeCode(long timeCode){
            long beforeTime = timeCode - startTimecode;
            long afterTime = timeCode - endTimecode;
            if (beforeTime >=0 && afterTime <= 0)
                return 0;
            else if (afterTime > 0)
                return 1;
            else
                return -1;
        }
    }
    abstract void handleIMEUpdate(long timecode, T imeElement);

    protected List<NextGenIMEElement> imeElements = new ArrayList<NextGenIMEElement>();
    protected int currentIndex = -1;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected T getCurrentIMEElement(long timecode){
        if (timecode < 0 || imeElements.size() == 0)
            return null;
        //Implementation #1: Binary Search o(log n)
        if (currentIndex == -1){
            currentIndex = imeElements.size()/2;
        }


        int searchIndex = currentIndex;
        int upperBoundIndex = imeElements.size()-1;
        int lowerBoundIndex = 0;

        while(searchIndex >= 0 && searchIndex < imeElements.size()) {
            NextGenIMEElement currentElement = imeElements.get(searchIndex);

            int compareTimeCodeValue = currentElement.compareTimeCode(timecode);
            if (compareTimeCodeValue == 0) {
                currentIndex = searchIndex;
                return imeElements.get(currentIndex).imeObject;
            } else if (compareTimeCodeValue > 0  && searchIndex < upperBoundIndex) { //after
                //check in next element is after this time code => gap in IME, no element for this time
                lowerBoundIndex = searchIndex;
                NextGenIMEElement nextElement = imeElements.get(searchIndex + 1);
                int nextCompare = nextElement.compareTimeCode(timecode);
                if (nextCompare < 0)
                    return null;
                else if (nextCompare == 0)
                    return nextElement.imeObject;

                int nextIndex = searchIndex + (upperBoundIndex - searchIndex )/2;
                if (nextIndex == searchIndex)   //if there's only 1 item left to be search and it's not a match
                    return null;
                else
                    searchIndex = nextIndex;
            } else if (compareTimeCodeValue < 0 && searchIndex > lowerBoundIndex) { // before
                upperBoundIndex = searchIndex;
                NextGenIMEElement nextElement = imeElements.get(searchIndex - 1);
                int nextCompare = nextElement.compareTimeCode(timecode);
                if (nextCompare > 0)
                    return null;
                else if (nextCompare == 0)
                    return nextElement.imeObject;
                int nextIndex = searchIndex / 2;
                if (nextIndex == searchIndex)       //if there's only 1 item left to be search and it's not a match
                    return null;
                else
                    searchIndex = nextIndex;
            } else {
                return null;
            }
        }
        return null;
    }

    public void playbackStatusUpdate(NextGenPlaybackStatus playbackStatus, long timecode){
        handleIMEUpdate(timecode, getCurrentIMEElement(timecode));
    }
}
