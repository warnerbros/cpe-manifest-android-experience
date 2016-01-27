package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
/**
 * Created by gzcheng on 1/19/16.
 */
public class NextGenPlayerBottomFragment extends Fragment implements NextGenPlaybackStatusListener{

    NextGenIMEActorFragment imeActorFragment;
    TextView imeText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_ime_lower_frame, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imeText = (TextView)view.findViewById(R.id.next_gen_ime_text);
        imeActorFragment = (NextGenIMEActorFragment)getActivity().getSupportFragmentManager().findFragmentById(R.id.ime_actor_fragment);

    }

    public void playbackStatusUpdate(final NextGenPlaybackStatus playbackStatus, final long timecode){
        if (imeActorFragment != null){
            imeActorFragment.playbackStatusUpdate(playbackStatus, timecode);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imeText != null)
                    imeText.setText(Long.toString(timecode));
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
}
