package com.wb.nextgenlibrary.model;

import com.wb.nextgenlibrary.data.MovieMetaData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/25/16.
 */
public abstract class IMEEngine<T>{


    //abstract void handleIMEUpdate(long timecode, T imeElement);

    protected List<T> imeElements = new ArrayList<T>();
    protected int currentIndex = -1;
    protected List<T> currentIMEItems = new ArrayList<T>();

    protected long lastSearchedTime = 0L;

    public IMEEngine(List<T> elements){
        imeElements = elements;
    }

    public IMEEngine(){}

    public void setImeElements(List<T> elements){
        imeElements = elements;
    }


    public List<T> getCurrentIMEItems(){
        return currentIMEItems;
    }

    public boolean computeCurrentIMEElement(long timecode){
        return linearSearch(timecode);
    }

    // returns true if there is an update of the current item
    public boolean binarySearch(long movieTimecode){
        T computedIMEItem = null;

        //double dTimeCode = (double)movieTimecode * 23.98/24;            // frame adjustment
        long timecode = movieTimecode;//(long)dTimeCode;

        if (currentIMEItems != null && currentIndex != -1 && compareCurrentTimeWithItemAtIndex(timecode, currentIndex) == 0) {
           return false;
        }else{
            currentIMEItems = new ArrayList<T>();
        }


        if (timecode < 0 || imeElements.size() == 0) {
            currentIMEItems = new ArrayList<T>();
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
        if (computedIMEItem != null) {
            if (currentIMEItems.size()> 0 && currentIMEItems.get(0).equals(computedIMEItem))
                return false;
            else
                currentIMEItems = new ArrayList<T>();
            currentIMEItems.add(computedIMEItem);
            lastSearchedTime = timecode;
            return true;
        }else
            return false;
    }

    // returns true if there is an update of the current item
    public boolean linearSearch(long movieTimecode){
        double dTimeCode = (double)movieTimecode * 23.98/24;            // frame adjustment
        long timecode = (long)dTimeCode;

        int startIndex = currentIndex;

        if (startIndex == -1 || (startIndex != 0  && compareCurrentTimeWithItemAtIndex(timecode, startIndex) < 0)){
            startIndex = 0;
        }

        currentIMEItems = new ArrayList<T>();
        for (int i = startIndex; i < imeElements.size(); i++){
            if (compareCurrentTimeWithItemAtIndex(timecode, i) == 0){  //within
                for (int j = i; j< imeElements.size(); j++){
                    if (compareCurrentTimeWithItemAtIndex(timecode, j) == 0) {  //within
                        currentIMEItems.add(imeElements.get(j));
                    }else if ( compareCurrentTimeWithItemAtIndex(timecode, j) < 0)      // timecode is before the this event
                        break;
                }

                break;
            } else if ( compareCurrentTimeWithItemAtIndex(timecode, i) < 0)     // timecode is before the this event
                break;
        }


        return true;
    }

    public T searchForClosestItem(final long movieTimecode){

        //double dTimeCode = (double)movieTimecode * 23.98/24;            // frame adjustment
        long timecode = movieTimecode;//(long)dTimeCode;
        if (currentIndex != -1){
            int compareTimeCodeValue = compareCurrentTimeWithItemAtIndex(timecode, currentIndex);
            if (compareTimeCodeValue > 0)
                return binarySearchForClosestItem(movieTimecode, compareTimeCodeValue, imeElements.size() - 1);
            else if (compareTimeCodeValue < 0)
                return binarySearchForClosestItem(movieTimecode, 0, currentIndex);
            else
                return imeElements.get(currentIndex);

        }else
            return binarySearchForClosestItem(movieTimecode, 0, imeElements.size() - 1);

    }

    private T binarySearchForClosestItem(final long timecode, int lowerBoundIndex, int upperBoundIndex){

        int searchIndex = lowerBoundIndex + (upperBoundIndex - lowerBoundIndex) / 2;

        if (lowerBoundIndex == upperBoundIndex)
            return imeElements.get(lowerBoundIndex);

        int compareTimeCodeValue = compareCurrentTimeWithItemAtIndex(timecode, searchIndex);

        if (compareTimeCodeValue > 0){ //after
            //check in next element is after this time code => gap in IME, no element for this time
            if (searchIndex + 1 < imeElements.size()){
                int toNext = compareCurrentTimeWithItemAtIndex(timecode, searchIndex + 1);
                if (toNext <= 0){
                    currentIndex = searchIndex;
                    return imeElements.get(searchIndex);
                } else{
                    return binarySearchForClosestItem(timecode, searchIndex, upperBoundIndex);
                }
            }
        } else if (compareTimeCodeValue < 0) {// before
            if (searchIndex > 0) {
                int toNext = compareCurrentTimeWithItemAtIndex(timecode, searchIndex - 1);
                if (toNext >= 0) {
                    currentIndex = searchIndex - 1;
                    return imeElements.get(searchIndex - 1);
                } else {
                    return binarySearchForClosestItem(timecode, lowerBoundIndex, searchIndex);
                }
            }
        }

        currentIndex = searchIndex;
        return imeElements.get(searchIndex);

    }

    /*
    Returns: 0 = timecode within the element time range
            >0 = timecode after the element time range
            <0 = timecode before the element time range

     */
    public abstract int compareCurrentTimeWithItemAtIndex(long timecode, int index);



}
