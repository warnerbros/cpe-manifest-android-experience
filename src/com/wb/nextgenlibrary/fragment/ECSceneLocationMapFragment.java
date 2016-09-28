package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData.LocationItem;
import com.wb.nextgenlibrary.util.HttpImageHelper;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECSceneLocationMapFragment extends Fragment implements /*AdapterView.OnItemSelectedListener,*/ View.OnClickListener, GoogleMap.OnMarkerClickListener{

    public static interface OnSceneLocationSelectedListener{
        void onSceneLocationIndexSelected(int selectedIndex);
        void onSceneLocationSelected(LocationItem location);
    }

    protected MapView mapView;

    //private Spinner locationSpinner;
    private Button mapButton;
    private Button satelliteButton;

    private List<LocationItem> defaultSceneLocations;
    //private List<SceneLocation> sceneLocations;
    //private ArrayAdapter<String> spinnerAdaptor;
    private OnSceneLocationSelectedListener onSceneLocationSelectedListener;
    private HashMap<LatLng, LocationItem> markerLocationMap = new HashMap<LatLng, LocationItem>();


    //LocationItem selectedLocationItem = null;
    String title = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.scene_location_map_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.ec_mapview);
        if(mapView != null) {
            mapView.onCreate(savedInstanceState);
            //mapView.setOn
        }

        if (defaultSceneLocations != null) {
            List<String> list = new ArrayList<String>();
            for (LocationItem scLoc : defaultSceneLocations) {
                list.add(scLoc.getTitle());
            }
            setupPins();
        }

        mapButton = (Button) view.findViewById(R.id.map_button);
        satelliteButton = (Button) view.findViewById(R.id.satellite_button);
        if (satelliteButton != null && mapButton != null) {
            mapButton.setOnClickListener(this);
            satelliteButton.setOnClickListener(this);
            onClick(mapButton);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy(){
        if(mapView != null)
            mapView.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onPause(){
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
        //locationSpinner.setOnItemSelectedListener(null);

    }

    @Override
    public void onResume(){
        super.onResume();
        if(mapView != null)
            mapView.onResume();
        //locationSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(final View v){
        if (mapButton != null && satelliteButton != null) {


            if (mapView != null) {
                //final LatLng location = new LatLng(selectedLocationItem.latitude, selectedLocationItem.longitude);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {

                        if (v.equals(mapButton)) {
                            if (googleMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL)
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            mapButton.setSelected(true);
                            satelliteButton.setSelected(false);
                        } else if (v.equals(satelliteButton) ) {
                            if (googleMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE)
                                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            mapButton.setSelected(false);
                            satelliteButton.setSelected(true);

                        }


                    }
                });
            }
        }
        NextGenAnalyticData.reportEvent(getActivity(), this, "Satellite/Map View",
                NextGenAnalyticData.AnalyticAction.ACTION_CLICK, null);
    }

    public void setOnSceneLocationSelectedListener(OnSceneLocationSelectedListener listener){
        onSceneLocationSelectedListener = listener;
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void setSelectionFromSlider(LocationItem location){

        setLocationItem(location.getTitle(), location);

    }

    public void setDefaultSceneLocations(List<LocationItem> locations){
        defaultSceneLocations = locations;
    }

    private void setupPins(){


        HttpImageHelper.getAllMapPinsBySceneLocation(defaultSceneLocations, new ResultListener<Boolean>() {
            @Override
            public void onResult(Boolean result) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mapView != null) {
                            //final LatLng location = new LatLng(selectedLocationItem.latitude, selectedLocationItem.longitude);
                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final GoogleMap googleMap) {

                                    //googleMap.getUiSettings().setMapToolbarEnabled(true);
                                    googleMap.getUiSettings().setCompassEnabled(true);
                                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                    //googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                    googleMap.setOnMarkerClickListener(ECSceneLocationMapFragment.this);

                                    googleMap.getMaxZoomLevel();

                                    googleMap.getUiSettings().setMapToolbarEnabled(true);
                                    googleMap.getUiSettings().setCompassEnabled(true);
                                    googleMap.getUiSettings().setZoomControlsEnabled(true);

                                    googleMap.setOnMapClickListener(null);

                                    LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();

                                    for (LocationItem sceneLocation : defaultSceneLocations) {
                                        LatLng latlng = new LatLng(sceneLocation.latitude, sceneLocation.longitude);

                                        boundsBuilder.include(latlng);
                                        markerLocationMap.put(latlng, sceneLocation);
                                        BitmapDescriptor bmDes ;
                                        if (sceneLocation.pinImage != null && !StringHelper.isEmpty(sceneLocation.pinImage.url)) {
                                            bmDes = BitmapDescriptorFactory.fromBitmap(HttpImageHelper.getMapPinBitmap(sceneLocation.pinImage.url));
                                        }else {
                                            bmDes = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                                        }
                                        MarkerOptions markerOpt = new MarkerOptions()
                                                .position(latlng).title(sceneLocation.getTitle()).snippet(sceneLocation.address)
                                                .icon(bmDes);

                                        googleMap.addMarker(markerOpt);
                                    }

                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
                                }
                            });
                        }
                    }
                });

            }

            @Override
            public <E extends Exception> void onException(E e) {
                NextGenLogger.d(F.TAG, e.getLocalizedMessage());
            }
        });

    }

    public boolean onMarkerClick(Marker marker){
        marker.getTitle();
        LatLng position = marker.getPosition();
        LocationItem sl = markerLocationMap.get(position);
        if (sl != null) {
            if (onSceneLocationSelectedListener != null && sl != null) {
                onSceneLocationSelectedListener.onSceneLocationSelected(sl);
            }
            setLocationItem(marker.getTitle(), sl);

        }
        return false;
    }

    public void setLocationItem(String title, LocationItem locationItem){
        if (locationItem != null){
            List<LocationItem> locationItems = new ArrayList<LocationItem>();
            locationItems.add(locationItem);
            setLocationItems(locationItem.getTitle(), locationItems);
        }else
            setLocationItems(title, defaultSceneLocations);
    }

    private void setLocationItems(String name, final List<LocationItem> sceneLocations){
        if (sceneLocations != null) {
            title = name;


            if (mapView != null) {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {


                        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
                        for (LocationItem sceneLocation : sceneLocations){
                            boundsBuilder.include(new LatLng(sceneLocation.latitude, sceneLocation.longitude));
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));


                        if (sceneLocations == null || sceneLocations.size() == 0)
                            return;

                        final LocationItem locationItem = sceneLocations.get(0);

                        if (locationItem != null) {

                            final LatLng location = new LatLng(locationItem.latitude, locationItem.longitude);
                            if (locationItem != null) {
                                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                                    @Override
                                    public void onCameraChange(CameraPosition camPos) {
                                        if (camPos.zoom > locationItem.zoom && location != null) {
                                            // set zoom 17 and disable zoom gestures so map can't be zoomed out
                                            // all the way
                                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, locationItem.zoom));
                                            googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                        }
                                        if (camPos.zoom <= 17) {
                                            googleMap.getUiSettings().setZoomGesturesEnabled(true);
                                        }


                                    }
                                });
                            }
                        }
                        //googleMap.addMarker(new MarkerOptions().position(new LatLng(locationItem.latitude, locationItem.longitude)).title("Marker"));
                    }
                });
            }

        }
    }



}
