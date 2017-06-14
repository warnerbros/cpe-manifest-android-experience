package com.wb.nextgenlibrary.fragment;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wb.nextgenlibrary.NextGenExperience;
import com.wb.nextgenlibrary.R;
import com.wb.nextgenlibrary.interfaces.NGEFragmentTransactionInterface;
import com.wb.nextgenlibrary.interfaces.NGEPlaybackStatusListener;
/**
 * Created by gzcheng on 1/19/16.
 */
public class IMEBottomFragment extends Fragment implements NGEPlaybackStatusListener {
    IMEElementsGridFragment imeGridFragment;
    IMEActorFragment imeActorsFragment;

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

        TextView actorsTitle = (TextView)view.findViewById(R.id.actor_list_title);
        if (actorsTitle != null)
            actorsTitle.setText(NextGenExperience.getMovieMetaData().getActorGroupText());
        imeGridFragment = (IMEElementsGridFragment) getChildFragmentManager().findFragmentById(R.id.ime_grid_fragment);
        imeActorsFragment = (IMEActorFragment) getChildFragmentManager().findFragmentById(R.id.ime_actor_fragment);
    }

    public void setFragmentTransactionInterface(NGEFragmentTransactionInterface fragmentTransaction){

    }

    public void playbackStatusUpdate(final NGEPlaybackStatusListener.NextGenPlaybackStatus playbackStatus, final long timecode){

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
