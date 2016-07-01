package com.wb.nextgen.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData.ExperienceData;
import com.wb.nextgen.data.MovieMetaData.PresentationDataItem;
import com.wb.nextgen.data.MovieMetaData.SceneLocation;
import com.wb.nextgen.fragment.ECGalleryViewFragment;
import com.wb.nextgen.fragment.ECSceneLocationMapFragment;
import com.wb.nextgen.fragment.ECVideoViewFragment;
import com.wb.nextgen.interfaces.SensitiveFragmentInterface;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 6/29/16.
 */
public class ECSceneLocationActivity extends AbstractECView {

    private ECVideoViewFragment videoViewFragment = null;
    private ECGalleryViewFragment galleryViewFragment = null;
    private ECSceneLocationMapFragment mapViewFragment = null;

    private RelativeLayout contentFrame;

    private RecyclerView locationECRecyclerView;
    private LinearLayoutManager locationECLayoutManager;
    private LocationECsAdapter locationECsAdapter;
    private NextGenFragmentTransactionEngine nextGenFragmentTransactionEngine;

    private List<SceneLocation> rootSceneLocations;

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
        }

        videoViewFragment = new ECVideoViewFragment();
        galleryViewFragment = new ECGalleryViewFragment();

        contentFrame = (RelativeLayout)findViewById(R.id.scene_location_content_frame);
        locationECRecyclerView = (RecyclerView)findViewById(R.id.scene_location_recycler_view);

        if (locationECLayoutManager != null){
            locationECLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            locationECRecyclerView.setLayoutManager(locationECLayoutManager);
            locationECsAdapter = new LocationECsAdapter();
            locationECRecyclerView.setAdapter(locationECsAdapter);
        }

        nextGenFragmentTransactionEngine.transitFragment(getSupportFragmentManager(), contentFrame.getId(), mapViewFragment);

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

    @Override
    public void onDestroy(){
        super.onDestroy();
        videoViewFragment = null;
        galleryViewFragment = null;
        mapViewFragment = null;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 1 )
            finish();


    }

    public class LocationECViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        // TextView personName;
        // TextView personAge;
        ImageView personPhoto;
        PresentationDataItem ecItem;

        LocationECViewHolder(View itemView, PresentationDataItem ecItem) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            // personName = (TextView)itemView.findViewById(R.id.person_name);
            //personAge = (TextView)itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            this.ecItem = ecItem;
            itemView.setOnClickListener(this);
        }

        public void setECItem(PresentationDataItem ecItem){
            this.ecItem = ecItem;
        }

        @Override
        public void onClick(View v) {
            /*final String url = ecItem.movieInfoUrl;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ECSceneLocationActivity.this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.dialog_leave_app));
            alertDialogBuilder.setMessage(getResources().getString(R.string.dialog_follow_link));
            alertDialogBuilder.setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                }
            });
            alertDialogBuilder.setNegativeButton(getResources().getString(android.R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialogBuilder.show();*/
        }
    }

    public class LocationECsAdapter extends RecyclerView.Adapter<LocationECViewHolder>{


        int lastloadingIndex = -1;
        static final int PAGEITEMCOUNT = 6;

        public void reset(){
            lastloadingIndex = -1;
        }

        @Override
        public LocationECViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.actor_filmography_cardview, viewGroup, false);
            LocationECViewHolder pvh = new LocationECViewHolder(v, null);
            return pvh;
        }
        public void onBindViewHolder(LocationECViewHolder holder, int position){
/*

            if (actorOjbect.getBaselineCastData().filmogrphies != null && actorOjbect.getBaselineCastData().filmogrphies.size() > position) {
                MovieMetaData.Filmography film = actorOjbect.getBaselineCastData().filmogrphies.get(position);
                holder.setFilmInfo(film);
                if (film.isFilmPosterRequest()) {
                    PicassoTrustAll.loadImageIntoView(getActivity(), film.getFilmPosterImageUrl(), holder.personPhoto);
                    NextGenLogger.d(F.TAG, "Position: " + position  +" loaded: " + film.getFilmPosterImageUrl());
                }else if (position < lastloadingIndex) {
                    //holder.personPhoto.setImageResource(R.drawable.poster_blank);
                    holder.personPhoto.setImageDrawable(null);
                } else {
                    holder.personPhoto.setImageDrawable(null);
                    final int requestStartIndex = position;
                    lastloadingIndex = requestStartIndex + PAGEITEMCOUNT;

                }
            }*/
            //holder.personPhoto.setImageResource(persons.get(i).photoId);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount(){
            /*if (ecGroupData..getBaselineCastData() != null && actorOjbect.getBaselineCastData().filmogrphies != null )
                return actorOjbect.getBaselineCastData().filmogrphies.size();
            else*/
                return 0;
        }

    }
}
