package com.wb.nextgen.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.ExperienceData;
import com.wb.nextgen.data.MovieMetaData.PresentationDataItem;
import com.wb.nextgen.fragment.ECGalleryViewFragment;
import com.wb.nextgen.fragment.ECSceneLocationMapFragment;
import com.wb.nextgen.fragment.ECVideoViewFragment;
import com.wb.nextgen.interfaces.SensitiveFragmentInterface;
import com.wb.nextgen.model.Presentation;
import com.wb.nextgen.model.SceneLocation;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.ImageGetter;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;
import com.wb.nextgen.util.utils.NextGenLogger;
import com.wb.nextgen.widget.FixedAspectRatioFrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 6/29/16.
 */
public class ECSceneLocationActivity extends AbstractECView implements ECSceneLocationMapFragment.OnSceneLocationSelectedListener {

    private ECVideoViewFragment videoViewFragment = null;
    private ECGalleryViewFragment galleryViewFragment = null;
    private ECSceneLocationMapFragment mapViewFragment = null;

    private RelativeLayout contentFrame;

    private RecyclerView locationECRecyclerView;
    private TextView sliderTitleTextView;
    private LinearLayoutManager locationECLayoutManager;
    private LocationECsAdapter locationECsAdapter;
    private NextGenFragmentTransactionEngine nextGenFragmentTransactionEngine;

    private List<SceneLocation> rootSceneLocations;

    private Fragment currentFragment;

    //private int currentSelectedIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SceneLocation rootSceneLocation = new SceneLocation(getResources().getString(R.string.location_full_map), null);
        rootSceneLocation.childrenSceneLocations.addAll(ecGroupData.getSceneLocations());

        rootSceneLocations = new ArrayList<SceneLocation>();
        rootSceneLocations.add(rootSceneLocation);
        rootSceneLocations.addAll(ecGroupData.getSceneLocations());


        nextGenFragmentTransactionEngine = new NextGenFragmentTransactionEngine(this);
        if (mapViewFragment == null){
            mapViewFragment = new ECSceneLocationMapFragment();
            mapViewFragment.setSceneLocations(rootSceneLocations);
            mapViewFragment.setOnSceneLocationSelectedListener(this);
        }

