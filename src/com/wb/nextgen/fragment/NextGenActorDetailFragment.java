package com.wb.nextgen.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;

import com.wb.nextgen.data.MovieMetaData;
import com.wb.nextgen.data.MovieMetaData.Filmography;
import com.wb.nextgen.data.MovieMetaData.CastData;
import com.wb.nextgen.network.BaselineApiDAO;
import com.wb.nextgen.util.PicassoTrustAll;
import com.wb.nextgen.util.concurrent.ResultListener;
import com.wb.nextgen.util.utils.F;
import com.wb.nextgen.util.utils.NextGenLogger;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorDetailFragment extends Fragment{

    CastData actorOjbect;
    ImageView fullImageView;
    TextView detailTextView;
    TextView actorNameTextView;
    RecyclerView filmographyRecyclerView;
    LinearLayoutManager filmographyLayoutManager;
    ActorDetailFimograpyAdapter filmographyAdaptor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_actor_detail_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fullImageView = (ImageView)view.findViewById(R.id.next_gen_detail_full_image);
        detailTextView = (TextView)view.findViewById(R.id.actor_biography_text);
        actorNameTextView = (TextView)view.findViewById(R.id.actor_real_name_text);
        filmographyRecyclerView = (RecyclerView)view.findViewById(R.id.actor_detail_filmography);

        if (filmographyRecyclerView != null){
            filmographyLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            filmographyRecyclerView.setLayoutManager(filmographyLayoutManager);
            filmographyAdaptor = new ActorDetailFimograpyAdapter();
            filmographyRecyclerView.setAdapter(filmographyAdaptor);
        }
        reloadDetail(actorOjbect);
    }

    public void setDetailObject(CastData object){
        actorOjbect = object;
    }

    public void reloadDetail(CastData object){
        actorOjbect = object;
        filmographyAdaptor.reset();


        if (actorOjbect != null && actorOjbect.getBaselineCastData() != null){
            Picasso.with(getActivity()).load(actorOjbect.getBaselineCastData().getFullImageUrl()).fit().centerCrop().into(fullImageView);
            //PicassoTrustAll.loadImageIntoView(getActivity(), actorOjbect.getBaselineCastData().getFullImageUrl(), fullImageView);

            if (actorOjbect.getBaselineCastData().filmogrphies == null){
                BaselineApiDAO.getFilmographyAndBioOfPerson(actorOjbect.getBaselineActorId(), new ResultListener<MovieMetaData.BaselineCastData>() {
                    @Override
                    public void onResult(MovieMetaData.BaselineCastData result) {
                        actorOjbect.getBaselineCastData().filmogrphies = result.filmogrphies;
                        actorOjbect.getBaselineCastData().biography = result.biography;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                detailTextView.setText(actorOjbect.getBaselineCastData().biography);
                                filmographyAdaptor.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public <E extends Exception> void onException(E e) {

                    }
                });
            }else{
                filmographyAdaptor.notifyDataSetChanged();
            }


            detailTextView.setText(actorOjbect.getBaselineCastData().biography);
            actorNameTextView.setText(actorOjbect.displayName);
            filmographyAdaptor.notifyDataSetChanged();
        }
    }

    public class FilmographyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
       // TextView personName;
       // TextView personAge;
        ImageView personPhoto;
        Filmography filmInfo;

        FilmographyViewHolder(View itemView, Filmography filmInfo) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
           // personName = (TextView)itemView.findViewById(R.id.person_name);
            //personAge = (TextView)itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
            this.filmInfo = filmInfo;
            itemView.setOnClickListener(this);
        }

        public void setFilmInfo(Filmography filmInfo){
            this.filmInfo = filmInfo;
        }

        @Override
        public void onClick(View v) {
            final String url = filmInfo.movieInfoUrl;
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
            alertDialogBuilder.show();
        }
    }

    public class ActorDetailFimograpyAdapter extends RecyclerView.Adapter<FilmographyViewHolder>{


        int lastloadingIndex = -1;
        static final int PAGEITEMCOUNT = 6;

        public void reset(){
            lastloadingIndex = -1;
        }

        @Override
        public FilmographyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.actor_filmography_cardview, viewGroup, false);
            FilmographyViewHolder pvh = new FilmographyViewHolder(v, actorOjbect.getBaselineCastData().filmogrphies.get(i));
            return pvh;
        }
        public void onBindViewHolder(FilmographyViewHolder holder, int position){
            //holder.personName.setText(actorOjbect.actorFilmography[position].title);
            //holder.personAge.setText(actorOjbect.actorFilmography[position].title);


            if (actorOjbect.getBaselineCastData().filmogrphies != null && actorOjbect.getBaselineCastData().filmogrphies.size() > position) {
                Filmography film = actorOjbect.getBaselineCastData().filmogrphies.get(position);
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
                    BaselineApiDAO.getFilmographyPosters(actorOjbect.getBaselineCastData().filmogrphies, requestStartIndex, PAGEITEMCOUNT, new ResultListener<List<MovieMetaData.FilmPoster>>() {
                        @Override
                        public void onResult(List<MovieMetaData.FilmPoster> result) {
                            for (int i = 0; i < result.size(); i++){
                                if (i + requestStartIndex >= actorOjbect.getBaselineCastData().filmogrphies.size())
                                    break;
                                else{
                                    Filmography filmography = actorOjbect.getBaselineCastData().filmogrphies.get(i + requestStartIndex);
                                    filmography.setFilmPoster(result.get(i));
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    filmographyAdaptor.notifyDataSetChanged();
                                }
                            });

                        }

                        @Override
                        public <E extends Exception> void onException(E e) {

                        }
                    });
                }
            }
            //holder.personPhoto.setImageResource(persons.get(i).photoId);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount(){
            if (actorOjbect.getBaselineCastData() != null && actorOjbect.getBaselineCastData().filmogrphies != null )
                return actorOjbect.getBaselineCastData().filmogrphies.size();
            else
                return 0;
        }

    }

    public CastData getDetailObject(){
        return actorOjbect;
    }

}
