package com.wb.nextgenlibrary.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.util.HttpImageHelper;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.concurrent.ResultListener;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gzcheng on 3/31/16.
 */
public class IMEECMapViewFragment extends AbstractNextGenFragment implements View.OnClickListener {
	protected MapView mapView;

    protected TextView ecTitle;
    protected TextView ecNameTextView;
    protected RecyclerView sceneLocationECRecyclerView;
    protected ECVideoViewFragment videoViewFragment;
    protected ECGalleryViewFragment galleryViewFragment;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;

    private FrameLayout videoFrame, galleryFrame;

    MovieMetaData.LocationItem selectedLocationItem = null;
    String title = null;

    Bundle savedInstanceState;
    public int getContentViewId(){
        return R.layout.ime_map_view;
    }
    private Button mapButton;
    private Button satelliteButton;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        mapView = (MapView) view.findViewById(R.id.ec_mapview);
        if(mapView != null)
            mapView.onCreate(savedInstanceState);
        ecTitle = (TextView) view.findViewById(R.id.ec_title_name);
        ecNameTextView = (TextView)view.findViewById(R.id.ec_content_name);
        if (selectedLocationItem != null && title != null){
            setLocationItem(title, selectedLocationItem);
        }

        videoFrame = (FrameLayout) view.findViewById(R.id.map_video_frame);
        galleryFrame = (FrameLayout) view.findViewById(R.id.map_gallery_frame);

        recyclerViewLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        sceneLocationECRecyclerView = (RecyclerView) view.findViewById(R.id.map_associate_ecs_list);
        sceneLocationECRecyclerView.setLayoutManager(recyclerViewLayoutManager);
        sceneLocationECRecyclerView.setAdapter(new ECsAdapter());

