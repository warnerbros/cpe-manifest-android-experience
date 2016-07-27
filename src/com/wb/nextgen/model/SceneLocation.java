package com.wb.nextgen.model;

import com.wb.nextgen.data.MovieMetaData.PresentationDataItem;
import com.wb.nextgen.data.MovieMetaData.LocationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/29/16.
 */
public class SceneLocation{
    private List<PresentationDataItem> presentationItems = null;
    public final List<SceneLocation> childrenSceneLocations = new ArrayList<SceneLocation>();
    public final LocationItem location;
    public final String name;
    public final SceneLocation parentSceneLocation;

    public SceneLocation(SceneLocation parentSceneLocation, String name, LocationItem location){
        this.parentSceneLocation = parentSceneLocation;
        this.name = name;
        this.location = location;

    }

    public LocationItem getRepresentativeLocationItem(){
        if (location != null){
            return location;
        }else if (childrenSceneLocations != null && childrenSceneLocations.size() > 0){
            return childrenSceneLocations.get(0).getRepresentativeLocationItem();
        }else
            return null;
    }

    public List<SceneLocation> getAllSubLocationItems(){
        List<SceneLocation> resultList = new ArrayList<SceneLocation>();
        if (location != null){
            resultList.add(this);
        }else if (childrenSceneLocations.size() > 0){
            for (SceneLocation child : childrenSceneLocations){
                resultList.addAll(child.getAllSubLocationItems());
            }
        }
        return resultList;
    }

    public SceneLocation getOuterMostParent(){
        SceneLocation outerMost = this;
        while (outerMost.parentSceneLocation != null){
            outerMost = outerMost.parentSceneLocation;
        }
        return outerMost;
    }

    public List<PresentationDataItem> getPresentationItems(){
        if (presentationItems == null){
            if (location.getPresentationDataItems() != null)
                presentationItems = location.getPresentationDataItems();
            else
                presentationItems = new ArrayList<PresentationDataItem>();
        }

        return presentationItems;
    }
}