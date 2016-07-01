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
import com.google.android.gms.maps.model.MarkerOptions;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData.LocationItem;
import com.wb.nextgen.data.MovieMetaData.SceneLocation;
import com.wb.nextgen.util.HttpImageHelper;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.ImageGetter;
import com.wb.nextgen.util.utils.NextGenLogger;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECSceneLocationMapFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    public static interface OnSceneLocationSelectedListener{
        void onSceneLocationSelected(int selectedIndex);
    }

    protected MapView mapView;

    private Spinner locationSpinner;
    private Button mapButton;
    private Button satelliteButton;

    private List<SceneLocation> sceneLocations;
    private ArrayAdapter<String> spinnerAdaptor;
    private OnSceneLocationSelectedListener onSceneLocationSelectedListener;


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
        if(mapView != null)
            mapView.onCreate(savedInstanceState);


        locationSpinner = (Spinner) view.findViewById(R.id.scene_locations_spinner);

        locationSpinner.setOnItemSelectedListener(this);

        if (sceneLocations != null) {
            List<String> list = new ArrayList<String>();
            for (SceneLocation scLoc : sceneLocations) {
                list.add(scLoc.name);
            }
            spinnerAdaptor = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, list);
            spinnerAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            if (locationSpinner != null)
                locationSpinner.setAdapter(spinnerAdaptor);
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
    public void onDestroy(){
        if(mapView != null)
            mapView.onDestroy();
        super.onDestroy();

    }

    @Override
    public void onResume(){
        super.onResume();
        if(mapView != null)
            mapView.onResume();
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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        SceneLocation locationItem = sceneLocations.get(pos);
        setLocationItem(locationItem.name, locationItem.getRepresentativeLocationItem());
        if (onSceneLocationSelectedListener != null){
            onSceneLocationSelectedListener.onSceneLocationSelected(pos);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void setSceneLocations(List<SceneLocation> locations){
        if (locations != null && locations.size() > 0) {
            sceneLocations = locations;
            setLocationItem(sceneLocations.get(0).name, sceneLocations.get(0).getRepresentativeLocationItem());
        }
    }

    private void setupPins(){
        final List<LocationItem> allLocations = new ArrayList<LocationItem>();
        if (sceneLocations != null && sceneLocations.size() > 1) {
            for (int i = 1; i < sceneLocations.size(); i++) {
                allLocations.addAll(sceneLocations.get(i).getAllSubLocationItems());

            }
        }

        HttpImageHelper.getAllMapPins(allLocations, new ResultListener<Boolean>() {
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

                                    googleMap.getUiSettings().setMapToolbarEnabled(true);
                                    googleMap.getUiSettings().setCompassEnabled(true);
                                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);



                                    for (LocationItem location : allLocations) {
                                        LatLng latlng = new LatLng(location.latitude, location.longitude);
                                        BitmapDescriptor bmDes =
                                                BitmapDescriptorFactory.fromBitmap(HttpImageHelper.getMapPinBitmap(location.pinImage.url));
                                        MarkerOptions markerOpt = new MarkerOptions()
                                                .position(latlng).title(location.getTitle()).snippet(location.address)
                                                .icon(bmDes);

                                        googleMap.addMarker(markerOpt).showInfoWindow();
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


    public void setLocationItem(String textTitle, final LocationItem locationItem){
        if (locationItem != null) {
            title = textTitle;


            if (mapView != null) {
                final LatLng location = new LatLng(locationItem.latitude, locationItem.longitude);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, locationItem.zoom));   // set location
                        googleMap.getMaxZoomLevel();

                        googleMap.getUiSettings().setMapToolbarEnabled(true);
                        googleMap.getUiSettings().setCompassEnabled(true);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        //BitmapDescriptor bmDes = BitmapDescriptorFactory.fromBitmap(NextGenApplication.getMovieMetaData().getMapPinBitmap());
                       /* grlgoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        MarkerOptions markerOpt = new MarkerOptions()
                                .position(location).title(locationItem.getTitle()).snippet(locationItem.address)
                                .icon(bmDes);

                        googleMap.addMarker(markerOpt).showInfoWindow();*/
                        //googleMap.getUiSettings().setMapToolbarEnabled(false);
                        googleMap.setOnMapClickListener(null);
                        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            @Override
                            public void onCameraChange(CameraPosition camPos) {
                                if (camPos.zoom > locationItem.zoom && location != null) {
                                    // set zoom 17 and disable zoom gestures so map can't be zoomed out
                                    // all the way
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,locationItem.zoom));
                                    googleMap.getUiSettings().setZoomGesturesEnabled(false);
                                }
                                if (camPos.zoom <= 17) {
                                    googleMap.getUiSettings().setZoomGesturesEnabled(true);
                                }

                                //LatLngBounds visibleBounds =  googleMap.getProjection().getVisibleRegion().latLngBounds;

                            }
                        });
                        //googleMap.addMarker(new MarkerOptions().position(new LatLng(locationItem.latitude, locationItem.longitude)).title("Marker"));
                    }
                });
            }

        }
    }



}
