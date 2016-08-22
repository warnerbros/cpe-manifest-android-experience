package com.wb.nextgen.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.wb.nextgen.NextGenExperience;
import com.wb.nextgen.R;

import com.wb.nextgen.activity.ActorGalleryActivity;
import com.wb.nextgen.adapter.ActorDetailGalleryRecyclerAdapter;
import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.Filmography;
import com.wb.nextgen.data.MovieMetaData.CastData;
import com.wb.nextgen.network.BaselineApiDAO;
import com.wb.nextgen.util.DialogUtils;
import com.wb.nextgen.util.NextGenUtils;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.StringHelper;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorDetailFragment extends AbstractNextGenFragment implements View.OnClickListener, ActorDetailGalleryRecyclerAdapter.ActorGalleryRecyclerSelectionListener{

    CastData actorOjbect;
    ImageView fullImageView;
    TextView detailTextView;
    TextView actorNameTextView;

    RecyclerView filmographyRecyclerView;
    LinearLayoutManager filmographyLayoutManager;
    ActorDetailFimograpyAdapter filmographyAdaptor;

    RecyclerView actorGalleryRecyclerView;
    LinearLayoutManager actorGalleryLayoutManager;
    ActorDetailGalleryRecyclerAdapter actorGalleryAdaptor;


    ImageButton facebookBtn;
    ImageButton twitterBtn;
    ImageButton instagramBtn;

    boolean bEnableActorGallery = false;

    int getContentViewId(){
        return 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (bEnableActorGallery){

        }
        return inflater.inflate(R.layout.next_gen_actor_detail_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullImageView = (ImageView)view.findViewById(R.id.next_gen_detail_full_image);
        detailTextView = (TextView)view.findViewById(R.id.actor_biography_text);
        actorNameTextView = (TextView)view.findViewById(R.id.actor_real_name_text);
        filmographyRecyclerView = (RecyclerView)view.findViewById(R.id.actor_detail_filmography);
        actorGalleryRecyclerView = (RecyclerView) view.findViewById(R.id.actor_gallery_recycler);
        View actorGalleryFrame = view.findViewById(R.id.actor_gallery_recycler_frame);

        facebookBtn = (ImageButton)view.findViewById(R.id.actor_page_facebook_button);
        twitterBtn = (ImageButton)view.findViewById(R.id.actor_page_twitter_button);
        instagramBtn = (ImageButton)view.findViewById(R.id.actor_page_instagram_button);
        if (facebookBtn != null){
            facebookBtn.setOnClickListener(this);
        }
        if (twitterBtn != null){
            twitterBtn.setOnClickListener(this);
        }
        if (instagramBtn != null){
            instagramBtn.setOnClickListener(this);
        }

        if (filmographyRecyclerView != null){
            filmographyLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            filmographyRecyclerView.setLayoutManager(filmographyLayoutManager);
            filmographyAdaptor = new ActorDetailFimograpyAdapter();
            filmographyRecyclerView.setAdapter(filmographyAdaptor);
        }

        if (actorGalleryRecyclerView != null && bEnableActorGallery){
            actorGalleryLayoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
            actorGalleryRecyclerView.setLayoutManager(actorGalleryLayoutManager);
            actorGalleryAdaptor = new ActorDetailGalleryRecyclerAdapter(getActivity(), this);
            actorGalleryRecyclerView.setAdapter(actorGalleryAdaptor);
        } else if (actorGalleryFrame != null){
            actorGalleryFrame.setVisibility(View.GONE);
        }
        reloadDetail(actorOjbect);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if (filmographyRecyclerView != null)
            filmographyRecyclerView.setAdapter(null);
        if (actorGalleryRecyclerView != null)
            actorGalleryRecyclerView.setAdapter(null);
    }

    public void setDetailObject(CastData object){
        actorOjbect = object;
    }

    public void setbEnableActorGallery(boolean enable){
        bEnableActorGallery = enable;
    }

    public void reloadDetail(CastData object){
        actorOjbect = object;

        if (actorOjbect != null && actorOjbect.getBaselineCastData() != null){
            Glide.with(getActivity()).load(actorOjbect.getBaselineCastData().getFullImageUrl()).fitCenter().centerCrop().into(fullImageView);
            //Picasso.with(getActivity()).load(actorOjbect.getBaselineCastData().getFullImageUrl()).fit().centerCrop().into(fullImageView);
            //PicassoTrustAll.loadImageIntoView(getActivity(), actorOjbect.getBaselineCastData().getFullImageUrl(), fullImageView);

            if (actorOjbect.getBaselineCastData().filmogrphies == null){
                BaselineApiDAO.getFilmographyAndBioOfPerson(actorOjbect.getBaselineActorId(), new ResultListener<MovieMetaData.BaselineCastData>() {
                    @Override
                    public void onResult(MovieMetaData.BaselineCastData result) {
                        actorOjbect.getBaselineCastData().filmogrphies = result.filmogrphies;
                        actorOjbect.getBaselineCastData().biography = result.biography;
                        if (getActivity() != null) {
                            synchronized (getActivity()) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        detailTextView.setText(actorOjbect.getBaselineCastData().biography);
                                        updateFilmographyList();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public <E extends Exception> void onException(E e) {

                    }
                });
            }else{
                updateFilmographyList();
            }

            String facebookUrl = actorOjbect.getBaselineCastData().getSocialMediaUrl(MovieMetaData.BaselineCastData.SOCIAL_MEDIA_KEY.FACEBOOK_KEY);
            if (!StringHelper.isEmpty(facebookUrl)){
                facebookBtn.setVisibility(View.VISIBLE);
            }else
                facebookBtn.setVisibility(View.GONE);

            String instagramUrl = actorOjbect.getBaselineCastData().getSocialMediaUrl(MovieMetaData.BaselineCastData.SOCIAL_MEDIA_KEY.INSTAGRAM_KEY);
            if (!StringHelper.isEmpty(instagramUrl)){
                instagramBtn.setVisibility(View.VISIBLE);
            }else
                instagramBtn.setVisibility(View.GONE);

            String twitterUrl = actorOjbect.getBaselineCastData().getSocialMediaUrl(MovieMetaData.BaselineCastData.SOCIAL_MEDIA_KEY.TWITTER_KEY);
            if (!StringHelper.isEmpty(twitterUrl)){
                twitterBtn.setVisibility(View.VISIBLE);
            }else
                twitterBtn.setVisibility(View.GONE);

            if (actorOjbect.getBaselineCastData().getGallery() != null && bEnableActorGallery){
                List<MovieMetaData.CastHeadShot> headShots = actorOjbect.getBaselineCastData().getGallery();
                if (headShots != null && headShots.size() > 1)
                    actorGalleryAdaptor.setCastHeadShots(headShots.subList(1, headShots.size()));
                else{
                    actorGalleryAdaptor.setCastHeadShots(null);
                }
                actorGalleryAdaptor.notifyDataSetChanged();
            }

            detailTextView.setText(actorOjbect.getBaselineCastData().biography);
            actorNameTextView.setText(actorOjbect.displayName);
        }
    }

    private void updateFilmographyList(){
        final List<Filmography> updateList;
        if (actorOjbect.getBaselineCastData() == null) {
            updateList = null;
        }else if ( actorOjbect.getBaselineCastData().filmogrphies != null && actorOjbect.getBaselineCastData().filmogrphies.size() > 0) {
            updateList = actorOjbect.getBaselineCastData().filmogrphies;
        }else {
            updateList = null;      //clear the list

        }

        if (getActivity() != null) {
            filmographyAdaptor.setFilmographies(updateList);
            filmographyAdaptor.notifyDataSetChanged();
        }


    }

    public class FilmographyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        ImageView personPhoto;
        Filmography filmInfo;

        FilmographyViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            itemView.setOnClickListener(this);
        }

        public void setFilmInfo(Filmography filmInfo, int position){
            this.filmInfo = filmInfo;
            if (filmInfo.isFilmPosterRequest()) {
                Glide.with(getActivity()).load(filmInfo.getFilmPosterImageUrl()).asBitmap().fitCenter().into(personPhoto);
                //NextGenLogger.d(F.TAG, "Position: " + position  +" loaded: " + filmInfo.getFilmPosterImageUrl());
            }
        }

        @Override
        public void onClick(View v) {
            if (NextGenExperience.getNextGenEventHandler() != null){
                NextGenExperience.getNextGenEventHandler().handleMovieTitleSelection(getActivity(), filmInfo.title);
            }
            //NextGenExperience.getMainMovieFragmentClass()
            /*
            final String url = filmInfo.movieInfoUrl;
            if (!StringHelper.isEmpty(url)){
                return;
            }
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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

    public class ActorDetailFimograpyAdapter extends RecyclerView.Adapter<FilmographyViewHolder>{

        List<Filmography> filmographies = null;

        public void setFilmographies(List<Filmography> filmographies){
            this.filmographies = filmographies;
        }

        @Override
        public FilmographyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.actor_filmography_cardview, viewGroup, false);
            FilmographyViewHolder pvh = new FilmographyViewHolder(v);
            return pvh;
        }

        public void onBindViewHolder(FilmographyViewHolder holder, int position){


            if (filmographies != null && filmographies.size() > position) {
                Filmography film = filmographies.get(position);
                holder.setFilmInfo(film, position);
            }

        }

        public int getItemCount(){
            if (filmographies != null )
                return filmographies.size();
            else
                return 0;
        }

    }

    public CastData getDetailObject(){
        return actorOjbect;
    }


    @Override
    public void onItemSelected(MovieMetaData.CastHeadShot headShot, int index){
        Intent actorGalleryIntent = new Intent(getActivity(), ActorGalleryActivity.class);
        String actorGallery = (new Gson()).toJson(actorOjbect.getBaselineCastData().getGallery());
        actorGalleryIntent.putExtra(ActorGalleryActivity.HEAD_SHOTS_KEY, actorGallery);
        actorGalleryIntent.putExtra(ActorGalleryActivity.CURRENT_INDEX_KEY, index + 1);

        startActivity(actorGalleryIntent);
    }

    @Override
    public void onClick(View v){
        String socialUrl = null;
        if (v.equals(facebookBtn)){
            socialUrl = actorOjbect.getBaselineCastData().getSocialMediaUrl(MovieMetaData.BaselineCastData.SOCIAL_MEDIA_KEY.FACEBOOK_KEY);

            try {
                getActivity().getPackageManager().getPackageInfo("com.facebook.katana", 0);
                // http://stackoverflow.com/a/24547437/1048340
                socialUrl = "fb://facewebmodal/f?href=" + socialUrl;
            } catch (PackageManager.NameNotFoundException e) {

            }

        }else if (v.equals(twitterBtn)){
            socialUrl = actorOjbect.getBaselineCastData().getSocialMediaUrl(MovieMetaData.BaselineCastData.SOCIAL_MEDIA_KEY.TWITTER_KEY);
        }else if (v.equals(instagramBtn)){
            socialUrl = actorOjbect.getBaselineCastData().getSocialMediaUrl(MovieMetaData.BaselineCastData.SOCIAL_MEDIA_KEY.INSTAGRAM_KEY);
        }

        if (!StringHelper.isEmpty(socialUrl)){
            final String targetUrl = socialUrl;
            DialogUtils.showLeavingAppDialog(getActivity(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    NextGenExperience.launchChromeWithUrl(targetUrl);
                }
            });
        }
    }

}
