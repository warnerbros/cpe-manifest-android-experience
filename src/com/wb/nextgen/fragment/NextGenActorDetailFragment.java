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
    TextView characterTextView;
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
        characterTextView = (TextView)view.findViewById(R.id.actor_character_name_text);
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



        if (actorOjbect != null && actorOjbect.getBaselineCastData() != null){
            PicassoTrustAll.loadImageIntoView(getActivity(), actorOjbect.getBaselineCastData().getFullImageUrl(), fullImageView);

            if (actorOjbect.getBaselineCastData().filmogrphies == null){
                BaselineApiDAO.getFilmographyOfPerson(actorOjbect.getBaselineActorId(), new ResultListener<List<Filmography>>() {
                    @Override
                    public void onResult(List<Filmography> result) {
                        actorOjbect.getBaselineCastData().filmogrphies = result;
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
            }else{
                filmographyAdaptor.notifyDataSetChanged();
            }


            detailTextView.setText(actorOjbect.getBaselineCastData().biography);
            characterTextView.setText(actorOjbect.charactorName);
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

        @Override
        public FilmographyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.actor_filmography_cardview, viewGroup, false);
            FilmographyViewHolder pvh = new FilmographyViewHolder(v, actorOjbect.getBaselineCastData().filmogrphies.get(i));
            return pvh;
        }
        public void onBindViewHolder(FilmographyViewHolder holder, int position){
            //holder.personName.setText(actorOjbect.actorFilmography[position].title);
            //holder.personAge.setText(actorOjbect.actorFilmography[position].title);
            if (actorOjbect.getBaselineCastData().filmogrphies != null && actorOjbect.getBaselineCastData().filmogrphies.size() > position)
                PicassoTrustAll.loadImageIntoView(getActivity(), actorOjbect.getBaselineCastData().filmogrphies.get(position).getFilmPosterImageUrl(), holder.personPhoto);
            //holder.personPhoto.setImageResource(persons.get(i).photoId);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        public int getItemCount(){
            if (actorOjbect.getBaselineCastData().filmogrphies != null )
                return actorOjbect.getBaselineCastData().filmogrphies.size();
            else
                return 0;
        }

    }

    public CastData getDetailObject(){
        return actorOjbect;
    }

}
