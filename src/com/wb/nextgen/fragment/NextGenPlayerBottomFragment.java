package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.wb.nextgen.R;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/19/16.
 */
public class NextGenPlayerBottomFragment extends Fragment implements NextGenPlaybackStatusListener{

    //final List<NextGenPlaybackStatusListener> listeners = new ArrayList<NextGenPlaybackStatusListener>();
    TextView imeText;
    StickyGridHeadersGridView gridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_ime_lower_frame, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imeText = (TextView)view.findViewById(R.id.next_gen_ime_text);
        NextGenIMEActorFragment imeActorFragment = (NextGenIMEActorFragment)getChildFragmentManager().findFragmentById(R.id.ime_actor_fragment);

        gridView = (StickyGridHeadersGridView)view.findViewById(R.id.ime_gridview);
        /*NextGenPlaybackStatusListener imeFragmentFrame1 = (NextGenPlaybackStatusListener)getChildFragmentManager().findFragmentById(R.id.ime_fragment_frame_1);
        NextGenPlaybackStatusListener imeFragmentFrame2 = (NextGenPlaybackStatusListener)getChildFragmentManager().findFragmentById(R.id.ime_fragment_frame_2);
        NextGenPlaybackStatusListener imeFragmentFrame3 = (NextGenPlaybackStatusListener)getChildFragmentManager().findFragmentById(R.id.ime_fragment_frame_3);
        NextGenPlaybackStatusListener imeFragmentFrame4 = (NextGenPlaybackStatusListener)getChildFragmentManager().findFragmentById(R.id.ime_fragment_frame_4);
        listeners.add(imeActorFragment);
        listeners.add(imeFragmentFrame1);
        listeners.add(imeFragmentFrame2);
        listeners.add(imeFragmentFrame3);
        listeners.add(imeFragmentFrame4);*/

    }

    public void playbackStatusUpdate(final NextGenPlaybackStatus playbackStatus, final long timecode){
        /*if (listeners.size()> 0 ){
            for (NextGenPlaybackStatusListener listener :listeners) {
                    if (listener !=  null)
                    listener.playbackStatusUpdate(playbackStatus, timecode);
            }
        }*/
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

    public int getMainFrameId(){
        return R.id.next_gen_ime_main_frame;
    }

    public int getLeftFrameId(){
        return R.id.next_gen_ime_left_frame;
    }

    public int getRightFrameId(){
        return R.id.next_gen_ime_right_frame;

    }
}
