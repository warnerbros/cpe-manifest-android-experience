package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

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
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData.LocationItem;
import com.wb.nextgen.model.SceneLocation;
import com.wb.nextgen.util.HttpImageHelper;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.ImageGetter;
import com.wb.nextgen.util.utils.NextGenLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECSceneLocationMapFragment extends Fragment implements /*AdapterView.OnItemSelectedListener,*/ View.OnClickListener, GoogleMap.OnMarkerClickListener{

    public static interface OnSceneLocationSelectedListener{
        void onSceneLocationIndexSelected(int selectedIndex);
        void onSceneLocationSelected(SceneLocation location);
    }

    protected MapView mapView;

    //private Spinner locationSpinner;
    private Button mapButton;
    private Button satelliteButton;

    private List<SceneLocation> defaultSceneLocations;
    //private List<SceneLocation> sceneLocations;
    //private ArrayAdapter<String> spinnerAdaptor;
    private OnSceneLocationSelectedListener onSceneLocationSelectedListener;
    private HashMap<LatLng, SceneLocation> markerLocationMap = new HashMap<LatLng, SceneLocation>();


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
            for (SceneLocation scLoc : defaultSceneLocations) {
                list.add(scLoc.name);
            }
            setupPins();
        }

        mapButton = (Button) view.findViewById(R.id.map_button);
        satelliteButton = (Button) view.findViewById(R.id.satellite_button);
        if (satelliteButton != null && mapButton != null) {
            satelliteButton.setOnClickListener(this);
            mapButton.setOnClickListener(this);
            onClick(satelliteButton);
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

                        if (v.equals(mapButton) && googleMap.getMapType() != GoogleMap.MAP_TYPE_NORMAL) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                            mapButton.setSelected(true);
                            satelliteButton.setSelected(false);
                        } else if (v.equals(satelliteButton) && googleMap.getMapType() != GoogleMap.MAP_TYPE_SATELLITE) {
                            googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                            mapButton.setSelected(false);
                            satelliteButton.setSelected(true);

                        }


                    }
                });
            }
        }
    }

    public void setOnSceneLocationSelectedListener(OnSceneLocationSelectedListener listener){
        onSceneLocationSelectedListener = listener;
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void setSelectionFromSlider(SceneLocation location){

        setLocationItem(location.name, location);

    }

    public void setDefaultSceneLocations(List<SceneLocation> locations){
        defaultSceneLocations = new ArrayList<SceneLocation>();
        for (int i = 0; i < locations.size(); i++) {
            defaultSceneLocations.addAll(locations.get(i).getAllSubLocationItems());
        }
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
                                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                    googleMap.setOnMarkerClickListener(ECSceneLocationMapFragment.this);


                                    for (SceneLocation sceneLocation : defaultSceneLocations) {
                                        LatLng latlng = new LatLng(sceneLocation.location.latitude, sceneLocation.location.longitude);

                                        markerLocationMap.put(latlng, sceneLocation);
                                        BitmapDescriptor bmDes =
                                                BitmapDescriptorFactory.fromBitmap(HttpImageHelper.getMapPinBitmap(sceneLocation.location.pinImage.url));
                                        MarkerOptions markerOpt = new MarkerOptions()
                                                .position(latlng).title(sceneLocation.location.getTitle()).snippet(sceneLocation.location.address)
                                                .icon(bmDes);

                                        googleMap.addMarker(markerOpt);
                                    }
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
        SceneLocation sl = markerLocationMap.get(position);
        if (sl != null) {
            if (onSceneLocationSelectedListener != null && sl != null) {
                onSceneLocationSelectedListener.onSceneLocationSelected(sl);
            }
            setLocationItem(sl.name, sl);

        }
        return false;
    }

    public void setLocationItem(String textTitle, SceneLocation sceneLocationObj){
        if (sceneLocationObj != null)
            setLocationItems(textTitle, sceneLocationObj.getAllSubLocationItems(), sceneLocationObj.location);
        else
            setLocationItems(textTitle, defaultSceneLocations, null);
    }

    private void setLocationItems(String textTitle, final List<SceneLocation> sceneLocations, final LocationItem locationItem){
        if (sceneLocations != null) {
            title = textTitle;


            if (mapView != null) {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {


                        LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
                        for (SceneLocation sceneLocation : sceneLocations){
                            boundsBuilder.include(new LatLng(sceneLocation.location.latitude, sceneLocation.location.longitude));
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));

                        googleMap.getMaxZoomLevel();

                        googleMap.getUiSettings().setMapToolbarEnabled(true);
                        googleMap.getUiSettings().setCompassEnabled(true);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);

                        googleMap.setOnMapClickListener(null);

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