        videoViewFragment = new ECVideoViewFragment();//(ECVideoViewFragment) getChildFragmentManager().findFragmentById(R.id.ime_secene_location_video);
        galleryViewFragment = new ECGalleryViewFragment();// getChildFragmentManager().findFragmentById(R.id.ime_secene_location_gallery);

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.map_video_frame, videoViewFragment, videoViewFragment.getClass().toString());
        fragmentTransaction.addToBackStack(videoViewFragment.getClass().toString());
        fragmentTransaction.commit();

        FragmentTransaction fragmentTransaction2 = getFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.map_gallery_frame, galleryViewFragment, galleryViewFragment.getClass().toString());
        fragmentTransaction2.addToBackStack(galleryViewFragment.getClass().toString());
        fragmentTransaction2.commit();

        mapButton = (Button) view.findViewById(R.id.map_button);
        satelliteButton = (Button) view.findViewById(R.id.satellite_button);
        if (satelliteButton != null && mapButton != null) {
            mapButton.setOnClickListener(this);
            satelliteButton.setOnClickListener(this);
            onClick(mapButton);
        }
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
        NGEAnalyticData.reportEvent(getActivity(), this, NGEAnalyticData.AnalyticAction.ACTION_SET_MAP_TYPE, v.equals(mapButton) ? "road" : "satellite", null);
    }

    @Override
    String getReportContentName(){
        return title;
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
    protected int getCloseButtonId(){
        return R.id.ime_map_close_button;
    }

    public void setLocationItem(String textTitle, final MovieMetaData.LocationItem locationItem){
        if (locationItem != null) {
            title = textTitle;
            selectedLocationItem = locationItem;
            if (ecTitle != null) {
                ecTitle.setText(textTitle);
            }
            if (ecNameTextView != null) {
                ecNameTextView.setText(selectedLocationItem.description);
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
                                mapView.getMapAsync(new IMEOnMapReadyCallback(selectedLocationItem));
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

    class IMEOnMapReadyCallback implements OnMapReadyCallback{
        MovieMetaData.LocationItem locationItem;
        GoogleMap.OnMapClickListener onMapClickListener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        };

        public IMEOnMapReadyCallback(MovieMetaData.LocationItem locationItem){
            this.locationItem = locationItem;

        }
        @Override
        public void onMapReady(final GoogleMap googleMap) {

            final LatLng location = new LatLng(locationItem.latitude, locationItem.longitude);

            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            googleMap.getUiSettings().setMapToolbarEnabled(false);


            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, locationItem.zoom));   // set location

            View currentLevelview = satelliteButton;
            int padding = satelliteButton.getHeight();
            while (currentLevelview != mapView.getParent() && currentLevelview != null){
                ViewGroup.LayoutParams layoutParams = currentLevelview.getLayoutParams();
                if (layoutParams instanceof LinearLayout.LayoutParams) {
                    padding += ((LinearLayout.LayoutParams) layoutParams).bottomMargin;
                } else if (layoutParams instanceof RelativeLayout.LayoutParams){
                    padding += ((RelativeLayout.LayoutParams) layoutParams).bottomMargin;
                }
                padding += currentLevelview.getPaddingTop() + currentLevelview.getPaddingBottom();
                if (currentLevelview.getParent() != null && currentLevelview.getParent() instanceof View)
                    currentLevelview = (View)(currentLevelview.getParent());
                else
                    currentLevelview = null;
            }
            googleMap.moveCamera(CameraUpdateFactory.scrollBy(0, -padding));
            googleMap.getMaxZoomLevel();
            BitmapDescriptor bmDes ;
            if (selectedLocationItem.pinImage != null && !StringHelper.isEmpty(selectedLocationItem.pinImage.url)) {
                bmDes = BitmapDescriptorFactory.fromBitmap(HttpImageHelper.getMapPinBitmap(selectedLocationItem.pinImage.url));
            }else {
                bmDes = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
            }

            MarkerOptions markerOpt = new MarkerOptions()
                    .position(location).title(selectedLocationItem.getTitle()).snippet(selectedLocationItem.address)
                    .icon(bmDes);

            googleMap.addMarker(markerOpt).showInfoWindow();
            googleMap.setOnMapClickListener(onMapClickListener);
            googleMap.setInfoWindowAdapter(new ECSceneLocationMapFragment.PopupAdapter(getActivity().getLayoutInflater()));
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

    public class ECViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        ImageView locationPhoto;
        ImageView locationPlayIcon;
        Object currentItem;

        ECViewHolder(View itemView, Object item) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            locationPhoto = (ImageView) itemView.findViewById(R.id.location_photo);
            locationPlayIcon = (ImageView) itemView.findViewById(R.id.location_play_image);
            this.currentItem = item;
            itemView.setOnClickListener(this);
        }

        public void setItem(Object item) {
            currentItem = item;
            if (item instanceof MovieMetaData.LocationItem) {
                final MovieMetaData.LocationItem locationItem = ((MovieMetaData.LocationItem) item);
                locationPlayIcon.setVisibility(View.INVISIBLE);
                if (locationItem != null) { // TODO: CHECK

                    ViewTreeObserver viewTreeObserver = locationPhoto.getViewTreeObserver();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {

                                locationPhoto.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                ViewGroup viewParent =(ViewGroup)locationPhoto.getParent();
                                int height = viewParent.getHeight();
                                int width = viewParent.getWidth();
                                final String mapImageUrl = locationItem.getGoogleMapImageUrl(width, height);

                                NextGenGlide.load(getActivity(), mapImageUrl).fitCenter().into(locationPhoto);

                            }
                        });
                    }

                }


            } else if (item instanceof MovieMetaData.PresentationDataItem) {
                if (item instanceof MovieMetaData.AudioVisualItem)
                    locationPlayIcon.setVisibility(View.VISIBLE);
                else
                    locationPlayIcon.setVisibility(View.INVISIBLE);
                if (TabletUtils.isTablet()){
                    NextGenGlide.load(getActivity(), ((MovieMetaData.PresentationDataItem) item).getPosterImgUrl()).fitCenter().into(locationPhoto);

                }else
                    NextGenGlide.load(getActivity(), ((MovieMetaData.PresentationDataItem) item).getPosterImgUrl()).fitCenter().into(locationPhoto);
            }
        }

        @Override
        public void onClick(View v) {
            if (currentItem != null) {
                if (currentItem instanceof MovieMetaData.LocationItem) {
                    if (videoViewFragment != null){
                        videoViewFragment.stopPlayback();
                    }
                    mapView.setVisibility(View.VISIBLE);
                    videoFrame.setVisibility(View.GONE);
                    galleryFrame.setVisibility(View.GONE);
                    NGEAnalyticData.reportEvent(getActivity(), IMEECMapViewFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_LOCATION_THUMBNAIL, ((MovieMetaData.LocationItem) currentItem).getId(), null);
                } else if (currentItem instanceof MovieMetaData.AudioVisualItem) {
                    mapView.setVisibility(View.GONE);
                    videoFrame.setVisibility(View.VISIBLE);
                    galleryFrame.setVisibility(View.GONE);

                    if (videoViewFragment != null) {
                        videoViewFragment.setShouldAutoPlay(true);
                        videoViewFragment.setShouldHideMetaData(true);
                        videoViewFragment.setShouldShowCloseBtn(false);
                        videoViewFragment.setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY);
                        videoViewFragment.setAudioVisualItem((MovieMetaData.AudioVisualItem) currentItem);
                        NGEAnalyticData.reportEvent(getActivity(), IMEECMapViewFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, ((MovieMetaData.AudioVisualItem) currentItem).videoId, null);
                    }

                } else if (currentItem instanceof MovieMetaData.ECGalleryItem) {
                    mapView.setVisibility(View.GONE);
                    videoFrame.setVisibility(View.GONE);
                    galleryFrame.setVisibility(View.VISIBLE);

                    if (videoViewFragment != null){
                        videoViewFragment.stopPlayback();
                    }
                    if (galleryViewFragment != null) {
                        galleryViewFragment.setShouldHideMetaData(true);
                        galleryViewFragment.setShouldShowCloseBtn(false);
                        galleryViewFragment.setShouldShowFullScreenBtn(false);
                        galleryViewFragment.setShouldShowShareBtn(false);
                        galleryViewFragment.setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY);

                        galleryViewFragment.setCurrentGallery((MovieMetaData.ECGalleryItem) currentItem);
                        NGEAnalyticData.reportEvent(getActivity(), IMEECMapViewFragment.this, NGEAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, ((MovieMetaData.ECGalleryItem) currentItem).galleryId, null);
                    }
                }
            }
        }
    }

    public class ECsAdapter extends RecyclerView.Adapter<ECViewHolder> {


        int lastloadingIndex = -1;
        static final int PAGEITEMCOUNT = 6;


        public void reset() {
            lastloadingIndex = -1;
        }

        @Override
        public ECViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ime_locations_card_view, viewGroup, false);
            ECViewHolder pvh = new ECViewHolder(v, null);
            return pvh;
        }

        public void onBindViewHolder(ECViewHolder holder, int position) {

            if (selectedLocationItem != null) {
                if (position == 0){
                    holder.setItem(selectedLocationItem);
                }else{
                    holder.setItem(selectedLocationItem.getPresentationDataItems().get(position - 1));
                }
            }

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount() {
            if (selectedLocationItem != null){
                return selectedLocationItem.getPresentationDataItems() != null ? 1 + selectedLocationItem.getPresentationDataItems().size() : 1;
            }else
                return 0;
        }

    }
    @Override
    protected void onCloseButtonClick(){
        getFragmentManager().popBackStack();        // pop gallery fragment
        getFragmentManager().popBackStack();        // pop video fragment
        super.onCloseButtonClick();
    }

}
