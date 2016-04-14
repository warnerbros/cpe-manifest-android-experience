package com.wb.nextgen.model;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/25/16.
 */
public abstract class NextGenIMEEngine <T>{


    //abstract void handleIMEUpdate(long timecode, T imeElement);

    protected List<T> imeElements = new ArrayList<T>();
    protected int currentIndex = -1;
    protected T currentIMEItem = null;


    public NextGenIMEEngine(List<T> elements){
        imeElements = elements;
    }

    public NextGenIMEEngine(){}

    public void setImeElements(List<T> elements){
        imeElements = elements;
    }

    public T getCurrentIMEElement(){
        if (currentIMEItem != null)
            return currentIMEItem;
        else
            return null;
    }

    // returns true if there is an update of the current item
    public boolean computeCurrentIMEElement(long timecode){
        T computedIMEItem = null;
        if (currentIMEItem != null && compareCurrentTimeWithItemAtIndex(timecode, currentIndex) == 0) {
           return false;
        }

        if (timecode < 0 || imeElements.size() == 0) {
            currentIMEItem = null;
            return false;
        }
        //Implementation #1: Binary Search o(log n)
        if (currentIndex == -1){
            currentIndex = imeElements.size()/2;
        }

        int searchIndex = currentIndex;
        int upperBoundIndex = imeElements.size()-1;
        int lowerBoundIndex = 0;

        while(searchIndex >= 0 && searchIndex < imeElements.size()) {
            T currentElement = imeElements.get(searchIndex);

            int compareTimeCodeValue = compareCurrentTimeWithItemAtIndex(timecode, searchIndex);
            if (compareTimeCodeValue == 0) {
                currentIndex = searchIndex;
                computedIMEItem = imeElements.get(currentIndex);
                break;
            } else if (compareTimeCodeValue > 0  && searchIndex < upperBoundIndex) { //after
                //check in next element is after this time code => gap in IME, no element for this time
                lowerBoundIndex = searchIndex;
                T nextElement = imeElements.get(searchIndex + 1);
                int nextCompare = compareCurrentTimeWithItemAtIndex(timecode, searchIndex+1);
                if (nextCompare < 0) {
                    computedIMEItem = null;
                    break;
                }else if (nextCompare == 0) {
                    currentIndex = nextCompare;
                    computedIMEItem =  nextElement;
                    break;
                } else {
                    int nextIndex = searchIndex + (upperBoundIndex - searchIndex) / 2;
                    if (nextIndex == searchIndex) {  //if there's only 1 item left to be search and it's not a match
                        computedIMEItem = null;
                        break;
                    } else
                        searchIndex = nextIndex;
                }
            } else if (compareTimeCodeValue < 0 && searchIndex > lowerBoundIndex) { // before
                upperBoundIndex = searchIndex;
                T nextElement = imeElements.get(searchIndex - 1);
                int nextCompare = compareCurrentTimeWithItemAtIndex(timecode, searchIndex - 1);
                if (nextCompare > 0) {
                    computedIMEItem = null;
                    break;
                } else if (nextCompare == 0) {
                    currentIndex = nextCompare;
                    computedIMEItem = nextElement;
                    break;
                }
                int nextIndex = searchIndex / 2;
                if (nextIndex == searchIndex) {      //if there's only 1 item left to be search and it's not a match
                    computedIMEItem = null;
                    break;
                } else
                    searchIndex = nextIndex;
            } else {
                computedIMEItem = null;
                break;
            }
        }
        if (computedIMEItem == null || !computedIMEItem.equals(currentIMEItem) ) {
            currentIMEItem = computedIMEItem;
            return true;
        }else
            return false;
    }



    public MovieMetaData.IMEElement<T> createNextGenIMEElement(long startTimeCode, long endTimeCode, T object){
        return new MovieMetaData.IMEElement<T>(startTimeCode, endTimeCode, object);
    }

    public abstract int compareCurrentTimeWithItemAtIndex(long timecode, int index);
    /*
    public void playbackStatusUpdate(NextGenPlaybackStatus playbackStatus, long timecode){
        handleIMEUpdate(timecode, getCurrentIMEElement(timecode));
    }*/
}
