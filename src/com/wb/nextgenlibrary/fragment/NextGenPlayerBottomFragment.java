package com.wb.nextgenlibrary.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgenlibrary.interfaces.NextGenPlaybackStatusListener;
/**
 * Created by gzcheng on 1/19/16.
 */
public class  NextGenPlayerBottomFragment extends Fragment implements NextGenPlaybackStatusListener {
    IMEElementsGridFragment imeGridFragment;
    NextGenIMEActorFragment imeActorsFragment;

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.next_gen_ime_lower_frame, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView backgroundImgView = (ImageView)view.findViewById(R.id.next_gen_ime_bottom_background);
        if (backgroundImgView != null && NextGenExperience.getMovieMetaData().getInMovieExperience().style != null){
            Picasso.with(getActivity()).load(NextGenExperience.getMovieMetaData().getInMovieExperience().style.getBackground().getImage().url).fit().into(backgroundImgView);
            //Glide.with(getActivity()).load(NextGenExperience.getMovieMetaData().getStyle().getBackgroundImageURL(NextGenStyle.NextGenAppearanceType.InMovie)).into(backgroundImgView);
        }
        imeGridFragment = (IMEElementsGridFragment) getChildFragmentManager().findFragmentById(R.id.ime_grid_fragment);
        imeActorsFragment = (NextGenIMEActorFragment) getChildFragmentManager().findFragmentById(R.id.ime_actor_fragment);
    }

    public void setFragmentTransactionInterface(NextGenFragmentTransactionInterface fragmentTransaction){

    }

    public void playbackStatusUpdate(final NextGenPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, final long timecode){

        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imeGridFragment != null)
                    imeGridFragment.playbackStatusUpdate(playbackStatus, timecode);
                if (imeActorsFragment != null){
                    imeActorsFragment.playbackStatusUpdate(playbackStatus, timecode);
                }
                //if (imeText != null)
                //   imeText.setText(Long.toString(timecode));
            }
        });

        switch (playbackStatus){
            case PREPARED:
                break;
            case STARTED:
                break;
            case STOP:
                break;
            case TIMESTAMP_UPDATE:
                break;
        }
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
    }

    @Override
    public void onResume(){
        super.onResume();
        imeActorsFragment.resetSelectedItem();
    }

    public void onOrientationChange(int orientation){
        imeGridFragment.onOrientationChange(orientation);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){

        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){

        }

    }
}
