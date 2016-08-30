package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.view.View;
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
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.util.HttpImageHelper;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECMapViewFragment extends AbstractNextGenFragment {
    protected MapView mapView;

    protected TextView triviaTitle;
    protected TextView triviaContent;


    MovieMetaData.LocationItem selectedLocationItem = null;
    String title = null;

    public int getContentViewId(){
        return R.layout.ec_map_view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.ec_mapview);
        if(mapView != null)
            mapView.onCreate(savedInstanceState);
        triviaTitle = (TextView) view.findViewById(R.id.ec_title_name);
        triviaContent = (TextView) view.findViewById(R.id.ec_content_name);
        if (selectedLocationItem != null && title != null){
            setLocationItem(title, selectedLocationItem);
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


    public void setLocationItem(String textTitle, final MovieMetaData.LocationItem locationItem){
        if (locationItem != null) {
            title = textTitle;
            selectedLocationItem = locationItem;
            if (triviaContent != null) {
                triviaContent.setText(selectedLocationItem.getTitle());
            }
            if (triviaTitle != null) {
                triviaTitle.setText(textTitle);
            }
            if (mapView != null) {
                final LatLng location = new LatLng(selectedLocationItem.latitude, selectedLocationItem.longitude);

                List<MovieMetaData.LocationItem> locList = new ArrayList<MovieMetaData.LocationItem>();
                locList.add(locationItem);
                HttpImageHelper.getAllMapPins(locList, new ResultListener<Boolean>() {
                    @Override
                    public void onResult(Boolean result) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapView.getMapAsync(new OnMapReadyCallback() {
                                    @Override
                                    public void onMapReady(final GoogleMap googleMap) {

                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, locationItem.zoom));   // set location
                                        googleMap.getMaxZoomLevel();

                                        BitmapDescriptor bmDes =
                                                BitmapDescriptorFactory.fromBitmap(HttpImageHelper.getMapPinBitmap(selectedLocationItem.pinImage.url));

                                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                                        MarkerOptions markerOpt = new MarkerOptions()
                                                .position(location).title(selectedLocationItem.getTitle()).snippet(selectedLocationItem.address)
                                                .icon(bmDes);

                                        googleMap.addMarker(markerOpt).showInfoWindow();
                                        //googleMap.getUiSettings().setMapToolbarEnabled(false);
                                        googleMap.setOnMapClickListener(null);
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

                                                //LatLngBounds visibleBounds =  googleMap.getProjection().getVisibleRegion().latLngBounds;

                                            }
                                        });
                                        //googleMap.addMarker(new MarkerOptions().position(new LatLng(locationItem.latitude, locationItem.longitude)).title("Marker"));
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public <E extends Exception> void onException(E e) {

                    }
                });


            }

        }
    }

}
