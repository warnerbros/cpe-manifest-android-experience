package com.wb.nextgen.model;

/**
 * Created by gzcheng on 1/29/16.
 */
public class SceneLocation {

    public static final SceneLocation LOCATION_LOS_ANGELES = new SceneLocation(34.052235, -118.243683, "Los Angeles");
    public static final SceneLocation LOCATION_NEW_YORK = new SceneLocation(40.7127, -74.0059, "New York");
    public static final SceneLocation LOCATION_SAN_FRANCISCO = new SceneLocation(37.7833, -122.4167, "San Francisco");
    public static final SceneLocation LOCATION_CHICAGO = new SceneLocation(41.8369, -87.6847, "Chicago");
    public static final SceneLocation LOCATION_BOSTON = new SceneLocation(42.3601, -71.0589, "Boston");
    public static final SceneLocation LOCATION_SYDNEY = new SceneLocation(-33.8650, 151.2094, "Sydney");

    public final double longitude;
    public final double latitude;
    public final String locationName;

    public SceneLocation(double latitude, double longitude, String locationName){
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
    }
}
