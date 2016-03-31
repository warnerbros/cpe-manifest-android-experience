package com.wb.nextgen.fragment;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.model.NextGenIMEEngine;
import com.wb.nextgen.model.Presentation;
import com.wb.nextgen.model.SceneLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/29/16.
 */
public class NextGenIMEMapFragment extends SupportMapFragment implements NextGenPlaybackStatusListener, LocationSource, LocationListener{

    GoogleMap map;
    Criteria myCriteria;
    OnLocationChangedListener myLocationListener = null;
    final int RQS_GooglePlayServices = 1;
    LocationManager locationMng;

    NextGenIMEEngine<SceneLocation> mapIMEEngine = new NextGenIMEEngine<SceneLocation>();

    List<MovieMetaData.IMEElement<SceneLocation>> mapElements = new ArrayList<MovieMetaData.IMEElement<SceneLocation>>();
   /* public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_ime_map, container, false);
    }*/

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        map = this.getMap();
        if (map != null){
            map.setBuildingsEnabled(true);
            map.setMyLocationEnabled(true);

            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            myCriteria = new Criteria();
            myCriteria.setAccuracy(Criteria.ACCURACY_FINE);
            locationMng = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        }

        mapElements.add(mapIMEEngine.createNextGenIMEElement(2000, 5000, SceneLocation.LOCATION_LOS_ANGELES));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(7000, 17000, SceneLocation.LOCATION_NEW_YORK));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(20000, 25000, SceneLocation.LOCATION_SYDNEY));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(30000, 36000, SceneLocation.LOCATION_CHICAGO));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(50000, 65000, SceneLocation.LOCATION_SAN_FRANCISCO));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(72000, 95000, SceneLocation.LOCATION_BOSTON));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(102000, 105000, SceneLocation.LOCATION_NEW_YORK));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(132000, 145000, SceneLocation.LOCATION_LOS_ANGELES));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(152000, 165000, SceneLocation.LOCATION_SYDNEY));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(172000, 185000, SceneLocation.LOCATION_CHICAGO));
        mapElements.add(mapIMEEngine.createNextGenIMEElement(192000, 205000, SceneLocation.LOCATION_BOSTON));
        mapIMEEngine.setImeElements(mapElements);
        //map = (GoogleMap)view.findViewById(R.id.ime_map);
    }

    @Override
    public void onResume() {
        super.onResume();

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(NextGenApplication.getContext());

        if (resultCode == ConnectionResult.SUCCESS){
            Toast.makeText(NextGenApplication.getContext(),
                    "isGooglePlayServicesAvailable SUCCESS",
                    Toast.LENGTH_LONG).show();

            //Register for location updates using a Criteria, and a callback on the specified looper thread.
            locationMng.requestLocationUpdates(
                    0L,    //minTime
                    0.0f,    //minDistance
                    myCriteria,  //criteria
                    this,    //listener
                    null);   //looper

            //Replaces the location source of the my-location layer.
            map.setLocationSource(this);

        }else{
            GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), RQS_GooglePlayServices);
        }

    }

    @Override
    public void onPause() {
        map.setLocationSource(null);
        locationMng.removeUpdates(this);

        super.onPause();
    }

    public void playbackStatusUpdate(NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, long timecode){
        if (! mapIMEEngine.computeCurrentIMEElement(timecode))
            return;


        final SceneLocation location = mapIMEEngine.getCurrentIMEElement();
        if (location == null)
            return;
        else{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CameraUpdate center =
                            CameraUpdateFactory.newLatLng(new LatLng(location.latitude,
                                    location.longitude));
                    CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);

                    map.moveCamera(center);
                    map.animateCamera(zoom);
                }
            });
        }
       // handleIMEUpdate(timecode, presentationIMEEngine.getCurrentIMEElement(timecode));
    }

    @Override
    public void onLocationChanged(Location location) {
        if (myLocationListener != null) {
            myLocationListener.onLocationChanged(location);

            double lat = location.getLatitude();
            double lon = location.getLongitude();


        }
    }
    @Override
    public void activate(OnLocationChangedListener listener) {
        myLocationListener = listener;
    }

    @Override
    public void deactivate() {
        myLocationListener = null;
    }
    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
}
