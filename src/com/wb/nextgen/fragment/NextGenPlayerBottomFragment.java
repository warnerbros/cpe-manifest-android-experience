package com.wb.nextgen.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wb.nextgen.R;
import com.wb.nextgen.interfaces.NextGenFragmentTransactionInterface;
import com.wb.nextgen.interfaces.NextGenPlaybackStatusListener;
import com.wb.nextgen.util.utils.NextGenFragmentTransactionEngine;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gzcheng on 1/19/16.
 */
public class NextGenPlayerBottomFragment extends Fragment implements NextGenPlaybackStatusListener, NextGenFragmentTransactionInterface {

    //final List<NextGenPlaybackStatusListener> listeners = new ArrayList<NextGenPlaybackStatusListener>();
    TextView imeText;
    IMEElementsGridFragment imeGridFragment;
    NextGenFragmentTransactionEngine nextGenFragmentTransactionEngine;
    private long lastTimeCode = -1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.next_gen_ime_lower_frame, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nextGenFragmentTransactionEngine = new NextGenFragmentTransactionEngine(getActivity());

        imeText = (TextView)view.findViewById(R.id.next_gen_ime_text);
        NextGenIMEActorFragment imeActorFragment = (NextGenIMEActorFragment)getChildFragmentManager().findFragmentById(R.id.ime_actor_fragment);

        imeGridFragment = (IMEElementsGridFragment)getChildFragmentManager().findFragmentById(R.id.ime_grid_fragment);
        imeGridFragment.setFragmentTransactionInterface(this);
    }

    public void playbackStatusUpdate(final NextGenPlaybackStatus playbackStatus, final long timecode){


        if (lastTimeCode == timecode)
            return;

        lastTimeCode = timecode;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (imeGridFragment != null)
                    imeGridFragment.playbackStatusUpdate(playbackStatus, timecode);
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

    //*************** NextGenFragmentTransactionInterface ***************
    @Override
    public void transitRightFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(getRightFrameId(), nextFragment);

    }

    @Override
    public void transitLeftFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(getLeftFrameId(), nextFragment);

    }

    @Override
    public void transitMainFragment(Fragment nextFragment){
        nextGenFragmentTransactionEngine.transitFragment(getMainFrameId(), nextFragment);
    }

    @Override
    public void resetUI(boolean bIsRoot){
        /*if (bIsRoot){
            setBackButtonLogo(R.drawable.home_logo);
            setBackButtonText(getResources().getString(R.string.home_button_text) );
        }else{
            setBackButtonLogo(R.drawable.back_logo);
            setBackButtonText(getResources().getString(R.string.back_button_text) );
        }*/
    }
}