        videoViewFragment = new ECVideoViewFragment();
        videoViewFragment.setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY);
        galleryViewFragment = new ECGalleryViewFragment();
        galleryViewFragment.setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY);

        sliderTitleTextView = (TextView) findViewById(R.id.scene_location_bottom_slider_text);
        if (sliderTitleTextView != null){
            sliderTitleTextView.setText(ecGroupData.title.toUpperCase());
        }
        contentFrame = (RelativeLayout)findViewById(R.id.scene_location_content_frame);
        locationECRecyclerView = (RecyclerView)findViewById(R.id.scene_location_recycler_view);

        if (locationECRecyclerView != null){
            locationECLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            locationECLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            locationECRecyclerView.setLayoutManager(locationECLayoutManager);
            locationECsAdapter = new LocationECsAdapter();
            locationECsAdapter.setSceneLocation(rootSceneLocations.get(0));
            locationECRecyclerView.setAdapter(locationECsAdapter);
        }

        transitToFragment(mapViewFragment);

    }

    private void transitToFragment(Fragment fragment){
        if (fragment != currentFragment){

            if (currentFragment!= null && currentFragment != mapViewFragment){
                onBackPressed();
            }
            nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), contentFrame.getId(), fragment);

            currentFragment = fragment;
        }
    }

    public void onLeftListItemSelected(ExperienceData ecContentData){

    }

    public int getContentViewId(){
        return R.layout.ec_scene_location_view;
    }


    public int getListItemViewLayoutId(){
        return 0;
    }


    public void onFullScreenChange(boolean bFullscreen){

    }

    public void onSceneLocationIndexSelected(int selectedIndex){
        //currentSelectedIndex = selectedIndex;
        if (locationECsAdapter != null) {
            locationECsAdapter.setSceneLocation(rootSceneLocations.get(selectedIndex));
            locationECsAdapter.notifyDataSetChanged();
        }
    }

    public void onSceneLocationSelected(SceneLocation location){
        //currentSelectedIndex = selectedIndex;
        if (locationECsAdapter != null) {
            locationECsAdapter.setSceneLocation(location);
            locationECsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        currentFragment = null;
        videoViewFragment = null;
        galleryViewFragment = null;
        mapViewFragment = null;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0 )
            finish();
        else
            currentFragment = mapViewFragment;

    }

    public class LocationECViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        TextView locationsCountText;
        TextView locationName;
        ImageView locationPhoto;
        ImageView locationPlayIcon;
        Object currentItem;
        int itemIndex;

        LocationECViewHolder(View itemView, Object item, int index) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            locationName = (TextView)itemView.findViewById(R.id.location_name);
            locationsCountText = (TextView)itemView.findViewById(R.id.location_count_text);
            locationPhoto = (ImageView)itemView.findViewById(R.id.location_photo);
            locationPlayIcon = (ImageView)itemView.findViewById(R.id.location_play_image);
            this.currentItem = item;
            itemIndex = index;
            itemView.setOnClickListener(this);
        }

        public void setItem(Object item, int position){
            currentItem = item;
            itemIndex = position;
            if (item instanceof SceneLocation) {
                MovieMetaData.LocationItem locationItem = ((SceneLocation)item).location;
                locationPlayIcon.setVisibility(View.INVISIBLE);
                if (locationItem == null) {
                    locationItem = ((SceneLocation)item).getRepresentativeLocationItem();

                }
                if (locationItem != null && locationItem.locationThumbnail != null) {
                    Glide.with(ECSceneLocationActivity.this).load(locationItem.locationThumbnail.url).into(locationPhoto);
                    locationName.setText(((SceneLocation)item).name);
                    int locationCount = ((SceneLocation)item).childrenSceneLocations.size();
                    if (locationCount == 0){
                        locationsCountText.setText("");
                    }else {
                        locationsCountText.setText(String.format(getResources().getString(R.string.locationsCountText), locationCount));
                    }
                    //PicassoTrustAll.loadImageIntoView(ECSceneLocationActivity.this, locationItem.locationThumbnail.url, holder.personPhoto);
                    NextGenLogger.d(F.TAG, "Position: " + itemIndex + " loaded: " + locationItem.locationThumbnail.url);
                }
            } else if (item instanceof PresentationDataItem){
                if (item instanceof MovieMetaData.AudioVisualItem)
                    locationPlayIcon.setVisibility(View.VISIBLE);
                else
                    locationPlayIcon.setVisibility(View.INVISIBLE);
                locationsCountText.setText("");
                locationName.setText(((PresentationDataItem) item).getTitle());
                Glide.with(ECSceneLocationActivity.this).load(((PresentationDataItem) item).getPosterImgUrl()).into(locationPhoto);
            }
        }

        @Override
        public void onClick(View v) {
            if (currentItem != null){
                if (currentItem instanceof SceneLocation){
                    mapViewFragment.setSelectionFromSlider(itemIndex);
                    locationECsAdapter.setSceneLocation((SceneLocation)currentItem);
                    locationECsAdapter.notifyDataSetChanged();
                }else if (currentItem instanceof MovieMetaData.AudioVisualItem){
                    videoViewFragment.setAudioVisualItem((MovieMetaData.AudioVisualItem)currentItem);

                    transitToFragment(videoViewFragment);
                }else if (currentItem instanceof MovieMetaData.ECGalleryItem){
                    galleryViewFragment.setCurrentGallery((MovieMetaData.ECGalleryItem)currentItem);
                    transitToFragment(galleryViewFragment);
                }
            }
        }
    }

    public class LocationECsAdapter extends RecyclerView.Adapter<LocationECViewHolder>{


        int lastloadingIndex = -1;
        static final int PAGEITEMCOUNT = 6;

        SceneLocation sceneLocation;

        public void setSceneLocation(SceneLocation sceneLocation){
            this.sceneLocation = sceneLocation;
        }

        public void reset(){
            lastloadingIndex = -1;
        }

        @Override
        public LocationECViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scene_locations_card_view, viewGroup, false);
            LocationECViewHolder pvh = new LocationECViewHolder(v, null, i);
            return pvh;
        }

        public void onBindViewHolder(LocationECViewHolder holder, int position){

            if (sceneLocation != null){
                if (sceneLocation.childrenSceneLocations.size() > 0){
                    holder.setItem(sceneLocation.childrenSceneLocations.get(position), position);
                }else if (sceneLocation.presentationItems.size() > 0){
                    holder.setItem(sceneLocation.presentationItems.get(position), position);
                }
            }

            //holder.personPhoto.setImageResource(persons.get(i).photoId);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount(){
            if (sceneLocation != null){
                if (sceneLocation.childrenSceneLocations.size() > 0){
                    return sceneLocation.childrenSceneLocations.size();
                }else if (sceneLocation.presentationItems.size() > 0){
                    return sceneLocation.presentationItems.size();
                } else
                    return 0;

            }else
                return 0;
        }

    }
}
