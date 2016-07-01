package com.wb.nextgen.activity;

import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.wb.nextgen.R;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.ExperienceData;
import com.wb.nextgen.data.MovieMetaData.PresentationDataItem;
import com.wb.nextgen.data.MovieMetaData.SceneLocation;
import com.wb.nextgen.fragment.ECGalleryViewFragment;
import com.wb.nextgen.fragment.ECSceneLocationMapFragment;
import com.wb.nextgen.fragment.ECVideoViewFragment;
import com.wb.nextgen.interfaces.SensitiveFragmentInterface;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;
import com.wb.nextgen.util.utils.NextGenLogger;

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

    private int currentSelectedIndex = 0;

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
        galleryViewFragment = new ECGalleryViewFragment();

        sliderTitleTextView = (TextView) findViewById(R.id.scene_location_bottom_slider_text);
        contentFrame = (RelativeLayout)findViewById(R.id.scene_location_content_frame);
        locationECRecyclerView = (RecyclerView)findViewById(R.id.scene_location_recycler_view);

        if (locationECRecyclerView != null){
            locationECLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
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

    public void onSceneLocationSelected(int selectedIndex){
        currentSelectedIndex = selectedIndex;
        if (locationECsAdapter != null)
            locationECsAdapter.notifyDataSetChanged();
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
        SceneLocation slItem;

        LocationECViewHolder(View itemView, SceneLocation slItem) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            // personName = (TextView)itemView.findViewById(R.id.person_name);
            //personAge = (TextView)itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            this.slItem = slItem;
            itemView.setOnClickListener(this);
        }

        public void setSceneLocationItem(SceneLocation item){
            slItem = item;
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
            SceneLocation currentSL = null;
            if (position < rootSceneLocations.get(currentSelectedIndex).childrenSceneLocations.size()){
                currentSL = rootSceneLocations.get(currentSelectedIndex).childrenSceneLocations.get(position);
            }


            if (currentSL != null) {
                holder.setSceneLocationItem(currentSL);
                /*Glide.with(this)
                        .load(currentItem.galleryImages.get(position).fullImage.url)
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
                                imageView.setImage(ImageSource.bitmap(bitmap));
                                //thumbView.setImageBitmap(bitmap);
                            }
                        });*/
                MovieMetaData.LocationItem locationItem = currentSL.getRepresentativeLocationItem();
                if (locationItem != null && locationItem.locationThumbnail != null) {

                    PicassoTrustAll.loadImageIntoView(ECSceneLocationActivity.this, locationItem.locationThumbnail.url, holder.personPhoto);
                    NextGenLogger.d(F.TAG, "Position: " + position + " loaded: " + locationItem.locationThumbnail.url);
                }

            }
            //holder.personPhoto.setImageResource(persons.get(i).photoId);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount(){
            SceneLocation currentSL = rootSceneLocations.get(currentSelectedIndex);
            return currentSL.childrenSceneLocations.size();
        }

    }
}
