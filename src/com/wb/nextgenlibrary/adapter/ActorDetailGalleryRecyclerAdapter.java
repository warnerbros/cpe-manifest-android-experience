package com.wb.nextgenlibrary.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.activity.NextGenPlayer;
import com.wb.nextgenlibrary.analytic.NextGenAnalyticData;
import com.wb.nextgenlibrary.data.MovieMetaData;

import java.util.List;

/**
 * Created by gzcheng on 7/11/16.
 */
public class ActorDetailGalleryRecyclerAdapter extends RecyclerView.Adapter<ActorDetailGalleryRecyclerAdapter.ActorGalleryViewHolder>{

    List<MovieMetaData.CastHeadShot> headShots = null;
    ActorGalleryRecyclerSelectionListener listener;
    Context mContext;
    int selectedIndex = -1;

    public ActorDetailGalleryRecyclerAdapter(Context context, ActorGalleryRecyclerSelectionListener listener){
        mContext = context;
        this.listener = listener;
    }

    public void setCastHeadShots(List<MovieMetaData.CastHeadShot> headShots){
        this.headShots = headShots;
    }

    public void setSelectedIndex(int index){
        selectedIndex = index;
    }


    @Override
    public ActorGalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.actor_gallery_cardview, viewGroup, false);
        ActorGalleryViewHolder pvh = new ActorGalleryViewHolder(v, listener);


        return pvh;
    }

    public void onBindViewHolder(ActorGalleryViewHolder holder, int position){

        MovieMetaData.CastHeadShot headShot = headShots.get(position );
        holder.setCastHeadShot(mContext, headShot, position);

        holder.itemView.setSelected(position == selectedIndex);

    }

    public int getItemCount(){
        if (headShots != null)
            return headShots.size();
        else
            return 0;
    }

    public static class ActorGalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cv;
        ImageView personPhoto;
        MovieMetaData.CastHeadShot headShot;
        ActorGalleryRecyclerSelectionListener listener;
        int index = 0;

        ActorGalleryViewHolder(View itemView, ActorGalleryRecyclerSelectionListener listener) {
            super(itemView);
            this.listener = listener;
            cv = (CardView)itemView.findViewById(R.id.cv);
            personPhoto = (ImageView)itemView.findViewById(R.id.gallery_photo);
            itemView.setOnClickListener(this);
        }

        public void setCastHeadShot(Context context, MovieMetaData.CastHeadShot headShot, int position){
            this.headShot = headShot;
            index = position;
            //Picasso.with(getActivity()).load(headShot.mediumUrl).fit().centerCrop().into(personPhoto);
            Glide.with(context).load(headShot.mediumUrl).centerCrop().into(personPhoto);

        }

        @Override
        public void onClick(View v) {
            if (listener != null){
                listener.onItemSelected(index);

                NextGenAnalyticData.reportEvent(null, null, "Actor Gallery Picture",
                        NextGenAnalyticData.AnalyticAction.ACTION_CLICK, headShot.fullSizeUrl);
            }
        }
    }


    public static interface ActorGalleryRecyclerSelectionListener{
        void onItemSelected(int index);
    }

}