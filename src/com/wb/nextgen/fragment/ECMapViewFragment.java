package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.util.PicassoTrustAll;

import java.util.Map;


/**
 * Created by gzcheng on 3/31/16.
 */
public class ECMapViewFragment extends Fragment {
    protected MapView mapView;

    protected TextView triviaTitle;
    protected TextView triviaContent;


    MovieMetaData.LocationItem selectedLocationItem = null;
    String title = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ec_map_view, container, false);
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


    public void setLocationItem(String textTitle, MovieMetaData.LocationItem locationItem){
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
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14.0f));   // set location

                        BitmapDescriptor bmDes = BitmapDescriptorFactory.fromBitmap(NextGenApplication.getMovieMetaData().getMapPinBitmap());
                        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        MarkerOptions markerOpt = new MarkerOptions()
                                .position(location).title(selectedLocationItem.getTitle()).snippet(selectedLocationItem.address)
                                .icon(bmDes);

                        googleMap.addMarker(markerOpt).showInfoWindow();
                        //googleMap.getUiSettings().setMapToolbarEnabled(false);
                        googleMap.setOnMapClickListener(null);
                        //googleMap.addMarker(new MarkerOptions().position(new LatLng(locationItem.latitude, locationItem.longitude)).title("Marker"));
                    }
                });
            }

        }
    }

}
