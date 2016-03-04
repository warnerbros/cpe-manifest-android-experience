package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wb.nextgen.R;

import com.wb.nextgen.data.DemoJSONData.ActorInfo;
import com.wb.nextgen.data.DemoJSONData.Filmography;
import com.wb.nextgen.util.PicassoTrustAll;

import org.w3c.dom.Text;

/**
 * Created by gzcheng on 1/13/16.
 */
public class NextGenActorDetailFragment extends Fragment{

    ActorInfo actorOjbect;
    ImageView fullImageView;
    TextView detailTextView;
    TextView actorNameTextView;
    TextView characterTextView;
    RecyclerView filmographyRecyclerView;
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

        if (actorOjbect != null){
            PicassoTrustAll.loadImageIntoView(getActivity(), actorOjbect.getFullImageUri(), fullImageView);
            detailTextView.setText(actorOjbect.biography);
            characterTextView.setText(actorOjbect.character);
            actorNameTextView.setText(actorOjbect.realName);
        }
    }

    public void setDetailObject(ActorInfo object){
        actorOjbect = object;
    }

    public void reloadDetail(ActorInfo object){
        setDetailObject(actorOjbect);
        if (object != null){
            PicassoTrustAll.loadImageIntoView(getActivity(), object.getFullImageUri(), fullImageView);
            detailTextView.setText(object.biography);
        }
    }

    public ActorInfo getDetailObject(){
        return actorOjbect;
    }

}
