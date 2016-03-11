package com.wb.nextgen.fragment;


import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.wb.nextgen.NextGenApplication;
import com.wb.nextgen.R;
import com.wb.nextgen.activity.InterStitialVideoPlayerActivity;
import com.wb.nextgen.activity.NextGenExtraActivity;
import com.wb.nextgen.activity.NextGenPlayer;
import com.wb.nextgen.util.PicassoTrustAll;

/**
 * Created by gzcheng on 1/7/16.
 */
public class NextGenStartupUpperFragment extends Fragment implements View.OnClickListener {
    ImageButton playMovieButton;
    ImageButton extraButton;
    ImageView movieLogo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_start_upper, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView bg = (ImageView)view.findViewById(R.id.next_gen_startup_layout);
        if (bg != null){
            String bgImageUri = "android.resource://com.wb.nextgen/" + R.drawable.front_page_bg;
            PicassoTrustAll.loadImageIntoView(NextGenApplication.getContext(), bgImageUri, bg);
        }
        movieLogo = (ImageView) view.findViewById(R.id.next_gen_startup_movie_logo);
        if (movieLogo != null){
            movieLogo.setImageResource(R.drawable.man_of_sett_top_logo);
        }
        playMovieButton = (ImageButton) view.findViewById(R.id.next_gen_startup_play_button);
        if (playMovieButton != null){
            playMovieButton.setImageResource(R.drawable.front_page_paly_button);
            playMovieButton.setOnClickListener(this);
        }
        extraButton = (ImageButton) view.findViewById(R.id.next_gen_startup_extra_button);
        if (extraButton != null){
            extraButton.setImageResource(R.drawable.front_page_extra_button);
            extraButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.next_gen_startup_play_button:
                Intent intent = new Intent(getActivity(), InterStitialVideoPlayerActivity.class);
                intent.setAction(android.content.Intent.ACTION_VIEW);
                //intent.setDataAndType(Uri.parse("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/video/MOSNG_202_KryptonianRobots.m3u8"), "video/*");
                intent.setDataAndType(Uri.parse("android.resource://com.wb.nextgen/" + R.raw.mos_nextgen_interstitial), "video/*");
                startActivity(intent);
                //Drm.manager().playMovie(getActivity(), FlixsterApplication.getCurrentPlayableContent(), PhysicalAsset.Definition.HD, "en_US", "en_US");
                //        lockOrientation();
                break;
            case R.id.next_gen_startup_extra_button:
                Intent extraIntent = new Intent(getActivity(), NextGenExtraActivity.class);
                startActivity(extraIntent);
                break;
        }
    }

    private void startMainMovie(){
        Intent intent = new Intent(getActivity(), NextGenPlayer.class);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("http://cdn.theplatform.services/u/ContentServer/WarnerBros/Static/mos/NextGEN/feature/ManOfSteel_Clean.mp4"), "video/*");
        startActivity(intent);
    }
}
