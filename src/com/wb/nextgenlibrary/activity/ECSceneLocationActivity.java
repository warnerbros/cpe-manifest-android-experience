package com.wb.nextgenlibrary.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.analytic.NGEAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;
import com.wb.nextgenlibrary.data.MovieMetaData.ExperienceData;
import com.wb.nextgenlibrary.data.MovieMetaData.PresentationDataItem;
import com.wb.nextgenlibrary.fragment.ECGalleryViewFragment;
import com.wb.nextgenlibrary.fragment.ECSceneLocationMapFragment;
import com.wb.nextgenlibrary.fragment.ECVideoViewFragment;
import com.wb.nextgenlibrary.util.TabletUtils;
import com.wb.nextgenlibrary.util.utils.F;
import com.wb.nextgenlibrary.util.utils.NGEFragmentTransactionEngine;
import com.wb.nextgenlibrary.util.utils.NextGenGlide;
import com.wb.nextgenlibrary.util.utils.NextGenLogger;
import com.wb.nextgenlibrary.util.utils.StringHelper;
import com.wb.nextgenlibrary.widget.FixedAspectRatioFrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 6/29/16.
 */
public class ECSceneLocationActivity extends AbstractECView implements ECSceneLocationMapFragment.OnSceneLocationSelectedListener {


    private ECSceneLocationMapFragment mapViewFragment = null;

    private RelativeLayout contentFrame;

    private RecyclerView locationECRecyclerView;
    //private TextView sliderTitleTextView;
    private RecyclerView sliderTitleText;


    private LinearLayoutManager locationECLayoutManager;
    private SliderTextAdapter sliderTextAdapter;
    private LocationECsAdapter locationECsAdapter;
    private NGEFragmentTransactionEngine fragmentTransactionEngine;
    private View sliderFrame;

    private List<MovieMetaData.LocationItem> rootSceneLocations;

    private Fragment currentFragment;

    //private int currentSelectedIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sliderFrame = findViewById(R.id.scene_location_slider_frame);

        rootSceneLocations = ecGroupData.getSceneLocations();

        fragmentTransactionEngine = new NGEFragmentTransactionEngine(this);
        if (mapViewFragment == null) {
            mapViewFragment = new ECSceneLocationMapFragment();
            mapViewFragment.setDefaultSceneLocations(rootSceneLocations);
            mapViewFragment.setOnSceneLocationSelectedListener(this);
        }

        sliderTitleText = (RecyclerView) findViewById(R.id.scene_location_bottom_text_slider);
        if (sliderTitleText != null) {
            sliderTitleText.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            sliderTextAdapter = new SliderTextAdapter();
            sliderTextAdapter.setSceneLocation(null);
            sliderTitleText.setAdapter(sliderTextAdapter);
        }
        contentFrame = (RelativeLayout) findViewById(R.id.scene_location_content_frame);
        locationECRecyclerView = (RecyclerView) findViewById(R.id.scene_location_recycler_view);

        if (locationECRecyclerView != null) {
            if (TabletUtils.isTablet()) {
                locationECLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            }else{
                locationECLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            }
            locationECRecyclerView.setLayoutManager(locationECLayoutManager);
            locationECsAdapter = new LocationECsAdapter();
            locationECsAdapter.setSceneLocation(null);
            locationECRecyclerView.setAdapter(locationECsAdapter);
        }

        fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), R.id.map_frame, mapViewFragment);


    }

    protected View getFullScreenDisappearView() {
        return sliderFrame;
    }

    private void transitToFragment(Fragment fragment) {
        if (fragment != currentFragment) {

            if (currentFragment != null && currentFragment != mapViewFragment) {
                onBackPressed();
            }
            fragmentTransactionEngine.transitFragment(getSupportFragmentManager(), contentFrame.getId(), fragment);

            currentFragment = fragment;
        }
    }

    public void onLeftListItemSelected(ExperienceData ecContentData) {

    }

    public int getContentViewId() {
        return R.layout.ec_scene_location_view;
    }


    public int getListItemViewLayoutId() {
        return 0;
    }


    public void onFullScreenChange(boolean bFullscreen) {
        if (currentFragment instanceof ECVideoViewFragment) {
            ((ECVideoViewFragment) currentFragment).onFullScreenChange(bFullscreen);
        } else if (currentFragment instanceof ECGalleryViewFragment) {
            ((ECGalleryViewFragment) currentFragment).onFullScreenChange(bFullscreen);
        }
    }

    public void onSceneLocationIndexSelected(int selectedIndex) {
        if (locationECsAdapter != null) {
            locationECsAdapter.setSceneLocation(rootSceneLocations.get(selectedIndex));
            locationECsAdapter.notifyDataSetChanged();
            locationECRecyclerView.smoothScrollToPosition(0);
        }
    }

    public void onSceneLocationSelected(MovieMetaData.LocationItem location) {
        if (locationECsAdapter != null) {
            locationECsAdapter.setSceneLocation(location);
            locationECsAdapter.notifyDataSetChanged();
            locationECRecyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    public void onDestroy() {
        sliderTextAdapter = null;
        locationECsAdapter = null;
        if (fragmentTransactionEngine != null)
            fragmentTransactionEngine.onDestroy();
        if (sliderTitleText != null)
            sliderTitleText.setAdapter(null);
        if (locationECRecyclerView != null)
            locationECRecyclerView.setAdapter(null);
        currentFragment = null;
        currentFragment = null;
        mapViewFragment = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        switch (getSupportFragmentManager().getBackStackEntryCount()) {
            case 0:                     // after map fragment being popped from fragment stack
                finish();
                break;
            case 1:                     // after either EC Gallery or EC Video Fragment being popped
                currentFragment = mapViewFragment;
                break;
            default:                    // do nothing if it's just a back from full screen toggling
                break;
        }

    }

    @Override
    public void onRequestToggleFullscreen() {

        super.onRequestToggleFullscreen();
        if (currentFragment != null && currentFragment instanceof ECGalleryViewFragment)
            ((ECGalleryViewFragment) currentFragment).onFullScreenChange(isContentFullScreen);

    }


    public class SliderTextViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView textCV;
        TextView locationTxt;
        TextView arrowTxt;
        String currentText;
        MovieMetaData.LocationItem sceneLocation;
        int itemIndex;

        SliderTextViewHolder(View itemView, String text, int index) {
            super(itemView);
            textCV = (CardView) itemView.findViewById(R.id.text_cv);
            arrowTxt = (TextView) itemView.findViewById(R.id.slider_arrow);
            locationTxt = (TextView) itemView.findViewById(R.id.slider_text);
            this.currentText = text;
            itemIndex = index;
            itemView.setOnClickListener(this);
        }

        public void setStringItem(String text, int position) {
            currentText = text;
            itemIndex = position;
            locationTxt.setText(text);
            locationTxt.setActivated(false);
            arrowTxt.setVisibility(View.GONE);
        }

        public void setSceneLocation(MovieMetaData.LocationItem sceneLocation, int position, boolean isLast) {
            this.sceneLocation = sceneLocation;
            itemIndex = position;
            arrowTxt.setVisibility(View.VISIBLE);
            locationTxt.setText(sceneLocation.getTitle());
            locationTxt.setActivated(isLast);
        }

        @Override
        public void onClick(View v) {
            if (sceneLocation != null) {
                mapViewFragment.setLocationItem(sceneLocation.getTitle(), sceneLocation);
                locationECsAdapter.setSceneLocation(sceneLocation);
                locationECsAdapter.notifyDataSetChanged();
            } else {

                mapViewFragment.setLocationItem(ecGroupData.title, null);
                locationECsAdapter.setSceneLocation(null);
                locationECsAdapter.notifyDataSetChanged();
            }
            locationECRecyclerView.smoothScrollToPosition(0);
            if (currentFragment != null && !(currentFragment instanceof ECSceneLocationMapFragment)){
                onBackPressed();
            }
        }
    }

    public class SliderTextAdapter extends RecyclerView.Adapter<SliderTextViewHolder> {


        int lastloadingIndex = -1;
        static final int PAGEITEMCOUNT = 6;

        MovieMetaData.LocationItem sceneLocation;
        List<MovieMetaData.LocationItem> sceneLocations = new ArrayList<MovieMetaData.LocationItem>();

        public void setSceneLocation(MovieMetaData.LocationItem sceneLocation) {
            this.sceneLocation = sceneLocation;
            sceneLocations = new ArrayList<MovieMetaData.LocationItem>();
            MovieMetaData.LocationItem thisSL = sceneLocation;
            if (thisSL != null) {
                sceneLocations.add(0, thisSL);
            }


        }

        public void reset() {
            lastloadingIndex = -1;
        }

        @Override
        public SliderTextViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.text_card_view, viewGroup, false);
            SliderTextViewHolder pvh = new SliderTextViewHolder(v, "", i);
            return pvh;
        }

        public void onBindViewHolder(SliderTextViewHolder holder, int position) {
            if (position == 0)
                holder.setStringItem(ecGroupData.title, position);
            else
                holder.setSceneLocation(sceneLocations.get(position - 1), position, position == getItemCount() - 1);

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount() {
            return sceneLocations.size() + 1;
        }

    }

    public class LocationECViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cv;
        TextView locationName;
        ImageView locationPhoto;
        ImageView locationPlayIcon;
        Object currentItem;
        int itemIndex;

        LocationECViewHolder(View itemView, Object item, int index) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            locationName = (TextView) itemView.findViewById(R.id.location_name);
            locationPhoto = (ImageView) itemView.findViewById(R.id.location_photo);
            locationPlayIcon = (ImageView) itemView.findViewById(R.id.location_play_image);
            this.currentItem = item;
            itemIndex = index;
            itemView.setOnClickListener(this);
        }

        public void setItem(Object item, int position) {
            currentItem = item;
            itemIndex = position;
            if (item instanceof MovieMetaData.LocationItem) {
                MovieMetaData.LocationItem locationItem = ((MovieMetaData.LocationItem) item);
                locationPlayIcon.setVisibility(View.INVISIBLE);
                if (locationItem == null) { // TODO: CHECK
                    locationItem = ((MovieMetaData.LocationItem) item);

                }
                if (locationItem != null && !StringHelper.isEmpty(locationItem.getPosterImgUrl())) {
                    NextGenGlide.load(ECSceneLocationActivity.this, locationItem.getPosterImgUrl()).into(locationPhoto);
                    locationName.setText(((MovieMetaData.LocationItem) item).getTitle());
                    int locationCount = 0;//((MovieMetaData.LocationItem)item).childrenSceneLocations.size();

                    NextGenLogger.d(F.TAG, "Position: " + itemIndex + " loaded: " + locationItem.getPosterImgUrl());
                }
            } else if (item instanceof PresentationDataItem) {
                if (item instanceof MovieMetaData.AudioVisualItem)
                    locationPlayIcon.setVisibility(View.VISIBLE);
                else
                    locationPlayIcon.setVisibility(View.INVISIBLE);
                locationName.setText(((PresentationDataItem) item).getTitle());
                NextGenGlide.load(ECSceneLocationActivity.this, ((PresentationDataItem) item).getPosterImgUrl()).into(locationPhoto);
            }
        }

        @Override
        public void onClick(View v) {
            if (currentItem != null) {
                if (currentItem instanceof MovieMetaData.LocationItem) {
                    mapViewFragment.setSelectionFromSlider((MovieMetaData.LocationItem) currentItem);
                    locationECsAdapter.setSceneLocation((MovieMetaData.LocationItem) currentItem);
                    locationECsAdapter.notifyDataSetChanged();
                    locationECRecyclerView.smoothScrollToPosition(0);
                    NGEAnalyticData.reportEvent(ECSceneLocationActivity.this, null, NGEAnalyticData.AnalyticAction.ACTION_SELECT_LOCATION_THUMBNAIL, ((MovieMetaData.LocationItem) currentItem).getId(), null);
                } else if (currentItem instanceof MovieMetaData.AudioVisualItem) {
                    ECVideoViewFragment videoViewFragment;
                    if (currentFragment instanceof ECVideoViewFragment) {
                        videoViewFragment = (ECVideoViewFragment) currentFragment;
                    } else {
                        videoViewFragment = new ECVideoViewFragment();
                        videoViewFragment.setShouldExitWhenComplete(true);
                        videoViewFragment.setShouldAutoPlay(true);
                        videoViewFragment.setShouldHideMetaData(true);
                        videoViewFragment.setShouldShowCloseBtn(true);
                        videoViewFragment.setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY);

                    }

                    videoViewFragment.setAudioVisualItem((MovieMetaData.AudioVisualItem) currentItem);

                    transitToFragment(videoViewFragment);
                    NGEAnalyticData.reportEvent(ECSceneLocationActivity.this, null, NGEAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, ((MovieMetaData.AudioVisualItem) currentItem).videoId, null);

                } else if (currentItem instanceof MovieMetaData.ECGalleryItem) {
                    ECGalleryViewFragment galleryViewFragment;
                    if (currentFragment instanceof ECGalleryViewFragment) {
                        galleryViewFragment = (ECGalleryViewFragment) currentFragment;
                    } else {
                        galleryViewFragment = new ECGalleryViewFragment();
                        galleryViewFragment.setShouldHideMetaData(true);
                        galleryViewFragment.setShouldShowCloseBtn(true);
                        galleryViewFragment.setBGColor(ECSceneLocationActivity.this.getResources().getColor(android.R.color.black));
                        galleryViewFragment.setAspectRatioFramePriority(FixedAspectRatioFrameLayout.Priority.HEIGHT_PRIORITY);
                    }
                    galleryViewFragment.setCurrentGallery((MovieMetaData.ECGalleryItem) currentItem);
                    transitToFragment(galleryViewFragment);
                    NGEAnalyticData.reportEvent(ECSceneLocationActivity.this, null, NGEAnalyticData.AnalyticAction.ACTION_SELECT_VIDEO, ((MovieMetaData.ECGalleryItem) currentItem).galleryId, null);
                }

            }
        }
    }

    public class LocationECsAdapter extends RecyclerView.Adapter<LocationECViewHolder> {


        int lastloadingIndex = -1;
        static final int PAGEITEMCOUNT = 6;

        MovieMetaData.LocationItem sceneLocation;

        public void setSceneLocation(MovieMetaData.LocationItem sceneLocation) {
            this.sceneLocation = sceneLocation;

            if (sliderTextAdapter != null) {
                sliderTextAdapter.setSceneLocation(sceneLocation);
                sliderTextAdapter.notifyDataSetChanged();
            }
        }

        public void reset() {
            lastloadingIndex = -1;
        }

        @Override
        public LocationECViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.scene_locations_card_view, viewGroup, false);
            LocationECViewHolder pvh = new LocationECViewHolder(v, null, i);
            return pvh;
        }

        public void onBindViewHolder(LocationECViewHolder holder, int position) {

            if (sceneLocation != null) {
                /*if (sceneLocation.childrenSceneLocations.size() > 0){
                    holder.setItem(sceneLocation.childrenSceneLocations.get(position), position);
                }else*/
                if (sceneLocation.getPresentationDataItems().size() > 0) {
                    holder.setItem(sceneLocation.getPresentationDataItems().get(position), position);
                }
            } else if (rootSceneLocations != null) {
                holder.setItem(rootSceneLocations.get(position), position);
            }

        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount() {
            if (sceneLocation != null) {
                if (sceneLocation.getPresentationDataItems().size() > 0) {
                    return sceneLocation.getPresentationDataItems().size();
                } else
                    return 0;

            } else if (rootSceneLocations != null) {
                return rootSceneLocations.size();
            } else
                return 0;
        }

    }
}
